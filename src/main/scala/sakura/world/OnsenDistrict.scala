package sakura.world

import sakura.{Palette, PlanetCurvature, ProceduralTextures, TextureCache, ToonMaterial}
import sakura.ThreeInterop.*
import sakura.foliage.{PlannedTransform, SceneBatch}
import vendor.three.*

import scala.math

/** 湯の坂 — the Yunosaka onsen lane.
  *
  * A four-and-a-half-metre stone pedestrian lane running west from the approach, flanked by
  * two-storey dark-timber machiya with lattice shopfronts and deep eaves, closing on a viewing
  * deck and a stone lantern under green and blossom canopies. Chosen against the shopping street
  * everywhere the two could read the same: stone instead of asphalt, timber and tile instead of
  * enamel and plastic, lanterns on eave brackets instead of strung across the sky. The layout
  * half is pure data like the shotengai's; the render half batches per colour family.
  */
object OnsenDistrict:

  import DistrictSignage.LanternSpot

  val CenterZ: Double       = 35.0
  val LaneHalfWidth: Double = 2.25
  val XWest: Double         = 30.0
  val XEast: Double         = 52.0
  val SouthDepth: Double    = 4.6
  val NorthDepth: Double    = 4.8

  val SouthFrontageZ: Double = CenterZ - LaneHalfWidth
  val NorthFrontageZ: Double = CenterZ + LaneHalfWidth

  val laneRect: ShotengaiDistrict.Rect =
    ShotengaiDistrict.Rect(XWest, XEast, SouthFrontageZ, NorthFrontageZ)

  enum MachiyaKind:
    case Yunoka, Kokeshi, Tail, Sakuraan, Mid, Ryokan, Bathhouse

  final case class MachiyaFootprint(kind: MachiyaKind, x0: Double, x1: Double, z0: Double, z1: Double)

  /** South row faces +z, north row faces -z; both stand clear of the lane rectangle. */
  val YunokaShop: MachiyaFootprint =
    MachiyaFootprint(MachiyaKind.Yunoka, 28.2, 32.6, SouthFrontageZ - SouthDepth, SouthFrontageZ)
  val KokeshiShop: MachiyaFootprint =
    MachiyaFootprint(MachiyaKind.Kokeshi, 33.0, 37.5, SouthFrontageZ - SouthDepth, SouthFrontageZ)

  val SouthShops: List[MachiyaFootprint] = List(
    YunokaShop,
    KokeshiShop,
    MachiyaFootprint(MachiyaKind.Tail, 39.5, 44.0, SouthFrontageZ - SouthDepth, SouthFrontageZ)
  )

  val SakuraanShop: MachiyaFootprint =
    MachiyaFootprint(MachiyaKind.Sakuraan, 34.0, 39.0, NorthFrontageZ, NorthFrontageZ + NorthDepth)

  val NorthShops: List[MachiyaFootprint] = List(
    MachiyaFootprint(MachiyaKind.Tail, 28.2, 33.0, NorthFrontageZ, NorthFrontageZ + NorthDepth),
    SakuraanShop,
    MachiyaFootprint(MachiyaKind.Mid, 39.5, 44.5, NorthFrontageZ, NorthFrontageZ + NorthDepth)
  )

  object Facilities:
    val Ryokan: MachiyaFootprint =
      MachiyaFootprint(MachiyaKind.Ryokan, 22.8, 27.0, NorthFrontageZ, NorthFrontageZ + NorthDepth)
    val Bathhouse: MachiyaFootprint =
      MachiyaFootprint(MachiyaKind.Bathhouse, 22.8, 27.0, SouthFrontageZ - SouthDepth, SouthFrontageZ)
    val all: Vector[(MachiyaFootprint, Boolean)] = Vector((Ryokan, false), (Bathhouse, true))

  /** The stone paving: three staggered courses of slabs across the lane's width. */
  object Paving:
    val Width: Double      = 8.0
    val SlabWidth: Double  = 1.4
    val SlabLength: Double = 1.42
    val Gap: Double        = 0.07
    val Rise: Double       = 0.05

    def rowZs: Vector[Double] = Vector(33.5, 35.0, 36.5)

    /** (x, z) of every slab, staggered half a pitch per course so the joints do not line up. */
    def slabs: Vector[(Double, Double)] =
      rowZs.zipWithIndex.flatMap { case (z, row) =>
        val pitch = SlabLength + Gap
        val start = XWest - 0.4 + (if row % 2 == 0 then 0.0 else pitch / 2.0)
        val count = math.floor((XEast - XWest + 0.8) / pitch).toInt
        (0 until count).map(index => (start + index * pitch + SlabLength / 2.0, z))
      }

    /** Dark tiles laid in the centre course's joints: the inlay line that keeps the stone from
      * reading as a blank field.
      */
    def inlay: Vector[(Double, Double)] =
      val pitch = SlabLength + Gap
      val count = math.floor((XEast - XWest + 0.8) / pitch).toInt
      (0 until count).map(index => (XWest - 0.4 + index * pitch, 35.0)).toVector
  end Paving

  /** The litter of fallen blossom on the stone. */
  object Petals:
    val Count: Int   = 130
    val Size: Double = 0.11

    def spots: Vector[(Double, Double)] =
      ProceduralTextures.PetalScatter.placements(Count, XWest - 0.5, SouthFrontageZ - 0.3,
        XEast - XWest + 1.0, NorthFrontageZ - SouthFrontageZ + 0.6, seed = 0x51ce)

  /** みやげ こけし堂's trestle of eleven dolls in six colours with painted heads. */
  object Kokeshi:
    val TableTopY: Double  = 0.83
    val TableX0: Double    = 33.75
    val TableX1: Double    = 36.75
    val TableZ: Double     = SouthFrontageZ + 0.62
    val Count: Int         = 11
    val FirstRowCount: Int = 6
    val BodyRadius: Double = 0.065
    val HeadRadius: Double = 0.062

    val BodyColors: Vector[Int] =
      Vector(0xb5322f, 0xf4c033, 0x2c3a52, 0xefe2c8, 0x8a4a62, 0x4f8f6a)
    val HeadHex: Int = 0xf0e2cc

    final case class Doll(x: Double, z: Double, colorIndex: Int, height: Double, row: Int)

    def row: Vector[Doll] =
      Vector.tabulate(Count) { index =>
        val row      = if index < FirstRowCount then 0 else 1
        val column   = index                % FirstRowCount
        val rowCount = if row == 0 then FirstRowCount else Count - FirstRowCount
        val x        = TableX0 + (TableX1 - TableX0) * (column + 0.5) / rowCount
        val height   = 0.18 + ((index * 37) % 13) / 13.0 * 0.12
        val rowZ     = if row == 0 then TableZ - 0.16 else TableZ + 0.18
        Doll(x, rowZ, index % BodyColors.size, height, row)
      }
  end Kokeshi

  /** The 湯 lanterns hang off the eaves on brackets, both frontages. */
  object Lanterns:
    val HangHeight: Double    = 3.0
    val Radius: Double        = 0.16
    val SouthXs: List[Double] = List(34.0, 36.8, 41.2, 44.0)
    val NorthXs: List[Double] = List(34.8, 37.0, 39.2)

    def spots: Vector[(Double, Double)] =
      (SouthXs.map((_, SouthFrontageZ + 0.30)) ++ NorthXs.map((_, NorthFrontageZ - 0.30))).toVector

  /** The viewing deck and the 石灯籠 behind its rail, both on the lane's axis. */
  object Deck:
    val X0: Double     = 27.2
    val X1: Double     = 30.4
    val Z0: Double     = 33.4
    val Z1: Double     = 36.6
    val Height: Double = 0.55

    final case class Brace(x0: Double, y0: Double, z: Double, x1: Double, y1: Double)

    val lanternX: Double     = 26.4
    val lanternScale: Double = 1.9

    def braces(groundY: Double): Vector[Brace] =
      val x0   = X0 + 0.22
      val x1   = X1 - 0.22
      val low  = groundY + 0.08
      val high = groundY + Height - 0.08
      Vector(-1.0, 1.0).flatMap { side =>
        val z = (Z0 + Z1) / 2.0 + side * ((Z1 - Z0) / 2.0 - 0.06)
        Vector(Brace(x0, low, z, x1, high), Brace(x0, high, z, x1, low))
      }
  end Deck

  /** The overhead canopy: green and blossom clusters leaning over the lane from both flanks, on
    * dark limbs. Everything clears the eaves.
    */
  object Canopy:
    val Clearance: Double = 6.5

    final case class Tree(x: Double, z: Double, scale: Double, seed: Long, crownBase: Double)

    val trees: List[Tree] = List(
      Tree(32.0, 31.7, 1.5, 9301L, Clearance + 0.3),
      Tree(32.0, 39.4, 1.4, 9302L, Clearance + 0.3),
      Tree(29.0, 31.7, 1.35, 9303L, Clearance + 0.3),
      Tree(29.0, 39.4, 1.3, 9304L, Clearance + 0.3)
    )

    val deckFramingTrees: List[Tree] = List(
      Tree(24.0, 30.2, 0.8, 9305L, Clearance + 0.5),
      Tree(24.0, 39.8, 0.8, 9306L, Clearance + 0.5)
    )

    /** One tree's contribution: limb segments, green blobs and blossom blobs, leaning toward the
      * lane axis. Pure data — the batch assembly consumes it directly.
      */
    def plan(groundAt: (Double, Double) => Double): (
      Vector[PlannedTransform],
      Vector[PlannedTransform],
      Vector[PlannedTransform]
    ) =
      val limbs  = Vector.newBuilder[PlannedTransform]
      val greens = Vector.newBuilder[PlannedTransform]
      val pinks  = Vector.newBuilder[PlannedTransform]
      (trees ++ deckFramingTrees).foreach { tree =>
        val towardLane = math.signum(CenterZ - tree.z)
        val groundY    = groundAt(tree.x, tree.z)
        val trunkH     = Clearance
        val crownBase  = tree.crownBase
        val branchX    = tree.x + towardLane * tree.scale
        val branchZ    = tree.z + towardLane * 1.6 * tree.scale
        limbs += limb(tree.x, groundY, tree.z, tree.x, groundY + trunkH, tree.z, 0.16 * tree.scale)
        limbs += limb(tree.x, groundY + trunkH, tree.z, branchX,
          groundAt(branchX, branchZ) + trunkH + 1.4 * tree.scale, branchZ, 0.09 * tree.scale)
        greens += blob(groundAt, tree.x, crownBase + 0.4 * tree.scale,
          tree.z + towardLane * 1.8 * tree.scale, 1.5 * tree.scale)
        greens += blob(groundAt, tree.x - 0.9 * tree.scale, crownBase,
          tree.z + towardLane * 1.0 * tree.scale, 1.1 * tree.scale)
        greens += blob(groundAt, tree.x + 0.8 * tree.scale, crownBase + 0.2 * tree.scale,
          tree.z + towardLane * 1.2 * tree.scale, 1.2 * tree.scale)
        greens += blob(groundAt, tree.x, crownBase + 0.7 * tree.scale,
          tree.z + towardLane * 0.8 * tree.scale, 1.0 * tree.scale)
        pinks += blob(groundAt, tree.x - 0.7 * tree.scale, crownBase + 0.1 * tree.scale,
          tree.z + towardLane * 2.2 * tree.scale, 1.2 * tree.scale)
        pinks += blob(groundAt, tree.x - 0.2 * tree.scale, crownBase + 0.2 * tree.scale,
          tree.z + towardLane * 2.5 * tree.scale, 1.2 * tree.scale)
        pinks += blob(groundAt, tree.x + 1.1 * tree.scale, crownBase + 0.3 * tree.scale,
          tree.z + towardLane * 0.7 * tree.scale, 0.85 * tree.scale)
      }
      (limbs.result(), greens.result(), pinks.result())
    end plan

    private def limb(
      x0: Double,
      y0: Double,
      z0: Double,
      x1: Double,
      y1: Double,
      z1: Double,
      radius: Double
    ): PlannedTransform =
      val dx         = x1 - x0
      val dy         = y1 - y0
      val dz         = z1 - z0
      val length     = math.sqrt(dx * dx + dy * dy + dz * dz)
      val axisLength = math.sqrt(dx * dx + dz * dz)
      if axisLength < 1e-6 then
        PlannedTransform((x1, y1 - length / 2.0, z1), (0.0, 0.0, 1.0), 0.0, (radius, length, radius))
      else
        val axis  = (dz / axisLength, 0.0, -dx / axisLength)
        val angle = math.acos(math.min(1.0, math.max(-1.0, dy / length)))
        PlannedTransform(
          ((x0 + x1) / 2.0, (y0 + y1) / 2.0, (z0 + z1) / 2.0),
          axis,
          angle, (radius, length, radius)
        )
    end limb

    private def blob(
      groundAt: (Double, Double) => Double,
      x: Double,
      heightAboveGround: Double,
      z: Double,
      radius: Double
    ): PlannedTransform =
      PlannedTransform((x, groundAt(x, z) + heightAboveGround, z), (0.83, 0.55, 0.1), 2.3, (radius, radius * 0.8, radius))
  end Canopy

  val Viewpoint: DistrictViewpoint = DistrictViewpoint(
    eyeX = 39.2,
    eyeZ = CenterZ,
    eyeHeight = 1.62,
    targetX = Deck.lanternX,
    targetZ = CenterZ,
    targetHeight = 1.62
  )

  private val H1 = 3.0
  private val H2 = 2.4

  object BuildingPlans:
    final case class Plan(
      footprint: MachiyaFootprint,
      facesSouth: Boolean,
      groundFloorHeight: Double,
      upperFloorHeight: Double,
      hasUpperFloor: Boolean,
      hasBalcony: Boolean,
      windowCenters: Vector[Double]
    )

    def forUnit(footprint: MachiyaFootprint, facesSouth: Boolean): Plan =
      val hasUpperFloor = footprint.kind match
        case MachiyaKind.Kokeshi | MachiyaKind.Bathhouse => false
        case _                                           => true
      val groundFloorHeight = if footprint.kind == MachiyaKind.Bathhouse then H1 + 1.1 else H1
      val upperFloorHeight  = if footprint.kind == MachiyaKind.Ryokan then H2 + 0.3 else H2
      val hasBalcony        = footprint.kind == MachiyaKind.Sakuraan || footprint.kind == MachiyaKind.Ryokan
      val width             = footprint.x1 - footprint.x0
      val windowCenters =
        if hasUpperFloor then Vector(footprint.x0 + width / 3.0, footprint.x0 + width * 2.0 / 3.0)
        else Vector.empty
      Plan(footprint, facesSouth, groundFloorHeight, upperFloorHeight, hasUpperFloor, hasBalcony, windowCenters)

    val all: Vector[Plan] =
      (SouthShops.map(unit => forUnit(unit, facesSouth = true)) ++
        NorthShops.map(unit => forUnit(unit, facesSouth = false)) ++
        Facilities.all.map { case (unit, facesSouth) => forUnit(unit, facesSouth) }).toVector
  end BuildingPlans

  object Channel:
    final case class Plan(
      centerX: Double,
      halfWidth: Double,
      z0: Double,
      z1: Double,
      bridgeSpan: Double,
      bridgeWidth: Double
    )

    val layout: Plan = Plan(
      centerX = 31.6,
      halfWidth = 0.42,
      z0 = SouthFrontageZ + 0.08,
      z1 = NorthFrontageZ - 0.08,
      bridgeSpan = 2.8,
      bridgeWidth = 3.4
    )

    def contains(x: Double, z: Double): Boolean =
      x >= layout.centerX - layout.halfWidth && x <= layout.centerX + layout.halfWidth &&
        z >= layout.z0 && z <= layout.z1
  end Channel

  object Footbath:
    final case class Plan(centerX: Double, centerZ: Double, width: Double, depth: Double, waterDepth: Double)

    val plan: Plan = Plan(centerX = 34.0, centerZ = NorthFrontageZ - 0.62, width = 1.45, depth = 0.82,
      waterDepth = 0.32)

  object StreetProps:
    final case class Planter(x: Double, z: Double, radius: Double, flowers: Boolean)
    final case class CrateStack(x: Double, z: Double, count: Int, yaw: Double)
    final case class CatBox(x: Double, z: Double, yaw: Double)
    final case class SignPost(x: Double, z: Double, height: Double)

    val planters: Vector[Planter] = Vector(
      Planter(33.0, NorthFrontageZ - 0.55, 0.24, flowers = true),
      Planter(28.9, SouthFrontageZ + 0.48, 0.22, flowers = false)
    )
    val crateStacks: Vector[CrateStack] = Vector(
      CrateStack(38.35, NorthFrontageZ - 0.55, count = 2, yaw = 0.2),
      CrateStack(38.0, SouthFrontageZ + 0.62, count = 3, yaw = -0.5)
    )
    val catBoxes: Vector[CatBox]    = Vector(CatBox(39.2, SouthFrontageZ + 0.72, yaw = -0.7))
    val signPosts: Vector[SignPost] = Vector(SignPost(33.35, SouthFrontageZ + 0.70, height = 1.3))

  object Dapple:
    final case class Patch(x: Double, z: Double, width: Double, depth: Double)

    val patches: Vector[Patch] = Vector(
      Patch(35.6, 34.0, 0.82, 0.32),
      Patch(37.0, 35.1, 0.56, 0.38),
      Patch(34.5, 36.2, 0.96, 0.28),
      Patch(36.2, 37.0, 0.62, 0.34),
      Patch(39.0, 35.8, 0.72, 0.30)
    )

  val KokeshiFasciaKey: String = "onsen-kokeshi-fascia"
  val KokeshiBladeKey: String  = "onsen-kokeshi-blade"
  val YunokaFasciaKey: String  = "onsen-yunoka-fascia"
  val YunokaBladeKey: String   = "onsen-yunoka-blade"
  val SakuraanKey: String      = "onsen-sakuraan-fascia"
  val SakuraanBladeKey: String = "onsen-sakuraan-blade"
  val KanmiKey: String         = "onsen-kanmi-sign"
  val LanternKey: String       = "onsen-lantern"

  private val SlabHex        = 0x8d97a4
  private val LaneBaseHex    = 0x686e82
  private val InlayHex       = 0x3c4148
  private val StoneHex       = 0x9aa0a8
  private val StoneDarkHex   = 0x6e747c
  private val WoodHex        = 0xb99a72
  private val PlasterHex     = 0xe8e2d4
  private val PlasterAltHex  = 0xd8cfbc
  private val TileEdgeHex    = 0x2c3844
  private val GlassWarmHex   = 0xf6dfae
  private val CanopyGreenHex = 0x4a7c3c

  private enum Part:
    case LaneBase, Slab, Inlay, StoneDark, Stone, TimberDark, Wood, Plaster, PlasterAlt, Tile,
      TileEdge, GlassWarm, Petal, Water, Planter, Leaf, Crate, CatBox, CatFur, Dapple, LanternGlow, Steam,
      LacquerRed

  def build(
    curvature: PlanetCurvature,
    groundAt: (Double, Double) => Double,
    textures: TextureCache
  ): SceneAssembly =
    val parts = Part.values.map(part => part -> new GeometryBuilder()).toMap
    addPaving(parts, groundAt)
    SouthShops.foreach(footprint => addMachiya(parts, groundAt, footprint, facesSouth = true))
    NorthShops.foreach(footprint => addMachiya(parts, groundAt, footprint, facesSouth = false))
    Facilities.all.foreach { case (footprint, facesSouth) => addMachiya(parts, groundAt, footprint, facesSouth) }
    addChannel(parts, groundAt)
    addBridge(parts, groundAt)
    addFootbath(parts, groundAt)
    addStreetProps(parts, groundAt)
    addKokeshiTable(parts, groundAt)
    addBenches(parts, groundAt)
    addDeck(parts, groundAt)
    addStoneLantern(parts, groundAt)
    addLanternBrackets(parts, groundAt)

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

    val (signGeometries, signMaterials) = addSignage(group, textures, curvature, groundAt)
    geometries ++= signGeometries
    materials ++= signMaterials

    val (lanterns, lanternGeometry, lanternMaterial) = DistrictSignage.lanternBatch(
      Lanterns.spots.map { case (x, z) =>
        LanternSpot(x, groundAt(x, z) + Lanterns.HangHeight, z, Lanterns.Radius)
      },
      textures.get(LanternKey),
      curvature
    )
    group.attach(lanterns)
    geometries += lanternGeometry
    materials += lanternMaterial

    val (limbs, greens, pinks) = Canopy.plan(groundAt)
    val limbGeometry           = new CylinderGeometry(1.0, 1.0, 1.0, 7.0, 1.0, false)
    val blobGeometry           = new SphereGeometry(1.0, 9.0, 7.0)
    val limbMaterial           = ToonMaterial.instancedCelMaterial(Palette.darkTimber, curvature)
    val greenMaterial          = ToonMaterial.instancedCelMaterial(CanopyGreenHex, curvature)
    val pinkMaterial           = ToonMaterial.instancedBlossomMaterial(Palette.sakuraPink, curvature)
    val canopyPasses = List(
      (SceneBatch.instancedMesh(limbGeometry, limbMaterial, limbs), limbGeometry, limbMaterial),
      (SceneBatch.instancedMesh(blobGeometry, greenMaterial, greens), blobGeometry, greenMaterial),
      (SceneBatch.instancedMesh(blobGeometry, pinkMaterial, pinks), blobGeometry, pinkMaterial)
    )
    canopyPasses.foreach { case (batch, geometry, material) =>
      group.attach(batch)
      geometries += geometry
      materials += material
    }

    val (dollMeshes, dollGeometries, dollMaterials) = kokeshiBatches(curvature, groundAt)
    dollMeshes.foreach(mesh => group.attach(mesh))
    geometries ++= dollGeometries
    materials ++= dollMaterials
    val (faces, faceGeometry, faceMaterial) = kokeshiFaceBatch(curvature, groundAt)
    group.attach(faces)
    geometries += faceGeometry
    materials += faceMaterial

    SceneAssembly(group, geometries.result(), materials.result())
  end build

  private def addPaving(parts: Map[Part, GeometryBuilder], groundAt: (Double, Double) => Double): Unit =
    val base    = parts(Part.LaneBase)
    val rows    = ((XEast - XWest + 0.8) / 0.5).toInt
    val columns = (Paving.Width / 0.5).toInt
    (0 until rows).foreach { row =>
      val x0 = XWest - 0.4 + (XEast - XWest + 0.8) * row / rows.toDouble
      val x1 = XWest - 0.4 + (XEast - XWest + 0.8) * (row + 1) / rows.toDouble
      (0 until columns).foreach { column =>
        val z0 = CenterZ - Paving.Width / 2.0 + Paving.Width * column / columns.toDouble
        val z1 = CenterZ - Paving.Width / 2.0 + Paving.Width * (column + 1) / columns.toDouble
        val cx = (x0 + x1) / 2.0
        val cz = (z0 + z1) / 2.0
        if !Channel.contains(cx, cz) then
          base.addBox(cx, groundAt(cx, cz) + 0.02, cz, x1 - x0 + 0.02, 0.06, z1 - z0 + 0.02)
      }
    }

    val slab = parts(Part.Slab)
    Paving.slabs.filterNot { case (x, z) => Channel.contains(x, z) }.foreach { case (x, z) =>
      slab.addBox(x, groundAt(x, z) + Paving.Rise, z, Paving.SlabLength, 0.045, Paving.SlabWidth)
    }

    val inlay = parts(Part.Inlay)
    Paving.inlay.filterNot { case (x, z) => Channel.contains(x, z) }.foreach { case (x, z) =>
      inlay.addBox(x, groundAt(x, z) + Paving.Rise + 0.005, z, 0.44, 0.035, 0.6)
    }

    val petal = parts(Part.Petal)
    Petals.spots.filterNot { case (x, z) => Channel.contains(x, z) }.foreach { case (x, z) =>
      petal.addBox(x, groundAt(x, z) + 0.08, z, Petals.Size, 0.012, Petals.Size * 0.8)
    }
    val dapple = parts(Part.Dapple)
    Dapple.patches.filterNot(patch => Channel.contains(patch.x, patch.z)).foreach { patch =>
      dapple.addBox(patch.x, groundAt(patch.x, patch.z) + Paving.Rise + 0.025, patch.z,
        patch.width, 0.012, patch.depth)
    }
  end addPaving

  private def addChannel(parts: Map[Part, GeometryBuilder], groundAt: (Double, Double) => Double): Unit =
    val channel = Channel.layout
    val centerZ = (channel.z0 + channel.z1) / 2.0
    val length  = channel.z1 - channel.z0
    val ground  = groundAt(channel.centerX, centerZ)
    val waterY  = ground + Paving.Rise - 0.42
    val stone   = parts(Part.StoneDark)
    val water   = parts(Part.Water)
    stone.addBox(channel.centerX, waterY - 0.12, centerZ, channel.halfWidth * 2.0, 0.18, length)
    List(-1.0, 1.0).foreach { side =>
      val wallX       = channel.centerX + side * (channel.halfWidth + 0.12)
      val localGround = groundAt(wallX, centerZ) + Paving.Rise
      stone.addBox(wallX, localGround - 0.22, centerZ, 0.24, 0.64, length)
      stone.addBox(wallX, localGround + 0.09, centerZ, 0.38, 0.12, length + 0.08)
    }
    water.addBox(channel.centerX, waterY, centerZ, channel.halfWidth * 1.88, 0.02, length - 0.08)
  end addChannel

  private def addBridge(parts: Map[Part, GeometryBuilder], groundAt: (Double, Double) => Double): Unit =
    val channel = Channel.layout
    val centerZ = (channel.z0 + channel.z1) / 2.0
    val ground  = groundAt(channel.centerX, centerZ) + Paving.Rise
    val deckY   = ground + 0.16
    val wood    = parts(Part.Wood)
    val dark    = parts(Part.TimberDark)
    (0 until 9).foreach { index =>
      val t      = (index + 0.5) / 9.0
      val x      = channel.centerX - channel.bridgeSpan / 2.0 + channel.bridgeSpan * t
      val camber = math.sin(t * math.Pi) * 0.045
      wood.addBox(x, deckY + camber, centerZ, channel.bridgeSpan / 9.0 - 0.025, 0.13, channel.bridgeWidth)
    }
    List(-1.0, 1.0).foreach { side =>
      val railZ = centerZ + side * (channel.bridgeWidth / 2.0 - 0.08)
      (-1 to 1).foreach { index =>
        val x = channel.centerX + index * channel.bridgeSpan / 2.0
        dark.addBox(x, deckY + 0.48, railZ, 0.11, 0.96, 0.11)
      }
      dark.addBox(channel.centerX, deckY + 0.90, railZ, channel.bridgeSpan, 0.08, 0.08)
      parts(Part.LacquerRed).addBox(channel.centerX, deckY + 0.48, railZ, channel.bridgeSpan, 0.06, 0.06)
      dark.addBeam(channel.centerX - channel.bridgeSpan / 2.0 + 0.08, deckY + 0.16, railZ,
        channel.centerX + channel.bridgeSpan / 2.0 - 0.08, deckY + 0.80, railZ, 0.055)
      dark.addBeam(channel.centerX - channel.bridgeSpan / 2.0 + 0.08, deckY + 0.80, railZ,
        channel.centerX + channel.bridgeSpan / 2.0 - 0.08, deckY + 0.16, railZ, 0.055)
    }
  end addBridge

  private def addFootbath(parts: Map[Part, GeometryBuilder], groundAt: (Double, Double) => Double): Unit =
    val bath   = Footbath.plan
    val ground = groundAt(bath.centerX, bath.centerZ) + Paving.Rise
    val rim    = parts(Part.StoneDark)
    rim.addBox(bath.centerX, ground + bath.waterDepth / 2.0, bath.centerZ,
      bath.width, bath.waterDepth, bath.depth)
    val waterY = ground + bath.waterDepth - 0.06
    parts(Part.Water).addBox(bath.centerX, waterY, bath.centerZ,
      bath.width - 0.22, 0.025, bath.depth - 0.22)
    parts(Part.Stone).addBox(bath.centerX, ground + bath.waterDepth + 0.06, bath.centerZ,
      bath.width + 0.12, 0.12, bath.depth + 0.12)
    (0 until 3).foreach { index =>
      val x = bath.centerX - 0.30 + index * 0.30
      parts(Part.Steam).addBeam(x, waterY, bath.centerZ, x + 0.12, waterY + 0.75 + index * 0.12,
        bath.centerZ + 0.10, 0.16)
    }
    parts(Part.Wood).addBox(bath.centerX + bath.width / 2.0 + 0.38, ground + 0.30, bath.centerZ,
      0.68, 0.10, bath.depth)
    parts(Part.TimberDark).addBox(bath.centerX + bath.width / 2.0 + 0.38, ground + 0.15,
      bath.centerZ - bath.depth * 0.32, 0.07, 0.30, 0.07)
    parts(Part.TimberDark).addBox(bath.centerX + bath.width / 2.0 + 0.38, ground + 0.15,
      bath.centerZ + bath.depth * 0.32, 0.07, 0.30, 0.07)
  end addFootbath

  private def addStreetProps(parts: Map[Part, GeometryBuilder], groundAt: (Double, Double) => Double): Unit =
    StreetProps.planters.foreach { planter =>
      val ground = groundAt(planter.x, planter.z) + Paving.Rise
      parts(Part.Planter).addCylinder(planter.x, ground + planter.radius * 0.72, planter.z,
        planter.radius * 0.78, planter.radius, planter.radius * 1.44, 10.0)
      parts(Part.StoneDark).addCylinder(planter.x, ground + planter.radius * 1.40, planter.z,
        planter.radius * 0.85, planter.radius * 0.85, planter.radius * 0.12, 10.0)
      (0 until (if planter.flowers then 5 else 4)).foreach { index =>
        val count = if planter.flowers then 5.0 else 4.0
        val angle = 2.0 * math.Pi * index / count
        parts(Part.Leaf).addBeam(planter.x, ground + planter.radius * 1.42, planter.z,
          planter.x + math.cos(angle) * planter.radius * 0.76, ground + planter.radius * 2.1,
          planter.z + math.sin(angle) * planter.radius * 0.76, 0.065)
      }
    }
    StreetProps.crateStacks.foreach { stack =>
      (0 until stack.count).foreach { index =>
        val ground = groundAt(stack.x, stack.z) + Paving.Rise
        val dx     = math.sin(stack.yaw) * index * 0.035
        val dz     = math.cos(stack.yaw) * index * 0.035
        val y      = ground + 0.14 + index * 0.28
        parts(Part.Crate).addBox(stack.x + dx, y, stack.z + dz, 0.50, 0.28, 0.34)
        parts(Part.Wood).addBox(stack.x + dx, y + 0.02, stack.z + dz, 0.52, 0.04, 0.36)
        List(-1.0, 1.0).foreach { side =>
          parts(Part.Wood).addBox(stack.x + dx, y, stack.z + dz + side * 0.14, 0.46, 0.18, 0.035)
        }
      }
    }
    StreetProps.catBoxes.foreach { catBox =>
      val ground = groundAt(catBox.x, catBox.z) + Paving.Rise
      parts(Part.CatBox).addBox(catBox.x, ground + 0.02, catBox.z, 0.62, 0.04, 0.46)
      List(-1.0, 1.0).foreach { side =>
        parts(Part.CatBox).addBox(catBox.x, ground + 0.15, catBox.z + side * 0.22, 0.62, 0.26, 0.03)
        parts(Part.CatBox).addBox(catBox.x + side * 0.30, ground + 0.15, catBox.z, 0.03, 0.26, 0.46)
      }
      val flapX = catBox.x + math.cos(catBox.yaw) * 0.18
      val flapZ = catBox.z + math.sin(catBox.yaw) * 0.18
      parts(Part.CatBox).addBeam(catBox.x - 0.31, ground + 0.30, catBox.z - 0.23,
        flapX, ground + 0.40, flapZ, 0.03)
      val catX = catBox.x + math.cos(catBox.yaw) * 0.03
      val catZ = catBox.z + math.sin(catBox.yaw) * 0.03
      parts(Part.CatFur).addBox(catX - 0.03, ground + 0.20, catZ - 0.02, 0.27, 0.14, 0.30)
      parts(Part.CatFur).addBox(catX + 0.12, ground + 0.25, catZ + 0.08, 0.14, 0.15, 0.14)
      parts(Part.CatFur).addTetrahedron(catBox.x + 0.12, ground + 0.30, catBox.z + 0.08,
        0.10, 0.12, 0.10, -0.16, 0.0)
    }
    StreetProps.signPosts.foreach { sign =>
      val ground = groundAt(sign.x, sign.z) + Paving.Rise
      parts(Part.TimberDark).addCylinder(sign.x, ground + sign.height / 2.0, sign.z, 0.035, 0.035, sign.height, 6.0)
      parts(Part.Wood).addBox(sign.x, ground + sign.height * 0.78, sign.z, 0.12, 0.42, 0.56)
      parts(Part.StoneDark).addBox(sign.x, ground + 0.05, sign.z, 0.28, 0.10, 0.28)
    }
  end addStreetProps

  /** One machiya: stone plinth, plaster volume, timber-framed lattice shopfront, tiled roof with
    * deep eaves, and the small tiled canopy over the frontage.
    */
  private def addMachiya(
    parts: Map[Part, GeometryBuilder],
    groundAt: (Double, Double) => Double,
    footprint: MachiyaFootprint,
    facesSouth: Boolean
  ): Unit =
    val architecture = BuildingPlans.forUnit(footprint, facesSouth)
    val s            = if architecture.facesSouth then 1.0 else -1.0
    val frontageZ    = if architecture.facesSouth then SouthFrontageZ else NorthFrontageZ
    val w            = footprint.x1 - footprint.x0
    val cx           = (footprint.x0 + footprint.x1) / 2.0
    val depth        = footprint.z1 - footprint.z0
    val y            = groundAt(cx, frontageZ - s * depth / 2.0)
    val floorHeight  = architecture.groundFloorHeight
    val upperHeight  = architecture.upperFloorHeight
    val oneFloor     = !architecture.hasUpperFloor
    val height       = if oneFloor then floorHeight + 0.4 else floorHeight + upperHeight

    val stone = parts(Part.StoneDark)
    stone.addBox(cx, y + 0.10, frontageZ - s * depth / 2.0, w + 0.2, 0.44, depth + 0.2)

    val wall =
      if Set(MachiyaKind.Sakuraan, MachiyaKind.Kokeshi, MachiyaKind.Ryokan, MachiyaKind.Bathhouse)
          .contains(footprint.kind)
      then parts(Part.PlasterAlt)
      else parts(Part.Plaster)
    val frontWallDepth = if footprint.kind == MachiyaKind.Yunoka then depth - 0.25 else depth - 0.04
    val frontWallInset = if footprint.kind == MachiyaKind.Yunoka then 0.08 else -0.02
    wall.addBox(cx, y + 0.36 + (floorHeight - 0.36) / 2.0,
      frontageZ - s * (depth / 2.0 + frontWallInset), w, floorHeight - 0.36, frontWallDepth)
    if !oneFloor then
      val upperDepth = depth - 0.24
      wall.addBox(cx, y + floorHeight + upperHeight / 2.0, frontageZ - s * (0.12 + upperDepth / 2.0), w,
        upperHeight, upperDepth)
      parts(Part.TimberDark).addBox(cx, y + floorHeight + 0.02, frontageZ - s * depth / 2.0,
        w + 0.18, 0.16, depth + 0.02)

    val roofBase = y + height
    val eave     = 0.62
    val rh       = 1.15
    val halfSpan = depth / 2.0 + eave
    val tile     = parts(Part.Tile)
    List(-1.0, 1.0).foreach { side =>
      (0 until 4).foreach { index =>
        val zEdge  = (halfSpan * index) / 4.0
        val zNext  = (halfSpan * (index + 1)) / 4.0
        val zc     = frontageZ - s * depth / 2.0 + side * (zEdge + zNext) / 2.0
        val yLevel = roofBase + rh * (1.0 - (index + 0.5) / 4.0)
        tile.addBox(cx, yLevel, zc, w + eave * 2.0, 0.14, zNext - zEdge + 0.12)
      }
    }
    parts(Part.TileEdge).addBox(cx, roofBase + rh + 0.04, frontageZ - s * depth / 2.0, w + eave * 2.0 + 0.08,
      0.2, 0.3)
    List(-1.0, 1.0).foreach { side =>
      parts(Part.TileEdge).addBox(cx, roofBase - 0.04, frontageZ - s * depth / 2.0 + side * (halfSpan - 0.02),
        w + eave * 2.0 + 0.06, 0.13, 0.16)
    }
    val ridgeZ   = frontageZ - s * depth / 2.0
    val ribCount = math.max(10, math.round((w + eave * 2.0) / 0.34).toInt)
    (0 until ribCount).foreach { index =>
      val ribX = footprint.x0 - eave + (w + eave * 2.0) * (index + 0.5) / ribCount
      List(-1.0, 1.0).foreach { side =>
        parts(Part.TileEdge).addBeam(ribX, roofBase + rh + 0.08, ridgeZ, ribX, roofBase + 0.08,
          ridgeZ + side * halfSpan, 0.022)
      }
    }
    parts(Part.TileEdge).addBeam(footprint.x0 - eave, roofBase + rh + 0.10, ridgeZ,
      footprint.x1 + eave, roofBase + rh + 0.10, ridgeZ, 0.045)
    List(-1.0, 1.0).foreach { side =>
      parts(Part.Wood).addBox(cx + side * (w / 2.0 + 0.02), roofBase + rh * 0.42,
        frontageZ - s * depth / 2.0, 0.14, rh * 0.84, depth - 0.1)
    }

    val sill = 0.42
    val head = floorHeight - 0.52
    val post = 0.16
    val wood = parts(Part.Wood)
    val dark = parts(Part.TimberDark)
    dark.addBox(cx, y + sill, frontageZ + s * 0.08, w, 0.18, 0.3)
    wood.addBox(cx, y + head + 0.11, frontageZ + s * 0.08, w, 0.22, 0.3)
    List(-1.0, 1.0).foreach { side =>
      dark.addBox(cx + side * (w / 2.0 - post / 2.0), y + floorHeight / 2.0, frontageZ + s * 0.08,
        post, floorHeight, 0.3)
    }

    val doorW = footprint.kind match
      case MachiyaKind.Kokeshi   => 1.6
      case MachiyaKind.Yunoka    => 1.2
      case MachiyaKind.Bathhouse => 2.6
      case _                     => 1.4
    val doorC = footprint.kind match
      case MachiyaKind.Yunoka => cx + 0.6
      case _                  => cx - 0.2
    val latticeSpan = (w / 2.0 - post) - doorW / 2.0
    List(-1.0, 1.0).foreach { side =>
      val panelC = doorC + side * (doorW / 2.0 + latticeSpan / 2.0)
      if latticeSpan > 0.3 then
        dark.addBox(panelC, y + (sill + head) / 2.0, frontageZ + s * 0.04, latticeSpan, head - sill, 0.08)
        val battens = math.max(2.0, math.round(latticeSpan / 0.135)).toInt
        (0 until battens).foreach { index =>
          val bx = panelC - latticeSpan / 2.0 + latticeSpan * (index + 0.5) / battens
          wood.addBox(bx, y + (sill + head) / 2.0, frontageZ + s * 0.12, 0.05, head - sill - 0.06, 0.09)
        }
        wood.addBox(panelC, y + sill + (head - sill) * 0.62, frontageZ + s * 0.14, latticeSpan, 0.06, 0.1)
    }

    dark.addBox(doorC, y + (head - 0.18) / 2.0 + 0.09, frontageZ + s * 0.03, doorW, head - 0.18, 0.06)
    if footprint.kind == MachiyaKind.Sakuraan then
      val shojiWidth  = doorW - 0.18
      val shojiHeight = head - 0.24
      val shojiY      = y + 0.12 + shojiHeight / 2.0
      parts(Part.GlassWarm).addBox(doorC, shojiY, frontageZ + s * 0.065, shojiWidth, shojiHeight, 0.02)
      List(-0.36, -0.12, 0.12, 0.36).foreach { offset =>
        wood.addBox(doorC + offset, shojiY, frontageZ + s * 0.12, 0.035, shojiHeight, 0.04)
      }
      (1 to 4).foreach { row =>
        wood.addBox(doorC, y + 0.12 + shojiHeight * row / 5.0, frontageZ + s * 0.12,
          shojiWidth, 0.035, 0.04)
      }
    else if footprint.kind == MachiyaKind.Yunoka then
      val windowX = cx - 0.98
      val windowY = y + 1.52
      val windowW = 1.78
      val windowH = 1.22
      parts(Part.GlassWarm).addBox(windowX, windowY, frontageZ + s * 0.04, windowW, windowH, 0.025)
      wood.addBox(windowX - windowW / 2.0, windowY, frontageZ + s * 0.12, 0.08, windowH + 0.16, 0.08)
      wood.addBox(windowX + windowW / 2.0, windowY, frontageZ + s * 0.12, 0.08, windowH + 0.16, 0.08)
      wood.addBox(windowX, windowY - windowH / 2.0, frontageZ + s * 0.12, windowW + 0.12, 0.08, 0.08)
      wood.addBox(windowX, windowY + windowH / 2.0, frontageZ + s * 0.12, windowW + 0.12, 0.08, 0.08)
      wood.addBox(windowX, windowY, frontageZ + s * 0.13, 0.06, windowH, 0.08)
    else
      parts(Part.GlassWarm).addBox(doorC - doorW / 2.0 + 0.12, y + (head - 0.3) / 2.0 + 0.1,
        frontageZ + s * 0.065, 0.5, head - 0.3, 0.02)
    end if
    parts(Part.Stone).addBox(doorC, y + 0.24, frontageZ + s * 0.36, doorW + 0.5, 0.16, 0.62)
    wood.addBox(doorC, y + head - 0.02, frontageZ + s * 0.12, doorW + 0.12, 0.16, 0.2)

    if !oneFloor then
      architecture.windowCenters.foreach { wx =>
        dark.addBox(wx, y + floorHeight + 1.3, frontageZ - s * 0.14, 1.4, 1.1, 0.16)
        parts(Part.GlassWarm).addBox(wx, y + floorHeight + 1.3, frontageZ - s * 0.08, 1.24, 0.94, 0.05)
        List(-0.36, 0.0, 0.36).foreach { offset =>
          wood.addBox(wx + offset, y + floorHeight + 1.3, frontageZ - s * 0.12, 0.05, 0.94, 0.07)
        }
        List(-0.28, 0.0, 0.28).foreach { offset =>
          wood.addBox(wx, y + floorHeight + 1.3 + offset, frontageZ - s * 0.12, 1.24, 0.045, 0.07)
        }
      }
      if architecture.hasBalcony then addMachiyaBalcony(parts, cx, w, frontageZ, s, y + floorHeight)

    val aw  = w + 0.24
    val out = 0.9
    (0 until 3).foreach { index =>
      val zc     = frontageZ + s * ((index + 0.5) * out / 3.0)
      val yLevel = y + head + 0.94 - index * 0.26 / 3.0
      tile.addBox(cx, yLevel, zc, aw, 0.1, out / 3.0 + 0.1)
    }
    parts(Part.TileEdge).addBox(cx, y + head + 0.94 - 0.26 / 2.0 - 0.05, frontageZ + s * out, aw + 0.05, 0.12, 0.12)
    List(-1.0, 1.0).foreach { side =>
      wood.addBox(cx + side * (aw / 2.0 - 0.14), y + head + 0.7, frontageZ + s * 0.16, 0.09, 0.5, 0.09)
    }
  end addMachiya

  private def addMachiyaBalcony(
    parts: Map[Part, GeometryBuilder],
    centerX: Double,
    frontageWidth: Double,
    frontageZ: Double,
    outward: Double,
    floorY: Double
  ): Unit =
    val width     = frontageWidth * 0.78
    val depth     = 0.82
    val outerZ    = frontageZ + outward * depth
    val platformY = floorY + 0.48
    parts(Part.Wood).addBox(centerX, platformY, frontageZ + outward * depth / 2.0, width, 0.10, depth)
    parts(Part.TimberDark).addBox(centerX, platformY + 0.84, outerZ, width, 0.07, 0.07)
    parts(Part.TimberDark).addBox(centerX, platformY + 0.44, outerZ, width, 0.06, 0.06)
    val posts = math.max(4, math.floor(width / 0.42).toInt)
    (0 to posts).foreach { index =>
      val x = centerX - width / 2.0 + width * index / posts
      parts(Part.TimberDark).addBox(x, platformY + 0.64, outerZ, 0.055, 0.50, 0.055)
    }
    List(-1.0, 1.0).foreach { side =>
      val x = centerX + side * width / 2.0
      parts(Part.TimberDark).addBeam(x, platformY + 0.44, frontageZ, x, platformY + 0.44, outerZ, 0.045)
      parts(Part.TimberDark).addBeam(x, platformY + 0.84, frontageZ, x, platformY + 0.84, outerZ, 0.045)
    }
  end addMachiyaBalcony

  private def addKokeshiTable(parts: Map[Part, GeometryBuilder], groundAt: (Double, Double) => Double): Unit =
    val cx     = (Kokeshi.TableX0 + Kokeshi.TableX1) / 2.0
    val z      = Kokeshi.TableZ
    val y      = groundAt(cx, z)
    val length = Kokeshi.TableX1 - Kokeshi.TableX0
    parts(Part.Wood).addBox(cx, y + Kokeshi.TableTopY, z, length, 0.09, 0.72)
    parts(Part.Wood).addBox(cx, y + Kokeshi.TableTopY - 0.12, z, length - 0.16, 0.13, 0.08)
    List(-1.0, 1.0).foreach { sideX =>
      List(-1.0, 1.0).foreach { sideZ =>
        parts(Part.TimberDark).addBox(cx + sideX * length / 2.0,
          y + Kokeshi.TableTopY / 2.0, z + sideZ * 0.26, 0.09, Kokeshi.TableTopY, 0.09)
      }
      val trestleX = cx + sideX * (length / 2.0 - 0.26)
      parts(Part.TimberDark).addBeam(trestleX, y + 0.04, z - 0.28, cx + sideX * 0.82,
        y + Kokeshi.TableTopY - 0.15, z - 0.28, 0.045)
      parts(Part.TimberDark).addBeam(trestleX, y + 0.04, z + 0.28, cx + sideX * 0.82,
        y + Kokeshi.TableTopY - 0.15, z + 0.28, 0.045)
      parts(Part.TimberDark).addBox(cx + sideX * 0.82, y + 0.17, z, 0.07, 0.06, 0.52)
    }
  end addKokeshiTable

  private def addBenches(parts: Map[Part, GeometryBuilder], groundAt: (Double, Double) => Double): Unit =
    List(34.8, 37.1).foreach { x =>
      val z = NorthFrontageZ - 0.55
      val y = groundAt(x, z)
      (0 until 3).foreach { index =>
        parts(Part.Wood).addBox(x, y + 0.42, z - 0.19 + index * 0.19, 1.5, 0.03, 0.15)
      }
      List(-1.0, 1.0).foreach { side =>
        parts(Part.TimberDark).addBox(x + side * 0.62, y + 0.20, z, 0.05, 0.40, 0.5)
        parts(Part.TimberDark).addBox(x + side * 0.62, y + 0.72, z + 0.23, 0.06, 0.64, 0.06)
      }
      parts(Part.Wood).addBox(x, y + 0.70, z + 0.23, 1.5, 0.04, 0.06)
      parts(Part.Wood).addBox(x, y + 0.91, z + 0.23, 1.5, 0.04, 0.06)
    }
  end addBenches

  private def addDeck(parts: Map[Part, GeometryBuilder], groundAt: (Double, Double) => Double): Unit =
    val cx = (Deck.X0 + Deck.X1) / 2.0
    val cz = (Deck.Z0 + Deck.Z1) / 2.0
    val y  = groundAt(cx, cz)

    (0 until 9).foreach { index =>
      val x = Deck.X0 + (Deck.X1 - Deck.X0) * (index + 0.5) / 9.0
      parts(Part.Wood).addBox(x, y + Deck.Height, cz, (Deck.X1 - Deck.X0) / 9.0 - 0.03, 0.1,
        Deck.Z1 - Deck.Z0)
    }
    List(-1.0, 1.0).foreach { sideX =>
      List(-1.0, 1.0).foreach { sideZ =>
        parts(Part.TimberDark).addBox(Deck.X0 + 0.16 + (sideX + 1.0) / 2.0 * (Deck.X1 - Deck.X0 - 0.32),
          y + Deck.Height / 2.0, cz + sideZ * (Deck.Z1 - Deck.Z0) / 2.0, 0.14, Deck.Height, 0.14)
      }
    }

    val railX = Deck.X0 + 0.06
    (0 until 5).foreach { index =>
      val z = Deck.Z0 + 0.06 + (Deck.Z1 - Deck.Z0 - 0.12) * index / 4.0
      parts(Part.Wood).addBox(railX, y + Deck.Height + 0.47, z, 0.06, 0.94, 0.06)
    }
    List(0.92, 0.5).foreach { railY =>
      parts(Part.Wood).addBox(railX, y + Deck.Height + railY, cz, 0.08, 0.07, Deck.Z1 - Deck.Z0)
    }
    List(-1.0, 1.0).foreach { side =>
      val z = cz + side * ((Deck.Z1 - Deck.Z0) / 2.0 - 0.06)
      (0 until 4).foreach { index =>
        val x = railX + 0.6 + index * 0.6
        parts(Part.Wood).addBox(x, y + Deck.Height + 0.47, z, 0.06, 0.94, 0.06)
      }
      List(0.92, 0.5).foreach { railY =>
        parts(Part.Wood).addBox(Deck.X0 + 0.3, y + Deck.Height + railY, z, Deck.X1 - Deck.X0 - 0.6, 0.07, 0.08)
      }
    }
    Deck.braces(y).foreach { brace =>
      parts(Part.TimberDark).addBeam(brace.x0, brace.y0, brace.z, brace.x1, brace.y1, brace.z, 0.075)
    }
  end addDeck

  /** 石灯籠: base, shaft, table, fire box with lit openings, and a cap. */
  private def addStoneLantern(parts: Map[Part, GeometryBuilder], groundAt: (Double, Double) => Double): Unit =
    val x = Deck.lanternX
    val z = CenterZ
    val y = groundAt(x, z)
    val s = Deck.lanternScale

    val dark  = parts(Part.StoneDark)
    val stone = parts(Part.Stone)
    dark.addBox(x, y + 0.08 * s, z, 0.6 * s, 0.16 * s, 0.6 * s)
    stone.addBox(x, y + 0.52 * s, z, 0.24 * s, 0.72 * s, 0.24 * s)
    dark.addBox(x, y + 0.93 * s, z, 0.52 * s, 0.10 * s, 0.52 * s)
    stone.addBox(x, y + 1.13 * s, z, 0.46 * s, 0.30 * s, 0.46 * s)
    List(0.0, math.Pi / 2.0).foreach { yaw =>
      val wx     = x + math.sin(yaw) * 0.24 * s
      val wz     = z + math.cos(yaw) * 0.24 * s
      val width  = if yaw == 0.0 then 0.16 * s else 0.02
      val depthD = if yaw == 0.0 then 0.02 else 0.16 * s
      parts(Part.LanternGlow).addBox(wx, y + 1.13 * s, wz, width, 0.17 * s, depthD)
    }
    dark.addTetrahedron(x, y + 1.28 * s, z, 0.78 * s, 0.30 * s, 0.78 * s, 0.0, 0.0)
    dark.addBox(x, y + 1.60 * s, z, 0.12 * s, 0.10 * s, 0.12 * s)
  end addStoneLantern

  private def addLanternBrackets(parts: Map[Part, GeometryBuilder], groundAt: (Double, Double) => Double): Unit =
    Lanterns.spots.foreach { case (x, z) =>
      val side      = if z < CenterZ then 1.0 else -1.0
      val frontageZ = if z < CenterZ then SouthFrontageZ else NorthFrontageZ
      parts(Part.TimberDark).addBox(x, groundAt(x, z) + Lanterns.HangHeight + 0.30,
        frontageZ + side * 0.10, 0.06, 0.06, 0.42)
    }
  end addLanternBrackets

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

    val kokeshi  = KokeshiShop
    val kokeshiY = groundAt((kokeshi.x0 + kokeshi.x1) / 2.0, kokeshi.z1)
    mount(
      DistrictSignage.signPlane(kokeshi.x1 - kokeshi.x0 - 0.5, 0.52, (kokeshi.x0 + kokeshi.x1) / 2.0,
        kokeshiY + H1 - 0.02, kokeshi.z1 + 0.14, yaw = 0.0, curvature = curvature),
      textures.get(KokeshiFasciaKey)
    )
    mount(
      DistrictSignage.signPlane(0.42, 1.7, kokeshi.x1 - 0.30, kokeshiY + 2.05, kokeshi.z1 + 0.18,
        yaw = math.Pi / 2.0, curvature = curvature),
      textures.get(KokeshiBladeKey),
      doubleSided = true
    )

    val yunoka  = YunokaShop
    val yunokaY = groundAt((yunoka.x0 + yunoka.x1) / 2.0, yunoka.z1)
    mount(
      DistrictSignage.signPlane(yunoka.x1 - yunoka.x0 - 0.5, 0.52, (yunoka.x0 + yunoka.x1) / 2.0,
        yunokaY + H1 - 0.02, yunoka.z1 + 0.14, yaw = 0.0, curvature = curvature),
      textures.get(YunokaFasciaKey)
    )
    mount(
      DistrictSignage.signPlane(0.42, 1.7, yunoka.x0 + 0.30, yunokaY + 2.05, yunoka.z1 + 0.18,
        yaw = math.Pi / 2.0, curvature = curvature),
      textures.get(YunokaBladeKey),
      doubleSided = true
    )

    val sakuraan  = SakuraanShop
    val sakuraanY = groundAt((sakuraan.x0 + sakuraan.x1) / 2.0, sakuraan.z0)
    mount(
      DistrictSignage.signPlane(sakuraan.x1 - sakuraan.x0 - 0.5, 0.52, (sakuraan.x0 + sakuraan.x1) / 2.0,
        sakuraanY + H1 - 0.02, sakuraan.z0 - 0.14, yaw = math.Pi, curvature = curvature),
      textures.get(SakuraanKey)
    )
    mount(
      DistrictSignage.signPlane(0.42, 1.7, sakuraan.x0 + 0.30, sakuraanY + 2.05, sakuraan.z0 - 0.18,
        yaw = -math.Pi / 2.0, curvature = curvature),
      textures.get(SakuraanBladeKey),
      doubleSided = true
    )
    mount(
      DistrictSignage.signPlane(0.46, 0.34, sakuraan.x0 + 1.2, sakuraanY + 1.55, sakuraan.z0 - 0.20,
        yaw = math.Pi, curvature = curvature),
      textures.get(KanmiKey)
    )

    (geometries.result(), materials.result())
  end addSignage

  /** The kokeshi dolls: bodies as one per-instance-coloured basic batch (souvenir colours are the
    * point, and an unlit batch keeps them saturated), heads as a second batch. Both bend host-side.
    */
  private def kokeshiBatches(
    curvature: PlanetCurvature,
    groundAt: (Double, Double) => Double
  ): (List[InstancedMeshRef], List[BufferGeometryRef], List[MaterialRef]) =
    val bodyGeometry = new CylinderGeometry(0.85, 1.0, 1.0, 8.0, 1.0, false)
    val headGeometry = new SphereGeometry(1.0, 7.0, 5.0)
    val bodyMaterial = new MeshBasicMaterial()
    val headMaterial = new MeshBasicMaterial()
    headMaterial.color.setHex(Kokeshi.HeadHex)
    val bodies = new InstancedMesh(bodyGeometry, bodyMaterial, Kokeshi.row.size.toDouble)
    val heads  = new InstancedMesh(headGeometry, headMaterial, Kokeshi.row.size.toDouble)
    val color  = new Color()
    Kokeshi.row.zipWithIndex.foreach { case (doll, index) =>
      val tableY = groundAt(doll.x, doll.z) + Kokeshi.TableTopY
      val bodyY  = tableY + doll.height / 2.0
      val headY  = tableY + doll.height + 0.10
      setScaled(bodies, index, doll.x, curvature.bend(doll.x, bodyY, doll.z), doll.z,
        Kokeshi.BodyRadius, doll.height, Kokeshi.BodyRadius)
      setScaled(heads, index, doll.x, curvature.bend(doll.x, headY, doll.z), doll.z,
        Kokeshi.HeadRadius, Kokeshi.HeadRadius, Kokeshi.HeadRadius)
      color.setHex(Kokeshi.BodyColors(doll.colorIndex).toDouble)
      bodies.setColorAt(index, color)
    }
    bodies.instanceMatrix.markNeedsUpdate()
    heads.instanceMatrix.markNeedsUpdate()
    (List(bodies, heads), List(bodyGeometry, headGeometry), List(bodyMaterial, headMaterial))
  end kokeshiBatches

  private def kokeshiFaceBatch(
    curvature: PlanetCurvature,
    groundAt: (Double, Double) => Double
  ): (InstancedMeshRef, BufferGeometryRef, MaterialRef) =
    val geometry      = new SphereGeometry(1.0, 6.0, 4.0)
    val material      = new MeshBasicMaterial()
    val mesh          = new InstancedMesh(geometry, material, (Kokeshi.row.size * 3).toDouble)
    val position      = new Vector3()
    val rotation      = new Quaternion()
    val scale         = new Vector3()
    val matrix        = new Matrix4()
    val color         = new Color()
    var instanceIndex = 0

    Kokeshi.row.foreach { doll =>
      val headY = groundAt(doll.x, doll.z) + Kokeshi.TableTopY + doll.height + 0.10
      List((-0.018, 0.006, 0x302923), (0.018, 0.006, 0x302923), (0.0, -0.020, 0xb5322f))
        .foreach { case (offsetX, offsetY, hex) =>
          val faceX = doll.x + offsetX
          val faceY = headY + offsetY
          val faceZ = doll.z + Kokeshi.HeadRadius * 0.94
          position.set(faceX, curvature.bend(faceX, faceY, faceZ), faceZ)
          rotation.setFromAxisAngle(asVector3Like(new Vector3(0.0, 1.0, 0.0)), 0.0)
          scale.set(0.009, 0.009, 0.006)
          matrix.compose(position, rotation, scale)
          mesh.setMatrixAt(instanceIndex, matrix)
          color.setHex(hex.toDouble)
          mesh.setColorAt(instanceIndex, color)
          instanceIndex += 1
        }
    }
    mesh.instanceMatrix.markNeedsUpdate()
    (mesh, geometry, material)
  end kokeshiFaceBatch

  private def setScaled(
    mesh: InstancedMeshRef,
    index: Int,
    x: Double,
    y: Double,
    z: Double,
    sx: Double,
    sy: Double,
    sz: Double
  ): Unit =
    val position = new Vector3()
    val rotation = new Quaternion()
    val scale    = new Vector3()
    val matrix   = new Matrix4()
    position.set(x, y, z)
    rotation.setFromAxisAngle(asVector3Like(new Vector3(0.0, 1.0, 0.0)), 0.0)
    scale.set(sx, sy, sz)
    matrix.compose(position, rotation, scale)
    mesh.setMatrixAt(index, matrix)

  private def partMaterial(part: Part, curvature: PlanetCurvature): MaterialRef =
    part match
      case Part.LaneBase    => ToonMaterial.celMaterial(LaneBaseHex, curvature)
      case Part.Slab        => ToonMaterial.celMaterial(SlabHex, curvature)
      case Part.Inlay       => ToonMaterial.celMaterial(InlayHex, curvature)
      case Part.StoneDark   => ToonMaterial.celMaterial(StoneDarkHex, curvature)
      case Part.Stone       => ToonMaterial.celMaterial(StoneHex, curvature)
      case Part.TimberDark  => ToonMaterial.celMaterial(Palette.darkTimber, curvature)
      case Part.Wood        => ToonMaterial.celMaterial(WoodHex, curvature)
      case Part.Plaster     => ToonMaterial.celMaterial(PlasterHex, curvature)
      case Part.PlasterAlt  => ToonMaterial.celMaterial(PlasterAltHex, curvature)
      case Part.Tile        => ToonMaterial.celMaterial(Palette.roofTile, curvature)
      case Part.TileEdge    => ToonMaterial.celMaterial(TileEdgeHex, curvature)
      case Part.GlassWarm   => ToonMaterial.flatMaterial(GlassWarmHex, curvature)
      case Part.Petal       => ToonMaterial.flatMaterial(Palette.sakuraPink, curvature)
      case Part.Water       => ToonMaterial.flatMaterial(0x5f9fa2, curvature)
      case Part.Planter     => ToonMaterial.celMaterial(0xb06a4c, curvature)
      case Part.Leaf        => ToonMaterial.celMaterial(0x4f8f6a, curvature)
      case Part.Crate       => ToonMaterial.celMaterial(0x967a54, curvature)
      case Part.CatBox      => ToonMaterial.celMaterial(0xc9a878, curvature)
      case Part.CatFur      => ToonMaterial.celMaterial(0xd8c9b4, curvature)
      case Part.Dapple      => ToonMaterial.flatMaterial(0x5c5968, curvature)
      case Part.LanternGlow => ToonMaterial.flatMaterial(0xffe4a0, curvature)
      case Part.Steam =>
        val material = ToonMaterial.flatMaterial(0xdcecea, curvature)
        material.transparent = true
        material.opacity = 0.16
        material.depthWrite = false
        material
      case Part.LacquerRed => ToonMaterial.celMaterial(0xb5322f, curvature)
end OnsenDistrict
