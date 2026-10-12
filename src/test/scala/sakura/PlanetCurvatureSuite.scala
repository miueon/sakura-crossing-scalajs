package sakura

import scala.math

class PlanetCurvatureSuite extends munit.FunSuite:

  private val curvature = PlanetCurvature.Default

  /** The step may land exactly on the tolerance boundary, where a sampled departure rounds a few
    * ulps above the analytic bound. A wrong derivation misses it by orders of magnitude more.
    */
  private val roundingAllowance = 1e-9

  test("the bend leaves the flat origin on the planet surface") {
    assertEqualsDouble(curvature.bend(0.0, 3.5, 0.0), 3.5, 1e-12)
  }

  test("the bend is symmetric in x and z and falls away from the origin") {
    val offAxis = curvature.bend(10.0, 0.0, 6.0)
    assertEqualsDouble(curvature.bend(-10.0, 0.0, 6.0), offAxis, 1e-12)
    assertEqualsDouble(curvature.bend(10.0, 0.0, -6.0), offAxis, 1e-12)
    assert(curvature.bend(20.0, 0.0, 0.0) < curvature.bend(10.0, 0.0, 0.0))
    assert(curvature.bend(5.0, 0.0, 0.0) < 0.0)
  }

  test("subdivisions keeps every chord within tolerance of the bend") {
    val tolerance = ChordTolerance(0.05)
    val span      = 300.0
    val segments  = curvature.subdivisions(span, tolerance)
    val step      = span / segments
    (0 until segments).foreach { index =>
      val error = maxChordError(-span / 2.0 + index * step, -span / 2.0 + (index + 1) * step)
      assert(
        error <= tolerance.value * (1.0 + roundingAllowance),
        s"chord $index departs from the bend by $error"
      )
    }
  }

  test("subdivisions grows with span and shrinks with tolerance") {
    val tight = ChordTolerance(0.01)
    val loose = ChordTolerance(0.5)
    assert(curvature.subdivisions(300.0, tight) > curvature.subdivisions(300.0, loose))
    assert(curvature.subdivisions(600.0, tight) > curvature.subdivisions(300.0, tight))
  }

  /** Worst departure between the true bend and its chord over `[x0, x1]` on the z = 0 line. */
  private def maxChordError(x0: Double, x1: Double): Double =
    val samples = 64
    val y0      = curvature.bend(x0, 0.0, 0.0)
    val y1      = curvature.bend(x1, 0.0, 0.0)
    (0 to samples)
      .map { sample =>
        val t         = sample.toDouble / samples
        val x         = x0 + (x1 - x0) * t
        val chordY    = y0 + (y1 - y0) * t
        val departure = math.abs(curvature.bend(x, 0.0, 0.0) - chordY)
        departure
      }
      .max
end PlanetCurvatureSuite
