package sakura

import scala.math

/** Flat-plane ground patch feeding the renderer.
  *
  * Positions carry [[TerrainHeightmap]] elevation and stay flat; the ground material's vertex
  * shader applies the planet bend, so the mesh must be subdivided finely enough to follow the
  * bend instead of cutting through it (see [[PlanetCurvature.subdivisions]]).
  *
  * The grid sits `TerrainDrop` under the `groundY` reference plane so every paved surface laid on
  * the plane (+0.012 carriageway, +0.004 gutter) wins the depth test — the upstream `TERRAIN_DROP`
  * contract. Each row is additionally dropped by its own chord excess: the grid samples the
  * street profile at its rows and chords between them, and a chord across the convex half of the
  * profile runs above the curve by up to `f''h²/8`, which at these row widths is most of the
  * drop budget. Precompensating keeps the *interpolated* surface under the plane everywhere, and
  * the planet bend can only sag it further down, never up.
  */
object GroundGeometry:

  /** How far below the `groundY` reference plane the graded terrain grid is drawn. */
  val TerrainDrop: Double = 0.015

  /** Interleaved `x y z` positions for a `span` x `span` patch centred on the origin, split into
    * `segments` x `segments` cells and emitted as two triangles per cell.
    *
    * Non-indexed: the scaffold draws one opaque material and shares no per-cell detail across
    * cells, so an index buffer would add nothing but another buffer to own.
    */
  def positions(span: Double, segments: Int): Vector[Double] =
    val cells =
      for
        i <- 0 until segments
        j <- 0 until segments
      yield cell(span, segments, i, j)
    cells.flatten.toVector

  private def cell(span: Double, segments: Int, i: Int, j: Int): Vector[Double] =
    val p00 = corner(span, segments, i, j)
    val p10 = corner(span, segments, i + 1, j)
    val p01 = corner(span, segments, i, j + 1)
    val p11 = corner(span, segments, i + 1, j + 1)
    p00 ++ p01 ++ p10 ++ p11 ++ p10 ++ p01

  private def corner(span: Double, segments: Int, i: Int, j: Int): Vector[Double] =
    val x    = -span / 2.0 + i * (span / segments)
    val z    = -span / 2.0 + j * (span / segments)
    val cell = span / segments
    Vector(x, rowY(z, cell), z)

  /** The graded row height at `z`: the dropped profile plane minus this row's chord excess,
    * measured against the rows on both sides the way the upstream grid precompensates.
    */
  private def rowY(z: Double, cell: Double): Double =
    def plane(value: Double): Double = TerrainHeightmap.groundY(value) - TerrainDrop
    val excess = Seq(-1.0, 1.0).foldLeft(0.0) { (worst, side) =>
      val chordMid = (plane(z) + plane(z + side * cell)) / 2.0
      math.max(worst, chordMid - plane(z + side * cell / 2.0))
    }
    plane(z) - math.max(0.0, excess)
end GroundGeometry
