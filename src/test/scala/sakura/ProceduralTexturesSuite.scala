package sakura

import ProceduralTextures.*

import scala.math

/** Laws of the pattern models behind the canvas textures. The draw pass is a dumb emission of
  * these values, so pinning them here pins what the crossing is painted with.
  */
class ProceduralTexturesSuite extends munit.FunSuite:

  private val tile = 256

  /** Pixels exactly on a band edge are the rasterizer's to assign either way; the coverage law
    * holds for every sample away from one.
    */
  private def onBandEdge(x: Double, y: Double): Boolean =
    val phase = HazardStripes.phase(x, y)
    phase == 0.0 || phase == HazardStripes.BandWidth.toDouble

  /** Whether a stroked band set would paint this point: a stroked segment of width `StrokeWidth`
    * covers `BandWidth / 2` of the phase on either side of its centre.
    */
  private def covered(segments: Vector[HazardStripes.Segment], x: Double, y: Double): Boolean =
    segments.exists(segment => math.abs(x - y - segment.x0) <= HazardStripes.BandWidth / 2.0)

  private val samples =
    for
      x <- 0 until tile by 13
      y <- 0 until tile by 11
    yield (x.toDouble, y.toDouble)

  test("the hazard tile continues into its neighbour on both axes") {
    assert(HazardStripes.tiles(tile, tile), "the default tile must be a whole number of periods")
    samples.foreach { case (x, y) =>
      assertEquals(HazardStripes.isBlack(x + tile, y), HazardStripes.isBlack(x, y), s"column $x")
      assertEquals(HazardStripes.isBlack(x, y + tile), HazardStripes.isBlack(x, y), s"row $y")
    }
  }

  test("the hazard bands run at exactly 45 degrees") {
    samples.foreach { case (x, y) =>
      (1 to 3).foreach { step =>
        val stepDouble = step.toDouble
        assertEquals(
          HazardStripes.phase(x + stepDouble, y + stepDouble),
          HazardStripes.phase(x, y),
          s"a diagonal step out of ($x, $y) moved the phase"
        )
        assertEquals(
          HazardStripes.phase(x + stepDouble, y),
          HazardStripes.phase(x, y - stepDouble),
          s"a horizontal and a vertical step out of ($x, $y) differ in rate"
        )
      }
    }
  }

  test("the bands the generator strokes paint exactly the black set, and their complement the yellow one") {
    val black  = HazardStripes.segments(tile, tile)
    val yellow = HazardStripes.segments(tile, tile, HazardStripes.BandWidth.toDouble)
    samples.foreach { case (x, y) =>
      if !onBandEdge(x, y) then
        assertEquals(covered(black, x, y), HazardStripes.isBlack(x, y), s"black band coverage at ($x, $y)")
        assertEquals(covered(yellow, x, y), !HazardStripes.isBlack(x, y), s"yellow band coverage at ($x, $y)")
    }
  }

  test("a tile that is not a whole number of periods is refused instead of seam-painted") {
    intercept[IllegalArgumentException](ProceduralTextures.warningStripeTexture(200, tile))
  }

  test("the asphalt grain stays inside its declared shade and is not a flat fill") {
    val bound  = Asphalt.FineShade + Asphalt.CoarseShade
    val shades = samples.map { case (x, y) => Asphalt.shadeAt(x.toInt, y.toInt) }
    shades.foreach(shade => assert(math.abs(shade) <= bound, s"grain $shade leaves the declared +/-$bound"))
    assert(shades.distinct.size >= 8, s"grain offers only ${shades.distinct.size} distinct shades over the tile")
  }

  test("the road markings stay inside the tile and the lane line keeps the tile edge") {
    val lane = RoadMarkings.laneLine(tile)
    val stop = RoadMarkings.stopLine(tile)
    assertEquals(lane.x, 0.0, "the lane line joins across repeats only from the tile edge")
    assert(lane.x + lane.width < tile, s"the lane line ${lane.width} px wide is not a line")
    assertEquals(stop.width, tile.toDouble, "the stop line crosses the whole road")
    assert(stop.y >= 0.0 && stop.y + stop.height <= tile, s"the stop line leaves the tile at ${stop.y}")
  }

  test("the crossbuck plate holds the wording inside its field") {
    val plate = CrossbuckSign.field(tile, tile)
    val band  = CrossbuckSign.wordBand(tile, tile)
    assert(band.x >= plate.x && band.x + band.width <= plate.x + plate.width, "the word band leaves the plate")
    assert(band.y >= plate.y && band.y + band.height <= plate.y + plate.height, "the word band leaves the plate")
    val wordingWidth = CrossbuckSign.Wording.length * CrossbuckSign.FontSize
    assert(wordingWidth <= band.width,
      s"${CrossbuckSign.Wording.length} characters need $wordingWidth px of ${band.width} px")
    assertEquals(CrossbuckSign.BladeAngle, math.Pi / 4.0, "the crossbuck blades are not square-on")
    val cornerReach = math.hypot(plate.width, plate.height)
    assert(CrossbuckSign.bladeLength(tile, tile) >= cornerReach, "a blade stops short of the plate corner")
  }
end ProceduralTexturesSuite
