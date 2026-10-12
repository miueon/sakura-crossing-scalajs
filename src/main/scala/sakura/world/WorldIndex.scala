package sakura.world

import sakura.ThreeInterop
import sakura.core.{Pal, Toon, Util}
import vendor.three.*

import scala.collection.mutable

/** Which builder closes a garden boundary. `Mesh` is the concrete-and-mesh default, which
  * reports its own height so the collider does not have to know which kind it got.
  */
enum WallKind:
  case Mesh, Timber, Block

final case class WallSpec(
  x: Double,
  z: Double,
  len: Double,
  h: Double,
  kind: WallKind,
  fence: Option[Boolean] = None
)

final case class PoleDef(
  x: Double,
  z: Double,
  h: Double,
  seed: Int,
  lamp: Boolean = false,
  armDir: Double = 1.0,
  baseSink: Option[Double] = None,
  transformer: Boolean = true
)

final case class PlanterSpot(x: Double, z: Double, r: Double, flower: Boolean, seed: Int)

/** 1:1 port of upstream `src/world/index.js`: the world assembly -- base layers, the 24
  * authored houses, the districts, the planting, the boundary walls, the lineside strip, the
  * utility run and the crossing corner, followed by the projection onto the planet.
  *
  * Placement here is composition work, not simulation: every object is positioned for what it
  * does in the opening frame. Everything above `bakeToPlanet` is still authored on a flat plane
  * and has no idea the planet exists.
  */
object WorldIndex:

  private val RetainingGapMin = -12.9
  private val RetainingGapMax = -10.7
  private val Approach        = 165.0

  private val Clear = 62.0

  /** The assembled world, plus everything a player or a later district needs to reason about it. */
  final case class World(
    root: Group[Object3DEventMap],
    colliders: Vector[Collider],
    platforms: Vector[Platform],
    cuts: Vector[Cut],
    interactables: Vector[Interactable],
    train: Train.TrainApi,
    crossing: Railway.CrossingApi,
    shop: Shop.ShopResult,
    petals: Petals.PetalsApi,
    planet: BuiltPlanet,
    bakeStats: BakeStats,
    private val sequenceUpdate: Double => Unit,
    private val updaters: Vector[Double => Unit]
  ):

    /** Ground height at a flat point.
      *
      * `fromY` is the height the query is made *from* -- the player's current feet -- and it is
      * what makes it possible to walk underneath something you can also walk on top of. Omit it
      * and every platform covering the point counts, which is the answer the builders want.
      */
    def heightAt(x: Double, z: Double, fromY: Option[Double] = None): Double =
      var height = Street.streetHeight(x, z) + Planet.reliefAt(x, z)
      cuts.foreach(box =>
        if x > box.x0 && x < box.x1 && z > box.z0 && z < box.z1 then height = math.min(height, box.top)
      )
      val reach = fromY.fold(Double.PositiveInfinity)(_ + 0.55)
      platforms.foreach(box =>
        if box.top <= reach && x > box.x0 && x < box.x1 && z > box.z0 && z < box.z1 then
          height = math.max(height, box.top)
      )
      height

    def update(dt: Double): Unit =
      sequenceUpdate(dt)
      train.update(dt)
      updaters.foreach(apply => apply(dt))
      petals.update(dt, train.gust, train.dir)
  end World

  /** Builds the whole world into `scene`. `extraDistricts` is the seam the remaining chunks plug
    * into: a builder that also hands back the planting it wants planted once, at the end.
    */
  def buildWorld(
    scene: Scene[Object3DEventMap],
    extraDistricts: WorldCtx => Seq[DistrictResult] = _ => Seq.empty
  ): World =
    val root = new Group()
    root.name = "world"
    ThreeInterop.addChild(scene, root)
    val ctx = new WorldCtx(scene, root)

    val planet = Planet.buildPlanet(scene)
    Street.buildStreet(ctx)
    val crossing = Railway.buildRailway(ctx)
    val train    = Train.buildTrain(ctx)
    val shop     = Shop.buildShop(ctx)

    houseDefs.foreach { plot =>
      val y = Street.groundY(plot.z)
      ctx.add(Buildings.house(houseOptions(plot, y)))
      ctx.collide(plot.x - plot.w / 2.0 - 0.1, plot.z - plot.d / 2.0 - 0.1,
        plot.x + plot.w / 2.0 + 0.1, plot.z + plot.d / 2.0 + 0.1, y + 2.72 * plot.floors)
    }

    val extras = extraDistricts(ctx)
    val plantings = Vector(
      Shrine.buildShrine(ctx),
      Shotengai.buildShotengai(ctx),
      RestCorner.buildRestCorner(ctx),
      Library.buildLibrary(ctx),
      NorthBlock.buildNorthBlock(ctx),
      Matsuri.buildMatsuri(ctx),
      Onsen.buildOnsen(ctx),
      Alleys.buildAlleys(ctx),
      District.buildDistrict(ctx, houseDefs),
      /* The motor vehicles run after every surface in the world is laid, because a car is seated
       * with `ctx.groundAt` and half of them stand on a lane, an apron or a bay that a district
       * module put down -- and after the housing sweep, so it parks between the clutter it sees. */
      Traffic.buildTraffic(ctx),
      Details.buildDetails(ctx).planting
    ) ++ extras.map(_.planting)
    extras.foreach(_.update.foreach(ctx.update))

    Trees.buildGrove(ctx, plantings.flatMap(_.grove))
    Trees.buildCedar(ctx, plantings.flatMap(_.cedar))
    Trees.buildBamboo(ctx, plantings.flatMap(_.bamboo))
    Petals.buildFallenPatches(ctx, plantings.flatMap(_.petals))

    dressWalls(ctx)
    buildRetainingWall(ctx)
    buildLinesideStrip(ctx)

    Trees.buildSakura(ctx,
      baseSakuraSpots.map(spot => spot.copy(y = Street.groundY(spot.z))) ++
        plantings.flatMap(_.sakura))
    Trees.buildShrubs(ctx, baseShrubSpots ++ plantings.flatMap(_.shrubs))

    val poles = erectPoles(ctx)
    stringCables(ctx, poles)
    buildCrossingCorner(ctx)

    val petals    = Petals.buildPetals(ctx)
    val sequence  = crossingSequence(ctx, train, crossing)
    val bakeStats = Planet.bakeToPlanet(root, maxEdge = 4.0)
    train.planetize()

    World(
      root = root,
      colliders = ctx.colliders.toVector,
      platforms = ctx.platforms.toVector,
      cuts = ctx.cuts.toVector,
      interactables = ctx.interactables.toVector,
      train = train,
      crossing = crossing,
      shop = shop,
      petals = petals,
      planet = planet,
      bakeStats = bakeStats,
      sequenceUpdate = sequence,
      updaters = ctx.updaters.toVector
    )
  end buildWorld

  private def houseOptions(plot: HouseDef, y: Double): Buildings.HouseOpts =
    Buildings.HouseOpts(
      x = plot.x,
      z = plot.z,
      y = y,
      w = plot.w,
      d = plot.d,
      face = plot.face,
      floors = plot.floors,
      seed = plot.seed,
      wall = Some(plot.wall),
      roof = Some(plot.roof),
      roofKind = Some(plot.roofKind)
    )

  private def dressWalls(ctx: WorldCtx): Unit =
    wallSpecs.foreach { spec =>
      val y = Street.groundY(spec.z)
      val boundary = spec.kind match
        case WallKind.Timber =>
          Buildings.timberFence(Buildings.FenceOpts(x = spec.x, y = y, z = spec.z, len = spec.len,
            axis = Buildings.Axis.Z, h = spec.h))
        case WallKind.Block =>
          Buildings.blockFence(Buildings.FenceOpts(x = spec.x, y = y, z = spec.z, len = spec.len,
            axis = Buildings.Axis.Z, h = spec.h))
        case WallKind.Mesh =>
          Buildings.wall(Buildings.WallOpts(x = spec.x, y = y, z = spec.z, len = spec.len,
            axis = Buildings.Axis.Z, h = spec.h, fence = spec.fence.getOrElse(false)))
      ctx.add(boundary)
      val half = spec.len / 2.0
      ctx.collide(spec.x - 0.2, spec.z - half, spec.x + 0.2, spec.z + half,
        y + ThreeInterop.userDataNumber(boundary, "top"))
    }

  /** Two runs with a gap between them: those two metres are the only way from the back land onto
    * the street on this side, so each run ends in a pier instead of stopping in mid-air.
    */
  private def buildRetainingWall(ctx: WorldCtx): Unit =
    val mat    = Toon.cel(Toon.CelOptions(color = Pal.concreteMid, bands = 3, tint = 0x6a6288))
    val capMat = Toon.cel(Toon.CelOptions(color = Pal.concrete, bands = 3, tint = 0x6f6790))
    val h      = 1.1
    Vector((-19.0, RetainingGapMin), (RetainingGapMax, -9.0)).foreach { case (z0, z1) =>
      val zc   = (z0 + z1) / 2.0
      val len  = z1 - z0
      val x    = Street.centerX(zc) - (Street.RoadHalf + Street.WalkW + 0.3)
      val yc   = Street.groundY(zc)
      val body = Util.box(0.4, h, len, mat, x, yc + h / 2.0 - 0.2, zc)
      body.castShadow = true
      body.receiveShadow = true
      ctx.add(body)
      ctx.add(Util.box(0.56, 0.12, len, capMat, x, yc + h - 0.14, zc))
      ctx.collide(x - 0.3, z0, x + 0.3, z1, yc + h)
      val pz   = if z1 == RetainingGapMin then z1 - 0.24 else z0 + 0.24
      val py   = Street.groundY(pz)
      val pier = Util.box(0.62, h + 0.24, 0.48, mat, x, py + (h + 0.24) / 2.0 - 0.2, pz)
      pier.castShadow = true
      pier.receiveShadow = true
      ctx.add(pier)
      ctx.add(Util.box(0.74, 0.1, 0.6, capMat, x, py + h + 0.02, pz))
      ctx.collide(x - 0.36, pz - 0.28, x + 0.36, pz + 0.28, py + h + 0.14)
    }
  end buildRetainingWall

  /** With the frontage held back there is an open corridor between the street and the railway
    * fence; it gets a footpath, an allotment and a shed, all low enough to keep the sight line
    * down the track clear.
    */
  private def buildLinesideStrip(ctx: WorldCtx): Unit =
    val pathMat = Toon.cel(Toon.CelOptions(color = Pal.concrete, bands = 3, tint = 0x6f6790))
    val path    = Util.box(26.0, 0.07, 1.15, pathMat, -17.5, 0.035, 4.35)
    path.receiveShadow = true
    ctx.add(path)
    val seamMat = Toon.cel(Toon.CelOptions(color = Pal.concreteMid, bands = 3, tint = 0x6a6288))
    (0 until 9).foreach(index => ctx.add(Util.box(0.06, 0.09, 1.15, seamMat, -5.0 - index * 3.0, 0.04, 4.35)))

    val shed     = new Group()
    val shedWall = Toon.cel(Toon.CelOptions(color = 0xd9d3c4, bands = 3, tint = 0x6f6790))
    val shedRoof = Toon.cel(Toon.CelOptions(color = Pal.roofTeal, bands = 3, tint = 0x514b70))
    ThreeInterop.addChild(shed, Util.box(2.6, 2.05, 2.0, shedWall, 0.0, 1.02, 0.0))
    ThreeInterop.addChild(shed, Util.box(2.9, 0.12, 2.3, shedRoof, 0.0, 2.12, 0.0))
    ThreeInterop.addChild(shed,
      Util.box(0.06, 1.55, 0.9,
        Toon.cel(Toon.CelOptions(color = 0x8a6f5c, bands = 3, tint = 0x5c5680)), 1.31, 0.8, 0.3))
    ThreeInterop.addChild(shed,
      Util.box(2.62, 0.09, 0.09,
        Toon.cel(Toon.CelOptions(color = Pal.metal, bands = 3)), 0.0, 2.02, 1.02))
    shed.position.set(-11.9, 0.0, 7.0)
    shed.rotation.y = -0.06
    Util.shadowify(shed)
    ctx.add(shed)
    ctx.collide(-13.3, 5.95, -10.5, 8.05, 2.1)

    ctx.add(Props.makeCrates(Props.CratesOpts(x = -9.6, z = 5.0, y = 0.0, n = 3, seed = 71, ry = 0.2)))
    ctx.add(Props.makePlanter(Props.PlanterOpts(x = -7.4, z = 4.9, y = 0.0, r = 0.24, flower = true,
      seed = 72, n = 5)))
    ctx.add(Props.makePlanter(Props.PlanterOpts(x = -8.3, z = 5.5, y = 0.0, r = 0.2, flower = false,
      seed = 73, n = 4)))
    ctx.add(Props.makeBicycle(Props.BicycleOpts(x = -13.9, z = 4.9, y = 0.0, ry = -0.1, lean = 0.06,
      color = 0x8f6fb5)))
  end buildLinesideStrip

  final private case class ErectedPole(x: Double, z: Double, top: Double)

  private def erectPoles(ctx: WorldCtx): Vector[ErectedPole] =
    poleDefs.map { def_ =>
      val onFootway = math.abs(def_.x - Street.centerX(def_.z)) < Street.RoadHalf + Street.WalkW
      val y = Street.groundY(def_.z) + (if onFootway then Street.WalkH else 0.0) -
        def_.baseSink.getOrElse(0.0)
      ctx.add(Props.makePole(Props.PoleOpts(x = def_.x, y = y, z = def_.z, h = def_.h, seed = def_.seed,
        lamp = def_.lamp, armDir = def_.armDir, transformer = def_.transformer)))
      ctx.collide(def_.x - 0.22, def_.z - 0.22, def_.x + 0.22, def_.z + 0.22, y + def_.h)
      ErectedPole(def_.x, def_.z, y + def_.h)
    }

  private def stringCables(ctx: WorldCtx, poles: Vector[ErectedPole]): Unit =
    val runs = mutable.ArrayBuffer.empty[Props.WireRun]

    def at(index: Int, dy: Double, dz: Double): Vector3 =
      new Vector3(poles(index).x, poles(index).top - 0.6 + dy, poles(index).z + dz)

    def chain(indices: Vector[Int], offsets: Vector[(Double, Double)]): Unit =
      offsets.foreach { case (dy, dz) =>
        runs += Props.WireRun(points = indices.map(index => at(index, dy, dz)), sag = 0.55)
      }

    chain(Vector(0, 1, 2, 3), Vector((0.0, -0.7), (-0.42, 0.0), (-0.86, 0.7)))
    chain(Vector(4, 5, 6), Vector((0.0, -0.7), (-0.42, 0.0), (-0.86, 0.7)))
    chain(Vector(7, 8), Vector((0.0, -0.6), (-0.45, 0.6)))
    chain(Vector(0, 4), Vector((-0.15, 0.2), (-0.62, -0.3)))
    chain(Vector(1, 7), Vector((-0.2, 0.3), (-0.7, -0.4)))
    chain(Vector(2, 7), Vector((-1.3, 0.9)))
    chain(Vector(9, 4), Vector((-1.0, 0.2)))
    chain(Vector(10, 1), Vector((-1.1, 0.4)))

    val drops = Vector(
      (0, new Vector3(-11.0, 2.35, 5.3)), (1, new Vector3(-6.0, Street.groundY(23.2) + 5.4, 21.0)),
      (7, new Vector3(5.9, Street.groundY(18.4) + 5.0, 17.0)), (4, new Vector3(5.6, Street.groundY(-10.5) + 5.2, -9.0)),
      (2, new Vector3(-6.6, Street.groundY(33.5) + 3.4, 32.0))
    )
    drops.foreach { case (index, target) =>
      runs += Props.WireRun(points = Vector(at(index, -1.9, 0.0), target), sag = 0.25, r = 0.022)
    }

    ctx.add(Props.makeWires(propCtx(ctx), runs.toVector))
  end stringCables

  private def propCtx(ctx: WorldCtx): PropCtx =
    PropCtx(add = obj => ctx.add(obj),
      collide = (x0, z0, x1, z1, top) => ctx.collide(x0, z0, x1, z1, top),
      groundAt = ctx.groundAt)

  /** The clutter that makes the corner read as a place where people live: the mirror watching
    * the crossing, the post box, the broken kerb, the truck, the bicycles, the planters, and the
    * cat that stretches when you say hello.
    */
  private def buildCrossingCorner(ctx: WorldCtx): Unit =
    def walkY(z: Double): Double = Street.groundY(z) + Street.WalkH

    ctx.add(Props.makeMirror(Props.MirrorOpts(x = -3.62, z = 3.72, y = walkY(3.72), ry = 2.5)))

    ctx.add(Props.makeShrine(Props.ShrineOpts(x = -5.45, z = 6.7, y = walkY(6.7), ry = 1.15)))
    ctx.collide(-6.0, 6.2, -4.9, 7.2, walkY(6.7) + 1.4)

    ctx.add(Props.makePostBox(Props.PostBoxOpts(x = 3.62, z = 4.55, y = walkY(4.55), ry = -1.4)))
    ctx.collide(3.3, 4.25, 3.95, 4.85, walkY(4.55) + 1.3)

    ctx.add(Props.makeCone(Props.ConeOpts(x = 2.62, z = 4.15, y = Street.groundY(4.15), ry = 0.4)))
    ctx.add(Props.makeCone(Props.ConeOpts(x = 2.42, z = 5.05, y = Street.groundY(5.05), ry = -0.7,
      tilt = 0.06)))
    ctx.add(Props.makeBarrier(Props.BarrierOpts(x = 2.62, z = 6.2, y = Street.groundY(6.2), ry = 0.06,
      len = 1.7)))
    ctx.add(Props.makeCone(Props.ConeOpts(x = -2.3, z = -4.3, y = Street.groundY(-4.3), ry = 0.2)))

    ctx.add(Props.makeKeiTruck(Props.KeiTruckOpts(x = -2.02, z = -7.4, y = Street.groundY(-7.4),
      ry = math.Pi / 2.0)))
    ctx.collide(-3.0, -9.2, -1.05, -5.6, Street.groundY(-7.4) + 1.9)

    ctx.add(Props.makeBicycle(Props.BicycleOpts(x = -4.3, z = 8.4, y = walkY(8.4),
      ry = math.Pi / 2.0 + 0.08, lean = -0.10, color = 0x3f6f9c)))
    ctx.add(Props.makeBicycle(Props.BicycleOpts(x = -4.3, z = 12.2, y = walkY(12.2),
      ry = -math.Pi / 2.0 + 0.06, lean = 0.08, color = 0xd8a03c)))
    ctx.add(Props.makeBicycle(Props.BicycleOpts(x = 4.3, z = 13.4, y = walkY(13.4),
      ry = math.Pi / 2.0 + 0.06, lean = 0.08, color = 0x9c5a4a)))
    ctx.add(Props.makeBicycle(Props.BicycleOpts(x = 5.7, z = 15.7, y = walkY(15.7),
      ry = -math.Pi / 2.0 - 0.14, lean = 0.07, color = 0x4f8f6a)))

    ctx.add(Props.makeUmbrella(Props.UmbrellaOpts(x = -4.62, z = 9.9, y = walkY(9.9), ry = 0.5)))
    ctx.add(Props.makeBins(Props.BinsOpts(x = -4.65, z = 15.0, y = walkY(15.0) - 0.02,
      ry = -math.Pi / 2.0)))
    ctx.add(Props.makeCrates(Props.CratesOpts(x = 5.55, z = 22.0, y = walkY(22.0), n = 3, seed = 41)))

    ctx.add(Props.makeGuardrail(Props.GuardrailOpts(x = 4.5, z = 15.4, y = walkY(15.4),
      ry = math.Pi / 2.0, len = 4.2)))
    ctx.add(Props.makeGuardrail(Props.GuardrailOpts(x = Street.centerX(-16.4) - Street.RoadHalf - 0.25,
      z = -16.4, y = Street.groundY(-16.4), ry = math.Pi / 2.0, len = 6.2)))

    planterSpots.foreach { spot =>
      ctx.add(Props.makePlanter(Props.PlanterOpts(x = spot.x, z = spot.z, y = walkY(spot.z),
        r = spot.r, flower = spot.flower, seed = spot.seed, n = 4)))
    }

    val catWall = Buildings.wall(Buildings.WallOpts(x = -5.05, z = 8.9, y = Street.groundY(8.9),
      len = 3.4, axis = Buildings.Axis.Z, h = 0.62, fence = false))
    ctx.add(catWall)
    ctx.collide(-5.3, 7.2, -4.8, 10.6, Street.groundY(8.9) + 0.7)
    buildCat(ctx)
  end buildCrossingCorner

  /** The cat sits on its own stretch of wall, and stretches when the player says hello. */
  private def buildCat(ctx: WorldCtx): Unit =
    val catX    = -5.05
    val groundY = Street.groundY(8.3)
    val cat     = Props.makeCat(Props.CatOpts(x = catX, z = 8.3, y = groundY + 0.7, ry = 1.35))
    ctx.add(cat)
    val hit = Util.box(0.8, 0.9, 0.9, Toon.flat(Toon.FlatOptions(color = 0xff0000, cache = false)),
      catX, groundY + 1.05, 8.3)
    hit.visible = false
    ctx.add(hit)

    val stretch = new StretchState
    ctx.interact(hit, "ねこ  ·  say hello", () => stretch.sayHello())

    val head = ThreeInterop.userDataObject(cat, "head")
    val tail = ThreeInterop.userDataObject(cat, "tail")
    val body = ThreeInterop.userDataObject(cat, "body")
    ctx.update(dt => stretch.step(dt, head, tail, body))

  final private class StretchState:
    private var stretch = 0.0
    private var target  = 0.0
    private var elapsed = 0.0

    def sayHello(): Unit = target = 1.0

    def step(dt: Double, head: Option[Object3D[?]], tail: Option[Object3D[?]], body: Option[Object3D[?]]): Unit =
      elapsed += dt
      stretch += (target - stretch) * (1.0 - math.exp(-dt * 3.5))
      if stretch > 0.94 then target = 0.0
      head.foreach { node =>
        node.position.y = 0.38 + math.sin(elapsed * 1.6) * 0.006 + stretch * 0.07
        node.rotation.z = stretch * 0.3
        node.rotation.x = -stretch * 0.25
      }
      tail.foreach(node => node.rotation.y = math.sin(elapsed * 0.9) * 0.22 + stretch * 0.5)
      body.foreach(node => node.scale.y = 0.92 + math.sin(elapsed * 1.6) * 0.008)

  /** The train never stops -- it circles the planet forever -- so the gates are driven by where
    * it actually is on the ring rather than by a timer. One lap is one crossing cycle, for free.
    */
  private def crossingSequence(ctx: WorldCtx, train: Train.TrainApi, crossing: Railway.CrossingApi): Double => Unit =
    crossing.request = () => train.x = Planet.wrapX(-(Approach - 12.0) * train.dir)

    val boomBlocks = Vector(1.0, -1.0).map { side =>
      ctx.collide(Street.centerX(0.0) - Street.RoadHalf - 0.6, side * Street.GateZ - 0.16,
        Street.centerX(0.0) + Street.RoadHalf + 0.6, side * Street.GateZ + 0.16, -1.0)
      ctx.colliders(ctx.colliders.length - 1)
    }

    var blink = 0.0
    var armT  = 0.0
    dt =>
      blink = (blink + dt * 1.6) % 1.0
      val ahead   = -train.offset * train.dir
      val closing = ahead < Approach && ahead > -Clear
      val rate    = dt / (if closing then 3.4 else 3.0)
      val step    = if closing then rate else -rate
      armT = math.max(0.0, math.min(1.0, armT + step))
      crossing.setArms(armT)
      crossing.setLamps(closing || armT > 0.02, blink)
      val down = if armT > 0.55 then 1.25 else -1.0
      boomBlocks.foreach(block => block.top = down)
  end crossingSequence

  /** The 24 plots `index.js` authors with `makeHouse`. The dressing fields are carried because
    * `dressHousing` reads them off the same definitions when the housing sweep runs. */
  val houseDefs: Vector[HouseDef] = Vector(
    HouseDef(x = -13.6, z = 12.6, w = 7, d = 7, face = Buildings.Face.XPlus, seed = 21, wall = 0, roof = 1,
      roofKind = Buildings.RoofKind.Gable, floors = 2),
    HouseDef(x = -9.4, z = 23.2, w = 7, d = 8, face = Buildings.Face.XPlus, seed = 22, wall = 2, roof = 0,
      roofKind = Buildings.RoofKind.Hip, floors = 2, postBoxOut = Some(0.55), postBoxSink = Some(0.02),
      gomiSink = Some(0.02)),
    HouseDef(x = -10.6, z = 33.5, w = 6.4, d = 7.2, face = Buildings.Face.XPlus, seed = 23, wall = 1, roof = 2,
      roofKind = Buildings.RoofKind.Gable, floors = 1, planterAlong = Some(-1.6), planterOut = Some(0.17),
      planterSpacing = Some(0.52), planterSink = Some(0.025), umbrellaSink = Some(0.02)),
    HouseDef(x = -13.5, z = 43, w = 7.4, d = 7.4, face = Buildings.Face.XPlus, seed = 24, wall = 4, roof = 1,
      roofKind = Buildings.RoofKind.Flat, floors = 2),
    HouseDef(x = 9.4, z = 20.4, w = 5.6, d = 7.8, face = Buildings.Face.XMinus, seed = 25, wall = 3, roof = 0,
      roofKind = Buildings.RoofKind.Gable, floors = 2, postBoxAlong = Some(-0.6), gomiOut = Some(-0.2),
      gomiSignAlong = Some(-1.6)),
    HouseDef(x = 8.9, z = 29.6, w = 6.6, d = 7.4, face = Buildings.Face.XMinus, seed = 26, wall = 5, roof = 3,
      roofKind = Buildings.RoofKind.Hip, floors = 2, postBoxAlong = Some(-0.2), bikeOut = Some(0.85)),
    HouseDef(x = 7.4, z = 38.5, w = 6.8, d = 7, face = Buildings.Face.XMinus, seed = 27, wall = 0, roof = 2,
      roofKind = Buildings.RoofKind.Gable, floors = 1, planterAlong = Some(1.2)),
    HouseDef(x = -8.7, z = -7.6, w = 6.8, d = 6.2, face = Buildings.Face.XPlus, seed = 28, wall = 1, roof = 0,
      roofKind = Buildings.RoofKind.Gable, floors = 2, postBoxAlong = Some(-0.15)),
    HouseDef(x = -8.2, z = -16.4, w = 6.6, d = 7, face = Buildings.Face.XPlus, seed = 29, wall = 4, roof = 1,
      roofKind = Buildings.RoofKind.Hip, floors = 2, postBoxOut = Some(0.35), planterAlong = Some(-2.8),
      planterOut = Some(0.3)),
    HouseDef(x = -15.2, z = -34.6, w = 7.4, d = 7, face = Buildings.Face.ZPlus, seed = 30, wall = 2, roof = 2,
      roofKind = Buildings.RoofKind.Gable, floors = 2),
    HouseDef(x = -5.4, z = -33.5, w = 8, d = 7.4, face = Buildings.Face.ZPlus, seed = 44, wall = 0, roof = 1,
      roofKind = Buildings.RoofKind.Gable, floors = 2, bikeLift = Some(0.03)),
    HouseDef(x = 8.5, z = -7.4, w = 6.6, d = 6, face = Buildings.Face.XMinus, seed = 31, wall = 0, roof = 3,
      roofKind = Buildings.RoofKind.Gable, floors = 1),
    HouseDef(x = 10, z = -16.2, w = 6.8, d = 7.2, face = Buildings.Face.XMinus, seed = 32, wall = 3, roof = 1,
      roofKind = Buildings.RoofKind.Hip, floors = 2),
    HouseDef(x = 14.4, z = -35.5, w = 7, d = 7.4, face = Buildings.Face.XMinus, seed = 45, wall = 1, roof = 3,
      roofKind = Buildings.RoofKind.Hip, floors = 1, skipBike = true),
    HouseDef(x = -18, z = -13, w = 8, d = 8, face = Buildings.Face.ZPlus, seed = 34, wall = 1, roof = 1,
      roofKind = Buildings.RoofKind.Gable, floors = 2),
    HouseDef(x = -27.5, z = -12, w = 7.6, d = 7.6, face = Buildings.Face.ZPlus, seed = 35, wall = 4, roof = 0,
      roofKind = Buildings.RoofKind.Hip, floors = 1),
    HouseDef(x = -37, z = -14.5, w = 8.4, d = 8, face = Buildings.Face.ZPlus, seed = 36, wall = 0, roof = 2,
      roofKind = Buildings.RoofKind.Gable, floors = 2),
    HouseDef(x = 21, z = -14, w = 8.2, d = 8, face = Buildings.Face.ZPlus, seed = 37, wall = 2, roof = 0,
      roofKind = Buildings.RoofKind.Hip, floors = 2),
    HouseDef(x = 31, z = -13, w = 7.8, d = 7.6, face = Buildings.Face.ZPlus, seed = 38, wall = 5, roof = 1,
      roofKind = Buildings.RoofKind.Gable, floors = 1),
    HouseDef(x = 41.5, z = -15, w = 8.6, d = 8.2, face = Buildings.Face.ZPlus, seed = 39, wall = 3, roof = 2,
      roofKind = Buildings.RoofKind.Flat, floors = 2),
    HouseDef(x = -22, z = 9.5, w = 8, d = 7.6, face = Buildings.Face.ZMinus, seed = 40, wall = 0, roof = 1,
      roofKind = Buildings.RoofKind.Gable, floors = 2),
    HouseDef(x = -33.6, z = 10.5, w = 7.6, d = 7.4, face = Buildings.Face.ZMinus, seed = 41, wall = 4, roof = 3,
      roofKind = Buildings.RoofKind.Hip, floors = 1),
    HouseDef(x = 21.2, z = 10, w = 5.8, d = 7.8, face = Buildings.Face.ZMinus, seed = 42, wall = 1, roof = 0,
      roofKind = Buildings.RoofKind.Gable, floors = 2),
    HouseDef(x = 30.5, z = 11, w = 7.8, d = 7.4, face = Buildings.Face.ZMinus, seed = 43, wall = 5, roof = 2,
      roofKind = Buildings.RoofKind.Hip, floors = 2),
  )

  /** Garden boundaries: three fence types scattered rather than alternated. */
  val wallSpecs: Vector[WallSpec] = Vector(
    WallSpec(x = -9.9, z = 12.6, len = 7.2, h = 0.72, kind = WallKind.Block),
    WallSpec(x = -5.1, z = 15.2, len = 5, h = 0.5, kind = WallKind.Mesh, fence = Some(true)),
    WallSpec(x = -5.3, z = 23.2, len = 8.2, h = 0.4, kind = WallKind.Timber),
    WallSpec(x = 6.4, z = 20.4, len = 8, h = 0.66, kind = WallKind.Block),
    WallSpec(x = 5.3, z = 29.6, len = 7.6, h = 0.4, kind = WallKind.Timber),
    WallSpec(x = -4.9, z = -16.4, len = 7.2, h = 0.7, kind = WallKind.Mesh, fence = Some(false)),
    WallSpec(x = 5.4, z = -7.4, len = 6.2, h = 0.5, kind = WallKind.Mesh, fence = Some(true)),
  )

  /** Utility poles. `y` is resolved against the footway the pole stands on. */
  val poleDefs: Vector[PoleDef] = Vector(
    PoleDef(x = -3.86, z = 4.55, h = 9.4, seed = 301, lamp = true, armDir = 1),
    PoleDef(x = -4.35, z = 14.2, h = 9, seed = 302, armDir = 1, baseSink = Some(0.02)),
    PoleDef(x = -4.85, z = 24.6, h = 9.2, seed = 303, lamp = true, armDir = 1),
    PoleDef(x = -6.6, z = 35, h = 8.8, seed = 304, armDir = 1, baseSink = Some(0.04)),
    PoleDef(x = 3.98, z = -6.6, h = 9.2, seed = 305, lamp = true, armDir = -1),
    PoleDef(x = 6.35, z = -16.5, h = 9, seed = 306, armDir = -1),
    PoleDef(x = 8.4, z = -31.6, h = 8.8, seed = 307, lamp = true, armDir = -1),
    PoleDef(x = 4.35, z = 20.8, h = 9, seed = 308, armDir = -1),
    PoleDef(x = 4.5, z = 34, h = 8.6, seed = 309, lamp = true, armDir = -1),
    PoleDef(x = 12.5, z = 3.7, h = 8.4, seed = 310, armDir = -1, transformer = false),
    PoleDef(x = -16.5, z = -4.4, h = 8.6, seed = 311, armDir = 1, transformer = false),
  )

  /** The small planters that make a street feel inhabited; `seed` is `300 + index`. */
  val planterSpots: Vector[PlanterSpot] = Vector(
    PlanterSpot(x = -4.6, z = 7.9, r = 0.22, flower = true, seed = 300),
    PlanterSpot(x = -4.5, z = 8.6, r = 0.18, flower = false, seed = 301),
    PlanterSpot(x = -4.7, z = 12.6, r = 0.2, flower = true, seed = 302),
    PlanterSpot(x = 4.45, z = 14.5, r = 0.19, flower = false, seed = 303),
    PlanterSpot(x = -4.5, z = 22, r = 0.21, flower = true, seed = 304),
    PlanterSpot(x = 4.4, z = 24.5, r = 0.2, flower = true, seed = 305),
    PlanterSpot(x = -4.6, z = -8.5, r = 0.22, flower = false, seed = 306),
    PlanterSpot(x = 5.2, z = -7.4, r = 0.2, flower = true, seed = 307),
    PlanterSpot(x = 4.5, z = 30, r = 0.19, flower = true, seed = 308),
  )

  /** The 29 authored cherries. District plantings are appended to these before the one
    * merged blossom mesh is built, so this list is the base, not the whole orchard. */
  val baseSakuraSpots: Vector[SakuraSpot] = Vector(
    SakuraSpot(x = -7.1, z = 5.8, scale = 1.22, seed = 101, lean = 0.13, leanDir = Some(1.9)),
    SakuraSpot(x = 6.3, z = 2.5, scale = 1.06, seed = 102, lean = 0.1, leanDir = Some(4.4)),
    SakuraSpot(x = -5.9, z = 17.9, scale = 1.16, seed = 128, lean = 0.14, leanDir = Some(1.6)),
    SakuraSpot(x = -5.6, z = 25.4, scale = 1.04, seed = 129, lean = 0.1, leanDir = Some(2.1)),
    SakuraSpot(x = -12.5, z = 3.9, scale = 1, seed = 103, lean = 0.08),
    SakuraSpot(x = -19, z = 3.9, scale = 1.12, seed = 104, lean = 0.1),
    SakuraSpot(x = -26.5, z = 4, scale = 0.95, seed = 105, lean = 0.06),
    SakuraSpot(x = -34, z = 3.9, scale = 1.08, seed = 106, lean = 0.09),
    SakuraSpot(x = 13.5, z = 3.9, scale = 1.05, seed = 107, lean = 0.07),
    SakuraSpot(x = 20.5, z = 4, scale = 0.98, seed = 108, lean = 0.1),
    SakuraSpot(x = 28, z = 3.9, scale = 1.14, seed = 109, lean = 0.05),
    SakuraSpot(x = 32.5, z = 4, scale = 1, seed = 110, lean = 0.09),
    SakuraSpot(x = -11, z = -4.6, scale = 1.04, seed = 111, lean = 0.08),
    SakuraSpot(x = -18.5, z = -4.6, scale = 0.96, seed = 112, lean = 0.1),
    SakuraSpot(x = 11.5, z = -4.7, scale = 1.1, seed = 113, lean = 0.07),
    SakuraSpot(x = 18.5, z = -7.4, scale = 1.02, seed = 114, lean = 0.06),
    SakuraSpot(x = 26.5, z = -7.8, scale = 1.16, seed = 115, lean = 0.09),
    SakuraSpot(x = 30, z = -6.9, scale = 0.98, seed = 116, lean = 0.05),
    SakuraSpot(x = -13.5, z = 15.5, scale = 1.1, seed = 117, lean = 0.11),
    SakuraSpot(x = 5.9, z = 25, scale = 1.05, seed = 118, lean = 0.08),
    SakuraSpot(x = -14, z = 30, scale = 1, seed = 119, lean = 0.07),
    SakuraSpot(x = -12, z = -14.5, scale = 1.08, seed = 120, lean = 0.1),
    SakuraSpot(x = 15, z = -20, scale = 1.03, seed = 121, lean = 0.06),
    SakuraSpot(x = -44, z = 4.5, scale = 1.2, seed = 122, lean = 0.08),
    SakuraSpot(x = 46, z = 4.2, scale = 1.15, seed = 123, lean = 0.06),
    SakuraSpot(x = -42, z = -4.9, scale = 1.1, seed = 124, lean = 0.09),
    SakuraSpot(x = 51, z = -9, scale = 1.18, seed = 125, lean = 0.07),
    SakuraSpot(x = -52, z = 6, scale = 1.25, seed = 126, lean = 0.05),
    SakuraSpot(x = 54, z = 5.5, scale = 1.22, seed = 127, lean = 0.08),
  )

  /** The 12 authored shrub clumps. */
  val baseShrubSpots: Vector[ShrubSpot] = Vector(
    ShrubSpot(x = -6.4, z = 6.6, y = 0, r = 0.5, count = 4, spread = 1.2, seed = 201),
    ShrubSpot(x = -5.7, z = 12.4, y = 0, r = 0.45, count = 3, spread = 1, seed = 202),
    ShrubSpot(x = -15.5, z = 4.6, y = 0, r = 0.5, count = 4, spread = 1.6, seed = 211),
    ShrubSpot(x = -21.5, z = 4.5, y = 0, r = 0.5, count = 4, spread = 1.6, seed = 212),
    ShrubSpot(x = 5.6, z = 15.6, y = 0, r = 0.5, count = 4, spread = 1.4, seed = 203),
    ShrubSpot(x = 5.4, z = 31.5, y = 0.03652031249999999, r = 0.48, count = 3, spread = 1.2, seed = 204),
    ShrubSpot(x = -5.4, z = -13.4, y = 0.0013765271905525614, r = 0.55, count = 5, spread = 1.6, seed = 205),
    ShrubSpot(x = 6.6, z = -6, y = 0, r = 0.5, count = 4, spread = 1.4, seed = 206),
    ShrubSpot(x = 16.5, z = -9.5, y = 0, r = 0.6, count = 5, spread = 2, seed = 207),
    ShrubSpot(x = 30, z = -9, y = 0, r = 0.6, count = 5, spread = 2.2, seed = 208),
    ShrubSpot(x = -16, z = 6, y = 0, r = 0.55, count = 4, spread = 1.8, seed = 209),
    ShrubSpot(x = 24, z = 6.5, y = 0, r = 0.55, count = 4, spread = 1.8, seed = 210),
  )

  /** Builds the whole world into `scene`. `extraDistricts` is the seam the remaining chunks plug
    * into: a builder that also hands back the planting it wants planted once, at the end.
    */
end WorldIndex
