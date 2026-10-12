// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "PerspectiveCamera")
class PerspectiveCamera(fov_1: js.UndefOr[Double] = js.undefined, aspect_1: js.UndefOr[Double] = js.undefined, near_1: js.UndefOr[Double] = js.undefined, far_1: js.UndefOr[Double] = js.undefined) extends Camera:
  var aspect: Double = js.native
  var far: Double = js.native
  var filmGauge: Double = js.native
  var filmOffset: Double = js.native
  var focus: Double = js.native
  var fov: Double = js.native
  def isPerspectiveCamera: Boolean = js.native
  var near: Double = js.native
  var view: ArrayCameraView | Null = js.native
  var zoom: Double = js.native
  def clearViewOffset(): Unit = js.native
  @JSName("copy")
  protected override def copy$raw(`object`: Object3D[Object3DEventMap], recursive: js.UndefOr[Boolean] = js.undefined): PerspectiveCamera = js.native
  def getEffectiveFOV(): Double = js.native
  def getFilmHeight(): Double = js.native
  def getFilmWidth(): Double = js.native
  def getFocalLength(): Double = js.native
  def getViewBounds(distance: Double, minTarget: Vector2, maxTarget: Vector2): Unit = js.native
  def getViewSize(distance: Double, target: Vector2): Vector2 = js.native
  def setFocalLength(focalLength: Double): Unit = js.native
  def setViewOffset(fullWidth: Double, fullHeight: Double, x: Double, y: Double, width: Double, height: Double): Unit = js.native
  override def toJSON(meta: js.UndefOr[JSONMeta] = js.undefined): PerspectiveCameraJSON = js.native
  def updateProjectionMatrix(): Unit = js.native

object PerspectiveCamera:
  extension [Self <: PerspectiveCamera](self: Self)
    inline def copy(source: PerspectiveCamera, recursive: js.UndefOr[Boolean] = js.undefined): Self = self.copy$raw(source, recursive).asInstanceOf[Self]
