// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait WebXRSpaceEventMap extends Object3DEventMap:
  var connected: WebXRSpaceEventMapSelect = js.native
  var disconnected: WebXRSpaceEventMapSelect = js.native
  var move: js.Any = js.native
  var pinchend: WebXRSpaceEventMapPinchend = js.native
  var pinchstart: WebXRSpaceEventMapPinchend = js.native
  var select: WebXRSpaceEventMapSelect = js.native
  var selectend: WebXRSpaceEventMapSelect = js.native
  var selectstart: WebXRSpaceEventMapSelect = js.native
  var squeeze: WebXRSpaceEventMapSelect = js.native
  var squeezeend: WebXRSpaceEventMapSelect = js.native
  var squeezestart: WebXRSpaceEventMapSelect = js.native

object WebXRSpaceEventMap:
  def apply(
    select: WebXRSpaceEventMapSelect,
    selectstart: WebXRSpaceEventMapSelect,
    selectend: WebXRSpaceEventMapSelect,
    squeeze: WebXRSpaceEventMapSelect,
    squeezestart: WebXRSpaceEventMapSelect,
    squeezeend: WebXRSpaceEventMapSelect,
    connected: WebXRSpaceEventMapSelect,
    disconnected: WebXRSpaceEventMapSelect,
    pinchend: WebXRSpaceEventMapPinchend,
    pinchstart: WebXRSpaceEventMapPinchend,
    move: js.Any,
    added: js.Any,
    removed: js.Any,
    childadded: AmbientLightAddEventListenerChildadded,
    childremoved: AmbientLightAddEventListenerChildadded
  ): WebXRSpaceEventMap =
    js.Dynamic.literal(
      "select" -> select.asInstanceOf[js.Any],
      "selectstart" -> selectstart.asInstanceOf[js.Any],
      "selectend" -> selectend.asInstanceOf[js.Any],
      "squeeze" -> squeeze.asInstanceOf[js.Any],
      "squeezestart" -> squeezestart.asInstanceOf[js.Any],
      "squeezeend" -> squeezeend.asInstanceOf[js.Any],
      "connected" -> connected.asInstanceOf[js.Any],
      "disconnected" -> disconnected.asInstanceOf[js.Any],
      "pinchend" -> pinchend.asInstanceOf[js.Any],
      "pinchstart" -> pinchstart.asInstanceOf[js.Any],
      "move" -> move.asInstanceOf[js.Any],
      "added" -> added.asInstanceOf[js.Any],
      "removed" -> removed.asInstanceOf[js.Any],
      "childadded" -> childadded.asInstanceOf[js.Any],
      "childremoved" -> childremoved.asInstanceOf[js.Any]
    ).asInstanceOf[WebXRSpaceEventMap]
