package sakura.world

import scala.math

import sakura.TerrainHeightmap

class CommuterTrainSuite extends munit.FunSuite:

  private val crossingX = 62.0

  test("the consist is centred across the crossing at t = 0 with cars one pitch apart") {
    assertEqualsDouble(CommuterTrain.positionAt(0.0, crossingX), crossingX, 1e-12)
    assertEqualsDouble(CommuterTrain.carCenterX(crossingX, 0), crossingX - CommuterTrain.CarPitch, 1e-12)
    assertEqualsDouble(CommuterTrain.carCenterX(crossingX, 1), crossingX, 1e-12)
    assertEqualsDouble(CommuterTrain.carCenterX(crossingX, 2), crossingX + CommuterTrain.CarPitch, 1e-12)
  }

  test("the run advances at the schedule speed and wraps back at the corridor end") {
    assertEqualsDouble(
      CommuterTrain.positionAt(2.0, crossingX),
      crossingX + 2.0 * CommuterTrain.Speed,
      1e-9
    )
    val wrapTime = (CommuterTrain.CorridorSpan / 2.0) / CommuterTrain.Speed
    assertEqualsDouble(
      CommuterTrain.positionAt(wrapTime - 0.01, crossingX),
      crossingX + CommuterTrain.CorridorSpan / 2.0 - 0.095,
      1e-9
    )
    assertEqualsDouble(
      CommuterTrain.positionAt(wrapTime + 0.01, crossingX),
      crossingX - CommuterTrain.CorridorSpan / 2.0 + 0.095,
      1e-9
    )
  }

  test("gate closure is full up to the closed distance and clear beyond the clear distance") {
    assertEqualsDouble(CommuterTrain.gateClosure(crossingX, crossingX), 1.0, 1e-12)
    assertEqualsDouble(
      CommuterTrain.gateClosure(crossingX + CommuterTrain.GateClosedDistance, crossingX),
      1.0,
      1e-12
    )
    assertEqualsDouble(
      CommuterTrain.gateClosure(crossingX + CommuterTrain.GateClearDistance, crossingX),
      0.0,
      1e-12
    )
    assertEqualsDouble(CommuterTrain.gateClosure(crossingX + 200.0, crossingX), 0.0, 1e-12)
  }

  test("gate closure eases symmetrically and monotonically between the two thresholds") {
    val midpoint = (CommuterTrain.GateClosedDistance + CommuterTrain.GateClearDistance) / 2.0
    assertEqualsDouble(CommuterTrain.gateClosure(crossingX + midpoint, crossingX), 0.5, 1e-12)
    assertEqualsDouble(CommuterTrain.gateClosure(crossingX - midpoint, crossingX), 0.5, 1e-12)

    val closures =
      (0 to 10).map(step => CommuterTrain.gateClosure(crossingX + 55.0 + step * 2.5, crossingX))
    assert(closures.zip(closures.tail).forall((before, after) => before >= after))
    assert(closures.forall(closure => closure >= 0.0 && closure <= 1.0))
  }

  test("the alarm lamp alternates with a full period at the flash rate") {
    assertEquals(CommuterTrain.activeAlarmLamp(0.10, 1.0), 0)
    assertEquals(CommuterTrain.activeAlarmLamp(0.40, 1.0), 1)
    assertEquals(CommuterTrain.activeAlarmLamp(0.70, 1.0), 0)

    val period = 1.0 / CommuterTrain.AlarmFlashHz
    (0 until 50).foreach { step =>
      val t = step * 0.037
      assertEquals(
        CommuterTrain.activeAlarmLamp(t + period, 1.0),
        CommuterTrain.activeAlarmLamp(t, 1.0),
        s"lamp phase drifted at t = $t"
      )
    }
  }

  test("the alarm lamp rests below the activation closure") {
    assertEquals(CommuterTrain.activeAlarmLamp(1234.5, 0.04), 0)
    assertEquals(CommuterTrain.activeAlarmLamp(1234.5, CommuterTrain.AlarmActiveClosure), 0)
  }

  test("each car seats at its terrain height and pitches along the tangent") {
    val (height, pitch) = CommuterTrain.carSeat(62.0, TerrainHeightmap.hillAt)
    assertEqualsDouble(height, TerrainHeightmap.hillAt(62.0, 0.0), 1e-12)
    val slope = TerrainHeightmap.hillAt(62.5, 0.0) - TerrainHeightmap.hillAt(61.5, 0.0)
    assertEqualsDouble(pitch, math.atan(slope), 1e-12)
  }

  test("the boom sweep runs from horizontal closed to the raised angle as the crossing clears") {
    assertEqualsDouble(CrossingBarrier.boomAngle(1.0), 0.0, 1e-12)
    assertEqualsDouble(CrossingBarrier.boomAngle(0.0), math.toRadians(78.0), 1e-12)
    assert(CrossingBarrier.boomAngle(0.5) > CrossingBarrier.boomAngle(0.9))
    assert(CrossingBarrier.boomAngle(0.0) > CrossingBarrier.boomAngle(0.5))
  }
end CommuterTrainSuite
