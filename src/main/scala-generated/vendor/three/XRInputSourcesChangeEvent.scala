// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRInputSourcesChangeEvent extends XRSessionEvent:
  def added: facadecontract.std.JSReadonlyArray[XRInputSource] = js.native
  def removed: facadecontract.std.JSReadonlyArray[XRInputSource] = js.native

object XRInputSourcesChangeEvent:
  def of(
    added: facadecontract.std.JSReadonlyArray[XRInputSource],
    AT_TARGET: 2,
    bubbles: Boolean,
    BUBBLING_PHASE: 3,
    cancelable: Boolean,
    cancelBubble: Boolean,
    CAPTURING_PHASE: 1,
    composed: Boolean,
    composedPath: js.Function0[js.Array[org.scalajs.dom.EventTarget]],
    currentTarget: org.scalajs.dom.EventTarget | Null,
    defaultPrevented: Boolean,
    eventPhase: Double,
    initEvent: js.Function3[String, js.UndefOr[Boolean], js.UndefOr[Boolean], Unit],
    isTrusted: Boolean,
    NONE: 0,
    preventDefault: js.Function0[Unit],
    removed: facadecontract.std.JSReadonlyArray[XRInputSource],
    returnValue: Boolean,
    session: XRSession,
    srcElement: org.scalajs.dom.EventTarget | Null,
    stopImmediatePropagation: js.Function0[Unit],
    stopPropagation: js.Function0[Unit],
    target: org.scalajs.dom.EventTarget | Null,
    timeStamp: Double,
    `type`: String
  ): XRInputSourcesChangeEvent =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "added" -> added.asInstanceOf[js.Any],
      "AT_TARGET" -> AT_TARGET.asInstanceOf[js.Any],
      "bubbles" -> bubbles.asInstanceOf[js.Any],
      "BUBBLING_PHASE" -> BUBBLING_PHASE.asInstanceOf[js.Any],
      "cancelable" -> cancelable.asInstanceOf[js.Any],
      "cancelBubble" -> cancelBubble.asInstanceOf[js.Any],
      "CAPTURING_PHASE" -> CAPTURING_PHASE.asInstanceOf[js.Any],
      "composed" -> composed.asInstanceOf[js.Any],
      "composedPath" -> composedPath.asInstanceOf[js.Any],
      "currentTarget" -> currentTarget.asInstanceOf[js.Any],
      "defaultPrevented" -> defaultPrevented.asInstanceOf[js.Any],
      "eventPhase" -> eventPhase.asInstanceOf[js.Any],
      "initEvent" -> initEvent.asInstanceOf[js.Any],
      "isTrusted" -> isTrusted.asInstanceOf[js.Any],
      "NONE" -> NONE.asInstanceOf[js.Any],
      "preventDefault" -> preventDefault.asInstanceOf[js.Any],
      "removed" -> removed.asInstanceOf[js.Any],
      "returnValue" -> returnValue.asInstanceOf[js.Any],
      "session" -> session.asInstanceOf[js.Any],
      "srcElement" -> srcElement.asInstanceOf[js.Any],
      "stopImmediatePropagation" -> stopImmediatePropagation.asInstanceOf[js.Any],
      "stopPropagation" -> stopPropagation.asInstanceOf[js.Any],
      "target" -> target.asInstanceOf[js.Any],
      "timeStamp" -> timeStamp.asInstanceOf[js.Any],
      "type" -> `type`.asInstanceOf[js.Any]
    ).asInstanceOf[XRInputSourcesChangeEvent]
