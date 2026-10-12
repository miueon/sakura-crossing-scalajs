package sakura

import scala.math

class TerrainHeightmapSuite extends munit.FunSuite:

  private val span  = 300.0
  private val steps = 10

  /** Flat-space elevation stays meaningful only while terrain is shallow against the planet:
    * relief approaching the radius itself would make the plane and its bent surface disagree.
    */
  private val maxRelief = PlanetCurvature.Default.radius.value / 10.0

  test("the crossing band is level: groundY is zero from z -13 to 28") {
    List(-13.0, -12.0, 0.0, 13.6, 20.5, 27.9, 28.0).foreach { z =>
      assertEqualsDouble(TerrainHeightmap.groundY(z), 0.0, 1e-12, s"z $z left the level band")
    }
  }

  test("the street climbs away from the crossing at the upstream grades") {
    assertEqualsDouble(TerrainHeightmap.groundY(-32.0), 1.05, 1e-12)
    assertEqualsDouble(TerrainHeightmap.groundY(-150.0), 1.05, 1e-12)
    assertEqualsDouble(TerrainHeightmap.groundY(48.0), 0.45, 1e-12)
    assertEqualsDouble(TerrainHeightmap.groundY(150.0), 0.45, 1e-12)
  }

  test("elevation depends only on z: no lateral tilt across the carriageway") {
    List(-40.0, -20.0, 0.0, 35.0, 60.0).foreach { z =>
      List(-30.0, -5.0, 0.0, 5.0, 30.0).foreach { x =>
        assertEqualsDouble(TerrainHeightmap.hillAt(x, z), TerrainHeightmap.groundY(z), 1e-12,
          s"hillAt($x, $z) drifted from groundY($z)")
      }
    }
  }

  test("the profile is continuous and shallow against the planet radius") {
    var previous = TerrainHeightmap.groundY(-span / 2.0)
    (-steps to steps).foreach { step =>
      val z = step * (span / steps)
      val y = TerrainHeightmap.groundY(z)
      assert(math.abs(y) <= maxRelief, s"elevation $y leaves the walkable relief bound")
      assert(math.abs(y - previous) < 1.1, s"the profile jumps between $previous and $y at z $z")
      previous = y
    }
  }
end TerrainHeightmapSuite
