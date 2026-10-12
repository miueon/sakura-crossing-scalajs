package sakura.world

import sakura.*
import vendor.three.*

import scala.collection.mutable
import scala.math

/** A GPU resource a mounted world assembly created; a scene that omits one of these from its
  * handle leaks it on unmount.
  */
enum SceneResource:
  case Geometry(value: BufferGeometry[?, ?])
  case Material(value: vendor.three.Material[?])

  def dispose(): Unit = this match
    case Geometry(value) => value.dispose()
    case Material(value) => value.dispose()

/** Merges static structure parts into one vertex-coloured cel mesh and one ink hull, so the whole
  * neighbourhood and shrine shade in two draw calls whatever they are built from. The scene is
  * draw-call bound, and a material per palette entry would spend the budget on colour variety
  * alone.
  *
  * Parts arrive as geometries already baked to world coordinates (translate and rotate applied to
  * the geometry, not to a mesh), which is also what seats every vertex on the curved ground
  * individually — no rigid-body bend approximation anywhere.
  */
final class CelBatch(curvature: PlanetCurvature):

  private val positions     = mutable.ArrayBuffer.empty[Double]
  private val normals       = mutable.ArrayBuffer.empty[Double]
  private val colors        = mutable.ArrayBuffer.empty[Double]
  private val hullPositions = mutable.ArrayBuffer.empty[Double]
  private val hullNormals   = mutable.ArrayBuffer.empty[Double]

  /** Adds one part. `outlined` parts also join the inverted-hull buffer — the batch outlines the
    * silhouettes it is given, not every seam. `bothFaces` parts get a second, reversed copy of
    * every triangle in the cel buffers only: the shared cel material culls backfaces, and an open
    * surface such as a roof would otherwise show nothing from below. The hull keeps the part's own
    * winding, because an inverted hull needs the outward shell alone — an inward-facing copy of an
    * open surface renders as a solid ink face over the part it was meant to outline.
    */
  def add(
    geometry: BufferGeometry[?, ?],
    baseColorHex: Int,
    outlined: Boolean = false,
    bothFaces: Boolean = false
  ): Unit =
    // three.js primitives are indexed; a linear read of the position attribute would rebuild them
    // as the raw vertex list and mangle every face.
    val source = geometry.getIndex() match
      case null => geometry
      case _    => geometry.toNonIndexed()
    val position           = attributeOf(source, "position")
    val normal             = attributeOf(source, "normal")
    val (red, green, blue) = Palette.rgb(baseColorHex)
    val count              = position.count.toInt
    (0 until count).foreach { index =>
      val x = position.getX(index)
      val y = position.getY(index)
      val z = position.getZ(index)
      positions += (x, y, z)
      normals += (normal.getX(index), normal.getY(index), normal.getZ(index))
      colors += (red, green, blue)
      if outlined then
        hullPositions += (x, y, z)
        hullNormals += (normal.getX(index), normal.getY(index), normal.getZ(index))
    }
    if bothFaces then
      (0 until count by 3).foreach { triangle =>
        List(triangle + 2, triangle + 1, triangle).foreach { index =>
          positions += (position.getX(index), position.getY(index), position.getZ(index))
          normals += (-normal.getX(index), -normal.getY(index), -normal.getZ(index))
          colors += (red, green, blue)
        }
      }
  end add

  private def attributeOf(geometry: BufferGeometry[?, ?], name: String): BufferAttribute[?] =
    geometry.getAttribute(name) match
      case attribute: BufferAttribute[?] => attribute
      case _ =>
        throw new IllegalStateException(s"sakura-crossing: a structure part carries no $name attribute")

  /** Builds the two meshes the batch owns, with their GPU resources. */
  def finish(inkThickness: Double): (List[Mesh[?, ?, ?]], List[SceneResource]) =
    val celGeometry = geometryOf(positions, normals, colors)
    val celMaterial = ToonMaterial.vertexColorCelMaterial(curvature)
    val celMesh     = new Mesh(celGeometry, celMaterial)
    var meshes      = List(celMesh)
    var resources   = List(SceneResource.Geometry(celGeometry), SceneResource.Material(celMaterial))
    if hullPositions.nonEmpty then
      val facetedHullGeometry = geometryOf(hullPositions, hullNormals, colors = Nil)
      val hullGeometry        = Outline.smoothedGeometry(facetedHullGeometry)
      facetedHullGeometry.dispose()
      val hullMaterial = ToonMaterial.outlineMaterial(inkThickness, curvature)
      val hullMesh     = new Mesh(hullGeometry, hullMaterial)
      ThreeInterop.setUserDataFlag(hullMesh, "isOutline")
      meshes ::= hullMesh
      resources ::= SceneResource.Geometry(hullGeometry)
      resources ::= SceneResource.Material(hullMaterial)
    (meshes, resources)

  private def geometryOf(
    positions: mutable.ArrayBuffer[Double],
    normals: mutable.ArrayBuffer[Double],
    colors: scala.collection.Seq[Double]
  ): BufferGeometry[?, ?] =
    val geometry          = new BufferGeometry()
    val positionAttribute = new Float32BufferAttribute(positions.size.toDouble, itemSize = 3)
    positions.grouped(3).zipWithIndex.foreach { case (vertex, index) =>
      positionAttribute.setXYZ(index, vertex(0), vertex(1), vertex(2))
    }
    geometry.setAttribute("position", positionAttribute)
    val normalAttribute = new Float32BufferAttribute(normals.size.toDouble, itemSize = 3)
    normals.grouped(3).zipWithIndex.foreach { case (vertex, index) =>
      normalAttribute.setXYZ(index, vertex(0), vertex(1), vertex(2))
    }
    geometry.setAttribute("normal", normalAttribute)
    if colors.nonEmpty then
      val colorAttribute = new Float32BufferAttribute(colors.size.toDouble, itemSize = 3)
      colors.grouped(3).zipWithIndex.foreach { case (channel, index) =>
        colorAttribute.setXYZ(index, channel(0), channel(1), channel(2))
      }
      geometry.setAttribute("color", colorAttribute)
    geometry
  end geometryOf
end CelBatch

/** Bakes one structure part's local frame into world-space geometry and hands it to the batch:
  * `T(world) · R(facing) · T(at) · tilt`, the same order a mesh under a rotated group carried.
  */
final private[world] class WorldPlacer(batch: CelBatch, worldX: Double, baseY: Double, worldZ: Double, facing: Double):

  def apply(
    geometry: BufferGeometry[?, ?],
    hex: Int,
    at: (Double, Double, Double),
    tilt: Double = 0.0,
    outlined: Boolean = false,
    bothFaces: Boolean = false
  ): Unit =
    if tilt != 0.0 then geometry.rotateX(tilt)
    geometry.translate(at._1, at._2, at._3)
    geometry.rotateY(facing)
    geometry.translate(worldX, baseY, worldZ)
    batch.add(geometry, hex, outlined, bothFaces)
end WorldPlacer

/** Pitched-roof surfaces emitted as explicit triangles in the roof's local frame: ridge along x
  * at height `rise`, eaves at z +/-. The profile lifts towards the eave — `y = rise - (rise -
  * eaveLift) t^2` — which is the flare a shrine eave carries; a house passes a small lift and a
  * shrine a large one.
  *
  * Every surface leaves here wound outward and single-sided; the batch's `bothFaces` copy is what
  * makes the hollow underside visible from below.
  */
object PitchedRoof:

  /** The two slopes from ridge to eaves, `bands` quads deep per side and full-width per quad. */
  def slopes(halfWidth: Double, halfSpan: Double, rise: Double, eaveLift: Double, bands: Int): BufferGeometry[?, ?] =
    val triangles =
      for
        side <- Vector(-1.0, 1.0)
        band <- 0 until bands
      yield slopeQuad(side, band, halfWidth, halfSpan, rise, eaveLift, bands)
    buildGeometry(triangles.flatten)

  /** The triangular gable closure at each end of the roof, between the slopes. */
  def gableEnds(halfWidth: Double, halfSpan: Double, rise: Double, eaveLift: Double): BufferGeometry[?, ?] =
    val apex  = (0.0, rise, 0.0)
    val eaveA = (0.0, eaveLift, -halfSpan)
    val eaveB = (0.0, eaveLift, halfSpan)
    val triangles =
      for endX <- Vector(-halfWidth, halfWidth)
      yield
        val at = (v: (Double, Double, Double)) => (endX + v._1, v._2, v._3)
        outward(Vector(at(apex), at(eaveA), at(eaveB)), mirrored = endX > 0.0)
    buildGeometry(triangles.flatten)

  private def slopeQuad(
    side: Double,
    band: Int,
    halfWidth: Double,
    halfSpan: Double,
    rise: Double,
    eaveLift: Double,
    bands: Int
  ): Vector[(Double, Double, Double)] =
    val x0 = -halfWidth
    val x1 = halfWidth
    val z0 = side * halfSpan * band / bands
    val z1 = side * halfSpan * (band + 1) / bands
    val y0 = rise - (rise - eaveLift) * math.pow(band.toDouble / bands, 2.0)
    val y1 = rise - (rise - eaveLift) * math.pow((band + 1.0) / bands, 2.0)
    outward(
      Vector((x1, y0, z0), (x1, y1, z1), (x0, y0, z0), (x1, y1, z1), (x0, y1, z1), (x0, y0, z0)),
      mirrored = side > 0.0
    )

  /** Winding for the face that points away from the roof. The two slopes and the two gable ends are
    * mirror images, so each pair needs its corners in opposite order: emitted alike, the +z slope
    * and the +x gable would face inward and the outline hull would cover them in ink.
    */
  private def outward(
    triangles: Vector[(Double, Double, Double)],
    mirrored: Boolean
  ): Vector[(Double, Double, Double)] =
    if mirrored then triangles.grouped(3).flatMap(_.reverse).toVector else triangles

  private def buildGeometry(triangles: Vector[(Double, Double, Double)]): BufferGeometry[?, ?] =
    // The attribute takes the flat component count, so a triangle list of n corners needs 3n slots.
    val attribute = new Float32BufferAttribute((triangles.size * 3).toDouble, itemSize = 3)
    triangles.zipWithIndex.foreach { case ((x, y, z), index) => attribute.setXYZ(index, x, y, z) }
    val geometry = new BufferGeometry()
    geometry.setAttribute("position", attribute)
    geometry.computeVertexNormals()
    geometry
end PitchedRoof

/** Procedural Japanese two-storey detached house: cream plaster mass on a concrete plinth, dark
  * timber bands, windows and an entrance porch under a pitched kawara-tiled roof, plus the
  * exterior AC unit (室外機) on the flank. Every surface is cel-shaded through [[ToonMaterial]]
  * with its world transform baked into the geometry, so the parts are authored in the flat plane
  * and the shader seats the house on the planet vertex by vertex.
  *
  * The plinth sinks below the terrain on purpose: the rendered ground chords across its 10-unit
  * cells while the house seats at [[TerrainHeightmap]] elevation, and the buried base absorbs
  * that difference without floating the mass or clipping it into the hill.
  */
object HouseGenerator:

  final private[sakura] case class Plan(
    centerX: Double,
    centerZ: Double,
    facing: Double,
    width: Double,
    depth: Double,
    baseY: Double
  )

  private[sakura] def plan(
    centerX: Double,
    centerZ: Double,
    facing: Double,
    width: Double,
    depth: Double,
    groundAt: (Double, Double) => Double
  ): Plan =
    Plan(centerX, centerZ, facing, width, depth, groundAt(centerX, centerZ))

  private val plinthTop     = 0.25
  private val plinthDepth   = 1.4
  private val eaveHeight    = 5.65
  private val storyLine     = 2.7
  private val bandThickness = 0.26
  private val roofOverhang  = 0.62
  private val roofPitch     = 0.62
  private val roofEaveLift  = 0.1
  private val roofBands     = 3
  private val doorWidth     = 1.05
  private val doorHeight    = 2.15

  /** Adds a house seated at the planned terrain elevation to the shared batch. */
  private[sakura] def addTo(batch: CelBatch, plan: Plan): Unit =
    val centerX = plan.centerX
    val centerZ = plan.centerZ
    val facing  = plan.facing
    val width   = plan.width
    val depth   = plan.depth
    val placer  = WorldPlacer(batch, centerX, plan.baseY, centerZ, facing)

    placer(new BoxGeometry(width + 0.5, plinthDepth, depth + 0.5), Palette.concrete,
      (0.0, plinthTop - plinthDepth / 2.0, 0.0))
    placer(new BoxGeometry(width, eaveHeight - plinthTop, depth), Palette.plaster,
      (0.0, plinthTop + (eaveHeight - plinthTop) / 2.0, 0.0), outlined = true)
    List(storyLine, eaveHeight - plinthTop - 0.1).foreach { bandY =>
      placer(new BoxGeometry(width + 0.12, bandThickness, depth + 0.12), Palette.darkTimber,
        (0.0, plinthTop + bandY, 0.0))
    }

    entrance(placer, width, depth)
    windows(placer, width, depth)
    acUnit(placer, width, depth)
    roof(placer, width, depth)
  end addTo

  /** The 玄関: door leaf under its bracketed mini roof, two posts and the concrete step. */
  private def entrance(place: WorldPlacer, width: Double, depth: Double): Unit =
    val doorX     = width * 0.18
    val frontFace = depth / 2.0

    place(new BoxGeometry(doorWidth, doorHeight, 0.1), Palette.darkTimber,
      (doorX, plinthTop + doorHeight / 2.0, frontFace + 0.05))
    place(new BoxGeometry(1.7, 0.1, 1.0), Palette.darkTimber, (doorX, 2.62, frontFace + 0.42), tilt = -0.1)
    List(-0.72, 0.72).foreach { offset =>
      place(new BoxGeometry(0.09, 2.35, 0.09), Palette.darkTimber, (doorX + offset, plinthTop + 1.175, frontFace + 0.85))
    }
    place(new BoxGeometry(1.7, 0.24, 0.5), Palette.concrete, (doorX, plinthTop - 0.02, frontFace + 0.6))

  /** Windows: three across the upper front, one lower front beside the door, one on the flank.
    * Each is a dark glass plate with a timber lintel and sill.
    */
  private def windows(place: WorldPlacer, width: Double, depth: Double): Unit =
    val frontFace = depth / 2.0

    def windowAt(x: Double, y: Double, z: Double, onFlank: Boolean): Unit =
      place(
        if onFlank then new BoxGeometry(0.09, 1.05, 1.15) else new BoxGeometry(1.15, 1.05, 0.09),
        Palette.asphalt, (x, y, z)
      )
      place(
        if onFlank then new BoxGeometry(0.13, 0.1, 1.3) else new BoxGeometry(1.3, 0.1, 0.13),
        Palette.darkTimber, (x, y + 0.585, z)
      )
      place(
        if onFlank then new BoxGeometry(0.14, 0.09, 1.3) else new BoxGeometry(1.3, 0.09, 0.14),
        Palette.darkTimber, (x, y - 0.575, z)
      )

    List(-width * 0.28, 0.0, width * 0.28).foreach { offsetX =>
      windowAt(offsetX, plinthTop + storyLine + 1.7, frontFace + 0.045, onFlank = false)
    }
    windowAt(-width * 0.26, plinthTop + 1.55, frontFace + 0.045, onFlank = false)
    windowAt(width / 2.0 + 0.045, plinthTop + 1.55, -depth * 0.12, onFlank = true)
  end windows

  /** The 室外機: fan body on the flank wall, fan disc facing out, two mounting brackets. */
  private def acUnit(place: WorldPlacer, width: Double, depth: Double): Unit =
    val wallX = width / 2.0
    val unitZ = -depth * 0.16
    val unitY = plinthTop + 1.35

    place(new BoxGeometry(0.34, 0.62, 0.78), Palette.concrete, (wallX + 0.17, unitY, unitZ))
    val fanGeometry = new CylinderGeometry(0.2, 0.2, 0.06, 20.0)
    fanGeometry.rotateZ(math.Pi / 2.0)
    place(fanGeometry, Palette.asphalt, (wallX + 0.37, unitY + 0.04, unitZ))
    List(-0.26, 0.26).foreach { offset =>
      place(new BoxGeometry(0.3, 0.06, 0.08), Palette.darkTimber, (wallX + 0.02, unitY - 0.34, unitZ + offset))
    }

  /** The kawara roof: flared-eave slopes, plaster gable closures, ridge cap. */
  private def roof(place: WorldPlacer, width: Double, depth: Double): Unit =
    val halfWidth = (width + 2.0 * roofOverhang) / 2.0
    val halfSpan  = depth / 2.0 + roofOverhang
    val rise      = roofPitch * halfSpan

    val slopes = PitchedRoof.slopes(halfWidth, halfSpan, rise, roofEaveLift, roofBands)
    slopes.translate(0.0, eaveHeight, 0.0)
    place(slopes, Palette.roofTile, (0.0, 0.0, 0.0), outlined = true, bothFaces = true)

    val gable = PitchedRoof.gableEnds(halfWidth, halfSpan, rise, roofEaveLift)
    gable.translate(0.0, eaveHeight, 0.0)
    place(gable, Palette.plaster, (0.0, 0.0, 0.0), bothFaces = true)

    place(new BoxGeometry(width + 2.0 * roofOverhang + 0.2, 0.18, 0.5), Palette.roofTile,
      (0.0, eaveHeight + rise + 0.06, 0.0))
end HouseGenerator
