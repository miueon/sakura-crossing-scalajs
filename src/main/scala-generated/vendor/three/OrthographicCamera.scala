// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "OrthographicCamera")
class OrthographicCamera(left_1: js.UndefOr[Double] = js.undefined, right_1: js.UndefOr[Double] = js.undefined, top_1: js.UndefOr[Double] = js.undefined, bottom_1: js.UndefOr[Double] = js.undefined, near_1: js.UndefOr[Double] = js.undefined, far_1: js.UndefOr[Double] = js.undefined) extends Camera:
  var bottom: Double = js.native
  var far: Double = js.native
  def isOrthographicCamera: Boolean = js.native
  var left: Double = js.native
  var near: Double = js.native
  var right: Double = js.native
  var top: Double = js.native
  var view: ArrayCameraView | Null = js.native
  var zoom: Double = js.native
  def clearViewOffset(): Unit = js.native
  @JSName("copy")
  protected override def copy$raw(`object`: Object3D[Object3DEventMap], recursive: js.UndefOr[Boolean] = js.undefined): OrthographicCamera = js.native
  def setViewOffset(fullWidth: Double, fullHeight: Double, x: Double, y: Double, width: Double, height: Double): Unit = js.native
  override def toJSON(meta: js.UndefOr[JSONMeta] = js.undefined): OrthographicCameraJSON = js.native
  def updateProjectionMatrix(): Unit = js.native

object OrthographicCamera:
  extension [Self <: OrthographicCamera](self: Self)
    inline def copy(source: OrthographicCamera, recursive: js.UndefOr[Boolean] = js.undefined): Self = self.copy$raw(source, recursive).asInstanceOf[Self]
