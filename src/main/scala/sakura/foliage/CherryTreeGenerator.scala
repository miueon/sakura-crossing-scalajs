package sakura.foliage

import scala.math
import scala.util.Random

import sakura.{Palette, PlanetCurvature, TerrainHeightmap, ToonMaterial}
import vendor.three.*

/** One seated cherry tree: root position on the flat plane plus the seed that drives its shape. */
final case class CherryPlacement(x: Double, z: Double, scale: Double, seed: Long)

/** Pure per-grove instance plan: dark timber limbs and layered blossom clusters on the flat plane. */
private[foliage] final case class CherryPlan(
  wood: List[PlannedTransform],
  canopyLight: List[PlannedTransform],
  canopyDeep: List[PlannedTransform]
)

/** Procedural cherry trees (桜の木) as three InstancedMesh batches: one dark timber batch for
  * trunks and branching limbs, and two blossom batches (light canopy masses with deep accents
  * tucked beneath) so a whole grove costs three draw calls regardless of tree count.
  */
object CherryTreeGenerator:

  def plan(placements: List[CherryPlacement]): CherryPlan =
    val trees = placements.map(tree)
    CherryPlan(
      wood = trees.flatMap(_.wood),
      canopyLight = trees.flatMap(_.canopyLight),
      canopyDeep = trees.flatMap(_.canopyDeep)
    )

  def build(plan: CherryPlan, curvature: PlanetCurvature): SceneBatch =
    val woodGeometry     = new CylinderGeometry(1.0, 1.0, 1.0, 7.0, 1.0)
    val blossomGeometry  = new SphereGeometry(1.0, 9.0, 7.0)
    val woodMaterial     = ToonMaterial.instancedCelMaterial(Palette.darkTimber, curvature)
    val lightMaterial    = ToonMaterial.instancedBlossomMaterial(Palette.sakuraPink, curvature)
    val deepMaterial     = ToonMaterial.instancedBlossomMaterial(Palette.sakuraDeep, curvature)
    SceneBatch(
      roots = List(
        SceneBatch.instancedMesh(woodGeometry, woodMaterial, plan.wood),
        SceneBatch.instancedMesh(blossomGeometry, lightMaterial, plan.canopyLight),
        SceneBatch.instancedMesh(blossomGeometry, deepMaterial, plan.canopyDeep)
      ),
      geometries = List(woodGeometry, blossomGeometry),
      materials = List(woodMaterial, lightMaterial, deepMaterial)
    )

  /** One tree: a leaning trunk, four to six main limbs fanned around it, occasional upright twigs,
    * and blossom blobs crowning every limb and twig plus a crown cluster over the trunk top.
    */
  private def tree(placement: CherryPlacement): CherryPlan =
    val rng    = new Random(placement.seed)
    val ground = TerrainHeightmap.hillAt(placement.x, placement.z)
    val yaw    = rng.nextDouble() * math.Pi * 2.0

    val trunkLength = placement.scale * between(rng, 2.7, 3.4)
    val trunkRadius = placement.scale * 0.14
    val trunkDir    = leanDirection(between(rng, 0.02, 0.12), yaw + rng.nextDouble() * math.Pi * 2.0)
    val root        = (placement.x, ground, placement.z)

    val wood  = List.newBuilder[PlannedTransform]
    val light = List.newBuilder[PlannedTransform]
    val deep  = List.newBuilder[PlannedTransform]

    wood += segment(root, trunkDir, trunkLength, trunkRadius)

    val limbCount = 4 + rng.nextInt(3)
    (0 until limbCount).foreach { limb =>
      val azimuth = yaw + limb * (2.0 * math.Pi / limbCount) + between(rng, -0.4, 0.4)
      val dir     = leanDirection(between(rng, 0.5, 1.0), azimuth)
      val attach  = slide(root, trunkDir, trunkLength * between(rng, 0.62, 0.94))
      val length  = placement.scale * between(rng, 1.2, 2.1)
      val radius  = trunkRadius * 0.45
      wood += segment(attach, dir, length, radius)

      val tip = slide(attach, dir, length)
      light += blossom(rng, tip, placement.scale * between(rng, 0.75, 1.3))
      deep += blossom(rng, slide(tip, (0.0, -1.0, 0.0), placement.scale * 0.35), placement.scale * between(rng, 0.4, 0.6))

      if rng.nextDouble() < 0.55 then
        val twigDir = normalize(add(dir, (between(rng, -0.3, 0.3), 0.9, between(rng, -0.3, 0.3))))
        val twigBase = slide(attach, dir, length * between(rng, 0.55, 0.95))
        val twigLength = placement.scale * between(rng, 0.5, 0.9)
        wood += segment(twigBase, twigDir, twigLength, radius * 0.55)
        light += blossom(rng, slide(twigBase, twigDir, twigLength), placement.scale * between(rng, 0.45, 0.7))
    }

    val trunkTop = slide(root, trunkDir, trunkLength)
    (0 until 3 + rng.nextInt(3)).foreach { _ =>
      val crownAzimuth = rng.nextDouble() * math.Pi * 2.0
      val crownRadius  = placement.scale * between(rng, 0.2, 1.2)
      light += blossom(
        rng,
        (trunkTop._1 + crownRadius * math.cos(crownAzimuth), trunkTop._2 + placement.scale * between(rng, 0.1, 0.6), trunkTop._3 + crownRadius * math.sin(crownAzimuth)),
        placement.scale * between(rng, 0.85, 1.4)
      )
    }

    CherryPlan(wood.result(), light.result(), deep.result())

  /** A limb segment: a unit cylinder centred on the segment's midpoint, oriented from +y onto its
    * direction and scaled to (radius, length, radius).
    */
  private def segment(
    base: (Double, Double, Double),
    dir: (Double, Double, Double),
    length: Double,
    radius: Double
  ): PlannedTransform =
    val (axis, angle) = upToAxisAngle(dir)
    PlannedTransform(
      position = slide(base, dir, length / 2.0),
      axis = axis,
      angle = angle,
      scale = (radius, length, radius)
    )

  private def blossom(rng: Random, center: (Double, Double, Double), radius: Double): PlannedTransform =
    val (axis, angle) = facetRotation(rng)
    PlannedTransform(
      position = center,
      axis = axis,
      angle = angle,
      scale = (radius, radius * between(rng, 0.72, 0.85), radius)
    )

  /** Direction leaning `lean` radians away from vertical toward `azimuth`. */
  private def leanDirection(lean: Double, azimuth: Double): (Double, Double, Double) =
    (math.sin(lean) * math.cos(azimuth), math.cos(lean), math.sin(lean) * math.sin(azimuth))

  /** Minimal rotation taking +y onto `dir`: the axis is cross(up, dir) = (dz, 0, -dx). A limb is a
    * cylinder, symmetric about its own axis, so the twist this leaves free is invisible.
    */
  private def upToAxisAngle(dir: (Double, Double, Double)): ((Double, Double, Double), Double) =
    val axisLength = math.sqrt(dir._1 * dir._1 + dir._3 * dir._3)
    if axisLength < 1e-6 then ((0.0, 0.0, 1.0), 0.0)
    else ((dir._3 / axisLength, 0.0, -dir._1 / axisLength), math.acos(math.min(1.0, math.max(-1.0, dir._2))))

  /** Random orientation for the low-poly blossom spheres so their facets do not align across a
    * grove: a random axis paired with a random turn.
    */
  private def facetRotation(rng: Random): ((Double, Double, Double), Double) =
    val raw    = (between(rng, -1.0, 1.0), between(rng, -1.0, 1.0), between(rng, -1.0, 1.0))
    val length = math.sqrt(raw._1 * raw._1 + raw._2 * raw._2 + raw._3 * raw._3)
    val axis   = if length < 1e-6 then (0.0, 0.0, 1.0) else (raw._1 / length, raw._2 / length, raw._3 / length)
    (axis, rng.nextDouble() * 2.0 * math.Pi)

  private def add(
      a: (Double, Double, Double),
      b: (Double, Double, Double)
  ): (Double, Double, Double) =
    (a._1 + b._1, a._2 + b._2, a._3 + b._3)

  private def slide(base: (Double, Double, Double), dir: (Double, Double, Double), distance: Double): (Double, Double, Double) =
    (base._1 + dir._1 * distance, base._2 + dir._2 * distance, base._3 + dir._3 * distance)

  private def normalize(v: (Double, Double, Double)): (Double, Double, Double) =
    val length = math.sqrt(v._1 * v._1 + v._2 * v._2 + v._3 * v._3)
    (v._1 / length, v._2 / length, v._3 / length)

  private def between(rng: Random, low: Double, high: Double): Double =
    low + rng.nextDouble() * (high - low)
end CherryTreeGenerator
