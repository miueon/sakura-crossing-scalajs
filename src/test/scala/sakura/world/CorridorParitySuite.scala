package sakura.world

import sakura.ThreeInterop
import sakura.core.Util
import vendor.three.*

import scala.annotation.nowarn
import scala.scalajs.js

/** Ordered structural parity against the upstream world corridor.
  *
  * Every expected row was produced by executing the upstream JavaScript
  * (src/world/{street,railway,train,shop,ground,vending,trees,petals}.js) under
  * Node with three 0.185.1; the district tracer was calibrated until it
  * reproduced /tmp/upstream-oracle/world-oracle.json exactly. Rows are
  * (meshCount, instancedCount, vertexCount, multiMatCount, groupSliceCount,
  * castShadowCount, receiveShadowCount, bbox, colorDigest) per top child, in
  * build order; bounding boxes ignore instance matrices, exactly as the oracle
  * tracer does.
  */
class CorridorParitySuite extends munit.FunSuite:

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
          val geometry = ThreeInterop.meshGeometry(mesh)
          val matrix   = ThreeInterop.matrixElements(node.matrixWorld)
          val position = ThreeInterop.attributeDoubles(geometry, "position")
          var index    = 0
          val count    = position.length / 3
          while index < count do
            val x      = position(index * 3)
            val y      = position(index * 3 + 1)
            val z      = position(index * 3 + 2)
            val worldX = matrix(0) * x + matrix(4) * y + matrix(8) * z + matrix(12)
            val worldY = matrix(1) * x + matrix(5) * y + matrix(9) * z + matrix(13)
            val worldZ = matrix(2) * x + matrix(6) * y + matrix(10) * z + matrix(14)
            if worldX < min(0) then min(0) = worldX
            if worldX > max(0) then max(0) = worldX
            if worldY < min(1) then min(1) = worldY
            if worldY > max(1) then max(1) = worldY
            if worldZ < min(2) then min(2) = worldZ
            if worldZ > max(2) then max(2) = worldZ
            index += 1
        }
    }
    val bbox =
      if min(0).isInfinite then Vector.empty
      else Vector(round3(min(0)), round3(min(1)), round3(min(2)), round3(max(0)), round3(max(1)), round3(max(2)))
    Traced(meshCount, instancedCount, vertexCount, multiMatCount, groupSliceCount,
      castShadowCount, receiveShadowCount, bbox, colors.toVector.take(8))
  end traceChild

  private def assertChild(actual: Traced, expected: ChildRow, where: String): Unit =
    val (meshCount, instancedCount, vertexCount, multiMatCount, groupSliceCount,
      castShadowCount, receiveShadowCount, bbox, colorDigest) = expected
    assertEquals(actual.meshCount, meshCount, s"$where meshCount")
    assertEquals(actual.instancedCount, instancedCount, s"$where instancedCount")
    assertEquals(actual.vertexCount, vertexCount, s"$where vertexCount")
    assertEquals(actual.multiMatCount, multiMatCount, s"$where multiMatCount")
    assertEquals(actual.groupSliceCount, groupSliceCount, s"$where groupSliceCount")
    assertEquals(actual.castShadowCount, castShadowCount, s"$where castShadowCount")
    assertEquals(actual.receiveShadowCount, receiveShadowCount, s"$where receiveShadowCount")
    assertEquals(actual.bbox, bbox, s"$where bbox")
    assertEquals(actual.colorDigest, colorDigest, s"$where colorDigest")

  private def addedChildren(root: Group[Object3DEventMap]): Vector[Object3D[?]] =
    root.children.toVector

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

  private def corridor(): WorldCtx =
    val scene = new Scene()
    val root  = new Group()
    root.name = "world"
    ThreeInterop.addChild(scene, root)
    WorldCtx(scene, root)

  private val streetChildren: Vector[ChildRow] = Vector(
    (1, 0, 25921, 0, 0, 0, 1, Vector(-160.0, -0.019, -180.0, 160.0, 1.035, 140.0), Vector(12895414)),
    (1, 0, 216, 0, 0, 0, 1, Vector(-6.55, 0.012, -66.0, 6.15, 1.062, 52.0), Vector(9341596)),
    (1, 0, 4, 0, 0, 0, 1, Vector(-2.2, 0.018, 6.8, 0.4, 0.018, 10.0), Vector(10130854)),
    (1, 0, 4, 0, 0, 0, 1, Vector(0.454, 0.018, 17.3, 2.454, 0.018, 21.7), Vector(10130854)),
    (1, 0, 4, 0, 0, 0, 1, Vector(-2.9, 0.018, -9.7, 0.1, 0.018, -7.3), Vector(10130854)),
    (1, 0, 4, 0, 0, 0, 1, Vector(-1.562, 0.018, 24.5, 0.838, 0.018, 29.5), Vector(10130854)),
    (1, 0, 4, 0, 0, 0, 1, Vector(-5.409, 0.115, 32.2, -3.209, 0.115, 35.8), Vector(10130854)),
    (1, 0, 4, 0, 0, 0, 1, Vector(1.786, 0.341, -21.5, 3.986, 0.341, -18.5), Vector(10130854)),
    (1, 0, 106, 0, 0, 0, 1, Vector(2.72, 0.024, -66.0, 5.86, 1.074, -3.35), Vector(16052982)),
    (1, 0, 106, 0, 0, 0, 1, Vector(-2.86, 0.024, -66.0, 0.28, 1.074, -3.35), Vector(16052982)),
    (1, 0, 84, 0, 0, 0, 1, Vector(-0.68, 0.024, 3.35, 2.86, 0.474, 52.0), Vector(16052982)),
    (1, 0, 84, 0, 0, 0, 1, Vector(-6.26, 0.024, 3.35, -2.72, 0.474, 52.0), Vector(16052982)),
    (1, 0, 4, 0, 0, 0, 0, Vector(0.7, 0.022, 7.075, 2.2, 0.022, 9.925), Vector(16777215)),
    (1, 0, 4, 0, 0, 0, 0, Vector(-2.25, 0.022, -11.025, -0.75, 0.022, -8.175), Vector(16777215)),
    (1, 0, 4, 0, 0, 0, 0, Vector(0.822, 0.022, 15.217, 2.172, 0.022, 17.783), Vector(16777215)),
    (1, 0, 4, 0, 0, 0, 0, Vector(-1.722, 0.171, -18.783, -0.372, 0.171, -16.217), Vector(16777215)),
    (1, 0, 4, 0, 0, 0, 0, Vector(0.122, 0.022, 23.765, 1.422, 0.022, 26.235), Vector(16777215)),
    (1, 0, 4, 0, 0, 0, 0, Vector(0.075, 0.026, 3.99, 2.625, 0.026, 4.41), Vector(16052982)),
    (1, 0, 4, 0, 0, 0, 0, Vector(-2.625, 0.026, -4.41, -0.075, 0.026, -3.99), Vector(16052982)),
    (1, 0, 106, 0, 0, 0, 1, Vector(3.15, 0.135, -66.0, 7.7, 1.185, -3.35), Vector(14473442)),
    (1, 0, 106, 0, 0, 0, 1, Vector(-4.7, 0.135, -66.0, -0.15, 1.185, -3.35), Vector(14473442)),
    (1, 0, 106, 0, 0, 0, 1, Vector(3.15, 0.0, -66.0, 6.15, 1.185, -3.35), Vector(13091536)),
    (1, 0, 106, 0, 0, 0, 1, Vector(-3.15, 0.0, -66.0, -0.15, 1.185, -3.35), Vector(13091536)),
    (1, 0, 106, 0, 0, 0, 1, Vector(4.7, 0.135, -66.0, 7.92, 1.185, -3.35), Vector(15196902)),
    (1, 0, 106, 0, 0, 0, 1, Vector(-4.92, 0.135, -66.0, -1.7, 1.185, -3.35), Vector(15196902)),
    (1, 0, 106, 0, 0, 0, 1, Vector(3.59, 0.149, -66.0, 6.93, 1.199, -3.35), Vector(16777215)),
    (1, 0, 106, 0, 0, 0, 1, Vector(-3.93, 0.149, -66.0, -0.59, 1.199, -3.35), Vector(16777215)),
    (1, 0, 84, 0, 0, 0, 1, Vector(-0.25, 0.135, 3.35, 4.7, 0.585, 52.0), Vector(14473442)),
    (1, 0, 84, 0, 0, 0, 1, Vector(-8.1, 0.135, 3.35, -3.15, 0.585, 52.0), Vector(14473442)),
    (1, 0, 84, 0, 0, 0, 1, Vector(-0.25, 0.0, 3.35, 3.15, 0.585, 52.0), Vector(13091536)),
    (1, 0, 84, 0, 0, 0, 1, Vector(-6.55, 0.0, 3.35, -3.15, 0.585, 52.0), Vector(13091536)),
    (1, 0, 84, 0, 0, 0, 1, Vector(1.3, 0.135, 3.35, 4.92, 0.585, 52.0), Vector(15196902)),
    (1, 0, 84, 0, 0, 0, 1, Vector(-8.32, 0.135, 3.35, -4.7, 0.585, 52.0), Vector(15196902)),
    (1, 0, 84, 0, 0, 0, 1, Vector(0.19, 0.149, 3.35, 3.93, 0.599, 52.0), Vector(16777215)),
    (1, 0, 84, 0, 0, 0, 1, Vector(-7.33, 0.149, 3.35, -3.59, 0.599, 52.0), Vector(16777215)),
    (1, 0, 106, 0, 0, 0, 1, Vector(2.85, 0.004, -66.0, 6.13, 1.054, -3.35), Vector(12433604)),
    (1, 0, 106, 0, 0, 0, 1, Vector(-3.13, 0.004, -66.0, 0.15, 1.054, -3.35), Vector(12433604)),
    (1, 0, 84, 0, 0, 0, 1, Vector(-0.55, 0.004, 3.35, 3.13, 0.454, 52.0), Vector(12433604)),
    (1, 0, 84, 0, 0, 0, 1, Vector(-6.53, 0.004, 3.35, -2.85, 0.454, 52.0), Vector(12433604)),
    (1, 12, 24, 0, 6, 0, 1, Vector(-0.13, -0.025, -0.31, 0.13, 0.025, 0.31), Vector(7170170)),
    (1, 7, 76, 0, 3, 0, 1, Vector(-0.32, -0.02, -0.32, 0.32, 0.02, 0.32), Vector(8883094)),
    (3, 0, 80, 1, 9, 2, 1, Vector(-9.932, 0.135, 16.1, -4.482, 2.255, 19.15),
      Vector(14276061, 8883094, 16447215, 16447215, 16447215, 16447215, 16777215, 14606054)),
    (1, 0, 52, 0, 3, 1, 0, Vector(3.899, 0.135, 21.95, 3.999, 2.535, 22.05), Vector(12106950)),
    (1, 0, 124, 1, 3, 1, 0, Vector(3.849, 1.985, 21.707, 4.048, 2.585, 22.293), Vector(16447215, 16777215, 14606054)),
  )

  private val streetColliders: Vector[ColliderRow] =
    Vector(
    )
  private val streetPlatforms: Vector[PlatformRow] = Vector(
    (-9.932, -5.732, 16.1, 19.1, 0.14),
  )
  private val streetInteractables = Vector()

  private val railwayChildren: Vector[ChildRow] = Vector(
    (232, 1605, 47964, 5, 838, 67, 47, Vector(-502.655, -0.08, -5.62, 502.655, 8.001, 5.16),
      Vector(8222342, 7169398, 7038066, 12762308, 7038066, 12762308, 14341599, 14341599)),
  )

  private val railwayColliders: Vector[ColliderRow] = Vector(
    (-96, 3.13, -5.2, 3.37, 1.2, None), (5.2, 3.13, 84.1, 3.37, 1.2, None), (85.9, 3.13, 108, 3.37, 1.2, None),
    (-96, -3.37, -85.4, -3.13, 1.2, None), (-83.6, -3.37, -5.2, -3.13, 1.2, None), (5.2, -3.37, 13.5, -3.13, 1.2, None),
    (39.5, -3.37, 108, -3.13, 1.2, None), (-3.97, 2.6, -3.17, 3.3, 2.4, None), (3.17, -3.3, 3.97, -2.6, 2.4, None),
    (3.17, 2.6, 3.97, 3.3, 2.4, None), (-3.97, -3.3, -3.17, -2.6, 2.4, None), (-5.95, -4.5, -4.15, -3.7, 1.4, None),
    (15.4, -5.72, 38.1, -1.82, 0.98, None), (-80, 4.6, -30, 5, 2.2, None), (-80.53, 4.44, -79.91, 5.16, 2.62, None),
    (-30.09, 4.44, -29.47, 5.16, 2.62, None), (46, 4.6, 80, 5, 2.2, None), (45.47, 4.44, 46.09, 5.16, 2.62, None),
    (79.91, 4.44, 80.53, 5.16, 2.62, None), (-80, -5, -30, -4.6, 2.2, None), (-80.53, -5.16, -79.91, -4.44, 2.62, None),
    (-30.09, -5.16, -29.47, -4.44, 2.62, None), (44, -5, 80, -4.6, 2.2, None), (43.47, -5.16, 44.09, -4.44, 2.62, None),
    (79.91, -5.16, 80.53, -4.44, 2.62, None),
  )
  private val railwayPlatforms: Vector[PlatformRow] = Vector(
    (15.5, 38, -5.62, -1.92, 0.98), (15.11, 15.53, -5.52, -3.12, 0.98), (14.75, 15.17, -5.52, -3.12, 0.817),
    (14.39, 14.81, -5.52, -3.12, 0.653), (14.03, 14.45, -5.52, -3.12, 0.49), (13.67, 14.09, -5.52, -3.12, 0.327),
    (13.31, 13.73, -5.52, -3.12, 0.163),
  )
  private val railwayInteractables = Vector("踏切スイッチ  ·  call a train")

  private val trainChildren: Vector[ChildRow] = Vector(
    (412, 0, 7928, 0, 156, 59, 21, Vector(-29.81, 0.3, -1.483, 29.81, 4.893, 1.483),
      Vector(3818072, 5331570, 10463419, 12173516, 15788760, 14674678, 3818072, 5331570)),
  )

  private val shopChildren: Vector[ChildRow] = Vector(
    (271, 48, 8490, 4, 1356, 146, 139, Vector(2.99, -0.005, 4.41, 12.05, 6.4, 12.755),
      Vector(15919059, 16447215, 5857658, 5857658, 15330800, 8883094, 8883094, 8883094)),
  )

  private val shopColliders: Vector[ColliderRow] = Vector(
    (4.77, 4.55, 12.02, 12.6, 5.9, None), (5.02, 11.2, 6.17, 12.3, 1.035, None), (3.82, 9.3, 4.47, 11.1, 0.535, None),
    (3.97, 4.95, 4.84, 6.15, 2.135, None), (3.97, 6.2, 4.84, 7.4, 2.135, None),
  )
  private val shopInteractables = Vector("シャッター  ·  open the shutter", "自動販売機  ·  buy a drink", "自動販売機  ·  buy a drink")

  private def renderDouble(value: Double): String =
    if value == value.toLong.toDouble then s"${value.toLong}.0" else value.toString

  private def render(traced: Traced): String =
    val bbox   = traced.bbox.map(renderDouble).mkString("Vector(", ", ", ")")
    val colors = traced.colorDigest.mkString("Vector(", ", ", ")")
    s"Traced(${traced.meshCount}, ${traced.instancedCount}, ${traced.vertexCount}, ${traced.multiMatCount}, " +
      s"${traced.groupSliceCount}, ${traced.castShadowCount}, ${traced.receiveShadowCount}, $bbox, $colors)"

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

  private def instanceSum(instanced: InstancedMesh[?, ?, ?]): (Int, Double) =
    val count = instanced.count.toInt
    // the upstream digest sums the whole instanceMatrix allocation, including any
    // unused tail beyond the live count, so the comparison reads the same span
    val attribute = instanced.instanceMatrix
    val total     = attribute.count.toInt * attribute.itemSize.toInt
    val values    = ThreeInterop.readAttributeDoubles(attribute, total)
    (count, math.round(values.sum * 1000.0) / 1000.0)

  private def instancedDigest(node: Object3D[?]): Vector[(Int, Double)] =
    val result = scala.collection.mutable.ArrayBuffer.empty[(Int, Double)]
    node.traverse { (child: Object3D[Object3DEventMap]) =>
      ThreeInterop.asInstancedObject(child).foreach(instanced => result += instanceSum(instanced))
    }
    result.toVector

  private def instancedDigestOfRoot(root: Object3D[?]): Vector[(Int, Double)] = instancedDigest(root)

  test("buildStreet matches the ordered upstream structural trace") {
    val ctx = corridor()
    Street.buildStreet(ctx)
    val children = addedChildren(ctx.root)
    assertEquals(children.length, 44)
    children.zip(streetChildren).foreach { (child, row) =>
      assertChild(traceChild(child), row, s"street child ${children.indexOf(child)}")
    }
    assertColliders(ctx.colliders.toVector, streetColliders, "street")
    assertPlatforms(ctx.platforms.toVector, streetPlatforms, "street")
    assertEquals(ctx.interactables.map(_.label).toVector, streetInteractables)
  }

  test("buildRailway matches the ordered upstream structural trace") {
    val ctx = corridor()
    Street.buildStreet(ctx)
    val before   = ctx.root.children.length
    val crossing = Railway.buildRailway(ctx)
    assertEquals(crossing.group.name, "crossing")
    val children = ctx.root.children.slice(before, ctx.root.children.length).toVector
    assertEquals(children.length, 1)
    children.zip(railwayChildren).foreach { (child, row) => assertChild(traceChild(child), row, "railway child 0") }
    assertColliders(ctx.colliders.drop(streetColliders.length).toVector, railwayColliders, "railway")
    assertPlatforms(ctx.platforms.drop(streetPlatforms.length).toVector, railwayPlatforms, "railway")
    assertEquals(ctx.interactables.map(_.label).toVector, railwayInteractables)
  }

  test("buildTrain matches the ordered upstream structural trace") {
    val ctx = corridor()
    Street.buildStreet(ctx)
    Railway.buildRailway(ctx)
    val before = ctx.root.children.length
    val train  = Train.buildTrain(ctx)
    assertEquals(train.group.name, "train")
    val children = ctx.root.children.slice(before, ctx.root.children.length).toVector
    assertEquals(children.length, 1)
    children.zip(trainChildren).foreach { (child, row) => assertChild(traceChild(child), row, "train child 0") }
    assertColliders(ctx.colliders.toVector, streetColliders ++ railwayColliders, "train prefix")
  }

  test("buildShop matches the ordered upstream structural trace") {
    val ctx = corridor()
    Street.buildStreet(ctx)
    Railway.buildRailway(ctx)
    Train.buildTrain(ctx)
    val before = ctx.root.children.length
    val shop   = Shop.buildShop(ctx)
    assertEquals(shop.group.name, "shop")
    val children = ctx.root.children.slice(before, ctx.root.children.length).toVector
    assertEquals(children.length, 1)
    children.zip(shopChildren).foreach { (child, row) => assertChild(traceChild(child), row, "shop child 0") }
    assertColliders(ctx.colliders.drop(streetColliders.length + railwayColliders.length).toVector, shopColliders, "shop")
    assertEquals(ctx.interactables.drop(1).map(_.label).toVector, shopInteractables)
  }

  test("ground helpers match upstream outputs") {
    val padCtx = corridor()
    Ground.pad(padCtx, Ground.PadOptions(x = 10.0, z = -5.0, w = 6.0, d = 4.0, y = 0.5, name = "forecourt"))
    assertEquals(render(traceChild(padCtx.root.children(0))),
      "Traced(1, 0, 24, 0, 6, 0, 1, Vector(7.0, 0.5, -7.0, 13.0, 0.57, -3.0), Vector(14276061))")
    assertEquals(padCtx.platforms.length, 1)
    assertEqualsDouble(round3(padCtx.platforms(0).x0), 7.0, 0.0)
    assertEqualsDouble(round3(padCtx.platforms(0).x1), 13.0, 0.0)
    assertEqualsDouble(round3(padCtx.platforms(0).z0), -7.0, 0.0)
    assertEqualsDouble(round3(padCtx.platforms(0).z1), -3.0, 0.0)
    assertEqualsDouble(round3(padCtx.platforms(0).top), 0.57, 0.0)

    val laneCtx = corridor()
    Ground.lane(laneCtx, Ground.LaneOptions(axis = "x", from = 2.0, to = 14.0, at = -8.0, w = 3.0, name = "laneX"))
    assertEquals(render(traceChild(laneCtx.root.children(0))),
      "Traced(3, 0, 72, 0, 18, 0, 3, Vector(2.0, -0.01, -9.76, 14.0, 0.15, -6.24), Vector(9341596, 13091536, 13091536))")
    assertEqualsDouble(round3(laneCtx.platforms(0).x0), 2.0, 0.0)
    assertEqualsDouble(round3(laneCtx.platforms(0).top), round3(0.11), 0.0)

    val laneZCtx = corridor()
    Ground.lane(laneZCtx, Ground.LaneOptions(axis = "z", from = 20.0, to = 34.0, at = 6.0, w = 2.6, name = "laneZ"))
    assertEquals(render(traceChild(laneZCtx.root.children(0))),
      "Traced(3, 0, 66, 0, 0, 0, 3, Vector(4.44, 0.05, 20.0, 7.56, 0.247, 34.0), Vector(9341596, 13091536, 13091536))")
    assertEquals(laneZCtx.platforms.length, 0)

    val lineCtx = corridor()
    Ground.laneLine(lineCtx, Ground.LineOptions(axis = "x", from = 0.0, to = 12.0, at = -7.9, dash = 1.2))
    Ground.laneLine(lineCtx, Ground.LineOptions(axis = "x", from = 0.0, to = 6.0, at = -7.7))
    assertEquals(lineCtx.root.children.length, 2)
    assertEquals(render(traceChild(lineCtx.root.children(0))),
      "Traced(1, 0, 120, 0, 0, 0, 0, Vector(0.0, 0.08, -7.95, 10.8, 0.1, -7.85), Vector(16052982))")
    assertEquals(render(traceChild(lineCtx.root.children(1))),
      "Traced(1, 0, 24, 0, 0, 0, 0, Vector(0.0, 0.08, -7.75, 6.0, 0.1, -7.65), Vector(16052982))")

    val stepCtx = corridor()
    val flight = Ground.steps(stepCtx,
      Ground.StepsOptions(x = -4.0, z = 30.0, n = 6, rise = 0.18, run = 0.4, w = 2.2, y = 0.3, dir = -1))
    assertEquals(render(traceChild(stepCtx.root.children(0))),
      "Traced(2, 0, 288, 0, 0, 1, 1, Vector(-5.12, 0.3, 27.6, -2.88, 1.382, 30.0), Vector(13025483, 10722735))")
    assertEquals(stepCtx.platforms.length, 6)
    assertEqualsDouble(round3(flight.top), 1.38, 0.0)
    assertEqualsDouble(round3(flight.end), 27.6, 0.0)

    val wallCtx = corridor()
    Ground.wallRun(wallCtx, Ground.WallOptions(axis = "x", from = -6.0, to = 18.0, at = 9.5, h = 1.9, t = 0.24))
    assertEquals(render(traceChild(wallCtx.root.children(0))),
      "Traced(2, 0, 288, 0, 0, 1, 2, Vector(-6.01, -0.05, 9.32, 18.01, 1.95, 9.68), Vector(12762568, 14276061))")
    assertEquals(wallCtx.colliders.length, 1)
    assertEqualsDouble(round3(wallCtx.colliders(0).x0), -6, 0.0)
    assertEqualsDouble(round3(wallCtx.colliders(0).top), 1.9, 0.0)

    val railCtx = corridor()
    Ground.railing(railCtx, Ground.RailingOptions(axis = "x", from = -3.0, to = 9.0, at = -12.0))
    assertEquals(render(traceChild(railCtx.root.children(0))),
      "Traced(1, 0, 414, 0, 0, 1, 0, Vector(-3.041, 0.0, -12.038, 9.041, 1.071, -11.958), Vector(16052982))")
    assertEqualsDouble(round3(railCtx.colliders(0).z0), -12.09, 0.0)

    val dappleCtx = corridor()
    Ground.dapple(dappleCtx,
      Ground.DappleOptions(x = 5.0, z = -20.0, y = 0.2, n = 7, r = 1.4, spread = 2.2, rng = Util.rngKit(777)))
    assertEquals(render(traceChild(dappleCtx.root.children(0))),
      "Traced(1, 7, 12, 0, 0, 0, 0, Vector(-1.0, 0.0, -0.951, 1.0, 0.0, 0.951), Vector(9076654))")
  }

  test("vending machines match upstream outputs") {
    val v0 = Vending.makeVendingMachine(0, 1).group
    assertEquals(render(traceChild(v0)),
      "Traced(64, 24, 1868, 1, 345, 2, 1, Vector(-0.572, -0.083, -0.36, 0.572, 2.04, 1.06), Vector(8883094, 16315888, 16777215, 14697791, 2961728, 16315888, 16315888, 16315888))")
    val v2 = Vending.makeVendingMachine(2, 2).group
    assertEquals(render(traceChild(v2)),
      "Traced(64, 24, 1868, 1, 345, 2, 1, Vector(-0.572, -0.083, -0.36, 0.572, 2.04, 1.06), Vector(8883094, 3054232, 16777215, 16643820, 2568506, 3054232, 3054232, 3054232))")

    val instanceDigests = instancedDigest(v0)
    assertEquals(instanceDigests.length, 2)
    instanceDigests.zip(Vector((12, 90.433), (12, 91.79))).foreach { (digest, expected) =>
      assertEquals(digest._1, expected._1, "instance count")
      assertEqualsDouble(digest._2, expected._2, 1e-6, "instance matrix sum")
    }

    val ctx = corridor()
    Vending.addVending(ctx, Vending.VendingOptions(x = 3.0, z = -6.0, y = 0.4, ry = 0.5, variant = 1, seed = 4))
    assertEquals(render(traceChild(ctx.root.children(0))),
      "Traced(64, 24, 1868, 1, 345, 62, 64, Vector(2.336, 0.317, -6.584, 4.0, 2.44, -4.801), Vector(8883094, 14368824, 16777215, 16643820, 2763832, 14368824, 14368824, 14368824))")
    assertEquals(ctx.colliders.length, 1)
    assertEqualsDouble(round3(ctx.colliders(0).x0), 2.44, 0.0)
    assertEquals(ctx.interactables.map(_.label).toVector, Vector("自動販売機  ·  buy a drink"))
  }

  test("trees match upstream outputs") {
    val sakuraCtx = corridor()
    Trees.buildSakura(sakuraCtx,
      Vector(
        SakuraSpot(x = -7.1, z = 5.8, scale = 1.22, seed = 101),
        SakuraSpot(x = 6.3, z = 2.5, scale = 1.06, seed = 102, lean = 0.14, leanDir = Some(0.9), tone = Some(1)),
        SakuraSpot(x = 0.0, z = -3.0, y = 0.5, scale = 0.9, seed = 103, collide = false),
      ))
    assertEquals(sakuraCtx.root.children.length, 4)
    sakuraCtx.root.children.toVector.zip(Vector(
      "Traced(1, 0, 902, 0, 0, 1, 1, Vector(-8.941, -0.015, -4.903, 8.996, 6.601, 7.929), Vector(10125442))",
      "Traced(1, 23, 240, 0, 0, 1, 0, Vector(-1.0, -1.0, -1.0, 1.0, 1.0, 1.0), Vector(16773364))",
      "Traced(1, 55, 240, 0, 0, 1, 0, Vector(-1.0, -1.0, -1.0, 1.0, 1.0, 1.0), Vector(16500440))",
      "Traced(1, 24, 240, 0, 0, 1, 0, Vector(-1.0, -1.0, -1.0, 1.0, 1.0, 1.0), Vector(15770560))"
    )).foreach { (child, expected) =>
      assertEquals(render(traceChild(child)), expected.toString)
    }
    val digest = instancedDigest(sakuraCtx.root)
    assertEquals(digest.length, 3)
    digest.zip(Vector((23, 138.186), (55, 577.839), (24, 97.055))).foreach { (actual, expected) =>
      assertEquals(actual._1, expected._1)
      assertEqualsDouble(actual._2, expected._2, 1e-6)
    }
    assertEquals(sakuraCtx.colliders.length, 2)
    sakuraCtx.colliders.toVector.zip(
      Vector((-7.381, 5.519, -6.819, 6.081, 3.258), (6.056, 2.256, 6.544, 2.744, 2.786))
    ).foreach { (actual, expected) =>
      assertEqualsDouble(round3(actual.x0), expected._1, 0.0)
      assertEqualsDouble(round3(actual.z0), expected._2, 0.0)
      assertEqualsDouble(round3(actual.x1), expected._3, 0.0)
      assertEqualsDouble(round3(actual.z1), expected._4, 0.0)
      assertEqualsDouble(round3(actual.top), expected._5, 0.0)
    }

    val groveCtx = corridor()
    Trees.buildGrove(groveCtx,
      Vector(
        GroveSpot(x = -14.0, z = 24.0, scale = 1.35, seed = 201),
        GroveSpot(x = 20.0, z = -14.0, scale = 1.0, seed = 202, spread = 1.4, lean = 0.1),
        GroveSpot(x = 30.0, z = 30.0, scale = 1.1, seed = 203, willow = true, tone = Some(2)),
      ))
    assertEquals(groveCtx.root.children.length, 5)
    groveCtx.root.children.toVector.zip(Vector(
      "Traced(1, 0, 718, 0, 0, 1, 1, Vector(-15.442, -0.014, -14.575, 31.531, 6.897, 31.696), Vector(7757666))",
      "Traced(1, 22, 240, 0, 0, 1, 0, Vector(-1.0, -1.0, -1.0, 1.0, 1.0, 1.0), Vector(9222276))",
      "Traced(1, 81, 240, 0, 0, 1, 0, Vector(-1.0, -1.0, -1.0, 1.0, 1.0, 1.0), Vector(6263920))",
      "Traced(1, 32, 240, 0, 0, 1, 0, Vector(-1.0, -1.0, -1.0, 1.0, 1.0, 1.0), Vector(4156242))",
      "Traced(1, 65, 240, 0, 0, 1, 0, Vector(-1.0, -1.0, -1.0, 1.0, 1.0, 1.0), Vector(11060361))"
    )).foreach { (child, expected) =>
      assertEquals(render(traceChild(child)), expected.toString)
    }

    val cedarCtx = corridor()
    Trees.buildCedar(cedarCtx,
      Vector(
        CedarSpot(x = -40.0, z = -30.0, scale = 1.0, seed = 301),
        CedarSpot(x = -46.0, z = -26.0, scale = 0.85, seed = 302, lean = 0.04),
      ))
    assertEquals(cedarCtx.root.children.length, 4)
    cedarCtx.root.children.toVector.zip(Vector(
      "Traced(1, 0, 160, 0, 0, 1, 1, Vector(-46.158, -0.003, -30.272, -39.764, 10.571, -25.821), Vector(8282448))",
      "Traced(1, 9, 31, 0, 2, 1, 0, Vector(-0.975, 0.0, -0.901, 0.975, 1.0, 1.0), Vector(6590571))",
      "Traced(1, 7, 31, 0, 2, 1, 0, Vector(-0.975, 0.0, -0.901, 0.975, 1.0, 1.0), Vector(4156242))",
      "Traced(1, 3, 31, 0, 2, 1, 0, Vector(-0.975, 0.0, -0.901, 0.975, 1.0, 1.0), Vector(3102016))"
    )).foreach { (child, expected) =>
      assertEquals(render(traceChild(child)), expected.toString)
    }
    assertEquals(cedarCtx.colliders.length, 2)

    val bambooCtx = corridor()
    Trees.buildBamboo(bambooCtx,
      Vector(
        BambooClump(x = -19.2, z = 30.0, seed = 401, n = 8, spread = 1.1),
        BambooClump(x = -22.0, z = 33.0, seed = 402, n = 11, scale = 0.9, y = 0.2),
      ))
    assertEquals(bambooCtx.root.children.length, 5)
    bambooCtx.root.children.toVector.zip(Vector(
      "Traced(1, 10, 34, 0, 3, 1, 0, Vector(-1.103, -0.5, -0.938, 1.103, 0.5, 1.16), Vector(9744491))",
      "Traced(1, 9, 34, 0, 3, 1, 0, Vector(-1.103, -0.5, -0.938, 1.103, 0.5, 1.16), Vector(7310416))",
      "Traced(1, 76, 34, 0, 3, 0, 0, Vector(-1.189, -0.5, -1.011, 1.189, 0.5, 1.25), Vector(12109962))",
      "Traced(1, 38, 60, 0, 0, 1, 0, Vector(-0.851, -0.851, -0.851, 0.851, 0.851, 0.851), Vector(10928248))",
      "Traced(1, 38, 60, 0, 0, 1, 0, Vector(-0.851, -0.851, -0.851, 0.851, 0.851, 0.851), Vector(7310416))"
    )).foreach { (child, expected) =>
      assertEquals(render(traceChild(child)), expected.toString)
    }

    val shrubCtx = corridor()
    Trees.buildShrubs(shrubCtx,
      Vector(
        ShrubSpot(x = 8.0, z = -4.0, count = 3, r = 0.55, spread = 1.0, seed = 501),
        ShrubSpot(x = -6.0, z = 10.0, count = 4, r = 0.4, spread = 0.8, seed = 502, y = 0.1),
      ))
    assertEquals(shrubCtx.root.children.length, 3)
    shrubCtx.root.children.toVector.zip(Vector(
      "Traced(1, 3, 240, 0, 0, 1, 1, Vector(-1.0, -1.0, -1.0, 1.0, 1.0, 1.0), Vector(5940600))",
      "Traced(1, 2, 240, 0, 0, 1, 1, Vector(-1.0, -1.0, -1.0, 1.0, 1.0, 1.0), Vector(4161376))",
      "Traced(1, 2, 240, 0, 0, 1, 1, Vector(-1.0, -1.0, -1.0, 1.0, 1.0, 1.0), Vector(8699287))"
    )).foreach { (child, expected) =>
      assertEquals(render(traceChild(child)), expected.toString)
    }
  }

  test("petals match upstream outputs and gust response") {
    val ctx = corridor()
    val api = Petals.buildPetals(ctx)
    assertEquals(ctx.root.children.length, 6)
    ctx.root.children.toVector.zip(Vector(
      "Traced(1, 539, 4, 0, 0, 0, 0, Vector(-0.093, -0.068, 0.0, 0.093, 0.068, 0.0), Vector(16570852))",
      "Traced(1, 274, 4, 0, 0, 0, 0, Vector(-0.093, -0.068, 0.0, 0.093, 0.068, 0.0), Vector(16773364))",
      "Traced(1, 167, 4, 0, 0, 0, 0, Vector(-0.093, -0.068, 0.0, 0.093, 0.068, 0.0), Vector(16170191))",
      "Traced(1, 214, 4, 0, 0, 0, 0, Vector(-0.085, 0.0, -0.062, 0.085, 0.0, 0.063), Vector(16570852))",
      "Traced(1, 208, 4, 0, 0, 0, 0, Vector(-0.085, 0.0, -0.062, 0.085, 0.0, 0.063), Vector(16773364))",
      "Traced(1, 198, 4, 0, 0, 0, 0, Vector(-0.085, 0.0, -0.062, 0.085, 0.0, 0.063), Vector(16170191))"
    )).foreach { (child, expected) =>
      assertEquals(render(traceChild(child)), expected.toString)
    }
    def digest(): Vector[(Int, Double)] = instancedDigestOfRoot(ctx.root)
    digest().zip(
      Vector((539, 4920.624), (274, 2217.069), (167, 1425.052), (214, 874.94), (208, 1096.484), (198, 931.75))
    ).foreach { (actual, expected) =>
      assertEquals(actual._1, expected._1, "settled instance count")
      assertEqualsDouble(actual._2, expected._2, 1e-6, "settled matrix sum")
    }
    api.update(0.1, 0.5, 1.0)
    digest().zip(
      Vector((539, 4930.267), (274, 2180.774), (167, 1403.43), (214, 874.94), (208, 1096.484), (198, 931.75))
    ).foreach { (actual, expected) =>
      assertEquals(actual._1, expected._1, "gusted instance count")
      assertEqualsDouble(actual._2, expected._2, 1e-6, "gusted matrix sum")
    }

    val patchCtx = corridor()
    Petals.buildFallenPatches(patchCtx,
      Vector(
        PetalPatch(x = 40.0, z = 20.0, y = 0.6, n = 40, w = 3.0, d = 2.2),
        PetalPatch(x = -30.0, z = -40.0, n = 25, w = 2.0, d = 1.5),
      ))
    assertEquals(patchCtx.root.children.length, 3)
    patchCtx.root.children.toVector.zip(Vector(
      "Traced(1, 24, 4, 0, 0, 0, 0, Vector(-0.085, 0.0, -0.062, 0.085, 0.0, 0.063), Vector(16570852))",
      "Traced(1, 26, 4, 0, 0, 0, 0, Vector(-0.085, 0.0, -0.062, 0.085, 0.0, 0.063), Vector(16773364))",
      "Traced(1, 15, 4, 0, 0, 0, 0, Vector(-0.085, 0.0, -0.062, 0.085, 0.0, 0.063), Vector(16170191))"
    )).foreach { (child, expected) =>
      assertEquals(render(traceChild(child)), expected.toString)
    }
  }

end CorridorParitySuite

/** The minimal `document.createElement("canvas")` shim the texture library needs under
  * NodeJSEnv: a no-op 2D context so every canvas generator runs and `CanvasTexture` sees real
  * width/height fields. Same shape as the core suite's stub, scoped to this suite.
  */
private[world] object WorldCanvasStub:

  def install(): Unit =
    js.Dynamic.global.globalThis.updateDynamic("document")(
      js.Dynamic.literal(
        createElement = (
          (tag: String) =>
            if tag == "canvas" then new StubCanvas
            else throw new IllegalStateException(s"sakura-crossing: unexpected element: $tag")
        ): js.Function1[String, StubCanvas]
      )
    )
end WorldCanvasStub

final private[world] class StubTextMetrics(val width: Double) extends js.Object

final private[world] class StubGradient extends js.Object:
  def addColorStop(offset: Double, color: String): Unit = ()

@nowarn("msg=unused explicit parameter")
final private[world] class StubContext2d extends js.Object:
  var fillStyle: String                                                      = ""
  var strokeStyle: String                                                    = ""
  var lineWidth: Double                                                      = 1.0
  var font: String                                                           = ""
  var textAlign: String                                                      = ""
  var globalAlpha: Double                                                    = 1.0
  def fillRect(x: Double, y: Double, w: Double, h: Double): Unit             = ()
  def strokeRect(x: Double, y: Double, w: Double, h: Double): Unit           = ()
  def clearRect(x: Double, y: Double, w: Double, h: Double): Unit            = ()
  def beginPath(): Unit                                                      = ()
  def closePath(): Unit                                                      = ()
  def moveTo(x: Double, y: Double): Unit                                     = ()
  def lineTo(x: Double, y: Double): Unit                                     = ()
  def arc(x: Double, y: Double, r: Double, start: Double, end: Double): Unit = ()
  def fill(): Unit                                                           = ()
  def stroke(): Unit                                                         = ()
  def save(): Unit                                                           = ()
  def restore(): Unit                                                        = ()
  def translate(x: Double, y: Double): Unit                                  = ()
  def rotate(angle: Double): Unit                                            = ()
  def scale(x: Double, y: Double): Unit                                      = ()
  def fillText(text: String, x: Double, y: Double): Unit                     = ()
  def strokeText(text: String, x: Double, y: Double): Unit                   = ()
  def measureText(text: String): StubTextMetrics                             = new StubTextMetrics(0.0)
  def createLinearGradient(x0: Double, y0: Double, x1: Double, y1: Double): StubGradient =
    new StubGradient
  def createRadialGradient(x0: Double, y0: Double, r0: Double, x1: Double, y1: Double, r1: Double): StubGradient =
    new StubGradient
  def quadraticCurveTo(cx: Double, cy: Double, x: Double, y: Double): Unit                                 = ()
  def bezierCurveTo(cx1: Double, cy1: Double, cx2: Double, cy2: Double, x: Double, y: Double): Unit        = ()
  def ellipse(x: Double, y: Double, rx: Double, ry: Double, rot: Double, start: Double, end: Double): Unit = ()
  def setLineDash(dash: js.Array[Double]): Unit                                                            = ()
  def roundRect(x: Double, y: Double, w: Double, h: Double): Unit                                          = ()
  def setTransform(a: Double, b: Double, c: Double, d: Double, e: Double, f: Double): Unit                 = ()
  def clip(): Unit                                                                                         = ()
  def arcTo(x1: Double, y1: Double, x2: Double, y2: Double, r: Double): Unit                               = ()
end StubContext2d

@nowarn("msg=unused explicit parameter")
final private[world] class StubCanvas extends js.Object:
  var width: Double                           = 0.0
  var height: Double                          = 0.0
  def getContext(kind: String): StubContext2d = new StubContext2d
end StubCanvas
