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

/** 1:1 port of upstream `src/world/shops.js`: the shopfront generator (one construction, many
  * tenants) and the pavement clutter that makes a street read as trading.
  */
object Shops:

  private val Walls = Vector(
    Pal.wallWhite, Pal.wallCream, Pal.wallBlue, Pal.wallBeige, Pal.wallGray, Pal.wallPink
  )
  private val Roofs = Vector(Pal.roofSlate, Pal.roofBlue, Pal.roofBrown, Pal.roofTeal)
  private val Awnings = Vector(
    Pal.awningGreen, Pal.awningOrange, Pal.awningBlue, Pal.awningCream, Pal.red
  )

  private object M:
    lazy val walls     = Walls.map(c => Toon.cel(CelOptions(color = c, bands = 3, tint = 0x6f6790)))
    lazy val roofs     = Roofs.map(c => Toon.cel(CelOptions(color = c, bands = 3, tint = 0x514b70)))
    lazy val awnings   = Awnings.map(c => Toon.cel(CelOptions(color = c, bands = 3, tint = 0x6f5680)))
    lazy val trim      = Toon.cel(CelOptions(color = Pal.trim, bands = 3, tint = 0x5c5680))
    lazy val metal     = Toon.cel(CelOptions(color = Pal.metal, bands = 3, tint = 0x666090))
    lazy val metalDark = Toon.cel(CelOptions(color = Pal.metalDark, bands = 3, tint = 0x5c5680))
    lazy val tile      = Toon.cel(CelOptions(color = 0xe9edf0, bands = 3, tint = 0x6f6790))
    lazy val dark      = Toon.cel(CelOptions(color = Pal.blackSoft, bands = 2, tint = 0x4b4560))
    lazy val wood      = Toon.cel(CelOptions(color = 0x9c7f5e, bands = 3, tint = 0x5c5680))
    lazy val concrete  = Toon.cel(CelOptions(color = Pal.concrete, bands = 3, tint = 0x6f6790))

  private def faceRy(face: Buildings.Face): Double = Buildings.faceRy(face)

  final case class ShopOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    w: Double = 6.0,
    d: Double = 6.5,
    face: Buildings.Face = Buildings.Face.ZPlus,
    kind: Option[ShopKind] = None,
    floors: Int = 2,
    h1: Double = 3.2,
    h2: Double = 2.7,
    recess: Double = 0.9,
    wall: Option[Int] = None,
    roof: Option[Int] = None,
    roofKind: Option[Buildings.RoofKind] = None,
    roofH: Option[Double] = None,
    awning: Option[Int] = None,
    awningOut: Option[Double] = None,
    shutter: Option[Double] = None,
    blade: Option[ShopKind] = None,
    bladeSide: Int = 1,
    noren: Option[Textures.NorenKind] = None,
    norenX: Option[Double] = None,
    posters: Boolean = true,
    posterLimit: Option[Int] = None,
    seed: Int = 13,
    fascia: Boolean = true,
    signBox: Boolean = false,
    openW: Option[Double] = None,
    interior: Option[Int] = None,
    interiorMap: Option[Texture[?, ?]] = None,
    lit: Boolean = false,
    balcony: Boolean = false
  )

  /** One construction, every tenant.  The unit is authored facing +Z (frontage along X, depth
    * along Z) and the group is rotated by `face`, which is far less error-prone than branching on
    * an axis inside every measurement.  The shopfront is a real recess -- the solid volume stops
    * `recess` short of the frontage line and piers plus a header frame the hole -- because a
    * glazed decal on a solid box reads as a sticker.
    */
  def makeShop(ctx: PropCtx, o: ShopOpts): Group[Object3DEventMap] =
    val rng = Util.rngKit(o.seed)
    val g   = new Group()
    g.name = "shop_" + o.kind.map(_.key).getOrElse("unit")

    val w      = o.w
    val d      = o.d
    val floors = o.floors
    val H1     = o.h1
    val H2     = o.h2
    val H      = if floors == 2 then H1 + H2 else H1
    val REC    = o.recess
    val front  = d / 2.0
    // upstream indexes these palettes with a plain `[]`, so the two Showa units on the
    // shopping street (wall 6/7 on a six-entry table) fall through to three's default
    // material rather than failing: an unlit white wall, reproduced here.
    val wallMat = M.walls.lift(o.wall.getOrElse(rng.int(0, Walls.length - 1)))
      .getOrElse(Toon.flat(FlatOptions(color = 0xffffff)))
    val roofMat = M.roofs.lift(o.roof.getOrElse(rng.int(0, Roofs.length - 1)))
      .getOrElse(Toon.flat(FlatOptions(color = 0xffffff)))

    val parts = mutable.LinkedHashMap(
      "wall"      -> mutable.ArrayBuffer[Util.BakePart](),
      "trim"      -> mutable.ArrayBuffer[Util.BakePart](),
      "roof"      -> mutable.ArrayBuffer[Util.BakePart](),
      "metal"     -> mutable.ArrayBuffer[Util.BakePart](),
      "metalDark" -> mutable.ArrayBuffer[Util.BakePart](),
      "tile"      -> mutable.ArrayBuffer[Util.BakePart]()
    )
    def push(key: String, geo: BufferGeometry[?, ?], mx: Matrix4): Unit =
      parts(key) += Util.BakePart(geo, Some(mx))

    /* main volume */
    push("wall", new BoxGeometry(w, H1, d - REC), Util.trs(0.0, H1 / 2.0, -REC / 2.0))
    push("trim", new BoxGeometry(w + 0.16, 0.4, d + 0.16), Util.trs(0.0, 0.2, 0.0))
    if floors == 2 then
      // upper storey, set back so the fascia and awning have somewhere to sit
      push("wall", new BoxGeometry(w, H2, d - 0.4), Util.trs(0.0, H1 + H2 / 2.0, -0.2))
      push("trim", new BoxGeometry(w + 0.12, 0.16, d - 0.3), Util.trs(0.0, H1, -0.2))

    /* roof */
    if o.roofKind.contains(Buildings.RoofKind.Gable) then
      val eave  = 0.42
      val rw    = w + eave * 2.0
      val rd    = d + eave * 2.0
      val rh    = o.roofH.getOrElse(1.25)
      val slope = math.atan2(rh, rw / 2.0)
      val slab  = math.hypot(rw / 2.0, rh) + 0.08
      Vector(-1.0, 1.0).foreach { s =>
        push("roof", new BoxGeometry(slab, 0.15, rd),
          Util.trs(s * (rw / 4.0), H + rh / 2.0, -0.2, 0.0, 0.0, -s * slope))
      }
      push("roof", new BoxGeometry(0.24, 0.18, rd), Util.trs(0.0, H + rh + 0.04, -0.2))
      val tri = new Shape()
      tri.moveTo(-w / 2.0, 0.0)
      tri.lineTo(w / 2.0, 0.0)
      tri.lineTo(0.0, rh * (1.0 - (eave * 2.0) / rw))
      tri.closePath()
      val triGeo = new ExtrudeGeometry(tri, ExtrudeGeometryOptions(depth = Some(0.16), bevelEnabled = Some(false)))
      triGeo.translate(0.0, 0.0, -0.08)
      Vector(-1.0, 1.0).foreach { s =>
        push("wall", triGeo, Util.trs(0.0, H, -0.2 + s * ((d - 0.4) / 2.0 - 0.08)))
      }
    else
      val dd = if floors == 2 then d - 0.4 else d
      push("roof", new BoxGeometry(w + 0.3, 0.2, dd + 0.3), Util.trs(0.0, H + 0.1, -0.2))
      // parapet, so a flat roof still has an edge to ink
      Vector(-1.0, 1.0).foreach { s =>
        push("roof", new BoxGeometry(w + 0.3, 0.34, 0.14),
          Util.trs(0.0, H + 0.37, -0.2 + s * (dd / 2.0 + 0.14)))
        push("roof", new BoxGeometry(0.14, 0.34, dd + 0.3),
          Util.trs(s * (w / 2.0 + 0.14), H + 0.37, -0.2))
      }
    end if

    /* the shopfront recess */
    val openW = math.min(w - 1.0, o.openW.getOrElse(w - 1.0))
    val pierW = (w - openW) / 2.0
    Vector(-1.0, 1.0).foreach { s =>
      push("wall", new BoxGeometry(pierW, H1, REC),
        Util.trs(s * (w - pierW) / 2.0, H1 / 2.0, front - REC / 2.0))
      push("tile", new BoxGeometry(pierW + 0.04, 0.62, 0.06),
        Util.trs(s * (w - pierW) / 2.0, 0.31, front + 0.02))
    }
    push("wall", new BoxGeometry(openW, H1 - 2.55, REC),
      Util.trs(0.0, H1 - (H1 - 2.55) / 2.0, front - REC / 2.0))
    // soffit and floor of the recess
    push("trim", new BoxGeometry(openW, 0.1, REC), Util.trs(0.0, 2.5, front - REC / 2.0))
    push("tile", new BoxGeometry(openW, 0.1, REC + 0.1), Util.trs(0.0, 0.05, front - REC / 2.0 + 0.05))

    /* The interior sits on the face of the solid volume, knocked well back toward violet: a shop
     * interior has to be darker than the sunlit frontage or the glass stops reading as glass.
     */
    val interiorTex = o.interiorMap.getOrElse(Textures.shopInterior(o.interior.getOrElse(0)))
    val inner = new Mesh(
      new PlaneGeometry(openW - 0.1, 2.2),
      Toon.flat(FlatOptions(color = 0x8b8598, map = Some(interiorTex), cache = false))
    )
    inner.position.set(0.0, 1.35, front - REC + 0.03)
    ThreeInterop.setUserDataFlag(inner, "noOutline")
    ThreeInterop.addChild(g, inner)

    // glass across the front of the recess, plus mullions
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
    // one angled highlight, the way glass is painted
    val hi = new Mesh(new PlaneGeometry(glassW * 0.24, 2.5),
      Toon.flat(FlatOptions(color = 0xf2f8ff, transparent = true, opacity = 0.18,
        depthWrite = Some(false), cache = false)))
    hi.position.set(-glassW * 0.22, 1.4, front - 0.04)
    hi.rotation.z = 0.3
    ThreeInterop.setUserDataFlag(hi, "noOutline")
    ThreeInterop.addChild(g, hi)

    /* shutter, if any: half down is the useful state -- the shop exists and is between shifts
     * without needing anybody standing in the doorway.
     */
    val shut = o.shutter.getOrElse(0.0)
    if shut != 0.0 then
      val tex = Textures.shutterTex(22)
      tex.wrapS = 1000
      tex.wrapT = 1000
      val SH = 2.55 * shut
      val sl = Util.box(openW - 0.1, SH, 0.06,
        Toon.cel(CelOptions(color = Pal.shutter, bands = 3, map = Some(tex), tint = 0x4b4560, cache = false)),
        0.0, 2.55 - SH / 2.0, front - 0.16)
      sl.castShadow = true
      sl.receiveShadow = true
      ThreeInterop.addChild(g, sl)
      push("metalDark", new BoxGeometry(openW + 0.14, 0.28, 0.2), Util.trs(0.0, 2.66, front - 0.16))
      push("metalDark", new BoxGeometry(openW, 0.1, 0.1), Util.trs(0.0, 2.55 - SH, front - 0.16))

    /* fascia sign: `fascia = false` for a unit that carries its name some other way -- the
     * bathhouse is the one, whose board would sit inside a projecting gable eave.
     */
    if o.fascia then
      o.kind.foreach { kind =>
        val lit   = o.signBox
        val dep   = if lit then 0.26 else 0.12
        val out   = if lit then 0.11 else 0.02
        val sideM = Toon.flat(FlatOptions(color = if lit then 0x3c3947 else Pal.wallGray))
        val topM  = Toon.flat(FlatOptions(color = if lit then 0x4a4653 else Pal.wallWhite))
        val faceM = Toon.flat(FlatOptions(color = if lit then 0xfff0d2 else 0xffffff,
          map = Some(Textures.shopFascia(kind)), cache = false))
        val board = new Mesh(new BoxGeometry(w - 0.2, 0.72, dep),
          Props.matArray(sideM, sideM, topM, sideM, faceM, topM))
        board.position.set(0.0, H1 + 0.44, front + out)
        board.castShadow = true
        ThreeInterop.addChild(g, board)
        Outline.hullOutline(board, OutlineOptions(thickness = 0.0032))
        if lit then
          // the case, a little larger all round and set back so it reads as a rim
          push("metalDark", new BoxGeometry(w - 0.04, 0.92, dep - 0.06),
            Util.trs(0.0, H1 + 0.44, front + out - 0.05))
        push("metal", new BoxGeometry(w - 0.14, 0.1, 0.2), Util.trs(0.0, H1 + 0.86, front + out))
      }
    end if

    /* awning: stripes as geometry, not texture -- under cel shading a painted stripe flattens
     * out and a modelled one keeps its edge.
     */
    o.awning.foreach { idx =>
      val out      = o.awningOut.getOrElse(1.5)
      val yA       = H1 - 0.16
      val drop     = 0.4
      val stripes  = math.max(6, math.round(w / 0.42).toInt)
      val sw       = w / stripes
      val stripesA = mutable.ArrayBuffer[Util.BakePart]()
      val stripesB = mutable.ArrayBuffer[Util.BakePart]()
      (0 until stripes).foreach { i =>
        val bucket = if i % 2 == 0 then stripesA else stripesB
        bucket += Util.BakePart(new BoxGeometry(sw, 0.07, out),
          Some(Util.trs(-w / 2.0 + sw * (i + 0.5), 0.0, out / 2.0)))
      }
      val grp = new Group()
      grp.position.set(0.0, yA, front)
      val cA = M.awnings(idx % Awnings.length)
      val cB = Toon.cel(CelOptions(color = 0xf6f1e6, bands = 3, tint = 0x6f5680))
      val mA = new Mesh(Util.bake(stripesA.toList), cA)
      val mB = new Mesh(Util.bake(stripesB.toList), cB)
      mA.castShadow = true
      mA.receiveShadow = true
      mB.castShadow = true
      mB.receiveShadow = true
      ThreeInterop.addChild(grp, mA)
      ThreeInterop.addChild(grp, mB)
      grp.rotation.x = math.atan2(drop, out)
      ThreeInterop.addChild(g, grp)
      Outline.hullOutline(mA, OutlineOptions(thickness = 0.003))

      val edgeZ  = front + out * math.cos(grp.rotation.x)
      val edgeY  = yA - out * math.sin(grp.rotation.x)
      val edging = Util.box(w, 0.3, 0.07, cA, 0.0, edgeY - 0.13, edgeZ - 0.03)
      edging.castShadow = true
      ThreeInterop.addChild(g, edging)
      // scalloped lower edge
      val scGeo    = new CircleGeometry(0.14, 10)
      val scallops = mutable.ArrayBuffer[Util.BakePart]()
      val n        = math.round(w / 0.5).toInt
      (0 until n).foreach { i =>
        scallops += Util.BakePart(scGeo, Some(Util.trs(-w / 2.0 + (w / n) * (i + 0.5), 0.0, 0.0)))
      }
      val scm = new Mesh(Util.bake(scallops.toList), cA)
      scm.position.set(0.0, edgeY - 0.28, edgeZ - 0.01)
      ThreeInterop.addChild(g, scm)
      Vector(-1.0, 1.0).foreach { sx =>
        val arm = Util.box(0.06, 0.06, out, M.metalDark, sx * (w / 2.0 - 0.3), 0.0, 0.0)
        arm.position.set(sx * (w / 2.0 - 0.3), yA - 0.05 - drop / 2.0, front + out / 2.0)
        arm.rotation.x = grp.rotation.x
        ThreeInterop.addChild(g, arm)
      }
    }

    /* projecting blade sign */
    o.blade.foreach { bladeKind =>
      val bw    = 0.52
      val bh    = 1.9
      val side  = o.bladeSide
      val bx    = side * (w / 2.0 - 0.18)
      val art   = Textures.shopBlade(bladeKind)
      val faceM = Toon.flat(FlatOptions(color = 0xffffff, map = Some(art), cache = false))
      val sideM = Toon.flat(FlatOptions(color = Pal.wallGray))
      val board = new Mesh(new BoxGeometry(0.1, bh, bw),
        Props.matArray(faceM, faceM, sideM, sideM, sideM, sideM))
      board.position.set(bx + side * 0.5, H1 + 1.5, front + 0.1)
      board.castShadow = true
      ThreeInterop.addChild(g, board)
      Outline.hullOutline(board, OutlineOptions(thickness = 0.003))
      push("metal", new BoxGeometry(side * 0.62, 0.08, 0.08),
        Util.trs(bx + side * 0.28, H1 + 2.32, front + 0.1))
      push("metal", new BoxGeometry(side * 0.62, 0.08, 0.08),
        Util.trs(bx + side * 0.28, H1 + 0.7, front + 0.1))
    }

    /* noren */
    o.noren.foreach { norenKind =>
      val nw = math.min(2.4, openW * 0.7)
      val cloth = new Mesh(new PlaneGeometry(nw, 0.72),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.norenTex(norenKind)),
          side = 2, cache = false)))
      cloth.position.set(o.norenX.getOrElse(0.0), 2.06, front - 0.22)
      // a slight lift on one side: the doorway curtain is always caught by air
      cloth.rotation.z = 0.025
      cloth.castShadow = true
      ThreeInterop.addChild(g, cloth)
      push("metalDark", new BoxGeometry(nw + 0.2, 0.05, 0.05),
        Util.trs(o.norenX.getOrElse(0.0), 2.44, front - 0.22))
    }

    /* upper storey windows */
    if floors == 2 then
      val cols = math.max(1, math.floor((w - 0.8) / 1.9).toInt)
      (0 until cols).foreach { i =>
        val px      = -w / 2.0 + (w * (i + 1)) / (cols + 1)
        val litPane = o.lit && rng.chance(0.55)
        push("metal", new BoxGeometry(1.5, 1.3, 0.16), Util.trs(px, H1 + 1.4, front - 0.2 + 0.08))
        val paneMat =
          if litPane then
            Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.litWindowTex(i % 3)), cache = false))
          else Toon.flat(FlatOptions(color = Pal.glassDark))
        ThreeInterop.addChild(g, Util.box(1.34, 1.14, 0.06, paneMat, px, H1 + 1.4, front - 0.14))
        push("metal", new BoxGeometry(0.07, 1.14, 0.07), Util.trs(px, H1 + 1.4, front - 0.12))
        push("trim", new BoxGeometry(1.7, 0.09, 0.22), Util.trs(px, H1 + 0.72, front - 0.16))
        if rng.chance(0.5) then
          val cur = new Mesh(new PlaneGeometry(0.6, 1.06),
            Toon.flat(FlatOptions(color = 0xe8e0d2, map = Some(Textures.curtainTex(i % 2)), cache = false)))
          cur.position.set(px + rng.sign * 0.34, H1 + 1.4, front - 0.11)
          ThreeInterop.setUserDataFlag(cur, "noOutline")
          ThreeInterop.addChild(g, cur)
      }
      // a balcony rail on some units
      if o.balcony then
        push("trim", new BoxGeometry(w - 0.6, 0.1, 0.8), Util.trs(0.0, H1 + 0.5, front + 0.2))
        push("metal", new BoxGeometry(w - 0.6, 0.06, 0.06), Util.trs(0.0, H1 + 1.42, front + 0.58))
        push("metalDark", new BoxGeometry(w - 0.6, 0.06, 0.06), Util.trs(0.0, H1 + 0.62, front + 0.58))
        val nb = math.round((w - 0.6) / 0.24).toInt
        (0 to nb).foreach { i =>
          push("metal", new BoxGeometry(0.04, 0.9, 0.04),
            Util.trs(-(w - 0.6) / 2.0 + ((w - 0.6) / nb) * i, H1 + 0.98, front + 0.58))
        }
    end if

    /* services and paper */
    val pipeSide = rng.sign
    push("metal", new CylinderGeometry(0.055, 0.055, H, 6),
      Util.trs(pipeSide * (w / 2.0 - 0.1), H / 2.0, -(d - REC) / 2.0 - 0.1 + 0.02))
    val meter = new Mesh(new BoxGeometry(0.3, 0.44, 0.1),
      Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.meterBox()), cache = false)))
    meter.position.set(-pipeSide * (w / 2.0 - 0.42), 1.8, front - REC + 0.06)
    ThreeInterop.addChild(g, meter)
    if o.posters then
      val n = math.min(rng.int(1, 2), o.posterLimit.getOrElse(2))
      (0 until n).foreach { _ =>
        val p = new Mesh(new PlaneGeometry(0.38, 0.52),
          Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.poster(rng.int(0, 3))), cache = false)))
        p.position.set(rng.range(-w / 2.0 + 0.6, w / 2.0 - 0.6), 1.72 + rng.range(-0.1, 0.1), front + 0.03)
        p.rotation.z = rng.range(-0.03, 0.03)
        ThreeInterop.setUserDataFlag(p, "noOutline")
        ThreeInterop.addChild(g, p)
      }

    val matFor: Map[String, Material[?]] = Map(
      "wall"  -> wallMat, "trim"      -> M.trim, "roof"      -> roofMat,
      "metal" -> M.metal, "metalDark" -> M.metalDark, "tile" -> M.tile
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
    g.rotation.y = faceRy(o.face)
    ctx.add(g)

    // collider in world axes: the unit was authored facing +Z, so a quarter turn swaps w and d
    val swap = o.face == Buildings.Face.XPlus || o.face == Buildings.Face.XMinus
    val hw   = (if swap then d else w) / 2.0
    val hd   = (if swap then w else d) / 2.0
    ctx.collide(o.x - hw - 0.05, o.z - hd - 0.05, o.x + hw + 0.05, o.z + hd + 0.05, o.y + H)
    g.userData("front") = front.asInstanceOf[js.Any]
    g
  end makeShop

  final case class PaperLanternOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    r: Double = 0.16,
    variant: Int = 0,
    drop: Double = 0.3,
    lit: Boolean = false,
    ry: Double = 0.0
  )

  def makePaperLantern(o: PaperLanternOpts): Group[Object3DEventMap] =
    val g = new Group()
    val body = new Mesh(
      new CylinderGeometry(o.r, o.r, o.r * 2.1, 12, 1, true),
      Toon.flat(FlatOptions(color = if o.lit then Pal.lanternLit else 0xffffff,
        map = Some(Textures.lanternTex(o.variant)), side = 2, cache = false))
    )
    body.position.y = -o.r * 1.05
    ThreeInterop.addChild(g, body)
    g.userData("glow") = body.material.asInstanceOf[js.Any]
    Vector(-1.0, 1.0).foreach { s =>
      ThreeInterop.addChild(g,
        Util.cyl(o.r * 0.45, o.r * 0.45, 0.04, 10, M.metalDark,
          0.0, s * o.r * 2.1, 0.0))
    }
    ThreeInterop.addChild(g,
      Util.cyl(0.012, 0.012, o.drop, 4, M.metalDark, 0.0, o.drop / 2.0, 0.0))
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makePaperLantern

  final case class MenuBoardOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0
  )

  def makeMenuBoard(o: MenuBoardOpts): Group[Object3DEventMap] =
    val g = new Group()
    val boardMats = Props.matArray(
      M.wood, M.wood, M.wood, M.wood,
      Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.menuBoard()), cache = false)),
      M.wood
    )
    val board = new Mesh(new BoxGeometry(0.58, 0.82, 0.05), boardMats)
    board.position.set(0.0, 0.62, 0.0)
    board.rotation.x = -0.2
    board.castShadow = true
    ThreeInterop.addChild(g, board)
    Outline.hullOutline(board, OutlineOptions(thickness = 0.003))
    Vector(-1.0, 1.0).foreach { s =>
      val leg = Util.box(0.06, 0.86, 0.06, M.wood, s * 0.24, 0.42, -0.16)
      leg.rotation.x = 0.24
      ThreeInterop.addChild(g, leg)
    }
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeMenuBoard

  final case class FreezerOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0
  )

  def makeFreezer(o: FreezerOpts): Group[Object3DEventMap] =
    val g = new Group()
    val body = Util.box(1.15, 0.86, 0.66,
      Toon.cel(CelOptions(color = Pal.freezer, bands = 3, tint = 0x6a6288)), 0.0, 0.43, 0.0)
    body.castShadow = true
    body.receiveShadow = true
    ThreeInterop.addChild(g, body)
    Outline.hullOutline(body, OutlineOptions(thickness = 0.0034))
    Vector(-1.0, 1.0).foreach { s =>
      ThreeInterop.addChild(g, Util.box(1.19, 0.06, 0.08, M.metal, 0.0, 0.88, s * 0.31))
      ThreeInterop.addChild(g, Util.box(0.09, 0.06, 0.7, M.metal, s * 0.55, 0.88, 0.0))
    }
    val lid = Util.box(1.0, 0.03, 0.54,
      Toon.flat(FlatOptions(color = 0xdff0f8, transparent = true, opacity = 0.4,
        depthWrite = Some(false), cache = false)),
      0.0, 0.885, 0.0)
    ThreeInterop.setUserDataFlag(lid, "noOutline")
    ThreeInterop.addChild(g, lid)
    val cols = Vector(0xe0453f, 0x3d6ec4, 0xf4c033, 0x2f9c9a, 0xe86f9c, 0xef8a3c)
    (0 until 9).foreach { i =>
      ThreeInterop.addChild(g,
        Util.box(0.11, 0.05, 0.16,
          Toon.cel(CelOptions(color = cols(i % cols.length), bands = 2, tint = 0x6f6790)),
          -0.44 + (i % 5) * 0.22, 0.8, -0.14 + (i / 5) * 0.22))
    }
    ThreeInterop.addChild(g,
      Util.box(1.17, 0.2, 0.68,
        Toon.cel(CelOptions(color = Pal.red, bands = 3, tint = 0x7a4060)), 0.0, 0.6, 0.0))
    val sign = new Mesh(new PlaneGeometry(0.44, 0.62),
      Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.flagTex(0)), cache = false)))
    sign.position.set(0.34, 0.42, 0.34)
    ThreeInterop.addChild(g, sign)
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeFreezer

  final case class GachaponOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    n: Int = 3
  )

  def makeGachapon(o: GachaponOpts): Group[Object3DEventMap] =
    val g          = new Group()
    val bodyColors = Vector(0xe0453f, 0xf4c033, 0x3d6ec4)
    (0 until o.n).foreach { i =>
      val x = -((o.n - 1) * 0.42) / 2.0 + i * 0.42
      val body = Util.box(0.4, 1.42, 0.44,
        Toon.cel(CelOptions(color = bodyColors(i % 3), bands = 3, tint = 0x6f6790)), x, 0.71, 0.0)
      body.castShadow = true
      body.receiveShadow = true
      ThreeInterop.addChild(g, body)
      val dome = new Mesh(new SphereGeometry(0.19, 12, 8),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.gachaTex()), cache = false)))
      dome.position.set(x, 1.14, 0.06)
      ThreeInterop.addChild(g, dome)
      ThreeInterop.addChild(g, Util.box(0.36, 0.24, 0.06, M.dark, x, 0.62, 0.23))
      val knob = Util.cyl(0.05, 0.05, 0.06, 8, M.metal, x, 0.78, 0.24)
      knob.rotateX(math.Pi / 2.0)
      ThreeInterop.addChild(g, knob)
      ThreeInterop.addChild(g,
        Util.box(0.3, 0.16, 0.04,
          Toon.flat(FlatOptions(color = 0xf6f2e8)), x, 0.38, 0.23))
    }
    ThreeInterop.addChild(g, Util.box(o.n * 0.42 + 0.06, 0.06, 0.5, M.metalDark, 0.0, 0.03, 0.0))
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeGachapon

  final case class ProduceStackOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    seed: Int = 21
  )

  def makeProduceStack(o: ProduceStackOpts): Group[Object3DEventMap] =
    val rng    = Util.rngKit(o.seed)
    val g      = new Group()
    val crateA = Toon.cel(CelOptions(color = Pal.crate, bands = 3, tint = 0x4a4a92))
    val crateB = Toon.cel(CelOptions(color = Pal.crateAlt, bands = 3, tint = 0x7a4060))
    val crateC = Toon.cel(CelOptions(color = 0x4f9d6a, bands = 3, tint = 0x4a4a92))
    val layout = Vector((0.0, 0), (0.0, 1), (0.6, 0), (0.6, 1), (0.6, 2), (1.2, 0))
    layout.zipWithIndex.foreach { case ((dx, ly), i) =>
      val c = Util.box(0.54, 0.28, 0.38, Vector(crateA, crateB, crateC)(i % 3),
        dx, 0.14 + ly * 0.28, rng.range(-0.04, 0.04))
      c.castShadow = true
      c.receiveShadow = true
      ThreeInterop.addChild(g, c)
      ThreeInterop.addChild(g,
        Util.box(0.48, 0.05, 0.32,
          Toon.cel(CelOptions(color = 0x2f3140, bands = 2)), dx, 0.27 + ly * 0.28, 0.0))
    }
    val blob = new IcosahedronGeometry(1.0, 0)
    (0 until 7).foreach { _ =>
      val f = new Mesh(blob,
        Toon.cel(CelOptions(color = rng.pick(Vector(0xf0a63c, 0x8fbf4a, 0xe0574a, 0xefe0b0)),
          bands = 2, tint = 0x6f6790)))
      val r = rng.range(0.06, 0.09)
      f.position.set(0.6 + rng.range(-0.18, 0.18), 0.86 + r, rng.range(-0.13, 0.13))
      f.scale.setScalar(r)
      f.castShadow = true
      ThreeInterop.addChild(g, f)
    }
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeProduceStack

  final case class ShopFlagOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    variant: Int = 0
  )

  def makeShopFlag(o: ShopFlagOpts): Group[Object3DEventMap] =
    val g    = new Group()
    val pole = Util.cyl(0.022, 0.022, 1.9, 5, M.metal, 0.0, 0.95, 0.0)
    pole.castShadow = true
    ThreeInterop.addChild(g, pole)
    ThreeInterop.addChild(g, Util.cyl(0.13, 0.15, 0.1, 10, M.concrete, 0.0, 0.05, 0.0))
    val cloth = new Mesh(new PlaneGeometry(0.38, 1.0),
      Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.flagTex(o.variant)),
        side = 2, cache = false)))
    cloth.position.set(0.2, 1.28, 0.02)
    cloth.rotation.y = 0.12
    cloth.castShadow = true
    ThreeInterop.addChild(g, cloth)
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
end Shops
