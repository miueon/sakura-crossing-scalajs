package sakura.world

import sakura.ProceduralTextures.*
import sakura.TerrainHeightmap

import scala.math

/** Laws of the two hero districts' layout plans and the sign textures they are painted with.
  * Both halves are pure data — corridor, frontages, prop seating, lantern geometry and camera
  * framing — so the composition the hero frames shoot is pinned without three.js.
  */
class DistrictsSuite extends munit.FunSuite:

  private val epsilon = 1e-9

  private def footprintRect(unit: ShotengaiDistrict.ShopFootprint): ShotengaiDistrict.Rect =
    ShotengaiDistrict.Rect(unit.x0, unit.x1, unit.z0, unit.z1)

  test("the shotengai's frontages flank the lane without crossing it or each other") {
    List(
      ShotengaiDistrict.WestShops.map(unit => (unit, true)),
      ShotengaiDistrict.EastShops.map(unit => (unit, false))
    ).foreach { row =>
      row.foreach { case (unit, west) =>
        val rect = footprintRect(unit)
        assert(!rect.crosses(ShotengaiDistrict.laneRect), s"${unit.kind} crosses the lane")
        assert(unit.z0 >= ShotengaiDistrict.ZStart - 1.0 && unit.z1 <= ShotengaiDistrict.ZEnd + 1.0,
          s"${unit.kind} runs past the corridor")
        if west then assertEqualsDouble(unit.x1, ShotengaiDistrict.WestKerbX, epsilon, s"${unit.kind} off the kerb")
        else assertEqualsDouble(unit.x0, ShotengaiDistrict.EastKerbX, epsilon, s"${unit.kind} off the kerb")
      }
      row.map(_._1).zip(row.map(_._1).tail).foreach { case (before, after) =>
        assert(before.z1 < after.z0, s"${after.kind} overlaps ${before.kind}")
      }
    }
  }

  test("the shotengai's paved base spans the full frontage corridor") {
    assert(ShotengaiDistrict.PavedWidth >= 14.0, s"paved width ${ShotengaiDistrict.PavedWidth} is too narrow")
  }

  test("the shotengai's pure shop plans carry displays, balconies, services, and street life") {
    val plans = ShotengaiDistrict.ShopArchitecture.all
    assertEquals(plans.size, ShotengaiDistrict.WestShops.size + ShotengaiDistrict.EastShops.size)
    assert(plans.forall(_.displayLevels >= 2), "every recessed shopfront needs stocked interior shelves")
    assert(plans.filter(_.hasUpperFloor).forall(_.windowCenters.nonEmpty), "upper shops need planned windows")
    assert(plans.exists(_.balcony.nonEmpty), "the upper rows need balcony frontage")
    assert(plans.flatMap(_.airConditioners).size >= 7, "the upper rows need wall-mounted air conditioners")
    assertEquals(ShotengaiDistrict.KeiVan.WheelZOffsets.size, 2, "the kei truck needs two axles")
    assertEquals(ShotengaiDistrict.KeiVan.Wipers.size, 2, "the cab needs paired windshield wipers")
    assertEquals(ShotengaiDistrict.UtilityLines.poles.size, 3, "the street needs utility poles along its run")
    assert(ShotengaiDistrict.StreetProps.planters.nonEmpty, "street frontage needs planted pots")
    assert(ShotengaiDistrict.StreetProps.crateStacks.nonEmpty, "street frontage needs timber crate stacks")
    assert(ShotengaiDistrict.StreetProps.shadows.nonEmpty, "tree cover needs dappled road shadows")
  }

  test("the shotengai's drain runs down the lane axis at a fixed pitch") {
    val tiles = ShotengaiDistrict.Drain.tiles
    assert(tiles.size >= 30, s"only ${tiles.size} drain tiles for the corridor")
    tiles.foreach { case (x, z) =>
      assertEqualsDouble(x, ShotengaiDistrict.CenterX, epsilon, "a drain tile left the lane axis")
      assert(z >= ShotengaiDistrict.ZStart && z <= ShotengaiDistrict.ZEnd, s"drain tile at $z outside the lane")
    }
    tiles.zip(tiles.tail).foreach { case (before, after) =>
      assertEqualsDouble(after._2 - before._2, ShotengaiDistrict.Drain.Pitch, epsilon, "drain pitch drifted")
    }
  }

  test("the shotengai's lanterns hang off sagging wires inside the shop lines") {
    val festoon = ShotengaiDistrict.Festoon
    assertEquals(festoon.lanterns.size, (festoon.WireCount * festoon.lanternT.size).toInt)
    festoon.lanterns.foreach { case (x, belowAnchor, z, radius) =>
      assert(x > ShotengaiDistrict.WestKerbX && x < ShotengaiDistrict.EastKerbX,
        s"lantern at x=$x not between the shopfronts")
      assert(z >= festoon.FirstZ && z <= festoon.wireZs.last, s"lantern at z=$z off the wire run")
      assert(belowAnchor > radius, "a lantern's paper sits above its own hang point")
    }
    assert(festoon.sagAt(0.5) > festoon.sagAt(0.32), "the wire must sag deepest mid-span")
    assert(math.abs(festoon.sagAt(0.0)) < epsilon && math.abs(festoon.sagAt(1.0)) < epsilon,
      "the wire must anchor level at both shopfronts")
  }

  test("the shotengai's furniture stands on the pavements clear of the carriageway") {
    val furniture = ShotengaiDistrict.Benches.Spots ++ ShotengaiDistrict.Vending.Spots ++
      ShotengaiDistrict.Bicycles.Spots
    furniture.foreach { case (x, z) =>
      assert(z > ShotengaiDistrict.ZStart && z < ShotengaiDistrict.ZEnd, s"furniture at z=$z off the lane")
      assert(!ShotengaiDistrict.carriagewayRect.contains(x, z), s"furniture at ($x, $z) on the carriageway")
    }
    val freezer = ShotengaiDistrict.Freezer
    assert(!ShotengaiDistrict.carriagewayRect.contains(freezer.CenterX, freezer.CenterZ),
      "the freezer blocks the carriageway")
    val van = ShotengaiDistrict.KeiVan.rect
    assert(van.z0 > ShotengaiDistrict.ZStart && van.z1 < ShotengaiDistrict.ZEnd, "the van runs past the lane")
    assert(van.x1 <= ShotengaiDistrict.WestKerbX + 1.7, "the van juts too far into the lane")
  }

  test("the shotengai's focal tree stands beyond the south end clear of both frontages") {
    val tree = ShotengaiDistrict.focalTree
    assert(tree.z < ShotengaiDistrict.ZStart, "the focal tree stands in the lane")
    (ShotengaiDistrict.WestShops ::: ShotengaiDistrict.EastShops).foreach { unit =>
      assert(!footprintRect(unit).contains(tree.x, tree.z), s"the tree stands in ${unit.kind}")
    }
  }

  test("the shotengai camera frames the soba frontage while looking south") {
    val viewpoint = ShotengaiDistrict.Viewpoint
    assert(viewpoint.eyeX > ShotengaiDistrict.WestKerbX && viewpoint.eyeX < ShotengaiDistrict.EastKerbX,
      "the eye must stay between the shop rows")
    assert(viewpoint.eyeZ >= ShotengaiDistrict.SobaShop.z0 && viewpoint.eyeZ <= ShotengaiDistrict.SobaShop.z1,
      "the eye must align with the soba frontage")
    assertEqualsDouble(viewpoint.eyeHeight, 1.62, epsilon, "the eye is not at eye height")
    assert(viewpoint.targetZ < ShotengaiDistrict.ZStart, "the aim point does not reach the street's end")
    assert(math.abs(viewpoint.targetX - viewpoint.eyeX) < 1.0, "the view is not along the lane")
    val bicycleNearZ = ShotengaiDistrict.Bicycles.Spots.map(_._2).max
    val bicycleFarZ  = ShotengaiDistrict.Bicycles.Spots.map(_._2).min
    assert(ShotengaiDistrict.Freezer.CenterZ > bicycleNearZ, "the freezer is not framed ahead of the bicycle row")
    assert(ShotengaiDistrict.BookCrates.Spots.forall(_._2 > bicycleNearZ),
      "the book crates are not in the nearer storefront layer")
    assert(ShotengaiDistrict.Gachapon.CenterZ < bicycleFarZ, "the gachapon bank is not in the midground")
  }

  test("the onsen's machiya flank the lane without crossing it or each other") {
    List(
      OnsenDistrict.SouthShops.map(unit => (unit, true)),
      OnsenDistrict.NorthShops.map(unit => (unit, false))
    ).foreach { row =>
      row.foreach { case (unit, south) =>
        val rect = ShotengaiDistrict.Rect(unit.x0, unit.x1, unit.z0, unit.z1)
        assert(!rect.crosses(OnsenDistrict.laneRect), s"${unit.kind} crosses the lane")
        assert(unit.x0 >= OnsenDistrict.Deck.X0 + 0.9 && unit.x1 <= OnsenDistrict.XEast + 1.0,
          s"${unit.kind} runs past the frontage corridor")
        if south then assertEqualsDouble(unit.z1, OnsenDistrict.SouthFrontageZ, epsilon, s"${unit.kind} off the kerb")
        else assertEqualsDouble(unit.z0, OnsenDistrict.NorthFrontageZ, epsilon, s"${unit.kind} off the kerb")
      }
      row.map(_._1).zip(row.map(_._1).tail).foreach { case (before, after) =>
        assert(before.x1 < after.x0, s"${after.kind} overlaps ${before.kind}")
      }
    }
  }

  test("the onsen's paving courses stagger and its inlay keeps to the centre line") {
    val slabs = OnsenDistrict.Paving.slabs
    assert(OnsenDistrict.Paving.Width >= 6.0, s"paved width ${OnsenDistrict.Paving.Width} is too narrow")
    assert(slabs.size > 30, s"only ${slabs.size} slabs laid")
    val rowStarts = OnsenDistrict.Paving.rowZs.map { rowZ =>
      slabs.filter { case (_, z) => math.abs(z - rowZ) < 0.01 }.minBy(_._1)._1
    }
    assert(rowStarts(0) != rowStarts(1), "adjacent courses must not share a joint line")
    assert(rowStarts(1) != rowStarts(2), "adjacent courses must not share a joint line")
    OnsenDistrict.Paving.inlay.foreach { case (x, z) =>
      assert(x >= OnsenDistrict.XWest - 0.5 && x <= OnsenDistrict.XEast + 0.5, s"inlay tile at $x off the lane")
      assertEqualsDouble(z, OnsenDistrict.CenterZ, 0.01, "inlay tile left the centre course")
    }
    val petals = OnsenDistrict.Petals.spots
    assertEquals(petals.size, OnsenDistrict.Petals.Count)
    petals.foreach { case (x, z) =>
      assert(x >= OnsenDistrict.XWest - 0.6 && x <= OnsenDistrict.XEast + 0.6, s"petal at $x off the lane")
      assert(z >= OnsenDistrict.SouthFrontageZ - 0.4 && z <= OnsenDistrict.NorthFrontageZ + 0.4,
        s"petal at $z off the lane")
    }
  }

  test("the onsen's channel, bridge, footbath, and street props remain in the photographed corridor") {
    val channel = OnsenDistrict.Channel.layout
    assert(channel.z0 < OnsenDistrict.CenterZ && channel.z1 > OnsenDistrict.CenterZ,
      "the hot-spring channel must cross the lane")
    assert(channel.halfWidth > 0.0 && channel.bridgeSpan > 0.0 && channel.bridgeWidth > 0.0)
    assert(channel.bridgeWidth < channel.z1 - channel.z0,
      "the crossing bridge must leave the open water edges visible")
    assert(OnsenDistrict.Channel.contains(channel.centerX, OnsenDistrict.CenterZ))
    assert(!OnsenDistrict.Channel.contains(channel.centerX + channel.halfWidth + 0.1, OnsenDistrict.CenterZ))
    assert(OnsenDistrict.Footbath.plan.waterDepth > 0.0, "the footbath needs a water basin")
    assert(OnsenDistrict.StreetProps.planters.nonEmpty && OnsenDistrict.StreetProps.crateStacks.nonEmpty)
    assert(OnsenDistrict.StreetProps.catBoxes.nonEmpty && OnsenDistrict.StreetProps.signPosts.nonEmpty)
    assert(OnsenDistrict.Dapple.patches.size >= 4, "the stone paving needs canopy shadows")
  }

  test("the onsen's kokeshi display is eleven dolls in two rows of colour order") {
    val dolls = OnsenDistrict.Kokeshi.row
    assertEquals(dolls.size, OnsenDistrict.Kokeshi.Count)
    assertEquals(dolls.count(_.row == 0), OnsenDistrict.Kokeshi.FirstRowCount)
    assertEquals(dolls.count(_.row == 1), OnsenDistrict.Kokeshi.Count - OnsenDistrict.Kokeshi.FirstRowCount)
    dolls.foreach { doll =>
      assert(doll.x >= OnsenDistrict.Kokeshi.TableX0 && doll.x <= OnsenDistrict.Kokeshi.TableX1,
        s"doll at ${doll.x} off the table")
      assert(doll.height >= 0.18 && doll.height <= 0.30, s"doll height ${doll.height} out of range")
      assert(doll.colorIndex >= 0 && doll.colorIndex < OnsenDistrict.Kokeshi.BodyColors.size)
    }
    assertEquals(dolls.map(_.colorIndex),
      (0 until OnsenDistrict.Kokeshi.Count)
        .map(_ % OnsenDistrict.Kokeshi.BodyColors.size).toVector,
      "the colours must cycle the palette")
  }

  test("the onsen's lanterns hang under the eaves along both frontages") {
    val lanterns = OnsenDistrict.Lanterns.spots
    assert(lanterns.size == OnsenDistrict.Lanterns.SouthXs.size + OnsenDistrict.Lanterns.NorthXs.size)
    lanterns.foreach { case (x, z) =>
      val southSide = z < OnsenDistrict.CenterZ
      val expectedZ = if southSide then OnsenDistrict.SouthFrontageZ + 0.30 else OnsenDistrict.NorthFrontageZ - 0.30
      assertEqualsDouble(z, expectedZ, epsilon, "a lantern left its frontage line")
      assert(x >= OnsenDistrict.XWest && x <= OnsenDistrict.XEast, s"lantern at $x off the street")
    }
  }

  test("the onsen's deck, lantern, and braces close the lane on its axis") {
    import OnsenDistrict.{Canopy, Deck}
    assertEqualsDouble((Deck.Z0 + Deck.Z1) / 2.0, OnsenDistrict.CenterZ, epsilon, "the deck left the lane axis")
    assert(Deck.X1 <= OnsenDistrict.XWest + 0.4, "the deck intrudes on the lane")
    assert(Deck.lanternX < Deck.X0, "the stone lantern must stand behind the deck rail")
    assertEqualsDouble(Deck.Z0 + (Deck.Z1 - Deck.Z0) / 2.0, OnsenDistrict.CenterZ, epsilon)
    val braces = Deck.braces(0.0)
    assertEquals(braces.size, 4, "the elevated deck needs X-braces on both sides")
    assert(braces.exists(brace => brace.y0 < brace.y1) && braces.exists(brace => brace.y0 > brace.y1),
      "each side needs both diagonals")

    val (limbs, greens, pinks) = Canopy.plan(TerrainHeightmap.hillAt)
    assert(limbs.nonEmpty && greens.nonEmpty && pinks.nonEmpty, "the canopy needs limbs and both greens and blossom")
    (greens ++ pinks).foreach { blob =>
      val heightAboveGround = blob.position._2 - TerrainHeightmap.hillAt(blob.position._1, blob.position._3)
      assert(heightAboveGround >= Canopy.Clearance,
        s"canopy centre at $heightAboveGround below roof clearance")
      assert(heightAboveGround <= 9.5, s"canopy centre at $heightAboveGround exceeds the sky gap")
    }
    Canopy.trees.foreach { tree =>
      assert(tree.x >= OnsenDistrict.XWest - 2.0 && tree.x <= OnsenDistrict.XEast + 2.0,
        s"tree at ${tree.x} off the street")
    }
    assertEquals(Canopy.deckFramingTrees.size, 2)
    Canopy.deckFramingTrees.foreach { tree =>
      assert(tree.x < Deck.X0, s"deck framing tree at ${tree.x} is not behind the deck")
      assert(tree.crownBase >= Canopy.Clearance && tree.crownBase + 0.8 * tree.scale <= 8.5,
        s"deck framing crown at ${tree.crownBase} is outside the sky gap")
    }
  }

  test("the onsen camera preserves upstream frontage sides and frames the shops before the deck") {
    val viewpoint = OnsenDistrict.Viewpoint
    assertEqualsDouble(viewpoint.eyeZ, OnsenDistrict.CenterZ, epsilon, "the eye left the lane axis")
    assertEqualsDouble(viewpoint.eyeX, 39.2, epsilon, "the eye left the east approach")
    assert(viewpoint.eyeX > OnsenDistrict.KokeshiShop.x1 && viewpoint.eyeX > OnsenDistrict.SakuraanShop.x1,
      "Kokeshi and Sakuraan must be in front of the eye")
    assert(OnsenDistrict.KokeshiShop.x0 > OnsenDistrict.YunokaShop.x1,
      "Kokeshi must be east of Yunoka")
    assertEqualsDouble(viewpoint.eyeHeight, 1.62, epsilon, "the eye is not at eye height")
    assertEqualsDouble(viewpoint.targetX, OnsenDistrict.Deck.lanternX, epsilon,
      "the aim point left the deck lantern")
    assertEqualsDouble(viewpoint.targetHeight, 1.62, epsilon, "the target is not level with the eye")
    assert(viewpoint.targetX < OnsenDistrict.XWest, "the aim point does not reach the street's end")
    assertEqualsDouble(viewpoint.targetZ, OnsenDistrict.CenterZ, epsilon, "the view is not along the lane")
    assertEqualsDouble(OnsenDistrict.KokeshiShop.z1, OnsenDistrict.SouthFrontageZ, epsilon,
      "Kokeshi-do left the upstream south frontage")
    assertEqualsDouble(OnsenDistrict.SakuraanShop.z0, OnsenDistrict.NorthFrontageZ, epsilon,
      "Sakuraan left the upstream north frontage")
    assert(OnsenDistrict.BuildingPlans.forUnit(OnsenDistrict.SakuraanShop, facesSouth = false).hasBalcony,
      "Sakuraan must retain its upper wooden balcony")
    assert(OnsenDistrict.BuildingPlans.all.exists(_.footprint.kind == OnsenDistrict.MachiyaKind.Ryokan),
      "the street's far end needs the upstream ryokan")
    assert(OnsenDistrict.BuildingPlans.all.exists(_.footprint.kind == OnsenDistrict.MachiyaKind.Bathhouse),
      "the street's far end needs the upstream bathhouse")
    assertEqualsDouble(OnsenDistrict.KokeshiShop.x0, 33.0, epsilon, "Kokeshi left the foreground")
    assertEqualsDouble(OnsenDistrict.KokeshiShop.x1, 37.5, epsilon, "Kokeshi left the foreground")
    assertEqualsDouble(OnsenDistrict.SakuraanShop.x0, 34.0, epsilon, "Sakuraan left the foreground")
    assertEqualsDouble(OnsenDistrict.SakuraanShop.x1, 39.0, epsilon, "Sakuraan left the foreground")
  }

  test("a noren's slits never cut through its glyphs") {
    List("そば", "ゆ", "和菓子").foreach { text =>
      val glyphs = Noren.glyphXs(text, 512.0)
      assertEquals(glyphs.size, text.length)
      val glyphHalf = 60.0
      glyphs.foreach { glyphX =>
        Noren.slitCenters(512.0).foreach { slitX =>
          assert(math.abs(glyphX - slitX) > Noren.SlitHalfWidth + glyphHalf,
            s"slit at $slitX cuts the glyph at $glyphX for '$text'")
        }
      }
    }
  }

  test("a fascia keeps its title, trade column and rules inside the board") {
    val width  = 1024.0
    val height = 224.0
    assert(FasciaBoard.titleCenterX(width) + width * 0.60 / 2.0 < FasciaBoard.subLeftX(width),
      "the title band runs under the trade column")
    assert(FasciaBoard.subLeftX(width) < width, "the trade column starts outside the board")
    assert(FasciaBoard.TopRuleHeight < height * 0.2 && FasciaBoard.BottomRuleHeight < height * 0.2,
      "the frame rules eat the board")
    assert(FasciaBoard.insetRuleY(height) > FasciaBoard.TopRuleHeight, "the inset rule overlaps the frame")
  }

  test("a blade board stacks its glyphs inside the board's margins") {
    val height = 768.0
    List("つばめ書房", "こけし堂", "ゆのか", "さくら庵", "アイス").foreach { text =>
      val glyphSize = 104.0
      val ys        = BladeBoard.glyphYs(text, height, glyphSize)
      assertEquals(ys.size, text.length)
      assert(ys.head >= BladeBoard.HeadMargin * height, "the first glyph starts above the head margin")
      assert(ys.last + glyphSize / 2.0 <= height - 14.0, s"'$text' runs off the blade's foot")
      ys.zip(ys.tail).foreach { case (before, after) =>
        assert(after > before + glyphSize * 0.9, "glyphs collide")
      }
    }
  }

  test("lantern paper wraps its ribs between the cap bands") {
    val height = 256.0
    val ribs   = LanternPaper.ribYs(height)
    assertEquals(ribs.size, LanternPaper.RibCount)
    assert(ribs.head >= height * LanternPaper.CapBand, "the first rib hides under the cap band")
    assert(ribs.last <= height * (1.0 - LanternPaper.CapBand), "the last rib hides under the cap band")
    ribs.zip(ribs.tail).foreach { case (before, after) => assert(after > before, "ribs out of order") }
  }

  test("petal litter is deterministic and spread over the rectangle") {
    val first  = PetalScatter.placements(40, 10.0, 20.0, 5.0, 3.0, seed = 7)
    val second = PetalScatter.placements(40, 10.0, 20.0, 5.0, 3.0, seed = 7)
    assertEquals(first, second, "the same seed must litter identically")
    first.foreach { case (x, z) =>
      assert(x >= 10.0 && x <= 15.0 && z >= 20.0 && z <= 23.0, s"petal at ($x, $z) outside the rectangle")
    }
    val other = PetalScatter.placements(40, 10.0, 20.0, 5.0, 3.0, seed = 8)
    assert(first != other, "a different seed must move the litter")
  }
end DistrictsSuite
