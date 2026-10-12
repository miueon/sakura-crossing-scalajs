package sakura.foliage

import scala.math

import sakura.{Palette, PlanetCurvature, TerrainHeightmap, ToonMaterial}
import vendor.three.*

/** Pure corridor furniture plan: every instance seats at the terrain height of its own (x, z). */
private[foliage] final case class FencePlan(
  concrete: List[PlannedTransform],
  timber: List[PlannedTransform]
)

/** Batched lineside furniture for the crossing corridor: roadside guardrails along the road (a z
  * axis corridor), the lineside fence flanking the implied railway (an x axis corridor), and the
  * road's utility poles. Two draw calls: one concrete box batch and one timber cylinder batch.
  */
object InstancedFences:

  private val railHalfSpan      = 96.0
  private val linesideOffset    = 9.0
  private val linesideSpacing   = 3.0
  private val linesidePostH     = 1.1
  private val guardOffset       = 8.0
  private val guardHalfSpan     = 64.0
  private val guardMouth        = 14.0
  private val guardSpacing      = 4.0
  private val guardPostH        = 0.7
  private val guardRailH        = 0.55
  private val poleOffset        = 13.0
  private val poleSpacing       = 18.0
  private val poleHalfSpan      = 63.0
  private val poleHeight        = 7.2
  private val crossarmH         = 6.7
  private val crossarmLength    = 1.6

  def plan(roadCenterX: Double, roadHalfWidth: Double): FencePlan =
    FencePlan(
      concrete = linesideFence(roadCenterX, roadHalfWidth) ++ guardrails(roadCenterX),
      timber = utilityPoles(roadCenterX)
    )

  def build(plan: FencePlan, curvature: PlanetCurvature): SceneBatch =
    val boxGeometry      = new BoxGeometry(1.0, 1.0, 1.0)
    val cylinderGeometry = new CylinderGeometry(1.0, 1.0, 1.0, 6.0, 1.0)
    val concreteMaterial = ToonMaterial.instancedCelMaterial(Palette.concrete, curvature)
    val timberMaterial   = ToonMaterial.instancedCelMaterial(Palette.timber, curvature)
    SceneBatch(
      roots = List(
        SceneBatch.instancedMesh(boxGeometry, concreteMaterial, plan.concrete),
        SceneBatch.instancedMesh(cylinderGeometry, timberMaterial, plan.timber)
      ),
      geometries = List(boxGeometry, cylinderGeometry),
      materials = List(concreteMaterial, timberMaterial)
    )

  /** Lineside fence: posts plus two horizontal rails per span, along both flanks of the railway. */
  private def linesideFence(roadCenterX: Double, roadHalfWidth: Double): List[PlannedTransform] =
    val instances = List.newBuilder[PlannedTransform]
    val roadClearance = roadHalfWidth + 1.6
    val runs = List(
      (roadCenterX - railHalfSpan, roadCenterX - roadClearance),
      (roadCenterX + roadClearance, roadCenterX + railHalfSpan)
    )
    for side <- Seq(-1.0, 1.0); (start, end) <- runs do
      val z = side * linesideOffset
      val spans = ((end - start) / linesideSpacing).toInt
      (0 to spans).foreach { step =>
        val x = start + step * linesideSpacing
        instances += post(x, z, linesidePostH, 0.07, 0.07)
        if step < spans then
          val spanCenter = x + linesideSpacing / 2.0
          Seq(0.55, 0.95).foreach { railHeight =>
            instances += PlannedTransform(
              position = (spanCenter, TerrainHeightmap.hillAt(spanCenter, z) + railHeight, z),
              axis = zAxis,
              angle = 0.0,
              scale = (linesideSpacing + 0.06, 0.05, 0.04)
            )
          }
      }
    instances.result()

  /** Roadside guardrails: posts plus one rail per span, interrupted at the crossing mouth. */
  private def guardrails(roadCenterX: Double): List[PlannedTransform] =
    val instances = List.newBuilder[PlannedTransform]
    val spans     = (guardHalfSpan - guardMouth) / guardSpacing
    for side <- Seq(-1.0, 1.0); run <- Seq(-1.0, 1.0) do
      val x = roadCenterX + side * guardOffset
      (0 to spans.toInt).foreach { step =>
        val z = run * (guardMouth + step * guardSpacing)
        instances += post(x, z, guardPostH, 0.12, 0.1)
        if step < spans.toInt then
          val spanCenter = z + run * guardSpacing / 2.0
          instances += PlannedTransform(
            position = (x, TerrainHeightmap.hillAt(x, spanCenter) + guardRailH, spanCenter),
            axis = zAxis,
            angle = 0.0,
            scale = (0.06, 0.16, guardSpacing + 0.06)
          )
      }
    instances.result()

  /** Utility poles with a single crossarm; the pole carries no collider-scale mass, so one slim
    * cylinder per pole reads correctly at distance.
    */
  private def utilityPoles(roadCenterX: Double): List[PlannedTransform] =
    val poleCount = (2.0 * poleHalfSpan / poleSpacing).toInt + 1
    (0 until poleCount).flatMap { step =>
      val z      = -poleHalfSpan + step * poleSpacing
      val x      = roadCenterX + poleOffset
      val ground = TerrainHeightmap.hillAt(x, z)
      List(
        PlannedTransform(
          position = (x, ground + poleHeight / 2.0, z),
          axis = zAxis,
          angle = 0.0,
          scale = (0.09, poleHeight, 0.09)
        ),
        PlannedTransform(
          position = (x, ground + crossarmH, z),
          axis = zAxis,
          angle = math.Pi / 2.0,
          scale = (0.045, crossarmLength, 0.045)
        )
      )
    }.toList

  private def post(x: Double, z: Double, height: Double, width: Double, depth: Double): PlannedTransform =
    PlannedTransform(
      position = (x, TerrainHeightmap.hillAt(x, z) + height / 2.0, z),
      axis = zAxis,
      angle = 0.0,
      scale = (width, height, depth)
    )

  private val zAxis: (Double, Double, Double) = (0.0, 0.0, 1.0)
end InstancedFences
