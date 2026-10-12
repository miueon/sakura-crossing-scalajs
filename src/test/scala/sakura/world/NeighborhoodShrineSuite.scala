package sakura.world

import sakura.TerrainHeightmap

import scala.math

class NeighborhoodShrineSuite extends munit.FunSuite:

  private val epsilon = 1e-9

  test("the shrine flight is the upstream geometry: eleven fixed treads to a 2.09 m terrace") {
    val steps = ShrineAssembly.stairPlan(
      ShrineAssembly.OriginX,
      ShrineAssembly.OriginZ,
      ShrineAssembly.Facing,
      TerrainHeightmap.hillAt
    )

    assertEquals(steps.size, 11, "the flight is not the upstream eleven treads")
    assertEqualsDouble(ShrineAssembly.StairRun / ShrineAssembly.StairRise, 0.46 / 0.19, 1e-12)
    steps.foreach { step =>
      val ground = TerrainHeightmap.hillAt(step.centerX, step.centerZ)
      assertEqualsDouble(step.terrainY, ground, epsilon)
      assertEqualsDouble(step.bottomY, ground, epsilon, "a tread is not seated on the grade")
      assert(step.topY > step.bottomY, "a tread has no solid rise")
    }
    steps.zip(steps.tail).foreach { case (before, after) =>
      val run = math.hypot(after.centerX - before.centerX, after.centerZ - before.centerZ)
      assertEqualsDouble(run, ShrineAssembly.StairRun, epsilon, "stair tread spacing changed")
      assertEqualsDouble(after.topY - before.topY, ShrineAssembly.StairRise, epsilon,
        "the flight stopped climbing at its fixed rise")
    }
    assertEqualsDouble(steps.last.topY, ShrineAssembly.TerraceTop, epsilon,
      "the flight does not land on the upstream terrace height")
  }

  test("the shrine viewpoint stands in the alley before the torii and looks up the flight") {
    val viewpoint = ShrineAssembly.Viewpoint

    assertEqualsDouble(viewpoint.eyeX, ShrineAssembly.OriginX, epsilon)
    assertEqualsDouble(viewpoint.targetX, ShrineAssembly.OriginX, epsilon)
    assert(viewpoint.eyeZ < 19.9 - 4.0, "the camera is not back from the torii")
    assert(viewpoint.targetZ > 30.0, "the aim point does not reach through the gate to the hall")
    assertEqualsDouble(viewpoint.eyeHeight, 1.62, epsilon)
    assert(viewpoint.targetHeight > viewpoint.eyeHeight, "the camera does not look up the approach")
  }
end NeighborhoodShrineSuite
