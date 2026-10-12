package sakura.world

import sakura.ThreeInterop
import sakura.core.{Outline, Pal, Textures, Toon, Util}
import sakura.core.Outline.OutlineOptions
import sakura.core.Textures.ShopKind
import sakura.core.Toon.{CelOptions, FlatOptions}
import vendor.three.*

import scala.collection.mutable
import scala.math
import scala.scalajs.js

/** 1:1 port of upstream `src/world/shotengai.js`: さくら坂商店街 -- the shopping street, its
  * twelve units, the alley approach, the bathhouse, the overhead wires and the pavement the
  * street actually lives on.
  *
  * The shop generator is inlined here rather than taken from `Shops`: upstream `shops.js` is a
  * parametric `makeShop(ctx, o)` and this street is where its options matter (two narrow Showa
  * units on the west row, and a 4.4 m bathhouse storey under a gable).
  */
object Shotengai:

  private val X0     = 19.2
  private val X1     = 25.2
  private val CX     = (X0 + X1) / 2.0
  private val ZS     = 16.3
  private val ZN     = 42.6
  private val AlleyZ = 15.1
  private val NorthZ = 45.2

  private val Walls = Vector(
    Pal.wallWhite, Pal.wallCream, Pal.wallBlue, Pal.wallBeige, Pal.wallGray, Pal.wallPink
  )
  private val Roofs = Vector(Pal.roofSlate, Pal.roofBlue, Pal.roofBrown, Pal.roofTeal)
  private val Awnings = Vector(
    Pal.awningGreen, Pal.awningOrange, Pal.awningBlue, Pal.awningCream, Pal.red
  )

  private object M:
    lazy val walls       = Walls.map(c => Toon.cel(CelOptions(color = c, bands = 3, tint = 0x6f6790)))
    lazy val roofs       = Roofs.map(c => Toon.cel(CelOptions(color = c, bands = 3, tint = 0x514b70)))
    lazy val awnings     = Awnings.map(c => Toon.cel(CelOptions(color = c, bands = 3, tint = 0x6f5680)))
    lazy val trim        = Toon.cel(CelOptions(color = Pal.trim, bands = 3, tint = 0x5c5680))
    lazy val metal       = Toon.cel(CelOptions(color = Pal.metal, bands = 3, tint = 0x666090))
    lazy val metalDark   = Toon.cel(CelOptions(color = Pal.metalDark, bands = 3, tint = 0x5c5680))
    lazy val tile        = Toon.cel(CelOptions(color = 0xe9edf0, bands = 3, tint = 0x6f6790))
    lazy val dark        = Toon.cel(CelOptions(color = Pal.blackSoft, bands = 2, tint = 0x4b4560))
    lazy val wood        = Toon.cel(CelOptions(color = 0x9c7f5e, bands = 3, tint = 0x5c5680))
    lazy val concrete    = Toon.cel(CelOptions(color = Pal.concrete, bands = 3, tint = 0x6f6790))
    lazy val concreteMid = Toon.cel(CelOptions(color = Pal.concreteMid, bands = 3, tint = 0x6a6288))
    lazy val drain       = Toon.cel(CelOptions(color = Pal.drain, bands = 3, tint = 0x5d5878))
    lazy val wire        = Toon.cel(CelOptions(color = 0x4c4658, bands = 2, tint = 0x413c58))

  final private case class UnitOptions(
    x: Double,
    z: Double,
    y: Double = 0.0,
    w: Double,
    d: Double = 6.5,
    face: Buildings.Face = Buildings.Face.ZPlus,
    kind: Option[ShopKind] = None,
    floors: Int = 2,
    h1: Double = 3.2,
    h2: Double = 2.7,
    recess: Double = 0.9,
    wall: Option[Int] = None,
    roof: Option[Int] = None,
    roofKind: Option[String] = None,
    roofH: Double = 1.25,
    awning: Option[Int] = None,
    awningOut: Double = 1.5,
    shutter: Option[Double] = None,
    blade: Option[ShopKind] = None,
    bladeSide: Option[Double] = None,
    noren: Option[Textures.NorenKind] = None,
    norenX: Option[Double] = None,
    lit: Boolean = false,
    balcony: Boolean = false,
    signBox: Boolean = false,
    posters: Option[Boolean] = None,
    posterLimit: Option[Int] = None,
    interior: Option[Int] = None,
    interiorKind: Option[Int] = None,
    openW: Option[Double] = None,
    seed: Int = 13,
    fascia: Boolean = true
  )

  final private case class StreetLantern(material: MeshBasicMaterial, along: Double, var at: Double)

  private def propCtx(ctx: WorldCtx): PropCtx =
    PropCtx(
      add = obj => ctx.add(obj),
      collide = (x0, z0, x1, z1, top) => ctx.collide(x0, z0, x1, z1, top),
      groundAt = (px, pz) => ctx.groundAt(px, pz)
    )

  private def lanternBody(group: Group[Object3DEventMap]): MeshBasicMaterial =
    val material = ThreeInterop.children(group).iterator
      .flatMap(child => ThreeInterop.asMesh(child))
      .map(_.material)
      .collectFirst { case basic: MeshBasicMaterial => basic }
      .getOrElse(throw new IllegalStateException("sakura-crossing: lantern has no lit body"))
    material

  private def lantern(
    lanterns: mutable.ArrayBuffer[StreetLantern],
    x: Double,
    y: Double,
    z: Double,
    variant: Int,
    r: Double,
    drop: Double,
    along: Double
  ): Group[Object3DEventMap] =
    val group = Shops.makePaperLantern(Shops.PaperLanternOpts(
      x = x, y = y, z = z, r = r, variant = variant, drop = drop, lit = false
    ))
    lanterns += StreetLantern(lanternBody(group), along, 0.0)
    group

  private def buildUnit(ctx: WorldCtx, o: UnitOptions): Group[Object3DEventMap] =
    val rng = Util.rngKit(o.seed)
    val g   = new Group()
    g.name = "shop_" + o.kind.map(_.key).getOrElse("unit")

    val w      = o.w
    val d      = o.d
    val floors = o.floors
    val h1     = o.h1
    val h2     = o.h2
    val h      = if floors == 2 then h1 + h2 else h1
    val rec    = o.recess
    val front  = d / 2.0
    /* Upstream indexes `WALLS` with the tenant table's wall number, and the two Showa units ask
     * for 6 and 7 in a six-entry array: three.js then falls back to its default white material.
     * Reproduce that rather than clamping, so the shop geometry stays identical. */
    val wallMat = o.wall.filter(index => index >= 0 && index < Walls.length)
      .map(M.walls(_)).getOrElse(Toon.flat(FlatOptions(color = 0xffffff)))
    val roofMat = o.roof.filter(index => index >= 0 && index < Roofs.length)
      .map(M.roofs(_)).getOrElse(Toon.flat(FlatOptions(color = 0xffffff)))

    val parts = mutable.LinkedHashMap(
      "wall"      -> mutable.ArrayBuffer.empty[Util.BakePart],
      "trim"      -> mutable.ArrayBuffer.empty[Util.BakePart],
      "roof"      -> mutable.ArrayBuffer.empty[Util.BakePart],
      "metal"     -> mutable.ArrayBuffer.empty[Util.BakePart],
      "metalDark" -> mutable.ArrayBuffer.empty[Util.BakePart],
      "tile"      -> mutable.ArrayBuffer.empty[Util.BakePart]
    )
    def push(key: String, geo: BufferGeometry[?, ?], mx: Matrix4): Unit =
      parts(key) += Util.BakePart(geo, Some(mx))

    push("wall", new BoxGeometry(w, h1, d - rec), Util.trs(0.0, h1 / 2.0, -rec / 2.0))
    push("trim", new BoxGeometry(w + 0.16, 0.4, d + 0.16), Util.trs(0.0, 0.2, 0.0))
    if floors == 2 then
      push("wall", new BoxGeometry(w, h2, d - 0.4), Util.trs(0.0, h1 + h2 / 2.0, -0.2))
      push("trim", new BoxGeometry(w + 0.12, 0.16, d - 0.3), Util.trs(0.0, h1, -0.2))

    /* Upstream's callers derive the roof from the storey count: a two-storey unit is built
     * flat (behind a parapet) and a single-storey one gets the gable the street reads. */
    val roofKind = o.roofKind.getOrElse(if floors == 2 then "flat" else "gable")
    if roofKind == "gable" then
      val eave  = 0.42
      val rw    = w + eave * 2.0
      val rd    = d + eave * 2.0
      val rh    = o.roofH
      val slope = math.atan2(rh, rw / 2.0)
      val slab  = math.hypot(rw / 2.0, rh) + 0.08
      Vector(-1.0, 1.0).foreach { s =>
        push("roof", new BoxGeometry(slab, 0.15, rd),
          Util.trs(s * (rw / 4.0), h + rh / 2.0, -0.2, 0.0, 0.0, -s * slope))
      }
      push("roof", new BoxGeometry(0.24, 0.18, rd), Util.trs(0.0, h + rh + 0.04, -0.2))
      val tri = new Shape()
      tri.moveTo(-w / 2.0, 0.0)
      tri.lineTo(w / 2.0, 0.0)
      tri.lineTo(0.0, rh * (1 - (eave * 2.0) / rw))
      tri.closePath()
      val triGeo = new ExtrudeGeometry(tri,
        ExtrudeGeometryOptions(depth = Some(0.16), bevelEnabled = Some(false)))
      triGeo.translate(0.0, 0.0, -0.08)
      Vector(-1.0, 1.0).foreach { s =>
        push("wall", triGeo, Util.trs(0.0, h, -0.2 + s * ((d - 0.4) / 2.0 - 0.08)))
      }
      triGeo.dispose()
    else
      push("roof", new BoxGeometry(w + 0.3, 0.2, (if floors == 2 then d - 0.4 else d) + 0.3),
        Util.trs(0.0, h + 0.1, -0.2))
      Vector(-1.0, 1.0).foreach { s =>
        push("roof", new BoxGeometry(w + 0.3, 0.34, 0.14),
          Util.trs(0.0, h + 0.37, -0.2 + s * ((if floors == 2 then d - 0.4 else d) / 2.0 + 0.14)))
        push("roof", new BoxGeometry(0.14, 0.34, (if floors == 2 then d - 0.4 else d) + 0.3),
          Util.trs(s * (w / 2.0 + 0.14), h + 0.37, -0.2))
      }
    end if

    val openW = math.min(w - 1.0, o.openW.getOrElse(w - 1.0))
    val pierW = (w - openW) / 2.0
    Vector(-1.0, 1.0).foreach { s =>
      push("wall", new BoxGeometry(pierW, h1, rec),
        Util.trs(s * (w - pierW) / 2.0, h1 / 2.0, front - rec / 2.0))
      push("tile", new BoxGeometry(pierW + 0.04, 0.62, 0.06),
        Util.trs(s * (w - pierW) / 2.0, 0.31, front + 0.02))
    }
    push("wall", new BoxGeometry(openW, h1 - 2.55, rec),
      Util.trs(0.0, h1 - (h1 - 2.55) / 2.0, front - rec / 2.0))
    push("trim", new BoxGeometry(openW, 0.1, rec), Util.trs(0.0, 2.5, front - rec / 2.0))
    push("tile", new BoxGeometry(openW, 0.1, rec + 0.1),
      Util.trs(0.0, 0.05, front - rec / 2.0 + 0.05))

    val interiorMap = o.interiorKind.map(k => Textures.showaInterior(k))
    val inner = new Mesh(new PlaneGeometry(openW - 0.1, 2.2),
      Toon.flat(FlatOptions(color = 0x8b8598,
        map = Some(interiorMap.getOrElse(Textures.shopInterior(o.interior.getOrElse(0)))),
        cache = false)))
    inner.position.set(0.0, 1.35, front - rec + 0.03)
    ThreeInterop.setUserDataFlag(inner, "noOutline")
    ThreeInterop.addChild(g, inner)

    val glassW = openW
    val pane = Util.box(glassW, 2.3, 0.04,
      Toon.flat(FlatOptions(color = Pal.glass, transparent = true, opacity = 0.22,
        depthWrite = Some(false), cache = false)),
      0.0, 1.35, front - 0.07)
    ThreeInterop.setUserDataFlag(pane, "noOutline")
    ThreeInterop.setUserDataFlag(pane, "noShadow")
    ThreeInterop.addChild(g, pane)
    val nm = math.max(2, math.round(glassW / 1.3).toInt)
    (0 to nm).foreach { i =>
      push("metal", new BoxGeometry(0.08, 2.35, 0.1),
        Util.trs(-glassW / 2.0 + (glassW / nm) * i, 1.35, front - 0.07))
    }
    push("metal", new BoxGeometry(glassW + 0.1, 0.1, 0.14), Util.trs(0.0, 2.5, front - 0.07))
    push("metal", new BoxGeometry(glassW + 0.1, 0.14, 0.16), Util.trs(0.0, 0.2, front - 0.07))
    val highlight = new Mesh(new PlaneGeometry(glassW * 0.24, 2.5),
      Toon.flat(FlatOptions(color = 0xf2f8ff, transparent = true, opacity = 0.18,
        depthWrite = Some(false), cache = false)))
    highlight.position.set(-glassW * 0.22, 1.4, front - 0.04)
    highlight.rotation.z = 0.3
    ThreeInterop.setUserDataFlag(highlight, "noOutline")
    ThreeInterop.addChild(g, highlight)

    o.shutter.filter(_ != 0.0).foreach { sh =>
      val texture = Textures.shutterTex(22)
      texture.wrapS = 1000
      texture.wrapT = 1000
      val sh2 = 2.55 * sh
      val slat = Util.box(openW - 0.1, sh2, 0.06,
        Toon.cel(CelOptions(color = Pal.shutter, bands = 3, map = Some(texture), tint = 0x4b4560,
          cache = false)),
        0.0, 2.55 - sh2 / 2.0, front - 0.16)
      slat.castShadow = true
      slat.receiveShadow = true
      ThreeInterop.addChild(g, slat)
      push("metalDark", new BoxGeometry(openW + 0.14, 0.28, 0.2), Util.trs(0.0, 2.66, front - 0.16))
      push("metalDark", new BoxGeometry(openW, 0.1, 0.1), Util.trs(0.0, 2.55 - sh2, front - 0.16))
    }

    o.kind.filter(_ => o.fascia).foreach { kind =>
      val lit     = o.signBox
      val dep     = if lit then 0.26 else 0.12
      val out     = if lit then 0.11 else 0.02
      val sideMat = Toon.flat(FlatOptions(color = if lit then 0x3c3947 else Pal.wallGray))
      val topMat  = Toon.flat(FlatOptions(color = if lit then 0x4a4653 else Pal.wallWhite))
      val boardMats = Props.matArray(
        sideMat, sideMat, topMat, sideMat,
        Toon.flat(FlatOptions(color = if lit then 0xfff0d2 else 0xffffff,
          map = Some(Textures.shopFascia(kind)), cache = false)),
        topMat
      )
      val board = new Mesh(new BoxGeometry(w - 0.2, 0.72, dep), boardMats)
      board.position.set(0.0, h1 + 0.44, front + out)
      board.castShadow = true
      ThreeInterop.addChild(g, board)
      Outline.hullOutline(board, OutlineOptions(thickness = 0.0032))
      if lit then
        push("metalDark", new BoxGeometry(w - 0.04, 0.92, dep - 0.06),
          Util.trs(0.0, h1 + 0.44, front + out - 0.05))
      push("metal", new BoxGeometry(w - 0.14, 0.1, 0.2), Util.trs(0.0, h1 + 0.86, front + out))
    }

    o.awning.foreach { awningIndex =>
      val out     = o.awningOut
      val yA      = h1 - 0.16
      val drop    = 0.4
      val stripes = math.max(6, math.round(w / 0.42).toInt)
      val a       = mutable.ArrayBuffer.empty[Util.BakePart]
      val b       = mutable.ArrayBuffer.empty[Util.BakePart]
      val sw      = w / stripes
      (0 until stripes).foreach { i =>
        val bucket = if i % 2 == 0 then a else b
        bucket += Util.BakePart(new BoxGeometry(sw, 0.07, out),
          Some(Util.trs(-w / 2.0 + sw * (i + 0.5), 0.0, out / 2.0)))
      }
      val grp = new Group()
      grp.position.set(0.0, yA, front)
      val cA = M.awnings(awningIndex % Awnings.length)
      val cB = Toon.cel(CelOptions(color = 0xf6f1e6, bands = 3, tint = 0x6f5680))
      val mA = new Mesh(Util.bake(a.toList), cA)
      val mB = new Mesh(Util.bake(b.toList), cB)
      mA.castShadow = true
      mB.castShadow = true
      mA.receiveShadow = true
      mB.receiveShadow = true
      ThreeInterop.addChild(grp, mA)
      ThreeInterop.addChild(grp, mB)
      grp.rotation.x = math.atan2(drop, out)
      ThreeInterop.addChild(g, grp)
      Outline.hullOutline(mA, OutlineOptions(thickness = 0.003))

      val edgeZ  = front + out * math.cos(grp.rotation.x)
      val edgeY  = yA - out * math.sin(grp.rotation.x)
      val fascia = Util.box(w, 0.3, 0.07, cA, 0.0, edgeY - 0.13, edgeZ - 0.03)
      fascia.castShadow = true
      ThreeInterop.addChild(g, fascia)
      val scallopGeo = new CircleGeometry(0.14, 10)
      val scallops   = mutable.ArrayBuffer.empty[Util.BakePart]
      val n          = math.round(w / 0.5).toInt
      (0 until n).foreach { i =>
        scallops += Util.BakePart(scallopGeo, Some(Util.trs(-w / 2.0 + (w / n) * (i + 0.5), 0.0, 0.0)))
      }
      val scm = new Mesh(Util.bake(scallops.toList), cA)
      scm.position.set(0.0, edgeY - 0.28, edgeZ - 0.01)
      ThreeInterop.addChild(g, scm)
      Vector(-1.0, 1.0).foreach { sx =>
        val arm = Util.box(0.06, 0.06, out, M.metalDark, 0.0, 0.0, 0.0)
        arm.position.set(sx * (w / 2.0 - 0.3), yA - 0.05 - drop / 2.0, front + out / 2.0)
        arm.rotation.x = grp.rotation.x
        ThreeInterop.addChild(g, arm)
      }
    }

    o.blade.foreach { bladeKind =>
      val bw    = 0.52
      val bh    = 1.9
      val bSide = o.bladeSide.getOrElse(1.0)
      val bx    = bSide * (w / 2.0 - 0.18)
      val art   = Textures.shopBlade(bladeKind)
      val gray  = Toon.flat(FlatOptions(color = Pal.wallGray))
      val boardMats = Props.matArray(
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(art), cache = false)),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(art), cache = false)),
        gray, gray, gray, gray
      )
      val board = new Mesh(new BoxGeometry(0.1, bh, bw), boardMats)
      board.position.set(bx + bSide * 0.5, h1 + 1.5, front + 0.1)
      board.castShadow = true
      ThreeInterop.addChild(g, board)
      Outline.hullOutline(board, OutlineOptions(thickness = 0.003))
      push("metal", new BoxGeometry(bSide * 0.62, 0.08, 0.08),
        Util.trs(bx + bSide * 0.28, h1 + 2.32, front + 0.1))
      push("metal", new BoxGeometry(bSide * 0.62, 0.08, 0.08),
        Util.trs(bx + bSide * 0.28, h1 + 0.7, front + 0.1))
    }

    o.noren.foreach { norenKind =>
      val nw     = math.min(2.4, openW * 0.7)
      val norenX = o.norenX.getOrElse(0.0)
      val cloth = new Mesh(new PlaneGeometry(nw, 0.72),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.norenTex(norenKind)),
          side = 2, cache = false)))
      cloth.position.set(norenX, 2.06, front - 0.22)
      cloth.rotation.z = 0.025
      cloth.castShadow = true
      ThreeInterop.addChild(g, cloth)
      push("metalDark", new BoxGeometry(nw + 0.2, 0.05, 0.05), Util.trs(norenX, 2.44, front - 0.22))
    }

    if floors == 2 then
      val cols = math.max(1, math.floor((w - 0.8) / 1.9).toInt)
      (0 until cols).foreach { i =>
        val px    = -w / 2.0 + (w * (i + 1)) / (cols + 1)
        val isLit = o.lit && rng.chance(0.55)
        push("metal", new BoxGeometry(1.5, 1.3, 0.16), Util.trs(px, h1 + 1.4, front - 0.2 + 0.08))
        val paneMat =
          if isLit then
            Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.litWindowTex(i % 3)),
              cache = false))
          else Toon.flat(FlatOptions(color = Pal.glassDark))
        ThreeInterop.addChild(g, Util.box(1.34, 1.14, 0.06, paneMat, px, h1 + 1.4, front - 0.14))
        push("metal", new BoxGeometry(0.07, 1.14, 0.07), Util.trs(px, h1 + 1.4, front - 0.12))
        push("trim", new BoxGeometry(1.7, 0.09, 0.22), Util.trs(px, h1 + 0.72, front - 0.16))
        if rng.chance(0.5) then
          val curtain = new Mesh(new PlaneGeometry(0.6, 1.06),
            Toon.flat(FlatOptions(color = 0xe8e0d2, map = Some(Textures.curtainTex(i % 2)),
              cache = false)))
          curtain.position.set(px + rng.sign * 0.34, h1 + 1.4, front - 0.11)
          ThreeInterop.setUserDataFlag(curtain, "noOutline")
          ThreeInterop.addChild(g, curtain)
      }
      if o.balcony then
        push("trim", new BoxGeometry(w - 0.6, 0.1, 0.8), Util.trs(0.0, h1 + 0.5, front + 0.2))
        push("metal", new BoxGeometry(w - 0.6, 0.06, 0.06), Util.trs(0.0, h1 + 1.42, front + 0.58))
        push("metalDark", new BoxGeometry(w - 0.6, 0.06, 0.06), Util.trs(0.0, h1 + 0.62, front + 0.58))
        val nb = math.round((w - 0.6) / 0.24).toInt
        (0 to nb).foreach { i =>
          push("metal", new BoxGeometry(0.04, 0.9, 0.04),
            Util.trs(-(w - 0.6) / 2.0 + ((w - 0.6) / nb) * i, h1 + 0.98, front + 0.58))
        }
    end if

    {
      val sd = rng.sign.toDouble
      push("metal", new CylinderGeometry(0.055, 0.055, h, 6),
        Util.trs(sd * (w / 2.0 - 0.1), h / 2.0, -(d - rec) / 2.0 - 0.1 + 0.02))
      val meter = new Mesh(new BoxGeometry(0.3, 0.44, 0.1),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.meterBox()), cache = false)))
      meter.position.set(-sd * (w / 2.0 - 0.42), 1.8, front - rec + 0.06)
      ThreeInterop.addChild(g, meter)
      if o.posters.getOrElse(true) then
        val n = math.min(rng.int(1, 2), o.posterLimit.getOrElse(2))
        (0 until n).foreach { _ =>
          val p = new Mesh(new PlaneGeometry(0.38, 0.52),
            Toon.flat(FlatOptions(color = 0xffffff,
              map = Some(Textures.poster(rng.int(0, 3))), cache = false)))
          p.position.set(rng.range(-w / 2.0 + 0.6, w / 2.0 - 0.6), 1.72 + rng.range(-0.1, 0.1),
            front + 0.03)
          p.rotation.z = rng.range(-0.03, 0.03)
          ThreeInterop.setUserDataFlag(p, "noOutline")
          ThreeInterop.addChild(g, p)
        }
    }

    val matFor: Map[String, Material[?]] = Map(
      "wall"      -> wallMat, "trim"     -> M.trim, "roof" -> roofMat, "metal" -> M.metal,
      "metalDark" -> M.metalDark, "tile" -> M.tile
    )
    parts.foreach { (key, bucket) =>
      if bucket.nonEmpty then
        val mesh = new Mesh(Util.bake(bucket.toList), matFor(key))
        mesh.castShadow = true
        mesh.receiveShadow = true
        ThreeInterop.addChild(g, mesh)
        if key == "wall" || key == "roof" then
          Outline.hullOutline(mesh, OutlineOptions(thickness = 0.0032))
    }

    g.position.set(o.x, o.y, o.z)
    g.rotation.y = Buildings.faceRy(o.face)
    ctx.add(g)
    val swap = o.face == Buildings.Face.XPlus || o.face == Buildings.Face.XMinus
    val hw   = (if swap then d else w) / 2.0
    val hd   = (if swap then w else d) / 2.0
    ctx.collide(o.x - hw - 0.05, o.z - hd - 0.05, o.x + hw + 0.05, o.z + hd + 0.05, o.y + h)
    g
  end buildUnit

  def buildShotengai(ctx: WorldCtx): Planting =
    val gm       = Ground.groundMats()
    val sakura   = mutable.ArrayBuffer.empty[SakuraSpot]
    val shrubs   = mutable.ArrayBuffer.empty[ShrubSpot]
    val grove    = mutable.ArrayBuffer.empty[GroveSpot]
    val petals   = mutable.ArrayBuffer.empty[PetalPatch]
    val lanterns = mutable.ArrayBuffer.empty[StreetLantern]

    Ground.lane(ctx,
      Ground.LaneOptions(axis = "z", at = CX, from = ZS - 0.6, to = ZN, w = X1 - X0,
        mat = Some(gm.asphaltWorn), kerb = false, rise = 0.05, name = "shotengaiRoad"))
    Vector(-1.0, 1.0).foreach { s =>
      Ground.lane(ctx,
        Ground.LaneOptions(axis = "z", at = CX + s * ((X1 - X0) / 2.0 - 0.55),
          from = ZS - 0.6, to = ZN, w = 1.1, mat = Some(gm.concrete), kerb = false, rise = 0.13,
          name = "shotengaiWalk"))
    }
    Ground.pad(ctx,
      Ground.PadOptions(x = (4.9 + X0) / 2.0, z = AlleyZ, w = X0 - 4.9, d = 2.4,
        y = Street.groundY(AlleyZ), h = 0.1, mat = Some(gm.concrete), name = "shotengaiAlley"))
    Ground.pad(ctx,
      Ground.PadOptions(x = (1.9 + 40.0) / 2.0, z = NorthZ, w = 38.1, d = 3.4,
        y = Street.groundY(NorthZ), h = 0.09, mat = Some(gm.asphaltWorn), name = "shotengaiNorthLane"))
    Ground.laneLine(ctx,
      Ground.LineOptions(axis = "x", at = NorthZ, from = 4.0, to = 38.0,
        y = Street.groundY(NorthZ) + 0.11, dash = 1.1))
    Ground.pad(ctx,
      Ground.PadOptions(x = CX, z = (ZN + NorthZ) / 2.0, w = X1 - X0 + 1.4,
        d = NorthZ - ZN + 1.8, y = Street.groundY(ZN), h = 0.08, mat = Some(gm.asphaltWorn),
        name = "shotengaiJunction"))

    {
      val n = math.round((ZN - ZS) / 0.9).toInt
      val parts = (0 until n).map { i =>
        val z = ZS + (i + 0.5) * ((ZN - ZS) / n)
        Util.BakePart(new BoxGeometry(0.34, 0.04, 0.72), Some(Util.trs(CX, Street.groundY(z) + 0.055, z)))
      }
      val channel = new Mesh(Util.bake(parts.toList), M.drain)
      channel.receiveShadow = true
      ctx.add(channel)
    }

    {
      val y  = Street.groundY(AlleyZ)
      val aw = 3.6
      val ah = 3.9
      Vector(-1.0, 1.0).foreach { s =>
        val post = Util.cyl(0.13, 0.16, ah, 8, M.metal, 5.6, y + ah / 2.0, AlleyZ + s * (aw / 2.0))
        post.castShadow = true
        ctx.add(post)
        ctx.add(Util.cyl(0.22, 0.26, 0.2, 8, M.concreteMid, 5.6, y + 0.1, AlleyZ + s * (aw / 2.0)))
        ctx.collide(5.44, AlleyZ + s * (aw / 2.0) - 0.18, 5.76, AlleyZ + s * (aw / 2.0) + 0.18, y + ah)
      }
      val art = Textures.shopFascia(ShopKind.Wagashi)
      val board = new Mesh(
        new BoxGeometry(0.14, 0.95, aw + 0.5),
        Props.matArray(
          Toon.flat(FlatOptions(color = 0xffffff, map = Some(art), cache = false)),
          Toon.flat(FlatOptions(color = 0xffffff, map = Some(art), cache = false)),
          Toon.flat(FlatOptions(color = Pal.wallWhite)), Toon.flat(FlatOptions(color = Pal.wallGray)),
          Toon.flat(FlatOptions(color = Pal.wallWhite)), Toon.flat(FlatOptions(color = Pal.wallWhite))
        )
      )
      board.position.set(5.6, y + ah - 0.3, AlleyZ)
      board.castShadow = true
      ctx.add(board)
      Outline.hullOutline(board, OutlineOptions(thickness = 0.0034))
      ctx.add(Util.box(0.3, 0.12, aw + 0.7, M.metalDark, 5.6, y + ah + 0.22, AlleyZ))
      Vector(-1.0, 1.0).foreach { s =>
        ctx.add(lantern(lanterns, x = 5.6, y = y + ah - 0.95, z = AlleyZ + s * (aw / 2.0 - 0.5),
          variant = 1, r = 0.17, drop = 0.26, along = 0.0))
      }
    }

    {
      val y   = Street.groundY(AlleyZ)
      val acZ = 16.5 - (0.15 + 0.09)
      ctx.add(
        Props.makeAircon(Props.AirconOpts(x = 8.4, z = acZ, y = y + 1.6, ry = math.Pi, w = 0.8, h = 0.56, feet = false))
      )
      ctx.add(Props.makeAircon(
        Props.AirconOpts(x = 11.3, z = acZ, y = y + 2.05, ry = math.Pi, w = 0.74, h = 0.52, feet = false)
      ))
      ctx.add(
        Props.makePlanter(Props.PlanterOpts(x = 7.4, z = AlleyZ - 0.9, y = y, r = 0.24, flower = true, seed = 61, n = 5))
      )
      ctx.add(
        Props.makePlanter(Props.PlanterOpts(x = 8.1, z = AlleyZ - 0.95, y = y, r = 0.2, flower = false, seed = 62, n = 4))
      )
      ctx.add(Props.makeCrates(Props.CratesOpts(x = 10.4, z = AlleyZ + 0.85, y = y, n = 3, seed = 63, ry = 0.15)))
      ctx.add(Props.makeCatBox(Props.CatBoxOpts(x = 13.9, z = AlleyZ + 0.9, y = y, ry = -0.5)))
      ctx.add(Props.makeBins(Props.BinsOpts(x = 16.4, z = AlleyZ - 0.85, y = y, ry = 0.1)))
      Props.makeLoosePaper(propCtx(ctx),
        Vector(
          Props.PaperSpot(x = 11.6, z = AlleyZ - 0.6, y = y + 0.1, ry = 0.9),
          Props.PaperSpot(x = 15.1, z = AlleyZ + 0.5, y = y + 0.1, ry = -0.4)
        ))
      val runs = mutable.ArrayBuffer.empty[Vector3]
      (0 until 5).foreach { i =>
        val x = 7.0 + i * 2.6
        ctx.add(lantern(lanterns, x = x, y = y + 2.85, z = AlleyZ, variant = 0, r = 0.15,
          drop = 0.22, along = 0.5 + i * 0.4))
        runs += new Vector3(x, y + 3.1, AlleyZ)
      }
      val wire = new Mesh(new TubeGeometry(Util.sagCurve(runs.head, runs.last, 0.35, 16), 18, 0.016, 4, false),
        Toon.cel(CelOptions(color = 0x4c4658, bands = 2, tint = 0x413c58)))
      ctx.add(wire)
    }

    val westDepth = 4.6
    val eastDepth = 5.6
    val wx        = X0 - westDepth / 2.0
    val ex        = X1 + eastDepth / 2.0

    Vector(
      UnitOptions(x = wx, d = 4.6, awningOut = 1.4, w = 22.2 - 16.6, wall = Some(3), roof = Some(3), awning = Some(0),
        floors = 2, interior = Some(2), lit = true, kind = Some(ShopKind.Hana),
        z = (16.6 + 22.2) / 2.0, face = Buildings.Face.XPlus, openW = Some(22.2 - 16.6 - 1.4), seed = 116),
      UnitOptions(x = wx, d = 4.6, awningOut = 1.4, w = 27.2 - 22.4, wall = Some(1), roof = Some(2), awning = Some(3),
        floors = 2, interior = Some(2), blade = Some(ShopKind.Kosho), bladeSide = Some(-1.0), shutter = Some(0.0),
        signBox = true, kind = Some(ShopKind.Kosho),
        z = (22.4 + 27.2) / 2.0, face = Buildings.Face.XPlus, openW = Some(27.2 - 22.4 - 1.4), seed = 122),
      UnitOptions(x = wx, d = 4.6, awningOut = 1.4, w = 31.8 - 27.4, wall = Some(2), roof = Some(1), awning = Some(2),
        floors = 1, interior = Some(2), shutter = Some(0.34), signBox = true, kind = Some(ShopKind.Cleaning),
        z = (27.4 + 31.8) / 2.0, face = Buildings.Face.XPlus, openW = Some(31.8 - 27.4 - 1.4), seed = 127),
      UnitOptions(x = wx, d = 4.6, awningOut = 1.4, w = 35.0 - 32.0, wall = Some(4), roof = Some(0), awning = Some(3),
        floors = 1, interior = Some(0), kind = Some(ShopKind.Sozai),
        z = (32.0 + 35.0) / 2.0, face = Buildings.Face.XPlus, openW = Some(35.0 - 32.0 - 1.4), seed = 132),
      UnitOptions(x = wx, d = 4.6, awningOut = 1.4, w = 38.3 - 35.2, wall = Some(7), roof = Some(1), floors = 2,
        interiorKind = Some(1), blade = Some(ShopKind.Denki), bladeSide = Some(-1.0), shutter = Some(0.0),
        kind = Some(ShopKind.Denki),
        z = (35.2 + 38.3) / 2.0, face = Buildings.Face.XPlus, openW = Some(38.3 - 35.2 - 1.4), seed = 135),
      UnitOptions(x = wx, d = 4.6, awningOut = 1.4, w = 41.6 - 38.5, wall = Some(6), roof = Some(2), awning = Some(4),
        floors = 2, interiorKind = Some(0), blade = Some(ShopKind.Record), bladeSide = Some(-1.0),
        noren = Some(Textures.NorenKind.Record), posters = Some(false), kind = Some(ShopKind.Record),
        z = (38.5 + 41.6) / 2.0, face = Buildings.Face.XPlus, openW = Some(41.6 - 38.5 - 1.4), seed = 138)
    ).map(unit => unit.copy(y = Street.groundY(unit.z))).foreach(unit => buildUnit(ctx, unit))

    Vector(
      UnitOptions(x = ex, d = eastDepth, w = 22.4 - 16.8, wall = Some(1), roof = Some(0),
        awning = Some(4), floors = 2, interior = Some(1), blade = Some(ShopKind.Ramen),
        noren = Some(Textures.NorenKind.Ramen), lit = true, posterLimit = Some(1), kind = Some(ShopKind.Ramen),
        z = (16.8 + 22.4) / 2.0, face = Buildings.Face.XMinus, awningOut = 1.5,
        openW = Some(22.4 - 16.8 - 1.4), seed = 216),
      UnitOptions(x = ex, d = eastDepth, w = 27.4 - 22.6, wall = Some(5), roof = Some(2),
        awning = Some(1), floors = 1, interior = Some(2), noren = Some(Textures.NorenKind.Wagashi),
        kind = Some(ShopKind.Wagashi),
        z = (22.6 + 27.4) / 2.0, face = Buildings.Face.XMinus, awningOut = 1.5,
        openW = Some(27.4 - 22.6 - 1.4), seed = 222),
      UnitOptions(x = ex, d = eastDepth, w = 34.8 - 30.2, wall = Some(0), roof = Some(3),
        awning = Some(1), floors = 2, interior = Some(2), blade = Some(ShopKind.Bunbo),
        lit = true, balcony = true, signBox = true, kind = Some(ShopKind.Bunbo),
        z = (30.2 + 34.8) / 2.0, face = Buildings.Face.XMinus, awningOut = 1.5,
        openW = Some(34.8 - 30.2 - 1.4), seed = 230)
    ).map(unit => unit.copy(y = Street.groundY(unit.z))).foreach(unit => buildUnit(ctx, unit))

    Ground.pad(ctx,
      Ground.PadOptions(x = (X1 + 31.8) / 2.0, z = 28.8, w = 31.8 - X1, d = 2.6,
        y = Street.groundY(28.8), h = 0.1, mat = Some(gm.concrete), name = "parkLane"))

    {
      val z0 = 35.4
      val z1 = 43.4
      val zc = (z0 + z1) / 2.0
      val d  = 8.4
      val bx = X1 + d / 2.0
      val y  = Street.groundY(zc)
      buildUnit(ctx,
        UnitOptions(x = bx, z = zc, y = y, w = z1 - z0, d = d,
          face = Buildings.Face.XMinus, kind = Some(ShopKind.Sento), floors = 1, h1 = 4.4,
          wall = Some(0), roof = Some(0), roofKind = Some("gable"), roofH = 2.3, interior = Some(3),
          openW = Some(4.6), shutter = None, blade = None, noren = Some(Textures.NorenKind.Sento),
          posters = Some(false), seed = 301, recess = 1.1, fascia = false))

      {
        val bw    = 4.9
        val bh    = 1.6
        val byf   = y + 3.6
        val art   = Textures.sentoFuji()
        val frame = Util.box(0.1, bh + 0.18, bw + 0.18, M.metalDark, X1 - 0.05, byf, zc)
        frame.castShadow = true
        ctx.add(frame)
        val board = new Mesh(
          new BoxGeometry(0.1, bh, bw),
          Props.matArray(
            Toon.flat(FlatOptions(color = 0xffffff, map = Some(art), cache = false)),
            Toon.flat(FlatOptions(color = 0xffffff, map = Some(art), cache = false)),
            Toon.flat(FlatOptions(color = Pal.wallGray)), Toon.flat(FlatOptions(color = Pal.wallGray)),
            Toon.flat(FlatOptions(color = Pal.wallGray)), Toon.flat(FlatOptions(color = Pal.wallGray))
          )
        )
        board.position.set(X1 - 0.11, byf, zc)
        board.castShadow = true
        ctx.add(board)
        Outline.hullOutline(board, OutlineOptions(thickness = 0.0032))
        Vector(-1.0, 1.0).foreach { s =>
          ctx.add(Util.box(0.26, 0.09, 0.5, M.metalDark, X1 - 0.2, byf + bh / 2.0 + 0.2, zc + s * 1.5))
        }
      }

      {
        val fx        = X1
        val lockerMat = Toon.cel(CelOptions(color = 0xb99a72, bands = 3, tint = 0x6f5680))
        val step = Util.box(0.44, 0.16, 4.4,
          Toon.cel(CelOptions(color = 0xd8cdb8, bands = 3, tint = 0x6a6288)), fx + 0.84, y + 0.14, zc)
        step.castShadow = true
        step.receiveShadow = true
        ctx.add(step)
        ctx.add(Util.box(0.06, 0.2, 4.4, M.wood, fx + 0.63, y + 0.12, zc))
        val bodies = mutable.ArrayBuffer.empty[Util.BakePart]
        val trim   = mutable.ArrayBuffer.empty[Util.BakePart]
        (0 until 3).foreach { i =>
          val lz = zc - 1.85 + i * 0.62
          bodies += Util.BakePart(new BoxGeometry(0.34, 1.62, 0.58), Some(Util.trs(fx + 0.86, y + 1.03, lz)))
          (0 until 4).foreach { r =>
            trim += Util.BakePart(new BoxGeometry(0.04, 0.035, 0.52),
              Some(Util.trs(fx + 0.69, y + 0.46 + r * 0.39, lz)))
            trim += Util.BakePart(new BoxGeometry(0.03, 0.1, 0.05),
              Some(Util.trs(fx + 0.69, y + 0.4 + r * 0.39, lz + 0.19)))
          }
        }
        val lm = new Mesh(Util.bake(bodies.toList), lockerMat)
        lm.castShadow = true
        lm.receiveShadow = true
        ctx.add(lm)
        ctx.add(new Mesh(Util.bake(trim.toList),
          Toon.cel(CelOptions(color = 0x8a6f52, bands = 2, tint = 0x5c5680))))

        val mat0 = new Mesh(new PlaneGeometry(0.5, 1.5),
          Toon.cel(CelOptions(color = 0x6e6a78, bands = 2, tint = 0x545070)))
        mat0.rotation.set(-math.Pi / 2.0, 0.0, 0.0)
        mat0.position.set(fx + 0.34, y + 0.11, zc + 0.5)
        ctx.add(mat0)
        Vector((0.0, 0.22), (0.17, -0.1)).foreach { case (dz, rot) =>
          val sole = Util.box(0.24, 0.03, 0.1,
            Toon.cel(CelOptions(color = 0x4f6f86, bands = 2, tint = 0x545070)), fx + 0.4, y + 0.185, zc + 1.5 + dz)
          sole.rotation.y = rot
          ctx.add(sole)
        }
      }

      val chx = bx + 2.6
      val chz = z0 + 1.6
      val stack = Util.cyl(0.42, 0.5, 9.0, 10,
        Toon.cel(CelOptions(color = 0xb08272, bands = 3, tint = 0x6f5680)), chx, y + 4.5, chz)
      stack.castShadow = true
      stack.receiveShadow = true
      ctx.add(stack)
      Outline.hullOutline(stack, OutlineOptions(thickness = 0.0034))
      ctx.add(Util.cyl(0.6, 0.6, 0.3, 10, M.concreteMid, chx, y + 9.1, chz))
      (0 until 3).foreach { i =>
        ctx.add(Util.cyl(0.53, 0.53, 0.14, 10,
          Toon.cel(CelOptions(color = 0x9c7268, bands = 3, tint = 0x6f5680)), chx, y + 2.2 + i * 2.4, chz))
      }
      ctx.collide(chx - 0.55, chz - 0.55, chx + 0.55, chz + 0.55, y + 9.2)

      ctx.add(Props.makeMilkCrate(Props.MilkCrateOpts(x = X1 + 0.8, z = zc + 2.6, y = y, n = 2, ry = -math.Pi / 2.0)))
      ctx.add(Props.makeUmbrellaStand(Props.UmbrellaStandOpts(x = X1 + 0.7, z = zc - 2.5, y = y, n = 4, seed = 302)))
      ctx.add(
        Props.makeBench(Props.BenchOpts(x = X1 + 0.9, z = zc + 3.4, y = y, ry = -math.Pi / 2.0, len = 1.4, back = false))
      )
      ctx.add(Props.makeBikeRack(Props.BikeRackOpts(x = X1 - 2.05, z = zc - 3.6,
        y = Street.groundY(zc - 3.6) + 0.08, n = 4, spacing = 0.66, ry = 0.0, seed = 22)))
      ctx.add(lantern(lanterns, x = X1 + 0.5, y = y + 3.1, z = zc + 0.1, variant = 2, r = 0.24, drop = 0.3, along = 30.0))
      ctx.add(Util.box(0.7, 0.06, 0.06, M.metalDark, X1 + 0.85, y + 3.4, zc + 0.1))
    }

    Showa.dressShowa(ctx, x = X0, denki = Showa.Frontage(z0 = 35.2, z1 = 38.3),
      record = Showa.Frontage(z0 = 38.5, z1 = 41.6))

    {
      val geometries = mutable.ArrayBuffer.empty[BufferGeometry[?, ?]]
      (0 until 7).foreach { i =>
        val z  = ZS + 2.2 + i * 3.7
        val wy = Street.groundY(z) + 4.4
        val a  = new Vector3(X0 - 0.2, wy, z)
        val b  = new Vector3(X1 + 0.2, wy, z)
        geometries += new TubeGeometry(Util.sagCurve(a, b, 0.5, 10), 12, 0.015, 4, false)
        Vector(0.32, 0.68).foreach { t =>
          val lx = X0 - 0.2 + (X1 - X0 + 0.4) * t
          val ly = wy - math.sin(math.Pi * t) * 0.5 * math.min(1.6, (X1 - X0) / 14.0)
          ctx.add(lantern(lanterns, x = lx, y = ly, z = z, variant = i % 2, r = 0.155,
            drop = 0.3, along = 3.0 + i))
        }
      }
      Vector(-1.0, 1.0).foreach { s =>
        val x   = CX + s * ((X1 - X0) / 2.0 - 0.15)
        val pts = mutable.ArrayBuffer.empty[Vector3]
        var z   = ZS
        while z <= ZN do
          pts += new Vector3(x, Street.groundY(z) + 5.1, z)
          z += 4.0
        geometries += new TubeGeometry(new CatmullRomCurve3(js.Array(pts.toVector*)), 24, 0.02, 4, false)
      }
      val merged = Util.bake(geometries.map(geometry => Util.BakePart(geometry)).toList)
      val mesh   = new Mesh(merged, M.wire)
      mesh.name = "shotengaiWires"
      ctx.add(mesh)
      geometries.foreach(_.dispose())
    }

    {
      val wEdge                 = X0 + 0.15
      val eEdge                 = X1 - 0.15
      def g0(z: Double): Double = Street.groundY(z) + 0.13

      ctx.add(Shops.makeProduceStack(Shops.ProduceStackOpts(x = wEdge + 0.5, z = 17.6, y = g0(17.6),
        ry = math.Pi / 2.0, seed = 401)))
      (0 until 4).foreach { i =>
        ctx.add(Props.makePlanter(Props.PlanterOpts(x = wEdge + 0.42, z = 19.2 + i * 0.62,
          y = g0(19.2), r = 0.24, flower = true, seed = 410 + i, n = 5)))
      }
      ctx.add(
        Shops.makeShopFlag(Shops.ShopFlagOpts(x = wEdge + 0.5, z = 21.6, y = g0(21.6), variant = 1, ry = -math.Pi / 2.0))
      )
      ctx.add(Props.makeCrates(Props.CratesOpts(x = wEdge + 0.5, z = 23.2, y = g0(23.2), n = 3, seed = 402, ry = -0.3)))
      ctx.add(Props.makeCrates(Props.CratesOpts(x = wEdge + 0.5, z = 24.0, y = g0(24.0), n = 2, seed = 403, ry = 0.2)))
      ctx.add(Props.makeBikeRack(
        Props.BikeRackOpts(x = wEdge + 1.05, z = 26.2, y = g0(26.2), n = 3, spacing = 0.64, ry = math.Pi, seed = 31)
      ))
      ctx.add(Props.makeBins(Props.BinsOpts(x = wEdge + 0.5, z = 30.6, y = g0(30.6), ry = -math.Pi / 2.0)))
      ctx.add(
        Props.makeUmbrellaStand(Props.UmbrellaStandOpts(x = wEdge + 0.45, z = 28.2, y = g0(28.2), n = 3, seed = 404))
      )
      ctx.add(
        Props.makeBroom(Props.BroomOpts(x = wEdge + 0.3, z = 27.8, y = g0(27.8), tilt = 0.03, roll = 0.17, ry = 1.2))
      )
      ctx.add(Props.makeBucket(Props.BucketOpts(x = wEdge + 0.34, z = 27.35, y = g0(27.35), ry = 0.5)))
      ctx.add(
        Props.makeBucket(Props.BucketOpts(x = wEdge + 0.5, z = 27.1, y = g0(27.1), ry = -0.8, tilt = 0.1, roll = 0.06))
      )
      ctx.add(
        Props.makeBroom(Props.BroomOpts(x = eEdge - 0.28, z = 22.2, y = g0(22.2), tilt = -0.04, roll = -0.17, ry = -1.4))
      )
      ctx.add(Props.makeBucket(Props.BucketOpts(x = eEdge - 0.36, z = 24.95, y = g0(24.95), ry = 1.1, water = true)))
      ctx.add(
        Shops.makeMenuBoard(Shops.MenuBoardOpts(x = eEdge - 0.55, z = 18.0, y = g0(18.0), ry = -math.Pi / 2.0 - 0.25))
      )
      ctx.add(Props.makeCrates(Props.CratesOpts(x = eEdge - 0.5, z = 21.4, y = g0(21.4), n = 4, seed = 405, ry = 0.25)))
      ctx.add(
        Shops.makeShopFlag(Shops.ShopFlagOpts(x = eEdge - 0.5, z = 16.9, y = g0(16.9), variant = 3, ry = -math.Pi / 2.0))
      )
      ctx.add(Shops.makeFreezer(Shops.FreezerOpts(x = eEdge - 0.6, z = 23.6, y = g0(23.6), ry = -math.Pi / 2.0)))
      ctx.collide(eEdge - 1.0, 23.0, eEdge - 0.2, 24.2, g0(23.6) + 0.9)
      ctx.add(Props.makeBench(
        Props.BenchOpts(x = eEdge - 0.55, z = 26.2, y = g0(26.2), ry = -math.Pi / 2.0, len = 1.5, back = false)
      ))
      ctx.add(Shops.makeGachapon(Shops.GachaponOpts(x = eEdge - 0.5, z = 31.4, y = g0(31.4), ry = -math.Pi / 2.0, n = 3)))
      ctx.collide(eEdge - 0.85, 30.7, eEdge - 0.15, 32.1, g0(31.4) + 1.5)
      ctx.add(
        Shops.makeShopFlag(Shops.ShopFlagOpts(x = eEdge - 0.5, z = 33.6, y = g0(33.6), variant = 2, ry = -math.Pi / 2.0))
      )
      Vending.addVending(ctx,
        Vending.VendingOptions(x = wEdge + 0.62, z = 29.2, y = g0(29.2), ry = math.Pi / 2.0, variant = 0, seed = 21))
      Vending.addVending(ctx,
        Vending.VendingOptions(x = wEdge + 0.62, z = 30.5, y = g0(30.5), ry = math.Pi / 2.0, variant = 2, seed = 22))
      ctx.add(
        Props.makeVendBin(Props.VendBinOpts(x = wEdge + 0.6, z = 31.4, y = g0(31.4), ry = math.Pi / 2.0, color = Pal.bin))
      )
      ctx.add(
        Shops.makeShopFlag(Shops.ShopFlagOpts(x = wEdge + 0.5, z = 32.7, y = g0(32.7), variant = 3, ry = -math.Pi / 2.0))
      )
      ctx.add(Props.makeCrates(Props.CratesOpts(x = wEdge + 0.48, z = 34.4, y = g0(34.4), n = 3, seed = 407, ry = -0.18)))
      Props.makeLoosePaper(propCtx(ctx),
        Vector(
          Props.PaperSpot(x = X0 + 0.7, z = 25.2, y = g0(25.2), ry = 0.5),
          Props.PaperSpot(x = X1 - 0.8, z = 34.2, y = g0(34.2), ry = -0.9),
          Props.PaperSpot(x = CX + 1.2, z = 37.0, y = Street.groundY(37.0) + 0.06, ry = 1.3)
        ))
      ctx.add(Props.makeLaundryPole(Props.LaundryPoleOpts(x = 27.4, z = 34.2, y = Street.groundY(34.2) + 3.3,
        ry = 0.0, len = 2.2, n = 3, seed = 406)))
    }

    {
      val zw = 14.05
      val y  = Street.groundY(zw)
      val board = new Mesh(
        new BoxGeometry(1.0, 2.6, 0.12),
        Props.matArray(
          Toon.flat(FlatOptions(color = Pal.wallGray)), Toon.flat(FlatOptions(color = Pal.wallGray)),
          Toon.flat(FlatOptions(color = Pal.wallGray)), Toon.flat(FlatOptions(color = Pal.wallGray)),
          Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.shopFascia(ShopKind.Kosho)), cache = false)),
          Toon.flat(FlatOptions(color = Pal.wallGray))
        )
      )
      board.position.set(20.6, y + 3.3, zw + 0.06)
      board.castShadow = true
      ctx.add(board)
      Outline.hullOutline(board, OutlineOptions(thickness = 0.003))
      ctx.add(Util.box(0.06, 0.06, 0.4, M.metalDark, 20.6, y + 4.7, zw + 0.18))
      Vector(19.6, 23.4).foreach { dx =>
        ctx.add(Util.cyl(0.055, 0.055, 5.2, 6, M.metal, dx, y + 2.6, zw + 0.1))
        (0 until 4).foreach { i =>
          ctx.add(Util.box(0.11, 0.06, 0.11, M.metalDark, dx, y + 0.8 + i * 1.2, zw + 0.14))
        }
      }
      ctx.add(Props.makeAircon(
        Props.AirconOpts(x = 22.6, z = zw + 0.09, y = y + 2.2, ry = 0.0, w = 0.86, h = 0.6, feet = false)
      ))
      ctx.add(
        Props.makeBikeRack(Props.BikeRackOpts(x = 23.0, z = zw + 0.95, y = y, n = 3, spacing = 0.62, ry = 0.0, seed = 41))
      )
      Vending.addVending(ctx, Vending.VendingOptions(x = 19.9, z = zw + 0.5, y = y, ry = 0.0, variant = 1, seed = 33))
      ctx.add(Props.makeVendBin(Props.VendBinOpts(x = 20.85, z = zw + 0.5, y = y, ry = 0.0, color = Pal.vendTeal)))
    }

    {
      val off    = new Color(0xb2abbb)
      val on     = new Color(Pal.lanternLit)
      val hold   = 18.0
      val spread = 72.0
      val fade   = 1.7
      val sorted = lanterns.sortBy(_.along)
      val span   = math.max(1, sorted.length - 1)
      sorted.zipWithIndex.foreach { (entry, i) =>
        entry.at = hold + (spread * i) / span
        entry.material.color.copy(off)
      }
      var elapsed = 0.0
      var done    = false
      ctx.update { dt =>
        if !done then
          elapsed += dt
          if elapsed >= hold + spread + fade then
            sorted.foreach(_.material.color.copy(on))
            done = true
          else
            sorted.foreach { entry =>
              val k = math.min(1.0, math.max(0.0, (elapsed - entry.at) / fade))
              entry.material.color.lerpColors(off, on, k * k * (3 - 2 * k))
            }
      }
    }

    sakura += SakuraSpot(x = CX - 1.4, z = ZN - 1.6, y = Street.groundY(ZN - 1.6), scale = 1.24, seed = 901, lean = 0.12,
      leanDir = Some(3.4))
    sakura += SakuraSpot(x = 20.4, z = NorthZ + 3.2, y = Street.groundY(NorthZ + 3.2), scale = 1.18, seed = 902,
      lean = 0.1, leanDir = Some(1.6))
    sakura += SakuraSpot(x = 31.4, z = NorthZ + 2.6, y = Street.groundY(NorthZ + 2.6), scale = 1.28, seed = 903,
      lean = 0.09, leanDir = Some(4.9))
    sakura += SakuraSpot(x = 12.6, z = NorthZ + 2.8, y = Street.groundY(NorthZ + 2.8), scale = 1.14, seed = 904,
      lean = 0.11, leanDir = Some(2.7))
    grove += GroveSpot(x = 7.8, z = 51.4, y = Street.groundY(51.4), scale = 1.8, seed = 906, spread = 1.2)
    grove += GroveSpot(x = 28.2, z = 53.1, y = Street.groundY(53.1), scale = 1.65, seed = 907, spread = 1.15)
    shrubs += ShrubSpot(x = 14.2, z = 43.6, y = Street.groundY(43.6), r = 0.5, count = 4, spread = 1.5, seed = 908)
    shrubs += ShrubSpot(x = 33.4, z = 41.0, y = Street.groundY(41.0), r = 0.55, count = 4, spread = 1.6, seed = 909)

    petals += PetalPatch(x = CX, z = 22.0, w = 5.6, d = 10.0, y = Street.groundY(22.0), n = 110)
    petals += PetalPatch(x = CX, z = 34.0, w = 5.6, d = 10.0, y = Street.groundY(34.0), n = 100)
    petals += PetalPatch(x = 12.0, z = AlleyZ, w = 12.0, d = 2.2, y = Street.groundY(AlleyZ) + 0.1, n = 60)

    Planting(sakura = sakura.toVector, shrubs = shrubs.toVector, grove = grove.toVector,
      petals = petals.toVector)
  end buildShotengai
end Shotengai
