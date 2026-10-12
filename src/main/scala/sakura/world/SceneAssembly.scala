package sakura.world

import sakura.{PlanetCurvature, ThreeInterop}
import vendor.three.*

import scala.collection.mutable.ArrayBuffer
import scala.math
import scala.scalajs.js.JSConverters.*

final case class SceneAssembly(
  group: Group[?],
  geometries: List[BufferGeometry[?, ?]],
  materials: List[Material[?]]
)

final private[world] class GeometryBuilder:
  final private case class Vec3(x: Double, y: Double, z: Double):
    def +(other: Vec3): Vec3     = Vec3(x + other.x, y + other.y, z + other.z)
    def -(other: Vec3): Vec3     = Vec3(x - other.x, y - other.y, z - other.z)
    def *(scale: Double): Vec3   = Vec3(x * scale, y * scale, z * scale)
    def dot(other: Vec3): Double = x * other.x + y * other.y + z * other.z
    def cross(other: Vec3): Vec3 =
      Vec3(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x)
    def normalized: Vec3 = this * (1.0 / math.sqrt(dot(this)))

  final private case class Vec2(u: Double, v: Double)

  private val positions = ArrayBuffer.empty[Double]
  private val normals   = ArrayBuffer.empty[Double]
  private val uvs       = ArrayBuffer.empty[Double]
  private val zeroUv    = Vec2(0.0, 0.0)
  private val faceUvs   = Vector(Vec2(0.0, 0.0), Vec2(1.0, 0.0), Vec2(1.0, 1.0), Vec2(0.0, 1.0))

  private val faceCorners = List(
    (0, 3, 2, 1), (4, 5, 6, 7), (0, 4, 7, 3), (1, 2, 6, 5), (0, 1, 5, 4), (3, 7, 6, 2)
  )

  def addBox(cx: Double, cy: Double, cz: Double, sx: Double, sy: Double, sz: Double): Unit =
    addOrientedBox(
      Vec3(cx, cy, cz),
      Vec3(1.0, 0.0, 0.0),
      Vec3(0.0, 1.0, 0.0),
      Vec3(0.0, 0.0, 1.0),
      sx,
      sy,
      sz
    )

  def addCylinder(
    cx: Double,
    cy: Double,
    cz: Double,
    topRadius: Double,
    bottomRadius: Double,
    height: Double,
    segments: Double
  ): Unit =
    val geometry = new CylinderGeometry(topRadius, bottomRadius, height, segments)
    geometry.translate(cx, cy, cz)
    addMerged(geometry)
    geometry.dispose()

  def addCone(cx: Double, cy: Double, cz: Double, radius: Double, height: Double, segments: Double): Unit =
    val geometry = new ConeGeometry(radius, height, segments)
    geometry.translate(cx, cy, cz)
    addMerged(geometry)
    geometry.dispose()

  /** Merges any geometry carrying position, normal and uv, after the caller has baked its
    * transform into the vertices.
    */
  def addMerged(geometry: BufferGeometry[?, ?]): Unit =
    // Three.js primitives are indexed; a linear read of the position attribute would rebuild them
    // as the raw vertex list and mangle every face.
    val source = geometry.getIndex() match
      case null => geometry
      case _    => geometry.toNonIndexed()
    val position = attributeOf(source, "position")
    val normal   = attributeOf(source, "normal")
    val uv       = attributeOf(source, "uv")
    val count    = position.count.toInt
    (0 until count).foreach { index =>
      append(
        Vec3(position.getX(index), position.getY(index), position.getZ(index)),
        Vec3(normal.getX(index), normal.getY(index), normal.getZ(index)),
        Vec2(uv.getX(index), uv.getY(index))
      )
    }

  private def attributeOf(geometry: BufferGeometry[?, ?], name: String): BufferAttribute[?] =
    geometry.getAttribute(name) match
      case attribute: BufferAttribute[?] => attribute
      case _                             => throw new IllegalStateException(s"sakura-crossing: no $name attribute")

  def addBeam(
    x0: Double,
    y0: Double,
    z0: Double,
    x1: Double,
    y1: Double,
    z1: Double,
    thickness: Double
  ): Unit =
    val start     = Vec3(x0, y0, z0)
    val end       = Vec3(x1, y1, z1)
    val delta     = end - start
    val length    = math.sqrt(delta.dot(delta))
    val direction = delta * (1.0 / length)
    val reference = if math.abs(direction.y) < 0.9 then Vec3(0.0, 1.0, 0.0) else Vec3(0.0, 0.0, 1.0)
    val side      = (reference - direction * reference.dot(direction)).normalized
    val depth     = direction.cross(side).normalized
    val center    = (start + end) * 0.5
    addOrientedBox(center, direction, side, depth, length, thickness, thickness)

  def addTetrahedron(
    x: Double,
    baseY: Double,
    z: Double,
    width: Double,
    height: Double,
    depth: Double,
    skewX: Double,
    skewZ: Double
  ): Unit =
    val a = Vec3(x - width / 2.0, baseY, z - depth / 2.0)
    val b = Vec3(x + width / 2.0, baseY, z - depth / 2.0)
    val c = Vec3(x, baseY, z + depth / 2.0)
    val d = Vec3(x + skewX * width, baseY + height, z + skewZ * depth)
    addTriangle(a, b, c)
    addTriangle(a, d, b)
    addTriangle(b, d, c)
    addTriangle(c, d, a)

  def result(): BufferGeometry[?, ?] =
    val geometry = new BufferGeometry()
    geometry.setAttribute("position", attribute(positions, 3))
    geometry.setAttribute("normal", attribute(normals, 3))
    geometry.setAttribute("uv", attribute(uvs, 2))
    geometry

  private def addOrientedBox(
    center: Vec3,
    xAxis: Vec3,
    yAxis: Vec3,
    zAxis: Vec3,
    sx: Double,
    sy: Double,
    sz: Double
  ): Unit =
    val hx = sx / 2.0
    val hy = sy / 2.0
    val hz = sz / 2.0
    val corners = Vector(
      center - xAxis * hx - yAxis * hy - zAxis * hz,
      center + xAxis * hx - yAxis * hy - zAxis * hz,
      center + xAxis * hx + yAxis * hy - zAxis * hz,
      center - xAxis * hx + yAxis * hy - zAxis * hz,
      center - xAxis * hx - yAxis * hy + zAxis * hz,
      center + xAxis * hx - yAxis * hy + zAxis * hz,
      center + xAxis * hx + yAxis * hy + zAxis * hz,
      center - xAxis * hx + yAxis * hy + zAxis * hz
    )
    faceCorners.foreach { case (a, b, c, d) =>
      addTriangle(corners(a), corners(b), corners(c), faceUvs(0), faceUvs(1), faceUvs(2))
      addTriangle(corners(a), corners(c), corners(d), faceUvs(0), faceUvs(2), faceUvs(3))
    }
  end addOrientedBox

  private def addTriangle(a: Vec3, b: Vec3, c: Vec3): Unit =
    addTriangle(a, b, c, zeroUv, zeroUv, zeroUv)

  private def addTriangle(a: Vec3, b: Vec3, c: Vec3, uvA: Vec2, uvB: Vec2, uvC: Vec2): Unit =
    val normal = (b - a).cross(c - a).normalized
    append(a, normal, uvA)
    append(b, normal, uvB)
    append(c, normal, uvC)

  private def append(point: Vec3, normal: Vec3, uv: Vec2): Unit =
    positions ++= Seq(point.x, point.y, point.z)
    normals ++= Seq(normal.x, normal.y, normal.z)
    uvs ++= Seq(uv.u, uv.v)

  private def attribute(values: ArrayBuffer[Double], itemSize: Int): Float32BufferAttribute =
    if itemSize == 2 then new Float32BufferAttribute(values.toJSArray, itemSize.toDouble)
    else
      val result = new Float32BufferAttribute(values.size.toDouble, itemSize.toDouble)
      values.grouped(3).zipWithIndex.foreach { case (point, index) =>
        result.setXYZ(index, point(0), point(1), point(2))
      }
      result
end GeometryBuilder

private[world] object WorldGeometry:
  def bend(geometry: BufferGeometry[?, ?], curvature: PlanetCurvature): Unit =
    val position = geometry.getAttribute("position") match
      case attribute: BufferAttribute[?] => attribute
      case _                             => throw new IllegalStateException("sakura-crossing: geometry has no position attribute")
    (0 until position.count.toInt).foreach { index =>
      val x = position.getX(index)
      val z = position.getZ(index)
      position.setXYZ(index, x, curvature.bend(x, position.getY(index), z), z)
    }
    ThreeInterop.markForUpload(position)
end WorldGeometry
