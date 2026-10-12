package sakura.world

import Buildings.Axis
import sakura.ThreeInterop
import sakura.core.Textures
import sakura.core.Textures.RoadPaintKind
import sakura.core.Toon
import sakura.core.Toon.FlatOptions
import vendor.three.*

import scala.collection.mutable

/** 1:1 port of upstream `src/world/traffic.js`: the vehicles standing in the street, the tyre marks
  * under each one, the 駐停車禁止 yellow lines, the 徐行 markings, the bus stop, the delineators and
  * the cones.
  */
object Traffic:

  /** A car's centre sits `Kerb` from the road centreline, which leaves 4.4 m of the 6.3 m
    * carriageway; the minibus is 2.08 across and so sits further out.
    */
  private val Kerb = 2.05
  private val Pi   = math.Pi

  private def east(z: Double, k: Double = Kerb): Double = Street.centerX(z) + k
  private def west(z: Double, k: Double = Kerb): Double = Street.centerX(z) - k

  /** One row of the parked table. `ry` is where the nose points; `y` is given only by the four
    * vehicles that are not seated on `ctx.groundAt` -- the supermarket's roof deck, and the 私道's
    * marked bay under the overbridge, where `heightAt` answers with the bridge deck because a
    * platform covers the point.
    */
  final case class Parked(
    tag: String,
    kind: Vehicles.VehicleKind,
    color: String,
    x: Double,
    z: Double,
    ry: Double,
    y: Option[Double] = None,
    skew: Option[Double] = None,
    load: Option[String] = None
  )

  /* Every position was probed against the world's colliders before it was written down, and the
   * rules that decided them are the same throughout: one kerbside vehicle in a frame is life and
   * two is congestion, nothing stands on a carriageway narrower than three metres, and no two
   * vehicles face each other across a road.
   */
  private def parked(): Vector[Parked] = Vector(
    // 踏切と駅前 -- 青空商店's delivery van at the alley mouth, and the only car on the east kerb
    // between the crossing and the canal.  Both are behind the spawn point: the shop's van at
    // z = 11.6 put a 1.88 m box in front of the opening camera.
    Parked("shopVan", Vehicles.VehicleKind.Keivan, "white", east(18.4), 18.4, Pi / 2.0),
    Parked("crossHatch", Vehicles.VehicleKind.Hatch, "cream", east(-18.6), -18.6, Pi / 2.0, skew = Some(-0.02)),
    // さくら坂商店街: one vehicle at a kerb in a 6.0 m corridor leaves 3.0 m, and these two are
    // five metres clear end to end, so the street never narrows on both sides at once.
    Parked("szTruck", Vehicles.VehicleKind.Keitruck, "slate", 20.5, 22.0, -Pi / 2.0, load = Some("sheet")),
    Parked("szVan", Vehicles.VehicleKind.Van, "pearl", 24.1, 31.0, Pi / 2.0),
    // the main road: 桜守裏町 is a 2.4 m footpath, so its residents park at its mouth
    Parked("uraKei", Vehicles.VehicleKind.Kei, "forest", west(50.6), 50.6, -Pi / 2.0),
    // ひばり駐車場: one bay of three, because a coin park with every bay taken reads as one nobody
    // can use
    Parked("coinA", Vehicles.VehicleKind.Hatch, "silver", 24.53, 49.2, -Pi / 2.0),
    // 月極駐車場: two bays of four, and not adjacent -- at 2.4 m centres a full row leaves 0.9 m
    // between bodies, which reads as a wall of metal rather than as a car park
    Parked("tsukiB", Vehicles.VehicleKind.Hatch, "white", 55.4, 9.1, Pi / 2.0),
    Parked("tsukiD", Vehicles.VehicleKind.Wagon, "navy", 60.2, 9.2, -Pi / 2.0),
    // 二丁目's spine: at the west kerb, which has no guard railing on it, nosing +z because Japan
    // drives on the left
    Parked("clinicSedan", Vehicles.VehicleKind.Sedan, "silver", 48.3, 20.6, -Pi / 2.0),
    // 公園前: the 私道's marked bay under the overbridge, which is why the ground is given
    // explicitly -- `heightAt` answers with the bridge deck at 4.68 m here
    Parked("courtKei", Vehicles.VehicleKind.Kei, "white", 41.45, 10.5, -Pi / 2.0, y = Some(0.06)),
    // 四丁目 and 三丁目: one of each pair of bays, and the middle bay under the carport
    Parked("hallVan", Vehicles.VehicleKind.Keivan, "silver", 21.55, 66.3, Pi / 2.0, skew = Some(0.025)),
    Parked("carbayKei", Vehicles.VehicleKind.Kei, "cream", 28.4, 74.9, -Pi / 2.0),
    Parked("terrPort", Vehicles.VehicleKind.Kei, "skyblue", 36.9, 60.4, 0.0, skew = Some(-0.02)),
    // 学校前通り: the minivan and kei use built parking, the lorry stands at the east kerb south of
    // the gate's no-stopping line
    Parked("dropMinivan", Vehicles.VehicleKind.Minivan, "pearl", -5.6, -39.4, 0.0),
    Parked("martLorry", Vehicles.VehicleKind.Boxtruck, "white", 5.0, -55.1, Pi / 2.0),
    Parked("staffKei", Vehicles.VehicleKind.Kei, "tea", -26.2, -49.65, Pi),
    // the canal: one vehicle between the crossing and the school gate, because the quiet is the
    // point of the whole channel
    Parked("worksTruck", Vehicles.VehicleKind.Keitruck, "white", west(-33.6), -33.6, -Pi / 2.0, load = Some("empty")),
    // 六丁目: three, which is more than anywhere else and is the reason the district exists --
    // only the bus is on the carriageway, nosing west out of its box, and its front door lands
    // level with the stop pole rather than with its back axle
    Parked("rokuBus", Vehicles.VehicleKind.Minibus, "cream", 69.20, 54.60, Pi),
    Parked("rokuBay1", Vehicles.VehicleKind.Kei, "white", 55.00, 56.75, -Pi / 2.0),
    Parked("rokuBay3", Vehicles.VehicleKind.Keitruck, "mint", 59.80, 56.79, -Pi / 2.0, load = Some("empty")),
    // 七丁目: six on the store's roof deck, which is half full on purpose -- a full car park is
    // the one arrangement in which the bay markings, the wheel stops and the numbers cannot be
    // seen.  Every z is derived: nose 0.6 m off the parapet, so the centre is edge -/+ (0.6 + L/2)
    // and no two kinds share a number.
    Parked("nanaDeck3", Vehicles.VehicleKind.Hatch, "white", -38.90, 79.09, -Pi / 2.0, y = Some(6.20)),
    Parked("nanaDeck5", Vehicles.VehicleKind.Kei, "mint", -34.10, 79.42, -Pi / 2.0, y = Some(6.20), skew = Some(0.035)),
    Parked("nanaDeck7", Vehicles.VehicleKind.Keivan, "pearl", -29.30, 79.42, -Pi / 2.0, y = Some(6.20)),
    Parked("nanaDeck9", Vehicles.VehicleKind.Kei, "mustard", -39.00, 68.18, Pi / 2.0, y = Some(6.20), skew = Some(-0.028)),
    Parked("nanaDeck11", Vehicles.VehicleKind.Minivan, "silver", -34.20, 68.65, Pi / 2.0, y = Some(6.20)),
    Parked("nanaDeck12", Vehicles.VehicleKind.Wagon, "tea", -31.80, 68.61, Pi / 2.0, y = Some(6.20)),
    // the yard, the service drive, the apron, the coin park opposite, and the one vehicle that is
    // actually on 七丁目通り
    Parked("nanaLorry", Vehicles.VehicleKind.Boxtruck, "white", -53.70, 74.60, -Pi / 2.0),
    Parked("nanaService", Vehicles.VehicleKind.Keitruck, "skyblue", -53.60, 85.40, Pi / 2.0, load = Some("crates")),
    Parked("nanaShort", Vehicles.VehicleKind.Kei, "skyblue", -31.20, 85.30, Pi / 2.0),
    Parked("nanaCoin2", Vehicles.VehicleKind.Hatch, "slate", -42.25, 98.53, -Pi / 2.0),
    Parked("nanaCoin5", Vehicles.VehicleKind.Kei, "cream", -34.75, 98.20, -Pi / 2.0, skew = Some(0.03)),
    Parked("nanaKerb", Vehicles.VehicleKind.Van, "pearl", -28.00, 93.71, Pi),
    // the school and ひばり山: five, every one off the carriageway, because the 裾道 is the only
    // road in the world with a hill on one side and a school wall on the other
    Parked("schStaff1", Vehicles.VehicleKind.Hatch, "pearl", 12.60, -68.60, Pi / 2.0),
    Parked("schStaff2", Vehicles.VehicleKind.Kei, "forest", 17.80, -68.60, Pi / 2.0, skew = Some(0.02)),
    Parked("schStoreVan", Vehicles.VehicleKind.Keivan, "white", 51.20, -71.40, Pi),
    Parked("uraKeiTruck", Vehicles.VehicleKind.Keitruck, "slate", 24.00, -98.00, Pi, load = Some("crates")),
    Parked("uraTurn", Vehicles.VehicleKind.Kei, "skyblue", 88.60, -63.40, 0.0),
    // ひばり湖: seven, and not one on the carriageway -- the 湖畔道路 is 4.0 m of mountain road
    // with a 10 % grade, and a car on it would be the only thing in the frame from the 見晴台
    Parked("cafeK", Vehicles.VehicleKind.Kei, "white", 176.00, -153.00, -Pi / 2.0),
    Parked("cafeVan", Vehicles.VehicleKind.Minivan, "silver", 180.00, -153.00, -Pi / 2.0, skew = Some(0.02)),
    Parked("cafeDeliv", Vehicles.VehicleKind.Van, "cream", 184.00, -152.80, -Pi / 2.0),
    Parked("campService", Vehicles.VehicleKind.Keivan, "tea", 207.00, -151.40, 0.0),
    Parked("campVisitor", Vehicles.VehicleKind.Kei, "skyblue", 211.00, -151.40, 0.0),
    Parked("damTruck", Vehicles.VehicleKind.Keitruck, "white", 148.40, -29.60, 0.0, load = Some("crates")),
    Parked("lakeLayby", Vehicles.VehicleKind.Hatch, "mint", 152.40, -32.40, Pi / 2.0)
  )

  /** One row of the scooter table. `lift` and `sink` adjust the seat height over the ground it
    * stands on; `seed` is kept because the table carries one, though the machine does not read it.
    */
  final case class Scooter(
    x: Double,
    z: Double,
    ry: Double,
    color: Int,
    seed: Int,
    lift: Option[Double] = None,
    sink: Option[Double] = None
  )

  /* Scooters are not `parkVehicle`: a 0.55 x 1.75 machine on its side stand is something you walk
   * round, not something you are stopped by, so none of them has a collider.
   */
  private def scooters(): Vector[Scooter] = Vector(
    Scooter(13.8, 5.6, 0.0, 0xc9d4dc, 9601),
    Scooter(24.75, 20.5, Pi / 2.0, 0xb8c0c8, 9602, lift = Some(0.13)),
    Scooter(3.4, 46.9, Pi / 2.0, 0x9fb0a4, 9603),
    Scooter(-19.6, -56.5, Pi / 2.0, 0xc4b49a, 9604),
    Scooter(-24.2, -35.8, Pi / 2.0, 0xa8b4c0, 9605),
    Scooter(-11.2, 55.0, Pi / 2.0, 0xcbbfa6, 9606),
    Scooter(12.2, 68.2, 0.0, 0xb0bec6, 9607),
    Scooter(-4.0, 20.5, -Pi / 2.0, 0xc2cdd4, 9608, sink = Some(0.02)),
    Scooter(172.4, -147.6, 0.0, 0xbfc9b4, 9617),
    Scooter(139.2, -68.4, -Pi / 2.0, 0xc8bca8, 9618),
    Scooter(62.55, 60.20, Pi / 2.0, 0xbfc9ce, 9609),
    Scooter(53.35, 62.30, Pi / 2.0, 0xcbbfa6, 9610),
    Scooter(-48.40, 86.40, Pi / 2.0, 0xb4bec4, 9611),
    Scooter(-47.55, 86.75, Pi / 2.0 - 0.14, 0xc9bfa2, 9612)
  )

  /** Upstream's `auditOverlaps`, which runs on every dev load and only warns.
    *
    * Every position in the table was probed against the world's colliders before it was written
    * down, and that is worth nothing for the vehicles in the *same* table: none of them is a
    * collider until the sweep runs, so a car can be tested clear and still be parked inside its
    * neighbour.  Two hatchbacks on the east kerb below the school were 1.6 m apart with 4.05 m of
    * body each and survived a whole round of screenshots that way, because from every angle one of
    * them hides the join.  Here the warning is an assertion instead.
    */
  def overlappingVehicles(): Vector[(String, String)] =
    val boxes = parked().map { v =>
      /* Upstream's `vehicleSize` is the SPEC entry widened by 0.06 for the kinds that carry a box
       * body, falling back to the flatbed's own 3.32 x 1.46 for the kei truck, which has no SPEC
       * entry at all.
       */
      val (length, width) = Vehicles.Spec.get(v.kind) match
        case Some(spec) => (spec.L, if spec.box.isDefined then spec.W + 0.06 else spec.W)
        case None       => (3.32, 1.46)
      val ry    = v.ry + v.skew.getOrElse(0.0)
      val cos   = math.abs(math.cos(ry))
      val sin   = math.abs(math.sin(ry))
      val halfW = (cos * length + sin * width) / 2.0
      val halfD = (sin * length + cos * width) / 2.0
      (v.tag, v.x - halfW, v.x + halfW, v.z - halfD, v.z + halfD)
    }
    val overlaps = mutable.ArrayBuffer.empty[(String, String)]
    boxes.indices.foreach { i =>
      (i + 1 until boxes.length).foreach { j =>
        val (tagA, ax0, ax1, az0, az1) = boxes(i)
        val (tagB, bx0, bx1, bz0, bz1) = boxes(j)
        val overlapX                   = math.min(ax1, bx1) - math.max(ax0, bx0)
        val overlapZ                   = math.min(az1, bz1) - math.max(az0, bz0)
        if overlapX > 0.0 && overlapZ > 0.0 then overlaps += ((tagA, tagB))
      }
    }
    overlaps.toVector
  end overlappingVehicles

  def buildTraffic(ctx: WorldCtx): Planting =
    val props  = District.propCtx(ctx)
    val petals = mutable.ArrayBuffer.empty[PetalPatch]
    val marks  = mutable.ArrayBuffer.empty[Vehicles.TyreSpot]

    parked().foreach { vehicle =>
      val y = vehicle.y.getOrElse(ctx.groundAt(vehicle.x, vehicle.z))
      Vehicles.parkVehicle(props,
        Vehicles.VehicleOpts(
          kind = vehicle.kind,
          color = Vehicles.Car(vehicle.color),
          x = vehicle.x,
          y = y,
          z = vehicle.z,
          ry = vehicle.ry,
          skew = vehicle.skew,
          load = vehicle.load
        ))
      /* Two faint bands under each one, so a bay that has been used looks used.  A vehicle at `ry`
       * noses along (cos ry, -sin ry), so the track pair is offset along the perpendicular
       * (sin ry, cos ry) -- and a PlaneGeometry scaled in its own z needs `ry + PI/2` to lie along
       * the vehicle rather than across it.
       */
      val angle = vehicle.ry + vehicle.skew.getOrElse(0.0)
      Vector(-1.0, 1.0).foreach { side =>
        marks += Vehicles.TyreSpot(
          x = vehicle.x + side * 0.62 * math.sin(angle),
          z = vehicle.z + side * 0.62 * math.cos(angle),
          y = y,
          ry = angle + Pi / 2.0,
          w = 0.24,
          d = 3.2
        )
      }
    }

    scooters().foreach { scooter =>
      val y = ctx.groundAt(scooter.x, scooter.z) + scooter.lift.getOrElse(0.0) - scooter.sink.getOrElse(0.0)
      ctx.add(StreetProps.makeScooter(StreetProps.ScooterOpts(
        x = scooter.x, y = y, z = scooter.z, ry = scooter.ry, color = scooter.color
      )))
    }
    Vehicles.tyreMarks(props, marks.toVector, opacity = 0.10)

    /* 駐停車禁止: solid single yellow at the kerb is no parking and no waiting in Japan, and it
     * goes exactly where this world needs a car not to be -- the two approaches to the level
     * crossing, and the school gate's own frontage.  The south run stops at z = -5.6 because the
     * kei truck stands from -5.74, and a no-waiting line down the flank of a parked truck says the
     * opposite of what it is for.
     */
    Vehicles.kerbLine(props,
      Vehicles.KerbLineOpts(axis = Axis.Z, at = Street.RoadHalf - 0.25,
        from = 3.4, to = 8.6, y = Street.groundY(6.5) + 0.01))
    Vehicles.kerbLine(props,
      Vehicles.KerbLineOpts(axis = Axis.Z, at = -(Street.RoadHalf - 0.25),
        from = -3.4, to = -5.6, y = Street.groundY(-5) + 0.01))
    Vehicles.kerbLine(props,
      Vehicles.KerbLineOpts(axis = Axis.Z,
        at = Street.centerX(-49.5) + Street.RoadHalf - 0.25, from = -52.4, to = -46.0,
        y = Street.groundY(-49.5) + 0.01))

    /* 徐行: two of them on the school road, one facing each way, drawn transparent and out of the
     * depth buffer like the 止まれ and the crossing diamonds, or the ink pass speckles the letters.
     */
    Vector((-45.0, 0.0), (-55.5, Pi)).foreach { (z, ry) =>
      val geometry = new PlaneGeometry(1.25, 2.4)
      geometry.rotateX(-Pi / 2.0)
      val mesh = new Mesh(geometry,
        Toon.flat(FlatOptions(color = 0xffffff,
          map = Some(Textures.roadPaint(RoadPaintKind.Slow)), transparent = true,
          depthWrite = Some(false), cache = false)))
      mesh.position.set(Street.centerX(z) + (if ry != 0.0 then -1.55 else 1.55),
        Street.groundY(z) + 0.022, z)
      mesh.rotation.y = ry
      ThreeInterop.setUserDataFlag(mesh, "noOutline")
      mesh.renderOrder = 1
      ctx.add(mesh)
    }

    /* The bus stop stands level with the minibus's front door rather than its back axle, and it has
     * no collider on purpose: the post is 0.11 m across on a 1.55 m footway and the player's radius
     * on each side of any collider is 0.68 m, so a box round it would leave 0.44 m of pavement.
     */
    val busStopZ = 37.6
    val busStopX = Street.centerX(busStopZ) + Street.RoadHalf + 0.55
    ctx.add(Vehicles.makeBusStop(Vehicles.BusStopOpts(x = busStopX, z = busStopZ,
      y = ctx.groundAt(busStopX, busStopZ), ry = -Pi / 2.0, h = 2.45)))

    /* 視線誘導標: the plastic wands on the edge of a lane too narrow for two things to pass.  No
     * collider on any of them -- they are frangible, they are 60 mm across, and thirty of them with
     * the player's radius added would be thirty 0.75 m obstructions on two-metre footways.
     */
    Vector((34.15, 56.4, 0.05), (34.15, 63.6, -0.04), (47.55, 30.2, 0.03), (47.55, 31.8, -0.05),
      (4.35, 58.6, 0.04)).foreach { (x, z, lean) =>
      ctx.add(Vehicles.makeDelineator(Vehicles.DelineatorOpts(x = x, z = z,
        y = ctx.groundAt(x, z), lean = lean)))
    }
    ctx.add(Props.makeCone(Props.ConeOpts(x = -3.0, z = -41.6, y = ctx.groundAt(-3.0, -41.6), ry = 0.3)))
    ctx.add(Props.makeCone(Props.ConeOpts(x = -3.0, z = -46.8, y = ctx.groundAt(-3.0, -46.8),
      ry = -0.5, tilt = 0.05)))

    // a second white line at the 送迎 bay's mouth, so it reads as a box a car pulls into rather
    // than as a patch of asphalt
    Ground.laneLine(ctx,
      Ground.LineOptions(axis = "x", at = -37.95, from = -7.7, to = -2.7,
        y = ctx.groundAt(-5.2, -37.95) + 0.02))

    // fall en blossom collects against a kerb and under anything that has not moved for a while
    petals += PetalPatch(x = 20.5, z = 22.0, w = 2.2, d = 4.2, y = ctx.groundAt(20.5, 22.0) + 0.02, n = 34)
    petals += PetalPatch(x = 24.53, z = 49.2, w = 2.2, d = 4.4, y = ctx.groundAt(24.53, 49.2) + 0.02, n = 30)
    petals += PetalPatch(x = 36.9, z = 60.4, w = 4.0, d = 2.4, y = ctx.groundAt(36.9, 60.4) + 0.02, n = 26)
    petals += PetalPatch(x = west(50.6), z = 50.6, w = 2.2, d = 4.6,
      y = ctx.groundAt(west(50.6), 50.6) + 0.02, n = 30)

    Planting(petals = petals.toVector)
  end buildTraffic

end Traffic
