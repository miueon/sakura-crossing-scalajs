package sakura.world

import sakura.Palette
import sakura.PlanetCurvature
import sakura.ThreeInterop
import sakura.ToonMaterial
import vendor.three.*

import scala.math

/** The three-car suburban EMU crossing the scene's level crossing.
  *
  * The schedule half is pure: position, gate closure and alarm phase are functions of time alone,
  * so the crossing sync is unit-testable without three.js. The rendering half keeps the consist to
  * one merged mesh per colour family per car (thirteen draws including the body's ink hull), each
  * car seated and pitched on the terrain by its own group; the cel and flat materials bend every
  * vertex onto the planet in the shader, so the moving cars need no host-side bending.
  */
object CommuterTrain:

  val CarCount: Int     = 3
  val CarLength: Double = 19.4
  val CarWidth: Double  = 2.86
  val CarPitch: Double  = 20.1
  val FloorY: Double    = 1.06
  val TopY: Double      = 3.74
  val RoofY: Double     = 3.96

  val Speed: Double              = 9.5
  val CorridorSpan: Double       = 700.0
  val GateClosedDistance: Double = 55.0
  val GateClearDistance: Double  = 80.0
  val AlarmFlashHz: Double       = 1.8
  val AlarmActiveClosure: Double = 0.05

  private val SeatBandHex      = 0x515a72
  private val GrabRailHex      = 0xb9c0cc
  private val CeilingLightHex  = 0xf0ead8
  private val OutlineThickness = 0.0038
  // The crossing approach camera needs the train's near-side faces to stay in the cream midtone.
  private val approachSunDirection: (Double, Double, Double) = (-0.52, 0.62, 0.56)

  private val WindowBaseY = 2.16
  private val WindowTopY  = 3.16
  private val HalfWidth   = CarWidth / 2.0
  private val BodyMidY    = (FloorY + TopY) / 2.0
  private val BodyHeight  = TopY - FloorY
  private val DoorXs      = List(-7.0, -2.4, 2.4, 7.0)
  private val DoorWidth   = 1.32
  private val WindowBays  = List((-8.5, 1.7), (-4.7, 3.2), (0.0, 3.4), (4.7, 3.2), (8.5, 1.7))
  private val BogieXs     = List(-6.3, 6.3)
  private val AcPodXs     = List(-5.6, -1.4, 3.2, 7.4)
  private val VentXs      = List(-8.2, 0.6, 8.6)
  private val RailTopY    = RailwayTrack.RailHeadTop
  private val WheelRadius = 0.43

  final case class TrainState(centerX: Double, gateClosure: Double, activeAlarmLamp: Int)

  /** Train centre x: the consist starts centred on the crossing at `t = 0`, runs towards `+x` at
    * [[Speed]] and wraps across the corridor ends, which sit far enough down the planet's bowl to
    * stay below the visible horizon from every camera orbit angle.
    */
  def positionAt(timeSeconds: Double, crossingX: Double): Double =
    val travelled = timeSeconds * Speed
    val wrapped =
      ((travelled % CorridorSpan) + CorridorSpan * 1.5) % CorridorSpan - CorridorSpan / 2.0
    crossingX + wrapped

  /** 1.0 while the consist centre is within [[GateClosedDistance]] of the crossing, easing to 0.0
    * by [[GateClearDistance]].
    */
  def gateClosure(trainCenterX: Double, crossingX: Double): Double =
    val distance = math.abs(trainCenterX - crossingX)
    if distance <= GateClosedDistance then 1.0
    else if distance >= GateClearDistance then 0.0
    else
      val t = (distance - GateClosedDistance) / (GateClearDistance - GateClosedDistance)
      1.0 - t * t * (3.0 - 2.0 * t)

  /** Which alarm lamp is lit: a square wave at [[AlarmFlashHz]] while the crossing is closing,
    * resting on 0 below [[AlarmActiveClosure]]. */
  def activeAlarmLamp(timeSeconds: Double, gateClosure: Double): Int =
    if gateClosure <= AlarmActiveClosure then 0
    else math.floor(timeSeconds * AlarmFlashHz * 2.0).toInt % 2

  def stateAt(timeSeconds: Double, crossingX: Double): TrainState =
    val center  = positionAt(timeSeconds, crossingX)
    val closure = gateClosure(center, crossingX)
    TrainState(center, closure, activeAlarmLamp(timeSeconds, closure))

  def carCenterX(trainCenterX: Double, index: Int): Double =
    trainCenterX + (index - 1) * CarPitch

  /** Flat-plane seating for one car: terrain height at its centre and the pitch that aligns the
    * body axis with the terrain tangent. The vertex-shader bend adds the planet curvature on top
    * of both rail and car alike, so the angle to match is the terrain slope, not the bent
    * surface's.
    */
  def carSeat(carCenterX: Double, groundAt: (Double, Double) => Double): (Double, Double) =
    val height = groundAt(carCenterX, 0.0)
    val slope  = groundAt(carCenterX + 0.5, 0.0) - groundAt(carCenterX - 0.5, 0.0)
    (height, math.atan(slope))

  def build(
    curvature: PlanetCurvature,
    groundAt: (Double, Double) => Double,
    crossingX: Double
  ): TrainRig =
    val materials = Map(
      CarPart.Body    -> approachCelMaterial(Palette.trainBody, curvature),
      CarPart.Stripe  -> approachCelMaterial(Palette.trainStripe, curvature),
      CarPart.Stripe2 -> approachCelMaterial(Palette.trainStripe2, curvature),
      CarPart.Roof    -> approachCelMaterial(Palette.trainRoof, curvature),
      CarPart.Skirt   -> approachCelMaterial(Palette.trainSkirt, curvature),
      CarPart.Steel   -> approachCelMaterial(Palette.railSteel, curvature),
      CarPart.Dark    -> approachCelMaterial(Palette.warningBlack, curvature),
      CarPart.Door    -> approachCelMaterial(Palette.trainDoor, curvature),
      CarPart.Window  -> ToonMaterial.flatMaterial(Palette.trainWindow, curvature),
      CarPart.Seat    -> ToonMaterial.flatMaterial(SeatBandHex, curvature),
      CarPart.Grab    -> ToonMaterial.flatMaterial(GrabRailHex, curvature),
      CarPart.Ceiling -> ToonMaterial.flatMaterial(CeilingLightHex, curvature)
    )
    val outline = ToonMaterial.outlineMaterial(OutlineThickness, curvature)

    val cars  = (0 until CarCount).map { index => buildCar(index, materials, outline) }
    val group = new Group()
    cars.foreach(car => ThreeInterop.addChild(group, car.group))

    TrainRig(
      group = group,
      cars = cars.map(_.group).toVector,
      crossingX = crossingX,
      groundAt = groundAt,
      geometries = cars.flatMap(_.geometries).toList,
      materials = materials.values.toList :+ outline
    )
  end build

  private def approachCelMaterial(baseColorHex: Int, curvature: PlanetCurvature): ShaderMaterial =
    ToonMaterial.celMaterialWithSunDirection(baseColorHex, curvature, approachSunDirection)

  private def buildCar(
    index: Int,
    materials: Map[CarPart, Material[?]],
    outline: Material[?]
  ): BuiltCar =
    val parts = CarPart.values.map(part => part -> new GeometryBuilder()).toMap
    addCarParts(index, parts)

    val car = new Group()
    val geometries = CarPart.values.flatMap { part =>
      val geometry = parts(part).result()
      val mesh     = new Mesh(geometry, materials(part))
      ThreeInterop.addChild(car, mesh)
      val outlineGeometry =
        if part == CarPart.Body then
          val smoothed = sakura.Outline.smoothedGeometry(geometry)
          val shell    = new Mesh(smoothed, outline)
          ThreeInterop.setUserDataFlag(mesh, "hasOutline")
          shell.renderOrder = mesh.renderOrder - 1.0
          shell.frustumCulled = mesh.frustumCulled
          shell.castShadow = false
          shell.receiveShadow = false
          ThreeInterop.setUserDataFlag(shell, "isOutline")
          ThreeInterop.addChild(car, shell)
          Some(smoothed)
        else None
      geometry :: outlineGeometry.toList
    }.toList
    BuiltCar(car, geometries)
  end buildCar

  private def addCarParts(index: Int, parts: Map[CarPart, GeometryBuilder]): Unit =
    val isLead = index == CarCount - 1
    val isTail = index == 0
    val face   = if isLead then 1.0 else -1.0
    val faceX  = if isLead then CarLength / 2.0 else -CarLength / 2.0
    val sides  = List(1.0, -1.0)

    parts(CarPart.Body).addBox(0.0, BodyMidY, 0.0, CarLength, BodyHeight, CarWidth)

    val roof = parts(CarPart.Roof)
    roof.addBox(0.0, (TopY + RoofY) / 2.0, 0.0, CarLength - 0.10, RoofY - TopY, CarWidth - 0.24)
    sides.foreach { side =>
      roof.addBox(0.0, TopY + 0.02, side * (HalfWidth - 0.06), CarLength - 0.05, 0.07, 0.10)
    }
    AcPodXs.zipWithIndex.foreach { case (podX, pod) =>
      roof.addBox(podX, RoofY + 0.13, if pod % 2 == 0 then 0.18 else -0.18, 2.1, 0.30, 1.5)
    }

    parts(CarPart.Stripe).addBox(0.0, 1.92, 0.0, CarLength + 0.02, 0.34, CarWidth + 0.03)
    parts(CarPart.Stripe2).addBox(0.0, 2.125, 0.0, CarLength + 0.02, 0.07, CarWidth + 0.04)

    val skirt = parts(CarPart.Skirt)
    skirt.addBox(0.0, FloorY - 0.25, 0.0, CarLength - 0.30, 0.50, CarWidth - 0.34)
    skirt.addBox(0.0, FloorY - 0.52, 0.0, CarLength - 1.60, 0.28, CarWidth - 0.80)

    val steel = parts(CarPart.Steel)
    BogieXs.foreach { bogieX =>
      steel.addBox(bogieX, 0.78, 0.0, 2.9, 0.42, CarWidth - 0.90)
    }
    VentXs.foreach { ventX =>
      steel.addBox(ventX, RoofY + 0.07, -0.7, 0.7, 0.16, 0.7)
    }
    if isLead || isTail then addPantograph(parts, if isLead then -4.0 else 4.0)

    val dark = parts(CarPart.Dark)
    sides.foreach { side =>
      DoorXs.foreach { doorX =>
        dark.addBox(doorX, BodyMidY - 0.02, side * (HalfWidth + 0.02), 0.05, BodyHeight - 0.12, 0.06)
      }
    }
    BogieXs.foreach { bogieX =>
      dark.addBox(bogieX, 0.62, 0.0, 3.3, 0.20, 0.28)
      List(-1.05, 1.05).foreach { wheelX =>
        List(-0.53, 0.53).foreach { wheelZ =>
          dark.addBox(
            bogieX + wheelX,
            RailTopY + WheelRadius,
            wheelZ,
            0.14,
            WheelRadius * 2.0,
            WheelRadius * 2.0
          )
        }
      }
    }
    if isLead || isTail then
      dark.addBox(faceX + face * 0.03, 2.86, 0.0, 0.10, 1.34, CarWidth - 0.22)
      dark.addBox(faceX + face * 0.25, 0.62, 0.0, 0.50, 0.22, 0.34)
      skirt.addBox(faceX + face * 0.10, 0.82, 0.0, 0.34, 0.78, CarWidth - 0.50)

    val door = parts(CarPart.Door)
    sides.foreach { side =>
      DoorXs.foreach { doorX =>
        door.addBox(doorX, BodyMidY - 0.02, side * (HalfWidth + 0.012), DoorWidth, BodyHeight - 0.12, 0.05)
      }
    }

    val window = parts(CarPart.Window)
    sides.foreach { side =>
      WindowBays.foreach { case (bayX, bayWidth) =>
        window.addBox(bayX, (WindowBaseY + WindowTopY) / 2.0, side * (HalfWidth + 0.032), bayWidth,
          WindowTopY - WindowBaseY, 0.03)
      }
      DoorXs.foreach { doorX =>
        window.addBox(doorX, (WindowBaseY + WindowTopY) / 2.0, side * (HalfWidth + 0.040), 0.94,
          WindowTopY - WindowBaseY, 0.03)
      }
      if isLead || isTail then
        List(-0.72, 0.72).foreach { paneZ =>
          window.addBox(faceX + face * 0.09, 2.88, paneZ, 0.06, 1.02, 1.16)
        }
    }

    val bandHeight  = WindowTopY - WindowBaseY
    val bandWidths  = WindowBays.map(_._2 - 0.06) ++ DoorXs.map(_ => 0.94 - 0.06)
    val bandCenters = WindowBays.map(_._1) ++ DoorXs
    sides.foreach { side =>
      val z = side * (HalfWidth + 0.05)
      bandCenters.zip(bandWidths).foreach { case (centerX, width) =>
        parts(CarPart.Seat).addBox(centerX, WindowBaseY + bandHeight * 0.14, z, width, bandHeight * 0.28, 0.012)
        parts(CarPart.Grab).addBox(centerX, WindowBaseY + bandHeight * 0.72, z, width, 0.045, 0.012)
        parts(CarPart.Ceiling).addBox(centerX, WindowBaseY + bandHeight * 0.93, z, width, 0.07, 0.012)
      }
    }
    if isLead then
      List(-1.06, 1.06).foreach { lightZ =>
        parts(CarPart.Ceiling).addBox(faceX + face * 0.13, 1.63, lightZ, 0.06, 0.16, 0.34)
      }
  end addCarParts

  private def addPantograph(parts: Map[CarPart, GeometryBuilder], baseX: Double): Unit =
    val steel = parts(CarPart.Steel)
    val baseY = RoofY + 0.13
    val kneeY = RoofY + 0.55
    val topY  = 4.90
    steel.addBox(baseX, RoofY + 0.09, 0.0, 1.5, 0.08, 1.5)
    steel.addBeam(baseX - 0.60, baseY, 0.0, baseX + 0.15, kneeY, 0.0, 0.05)
    steel.addBeam(baseX + 0.60, baseY, 0.0, baseX - 0.15, kneeY, 0.0, 0.05)
    steel.addBeam(baseX + 0.15, kneeY, 0.0, baseX - 0.20, topY, 0.0, 0.045)
    steel.addBeam(baseX - 0.15, kneeY, 0.0, baseX + 0.20, topY, 0.0, 0.045)
    steel.addBox(baseX, topY + 0.04, 0.0, 0.30, 0.08, 1.34)

  private enum CarPart:
    case Body, Stripe, Stripe2, Roof, Skirt, Steel, Dark, Door, Window, Seat, Grab, Ceiling

  final private case class BuiltCar(group: Group[?], geometries: List[BufferGeometry[?, ?]])
end CommuterTrain

/** The mounted consist: stepping it moves each car group to its terrain seating and reports the
  * crossing state the barriers and alarm lamps animate from.
  */
final class TrainRig(
  val group: Group[?],
  cars: Vector[Group[?]],
  crossingX: Double,
  groundAt: (Double, Double) => Double,
  val geometries: List[BufferGeometry[?, ?]],
  val materials: List[Material[?]]
):

  def step(timeSeconds: Double): CommuterTrain.TrainState =
    val state = CommuterTrain.stateAt(timeSeconds, crossingX)
    cars.zipWithIndex.foreach { case (car, index) =>
      val carX            = CommuterTrain.carCenterX(state.centerX, index)
      val (height, pitch) = CommuterTrain.carSeat(carX, groundAt)
      car.position.set(carX, height, 0.0)
      car.rotation.set(0.0, 0.0, pitch)
    }
    state
