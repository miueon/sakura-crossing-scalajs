// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait InstancedInterleavedBufferToJSONReturn extends js.Object:
  var buffer: String = js.native
  var stride: Double = js.native
  var `type`: String = js.native
  var uuid: String = js.native

object InstancedInterleavedBufferToJSONReturn:
  def apply(
    uuid: String,
    buffer: String,
    `type`: String,
    stride: Double
  ): InstancedInterleavedBufferToJSONReturn =
    js.Dynamic.literal(
      "uuid" -> uuid.asInstanceOf[js.Any],
      "buffer" -> buffer.asInstanceOf[js.Any],
      "type" -> `type`.asInstanceOf[js.Any],
      "stride" -> stride.asInstanceOf[js.Any]
    ).asInstanceOf[InstancedInterleavedBufferToJSONReturn]
