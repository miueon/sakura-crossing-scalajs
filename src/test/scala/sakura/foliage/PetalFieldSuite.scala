package sakura.foliage

import scala.math

import sakura.TerrainHeightmap

class PetalFieldSuite extends munit.FunSuite:

  private val count  = 256
  private val inner  = 8.0
  private val outer  = 46.0

  private def steppedField(seconds: Double): PetalField =
    val field = new PetalField(count, inner, outer, seed = 1L)
    var t     = 0.0
    while t < seconds do
      field.step(t, 0.016)
      t += 0.016
    field

  test("recycling keeps every petal at or above the ground after each step") {
    val field = steppedField(seconds = 320.0)
    (0 until count).foreach { i =>
      val ground = TerrainHeightmap.hillAt(field.x(i), field.z(i))
      assert(clue(field.y(i)) >= ground - 1e-9, s"petal $i sank below the terrain")
      assert(!field.x(i).isNaN && !field.y(i).isNaN && !field.z(i).isNaN)
    }
  }

  test("the field is deterministic for a fixed seed") {
    val first  = steppedField(seconds = 5.0)
    val second = steppedField(seconds = 5.0)
    (0 until count).foreach { i =>
      assert(first.x(i) == second.x(i))
      assert(first.y(i) == second.y(i))
      assert(first.z(i) == second.z(i))
    }
  }

  test("petals drift with the wind and tumble while they settle") {
    val field = new PetalField(count, inner, outer, seed = 3L)
    val x0     = (0 until count).map(field.x).sum
    val z0     = (0 until count).map(field.z).sum
    val firstX = field.angleXOf(0)
    val firstY = field.angleYOf(0)
    // At t = 0 both gusts are near their positive crest, and a 50 ms step cannot recycle any petal
    // from its spawn height, so the mean motion is the wind itself.
    val dt = 0.05
    field.step(0.0, dt)
    val x1 = (0 until count).map(field.x).sum
    val z1 = (0 until count).map(field.z).sum
    assert(clue(x1 - x0) > 0.5 * field.windX(0.0) * dt * count)
    assert(clue(z1 - z0) > 0.5 * field.windZ(0.0) * dt * count)
    assert(clue(math.abs(field.angleXOf(0) - firstX)) >= 0.8 * dt * 0.99, "petals must tumble while falling")
    assert(clue(math.abs(field.angleYOf(0) - firstY)) >= 0.8 * dt * 0.99, "petals must tumble around a second axis")
  }

  test("a stalled tab's dt spike is clamped, not tunnelled") {
    val field = new PetalField(count, inner, outer, seed = 5L)
    field.step(0.0, 3600.0)
    (0 until count).foreach { i =>
      val ground = TerrainHeightmap.hillAt(field.x(i), field.z(i))
      assert(clue(field.y(i)) >= ground - 1e-9)
      assert(clue(math.abs(field.y(i) - ground)) <= 20.0 + 1e-9, "a clamped step cannot fall far")
    }
  }

  test("a configured field spawns within its annulus around the world center") {
    val centerX = 62.0
    val centerZ = -4.0
    val field = new PetalField(
      count,
      inner,
      outer,
      seed = 7L,
      centerX = centerX,
      centerZ = centerZ
    )
    (0 until count).foreach { i =>
      val dx = field.x(i) - centerX
      val dz = field.z(i) - centerZ
      val radius = math.sqrt(dx * dx + dz * dz)
      val ground = TerrainHeightmap.hillAt(field.x(i), field.z(i))
      assert(radius >= inner - 1e-9 && radius <= outer + 1e-9, s"petal $i left its centered spawn ring")
      assert(field.y(i) >= ground + 1.5 - 1e-9, s"petal $i was raised from the wrong ground point")
      assert(field.y(i) <= ground + 20.0 + 1e-9, s"petal $i exceeded its canopy spawn height")
    }
  }
