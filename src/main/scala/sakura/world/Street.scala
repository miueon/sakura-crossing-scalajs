package sakura.world

import sakura.ThreeInterop
import sakura.core.{Pal, Textures, Toon, Util}
import vendor.three.*

import scala.scalajs.js

/** The street, ported 1:1 from upstream `src/world/street.js`.
  *
  * Everything in the world is placed relative to a single curved centreline. The
  * stretch around the railway crossing is kept straight so the crossing reads
  * cleanly, then the road bends and climbs away to the north-west and bends the
  * other way behind the player, which hides both ends of the scene without a
  * visible wall.
  */
object Street:

  val RoadHalf: Double    = 3.15
  val WalkW: Double       = 1.55
  val WalkH: Double       = 0.135
  val TerrainDrop: Double = 0.015
  val ZMin: Double        = -66.0
  val ZMax: Double        = 52.0

  /** Track centre is z = 0; the gates stand just outside the ballast. */
  val TrackHalf: Double = 2.2
  val GateZ: Double     = 2.95
  val CrossBand: Double = 3.35

  /** Lateral drift of the road centre. */
  def centerX(z: Double): Double =
    3.0 * Util.sstep(-11.0, -36.0, z) - 3.4 * Util.sstep(16.0, 44.0, z)

  /** Ground height along the street: it climbs gently past the crossing. */
  def groundY(z: Double): Double =
    1.05 * Util.sstep(-13.0, -32.0, z) + 0.45 * Util.sstep(28.0, 48.0, z)

  /** Signed distance from the road centre. */
  def lateral(x: Double, z: Double): Double = x - centerX(z)

  def isSidewalk(x: Double, z: Double): Boolean =
    /* Bounded in z: without this line the lateral test answers "footway" for two
     * invisible 0.135 m ledges running off past the ends of the carriageway. */
    if z < ZMin || z > ZMax then false
    else
      val distance = math.abs(lateral(x, z))
      if math.abs(z) < CrossBand then false
      else distance > RoadHalf - 0.02 && distance < RoadHalf + WalkW

  /** Height of the walkable surface (road, kerb, crossing deck). */
  def streetHeight(x: Double, z: Double): Double =
    groundY(z) + (if isSidewalk(x, z) then WalkH else 0.0)

  final case class StripEdge(x: Double, y: Double)

  final case class StripOptions(
    z0: Double,
    z1: Double,
    step: Double = 1.2,
    a: Double => StripEdge,
    b: Double => StripEdge,
    uv: (Double, Double) = (1.0, 1.0),
    flip: Boolean = false
  )

  /** Quad strip swept along z. `a(z)` and `b(z)` return the two edge points; for
    * horizontal surfaces a is the -X edge, for vertical faces a is the bottom
    * edge.
    */
  def makeStrip(options: StripOptions): BufferGeometry[?, ?] =
    val rows = math.max(2, math.round(math.abs(options.z1 - options.z0) / options.step) + 1).toInt
    val pos  = mutablePos(rows)
    val uvs  = mutablePos2(rows)
    val idx  = js.Array[Double]()
    (0 until rows).foreach { i =>
      val t  = i.toDouble / (rows - 1)
      val z  = options.z0 + (options.z1 - options.z0) * t
      val pa = options.a(z)
      val pb = options.b(z)
      pos += (pa.x, pa.y, z, pb.x, pb.y, z)
      uvs += (0.0, t * options.uv._2, options.uv._1, t * options.uv._2)
    }
    (0 until rows - 1).foreach { i =>
      val o = i * 2
      if options.flip then
        idx.push(o, o + 1, o + 2, o + 1, o + 3, o + 2)
      else idx.push(o, o + 2, o + 1, o + 1, o + 2, o + 3)
    }
    val geometry = new BufferGeometry()
    ThreeInterop.setFloatAttribute(geometry, "position", pos.result(), 3)
    ThreeInterop.setFloatAttribute(geometry, "uv", uvs.result(), 2)
    geometry.setIndex(idx)
    geometry.computeVertexNormals()
    geometry
  end makeStrip

  private def mutablePos(rows: Int): scala.collection.mutable.ArrayBuilder[Double] =
    val builder = scala.collection.mutable.ArrayBuilder.make[Double]
    builder.sizeHint(rows * 6)
    builder

  private def mutablePos2(rows: Int): scala.collection.mutable.ArrayBuilder[Double] =
    val builder = scala.collection.mutable.ArrayBuilder.make[Double]
    builder.sizeHint(rows * 4)
    builder

  final private case class SideStripOptions(
    from: Double,
    to: Double,
    mat: Material[?],
    off0: Double,
    off1: Double,
    y: Double,
    uv: (Double, Double) = (1.0, 1.0),
    receive: Boolean = true,
    name: String = "strip"
  )

  /** Two strips, one on each side of the road, offset from the centre. */
  private def sideStrips(ctx: WorldCtx, options: SideStripOptions): Unit =
    Vector(1.0, -1.0).foreach { s =>
      val edgeA = (z: Double) =>
        StripEdge(centerX(z) + s * (if s > 0 then options.off0 else options.off1), groundY(z) + options.y)
      val edgeB = (z: Double) =>
        StripEdge(centerX(z) + s * (if s > 0 then options.off1 else options.off0), groundY(z) + options.y)
      val geometry = makeStrip(StripOptions(z0 = options.from, z1 = options.to, a = edgeA, b = edgeB,
        uv = options.uv, flip = s < 0))
      val mesh = new Mesh(geometry, options.mat)
      mesh.receiveShadow = options.receive
      mesh.name = options.name + (if s > 0 then "_r" else "_l")
      ctx.add(mesh)
      ()
    }

  private def buildTerrain(ctx: WorldCtx, mat: Material[?]): Unit =
    /* 2.0 m cells (160 segments over 320 m) keep every triangle under the
     * planet-wrapping edge limit, so nothing is split on the way to the sphere.
     * Each row is also dropped by its own chord excess so the interpolated
     * surface stays under the graded plane rather than only its vertices. */
    val width = 320.0
    val depth = 320.0
    val seg   = 160
    val off   = -20.0
    val row   = depth / seg
    val plane = (z: Double) => groundY(z) - TerrainDrop
    val rowY = (z: Double) =>
      var excess = 0.0
      Vector(-1.0, 1.0).foreach { s =>
        val chordMid = (plane(z) + plane(z + s * row)) / 2.0
        excess = math.max(excess, chordMid - plane(z + (s * row) / 2.0))
      }
      plane(z) - excess
    val geometry = new PlaneGeometry(width, depth, seg.toDouble, seg.toDouble)
    geometry.rotateX(-math.Pi / 2.0)
    val positions = ThreeInterop.positionAttribute(geometry)
    (0 until positions.count.toInt).foreach { i =>
      positions.setY(i.toDouble, rowY(positions.getZ(i.toDouble) + off))
    }
    ThreeInterop.markForUpload(positions)
    geometry.translate(0.0, 0.0, off)
    Landform.cutTrench(geometry)
    geometry.computeVertexNormals()
    val mesh = new Mesh(geometry, mat)
    mesh.receiveShadow = true
    mesh.name = "terrain"
    ctx.add(mesh)
    ()
  end buildTerrain

  def buildStreet(ctx: WorldCtx): Unit =
    val matTerrain   = Toon.cel(Toon.CelOptions(color = 0xc4c4b6, bands = 3, tint = 0x7a7396))
    val matRoad      = Toon.cel(Toon.CelOptions(color = Pal.road, bands = 3, tint = 0x6a608f))
    val matRoadPatch = Toon.cel(Toon.CelOptions(color = Pal.roadWorn, bands = 3, tint = 0x6a608f))
    val matWalk      = Toon.cel(Toon.CelOptions(color = Pal.sidewalk, bands = 3, tint = 0x7d74a0))
    val matWalkAlt   = Toon.cel(Toon.CelOptions(color = Pal.sidewalkAlt, bands = 3, tint = 0x7d74a0))
    val matCurb      = Toon.cel(Toon.CelOptions(color = Pal.curb, bands = 3, tint = 0x6f6790))
    val matLine      = Toon.cel(Toon.CelOptions(color = Pal.lineWhite, bands = 2, tint = 0x8e86ad))
    val matGutter    = Toon.cel(Toon.CelOptions(color = Pal.gutter, bands = 3, tint = 0x6f6790))
    val matDrain = Toon.cel(Toon.CelOptions(color = Pal.drain, bands = 3,
      map = Some(Textures.drainTex()), tint = 0x5d5878))
    val matTactile = Toon.cel(Toon.CelOptions(color = 0xffffff, bands = 2,
      map = Some(Textures.tactileTex(false)), tint = 0x9a7f4a))

    buildTerrain(ctx, matTerrain)

    // asphalt
    val roadA    = (z: Double) => StripEdge(centerX(z) - RoadHalf, groundY(z) + 0.012)
    val roadB    = (z: Double) => StripEdge(centerX(z) + RoadHalf, groundY(z) + 0.012)
    val roadMesh = new Mesh(makeStrip(StripOptions(z0 = ZMin, z1 = ZMax, a = roadA, b = roadB, step = 1.1)), matRoad)
    roadMesh.receiveShadow = true
    roadMesh.name = "road"
    ctx.add(roadMesh)

    // a few flat repair patches so the asphalt is not a dead field
    Vector(
      (-0.9, 8.4, 2.6, 3.2), (1.6, 19.5, 2.0, 4.4), (-1.4, -8.5, 3.0, 2.4), (0.8, 27.0, 2.4, 5.0), (-1.9, 34.0, 2.2, 3.6),
      (2.0, -20.0, 2.2, 3.0)
    ).foreach { case (dx, z, w, d) =>
      val geometry = new PlaneGeometry(w, d)
      geometry.rotateX(-math.Pi / 2.0)
      val mesh = new Mesh(geometry, matRoadPatch)
      mesh.position.set(centerX(z) + dx, groundY(z) + 0.018, z)
      mesh.receiveShadow = true
      ThreeInterop.setUserDataFlag(mesh, "noOutline")
      ctx.add(mesh)
    }

    // white edge lines, broken by the crossing
    Vector((ZMin, -CrossBand), (CrossBand, ZMax)).foreach { case (z0, z1) =>
      sideStrips(ctx,
        SideStripOptions(from = z0, to = z1, mat = matLine, off0 = 2.72, off1 = 2.86,
          y = 0.024, name = "edgeline"))
    }

    // painted road text and crossing-ahead diamonds
    def paint(kind: Textures.RoadPaintKind, dx: Double, z: Double, size: Double, ry: Double = 0.0): Unit =
      val geometry = new PlaneGeometry(size, size * 1.9)
      geometry.rotateX(-math.Pi / 2.0)
      val mesh = new Mesh(geometry,
        Toon.flat(Toon.FlatOptions(color = 0xffffff,
          map = Some(Textures.roadPaint(kind)), transparent = true, depthWrite = Some(false), cache = false)))
      mesh.position.set(centerX(z) + dx, groundY(z) + 0.022, z)
      mesh.rotation.y = ry
      ThreeInterop.setUserDataFlag(mesh, "noOutline")
      mesh.renderOrder = 1.0
      ctx.add(mesh)
      ()

    paint(Textures.RoadPaintKind.Stop, 1.45, 8.5, 1.5)
    paint(Textures.RoadPaintKind.Stop, -1.5, -9.6, 1.5, math.Pi)
    paint(Textures.RoadPaintKind.Diamond, 1.5, 16.5, 1.35)
    paint(Textures.RoadPaintKind.Diamond, -1.55, -17.5, 1.35, math.Pi)
    paint(Textures.RoadPaintKind.Diamond, 1.6, 25.0, 1.3)

    // stop bars either side of the crossing
    Vector(1.0, -1.0).foreach { s =>
      val z        = s * (CrossBand + 0.85)
      val geometry = new PlaneGeometry(2.55, 0.42)
      geometry.rotateX(-math.Pi / 2.0)
      val mesh = new Mesh(geometry, matLine)
      mesh.position.set(centerX(z) + s * 1.35, groundY(z) + 0.026, z)
      ThreeInterop.setUserDataFlag(mesh, "noOutline")
      ctx.add(mesh)
      ()
    }

    // kerbs and pavement
    Vector((ZMin, -CrossBand), (CrossBand, ZMax)).foreach { case (z0, z1) =>
      sideStrips(ctx,
        SideStripOptions(from = z0, to = z1, mat = matWalk,
          off0 = RoadHalf, off1 = RoadHalf + WalkW, y = WalkH, name = "walk"))
      Vector(1.0, -1.0).foreach { s =>
        val kerbA = (z: Double) => StripEdge(centerX(z) + s * RoadHalf, groundY(z))
        val kerbB = (z: Double) => StripEdge(centerX(z) + s * RoadHalf, groundY(z) + WalkH)
        val mesh  = new Mesh(makeStrip(StripOptions(z0 = z0, z1 = z1, a = kerbA, b = kerbB, flip = s < 0)), matCurb)
        mesh.receiveShadow = true
        ctx.add(mesh)
        ()
      }
      sideStrips(ctx,
        SideStripOptions(from = z0, to = z1, mat = matWalkAlt,
          off0 = RoadHalf + WalkW, off1 = RoadHalf + WalkW + 0.22, y = WalkH, name = "walkedge"))
      sideStrips(ctx,
        SideStripOptions(from = z0, to = z1, mat = matTactile,
          off0 = RoadHalf + 0.44, off1 = RoadHalf + 0.78, y = WalkH + 0.014,
          uv = (1.0, math.abs(z1 - z0) / 0.34), name = "tactile"))
    }

    // drainage channel along the kerb, with grates
    Vector((ZMin, -CrossBand), (CrossBand, ZMax)).foreach { case (z0, z1) =>
      sideStrips(ctx,
        SideStripOptions(from = z0, to = z1, mat = matGutter,
          off0 = RoadHalf - 0.30, off1 = RoadHalf - 0.02, y = 0.004, name = "gutter"))
    }
    val grateDepth    = 0.62
    val grateCount    = 26
    val grateGeometry = new BoxGeometry(0.26, 0.05, grateDepth)
    val grates        = new InstancedMesh(grateGeometry, matDrain, grateCount.toDouble)
    var grateIndex    = 0
    val dummy         = new Object3D()
    var z             = -44.0
    while z <= 46.0 && grateIndex < grateCount do
      if !(math.abs(z) < CrossBand + 1.2) then
        val s = if grateIndex % 2 == 0 then 1.0 else -1.0
        dummy.position.set(centerX(z) + s * (RoadHalf - 0.16), groundY(z) + 0.03, z)
        dummy.rotation.set(0.0, math.atan2(centerX(z + grateDepth / 2.0) - centerX(z - grateDepth / 2.0), grateDepth),
          0.0)
        dummy.updateMatrix()
        grates.setMatrixAt(grateIndex.toDouble, dummy.matrix)
        grateIndex += 1
      z += 7.4
    grates.count = grateIndex.toDouble
    grates.receiveShadow = true
    ctx.add(grates)

    // manhole covers
    val manholeGeometry = new CylinderGeometry(0.32, 0.32, 0.04, 12.0)
    val manholes = new InstancedMesh(manholeGeometry,
      Toon.cel(Toon.CelOptions(color = Pal.metalDark, bands = 3)), 7.0)
    Vector(
      (-1.1, 6.2), (1.4, 15.0), (-0.6, -9.5), (1.9, 24.0), (-1.7, 31.5), (0.4, -19.0), (2.2, 41.0)
    ).zipWithIndex.foreach { case ((dx, spotZ), i) =>
      dummy.position.set(centerX(spotZ) + dx, groundY(spotZ) + 0.024, spotZ)
      dummy.updateMatrix()
      manholes.setMatrixAt(i.toDouble, dummy.matrix)
    }
    manholes.receiveShadow = true
    ctx.add(manholes)

    // side alley: a level gap in the left-hand frontage
    val zc       = 17.6
    val xc       = centerX(zc) - (RoadHalf + WalkW + 1.9)
    val alley    = new Group()
    val alleyMat = Toon.cel(Toon.CelOptions(color = Pal.concrete, bands = 3, tint = 0x6f6790))
    val floor    = new Mesh(new PlaneGeometry(4.2, 3.0), alleyMat)
    floor.rotation.x = -math.Pi / 2.0
    floor.position.set(xc - 1.2, groundY(zc) + WalkH + 0.005, zc)
    floor.receiveShadow = true
    ThreeInterop.addChild(alley, floor)
    val post = Util.cyl(0.045, 0.045, 2.1, 8,
      Toon.cel(Toon.CelOptions(color = Pal.metalDark, bands = 3)),
      xc + 1.6, groundY(zc) + WalkH + 1.05, zc + 1.5)
    post.castShadow = true
    ThreeInterop.addChild(alley, post)
    val plate = new Mesh(
      new BoxGeometry(1.1, 0.28, 0.04),
      ThreeInterop.materialArray(
        Toon.flat(Toon.FlatOptions(color = Pal.wallWhite)),
        Toon.flat(Toon.FlatOptions(color = Pal.wallWhite)),
        Toon.flat(Toon.FlatOptions(color = Pal.wallWhite)),
        Toon.flat(Toon.FlatOptions(color = Pal.wallWhite)),
        Toon.flat(Toon.FlatOptions(color = 0xffffff, map = Some(Textures.alleyPlate()), cache = false)),
        Toon.flat(Toon.FlatOptions(color = Pal.wallGray))
      )
    )
    plate.position.set(xc + 1.6, groundY(zc) + WalkH + 1.98, zc + 1.53)
    plate.castShadow = true
    ThreeInterop.addChild(alley, plate)
    ctx.add(alley)
    /* This gap is level: register the visible slab as a low walkable
     * continuation of the footway instead of a wall. */
    ctx.platform(
      x0 = xc - 3.3, x1 = xc + 0.9,
      z0 = zc - 1.5, z1 = zc + 1.5,
      top = groundY(zc) + WalkH + 0.005
    )

    // no-parking sign on the near right
    val signZ = 22.0
    val signX = centerX(signZ) + RoadHalf + 1.2
    val signY = groundY(signZ) + WalkH
    val signPost = Util.cyl(0.045, 0.05, 2.4, 8,
      Toon.cel(Toon.CelOptions(color = Pal.metal, bands = 3)), signX, signY + 1.2, signZ)
    signPost.castShadow = true
    ctx.add(signPost)
    val disc = new Mesh(
      new CylinderGeometry(0.3, 0.3, 0.035, 20.0),
      ThreeInterop.materialArray(
        Toon.cel(Toon.CelOptions(color = Pal.wallWhite, bands = 2)),
        Toon.flat(Toon.FlatOptions(color = 0xffffff, map = Some(Textures.noParking()), cache = false)),
        Toon.cel(Toon.CelOptions(color = Pal.wallGray, bands = 2))
      )
    )
    disc.rotation.set(0.0, -0.28, math.Pi / 2.0)
    disc.position.set(signX, signY + 2.15, signZ)
    disc.castShadow = true
    ctx.add(disc)
    ()
  end buildStreet
end Street
