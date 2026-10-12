// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRSessionEventMap extends js.Object:
  var end: XRSessionEvent = js.native
  var frameratechange: XRSessionEvent = js.native
  var inputsourceschange: XRInputSourcesChangeEvent = js.native
  var select: XRInputSourceEvent = js.native
  var selectend: XRInputSourceEvent = js.native
  var selectstart: XRInputSourceEvent = js.native
  var squeeze: XRInputSourceEvent = js.native
  var squeezeend: XRInputSourceEvent = js.native
  var squeezestart: XRInputSourceEvent = js.native
  var visibilitychange: XRSessionEvent = js.native

object XRSessionEventMap:
  def apply(
    inputsourceschange: XRInputSourcesChangeEvent,
    end: XRSessionEvent,
    visibilitychange: XRSessionEvent,
    frameratechange: XRSessionEvent,
    select: XRInputSourceEvent,
    selectstart: XRInputSourceEvent,
    selectend: XRInputSourceEvent,
    squeeze: XRInputSourceEvent,
    squeezestart: XRInputSourceEvent,
    squeezeend: XRInputSourceEvent
  ): XRSessionEventMap =
    js.Dynamic.literal(
      "inputsourceschange" -> inputsourceschange.asInstanceOf[js.Any],
      "end" -> end.asInstanceOf[js.Any],
      "visibilitychange" -> visibilitychange.asInstanceOf[js.Any],
      "frameratechange" -> frameratechange.asInstanceOf[js.Any],
      "select" -> select.asInstanceOf[js.Any],
      "selectstart" -> selectstart.asInstanceOf[js.Any],
      "selectend" -> selectend.asInstanceOf[js.Any],
      "squeeze" -> squeeze.asInstanceOf[js.Any],
      "squeezestart" -> squeezestart.asInstanceOf[js.Any],
      "squeezeend" -> squeezeend.asInstanceOf[js.Any]
    ).asInstanceOf[XRSessionEventMap]
