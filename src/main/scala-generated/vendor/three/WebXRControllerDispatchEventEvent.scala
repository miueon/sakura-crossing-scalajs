// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait WebXRControllerDispatchEventEvent extends js.Object:
  var data: js.UndefOr[XRInputSource] = js.native
  var `type`: "connected" | "disconnected" | "end" | "frameratechange" | "select" | "selectend" | "selectstart" | "squeeze" | "squeezeend" | "squeezestart" | "visibilitychange" = js.native

object WebXRControllerDispatchEventEvent:
  def apply(
    `type`: "connected" | "disconnected" | "end" | "frameratechange" | "select" | "selectend" | "selectstart" | "squeeze" | "squeezeend" | "squeezestart" | "visibilitychange",
    data: js.UndefOr[XRInputSource] = js.undefined
  ): WebXRControllerDispatchEventEvent =
    val _res = js.Dynamic.literal("type" -> `type`.asInstanceOf[js.Any])
    data.foreach(v => _res.updateDynamic("data")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[WebXRControllerDispatchEventEvent]
