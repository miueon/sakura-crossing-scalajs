package sakura.world

import sakura.ThreeInterop
import sakura.core.{Outline, Pal, Textures, Toon, Util}
import sakura.core.Outline.OutlineOptions
import sakura.core.Toon.{CelOptions, FlatOptions}
import vendor.three.*

import scala.collection.mutable
import scala.math
import scala.scalajs.js

/** 1:1 port of upstream `src/world/vehicles.js`: one generator, nine bodies, kind as a table
  * row. Authored along +x with the nose at +x, origin at ground level in the footprint centre.
  */
object Vehicles:

  enum VehicleKind:
    case Kei, Keivan, Hatch, Sedan, Wagon, Minivan, Van, Boxtruck, Minibus, Keitruck

  final case class CarSpec(
    L: Double,
    W: Double,
    R: Double,
    axle: Vector[Double],
    sill: Double,
    waist: Double,
    roof: Double,
    cab: Vector[Double],
    rakeF: Double,
    rakeR: Double,
    side: Option[Vector[Double]] = None,
    seams: Vector[Double] = Vector.empty,
    handles: Vector[Double] = Vector.empty,
    slider: Option[Double] = None,
    rails: Boolean = false,
    steelies: Boolean = false,
    doors: Vector[Double] = Vector.empty,
    bus: Boolean = false,
    box: Option[(Double, Double, Double, Double)] = None
  )

  val Car: Map[String, Int] = Map(
    "white"    -> 0xf2eee6, "pearl"    -> 0xe8e4dc, "cream"   -> 0xe9dfc6, "silver" -> 0xc9c8cc,
    "gunmetal" -> 0x8d8f98, "charcoal" -> 0x63626e, "skyblue" -> 0xc2d3de, "slate"  -> 0x7f93a4,
    "mint"     -> 0xc3d8cc, "forest"   -> 0x6d8c78, "wine"    -> 0x8b4a4c, "tea"    -> 0xcbbfa6,
    "mustard"  -> 0xd9b45f, "navy"     -> 0x5a7093
  )

  val Spec: Map[VehicleKind, CarSpec] = Map(
    VehicleKind.Kei -> CarSpec(
      L = 3.40, W = 1.475, R = 0.275, axle = Vector(1.16, -1.19),
      sill = 0.38, waist = 1.00, roof = 1.70,
      cab = Vector(-1.60, 1.15), rakeF = 0.42, rakeR = 0.14,
      seams = Vector(0.12, -0.80), handles = Vector(-0.60, -1.40)
    ),
    VehicleKind.Keivan -> CarSpec(
      L = 3.40, W = 1.475, R = 0.275, axle = Vector(1.18, -1.16),
      sill = 0.38, waist = 1.02, roof = 1.88,
      cab = Vector(-1.66, 1.55), rakeF = 0.28, rakeR = 0.06,
      side = Some(Vector(0.34, 1.22)), seams = Vector(0.30), handles = Vector(0.04), rails = true
    ),
    VehicleKind.Hatch -> CarSpec(
      L = 4.05, W = 1.695, R = 0.30, axle = Vector(1.32, -1.22),
      sill = 0.36, waist = 0.98, roof = 1.52,
      cab = Vector(-1.72, 0.88), rakeF = 0.62, rakeR = 0.52,
      seams = Vector(0.10, -0.85), handles = Vector(-0.20, -1.10)
    ),
    VehicleKind.Sedan -> CarSpec(
      L = 4.42, W = 1.695, R = 0.31, axle = Vector(1.36, -1.28),
      sill = 0.36, waist = 0.97, roof = 1.44,
      cab = Vector(-1.55, 0.95), rakeF = 0.68, rakeR = 0.62,
      seams = Vector(0.16, -0.78), handles = Vector(-0.14, -1.02)
    ),
    VehicleKind.Wagon -> CarSpec(
      L = 4.25, W = 1.690, R = 0.30, axle = Vector(1.32, -1.20),
      sill = 0.36, waist = 0.97, roof = 1.54,
      cab = Vector(-1.92, 0.93), rakeF = 0.60, rakeR = 0.16,
      seams = Vector(0.14, -0.80), handles = Vector(-0.16, -1.06), steelies = true
    ),
    VehicleKind.Minivan -> CarSpec(
      L = 4.34, W = 1.695, R = 0.30, axle = Vector(1.42, -1.20),
      sill = 0.38, waist = 1.06, roof = 1.80,
      cab = Vector(-2.00, 1.32), rakeF = 0.58, rakeR = 0.14,
      seams = Vector(0.28), handles = Vector(0.02, -1.30),
      slider = Some(-0.70), rails = true
    ),
    VehicleKind.Van -> CarSpec(
      L = 4.44, W = 1.695, R = 0.31, axle = Vector(1.48, -1.28),
      sill = 0.40, waist = 1.12, roof = 1.98,
      cab = Vector(-2.14, 2.02), rakeF = 0.32, rakeR = 0.06,
      side = Some(Vector(0.70, 1.62)), seams = Vector(0.60), handles = Vector(0.34), rails = true
    ),
    VehicleKind.Boxtruck -> CarSpec(
      L = 4.80, W = 1.695, R = 0.325, axle = Vector(1.62, -1.16),
      sill = 0.46, waist = 1.20, roof = 1.98,
      cab = Vector(0.42, 2.16), rakeF = 0.28, rakeR = 0.10,
      side = Some(Vector(0.72, 1.84)), seams = Vector(0.62), handles = Vector(0.30),
      box = Some((-2.40, 0.28, 1.06, 2.46))
    ),
    VehicleKind.Minibus -> CarSpec(
      L = 6.30, W = 2.08, R = 0.36, axle = Vector(2.06, -1.86),
      sill = 0.52, waist = 1.26, roof = 2.60,
      cab = Vector(-3.04, 2.94), rakeF = 0.26, rakeR = 0.08,
      side = Some(Vector(-2.80, 2.55)), seams = Vector(1.72, -0.36), handles = Vector.empty,
      doors = Vector(1.72, -0.36), bus = true
    )
  )

  final case class VehicleSize(L: Double, W: Double, H: Double)

  def vehicleSize(kind: VehicleKind): VehicleSize =
    Spec.get(kind) match
      case Some(s) =>
        VehicleSize(s.L, if s.box.isDefined then s.W + 0.06 else s.W,
          if s.box.isDefined then s.box.get._4 else s.roof)
      case None => VehicleSize(3.32, 1.46, 1.90)

  private object M:
    lazy val dark        = Toon.cel(CelOptions(color = 0x36333e, bands = 2, tint = 0x4b4560))
    lazy val tyre        = Toon.cel(CelOptions(color = 0x3b3843, bands = 2, tint = 0x4b4560))
    lazy val brite       = Toon.cel(CelOptions(color = Pal.metal, bands = 3, tint = 0x666090))
    lazy val briteDark   = Toon.cel(CelOptions(color = Pal.metalDark, bands = 3, tint = 0x5c5680))
    lazy val concreteMid = Toon.cel(CelOptions(color = Pal.concreteMid, bands = 3, tint = 0x6a6288))
    lazy val glass       = Toon.flat(FlatOptions(color = Pal.glassDark))
    lazy val lampF       = Toon.flat(FlatOptions(color = 0xfff2d4))
    lazy val lampR       = Toon.flat(FlatOptions(color = 0xd8564e))
    lazy val plate =
      Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.platePlate()), cache = false))

  private def bodyMat(c: Int): MeshToonMaterial =
    Toon.cel(CelOptions(color = c, bands = 3, tint = 0x6a6288))

  private def deepMat(c: Int): MeshToonMaterial =
    val deep = new Color(c.toDouble).multiplyScalar(0.76).getHex().toInt
    Toon.cel(CelOptions(color = deep, bands = 3, tint = 0x5e5680))

  private def panel(
    arr: mutable.ArrayBuffer[Util.BakePart],
    ax: Double,
    ay: Double,
    bx: Double,
    by: Double,
    w: Double,
    t: Double
  ): Unit =
    val dx  = bx - ax
    val dy  = by - ay
    val len = math.hypot(dx, dy)
    if len >= 1e-4 then
      arr += Util.BakePart(new BoxGeometry(len, t, w),
        Some(Util.trs((ax + bx) / 2.0, (ay + by) / 2.0, 0.0, 0.0, 0.0, math.atan2(dy, dx))))

  private def emit(
    g: Group[Object3DEventMap],
    parts: mutable.LinkedHashMap[String, mutable.ArrayBuffer[Util.BakePart]],
    matFor: Map[String, Material[?]],
    noCast: Vector[String] = Vector.empty
  ): Unit =
    parts.foreach { (key, bucket) =>
      if bucket.nonEmpty then
        val mesh = new Mesh(Util.bake(bucket.toList), matFor(key))
        mesh.castShadow = !noCast.contains(key)
        mesh.receiveShadow = true
        ThreeInterop.addChild(g, mesh)
    }

  final case class VehicleOpts(
    kind: VehicleKind = VehicleKind.Kei,
    color: Int = 0xf2eee6,
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    skew: Option[Double] = None,
    hero: Boolean = false,
    load: Option[String] = None
  )

  def makeVehicle(o: VehicleOpts): Group[Object3DEventMap] =
    if o.kind == VehicleKind.Keitruck then
      /* The flatbed is `props.js`'s, not this file's, so the colour has to be handed over rather
       * than defaulted -- upstream passes `color: o.color` and `makeKeiTruck` falls back to its own
       * タクシー yellow only when it is absent.  `hero` is the other way round: upstream leaves it
       * undefined on this path and `props.js` reads `o.hero !== false`, so the flatbed always gets
       * its outline shells unless a caller says otherwise.
       */
      return Props.makeKeiTruck(Props.KeiTruckOpts(
        x = o.x, y = o.y, z = o.z, ry = o.ry, color = Some(o.color), load = o.load, hero = true
      ))

    val s = Spec(o.kind)
    val g = new Group()

    val mBody = bodyMat(o.color)
    val mDeep = deepMat(o.color)

    val HW        = s.W / 2.0
    val TRACK     = s.W - 0.17
    val TW        = if s.R < 0.3 then 0.165 else 0.195
    val CW        = s.W - 0.14
    val roofFront = s.cab(1) - s.rakeF
    val roofRear  = s.cab(0) + s.rakeR
    val side      = s.side.getOrElse(Vector(roofRear, roofFront))

    val parts = mutable.LinkedHashMap(
      "body"  -> mutable.ArrayBuffer[Util.BakePart](),
      "deep"  -> mutable.ArrayBuffer[Util.BakePart](),
      "dark"  -> mutable.ArrayBuffer[Util.BakePart](),
      "brite" -> mutable.ArrayBuffer[Util.BakePart](),
      "glass" -> mutable.ArrayBuffer[Util.BakePart](),
      "lampF" -> mutable.ArrayBuffer[Util.BakePart](),
      "lampR" -> mutable.ArrayBuffer[Util.BakePart]()
    )
    def push(key: String, geo: BufferGeometry[?, ?], mx: Matrix4): Unit =
      parts(key) += Util.BakePart(geo, Some(mx))

    push("body", new BoxGeometry(s.L, s.waist - s.sill, s.W),
      Util.trs(0.0, (s.sill + s.waist) / 2.0, 0.0))
    push("deep", new BoxGeometry(s.L - 0.16, s.sill - 0.10, s.W - 0.07),
      Util.trs(0.0, (s.sill + 0.10) / 2.0 + 0.05, 0.0))

    if s.L / 2.0 - s.cab(1) > 0.55 then
      panel(parts("body"), s.L / 2.0 - 0.01, s.waist - 0.15, s.L / 2.0 - 0.46, s.waist + 0.01,
        s.W - 0.05, 0.13)
    if s.cab(0) + s.L / 2.0 > 0.45 then
      panel(parts("body"), -s.L / 2.0 + 0.01, s.waist - 0.10, -s.L / 2.0 + 0.30, s.waist + 0.01,
        s.W - 0.05, 0.11)

    push("body", new BoxGeometry(roofFront - roofRear, s.roof - s.waist, CW),
      Util.trs((roofFront + roofRear) / 2.0, (s.waist + s.roof) / 2.0, 0.0))
    panel(parts("body"), s.cab(1), s.waist, roofFront, s.roof, CW, 0.11)
    panel(parts("body"), s.cab(0), s.waist, roofRear, s.roof, CW, 0.11)
    push("body", new BoxGeometry(roofFront - roofRear + 0.05, 0.05, CW + 0.03),
      Util.trs((roofFront + roofRear) / 2.0, s.roof - 0.015, 0.0))
    Vector(-1.0, 1.0).foreach { t =>
      push("deep", new BoxGeometry(roofFront - roofRear, 0.035, 0.035),
        Util.trs((roofFront + roofRear) / 2.0, s.roof - 0.05, t * (CW / 2.0 + 0.005)))
    }

    val cx = (roofFront + roofRear) / 2.0
    val cy = (s.waist + s.roof) / 2.0
    def lay(ax: Double, ay: Double, bx: Double, by: Double, k: Double): Unit =
      val dx  = bx - ax
      val dy  = by - ay
      val len = math.hypot(dx, dy)
      val ux  = dx / len
      val uy  = dy / len
      var nx  = uy
      var ny  = -ux
      if nx * ((ax + bx) / 2.0 - cx) + ny * ((ay + by) / 2.0 - cy) < 0 then
        nx = -nx
        ny = -ny
      val off = 0.062
      panel(parts("glass"),
        ax + ux * k + nx * off, ay + uy * k + ny * off,
        bx - ux * k + nx * off, by - uy * k + ny * off, CW - 0.15, 0.055)
    lay(s.cab(1), s.waist + 0.05, roofFront, s.roof - 0.045, 0.05)
    lay(s.cab(0), s.waist + 0.05, roofRear, s.roof - 0.045, 0.05)

    val y0 = s.waist + 0.035
    val y1 = s.roof - 0.075
    val x0 = math.max(side(0), roofRear) + 0.04
    val x1 = math.min(side(1), roofFront) - 0.04
    if x1 > x0 + 0.12 then
      Vector(-1.0, 1.0).foreach { t =>
        push("glass", new BoxGeometry(x1 - x0, y1 - y0, 0.05),
          Util.trs((x0 + x1) / 2.0, (y0 + y1) / 2.0, t * (CW / 2.0 - 0.006)))
      }
      s.seams.foreach { px =>
        if px >= x0 + 0.06 && px <= x1 - 0.06 then
          Vector(-1.0, 1.0).foreach { t =>
            push("body", new BoxGeometry(0.075, y1 - y0 + 0.02, 0.05),
              Util.trs(px, (y0 + y1) / 2.0, t * (CW / 2.0 - 0.002)))
          }
      }

    s.axle.foreach { ax =>
      Vector(-1.0, 1.0).foreach { t =>
        val z = t * (TRACK / 2.0)
        push("dark", new CylinderGeometry(s.R, s.R, TW, 14), Util.trs(ax, s.R, z, math.Pi / 2.0))
        push(if s.steelies then "deep" else "brite",
          new CylinderGeometry(s.R * 0.62, s.R * 0.62, TW + 0.02, 12),
          Util.trs(ax, s.R, z, math.Pi / 2.0))
        push("dark", new CylinderGeometry(s.R * 0.22, s.R * 0.22, TW + 0.04, 8),
          Util.trs(ax, s.R, z, math.Pi / 2.0))
        push("dark", new TorusGeometry(s.R + 0.015, 0.055, 4, 12, math.Pi),
          Util.trs(ax, s.R + 0.01, t * (HW - 0.095)))
        push("deep", new TorusGeometry(s.R + 0.055, 0.030, 4, 14, math.Pi),
          Util.trs(ax, s.R + 0.01, t * (HW + 0.008), 0.0, 0.0, 0.0, 1.0, 1.0, 1.3))
      }
    }

    val lampY = math.min(s.waist - 0.17, s.sill + 0.36)
    Vector((1.0, true), (-1.0, false)).foreach { (sx, isFront) =>
      val x = sx * (s.L / 2.0)
      push("body", new BoxGeometry(0.10, 0.36, s.W - 0.02), Util.trs(x + sx * 0.04, s.sill + 0.12, 0.0))
      push("dark", new BoxGeometry(0.05, 0.07, s.W - 0.46), Util.trs(x + sx * 0.08, s.sill + 0.04, 0.0))
      val lz = s.W / 2.0 - 0.20
      Vector(-1.0, 1.0).foreach { t =>
        if isFront then
          push("lampF", new BoxGeometry(0.05, 0.16, 0.32), Util.trs(x + 0.015, lampY, t * lz))
        else
          push("lampR", new BoxGeometry(0.05, 0.21, 0.155), Util.trs(x - 0.015, lampY + 0.05, t * lz))
          push("dark", new BoxGeometry(0.055, 0.028, 0.165), Util.trs(x - 0.02, lampY + 0.05, t * lz))
          push("lampF", new BoxGeometry(0.05, 0.055, 0.075),
            Util.trs(x - 0.015, lampY - 0.10, t * (lz - 0.02)))
      }
      if isFront then
        push("dark", new BoxGeometry(0.04, 0.15, s.W - 0.66), Util.trs(x + 0.02, lampY + 0.01, 0.0))
        push("dark", new BoxGeometry(0.05, 0.13, s.W - 0.50), Util.trs(x + 0.05, s.sill + 0.16, 0.0))
      else
        push("dark", new BoxGeometry(0.03, 0.022, s.W - 0.30), Util.trs(x - 0.025, s.sill + 0.30, 0.0))
        push("brite", new BoxGeometry(0.05, 0.045, 0.34),
          Util.trs(x - 0.03, s.waist - 0.13, s.W * 0.14))
    }
    push("brite", new CylinderGeometry(0.033, 0.033, 0.14, 8),
      Util.trs(-s.L / 2.0 - 0.02, s.sill - 0.05, -(s.W / 2.0 - 0.32), 0.0, 0.0, math.Pi / 2.0))

    val pg = mutable.ArrayBuffer[Util.BakePart]()
    Vector(1.0, -1.0).foreach { sx =>
      pg += Util.BakePart(new PlaneGeometry(0.33, 0.165),
        Some(Util.trs(sx * (s.L / 2.0 + 0.10), s.sill + 0.13, sx * 0.10, 0.0, sx * math.Pi / 2.0, 0.0)))
    }
    val pm = new Mesh(Util.bake(pg.toList), M.plate)
    pm.castShadow = false
    ThreeInterop.addChild(g, pm)

    Vector(-1.0, 1.0).foreach { t =>
      val z = t * (HW + 0.004)
      s.seams.foreach { px =>
        push("dark", new BoxGeometry(0.022, s.waist - s.sill - 0.10, 0.02),
          Util.trs(px, (s.sill + s.waist) / 2.0 + 0.02, z))
      }
      s.handles.foreach { px =>
        push("brite", new BoxGeometry(0.15, 0.045, 0.035),
          Util.trs(px, s.waist - 0.15, z + t * 0.012))
      }
      s.slider.foreach { sl =>
        push("deep", new BoxGeometry(1.10, 0.045, 0.03),
          Util.trs(sl, s.waist - 0.045, z + t * 0.008))
      }
      push("deep", new BoxGeometry(s.L - 0.5, 0.06, 0.03), Util.trs(0.0, s.sill + 0.03, z))
    }

    val mx = s.cab(1) - 0.14
    val my = s.waist + 0.14
    Vector(-1.0, 1.0).foreach { t =>
      val z0 = t * (HW - 0.02)
      val z1 = t * (HW + 0.17)
      push("deep", new BoxGeometry(0.07, 0.05, 0.19), Util.trs(mx, my, (z0 + z1) / 2.0))
      push("deep", new BoxGeometry(0.10, 0.16, 0.07), Util.trs(mx - 0.01, my + 0.03, z1))
      push("brite", new BoxGeometry(0.015, 0.12, 0.05), Util.trs(mx - 0.06, my + 0.03, z1))
    }

    val wy = s.waist + 0.035
    Vector(-1.0, 1.0).foreach { t =>
      push("dark", new BoxGeometry(0.34, 0.022, 0.022),
        Util.trs(s.cab(1) - 0.20, wy, t * 0.28, 0.0, 0.0, 0.12))
    }
    if s.rails then
      Vector(-1.0, 1.0).foreach { t =>
        push("deep", new BoxGeometry(roofFront - roofRear - 0.25, 0.05, 0.05),
          Util.trs((roofFront + roofRear) / 2.0, s.roof + 0.045, t * (CW / 2.0 - 0.16)))
      }

    if s.bus then
      s.doors.foreach { dx =>
        push("dark", new BoxGeometry(0.9, s.waist - s.sill - 0.06, 0.03),
          Util.trs(dx, (s.sill + s.waist) / 2.0 + 0.02, HW + 0.008))
        push("glass", new BoxGeometry(0.82, 0.52, 0.04),
          Util.trs(dx, s.waist - 0.26, HW + 0.014))
      }
      push("dark", new BoxGeometry(0.10, 0.24, 1.10), Util.trs(s.cab(1) - 0.18, s.roof - 0.22, 0.0))
      push("lampF", new BoxGeometry(0.03, 0.16, 0.96), Util.trs(s.cab(1) - 0.12, s.roof - 0.22, 0.0))
      push("deep", new BoxGeometry(s.L - 0.4, 0.20, s.W + 0.01), Util.trs(0.0, s.sill + 0.02, 0.0))

    s.box.foreach { (bx0, bx1, by0, by1) =>
      push("body", new BoxGeometry(bx1 - bx0, by1 - by0, s.W + 0.06),
        Util.trs((bx0 + bx1) / 2.0, (by0 + by1) / 2.0, 0.0))
      (0 until 7).foreach { i =>
        val x = bx0 + 0.2 + i * ((bx1 - bx0 - 0.4) / 6.0)
        Vector(-1.0, 1.0).foreach { t =>
          push("deep", new BoxGeometry(0.05, by1 - by0 - 0.14, 0.03),
            Util.trs(x, (by0 + by1) / 2.0, t * (s.W / 2.0 + 0.045)))
        }
      }
      push("deep", new BoxGeometry(0.06, by1 - by0 - 0.12, s.W - 0.04),
        Util.trs(bx0 - 0.03, (by0 + by1) / 2.0, 0.0))
      push("dark", new BoxGeometry(0.04, 0.10, s.W - 0.22), Util.trs(bx0 - 0.07, by0 + 0.14, 0.0))
      push("deep", new BoxGeometry(bx1 - bx0 + 0.06, 0.07, s.W + 0.12),
        Util.trs((bx0 + bx1) / 2.0, by1 + 0.02, 0.0))
    }

    emit(g, parts,
      Map(
        "body"  -> mBody, "deep"    -> mDeep, "dark"    -> M.dark, "brite" -> M.brite,
        "glass" -> M.glass, "lampF" -> M.lampF, "lampR" -> M.lampR
      ),
      noCast = Vector("glass", "lampF", "lampR"))

    if o.hero then
      val bodyMesh = (0 until g.children.length)
        .map(i => g.children(i))
        .collectFirst { case mesh: Mesh[?, ?, ?] if mesh.material == mBody => mesh }
      bodyMesh.foreach(Outline.hullOutline(_, OutlineOptions(thickness = 0.0034)))

    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry + o.skew.getOrElse(0.0)
    val size = vehicleSize(o.kind)
    g.userData("vehicle") = js.Dictionary(
      "kind" -> o.kind.toString.asInstanceOf[js.Any],
      "L"    -> size.L.asInstanceOf[js.Any],
      "W"    -> size.W.asInstanceOf[js.Any],
      "H"    -> size.H.asInstanceOf[js.Any]
    )
    g
  end makeVehicle

  def parkVehicle(ctx: PropCtx, o: VehicleOpts): Group[Object3DEventMap] =
    val g = makeVehicle(o)
    ctx.add(g)
    val size = vehicleSize(o.kind)
    val ry   = o.ry + o.skew.getOrElse(0.0)
    val c    = math.abs(math.cos(ry))
    val sv   = math.abs(math.sin(ry))
    val hw   = (c * size.L + sv * size.W) / 2.0
    val hd   = (sv * size.L + c * size.W) / 2.0
    ctx.collide(o.x - hw + 0.06, o.z - hd + 0.06, o.x + hw - 0.06, o.z + hd - 0.06,
      o.y + size.H - 0.15)
    g

  final case class BayPaintOpts(
    x: Double = 0.0,
    z: Double = 0.0,
    y: Double = 0.0,
    w: Double = 2.4,
    d: Double = 5.0,
    ry: Double = 0.0,
    t: Double = 0.10,
    head: Boolean = true
  )

  def bayPaint(ctx: PropCtx, o: BayPaintOpts): Mesh[?, ?, ?] =
    val mat   = Toon.flat(FlatOptions(color = Pal.lineWhite))
    val parts = mutable.ArrayBuffer[Util.BakePart]()
    Vector(-1.0, 1.0).foreach { sx =>
      parts += Util.BakePart(new BoxGeometry(o.t, 0.02, o.d),
        Some(Util.trs(sx * (o.w / 2.0), 0.0, 0.0)))
    }
    if o.head then
      parts += Util.BakePart(new BoxGeometry(o.w + o.t, 0.02, o.t),
        Some(Util.trs(0.0, 0.0, -o.d / 2.0)))
    val mesh = new Mesh(Util.bake(parts.toList), mat)
    mesh.position.set(o.x, o.y + 0.022, o.z)
    mesh.rotation.y = o.ry
    ThreeInterop.setUserDataFlag(mesh, "noOutline")
    ctx.add(mesh)
    mesh

  final case class KerbLineOpts(
    axis: Buildings.Axis = Buildings.Axis.X,
    at: Double = 0.0,
    from: Double = 0.0,
    to: Double = 0.0,
    y: Double = 0.0
  )

  def kerbLine(ctx: PropCtx, o: KerbLineOpts): Mesh[?, ?, ?] =
    val len = math.abs(o.to - o.from)
    val mesh = Util.box(
      if o.axis == Buildings.Axis.X then len else 0.11, 0.02,
      if o.axis == Buildings.Axis.X then 0.11 else len,
      Toon.flat(FlatOptions(color = Pal.lineYellow)),
      if o.axis == Buildings.Axis.X then (o.from + o.to) / 2.0 else o.at,
      o.y + 0.022,
      if o.axis == Buildings.Axis.X then o.at else (o.from + o.to) / 2.0
    )
    ThreeInterop.setUserDataFlag(mesh, "noOutline")
    ctx.add(mesh)
    mesh

  final case class DelineatorOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    h: Double = 0.80,
    lean: Double = 0.0,
    ry: Double = 0.0
  )

  def makeDelineator(o: DelineatorOpts): Group[Object3DEventMap] =
    val g = new Group()
    val parts = mutable.LinkedHashMap(
      "pale" -> mutable.ArrayBuffer[Util.BakePart](),
      "band" -> mutable.ArrayBuffer[Util.BakePart](),
      "base" -> mutable.ArrayBuffer[Util.BakePart]()
    )
    def push(key: String, geo: BufferGeometry[?, ?], mx: Matrix4): Unit =
      parts(key) += Util.BakePart(geo, Some(mx))
    push("pale", new CylinderGeometry(0.032, 0.036, o.h, 8), Util.trs(0.0, o.h / 2.0, 0.0))
    push("base", new CylinderGeometry(0.075, 0.095, 0.06, 10), Util.trs(0.0, 0.03, 0.0))
    Vector(o.h - 0.10, o.h - 0.26).foreach { y =>
      push("band", new CylinderGeometry(0.039, 0.039, 0.06, 8), Util.trs(0.0, y, 0.0))
    }
    emit(g, parts,
      Map(
        "pale" -> Toon.cel(CelOptions(color = 0xf0eee8, bands = 2, tint = 0x7d74a0)),
        "band" -> Toon.cel(CelOptions(color = Pal.orange, bands = 2, tint = 0x8f6050)),
        "base" -> M.briteDark
      ))
    g.position.set(o.x, o.y, o.z)
    g.rotation.z = o.lean
    g.rotation.y = o.ry
    g
  end makeDelineator

  final case class BusStopOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    h: Double = 2.45,
    ry: Double = 0.0,
    variant: Int = 0
  )

  def makeBusStop(o: BusStopOpts): Group[Object3DEventMap] =
    val g    = new Group()
    val post = new Mesh(new CylinderGeometry(0.045, 0.055, o.h, 8), M.briteDark)
    post.position.y = o.h / 2.0
    post.castShadow = true
    ThreeInterop.addChild(g, post)
    ThreeInterop.addChild(g, Util.box(0.24, 0.09, 0.24, M.concreteMid, 0.0, 0.045, 0.0))

    val side     = Toon.cel(CelOptions(color = 0xe6e3e8, bands = 3, tint = 0x6f6790))
    val plateMap = Textures.busStopPlate(o.variant)
    val head = new Mesh(new BoxGeometry(0.52, 0.52, 0.06),
      js.Array(side, side, side, side,
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(plateMap), cache = false)),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.mirrored(plateMap)), cache = false))))
    head.position.set(0.0, o.h - 0.30, 0.0)
    head.castShadow = true
    ThreeInterop.addChild(g, head)

    val caseSide = Toon.cel(CelOptions(color = Pal.metal, bands = 3, tint = 0x666090))
    val tt = new Mesh(new BoxGeometry(0.34, 0.51, 0.05),
      js.Array(caseSide, caseSide, caseSide, caseSide,
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.busTimetable()), cache = false)),
        caseSide))
    tt.position.set(0.0, o.h - 0.98, -0.03)
    tt.rotation.y = math.Pi
    tt.castShadow = true
    ThreeInterop.addChild(g, tt)

    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeBusStop

  final case class TyreSpot(
    x: Double = 0.0,
    z: Double = 0.0,
    y: Double = 0.0,
    ry: Double = 0.0,
    w: Double = 0.22,
    d: Double = 2.4
  )

  def tyreMarks(ctx: PropCtx, spots: Vector[TyreSpot], opacity: Double = 0.12): Mesh[?, ?, ?] =
    if spots.isEmpty then throw new IllegalArgumentException("sakura-crossing: tyre marks need spots")
    val geo = new PlaneGeometry(1.0, 1.0)
    geo.rotateX(-math.Pi / 2.0)
    val mat = Toon.flat(FlatOptions(
      color = 0x6e6880, transparent = true, opacity = opacity, depthWrite = Some(false),
      cache = false
    ))
    val inst  = new InstancedMesh(geo, mat, spots.length.toDouble)
    val dummy = new Object3D()
    spots.zipWithIndex.foreach { (sp, i) =>
      dummy.position.set(sp.x, sp.y + 0.025, sp.z)
      dummy.rotation.set(0.0, sp.ry, 0.0)
      dummy.scale.set(sp.w, 1.0, sp.d)
      dummy.updateMatrix()
      inst.setMatrixAt(i, dummy.matrix)
    }
    ThreeInterop.setUserDataFlag(inst, "noOutline")
    inst.renderOrder = 1.0
    ctx.add(inst)
    inst
  end tyreMarks
end Vehicles
