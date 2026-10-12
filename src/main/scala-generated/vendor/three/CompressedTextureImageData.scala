// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait CompressedTextureImageData extends js.Object:
  var height: Double = js.native
  var width: Double = js.native

object CompressedTextureImageData:
  def apply(
    width: Double,
    height: Double
  ): CompressedTextureImageData =
    js.Dynamic.literal(
      "width" -> width.asInstanceOf[js.Any],
      "height" -> height.asInstanceOf[js.Any]
    ).asInstanceOf[CompressedTextureImageData]
