package sakura.world

import sakura.ProceduralTextures

import scala.math

class CrossingStreetscapeSuite extends munit.FunSuite:

  test("Tomare is laid out vertically, in reading order, within the lane") {
    val glyphs = ProceduralTextures.Tomare.Glyphs
    val centers = ProceduralTextures.Tomare.glyphCenters(512)

    assertEquals(glyphs, Vector("止", "ま", "れ"))
    assertEquals(ProceduralTextures.Tomare.RoadTextureOrder, Vector("れ", "ま", "止"))
    assertEquals(centers, Vector(128.0, 256.0, 384.0))
    assert(centers.zip(centers.tail).forall { case (upper, lower) => upper < lower })
  }

  test("both upstream stop markings sit inside their lane, ahead of the crossing band") {
    assertEquals(CrossingStreetscape.StopMarkings.size, 2)
    CrossingStreetscape.StopMarkings.foreach { (laneX, z, _) =>
      assert(math.abs(laneX) + CrossingStreetscape.StopMarkingWidth / 2.0 <
        CrossingStreetscape.RoadHalfWidth, s"marking at ($laneX, $z) leaves the carriageway")
      assert(math.abs(z) > CrossingStreetscape.CrossBand, s"marking at ($laneX, $z) sits inside the crossing band")
    }
  }

  test("the carriageway is the upstream 6.3 m span with 1.55 m sidewalks either side") {
    assertEqualsDouble(CrossingStreetscape.RoadHalfWidth, 3.15, 1e-12)
    assertEqualsDouble(CrossingStreetscape.SidewalkWidth, 1.55, 1e-12)
    assertEqualsDouble(CrossingStreetscape.SidewalkHeight, 0.135, 1e-12)
  }

  test("the corner shop stands on the east frontage, clear of the sidewalk") {
    assert(AozoraCornerShop.ShopFrontX >
      CrossingStreetscape.RoadHalfWidth + CrossingStreetscape.SidewalkWidth)
    assert(AozoraCornerShop.ShopFrontX < 5.0)
  }
end CrossingStreetscapeSuite
