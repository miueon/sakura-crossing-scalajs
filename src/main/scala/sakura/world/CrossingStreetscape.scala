package sakura.world

import sakura.{Palette, PlanetCurvature, ProceduralTextures, TerrainHeightmap, TextureCache, ThreeInterop, ToonMaterial}
import vendor.three.*

import scala.math
import scala.scalajs.js

/** The street around the level crossing, ported from upstream `street.js` and the crossing corner
  * cluster of upstream `index.js`: sidewalks, kerbs and tactile paving either side of the
  * carriageway, the painted 止まれ and stop bars, and the corner cluster — bicycles, the garden
  * wall, the roadside 祠, utility poles with their cable runs — that anchors the hero frame.
  */
object CrossingStreetscape:
  val RoadHalfWidth: Double  = RailwayTrack.RoadCrossingWidth / 2.0
  val SidewalkWidth: Double  = 1.55
  val SidewalkHeight: Double = 0.135
  val CrossBand: Double      = 3.35
  val RoadZMin: Double       = -66.0
  val RoadZMax: Double       = 52.0
  val ShopFrontX: Double     = RoadHalfWidth + SidewalkWidth + 0.12

  /** The painted 止まれ markings: lane offset, position and the yaw that faces approaching
    * traffic, one per crossing approach.
    */
  val StopMarkings: List[(Double, Double, Double)] = List((1.45, 8.5, 0.0), (-1.5, -9.6, math.Pi))
  val StopMarkingWidth: Double                     = 1.5
  val StopMarkingLength: Double                    = 2.85

  private val walkZRanges = List((CrossBand, RoadZMax), (RoadZMin, -CrossBand))

  def build(curvature: PlanetCurvature, textures: TextureCache): SceneAssembly =
    val group = new Group()

    val (walkGeometries, walkMaterials)       = sidewalks(group, curvature)
    val (paintGeometries, paintMaterials)     = roadPaint(group, textures, curvature)
    val (clusterGeometries, clusterMaterials) = cornerCluster(group, curvature)
    val (tactileGeometries, tactileMaterials) = addTactileStrips(group, textures, curvature)
    val (dappleGeometries, dappleMaterials)   = dappledShadows(group, curvature)

    SceneAssembly(
      group,
      walkGeometries ++ paintGeometries ++ clusterGeometries ++
        tactileGeometries ++ dappleGeometries,
      walkMaterials ++ paintMaterials ++ clusterMaterials ++
        tactileMaterials ++ dappleMaterials
    )
  end build

  private def dappledShadows(
    group: Group[?],
    curvature: PlanetCurvature
  ): (List[BufferGeometry[?, ?]], List[Material[?]]) =
    val geometry = new CircleGeometry(1.0, 10.0)
    geometry.rotateX(-math.Pi / 2.0)
    val material = new MeshBasicMaterial()
    material.color.setHex(0x8a7fae.toDouble)
    material.transparent = true
    material.opacity = 0.13
    material.depthWrite = false
    val clusters = List(
      (-7.1, 5.8, 1.6, 2.4, 8), (6.3, 2.5, 1.5, 2.2, 8), (-5.9, 17.9, 1.4, 2.4, 8), (13.5, 3.9, 1.5, 2.3, 8)
    )
    val count     = clusters.map(_._5).sum
    val instances = new InstancedMesh(geometry, material, count.toDouble)
    val transform = new Object3D()
    val random    = new scala.util.Random(2903L)
    var index     = 0
    clusters.foreach { (centerX, centerZ, radius, spread, amount) =>
      (0 until amount).foreach { _ =>
        val size = radius * (0.55 + random.nextDouble() * 0.8)
        val x    = centerX + (random.nextDouble() * 2.0 - 1.0) * spread
        val z    = centerZ + (random.nextDouble() * 2.0 - 1.0) * spread
        transform.position.set(x, curvature.bend(x, TerrainHeightmap.groundY(z) + 0.03, z), z)
        transform.rotation.set(0.0, random.nextDouble() * 3.0, 0.0)
        transform.scale.set(size, 1.0, size * (0.7 + random.nextDouble() * 0.4))
        transform.updateMatrix()
        instances.setMatrixAt(index, transform.matrix)
        index += 1
      }
    }
    ThreeInterop.markForUpload(instances.instanceMatrix)
    ThreeInterop.setUserDataFlag(instances, "noOutline")
    instances.renderOrder = 1.0
    ThreeInterop.addChild(group, instances)
    (List(geometry), List(material))
  end dappledShadows

  /** Sidewalk slabs, kerb faces, outer edge strips, the tactile paving band and the kerbside
    * gutter — one run each side of the crossing band, seated row by row on the ground plane.
    */
  private def sidewalks(group: Group[?], curvature: PlanetCurvature): (List[BufferGeometry[?, ?]], List[Material[?]]) =
    val (walk, kerb, walkEdge, gutter) =
      (new GeometryBuilder, new GeometryBuilder, new GeometryBuilder, new GeometryBuilder)
    val step = 2.0

    walkZRanges.foreach { case (z0, z1) =>
      val rows = math.ceil((z1 - z0) / step).toInt
      (0 until rows).foreach { row =>
        val za = z0 + row * step
        val zb = math.min(z1, za + step)
        val zm = (za + zb) / 2.0
        List(-1.0, 1.0).foreach { side =>
          walk.addBox(side * (RoadHalfWidth + SidewalkWidth / 2.0),
            TerrainHeightmap.groundY(zm) + SidewalkHeight / 2.0, zm, SidewalkWidth,
            SidewalkHeight + 0.06, zb - za + 0.02)
          kerb.addBox(side * (RoadHalfWidth - 0.03),
            TerrainHeightmap.groundY(zm) + SidewalkHeight / 2.0, zm, 0.16, SidewalkHeight + 0.02,
            zb - za + 0.02)
          walkEdge.addBox(side * (RoadHalfWidth + SidewalkWidth + 0.11),
            TerrainHeightmap.groundY(zm) + SidewalkHeight / 2.0, zm, 0.22, SidewalkHeight + 0.04,
            zb - za + 0.02)
          gutter.addBox(side * (RoadHalfWidth - 0.155),
            TerrainHeightmap.groundY(zm) + 0.004, zm, 0.28, 0.012, zb - za + 0.02)
        }
      }
    }

    val geometries = List(walk.result(), kerb.result(), walkEdge.result(), gutter.result())
    val materials = List(
      ToonMaterial.celMaterial(0x918d9b, curvature),
      ToonMaterial.celMaterial(0x74717f, curvature),
      ToonMaterial.celMaterial(0x9c988f, curvature),
      ToonMaterial.celMaterial(0x5c5a66, curvature)
    )
    geometries.lazyZip(materials).foreach { (geometry, material) =>
      val mesh = new Mesh(geometry, material)
      ThreeInterop.setUserDataFlag(mesh, "noOutline")
      ThreeInterop.addChild(group, mesh)
    }

    (geometries, materials)
  end sidewalks

  /** The yellow tactile band down the inside edge of each footway. */
  private def addTactileStrips(
    group: Group[?],
    textures: TextureCache,
    curvature: PlanetCurvature
  ): (List[BufferGeometry[?, ?]], List[Material[?]]) =
    // The two ranges tile at different rates; each gets its own cache key so the repeat on one
    // texture cannot stretch the other range's strip.
    val keys       = List(ProceduralTextures.TactilePavingKey, ProceduralTextures.TactilePavingNorthKey)
    val geometries = List.newBuilder[BufferGeometry[?, ?]]
    val materials  = List.newBuilder[Material[?]]
    walkZRanges.zip(keys).foreach { case ((z0, z1), key) =>
      val length  = z1 - z0
      val texture = textures.get(key)
      texture.repeat.set(1.0, length / 0.34)
      val material = new MeshBasicMaterial(MeshBasicMaterialParameters(map = js.defined(texture)))
      val strip    = new GeometryBuilder()
      List(-1.0, 1.0).foreach { side =>
        val zm   = (z0 + z1) / 2.0
        val half = new PlaneGeometry(0.34, length)
        half.rotateX(-math.Pi / 2.0)
        half.translate(side * (RoadHalfWidth + 0.61),
          TerrainHeightmap.groundY(zm) + SidewalkHeight + 0.014, zm)
        strip.addMerged(half)
        half.dispose()
      }
      val geometry = strip.result()
      WorldGeometry.bend(geometry, curvature)
      val mesh = new Mesh(geometry, material)
      ThreeInterop.setUserDataFlag(mesh, "noOutline")
      ThreeInterop.addChild(group, mesh)
      geometries += geometry
      materials += material
    }
    (geometries.result(), materials.result())
  end addTactileStrips

  /** Road paint: white edge lines broken by the crossing, the stop bars either side of the
    * tracks, the 止まれ markings facing each approach, and the near manhole.
    */
  private def roadPaint(
    group: Group[?],
    textures: TextureCache,
    curvature: PlanetCurvature
  ): (List[BufferGeometry[?, ?]], List[Material[?]]) =
    val edgeLines = new GeometryBuilder
    walkZRanges.foreach { case (z0, z1) =>
      val rows = math.ceil((z1 - z0) / 4.0).toInt
      (0 until rows).foreach { row =>
        val za = z0 + row * 4.0
        val zb = math.min(z1, za + 4.0)
        val zm = (za + zb) / 2.0
        List(-1.0, 1.0).foreach { side =>
          edgeLines.addBox(side * 2.79, TerrainHeightmap.groundY(zm) + 0.024, zm, 0.14, 0.012,
            zb - za + 0.02)
        }
      }
    }
    List(1.0, -1.0).foreach { side =>
      val z = side * (CrossBand + 0.85)
      edgeLines.addBox(side * 1.35, TerrainHeightmap.groundY(z) + 0.026, z, 2.55, 0.012, 0.42)
    }

    val edgeLineGeometry = edgeLines.result()
    val edgeLineMaterial = ToonMaterial.celMaterial(0xf2f0ec, curvature)
    val edgeLineMesh     = new Mesh(edgeLineGeometry, edgeLineMaterial)
    ThreeInterop.setUserDataFlag(edgeLineMesh, "noOutline")
    ThreeInterop.addChild(group, edgeLineMesh)

    val stopTexture  = textures.get(ProceduralTextures.TomareRoadKey)
    val stopMaterial = new MeshBasicMaterial(MeshBasicMaterialParameters(map = js.defined(stopTexture)))
    stopMaterial.transparent = true
    stopMaterial.depthWrite = false
    val markings = new GeometryBuilder()
    StopMarkings.foreach { case (laneX, z, yaw) =>
      val marking = new PlaneGeometry(StopMarkingWidth, StopMarkingLength)
      marking.rotateX(-math.Pi / 2.0)
      marking.rotateY(yaw)
      marking.translate(laneX, TerrainHeightmap.groundY(z) + 0.022, z)
      markings.addMerged(marking)
      marking.dispose()
    }
    val stopGeometry = markings.result()
    WorldGeometry.bend(stopGeometry, curvature)
    val stopMesh = new Mesh(stopGeometry, stopMaterial)
    ThreeInterop.setUserDataFlag(stopMesh, "noOutline")
    ThreeInterop.addChild(group, stopMesh)

    val manholeX        = -1.1
    val manholeZ        = 6.2
    val manholeGeometry = new CylinderGeometry(0.32, 0.32, 0.04, 12.0)
    val manholeMaterial = ToonMaterial.celMaterial(0x625f70, curvature)
    val manhole         = new Mesh(manholeGeometry, manholeMaterial)
    manhole.position.set(manholeX, TerrainHeightmap.groundY(manholeZ) + 0.024, manholeZ)
    ThreeInterop.addChild(group, manhole)

    val diamondMaterial = new MeshBasicMaterial()
    diamondMaterial.color.setHex(0xf4f2f6.toDouble)
    val diamondGeometries = List((1.5, 16.5, 1.35), (-1.55, -17.5, 1.35), (1.6, 25.0, 1.3)).map {
      case (x, z, size) =>
        val diamond = new CircleGeometry(size, 4.0)
        diamond.scale(1.0, 1.9, 1.0)
        diamond.rotateX(-math.Pi / 2.0)
        diamond.translate(x, TerrainHeightmap.groundY(z) + 0.022, z)
        WorldGeometry.bend(diamond, curvature)
        val mesh = new Mesh(diamond, diamondMaterial)
        ThreeInterop.setUserDataFlag(mesh, "noOutline")
        ThreeInterop.addChild(group, mesh)
        diamond
    }

    (List(edgeLineGeometry, manholeGeometry, stopGeometry) ++ diamondGeometries,
      List(edgeLineMaterial, manholeMaterial, stopMaterial, diamondMaterial))
  end roadPaint

  /** The crossing corner cluster from upstream `index.js`: the near-field anchor bicycle and its
    * neighbour on the left kerb with the garden wall, planters and the roadside 祟, the convex
    * mirror, the vending-side cones and pedestrian barrier, the kei truck beyond the crossing,
    * and the utility poles with the cable runs that stride the road and the railway.
    */
  private def cornerCluster(
    group: Group[?],
    curvature: PlanetCurvature
  ): (List[BufferGeometry[?, ?]], List[Material[?]]) =
    val stone      = new GeometryBuilder
    val green      = new GeometryBuilder
    val bikeDark   = new GeometryBuilder
    val bikeBright = new GeometryBuilder
    val accent     = new GeometryBuilder
    val bikeColors = List(0x3f6f9c, 0xd8a03c, 0x9c5a4a, 0x4f8f6a)
    val bikeFrames = bikeColors.map(_ => new GeometryBuilder)

    gardenWall(stone)
    waysideShrine(stone, green)
    mirror(stone)
    planters(stone, green)
    bicycle(bikeFrames(0), bikeDark, bikeBright, -4.3, 8.4, math.Pi / 2.0 + 0.08, -0.10)
    bicycle(bikeFrames(1), bikeDark, bikeBright, -4.3, 12.2, -math.Pi / 2.0 + 0.06, 0.08)
    bicycle(bikeFrames(2), bikeDark, bikeBright, 4.3, 13.4, math.Pi / 2.0 + 0.06, 0.08)
    bicycle(bikeFrames(3), bikeDark, bikeBright, 5.7, 15.7, -math.Pi / 2.0 - 0.14, 0.07)
    conesAndBarrier(stone, accent)
    keiTruck(stone)
    guardrail(stone, -3.4, -16.4, 6.2)

    val geometries = List(stone.result(), green.result(), accent.result(), bikeDark.result(),
      bikeBright.result()) ++ bikeFrames.map(_.result())
    val materials = List(
      ToonMaterial.celMaterial(0x8a8794, curvature),
      ToonMaterial.celMaterial(Palette.hillMid, curvature),
      ToonMaterial.celMaterial(Palette.vermilion, curvature),
      ToonMaterial.celMaterial(Palette.warningBlack, curvature),
      ToonMaterial.celMaterial(Palette.metal, curvature)
    ) ++ bikeColors.map(ToonMaterial.celMaterial(_, curvature))
    geometries.lazyZip(materials).foreach { (geometry, material) =>
      ThreeInterop.addChild(group, new Mesh(geometry, material))
    }

    val (poleGeometries, poleMaterials)       = addPolesAndWires(group, curvature)
    val (shelterGeometries, shelterMaterials) = bicycleShelter(group, curvature)
    (geometries ++ poleGeometries ++ shelterGeometries,
      materials ++ poleMaterials ++ shelterMaterials)
  end cornerCluster

  private def bicycleShelter(
    group: Group[?],
    curvature: PlanetCurvature
  ): (List[BufferGeometry[?, ?]], List[Material[?]]) =
    val width    = 4.2
    val depth    = 1.9
    val height   = 2.05
    val fall     = 0.18
    val x        = -4.5
    val z        = 8.5
    val ground   = TerrainHeightmap.groundY(z) + SidewalkHeight
    val tilt     = math.atan2(fall, depth)
    val supports = new GeometryBuilder

    List(-1.0, 1.0).foreach { sx =>
      List(-1.0, 1.0).foreach { sz =>
        val postHeight = height + (if sz > 0.0 then 0.0 else fall)
        supports.addCylinder(sx * (width / 2.0 - 0.12), postHeight / 2.0,
          sz * (depth / 2.0 - 0.1), 0.045, 0.05, postHeight, 7.0)
        supports.addCylinder(sx * (width / 2.0 - 0.12), 0.06,
          sz * (depth / 2.0 - 0.1), 0.1, 0.12, 0.12, 8.0)
      }
    }
    List(-1.0, 1.0).foreach { sx =>
      val beam = new BoxGeometry(0.06, 0.06, depth / math.cos(tilt))
      beam.rotateX(tilt)
      beam.translate(sx * (width / 2.0 - 0.12), height + fall / 2.0 + 0.02, 0.0)
      supports.addMerged(beam)
      beam.dispose()
    }
    supports.addBox(0.0, height + 0.02, depth / 2.0 - 0.08, width, 0.07, 0.09)
    supports.addBox(0.0, height + fall - 0.02, -depth / 2.0 + 0.06, width, 0.09, 0.11)

    val supportGeometry = supports.result()
    val supportMaterial = ToonMaterial.celMaterial(Palette.metalDark, curvature)
    val shelter         = new Group()
    shelter.position.set(x, ground, z)
    shelter.rotation.y = math.Pi / 2.0
    shelter.add(new Mesh(supportGeometry, supportMaterial))

    val ribs     = new GeometryBuilder
    val ribCount = math.round(width / 0.24).toInt
    (0 until ribCount).foreach { index =>
      val rib = new BoxGeometry(0.05, 0.05, depth / math.cos(tilt) + 0.16)
      rib.rotateX(tilt)
      rib.translate(-width / 2.0 + 0.1 + index * ((width - 0.2) / (ribCount - 1)),
        height + fall / 2.0 + 0.09, 0.0)
      ribs.addMerged(rib)
      rib.dispose()
    }
    val ribGeometry = ribs.result()
    val ribMaterial = ToonMaterial.celMaterial(Palette.metal, curvature)
    shelter.add(new Mesh(ribGeometry, ribMaterial))
    ThreeInterop.addChild(group, shelter)

    val roofGeometry = new BoxGeometry(width + 0.16, 0.03, depth / math.cos(tilt) + 0.2)
    roofGeometry.rotateX(tilt)
    roofGeometry.rotateY(math.Pi / 2.0)
    roofGeometry.translate(x, ground + height + fall / 2.0 + 0.07, z)
    WorldGeometry.bend(roofGeometry, curvature)
    val roofMaterial = new MeshBasicMaterial()
    roofMaterial.color.setHex(Palette.bicycleShelterSheet.toDouble)
    roofMaterial.transparent = true
    roofMaterial.opacity = 0.58
    roofMaterial.depthWrite = false
    ThreeInterop.addChild(group, new Mesh(roofGeometry, roofMaterial))

    (List(supportGeometry, ribGeometry, roofGeometry),
      List(supportMaterial, ribMaterial, roofMaterial))
  end bicycleShelter

  /** The low garden wall on the left kerb that doubles as the foreground edge of the shot. */
  private def gardenWall(builder: GeometryBuilder): Unit =
    val x      = -5.05
    val zm     = 8.9
    val ground = TerrainHeightmap.groundY(zm)
    builder.addBox(x, ground + 0.31, zm, 0.2, 0.62, 3.4)
    builder.addBox(x, ground + 0.65, zm, 0.26, 0.06, 3.5)

  /** The roadside 祠: stone plinth and the little seated figure. */
  private def waysideShrine(builder: GeometryBuilder, green: GeometryBuilder): Unit =
    val x = -5.45
    val z = 6.7
    val y = TerrainHeightmap.groundY(z)
    builder.addBox(x, y + 0.08, z, 1.05, 0.16, 0.85)
    builder.addBox(x, y + 0.31, z, 0.85, 0.3, 0.68)
    builder.addBox(x, y + 0.48, z, 0.9, 0.05, 0.72)
    green.addCylinder(x, y + 0.7, z, 0.115, 0.16, 0.4, 10.0)

  /** The convex mirror watching the crossing from the near kerb. */
  private def mirror(builder: GeometryBuilder): Unit =
    val x = -3.62
    val z = 3.72
    val y = TerrainHeightmap.groundY(z) + SidewalkHeight
    builder.addCylinder(x, y + 1.275, z, 0.055, 0.07, 2.55, 8.0)
    builder.addCylinder(x, y + 0.08, z, 0.14, 0.16, 0.16, 8.0)
    val rim = new CylinderGeometry(0.46, 0.46, 0.1, 20.0)
    rim.rotateX(math.Pi / 2.0)
    rim.translate(x, y + 2.45, z + 0.06)
    builder.addMerged(rim)
    rim.dispose()

  private def planters(builder: GeometryBuilder, green: GeometryBuilder): Unit =
    List((-4.6, 7.9, 0.22), (-4.5, 8.6, 0.18), (-4.7, 12.6, 0.2)).foreach {
      case (x, z, radius) =>
        val y = TerrainHeightmap.groundY(z) + SidewalkHeight
        builder.addCylinder(x, y + 0.14, z, radius, radius * 1.08, 0.28, 10.0)
        green.addBox(x, y + 0.36, z, radius * 1.5, 0.22, radius * 1.5)
    }

  private def bicycle(
    frame: GeometryBuilder,
    dark: GeometryBuilder,
    bright: GeometryBuilder,
    x: Double,
    z: Double,
    yaw: Double,
    lean: Double
  ): Unit =
    val ground = TerrainHeightmap.groundY(z) + SidewalkHeight
    val radius = 0.33

    def world(lx: Double, ly: Double, lz: Double = 0.0): (Double, Double, Double) =
      val rolledY = ly * math.cos(lean) - lz * math.sin(lean)
      val rolledZ = ly * math.sin(lean) + lz * math.cos(lean)
      (x + lx * math.cos(yaw) + rolledZ * math.sin(yaw), ground + rolledY,
        z - lx * math.sin(yaw) + rolledZ * math.cos(yaw))

    def addGeometry(
      builder: GeometryBuilder,
      geometry: BufferGeometry[?, ?],
      lx: Double,
      ly: Double,
      lz: Double = 0.0
    ): Unit =
      geometry.rotateX(lean)
      geometry.rotateY(yaw)
      val (wx, wy, wz) = world(lx, ly, lz)
      geometry.translate(wx, wy, wz)
      builder.addMerged(geometry)
      geometry.dispose()

    def tube(
      builder: GeometryBuilder,
      a: (Double, Double, Double),
      b: (Double, Double, Double),
      thickness: Double
    ): Unit =
      val (x0, y0, z0) = world(a._1, a._2, a._3)
      val (x1, y1, z1) = world(b._1, b._2, b._3)
      builder.addBeam(x0, y0, z0, x1, y1, z1, thickness)

    val rear          = (-0.52, 0.348, 0.0)
    val front         = (0.55, 0.33, 0.0)
    val bottomBracket = (-0.10, 0.28, 0.0)
    val seatCluster   = (-0.27, 0.86, 0.0)
    val headBottom    = (0.44, 0.60, 0.0)
    val headTop       = (0.49, 0.86, 0.0)
    val handlebar     = (0.46, 0.97, 0.0)
    val saddle        = (-0.31, 1.0, 0.0)

    List(rear, front).foreach { (hubX, hubY, hubZ) =>
      val tire = new TorusGeometry(radius, 0.021, 5.0, 18.0)
      addGeometry(dark, tire, hubX, hubY, hubZ)
      val hub = new CylinderGeometry(0.035, 0.035, 0.085, 6.0)
      hub.rotateX(math.Pi / 2.0)
      addGeometry(dark, hub, hubX, hubY, hubZ)
      val mudguard = new TorusGeometry(radius + 0.055, 0.018, 4.0, 10.0, math.Pi * 0.78)
      mudguard.rotateZ(math.Pi * 0.11)
      addGeometry(frame, mudguard, hubX, hubY, hubZ)
    }

    tube(frame, bottomBracket, seatCluster, 0.026)
    tube(frame, bottomBracket, headBottom, 0.028)
    tube(frame, headBottom, headTop, 0.030)
    tube(frame, seatCluster, headTop, 0.024)
    List(-1.0, 1.0).foreach { side =>
      tube(frame, bottomBracket, (rear._1, rear._2, side * 0.052), 0.019)
      tube(frame, seatCluster, (rear._1, rear._2, side * 0.052), 0.017)
      tube(bright, (headBottom._1, headBottom._2, side * 0.045), (front._1, front._2, side * 0.05), 0.017)
    }

    tube(bright, seatCluster, saddle, 0.019)
    val saddleGeometry = new BoxGeometry(0.23, 0.055, 0.12)
    addGeometry(dark, saddleGeometry, saddle._1 - 0.02, saddle._2 + 0.04)
    tube(bright, headTop, handlebar, 0.021)
    val bar = new CylinderGeometry(0.019, 0.019, 0.54, 6.0)
    bar.rotateX(math.Pi / 2.0)
    addGeometry(bright, bar, handlebar._1, handlebar._2, handlebar._3)
    List(-1.0, 1.0).foreach { side =>
      val grip = new CylinderGeometry(0.023, 0.023, 0.1, 6.0)
      grip.rotateX(math.Pi / 2.0)
      addGeometry(dark, grip, handlebar._1, handlebar._2, side * 0.22)
    }
    val bell = new CylinderGeometry(0.028, 0.028, 0.03, 8.0)
    addGeometry(dark, bell, handlebar._1 - 0.05, handlebar._2 + 0.03, -0.12)

    val chainring = new CylinderGeometry(0.075, 0.075, 0.012, 12.0)
    chainring.rotateX(math.Pi / 2.0)
    addGeometry(dark, chainring, bottomBracket._1, bottomBracket._2, 0.0)
    List(-1.0, 1.0).foreach { side =>
      tube(bright, bottomBracket, (bottomBracket._1 + side * 0.02,
          bottomBracket._2 - side * 0.15, side * 0.075), 0.013)
      val pedal = new BoxGeometry(0.09, 0.02, 0.05)
      addGeometry(dark, pedal, bottomBracket._1 + side * 0.02,
        bottomBracket._2 - side * 0.15, side * 0.105)
    }
    tube(bright, (-0.16, 0.26, 0.06), (-0.30, 0.015, 0.13), 0.014)

    val rackY = radius + 0.43
    val rack  = new BoxGeometry(0.29, 0.022, 0.15)
    addGeometry(bright, rack, rear._1 + 0.02, rackY)
    List(-1.0, 1.0).foreach { side =>
      tube(bright, (rear._1 + 0.13, rackY, side * 0.068), (seatCluster._1 + 0.03, seatCluster._2 - 0.09, side * 0.03),
        0.011)
      tube(bright, (rear._1 - 0.11, rackY, side * 0.068), (rear._1 + 0.02, rear._2 + 0.04, side * 0.055), 0.011)
    }

    val basket = new CylinderGeometry(0.16, 0.12, 0.2, 8.0, 1.0, true)
    addGeometry(bright, basket, 0.69, 0.87, 0.0)
    val basketFloor = new CylinderGeometry(0.12, 0.12, 0.016, 8.0)
    addGeometry(bright, basketFloor, 0.69, 0.775, 0.0)
    val basketRim = new TorusGeometry(0.16, 0.012, 4.0, 10.0)
    basketRim.rotateX(math.Pi / 2.0)
    addGeometry(bright, basketRim, 0.69, 0.97, 0.0)
    tube(bright, (0.57, 0.95, 0.0), (handlebar._1 + 0.02, handlebar._2 - 0.03, 0.0), 0.012)
    tube(bright, (0.64, 0.77, 0.0), (headBottom._1 + 0.02, headBottom._2 + 0.02, 0.0), 0.012)
  end bicycle

  /** Cones at the broken kerb and the red-and-white pedestrian barrier beside them. */
  private def conesAndBarrier(builder: GeometryBuilder, accent: GeometryBuilder): Unit =
    List((2.62, 4.15), (2.42, 5.05), (-2.3, -4.3)).foreach { case (x, z) =>
      val y = TerrainHeightmap.groundY(z)
      builder.addBox(x, y + 0.018, z, 0.34, 0.035, 0.34)
      accent.addCone(x, y + 0.32, z, 0.13, 0.6, 10.0)
      builder.addCylinder(x, y + 0.36, z, 0.088, 0.105, 0.09, 10.0)
    }

    val barrierX = 2.62
    val barrierZ = 6.2
    val barrierY = TerrainHeightmap.groundY(barrierZ)
    accent.addBox(barrierX, barrierY + 0.86, barrierZ, 1.7, 0.09, 0.05)
    builder.addBox(barrierX, barrierY + 0.62, barrierZ, 1.6, 0.07, 0.04)
    List(-0.85, 0.85).foreach { offset =>
      builder.addCylinder(barrierX + offset, barrierY + 0.5, barrierZ, 0.045, 0.05, 1.0, 8.0)
      List(0.34, 0.72).foreach { height =>
        accent.addCylinder(barrierX + offset, barrierY + height, barrierZ, 0.052, 0.052, 0.2, 8.0)
      }
    }

  /** The white kei truck parked on the far-side kerb, nose along the street. */
  private def keiTruck(builder: GeometryBuilder): Unit =
    val x = -2.02
    val z = -7.4
    val y = TerrainHeightmap.groundY(z)
    builder.addBox(x, y + 0.62, z, 1.46, 0.5, 3.32)
    builder.addBox(x, y + 1.12, z + 0.85, 1.4, 0.72, 1.3)
    builder.addBox(x, y + 1.5, z + 0.85, 1.3, 0.18, 1.1)
    builder.addBox(x, y + 0.2, z, 1.3, 0.4, 3.0)

  /** One guardrail run: posts and a single rail, down the far kerb. */
  private def guardrail(builder: GeometryBuilder, x: Double, z: Double, length: Double): Unit =
    val spans = math.round(length / 2.0).toInt
    (0 to spans).foreach { step =>
      val sz = z - length / 2.0 + step * 2.0
      builder.addBox(x, TerrainHeightmap.groundY(sz) + 0.36, sz, 0.1, 0.72, 0.1)
    }
    builder.addBox(x, TerrainHeightmap.groundY(z) + 0.68, z, 0.06, 0.16, length)

  /** The utility poles that carry the crossing's overhead cabling, and the sagging wire runs
    * between them — the left-hand street chain, the cables striding over the railway, and the
    * service drop into the far-side house.
    */
  private def addPolesAndWires(
    group: Group[?],
    curvature: PlanetCurvature
  ): (List[BufferGeometry[?, ?]], List[Material[?]]) =
    val poles = List(
      (-3.86, 4.55, 9.4, true), (-4.35, 14.2, 9.0, false), (3.98, -6.6, 9.2, true), (12.5, 3.7, 8.4, false),
      (-16.5, -4.4, 8.6, false)
    )

    def baseOf(x: Double, z: Double): Double =
      val onWalk = math.abs(x) > RoadHalfWidth && math.abs(x) < RoadHalfWidth + SidewalkWidth
      TerrainHeightmap.groundY(z) + (if onWalk then SidewalkHeight else 0.0)

    val poleParts = new GeometryBuilder
    val darkParts = new GeometryBuilder
    val warmParts = new GeometryBuilder

    poles.foreach { (x, z, height, lamp) =>
      val y = baseOf(x, z)
      poleParts.addCylinder(x, y + height / 2.0, z, 0.11, 0.19, height, 8.0)
      poleParts.addCylinder(x, y + 0.11, z, 0.24, 0.28, 0.22, 8.0)
      List(height - 0.55, height - 1.5).zipWithIndex.foreach { (armY, armIndex) =>
        val armLength = if armIndex == 0 then 2.1 else 1.7
        darkParts.addBox(x, y + armY, z, 0.09, 0.1, armLength)
        darkParts.addBox(x, y + armY - 0.3, z, 0.06, 0.5, 0.06)
        (-1 to 1).foreach { side =>
          if !(side == 0 && armIndex == 1) then
            poleParts.addCylinder(x, y + armY + 0.13, z + side * armLength / 2.4, 0.06, 0.075,
              0.16, 7.0)
        }
      }
      if lamp then
        val towardRoad = if x < 0.0 then 1.0 else -1.0
        darkParts.addBox(x + towardRoad * 0.75, y + height - 0.2, z, 1.5, 0.08, 0.08)
        warmParts.addBox(x + towardRoad * 1.4, y + height - 0.34, z, 0.5, 0.22, 0.28)
    }

    val wires = new GeometryBuilder
    def wireTop(index: Int, dy: Double, dz: Double): (Double, Double, Double) =
      val (x, z, height, _) = poles(index)
      (x, baseOf(x, z) + height - 0.6 + dy, z + dz)

    def wireRun(a: (Double, Double, Double), b: (Double, Double, Double), sag: Double): Unit =
      val segments = 4
      (0 until segments).foreach { segment =>
        def pointAt(t: Double): (Double, Double, Double) =
          val dip = sag * 4.0 * t * (1.0 - t)
          (a._1 + (b._1 - a._1) * t, a._2 + (b._2 - a._2) * t - dip, a._3 + (b._3 - a._3) * t)
        val p0 = pointAt(segment.toDouble / segments)
        val p1 = pointAt((segment + 1).toDouble / segments)
        wires.addBeam(p0._1, p0._2, p0._3, p1._1, p1._2, p1._3, 0.026)
      }

    List((0.0, -0.7), (-0.42, 0.0), (-0.86, 0.7)).foreach { (dy, dz) =>
      wireRun(wireTop(0, dy, dz), wireTop(1, dy, dz), 0.55)
    }
    List((-0.15, 0.2), (-0.62, -0.3)).foreach { (dy, dz) =>
      wireRun(wireTop(0, dy, dz), wireTop(2, dy, dz), 0.55)
    }
    wireRun(wireTop(4, -1.1, 0.4), wireTop(1, -1.1, 0.4), 0.55)
    wireRun(wireTop(3, -1.0, 0.2), wireTop(2, -1.0, 0.2), 0.55)
    wireRun(wireTop(2, -1.9, 0.0), (5.6, TerrainHeightmap.groundY(-9.0) + 5.2, -9.0), 0.25)

    val geometries = List(poleParts.result(), darkParts.result(), warmParts.result(), wires.result())
    val materials = List(
      ToonMaterial.celMaterial(0xb8b4ba, curvature),
      ToonMaterial.celMaterial(0x4c4a58, curvature),
      ToonMaterial.flatMaterial(0xfff2d4, curvature),
      ToonMaterial.celMaterial(0x35323d, curvature)
    )
    geometries.lazyZip(materials).foreach { (geometry, material) =>
      ThreeInterop.addChild(group, new Mesh(geometry, material))
    }
    (geometries, materials)
  end addPolesAndWires
end CrossingStreetscape
