// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait SerializedImageData extends js.Object:
  var data: js.Array[Double] = js.native
  var height: Double = js.native
  var `type`: String = js.native
  var width: Double = js.native

object SerializedImageData:
  def apply(
    data: js.Array[Double],
    width: Double,
    height: Double,
    `type`: String
  ): SerializedImageData =
    js.Dynamic.literal(
      "data" -> data.asInstanceOf[js.Any],
      "width" -> width.asInstanceOf[js.Any],
      "height" -> height.asInstanceOf[js.Any],
      "type" -> `type`.asInstanceOf[js.Any]
    ).asInstanceOf[SerializedImageData]
