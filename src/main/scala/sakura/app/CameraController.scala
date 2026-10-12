package sakura.app

import sakura.ThreeInterop
import sakura.world.Planet
import vendor.three.*

import scala.math

/** A viewpoint in the flat authoring frame: where the player stands, where they are looking, and
  * how far above the local ground their eye sits. The sphere enters only when the camera is
  * placed -- `Planet.basisAt` turns the flat point into a surface frame, exactly as upstream
  * `src/core/player.js` does.
  */
final private[sakura] case class CameraPose(
  x: Double,
  z: Double,
  yaw: Double,
  pitch: Double,
  lift: Double = 0.0
):
  def moved(dx: Double, dz: Double): CameraPose = copy(x = x + dx, z = z + dz)

private[sakura] enum CameraPreset(val key: Int, val label: String, val buttonLabel: String):
  case Crossing        extends CameraPreset(1, "The Level Crossing", "1 踏切")
  case Shotengai       extends CameraPreset(2, "さくら坂商店街", "2 商店街")
  case Onsen           extends CameraPreset(3, "湯の坂温泉街", "3 湯の坂")
  case Shrine          extends CameraPreset(4, "桜守神社", "4 神社")
  case Station         extends CameraPreset(5, "ひばり台駅", "5 駅")
  case CrossingBarrier extends CameraPreset(6, "Crossing Barrier Close-up", "6 遮断機")
  case Overbridge      extends CameraPreset(7, "Pedestrian Overbridge", "7 歩道橋")
  case SchoolApproach  extends CameraPreset(8, "High School Approach Road", "8 通学路")
  case Shoreline       extends CameraPreset(9, "Coastal Shoreline", "9 水辺")

  def pose: CameraPose = CameraPreset.poseFor(this)

end CameraPreset

private[sakura] object CameraPreset:

  /** Upstream's standing eye height (`src/core/player.js`). */
  private[app] val EyeHeight = 1.62

  /** The nine viewpoints, in flat authoring coordinates. The first four are the upstream hero
    * poses verbatim -- switching to one places the camera there with no interpolation, so an
    * automated capture is deterministic.
    */
  def poseFor(preset: CameraPreset): CameraPose = preset match
    case Crossing  => CameraPose(1.85, 13.6, 0.2, -0.01)
    case Shotengai => CameraPose(22.2, 20.0, 3.1416, 0.04)
    case Onsen     => CameraPose(-20.4, 48.8, 1.5708, 0.02)
    case Shrine    => CameraPose(-27.9, 14.0, 3.1416, 0.1)
    /* The platform and the overbridge deck are seven metres and one metre up; the lift is the
     * difference between their surface and the ground under them. */
    case Station         => CameraPose(26.75, -3.6, 1.742, -0.08, lift = 0.98)
    case CrossingBarrier => CameraPose(4.9, 6.8, 0.227, -0.05)
    case Overbridge      => CameraPose(41.0, 0.0, 1.5708, -0.16, lift = 7.2)
    case SchoolApproach  => CameraPose(1.5, -22.0, -0.14, 0.02)
    case Shoreline       => CameraPose(-34.0, -20.6, 1.62, -0.07)

  val all: Vector[CameraPreset] = CameraPreset.values.toVector

  def fromKey(key: String): Option[CameraPreset] = all.find(_.key.toString == key)
end CameraPreset

private[sakura] object CameraController:

  /** Places the camera on the planet surface.
    *
    * The simulation stays in flat (x, z) authoring space; only the presentation is spherical.
    * The flat point becomes a surface point, the tangent frame becomes the camera's basis, and
    * the yaw and pitch ride on top of it as a local rotation -- `basisQ * yawQ * pitchQ` -- so
    * "up" is always away from the planet centre and the whole frame rolls as you walk round it.
    */
  def place(camera: PerspectiveCamera, pose: CameraPose, eyeY: Double): Unit =
    val basis = Planet.basisAt(pose.x, pose.z)
    val basisQ = new Quaternion().setFromRotationMatrix(
      new Matrix4().makeBasis(basis.east, basis.up, basis.north)
    )
    val localQ = new Quaternion().setFromEuler(new Euler(pose.pitch, pose.yaw, 0.0, "YXZ"))
    camera.position.copy(ThreeInterop.asVector3Like(Planet.positionAt(pose.x, eyeY, pose.z)))
    camera.quaternion.copy(ThreeInterop.asQuaternionLike(basisQ)).multiply(localQ)
    camera.up.copy(ThreeInterop.asVector3Like(basis.up))
  end place

  /** The flat-space forward direction of a viewpoint: what "walk forward" means for a pose. */
  def forward(pose: CameraPose): (Double, Double) = (-math.sin(pose.yaw), -math.cos(pose.yaw))
end CameraController
