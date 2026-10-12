// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait ArrayCameraView extends js.Object:
  var enabled: Boolean = js.native
  var fullHeight: Double = js.native
  var fullWidth: Double = js.native
  var height: Double = js.native
  var offsetX: Double = js.native
  var offsetY: Double = js.native
  var width: Double = js.native

object ArrayCameraView:
  def apply(
    enabled: Boolean,
    fullWidth: Double,
    fullHeight: Double,
    offsetX: Double,
    offsetY: Double,
    width: Double,
    height: Double
  ): ArrayCameraView =
    js.Dynamic.literal(
      "enabled" -> enabled.asInstanceOf[js.Any],
      "fullWidth" -> fullWidth.asInstanceOf[js.Any],
      "fullHeight" -> fullHeight.asInstanceOf[js.Any],
      "offsetX" -> offsetX.asInstanceOf[js.Any],
      "offsetY" -> offsetY.asInstanceOf[js.Any],
      "width" -> width.asInstanceOf[js.Any],
      "height" -> height.asInstanceOf[js.Any]
    ).asInstanceOf[ArrayCameraView]
