package sakura.world

import sakura.{MaterialTexture, Palette, PlanetCurvature, ThreeInterop, ToonMaterial}
import vendor.three.*

import scala.math
import scala.scalajs.js

object CrossingBarrier:
  private val BoomLength        = 6.8
  private val BoomHeight        = 1.24
  private val BoomThickness     = 0.17
  private val BoomSegmentLength = 0.52
  private val BoomRaisedAngle   = math.toRadians(78.0)
  private val MastHeight        = 2.45
  private val SignalLensSize    = 0.13
  private val AlarmRed          = 0xe83028
  private val AlarmIdle         = 0x461210
  private val GateZ             = 2.95

  /** One corner of the crossing: `side` picks the road side, `trackSide` the track side. Arm
    * corners carry the barrier machine; sign corners carry the crossbuck and 踏切注意 board.
    */
  final private case class Corner(side: Double, trackSide: Double, isArm: Boolean):
    val x: Double = side * (CrossingStreetscape.RoadHalfWidth + 0.42)
    val z: Double = trackSide * GateZ

  private val corners = List(
    Corner(-1.0, 1.0, isArm = true),
    Corner(1.0, -1.0, isArm = true),
    Corner(1.0, 1.0, isArm = false),
    Corner(-1.0, -1.0, isArm = false)
  )

  /** One sweeping boom arm: the pivot group carries flat-authored geometry along its direction,
    * and the flat material bends each vertex in the shader, so the arm stays seated on the planet
    * at any sweep angle — host-side bending cannot follow a rotating transform.
    */
  final case class Boom(pivot: Group[?], direction: Double)

  final case class BarrierRig(
    scene: SceneAssembly,
    booms: List[Boom],
    lampA: MeshBasicMaterial,
    lampB: MeshBasicMaterial
  )

  /** Sweep angle of the boom arm: horizontal (0 rad) when the crossing is fully closed, raised to
    * 78 degrees when clear. The closure factor is already eased, so the sweep eases with it.
    */
  def boomAngle(gateClosure: Double): Double =
    (1.0 - gateClosure) * BoomRaisedAngle

  def applyState(rig: BarrierRig, gateClosure: Double, activeAlarmLamp: Int): Unit =
    val raised = boomAngle(gateClosure)
    rig.booms.foreach { boom =>
      boom.pivot.rotation.set(0.0, 0.0, boom.direction * raised)
    }
    val alarming = gateClosure > CommuterTrain.AlarmActiveClosure
    rig.lampA.color.setHex(if alarming && activeAlarmLamp == 0 then AlarmRed else AlarmIdle)
    rig.lampB.color.setHex(if alarming && activeAlarmLamp == 1 then AlarmRed else AlarmIdle)

  /** The upstream crossing hardware: a squat barrier machine per arm corner whose boom pivots at
    * waist height, a separate striped mast carrying the alternating signal lamps and the bell, and
    * the crossbuck plus 踏切注意 board on the sign corners. Splitting machine from mast is what
    * keeps the boom at waist height instead of hanging off the signal head.
    */
  def build(
    curvature: PlanetCurvature,
    groundAt: (Double, Double) => Double,
    signTexture: MaterialTexture,
    offsetX: Double
  ): BarrierRig =
    val machines       = new GeometryBuilder()
    val masts          = new GeometryBuilder()
    val blacks         = new GeometryBuilder()
    val boards         = new GeometryBuilder()
    val furniture      = new GeometryBuilder()
    val boomGeometries = List.newBuilder[BufferGeometry[?, ?]]

    val boomYellowMaterial = ToonMaterial.flatMaterial(Palette.warningYellow, curvature)
    val boomDarkMaterial   = ToonMaterial.flatMaterial(Palette.warningBlack, curvature)
    val machineMaterial    = ToonMaterial.celMaterial(0x5a6270, curvature)
    val mastMaterial       = ToonMaterial.celMaterial(Palette.warningYellow, curvature)
    val blackMaterial      = ToonMaterial.celMaterial(Palette.warningBlack, curvature)
    val signMaterial =
      new MeshBasicMaterial(MeshBasicMaterialParameters(map = js.defined(signTexture)))
    val lampAMaterial = new MeshBasicMaterial()
    val lampBMaterial = new MeshBasicMaterial()
    lampAMaterial.color.setHex(AlarmIdle)
    lampBMaterial.color.setHex(AlarmIdle)

    val booms = corners.filter(_.isArm).map { corner =>
      val x    = offsetX + corner.x
      val base = groundAt(x, corner.z)
      val dir  = -corner.side
      machines.addBox(x, base + 0.1, corner.z, 0.66, 0.2, 0.62)
      machines.addBox(x, base + 0.2 + 0.46, corner.z, 0.46, 0.92, 0.38)
      machines.addBox(x, base + 0.2 + 0.955, corner.z, 0.54, 0.07, 0.46)
      (0 until 3).foreach { band =>
        val part = if band % 2 == 0 then masts else blacks
        part.addBox(x, base + 0.34 + band * 0.24, corner.z, 0.48, 0.11, 0.4)
      }
      machines.addBox(x, base + BoomHeight, corner.z + corner.trackSide * 0.2, 0.2, 0.2, 0.14)

      val boomYellow   = new GeometryBuilder()
      val boomDark     = new GeometryBuilder()
      val boomSegments = math.ceil(BoomLength / BoomSegmentLength).toInt
      (0 until boomSegments).foreach { index =>
        val center = (index + 0.5) * BoomSegmentLength * dir
        val part   = if index % 2 == 0 then boomYellow else boomDark
        part.addBox(center, 0.0, 0.0, BoomSegmentLength + 0.01, BoomThickness, 0.09)
      }
      (1 until boomSegments - 1 by 3).foreach { index =>
        val center = (index + 0.5) * BoomSegmentLength * dir
        boomDark.addBox(center, -0.14, 0.06, 0.11, 0.11, 0.07)
        boomDark.addBox(center, -0.05, 0.06, 0.03, 0.12, 0.03)
      }
      val boomYellowGeometry = boomYellow.result()
      val boomDarkGeometry   = boomDark.result()
      boomGeometries += boomYellowGeometry
      boomGeometries += boomDarkGeometry

      val pivot = new Group()
      pivot.position.set(
        x,
        curvature.bend(x, base + BoomHeight, corner.z),
        corner.z + corner.trackSide * 0.2
      )
      pivot.rotation.y = if corner.side > 0.0 then math.Pi else 0.0
      pivot.add(new Mesh(boomYellowGeometry, boomYellowMaterial))
      pivot.add(new Mesh(boomDarkGeometry, boomDarkMaterial))
      Boom(pivot, corner.side)
    }

    corners.foreach { corner =>
      val x     = offsetX + corner.x
      val base  = groundAt(x, corner.z)
      val mastX = x + (if corner.isArm then corner.side * 0.44 else 0.0)
      masts.addCylinder(mastX, base + 0.2 + MastHeight / 2.0, corner.z, 0.075, 0.09, MastHeight, 8.0)
      (0 until 4).foreach { band =>
        blacks.addCylinder(mastX, base + 0.42 + band * 0.56, corner.z, 0.082, 0.082, 0.22, 8.0)
      }
      blacks.addBox(mastX, base + 0.2 + MastHeight + 0.08, corner.z, 0.86, 0.13, 0.1)

      if !corner.isArm then
        boards.addBox(x, base + 0.2 + MastHeight + 0.62, corner.z, 1.15, 0.58, 0.05)
        List(0.72, -0.72).foreach { rotation =>
          val half = 0.6
          blacks.addBeam(x - math.cos(rotation) * half,
            base + 0.2 + MastHeight + 1.28 -
              math.sin(rotation) * half,
            corner.z, x + math.cos(rotation) * half,
            base + 0.2 + MastHeight + 1.28 + math.sin(rotation) * half, corner.z, 0.15)
        }
        boards.addCylinder(x, base + 0.2 + MastHeight + 0.65, corner.z, 0.06, 0.07, 1.3, 8.0)
    }

    relayFurniture(machines, groundAt, offsetX)
    crossingKerbs(machines, groundAt, offsetX)

    val machineGeometry = machines.result()
    val mastGeometry    = masts.result()
    val blackGeometry   = blacks.result()
    val boardGeometry   = boards.result()

    val group = new Group()
    group.add(new Mesh(machineGeometry, machineMaterial))
    group.add(new Mesh(mastGeometry, mastMaterial))
    group.add(new Mesh(blackGeometry, blackMaterial))
    group.add(new Mesh(boardGeometry, signMaterial))
    booms.foreach(boom => ThreeInterop.addChild(group, boom.pivot))
    lensPlanes(corners, groundAt, offsetX, curvature, lampAMaterial, lampBMaterial)
      .foreach(ThreeInterop.addChild(group, _))

    BarrierRig(
      scene = SceneAssembly(
        group,
        List(machineGeometry, mastGeometry, blackGeometry, boardGeometry) ++
          boomGeometries.result(),
        List(machineMaterial, mastMaterial, blackMaterial, signMaterial,
          boomYellowMaterial, boomDarkMaterial, lampAMaterial, lampBMaterial)
      ),
      booms = booms,
      lampA = lampAMaterial,
      lampB = lampBMaterial
    )
  end build

  /** The alternating signal lenses, merged into one mesh per alarm phase: their material colour
    * is the animated alarm state, so they cannot bake into the static head geometry, but the
    * scene is draw-call bound and eight single-quad meshes spend that budget on nothing.
    */
  private def lensPlanes(
    corners: List[Corner],
    groundAt: (Double, Double) => Double,
    offsetX: Double,
    curvature: PlanetCurvature,
    lampAMaterial: MeshBasicMaterial,
    lampBMaterial: MeshBasicMaterial
  ): List[Mesh[?, ?, ?]] =
    List(-0.28 -> lampAMaterial, 0.28 -> lampBMaterial).map { (lensX, material) =>
      val lenses = new GeometryBuilder()
      corners.foreach { corner =>
        val base  = groundAt(offsetX + corner.x, corner.z)
        val mastX = offsetX + corner.x + (if corner.isArm then corner.side * 0.44 else 0.0)
        val face  = corner.trackSide
        val lens  = new PlaneGeometry(SignalLensSize * 2.0, SignalLensSize * 2.0)
        lens.rotateY(if face > 0.0 then 0.0 else math.Pi)
        lens.translate(mastX + lensX, base + 0.2 + MastHeight - 0.1, corner.z + face * 0.09)
        lenses.addMerged(lens)
        lens.dispose()
      }
      val geometry = lenses.result()
      WorldGeometry.bend(geometry, curvature)
      new Mesh(geometry, material)
    }

  private def relayFurniture(
    builder: GeometryBuilder,
    groundAt: (Double, Double) => Double,
    offsetX: Double
  ): Unit =
    val x = offsetX - (CrossingStreetscape.RoadHalfWidth + 1.5)
    val z = -(GateZ + 1.15)
    val y = groundAt(x, z)
    builder.addBox(x, y + 0.66, z, 0.78, 1.32, 0.5)
    builder.addBox(x, y + 1.36, z, 0.86, 0.08, 0.58)
    builder.addBox(x - 0.9, y + 0.45, z + 0.1, 0.52, 0.9, 0.4)
    builder.addBox(x - 0.9, y + 0.93, z + 0.1, 0.6, 0.06, 0.46)

  /** Safety kerbs guiding pedestrians through the crossing deck, both road edges. */
  private def crossingKerbs(
    builder: GeometryBuilder,
    groundAt: (Double, Double) => Double,
    offsetX: Double
  ): Unit =
    List(-1.0, 1.0).foreach { side =>
      val x = offsetX + side * (CrossingStreetscape.RoadHalfWidth + 0.15)
      builder.addBox(x, groundAt(x, 0.0) + 0.08, 0.0, 0.34, 0.16, 6.0)
    }
end CrossingBarrier
