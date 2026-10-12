package sakura.core

import sakura.ThreeInterop
import vendor.three.*

import scala.collection.mutable
import scala.math
import scala.scalajs.js.JSConverters.*

/** Geometry and randomness helpers ported 1:1 from upstream `src/core/util.js`. */
object Util:

  val TAU: Double = math.Pi * 2.0
  val DEG: Double = math.Pi / 180.0

  def clamp(v: Double, a: Double, b: Double): Double =
    if v < a then a else if v > b then b else v

  def lerp(a: Double, b: Double, t: Double): Double = a + (b - a) * t

  def invLerp(a: Double, b: Double, v: Double): Double = (v - a) / (b - a)

  /** Hermite smoothstep that tolerates a > b (descending ranges); a zero range collapses
    * through the upstream's 1e-6 denominator guard rather than a NaN.
    */
  def sstep(a: Double, b: Double, v: Double): Double =
    val span = b - a
    val t    = clamp((v - a) / (if span == 0.0 then 1e-6 else span), 0.0, 1.0)
    t * t * (3.0 - 2.0 * t)

  /** Deterministic PRNG so the street looks identical on every load.
    * Bit-exact port of mulberry32: all arithmetic wraps at int32 exactly as
    * JavaScript's `+`, `^`, `|`, `>>>`, and `Math.imul` do.
    */
  def mulberry32(seed: Int): () => Double =
    var a = seed
    () =>
      a = a + 0x6d2b79f5
      var t = a
      t = (t ^ (t >>> 15)) * (t | 1)
      t ^= t + ((t ^ (t >>> 7)) * (t | 61))
      ((t ^ (t >>> 14)).toLong & 0xffffffffL) / 4294967296.0

  /** Small helper bundle around a seeded PRNG; every helper draws from the one stream. */
  final class RngKit private[Util] (val next: () => Double):
    def range(a: Double, b: Double): Double = a + (b - a) * next()

    def int(a: Int, b: Int): Int = math.floor(a + (b - a + 1) * next()).toInt

    def pick[A](values: Vector[A]): A =
      values(math.floor(next() * values.size).toInt % values.size)

    def chance(p: Double): Boolean = next() < p

    def sign: Int = if next() < 0.5 then -1 else 1

  def rngKit(seed: Int): RngKit = RngKit(mulberry32(seed))

  /** Compose a matrix from loose position/euler/scale args. */
  def trs(
    px: Double = 0.0,
    py: Double = 0.0,
    pz: Double = 0.0,
    rx: Double = 0.0,
    ry: Double = 0.0,
    rz: Double = 0.0,
    sx: Double = 1.0,
    sy: Double = 1.0,
    sz: Double = 1.0
  ): Matrix4 =
    val position   = new Vector3(px, py, pz)
    val quaternion = new Quaternion().setFromEuler(new Euler(rx, ry, rz))
    new Matrix4().compose(position, quaternion, new Vector3(sx, sy, sz))

  /** A box mesh whose local origin sits at the centre of its base. */
  def boxOnGround(w: Double, h: Double, d: Double, mat: Material[?]): Mesh[?, ?, ?] =
    val geometry = new BoxGeometry(w, h, d)
    geometry.translate(0.0, h / 2.0, 0.0)
    new Mesh(geometry, mat)

  def box(
    w: Double,
    h: Double,
    d: Double,
    mat: Material[?],
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0
  ): Mesh[?, ?, ?] =
    val mesh = new Mesh(new BoxGeometry(w, h, d), mat)
    mesh.position.set(x, y, z)
    mesh

  def cyl(
    rt: Double,
    rb: Double,
    h: Double,
    seg: Int,
    mat: Material[?],
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0
  ): Mesh[?, ?, ?] =
    val mesh = new Mesh(new CylinderGeometry(rt, rb, h, seg.toDouble), mat)
    mesh.position.set(x, y, z)
    mesh

  def plane(
    w: Double,
    h: Double,
    mat: Material[?],
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0
  ): Mesh[?, ?, ?] =
    val mesh = new Mesh(new PlaneGeometry(w, h), mat)
    mesh.position.set(x, y, z)
    mesh

  /** Recursively enable shadow casting/receiving on a subtree. Transparent meshes and meshes
    * flagged `noShadow` are skipped: glass, highlight quads and netting are there to be seen
    * through, and letting them cast would drop a hard shadow over whatever they cover.
    */
  def shadowify(obj: Object3D[?], cast: Boolean = true, receive: Boolean = true): obj.type =
    obj.traverse((node: Object3D[Object3DEventMap]) =>
      if ThreeInterop.isMesh(node) then
        val seeThrough = ThreeInterop.hasUserDataFlag(node, "noShadow") ||
          ThreeInterop.transparentMaterial(node)
        node.castShadow = cast && !seeThrough
        node.receiveShadow = receive
    )
    obj

  /** A catenary-ish sagging curve between two points. */
  def sagCurve(a: Vector3, b: Vector3, sag: Double, segments: Int = 14): CatmullRomCurve3 =
    val points = (0 to segments).map { index =>
      val t     = index.toDouble / segments
      val point = new Vector3().lerpVectors(ThreeInterop.asVector3Like(a), ThreeInterop.asVector3Like(b), t)
      point.y -= math.sin(math.Pi * t) * sag
      point
    }
    new CatmullRomCurve3(points.toJSArray)

  final case class BakePart(geometry: BufferGeometry[?, ?], matrix: Option[Matrix4] = None)

  /** Merge a list of geometries into one buffer geometry (single draw call). Mixed
    * indexed/non-indexed batches are flattened to non-indexed first, exactly as upstream
    * `mergeGeometries` requires, and only the attributes every part shares survive.
    */
  def bake(parts: List[BakePart]): BufferGeometry[?, ?] =
    var geos = parts.map { part =>
      val geometry = part.geometry.clone()
      part.matrix.foreach(matrix => geometry.applyMatrix4(matrix))
      geometry
    }
    val indexed = geos.count(geometry => geometry.getIndex() != null)
    if indexed > 0 && indexed < geos.length then
      geos = geos.map { geometry =>
        if geometry.getIndex() == null then geometry else geometry.toNonIndexed()
      }
    val common = geos.map(geometry => ThreeInterop.attributeNames(geometry).toSet).reduce(_ & _)
    geos.foreach { geometry =>
      ThreeInterop
        .attributeNames(geometry)
        .filterNot(common)
        .foreach(name => geometry.deleteAttribute(name))
    }
    mergeGeometries(geos, common)

  private def mergeGeometries(
    geos: List[BufferGeometry[?, ?]],
    common: Set[String]
  ): BufferGeometry[?, ?] =
    val merged = new BufferGeometry()
    common.foreach { name =>
      val itemSize = ThreeInterop.attributeItemSize(geos.head, name).toInt
      val values   = mutable.ArrayBuilder.make[Double]
      geos.foreach(geometry => values ++= ThreeInterop.attributeDoubles(geometry, name))
      ThreeInterop.setFloatAttribute(merged, name, values.result(), itemSize)
    }
    if geos.forall(geometry => geometry.getIndex() != null) then
      val indices = mutable.ArrayBuilder.make[Double]
      var offset  = 0
      geos.foreach { geometry =>
        val index = geometry.getIndex()
        val count = index.count.toInt
        (0 until count).foreach { position =>
          indices += index.getX(position.toDouble) + offset
        }
        offset += ThreeInterop.positionCount(geometry).toInt
      }
      ThreeInterop.setIndexAttribute(merged, indices.result())
    merged
  end mergeGeometries
end Util
