// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait DepthTextureImageData extends js.Object:
  var depth: Double = js.native
  var height: js.UndefOr[Double] = js.native
  var width: js.UndefOr[Double] = js.native

object DepthTextureImageData:
  def apply(
    width: js.UndefOr[Double],
    height: js.UndefOr[Double],
    depth: Double
  ): DepthTextureImageData =
    js.Dynamic.literal(
      "width" -> width.asInstanceOf[js.Any],
      "height" -> height.asInstanceOf[js.Any],
      "depth" -> depth.asInstanceOf[js.Any]
    ).asInstanceOf[DepthTextureImageData]
