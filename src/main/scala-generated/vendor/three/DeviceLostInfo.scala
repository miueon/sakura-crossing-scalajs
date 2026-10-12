// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait DeviceLostInfo extends js.Object:
  var api: "WebGL" | "WebGPU" = js.native
  var message: String = js.native
  var originalEvent: facadecontract.std.JSUnknown = js.native
  var reason: String | Null = js.native

object DeviceLostInfo:
  def apply(
    api: "WebGL" | "WebGPU",
    message: String,
    reason: String | Null,
    originalEvent: facadecontract.std.JSUnknown
  ): DeviceLostInfo =
    js.Dynamic.literal(
      "api" -> api.asInstanceOf[js.Any],
      "message" -> message.asInstanceOf[js.Any],
      "reason" -> reason.asInstanceOf[js.Any],
      "originalEvent" -> originalEvent.asInstanceOf[js.Any]
    ).asInstanceOf[DeviceLostInfo]
