// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait InterleavedBufferAttributeToJSONReturn extends js.Object:
  var data: String = js.native
  var isInterleavedBufferAttribute: true = js.native
  var itemSize: Double = js.native
  var normalized: Boolean = js.native
  var offset: Double = js.native

object InterleavedBufferAttributeToJSONReturn:
  def apply(
    isInterleavedBufferAttribute: true = true,
    itemSize: Double,
    data: String,
    offset: Double,
    normalized: Boolean
  ): InterleavedBufferAttributeToJSONReturn =
    js.Dynamic.literal(
      "isInterleavedBufferAttribute" -> isInterleavedBufferAttribute.asInstanceOf[js.Any],
      "itemSize" -> itemSize.asInstanceOf[js.Any],
      "data" -> data.asInstanceOf[js.Any],
      "offset" -> offset.asInstanceOf[js.Any],
      "normalized" -> normalized.asInstanceOf[js.Any]
    ).asInstanceOf[InterleavedBufferAttributeToJSONReturn]
