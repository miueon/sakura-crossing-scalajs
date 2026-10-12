package sakura

class GroundGeometrySuite extends munit.FunSuite:

  private val span     = 300.0
  private val segments = 4

  private val positions = GroundGeometry.positions(span, segments)

  test("every graded vertex sits below the groundY plane by at least the terrain drop") {
    assertEquals(positions.size, segments * segments * 6 * 3)
    positions.grouped(3).foreach { vertex =>
      val x = vertex(0)
      val z = vertex(2)
      assert(x >= -span / 2.0 && x <= span / 2.0, s"x $x left the patch")
      assert(z >= -span / 2.0 && z <= span / 2.0, s"z $z left the patch")
      assert(vertex(1) <= TerrainHeightmap.groundY(z) - GroundGeometry.TerrainDrop + 1e-12,
        s"vertex at ($x, $z) rose above the reference plane")
    }
  }

  test("the precompensated sag stays bounded: no vertex falls more than half a metre under") {
    positions.grouped(3).foreach { vertex =>
      val z = vertex(2)
      assert(vertex(1) >= TerrainHeightmap.groundY(z) - GroundGeometry.TerrainDrop - 0.5,
        s"vertex at z $z sagged too far under the plane")
    }
  }

  test("the graded height is independent of x") {
    val byZ = positions.grouped(3).toVector.groupBy(vertex =>
      math.round(vertex(2) / (span / segments)).toInt)
    byZ.foreach { (row, vertices) =>
      val first = vertices.head(1)
      vertices.foreach { vertex =>
        assertEqualsDouble(vertex(1), first, 1e-12, s"row $row tilts across x")
      }
    }
  }

  test("triangles wind counter-clockwise as the camera above the patch sees them") {
    positions.grouped(9).foreach { triangle =>
      val x0         = triangle(0)
      val z0         = triangle(2)
      val x1         = triangle(3)
      val z1         = triangle(5)
      val x2         = triangle(6)
      val z2         = triangle(8)
      val doubleArea = (x1 - x0) * (z0 - z2) - (x2 - x0) * (z0 - z1)
      assert(doubleArea > 0.0, s"triangle wound away from the camera: signed area $doubleArea")
    }
  }
end GroundGeometrySuite
