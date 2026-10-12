// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait CompressedArrayTextureImageData extends CompressedTextureImageData:
  var depth: Double = js.native

object CompressedArrayTextureImageData:
  def apply(
    depth: Double,
    width: Double,
    height: Double
  ): CompressedArrayTextureImageData =
    js.Dynamic.literal(
      "depth" -> depth.asInstanceOf[js.Any],
      "width" -> width.asInstanceOf[js.Any],
      "height" -> height.asInstanceOf[js.Any]
    ).asInstanceOf[CompressedArrayTextureImageData]
