package sakura.foliage

import scala.math

import sakura.TerrainHeightmap
import sakura.world.RailwayTrack

class InstancedFencesSuite extends munit.FunSuite:

  test("plan is deterministic") {
    assertEquals(InstancedFences.plan(0.0, 6.0), InstancedFences.plan(0.0, 6.0))
  }

  test("the corridor carries real batches of furniture") {
    val plan = InstancedFences.plan(0.0, 6.0)
    assert(clue(plan.concrete.size) >= 200, "posts and rails must collapse into a real batch")
    assert(clue(plan.timber.size) >= 16, "poles and crossarms must collapse into a real batch")
  }

  test("every instance seats exactly at the terrain height of its own position") {
    InstancedFences.plan(0.0, 6.0).concrete.foreach { transform =>
      val (px, py, pz) = transform.position
      assert(clue(py) >= TerrainHeightmap.hillAt(px, pz), s"concrete instance sinks under terrain at ($px, $pz)")
    }
    InstancedFences.plan(0.0, 6.0).timber.foreach { transform =>
      val (px, py, pz) = transform.position
      assert(clue(py) >= TerrainHeightmap.hillAt(px, pz), s"timber instance sinks under terrain at ($px, $pz)")
    }
  }

  test("lineside fences leave the road crossing and its kerb clearance open") {
    val roadCenterX = 0.0
    val roadHalfWidth = RailwayTrack.RoadCrossingWidth / 2.0
    val roadEdge = roadHalfWidth + 1.5
    val crossingFence = InstancedFences.plan(roadCenterX, roadHalfWidth).concrete.filter { transform =>
      math.abs(transform.position._3) <= 9.1
    }

    assert(crossingFence.nonEmpty)
    assert(crossingFence.forall { transform =>
      val (x, _, _) = transform.position
      math.abs(x - roadCenterX) - transform.scale._1 / 2.0 > roadEdge
    }, "a fence post or rail occupies the asphalt crossing")
    assert(crossingFence.exists(_.position._1 < roadCenterX))
    assert(crossingFence.exists(_.position._1 > roadCenterX))
  }
