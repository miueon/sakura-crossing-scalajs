package sakura.world

import Buildings.*
import Housing.*
import Props.*
import Shops.*
import StreetProps.*
import Vehicles.*
import sakura.core.{Textures, Util}
import sakura.core.Textures.{NorenKind, ShopKind}
import vendor.three.*

import scala.collection.mutable
import scala.math
import scala.scalajs.js

/** Upstream structural parity for the shared prop, building, housing, shop and vehicle
  * generators: per-builder mesh/instance/vertex/material-group/shadow counts and world
  * bounding box, pinned against ground truth extracted by running the upstream modules
  * (`props.js`, `streetprops.js`, `buildings.js`, `housing.js`, `shops.js`, `vehicles.js`)
  * under three@0.185.1. The upstream 26-key oracle matches 26/26 on the same harness.
  */
class PropsAndShopsParitySuite extends munit.FunSuite:

  private val B  = Buildings
  private val H  = Housing
  private val P  = Props
  private val S  = Shops
  private val SP = StreetProps
  private val V  = Vehicles

  override def beforeAll(): Unit = ParityCanvasStub.install()

  final private case class Row(
    label: String,
    build: () => Object3D[?],
    meshCount: Int,
    instancedCount: Int,
    vertexCount: Int,
    multiMatCount: Int,
    groupSliceCount: Int,
    castShadowCount: Int,
    receiveShadowCount: Int,
    bbox: Vector[Double]
  )

  private def sstep(a: Double, b: Double, v: Double): Double = Util.sstep(a, b, v)

  // Upstream street.js terrain constants, replicated as the parity-position fixture.
  private val RoadHalf = 3.15
  private val WalkW    = 1.55
  private val WalkH    = 0.135

  private def centerX(z: Double): Double =
    3.0 * sstep(-11.0, -36.0, z) - 3.4 * sstep(16.0, 44.0, z)

  private def groundY(z: Double): Double =
    1.05 * sstep(-13.0, -32.0, z) + 0.45 * sstep(28.0, 48.0, z)

  private def walkY(z: Double): Double = groundY(z) + WalkH

  private def poleY(x: Double, z: Double): Double =
    groundY(z) + (if math.abs(x - centerX(z)) < RoadHalf + WalkW then WalkH else 0.0)

  private def Vec(x: Double, y: Double, z: Double): Vector3 = new Vector3(x, y, z)

  private def ctx: PropCtx =
    val added = new js.Array[Object3D[?]]()
    PropCtx(
      add = obj => added.push(obj),
      collide = (_, _, _, _, _) => (),
      groundAt = (x, z) => groundY(z)
    )

  private def added(first: Object3D[?]): Object3D[?] = first

  private def round3(value: Double): Double = math.round(value * 1000.0) / 1000.0

  private val signatures = mutable.ArrayBuffer.empty[String]

  private def stats(root: Object3D[?]): (Int, Int, Int, Int, Int, Int, Int, Vector[Double]) =
    var meshCount          = 0
    var instancedCount     = 0
    var vertexCount        = 0
    var multiMatCount      = 0
    var groupSliceCount    = 0
    var castShadowCount    = 0
    var receiveShadowCount = 0
    def visit(node: Object3D[?]): Unit =
      node match
        case mesh: Mesh[?, ?, ?] =>
          meshCount += 1
          val positionCount = mesh.geometry.asInstanceOf[BufferGeometry[?, ?]].getAttribute("position") match
            case attribute: BufferAttribute[?] => attribute.count.toInt
            case _                             => -1
          signatures += s"${mesh.geometry.`type`}/${mesh.geometry.groups.length}/$positionCount" +
            s"@${round3(mesh.position.x)},${round3(mesh.position.y)},${round3(mesh.position.z)}"
          mesh match
            case instanced: InstancedMesh[?, ?, ?] => instancedCount += 1
            case _                                 => ()
          val position = mesh.geometry.asInstanceOf[BufferGeometry[?, ?]].getAttribute("position") match
            case attribute: BufferAttribute[?] => attribute
            case _                             => throw new IllegalStateException("sakura-crossing: parity subject has no position attribute")
          vertexCount += position.count.toInt
          if js.Array.isArray(mesh.material.asInstanceOf[Any]) then multiMatCount += 1
          groupSliceCount += mesh.geometry.groups.length
          if mesh.castShadow then castShadowCount += 1
          if mesh.receiveShadow then receiveShadowCount += 1
        case _ => ()
      end match
      val children = node.children
      (0 until children.length).foreach(i => visit(children(i)))
    end visit
    visit(root)
    root.updateMatrixWorld(true)
    val box     = new Box3().setFromObject(root.asInstanceOf[Object3D[Object3DEventMap]])
    val bboxMin = box.min
    val bboxMax = box.max
    (meshCount, instancedCount, vertexCount, multiMatCount, groupSliceCount,
      castShadowCount, receiveShadowCount,
      Vector(round3(bboxMin.x), round3(bboxMin.y), round3(bboxMin.z),
        round3(bboxMax.x), round3(bboxMax.y), round3(bboxMax.z)))
  end stats

  private val rows: Vector[Row] = Vector(
    Row("houseGable2",
      () =>
        B.house(HouseOpts(x = -13.6, z = 12.6, w = 7.0, d = 7.0, face = Buildings.Face.XPlus, floors = 2, seed = 21,
          wall = Some(0), roof = Some(1), roofKind = Some(Buildings.RoofKind.Gable), y = groundY(12.6))),
      12, 0, 1124, 0, 6, 9, 8, Vector(-17.581, -0.055, 8.680, -9.190, 6.931, 16.520)),
    Row("houseHip2",
      () =>
        B.house(HouseOpts(x = -9.4, z = 23.2, w = 7.0, d = 8.0, face = Buildings.Face.XPlus, floors = 2, seed = 22,
          wall = Some(2), roof = Some(0), roofKind = Some(Buildings.RoofKind.Hip), y = groundY(12.6))),
      12, 0, 1119, 0, 6, 9, 8, Vector(-13.320, -0.055, 18.780, -4.990, 8.940, 27.620)),
    Row("houseFlat2",
      () =>
        B.house(HouseOpts(x = -13.5, z = 43.0, w = 7.4, d = 7.4, face = Buildings.Face.XPlus, floors = 2, seed = 24,
          wall = Some(4), roof = Some(1), roofKind = Some(Buildings.RoofKind.Flat), y = groundY(12.6))),
      11, 0, 1106, 0, 6, 8, 8, Vector(-17.320, -0.055, 39.180, -9.050, 7.840, 46.820)),
    Row("wall",
      () =>
        B.wall(WallOpts(x = -5.1, z = 15.2, len = 5.0, axis = Buildings.Axis.Z, h = 0.5, fence = true, y = groundY(15.2))),
      3, 0, 792, 0, 12, 2, 1, Vector(-5.240, 0.000, 12.670, -4.960, 1.350, 17.730)),
    Row("timberFence",
      () => B.timberFence(FenceOpts(x = -5.3, z = 23.2, len = 8.2, axis = Buildings.Axis.Z, h = 0.4, y = groundY(23.2))),
      3, 0, 1392, 0, 6, 3, 3, Vector(-5.430, 0.000, 19.035, -5.170, 1.505, 27.365)),
    Row("blockFence",
      () => B.blockFence(FenceOpts(x = -9.9, z = 12.6, len = 7.2, axis = Buildings.Axis.Z, h = 0.72, y = groundY(12.6))),
      4, 0, 2256, 0, 12, 2, 3, Vector(-10.035, 0.000, 8.970, -9.765, 1.200, 16.230)),
    Row("pole",
      () => P.makePole(PoleOpts(x = -3.86, z = 4.55, h = 9.4, seed = 301, lamp = true, armDir = 1, y = poleY(-3.86, 4.55))),
      8, 0, 1125, 0, 8, 5, 4, Vector(-4.240, 0.135, 3.500, -2.260, 9.535, 5.600)),
    Row("wires",
      () =>
        added(P.makeWires(ctx, Vector(WireRun(Vector(Vec(-3.86, 9.4, 4.55), Vec(-4.35, 9.0, 14.2)), sag = 0.5, r = 0.026)))),
      1, 0, 75, 0, 0, 0, 0, Vector(-4.376, 8.806, 4.546, -3.834, 9.426, 14.202)),
    Row("keiTruck", () => P.makeKeiTruck(KeiTruckOpts(x = -2.02, z = -7.4, y = groundY(-7.4), ry = math.Pi / 2.0)), 39,
      0, 1284, 0, 204, 9, 2, Vector(-2.965, 0.000, -9.140, -1.075, 2.000, -5.660)),
    Row("bicycle",
      () =>
        P.makeBicycle(BicycleOpts(x = -4.3, z = 8.4, y = walkY(8.4), ry = math.Pi / 2.0 + 0.08, lean = -0.10,
          color = 0x3f6f9c)),
      6, 0, 1779, 0, 0, 4, 0, Vector(-4.746, 0.092, 7.470, -3.961, 1.224, 9.309)),
    Row("mirror", () => P.makeMirror(MirrorOpts(x = -3.62, z = 3.72, y = walkY(3.72), ry = 2.5)), 8, 0, 635, 0, 16, 2, 0,
      Vector(-4.316, 0.135, 3.245, -3.186, 3.045, 4.543)),
    Row("postBox", () => P.makePostBox(PostBoxOpts(x = 3.62, z = 4.55, y = walkY(4.55), ry = -1.4)), 8, 0, 519, 0, 30, 2,
      1, Vector(3.273, 0.135, 4.203, 3.967, 1.685, 4.897)),
    Row("waysideShrine", () => P.makeShrine(ShrineOpts(x = -5.45, z = 6.7, y = walkY(6.7), ry = 1.15)), 21, 0, 694, 0,
      87, 5, 0, Vector(-6.052, 0.135, 5.990, -4.848, 1.634, 7.410)),
    Row("cat", () => P.makeCat(CatOpts(x = -5.05, z = 8.3, y = groundY(8.3) + 0.7, ry = 1.35)), 11, 0, 747, 0, 10, 3, 0,
      Vector(-5.373, 0.700, 8.011, -4.745, 1.245, 8.538)),
    Row("cone", () => P.makeCone(ConeOpts(x = 2.62, z = 4.15, y = groundY(4.15), ry = 0.4)), 3, 0, 131, 0, 11, 1, 0,
      Vector(2.397, 0.000, 3.927, 2.843, 0.620, 4.373)),
    Row("barrier", () => P.makeBarrier(BarrierOpts(x = 2.62, z = 6.2, y = groundY(6.2), ry = 0.06, len = 1.7)), 10, 0,
      408, 0, 42, 3, 0, Vector(1.684, 0.000, 6.024, 3.556, 1.000, 6.376)),
    Row("guardrail",
      () => P.makeGuardrail(GuardrailOpts(x = 4.5, z = 15.4, y = walkY(15.4), ry = math.Pi / 2.0, len = 4.2)), 3, 0, 120,
      0, 12, 2, 0, Vector(4.445, 0.135, 13.245, 4.560, 0.985, 17.555)),
    Row("planter",
      () => P.makePlanter(PlanterOpts(x = -4.6, z = 7.9, y = walkY(7.9), r = 0.22, flower = true, seed = 300, n = 4)),
      10, 0, 612, 0, 9, 5, 1, Vector(-4.956, 0.135, 7.603, -4.290, 0.861, 8.198)),
    Row("umbrella", () => P.makeUmbrella(UmbrellaOpts(x = -4.62, z = 9.9, y = walkY(9.9), ry = 0.5)), 11, 0, 295, 0, 52,
      0, 0, Vector(-5.033, 0.125, 9.762, -4.408, 1.200, 10.455)),
    Row("bins", () => P.makeBins(BinsOpts(x = -4.65, z = 15.0, y = walkY(15.0) - 0.02, ry = -math.Pi / 2.0)), 7, 0, 168,
      0, 42, 3, 3, Vector(-4.940, 0.115, 14.710, -4.360, 0.825, 16.290)),
    Row("bikeRack", () => P.makeBikeRack(BikeRackOpts(n = 8, spacing = 0.62, seed = 17)), 4, 4, 1751, 0, 0, 4, 4,
      Vector(-0.902, -0.031, -2.467, 0.932, 1.081, 2.480)),
    Row("noticeBoard", () => P.makeNoticeBoard(NoticeBoardOpts()), 6, 0, 144, 1, 36, 4, 1,
      Vector(-1.220, 0.000, -0.073, 1.220, 2.323, 0.273)),
    Row("bench", () => P.makeBench(BenchOpts()), 9, 0, 216, 0, 54, 9, 9,
      Vector(-0.850, 0.000, -0.306, 0.850, 0.922, 0.225)),
    Row("signPost", () => P.makeSignPost(SignPostOpts()), 2, 0, 104, 0, 6, 1, 0,
      Vector(-0.120, 0.000, -0.120, 0.120, 2.400, 0.120)),
    Row("vendBin", () => P.makeVendBin(VendBinOpts()), 8, 0, 370, 0, 25, 1, 0,
      Vector(-0.247, 0.000, -0.260, 0.247, 0.935, 0.260)),
    Row("aircon",
      () =>
        P.makeAircon(AirconOpts(x = 8.4, z = 16.11, y = groundY(15.1) + 1.6, ry = math.Pi, w = 0.8, h = 0.56,
          feet = false)),
      15, 0, 424, 0, 87, 1, 1, Vector(8.000, 1.560, 15.920, 8.800, 2.205, 16.352)),
    Row("laundryPole",
      () => P.makeLaundryPole(LaundryPoleOpts(x = 46.4, z = 53.55, y = 0.45, ry = 0.0, len = 2.4, n = 4, seed = 7745)),
      13, 0, 336, 0, 39, 5, 0, Vector(45.080, 0.450, 53.430, 47.720, 2.224, 53.670)),
    Row("crow", () => P.makeCrow(CrowOpts()), 10, 0, 405, 0, 32, 10, 0,
      Vector(-0.064, 0.001, -0.312, 0.064, 0.209, 0.243)),
    Row("catBox", () => P.makeCatBox(CatBoxOpts(x = 13.9, z = 16.0, y = groundY(15.1), ry = -0.5)), 11, 0, 435, 0, 40, 2,
      11, Vector(13.433, 0.000, 15.636, 14.295, 0.356, 16.505)),
    Row("loosePaper",
      () =>
        added(P.makeLoosePaper(ctx,
          Vector(PaperSpot(x = 11.6, z = 14.5, y = groundY(15.1) + 0.1, ry = 0.9),
            PaperSpot(x = 15.1, z = 15.6, y = groundY(15.1) + 0.1, ry = -0.4)))),
      1, 1, 4, 0, 0, 0, 0, Vector(11.421, 0.122, 14.328, 15.253, 0.122, 15.774)),
    Row("umbrellaStand", () => P.makeUmbrellaStand(UmbrellaStandOpts(x = 12.2, z = 20.3, y = groundY(15.1), ry = math.Pi)),
      11, 0, 338, 0, 25, 1, 0, Vector(11.963, 0.000, 20.100, 12.479, 1.085, 20.579)),
    Row("hoseBox", () => P.makeHoseBox(HoseBoxOpts()), 3, 0, 72, 0, 18, 1, 0,
      Vector(-0.080, 0.000, -0.250, 0.115, 0.720, 0.250)),
    Row("flowerBed", () => P.makeFlowerBed(FlowerBedOpts()), 24, 0, 1260, 0, 30, 14, 0,
      Vector(-0.750, 0.000, -2.050, 0.750, 0.496, 2.050)),
    Row("bucket", () => P.makeBucket(BucketOpts(x = 45.1, z = 49.4, y = 0.45, ry = 0.4, water = true)), 5, 0, 212, 0, 7,
      1, 0, Vector(44.917, 0.450, 49.213, 45.283, 0.830, 49.587)),
    Row("broom", () => P.makeBroom(BroomOpts()), 3, 0, 102, 0, 7, 2, 0,
      Vector(-0.219, -0.060, -0.162, 0.175, 1.458, 0.381)),
    Row("milkCrate", () => P.makeMilkCrate(MilkCrateOpts(x = 45.9, z = 47.85, y = 0.45, n = 2, ry = -0.35)), 8, 0, 304,
      0, 36, 2, 2, Vector(45.638, 0.450, 47.624, 46.162, 1.070, 48.076)),
    Row("tapPost", () => P.makeTapPost(TapPostOpts(x = 22.4, z = -81.2, y = 0.0, ry = math.Pi, h = 0.88)), 9, 0, 381, 0,
      30, 1, 1, Vector(22.092, 0.000, -81.500, 22.708, 0.939, -80.900)),
    Row("storageShed", () => P.makeStorageShed(StorageShedOpts(x = 55.0, z = 66.4, y = 0.0, ry = math.Pi)), 12, 0, 480,
      0, 66, 3, 1, Vector(54.280, 0.000, 65.955, 55.720, 1.816, 66.851)),
    Row("potShelf",
      () => P.makePotShelf(PotShelfOpts(x = 35.9, z = 49.1, y = 0.45, ry = -math.Pi / 2.0, w = 1.2, n = 6, seed = 7713)),
      19, 0, 1972, 0, 18, 19, 0, Vector(35.752, 0.450, 48.493, 36.043, 1.458, 49.707)),
    Row("mailboxBank",
      () => P.makeMailboxBank(MailboxBankOpts(x = 43.75, z = 49.0, y = 0.45, ry = -math.Pi / 2.0, cols = 4, rows = 2)),
      37, 0, 1016, 0, 198, 3, 1, Vector(43.580, 0.450, 48.560, 43.880, 1.720, 49.440)),
    Row("deliveryBox", () => P.makeDeliveryBox(DeliveryBoxOpts(x = 48.0, z = 47.15, y = 0.45, ry = -0.3)), 8, 0, 192, 0,
      48, 1, 1, Vector(47.693, 0.450, 46.856, 48.307, 1.140, 47.444)),
    Row("doormat", () => P.makeDoormat(DoormatOpts(x = 36.0, z = 52.95, y = 0.45, ry = math.Pi / 2.0, w = 0.56, d = 0.34)),
      2, 0, 48, 0, 12, 0, 1, Vector(35.830, 0.450, 52.670, 36.170, 0.477, 53.230)),
    Row("ivy", () => P.makeIvy(IvyOpts(len = 3.0, top = 0.9, drop = 0.6)), 2, 2, 120, 0, 0, 2, 0,
      Vector(-1.525, 0.232, -0.132, 1.481, 0.989, 0.141)),
    Row("returnPost", () => P.makeReturnPost(ReturnPostOpts()), 9, 0, 224, 0, 45, 2, 1,
      Vector(-0.370, 0.000, -0.290, 0.370, 1.235, 0.290)),
    Row("petBowl", () => P.makePetBowl(PetBowlOpts()), 4, 0, 152, 0, 6, 2, 0,
      Vector(-0.081, 0.000, -0.085, 0.301, 0.050, 0.085)),
    Row("phoneBooth", () => P.makePhoneBooth(PhoneBoothOpts()), 24, 0, 2237, 1, 18, 6, 5,
      Vector(-0.580, 0.000, -0.580, 0.580, 2.670, 0.580)),
    Row("guideBoard", () => P.makeGuideBoard(GuideBoardOpts()), 8, 0, 248, 1, 42, 3, 1,
      Vector(-0.830, 0.000, -0.130, 0.830, 2.159, 0.292)),
    Row("recycleBox", () => P.makeRecycleBox(RecycleBoxOpts()), 9, 0, 216, 0, 54, 2, 1,
      Vector(-0.455, 0.000, -0.317, 0.455, 0.766, 0.305)),
    Row("crates", () => P.makeCrates(CratesOpts(x = 5.55, z = 22.0, y = walkY(22.0), n = 3, seed = 41)), 3, 0, 72, 0, 18,
      3, 3, Vector(5.297, 0.135, 21.841, 5.827, 0.975, 22.214)),
    Row("paperLantern", () => S.makePaperLantern(PaperLanternOpts(y = 2.5)), 4, 0, 182, 0, 10, 0, 0,
      Vector(-0.160, 2.144, -0.160, 0.160, 2.856, 0.160)),
    Row("menuBoard", () => S.makeMenuBoard(MenuBoardOpts()), 4, 0, 96, 1, 24, 1, 0,
      Vector(-0.290, -0.005, -0.291, 0.290, 1.027, 0.106)),
    Row("freezer", () => S.makeFreezer(FreezerOpts()), 18, 0, 412, 0, 102, 1, 1,
      Vector(-0.595, 0.000, -0.350, 0.595, 0.910, 0.350)),
    Row("gachapon", () => S.makeGachapon(GachaponOpts()), 16, 0, 747, 0, 69, 3, 3,
      Vector(-0.660, 0.000, -0.250, 0.660, 1.420, 0.270)),
    Row("produceStack", () => S.makeProduceStack(ProduceStackOpts(seed = 11)), 19, 0, 708, 0, 72, 13, 6,
      Vector(-0.270, 0.000, -0.189, 1.470, 1.018, 0.218)),
    Row("shopFlag", () => S.makeShopFlag(ShopFlagOpts()), 3, 0, 102, 0, 6, 2, 0,
      Vector(-0.143, 0.000, -0.150, 0.389, 1.900, 0.150)),
    Row("shop_hana",
      () =>
        added(S.makeShop(ctx,
          ShopOpts(
            x = 16.9, z = (16.6 + 22.2) / 2.0, y = groundY((16.6 + 22.2) / 2.0), w = 22.2 - 16.6, d = 4.6,
            face = Buildings.Face.XPlus,
            kind = Some(ShopKind.Hana), floors = 2, wall = Some(3), roof = Some(3),
            roofKind = Some(Buildings.RoofKind.Flat), awning = Some(0), awningOut = Some(1.4),
            bladeSide = -1, interior = Some(2), lit = true, openW = Some((22.2 - 16.6) - 1.4), seed = 100 + 16
          ))),
      23, 0, 1816, 1, 54, 9, 7, Vector(14.39, 0.0, 16.39, 20.581, 6.44, 22.41)),
    Row("shop_kosho",
      () =>
        added(S.makeShop(ctx,
          ShopOpts(
            x = 16.9, z = (22.4 + 27.2) / 2.0, y = groundY((22.4 + 27.2) / 2.0), w = 27.2 - 22.4, d = 4.6,
            face = Buildings.Face.XPlus,
            kind = Some(ShopKind.Kosho), floors = 2, wall = Some(1), roof = Some(2),
            roofKind = Some(Buildings.RoofKind.Flat), awning = Some(3), awningOut = Some(1.4),
            blade = Some(ShopKind.Kosho), bladeSide = -1, interior = Some(2), signBox = true,
            openW = Some((27.2 - 22.4) - 1.4), seed = 100 + 22
          ))),
      28, 0, 1860, 2, 66, 11, 8, Vector(14.39, 0.0, 22.19, 20.581, 6.44, 27.61)),
    Row("shop_cleaning",
      () =>
        added(S.makeShop(ctx,
          ShopOpts(
            x = 16.9, z = (27.4 + 31.8) / 2.0, y = groundY((27.4 + 31.8) / 2.0), w = 31.8 - 27.4, d = 4.6,
            face = Buildings.Face.XPlus,
            kind = Some(ShopKind.Cleaning), floors = 1, wall = Some(2), roof = Some(1),
            roofKind = Some(Buildings.RoofKind.Gable), awning = Some(2), awningOut = Some(1.4),
            bladeSide = -1, interior = Some(2), shutter = Some(0.34), signBox = true,
            openW = Some((31.8 - 27.4) - 1.4), seed = 100 + 27
          ))),
      24, 0, 1520, 1, 48, 11, 9, Vector(13.98, 0.008, 26.912, 20.581, 4.588, 32.288)),
    Row("shop_sozai",
      () =>
        added(S.makeShop(ctx,
          ShopOpts(
            x = 16.9, z = (32.0 + 35.0) / 2.0, y = groundY((32.0 + 35.0) / 2.0), w = 35.0 - 32.0, d = 4.6,
            face = Buildings.Face.XPlus,
            kind = Some(ShopKind.Sozai), floors = 1, wall = Some(4), roof = Some(0),
            roofKind = Some(Buildings.RoofKind.Gable), awning = Some(3), awningOut = Some(1.4),
            bladeSide = -1, interior = Some(0), openW = Some((35.0 - 32.0) - 1.4), seed = 100 + 32
          ))),
      21, 0, 1288, 1, 42, 9, 7, Vector(13.98, 0.083, 31.506, 20.581, 4.663, 35.494)),
    Row("shop_denki",
      () =>
        added(S.makeShop(ctx,
          ShopOpts(
            x = 16.9, z = (35.2 + 38.3) / 2.0, y = groundY((35.2 + 38.3) / 2.0), w = 38.3 - 35.2, d = 4.6,
            face = Buildings.Face.XPlus,
            kind = Some(ShopKind.Denki), floors = 2, wall = Some(7), roof = Some(1),
            roofKind = Some(Buildings.RoofKind.Flat), blade = Some(ShopKind.Denki), bladeSide = -1,
            interiorMap = Some(Textures.showaInterior(1)), openW = Some((38.3 - 35.2) - 1.4), seed = 100 + 35
          ))),
      18, 0, 1112, 2, 42, 7, 5, Vector(14.39, 0.183, 34.99, 19.56, 6.623, 38.71)),
    Row("shop_record",
      () =>
        added(S.makeShop(ctx,
          ShopOpts(
            x = 16.9, z = (38.5 + 41.6) / 2.0, y = groundY((38.5 + 41.6) / 2.0), w = 41.6 - 38.5, d = 4.6,
            face = Buildings.Face.XPlus,
            kind = Some(ShopKind.Record), floors = 2, wall = Some(6), roof = Some(2),
            roofKind = Some(Buildings.RoofKind.Flat), awning = Some(4), awningOut = Some(1.4),
            blade = Some(ShopKind.Record), bladeSide = -1, noren = Some(NorenKind.Record),
            interiorMap = Some(Textures.showaInterior(0)), posters = false, openW = Some((41.6 - 38.5) - 1.4),
            seed = 100 + 38
          ))),
      26, 0, 1544, 2, 60, 12, 8, Vector(14.39, 0.293, 38.29, 20.581, 6.733, 42.01)),
    Row("shop_ramen",
      () =>
        added(S.makeShop(ctx,
          ShopOpts(
            x = 28.0, z = (16.8 + 22.4) / 2.0, y = groundY((16.8 + 22.4) / 2.0), w = 22.4 - 16.8, d = 5.6,
            face = Buildings.Face.XMinus,
            kind = Some(ShopKind.Ramen), floors = 2, wall = Some(1), roof = Some(0),
            roofKind = Some(Buildings.RoofKind.Flat), awning = Some(4), awningOut = Some(1.5),
            blade = Some(ShopKind.Ramen), noren = Some(NorenKind.Ramen), interior = Some(1),
            lit = true, posterLimit = Some(1), openW = Some((22.4 - 16.8) - 1.4), seed = 200 + 16
          ))),
      29, 0, 1948, 2, 66, 12, 8, Vector(23.718, 0.0, 16.59, 31.01, 6.44, 22.81)),
    Row("shop_wagashi",
      () =>
        added(S.makeShop(ctx,
          ShopOpts(
            x = 28.0, z = (22.6 + 27.4) / 2.0, y = groundY((22.6 + 27.4) / 2.0), w = 27.4 - 22.6, d = 5.6,
            face = Buildings.Face.XMinus,
            kind = Some(ShopKind.Wagashi), floors = 1, wall = Some(5), roof = Some(2),
            roofKind = Some(Buildings.RoofKind.Gable), awning = Some(1), awningOut = Some(1.5),
            noren = Some(NorenKind.Wagashi), interior = Some(2), openW = Some((27.4 - 22.6) - 1.4), seed = 200 + 22
          ))),
      24, 0, 1536, 1, 42, 11, 8, Vector(23.718, 0.0, 22.113, 31.42, 4.58, 27.887)),
    Row("shop_bunbo",
      () =>
        added(S.makeShop(ctx,
          ShopOpts(
            x = 28.0, z = (30.2 + 34.8) / 2.0, y = groundY((30.2 + 34.8) / 2.0), w = 34.8 - 30.2, d = 5.6,
            face = Buildings.Face.XMinus,
            kind = Some(ShopKind.Bunbo), floors = 2, wall = Some(0), roof = Some(3),
            roofKind = Some(Buildings.RoofKind.Flat), awning = Some(1), awningOut = Some(1.5),
            blade = Some(ShopKind.Bunbo), interior = Some(2), lit = true, balcony = true,
            signBox = true, openW = Some((34.8 - 30.2) - 1.4), seed = 200 + 30
          ))),
      27, 0, 2232, 2, 60, 11, 8, Vector(23.718, 0.058, 29.99, 31.01, 6.498, 35.21)),
    Row("shop_sento",
      () =>
        added(S.makeShop(ctx,
          ShopOpts(
            x = 29.4, z = (35.4 + 43.4) / 2.0, y = groundY((35.4 + 43.4) / 2.0), w = 43.4 - 35.4, d = 8.4,
            face = Buildings.Face.XMinus,
            kind = Some(ShopKind.Sento), floors = 1, h1 = 4.4, wall = Some(0), roof = Some(0),
            roofKind = Some(Buildings.RoofKind.Gable), roofH = Some(2.3), interior = Some(3),
            openW = Some(4.6), noren = Some(NorenKind.Sento), posters = false, seed = 301,
            recess = 1.1, fascia = false
          ))),
      13, 0, 880, 0, 12, 7, 6, Vector(24.98, 0.272, 34.91, 34.22, 7.102, 43.89)),
    Row("gomiHouse", () => SP.makeGomiHouse(GomiHouseOpts(x = 16.8, z = -82.0, y = 0.0, ry = math.Pi, w = 2.6, d = 1.6)),
      12, 0, 744, 1, 24, 9, 8, Vector(15.430, 0.000, -83.709, 18.170, 1.366, -81.112)),
    Row("lockerBank", () => SP.makeLockerBank(LockerBankOpts(x = 56.3, z = 61.02, y = 0.0, ry = math.Pi)), 8, 0, 936, 1,
      24, 6, 5, Vector(55.605, 0.000, 60.710, 56.995, 1.775, 61.330)),
    Row("scooter", () => SP.makeScooter(ScooterOpts(x = -12.9, z = 78.6, y = 0.52, ry = math.Pi / 2.0, color = 0x6f7f96)),
      9, 0, 2512, 1, 21, 5, 4, Vector(-13.337, 0.491, 77.764, -12.581, 1.858, 79.426)),
    Row("kitchenGarden",
      () => SP.makeKitchenGarden(KitchenGardenOpts(x = 76.8, z = 66.4, y = 0.0, ry = 0.0, w = 2.2, d = 1.1)), 8, 1, 786,
      0, 6, 5, 5, Vector(75.279, -0.014, 65.850, 77.900, 0.930, 66.950)),
    Row("kidBike", () => SP.makeKidBike(KidBikeOpts(x = 75.35, z = 58.88, y = 0.0, ry = 0.0, color = 0x9c5a4a)), 7, 0,
      1987, 0, 0, 5, 0, Vector(74.793, -0.010, 58.654, 75.926, 0.662, 59.106)),
    Row("dryingRack", () => SP.makeDryingRack(DryingRackOpts(x = 57.6, z = 66.5, y = 0.0, ry = math.Pi / 2.0)), 7, 0,
      596, 0, 0, 7, 2, Vector(57.105, 0.000, 65.900, 58.095, 1.065, 67.100)),
    Row("ballBox", () => SP.makeBallBox(BallBoxOpts(x = 67.65, z = 41.3, y = 0.0, ry = -math.Pi / 2.0)), 5, 0, 1167, 0,
      0, 5, 2, Vector(67.400, 0.000, 40.950, 67.900, 0.450, 41.995)),
    Row("wheelStops", () => SP.makeWheelStops(WheelStopsOpts(ry = math.Pi, n = 1, gauge = 1.4)), 5, 0, 328, 1, 6, 4, 4,
      Vector(-1.000, 0.000, -0.080, 1.000, 0.438, 0.282)),
    Row("gasMeter", () => SP.makeGasMeter(GasMeterOpts(x = 53.86, z = 62.8, y = 0.0, ry = -math.Pi / 2.0)), 9, 0, 918, 0,
      21, 6, 6, Vector(53.699, 0.000, 62.525, 54.020, 1.212, 62.985)),
    Row("waterMeter", () => SP.makeWaterMeter(WaterMeterOpts()), 4, 0, 240, 0, 6, 0, 3,
      Vector(-0.190, -0.006, -0.150, 0.190, 0.026, 0.150)),
    Row("chalkMarks",
      () =>
        added(SP.makeChalkMarks(ctx,
          Vector(ChalkSpot(x = 65.6, z = 47.2, y = 0.0, scale = 0.9, ry = 0.3, seed = 7),
            ChalkSpot(x = 66.8, z = 46.9, y = 0.0, scale = 0.7, ry = -0.5, seed = 8)))),
      1, 1, 4, 0, 0, 0, 0, Vector(65.076, 0.020, 46.125, 67.209, 0.020, 48.293)),
    Row("atticHouse",
      () =>
        H.makeAtticHouse(AtticHouseOpts(x = 39.2, z = 51.4, y = 0.45, w = 7.2, d = 6.6, face = Buildings.Face.XMinus,
          seed = 7711, wall = 6, roof = 0, door = 2, nameVariant = 0, lit = true, litDormer = false)),
      24, 0, 3272, 0, 30, 13, 9, Vector(34.955, 0.450, 47.095, 43.170, 10.510, 55.705)),
    Row("walkup",
      () =>
        H.makeWalkup(WalkupOpts(x = 47.0, z = 55.9, y = 0.45, w = 8.0, d = 7.0, face = Buildings.Face.XPlus, floors = 3,
          units = 4, seed = 7721, wall = 4, roof = 0, door = 4, plate = 0)),
      47, 0, 9546, 1, 120, 11, 7, Vector(42.530, 0.450, 51.570, 51.200, 10.650, 61.675)),
    Row("terrace",
      () =>
        H.makeTerrace(TerraceOpts(x = 41.8, z = 66.9, y = 0.45, units = 3, unitW = 2.9, d = 6.2,
          face = Buildings.Face.XMinus, seed = 7731, wall = 7, roof = 1)),
      30, 0, 3160, 0, 72, 13, 9, Vector(37.870, 0.450, 62.160, 45.308, 6.815, 71.640)),
    Row("carport",
      () => H.makeCarport(CarportOpts(x = 32.6, z = 65.85, y = 0.45, ry = math.Pi / 2.0, w = 2.7, d = 4.6, h = 2.3)), 4,
      0, 800, 0, 12, 1, 0, Vector(30.199, 0.450, 64.420, 35.001, 3.089, 67.280)),
    Row("bikeShelter",
      () =>
        H.makeBikeShelter(BikeShelterOpts(x = 51.6, z = 49.6, y = 0.45, ry = math.Pi / 2.0, w = 4.0, d = 1.8, h = 2.05)),
      3, 0, 920, 0, 6, 1, 0, Vector(50.599, 0.450, 47.520, 52.601, 2.803, 51.680)),
    Row("vehicle_kei", () => V.makeVehicle(VehicleOpts(kind = VehicleKind.Kei)), 8, 0, 2684, 0, 0, 4, 7,
      Vector(-1.805, 0.000, -0.942, 1.805, 1.728, 0.942)),
    Row("vehicle_keivan", () => V.makeVehicle(VehicleOpts(kind = VehicleKind.Keivan)), 8, 0, 2540, 0, 0, 4, 7,
      Vector(-1.805, 0.000, -0.942, 1.805, 1.950, 0.942)),
    Row("vehicle_hatch", () => V.makeVehicle(VehicleOpts(kind = VehicleKind.Hatch)), 8, 0, 2708, 0, 0, 4, 7,
      Vector(-2.130, 0.000, -1.053, 2.130, 1.561, 1.053)),
    Row("vehicle_sedan", () => V.makeVehicle(VehicleOpts(kind = VehicleKind.Sedan)), 8, 0, 2732, 0, 0, 4, 7,
      Vector(-2.315, 0.000, -1.053, 2.315, 1.485, 1.053)),
    Row("vehicle_wagon", () => V.makeVehicle(VehicleOpts(kind = VehicleKind.Wagon)), 8, 0, 2708, 0, 0, 4, 7,
      Vector(-2.230, 0.000, -1.050, 2.230, 1.580, 1.050)),
    Row("vehicle_minivan", () => V.makeVehicle(VehicleOpts(kind = VehicleKind.Minivan)), 8, 0, 2708, 0, 0, 4, 7,
      Vector(-2.275, 0.000, -1.053, 2.275, 1.870, 1.053)),
    Row("vehicle_van", () => V.makeVehicle(VehicleOpts(kind = VehicleKind.Van)), 8, 0, 2540, 0, 0, 4, 7,
      Vector(-2.325, 0.000, -1.053, 2.325, 2.050, 1.053)),
    Row("vehicle_boxtruck", () => V.makeVehicle(VehicleOpts(kind = VehicleKind.Boxtruck)), 8, 0, 2948, 0, 0, 4, 7,
      Vector(-2.505, 0.000, -1.053, 2.505, 2.515, 1.053)),
    Row("vehicle_minibus", () => V.makeVehicle(VehicleOpts(kind = VehicleKind.Minibus)), 8, 0, 2756, 0, 0, 4, 7,
      Vector(-3.255, 0.000, -1.245, 3.255, 2.610, 1.245)),
    Row("delineator", () => V.makeDelineator(DelineatorOpts(lean = -0.04)), 3, 0, 220, 0, 0, 3, 3,
      Vector(-0.090, -0.004, -0.095, 0.093, 0.801, 0.095)),
    Row("busStop", () => V.makeBusStop(BusStopOpts(x = 67.4, z = 0.0, y = 0.0, ry = math.Pi, h = 2.45, variant = 1)), 4,
      0, 124, 2, 21, 3, 0, Vector(67.140, 0.000, -0.120, 67.660, 2.450, 0.120)),
    Row("bayPaint", () => added(V.bayPaint(ctx, BayPaintOpts())), 1, 0, 72, 0, 0, 0, 0,
      Vector(-1.250, 0.012, -2.550, 1.250, 0.032, 2.500)),
    Row("kerbLine", () => added(V.kerbLine(ctx, KerbLineOpts(axis = Buildings.Axis.Z, at = 0.0, from = 0.0, to = 6.0))),
      1, 0, 24, 0, 6, 0, 0, Vector(-0.055, 0.012, 0.000, 0.055, 0.032, 6.000)),
    Row("tyreMarks",
      () =>
        added(V.tyreMarks(ctx,
          Vector(TyreSpot(x = 0.0, z = 0.0, y = 0.0, ry = 0.3),
            TyreSpot(x = 1.0, z = 1.0, y = 0.0, ry = -0.2, w = 0.22, d = 2.2)))),
      1, 1, 4, 0, 0, 0, 0, Vector(-0.460, 0.025, -1.179, 1.326, 0.025, 2.100)),
  )

  rows.foreach { row =>
    test(s"upstream structural parity: ${row.label}") {
      signatures.clear()
      val (meshCount, instancedCount, vertexCount, multiMatCount, groupSliceCount,
        castShadowCount, receiveShadowCount, bbox) = stats(row.build())
      val counts = List(
        "mesh"          -> (meshCount, row.meshCount),
        "instanced"     -> (instancedCount, row.instancedCount),
        "vertex"        -> (vertexCount, row.vertexCount),
        "multiMat"      -> (multiMatCount, row.multiMatCount),
        "groupSlice"    -> (groupSliceCount, row.groupSliceCount),
        "castShadow"    -> (castShadowCount, row.castShadowCount),
        "receiveShadow" -> (receiveShadowCount, row.receiveShadowCount)
      )
      val diffs = counts.collect {
        case (name, (actual, expected)) if actual != expected =>
          s"$name $actual/$expected"
      } ++ bbox.zip(row.bbox).zipWithIndex.collect {
        case ((actual, expected), i) if math.abs(actual - expected) > 0.0011 =>
          s"bbox[$i] ${round3(actual)}/${round3(expected)}"
      }
      if diffs.nonEmpty then
        // the per-mesh signature is what turns "some count is off" into a specific missing or
        // extra mesh: compare it against the oracle's own `signature` field for the same row.
        println(s"[sig] ${row.label}: ${signatures.mkString(",")}")
        fail(s"${row.label}: ${diffs.mkString(", ")}")
    }
  }
end PropsAndShopsParitySuite

/** The no-op canvas stub Housing's block-plate texture needs under NodeJSEnv. */
private[world] object ParityCanvasStub:

  def install(): Unit =
    js.Dynamic.global.globalThis.updateDynamic("document")(
      js.Dynamic.literal(
        createElement = (
          (tag: String) =>
            if tag == "canvas" then new ParityStubCanvas
            else throw new IllegalStateException(s"sakura-crossing: unexpected element: $tag")
        ): js.Function1[String, ParityStubCanvas]
      )
    )
end ParityCanvasStub

final private[world] class ParityStubTextMetrics(val width: Double) extends js.Object

final private[world] class ParityStubGradient extends js.Object:
  def addColorStop(offset: Double, color: String): Unit = ()

final private[world] class ParityStubContext2d extends js.Object:
  var fillStyle: String                = ""
  var strokeStyle: String              = ""
  var lineWidth: Double                = 1.0
  var font: String                     = ""
  var textAlign: String                = "start"
  var textBaseline: String             = "alphabetic"
  var globalAlpha: Double              = 1.0
  var globalCompositeOperation: String = "source-over"
  var lineCap: String                  = "butt"
  var imageSmoothingEnabled: Boolean   = true

  def fillRect(x: Double, y: Double, w: Double, h: Double): Unit   = ()
  def clearRect(x: Double, y: Double, w: Double, h: Double): Unit  = ()
  def strokeRect(x: Double, y: Double, w: Double, h: Double): Unit = ()
  def beginPath(): Unit                                            = ()
  def closePath(): Unit                                            = ()
  def moveTo(x: Double, y: Double): Unit                           = ()
  def lineTo(x: Double, y: Double): Unit                           = ()
  def arc(
    x: Double,
    y: Double,
    radius: Double,
    startAngle: Double,
    endAngle: Double,
    anticlockwise: Boolean = false
  ): Unit = ()
  def ellipse(
    x: Double,
    y: Double,
    radiusX: Double,
    radiusY: Double,
    rotation: Double,
    startAngle: Double,
    endAngle: Double,
    counterclockwise: Boolean = false
  ): Unit = ()
  def bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double): Unit = ()
  def quadraticCurveTo(cpx: Double, cpy: Double, x: Double, y: Double): Unit                            = ()
  def fill(): Unit                                                                                      = ()
  def stroke(): Unit                                                                                    = ()
  def fillText(text: String, x: Double, y: Double, maxWidth: Double = 0.0): Unit                        = ()
  def measureText(text: String): ParityStubTextMetrics                                                  = new ParityStubTextMetrics(0.0)
  def save(): Unit                                                                                      = ()
  def restore(): Unit                                                                                   = ()
  def translate(x: Double, y: Double): Unit                                                             = ()
  def rotate(angle: Double): Unit                                                                       = ()
  def setLineDash(segments: js.Array[Double]): Unit                                                     = ()
  def createLinearGradient(x0: Double, y0: Double, x1: Double, y1: Double): ParityStubGradient =
    new ParityStubGradient
  def createRadialGradient(x0: Double, y0: Double, r0: Double, x1: Double, y1: Double, r1: Double): ParityStubGradient =
    new ParityStubGradient
end ParityStubContext2d

final private[world] class ParityStubCanvas extends js.Object:
  var width: Int                           = 0
  var height: Int                          = 0
  private val context: ParityStubContext2d = new ParityStubContext2d

  def getContext(contextId: String): ParityStubContext2d =
    if contextId != "2d" then
      throw new IllegalStateException(s"sakura-crossing: unexpected context kind: $contextId")
    context
end ParityStubCanvas
