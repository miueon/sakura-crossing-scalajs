package sakura.foliage

import sakura.TerrainHeightmap

class CherryTreeGeneratorSuite extends munit.FunSuite:

  private val placements = List(
    CherryPlacement(x = -24.0, z = -26.0, scale = 1.25, seed = 101L),
    CherryPlacement(x = 18.0, z = 22.0, scale = 0.9, seed = 202L)
  )

  test("plan is deterministic for a fixed seed") {
    assertEquals(CherryTreeGenerator.plan(placements), CherryTreeGenerator.plan(placements))
  }

  test("every tree contributes branching timber and layered blossom") {
    val plan = CherryTreeGenerator.plan(placements)
    // One trunk plus at least its four main limbs per tree.
    assert(clue(plan.wood.size) >= placements.size * 5)
    // Crown cluster plus per-limb blobs dominate; every limb also carries a deep accent.
    assert(clue(plan.canopyLight.size) >= placements.size * 7)
    assert(clue(plan.canopyDeep.size) >= placements.size * 4)
  }

  test("timber and blossom seat on or above the terrain, never under it") {
    // A plan instance may drift from its root onto rising terrain, but by less than the terrain's
    // steepest gradient over the tree's own reach, so a small slope margin bounds the check.
    val slopeMargin = 0.55
    CherryTreeGenerator.plan(placements).wood.foreach { transform =>
      val (px, py, pz) = transform.position
      assert(clue(py) >= TerrainHeightmap.hillAt(px, pz) - slopeMargin)
    }
  }

  test("the trunk rises from the ground instead of floating or sinking") {
    placements.foreach { placement =>
      val trunk = CherryTreeGenerator.plan(List(placement)).wood.head
      val (px, py, pz) = trunk.position
      val ground = TerrainHeightmap.hillAt(placement.x, placement.z)
      assert(clue(py) >= ground + 0.8, s"trunk centre $py sits too low against ground $ground at ($px, $pz)")
    }
  }
