package sakura.world

import sakura.ThreeInterop
import sakura.core.{Outline, Pal, Textures, Toon, Util}
import sakura.core.Outline.OutlineOptions
import sakura.core.Toon.{CelOptions, FlatOptions}
import vendor.three.*

import scala.collection.mutable
import scala.math
import scala.scalajs.js
import scala.scalajs.js.JSConverters.*

/** The minimal scene-injection seam shared by the prop builders: `#1393`'s WorldCtx adapts to
  * it, so the two lanes merge with zero conflicts.
  */
final case class PropCtx(
  add: Object3D[?] => Unit,
  collide: (Double, Double, Double, Double, Double) => Unit,
  groundAt: (Double, Double) => Double
)

/** 1:1 port of upstream `src/world/props.js`: street furniture -- utility poles and their
  * cables, the kei truck, bicycles, mirrors, post boxes, cones, guardrails, planters, and the
  * back-street clutter that makes a lane read as lived in.
  */
object Props:

  private object M:
    lazy val pole        = Toon.cel(CelOptions(color = 0xd6d2d8, bands = 3, tint = 0x6a6288))
    lazy val metal       = Toon.cel(CelOptions(color = Pal.metal, bands = 3, tint = 0x666090))
    lazy val metalDark   = Toon.cel(CelOptions(color = Pal.metalDark, bands = 3, tint = 0x5c5680))
    lazy val dark        = Toon.cel(CelOptions(color = Pal.black, bands = 2, tint = 0x4b4560))
    lazy val wire        = Toon.cel(CelOptions(color = 0x4c4658, bands = 2, tint = 0x413c58))
    lazy val red         = Toon.cel(CelOptions(color = Pal.red, bands = 3, tint = 0x7a4060))
    lazy val white       = Toon.cel(CelOptions(color = Pal.wallWhite, bands = 3, tint = 0x6f6790))
    lazy val concrete    = Toon.cel(CelOptions(color = Pal.concrete, bands = 3, tint = 0x6f6790))
    lazy val concreteMid = Toon.cel(CelOptions(color = Pal.concreteMid, bands = 3, tint = 0x6a6288))
    lazy val terracotta  = Toon.cel(CelOptions(color = 0xc57a5a, bands = 3, tint = 0x6f5680))
    lazy val leaf        = Toon.cel(CelOptions(color = Pal.leaf, bands = 3, tint = 0x5b6f8c))
    lazy val leafDeep    = Toon.cel(CelOptions(color = Pal.leafDeep, bands = 3, tint = 0x5b6f8c))
    lazy val wood        = Toon.cel(CelOptions(color = 0x9c7f5e, bands = 3, tint = 0x5c5680))
    lazy val pale        = Toon.flat(FlatOptions(color = 0xf6f2e8))

  /** BoxGeometry's multi-material parameter wants the default event map; unify the mixed
    * cel/flat material lists every builder builds.
    */
  private[sakura] def matArray(ms: Material[?]*): js.Array[Material[MaterialEventMap]] =
    js.Array(ms.map(_.asInstanceOf[Material[MaterialEventMap]])*)

  final case class PoleOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    h: Double = 9.2,
    seed: Int = 5,
    lamp: Boolean = false,
    armDir: Double = 1.0,
    armYs: Option[Vector[Double]] = None,
    transformer: Boolean = true,
    plateFace: Option[Double] = None
  )

  def makePole(o: PoleOpts): Group[Object3DEventMap] =
    val rng = Util.rngKit(o.seed)
    val g   = new Group()
    val H   = o.h
    val parts = mutable.LinkedHashMap(
      "pole"  -> mutable.ArrayBuffer[Util.BakePart](),
      "metal" -> mutable.ArrayBuffer[Util.BakePart](),
      "dark"  -> mutable.ArrayBuffer[Util.BakePart](),
      "white" -> mutable.ArrayBuffer[Util.BakePart]()
    )
    def push(key: String, geo: BufferGeometry[?, ?], mx: Matrix4): Unit =
      parts(key) += Util.BakePart(geo, Some(mx))

    push("pole", new CylinderGeometry(0.11, 0.19, H, 8), Util.trs(0.0, H / 2.0, 0.0))
    push("pole", new CylinderGeometry(0.24, 0.28, 0.22, 8), Util.trs(0.0, 0.11, 0.0))

    val armYs = o.armYs.getOrElse(Vector(H - 0.55, H - 1.5))
    armYs.zipWithIndex.foreach { (y, ai) =>
      val len = if ai == 0 then 2.1 else 1.7
      push("dark", new BoxGeometry(0.09, 0.1, len), Util.trs(0.0, y, 0.0))
      push("metal", new BoxGeometry(0.06, 0.5, 0.06), Util.trs(0.0, y - 0.3, 0.0))
      (-1 to 1).foreach { i =>
        if !(i == 0 && ai == 1) then
          push("white", new CylinderGeometry(0.06, 0.075, 0.16, 7),
            Util.trs(0.0, y + 0.13, (i * len) / 2.4))
          push("metal", new CylinderGeometry(0.02, 0.02, 0.14, 5),
            Util.trs(0.0, y + 0.04, (i * len) / 2.4))
      }
    }

    if o.transformer then
      val ty = H - 2.9
      push("metal", new BoxGeometry(0.5, 0.14, 1.5),
        Util.trs(o.armDir * 0.34, ty + 0.62, 0.0))
      Vector(-0.42, 0.42).foreach { dz =>
        push("metal", new CylinderGeometry(0.24, 0.24, 0.72, 10),
          Util.trs(o.armDir * 0.34, ty + 0.24, dz))
        push("metal", new CylinderGeometry(0.26, 0.26, 0.06, 10),
          Util.trs(o.armDir * 0.34, ty + 0.62, dz))
      }
      push("dark", new BoxGeometry(0.28, 0.5, 0.28), Util.trs(-o.armDir * 0.24, ty + 1.1, 0.0))

    push("dark", new CylinderGeometry(0.045, 0.045, H - 1.4, 5),
      Util.trs(o.armDir * 0.135, (H - 1.4) / 2.0, 0.06))

    val plate = new Mesh(
      new CylinderGeometry(0.205, 0.21, 0.62, 12, 1, true, -1.0, 2.0),
      Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.warningPlate(rng.int(0, 2))),
        side = 2, cache = false))
    )
    plate.position.set(0.0, 2.45, 0.0)
    plate.rotation.y = o.plateFace.getOrElse(
      if o.armDir > 0 then math.Pi / 2.0 else -math.Pi / 2.0
    )
    plate.castShadow = true
    ThreeInterop.addChild(g, plate)

    if o.lamp then
      push("metal", new CylinderGeometry(0.05, 0.05, 1.3, 6),
        Util.trs(o.armDir * 0.65, H - 3.9, 0.0, 0.0, 0.0, math.Pi / 2.0))
      val shade = new Mesh(new ConeGeometry(0.32, 0.26, 12, 1, true), M.metal)
      shade.position.set(o.armDir * 1.28, H - 4.02, 0.0)
      ThreeInterop.addChild(g, shade)
      ThreeInterop.addChild(g,
        Util.box(0.26, 0.05, 0.26,
          Toon.flat(FlatOptions(color = 0xfff2d0)), o.armDir * 1.28, H - 4.16, 0.0))

    val matFor: Map[String, Material[?]] = Map(
      "pole" -> M.pole, "metal" -> M.metal, "dark" -> M.dark, "white" -> M.white
    )
    parts.foreach { (key, bucket) =>
      if bucket.nonEmpty then
        val mesh = new Mesh(Util.bake(bucket.toList), matFor(key))
        mesh.castShadow = true
        mesh.receiveShadow = true
        ThreeInterop.addChild(g, mesh)
        if key == "pole" then Outline.hullOutline(mesh, OutlineOptions(thickness = 0.0034))
    }

    g.position.set(o.x, o.y, o.z)
    g.userData("top") = (o.y + H).asInstanceOf[js.Any]
    g
  end makePole

  final case class WireRun(
    points: Vector[Vector3],
    sag: Double = 0.5,
    r: Double = 0.026
  )

  def makeWires(ctx: PropCtx, runs: Vector[WireRun]): Mesh[?, ?, ?] =
    val geos = mutable.ArrayBuffer[BufferGeometry[?, ?]]()
    runs.foreach { run =>
      (0 until run.points.length - 1).foreach { i =>
        val a     = run.points(i)
        val b     = run.points(i + 1)
        val dist  = a.distanceTo(ThreeInterop.asVector3Like(b))
        val curve = Util.sagCurve(a, b, run.sag * math.min(1.6, dist / 14.0), 12)
        geos += new TubeGeometry(curve, 14, run.r, 4, false)
      }
    }
    if geos.isEmpty then throw new IllegalArgumentException("sakura-crossing: wires need runs")
    val merged = if geos.length == 1 then geos.head
    else Util.bake(geos.map(g => Util.BakePart(g)).toList)
    val mesh = new Mesh(merged, M.wire)
    ctx.add(mesh)
    mesh

  final case class KeiTruckOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    color: Option[Int] = None,
    load: Option[String] = None,
    hero: Boolean = true
  )

  def makeKeiTruck(o: KeiTruckOpts): Group[Object3DEventMap] =
    val g    = new Group()
    val base = o.color.getOrElse(Pal.taxiYellow)
    val body = Toon.cel(CelOptions(color = base, bands = 3, tint = 0x8f7050))
    val bodyDeep =
      if o.color.isEmpty then Toon.cel(CelOptions(color = Pal.taxiYellowDeep, bands = 3, tint = 0x8f7050))
      else
        val deep = new Color(base.toDouble).multiplyScalar(0.78).getHex().toInt
        Toon.cel(CelOptions(color = deep, bands = 3, tint = 0x8f7050))
    val glass = Toon.flat(FlatOptions(color = Pal.glassDark))
    val L     = 3.32
    val W     = 1.46
    val hero  = o.hero

    val chassis = Util.box(L, 0.34, W, bodyDeep, 0.0, 0.68, 0.0)
    chassis.castShadow = true
    chassis.receiveShadow = true
    ThreeInterop.addChild(g, chassis)
    if hero then Outline.hullOutline(chassis, OutlineOptions(thickness = 0.0036))

    val cab = Util.box(1.26, 1.06, W, body, L / 2.0 - 0.68, 1.38, 0.0)
    cab.castShadow = true
    cab.receiveShadow = true
    ThreeInterop.addChild(g, cab)
    if hero then Outline.hullOutline(cab, OutlineOptions(thickness = 0.0036))
    ThreeInterop.addChild(g, Util.box(0.06, 0.62, W - 0.16, glass, L / 2.0 - 0.06, 1.62, 0.0))
    Vector(-1.0, 1.0).foreach { s =>
      ThreeInterop.addChild(g,
        Util.box(1.0, 0.56, 0.06, glass, L / 2.0 - 0.7, 1.6, s * (W / 2.0 - 0.02)))
    }
    ThreeInterop.addChild(g, Util.box(1.3, 0.1, W + 0.06, bodyDeep, L / 2.0 - 0.68, 1.9, 0.0))
    ThreeInterop.addChild(g, Util.box(0.08, 0.62, 0.07, bodyDeep, L / 2.0 - 0.08, 1.62, 0.0))

    ThreeInterop.addChild(g,
      Util.box(1.86, 0.06, W,
        Toon.cel(CelOptions(color = 0xbba98c, bands = 3, tint = 0x6f6790)), -0.62, 0.88, 0.0))
    Vector(-1.0, 1.0).foreach { s =>
      ThreeInterop.addChild(g, Util.box(1.86, 0.42, 0.07, body, -0.62, 1.06, s * (W / 2.0 - 0.03)))
    }
    ThreeInterop.addChild(g, Util.box(0.07, 0.42, W, body, -1.55, 1.06, 0.0))

    o.load.getOrElse("crates") match
      case "crates" =>
        (0 until 3).foreach { i =>
          val c = Util.box(0.42, 0.26, 0.34,
            Toon.cel(CelOptions(color = if i == 1 then Pal.crate else Pal.crateAlt, bands = 3,
              tint = 0x4a4a92)),
            -0.35 - i * 0.15, 1.04 + i * 0.26, if i % 2 == 1 then 0.08 else -0.08)
          c.castShadow = true
          ThreeInterop.addChild(g, c)
        }
      case "sheet" =>
        val sheet = Toon.cel(CelOptions(color = 0x8fa2b4, bands = 3, tint = 0x5c5680))
        val s1    = Util.box(1.5, 0.4, W - 0.18, sheet, -0.62, 1.11, 0.0)
        s1.castShadow = true
        s1.receiveShadow = true
        ThreeInterop.addChild(g, s1)
        ThreeInterop.addChild(g,
          Util.box(1.62, 0.07, W - 0.06,
            Toon.cel(CelOptions(color = 0x7e8fa0, bands = 3, tint = 0x5c5680)), -0.62, 0.94, 0.0))
        Vector(-1.12, -0.62, -0.12).foreach { dx =>
          ThreeInterop.addChild(g,
            Util.box(0.05, 0.44, W - 0.14,
              Toon.cel(CelOptions(color = Pal.rope, bands = 3, tint = 0x6f6790)), dx, 1.11, 0.0))
        }
      case _ => ()
    end match

    val wheelGeo = new CylinderGeometry(0.29, 0.29, 0.2, 12)
    wheelGeo.rotateX(math.Pi / 2.0)
    val hubGeo = new CylinderGeometry(0.13, 0.13, 0.22, 10)
    hubGeo.rotateX(math.Pi / 2.0)
    Vector(L / 2.0 - 0.72, -L / 2.0 + 0.6).foreach { wx =>
      Vector(-1.0, 1.0).foreach { s =>
        val w = new Mesh(wheelGeo, M.dark)
        w.position.set(wx, 0.29, s * (W / 2.0 - 0.06))
        w.castShadow = true
        ThreeInterop.addChild(g, w)
        val h = new Mesh(hubGeo,
          Toon.cel(CelOptions(color = 0xe8e6ea, bands = 2, tint = 0x6f6790)))
        h.position.set(wx, 0.29, s * (W / 2.0 - 0.02))
        ThreeInterop.addChild(g, h)
        ThreeInterop.addChild(g,
          Util.box(0.72, 0.1, 0.1, bodyDeep, wx, 0.62, s * (W / 2.0 + 0.01)))
      }
    }

    Vector(-1.0, 1.0).foreach { s =>
      ThreeInterop.addChild(g,
        Util.box(0.06, 0.16, 0.26,
          Toon.flat(FlatOptions(color = 0xfff4d8)), L / 2.0 + 0.01, 1.0, s * 0.48))
      ThreeInterop.addChild(g,
        Util.box(0.06, 0.13, 0.2,
          Toon.flat(FlatOptions(color = 0xf06050)), -L / 2.0 - 0.01, 1.0, s * 0.48))
      ThreeInterop.addChild(g, Util.box(0.05, 0.16, 0.11, M.dark, L / 2.0 - 0.5, 1.92, s * (W / 2.0 + 0.16)))
      ThreeInterop.addChild(g,
        Util.box(0.04, 0.04, 0.2, M.metalDark, L / 2.0 - 0.5, 1.86, s * (W / 2.0 + 0.08)))
    }
    ThreeInterop.addChild(g, Util.box(0.1, 0.16, W - 0.1, M.metal, L / 2.0 + 0.03, 0.78, 0.0))
    ThreeInterop.addChild(g, Util.box(0.1, 0.16, W - 0.1, M.metal, -L / 2.0 - 0.03, 0.78, 0.0))
    val plate = new Mesh(new PlaneGeometry(0.4, 0.2),
      Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.platePlate()), cache = false)))
    plate.position.set(-L / 2.0 - 0.05, 0.95, 0.0)
    plate.rotation.y = -math.Pi / 2.0
    ThreeInterop.addChild(g, plate)

    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeKeiTruck

  private val BikeRearLift = 0.018
  final private case class BikeFrame(
    R: Double,
    A: Vector3,
    B: Vector3,
    BB: Vector3,
    SC: Vector3,
    HB: Vector3,
    HT: Vector3,
    BAR: Vector3,
    SAD: Vector3
  )
  private val Bike = BikeFrame(
    R = 0.33,
    A = new Vector3(-0.52, 0.33 + BikeRearLift, 0.0),
    B = new Vector3(0.55, 0.33, 0.0),
    BB = new Vector3(-0.10, 0.28, 0.0),
    SC = new Vector3(-0.27, 0.86, 0.0),
    HB = new Vector3(0.44, 0.60, 0.0),
    HT = new Vector3(0.49, 0.86, 0.0),
    BAR = new Vector3(0.46, 0.97, 0.0),
    SAD = new Vector3(-0.31, 1.00, 0.0)
  )

  private val bikeUp = new Vector3(0.0, 1.0, 0.0)

  private def tube(arr: mutable.ArrayBuffer[Util.BakePart], a: Vector3, b: Vector3, r: Double): Unit =
    val dir = new Vector3().subVectors(ThreeInterop.asVector3Like(a), ThreeInterop.asVector3Like(b))
    val len = dir.length()
    if len >= 1e-4 then
      arr += Util.BakePart(unitCylGeo,
        Some(new Matrix4().compose(
          new Vector3().addVectors(ThreeInterop.asVector3Like(a), ThreeInterop.asVector3Like(b))
            .multiplyScalar(0.5),
          new Quaternion().setFromUnitVectors(ThreeInterop.asVector3Like(bikeUp).asInstanceOf[Vector3],
            ThreeInterop.asVector3Like(dir.normalize())),
          new Vector3(r, len, r)
        )))

  private val unitCylGeo = new CylinderGeometry(1.0, 1.0, 1.0, 6, 1)

  private val bikeGeometryCache: mutable.Map[String, BufferGeometry[?, ?]] =
    mutable.HashMap.empty

  private def bicycleGeometry: Map[String, BufferGeometry[?, ?]] =
    if bikeGeometryCache.nonEmpty then bikeGeometryCache.toMap
    else
      val R     = Bike.R
      val dark  = mutable.ArrayBuffer[Util.BakePart]()
      val frame = mutable.ArrayBuffer[Util.BakePart]()
      val brite = mutable.ArrayBuffer[Util.BakePart]()
      val meshP = mutable.ArrayBuffer[Util.BakePart]()
      def at(
        arr: mutable.ArrayBuffer[Util.BakePart],
        geo: BufferGeometry[?, ?],
        p: Vector3,
        rx: Double = 0.0,
        ry: Double = 0.0,
        rz: Double = 0.0
      ): Unit =
        arr += Util.BakePart(geo, Some(Util.trs(p.x, p.y, p.z, rx, ry, rz)))

      val rim   = new TorusGeometry(R, 0.021, 5.0, 18.0)
      val guard = new TorusGeometry(R + 0.055, 0.018, 4.0, 10.0, math.Pi * 0.78)
      Vector(Bike.A, Bike.B).foreach { hub =>
        at(dark, rim, hub)
        at(dark, new CylinderGeometry(0.035, 0.035, 0.085, 6), hub, math.Pi / 2.0)
        at(frame, guard, hub, 0.0, 0.0, math.Pi * 0.11)
      }

      tube(frame, Bike.BB, Bike.SC, 0.026)
      tube(frame, Bike.BB, Bike.HB, 0.028)
      tube(frame, Bike.HB, Bike.HT, 0.030)
      tube(frame, Bike.SC, Bike.HT, 0.024)
      Vector(-1.0, 1.0).foreach { s =>
        val rear = new Vector3(Bike.A.x, Bike.A.y, s * 0.052)
        tube(frame, Bike.BB, rear, 0.019)
        tube(frame, Bike.SC, rear, 0.017)
        tube(brite, new Vector3(Bike.HB.x, Bike.HB.y, s * 0.045),
          new Vector3(Bike.B.x, Bike.B.y, s * 0.05), 0.017)
      }

      tube(brite, Bike.SC, Bike.SAD, 0.019)
      at(dark, new BoxGeometry(0.23, 0.055, 0.12),
        new Vector3(Bike.SAD.x - 0.02, Bike.SAD.y + 0.04, 0.0))
      tube(brite, Bike.HT, Bike.BAR, 0.021)
      at(brite, new CylinderGeometry(0.019, 0.019, 0.54, 6), Bike.BAR, math.Pi / 2.0)
      Vector(-1.0, 1.0).foreach { s =>
        at(dark, new CylinderGeometry(0.023, 0.023, 0.1, 6),
          new Vector3(Bike.BAR.x, Bike.BAR.y, s * 0.22), math.Pi / 2.0)
      }
      at(dark, new CylinderGeometry(0.028, 0.028, 0.03, 8),
        new Vector3(Bike.BAR.x - 0.05, Bike.BAR.y + 0.03, -0.12))

      at(dark, new CylinderGeometry(0.075, 0.075, 0.012, 12), Bike.BB, math.Pi / 2.0)
      Vector(-1.0, 1.0).foreach { s =>
        val crank = new Vector3(Bike.BB.x + s * 0.02, Bike.BB.y - s * 0.15, s * 0.075)
        tube(brite, new Vector3(Bike.BB.x, Bike.BB.y, s * 0.075), crank, 0.013)
        at(dark, new BoxGeometry(0.09, 0.02, 0.05),
          new Vector3(crank.x, crank.y, s * 0.105))
      }
      tube(brite, new Vector3(-0.16, 0.26, 0.06), new Vector3(-0.30, 0.015, 0.13), 0.014)

      val rackY = R + 0.43
      at(brite, new BoxGeometry(0.29, 0.022, 0.15),
        new Vector3(Bike.A.x + 0.02, rackY, 0.0))
      Vector(-1.0, 1.0).foreach { s =>
        tube(brite, new Vector3(Bike.A.x + 0.13, rackY, s * 0.068),
          new Vector3(Bike.SC.x + 0.03, Bike.SC.y - 0.09, s * 0.03), 0.011)
        tube(brite, new Vector3(Bike.A.x - 0.11, rackY, s * 0.068),
          new Vector3(Bike.A.x + 0.02, Bike.A.y + 0.04, s * 0.055), 0.011)
      }

      val BX = 0.69
      val BY = 0.87
      at(meshP, new CylinderGeometry(0.16, 0.12, 0.2, 8, 1, true), new Vector3(BX, BY, 0.0))
      at(brite, new CylinderGeometry(0.12, 0.12, 0.016, 8), new Vector3(BX, BY - 0.095, 0.0))
      at(brite, new TorusGeometry(0.16, 0.012, 4.0, 10.0), new Vector3(BX, BY + 0.1, 0.0), math.Pi / 2.0)
      tube(brite, new Vector3(BX - 0.12, BY + 0.08, 0.0),
        new Vector3(Bike.BAR.x + 0.02, Bike.BAR.y - 0.03, 0.0), 0.012)
      tube(brite, new Vector3(BX - 0.05, BY - 0.1, 0.0),
        new Vector3(Bike.HB.x + 0.02, Bike.HB.y + 0.02, 0.0), 0.012)
      at(dark, new BoxGeometry(0.06, 0.05, 0.07), new Vector3(0.52, 0.54, 0.0))

      bikeGeometryCache("dark") = Util.bake(dark.toList)
      bikeGeometryCache("frame") = Util.bake(frame.toList)
      bikeGeometryCache("brite") = Util.bake(brite.toList)
      bikeGeometryCache("mesh") = Util.bake(meshP.toList)
      bikeGeometryCache.toMap

  final case class BicycleOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    color: Int = 0x3f6f9c,
    lean: Double = 0.0
  )

  def makeBicycle(o: BicycleOpts): Group[Object3DEventMap] =
    val geo   = bicycleGeometry
    val g     = new Group()
    val inner = new Group()
    ThreeInterop.addChild(g, inner)

    val frameMat  = Toon.cel(CelOptions(color = o.color, bands = 3, tint = 0x4a4a92))
    val basketMat = Toon.cel(CelOptions(color = Pal.metal, bands = 3, side = 2, tint = 0x666090))
    Vector("dark" -> M.dark, "frame" -> frameMat, "brite" -> M.metal, "mesh" -> basketMat)
      .foreach { (key, mat) =>
        val mesh = new Mesh(geo(key), mat)
        mesh.castShadow = true
        ThreeInterop.addChild(inner, mesh)
      }
    Vector(Bike.A, Bike.B).foreach { hub =>
      val sp = new Mesh(new CircleGeometry(Bike.R - 0.035, 12),
        Toon.flat(FlatOptions(color = 0xc9c6d0, transparent = true, opacity = 0.32,
          depthWrite = Some(false), cache = false)))
      sp.position.set(hub.x, hub.y, 0.002)
      ThreeInterop.setUserDataFlag(sp, "noOutline")
      ThreeInterop.addChild(inner, sp)
    }

    inner.rotation.x = o.lean
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeBicycle

  final case class MirrorOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    h: Double = 2.55,
    r: Double = 0.46
  )

  def makeMirror(o: MirrorOpts): Group[Object3DEventMap] =
    val g    = new Group()
    val H    = o.h
    val post = Util.cyl(0.055, 0.07, H, 8, M.metalDark, 0.0, H / 2.0, 0.0)
    post.castShadow = true
    ThreeInterop.addChild(g, post)
    ThreeInterop.addChild(g, Util.cyl(0.14, 0.16, 0.16, 8, M.concreteMid, 0.0, 0.08, 0.0))
    Outline.hullOutline(post, OutlineOptions(thickness = 0.0032))

    val R       = o.r
    val backMat = Toon.cel(CelOptions(color = Pal.mirrorBack, bands = 3, tint = 0x8f7050))
    val rim     = new Mesh(new CylinderGeometry(R, R, 0.1, 20), backMat)
    rim.rotation.x = math.Pi / 2.0
    rim.position.set(0.0, H - 0.1, 0.06)
    rim.castShadow = true
    ThreeInterop.addChild(g, rim)
    Outline.hullOutline(rim, OutlineOptions(thickness = 0.0034))
    val face = new Mesh(new SphereGeometry(R * 2.1, 20, 10, 0.0, math.Pi * 2.0, 0.0, 0.24),
      Toon.cel(CelOptions(color = Pal.mirrorFace, bands = 2, tint = 0x7d8fb0)))
    face.rotation.x = -math.Pi / 2.0
    face.position.set(0.0, H - 0.1, 0.11)
    ThreeInterop.addChild(g, face)
    val glint = new Mesh(new CircleGeometry(R * 0.3, 12),
      Toon.flat(FlatOptions(color = 0xffffff, transparent = true, opacity = 0.4,
        depthWrite = Some(false), cache = false)))
    glint.position.set(-R * 0.3, H + 0.05, 0.2)
    ThreeInterop.setUserDataFlag(glint, "noOutline")
    ThreeInterop.addChild(g, glint)
    val hood = new Mesh(new CylinderGeometry(R + 0.04, R + 0.04, 0.2, 20, 1, true, 0.6, 1.95),
      backMat)
    hood.rotation.x = math.Pi / 2.0
    hood.position.set(0.0, H - 0.1, 0.12)
    ThreeInterop.addChild(g, hood)

    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeMirror

  final case class PostBoxOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0
  )

  def makePostBox(o: PostBoxOpts): Group[Object3DEventMap] =
    val g   = new Group()
    val red = Toon.cel(CelOptions(color = Pal.redDeep, bands = 3, tint = 0x7a4060))
    ThreeInterop.addChild(g, Util.cyl(0.28, 0.3, 0.16, 12, M.concreteMid, 0.0, 0.08, 0.0))
    val body = new Mesh(new CylinderGeometry(0.24, 0.24, 1.15, 14), red)
    body.position.set(0.0, 0.74, 0.0)
    body.castShadow = true
    body.receiveShadow = true
    ThreeInterop.addChild(g, body)
    Outline.hullOutline(body, OutlineOptions(thickness = 0.0036))
    val cap = new Mesh(new SphereGeometry(0.24, 14, 8, 0.0, math.Pi * 2.0, 0.0, math.Pi / 2.0), red)
    cap.position.set(0.0, 1.31, 0.0)
    cap.castShadow = true
    ThreeInterop.addChild(g, cap)
    ThreeInterop.addChild(g,
      Util.cyl(0.26, 0.26, 0.05, 14,
        Toon.cel(CelOptions(color = Pal.red, bands = 3, tint = 0x7a4060)), 0.0, 1.3, 0.0))
    ThreeInterop.addChild(g, Util.box(0.3, 0.07, 0.06, M.dark, 0.0, 1.14, 0.21))
    ThreeInterop.addChild(g,
      Util.box(0.26, 0.34, 0.04,
        Toon.cel(CelOptions(color = 0xb02c28, bands = 3, tint = 0x7a4060)), 0.0, 0.62, 0.22))
    ThreeInterop.addChild(g,
      Util.box(0.16, 0.1, 0.03,
        Toon.flat(FlatOptions(color = 0xf6f2e8)), 0.0, 0.42, 0.235))
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makePostBox

  final case class ShrineOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0
  )

  def makeShrine(o: ShrineOpts): Group[Object3DEventMap] =
    val g         = new Group()
    val stone     = Toon.cel(CelOptions(color = 0xada7b6, bands = 3, tint = 0x655d80))
    val stoneDark = Toon.cel(CelOptions(color = 0x8d8798, bands = 3, tint = 0x605878))
    ThreeInterop.addChild(g, Util.box(1.05, 0.16, 0.85, stoneDark, 0.0, 0.08, 0.0))
    ThreeInterop.addChild(g, Util.box(0.85, 0.3, 0.68, stone, 0.0, 0.31, 0.0))
    ThreeInterop.addChild(g, Util.box(0.9, 0.05, 0.72, stoneDark, 0.0, 0.48, 0.0))
    val body = new Mesh(new CylinderGeometry(0.115, 0.16, 0.4, 10), stone)
    body.position.set(0.0, 0.7, 0.0)
    body.castShadow = true
    ThreeInterop.addChild(g, body)
    val head = new Mesh(new SphereGeometry(0.125, 12, 9), stone)
    head.position.set(0.0, 1.0, 0.0)
    head.castShadow = true
    ThreeInterop.addChild(g, head)
    val halo = new Mesh(new CircleGeometry(0.17, 14), stoneDark)
    halo.position.set(0.0, 1.0, -0.05)
    halo.rotation.y = math.Pi
    ThreeInterop.addChild(g, halo)
    Vector(-1.0, 1.0).foreach { s =>
      val eye = new Mesh(new BoxGeometry(0.035, 0.012, 0.01),
        Toon.cel(CelOptions(color = 0x5f5768, bands = 2)))
      eye.position.set(s * 0.04, 1.01, 0.122)
      ThreeInterop.addChild(g, eye)
    }
    val bib = new Mesh(new PlaneGeometry(0.26, 0.3),
      Toon.cel(CelOptions(color = Pal.shrineBib, bands = 2, side = 2, tint = 0x7a4060)))
    bib.position.set(0.0, 0.76, 0.14)
    bib.rotation.x = 0.16
    bib.castShadow = true
    ThreeInterop.addChild(g, bib)
    ThreeInterop.addChild(g,
      Util.box(0.22, 0.035, 0.035,
        Toon.cel(CelOptions(color = Pal.redDeep, bands = 2, tint = 0x7a4060)), 0.0, 0.9, 0.1))
    val postMat = Toon.cel(CelOptions(color = 0x8a6f5c, bands = 3, tint = 0x5c5680))
    Vector(-0.42, 0.42).foreach { sx =>
      Vector(-0.3, 0.3).foreach { sz =>
        ThreeInterop.addChild(g, Util.box(0.07, 1.24, 0.07, postMat, sx, 0.625, sz))
      }
    }
    val roofMat = Toon.cel(CelOptions(color = Pal.roofSlate, bands = 3, tint = 0x514b70))
    Vector(-1.0, 1.0).foreach { s =>
      val slab = Util.box(0.72, 0.07, 0.78, roofMat, s * 0.26, 1.32, 0.0)
      slab.rotation.z = -s * 0.42
      slab.castShadow = true
      ThreeInterop.addChild(g, slab)
    }
    ThreeInterop.addChild(g, Util.box(0.14, 0.08, 0.84, roofMat, 0.0, 1.44, 0.0))
    Vector(-0.26, 0.26).foreach { sx =>
      ThreeInterop.addChild(g,
        Util.cyl(0.06, 0.05, 0.11, 8,
          Toon.cel(CelOptions(color = 0xe8e4d8, bands = 2)), sx, 0.51, 0.2))
    }
    val flower = new Mesh(new IcosahedronGeometry(0.09, 0),
      Toon.cel(CelOptions(color = Pal.blossomDeep, bands = 2, tint = 0x8f7aa8)))
    flower.position.set(-0.26, 0.63, 0.2)
    ThreeInterop.addChild(g, flower)
    val plate = new Mesh(new PlaneGeometry(0.18, 0.36),
      Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.shrinePlate()), cache = false)))
    plate.position.set(0.43, 0.75, 0.32)
    plate.rotation.y = 0.3
    ThreeInterop.addChild(g, plate)

    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeShrine

  final case class CatOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0
  )

  def makeCat(o: CatOpts): Group[Object3DEventMap] =
    val g = new Group()
    ThreeInterop.setUserDataFlag(g, "planetRigid")
    val fur     = Toon.cel(CelOptions(color = Pal.cat, bands = 3, tint = 0x7a6f96))
    val furDark = Toon.cel(CelOptions(color = Pal.catDark, bands = 3, tint = 0x6a5f86))

    val body = new Mesh(new SphereGeometry(0.19, 12, 9), fur)
    body.scale.set(1.0, 0.92, 1.35)
    body.position.set(0.0, 0.19, 0.0)
    body.castShadow = true
    ThreeInterop.addChild(g, body)
    val patch = new Mesh(new SphereGeometry(0.16, 10, 8), furDark)
    patch.scale.set(0.92, 0.6, 1.15)
    patch.position.set(0.0, 0.27, -0.03)
    ThreeInterop.addChild(g, patch)

    val head = new Group()
    head.position.set(0.0, 0.38, 0.18)
    val skull = new Mesh(new SphereGeometry(0.115, 12, 9), fur)
    skull.scale.set(1.0, 0.95, 0.95)
    skull.castShadow = true
    ThreeInterop.addChild(head, skull)
    Vector(-1.0, 1.0).foreach { s =>
      val ear = new Mesh(new ConeGeometry(0.055, 0.09, 4), fur)
      ear.position.set(s * 0.062, 0.11, -0.01)
      ear.rotation.z = s * 0.2
      ThreeInterop.addChild(head, ear)
    }
    Vector(-1.0, 1.0).foreach { s =>
      val eye = new Mesh(new SphereGeometry(0.018, 8, 6), furDark)
      eye.position.set(s * 0.045, 0.015, 0.098)
      ThreeInterop.addChild(head, eye)
    }
    val nose = new Mesh(new SphereGeometry(0.014, 6, 5),
      Toon.cel(CelOptions(color = 0xe0a0a8, bands = 2)))
    nose.position.set(0.0, -0.022, 0.11)
    ThreeInterop.addChild(head, nose)
    ThreeInterop.addChild(g, head)

    Vector(-1.0, 1.0).foreach { s =>
      ThreeInterop.addChild(g, Util.cyl(0.035, 0.04, 0.16, 7, fur, s * 0.07, 0.08, 0.16))
    }
    val tail = new Group()
    val tailCurve = new CatmullRomCurve3(Vector(
      new Vector3(0.0, 0.2, -0.2),
      new Vector3(0.06, 0.28, -0.3),
      new Vector3(0.16, 0.34, -0.26),
      new Vector3(0.2, 0.3, -0.14)
    ).toJSArray)
    val tailMesh = new Mesh(new TubeGeometry(tailCurve, 14, 0.028, 5, false), furDark)
    tailMesh.castShadow = true
    ThreeInterop.addChild(tail, tailMesh)
    ThreeInterop.addChild(g, tail)

    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g.userData("head") = head.asInstanceOf[js.Any]
    g.userData("tail") = tail.asInstanceOf[js.Any]
    g.userData("body") = body.asInstanceOf[js.Any]
    g
  end makeCat

  final case class ConeOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    tilt: Double = 0.0
  )

  def makeCone(o: ConeOpts): Group[Object3DEventMap] =
    val g      = new Group()
    val orange = Toon.cel(CelOptions(color = Pal.orange, bands = 3, tint = 0x8f6050))
    ThreeInterop.addChild(g,
      Util.box(0.34, 0.035, 0.34,
        Toon.cel(CelOptions(color = 0xd8763c, bands = 3, tint = 0x8f6050)), 0.0, 0.018, 0.0))
    val cone = new Mesh(new ConeGeometry(0.13, 0.6, 10), orange)
    cone.position.set(0.0, 0.32, 0.0)
    cone.castShadow = true
    ThreeInterop.addChild(g, cone)
    val band = new Mesh(new CylinderGeometry(0.088, 0.105, 0.09, 10), M.white)
    band.position.set(0.0, 0.36, 0.0)
    ThreeInterop.addChild(g, band)
    g.position.set(o.x, o.y, o.z)
    g.rotation.set(o.tilt, o.ry, 0.0)
    g
  end makeCone

  final case class BarrierOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    len: Double = 1.6
  )

  def makeBarrier(o: BarrierOpts): Group[Object3DEventMap] =
    val g   = new Group()
    val red = Toon.cel(CelOptions(color = Pal.red, bands = 3, tint = 0x7a4060))
    Vector(-1.0, 1.0).foreach { s =>
      val post = Util.cyl(0.045, 0.05, 1.0, 8, M.white, (s * o.len) / 2.0, 0.5, 0.0)
      post.castShadow = true
      ThreeInterop.addChild(g, post)
      ThreeInterop.addChild(g, Util.box(0.16, 0.05, 0.24, M.dark, (s * o.len) / 2.0, 0.025, 0.0))
      Vector(0.34, 0.72).foreach { y =>
        ThreeInterop.addChild(g, Util.cyl(0.052, 0.052, 0.2, 8, red, (s * o.len) / 2.0, y, 0.0))
      }
    }
    val bar = Util.box(o.len, 0.09, 0.05, red, 0.0, 0.86, 0.0)
    bar.castShadow = true
    ThreeInterop.addChild(g, bar)
    ThreeInterop.addChild(g, Util.box(o.len - 0.1, 0.07, 0.04, M.white, 0.0, 0.62, 0.0))
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeBarrier

  final case class GuardrailOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    len: Double = 6.0
  )

  def makeGuardrail(o: GuardrailOpts): Group[Object3DEventMap] =
    val g     = new Group()
    val n     = math.max(2, math.round(o.len / 2.0).toInt)
    val parts = mutable.ArrayBuffer[Util.BakePart]()
    (0 to n).foreach { i =>
      val t = -o.len / 2.0 + (o.len / n) * i
      parts += Util.BakePart(new BoxGeometry(0.11, 0.82, 0.11), Some(Util.trs(t, 0.41, 0.0)))
    }
    val beam = new Mesh(Util.bake(parts.toList), M.metalDark)
    beam.castShadow = true
    ThreeInterop.addChild(g, beam)
    val rail = Util.box(o.len, 0.26, 0.07, M.white, 0.0, 0.72, 0.02)
    rail.castShadow = true
    ThreeInterop.addChild(g, rail)
    ThreeInterop.addChild(g, Util.box(o.len, 0.05, 0.1, M.metal, 0.0, 0.72, 0.01))
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeGuardrail

  final case class PlanterOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    r: Double = 0.2,
    n: Int = 4,
    flower: Boolean = false,
    seed: Int = 3
  )

  def makePlanter(o: PlanterOpts): Group[Object3DEventMap] =
    val rng = Util.rngKit(o.seed)
    val g   = new Group()
    val r   = o.r
    val pot = new Mesh(new CylinderGeometry(r, r * 0.78, r * 1.5, 10), M.terracotta)
    pot.position.set(0.0, r * 0.75, 0.0)
    pot.castShadow = true
    pot.receiveShadow = true
    ThreeInterop.addChild(g, pot)
    ThreeInterop.addChild(g,
      Util.cyl(r * 1.08, r * 1.08, r * 0.14, 10,
        Toon.cel(CelOptions(color = 0xb06a4c, bands = 3, tint = 0x6f5680)), 0.0, r * 1.46, 0.0))
    ThreeInterop.addChild(g,
      Util.cyl(r * 0.86, r * 0.86, r * 0.1, 10,
        Toon.cel(CelOptions(color = 0x6b5a4a, bands = 2)), 0.0, r * 1.46, 0.0))
    (0 until o.n).foreach { i =>
      val blob = new Mesh(new IcosahedronGeometry(r * rng.range(0.5, 0.85), 0),
        if i % 3 == 0 then M.leafDeep else M.leaf)
      blob.position.set(rng.range(-r * 0.6, r * 0.6), r * 1.7 + rng.range(0, r * 0.7),
        rng.range(-r * 0.6, r * 0.6))
      blob.rotation.set(rng.range(0, 3), rng.range(0, 3), rng.range(0, 3))
      blob.castShadow = true
      ThreeInterop.addChild(g, blob)
    }
    if o.flower then
      (0 until 3).foreach { _ =>
        val f = new Mesh(new IcosahedronGeometry(r * 0.2, 0),
          Toon.cel(CelOptions(color = rng.pick(Vector(Pal.red, Pal.yellow, Pal.blossomDeep)),
            bands = 2, tint = 0x8f7aa8)))
        f.position.set(rng.range(-r * 0.5, r * 0.5), r * 2.1, rng.range(-r * 0.5, r * 0.5))
        ThreeInterop.addChild(g, f)
      }
    g.position.set(o.x, o.y, o.z)
    g
  end makePlanter

  final case class UmbrellaOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    tilt: Double = 0.22,
    roll: Double = 0.16
  )

  def makeUmbrella(o: UmbrellaOpts): Group[Object3DEventMap] =
    val g = new Group()
    val canopy = new Mesh(new ConeGeometry(0.2, 0.62, 8, 1, true),
      Toon.flat(FlatOptions(color = Pal.umbrella, transparent = true, opacity = 0.42, side = 2,
        depthWrite = Some(false), cache = false)))
    canopy.position.set(0.0, 0.72, 0.0)
    ThreeInterop.setUserDataFlag(canopy, "noOutline")
    ThreeInterop.addChild(g, canopy)
    (0 until 8).foreach { i =>
      val a   = (i.toDouble / 8.0) * math.Pi * 2.0
      val rib = Util.box(math.hypot(0.2, 0.62), 0.012, 0.012, M.metal)
      rib.rotation.set(0.0, -a, -math.atan2(0.62, 0.2))
      rib.position.set(math.cos(a) * 0.1, 0.72, math.sin(a) * 0.1)
      ThreeInterop.addChild(g, rib)
    }
    ThreeInterop.addChild(g, Util.cyl(0.014, 0.014, 1.0, 6, M.metal, 0.0, 0.5, 0.0))
    val handle = new Mesh(new TorusGeometry(0.05, 0.014, 4.0, 8.0, math.Pi),
      Toon.cel(CelOptions(color = 0x6a5a5e, bands = 2)))
    handle.position.set(0.05, 0.02, 0.0)
    handle.rotation.set(math.Pi / 2.0, 0.0, 0.0)
    ThreeInterop.addChild(g, handle)
    g.position.set(o.x, o.y, o.z)
    g.rotation.set(o.tilt, o.ry, o.roll)
    g
  end makeUmbrella

  final case class BinsOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0
  )

  def makeBins(o: BinsOpts): Group[Object3DEventMap] =
    val g      = new Group()
    val colors = Vector(Pal.bin, 0x7fae6a, 0xd8c34a)
    colors.zipWithIndex.foreach { (c, i) =>
      val b = Util.box(0.44, 0.62, 0.4,
        Toon.cel(CelOptions(color = c, bands = 3, tint = 0x6f6790)), i * 0.5, 0.31, 0.0)
      b.castShadow = true
      b.receiveShadow = true
      ThreeInterop.addChild(g, b)
      ThreeInterop.addChild(g,
        Util.box(0.47, 0.05, 0.43,
          Toon.cel(CelOptions(color = Pal.metalDark, bands = 3)), i * 0.5, 0.64, 0.0))
    }
    val mesh = Textures.chainLinkTex().clone()
    mesh.wrapS = 1000
    mesh.wrapT = 1000
    mesh.repeat.set(11.0, 2.4)
    ThreeInterop.markForUpload(mesh)
    val net = new Mesh(new BoxGeometry(1.58, 0.3, 0.58),
      Toon.flat(FlatOptions(color = 0x5f8f6a, map = Some(mesh), transparent = true, opacity = 0.9,
        side = 2, depthWrite = Some(false), cache = false)))
    net.position.set(0.5, 0.56, 0.0)
    ThreeInterop.setUserDataFlag(net, "noOutline")
    ThreeInterop.addChild(g, net)
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeBins

  final case class BikeRackOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    n: Int = 8,
    spacing: Double = 0.62,
    seed: Int = 17
  )

  def makeBikeRack(o: BikeRackOpts): Group[Object3DEventMap] =
    val rng      = Util.rngKit(o.seed)
    val g        = new Group()
    val shared   = bicycleGeometry
    val frames   = Vector(0x3f6f9c, 0xd8a03c, 0x9c5a4a, 0x4f8f6a, 0x8f6fb5, 0x4a4a92, 0xc7c2d0, 0x2f9c9a)
    val instDark = new InstancedMesh(shared("dark"), M.dark, o.n.toDouble)
    val instFrame = new InstancedMesh(shared("frame"),
      Toon.cel(CelOptions(color = 0xffffff, bands = 3, tint = 0x4a4a92, cache = false)), o.n.toDouble)
    val instBrite = new InstancedMesh(shared("brite"), M.metal, o.n.toDouble)
    val instMesh = new InstancedMesh(shared("mesh"),
      Toon.cel(CelOptions(color = Pal.metal, bands = 3, side = 2, tint = 0x666090)), o.n.toDouble)
    val dummy = new Object3D()
    val col   = new Color()
    val ry0   = o.ry
    (0 until o.n).foreach { i =>
      val t = -((o.n - 1) * o.spacing) / 2.0 + i * o.spacing
      dummy.position.set(-math.sin(ry0) * t, 0.0, math.cos(ry0) * t)
      dummy.rotation.set(rng.range(-0.07, 0.07), ry0 + rng.range(-0.05, 0.05), 0.0, "YXZ")
      dummy.updateMatrix()
      instDark.setMatrixAt(i, dummy.matrix)
      instFrame.setMatrixAt(i, dummy.matrix)
      instBrite.setMatrixAt(i, dummy.matrix)
      instMesh.setMatrixAt(i, dummy.matrix)
      col.set(frames((i * 3 + o.seed) % frames.length))
      instFrame.setColorAt(i, col)
    }
    ThreeInterop.markForUpload(instFrame.instanceColor)
    Vector(instDark, instFrame, instBrite, instMesh).foreach { inst =>
      inst.castShadow = true
      inst.receiveShadow = true
      ThreeInterop.addChild(g, inst)
    }
    g.position.set(o.x, o.y, o.z)
    g
  end makeBikeRack

  final case class NoticeBoardOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    w: Double = 2.2,
    h: Double = 1.25,
    y0: Double = 0.85,
    wood: Int = 0x8a6f52
  )

  def makeNoticeBoard(o: NoticeBoardOpts): Group[Object3DEventMap] =
    val g       = new Group()
    val woodMat = Toon.cel(CelOptions(color = o.wood, bands = 3, tint = 0x5c5680))
    Vector(-1.0, 1.0).foreach { s =>
      val p = Util.box(0.11, o.y0 + o.h + 0.1, 0.11, woodMat,
        (s * (o.w - 0.2)) / 2.0, (o.y0 + o.h + 0.1) / 2.0, 0.0)
      p.castShadow = true
      ThreeInterop.addChild(g, p)
    }
    val panelMats = Props.matArray(woodMat, woodMat, woodMat, woodMat,
      Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.corkBoard()), cache = false)),
      woodMat)
    val panel = new Mesh(new BoxGeometry(o.w, o.h, 0.09), panelMats)
    panel.position.set(0.0, o.y0 + o.h / 2.0, 0.0)
    panel.castShadow = true
    panel.receiveShadow = true
    ThreeInterop.addChild(g, panel)
    Outline.hullOutline(panel, OutlineOptions(thickness = 0.003))
    ThreeInterop.addChild(g, Util.box(o.w + 0.1, 0.09, 0.14, woodMat, 0.0, o.y0 + o.h + 0.04, 0.0))
    val hood = Util.box(o.w + 0.24, 0.06, 0.34,
      Toon.cel(CelOptions(color = Pal.roofSlate, bands = 3, tint = 0x514b70)),
      0.0, o.y0 + o.h + 0.16, 0.1)
    hood.rotation.x = -0.2
    hood.castShadow = true
    ThreeInterop.addChild(g, hood)
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeNoticeBoard

  final case class BenchOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    len: Double = 1.7,
    wood: Int = 0xc0a582,
    back: Boolean = true
  )

  def makeBench(o: BenchOpts): Group[Object3DEventMap] =
    val g    = new Group()
    val slat = Toon.cel(CelOptions(color = o.wood, bands = 3, tint = 0x6f6790))
    val leg  = M.metalDark
    (0 until 3).foreach { i =>
      ThreeInterop.addChild(g, Util.box(o.len, 0.05, 0.13, slat, 0.0, 0.44, -0.16 + i * 0.16))
    }
    if o.back then
      (0 until 2).foreach { i =>
        val b = Util.box(o.len, 0.13, 0.05, slat, 0.0, 0.66 + i * 0.17, -0.22)
        b.castShadow = true
        ThreeInterop.addChild(g, b)
      }
      Vector(-1.0, 1.0).foreach { s =>
        val up = Util.box(0.07, 0.52, 0.07, leg, (s * (o.len - 0.3)) / 2.0, 0.66, -0.24)
        up.rotation.x = 0.12
        ThreeInterop.addChild(g, up)
      }
    Vector(-1.0, 1.0).foreach { s =>
      ThreeInterop.addChild(g, Util.box(0.08, 0.44, 0.42, leg, (s * (o.len - 0.3)) / 2.0, 0.22, -0.04))
    }
    g.traverse((n: Object3D[Object3DEventMap]) =>
      if ThreeInterop.isMesh(n) then
        n.castShadow = true
        n.receiveShadow = true
    )
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeBench

  final case class SignPostOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    h: Double = 2.4
  )

  def makeSignPost(o: SignPostOpts): Group[Object3DEventMap] =
    val g    = new Group()
    val post = Util.cyl(0.045, 0.05, o.h, 8, M.metal, 0.0, o.h / 2.0, 0.0)
    post.castShadow = true
    ThreeInterop.addChild(g, post)
    ThreeInterop.addChild(g, Util.cyl(0.1, 0.12, 0.14, 8, M.concreteMid, 0.0, 0.07, 0.0))
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeSignPost

  final case class VendBinOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    color: Int = 0x2f9c9a
  )

  def makeVendBin(o: VendBinOpts): Group[Object3DEventMap] =
    val g = new Group()
    val body = new Mesh(new CylinderGeometry(0.24, 0.22, 0.86, 10, 1, true),
      Toon.cel(CelOptions(color = o.color, bands = 3, side = 2, tint = 0x5c5680)))
    body.position.set(0.0, 0.43, 0.0)
    body.castShadow = true
    ThreeInterop.addChild(g, body)
    ThreeInterop.addChild(g, Util.cyl(0.26, 0.26, 0.06, 10, M.metalDark, 0.0, 0.89, 0.0))
    Vector(-1.0, 1.0).foreach { s =>
      ThreeInterop.addChild(g,
        Util.cyl(0.08, 0.08, 0.03, 8,
          Toon.cel(CelOptions(color = 0x2f3140, bands = 2)), s * 0.1, 0.92, 0.0))
    }
    ThreeInterop.addChild(g,
      Util.box(0.3, 0.11, 0.02,
        Toon.flat(FlatOptions(color = 0xf6f2e8)), 0.0, 0.62, 0.235))
    (0 until 3).foreach { i =>
      ThreeInterop.addChild(g,
        Util.cyl(0.031, 0.031, 0.11, 8,
          Toon.cel(CelOptions(color = Vector(0xe0453f, 0x3d6ec4, 0xf4c033)(i), bands = 2,
            tint = 0x6f6790)),
          -0.08 + i * 0.08, 0.72, -0.04 + (i % 2) * 0.07))
    }
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeVendBin

  final case class AirconOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    w: Double = 0.82,
    h: Double = 0.58,
    d: Double = 0.3,
    color: Int = 0xe2e0e4,
    feet: Boolean = true,
    standoff: Double = 0.09
  )

  def makeAircon(o: AirconOpts): Group[Object3DEventMap] =
    val g     = new Group()
    val shell = Toon.cel(CelOptions(color = o.color, bands = 3, tint = 0x6f6790))
    val b     = Util.box(o.w, o.h, o.d, shell, 0.0, o.h / 2.0, 0.0)
    b.castShadow = true
    b.receiveShadow = true
    ThreeInterop.addChild(g, b)
    Outline.hullOutline(b, OutlineOptions(thickness = 0.003))
    val ring = new Mesh(new CylinderGeometry(o.w * 0.28, o.w * 0.28, 0.03, 14), M.metalDark)
    ring.rotation.x = math.Pi / 2.0
    ring.position.set(o.w * 0.06, o.h * 0.52, o.d / 2.0 + 0.015)
    ThreeInterop.addChild(g, ring)
    (0 until 5).foreach { i =>
      ThreeInterop.addChild(g,
        Util.box(o.w * 0.5, 0.02, 0.02, M.metalDark,
          o.w * 0.06, o.h * 0.52 - 0.12 + i * 0.06, o.d / 2.0 + 0.03))
    }
    ThreeInterop.addChild(g,
      Util.box(o.w, 0.05, o.d + 0.04,
        Toon.cel(CelOptions(color = 0xcac7cf, bands = 3, tint = 0x6a6288)), 0.0, o.h + 0.02, 0.0))
    if o.feet then
      Vector(-1.0, 1.0).foreach { s =>
        ThreeInterop.addChild(g,
          Util.box(0.1, 0.14, o.d, M.metalDark,
            (s * (o.w - 0.2)) / 2.0, -0.07, 0.0))
      }
    else
      val so = o.standoff
      Vector(-1.0, 1.0).foreach { s =>
        val bx = (s * (o.w - 0.18)) / 2.0
        ThreeInterop.addChild(g, Util.box(0.05, 0.04, o.d + so, M.metalDark, bx, -0.02, -so / 2.0))
        ThreeInterop.addChild(g,
          Util.box(0.05, o.h * 0.66, 0.035, M.metalDark, bx, o.h * 0.31,
            -(o.d / 2.0 + so) + 0.018))
        ThreeInterop.addChild(g,
          Util.box(0.09, 0.09, 0.02, M.metalDark, bx, o.h * 0.55,
            -(o.d / 2.0 + so) + 0.008))
      }
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeAircon

  final case class LaundryPoleOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    len: Double = 2.4,
    h: Double = 1.75,
    n: Int = 4,
    seed: Int = 31
  )

  def makeLaundryPole(o: LaundryPoleOpts): Group[Object3DEventMap] =
    val rng = Util.rngKit(o.seed)
    val g   = new Group()
    Vector(-1.0, 1.0).foreach { s =>
      ThreeInterop.addChild(g, Util.cyl(0.035, 0.04, o.h, 6, M.metal, (s * o.len) / 2.0, o.h / 2.0, 0.0))
      ThreeInterop.addChild(g, Util.cyl(0.1, 0.12, 0.1, 8, M.concreteMid, (s * o.len) / 2.0, 0.05, 0.0))
    }
    val bar = Util.cyl(0.028, 0.028, o.len + 0.16, 6, M.metal, 0.0, o.h, 0.0)
    bar.rotation.z = math.Pi / 2.0
    bar.castShadow = true
    ThreeInterop.addChild(g, bar)
    val cols = Vector(Pal.wallBlue, Pal.blossom, Pal.wallWhite, Pal.yellow, Pal.wallCream, 0xa8cfe0)
    (0 until o.n).foreach { i =>
      val w = rng.range(0.3, 0.5)
      val h = rng.range(0.5, 0.8)
      val t = new Mesh(new PlaneGeometry(w, h),
        Toon.cel(CelOptions(color = rng.pick(cols), bands = 2, side = 2, tint = 0x6f6790)))
      t.position.set(-o.len / 2.0 + 0.3 + (i * (o.len - 0.6)) / math.max(1, o.n - 1),
        o.h - h / 2.0 - 0.04, 0.0)
      t.rotation.set(0.0, rng.range(-0.14, 0.14), rng.range(-0.05, 0.05))
      t.castShadow = true
      ThreeInterop.addChild(g, t)
      ThreeInterop.addChild(g,
        Util.box(0.05, 0.03, 0.03,
          Toon.flat(FlatOptions(color = 0xd8d4dc)), t.position.x - w / 2.0 + 0.04, o.h, 0.0))
    }
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeLaundryPole

  final case class CrowOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0
  )

  def makeCrow(o: CrowOpts): Group[Object3DEventMap] =
    val g       = new Group()
    val dark    = Toon.cel(CelOptions(color = 0x33303e, bands = 2, tint = 0x413c58))
    val back    = Toon.cel(CelOptions(color = 0x4a4557, bands = 2, tint = 0x453f5c))
    val beakMat = Toon.cel(CelOptions(color = 0x5e5768, bands = 2, tint = 0x453f5c))
    val body    = new Mesh(new SphereGeometry(0.075, 9, 7), dark)
    body.scale.set(0.85, 0.82, 1.75)
    body.position.set(0.0, 0.095, 0.01)
    ThreeInterop.addChild(g, body)
    val spine = new Mesh(new SphereGeometry(0.062, 9, 6), back)
    spine.scale.set(0.66, 0.5, 1.55)
    spine.position.set(0.0, 0.135, 0.0)
    ThreeInterop.addChild(g, spine)
    val head = new Mesh(new SphereGeometry(0.052, 9, 7), dark)
    head.scale.set(0.9, 0.85, 1.05)
    head.position.set(0.0, 0.165, 0.115)
    ThreeInterop.addChild(g, head)
    val beak = new Mesh(new ConeGeometry(0.019, 0.085, 5), beakMat)
    beak.rotation.x = math.Pi / 2.0
    beak.position.set(0.0, 0.158, 0.20)
    ThreeInterop.addChild(g, beak)
    val tail = new Mesh(new BoxGeometry(0.052, 0.016, 0.2), dark)
    tail.position.set(0.0, 0.115, -0.185)
    tail.rotation.x = -0.42
    ThreeInterop.addChild(g, tail)
    val tailTip = new Mesh(new BoxGeometry(0.038, 0.014, 0.075), back)
    tailTip.position.set(0.0, 0.155, -0.275)
    tailTip.rotation.x = -0.42
    ThreeInterop.addChild(g, tailTip)
    Vector(-1.0, 1.0).foreach { s =>
      val wing = new Mesh(new BoxGeometry(0.016, 0.055, 0.17), dark)
      wing.position.set(s * 0.056, 0.1, -0.02)
      wing.rotation.x = -0.12
      ThreeInterop.addChild(g, wing)
    }
    Vector(-1.0, 1.0).foreach { s =>
      ThreeInterop.addChild(g, Util.cyl(0.008, 0.008, 0.055, 4, beakMat, s * 0.026, 0.028, 0.03))
    }
    g.traverse((n: Object3D[Object3DEventMap]) => if ThreeInterop.isMesh(n) then n.castShadow = true)
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeCrow

  final case class CatBoxOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    cat: Int = 0xd8c9b4
  )

  def makeCatBox(o: CatBoxOpts): Group[Object3DEventMap] =
    val g      = new Group()
    val card   = Toon.cel(CelOptions(color = 0xc9a878, bands = 3, tint = 0x6f6790))
    val cardIn = Toon.cel(CelOptions(color = 0xb08f62, bands = 3, tint = 0x6a6288))
    val W      = 0.62
    val D      = 0.46
    val H      = 0.28
    ThreeInterop.addChild(g, Util.box(W, 0.04, D, cardIn, 0.0, 0.02, 0.0))
    Vector(-1.0, 1.0).foreach { s =>
      ThreeInterop.addChild(g, Util.box(W, H, 0.03, card, 0.0, H / 2.0, (s * D) / 2.0))
      ThreeInterop.addChild(g, Util.box(0.03, H, D, card, (s * W) / 2.0, H / 2.0, 0.0))
    }
    val flap = Util.box(W, 0.03, 0.2, card, 0.0, H, D / 2.0 + 0.09)
    flap.rotation.x = -0.7
    flap.castShadow = true
    ThreeInterop.addChild(g, flap)
    val fur  = Toon.cel(CelOptions(color = o.cat, bands = 3, tint = 0x7a6f96))
    val curl = new Mesh(new SphereGeometry(0.15, 10, 8), fur)
    curl.scale.set(1.15, 0.7, 0.95)
    curl.position.set(0.0, 0.16, -0.02)
    curl.castShadow = true
    ThreeInterop.addChild(g, curl)
    val head = new Mesh(new SphereGeometry(0.085, 10, 8), fur)
    head.position.set(0.12, 0.16, 0.08)
    ThreeInterop.addChild(g, head)
    Vector(-1.0, 1.0).foreach { s =>
      val ear = new Mesh(new ConeGeometry(0.04, 0.06, 4), fur)
      ear.position.set(0.12, 0.23, 0.08 + s * 0.045)
      ear.rotation.z = -0.3
      ThreeInterop.addChild(g, ear)
    }
    val tail = new Mesh(new TorusGeometry(0.09, 0.022, 4.0, 10.0, math.Pi * 1.1),
      Toon.cel(CelOptions(color = 0xa8977f, bands = 3, tint = 0x6a5f86)))
    tail.rotation.set(math.Pi / 2.0, 0.0, 0.4)
    tail.position.set(-0.06, 0.19, 0.07)
    ThreeInterop.addChild(g, tail)
    g.traverse((n: Object3D[Object3DEventMap]) => if ThreeInterop.isMesh(n) then n.receiveShadow = true)
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeCatBox

  final case class PaperSpot(
    x: Double,
    z: Double,
    y: Double = 0.0,
    ry: Double = 0.0,
    tilt: Double = 0.0
  )

  def makeLoosePaper(ctx: PropCtx, spots: Vector[PaperSpot]): Mesh[?, ?, ?] =
    if spots.isEmpty then throw new IllegalArgumentException("sakura-crossing: loose paper needs spots")
    val geo = new PlaneGeometry(0.21, 0.29)
    geo.rotateX(-math.Pi / 2.0)
    val mat   = Toon.flat(FlatOptions(color = 0xfbfaf4, map = Some(Textures.paperSheet()), cache = false))
    val inst  = new InstancedMesh(geo, mat, spots.length.toDouble)
    val dummy = new Object3D()
    spots.zipWithIndex.foreach { (s, i) =>
      dummy.position.set(s.x, s.y + 0.022, s.z)
      dummy.rotation.set(s.tilt, s.ry, 0.0)
      dummy.updateMatrix()
      inst.setMatrixAt(i, dummy.matrix)
    }
    ThreeInterop.setUserDataFlag(inst, "noOutline")
    inst.renderOrder = 2.0
    ctx.add(inst)
    inst
  end makeLoosePaper

  final case class UmbrellaStandOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    n: Int = 3,
    seed: Int = 51
  )

  def makeUmbrellaStand(o: UmbrellaStandOpts): Group[Object3DEventMap] =
    val rng = Util.rngKit(o.seed)
    val g   = new Group()
    val body = new Mesh(new CylinderGeometry(0.2, 0.18, 0.52, 10, 1, true),
      Toon.cel(CelOptions(color = 0xcfcbd4, bands = 3, side = 2, tint = 0x6a6288)))
    body.position.y = 0.26
    body.castShadow = true
    ThreeInterop.addChild(g, body)
    ThreeInterop.addChild(g, Util.cyl(0.19, 0.19, 0.03, 10, M.metalDark, 0.0, 0.03, 0.0))
    (0 until o.n).foreach { i =>
      val a    = (i.toDouble / o.n) * math.Pi * 2.0 + 0.6
      val lean = rng.range(0.1, 0.22)
      val u    = new Group()
      ThreeInterop.addChild(u, Util.cyl(0.013, 0.013, 0.94, 5, M.metal, 0.0, 0.47, 0.0))
      val canopy = new Mesh(new ConeGeometry(0.075, 0.42, 7, 1, true),
        Toon.flat(FlatOptions(
          color = rng.pick(Vector(Pal.umbrella, Pal.umbrella, 0xd8d4e4, 0xe4d8e0)),
          transparent = true, opacity = 0.46, side = 2, depthWrite = Some(false), cache = false
        )))
      canopy.position.y = 0.72
      ThreeInterop.setUserDataFlag(canopy, "noOutline")
      ThreeInterop.addChild(u, canopy)
      ThreeInterop.addChild(u,
        Util.cyl(0.014, 0.014, 0.1, 5,
          Toon.cel(CelOptions(color = 0x6a5a5e, bands = 2)), 0.0, 0.98, 0.0))
      u.position.set(math.cos(a) * 0.09, 0.06, math.sin(a) * 0.09)
      u.rotation.set(math.sin(a) * lean, 0.0, -math.cos(a) * lean)
      ThreeInterop.addChild(g, u)
    }
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeUmbrellaStand

  final case class HoseBoxOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    w: Double = 0.16
  )

  def makeHoseBox(o: HoseBoxOpts): Group[Object3DEventMap] =
    val g   = new Group()
    val red = Toon.cel(CelOptions(color = Pal.redDeep, bands = 3, tint = 0x7a4060))
    val b   = Util.box(o.w, 0.72, 0.5, red, 0.0, 0.36, 0.0)
    b.castShadow = true
    ThreeInterop.addChild(g, b)
    ThreeInterop.addChild(g,
      Util.box(0.02, 0.6, 0.4,
        Toon.cel(CelOptions(color = 0xb02c28, bands = 3, tint = 0x7a4060)), o.w / 2.0 + 0.01, 0.36, 0.0))
    ThreeInterop.addChild(g,
      Util.box(0.03, 0.07, 0.24,
        Toon.flat(FlatOptions(color = 0xf6f2e8)), o.w / 2.0 + 0.02, 0.58, 0.0))
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeHoseBox

  final case class FlowerBedOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    w: Double = 1.4,
    d: Double = 4.0,
    n: Int = -1,
    seed: Int = 61,
    edge: Int = 0x9c7f5e
  )

  def makeFlowerBed(o: FlowerBedOpts): Group[Object3DEventMap] =
    val rng  = Util.rngKit(o.seed)
    val g    = new Group()
    val edge = Toon.cel(CelOptions(color = o.edge, bands = 3, tint = 0x5c5680))
    val soil = Toon.cel(CelOptions(color = 0x8f7a62, bands = 3, tint = 0x615a80))
    ThreeInterop.addChild(g, Util.box(o.w, 0.16, o.d, soil, 0.0, 0.08, 0.0))
    Vector(-1.0, 1.0).foreach { s =>
      ThreeInterop.addChild(g, Util.box(o.w + 0.1, 0.22, 0.09, edge, 0.0, 0.11, (s * o.d) / 2.0))
      ThreeInterop.addChild(g, Util.box(0.09, 0.22, o.d + 0.1, edge, (s * o.w) / 2.0, 0.11, 0.0))
    }
    val leafMat = Vector(M.leaf, M.leafDeep,
      Toon.cel(CelOptions(color = Pal.leafPale, bands = 3, tint = 0x5b6f8c)))
    val n    = if o.n >= 0 then o.n else math.round(o.d * 3.4).toInt
    val blob = new IcosahedronGeometry(1.0, 0)
    (0 until n).foreach { i =>
      val r = rng.range(0.09, 0.16)
      val b = new Mesh(blob, leafMat(i % 3))
      b.position.set(rng.range(-o.w * 0.33, o.w * 0.33), 0.18 + r * 0.7,
        rng.range(-o.d * 0.45, o.d * 0.45))
      b.scale.set(r, r * 0.8, r)
      b.rotation.set(rng.range(0, 3), rng.range(0, 3), rng.range(0, 3))
      b.castShadow = true
      ThreeInterop.addChild(g, b)
      if i % 3 == 0 then
        val f = new Mesh(blob,
          Toon.cel(CelOptions(
            color = rng.pick(Vector(Pal.red, Pal.yellow, Pal.blossomDeep, Pal.orange, Pal.purple)),
            bands = 2, tint = 0x8f7aa8
          )))
        f.position.set(b.position.x + rng.range(-0.1, 0.1), 0.18 + r * 1.5,
          b.position.z + rng.range(-0.1, 0.1))
        f.scale.setScalar(rng.range(0.045, 0.07))
        ThreeInterop.addChild(g, f)
    }
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeFlowerBed

  final case class BucketOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    color: Int = 0xb8bcc6,
    water: Boolean = false,
    tilt: Double = 0.0,
    roll: Double = 0.0
  )

  def makeBucket(o: BucketOpts): Group[Object3DEventMap] =
    val g = new Group()
    val body = new Mesh(new CylinderGeometry(0.14, 0.11, 0.24, 10, 1, true),
      Toon.cel(CelOptions(color = o.color, bands = 3, side = 2, tint = 0x666090)))
    body.position.y = 0.12
    body.castShadow = true
    ThreeInterop.addChild(g, body)
    ThreeInterop.addChild(g, Util.cyl(0.11, 0.11, 0.02, 10, M.metalDark, 0.0, 0.01, 0.0))
    ThreeInterop.addChild(g, Util.cyl(0.145, 0.145, 0.02, 10, M.metal, 0.0, 0.24, 0.0))
    val handle = new Mesh(new TorusGeometry(0.13, 0.012, 4.0, 9.0, math.Pi), M.metalDark)
    handle.rotation.y = math.Pi / 2.0
    handle.position.y = 0.24
    ThreeInterop.addChild(g, handle)
    if o.water then
      val wat = new Mesh(new CircleGeometry(0.115, 10),
        Toon.flat(FlatOptions(color = Pal.water)))
      wat.rotation.x = -math.Pi / 2.0
      wat.position.y = 0.19
      ThreeInterop.addChild(g, wat)
    g.position.set(o.x, o.y, o.z)
    g.rotation.set(o.tilt, o.ry, o.roll)
    g
  end makeBucket

  final case class BroomOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    tilt: Double = 0.24,
    roll: Double = 0.1
  )

  def makeBroom(o: BroomOpts): Group[Object3DEventMap] =
    val g = new Group()
    val handle = Util.cyl(0.022, 0.026, 1.5, 6,
      Toon.cel(CelOptions(color = 0xc2a874, bands = 3, tint = 0x6f6790)), 0.0, 0.75, 0.0)
    handle.castShadow = true
    ThreeInterop.addChild(g, handle)
    val head = new Mesh(new ConeGeometry(0.18, 0.44, 7, 1, true),
      Toon.cel(CelOptions(color = 0xa88a5e, bands = 3, side = 2, tint = 0x6f6790)))
    head.position.y = 0.22
    head.castShadow = true
    ThreeInterop.addChild(g, head)
    ThreeInterop.addChild(g,
      Util.cyl(0.055, 0.05, 0.08, 7,
        Toon.cel(CelOptions(color = 0x8a6f52, bands = 2)), 0.0, 0.43, 0.0))
    g.position.set(o.x, o.y, o.z)
    g.rotation.set(o.tilt, o.ry, o.roll)
    g
  end makeBroom

  final case class MilkCrateOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    color: Int = 0xd8d4dc,
    n: Int = 2
  )

  def makeMilkCrate(o: MilkCrateOpts): Group[Object3DEventMap] =
    val g     = new Group()
    val crate = Toon.cel(CelOptions(color = o.color, bands = 3, tint = 0x6a6288))
    (0 until o.n).foreach { i =>
      val c = Util.box(0.44, 0.24, 0.32, crate, 0.0, 0.12 + i * 0.24, 0.0)
      c.castShadow = true
      c.receiveShadow = true
      ThreeInterop.addChild(g, c)
      ThreeInterop.addChild(g,
        Util.box(0.38, 0.04, 0.26,
          Toon.cel(CelOptions(color = 0x9a94a6, bands = 2)), 0.0, 0.23 + i * 0.24, 0.0))
    }
    (0 until 4).foreach { i =>
      ThreeInterop.addChild(g,
        Util.cyl(0.032, 0.032, 0.14, 8,
          Toon.cel(CelOptions(color = 0xf6f2ea, bands = "soft", tint = 0x9c93b8)),
          -0.14 + (i % 2) * 0.1, 0.24 * o.n + 0.07, -0.07 + (i / 2) * 0.14))
    }
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeMilkCrate

  final case class TapPostOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    h: Double = 0.86
  )

  def makeTapPost(o: TapPostOpts): Group[Object3DEventMap] =
    val g    = new Group()
    val H    = o.h
    val dish = new Mesh(new CylinderGeometry(0.29, 0.26, 0.1, 12), M.concreteMid)
    dish.position.y = 0.05
    dish.receiveShadow = true
    ThreeInterop.addChild(g, dish)
    val lip = new Mesh(new TorusGeometry(0.28, 0.028, 4.0, 14.0), M.concrete)
    lip.translateY(0.1)
    lip.rotateX(math.Pi / 2.0)
    ThreeInterop.addChild(g, lip)
    val wet = new Mesh(new CircleGeometry(0.2, 12),
      Toon.flat(FlatOptions(color = 0x8e94a8, transparent = true, opacity = 0.45,
        depthWrite = Some(false), cache = false)))
    wet.rotation.x = -math.Pi / 2.0
    wet.position.y = 0.101
    ThreeInterop.setUserDataFlag(wet, "noOutline")
    ThreeInterop.addChild(g, wet)
    val riser = Util.cyl(0.028, 0.032, H, 8, M.metal, 0.0, H / 2.0, 0.0)
    riser.castShadow = true
    ThreeInterop.addChild(g, riser)
    ThreeInterop.addChild(g, Util.box(0.12, 0.16, 0.1, M.metalDark, 0.0, 0.26, 0.06))
    val brass = Toon.cel(CelOptions(color = 0xa8925e, bands = 3, tint = 0x6a5a80))
    val spout = Util.cyl(0.02, 0.02, 0.16, 6, brass, 0.0, H - 0.05, 0.08)
    spout.rotation.x = math.Pi / 2.0 - 0.5
    ThreeInterop.addChild(g, spout)
    ThreeInterop.addChild(g, Util.cyl(0.035, 0.035, 0.05, 8, brass, 0.0, H, 0.0))
    Vector(0.0, math.Pi / 2.0).foreach { r =>
      val bar = Util.box(0.14, 0.018, 0.018,
        Toon.cel(CelOptions(color = 0x9c5a4a, bands = 3, tint = 0x7a4060)), 0.0, H + 0.05, 0.0)
      bar.rotation.y = r
      ThreeInterop.addChild(g, bar)
    }
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeTapPost

  final case class StorageShedOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    w: Double = 1.3,
    d: Double = 0.72,
    h: Double = 1.62,
    color: Int = 0xcdd2cf
  )

  def makeStorageShed(o: StorageShedOpts): Group[Object3DEventMap] =
    val g     = new Group()
    val shell = Toon.cel(CelOptions(color = o.color, bands = 3, tint = 0x6a6288))
    val trim  = Toon.cel(CelOptions(color = 0x8f9a96, bands = 3, tint = 0x5c5680))
    ThreeInterop.addChild(g, Util.box(o.w + 0.1, 0.09, o.d + 0.1, M.concreteMid, 0.0, 0.045, 0.0))
    val body = Util.box(o.w, o.h, o.d, shell, 0.0, 0.09 + o.h / 2.0, 0.0)
    body.castShadow = true
    body.receiveShadow = true
    ThreeInterop.addChild(g, body)
    Outline.hullOutline(body, OutlineOptions(thickness = 0.0034))
    val ribs = mutable.ArrayBuffer[Util.BakePart]()
    val n    = math.round(o.w / 0.14).toInt
    (0 until n).foreach { i =>
      ribs += Util.BakePart(new BoxGeometry(0.05, o.h - 0.24, o.d + 0.03),
        Some(Util.trs(-o.w / 2.0 + 0.07 + i * ((o.w - 0.14) / (n - 1)), 0.09 + o.h / 2.0, 0.0)))
    }
    val rm = new Mesh(Util.bake(ribs.toList), trim)
    rm.castShadow = true
    ThreeInterop.addChild(g, rm)
    val roof = Util.box(o.w + 0.12, 0.06, o.d + 0.14, trim, 0.0, 0.09 + o.h + 0.05, -0.02)
    roof.rotation.x = 0.06
    roof.castShadow = true
    ThreeInterop.addChild(g, roof)
    ThreeInterop.addChild(g, Util.box(o.w + 0.14, 0.05, 0.05, trim, 0.0, 0.09 + o.h + 0.015, o.d / 2.0 + 0.06))
    Vector(-1.0, 1.0).foreach { s =>
      ThreeInterop.addChild(g,
        Util.box(o.w / 2.0 - 0.03, o.h - 0.3, 0.03, shell,
          s * (o.w / 4.0), 0.09 + o.h / 2.0 - 0.02, o.d / 2.0 + 0.02))
      ThreeInterop.addChild(g,
        Util.box(0.04, 0.24, 0.05, M.metalDark, s * 0.07,
          0.09 + o.h * 0.55, o.d / 2.0 + 0.045))
    }
    ThreeInterop.addChild(g, Util.box(o.w, 0.05, 0.06, trim, 0.0, 0.09 + o.h - 0.13, o.d / 2.0 + 0.03))
    ThreeInterop.addChild(g, Util.box(o.w, 0.05, 0.06, trim, 0.0, 0.24, o.d / 2.0 + 0.03))
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g.userData("top") = (0.09 + o.h + 0.08).asInstanceOf[js.Any]
    g
  end makeStorageShed

  final case class PotShelfOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    w: Double = 1.1,
    n: Int = 6,
    seed: Int = 91
  )

  def makePotShelf(o: PotShelfOpts): Group[Object3DEventMap] =
    val rng   = Util.rngKit(o.seed)
    val g     = new Group()
    val w     = o.w
    val d     = 0.34
    val frame = mutable.ArrayBuffer[Util.BakePart]()
    Vector(-1.0, 1.0).foreach { sx =>
      Vector(-1.0, 1.0).foreach { sz =>
        frame += Util.BakePart(new CylinderGeometry(0.016, 0.016, 0.78, 5),
          Some(Util.trs(sx * (w / 2.0 - 0.05), 0.39, sz * (d / 2.0 - 0.04))))
      }
    }
    Vector(0.36, 0.74).foreach { y =>
      Vector(-1.0, 1.0).foreach { sz =>
        frame += Util.BakePart(new BoxGeometry(w, 0.022, 0.022),
          Some(Util.trs(0.0, y, sz * (d / 2.0 - 0.04))))
      }
      val n = math.round(w / 0.09).toInt
      (0 to n).foreach { i =>
        frame += Util.BakePart(new BoxGeometry(0.014, 0.014, d - 0.08),
          Some(Util.trs(-w / 2.0 + (w / n) * i, y, 0.0)))
      }
    }
    val fm = new Mesh(Util.bake(frame.toList), M.metal)
    fm.castShadow = true
    ThreeInterop.addChild(g, fm)
    (0 until o.n).foreach { i =>
      val y   = if i % 2 == 1 then 0.75 else 0.37
      val r   = rng.range(0.07, 0.1)
      val t   = -w / 2.0 + 0.14 + (i % 3) * ((w - 0.28) / 2.0) + rng.range(-0.03, 0.03)
      val pot = new Mesh(new CylinderGeometry(r, r * 0.76, r * 1.5, 9), M.terracotta)
      pot.position.set(t, y + r * 0.75, rng.range(-0.05, 0.05))
      pot.castShadow = true
      ThreeInterop.addChild(g, pot)
      if rng.chance(0.78) then
        (0 until 3).foreach { k =>
          val blob = new Mesh(new IcosahedronGeometry(r * rng.range(0.5, 0.85), 0),
            if k == 0 then M.leafDeep else M.leaf)
          blob.position.set(pot.position.x + rng.range(-r * 0.6, r * 0.6),
            y + r * 1.7 + rng.range(0, r * 0.6), pot.position.z + rng.range(-r * 0.5, r * 0.5))
          blob.rotation.set(rng.range(0, 3), rng.range(0, 3), rng.range(0, 3))
          blob.castShadow = true
          ThreeInterop.addChild(g, blob)
        }
    }
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makePotShelf

  final case class MailboxBankOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    cols: Int = 4,
    rows: Int = 2,
    y0: Double = 0.85
  )

  def makeMailboxBank(o: MailboxBankOpts): Group[Object3DEventMap] =
    val g     = new Group()
    val cw    = 0.19
    val ch    = 0.16
    val w     = o.cols * cw + 0.06
    val h     = o.rows * ch + 0.06
    val shell = Toon.cel(CelOptions(color = 0xb9bec6, bands = 3, tint = 0x666090))
    Vector(-1.0, 1.0).foreach { s =>
      val p = Util.box(0.05, o.y0 + 0.06, 0.05, M.metalDark,
        s * (w / 2.0 - 0.05), (o.y0 + 0.06) / 2.0, 0.0)
      p.castShadow = true
      ThreeInterop.addChild(g, p)
    }
    val body = Util.box(w, h, 0.24, shell, 0.0, o.y0 + h / 2.0, 0.0)
    body.castShadow = true
    body.receiveShadow = true
    ThreeInterop.addChild(g, body)
    Outline.hullOutline(body, OutlineOptions(thickness = 0.003))
    ThreeInterop.addChild(g,
      Util.box(w + 0.06, 0.04, 0.3,
        Toon.cel(CelOptions(color = 0x8f9aa6, bands = 3, tint = 0x5c5680)), 0.0, o.y0 + h + 0.02, 0.02))
    (0 until o.rows).foreach { r =>
      (0 until o.cols).foreach { k =>
        val dx = -w / 2.0 + 0.03 + cw * (k + 0.5)
        val dy = o.y0 + 0.03 + ch * (o.rows - 1 - r) + ch / 2.0
        ThreeInterop.addChild(g,
          Util.box(cw - 0.022, ch - 0.022, 0.02,
            Toon.cel(CelOptions(color = 0xcfd4da, bands = 3, tint = 0x666090)), dx, dy, 0.125))
        ThreeInterop.addChild(g,
          Util.box(cw - 0.07, 0.016, 0.014,
            Toon.cel(CelOptions(color = 0x5f5768, bands = 2)), dx, dy + 0.038, 0.135))
        val lock = Util.cyl(0.011, 0.011, 0.014, 6, M.metalDark, dx + cw / 2.0 - 0.045, dy - 0.035, 0.135)
        lock.rotateX(math.Pi / 2.0)
        ThreeInterop.addChild(g, lock)
        ThreeInterop.addChild(g,
          Util.box(0.045, 0.026, 0.008,
            Toon.flat(FlatOptions(color = 0xf6f2e8)), dx - cw / 2.0 + 0.045, dy - 0.035, 0.136))
      }
    }
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeMailboxBank

  final case class DeliveryBoxOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    color: Int = 0x6f7a70
  )

  def makeDeliveryBox(o: DeliveryBoxOpts): Group[Object3DEventMap] =
    val g     = new Group()
    val w     = 0.44
    val d     = 0.4
    val h     = 0.6
    val shell = Toon.cel(CelOptions(color = o.color, bands = 3, tint = 0x5b6f8c))
    ThreeInterop.addChild(g, Util.box(w + 0.06, 0.05, d + 0.06, M.concreteMid, 0.0, 0.025, 0.0))
    val b = Util.box(w, h, d, shell, 0.0, 0.05 + h / 2.0, 0.0)
    b.castShadow = true
    b.receiveShadow = true
    ThreeInterop.addChild(g, b)
    Outline.hullOutline(b, OutlineOptions(thickness = 0.003))
    val lid = Util.box(w - 0.04, 0.24, 0.03,
      Toon.cel(CelOptions(color = 0x5f6a62, bands = 3, tint = 0x5b6f8c)), 0.0, 0.05 + h - 0.16, d / 2.0 + 0.015)
    ThreeInterop.addChild(g, lid)
    ThreeInterop.addChild(g, Util.box(0.16, 0.025, 0.03, M.metalDark, 0.0, 0.05 + h - 0.06, d / 2.0 + 0.03))
    ThreeInterop.addChild(g,
      Util.box(0.12, 0.1, 0.02,
        Toon.cel(CelOptions(color = 0x8f9a92, bands = 2, tint = 0x5b6f8c)), -0.12, 0.05 + h * 0.42, d / 2.0 + 0.012))
    ThreeInterop.addChild(g,
      Util.box(0.2, 0.09, 0.01,
        Toon.flat(FlatOptions(color = 0xf2efe4)), 0.08, 0.05 + h * 0.42, d / 2.0 + 0.008))
    ThreeInterop.addChild(g,
      Util.box(w + 0.03, 0.04, d + 0.03,
        Toon.cel(CelOptions(color = 0x5f6a62, bands = 3, tint = 0x5b6f8c)), 0.0, 0.05 + h + 0.02, 0.0))
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeDeliveryBox

  final case class DoormatOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    w: Double = 0.6,
    d: Double = 0.36,
    color: Int = 0x8a7f6a
  )

  def makeDoormat(o: DoormatOpts): Group[Object3DEventMap] =
    val g = new Group()
    val mat0 = Util.box(o.w, 0.022, o.d,
      Toon.cel(CelOptions(color = o.color, bands = 2, tint = 0x615a80)), 0.0, 0.011, 0.0)
    mat0.receiveShadow = true
    ThreeInterop.addChild(g, mat0)
    ThreeInterop.addChild(g,
      Util.box(o.w - 0.09, 0.026, o.d - 0.08,
        Toon.cel(CelOptions(color = 0x9c9078, bands = 2, tint = 0x615a80)), 0.0, 0.014, 0.0))
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeDoormat

  final case class IvyOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    len: Double = 3.0,
    top: Double = 0.9,
    drop: Double = 0.6,
    n: Int = -1,
    face: Double = 0.0,
    seed: Int = 77
  )

  def makeIvy(o: IvyOpts): Group[Object3DEventMap] =
    val rng   = Util.rngKit(o.seed)
    val g     = new Group()
    val n     = if o.n >= 0 then o.n else math.round(o.len * 16).toInt
    val geo   = new IcosahedronGeometry(1.0, 0)
    val lists = Vector(mutable.ArrayBuffer[Matrix4](), mutable.ArrayBuffer[Matrix4]())
    (0 until n).foreach { _ =>
      val t = rng.range(-o.len / 2.0, o.len / 2.0)
      val u = math.pow(rng.next(), 1.7)
      val y = o.top - u * o.drop
      val r = rng.range(0.055, 0.1) * (1 - u * 0.35)
      lists(if rng.chance(0.62) then 0 else 1) += Util.trs(
        t + rng.range(-0.06, 0.06), y, rng.range(-0.04, 0.04) + o.face * (0.02 + u * 0.03),
        rng.range(0, 3), rng.range(0, 3), rng.range(0, 3), r, r * 0.7, r
      )
    }
    val cols = Vector(Pal.leafDeep, Pal.leaf)
    lists.zipWithIndex.foreach { (list, i) =>
      if list.nonEmpty then
        val inst = new InstancedMesh(geo,
          Toon.cel(CelOptions(color = cols(i), bands = 3, tint = 0x5b6f8c)), list.length.toDouble)
        list.zipWithIndex.foreach { (mx, k) => inst.setMatrixAt(k, mx) }
        inst.castShadow = true
        ThreeInterop.addChild(g, inst)
    }
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeIvy

  final case class ReturnPostOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0
  )

  def makeReturnPost(o: ReturnPostOpts): Group[Object3DEventMap] =
    val g     = new Group()
    val w     = 0.62
    val d     = 0.46
    val h     = 1.0
    val shell = Toon.cel(CelOptions(color = 0x3f7a6a, bands = 3, tint = 0x4a6a80))
    ThreeInterop.addChild(g, Util.box(w + 0.12, 0.08, d + 0.12, M.concreteMid, 0.0, 0.04, 0.0))
    val b = Util.box(w, h, d, shell, 0.0, 0.08 + h / 2.0, 0.0)
    b.castShadow = true
    b.receiveShadow = true
    ThreeInterop.addChild(g, b)
    Outline.hullOutline(b, OutlineOptions(thickness = 0.0034))
    val top0 = Util.box(w + 0.06, 0.1, d + 0.06,
      Toon.cel(CelOptions(color = 0x356656, bands = 3, tint = 0x4a6a80)), 0.0, 0.08 + h + 0.05, 0.0)
    top0.rotation.x = 0.16
    top0.castShadow = true
    ThreeInterop.addChild(g, top0)
    ThreeInterop.addChild(g,
      Util.box(w - 0.18, 0.045, 0.1,
        Toon.cel(CelOptions(color = Pal.black, bands = 2, tint = 0x4b4560)), 0.0, 0.08 + h + 0.08, -0.1))
    ThreeInterop.addChild(g,
      Util.box(w - 0.1, 0.03, 0.16,
        Toon.cel(CelOptions(color = 0x356656, bands = 3, tint = 0x4a6a80)), 0.0, 0.08 + h + 0.14, -0.15))
    val plate = new Mesh(new PlaneGeometry(0.5, 0.25),
      Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.returnPlate()), cache = false)))
    plate.position.set(0.0, 0.08 + h * 0.72, -(d / 2.0 + 0.005))
    plate.rotation.y = math.Pi
    ThreeInterop.setUserDataFlag(plate, "noOutline")
    ThreeInterop.addChild(g, plate)
    ThreeInterop.addChild(g,
      Util.box(w - 0.12, 0.4, 0.02,
        Toon.cel(CelOptions(color = 0x356656, bands = 3, tint = 0x4a6a80)), 0.0, 0.08 + h * 0.3, -(d / 2.0 + 0.005)))
    val knob = Util.cyl(0.022, 0.022, 0.02, 8, M.metalDark, 0.16, 0.08 + h * 0.3, -(d / 2.0 + 0.02))
    knob.rotateX(math.Pi / 2.0)
    ThreeInterop.addChild(g, knob)
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeReturnPost

  final case class PetBowlOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0
  )

  def makePetBowl(o: PetBowlOpts): Group[Object3DEventMap] =
    val g = new Group()
    Vector((0.0, 0xe0574a), (0.22, 0x4a7fae)).foreach { (dx, c) =>
      val bowl = new Mesh(new CylinderGeometry(0.085, 0.06, 0.05, 10),
        Toon.cel(CelOptions(color = c, bands = 3, tint = 0x6f6790)))
      bowl.position.set(dx, 0.025, 0.0)
      bowl.castShadow = true
      ThreeInterop.addChild(g, bowl)
      val fill = new Mesh(new CircleGeometry(0.065, 10),
        Toon.flat(FlatOptions(color = if dx > 0 then Pal.water else 0xc8a878)))
      fill.rotation.x = -math.Pi / 2.0
      fill.position.set(dx, 0.045, 0.0)
      ThreeInterop.addChild(g, fill)
    }
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makePetBowl

  final case class PhoneBoothOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    w: Double = 1.0,
    d: Double = 1.0,
    color: Int = 0x4f8f6a
  )

  def makePhoneBooth(o: PhoneBoothOpts): Group[Object3DEventMap] =
    val g         = new Group()
    val W         = o.w
    val D         = o.d
    val H         = 2.30
    val SILL      = 0.56
    val TRAN      = 1.98
    val PLINTH    = 0.14
    val POST      = 0.075
    val green     = Toon.cel(CelOptions(color = o.color, bands = 3, tint = 0x4a6a80))
    val greenDeep = Toon.cel(CelOptions(color = 0x3a6b52, bands = 3, tint = 0x46647c))
    val cream     = Toon.cel(CelOptions(color = 0xf2efe4, bands = 3, tint = 0x6f6790))
    val glassMat = Toon.flat(FlatOptions(color = 0xd8e8f0, transparent = true, opacity = 0.42,
      depthWrite = Some(false), side = 2, cache = false))
    val parts = mutable.LinkedHashMap(
      "green" -> mutable.ArrayBuffer[Util.BakePart](),
      "deep"  -> mutable.ArrayBuffer[Util.BakePart](),
      "cream" -> mutable.ArrayBuffer[Util.BakePart](),
      "metal" -> mutable.ArrayBuffer[Util.BakePart](),
      "dark"  -> mutable.ArrayBuffer[Util.BakePart]()
    )
    def push(key: String, geo: BufferGeometry[?, ?], mx: Matrix4): Unit =
      parts(key) += Util.BakePart(geo, Some(mx))

    push("deep", new BoxGeometry(W + 0.12, PLINTH, D + 0.12), Util.trs(0.0, PLINTH / 2.0, 0.0))
    push("metal", new BoxGeometry(W + 0.14, 0.035, D + 0.14), Util.trs(0.0, PLINTH + 0.015, 0.0))
    Vector(-1.0, 1.0).foreach { sx =>
      Vector(-1.0, 1.0).foreach { sz =>
        push("green", new BoxGeometry(POST, H - PLINTH, POST),
          Util.trs(sx * (W / 2.0 - POST / 2.0), PLINTH + (H - PLINTH) / 2.0,
            sz * (D / 2.0 - POST / 2.0)))
      }
    }
    Vector(
      (W, POST, 0.0, D / 2.0 - POST / 2.0), (W, POST, 0.0, -(D / 2.0 - POST / 2.0)), (POST, D, W / 2.0 - POST / 2.0, 0.0),
      (POST, D, -(W / 2.0 - POST / 2.0), 0.0)
    ).foreach { (w0, d0, x0, z0) =>
      push("green", new BoxGeometry(w0, 0.09, d0), Util.trs(x0, H - 0.045, z0))
    }
    Vector(
      (0.0, W - POST * 2.0, 0.0, D / 2.0 - 0.035, false), (W / 2.0 - 0.035, D - POST * 2.0, W / 2.0 - 0.035, 0.0, true),
      (-(W / 2.0 - 0.035), D - POST * 2.0, -(W / 2.0 - 0.035), 0.0, true)
    ).foreach { (_, sw, px, pz, along) =>
      val gx = if along then 0.07 else sw
      val gz = if along then sw else 0.07
      push("deep", new BoxGeometry(gx, SILL - PLINTH - 0.06, gz),
        Util.trs(px, (SILL + PLINTH) / 2.0 + 0.03, pz))
      push("green", new BoxGeometry(gx + 0.02, 0.07, gz + 0.02), Util.trs(px, PLINTH + 0.035, pz))
      push("cream", new BoxGeometry(gx + 0.02, 0.05, gz + 0.02), Util.trs(px, SILL + 0.02, pz))
      push("green", new BoxGeometry(gx, 0.07, gz), Util.trs(px, TRAN + 0.035, pz))
      val pane = new Mesh(new PlaneGeometry(sw, TRAN - SILL - 0.06), glassMat)
      pane.position.set(px, (SILL + TRAN) / 2.0, pz)
      if along then pane.rotation.y = math.Pi / 2.0
      ThreeInterop.setUserDataFlag(pane, "noOutline")
      ThreeInterop.setUserDataFlag(pane, "noShadow")
      ThreeInterop.addChild(g, pane)
      val hop = new Mesh(new PlaneGeometry(sw, H - TRAN - 0.12), glassMat)
      hop.position.set(px, (TRAN + 0.07 + H - 0.09) / 2.0, pz)
      if along then hop.rotation.y = math.Pi / 2.0
      ThreeInterop.setUserDataFlag(hop, "noOutline")
      ThreeInterop.setUserDataFlag(hop, "noShadow")
      ThreeInterop.addChild(g, hop)
    }

    definePhoneDoor(g, push, W, D, H, SILL, TRAN, PLINTH, POST, glassMat)

    val art     = Textures.phoneBoxSign()
    val faceMat = Toon.flat(FlatOptions(color = 0xfff6e2, map = Some(art), cache = false))
    val sideMat = Toon.flat(FlatOptions(color = 0x3a6b52))
    val boxMats = Props.matArray(faceMat, faceMat, sideMat, sideMat, faceMat, faceMat)
    val box0    = new Mesh(new BoxGeometry(W + 0.04, 0.3, D + 0.04), boxMats)
    box0.position.set(0.0, H + 0.15, 0.0)
    box0.castShadow = true
    ThreeInterop.addChild(g, box0)
    Outline.hullOutline(box0, OutlineOptions(thickness = 0.003))
    push("deep", new BoxGeometry(W + 0.16, 0.07, D + 0.16), Util.trs(0.0, H + 0.335, 0.0))
    push("deep", new BoxGeometry(W + 0.1, 0.05, D + 0.1), Util.trs(0.0, H + 0.02, 0.0))

    definePhoneInstrument(g, push, D, H)

    val n0 = new Mesh(new PlaneGeometry(0.17, 0.23),
      Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.phoneNotice(0)), cache = false)))
    n0.position.set(-(W / 2.0 - 0.055), 1.62, 0.16)
    n0.rotation.y = -math.Pi / 2.0
    ThreeInterop.setUserDataFlag(n0, "noOutline")
    ThreeInterop.addChild(g, n0)
    val n1 = new Mesh(new PlaneGeometry(0.15, 0.2),
      Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.phoneNotice(1)), cache = false)))
    n1.position.set(0.24, 1.66, -(D / 2.0 - 0.09))
    n1.rotation.y = math.Pi
    ThreeInterop.setUserDataFlag(n1, "noOutline")
    ThreeInterop.addChild(g, n1)

    Vector((0.16, -(D / 2.0 - 0.05), math.Pi, 0.26), (-(W / 2.0 - 0.05), -0.1, -math.Pi / 2.0, 0.22))
      .foreach { (dx, dz, ry, gw) =>
        val hi = new Mesh(new PlaneGeometry(gw, (TRAN - SILL) * 1.02),
          Toon.flat(FlatOptions(color = 0xf4fbff, transparent = true, opacity = 0.3,
            depthWrite = Some(false), cache = false)))
        hi.position.set(dx, (SILL + TRAN) / 2.0, dz)
        hi.rotation.set(0.0, ry, 0.26)
        ThreeInterop.setUserDataFlag(hi, "noOutline")
        ThreeInterop.addChild(g, hi)
      }

    Vector((-(W / 2.0 - 0.035), 0.18, 0.3, 0.16), (W / 2.0 - 0.035, -0.1, 0.22, 0.12))
      .foreach { (dx, dz, w0, h0) =>
        val fade = new Mesh(new PlaneGeometry(w0, h0),
          Toon.flat(FlatOptions(color = 0x7fae90, transparent = true, opacity = 0.4,
            depthWrite = Some(false), cache = false)))
        fade.position.set(dx + math.signum(dx) * 0.005, 0.4, dz)
        fade.rotation.y = if dx < 0 then -math.Pi / 2.0 else math.Pi / 2.0
        ThreeInterop.setUserDataFlag(fade, "noOutline")
        ThreeInterop.addChild(g, fade)
      }

    val matFor: Map[String, Material[?]] = Map(
      "green" -> green, "deep" -> greenDeep, "cream" -> cream, "metal" -> M.metal, "dark" -> M.dark
    )
    parts.foreach { (key, bucket) =>
      if bucket.nonEmpty then
        val mesh = new Mesh(Util.bake(bucket.toList), matFor(key))
        mesh.castShadow = true
        mesh.receiveShadow = true
        ThreeInterop.addChild(g, mesh)
        if key == "green" then Outline.hullOutline(mesh, OutlineOptions(thickness = 0.0034))
    }

    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g.userData("top") = (H + 0.37).asInstanceOf[js.Any]
    g
  end makePhoneBooth

  private def definePhoneDoor(
    g: Group[Object3DEventMap],
    push: (String, BufferGeometry[?, ?], Matrix4) => Unit,
    W: Double,
    D: Double,
    H: Double,
    SILL: Double,
    TRAN: Double,
    PLINTH: Double,
    POST: Double,
    glassMat: Material[?]
  ): Unit =
    val zf = -(D / 2.0 - 0.035)
    val LW = W - POST * 2.0 - 0.06
    Vector(-1.0, 1.0).foreach { sx =>
      push("green", new BoxGeometry(0.06, TRAN - PLINTH, 0.06),
        Util.trs(sx * LW / 2.0, (TRAN + PLINTH) / 2.0, zf))
    }
    push("green", new BoxGeometry(LW, 0.06, 0.06), Util.trs(0.0, TRAN - 0.03, zf))
    push("deep", new BoxGeometry(LW, SILL - PLINTH - 0.06, 0.055),
      Util.trs(0.0, (SILL + PLINTH) / 2.0 + 0.03, zf))
    push("green", new BoxGeometry(LW + 0.02, 0.07, 0.07), Util.trs(0.0, PLINTH + 0.035, zf))
    push("cream", new BoxGeometry(LW + 0.02, 0.045, 0.07), Util.trs(0.0, SILL + 0.02, zf))
    val leaf = new Mesh(new PlaneGeometry(LW - 0.08, TRAN - SILL - 0.1), glassMat)
    leaf.position.set(0.0, (SILL + TRAN) / 2.0, zf)
    ThreeInterop.setUserDataFlag(leaf, "noOutline")
    ThreeInterop.setUserDataFlag(leaf, "noShadow")
    ThreeInterop.addChild(g, leaf)
    val hop = new Mesh(new PlaneGeometry(LW, H - TRAN - 0.12), glassMat)
    hop.position.set(0.0, (TRAN + 0.07 + H - 0.09) / 2.0, zf)
    ThreeInterop.setUserDataFlag(hop, "noOutline")
    ThreeInterop.setUserDataFlag(hop, "noShadow")
    ThreeInterop.addChild(g, hop)
    Vector(0.72, 1.42, 1.88).foreach { y =>
      push("metal", new CylinderGeometry(0.022, 0.022, 0.07, 6), Util.trs(-LW / 2.0, y, zf - 0.045))
    }
    push("metal", new CylinderGeometry(0.018, 0.018, 0.46, 6), Util.trs(LW / 2.0 - 0.09, 1.24, zf - 0.075))
    Vector(1.02, 1.46).foreach { y =>
      push("metal", new BoxGeometry(0.06, 0.03, 0.06), Util.trs(LW / 2.0 - 0.09, y, zf - 0.055))
    }
  end definePhoneDoor

  private def definePhoneInstrument(
    g: Group[Object3DEventMap],
    push: (String, BufferGeometry[?, ?], Matrix4) => Unit,
    D: Double,
    H: Double
  ): Unit =
    val zb = D / 2.0 - 0.16
    push("cream", new BoxGeometry(0.78, 0.05, 0.28), Util.trs(0.0, 1.02, zb))
    Vector(-1.0, 1.0).foreach { sx =>
      push("metal", new BoxGeometry(0.05, 0.05, 0.26), Util.trs(sx * 0.34, 0.98, zb))
    }
    push("cream", new BoxGeometry(0.34, 0.46, 0.17), Util.trs(0.04, 1.28, zb + 0.02))
    push("dark", new BoxGeometry(0.24, 0.2, 0.03), Util.trs(0.04, 1.24, zb - 0.08))
    (0 until 4).foreach { r =>
      (0 until 3).foreach { k =>
        push("cream", new BoxGeometry(0.05, 0.032, 0.015),
          Util.trs(-0.03 + k * 0.07, 1.31 - r * 0.045, zb - 0.096))
      }
    }
    push("metal", new BoxGeometry(0.1, 0.02, 0.02), Util.trs(0.13, 1.46, zb - 0.09))
    push("dark", new BoxGeometry(0.09, 0.012, 0.02), Util.trs(-0.06, 1.46, zb - 0.09))
    push("metal", new BoxGeometry(0.12, 0.05, 0.03), Util.trs(0.04, 1.09, zb - 0.09))
    push("dark", new BoxGeometry(0.05, 0.04, 0.06), Util.trs(-0.21, 1.45, zb - 0.06))
    push("cream", new BoxGeometry(0.045, 0.2, 0.04), Util.trs(-0.21, 1.29, zb - 0.1))
    Vector(-0.095, 0.095).foreach { dy =>
      push("cream", new BoxGeometry(0.075, 0.06, 0.085), Util.trs(-0.21, 1.29 + dy, zb - 0.1))
    }
    val cord = new Mesh(
      new TubeGeometry(
        new CatmullRomCurve3(Vector(
          new Vector3(-0.21, 1.19, zb - 0.1), new Vector3(-0.19, 1.07, zb - 0.06),
          new Vector3(-0.12, 1.05, zb - 0.09), new Vector3(-0.05, 1.1, zb - 0.04)
        ).toJSArray),
        12, 0.011, 4, false
      ),
      Toon.cel(CelOptions(color = 0x5f5768, bands = 2, tint = 0x453f5c))
    )
    ThreeInterop.addChild(g, cord)
    push("metal", new BoxGeometry(0.6, 0.035, 0.24), Util.trs(0.0, 0.74, zb - 0.02, -0.26, 0.0, 0.0))
    push("metal", new BoxGeometry(0.6, 0.16, 0.03), Util.trs(0.0, 0.68, zb - 0.13))
    Vector((-0.14, 0xd8cdb8), (0.12, 0xc9b89c)).foreach { (dx, _) =>
      push("cream", new BoxGeometry(0.2, 0.07, 0.2), Util.trs(dx, 0.79, zb - 0.02, -0.26, 0.0, 0.0))
      push("dark", new BoxGeometry(0.2, 0.012, 0.2), Util.trs(dx, 0.825, zb - 0.02, -0.26, 0.0, 0.0))
    }
    push("cream", new BoxGeometry(0.5, 0.05, 0.2), Util.trs(0.0, H - 0.13, 0.06))
    ThreeInterop.addChild(g,
      Util.box(0.44, 0.02, 0.16,
        Toon.flat(FlatOptions(color = 0xfff1d4)), 0.0, H - 0.16, 0.06))
  end definePhoneInstrument

  final case class GuideBoardOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    w: Double = 1.5,
    h: Double = 1.05,
    y0: Double = 0.95
  )

  def makeGuideBoard(o: GuideBoardOpts): Group[Object3DEventMap] =
    val g     = new Group()
    val frame = Toon.cel(CelOptions(color = 0x6f7a86, bands = 3, tint = 0x5c5680))
    Vector(-1.0, 1.0).foreach { s =>
      val p = Util.box(0.09, o.y0 + o.h, 0.09, frame, s * (o.w / 2.0 - 0.12), (o.y0 + o.h) / 2.0, 0.0)
      p.castShadow = true
      ThreeInterop.addChild(g, p)
      ThreeInterop.addChild(g, Util.cyl(0.11, 0.13, 0.12, 8, M.concreteMid, s * (o.w / 2.0 - 0.12), 0.06, 0.0))
    }
    val panelMats = Props.matArray(frame, frame, frame, frame,
      Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.guideBoard()), cache = false)),
      frame)
    val panel = new Mesh(new BoxGeometry(o.w, o.h, 0.08), panelMats)
    panel.position.set(0.0, o.y0 + o.h / 2.0, 0.09)
    panel.castShadow = true
    panel.receiveShadow = true
    ThreeInterop.addChild(g, panel)
    Outline.hullOutline(panel, OutlineOptions(thickness = 0.003))
    val hood = Util.box(o.w + 0.16, 0.05, 0.3, frame, 0.0, o.y0 + o.h + 0.11, 0.14)
    hood.rotation.x = -0.16
    ThreeInterop.addChild(g, hood)
    ThreeInterop.addChild(g, Util.box(o.w + 0.06, 0.07, 0.14, frame, 0.0, o.y0 - 0.03, 0.07))
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeGuideBoard

  final case class RecycleBoxOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    w: Double = 0.86,
    d: Double = 0.54,
    h: Double = 0.66,
    color: Int = 0x4a7fae
  )

  def makeRecycleBox(o: RecycleBoxOpts): Group[Object3DEventMap] =
    val g    = new Group()
    val body = Toon.cel(CelOptions(color = o.color, bands = 3, tint = 0x4a4a92))
    val b    = Util.box(o.w, o.h, o.d, body, 0.0, o.h / 2.0, 0.0)
    b.castShadow = true
    b.receiveShadow = true
    ThreeInterop.addChild(g, b)
    Outline.hullOutline(b, OutlineOptions(thickness = 0.0032))
    val lid = Util.box(o.w + 0.05, 0.07, o.d + 0.05,
      Toon.cel(CelOptions(color = 0x3f6f9c, bands = 3, tint = 0x4a4a92)), 0.0, o.h + 0.05, -0.02)
    lid.rotation.x = -0.07
    lid.castShadow = true
    ThreeInterop.addChild(g, lid)
    Vector(-1.0, 1.0).foreach { s =>
      ThreeInterop.addChild(g,
        Util.box(0.09, 0.11, 0.03, M.metalDark,
          s * (o.w / 2.0 - 0.14), o.h - 0.02, o.d / 2.0 + 0.02))
    }
    (0 until 3).foreach { i =>
      ThreeInterop.addChild(g,
        Util.box(o.w + 0.02, 0.025, o.d + 0.02,
          Toon.cel(CelOptions(color = 0x3f6f9c, bands = 2, tint = 0x4a4a92)), 0.0, 0.16 + i * 0.18, 0.0))
    }
    ThreeInterop.addChild(g,
      Util.box(0.42, 0.16, 0.02,
        Toon.flat(FlatOptions(color = 0xf2efe4)), 0.0, o.h * 0.56, o.d / 2.0 + 0.015))
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeRecycleBox

  final case class CratesOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    n: Int = 4,
    seed: Int = 4
  )

  def makeCrates(o: CratesOpts): Group[Object3DEventMap] =
    val rng = Util.rngKit(o.seed)
    val g   = new Group()
    (0 until o.n).foreach { i =>
      val c = Util.box(0.5, 0.28, 0.34,
        Toon.cel(CelOptions(color = rng.pick(Vector(Pal.crate, Pal.crateAlt, 0x4f9d6a)),
          bands = 3, tint = 0x4a4a92)),
        rng.range(-0.05, 0.05), 0.14 + i * 0.28, rng.range(-0.05, 0.05))
      c.rotation.y = rng.range(-0.1, 0.1)
      c.castShadow = true
      c.receiveShadow = true
      ThreeInterop.addChild(g, c)
    }
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeCrates

end Props
