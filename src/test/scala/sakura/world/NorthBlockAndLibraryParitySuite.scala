package sakura.world

import sakura.ThreeInterop
import vendor.three.*

import scala.scalajs.js

/** Ordered structural parity for the shrine and shopping-street assemblies against
  * `/tmp/upstream-oracle/world-oracle.json`, whose rows were traced from the upstream
  * JavaScript (`src/world/shrine.js`, `showa.js`, `shotengai.js`, `shops.js`) under Node
  * with three 0.185.1. Rows are (meshCount, instancedCount, vertexCount, multiMatCount,
  * groupSliceCount, castShadowCount, receiveShadowCount, bbox, colorDigest) per top child,
  * in build order; bounding boxes ignore instance matrices, exactly as the oracle tracer does.
  */
class NorthBlockAndLibraryParitySuite extends munit.FunSuite:

  override def beforeAll(): Unit = WorldCanvasStub.install()

  private type ChildRow =
    (Int, Int, Int, Int, Int, Int, Int, Vector[Double], Vector[Int])
  private type ColliderRow = (Double, Double, Double, Double, Double, Option[Double])
  private type PlatformRow = (Double, Double, Double, Double, Double)

  final private case class Traced(
    meshCount: Int,
    instancedCount: Int,
    vertexCount: Int,
    multiMatCount: Int,
    groupSliceCount: Int,
    castShadowCount: Int,
    receiveShadowCount: Int,
    bbox: Vector[Double],
    colorDigest: Vector[Int]
  )

  private def round3(value: Double): Double = math.round(value * 1000.0) / 1000.0

  private def traceChild(child: Object3D[?]): Traced =
    child.updateMatrixWorld(js.defined(true))
    var meshCount          = 0
    var instancedCount     = 0
    var vertexCount        = 0
    var multiMatCount      = 0
    var groupSliceCount    = 0
    var castShadowCount    = 0
    var receiveShadowCount = 0
    val colors             = scala.collection.mutable.ArrayBuffer.empty[Int]
    val min                = Array(Double.PositiveInfinity, Double.PositiveInfinity, Double.PositiveInfinity)
    val max                = Array(Double.NegativeInfinity, Double.NegativeInfinity, Double.NegativeInfinity)
    child.traverse { (node: Object3D[Object3DEventMap]) =>
      if ThreeInterop.isMesh(node) then
        meshCount += 1
        ThreeInterop.asInstancedObject(node).foreach(instanced => instancedCount += instanced.count.toInt)
        vertexCount += ThreeInterop.positionCount(node.asInstanceOf[Mesh[?, ?, ?]].geometry).toInt
        ThreeInterop.asMesh(node).foreach { mesh =>
          val geometry = ThreeInterop.meshGeometry(mesh)
          groupSliceCount += geometry.groups.length
          materialColorsOf(mesh).take(8 - colors.length).foreach(colors += _)
          if colors.length > 8 then colors.remove(8, colors.length - 8)
          multiMatCount += (if hasMaterialArray(mesh) then 1 else 0)
        }
        if node.castShadow then castShadowCount += 1
        if node.receiveShadow then receiveShadowCount += 1
        ThreeInterop.asMesh(node).foreach { mesh =>
          val geometry  = ThreeInterop.meshGeometry(mesh)
          val matrix    = ThreeInterop.matrixElements(node.matrixWorld)
          val position  = ThreeInterop.attributeDoubles(geometry, "position")
          var localMinX = Double.PositiveInfinity
          var localMinY = Double.PositiveInfinity
          var localMinZ = Double.PositiveInfinity
          var localMaxX = Double.NegativeInfinity
          var localMaxY = Double.NegativeInfinity
          var localMaxZ = Double.NegativeInfinity
          var index     = 0
          val count     = position.length / 3
          while index < count do
            val x = position(index * 3)
            val y = position(index * 3 + 1)
            val z = position(index * 3 + 2)
            if x < localMinX then localMinX = x
            if x > localMaxX then localMaxX = x
            if y < localMinY then localMinY = y
            if y > localMaxY then localMaxY = y
            if z < localMinZ then localMinZ = z
            if z > localMaxZ then localMaxZ = z
            index += 1
          /* The oracle measures as three's `Box3.setFromObject` does: each mesh's local
           * axis-aligned box is transformed and unioned, so a rotated part reads as the box
           * its box sweeps out rather than as its own vertex extent. */
          Vector(localMinX, localMaxX).foreach { x =>
            Vector(localMinY, localMaxY).foreach { y =>
              Vector(localMinZ, localMaxZ).foreach { z =>
                val worldX = matrix(0) * x + matrix(4) * y + matrix(8) * z + matrix(12)
                val worldY = matrix(1) * x + matrix(5) * y + matrix(9) * z + matrix(13)
                val worldZ = matrix(2) * x + matrix(6) * y + matrix(10) * z + matrix(14)
                if worldX < min(0) then min(0) = worldX
                if worldX > max(0) then max(0) = worldX
                if worldY < min(1) then min(1) = worldY
                if worldY > max(1) then max(1) = worldY
                if worldZ < min(2) then min(2) = worldZ
                if worldZ > max(2) then max(2) = worldZ
              }
            }
          }
        }
    }
    val bbox =
      if min(0).isInfinite then Vector.empty
      else Vector(round3(min(0)), round3(min(1)), round3(min(2)), round3(max(0)), round3(max(1)), round3(max(2)))
    Traced(meshCount, instancedCount, vertexCount, multiMatCount, groupSliceCount,
      castShadowCount, receiveShadowCount, bbox, colors.toVector.take(8))
  end traceChild

  private def renderDouble(value: Double): String =
    if value == value.toLong.toDouble then s"${value.toLong}.0" else value.toString

  private def render(traced: Traced): String =
    val bbox   = traced.bbox.map(renderDouble).mkString("Vector(", ", ", ")")
    val colors = traced.colorDigest.mkString("Vector(", ", ", ")")
    s"Traced(${traced.meshCount}, ${traced.instancedCount}, ${traced.vertexCount}, ${traced.multiMatCount}, " +
      s"${traced.groupSliceCount}, ${traced.castShadowCount}, ${traced.receiveShadowCount}, $bbox, $colors)"

  private def assertChildren(actual: Vector[Object3D[?]], expected: Vector[ChildRow], where: String): Unit =
    assertEquals(actual.length, expected.length, s"$where top-child count")
    val mismatches = scala.collection.mutable.ArrayBuffer.empty[String]
    actual.zip(expected).zipWithIndex.foreach { case ((child, row), index) =>
      val traced = traceChild(child)
      val e      = render(Traced(row._1, row._2, row._3, row._4, row._5, row._6, row._7, row._8, row._9))
      val got    = render(traced)
      val name   = if child.name.nonEmpty then child.name else child.`type`
      if got != e then mismatches += s"child[$index] ($name)\n  got $got\n  exp $e"
    }
    if mismatches.nonEmpty then
      fail(s"$where: ${mismatches.length} of ${expected.length} children differ\n" + mismatches.mkString("\n"))

  private def assertColliders(actual: Vector[Collider], expected: Vector[ColliderRow], where: String): Unit =
    assertEquals(actual.length, expected.length, s"$where collider count")
    actual.zip(expected).foreach { (c, e) =>
      assertEqualsDouble(round3(c.x0), e._1, 0.0, s"$where collider x0")
      assertEqualsDouble(round3(c.z0), e._2, 0.0, s"$where collider z0")
      assertEqualsDouble(round3(c.x1), e._3, 0.0, s"$where collider x1")
      assertEqualsDouble(round3(c.z1), e._4, 0.0, s"$where collider z1")
      assertEqualsDouble(round3(c.top), e._5, 0.0, s"$where collider top")
    }

  private def assertPlatforms(actual: Vector[Platform], expected: Vector[PlatformRow], where: String): Unit =
    assertEquals(actual.length, expected.length, s"$where platform count")
    actual.zip(expected).foreach { (p, e) =>
      assertEqualsDouble(round3(p.x0), e._1, 0.0, s"$where platform x0")
      assertEqualsDouble(round3(p.x1), e._2, 0.0, s"$where platform x1")
      assertEqualsDouble(round3(p.z0), e._3, 0.0, s"$where platform z0")
      assertEqualsDouble(round3(p.z1), e._4, 0.0, s"$where platform z1")
      assertEqualsDouble(round3(p.top), e._5, 0.0, s"$where platform top")
    }

  private def materialColorsOf(mesh: Mesh[?, ?, ?]): Vector[Int] =
    mesh.material match
      case materials: js.Array[Material[?]] =>
        materials.toVector.collect {
          case material: MeshBasicMaterial => material.color.getHex().toInt
          case material: MeshToonMaterial  => material.color.getHex().toInt
        }
      case material: MeshBasicMaterial => Vector(material.color.getHex().toInt)
      case material: MeshToonMaterial  => Vector(material.color.getHex().toInt)
      case _                           => Vector.empty

  private def hasMaterialArray(mesh: Mesh[?, ?, ?]): Boolean =
    mesh.material match
      case _: js.Array[Material[?]] => true
      case _                        => false

  private def districtCtx(): WorldCtx =
    val scene = new Scene()
    val root  = new Group()
    root.name = "world"
    ThreeInterop.addChild(scene, root)
    WorldCtx(scene, root)

  private lazy val libraryChildren: Vector[ChildRow] = Vector(
    (1, 0, 24, 0, 6, 0, 1,
      Vector(9.6, 0.45, 46.9, 22.2, 0.52, 50.7), Vector(14276061)),
    (1, 0, 24, 0, 6, 0, 1,
      Vector(9.6, 0.455, 49.6, 22.2, 0.525, 50.7), Vector(13616828)),
    (58, 0, 4308, 1, 180, 9, 9,
      Vector(10.75, 0.45, 50.15, 20.85, 8.11, 57.94),
      Vector(16773588, 14078666, 10130596, 10338516, 10130596, 10338516, 10130596, 10338516)),
    (16, 0, 656, 0, 87, 8, 4,
      Vector(11.69, 0.44, 48.665, 20.652, 3.39, 50.65),
      Vector(14078666, 14078666, 14078666, 14276061, 14078666, 12106950, 14078666, 13810584)),
    (1, 0, 24, 0, 6, 0, 1,
      Vector(11.7, 0.72, 49.525, 17.8, 0.82, 50.825), Vector(13616828)),
    (2, 0, 96, 0, 0, 1, 1,
      Vector(13.43, 0.52, 48.695, 14.77, 0.823, 49.575), Vector(13616828, 12762568)),
    (2, 0, 48, 0, 12, 0, 1,
      Vector(13.45, 0.82, 50.55, 14.75, 0.847, 51.25), Vector(7236216, 10260600)),
    (14, 0, 422, 0, 32, 1, 0,
      Vector(12.389, 0.82, 50.848, 12.896, 1.906, 51.366),
      Vector(13618132, 8883094, 12106950, 14997728, 6969950, 12106950, 14216436, 6969950)),
    (7, 0, 168, 0, 42, 3, 3,
      Vector(20.71, 0.45, 51.21, 21.29, 1.16, 52.79),
      Vector(6131640, 8883094, 8367722, 8883094, 14205770, 8883094, 6262634)),
    (1, 0, 4, 0, 0, 0, 0,
      Vector(12.16, 2.04, 51.17, 12.16, 2.6, 51.59), Vector(16777215)),
    (1, 0, 4, 0, 0, 0, 0,
      Vector(16.04, 2.045, 51.18, 16.04, 2.595, 51.58), Vector(16777215)),
    (9, 0, 224, 0, 45, 2, 1,
      Vector(10.953, 0.52, 49.775, 11.747, 1.755, 50.425),
      Vector(12762568, 4160106, 3499606, 3288635, 3499606, 16777215, 3499606, 8883094)),
    (12, 0, 228, 1, 54, 4, 1,
      Vector(9.003, 0.52, 48.989, 11.1, 2.743, 50.44),
      Vector(9072466, 9072466, 9072466, 9072466, 9072466, 9072466, 16777215, 9072466)),
    (4, 16, 1751, 0, 0, 4, 4,
      Vector(10.001, 0.504, 47.43, 11.829, 1.587, 47.97), Vector(3288635, 16777215, 12106950, 12106950)),
    (1, 0, 120, 0, 0, 0, 0,
      Vector(10.86, 0.522, 45.97, 10.94, 0.542, 50.11), Vector(16052982)),
    (9, 0, 216, 0, 54, 9, 9,
      Vector(13.1, 0.52, 47.094, 14.9, 1.442, 47.625),
      Vector(12625282, 12625282, 12625282, 12625282, 12625282, 8883094, 8883094, 8883094)),
    (9, 0, 216, 0, 54, 9, 9,
      Vector(15.5, 0.52, 47.094, 17.3, 1.442, 47.625),
      Vector(12625282, 12625282, 12625282, 12625282, 12625282, 8883094, 8883094, 8883094)),
    (21, 0, 1080, 0, 30, 12, 0,
      Vector(21.25, 0.52, 46.85, 22.25, 0.98, 50.35),
      Vector(9402978, 10256222, 10256222, 10256222, 10256222, 5940600, 14697791, 4161376)),
    (11, 0, 672, 0, 9, 6, 1,
      Vector(11.883, 0.52, 49.819, 12.59, 1.395, 50.539),
      Vector(12941914, 11561548, 7035466, 4161376, 5940600, 5940600, 4161376, 5940600)),
    (11, 0, 672, 0, 9, 6, 1,
      Vector(16.728, 0.52, 46.73, 17.315, 1.215, 47.279),
      Vector(12941914, 11561548, 7035466, 4161376, 5940600, 5940600, 4161376, 5940600)),
    (2, 0, 88, 0, 6, 1, 0,
      Vector(18.63, 0.52, 47.43, 18.77, 4.52, 47.57), Vector(8883094)),
    (1, 0, 52, 0, 3, 0, 0,
      Vector(18.54, 0.52, 47.34, 18.86, 0.7, 47.66), Vector(12762568)),
    (1, 0, 24, 0, 6, 0, 0,
      Vector(18.67, 4.45, 46.91, 18.73, 4.51, 47.53), Vector(8883094)),
    (1, 0, 26, 0, 1, 0, 0,
      Vector(18.42, 4.27, 46.66, 18.98, 4.49, 47.22), Vector(12106950)),
    (1, 0, 24, 0, 6, 0, 0,
      Vector(18.59, 4.225, 46.83, 18.81, 4.275, 47.05), Vector(16773840)),
    (1, 0, 24, 0, 6, 0, 1,
      Vector(12.05, 0.502, 47.45, 13.15, 0.562, 48.55), Vector(9402978)),
    (1, 0, 96, 0, 0, 0, 0,
      Vector(11.91, 0.5, 47.31, 13.29, 0.6, 48.69), Vector(12762568)),
    (1, 0, 24, 0, 6, 0, 1,
      Vector(19.85, 0.502, 47.85, 20.95, 0.562, 48.95), Vector(9402978)),
    (1, 0, 96, 0, 0, 0, 0,
      Vector(19.71, 0.5, 47.71, 21.09, 0.6, 49.09), Vector(12762568)),
    (1, 2, 4, 0, 0, 0, 0,
      Vector(-0.105, 0.0, -0.145, 0.105, 0.0, 0.145), Vector(16513780)),
    (1, 0, 24, 0, 6, 0, 1,
      Vector(1.7, 0.45, 46.8, 6.7, 0.52, 49.4), Vector(14276061)),
    (1, 0, 24, 0, 6, 0, 1,
      Vector(1.9, 0.5, 47.25, 2.2, 0.55, 47.95), Vector(7170170)),
    (24, 0, 2237, 1, 18, 6, 5,
      Vector(2.76, 0.52, 47.86, 4.24, 3.19, 49.34),
      Vector(14215408, 14215408, 14215408, 14215408, 14215408, 14215408, 14215408, 14215408)),
    (8, 0, 248, 1, 42, 3, 1,
      Vector(4.97, 0.52, 48.608, 6.63, 2.679, 49.03),
      Vector(7305862, 12762568, 7305862, 12762568, 7305862, 7305862, 7305862, 7305862)),
    (9, 0, 216, 0, 54, 2, 1,
      Vector(5.77, 0.52, 47.098, 6.818, 1.286, 47.94),
      Vector(4882350, 4157340, 8883094, 8883094, 4157340, 4157340, 4157340, 15921124)),
    (3, 0, 120, 0, 12, 2, 0,
      Vector(1.945, 0.52, 46.645, 2.06, 1.37, 50.155), Vector(8883094, 16447215, 12106950)),
    (1, 0, 52, 0, 3, 1, 0,
      Vector(6.835, 0.52, 46.835, 6.965, 4.12, 46.965), Vector(8883094)),
    (1, 0, 52, 0, 3, 0, 0,
      Vector(6.75, 0.52, 46.75, 7.05, 0.68, 47.05), Vector(12762568)),
    (1, 0, 24, 0, 6, 0, 0,
      Vector(6.87, 4.05, 46.36, 6.93, 4.11, 46.92), Vector(8883094)),
    (1, 0, 26, 0, 1, 0, 0,
      Vector(6.64, 3.9, 46.14, 7.16, 4.1, 46.66), Vector(12106950)),
    (1, 0, 24, 0, 6, 0, 0,
      Vector(6.8, 3.855, 46.3, 7.0, 3.905, 46.5), Vector(16773840)),
    (11, 0, 672, 0, 9, 6, 1,
      Vector(2.157, 0.52, 46.853, 2.848, 1.244, 47.459),
      Vector(12941914, 11561548, 7035466, 4161376, 5940600, 5940600, 4161376, 5940600)),
    (7, 0, 432, 0, 9, 5, 1,
      Vector(2.102, 0.52, 49.384, 2.652, 1.17, 49.858),
      Vector(12941914, 11561548, 7035466, 4161376, 5940600, 5940600, 4161376)),
    (1, 1, 4, 0, 0, 0, 0,
      Vector(-0.105, 0.0, -0.145, 0.105, 0.0, 0.145), Vector(16513780)),
  )

  private lazy val libraryColliderRows: Vector[ColliderRow] = Vector(
    (10.9, 50.5, 12.0, 57.7, 6.75, None), (16.2, 50.5, 20.7, 57.7, 6.75, None), (12.0, 51.35, 16.2, 57.7, 6.75, None),
    (17.4, 49.09, 20.6, 49.27, 1.7, None), (11.87, 49.34, 12.07, 49.54, 3.25, None),
    (16.13, 49.34, 16.33, 49.54, 3.25, None), (11.0, 49.85, 11.7, 50.4, 1.72, None), (9.3, 49.15, 10.8, 50.5, 2.62, None),
    (18.52, 47.32, 18.88, 47.68, 4.52, None), (2.85, 47.95, 4.15, 49.25, 3.22, None),
    (4.9, 48.75, 6.7, 49.05, 2.62, None), (5.85, 47.2, 6.75, 47.8, 1.22, None), (6.74, 46.74, 7.06, 47.06, 4.12, None),
  )

  private lazy val libraryInteractables: Vector[String] = Vector(
  )

  private lazy val libraryPlatformRows: Vector[PlatformRow] = Vector(
    (9.6, 22.2, 46.9, 50.7, 0.52), (9.6, 22.2, 49.6, 50.7, 0.525), (12.05, 16.15, 50.5, 51.45, 0.82),
    (11.7, 17.8, 49.525, 50.825, 0.82), (13.45, 14.75, 48.675, 49.155, 0.67), (13.45, 14.75, 49.115, 49.595, 0.82),
    (20.023, 20.72, 49.25, 50.55, 0.556), (19.566, 20.263, 49.25, 50.55, 0.599), (19.109, 19.806, 49.25, 50.55, 0.642),
    (18.651, 19.349, 49.25, 50.55, 0.685), (18.194, 18.891, 49.25, 50.55, 0.728), (17.737, 18.434, 49.25, 50.55, 0.771),
    (17.28, 17.977, 49.25, 50.55, 0.814), (1.7, 6.7, 46.8, 49.4, 0.52),
  )

  test("library assembly matches the upstream structural trace") {
    val ctx    = districtCtx()
    val result = Library.buildLibrary(ctx)
    assertChildren(ctx.root.children.toVector, libraryChildren, "library")
    assertColliders(ctx.colliders.toVector, libraryColliderRows, "library")
    assertPlatforms(ctx.platforms.toVector, libraryPlatformRows, "library")
    assertEquals(ctx.cuts.length, 0, "library cut count")
    assertEquals(ctx.interactables.toVector.map(_.label), libraryInteractables, "library interactables")
    assert(result != null, "library returns its planting")
  }

  private lazy val northblockChildren: Vector[ChildRow] = Vector(
    (1, 0, 24, 0, 6, 0, 1,
      Vector(40.0, 0.426, 43.5, 48.6, 0.516, 46.9), Vector(10130854)),
    (1, 0, 72, 0, 0, 0, 0,
      Vector(40.333, 0.526, 45.15, 46.9, 0.546, 45.25), Vector(16052982)),
    (1, 0, 26, 0, 0, 0, 1,
      Vector(30.8, 0.492, 46.4, 34.0, 0.5, 63.0), Vector(10130854)),
    (1, 0, 432, 0, 0, 0, 1,
      Vector(30.89, 0.481, 46.511, 31.19, 0.525, 62.889), Vector(7170170)),
    (1, 0, 76, 0, 3, 0, 1,
      Vector(32.6, 0.5, 49.3, 33.2, 0.54, 49.9), Vector(8883094)),
    (1, 0, 76, 0, 3, 0, 1,
      Vector(32.6, 0.5, 57.1, 33.2, 0.54, 57.7), Vector(8883094)),
    (1, 0, 24, 0, 6, 0, 1,
      Vector(31.15, 0.505, 52.45, 32.65, 0.525, 54.35), Vector(9341596)),
    (1, 0, 24, 0, 6, 0, 1,
      Vector(32.4, 0.505, 59.5, 33.6, 0.525, 60.9), Vector(9341596)),
    (27, 0, 2608, 2, 72, 12, 9,
      Vector(0.72, 0.45, 48.714, 7.62, 7.58, 55.086),
      Vector(9143704, 10338516, 15923455, 7236218, 14606054, 14606054, 16447215, 14606054)),
    (1, 0, 24, 0, 6, 0, 1,
      Vector(0.3, 0.45, 48.9, 2.0, 0.52, 54.9), Vector(14276061)),
    (19, 0, 708, 0, 72, 13, 6,
      Vector(1.042, 0.52, 49.93, 1.474, 1.535, 51.67),
      Vector(4161471, 3092800, 14834250, 3092800, 5217642, 3092800, 4161471, 3092800)),
    (3, 0, 72, 0, 18, 3, 3,
      Vector(1.158, 0.52, 53.564, 1.75, 1.36, 54.017), Vector(5217642, 14834250, 5217642)),
    (3, 0, 102, 0, 6, 2, 0,
      Vector(1.15, 0.52, 49.357, 1.45, 2.42, 49.889), Vector(12106950, 14276061, 16777215)),
    (64, 24, 1868, 1, 345, 62, 64,
      Vector(0.22, 0.437, 52.228, 1.64, 2.56, 53.372),
      Vector(8883094, 14368824, 16777215, 16643820, 2763832, 14368824, 14368824, 14368824)),
    (8, 0, 370, 0, 25, 1, 0,
      Vector(1.04, 0.52, 51.703, 1.56, 1.455, 52.197),
      Vector(3054232, 8883094, 3092800, 3092800, 16184040, 14697791, 4026052, 16039987)),
    (6, 0, 1779, 0, 0, 4, 0,
      Vector(0.369, 0.518, 53.069, 1.096, 1.632, 54.906),
      Vector(3288635, 4157340, 12106950, 12106950, 13223632, 13223632)),
    (3, 0, 102, 0, 7, 2, 0,
      Vector(1.513, 0.498, 49.651, 1.921, 2.012, 50.104), Vector(12757108, 11045470, 9072466)),
    (1, 0, 24, 1, 6, 1, 0,
      Vector(1.53, 0.779, 54.05, 1.63, 1.441, 54.55), Vector(16777215, 16777215, 10256222, 10256222, 10256222, 10256222)),
    (1, 0, 288, 0, 0, 1, 1,
      Vector(2.75, 0.66, 54.96, 6.05, 3.77, 55.24), Vector(12762568)),
    (1, 0, 280, 0, 0, 1, 0,
      Vector(3.22, 0.85, 55.57, 5.58, 4.74, 55.63), Vector(12106950)),
    (11, 0, 308, 0, 33, 4, 0,
      Vector(3.68, 0.45, 55.98, 6.12, 2.224, 56.22),
      Vector(12106950, 12762568, 12106950, 12762568, 12106950, 16447215, 14210268, 16500440)),
    (18, 0, 1818, 0, 15, 18, 0,
      Vector(5.593, 0.45, 55.247, 6.607, 1.412, 55.546),
      Vector(12106950, 12941914, 4161376, 5940600, 5940600, 12941914, 4161376, 5940600)),
    (11, 0, 328, 0, 63, 1, 1,
      Vector(7.07, 0.61, 50.07, 7.43, 1.395, 50.93),
      Vector(14868708, 8883094, 8883094, 8883094, 8883094, 8883094, 8883094, 13289423)),
    (1, 0, 24, 0, 6, 0, 1,
      Vector(23.2, 0.45, 47.3, 30.4, 0.51, 52.0), Vector(11117483)),
    (1, 0, 24, 0, 0, 0, 0,
      Vector(23.35, 0.52, 47.6, 23.45, 0.54, 51.6), Vector(16052982)),
    (1, 0, 24, 0, 0, 0, 0,
      Vector(25.617, 0.52, 47.6, 25.717, 0.54, 51.6), Vector(16052982)),
    (1, 0, 24, 0, 0, 0, 0,
      Vector(27.883, 0.52, 47.6, 27.983, 0.54, 51.6), Vector(16052982)),
    (1, 0, 24, 0, 0, 0, 0,
      Vector(30.15, 0.52, 47.6, 30.25, 0.54, 51.6), Vector(16052982)),
    (1, 0, 24, 0, 6, 0, 0,
      Vector(23.833, 0.515, 51.02, 25.233, 0.625, 51.18), Vector(12762568)),
    (1, 0, 24, 0, 6, 1, 0,
      Vector(24.223, 0.4, 50.042, 24.843, 0.78, 50.558), Vector(13159636)),
    (1, 0, 24, 0, 6, 0, 0,
      Vector(24.283, 0.46, 49.75, 24.783, 0.56, 50.05), Vector(8883094)),
    (1, 0, 24, 0, 6, 0, 0,
      Vector(26.1, 0.515, 51.02, 27.5, 0.625, 51.18), Vector(12762568)),
    (1, 0, 24, 0, 6, 1, 0,
      Vector(26.49, 0.4, 50.042, 27.11, 0.78, 50.558), Vector(13159636)),
    (1, 0, 24, 0, 6, 0, 0,
      Vector(26.55, 0.46, 49.75, 27.05, 0.56, 50.05), Vector(8883094)),
    (1, 0, 24, 0, 6, 0, 0,
      Vector(28.367, 0.515, 51.02, 29.767, 0.625, 51.18), Vector(12762568)),
    (1, 0, 24, 0, 6, 1, 0,
      Vector(28.757, 0.4, 50.042, 29.377, 0.78, 50.558), Vector(13159636)),
    (1, 0, 24, 0, 6, 0, 0,
      Vector(28.817, 0.46, 49.75, 29.317, 0.56, 50.05), Vector(8883094)),
    (2, 0, 48, 0, 12, 1, 1,
      Vector(29.48, 0.45, 47.75, 29.92, 1.85, 48.25), Vector(15000294)),
    (1, 0, 24, 0, 6, 0, 0,
      Vector(29.445, 1.25, 47.83, 29.475, 1.65, 48.17), Vector(3815236)),
    (4, 0, 152, 1, 18, 2, 0,
      Vector(29.78, 0.45, 48.99, 30.02, 3.15, 49.61),
      Vector(12106950, 12762568, 14606054, 14606054, 14606054, 14606054, 16777215, 16777215)),
    (1, 0, 46, 0, 3, 0, 0,
      Vector(23.346, 0.45, 51.6, 23.454, 1.05, 51.705), Vector(8883094)),
    (1, 0, 46, 0, 3, 0, 0,
      Vector(25.613, 0.45, 51.6, 25.72, 1.05, 51.705), Vector(8883094)),
    (1, 0, 46, 0, 3, 0, 0,
      Vector(27.88, 0.45, 51.6, 27.987, 1.05, 51.705), Vector(8883094)),
    (1, 0, 46, 0, 3, 0, 0,
      Vector(30.146, 0.45, 51.6, 30.254, 1.05, 51.705), Vector(8883094)),
    (2, 0, 96, 0, 0, 1, 2,
      Vector(22.84, 0.4, 47.29, 23.16, 1.85, 52.01), Vector(12762568, 14276061)),
    (2, 64, 120, 0, 0, 2, 0,
      Vector(22.149, -0.401, 48.799, 23.851, 1.301, 50.501), Vector(4161376, 5940600)),
    (24, 0, 3272, 0, 30, 13, 9,
      Vector(34.955, 0.45, 47.095, 43.17, 10.51, 55.705),
      Vector(8216902, 8216902, 5464698, 5464698, 15393492, 16777215, 5464698, 15393492)),
    (3, 0, 504, 0, 6, 3, 3,
      Vector(33.92, 0.45, 47.735, 34.18, 1.825, 50.465), Vector(12762568, 9072476, 11573878)),
    (3, 0, 648, 0, 6, 3, 3,
      Vector(33.92, 0.45, 51.535, 34.18, 1.825, 55.065), Vector(12762568, 9072476, 11573878)),
    (3, 0, 408, 0, 6, 3, 3,
      Vector(33.985, 0.45, 47.67, 35.965, 1.825, 47.93), Vector(12762568, 9072476, 11573878)),
    (3, 0, 408, 0, 6, 3, 3,
      Vector(33.985, 0.45, 54.87, 35.965, 1.825, 55.13), Vector(12762568, 9072476, 11573878)),
    (1, 0, 24, 0, 6, 1, 0,
      Vector(33.98, 0.45, 50.33, 34.12, 1.85, 50.47), Vector(7624255)),
    (1, 0, 24, 0, 6, 1, 0,
      Vector(33.98, 0.45, 51.53, 34.12, 1.85, 51.67), Vector(7624255)),
    (1, 0, 184, 0, 0, 0, 1,
      Vector(34.336, 0.45, 50.702, 36.054, 0.53, 52.19), Vector(13025483)),
    (19, 0, 1972, 0, 18, 19, 0,
      Vector(35.052, 0.45, 48.493, 35.343, 1.458, 49.707),
      Vector(12106950, 12941914, 4161376, 5940600, 5940600, 12941914, 12941914, 4161376)),
    (9, 0, 381, 0, 30, 1, 1,
      Vector(35.05, 0.45, 53.692, 35.65, 1.369, 54.308),
      Vector(12762568, 14276061, 9344168, 12106950, 8883094, 11047518, 11047518, 10246730)),
    (5, 0, 212, 0, 7, 1, 0,
      Vector(34.704, 0.45, 53.502, 35.096, 0.83, 53.898), Vector(12106950, 8883094, 12106950, 8883094, 9681102)),
    (8, 0, 519, 0, 30, 2, 1,
      Vector(34.031, 0.45, 49.731, 34.769, 2.0, 50.469),
      Vector(12762568, 11874863, 11874863, 14697791, 3288635, 11545640, 16184040)),
    (2, 0, 48, 0, 12, 0, 1,
      Vector(35.23, 0.45, 52.67, 35.57, 0.477, 53.23), Vector(9076586, 10260600)),
    (6, 0, 1779, 0, 0, 4, 0,
      Vector(33.241, 0.513, 52.861, 34.04, 1.636, 54.7), Vector(3288635, 5214058, 12106950, 12106950, 13223632, 13223632)),
    (11, 0, 338, 0, 25, 1, 0,
      Vector(35.071, 0.45, 53.206, 35.591, 1.534, 53.7),
      Vector(13618132, 8883094, 12106950, 14210276, 6969950, 12106950, 14216436, 6969950)),
    (47, 0, 9546, 1, 120, 11, 7,
      Vector(21.93, 0.45, 53.87, 30.6, 10.65, 63.975),
      Vector(5464698, 15921124, 16777215, 15921124, 5464698, 15921124, 5464698, 15921124)),
    (37, 0, 1016, 0, 198, 3, 1,
      Vector(30.28, 0.45, 54.96, 30.58, 1.72, 55.84),
      Vector(8883094, 8883094, 12172998, 9411238, 13620442, 6248296, 8883094, 16184040)),
    (3, 0, 920, 0, 6, 1, 0,
      Vector(30.099, 0.45, 56.72, 32.101, 2.803, 60.88), Vector(8883094, 11584720, 12106950)),
    (4, 12, 1751, 0, 0, 4, 4,
      Vector(30.881, 0.524, 58.53, 32.709, 1.607, 59.07), Vector(3288635, 16777215, 12106950, 12106950)),
    (7, 0, 168, 0, 42, 3, 3,
      Vector(30.31, 0.45, 60.91, 30.89, 1.16, 62.49),
      Vector(6131640, 8883094, 8367722, 8883094, 14205770, 8883094, 6262634)),
    (4, 0, 152, 1, 18, 2, 0,
      Vector(30.38, 0.45, 60.38, 30.62, 1.95, 60.82),
      Vector(12106950, 12762568, 14606054, 14606054, 14606054, 14606054, 16777215, 14606054)),
    (8, 0, 192, 0, 48, 1, 1,
      Vector(30.155, 0.45, 56.15, 30.63, 1.14, 56.65),
      Vector(12762568, 7305840, 6253154, 8883094, 9411218, 15921124, 6253154)),
    (11, 0, 328, 0, 63, 1, 1,
      Vector(22.46, 0.71, 55.15, 22.82, 1.515, 56.05),
      Vector(14868708, 8883094, 8883094, 8883094, 8883094, 8883094, 8883094, 13289423)),
    (11, 0, 328, 0, 63, 1, 1,
      Vector(22.46, 0.71, 57.75, 22.82, 1.515, 58.65),
      Vector(14868708, 8883094, 8883094, 8883094, 8883094, 8883094, 8883094, 13289423)),
    (11, 0, 672, 0, 9, 6, 1,
      Vector(30.063, 0.45, 54.09, 30.751, 1.279, 54.781),
      Vector(12941914, 11561548, 7035466, 4161376, 5940600, 5940600, 4161376, 5940600)),
    (11, 0, 435, 0, 40, 2, 11,
      Vector(30.07, 0.45, 59.244, 30.79, 0.806, 59.95),
      Vector(11571042, 13215864, 13215864, 13215864, 13215864, 13215864, 14207412, 14207412)),
    (1, 1, 4, 0, 0, 0, 0,
      Vector(-0.105, 0.0, -0.145, 0.105, 0.0, 0.145), Vector(16513780)),
    (30, 0, 3160, 0, 72, 13, 9,
      Vector(38.37, 0.45, 55.66, 45.808, 6.815, 65.14),
      Vector(5204824, 5464698, 15393492, 5464698, 15393492, 14083054, 14083054, 16777215)),
    (1, 0, 24, 0, 6, 0, 1,
      Vector(34.0, 0.45, 56.05, 39.2, 0.52, 64.75), Vector(10130854)),
    (1, 0, 24, 0, 0, 0, 0,
      Vector(56.0, 0.53, 34.3, 56.1, 0.55, 39.0), Vector(16052982)),
    (1, 0, 24, 0, 0, 0, 0,
      Vector(58.9, 0.53, 34.3, 59.0, 0.55, 39.0), Vector(16052982)),
    (1, 0, 24, 0, 0, 0, 0,
      Vector(61.8, 0.53, 34.3, 61.9, 0.55, 39.0), Vector(16052982)),
    (1, 0, 24, 0, 0, 0, 0,
      Vector(64.7, 0.53, 34.3, 64.8, 0.55, 39.0), Vector(16052982)),
    (1, 0, 24, 0, 6, 0, 0,
      Vector(38.42, 0.515, 56.85, 38.58, 0.625, 58.15), Vector(12762568)),
    (1, 0, 24, 0, 6, 0, 0,
      Vector(38.42, 0.515, 59.75, 38.58, 0.625, 61.05), Vector(12762568)),
    (1, 0, 24, 0, 6, 0, 0,
      Vector(38.42, 0.515, 62.65, 38.58, 0.625, 63.95), Vector(12762568)),
    (4, 0, 800, 0, 12, 1, 0,
      Vector(34.199, 0.45, 58.97, 39.001, 3.089, 61.83), Vector(8883094, 10270928, 12106950, 12762568)),
    (6, 0, 1779, 0, 0, 4, 0,
      Vector(37.785, 0.528, 57.466, 38.682, 1.642, 59.315),
      Vector(3288635, 10246730, 12106950, 12106950, 13223632, 13223632)),
    (6, 0, 1779, 0, 0, 4, 0,
      Vector(37.785, 0.528, 61.466, 38.682, 1.642, 63.315),
      Vector(3288635, 14196796, 12106950, 12106950, 13223632, 13223632)),
    (11, 0, 672, 0, 9, 6, 1,
      Vector(38.387, 0.45, 56.037, 39.072, 1.215, 56.659),
      Vector(12941914, 11561548, 7035466, 4161376, 5940600, 5940600, 4161376, 5940600)),
    (7, 0, 432, 0, 9, 5, 1,
      Vector(38.522, 0.45, 64.053, 39.01, 1.071, 64.596),
      Vector(12941914, 11561548, 7035466, 4161376, 5940600, 5940600, 4161376)),
    (2, 0, 48, 0, 12, 0, 1,
      Vector(38.47, 0.45, 56.53, 38.83, 0.477, 57.13), Vector(9076586, 10260600)),
    (2, 0, 48, 0, 12, 0, 1,
      Vector(38.47, 0.45, 62.33, 38.83, 0.477, 62.93), Vector(6974040, 10260600)),
    (8, 0, 519, 0, 30, 2, 1,
      Vector(34.253, 0.45, 56.703, 34.947, 2.0, 57.397),
      Vector(12762568, 11874863, 11874863, 14697791, 3288635, 11545640, 16184040)),
    (7, 0, 168, 0, 42, 3, 3,
      Vector(34.41, 0.45, 63.86, 34.99, 1.16, 65.44),
      Vector(6131640, 8883094, 8367722, 8883094, 14205770, 8883094, 6262634)),
    (12, 0, 480, 0, 66, 3, 1,
      Vector(37.775, 0.45, 63.38, 38.631, 2.195, 64.72),
      Vector(12762568, 13488847, 9411222, 9411222, 9411222, 13488847, 8883094, 13488847)),
    (8, 0, 254, 0, 18, 1, 0,
      Vector(38.45, 0.45, 59.65, 38.987, 1.533, 60.072),
      Vector(13618132, 8883094, 12106950, 14216436, 6969950, 12106950, 14216436, 6969950)),
    (11, 0, 914, 0, 6, 8, 8,
      Vector(42.58, 0.395, 48.11, 49.02, 6.67, 54.92),
      Vector(16777215, 15785178, 5204848, 9143446, 12106950, 8883094, 5464698, 14276061)),
    (4, 0, 1296, 0, 12, 2, 3,
      Vector(42.57, 0.45, 47.165, 46.43, 1.55, 47.435), Vector(12762568, 11117490, 14276061, 14276061)),
    (4, 0, 576, 0, 12, 2, 3,
      Vector(47.57, 0.45, 47.165, 49.03, 1.55, 47.435), Vector(12762568, 11117490, 14276061, 14276061)),
    (4, 0, 576, 0, 12, 2, 3,
      Vector(42.465, 0.45, 47.27, 42.735, 1.55, 49.03), Vector(12762568, 11117490, 14276061, 14276061)),
    (4, 0, 576, 0, 12, 2, 3,
      Vector(48.865, 0.45, 47.27, 49.135, 1.55, 49.03), Vector(12762568, 11117490, 14276061, 14276061)),
    (8, 0, 519, 0, 30, 2, 1,
      Vector(47.446, 0.45, 47.296, 48.154, 2.0, 48.004),
      Vector(12762568, 11874863, 11874863, 14697791, 3288635, 11545640, 16184040)),
    (8, 0, 192, 0, 48, 1, 1,
      Vector(48.093, 0.45, 47.206, 48.707, 1.14, 47.794),
      Vector(12762568, 7305840, 6253154, 8883094, 9411218, 15921124, 6253154)),
    (11, 0, 672, 0, 9, 6, 1,
      Vector(43.57, 0.45, 48.032, 44.234, 1.204, 48.746),
      Vector(12941914, 11561548, 7035466, 4161376, 5940600, 5940600, 4161376, 5940600)),
    (7, 0, 432, 0, 9, 5, 1,
      Vector(44.158, 0.45, 47.925, 44.761, 1.175, 48.527),
      Vector(12941914, 11561548, 7035466, 4161376, 5940600, 5940600, 4161376)),
    (13, 0, 336, 0, 39, 5, 0,
      Vector(45.08, 0.45, 55.18, 47.72, 2.224, 55.42),
      Vector(12106950, 12762568, 12106950, 12762568, 12106950, 16500440, 14210268, 16447215)),
    (11, 0, 328, 0, 63, 1, 1,
      Vector(42.56, 0.31, 51.9, 42.92, 1.075, 52.7),
      Vector(14868708, 8883094, 8883094, 8883094, 8883094, 8883094, 8883094, 13289423)),
    (2, 0, 88, 0, 6, 1, 0,
      Vector(30.375, 0.45, 54.525, 30.525, 5.05, 54.675), Vector(8883094)),
    (1, 0, 52, 0, 3, 0, 0,
      Vector(30.29, 0.45, 54.44, 30.61, 0.63, 54.76), Vector(12762568)),
    (1, 0, 24, 0, 6, 0, 0,
      Vector(30.42, 4.98, 54.58, 30.48, 5.04, 55.3), Vector(8883094)),
    (1, 0, 26, 0, 1, 0, 0,
      Vector(30.17, 4.8, 54.98, 30.73, 5.02, 55.54), Vector(12106950)),
    (1, 0, 24, 0, 6, 0, 0,
      Vector(30.34, 4.755, 55.15, 30.56, 4.805, 55.37), Vector(16773840)),
    (4, 0, 152, 1, 18, 2, 0,
      Vector(34.274, 0.45, 47.243, 34.726, 2.55, 47.557),
      Vector(12106950, 12762568, 14606054, 14606054, 14606054, 14606054, 16777215, 16777215)),
    (1, 0, 52, 0, 3, 1, 0,
      Vector(31.235, 0.446, 46.835, 31.365, 1.226, 46.965), Vector(12106950)),
    (1, 0, 52, 0, 3, 0, 0,
      Vector(31.24, 1.086, 46.84, 31.36, 1.206, 46.96), Vector(14697791)),
    (1, 0, 52, 0, 3, 1, 0,
      Vector(32.235, 0.446, 46.835, 32.365, 1.226, 46.965), Vector(12106950)),
    (1, 0, 52, 0, 3, 0, 0,
      Vector(32.24, 1.086, 46.84, 32.36, 1.206, 46.96), Vector(14697791)),
    (1, 0, 52, 0, 3, 1, 0,
      Vector(33.235, 0.446, 46.835, 33.365, 1.226, 46.965), Vector(12106950)),
    (1, 0, 52, 0, 3, 0, 0,
      Vector(33.24, 1.086, 46.84, 33.36, 1.206, 46.96), Vector(14697791)),
    (1, 0, 276, 0, 0, 1, 0,
      Vector(30.459, 0.5, 62.762, 34.341, 1.441, 62.842), Vector(12106950)),
    (5, 0, 535, 0, 0, 4, 0,
      Vector(30.24, 0.45, 47.65, 30.76, 8.85, 49.55), Vector(14078680, 3288635, 16447215, 12106950)),
    (5, 0, 535, 0, 0, 4, 0,
      Vector(30.24, 0.45, 57.45, 30.76, 8.65, 59.35), Vector(14078680, 3288635, 16447215, 12106950)),
    (1, 0, 340, 0, 0, 0, 0,
      Vector(23.591, 6.411, 45.58, 30.522, 8.352, 58.703), Vector(4998744)),
  )

  private lazy val northblockColliderRows: Vector[ColliderRow] = Vector(
    (1.95, 49.15, 7.05, 54.65, 6.15, None), (0.72, 52.24, 1.84, 53.36, 2.47, None), (29.4, 47.7, 30.0, 48.3, 1.85, None),
    (22.85, 47.3, 23.15, 52.0, 1.8, None), (35.8, 47.7, 42.6, 55.1, 8.71, None), (33.91, 47.8, 34.19, 50.4, 1.83, None),
    (33.91, 51.6, 34.19, 55.0, 1.83, None), (34.05, 47.66, 35.9, 47.94, 1.83, None),
    (34.05, 54.86, 35.9, 55.14, 1.83, None), (22.6, 54.0, 30.1, 62.4, 9.17, None), (22.7, 62.3, 30.1, 64.0, 1.65, None),
    (30.3, 54.9, 30.65, 55.9, 1.75, None), (39.1, 55.95, 45.5, 64.85, 6.79, None), (37.8, 63.65, 38.6, 64.45, 2.15, None),
    (42.9, 48.9, 48.7, 54.5, 4.57, None), (42.6, 47.16, 46.4, 47.44, 1.55, None), (47.6, 47.16, 49.0, 47.44, 1.55, None),
    (42.46, 47.3, 42.74, 49.0, 1.55, None), (48.86, 47.3, 49.14, 49.0, 1.55, None),
    (30.27, 54.42, 30.63, 54.78, 5.05, None), (30.5, 62.71, 34.3, 62.89, 1.42, None),
    (30.3, 48.4, 30.7, 48.8, 8.85, None), (30.3, 58.2, 30.7, 58.6, 8.65, None),
  )

  private lazy val northblockInteractables: Vector[String] = Vector(
    "\u81ea\u52d5\u8ca9\u58f2\u6a5f  \u00b7  buy a drink",
  )

  private lazy val northblockPlatformRows: Vector[PlatformRow] = Vector(
    (40.0, 48.6, 43.5, 46.9, 0.516), (0.3, 2.0, 48.9, 54.9, 0.52), (23.2, 30.4, 47.3, 52.0, 0.51),
    (34.0, 39.2, 56.05, 64.75, 0.52),
  )

  test("northblock assembly matches the upstream structural trace") {
    val ctx    = districtCtx()
    val result = NorthBlock.buildNorthBlock(ctx)
    assertChildren(ctx.root.children.toVector, northblockChildren, "northblock")
    assertColliders(ctx.colliders.toVector, northblockColliderRows, "northblock")
    assertPlatforms(ctx.platforms.toVector, northblockPlatformRows, "northblock")
    assertEquals(ctx.cuts.length, 0, "northblock cut count")
    assertEquals(ctx.interactables.toVector.map(_.label), northblockInteractables, "northblock interactables")
    assert(result != null, "northblock returns its planting")
  }
end NorthBlockAndLibraryParitySuite
