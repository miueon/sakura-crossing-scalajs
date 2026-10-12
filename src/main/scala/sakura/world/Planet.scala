package sakura.world

import sakura.ThreeInterop
import sakura.core.{Toon, Util}
import vendor.three.*

import scala.collection.mutable
import scala.math
import scala.scalajs.js

/** The planet: the projection layer that wraps the flat-authored XZ plane onto a sphere,
  * ported 1:1 from upstream `src/world/planet.js`.
  *
  * The mapping is equirectangular, with the railway deliberately placed on the equator:
  * x -> longitude (wraps at exactly one circumference), z -> latitude (poles a quarter
  * circumference along the street), so the railway lands on a great circle and closes into a
  * seamless loop with zero distortion.
  */
final case class PlanetBasis(up: Vector3, east: Vector3, north: Vector3)

final case class FlatPoint(var x: Double, var y: Double, var z: Double)

final case class BakeStats(wrapped: Int, instanced: Int, rigid: Int, tris: Int)

final case class BuiltPlanet(group: Group[Object3DEventMap], land: Mesh[?, ?, ?])

object Planet:

  /** Planet radius in metres. Everything else derives from this one number. */
  val R: Double = 160.0

  val Circumference: Double = 2.0 * math.Pi * R

  /** Sphere centre, chosen so the flat origin sits on the surface with +Y up. */
  val Center: Vector3 = new Vector3(0.0, -R, 0.0)

  /** Visible ground horizon for an eye at height h -- useful for framing. */
  def horizonFor(h: Double): Double = math.sqrt(2.0 * R * h + h * h)

  /* ------------------------------- mapping ------------------------------- */

  private def unitNormal(x: Double, z: Double): Vector3 =
    val longitude = x / R
    val latitude  = z / R
    val cosLat    = math.cos(latitude)
    new Vector3(math.sin(longitude) * cosLat, math.cos(longitude) * cosLat, math.sin(latitude))

  /** Outward surface normal ("up") at flat coordinates (x, z). */
  def normalAt(x: Double, z: Double): Vector3 = unitNormal(x, z)

  /** World position of the flat point (x, z) raised y metres above the surface. */
  def positionAt(x: Double, y: Double, z: Double): Vector3 =
    unitNormal(x, z).multiplyScalar(R + y).add(ThreeInterop.asVector3Like(Center))

  /** Orthonormal tangent frame: east is +x, north is +z, up is the normal. At the origin
    * this is the identity, which is why district content near the crossing needs almost no
    * correction.
    */
  def basisAt(x: Double, z: Double): PlanetBasis =
    val longitude = x / R
    val latitude  = z / R
    val sinLon    = math.sin(longitude)
    val cosLon    = math.cos(longitude)
    val sinLat    = math.sin(latitude)
    val cosLat    = math.cos(latitude)
    PlanetBasis(
      up = new Vector3(sinLon * cosLat, cosLon * cosLat, sinLat),
      east = new Vector3(cosLon, -sinLon, 0.0),
      north = new Vector3(-sinLon * sinLat, -cosLon * sinLat, cosLat)
    )

  /** Full placement matrix for the flat point (x, z) at height y. */
  def frameAt(x: Double, z: Double, y: Double = 0.0): Matrix4 =
    val basis = basisAt(x, z)
    new Matrix4()
      .makeBasis(basis.east, basis.up, basis.north)
      .setPosition(positionAt(x, y, z))

  /** Inverse mapping: a point (or direction) on the sphere back to flat (x, z). */
  def flatAt(worldPos: Vector3): FlatPoint =
    val relative = new Vector3().copy(ThreeInterop.asVector3Like(worldPos)).sub(ThreeInterop.asVector3Like(Center))
    val radius   = if relative.length() == 0.0 then 1.0 else relative.length()
    relative.multiplyScalar(1.0 / radius)
    FlatPoint(
      x = R * math.atan2(relative.x, relative.y),
      y = radius - R,
      z = R * math.asin(MathUtils.clamp(relative.z, -1.0, 1.0))
    )

  /** Shortest signed difference between two longitudes, in metres of arc. */
  def wrapDelta(a: Double, b: Double): Double =
    var d = a - b
    while d > Circumference / 2.0 do d -= Circumference
    while d < -Circumference / 2.0 do d += Circumference
    d

  /** Fold a longitude into [-C/2, C/2). */
  def wrapX(x: Double): Double =
    val c = Circumference
    (((x + c / 2.0) % c) + c) % c - c / 2.0

  /* ---------------------------- geometry wrapping ---------------------------- */

  final private case class AttrArray(name: String, size: Int, src: Array[Double])

  /** Split any triangle with an edge longer than `maxEdge`.
    *
    * This is what makes wrapping arbitrary geometry safe. A 1000-metre rail is authored as a box
    * with two vertices along its length; bending only those two vertices would chord straight
    * through the planet. Bisecting the long edge until every span is short means the same flat
    * builders can produce planet-scale geometry with no changes.
    *
    * The material index of every triangle is carried through all the passes because both ends of
    * this function throw `geometry.groups` away: `toNonIndexed()` does not copy them, and the
    * rebuilt geometry starts empty. Group boundaries survive because triangles are processed in
    * order and each emits either one or two triangles, keeping the per-triangle array aligned
    * with the vertex output.
    */
  def subdivideLongEdges(geo: BufferGeometry[?, ?], maxEdge: Double): BufferGeometry[?, ?] =
    val g    = if geo.getIndex() != null then geo.toNonIndexed() else geo
    val max2 = maxEdge * maxEdge

    var arrays =
      ThreeInterop.attributeNames(g).map { name =>
        AttrArray(name, ThreeInterop.attributeItemSize(g, name).toInt, ThreeInterop.attributeDoubles(g, name))
      }
    var count = ThreeInterop.positionCount(g).toInt

    val srcGroups = if geo.groups.length > 0 then Some(geo.groups.toVector) else None
    var gid       = new Array[Int](count / 3)
    srcGroups.foreach { groups =>
      groups.foreach { group =>
        val first = math.floor(group.start / 3.0).toInt
        val last  = math.min(gid.length, first + math.floor(group.count / 3.0).toInt)
        (first until last).foreach { triangle =>
          gid(triangle) = group.materialIndex.toOption.map(_.toInt).getOrElse(0)
        }
      }
    }

    def positionSrc: Array[Double] = arrays.find(_.name == "position").get.src

    // bounded pass count: each pass at least halves the longest edge
    var pass   = 0
    var splits = 1
    while pass < 12 && splits > 0 do
      pass += 1
      splits = 0
      val pos          = positionSrc
      val out          = arrays.map(array => (array, mutable.ArrayBuilder.make[Double]))
      val gidOut       = mutable.ArrayBuilder.make[Int]
      var triangleHead = 0
      while triangleHead < count do
        var worst = -1.0
        var edge  = 0
        (0 until 3).foreach { candidate =>
          val a  = triangleHead + candidate
          val b  = triangleHead + (candidate + 1) % 3
          val dx = pos(a * 3) - pos(b * 3)
          val dy = pos(a * 3 + 1) - pos(b * 3 + 1)
          val dz = pos(a * 3 + 2) - pos(b * 3 + 2)
          val d2 = dx * dx + dy * dy + dz * dz
          if d2 > worst then
            worst = d2
            edge = candidate
        }
        if worst <= max2 then
          arrays.foreach { array =>
            val builder = out.find(_._1.name == array.name).get._2
            (0 until 3).foreach { corner =>
              (0 until array.size).foreach(k =>
                builder += array.src((triangleHead + corner) * array.size + k)
              )
            }
          }
          gidOut += gid(triangleHead / 3)
        else
          splits += 1
          gidOut += gid(triangleHead / 3)
          gidOut += gid(triangleHead / 3)
          val i0 = triangleHead + edge
          val i1 = triangleHead + (edge + 1) % 3
          val i2 = triangleHead + (edge + 2) % 3
          arrays.foreach { array =>
            val builder = out.find(_._1.name == array.name).get._2
            val mid = (0 until array.size).map(k =>
              (array.src(i0 * array.size + k) + array.src(i1 * array.size + k)) * 0.5
            )
            (0 until array.size).foreach(k => builder += array.src(i0 * array.size + k))
            mid.foreach(builder += _)
            (0 until array.size).foreach(k => builder += array.src(i2 * array.size + k))
            mid.foreach(builder += _)
            (0 until array.size).foreach(k => builder += array.src(i1 * array.size + k))
            (0 until array.size).foreach(k => builder += array.src(i2 * array.size + k))
          }
        end if
        triangleHead += 3
      end while
      /* Each pass re-encodes through a `Float32Array`, exactly as upstream does: the
       * subdivision decision compares edge lengths, so keeping the previous pass in double
       * precision moves edges that sit on the `maxEdge` threshold and changes how many rings a
       * tall part is cut into. */
      arrays = arrays.map { array =>
        val next = out.find(_._1.name == array.name).get._2.result().map(value => value.toFloat.toDouble)
        AttrArray(array.name, array.size, next)
      }
      gid = gidOut.result()
      count = positionSrc.length / 3
    end while

    val result = new BufferGeometry()
    arrays.foreach(array => ThreeInterop.setFloatAttribute(result, array.name, array.src, array.size))
    // one group per run of triangles sharing a material; single-material geometry is left
    // group-free, exactly as it arrived
    srcGroups.foreach { _ =>
      var start = 0
      (1 to gid.length).foreach { triangle =>
        if triangle == gid.length || gid(triangle) != gid(start) then
          result.addGroup((start * 3).toDouble, ((triangle - start) * 3).toDouble, gid(start).toDouble)
          start = triangle
      }
    }
    if g != geo then g.dispose()
    result
  end subdivideLongEdges

  /** Bend a geometry whose vertices are flat *world* coordinates onto the sphere. Returns a new
    * geometry; the input is left alone.
    */
  def wrapGeometry(geo: BufferGeometry[?, ?], maxEdge: Double = 3.0): BufferGeometry[?, ?] =
    val bent = subdivideLongEdges(geo, maxEdge)
    val pos  = ThreeInterop.positionAttribute(bent)
    (0 until pos.count.toInt).foreach { index =>
      val world = positionAt(pos.getX(index.toDouble), pos.getY(index.toDouble), pos.getZ(index.toDouble))
      pos.setXYZ(index.toDouble, world.x, world.y, world.z)
    }
    ThreeInterop.markForUpload(pos)
    bent.deleteAttribute("normal")
    bent.computeVertexNormals()
    bent.computeBoundingSphere()
    bent

  /* -------------------------------- baking -------------------------------- */

  private def underRigid(root: Object3D[?], node: Object3D[?]): Boolean =
    var parent = node.parent
    var found  = false
    while parent != null && !found && parent != root.parent do
      if ThreeInterop.hasUserDataFlag(parent, "planetRigid") then found = true
      parent = parent.parent
    found

  /** Project an already-built flat world onto the planet.
    *
    * Runs once, after every builder has finished, and only touches geometry and transforms --
    * which is the point: none of the world modules need to know the planet exists.
    *  - InstancedMesh -> each instance is re-seated rigidly on the surface
    *  - planetRigid   -> left in flat space; used for animated rigs near the origin
    *  - everything else -> subdivided and bent vertex by vertex
    */
  def bakeToPlanet(root: Object3D[?], maxEdge: Double = 3.0): BakeStats =
    root.updateMatrixWorld(js.defined(true))

    val rigid = mutable.ListBuffer.empty[(Object3D[?], Matrix4)]
    root.traverse((node: Object3D[Object3DEventMap]) =>
      if ThreeInterop.hasUserDataFlag(node, "planetRigid") && !underRigid(root, node) then
        rigid += ((node, node.matrixWorld.clone()))
    )

    val jobs = mutable.ListBuffer.empty[(Object3D[?], Matrix4)]
    root.traverse((node: Object3D[Object3DEventMap]) =>
      if (ThreeInterop.isMesh(node) || ThreeInterop.isLine(node)) &&
        !ThreeInterop.hasUserDataFlag(node, "planetRigid") && !underRigid(root, node)
      then jobs += ((node, node.matrixWorld.clone()))
    )

    var wrapped   = 0
    var instanced = 0
    var tris      = 0

    jobs.foreach { (obj, world) =>
      ThreeInterop.asInstancedObject(obj) match
        case Some(source) =>
          val matrixAttribute = source.instanceMatrix
          (0 until source.count.toInt).foreach { index =>
            val matrix = new Matrix4()
            ThreeInterop.matrixFromAttribute(matrix, matrixAttribute, index * 16)
            matrix.premultiply(world)
            val position = new Vector3()
            val rotation = new Quaternion()
            val scale    = new Vector3()
            matrix.decompose(position, rotation, scale)
            val basis  = basisAt(position.x, position.z)
            val frame  = new Matrix4().makeBasis(basis.east, basis.up, basis.north)
            val seated = new Quaternion().setFromRotationMatrix(frame)
            seated.multiply(rotation)
            matrix.compose(positionAt(position.x, position.y, position.z), seated, scale)
            ThreeInterop.matrixToAttribute(matrix, matrixAttribute, index * 16)
          }
          ThreeInterop.markForUpload(matrixAttribute)
          /* Instanced meshes stay unculled: their bound would have to come from the instance
           * cloud rather than the source geometry, and most of them span the district anyway. */
          obj.frustumCulled = false
          instanced += source.count.toInt
        case None =>
          ThreeInterop.asMesh(obj).foreach { mesh =>
            val flat = ThreeInterop.meshGeometry(mesh).clone().applyMatrix4(world)
            val bent = wrapGeometry(flat, maxEdge)
            flat.dispose()
            ThreeInterop.setMeshGeometry(mesh, bent)
            /* Everything else *is* culled, and this matters: the bake leaves every mesh with an
             * identity transform and its geometry in root space, so the geometry's own bounding
             * sphere is already a world-space bound and the frustum test is exact. */
            mesh.frustumCulled = true
            wrapped += 1
            tris += (ThreeInterop.positionCount(bent) / 3.0).toInt
          }
      end match
      obj.position.set(0.0, 0.0, 0.0)
      obj.quaternion.identity()
      obj.scale.set(1.0, 1.0, 1.0)
      obj.matrixAutoUpdate = true
    }

    // Baked geometry is in root space now, so every container above it must become the identity
    // or its transform would apply a second time.
    root.traverse((node: Object3D[Object3DEventMap]) =>
      if node != root && !ThreeInterop.isMesh(node) && !ThreeInterop.isLine(node) &&
        !ThreeInterop.hasUserDataFlag(node, "planetRigid") && !underRigid(root, node)
      then
        node.position.set(0.0, 0.0, 0.0)
        node.quaternion.identity()
        node.scale.set(1.0, 1.0, 1.0)
    )

    // Re-seat the rigid rigs: their parents are identity by now, so the local transform is the
    // world transform.
    rigid.foreach { (obj, world) =>
      val position = new Vector3()
      val rotation = new Quaternion()
      val scale    = new Vector3()
      world.decompose(position, rotation, scale)
      val basis = basisAt(position.x, position.z)
      val frame = new Matrix4().makeBasis(basis.east, basis.up, basis.north)
      obj.position.copy(ThreeInterop.asVector3Like(positionAt(position.x, position.y, position.z)))
      obj.quaternion.setFromRotationMatrix(frame).multiply(rotation)
      obj.scale.copy(ThreeInterop.asVector3Like(scale))
    }

    BakeStats(wrapped, instanced, rigid.length, tris)
  end bakeToPlanet

  /* ------------------------------- the sphere ------------------------------- */

  private val TerrainDrop   = 0.015
  private val CanalZ        = -24.0
  private val CanalX0       = -98.0
  private val CanalX1       = 106.0
  private val FootprintHalf = 3.4

  // Exact ports of the street helpers `buildPlanet` consumes; the street chunk will own the
  // full surface model, these are the two curves the sphere needs to agree with it.
  private def groundY(z: Double): Double =
    1.05 * Util.sstep(-13.0, -32.0, z) + 0.45 * Util.sstep(28.0, 48.0, z)

  private def inTrench(x: Double, z: Double): Boolean =
    x > CanalX0 && x < CanalX1 && math.abs(z - CanalZ) < FootprintHalf

  // Graded pads for the outlying districts; inert while Relief stays 0, kept because they are
  // the record of which ground each district needed flat.
  private val Pads = Vector(
    (32.0, -50.0, 28.0, 28.0), (-37.0, -27.0, 30.0, 15.0), (-31.0, 30.0, 20.0, 21.0), (-32.0, 50.0, 22.0, 18.0),
    (30.0, 31.0, 21.0, 22.0), (22.0, 54.0, 26.0, 13.0), (-46.0, -11.0, 22.0, 11.0), (54.0, 22.0, 16.0, 26.0),
    (20.0, 70.0, 26.0, 12.0), (-13.0, -48.0, 15.0, 14.0), (-14.0, 54.0, 12.0, 12.0), (62.0, 55.0, 17.0, 11.0)
  )

  /** How much of the low-hill relief is applied. Zero: the planet is a true sphere; with the
    * relief gone the masks are inert and the whole thing collapses to a sphere of radius
    * `R + groundY(z)`.
    */
  private val Relief = 0.0

  def reliefAt(x: Double, z: Double): Double =
    if Relief <= 0.0 then 0.0
    else
      val corridor = MathUtils.smoothstep(math.abs(z), 7.0, 26.0)
      val channel  = MathUtils.smoothstep(math.abs(z - CanalZ), 7.0, 20.0)
      val district = MathUtils.smoothstep(
        math.hypot(wrapDelta(x, 0.0) * 0.55, z * 0.5), 26.0, 70.0
      )
      var mask = math.min(corridor, channel) * district
      Pads.foreach { case (padX, padZ, padRx, padRz) =>
        if mask > 0.001 then
          val distance = math.max(
            math.abs(wrapDelta(x, padX)) / padRx,
            math.abs(z - padZ) / padRz
          )
          mask *= MathUtils.smoothstep(distance, 1.0, 1.5)
      }
      if mask <= 0.001 then 0.0
      else
        val longitude = x / R
        val latitude  = z / R
        var height    = 0.0
        height += math.sin(longitude * 3.1 + 0.7) * math.cos(latitude * 2.3) * 6.0
        height += math.sin(longitude * 7.3 - 1.9) * math.cos(latitude * 5.1 + 0.4) * 2.6
        height += math.sin(longitude * 13.7 + 2.4) * math.cos(latitude * 11.3) * 1.1
        math.max(0.0, height + 2.2) * mask * Relief

  /** Drop the sphere's faces under the canal excavation: a triangle goes if *any* of its corners
    * is inside the footprint, so nothing survives that could rise through the channel bed.
    */
  private def cutSphereTrench(geo: BufferGeometry[?, ?]): Unit =
    val pos  = ThreeInterop.positionAttribute(geo)
    val keep = mutable.ArrayBuilder.make[Double]
    (0 until pos.count.toInt by 3).foreach { triangle =>
      def inside(corner: Int): Boolean =
        val flat = flatAt(new Vector3(pos.getX(corner.toDouble), pos.getY(corner.toDouble), pos.getZ(corner.toDouble)))
        inTrench(flat.x, flat.z)
      if !(inside(triangle) || inside(triangle + 1) || inside(triangle + 2)) then
        (0 until 3).foreach { corner =>
          keep += pos.getX((triangle + corner).toDouble)
          keep += pos.getY((triangle + corner).toDouble)
          keep += pos.getZ((triangle + corner).toDouble)
        }
    }
    geo.setAttribute("position", new Float32BufferAttribute(js.Array(keep.result()*), 3.0))

  def buildPlanet(scene: Scene[Object3DEventMap]): BuiltPlanet =
    val group = new Group()
    group.name = "planet"

    /* An icosphere rather than a UV sphere: even triangles and no pole pinching. Detail 30 is
     * derived, not chosen: at R = 160 the edge is 168 / (d + 1) metres and a facet's centre sags
     * below the true sphere by about e^2 / (6R). Detail 30 is a 5.4 m triangle sagging 30 mm,
     * comfortably inside the 65 mm the terrain grid clears by. */
    val geo    = new IcosahedronGeometry(R, 30.0)
    val pos    = ThreeInterop.positionAttribute(geo)
    val normal = new Vector3()
    val point  = new Vector3()
    (0 until pos.count.toInt).foreach { index =>
      normal
        .set(pos.getX(index.toDouble), pos.getY(index.toDouble), pos.getZ(index.toDouble))
        .normalize()
      point.copy(ThreeInterop.asVector3Like(normal)).multiplyScalar(R).add(ThreeInterop.asVector3Like(Center))
      val flat = flatAt(point)
      // 65 mm under the graded terrain grid, wherever the grid reaches; out past it the sphere
      // *is* the ground and `reliefAt` is the whole shape of it
      val height =
        reliefAt(flat.x, flat.z) + groundY(flat.z) * 0.999 - TerrainDrop - 0.065
      point.copy(ThreeInterop.asVector3Like(normal)).multiplyScalar(R + height).add(ThreeInterop.asVector3Like(Center))
      pos.setXYZ(index.toDouble, point.x, point.y, point.z)
    }
    ThreeInterop.markForUpload(pos)
    geo.deleteAttribute("normal")
    cutSphereTrench(geo)
    /* Normals taken from the sphere, not from the faces: every vertex sits on a sphere about
     * `Center`, so its normal is simply the radial direction, and the toon ramp's terminator
     * stays a clean curve instead of a staircase of facets. */
    val worldPositions = ThreeInterop.positionAttribute(geo)
    val normals        = new js.typedarray.Float32Array(worldPositions.count.toInt * 3)
    val radial         = new Vector3()
    (0 until worldPositions.count.toInt).foreach { index =>
      radial
        .set(
          worldPositions.getX(index.toDouble),
          worldPositions.getY(index.toDouble),
          worldPositions.getZ(index.toDouble)
        )
        .sub(ThreeInterop.asVector3Like(Center))
        .normalize()
      normals(index * 3) = radial.x.toFloat
      normals(index * 3 + 1) = radial.y.toFloat
      normals(index * 3 + 2) = radial.z.toFloat
    }
    geo.setAttribute("normal", new BufferAttribute(normals, 3.0))

    val land = new Mesh(
      geo,
      Toon.cel(Toon.CelOptions(color = 0xc4c4b6, bands = 4, tint = 0x7a7396))
    )
    land.receiveShadow = true
    // Must NOT cast: a planet-sized closed sphere renders its far hemisphere into the shadow
    // map and drops the entire surface into its own shadow.
    land.castShadow = false
    land.frustumCulled = false
    land.name = "planetLand"
    ThreeInterop.addChild(group, land)

    ThreeInterop.addChild(scene, group)
    BuiltPlanet(group, land)
  end buildPlanet
end Planet
