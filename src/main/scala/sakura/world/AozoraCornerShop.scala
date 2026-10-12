package sakura.world

import sakura.{
  MaterialTexture, Palette, PlanetCurvature, ProceduralTextures, TerrainHeightmap, TextureCache, ThreeInterop,
  ToonMaterial
}
import sakura.foliage.{PlannedTransform, SceneBatch}
import vendor.three.*

import scala.math
import scala.scalajs.js

/** 青空商店 — the corner shop beside the crossing, ported from upstream `shop.js`: a cream
  * two-storey volume on the east frontage with the red scalloped awning running past the vending
  * bay to the corner, the half-lowered shutter, the tiled base, the upstairs balcony with its
  * laundry, and the two vending machines and postbox that anchor the right of the hero frame.
  */
object AozoraCornerShop:
  private val ShopNearZ     = 4.55
  private val ShopFarZ      = 12.6
  private val ShopDepth     = 7.2
  private val ShopSide      = 1.0
  private val AwningRed     = Palette.vermilion
  private val AwningRedSoft = 0xd8564e

  private[sakura] val ShopFrontX: Double = ShopSide * CrossingStreetscape.ShopFrontX
  private val MachineWhite               = 0xf0eee9
  private val MachineTeal                = 0x2e9a98
  private val MachineDark                = 0x28313c
  private val MetalGray                  = 0x777985

  private val H1 = 3.15
  private val H2 = 2.75

  def build(curvature: PlanetCurvature, textures: TextureCache): SceneAssembly =
    val group  = new Group()
    val frontX = ShopFrontX
    val zMid   = (ShopNearZ + ShopFarZ) / 2.0
    val ground = TerrainHeightmap.groundY(zMid)
    val walk   = TerrainHeightmap.groundY(zMid) + CrossingStreetscape.SidewalkHeight

    val volumes = volume(frontX, zMid, ground, curvature)
    volumes.foreach(volume => ThreeInterop.addChild(group, volume.node))
    val shutter = shopfront(frontX, ground, curvature)
    shutter.foreach(part => ThreeInterop.addChild(group, part.node))
    val awningParts = awning(frontX, ground, curvature)
    awningParts.foreach(part => ThreeInterop.addChild(group, part.node))
    val upstairsParts = upstairs(frontX, ground, curvature)
    upstairsParts.foreach(part => ThreeInterop.addChild(group, part.node))
    val machines = vendingBay(frontX, textures, curvature)
    machines.foreach(machine => ThreeInterop.addChild(group, machine.node))
    val postbox = postBox(curvature)
    postbox.foreach(part => ThreeInterop.addChild(group, part.node))
    val servicesParts = services(frontX, ground, textures, curvature)
    servicesParts.foreach(part => ThreeInterop.addChild(group, part.node))
    val clutterParts = pavementClutter(frontX, ground, curvature)
    clutterParts.foreach(part => ThreeInterop.addChild(group, part.node))

    val (signMesh, signGeometry, signMaterial) = signBoard(frontX, ground, zMid, textures, curvature)
    ThreeInterop.addChild(group, signMesh)

    SceneAssembly(
      group,
      volumes.flatMap(_.assemblyGeometries) ++ shutter.flatMap(_.assemblyGeometries) ++
        awningParts.flatMap(_.assemblyGeometries) ++ upstairsParts.flatMap(_.assemblyGeometries) ++
        machines.flatMap(_.assemblyGeometries) ++ postbox.flatMap(_.assemblyGeometries) ++
        servicesParts.flatMap(_.assemblyGeometries) ++ clutterParts.flatMap(_.assemblyGeometries) :+ signGeometry,
      volumes.flatMap(_.assemblyMaterials) ++ shutter.flatMap(_.assemblyMaterials) ++
        awningParts.flatMap(_.assemblyMaterials) ++ upstairsParts.flatMap(_.assemblyMaterials) ++
        machines.flatMap(_.assemblyMaterials) ++ postbox.flatMap(_.assemblyMaterials) ++
        servicesParts.flatMap(_.assemblyMaterials) ++ clutterParts.flatMap(_.assemblyMaterials) :+ signMaterial
    )
  end build

  /** One mesh group plus the geometries and materials it contributed, so the host can dispose
    * everything the shop created.
    */
  final private case class PartMesh(
    node: Object3D[?],
    assemblyGeometries: List[BufferGeometry[?, ?]],
    assemblyMaterials: List[Material[?]]
  )

  private def part(builder: GeometryBuilder, material: Material[?]): PartMesh =
    val geometry = builder.result()
    PartMesh(new Mesh(geometry, material), List(geometry), List(material))

  /** The main volume: cream ground floor, set-back upper storey, parapet and string course, and
    * the tiled base along the frontage.
    */
  private def volume(frontX: Double, zMid: Double, ground: Double, curvature: PlanetCurvature): List[PartMesh] =
    val walls = new GeometryBuilder
    walls.addBox(frontX + ShopDepth / 2.0, ground + H1 / 2.0, zMid, ShopDepth, H1, ShopFarZ - ShopNearZ)
    walls.addBox(frontX + 0.35 + (ShopDepth - 0.5) / 2.0, ground + H1 + H2 / 2.0, zMid,
      ShopDepth - 0.5, H2, ShopFarZ - ShopNearZ - 0.3)
    val wallGeometry = walls.result()
    val (wallGroup, wallOutlineGeometries, wallMaterials) =
      ToonMaterial.celMeshWithOutline(wallGeometry, 0xf1dfb9, 0.0038, curvature)

    val roofs = new GeometryBuilder
    roofs.addBox(frontX + 0.4 + (ShopDepth - 0.5) / 2.0, ground + H1 + H2 + 0.11, zMid,
      ShopDepth - 0.4, 0.22, ShopFarZ - ShopNearZ - 0.2)
    roofs.addBox(frontX + ShopDepth / 2.0, ground + H1 + 0.05, zMid, ShopDepth + 0.06, 0.18,
      ShopFarZ - ShopNearZ + 0.06)
    val roofMesh = part(roofs, ToonMaterial.celMaterial(0x484756, curvature))

    val tile = new GeometryBuilder
    tile.addBox(frontX - 0.04, ground + 0.275, zMid, 0.1, 0.55, ShopFarZ - ShopNearZ)
    val tileMesh = part(tile, ToonMaterial.celMaterial(0xe9edf0, curvature))

    PartMesh(wallGroup, wallGeometry :: wallOutlineGeometries, wallMaterials) :: roofMesh :: tileMesh :: Nil
  end volume

  /** The half-lowered shutter over the doorway and the shopfront glazing with its glimpse of
    * interior shelving.
    */
  private def shopfront(frontX: Double, ground: Double, curvature: PlanetCurvature): List[PartMesh] =
    val shutterZ = ShopNearZ + 4.35
    val frames   = new GeometryBuilder
    frames.addBox(frontX - 0.02, ground + H1 - 0.22, shutterZ, 0.18, 0.34, 3.3)
    List(-1.0, 1.0).foreach { side =>
      frames.addBox(frontX - 0.03, ground + (H1 - 0.4) / 2.0, shutterZ + side * 1.57, 0.2, H1 - 0.4,
        0.14)
    }
    val glassZ = ShopFarZ - 1.15
    frames.addBox(frontX - 0.05, ground + 0.45 + (H1 - 0.9) / 2.0, glassZ, 0.12, H1 - 0.9, 1.9)
    List(-1.0, 0.0, 1.0).foreach { dz =>
      frames.addBox(frontX - 0.06, ground + 0.45 + (H1 - 0.9) / 2.0, glassZ + dz, 0.16, H1 - 0.85,
        0.09)
    }
    frames.addBox(frontX - 0.06, ground + 0.22, glassZ, 0.2, 0.45, 2.0)
    frames.addBox(frontX - 0.06, ground + H1 - 0.5, glassZ, 0.2, 0.16, 2.0)
    val frameMesh = part(frames, ToonMaterial.celMaterial(MetalGray, curvature))

    val interior = new GeometryBuilder
    interior.addBox(frontX + 0.3, ground + (H1 - 0.5) / 2.0, shutterZ, 0.5, H1 - 0.5, 3.0)
    interior.addBox(frontX + 0.35, ground + 0.5 + (H1 - 1.1) / 2.0, glassZ, 0.5, H1 - 1.1, 1.6)
    (0 until 3).foreach { shelf =>
      interior.addBox(frontX + 0.2, ground + 0.8 + shelf * 0.55, glassZ, 0.3, 0.08, 1.4)
    }
    val interiorMesh = part(interior, ToonMaterial.celMaterial(0xe8dfc8, curvature))

    val slats = new GeometryBuilder
    (0 until 15).foreach { row =>
      slats.addBox(frontX - 0.055, ground + 0.15 + row * 0.16, shutterZ, 0.07, 0.07, 3.0)
    }
    slats.addBox(frontX - 0.055, ground + 0.05, shutterZ, 0.11, 0.11, 3.02)
    val slatMesh = part(slats, ToonMaterial.celMaterial(0xbab7c1, curvature))

    frameMesh :: interiorMesh :: slatMesh :: Nil
  end shopfront

  /** The red scalloped awning: a stepped slope from the fascia past the vending bay to the
    * corner, its hem bar, scallop flaps and support arms.
    */
  private def awning(frontX: Double, ground: Double, curvature: PlanetCurvature): List[PartMesh] =
    val zA    = ShopNearZ + 2.95
    val zB    = ShopFarZ + 0.05
    val out   = 1.85
    val drop  = 0.42
    val yA    = ground + H1 - 0.18
    val slope = math.atan2(drop, out)
    val zMid  = (zA + zB) / 2.0

    val red     = new GeometryBuilder
    val soft    = new GeometryBuilder
    val stripes = 18
    (0 until stripes).foreach { stripe =>
      val along   = (stripe + 0.5) * out / stripes
      val x       = frontX - along * math.cos(slope)
      val y       = yA - along * math.sin(slope) + 0.035
      val builder = if stripe % 2 == 0 then red else soft
      builder.addBox(x, y, zMid, out / stripes + 0.05, 0.07, zB - zA)
    }
    val edgeX = frontX - out * math.cos(slope)
    val edgeY = yA - out * math.sin(slope)
    red.addBox(edgeX + 0.03, edgeY - 0.15, zMid, 0.08, 0.34, zB - zA)

    val redGeometry = red.result()
    val (redGroup, redOutlineGeometries, redMaterials) =
      ToonMaterial.celMeshWithOutline(redGeometry, AwningRed, 0.0038, curvature)
    val softMesh = part(soft, ToonMaterial.celMaterial(AwningRedSoft, curvature))

    val scallops        = math.round((zB - zA) / 0.55).toInt
    val scallopGeometry = new SphereGeometry(0.16, 10.0, 8.0)
    val scallopMaterial = ToonMaterial.instancedCelMaterial(AwningRed, curvature)
    val scallopTransforms = (0 until scallops).map { index =>
      PlannedTransform(
        position = (edgeX - 0.01, edgeY - 0.32, zA + (zB - zA) * (index + 0.5) / scallops),
        axis = (0.0, 0.0, 1.0),
        angle = math.Pi / 2.0,
        scale = (1.0, 1.0, 1.0)
      )
    }
    val scallopBatch = SceneBatch.instancedMesh(scallopGeometry, scallopMaterial, scallopTransforms)

    val arms = new GeometryBuilder
    List(zA + 0.5, zMid, zB - 0.5).foreach { z =>
      arms.addBox(frontX - out / 2.0 * math.cos(slope), yA - out / 2.0 * math.sin(slope) - 0.08, z,
        out * 1.02, 0.07, 0.07)
      arms.addBox(frontX - 0.14, yA - 0.4, z, 0.06, 0.78, 0.06)
    }
    val armMesh = part(arms, ToonMaterial.celMaterial(0x4c4a58, curvature))

    PartMesh(redGroup, redGeometry :: redOutlineGeometries, redMaterials) :: softMesh ::
      PartMesh(scallopBatch, List(scallopGeometry), List(scallopMaterial)) :: armMesh :: Nil
  end awning

  /** The upper storey: three windows, the cantilevered balcony over the middle one and the
    * laundry hung out to dry.
    */
  private def upstairs(frontX: Double, ground: Double, curvature: PlanetCurvature): List[PartMesh] =
    val xWall  = frontX + 0.35
    val frames = new GeometryBuilder
    val glass  = new GeometryBuilder
    List(ShopNearZ + 2.0, ShopNearZ + 4.6, ShopFarZ - 1.1).foreach { z =>
      frames.addBox(xWall + 0.05, ground + H1 + 1.38, z, 0.16, 1.44, 1.66)
      glass.addBox(xWall - 0.035, ground + H1 + 1.38, z, 0.05, 1.26, 1.5)
      frames.addBox(xWall - 0.07, ground + H1 + 1.38, z, 0.05, 1.26, 0.07)
      frames.addBox(xWall, ground + H1 + 0.6, z, 0.2, 0.09, 1.74)
    }
    val frameMesh = part(frames, ToonMaterial.celMaterial(MetalGray, curvature))
    val glassMesh = part(glass, ToonMaterial.flatMaterial(0x566778, curvature))

    val balcony = new GeometryBuilder
    val bz      = ShopNearZ + 4.6
    val bx      = frontX + 0.02
    balcony.addBox(bx + 0.18, ground + H1 + 0.62, bz, 0.42, 0.09, 2.2)
    List(0.44, 0.9).foreach { rise =>
      balcony.addBox(bx, ground + H1 + 0.62 + rise, bz, 0.06, 0.06, 2.2)
    }
    (0 to 9).foreach { index =>
      balcony.addBox(bx, ground + H1 + 0.9, bz - 1.05 + index * 0.233, 0.04, 0.52, 0.04)
    }
    balcony.addBox(bx + 0.05, ground + H1 + 1.62, bz, 0.04, 0.04, 1.9)
    val balconyMesh = part(balcony, ToonMaterial.celMaterial(0x4c4a58, curvature))

    val laundry = new GeometryBuilder
    val towels  = List(0.6, 0.48, 0.54)
    towels.zipWithIndex.foreach { case (height, index) =>
      laundry.addBox(bx + 0.05, ground + H1 + 1.6 - height / 2.0, bz - 0.62 + index * 0.62,
        0.03, height, 0.3)
    }
    val laundryMesh = part(laundry, ToonMaterial.celMaterial(Palette.plaster, curvature))

    frameMesh :: glassMesh :: balconyMesh :: laundryMesh :: Nil
  end upstairs

  /** The vending bay: そら茶 in white and なごみ in teal, facing west onto the sidewalk, with
    * their lit header panels and the can rows behind the glass.
    */
  private def vendingBay(frontX: Double, textures: TextureCache, curvature: PlanetCurvature): List[PartMesh] =
    val machineX = frontX - 0.42
    val spots = List((ShopNearZ + 1.0, MachineWhite, ProceduralTextures.SoraTeaHeaderKey),
      (ShopNearZ + 2.25, MachineTeal, ProceduralTextures.NagomiHeaderKey))

    val white = new GeometryBuilder
    val teal  = new GeometryBuilder
    val dark  = new GeometryBuilder
    val cans  = new GeometryBuilder
    spots.foreach { (z, _, _) =>
      val walkY = TerrainHeightmap.groundY(z) + CrossingStreetscape.SidewalkHeight
      val body  = if z == spots.head._1 then white else teal
      body.addBox(machineX, walkY + 0.98, z, 0.72, 1.96, 1.06)
      dark.addBox(machineX - 0.37, walkY + 1.18, z, 0.045, 0.92, 0.84)
      dark.addBox(machineX - 0.4, walkY + 0.57, z + 0.29, 0.045, 0.34, 0.25)
      dark.addBox(machineX - 0.4, walkY + 0.25, z, 0.045, 0.16, 0.54)
      for row <- 0 until 4; column <- 0 until 3 do
        cans.addBox(machineX - 0.395, walkY + 0.72 + row * 0.22, z + (column - 1) * 0.23,
          0.05, 0.16, 0.09)
    }
    val whiteMesh = part(white, ToonMaterial.celMaterial(MachineWhite, curvature))
    val tealMesh  = part(teal, ToonMaterial.celMaterial(MachineTeal, curvature))
    val darkMesh  = part(dark, ToonMaterial.flatMaterial(MachineDark, curvature))

    val headers = spots.map { (z, _, key) =>
      headerPlane(machineX - 0.375, z, textures.get(key), curvature)
    }
    val canTexture = textures.get(ProceduralTextures.VendingCanKey)
    val canMesh = part(cans,
      new MeshBasicMaterial(MeshBasicMaterialParameters(
        map = js.defined(canTexture)
      )))

    whiteMesh :: tealMesh :: darkMesh :: canMesh :: headers
  end vendingBay

  /** A vending machine's lit header panel facing the street. */
  private def headerPlane(x: Double, z: Double, texture: MaterialTexture, curvature: PlanetCurvature): PartMesh =
    val walkY    = TerrainHeightmap.groundY(z) + CrossingStreetscape.SidewalkHeight
    val geometry = new PlaneGeometry(0.98, 0.28)
    geometry.rotateY(-math.Pi / 2.0)
    geometry.translate(x, walkY + 1.73, z)
    WorldGeometry.bend(geometry, curvature)
    val material = new MeshBasicMaterial(MeshBasicMaterialParameters(map = js.defined(texture)))
    PartMesh(new Mesh(geometry, material), List(geometry), List(material))

  /** The red cylindrical post box on the shop side of the crossing. */
  private def postBox(curvature: PlanetCurvature): List[PartMesh] =
    val x      = 3.62
    val z      = 4.55
    val ground = TerrainHeightmap.groundY(z) + CrossingStreetscape.SidewalkHeight

    val base = new GeometryBuilder
    base.addCylinder(x, ground + 0.08, z, 0.28, 0.3, 0.16, 12.0)
    val baseMesh = part(base, ToonMaterial.celMaterial(0x85818d, curvature))

    val body = new CylinderGeometry(0.24, 0.24, 1.15, 14.0)
    body.translate(x, ground + 0.74, z)
    val (bodyGroup, bodyOutlineGeometries, bodyMaterials) =
      ToonMaterial.celMeshWithOutline(body, 0xb02c28, 0.0038, curvature)

    val details = new GeometryBuilder
    details.addBox(x, ground + 1.3, z, 0.52, 0.1, 0.52)
    details.addBox(x, ground + 1.14, z + 0.21, 0.3, 0.07, 0.06)
    details.addBox(x, ground + 1.15, z + 0.245, 0.16, 0.014, 0.012)
    details.addBox(x, ground + 0.62, z + 0.22, 0.26, 0.34, 0.04)
    details.addBox(x, ground + 0.42, z + 0.235, 0.16, 0.1, 0.03)
    val detailMesh = part(details, ToonMaterial.celMaterial(0x302a31, curvature))

    PartMesh(bodyGroup, body :: bodyOutlineGeometries, bodyMaterials) :: baseMesh :: detailMesh :: Nil
  end postBox

  /** Wall service clutter: AC, pipes, meter, taped notices, the hanging banner and the gable ladder. */
  private def services(
    frontX: Double,
    ground: Double,
    textures: TextureCache,
    curvature: PlanetCurvature
  ): List[PartMesh] =
    val units = new GeometryBuilder
    units.addBox(frontX + 0.3, ground + H1 + 0.45, ShopFarZ - 0.75, 0.62, 0.62, 0.95)
    units.addCylinder(frontX - 0.02, ground + H1 + 0.45, ShopFarZ - 0.75, 0.22, 0.22, 0.06, 12.0)
    val unitMesh = part(units, ToonMaterial.celMaterial(0xe4e2e6, curvature))

    val pipes = new GeometryBuilder
    List((ShopFarZ - 0.2, H1), (ShopNearZ + 0.35, H1 + 1.2)).foreach { (z, height) =>
      pipes.addCylinder(frontX - 0.06, ground + height / 2.0, z, 0.05, 0.05, height, 8.0)
      (0 until math.floor(height / 1.1).toInt).foreach { bracket =>
        pipes.addBox(frontX - 0.04, ground + 0.6 + bracket * 1.1, z, 0.1, 0.06, 0.1)
      }
    }
    pipes.addBox(frontX - 0.06, ground + 1.75, ShopNearZ + 0.75, 0.14, 0.5, 0.36)
    val pipeMesh = part(pipes, ToonMaterial.celMaterial(MetalGray, curvature))

    val posters = List(
      (ProceduralTextures.AozoraSignKey, ShopNearZ + 2.55, ground + 1.92),
      (ProceduralTextures.SoraTeaHeaderKey, ShopNearZ + 3.03, ground + 1.80),
      (ProceduralTextures.NagomiHeaderKey, ShopNearZ + 3.51, ground + 1.92)
    ).map { (key, z, y) =>
      texturedPanel(textures.get(key), frontX - 0.055, y, z, 0.42, 0.58, -math.Pi / 2.0, curvature)
    }

    val banner = texturedPanel(textures.get(ProceduralTextures.AozoraSignKey),
      frontX - 1.6, ground + H1 - 1.35, ShopNearZ + 0.55, 0.42, 1.7, -math.Pi / 2.0, curvature)
    val warning = texturedPanel(textures.get(ProceduralTextures.UtilityWarningKey),
      frontX - 0.055, ground + 2.55, ShopNearZ + 1.0, 0.3, 0.6, -math.Pi / 2.0, curvature)

    val gable = new GeometryBuilder
    val north = ShopFarZ + 0.01
    gable.addBox(frontX + 0.4 + ShopDepth / 2.0 - 0.3, ground + 1.55, north,
      ShopDepth - 0.6, 0.16, 0.14)
    List(1.5, 4.9).foreach { offset =>
      gable.addCylinder(frontX + offset, ground + (H1 + 0.4) / 2.0, north + 0.04,
        0.05, 0.05, H1 + 0.4, 8.0)
      (0 until 3).foreach { index =>
        gable.addBox(frontX + offset, ground + 0.7 + index * 1.1, north + 0.08,
          0.11, 0.06, 0.11)
      }
    }
    gable.addBox(frontX + 2.9, ground + H1 + 1.5, north - 0.02, 1.05, 0.95, 0.14)
    gable.addBox(frontX + 2.9, ground + H1 + 1.5, north + 0.05, 0.9, 0.8, 0.06)
    gable.addBox(frontX + 2.9, ground + H1 + 1.5, north + 0.08, 0.06, 0.8, 0.07)
    List(frontX + 5.6, frontX + 6.02).foreach { x =>
      gable.addBox(x, ground + H1 + (H2 + 0.5) / 2.0, north + 0.12, 0.05, H2 + 0.5, 0.05)
    }
    (0 until 7).foreach { index =>
      gable.addBox(frontX + 5.81, ground + H1 + 0.25 + index * 0.42, north + 0.12,
        0.46, 0.035, 0.035)
    }
    val gableMesh = part(gable, ToonMaterial.celMaterial(MetalGray, curvature))
    val gablePosters = List(
      (ProceduralTextures.AozoraSignKey, frontX + 0.75), (ProceduralTextures.SoraTeaHeaderKey, frontX + 1.3)
    ).map { (key, x) =>
      texturedPanel(textures.get(key), x, ground + 1.05, north + 0.05, 0.44, 0.6, 0.0, curvature)
    }

    List(unitMesh, pipeMesh, gableMesh) ++ posters ++ List(banner, warning) ++ gablePosters
  end services

  private def texturedPanel(
    texture: MaterialTexture,
    x: Double,
    y: Double,
    z: Double,
    width: Double,
    height: Double,
    yaw: Double,
    curvature: PlanetCurvature
  ): PartMesh =
    val geometry = new PlaneGeometry(width, height)
    geometry.rotateY(yaw)
    geometry.translate(x, y, z)
    WorldGeometry.bend(geometry, curvature)
    val material = new MeshBasicMaterial(MeshBasicMaterialParameters(map = js.defined(texture)))
    val mesh     = new Mesh(geometry, material)
    ThreeInterop.setUserDataFlag(mesh, "noOutline")
    PartMesh(mesh, List(geometry), List(material))

  private def pavementClutter(frontX: Double, ground: Double, curvature: PlanetCurvature): List[PartMesh] =
    val walkY       = ground + CrossingStreetscape.SidewalkHeight
    val cratesA     = new GeometryBuilder
    val cratesB     = new GeometryBuilder
    val crateRims   = new GeometryBuilder
    val crateWidth  = 0.52
    val crateHeight = 0.29
    val crateDepth  = 0.36
    val layout =
      List((0.0, 0.0, 0.0), (0.0, 1.0, 0.0), (0.0, 2.0, 0.0), (0.58, 0.0, 0.05), (0.58, 1.0, 0.05), (-0.05, 0.0, 0.42))
    layout.zipWithIndex.foreach { case ((dx, level, dz), index) =>
      val x      = frontX + 0.55 + dx
      val z      = ShopFarZ - 0.85 + dz
      val target = if index % 2 == 0 then cratesA else cratesB
      target.addBox(x, walkY + crateHeight / 2.0 + level * crateHeight, z,
        crateWidth, crateHeight, crateDepth)
      crateRims.addBox(x, walkY + crateHeight - 0.01 + level * crateHeight, z,
        crateWidth - 0.06, 0.05, crateDepth - 0.06)
    }
    val crateParts = List(
      part(cratesA, ToonMaterial.celMaterial(Palette.crate, curvature)),
      part(cratesB, ToonMaterial.celMaterial(Palette.crateAlt, curvature)),
      part(crateRims, ToonMaterial.celMaterial(Palette.warningBlack, curvature))
    )

    val table = new GeometryBuilder
    table.addBox(frontX - 0.68, walkY + 0.28, ShopFarZ - 2.4, 0.6, 0.05, 1.7)
    List(-0.7, 0.7).foreach { offset =>
      table.addBox(frontX - 0.68, walkY + 0.14, ShopFarZ - 2.4 + offset, 0.05, 0.28, 0.05)
    }
    val tablePart = part(table, ToonMaterial.celMaterial(Palette.metalWarm, curvature))

    val baskets       = new GeometryBuilder
    val produceColors = List(0xf0a63c, 0x8fbf4a, 0xe0574a)
    val produce       = produceColors.map(_ => new GeometryBuilder)
    (0 until 3).foreach { index =>
      val x = frontX - 0.7 + index * 0.05
      val z = ShopFarZ - 2.9 + index * 0.52
      baskets.addCylinder(x, walkY + 0.385, z, 0.24, 0.19, 0.16, 10.0)
      produceColors.indices.foreach { item =>
        produce(item).addBox(x + (item - 1) * 0.1, walkY + 0.49, z + (item - 1) * 0.08,
          0.11, 0.12, 0.11)
      }
    }
    val basketPart = part(baskets, ToonMaterial.celMaterial(Palette.basket, curvature))
    val produceParts = produce.zip(produceColors).map { (builder, color) =>
      part(builder, ToonMaterial.celMaterial(color, curvature))
    }

    val bin  = new GeometryBuilder
    val binZ = ShopNearZ + 0.12
    bin.addCylinder(frontX - 0.62, walkY + 0.36, binZ, 0.24, 0.21, 0.72, 12.0)
    bin.addCylinder(frontX - 0.62, walkY + 0.74, binZ, 0.26, 0.26, 0.06, 12.0)
    val binPart = part(bin, ToonMaterial.celMaterial(Palette.bin, curvature))

    crateParts ++ List(tablePart, basketPart, binPart) ++ produceParts
  end pavementClutter

  /** The 青空商店 sign board over the shopfront with its mounting bar. */
  private def signBoard(
    frontX: Double,
    ground: Double,
    zMid: Double,
    textures: TextureCache,
    curvature: PlanetCurvature
  ): (Mesh[?, ?, ?], BufferGeometry[?, ?], MeshBasicMaterial) =
    val signTexture = textures.get(ProceduralTextures.AozoraSignKey)
    val geometry    = new PlaneGeometry(5.6, 0.78)
    geometry.rotateY(-math.Pi / 2.0)
    geometry.translate(frontX - 0.06, ground + H1 + 0.62, zMid + 0.1)
    WorldGeometry.bend(geometry, curvature)
    val material = new MeshBasicMaterial(MeshBasicMaterialParameters(map = js.defined(signTexture)))
    (new Mesh(geometry, material), geometry, material)
end AozoraCornerShop
