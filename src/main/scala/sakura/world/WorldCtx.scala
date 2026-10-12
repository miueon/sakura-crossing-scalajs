package sakura.world

import sakura.ThreeInterop
import vendor.three.*

import scala.collection.mutable

/** The builder context threaded through every world module, ported 1:1 from
  * upstream `src/world/index.js`: a shared scene graph root plus the collider,
  * platform, cut, interaction and updater registries the districts fill.
  */
/** `top` is live: the crossing's boom blocks are colliders that only exist while the arms are
  * down, so the world toggles their ceiling rather than rebuilding the registry every frame.
  */
final case class Collider(x0: Double, z0: Double, x1: Double, z1: Double, var top: Double, bottom: Option[Double] = None)

final case class Platform(x0: Double, x1: Double, z0: Double, z1: Double, top: Double)

final case class Cut(x0: Double, x1: Double, z0: Double, z1: Double, top: Double)

final case class Interactable(hitbox: Mesh[?, ?, ?], label: String, action: () => Unit)

final case class SakuraSpot(
  x: Double,
  z: Double,
  y: Double = 0.0,
  scale: Double = 1.0,
  seed: Int = 1,
  lean: Double = 0.0,
  leanDir: Option[Double] = None,
  tone: Option[Int] = None,
  collide: Boolean = true
)

final case class ShrubSpot(
  x: Double,
  z: Double,
  y: Double = 0.0,
  count: Int = 3,
  r: Double = 0.55,
  spread: Double = 1.0,
  seed: Int = 11
)

final case class BambooClump(
  x: Double,
  z: Double,
  y: Double = 0.0,
  n: Int = 9,
  scale: Double = 1.0,
  spread: Double = 1.2,
  seed: Int = 5
)

final case class GroveSpot(
  x: Double,
  z: Double,
  y: Double = 0.0,
  scale: Double = 1.0,
  seed: Int = 1,
  lean: Double = 0.0,
  leanDir: Option[Double] = None,
  spread: Double = 1.0,
  tone: Option[Int] = None,
  willow: Boolean = false,
  collide: Boolean = true
)

final case class CedarSpot(
  x: Double,
  z: Double,
  y: Double = 0.0,
  scale: Double = 1.0,
  seed: Int = 3,
  lean: Double = 0.0,
  leanDir: Option[Double] = None,
  collide: Boolean = true
)

final case class PetalPatch(x: Double, z: Double, y: Double = 0.0, n: Int = 90, w: Double, d: Double)

/** The planting a district hands back for `index.js` to plant once, at the end: the sakura
  * builder merges every tree in the world into one mesh plus three instanced canopies, so it
  * cannot run until every district has said what it wants planted.
  */
final case class Planting(
  sakura: Vector[SakuraSpot] = Vector.empty,
  shrubs: Vector[ShrubSpot] = Vector.empty,
  bamboo: Vector[BambooClump] = Vector.empty,
  grove: Vector[GroveSpot] = Vector.empty,
  cedar: Vector[CedarSpot] = Vector.empty,
  petals: Vector[PetalPatch] = Vector.empty
)

/** One `houseDefs` entry from upstream `index.js`: the footprint the housing sweep builds, plus
  * the per-plot slots that nudge its clutter off a neighbour's. The dressing slots live here
  * because `dressHousing` reads them back off the same definitions the generator was given.
  */
final case class HouseDef(
  x: Double,
  z: Double,
  w: Double,
  d: Double,
  face: Buildings.Face,
  seed: Int,
  wall: Int,
  roof: Int,
  roofKind: Buildings.RoofKind,
  floors: Int = 2,
  postBoxOut: Option[Double] = None,
  postBoxSink: Option[Double] = None,
  postBoxAlong: Option[Double] = None,
  gomiSink: Option[Double] = None,
  gomiOut: Option[Double] = None,
  gomiSignAlong: Option[Double] = None,
  planterAlong: Option[Double] = None,
  planterOut: Option[Double] = None,
  planterSpacing: Option[Double] = None,
  planterSink: Option[Double] = None,
  umbrellaSink: Option[Double] = None,
  bikeOut: Option[Double] = None,
  bikeLift: Option[Double] = None,
  skipBike: Boolean = false
)

final case class DistrictResult(
  name: String,
  root: Group[Object3DEventMap],
  planting: Planting = Planting(),
  update: Option[Double => Unit] = None
)

final class WorldCtx(val scene: Scene[Object3DEventMap], val root: Group[Object3DEventMap]):
  val colliders     = mutable.ListBuffer.empty[Collider]
  val platforms     = mutable.ListBuffer.empty[Platform]
  val cuts          = mutable.ListBuffer.empty[Cut]
  val interactables = mutable.ListBuffer.empty[Interactable]
  val updaters      = mutable.ListBuffer.empty[Double => Unit]

  def add(obj: Object3D[?]): obj.type =
    ThreeInterop.addChild(root, obj)
    obj

  def collide(x0: Double, z0: Double, x1: Double, z1: Double, top: Double, bottom: Option[Double] = None): Unit =
    colliders += Collider(math.min(x0, x1), math.min(z0, z1), math.max(x0, x1), math.max(z0, z1), top, bottom)

  def platform(x0: Double, x1: Double, z0: Double, z1: Double, top: Double): Unit =
    platforms += Platform(x0, x1, z0, z1, top)

  def cut(x0: Double, x1: Double, z0: Double, z1: Double, top: Double): Unit =
    cuts += Cut(x0, x1, z0, z1, top)

  /** The height a prop actually stands on: cuts and platforms included, the same
    * answer `world.heightAt` gives. The hills layer joins this query when its
    * chunk lands; until then the graded street plane carries it.
    */
  def groundAt(x: Double, z: Double): Double =
    var height = Street.streetHeight(x, z) + Planet.reliefAt(x, z)
    cuts.foreach(cutBox =>
      if x > cutBox.x0 && x < cutBox.x1 && z > cutBox.z0 && z < cutBox.z1 then height = math.min(height, cutBox.top)
    )
    platforms.foreach(platformBox =>
      if x > platformBox.x0 && x < platformBox.x1 && z > platformBox.z0 && z < platformBox.z1 then
        height = math.max(height, platformBox.top)
    )
    height

  def interact(hitbox: Mesh[?, ?, ?], label: String, action: () => Unit): Unit =
    interactables += Interactable(hitbox, label, action)

  def update(fn: Double => Unit): Unit = updaters += fn
end WorldCtx
