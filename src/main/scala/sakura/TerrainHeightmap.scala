package sakura

import scala.math

/** Terrain elevation on the flat plane the simulation runs on.
  *
  * Rendering bends the surface with [[PlanetCurvature]]; walking, collision and camera height read
  * elevations straight from here. The profile is the upstream `street.js` `groundY(z)`: the street
  * climbs gently past the crossing, and the carriageway elevation depends only on `z` — there is
  * no lateral tilt anywhere, so the crossing and both hero districts sit level across `x`.
  */
object TerrainHeightmap:

  private val SouthRise = 1.05
  private val NorthRise = 0.45

  /** Ground height along the street: level through the crossing band, climbing away both ends. */
  def groundY(z: Double): Double =
    SouthRise * sstep(-13.0, -32.0, z) + NorthRise * sstep(28.0, 48.0, z)

  /** Height of the walkable reference plane at any point; `x` is irrelevant by construction. */
  def hillAt(x: Double, z: Double): Double = groundY(z)

  /** Upstream `sstep`: a C1 smoothstep from 0 at `a` to 1 at `b` (either order). */
  private def sstep(a: Double, b: Double, v: Double): Double =
    val t = math.max(0.0, math.min(1.0, (v - a) / (b - a)))
    t * t * (3.0 - 2.0 * t)
