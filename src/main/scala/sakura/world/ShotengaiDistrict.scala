package sakura.world

import sakura.{Palette, PlanetCurvature, TerrainHeightmap, TextureCache, ToonMaterial}
import sakura.ThreeInterop.*
import sakura.foliage.{CherryPlacement, CherryTreeGenerator, PlannedTransform, SceneBatch}
import vendor.three.*

import scala.math
import scala.scalajs.js

/** One hero district's still camera framing: eye and aim point on the flat plane, both seated on
  * the terrain and bent onto the planet by the host at placement time.
  */
final private[sakura] case class DistrictViewpoint(
  eyeX: Double,
  eyeZ: Double,
  eyeHeight: Double,
  targetX: Double,
  targetZ: Double,
  targetHeight: Double
)

private[sakura] object DistrictViewpoint:
  private val slopeProbe = 0.01

  def atPitch(
    eyeX: Double,
    eyeZ: Double,
    eyeHeight: Double,
    targetX: Double,
    targetZ: Double,
    pitch: Double
  ): DistrictViewpoint =
    val dx       = targetX - eyeX
    val dz       = targetZ - eyeZ
    val distance = math.hypot(dx, dz)
    require(distance > 0.0, "district camera target must differ from its eye")

    val forwardX     = dx / distance
    val forwardZ     = dz / distance
    val eyeGround    = TerrainHeightmap.hillAt(eyeX, eyeZ)
    val targetGround = TerrainHeightmap.hillAt(targetX, targetZ)
    val terrainSlope =
      (TerrainHeightmap.hillAt(eyeX + forwardX * slopeProbe, eyeZ + forwardZ * slopeProbe) -
        TerrainHeightmap.hillAt(eyeX - forwardX * slopeProbe, eyeZ - forwardZ * slopeProbe)) /
        (2.0 * slopeProbe)
    // The flat aim point must compensate local grade and planet sag before the host bends it.
    val curvatureSag = distance * distance / (2.0 * PlanetCurvature.Default.radius.value)
    val targetHeight =
      eyeHeight + eyeGround - targetGround + distance * (terrainSlope + math.tan(pitch)) + curvatureSag

    DistrictViewpoint(eyeX, eyeZ, eyeHeight, targetX, targetZ, targetHeight)
  end atPitch
end DistrictViewpoint

/** Shared kit for the two hero pedestrian districts: the bent textured sign plane and the
  * instanced paper-lantern batch. Hosted here beside the first district the way
  * [[SceneAssembly]]'s file hosts the geometry builder both use.
  */
private[world] object DistrictSignage:

  /** A single-faced textured sign plane: authored flat, rotated to face `yaw` radians about y,
    * translated and bent onto the planet. `tiltZ` lifts one edge, which is the noren's caught-by-
    * air look.
    */
  def signPlane(
    width: Double,
    height: Double,
    x: Double,
    y: Double,
    z: Double,
    yaw: Double,
    curvature: PlanetCurvature,
    tiltZ: Double = 0.0
  ): BufferGeometryRef =
    val geometry = new PlaneGeometry(width, height)
    if tiltZ != 0.0 then geometry.rotateZ(tiltZ)
    geometry.rotateY(yaw)
    geometry.translate(x, y, z)
    WorldGeometry.bend(geometry, curvature)
    geometry

  def texturedMaterial(texture: TextureRef, doubleSided: Boolean): MeshBasicMaterial =
    new MeshBasicMaterial(
      MeshBasicMaterialParameters(
        map = js.defined(texture.asMaterialMap),
        side = if doubleSided then js.defined(2) else js.undefined
      )
    )

  /** Where one hanging lantern's paper body sits. */
  final case class LanternSpot(x: Double, y: Double, z: Double, radius: Double)

  /** One instanced batch of paper lanterns in one paper. The instances are bent onto the planet
    * host-side — a basic material has no bend of its own, and a lantern is small enough that the
    * bend across its own extent is sub-millimetre.
    */
  def lanternBatch(
    spots: Seq[LanternSpot],
    texture: TextureRef,
    curvature: PlanetCurvature
  ): (InstancedMeshRef, BufferGeometryRef, MeshBasicMaterial) =
    val geometry = new CylinderGeometry(1.0, 1.0, 2.1, 12.0, 1.0, false)
    val material = texturedMaterial(texture, doubleSided = false)
    val mesh     = new InstancedMesh(geometry, material, spots.size.toDouble)
    val position = new Vector3()
    val rotation = new Quaternion()
    val scale    = new Vector3()
    val matrix   = new Matrix4()
    spots.zipWithIndex.foreach { case (spot, index) =>
      position.set(spot.x, curvature.bend(spot.x, spot.y, spot.z), spot.z)
      rotation.setFromAxisAngle(asVector3Like(new Vector3(0.0, 1.0, 0.0)), 0.0)
      scale.set(spot.radius, spot.radius, spot.radius)
      matrix.compose(position, rotation, scale)
      mesh.setMatrixAt(index, matrix)
    }
    mesh.instanceMatrix.markNeedsUpdate()
    (mesh, geometry, material)
  end lanternBatch
end DistrictSignage

/** さくら坂商店街 — the Sakurazaka shopping street.
  *
  * A six-metre pedestrian lane off the west side of the crossing's approach: shops hard against
  * both kerbs, a slotted drain down the middle, festoon wires carrying paper lanterns overhead,
  * and the street closing on a blooming sakura at its north end. The layout half is pure data —
  * corridor, frontages, drain pitch, wire and lantern geometry, prop seating and the hero camera
  * framing — so the composition is testable without three.js. The render half batches the whole
  * street into one merged mesh per colour family, with the signage as bent textured planes and
  * the lanterns, wheels and canopies as instanced batches.
  */
object ShotengaiDistrict:

  import DistrictSignage.LanternSpot

  val CenterX: Double        = -45.0
  val LaneHalfWidth: Double  = 3.0
  val PavedWidth: Double     = 14.0
  val PavementWidth: Double  = 1.1
  val ZStart: Double         = 6.8
  val ZEnd: Double           = 40.0
  val FrontageDepth: Double  = 5.0
  val ApproachLength: Double = 0.6

  val WestKerbX: Double = CenterX - LaneHalfWidth
  val EastKerbX: Double = CenterX + LaneHalfWidth

  /** Surface height of the raised pavements above the terrain: everything stood on a pavement
    * seats here, not at the terrain, or it sinks its own depth into the kerb.
    */
  val PavementRise: Double = 0.17

  /** The lane rectangle between the two shopfront lines. */
  val laneRect: Rect = Rect(WestKerbX, EastKerbX, ZStart, ZEnd)

  /** The carriageway the drain crosses and nothing may park on. */
  val carriagewayRect: Rect =
    Rect(CenterX - (LaneHalfWidth - PavementWidth), CenterX + (LaneHalfWidth - PavementWidth), ZStart, ZEnd)

  final case class Rect(x0: Double, x1: Double, z0: Double, z1: Double):
    def contains(x: Double, z: Double): Boolean =
      x >= x0 && x <= x1 && z >= z0 && z <= z1
    /** True overlap, ignoring shared edges: a frontage standing hard against the kerb line is the
      * street's own construction, not a collision with it.
      */
    def crosses(other: Rect): Boolean =
      x0 < other.x1 && other.x0 < x1 && z0 < other.z1 && other.z0 < z1

  enum ShopKind:
    case Tail, Storage, House, Soba, Mid, Shirayuki, Tsubame

  final case class ShopFootprint(kind: ShopKind, x0: Double, x1: Double, z0: Double, z1: Double)

  val SobaShop: ShopFootprint =
    ShopFootprint(ShopKind.Soba, WestKerbX - FrontageDepth, WestKerbX, 32.4, 38.6)

  object HeroFrame:
    val UpstreamEyeZ: Double = 20.0
    val LocalEyeZ: Double    = (SobaShop.z0 + SobaShop.z1) / 2.0

    def localZ(upstreamZ: Double): Double = LocalEyeZ + UpstreamEyeZ - upstreamZ

  /** West row faces +x, east row faces -x; both stand clear of the lane rectangle. The signed
    * tenants — the soba shop and the bookstore — take the north end because the hero camera looks
    * south from there, which is the frame the reference composes.
    */
  val WestShops: List[ShopFootprint] = List(
    ShopFootprint(ShopKind.Tail, WestKerbX - FrontageDepth, WestKerbX, 12.0, 19.6),
    ShopFootprint(ShopKind.Storage, WestKerbX - FrontageDepth, WestKerbX, 20.0, 25.6),
    ShopFootprint(ShopKind.House, WestKerbX - FrontageDepth, WestKerbX, 26.0, 32.0),
    SobaShop
  )

  val EastShops: List[ShopFootprint] = List(
    ShopFootprint(ShopKind.Tail, EastKerbX, EastKerbX + FrontageDepth, 12.0, 19.1),
    ShopFootprint(ShopKind.Mid, EastKerbX, EastKerbX + FrontageDepth, 19.5, 27.0),
    ShopFootprint(ShopKind.Shirayuki, EastKerbX, EastKerbX + FrontageDepth, 27.5, 32.0),
    ShopFootprint(ShopKind.Tsubame, EastKerbX, EastKerbX + FrontageDepth, 32.4, 38.4)
  )

  /** The slotted channel down the lane's axis: the dark line that stops six metres of pale paving
    * reading as a blank corridor.
    */
  object Drain:
    val Pitch: Double      = 0.9
    val TileWidth: Double  = 0.34
    val TileLength: Double = 0.72
    val Rise: Double       = 0.105

    def tiles: Vector[(Double, Double)] =
      val count = math.floor((ZEnd - ZStart) / Pitch).toInt
      Vector.tabulate(count)(index => (CenterX, ZStart + (index + 0.5) * Pitch))

  object Festoon:
    val WireCount: Double     = 7.0
    val FirstZ: Double        = 14.8
    val Spacing: Double       = 3.7
    val HangHeight: Double    = 4.4
    val Overhang: Double      = 0.2
    val SagDepth: Double      = 0.35
    val LanternDrop: Double   = 0.30
    val LanternRadius: Double = 0.155
    val WireSegments: Int     = 8

    def wireZs: Vector[Double] =
      Vector.tabulate(WireCount.toInt)(index => FirstZ + index * Spacing)

    /** The festoon's dip at a proportional position along the wire. */
    def sagAt(t: Double): Double = SagDepth * math.sin(math.Pi * math.min(1.0, math.max(0.0, t)))

    def lanternT: List[Double] = List(0.32, 0.68)

    /** Wire x at a proportional position, between the anchor points just past each kerb. */
    def wireX(t: Double): Double =
      WestKerbX - Overhang + (EastKerbX - WestKerbX + 2.0 * Overhang) * t

    /** Every lantern: body centre x/z plus how far below its wire's anchor height it hangs. */
    def lanterns: Vector[(Double, Double, Double, Double)] =
      wireZs.flatMap { z =>
        lanternT.map { t =>
          (wireX(t), sagAt(t) + LanternDrop + LanternRadius * 1.05, z, LanternRadius)
        }
      }
  end Festoon

  /** The white kei truck parked nose-south in front of the plaster house. */
  object KeiVan:
    final case class Wiper(fromX: Double, fromY: Double, toX: Double, toY: Double)

    val Length: Double                = 3.32
    val Width: Double                 = 1.46
    val WheelRadius: Double           = 0.29
    val TyreWidth: Double             = 0.20
    val WheelZOffsets: Vector[Double] = Vector(-0.94, 1.06)
    val Wipers: Vector[Wiper] = Vector(
      Wiper(-0.28, 1.34, -0.08, 1.45),
      Wiper(0.08, 1.45, 0.28, 1.34)
    )
    val CenterX: Double = WestKerbX + 0.85
    val CenterZ: Double = 29.0

    val rect: Rect =
      Rect(CenterX - Width / 2.0, CenterX + Width / 2.0, CenterZ - Length / 2.0, CenterZ + Length / 2.0)

  object Freezer:
    val CenterX: Double = WestKerbX + 0.62
    val CenterZ: Double = HeroFrame.localZ(23.6)
    val Width: Double   = 0.66
    val Depth: Double   = 1.15

  object Benches:
    val Spots: List[(Double, Double)] = List((WestKerbX + 0.58, HeroFrame.localZ(26.2)))

  object Vending:
    val Spots: List[(Double, Double)] = List(
      (EastKerbX - 0.68, HeroFrame.localZ(30.5)), (EastKerbX - 0.68, HeroFrame.localZ(29.2))
    )

  object Bicycles:
    val CenterZ: Double = HeroFrame.localZ(26.2)
    val Spots: List[(Double, Double)] =
      List.tabulate(4)(index => (EastKerbX - 0.62, CenterZ + (index - 1.5) * 0.64))

  object BlueStall:
    val CenterX: Double = EastKerbX - 0.75
    val CenterZ: Double = 35.5

  object Gachapon:
    val CenterX: Double = EastKerbX - 0.62
    val CenterZ: Double = HeroFrame.localZ(31.4)
    val Count: Int      = 3
    val Spacing: Double = 0.42

    def spots: Vector[(Double, Double)] =
      Vector.tabulate(Count)(index => (CenterX, CenterZ + (index - 1) * Spacing))

  object BookCrates:
    val CenterX: Double                 = EastKerbX - 0.58
    val Spots: Vector[(Double, Double)] = Vector(23.2, 24.0).map(z => (CenterX, HeroFrame.localZ(z)))

  object ShopFlags:
    final case class Spot(x: Double, z: Double, variant: Int, facesEast: Boolean)

    val Spots: Vector[Spot] = Vector(
      Spot(WestKerbX + 0.65, HeroFrame.localZ(21.6), variant = 1, facesEast = true),
      Spot(EastKerbX - 0.65, HeroFrame.localZ(16.9), variant = 3, facesEast = false),
      Spot(EastKerbX - 0.65, HeroFrame.localZ(33.6), variant = 2, facesEast = false),
      Spot(WestKerbX + 0.65, HeroFrame.localZ(32.7), variant = 3, facesEast = true)
    )

  private val H1 = 3.2
  private val H2 = 2.7

  object ShopArchitecture:
    final case class Balcony(width: Double, projection: Double)
    final case class AirConditioner(centerZ: Double, floorY: Double)
    final case class Plan(
      footprint: ShopFootprint,
      facesEast: Boolean,
      hasUpperFloor: Boolean,
      windowCenters: Vector[Double],
      balcony: Option[Balcony],
      airConditioners: Vector[AirConditioner],
      displayLevels: Int
    )

    def forShop(footprint: ShopFootprint, facesEast: Boolean): Plan =
      val frontageLength = footprint.z1 - footprint.z0
      val hasUpperFloor  = footprint.kind != ShopKind.Storage
      val windowCount    = if hasUpperFloor then math.max(1, math.floor((frontageLength - 0.8) / 1.9).toInt) else 0
      val windowCenters = Vector.tabulate(windowCount)(index =>
        footprint.z0 + frontageLength * (index + 1) / (windowCount + 1).toDouble
      )
      val balcony =
        if Set(ShopKind.House, ShopKind.Shirayuki, ShopKind.Tsubame).contains(footprint.kind) then
          Some(Balcony(frontageLength * 0.72, 0.78))
        else None
      val conditionerCount = if hasUpperFloor then if footprint.kind == ShopKind.Tsubame then 2 else 1 else 0
      val airConditioners = Vector.tabulate(conditionerCount)(index =>
        AirConditioner(
          centerZ = footprint.z0 + frontageLength * (index + 1) / (conditionerCount + 1).toDouble,
          floorY = H1 + 0.72
        )
      )
      Plan(footprint, facesEast, hasUpperFloor, windowCenters, balcony, airConditioners, displayLevels = 2)

    val all: Vector[Plan] =
      (WestShops.map(footprint => forShop(footprint, facesEast = false)) ++
        EastShops.map(footprint => forShop(footprint, facesEast = true))).toVector
  end ShopArchitecture

  object StreetProps:
    final case class Planter(x: Double, z: Double, radius: Double, flowers: Boolean)
    final case class CrateStack(x: Double, z: Double, count: Int, yaw: Double)
    final case class Shadow(x: Double, z: Double, width: Double, depth: Double)

    val planters: Vector[Planter] = Vector(
      Planter(WestKerbX + 0.48, HeroFrame.localZ(16.8), 0.24, flowers = true),
      Planter(EastKerbX - 0.48, HeroFrame.localZ(18.4), 0.22, flowers = false),
      Planter(EastKerbX - 0.48, HeroFrame.localZ(39.0), 0.20, flowers = false)
    )
    val crateStacks: Vector[CrateStack] = Vector(
      CrateStack(WestKerbX + 0.48, HeroFrame.localZ(34.4), count = 3, yaw = -0.18),
      CrateStack(EastKerbX - 0.48, HeroFrame.localZ(21.4), count = 4, yaw = 0.25)
    )
    val shadows: Vector[Shadow] = Vector(
      Shadow(CenterX - 1.2, HeroFrame.localZ(24.6), 0.86, 0.34),
      Shadow(CenterX + 1.0, HeroFrame.localZ(27.1), 0.64, 0.48),
      Shadow(CenterX - 0.5, HeroFrame.localZ(30.2), 1.10, 0.28),
      Shadow(CenterX + 1.4, HeroFrame.localZ(33.0), 0.72, 0.36)
    )
  end StreetProps

  object UtilityLines:
    final case class Pole(x: Double, z: Double, height: Double)

    val poles: Vector[Pole] = Vector(21.0, 28.0, 35.0).map(upstreamZ =>
      Pole(WestKerbX + 0.42, HeroFrame.localZ(upstreamZ), 7.1)
    )
    val wireXs: Vector[Double] = Vector(WestKerbX + 0.3, CenterX, EastKerbX - 0.3)

  /** The street closes on a blooming sakura standing in the paving just south of the drain run's
    * end; the hero camera looks south from the north end so the soba row sits screen-left the way
    * the reference frame composes it.
    */
  val focalTree: CherryPlacement = CherryPlacement(-45.3, 6.55, 1.8, 8201L)

  val Viewpoint: DistrictViewpoint = DistrictViewpoint.atPitch(
    eyeX = CenterX - 0.35,
    eyeZ = HeroFrame.LocalEyeZ,
    eyeHeight = 1.62,
    targetX = CenterX - 0.25,
    targetZ = focalTree.z,
    pitch = 0.04
  )

  val SobaNorenKey: String         = "shotengai-soba-noren"
  val TsubameFasciaKey: String     = "shotengai-tsubame-fascia"
  val TsubameBladeKey: String      = "shotengai-tsubame-blade"
  val ShirayukiKey: String         = "shotengai-shirayuki-fascia"
  val LanternKey: String           = "shotengai-lantern"
  val VendingKey: String           = "shotengai-vending"
  val IceFlagKey: String           = "shotengai-ice-flag"
  val ShopFlag1Key: String         = "shotengai-shop-flag-1"
  val ShopFlag2Key: String         = "shotengai-shop-flag-2"
  val ShopFlag3Key: String         = "shotengai-shop-flag-3"
  val ShopFlagKeys: Vector[String] = Vector(IceFlagKey, ShopFlag1Key, ShopFlag2Key, ShopFlag3Key)
  val FreezerPanelKey: String      = "freezer-ice"
  val GachaponKey: String          = "gachapon-fascia"
  val KeiPlateKey: String          = "shotengai-kei-plate"

  private val AwningOrangeHex  = 0xe89428
  private val AwningCreamHex   = 0xf6f1e6
  private val WireHex          = 0x4c4658
  private val ApplianceDeepHex = 0xbdb9b0
  private val StallBlueHex     = 0x465478
  private val TyreHex          = 0x3b3843
  private val CanopyGreenHex   = 0x4a7c3c
  private val PlasterAltHex    = 0xe6ddc8

  private enum Part:
    case Lane, Pavement, Drain, Plaster, PlasterAlt, TimberDark, WoodPale, RoofTile, GlassDark,
      AwningOrange, AwningCream, MetalDark, ApplianceWhite, ApplianceDeep, ApplianceRed, AccentNavy, StallBlue,
      BicycleFrame, BicycleRubber, BookRed, BookBlue, BookGold, GachaRed, GachaYellow, GachaBlue, CrateGreen,
      FreezerGlass, LampWarm, LampRed, LampAmber, Planter, RoadShadow

  def build(
    curvature: PlanetCurvature,
    groundAt: (Double, Double) => Double,
    textures: TextureCache
  ): SceneAssembly =
    val parts = Part.values.map(part => part -> new GeometryBuilder()).toMap
    addSurfaces(parts, groundAt)
    addWestRow(parts, groundAt)
    addEastRow(parts, groundAt)
    addOverhead(parts, groundAt)
    addKeiVan(parts, groundAt)
    addFreezer(parts, groundAt)
    addBlueStall(parts, groundAt)
    addBench(parts, Benches.Spots, groundAt)
    addVending(parts, groundAt)
    addGachapon(parts, groundAt)
    addBookCrates(parts, groundAt)
    addShopFlags(parts, groundAt)
    addBicycles(parts, groundAt)
    addStreetDetails(parts, groundAt)

    val group          = new Group()
    val geometries     = List.newBuilder[BufferGeometryRef]
    val materials      = List.newBuilder[MaterialRef]
    val partMaterialBy = Part.values.map(part => part -> partMaterial(part, curvature)).toMap

    Part.values.foreach { part =>
      val geometry = parts(part).result()
      geometries += geometry
      materials += partMaterialBy(part)
      group.add(new Mesh(geometry, partMaterialBy(part)))
    }

    val (gachaMeshes, gachaGeometries, gachaMaterials) = gachaponDetails(textures.get(GachaponKey), curvature, groundAt)
    gachaMeshes.foreach(mesh => group.attach(mesh))
    geometries ++= gachaGeometries
    materials ++= gachaMaterials

    val (signGeometries, signMaterials) = addSignage(group, textures, curvature, groundAt)
    geometries ++= signGeometries
    materials ++= signMaterials

    val (lanterns, lanternGeometry, lanternMaterial) = DistrictSignage.lanternBatch(
      Festoon.lanterns.map { case (x, belowAnchor, z, radius) =>
        LanternSpot(x, groundAt(x, z) + Festoon.HangHeight - belowAnchor, z, radius)
      },
      textures.get(LanternKey),
      curvature
    )
    group.attach(lanterns)
    geometries += lanternGeometry
    materials += lanternMaterial

    val (wheels, wheelGeometry, wheelMaterial) = wheelBatch(curvature, groundAt)
    group.attach(wheels)
    geometries += wheelGeometry
    materials += wheelMaterial

    val greenGeometry = new SphereGeometry(1.0, 9.0, 7.0)
    val greenMaterial = ToonMaterial.instancedCelMaterial(CanopyGreenHex, curvature)
    val greenBatch    = SceneBatch.instancedMesh(greenGeometry, greenMaterial, greenCanopy(groundAt))
    group.attach(greenBatch)
    geometries += greenGeometry
    materials += greenMaterial

    val treeBatch = CherryTreeGenerator.build(CherryTreeGenerator.plan(List(focalTree)), curvature)
    treeBatch.roots.foreach(child => group.attach(child))
    geometries ++= treeBatch.geometries
    materials ++= treeBatch.materials

    SceneAssembly(group, geometries.result(), materials.result())
  end build

  private def addSurfaces(parts: Map[Part, GeometryBuilder], groundAt: (Double, Double) => Double): Unit =
    val rows    = 96
    val columns = (PavedWidth / 0.5).toInt
    val lane    = parts(Part.Lane)
    (0 until columns).foreach { column =>
      val x0 = CenterX - PavedWidth / 2.0 + PavedWidth * column / columns.toDouble
      val x1 = CenterX - PavedWidth / 2.0 + PavedWidth * (column + 1) / columns.toDouble
      (0 until rows).foreach { row =>
        val z0 = ZStart - ApproachLength + (ZEnd - ZStart + ApproachLength) * row / rows.toDouble
        val z1 = ZStart - ApproachLength + (ZEnd - ZStart + ApproachLength) * (row + 1) / rows.toDouble
        val cx = (x0 + x1) / 2.0
        val cz = (z0 + z1) / 2.0
        lane.addBox(cx, groundAt(cx, cz) + 0.05, cz, x1 - x0 + 0.02, 0.08, z1 - z0 + 0.02)
      }
    }

    val pavement = parts(Part.Pavement)
    List(-1.0, 1.0).foreach { side =>
      val cx = CenterX + side * (LaneHalfWidth - PavementWidth / 2.0)
      (0 until rows).foreach { row =>
        val z0 = ZStart + (ZEnd - ZStart) * row / rows.toDouble
        val z1 = ZStart + (ZEnd - ZStart) * (row + 1) / rows.toDouble
        val y0 = groundAt(cx, z0) + 0.13
        val y1 = groundAt(cx, z1) + 0.13
        pavement.addBox(cx, (y0 + y1) / 2.0, (z0 + z1) / 2.0, PavementWidth, (y1 - y0) + 0.08, z1 - z0 + 0.02)
      }
    }

    val drain = parts(Part.Drain)
    Drain.tiles.foreach { case (x, z) =>
      drain.addBox(x, groundAt(x, z) + Drain.Rise, z, Drain.TileWidth, 0.04, Drain.TileLength)
    }
  end addSurfaces

  /** One shophouse unit: a plaster volume with a shopfront recess, upper windows, a flat roof with
    * parapet, and the tenant's awning where it has one.
    */
  private def addShophouse(
    parts: Map[Part, GeometryBuilder],
    groundAt: (Double, Double) => Double,
    footprint: ShopFootprint,
    facesEast: Boolean,
    wallPart: Part,
    awningPart: Option[Part],
    shutter: Double
  ): Unit =
    val architecture = ShopArchitecture.forShop(footprint, facesEast)
    val s            = if architecture.facesEast then -1.0 else 1.0
    val frontageX    = if architecture.facesEast then EastKerbX else WestKerbX
    val w            = footprint.z1 - footprint.z0
    val cz           = (footprint.z0 + footprint.z1) / 2.0
    val volumeCx     = (footprint.x0 + footprint.x1) / 2.0
    val y            = groundAt(volumeCx, cz)
    val oneFloor     = !architecture.hasUpperFloor
    val height       = if oneFloor then H1 else H1 + H2

    val wall   = parts(wallPart)
    val recess = 0.9
    val depth  = footprint.x1 - footprint.x0
    wall.addBox(volumeCx - s * recess / 2.0, y + H1 / 2.0 - 0.1, cz, depth - recess, H1 - 0.2, w - 0.9)
    wall.addBox(volumeCx, y - 0.15, cz, depth + 0.16, 0.5, w + 0.16)
    if !oneFloor then
      val upperDepth = footprint.x1 - footprint.x0 - 0.4
      wall.addBox(frontageX - s * (0.2 + upperDepth / 2.0), y + H1 + H2 / 2.0, cz, upperDepth, H2, w - 0.3)
      parts(Part.TimberDark).addBox(frontageX - s * 0.24, y + H1 + 0.02, cz, 0.18, 0.16, w - 0.2)

    val roof = parts(Part.RoofTile)
    roof.addBox(volumeCx, y + height + 0.1, cz, footprint.x1 - footprint.x0 + 0.3, 0.2,
      (if oneFloor then w else w - 0.3) + 0.3)
    List(-1.0, 1.0).foreach { side =>
      roof.addBox(volumeCx, y + height + 0.37, cz + side * (w / 2.0 + 0.02), footprint.x1 - footprint.x0 + 0.3,
        0.34, 0.14)
    }
    roof.addBox(frontageX - s * 0.02, y + height + 0.37, cz, 0.14, 0.34, w + 0.16)
    List(-1.0, 1.0).foreach { side =>
      roof.addBox(volumeCx + side * ((footprint.x1 - footprint.x0) / 2.0 + 0.02), y + height + 0.37, cz, 0.14,
        0.34, w + 0.16)
    }

    val openW = w - 1.4
    val pierW = (w - openW) / 2.0
    List(-1.0, 1.0).foreach { side =>
      wall.addBox(frontageX - s * 0.45, y + H1 / 2.0 - 0.1, cz + side * (w - pierW) / 2.0, 0.9, H1 - 0.2, pierW)
      parts(Part.TimberDark).addBox(frontageX - s * 0.46, y + 0.31, cz + side * (w - pierW) / 2.0, 0.92, 0.62,
        pierW + 0.04)
    }
    wall.addBox(frontageX - s * 0.45, y + H1 - 0.3, cz, 0.9, 0.6, openW)
    parts(Part.WoodPale).addBox(frontageX - s * 0.45, y + 0.08, cz, 0.9, 0.12, openW)
    parts(Part.WoodPale).addBox(frontageX + s * 0.25, y + 0.11, cz, 0.5, 0.12, 1.6)

    parts(Part.GlassDark).addBox(frontageX - s * 0.85, y + 1.35, cz, 0.05, 2.2, openW - 0.1)
    parts(Part.GlassDark).addBox(frontageX - s * 0.07, y + 1.35, cz, 0.04, 2.3, openW)
    val mullions = math.max(2.0, math.floor(openW / 1.3)).toInt
    (0 to mullions).foreach { index =>
      parts(Part.MetalDark).addBox(frontageX - s * 0.09, y + 1.35, cz - openW / 2.0 + openW * index / mullions,
        0.1, 2.35, 0.08)
    }
    addShopDisplay(parts, architecture, frontageX, s, y, openW, cz)

    if shutter > 0.0 then
      val shutterHeight = 2.55 * shutter
      parts(Part.MetalDark).addBox(frontageX - s * 0.16, y + 2.55 - shutterHeight / 2.0, cz, 0.06, shutterHeight,
        openW - 0.1)
      parts(Part.MetalDark).addBox(frontageX - s * 0.16, y + 2.66, cz, 0.2, 0.28, openW + 0.14)

    if !oneFloor then
      architecture.windowCenters.foreach { pz =>
        parts(Part.TimberDark).addBox(frontageX - s * 0.26, y + H1 + 1.4, pz, 0.16, 1.3, 1.5)
        parts(Part.GlassDark).addBox(frontageX - s * 0.21, y + H1 + 1.4, pz, 0.06, 1.14, 1.34)
        parts(Part.TimberDark).addBox(frontageX - s * 0.20, y + H1 + 0.72, pz, 0.22, 0.09, 1.7)
      }
      architecture.balcony.foreach(plan => addBalcony(parts, frontageX, s, cz, y, plan))
      architecture.airConditioners.foreach(plan => addAirConditioner(parts, frontageX, s, y, plan))

    awningPart.foreach(part => addAwning(parts, part, frontageX, s, cz, w, y))
  end addShophouse

  private def addShopDisplay(
    parts: Map[Part, GeometryBuilder],
    plan: ShopArchitecture.Plan,
    frontageX: Double,
    outward: Double,
    ground: Double,
    openingWidth: Double,
    centerZ: Double
  ): Unit =
    val counterX = frontageX - outward * 0.62
    parts(Part.WoodPale).addBox(counterX, ground + 0.52, centerZ, 0.62, 0.12, openingWidth - 0.72)
    val productParts = Vector(Part.BookRed, Part.BookBlue, Part.BookGold, Part.ApplianceRed, Part.GachaYellow)
    val itemCount    = math.max(3, math.floor((openingWidth - 1.0) / 0.62).toInt)
    (0 until plan.displayLevels).foreach { level =>
      val shelfX = frontageX - outward * (1.45 + level * 0.42)
      val shelfY = ground + 0.88 + level * 0.52
      parts(Part.WoodPale).addBox(shelfX, shelfY, centerZ, 0.12, 0.07, openingWidth - 0.86)
      (0 until itemCount).foreach { index =>
        val z = centerZ - (openingWidth - 1.1) / 2.0 + (openingWidth - 1.1) * (index + 0.5) / itemCount
        parts(productParts((index + level) % productParts.size)).addBox(
          shelfX,
          shelfY + 0.15,
          z,
          0.17,
          0.20 + (index % 2) * 0.06,
          0.16
        )
      }
    }
  end addShopDisplay

  private def addBalcony(
    parts: Map[Part, GeometryBuilder],
    frontageX: Double,
    outward: Double,
    centerZ: Double,
    ground: Double,
    balcony: ShopArchitecture.Balcony
  ): Unit =
    val floorY = ground + H1 + 0.54
    val outerX = frontageX + outward * balcony.projection
    parts(Part.WoodPale).addBox(frontageX + outward * balcony.projection / 2.0, floorY, centerZ,
      balcony.projection, 0.12, balcony.width)
    parts(Part.TimberDark).addBox(outerX, floorY + 0.45, centerZ, 0.08, 0.08, balcony.width)
    parts(Part.WoodPale).addBox(outerX, floorY + 0.89, centerZ, 0.08, 0.08, balcony.width)
    val postCount = math.max(3, math.floor(balcony.width / 0.42).toInt)
    (0 to postCount).foreach { index =>
      val z = centerZ - balcony.width / 2.0 + balcony.width * index / postCount
      parts(Part.TimberDark).addBox(outerX, floorY + 0.67, z, 0.06, 0.48, 0.06)
    }
    List(-1.0, 1.0).foreach { side =>
      val z = centerZ + side * balcony.width / 2.0
      parts(Part.TimberDark).addBeam(frontageX, floorY + 0.45, z, outerX, floorY + 0.45, z, 0.05)
    }
  end addBalcony

  private def addAirConditioner(
    parts: Map[Part, GeometryBuilder],
    frontageX: Double,
    outward: Double,
    ground: Double,
    unit: ShopArchitecture.AirConditioner
  ): Unit =
    val bodyX = frontageX + outward * 0.32
    parts(Part.ApplianceWhite).addBox(bodyX, ground + unit.floorY, unit.centerZ, 0.62, 0.50, 0.76)
    parts(Part.ApplianceDeep).addBox(bodyX + outward * 0.33, ground + unit.floorY, unit.centerZ, 0.06, 0.30, 0.50)
    (-2 to 2).foreach { index =>
      parts(Part.MetalDark).addBox(bodyX + outward * 0.37, ground + unit.floorY + index * 0.055,
        unit.centerZ, 0.025, 0.025, 0.48)
    }
    List(-1.0, 1.0).foreach { side =>
      parts(Part.MetalDark).addBeam(frontageX - outward * 0.12, ground + unit.floorY - 0.30,
        unit.centerZ + side * 0.24, bodyX, ground + unit.floorY - 0.30,
        unit.centerZ + side * 0.24, 0.035)
    }
    parts(Part.MetalDark).addBeam(frontageX - outward * 0.04, ground + H1 - 0.2, unit.centerZ + 0.34,
      frontageX - outward * 0.04, ground + unit.floorY, unit.centerZ + 0.34, 0.025)
  end addAirConditioner

  private def addStreetDetails(parts: Map[Part, GeometryBuilder], groundAt: (Double, Double) => Double): Unit =
    StreetProps.planters.foreach { planter =>
      val ground = groundAt(planter.x, planter.z) + PavementRise
      parts(Part.Planter).addCylinder(planter.x, ground + planter.radius * 0.72, planter.z,
        planter.radius * 0.78, planter.radius, planter.radius * 1.44, 10.0)
      parts(Part.TimberDark).addCylinder(planter.x, ground + planter.radius * 1.40, planter.z,
        planter.radius * 0.85, planter.radius * 0.85, planter.radius * 0.12, 10.0)
      val leaves = if planter.flowers then 5 else 4
      (0 until leaves).foreach { index =>
        val angle = 2.0 * math.Pi * index / leaves
        val x0    = planter.x
        val z0    = planter.z
        val x1    = planter.x + math.cos(angle) * planter.radius * 0.75
        val z1    = planter.z + math.sin(angle) * planter.radius * 0.75
        parts(Part.CrateGreen).addBeam(x0, ground + planter.radius * 1.42, z0, x1,
          ground + planter.radius * 2.2, z1, 0.07)
        if planter.flowers then
          parts(Part.LampWarm).addBox(x1, ground + planter.radius * 2.18, z1, 0.08, 0.08, 0.08)
      }
    }
    StreetProps.crateStacks.foreach { stack =>
      (0 until stack.count).foreach { index =>
        val ground = groundAt(stack.x, stack.z) + PavementRise
        val y      = ground + 0.14 + index * 0.28
        val dx     = math.sin(stack.yaw) * index * 0.035
        val dz     = math.cos(stack.yaw) * index * 0.035
        parts(Part.CrateGreen).addBox(stack.x + dx, y, stack.z + dz, 0.50, 0.28, 0.34)
        parts(Part.WoodPale).addBox(stack.x + dx, y + 0.02, stack.z + dz, 0.52, 0.04, 0.36)
        List(-1.0, 1.0).foreach { side =>
          parts(Part.WoodPale).addBox(stack.x + dx, y, stack.z + dz + side * 0.14, 0.46, 0.18, 0.035)
        }
      }
    }
    StreetProps.shadows.foreach { shadow =>
      val ground = groundAt(shadow.x, shadow.z)
      parts(Part.RoadShadow).addBox(shadow.x, ground + 0.095, shadow.z, shadow.width, 0.012, shadow.depth)
    }
    addUtilityLines(parts, groundAt)
  end addStreetDetails

  private def addUtilityLines(parts: Map[Part, GeometryBuilder], groundAt: (Double, Double) => Double): Unit =
    val poleMaterial = parts(Part.TimberDark)
    val wireMaterial = parts(Part.MetalDark)
    UtilityLines.poles.foreach { pole =>
      val base = groundAt(pole.x, pole.z)
      poleMaterial.addBox(pole.x, base + pole.height / 2.0, pole.z, 0.18, pole.height, 0.18)
      poleMaterial.addBox((pole.x + EastKerbX) / 2.0, base + pole.height - 0.24, pole.z,
        EastKerbX - pole.x + 0.2, 0.12, 0.12)
      UtilityLines.wireXs.foreach { x =>
        wireMaterial.addBox(x, base + pole.height - 0.25, pole.z, 0.07, 0.12, 0.08)
      }
    }
    UtilityLines.wireXs.foreach { x =>
      UtilityLines.poles.zip(UtilityLines.poles.tail).foreach { case (near, far) =>
        (0 until 8).foreach { index =>
          val t0   = index / 8.0
          val t1   = (index + 1) / 8.0
          val z0   = near.z + (far.z - near.z) * t0
          val z1   = near.z + (far.z - near.z) * t1
          val sag0 = 0.16 * math.sin(math.Pi * t0)
          val sag1 = 0.16 * math.sin(math.Pi * t1)
          val y0   = groundAt(x, z0) + 6.85 - sag0
          val y1   = groundAt(x, z1) + 6.85 - sag1
          wireMaterial.addBeam(x, y0, z0, x, y1, z1, 0.025)
        }
      }
    }
  end addUtilityLines

  /** A scalloped fabric awning as a stepped slope: five overlapping slices out from the fascia, a
    * hem bar at the low edge, and alternating scallop flaps below it.
    */
  private def addAwning(
    parts: Map[Part, GeometryBuilder],
    part: Part,
    frontageX: Double,
    s: Double,
    cz: Double,
    w: Double,
    y: Double
  ): Unit =
    val out         = 1.4
    val drop        = 0.4
    val yA          = y + H1 - 0.16
    val stripeCount = if part == Part.StallBlue then math.max(6, math.round((w - 0.3) / 0.42).toInt) else 1
    val stripeWidth = (w - 0.3) / stripeCount
    (0 until stripeCount).foreach { stripe =>
      val stripePart = if part == Part.StallBlue && stripe % 2 == 1 then Part.AwningCream else part
      val stripeZ    = cz - (w - 0.3) / 2.0 + (stripe + 0.5) * stripeWidth
      (0 until 5).foreach { index =>
        parts(stripePart).addBox(frontageX + s * ((index + 0.5) * out / 5.0),
          yA - index * drop / 5.0 + 0.035, stripeZ, out / 5.0 + 0.06, 0.07, stripeWidth + 0.02)
      }
    }
    val edgeX = frontageX + s * out
    val edgeY = yA - drop
    parts(part).addBox(edgeX, edgeY - 0.13, cz, 0.07, 0.30, w - 0.3)
    val scallops = math.round((w - 0.3) / 0.5).toInt
    (0 until scallops).foreach { index =>
      val sz   = cz - (w - 0.3) / 2.0 + (w - 0.3) * (index + 0.5) / scallops
      val hang = if index % 2 == 0 then 0.10 else 0.02
      val scallopPart =
        if part == Part.StallBlue && index % 2 == 1 then Part.AwningCream else part
      parts(scallopPart).addBox(edgeX, edgeY - 0.30 - hang, sz, 0.06, 0.12, (w - 0.3) / scallops - 0.06)
    }
    List(-1.0, 1.0).foreach { side =>
      parts(Part.MetalDark).addBox(frontageX + s * out / 2.0, yA - drop / 2.0 - 0.04, cz + side * (w / 2.0 - 0.5),
        out, 0.06, 0.06)
    }
  end addAwning

  private def addWestRow(parts: Map[Part, GeometryBuilder], groundAt: (Double, Double) => Double): Unit =
    addShophouse(parts, groundAt, WestShops(0), facesEast = false, Part.Plaster, awningPart = None, shutter = 0.0)
    addShophouse(parts, groundAt, WestShops(1), facesEast = false, Part.Plaster, awningPart = None, shutter = 0.34)
    addShophouse(parts, groundAt, WestShops(2), facesEast = false, Part.PlasterAlt, awningPart = None, shutter = 0.0)
    addShophouse(parts, groundAt, SobaShop, facesEast = false, Part.PlasterAlt,
      awningPart = Some(Part.AwningOrange), shutter = 0.0)

    val soba = SobaShop
    val cz   = (soba.z0 + soba.z1) / 2.0
    val y    = groundAt((soba.x0 + soba.x1) / 2.0, cz)
    parts(Part.MetalDark).addBox(WestKerbX - 0.04, y + 2.44, cz, 0.05, 0.05, 1.9)
  end addWestRow

  private def addEastRow(parts: Map[Part, GeometryBuilder], groundAt: (Double, Double) => Double): Unit =
    addShophouse(parts, groundAt, EastShops(0), facesEast = true, Part.PlasterAlt, awningPart = None, shutter = 0.0)
    addShophouse(parts, groundAt, EastShops(1), facesEast = true, Part.Plaster, awningPart = None, shutter = 0.0)
    addShophouse(parts, groundAt, EastShops(2), facesEast = true, Part.PlasterAlt,
      awningPart = Some(Part.StallBlue), shutter = 0.34)
    addShophouse(parts, groundAt, EastShops(3), facesEast = true, Part.Plaster,
      awningPart = Some(Part.AwningCream), shutter = 0.0)

    val tsubame = EastShops(3)
    val cz      = (tsubame.z0 + tsubame.z1) / 2.0
    val y       = groundAt((tsubame.x0 + tsubame.x1) / 2.0, cz)
    val bladeZ  = tsubame.z1 - 0.45
    parts(Part.MetalDark).addBox(EastKerbX - 0.19, y + 4.6, bladeZ, 0.7, 0.08, 0.08)
    parts(Part.MetalDark).addBox(EastKerbX - 0.19, y + 2.9, bladeZ, 0.7, 0.08, 0.08)
  end addEastRow

  /** The overhead: seven sagging festoon wires across the lane — the lantern batch hangs off them
    * — and a longitudinal cable pair the length of the street.
    */
  private def addOverhead(parts: Map[Part, GeometryBuilder], groundAt: (Double, Double) => Double): Unit =
    val wire = parts(Part.MetalDark)
    Festoon.wireZs.foreach { z =>
      val anchorY = groundAt(CenterX, z) + Festoon.HangHeight
      (0 until Festoon.WireSegments).foreach { index =>
        val t0 = index.toDouble / Festoon.WireSegments
        val t1 = (index + 1).toDouble / Festoon.WireSegments
        wire.addBeam(Festoon.wireX(t0), anchorY - Festoon.sagAt(t0), z, Festoon.wireX(t1),
          anchorY - Festoon.sagAt(t1), z, 0.016)
      }
    }
    Festoon.lanterns.foreach { case (x, belowAnchor, z, radius) =>
      val anchorY = groundAt(x, z) + Festoon.HangHeight
      wire.addBox(x, anchorY - (belowAnchor - radius) / 2.0 - radius, z, 0.02,
        belowAnchor - radius * 2.05, 0.02)
    }
    List(WestKerbX + 0.15, EastKerbX - 0.15).foreach { x =>
      var previousZ = ZStart
      val startY    = groundAt(x, ZStart) + 5.1
      (1 to 8).foreach { index =>
        val z   = ZStart + (ZEnd - ZStart) * index / 8.0
        val sag = if index % 2 == 0 then 0.12 else 0.0
        wire.addBeam(x, startY - (if (index - 1) % 2 == 0 then 0.12 else 0.0), previousZ, x, startY - sag, z, 0.02)
        previousZ = z
      }
    }
  end addOverhead

  /** The bent textured planes: noren, fascias, blade, vending fronts, ice flag and the van's
    * plates.
    */
  private def addSignage(
    group: GroupRef,
    textures: TextureCache,
    curvature: PlanetCurvature,
    groundAt: (Double, Double) => Double
  ): (List[BufferGeometryRef], List[MaterialRef]) =
    val geometries = List.newBuilder[BufferGeometryRef]
    val materials  = List.newBuilder[MaterialRef]

    def mount(geometry: BufferGeometryRef, texture: TextureRef, doubleSided: Boolean = false): Unit =
      val material = DistrictSignage.texturedMaterial(texture, doubleSided)
      group.add(new Mesh(geometry, material))
      geometries += geometry
      materials += material

    val soba   = SobaShop
    val sobaY  = groundAt((soba.x0 + soba.x1) / 2.0, (soba.z0 + soba.z1) / 2.0)
    val norenZ = soba.z0 + 0.55
    mount(
      DistrictSignage.signPlane(1.9, 0.72, WestKerbX + 1.0, sobaY + 2.06, norenZ,
        yaw = math.Pi / 2.0, curvature = curvature, tiltZ = 0.025),
      textures.get(SobaNorenKey),
      doubleSided = true
    )

    val tsubame  = EastShops(3)
    val tsubameY = groundAt((tsubame.x0 + tsubame.x1) / 2.0, (tsubame.z0 + tsubame.z1) / 2.0)
    mount(
      DistrictSignage.signPlane(tsubame.z1 - tsubame.z0 - 0.3, 0.72, EastKerbX - 0.02, tsubameY + H1 + 0.44,
        (tsubame.z0 + tsubame.z1) / 2.0, yaw = -math.Pi / 2.0, curvature = curvature),
      textures.get(TsubameFasciaKey)
    )
    mount(
      DistrictSignage.signPlane(0.52, 1.9, EastKerbX - 0.38, tsubameY + 3.75, tsubame.z1 - 0.45, yaw = 0.0,
        curvature = curvature),
      textures.get(TsubameBladeKey),
      doubleSided = true
    )
    ShopFlags.Spots.foreach { spot =>
      val flagY   = groundAt(spot.x, spot.z) + PavementRise
      val outward = if spot.facesEast then 1.0 else -1.0
      mount(
        DistrictSignage.signPlane(0.38, 1.0, spot.x + outward * 0.2, flagY + 1.25, spot.z,
          yaw = if spot.facesEast then math.Pi / 2.0 else -math.Pi / 2.0, curvature = curvature),
        textures.get(ShopFlagKeys(spot.variant)),
        doubleSided = true
      )
    }

    val shirayuki  = EastShops(2)
    val shirayukiY = groundAt((shirayuki.x0 + shirayuki.x1) / 2.0, (shirayuki.z0 + shirayuki.z1) / 2.0)
    mount(
      DistrictSignage.signPlane(shirayuki.z1 - shirayuki.z0 - 0.3, 0.72, EastKerbX - 0.02,
        shirayukiY + H1 + 0.44, (shirayuki.z0 + shirayuki.z1) / 2.0, yaw = -math.Pi / 2.0, curvature = curvature),
      textures.get(ShirayukiKey)
    )

    Vending.Spots.foreach { case (x, z) =>
      mount(
        DistrictSignage.signPlane(0.72, 1.2, x - 0.37, groundAt(x, z) + 1.05, z, yaw = -math.Pi / 2.0,
          curvature = curvature),
        textures.get(VendingKey)
      )
    }

    val freezerY = groundAt(Freezer.CenterX, Freezer.CenterZ)
    mount(
      DistrictSignage.signPlane(0.72, 0.22, Freezer.CenterX + 0.34, freezerY + PavementRise + 0.79,
        Freezer.CenterZ - 0.20, yaw = math.Pi / 2.0, curvature = curvature),
      textures.get(FreezerPanelKey)
    )
    mount(
      DistrictSignage.signPlane(0.40, 0.55, Freezer.CenterX + 0.34, freezerY + PavementRise + 0.42,
        Freezer.CenterZ + 0.34, yaw = math.Pi / 2.0, curvature = curvature),
      textures.get(IceFlagKey)
    )

    val vanY = groundAt(KeiVan.CenterX, KeiVan.CenterZ) + 0.13
    mount(
      DistrictSignage.signPlane(0.40, 0.20, KeiVan.CenterX, vanY + 0.95, KeiVan.CenterZ - KeiVan.Length / 2.0 - 0.06,
        yaw = math.Pi, curvature = curvature),
      textures.get(KeiPlateKey)
    )
    mount(
      DistrictSignage.signPlane(0.40, 0.20, KeiVan.CenterX, vanY + 0.95, KeiVan.CenterZ + KeiVan.Length / 2.0 + 0.06,
        yaw = 0.0, curvature = curvature),
      textures.get(KeiPlateKey)
    )

    (geometries.result(), materials.result())
  end addSignage

  /** The kei truck, authored nose-north toward the hero camera: cab, glazed box, bed and bumpers.
    */
  private def addKeiVan(parts: Map[Part, GeometryBuilder], groundAt: (Double, Double) => Double): Unit =
    val x    = KeiVan.CenterX
    val z    = KeiVan.CenterZ
    val y    = groundAt(x, z) + 0.13
    val half = KeiVan.Width / 2.0
    val nose = z + KeiVan.Length / 2.0

    val body = parts(Part.ApplianceWhite)
    body.addBox(x, y + 0.68, z, KeiVan.Width, 0.34, KeiVan.Length)
    body.addBox(x, y + 1.38, nose - 0.98, KeiVan.Width, 1.06, 1.26)
    body.addBox(x, y + 1.9, nose - 0.98, KeiVan.Width + 0.06, 0.1, 1.3)
    List(-1.0, 1.0).foreach { side =>
      body.addBox(x + side * (half - 0.035), y + 1.06, z - 0.62, 0.07, 0.42, 1.86)
    }
    body.addBox(x, y + 1.06, z - 1.55, KeiVan.Width - 0.06, 0.42, 0.07)

    val deep = parts(Part.ApplianceDeep)
    deep.addBox(x, y + 1.62, nose - 0.34, KeiVan.Width - 0.16, 0.62, 0.08)
    deep.addBox(x, y + 0.56, z, KeiVan.Width - 0.2, 0.16, KeiVan.Length - 0.3)
    deep.addBox(x - 0.28, y + 1.02, nose - 1.24, 0.38, 0.26, 0.32)
    deep.addBox(x + 0.28, y + 1.02, nose - 1.24, 0.38, 0.26, 0.32)
    deep.addBox(x, y + 1.28, nose - 0.72, KeiVan.Width - 0.18, 0.12, 0.20)

    val glass = parts(Part.GlassDark)
    glass.addBox(x, y + 1.62, nose - 0.31, KeiVan.Width - 0.16, 0.62, 0.06)
    List(-1.0, 1.0).foreach { side =>
      glass.addBox(x + side * (half - 0.02), y + 1.6, nose - 0.85, 0.06, 0.56, 1.0)
    }

    val dark = parts(Part.MetalDark)
    List(-1.0, 1.0).foreach { side =>
      dark.addBox(x + side * 0.48, y + 1.0, nose + 0.03, 0.26, 0.16, 0.06)
      dark.addBox(x + side * (half + 0.14), y + 1.92, nose - 0.66, 0.11, 0.16, 0.05)
      dark.addBox(x + side * (half + 0.06), y + 1.86, nose - 0.66, 0.2, 0.04, 0.04)
      dark.addBox(x + side * (half - 0.02), y + 0.62, nose - 0.55, 0.1, 0.1, 0.72)
      parts(Part.GlassDark).addBox(x + side * (half + 0.15), y + 1.92, nose - 0.66, 0.035, 0.10, 0.10)
    }
    dark.addBox(x, y + 0.78, nose - 0.03, KeiVan.Width - 0.1, 0.16, 0.1)
    dark.addBox(x, y + 0.50, nose, 0.5, 0.26, 0.10)
    KeiVan.Wipers.foreach { wiper =>
      dark.addBeam(x + wiper.fromX, y + wiper.fromY, nose - 0.27, x + wiper.toX, y + wiper.toY,
        nose - 0.27, 0.018)
    }
    KeiVan.WheelZOffsets.foreach { offset =>
      List(-1.0, 1.0).foreach { side =>
        val wheelX = x + side * (half + 0.015)
        val wheelY = y + KeiVan.WheelRadius
        (0 until 8).foreach { index =>
          val angle0 = math.Pi * index / 8.0
          val angle1 = math.Pi * (index + 1) / 8.0
          dark.addBeam(wheelX, wheelY + (KeiVan.WheelRadius + 0.07) * math.sin(angle0),
            z + offset -
              (KeiVan.WheelRadius + 0.07) * math.cos(angle0),
            wheelX,
            wheelY + (KeiVan.WheelRadius + 0.07) * math.sin(angle1),
            z + offset -
              (KeiVan.WheelRadius + 0.07) * math.cos(angle1),
            0.035)
        }
      }
    }

    parts(Part.LampWarm).addBox(x - 0.44, y + 1.0, nose + 0.01, 0.26, 0.15, 0.05)
    parts(Part.LampWarm).addBox(x + 0.44, y + 1.0, nose + 0.01, 0.26, 0.15, 0.05)
    parts(Part.LampAmber).addBox(x - 0.27, y + 1.0, nose + 0.02, 0.12, 0.12, 0.05)
    parts(Part.LampAmber).addBox(x + 0.27, y + 1.0, nose + 0.02, 0.12, 0.12, 0.05)
    List(-1.0, 1.0).foreach { side =>
      parts(Part.LampRed).addBox(x + side * 0.48, y + 1.0, nose - KeiVan.Length + 0.01, 0.2, 0.13, 0.05)
      parts(Part.LampAmber).addBox(x + side * (half + 0.01), y + 0.92, nose - 0.38, 0.05, 0.10, 0.12)
    }

    parts(Part.WoodPale).addBox(x, y + 1.04, z - 0.64, 0.34, 0.26, 0.42)
    parts(Part.WoodPale).addBox(x, y + 1.30, z - 0.79, 0.34, 0.26, 0.42)
  end addKeiVan

  private def addFreezer(parts: Map[Part, GeometryBuilder], groundAt: (Double, Double) => Double): Unit =
    val x = Freezer.CenterX
    val z = Freezer.CenterZ
    val y = groundAt(x, z) + PavementRise

    parts(Part.ApplianceWhite).addBox(x, y + 0.43, z, Freezer.Width, 0.86, Freezer.Depth)
    parts(Part.ApplianceRed).addBox(x, y + 0.60, z, Freezer.Width + 0.02, 0.20, Freezer.Depth + 0.02)
    List(-1.0, 1.0).foreach { side =>
      parts(Part.ApplianceDeep).addBox(x + side * 0.31, y + 0.88, z, 0.08, 0.06, 1.19)
      parts(Part.ApplianceDeep).addBox(x, y + 0.88, z + side * 0.55, 0.66, 0.06, 0.09)
    }
    parts(Part.FreezerGlass).addBox(x, y + 0.885, z, 0.54, 0.03, 1.0)

    val wrapperParts = Vector(Part.GachaRed, Part.GachaBlue, Part.GachaYellow, Part.StallBlue,
      Part.LampRed, Part.AwningOrange)
    (0 until 9).foreach { index =>
      val part   = wrapperParts(index % wrapperParts.size)
      val localX = -0.44 + (index % 5) * 0.22
      val localZ = -0.14 + (index / 5) * 0.22
      parts(part).addBox(x - localZ, y + 0.80, z + localX, 0.16, 0.05, 0.11)
    }
  end addFreezer

  private def addBlueStall(parts: Map[Part, GeometryBuilder], groundAt: (Double, Double) => Double): Unit =
    val x    = BlueStall.CenterX
    val z    = BlueStall.CenterZ
    val y    = groundAt(x, z) + PavementRise
    val body = parts(Part.StallBlue)
    body.addBox(x, y + 0.78, z, 0.82, 1.56, 1.45)
    body.addBox(x, y + 1.62, z, 1.10, 0.12, 1.78)
    body.addBox(x - 0.46, y + 1.18, z, 0.08, 0.55, 1.12)
    parts(Part.GlassDark).addBox(x - 0.43, y + 1.22, z, 0.04, 0.48, 0.98)
    parts(Part.WoodPale).addBox(x - 0.50, y + 0.88, z, 0.16, 0.09, 1.56)
  end addBlueStall

  private def addBench(
    parts: Map[Part, GeometryBuilder],
    spots: List[(Double, Double)],
    groundAt: (Double, Double) => Double
  ): Unit =
    spots.foreach { case (x, z) =>
      val y = groundAt(x, z) + PavementRise
      (0 until 3).foreach { index =>
        parts(Part.WoodPale).addBox(x - 0.19 + index * 0.19, y + 0.42, z, 0.15, 0.03, 1.5)
      }
      List(-1.0, 1.0).foreach { side =>
        parts(Part.MetalDark).addBox(x, y + 0.20, z + side * 0.62, 0.5, 0.40, 0.05)
      }
    }
  end addBench

  private def addVending(parts: Map[Part, GeometryBuilder], groundAt: (Double, Double) => Double): Unit =
    Vending.Spots.foreach { case (x, z) =>
      val y = groundAt(x, z) + PavementRise
      parts(Part.AccentNavy).addBox(x, y + 0.93, z, 0.72, 1.86, 0.98)
      parts(Part.LampWarm).addBox(x - 0.37, y + 1.62, z, 0.03, 0.26, 0.74)
      parts(Part.MetalDark).addBox(x, y + 0.03, z, 0.76, 0.06, 1.02)
    }
  end addVending

  private def addGachapon(parts: Map[Part, GeometryBuilder], groundAt: (Double, Double) => Double): Unit =
    val bodyParts = Vector(Part.GachaRed, Part.GachaYellow, Part.GachaBlue)
    Gachapon.spots.zipWithIndex.foreach { case ((x, z), index) =>
      val y = groundAt(x, z) + PavementRise
      parts(bodyParts(index)).addBox(x, y + 0.71, z, 0.44, 1.42, 0.40)
      parts(Part.MetalDark).addBox(x - 0.23, y + 0.62, z, 0.06, 0.24, 0.36)
      parts(Part.MetalDark).addBox(x - 0.24, y + 0.76, z, 0.06, 0.07, 0.12)
      val knob = new CylinderGeometry(0.05, 0.05, 0.06, 8.0)
      knob.rotateZ(math.Pi / 2.0)
      knob.translate(x - 0.27, y + 0.78, z)
      parts(Part.MetalDark).addMerged(knob)
      knob.dispose()
      parts(Part.ApplianceWhite).addBox(x - 0.24, y + 0.38, z, 0.04, 0.16, 0.30)
      parts(Part.MetalDark).addBox(x, y + 0.03, z, 0.50, 0.06, 0.46)
    }
  end addGachapon

  private def addBookCrates(parts: Map[Part, GeometryBuilder], groundAt: (Double, Double) => Double): Unit =
    val books = Vector(Part.BookRed, Part.BookBlue, Part.BookGold, Part.WoodPale, Part.BookRed)
    BookCrates.Spots.foreach { case (x, z) =>
      val y = groundAt(x, z) + PavementRise
      parts(Part.TimberDark).addBox(x, y + 0.16, z, 0.62, 0.32, 0.46)
      parts(Part.WoodPale).addBox(x, y + 0.31, z, 0.58, 0.025, 0.42)
      List(-1.0, 1.0).foreach { side =>
        parts(Part.WoodPale).addBox(x, y + 0.17, z + side * 0.20, 0.56, 0.22, 0.045)
      }
      books.zipWithIndex.foreach { case (part, index) =>
        val bookX  = x - 0.23 + index * 0.115
        val height = 0.28 + (index % 2) * 0.06
        parts(part).addBox(bookX, y + 0.32 + height / 2.0, z, 0.09, height, 0.30)
        parts(Part.WoodPale).addBox(bookX, y + 0.43, z + 0.153, 0.055, 0.025, 0.012)
      }
    }
  end addBookCrates

  private def addShopFlags(parts: Map[Part, GeometryBuilder], groundAt: (Double, Double) => Double): Unit =
    ShopFlags.Spots.foreach { spot =>
      val y       = groundAt(spot.x, spot.z) + PavementRise
      val outward = if spot.facesEast then 1.0 else -1.0
      parts(Part.MetalDark).addCylinder(spot.x, y + 0.95, spot.z, 0.022, 0.022, 1.9, 5.0)
      parts(Part.Pavement).addCylinder(spot.x, y + 0.05, spot.z, 0.13, 0.15, 0.1, 10.0)
      parts(Part.MetalDark).addBeam(spot.x, y + 1.73, spot.z, spot.x + outward * 0.36,
        y + 1.73, spot.z, 0.018)
    }
  end addShopFlags

  private def addBicycles(parts: Map[Part, GeometryBuilder], groundAt: (Double, Double) => Double): Unit =
    val frame  = parts(Part.BicycleFrame)
    val rubber = parts(Part.BicycleRubber)
    val metal  = parts(Part.MetalDark)

    Bicycles.Spots.foreach { case (x, z) =>
      val y      = groundAt(x, z) + PavementRise
      val rear   = (-0.52, 0.348, 0.0)
      val front  = (0.55, 0.33, 0.0)
      val bb     = (-0.10, 0.28, 0.0)
      val seat   = (-0.27, 0.86, 0.0)
      val fork   = (0.44, 0.60, 0.0)
      val head   = (0.49, 0.86, 0.0)
      val bars   = (0.46, 0.97, 0.0)
      val saddle = (-0.31, 1.00, 0.0)

      bicycleTube(frame, x, y, z, bb, seat, 0.026)
      bicycleTube(frame, x, y, z, bb, fork, 0.028)
      bicycleTube(frame, x, y, z, fork, head, 0.03)
      bicycleTube(frame, x, y, z, seat, head, 0.024)
      List(-1.0, 1.0).foreach { side =>
        bicycleTube(frame, x, y, z, (bb._1, bb._2, side * 0.075), (rear._1, rear._2, side * 0.052), 0.019)
        bicycleTube(frame, x, y, z, (seat._1, seat._2, side * 0.03), (rear._1, rear._2, side * 0.052), 0.017)
        bicycleTube(frame, x, y, z, (fork._1, fork._2, side * 0.045), (front._1, front._2, side * 0.05), 0.017)
      }
      bicycleTube(metal, x, y, z, seat, saddle, 0.019)
      bicycleTube(metal, x, y, z, head, bars, 0.021)
      bicycleTube(metal, x, y, z, (bars._1, bars._2, -0.27), (bars._1, bars._2, 0.27), 0.019)

      List(rear, front).foreach { case (hubX, hubY, _) =>
        val samples = 18
        (0 until samples).foreach { index =>
          val angle0 = math.Pi * 2.0 * index / samples
          val angle1 = math.Pi * 2.0 * (index + 1) / samples
          val x0     = x + hubX + 0.33 * math.cos(angle0)
          val y0     = y + hubY + 0.33 * math.sin(angle0)
          val x1     = x + hubX + 0.33 * math.cos(angle1)
          val y1     = y + hubY + 0.33 * math.sin(angle1)
          rubber.addBeam(x0, y0, z, x1, y1, z, 0.022)
        }
        (0 until 8).foreach { index =>
          val angle = math.Pi * 2.0 * index / 8.0
          val rimX  = x + hubX + 0.31 * math.cos(angle)
          val rimY  = y + hubY + 0.31 * math.sin(angle)
          bicycleTube(metal, x, y, z + 0.04, (hubX, hubY, 0.0), (rimX - x, rimY - y, 0.0), 0.008)
        }
        metal.addBeam(x + hubX, y + hubY, z - 0.07, x + hubX, y + hubY, z + 0.07, 0.032)
        (0 until 8).foreach { index =>
          val angle0 = math.Pi * index / 8.0
          val angle1 = math.Pi * (index + 1) / 8.0
          bicycleTube(metal, x, y, z, (hubX + 0.38 * math.cos(angle0), hubY + 0.38 * math.sin(angle0), 0.0),
            (hubX + 0.38 * math.cos(angle1), hubY + 0.38 * math.sin(angle1), 0.0), 0.013)
        }
      }

      bicycleTube(metal, x, y, z, bars, (bars._1 + 0.05, bars._2 + 0.03, -0.12), 0.014)
      metal.addBox(x + saddle._1 - 0.02, y + saddle._2 + 0.04, z, 0.23, 0.055, 0.12)
      bicycleTube(metal, x, y, z, (bb._1, bb._2, 0.0), (bb._1, bb._2 - 0.15, 0.075), 0.013)
      metal.addBox(x + bb._1, y + bb._2 - 0.15, z + 0.105, 0.09, 0.02, 0.05)
      bicycleTube(metal, x, y, z, (bb._1, bb._2, 0.0), (bb._1, bb._2 + 0.15, -0.075), 0.013)
      metal.addBox(x + bb._1, y + bb._2 + 0.15, z - 0.105, 0.09, 0.02, 0.05)

      metal.addBox(x + 0.69, y + 0.87, z, 0.28, 0.025, 0.24)
      for side <- List(-1.0, 1.0) do
        bicycleTube(metal, x, y, z, (0.55, 0.74, side * 0.12), (0.69, 0.97, side * 0.12), 0.012)
        bicycleTube(metal, x, y, z, (0.69, 0.75, side * 0.12), (0.69, 0.97, side * 0.12), 0.012)
        bicycleTube(metal, x, y, z, (-0.39, 0.76, side * 0.068), (seat._1, seat._2 - 0.09, side * 0.03), 0.011)
      metal.addBeam(x + 0.55, y + 0.75, z - 0.12, x + 0.55, y + 0.95, z - 0.12, 0.012)
      metal.addBeam(x + 0.55, y + 0.75, z + 0.12, x + 0.55, y + 0.95, z + 0.12, 0.012)
    }
  end addBicycles

  private def bicycleTube(
    builder: GeometryBuilder,
    x: Double,
    y: Double,
    z: Double,
    from: (Double, Double, Double),
    to: (Double, Double, Double),
    radius: Double
  ): Unit =
    builder.addBeam(x + from._1, y + from._2, z + from._3, x + to._1, y + to._2, z + to._3, radius)

  private def gachaponDetails(
    texture: TextureRef,
    curvature: PlanetCurvature,
    groundAt: (Double, Double) => Double
  ): (List[InstancedMeshRef], List[BufferGeometryRef], List[MaterialRef]) =
    val geometry = new SphereGeometry(0.19, 12.0, 8.0)
    val material = DistrictSignage.texturedMaterial(texture, doubleSided = false)
    val globes   = new InstancedMesh(geometry, material, Gachapon.spots.size.toDouble)
    val position = new Vector3()
    val rotation = new Quaternion()
    val scale    = new Vector3()
    val matrix   = new Matrix4()
    Gachapon.spots.zipWithIndex.foreach { case ((x, z), index) =>
      val y      = groundAt(x, z) + PavementRise + 1.14
      val globeX = x - 0.06
      position.set(globeX, curvature.bend(globeX, y, z), z)
      rotation.setFromAxisAngle(asVector3Like(new Vector3(0.0, 1.0, 0.0)), -math.Pi / 2.0)
      scale.set(1.0, 1.0, 1.0)
      matrix.compose(position, rotation, scale)
      globes.setMatrixAt(index, matrix)
    }
    globes.instanceMatrix.markNeedsUpdate()
    (List(globes), List(geometry), List(material))
  end gachaponDetails

  /** The van's four rubber tyres; bicycle wheels are modelled as open rims and spokes above. */
  private def wheelBatch(
    curvature: PlanetCurvature,
    groundAt: (Double, Double) => Double
  ): (InstancedMeshRef, BufferGeometryRef, MaterialRef) =
    val geometry  = new CylinderGeometry(1.0, 1.0, 1.0, 12.0, 1.0, false)
    val material  = ToonMaterial.instancedCelMaterial(TyreHex, curvature)
    val vanGround = groundAt(KeiVan.CenterX, KeiVan.CenterZ) + 0.13
    val vanWheels = KeiVan.WheelZOffsets.flatMap { offset =>
      List(-1.0, 1.0).map { side =>
        wheel(KeiVan.CenterX + side * (KeiVan.Width / 2.0 - 0.10), vanGround + KeiVan.WheelRadius,
          KeiVan.CenterZ + offset, KeiVan.WheelRadius, KeiVan.TyreWidth, axisX = false)
      }
    }
    val mesh = SceneBatch.instancedMesh(geometry, material, vanWheels)
    (mesh, geometry, material)
  end wheelBatch

  private def wheel(
    x: Double,
    y: Double,
    z: Double,
    radius: Double,
    width: Double,
    axisX: Boolean
  ): PlannedTransform =
    val axis = if axisX then (1.0, 0.0, 0.0) else (0.0, 0.0, 1.0)
    PlannedTransform(position = (x, y, z), axis = axis, angle = math.Pi / 2.0, scale = (radius, width, radius))

  /** Green foliage tucked under the focal tree's blossom so the canopy reads as a tree in bloom
    * rather than a pink cloud.
    */
  private def greenCanopy(groundAt: (Double, Double) => Double): Seq[PlannedTransform] =
    val ground = groundAt(focalTree.x, focalTree.z)
    def blob(dx: Double, dy: Double, dz: Double, radius: Double): PlannedTransform =
      PlannedTransform(
        position = (focalTree.x + dx * focalTree.scale, ground + dy * focalTree.scale,
          focalTree.z + dz * focalTree.scale),
        axis = (0.936, 0.352, 0.0),
        angle = 2.1,
        scale = (radius * focalTree.scale, radius * focalTree.scale * 0.8, radius * focalTree.scale)
      )
    Vector(
      blob(-0.9, 4.4, 0.3, 1.15),
      blob(0.8, 4.1, -0.4, 0.95),
      blob(0.0, 5.3, 0.7, 1.05),
      blob(-0.4, 5.8, -0.5, 0.85),
      blob(0.6, 5.0, 0.5, 0.8)
    )

  private def partMaterial(part: Part, curvature: PlanetCurvature): MaterialRef =
    part match
      case Part.Lane           => ToonMaterial.celMaterial(Palette.asphalt, curvature)
      case Part.Pavement       => ToonMaterial.celMaterial(Palette.concrete, curvature)
      case Part.Drain          => ToonMaterial.celMaterial(0x3f434b, curvature)
      case Part.Plaster        => ToonMaterial.celMaterial(Palette.plaster, curvature)
      case Part.PlasterAlt     => ToonMaterial.celMaterial(PlasterAltHex, curvature)
      case Part.TimberDark     => ToonMaterial.celMaterial(Palette.darkTimber, curvature)
      case Part.WoodPale       => ToonMaterial.celMaterial(0x9c7f5e, curvature)
      case Part.RoofTile       => ToonMaterial.celMaterial(Palette.roofTile, curvature)
      case Part.GlassDark      => ToonMaterial.flatMaterial(Palette.trainWindow, curvature)
      case Part.AwningOrange   => ToonMaterial.celMaterial(AwningOrangeHex, curvature)
      case Part.AwningCream    => ToonMaterial.celMaterial(AwningCreamHex, curvature)
      case Part.MetalDark      => ToonMaterial.celMaterial(WireHex, curvature)
      case Part.ApplianceWhite => ToonMaterial.celMaterial(0xf2eee6, curvature)
      case Part.ApplianceDeep  => ToonMaterial.celMaterial(ApplianceDeepHex, curvature)
      case Part.ApplianceRed   => ToonMaterial.celMaterial(0xd8564e, curvature)
      case Part.AccentNavy     => ToonMaterial.celMaterial(0x2c3a52, curvature)
      case Part.StallBlue      => ToonMaterial.celMaterial(StallBlueHex, curvature)
      case Part.BicycleFrame   => ToonMaterial.celMaterial(0x3f6f9c, curvature)
      case Part.BicycleRubber  => ToonMaterial.celMaterial(TyreHex, curvature)
      case Part.BookRed        => ToonMaterial.celMaterial(0xb5322f, curvature)
      case Part.BookBlue       => ToonMaterial.celMaterial(0x2c3a52, curvature)
      case Part.BookGold       => ToonMaterial.celMaterial(0xc08a3e, curvature)
      case Part.CrateGreen     => ToonMaterial.celMaterial(0x4f9d6a, curvature)
      case Part.GachaRed       => ToonMaterial.celMaterial(0xe0453f, curvature)
      case Part.GachaYellow    => ToonMaterial.celMaterial(0xf4c033, curvature)
      case Part.GachaBlue      => ToonMaterial.celMaterial(0x3d6ec4, curvature)
      case Part.FreezerGlass =>
        val glass = ToonMaterial.flatMaterial(0xdff0f8, curvature)
        glass.transparent = true
        glass.opacity = 0.4
        glass.depthWrite = false
        glass
      case Part.LampWarm   => ToonMaterial.flatMaterial(0xfff2d4, curvature)
      case Part.LampRed    => ToonMaterial.flatMaterial(0xf06050, curvature)
      case Part.LampAmber  => ToonMaterial.flatMaterial(0xffb347, curvature)
      case Part.Planter    => ToonMaterial.celMaterial(0xb06a4c, curvature)
      case Part.RoadShadow => ToonMaterial.flatMaterial(0x5c5968, curvature)
end ShotengaiDistrict
