// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait FramebufferTextureImageData extends js.Object:
  var height: Double = js.native
  var width: Double = js.native

object FramebufferTextureImageData:
  def apply(
    width: Double,
    height: Double
  ): FramebufferTextureImageData =
    js.Dynamic.literal(
      "width" -> width.asInstanceOf[js.Any],
      "height" -> height.asInstanceOf[js.Any]
    ).asInstanceOf[FramebufferTextureImageData]
