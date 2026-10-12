package sakura.world

import sakura.ThreeInterop
import sakura.core.{Outline, Pal, Textures, Toon, Util}
import sakura.core.Outline.OutlineOptions
import sakura.core.Toon.{CelOptions, FlatOptions}
import vendor.three.*

import scala.collection.mutable
import scala.math
import scala.scalajs.js

/** 1:1 port of upstream `src/world/streetprops.js`: eleven props for the residential blocks,
  * drawn from joints between named points rather than by eye.
  */
object StreetProps:

  private object M:
    lazy val concrete    = Toon.cel(CelOptions(color = Pal.concrete, bands = 3, tint = 0x6f6790))
    lazy val concreteMid = Toon.cel(CelOptions(color = Pal.concreteMid, bands = 3, tint = 0x6a6288))
    lazy val metal       = Toon.cel(CelOptions(color = Pal.metal, bands = 3, tint = 0x666090))
    lazy val metalDark   = Toon.cel(CelOptions(color = Pal.metalDark, bands = 3, tint = 0x5c5680))
    lazy val dark        = Toon.cel(CelOptions(color = Pal.black, bands = 2, tint = 0x4b4560))
    lazy val shell       = Toon.cel(CelOptions(color = 0xc4c8ce, bands = 3, tint = 0x666090))
    lazy val shellTrim   = Toon.cel(CelOptions(color = 0x9aa0a8, bands = 3, tint = 0x5c5680))
    lazy val wood        = Toon.cel(CelOptions(color = 0x9c7f5e, bands = 3, tint = 0x5c5680))
    lazy val woodDark    = Toon.cel(CelOptions(color = 0x7d6348, bands = 3, tint = 0x5c5680))
    lazy val soil        = Toon.cel(CelOptions(color = 0x74624e, bands = 3, tint = 0x615a80))
    lazy val bamboo      = Toon.cel(CelOptions(color = Pal.bamboo, bands = 3, tint = 0x5b6f8c))
    lazy val twine       = Toon.cel(CelOptions(color = Pal.rope, bands = 3, tint = 0x6f6790))
    lazy val pale        = Toon.flat(FlatOptions(color = 0xf6f2e8))

  private val Up           = new Vector3(0.0, 1.0, 0.0)
  private val unitCylCache = mutable.HashMap.empty[Int, CylinderGeometry]

  private def unitCyl(seg: Int): CylinderGeometry =
    unitCylCache.getOrElseUpdate(seg, new CylinderGeometry(1.0, 1.0, 1.0, seg.toDouble, 1.0))

  private def member(
    arr: mutable.ArrayBuffer[Util.BakePart],
    a: Vector3,
    b: Vector3,
    r: Double,
    seg: Int = 6
  ): Unit =
    val dir = new Vector3().subVectors(ThreeInterop.asVector3Like(a), ThreeInterop.asVector3Like(b))
    val len = dir.length()
    if len >= 1e-4 then
      arr += Util.BakePart(unitCyl(seg),
        Some(new Matrix4().compose(
          new Vector3().addVectors(ThreeInterop.asVector3Like(a), ThreeInterop.asVector3Like(b))
            .multiplyScalar(0.5),
          new Quaternion().setFromUnitVectors(ThreeInterop.asVector3Like(Up).asInstanceOf[Vector3],
            ThreeInterop.asVector3Like(dir.normalize())),
          new Vector3(r, len, r)
        )))

  private def emit(
    g: Group[Object3DEventMap],
    parts: mutable.LinkedHashMap[String, mutable.ArrayBuffer[Util.BakePart]],
    matFor: Map[String, Material[?]],
    noCast: Vector[String] = Vector.empty,
    outline: Option[String] = None,
    thickness: Double = 0.0032
  ): Unit =
    parts.foreach { (key, bucket) =>
      if bucket.nonEmpty then
        val mesh = new Mesh(Util.bake(bucket.toList), matFor(key))
        mesh.castShadow = !noCast.contains(key)
        mesh.receiveShadow = true
        ThreeInterop.addChild(g, mesh)
        if Some(key) == outline then Outline.hullOutline(mesh, OutlineOptions(thickness = thickness))
    }

  private def latticePanel(w: Double, h: Double, cell: Double, color: Int = 0xc2c8d0): Mesh[?, ?, ?] =
    val tex = Textures.chainLinkTex().clone()
    tex.wrapS = 1000
    tex.wrapT = 1000
    tex.repeat.set(w / cell, h / cell)
    ThreeInterop.markForUpload(tex)
    val panel = new Mesh(new PlaneGeometry(w, h),
      Toon.flat(FlatOptions(color = color, map = Some(tex), transparent = true, opacity = 0.82,
        side = 2, depthWrite = Some(false), cache = false)))
    ThreeInterop.setUserDataFlag(panel, "noOutline")
    ThreeInterop.setUserDataFlag(panel, "noShadow")
    panel

  final case class GomiHouseOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    w: Double = 1.9,
    d: Double = 1.1,
    h: Double = 1.05,
    gateOpen: Double = -0.42
  )

  def makeGomiHouse(o: GomiHouseOpts): Group[Object3DEventMap] =
    val g    = new Group()
    val W    = o.w
    val D    = o.d
    val H    = o.h
    val T    = 0.12
    val SLAB = 0.09
    val TOP  = SLAB + H + 0.06
    val parts = mutable.LinkedHashMap(
      "block" -> mutable.ArrayBuffer[Util.BakePart](),
      "cap"   -> mutable.ArrayBuffer[Util.BakePart](),
      "sheet" -> mutable.ArrayBuffer[Util.BakePart](),
      "metal" -> mutable.ArrayBuffer[Util.BakePart](),
      "lid"   -> mutable.ArrayBuffer[Util.BakePart]()
    )
    def push(key: String, geo: BufferGeometry[?, ?], mx: Matrix4): Unit =
      parts(key) += Util.BakePart(geo, Some(mx))

    push("block", new BoxGeometry(W + 0.12, SLAB, D + 0.12), Util.trs(0.0, SLAB / 2.0, 0.0))
    push("block", new BoxGeometry(W, H, T), Util.trs(0.0, SLAB + H / 2.0, -(D / 2.0 - T / 2.0)))
    Vector(-1.0, 1.0).foreach { sx =>
      push("block", new BoxGeometry(T, H, D),
        Util.trs(sx * (W / 2.0 - T / 2.0), SLAB + H / 2.0, 0.0))
      push("cap", new BoxGeometry(T + 0.08, 0.06, D + 0.08),
        Util.trs(sx * (W / 2.0 - T / 2.0), TOP - 0.03, 0.0))
    }
    push("cap", new BoxGeometry(W + 0.08, 0.06, T + 0.08),
      Util.trs(0.0, TOP - 0.03, -(D / 2.0 - T / 2.0)))

    val RAKE = -0.10
    push("sheet", new BoxGeometry(W + 0.14, 0.05, D + 0.22),
      Util.trs(0.0, TOP + 0.05, 0.02, RAKE, 0.0, 0.0))
    push("sheet", new BoxGeometry(W + 0.14, 0.05, 0.06), Util.trs(0.0, TOP + 0.11, D / 2.0 + 0.11))
    Vector(-1.0, 1.0).foreach { sx =>
      push("metal", new BoxGeometry(0.05, 0.05, D + 0.1),
        Util.trs(sx * (W / 2.0 - T), TOP + 0.01, 0.02, RAKE, 0.0, 0.0))
    }

    Vector(Pal.bin, 0x7fae6a, 0xd8c34a).zipWithIndex.foreach { (c, i) =>
      val bx = -0.52 + i * 0.52
      val b = Util.box(0.44, 0.62, 0.4,
        Toon.cel(CelOptions(color = c, bands = 3, tint = 0x6f6790)), bx, SLAB + 0.31, -0.06)
      b.castShadow = true
      b.receiveShadow = true
      ThreeInterop.addChild(g, b)
      push("lid", new BoxGeometry(0.47, 0.05, 0.43), Util.trs(bx, SLAB + 0.64, -0.06))
    }

    val LW   = W - 2 * T - 0.04
    val LH   = 0.92
    val gate = new Group()
    gate.position.set(-(W / 2.0 - T), SLAB, D / 2.0 - 0.06)
    gate.rotation.y = o.gateOpen
    val gp = mutable.ArrayBuffer[Util.BakePart]()
    Vector(0.025, LW - 0.025).foreach { t =>
      gp += Util.BakePart(new BoxGeometry(0.05, LH, 0.05), Some(Util.trs(t, LH / 2.0, 0.0)))
    }
    Vector(0.025, LH / 2.0, LH - 0.025).foreach { y =>
      gp += Util.BakePart(new BoxGeometry(LW, 0.045, 0.045),
        Some(Util.trs(LW / 2.0, y, 0.0)))
    }
    val frame = new Mesh(Util.bake(gp.toList), M.metal)
    frame.castShadow = true
    ThreeInterop.addChild(gate, frame)
    val panel = latticePanel(LW - 0.08, LH - 0.1, 0.11)
    panel.position.set(LW / 2.0, LH / 2.0, 0.0)
    ThreeInterop.addChild(gate, panel)
    ThreeInterop.addChild(g, gate)
    Vector(SLAB + 0.16, SLAB + LH - 0.16).foreach { y =>
      push("metal", new CylinderGeometry(0.028, 0.028, 0.09, 6),
        Util.trs(-(W / 2.0 - T), y, D / 2.0 - 0.06))
    }
    push("metal", new BoxGeometry(0.05, 0.1, 0.06),
      Util.trs(W / 2.0 - T + 0.02, SLAB + 0.5, D / 2.0 - 0.05))

    emit(g, parts,
      Map(
        "block" -> M.concreteMid, "cap" -> M.concrete, "sheet" -> M.shellTrim,
        "metal" -> M.metalDark, "lid"   -> M.dark
      ),
      noCast = Vector("cap"), outline = Some("block"), thickness = 0.0034)

    val sideMat   = Toon.flat(FlatOptions(color = Pal.wallGray))
    val faceMat   = Toon.flat(FlatOptions(color = 0xf2efe4))
    val plateMats = Props.matArray(sideMat, faceMat, sideMat, sideMat, sideMat, sideMat)
    val plate     = new Mesh(new BoxGeometry(0.03, 0.3, 0.42), plateMats)
    plate.position.set(-(W / 2.0 + 0.005), SLAB + H * 0.6, 0.06)
    plate.castShadow = true
    ThreeInterop.addChild(g, plate)

    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g.userData("top") = (TOP + 0.14).asInstanceOf[js.Any]
    g
  end makeGomiHouse

  final case class LockerBankOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    color: Int = 0xbfc6c2
  )

  def makeLockerBank(o: LockerBankOpts): Group[Object3DEventMap] =
    val g     = new Group()
    val W     = 1.25
    val D     = 0.5
    val PL    = 0.1
    val BH    = 1.42
    val TOPB  = PL + BH
    val HEAD  = 0.16
    val RIDGE = 1.75
    val parts = mutable.LinkedHashMap(
      "panel" -> mutable.ArrayBuffer[Util.BakePart](),
      "metal" -> mutable.ArrayBuffer[Util.BakePart](),
      "dark"  -> mutable.ArrayBuffer[Util.BakePart](),
      "card"  -> mutable.ArrayBuffer[Util.BakePart]()
    )
    def push(key: String, geo: BufferGeometry[?, ?], mx: Matrix4): Unit =
      parts(key) += Util.BakePart(geo, Some(mx))

    push("metal", new BoxGeometry(W + 0.08, PL, D + 0.08), Util.trs(0.0, PL / 2.0, 0.0))
    val body = Util.box(W, BH, D,
      Toon.cel(CelOptions(color = o.color, bands = 3, tint = 0x666090)), 0.0, PL + BH / 2.0, 0.0)
    body.castShadow = true
    body.receiveShadow = true
    ThreeInterop.addChild(g, body)
    Outline.hullOutline(body, OutlineOptions(thickness = 0.0034))

    val DW  = 0.86
    val dcx = -W / 2.0 + 0.04 + DW / 2.0
    var top = TOPB - 0.04
    Vector(0.22, 0.31, 0.31, 0.46).foreach { dh =>
      val cy = top - dh / 2.0
      push("panel", new BoxGeometry(DW, dh - 0.02, 0.022), Util.trs(dcx, cy, D / 2.0 + 0.011))
      push("metal", new BoxGeometry(0.14, 0.03, 0.035),
        Util.trs(dcx + DW / 2.0 - 0.11, cy, D / 2.0 + 0.036))
      push("card", new BoxGeometry(0.055, 0.032, 0.008),
        Util.trs(dcx - DW / 2.0 + 0.06, cy + dh / 2.0 - 0.06, D / 2.0 + 0.028))
      push("dark", new BoxGeometry(0.022, 0.022, 0.02),
        Util.trs(dcx + DW / 2.0 - 0.04, cy - dh / 2.0 + 0.05, D / 2.0 + 0.03))
      top -= dh + 0.02
    }

    val px = dcx + DW / 2.0 + 0.02 + 0.13
    push("dark", new BoxGeometry(0.26, 0.94, 0.02), Util.trs(px, PL + 0.94, D / 2.0 + 0.01))
    ThreeInterop.addChild(g,
      Util.box(0.19, 0.13, 0.014,
        Toon.flat(FlatOptions(color = 0x9fb8c4)), px, PL + 1.3, D / 2.0 + 0.027))
    (0 until 4).foreach { r =>
      (0 until 3).foreach { k =>
        push("card", new BoxGeometry(0.05, 0.036, 0.014),
          Util.trs(px - 0.07 + k * 0.07, PL + 1.12 - r * 0.05, D / 2.0 + 0.026))
      }
    }
    push("metal", new BoxGeometry(0.16, 0.02, 0.02), Util.trs(px, PL + 0.84, D / 2.0 + 0.026))
    push("card", new BoxGeometry(0.2, 0.14, 0.008), Util.trs(px, PL + 0.6, D / 2.0 + 0.024))

    val sideMat  = Toon.cel(CelOptions(color = 0x8f9aa0, bands = 3, tint = 0x5c5680))
    val faceMat  = Toon.flat(FlatOptions(color = 0xeae6da))
    val headMats = Props.matArray(sideMat, sideMat, sideMat, sideMat, faceMat, sideMat)
    val head     = new Mesh(new BoxGeometry(W, HEAD, D - 0.04), headMats)
    head.position.set(0.0, TOPB + HEAD / 2.0, 0.0)
    head.castShadow = true
    ThreeInterop.addChild(g, head)
    val EAVE = RIDGE - 0.07
    Vector(-1.0, 1.0).foreach { sz =>
      push("metal", new BoxGeometry(W + 0.12, 0.04, 0.32),
        Util.trs(0.0, (RIDGE + EAVE) / 2.0, sz * 0.15, sz * 0.24, 0.0, 0.0))
    }
    push("metal", new BoxGeometry(W + 0.14, 0.05, 0.07), Util.trs(0.0, RIDGE, 0.0))

    emit(g, parts,
      Map(
        "panel" -> Toon.cel(CelOptions(color = 0xd2d8d4, bands = 3, tint = 0x666090)),
        "metal" -> M.metalDark, "dark" -> M.dark, "card" -> M.pale
      ))

    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g.userData("top") = RIDGE.asInstanceOf[js.Any]
    g
  end makeLockerBank

  final case class ScooterOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    color: Int = 0xc9d4dc,
    lean: Double = -0.09,
    cockpit: Boolean = false
  )

  def makeScooter(o: ScooterOpts): Group[Object3DEventMap] =
    val g     = new Group()
    val inner = new Group()
    ThreeInterop.addChild(g, inner)

    val R = 0.2
    val P = Map(
      "RA"  -> new Vector3(-0.6, R, 0.0), "FA"    -> new Vector3(0.57, R, 0.0),
      "ENG" -> new Vector3(-0.3, 0.28, 0.0), "RS" -> new Vector3(-0.22, 0.3, 0.0),
      "FS"  -> new Vector3(0.3, 0.28, 0.0), "HS"  -> new Vector3(0.48, 0.62, 0.0),
      "HT"  -> new Vector3(0.4, 0.96, 0.0), "BAR" -> new Vector3(0.38, 1.0, 0.0),
      "SHK" -> new Vector3(-0.44, 0.5, 0.0)
    )
    val parts = mutable.LinkedHashMap(
      "dark"  -> mutable.ArrayBuffer[Util.BakePart](),
      "metal" -> mutable.ArrayBuffer[Util.BakePart](),
      "body"  -> mutable.ArrayBuffer[Util.BakePart](),
      "amber" -> mutable.ArrayBuffer[Util.BakePart](),
      "dial"  -> mutable.ArrayBuffer[Util.BakePart]()
    )
    def push(key: String, geo: BufferGeometry[?, ?], mx: Matrix4): Unit =
      parts(key) += Util.BakePart(geo, Some(mx))
    val bodyMat = Toon.cel(CelOptions(color = o.color, bands = 3, tint = 0x6f6790))

    Vector(P("RA"), P("FA")).foreach { hub =>
      push("dark", new CylinderGeometry(R, R, 0.09, 14), Util.trs(hub.x, hub.y, 0.0, math.Pi / 2.0))
      push("metal", new CylinderGeometry(0.125, 0.125, 0.11, 12),
        Util.trs(hub.x, hub.y, 0.0, math.Pi / 2.0))
      push("dark", new CylinderGeometry(0.038, 0.038, 0.13, 8),
        Util.trs(hub.x, hub.y, 0.0, math.Pi / 2.0))
    }
    push("body", new TorusGeometry(R + 0.04, 0.026, 4, 14, math.Pi * 0.85),
      Util.trs(P("FA").x, P("FA").y, 0.0, 0.0, 0.0, -0.72, 1.0, 1.0, 2.2))
    push("body", new TorusGeometry(R + 0.05, 0.032, 4, 12, math.Pi * 0.62),
      Util.trs(P("RA").x, P("RA").y, 0.0, 0.0, 0.0, 0.55, 1.0, 1.0, 1.9))

    Vector(-1.0, 1.0).foreach { s =>
      member(parts("metal"), new Vector3(P("HS").x, P("HS").y, s * 0.055),
        new Vector3(P("FA").x, P("FA").y, s * 0.055), 0.019)
      member(parts("metal"), new Vector3(P("ENG").x, P("ENG").y, s * 0.062),
        new Vector3(P("RA").x, P("RA").y, s * 0.062), 0.022)
    }
    member(parts("metal"), P("HS"), P("HT"), 0.026)
    member(parts("metal"), P("HS"), P("FS"), 0.026)
    member(parts("metal"), P("FS"), P("RS"), 0.024)
    member(parts("metal"), P("RS"), P("SHK"), 0.024)
    member(parts("metal"), new Vector3(P("SHK").x, P("SHK").y, 0.07),
      new Vector3(P("RA").x + 0.02, P("RA").y + 0.04, 0.07), 0.024)
    member(parts("metal"), new Vector3(-0.14, 0.26, -0.09),
      new Vector3(-0.24, 0.015, -0.19), 0.016)

    Vector(-1.0, 1.0).foreach { s =>
      push("body", new BoxGeometry(0.5, 0.24, 0.11), Util.trs(-0.4, 0.4, s * 0.125))
    }
    push("body", new BoxGeometry(0.46, 0.09, 0.34), Util.trs(-0.4, 0.505, 0.0))
    push("dark", new BoxGeometry(0.34, 0.08, 0.3), Util.trs(-0.46, 0.59, 0.0))
    push("dark", new BoxGeometry(0.16, 0.065, 0.2), Util.trs(-0.24, 0.575, 0.0))
    push("body", new BoxGeometry(0.56, 0.03, 0.36), Util.trs(0.06, 0.275, 0.0))
    push("dark", new BoxGeometry(0.46, 0.014, 0.28), Util.trs(0.04, 0.297, 0.0))
    push("body", new BoxGeometry(0.16, 0.46, 0.42), Util.trs(0.38, 0.68, 0.0, 0.0, 0.0, 0.22))
    push("body", new BoxGeometry(0.16, 0.16, 0.38), Util.trs(0.3, 0.4, 0.0))
    push("body", new BoxGeometry(0.16, 0.18, 0.3), Util.trs(0.4, 0.94, 0.0))
    member(parts("metal"), new Vector3(-0.28, 0.28, 0.09), new Vector3(-0.5, 0.245, 0.13), 0.024)
    push("metal", new CylinderGeometry(0.045, 0.045, 0.24, 10),
      Util.trs(-0.62, 0.24, 0.14, 0.0, 0.0, math.Pi / 2.0))

    push("metal", new BoxGeometry(0.28, 0.025, 0.26), Util.trs(-0.66, 0.655, 0.0))
    Vector(-1.0, 1.0).foreach { s =>
      member(parts("metal"), new Vector3(-0.56, 0.65, s * 0.11),
        new Vector3(-0.5, 0.55, s * 0.13), 0.014)
      member(parts("metal"), new Vector3(-0.78, 0.65, s * 0.11),
        new Vector3(-0.68, 0.55, s * 0.12), 0.014)
      member(parts("metal"), new Vector3(-0.54, 0.6, s * 0.145),
        new Vector3(-0.72, 0.7, s * 0.115), 0.013)
    }
    member(parts("metal"), new Vector3(-0.72, 0.7, -0.115), new Vector3(-0.72, 0.7, 0.115), 0.013)
    val sideMat = Toon.flat(FlatOptions(color = Pal.wallGray))
    val faceMat = Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.platePlate()),
      cache = false))
    val plateMats = Props.matArray(sideMat, faceMat, sideMat, sideMat, sideMat, sideMat)
    val plate     = new Mesh(new BoxGeometry(0.02, 0.13, 0.24), plateMats)
    plate.position.set(-0.77, 0.42, 0.0)
    plate.castShadow = true
    ThreeInterop.addChild(inner, plate)

    push("metal", new CylinderGeometry(0.018, 0.018, 0.56, 6),
      Util.trs(P("BAR").x, P("BAR").y, 0.0, math.Pi / 2.0))
    member(parts("metal"), P("HT"), P("BAR"), 0.022)
    Vector(-1.0, 1.0).foreach { s =>
      push("dark", new CylinderGeometry(0.024, 0.024, 0.11, 6),
        Util.trs(P("BAR").x, P("BAR").y, s * 0.22, math.Pi / 2.0))
      member(parts("metal"), new Vector3(0.36, 1.02, s * 0.18),
        new Vector3(0.32, 1.24, s * 0.24), 0.012)
      push("dark", new BoxGeometry(0.03, 0.11, 0.14), Util.trs(0.31, 1.26, s * 0.25))
      ThreeInterop.addChild(inner,
        Util.box(0.008, 0.09, 0.12,
          Toon.flat(FlatOptions(color = Pal.mirrorFace)), 0.294, 1.26, s * 0.25))
      push("amber", new BoxGeometry(0.06, 0.05, 0.05), Util.trs(0.45, 0.86, s * 0.19))
    }

    push("metal", new CylinderGeometry(0.095, 0.095, 0.06, 14),
      Util.trs(0.48, 0.9, 0.0, 0.0, 0.0, math.Pi / 2.0))
    val lampFace = Util.cyl(0.082, 0.082, 0.02, 14,
      Toon.flat(FlatOptions(color = 0xfff4d8)), 0.514, 0.9, 0.0)
    lampFace.rotateZ(math.Pi / 2.0)
    ThreeInterop.addChild(inner, lampFace)

    if o.cockpit then
      push("dark", new CylinderGeometry(0.062, 0.062, 0.05, 14), Util.trs(0.392, 1.06, 0.0, 0.0, 0.0, 0.52))
      Vector(-1.0, 1.0).foreach { s =>
        member(parts("metal"), new Vector3(0.40, 1.0, s * 0.163),
          new Vector3(0.468, 0.988, s * 0.248), 0.009)
      }
      push("metal", new CylinderGeometry(0.026, 0.026, 0.05, 10),
        Util.trs(0.315, 0.88, -0.05, 0.0, 0.0, math.Pi / 2.0))
      val n = new Vector3(-1.0, 0.0, 0.0).applyEuler(new Euler(0.0, 0.0, 0.22))
      val a = new Vector3(0.38, 0.68, 0.0).addScaledVector(ThreeInterop.asVector3Like(n), 0.081)
      val b = a.clone().addScaledVector(ThreeInterop.asVector3Like(n), 0.05)
      member(parts("metal"), a, b, 0.009)
      member(parts("metal"), b, b.clone().add(ThreeInterop.asVector3Like(new Vector3(0.0, 0.035, 0.0))), 0.009)

    emit(inner, parts,
      Map(
        "dark"  -> M.dark, "metal" -> M.metal, "body" -> bodyMat,
        "amber" -> Toon.cel(CelOptions(color = Pal.orange, bands = 2, tint = 0x8f6050)),
        "dial"  -> Toon.flat(FlatOptions(color = 0xe9e6da))
      ),
      outline = Some("body"), thickness = 0.0034)

    inner.rotation.x = o.lean
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g.userData("inner") = inner.asInstanceOf[js.Any]
    g
  end makeScooter

  final case class KitchenGardenOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    w: Double = 1.6,
    d: Double = 0.9,
    h: Double = 0.3,
    seed: Int = 131,
    canColor: Int = 0x3f9c86
  )

  def makeKitchenGarden(o: KitchenGardenOpts): Group[Object3DEventMap] =
    val rng = Util.rngKit(o.seed)
    val g   = new Group()
    val W   = o.w
    val D   = o.d
    val H   = o.h
    val parts = mutable.LinkedHashMap(
      "wood"   -> mutable.ArrayBuffer[Util.BakePart](),
      "post"   -> mutable.ArrayBuffer[Util.BakePart](),
      "bamboo" -> mutable.ArrayBuffer[Util.BakePart]()
    )
    def push(key: String, geo: BufferGeometry[?, ?], mx: Matrix4): Unit =
      parts(key) += Util.BakePart(geo, Some(mx))

    Vector(-1.0, 1.0).foreach { sx =>
      Vector(-1.0, 1.0).foreach { sz =>
        push("post", new BoxGeometry(0.08, H + 0.06, 0.08),
          Util.trs(sx * (W / 2.0 - 0.04), (H + 0.06) / 2.0, sz * (D / 2.0 - 0.04)))
      }
    }
    Vector(H * 0.25, H * 0.72).foreach { y =>
      Vector(-1.0, 1.0).foreach { sz =>
        push("wood", new BoxGeometry(W, 0.13, 0.028), Util.trs(0.0, y, sz * (D / 2.0 - 0.014)))
      }
      Vector(-1.0, 1.0).foreach { sx =>
        push("wood", new BoxGeometry(0.028, 0.13, D - 0.05),
          Util.trs(sx * (W / 2.0 - 0.014), y, 0.0))
      }
    }
    val soil = Util.box(W - 0.06, H - 0.04, D - 0.06, M.soil, 0.0, (H - 0.04) / 2.0, 0.0)
    soil.receiveShadow = true
    ThreeInterop.addChild(g, soil)
    val SOIL = H - 0.04

    val geo  = new IcosahedronGeometry(1.0, 0)
    val rows = Vector(-0.24, 0.0, 0.24)
    val per  = 9
    val inst = new InstancedMesh(geo,
      Toon.cel(CelOptions(color = 0xffffff, bands = 3, tint = 0x5b6f8c, cache = false)),
      (rows.length * per).toDouble)
    val dummy = new Object3D()
    val col   = new Color()
    var i     = 0
    rows.foreach { rz =>
      (0 until per).foreach { k =>
        val r = rng.range(0.05, 0.08)
        dummy.position.set(-W / 2.0 + 0.16 + (k * (W - 0.32)) / (per - 1) + rng.range(-0.03, 0.03),
          SOIL + r * 0.9, rz + rng.range(-0.05, 0.05))
        dummy.rotation.set(rng.range(-0.2, 0.2), rng.range(0, 3), rng.range(-0.2, 0.2))
        dummy.scale.set(r, r * 1.6, r)
        dummy.updateMatrix()
        inst.setMatrixAt(i, dummy.matrix)
        col.set(if k % 3 == 0 then Pal.leafDeep else Pal.leaf)
        inst.setColorAt(i, col)
        i += 1
      }
    }
    ThreeInterop.markForUpload(inst.instanceColor)
    inst.castShadow = true
    ThreeInterop.addChild(g, inst)

    val A0 = new Vector3(0.4, SOIL - 0.04, -0.06)
    val A1 = new Vector3(0.78, 0.92, 0.06)
    val B0 = new Vector3(0.78, SOIL - 0.04, 0.06)
    val B1 = new Vector3(0.4, 0.92, -0.06)
    member(parts("bamboo"), A0, A1, 0.021, 7)
    member(parts("bamboo"), B0, B1, 0.021, 7)
    val cross =
      new Vector3().addVectors(ThreeInterop.asVector3Like(A0), ThreeInterop.asVector3Like(A1)).multiplyScalar(0.5)
    val lash = new Mesh(new TorusGeometry(0.038, 0.009, 4, 10), M.twine)
    lash.position.copy(ThreeInterop.asVector3Like(cross))
    lash.rotation.set(math.Pi / 2.0, 0.0, 0.4)
    ThreeInterop.addChild(g, lash)
    val line = new Mesh(
      new TubeGeometry(
        Util.sagCurve(
          cross.clone().add(ThreeInterop.asVector3Like(new Vector3(0.0, -0.03, 0.0))),
          new Vector3(-(W / 2.0 - 0.04), H + 0.02, -(D / 2.0 - 0.04)), 0.06, 12
        ),
        12, 0.008, 4, false
      ),
      M.twine
    )
    ThreeInterop.addChild(g, line)

    val cp = mutable.ArrayBuffer[Util.BakePart]()
    cp += Util.BakePart(new BoxGeometry(0.19, 0.24, 0.16), Some(Util.trs(0.0, 0.12, 0.0)))
    cp += Util.BakePart(new BoxGeometry(0.2, 0.03, 0.17), Some(Util.trs(0.0, 0.25, 0.0)))
    cp += Util.BakePart(new CylinderGeometry(0.022, 0.03, 0.3, 8),
      Some(Util.trs(-0.15, 0.2, 0.0, 0.0, 0.0, 0.7)))
    cp += Util.BakePart(new CylinderGeometry(0.05, 0.032, 0.05, 8),
      Some(Util.trs(-0.25, 0.31, 0.0, 0.0, 0.0, 0.7)))
    cp += Util.BakePart(new TorusGeometry(0.06, 0.014, 4, 9, math.Pi * 1.1),
      Some(Util.trs(0.07, 0.26, 0.0, math.Pi / 2.0, 0.0, -1.4)))
    val can = new Mesh(Util.bake(cp.toList),
      Toon.cel(CelOptions(color = o.canColor, bands = 3, tint = 0x4a6a80)))
    can.castShadow = true
    can.receiveShadow = true
    can.position.set(-(W / 2.0 + 0.13), 0.028, 0.16)
    can.rotation.z = -0.3
    ThreeInterop.addChild(g, can)

    emit(g, parts, Map("wood" -> M.wood, "post" -> M.woodDark, "bamboo" -> M.bamboo))

    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g.userData("top") = (H + 0.06).asInstanceOf[js.Any]
    g
  end makeKitchenGarden

  final case class KidBikeOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    scale: Double = 0.62,
    color: Int = 0xd8563c,
    lean: Double = 0.0
  )

  def makeKidBike(o: KidBikeOpts): Group[Object3DEventMap] =
    val g      = new Group()
    val scaled = new Group()
    scaled.scale.setScalar(o.scale)
    ThreeInterop.addChild(g, scaled)

    ThreeInterop.addChild(scaled,
      Props.makeBicycle(Props.BicycleOpts(
        x = 0.0, z = 0.0, color = o.color, lean = o.lean
      )))

    val parts = mutable.ArrayBuffer[Util.BakePart]()
    Vector(-1.0, 1.0).foreach { s =>
      member(parts, new Vector3(-0.52, 0.31, s * 0.06), new Vector3(-0.44, 0.15, s * 0.3), 0.018)
      parts += Util.BakePart(new CylinderGeometry(0.15, 0.15, 0.06, 10),
        Some(Util.trs(-0.44, 0.14, s * 0.335, math.Pi / 2.0)))
    }
    val mesh = new Mesh(Util.bake(parts.toList), M.dark)
    mesh.castShadow = true
    ThreeInterop.addChild(scaled, mesh)

    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeKidBike

  final case class DryingRackOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    w: Double = 1.2,
    h: Double = 1.05,
    n: Int = 5,
    seed: Int = 137
  )

  def makeDryingRack(o: DryingRackOpts): Group[Object3DEventMap] =
    val rng    = Util.rngKit(o.seed)
    val g      = new Group()
    val W      = o.w
    val H      = o.h
    val SPREAD = 0.46
    val parts = mutable.LinkedHashMap(
      "metal" -> mutable.ArrayBuffer[Util.BakePart](),
      "foot"  -> mutable.ArrayBuffer[Util.BakePart]()
    )
    def push(key: String, geo: BufferGeometry[?, ?], mx: Matrix4): Unit =
      parts(key) += Util.BakePart(geo, Some(mx))

    Vector(-1.0, 1.0).foreach { sx =>
      val apex = new Vector3(sx * (W / 2.0 - 0.04), H, 0.0)
      Vector(-1.0, 1.0).foreach { sz =>
        val foot = new Vector3(sx * (W / 2.0 - 0.04), 0.02, sz * SPREAD)
        member(parts("metal"), foot, apex, 0.017)
        push("foot", new BoxGeometry(0.06, 0.04, 0.07), Util.trs(foot.x, 0.02, foot.z))
      }
      val sy = 0.45
      val sw = SPREAD * (1 - sy / H)
      member(parts("metal"), new Vector3(apex.x, sy, -sw), new Vector3(apex.x, sy, sw), 0.014)
    }
    val BARS = Vector((H, 0.0), (0.58, -0.2), (0.58, 0.2))
    BARS.foreach { (by, bz) =>
      push("metal", new CylinderGeometry(0.014, 0.014, W, 6),
        Util.trs(0.0, by, bz, 0.0, 0.0, math.Pi / 2.0))
    }

    val COLS = Vector(Pal.wallBlue, Pal.blossom, Pal.wallWhite, Pal.yellow, Pal.wallCream, 0xa8cfe0)
    (0 until o.n).foreach { i =>
      val (by, bz) = BARS(i % BARS.length)
      val w        = rng.range(0.28, 0.42)
      val h        = rng.range(0.38, 0.56)
      val t = new Mesh(new PlaneGeometry(w, h),
        Toon.cel(CelOptions(color = rng.pick(COLS), bands = 2, side = 2, tint = 0x6f6790)))
      t.position.set(rng.range(-W / 2.0 + 0.24, W / 2.0 - 0.24), by - h / 2.0 - 0.03,
        bz + math.signum(bz) * 0.03)
      t.rotation.set(math.signum(bz) * 0.14, rng.range(-0.16, 0.16), rng.range(-0.06, 0.06))
      t.castShadow = true
      ThreeInterop.addChild(g, t)
      push("metal", new BoxGeometry(0.04, 0.03, 0.03),
        Util.trs(t.position.x - w / 2.0 + 0.03, by, bz))
    }

    emit(g, parts, Map("metal" -> M.metal, "foot" -> M.dark))

    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g.userData("top") = H.asInstanceOf[js.Any]
    g
  end makeDryingRack

  final case class BallBoxOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    seed: Int = 149,
    loose: Double = 0.58
  )

  def makeBallBox(o: BallBoxOpts): Group[Object3DEventMap] =
    val rng = Util.rngKit(o.seed)
    val g   = new Group()
    val W   = 0.7
    val D   = 0.5
    val H   = 0.45
    val parts = mutable.LinkedHashMap(
      "wood" -> mutable.ArrayBuffer[Util.BakePart](),
      "band" -> mutable.ArrayBuffer[Util.BakePart]()
    )
    def push(key: String, geo: BufferGeometry[?, ?], mx: Matrix4): Unit =
      parts(key) += Util.BakePart(geo, Some(mx))

    Vector(-1.0, 1.0).foreach { sx =>
      Vector(-1.0, 1.0).foreach { sz =>
        push("wood", new BoxGeometry(0.05, H, 0.05),
          Util.trs(sx * (W / 2.0 - 0.025), H / 2.0, sz * (D / 2.0 - 0.025)))
      }
    }
    Vector(0.06, 0.2, 0.34).foreach { y =>
      Vector(-1.0, 1.0).foreach { sz =>
        push("wood", new BoxGeometry(W - 0.04, 0.085, 0.02), Util.trs(0.0, y, sz * (D / 2.0 - 0.01)))
      }
      Vector(-1.0, 1.0).foreach { sx =>
        push("wood", new BoxGeometry(0.02, 0.085, D - 0.06),
          Util.trs(sx * (W / 2.0 - 0.01), y, 0.0))
      }
    }
    Vector(-1.0, 1.0).foreach { sz =>
      push("wood", new BoxGeometry(W, 0.03, 0.06), Util.trs(0.0, H - 0.015, sz * (D / 2.0 - 0.03)))
    }
    Vector(-1.0, 1.0).foreach { sx =>
      push("wood", new BoxGeometry(0.06, 0.03, D - 0.12),
        Util.trs(sx * (W / 2.0 - 0.03), H - 0.015, 0.0))
    }
    Vector(-0.16, 0.0, 0.16).foreach { z =>
      push("wood", new BoxGeometry(W - 0.06, 0.02, 0.1), Util.trs(0.0, 0.05, z))
    }

    val COLS  = Vector(Pal.red, Pal.blue, Pal.yellow, 0x4f9d6a, Pal.orange)
    val FLOOR = 0.06
    val spots = Vector(
      (-0.15, 0.0, 0.11, FLOOR + 0.11), (0.15, 0.03, 0.1, FLOOR + 0.1), (o.loose, 0.12, 0.11, 0.11)
    )
    spots.zipWithIndex.foreach { case ((bx, bz, r, by), i) =>
      val ball = new Mesh(new SphereGeometry(r, 12, 9),
        Toon.cel(CelOptions(color = COLS((i * 2 + o.seed) % COLS.length), bands = 3, tint = 0x6f6790)))
      ball.position.set(bx, by, bz)
      ball.castShadow = true
      ThreeInterop.addChild(g, ball)
      push("band", new TorusGeometry(r * 0.99, 0.012, 4, 14),
        Util.trs(bx, by, bz, rng.range(0, 3), rng.range(0, 3), rng.range(0, 3)))
    }

    emit(g, parts,
      Map(
        "wood" -> M.wood,
        "band" -> Toon.cel(CelOptions(color = Pal.lineWhite, bands = 3, tint = 0x6f6790))
      ))

    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g.userData("top") = H.asInstanceOf[js.Any]
    g
  end makeBallBox

  final case class WheelStopsOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    n: Int = 2,
    pitch: Double = 2.5,
    gauge: Double = 1.4
  )

  def makeWheelStops(o: WheelStopsOpts): Group[Object3DEventMap] =
    val g   = new Group()
    val LEN = 0.6
    val parts = mutable.LinkedHashMap(
      "concrete" -> mutable.ArrayBuffer[Util.BakePart](),
      "chamfer"  -> mutable.ArrayBuffer[Util.BakePart](),
      "dark"     -> mutable.ArrayBuffer[Util.BakePart](),
      "metal"    -> mutable.ArrayBuffer[Util.BakePart]()
    )
    def push(key: String, geo: BufferGeometry[?, ?], mx: Matrix4): Unit =
      parts(key) += Util.BakePart(geo, Some(mx))

    (0 until o.n).foreach { i =>
      val bx = -((o.n - 1) * o.pitch) / 2.0 + i * o.pitch
      Vector(-1.0, 1.0).foreach { s =>
        val sx = bx + (s * o.gauge) / 2.0
        push("concrete", new BoxGeometry(LEN, 0.12, 0.16), Util.trs(sx, 0.06, 0.0))
        push("chamfer", new BoxGeometry(LEN - 0.04, 0.04, 0.11), Util.trs(sx, 0.14, 0.0))
        Vector(-0.18, 0.18).foreach { t =>
          push("dark", new CylinderGeometry(0.018, 0.018, 0.03, 6), Util.trs(sx + t, 0.165, 0.0))
        }
      }
      push("metal", new BoxGeometry(0.045, 0.35, 0.045), Util.trs(bx, 0.175, -0.26))
      val side      = Toon.cel(CelOptions(color = 0xe8e4da, bands = 3, tint = 0x6f6790))
      val plateMats = Props.matArray(side, side, side, side, side, side)
      val plate     = new Mesh(new BoxGeometry(0.18, 0.12, 0.022), plateMats)
      plate.position.set(bx, 0.36, -0.24)
      plate.castShadow = true
      ThreeInterop.addChild(g, plate)
      push("metal", new BoxGeometry(0.18, 0.026, 0.026), Util.trs(bx, 0.425, -0.24))
    }

    emit(g, parts,
      Map(
        "concrete" -> M.concreteMid, "chamfer" -> M.concrete, "dark" -> M.dark,
        "metal"    -> M.metalDark
      ),
      noCast = Vector("chamfer"))

    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g.userData("top") = 0.16.asInstanceOf[js.Any]
    g
  end makeWheelStops

  final case class GasMeterOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    standoff: Double = 0.06,
    y0: Double = 0.72
  )

  def makeGasMeter(o: GasMeterOpts): Group[Object3DEventMap] =
    val g    = new Group()
    val W    = 0.34
    val HH   = 0.46
    val D    = 0.2
    val so   = o.standoff
    val Y0   = o.y0
    val WALL = -(D / 2.0 + so)
    val parts = mutable.LinkedHashMap(
      "shell" -> mutable.ArrayBuffer[Util.BakePart](),
      "door"  -> mutable.ArrayBuffer[Util.BakePart](),
      "metal" -> mutable.ArrayBuffer[Util.BakePart](),
      "dark"  -> mutable.ArrayBuffer[Util.BakePart](),
      "pipe"  -> mutable.ArrayBuffer[Util.BakePart]()
    )
    def push(key: String, geo: BufferGeometry[?, ?], mx: Matrix4): Unit =
      parts(key) += Util.BakePart(geo, Some(mx))

    val body = Util.box(W, HH, D, M.shell, 0.0, Y0 + HH / 2.0, 0.0)
    body.castShadow = true
    body.receiveShadow = true
    ThreeInterop.addChild(g, body)
    Outline.hullOutline(body, OutlineOptions(thickness = 0.003))
    push("shell", new BoxGeometry(W + 0.03, 0.035, D + 0.03),
      Util.trs(0.0, Y0 + HH + 0.015, 0.0))

    push("door", new BoxGeometry(W - 0.04, HH - 0.06, 0.02),
      Util.trs(0.0, Y0 + HH / 2.0, D / 2.0 + 0.01))
    push("metal", new BoxGeometry(0.05, 0.09, 0.03),
      Util.trs(W / 2.0 - 0.05, Y0 + HH / 2.0, D / 2.0 + 0.03))
    Vector(Y0 + HH - 0.07, Y0 + HH - 0.11).foreach { y =>
      push("dark", new BoxGeometry(W - 0.14, 0.014, 0.016), Util.trs(0.0, y, D / 2.0 + 0.026))
    }
    push("metal", new CylinderGeometry(0.062, 0.062, 0.016, 14),
      Util.trs(-0.06, Y0 + 0.28, D / 2.0 + 0.035, math.Pi / 2.0))
    val dial = Util.cyl(0.052, 0.052, 0.014, 14, M.pale, -0.06, Y0 + 0.28, D / 2.0 + 0.048)
    dial.rotateX(math.Pi / 2.0)
    ThreeInterop.addChild(g, dial)
    push("dark", new BoxGeometry(0.048, 0.006, 0.006),
      Util.trs(-0.048, Y0 + 0.29, D / 2.0 + 0.058))
    ThreeInterop.addChild(g,
      Util.box(0.13, 0.06, 0.008, M.pale, 0.06, Y0 + 0.11, D / 2.0 + 0.026))

    Vector(-1.0, 1.0).foreach { s =>
      val bx = (s * (W - 0.1)) / 2.0
      push("metal", new BoxGeometry(0.04, 0.035, D + so), Util.trs(bx, Y0 - 0.018, -so / 2.0))
      push("metal", new BoxGeometry(0.04, 0.17, 0.03), Util.trs(bx, Y0 + 0.07, WALL + 0.015))
      push("metal", new BoxGeometry(0.07, 0.07, 0.018), Util.trs(bx, Y0 + 0.14, WALL + 0.009))
    }

    val px  = -(W / 2.0 + 0.05)
    val pz  = WALL + 0.075
    val ELB = Y0 - 0.06
    member(parts("pipe"), new Vector3(px, 0.14, pz), new Vector3(px, ELB, pz), 0.022, 8)
    member(parts("pipe"), new Vector3(px, ELB, pz), new Vector3(-0.08, ELB, pz), 0.022, 8)
    member(parts("pipe"), new Vector3(-0.08, ELB, pz), new Vector3(-0.08, Y0 + 0.02, pz), 0.022, 8)
    Vector(new Vector3(px, ELB, pz), new Vector3(-0.08, ELB, pz)).foreach { p =>
      push("pipe", new SphereGeometry(0.026, 8, 6), Util.trs(p.x, p.y, p.z))
    }
    push("pipe", new CylinderGeometry(0.032, 0.032, 0.14, 8), Util.trs(px, 0.07, pz))
    push("metal", new BoxGeometry(0.11, 0.03, 0.11), Util.trs(px, 0.015, pz))
    push("metal", new BoxGeometry(0.05, 0.028, 0.075), Util.trs(px, 0.46, WALL + 0.038))

    emit(g, parts,
      Map(
        "shell" -> M.shellTrim,
        "door"  -> Toon.cel(CelOptions(color = 0xd2d6da, bands = 3, tint = 0x666090)),
        "metal" -> M.metalDark, "dark" -> M.dark, "pipe" -> M.metal
      ))

    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g.userData("top") = (Y0 + HH + 0.03).asInstanceOf[js.Any]
    g
  end makeGasMeter

  final case class WaterMeterOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    w: Double = 0.32,
    d: Double = 0.24
  )

  def makeWaterMeter(o: WaterMeterOpts): Group[Object3DEventMap] =
    val g   = new Group()
    val W   = o.w
    val D   = o.d
    val RIM = 0.03
    val parts = mutable.LinkedHashMap(
      "rim"   -> mutable.ArrayBuffer[Util.BakePart](),
      "lid"   -> mutable.ArrayBuffer[Util.BakePart](),
      "panel" -> mutable.ArrayBuffer[Util.BakePart]()
    )
    def push(key: String, geo: BufferGeometry[?, ?], mx: Matrix4): Unit =
      parts(key) += Util.BakePart(geo, Some(mx))

    Vector(-1.0, 1.0).foreach { sz =>
      push("rim", new BoxGeometry(W + RIM * 2.0, 0.026, RIM),
        Util.trs(0.0, 0.008, sz * (D / 2.0 + RIM / 2.0)))
    }
    Vector(-1.0, 1.0).foreach { sx =>
      push("rim", new BoxGeometry(RIM, 0.026, D), Util.trs(sx * (W / 2.0 + RIM / 2.0), 0.008, 0.0))
    }
    push("lid", new BoxGeometry(W - 0.005, 0.022, D - 0.005), Util.trs(0.0, 0.005, 0.0))
    Vector(-1.0, 1.0).foreach { sz =>
      push("lid", new BoxGeometry(W - 0.08, 0.006, 0.022), Util.trs(0.0, 0.018, sz * 0.05))
    }
    push("lid", new BoxGeometry(0.06, 0.008, 0.024), Util.trs(W / 2.0 - 0.05, 0.019, 0.0))
    push("panel", new BoxGeometry(0.11, 0.006, 0.042), Util.trs(-0.05, 0.019, 0.0))

    val rimMesh = new Mesh(Util.bake(parts("rim").toList), M.concreteMid)
    val lidMesh = new Mesh(Util.bake(parts("lid").toList),
      Toon.cel(CelOptions(color = 0x6f7480, bands = 3, tint = 0x5c5680)))
    val panelMesh = new Mesh(Util.bake(parts("panel").toList), M.pale)
    Vector(rimMesh, lidMesh, panelMesh).foreach { mesh =>
      mesh.castShadow = false
      mesh.receiveShadow = true
      ThreeInterop.addChild(g, mesh)
    }
    ThreeInterop.addChild(g, Util.box(0.05, 0.01, 0.016, M.dark, W / 2.0 - 0.085, 0.021, 0.0))

    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g.userData("top") = 0.021.asInstanceOf[js.Any]
    g
  end makeWaterMeter

  final case class ChalkSpot(
    x: Double,
    z: Double,
    y: Double = 0.0,
    ry: Double = 0.0,
    scale: Double = 1.0,
    seed: Int = 7
  )

  def makeChalkMarks(ctx: PropCtx, spots: Vector[ChalkSpot], opacity: Double = 0.3): Mesh[?, ?, ?] =
    case class Stroke(x: Double, z: Double, y: Double, len: Double, w: Double, ang: Double)
    val strokes = mutable.ArrayBuffer[Stroke]()
    def line(arr: mutable.ArrayBuffer[Stroke], x0: Double, z0: Double, x1: Double, z1: Double, w: Double): Unit =
      val dx = x1 - x0
      val dz = z1 - z0
      arr += Stroke((x0 + x1) / 2.0, (z0 + z1) / 2.0, 0.0, math.hypot(dx, dz), w,
        math.atan2(-dz, dx))
    def rect(arr: mutable.ArrayBuffer[Stroke], cx: Double, cz: Double, w: Double, d: Double, t: Double): Unit =
      line(arr, cx - w / 2.0, cz - d / 2.0, cx + w / 2.0, cz - d / 2.0, t)
      line(arr, cx - w / 2.0, cz + d / 2.0, cx + w / 2.0, cz + d / 2.0, t)
      line(arr, cx - w / 2.0, cz - d / 2.0, cx - w / 2.0, cz + d / 2.0, t)
      line(arr, cx + w / 2.0, cz - d / 2.0, cx + w / 2.0, cz + d / 2.0, t)

    spots.foreach { s =>
      val rng   = Util.rngKit(s.seed)
      val local = mutable.ArrayBuffer[Stroke]()
      if rng.chance(0.55) then
        val n    = rng.int(3, 4)
        val side = rng.range(0.4, 0.5)
        (0 until n).foreach(i => rect(local, 0.0, -0.3 + i * (side + 0.02), side, side, 0.03))
      else
        val r = rng.range(0.4, 0.6)
        val n = 12
        (0 until n).foreach { i =>
          val a0 = (i.toDouble / n) * math.Pi * 2.0
          val a1 = ((i + 1).toDouble / n) * math.Pi * 2.0
          line(local, math.cos(a0) * r, math.sin(a0) * r, math.cos(a1) * r, math.sin(a1) * r, 0.028)
        }
      // upstream writes `for (let i = 0; i < rng.int(2, 4); i++)`, so the bound is drawn
      // again on every pass: reproducing that draw order is what keeps the two streak
      // sets identical, not the nominal 2..4 count.
      var streak = 0
      while streak < rng.int(2, 4) do
        val a   = rng.range(0, math.Pi * 2.0)
        val len = rng.range(0.25, 0.7)
        val cx  = rng.range(-0.7, 0.7)
        val cz  = rng.range(-0.7, 0.7)
        line(local, cx, cz, cx + math.cos(a) * len, cz + math.sin(a) * len, rng.range(0.02, 0.035))
        streak += 1

      val k  = s.scale
      val ry = s.ry
      val c  = math.cos(ry)
      val sn = math.sin(ry)
      local.foreach { st =>
        val lx = st.x * k
        val lz = st.z * k
        strokes += Stroke(
          s.x + lx * c + lz * sn, s.z - lx * sn + lz * c, s.y + 0.02,
          st.len * k, st.w, ry + st.ang
        )
      }
    }
    if strokes.isEmpty then throw new IllegalArgumentException("sakura-crossing: chalk needs spots")

    val geo = new PlaneGeometry(1.0, 1.0)
    geo.rotateX(-math.Pi / 2.0)
    val inst = new InstancedMesh(geo,
      Toon.flat(FlatOptions(color = 0xf6f4ee, transparent = true, opacity = opacity,
        depthWrite = Some(false), cache = false)),
      strokes.length.toDouble)
    val dummy = new Object3D()
    strokes.zipWithIndex.foreach { (st, i) =>
      dummy.position.set(st.x, st.y, st.z)
      dummy.rotation.set(0.0, st.ang, 0.0)
      dummy.scale.set(st.len, 1.0, st.w)
      dummy.updateMatrix()
      inst.setMatrixAt(i, dummy.matrix)
    }
    ThreeInterop.setUserDataFlag(inst, "noOutline")
    inst.renderOrder = 1.0
    ctx.add(inst)
    inst
  end makeChalkMarks

end StreetProps
