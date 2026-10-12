// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRInputSourceEvent extends js.Object:
  def AT_TARGET: 2 = js.native
  def bubbles: Boolean = js.native
  def BUBBLING_PHASE: 3 = js.native
  def cancelable: Boolean = js.native
  var cancelBubble: Boolean = js.native
  def CAPTURING_PHASE: 1 = js.native
  def composed: Boolean = js.native
  def currentTarget: org.scalajs.dom.EventTarget | Null = js.native
  def defaultPrevented: Boolean = js.native
  def eventPhase: Double = js.native
  def frame: XRFrame = js.native
  def inputSource: XRInputSource = js.native
  def isTrusted: Boolean = js.native
  def NONE: 0 = js.native
  var returnValue: Boolean = js.native
  def srcElement: org.scalajs.dom.EventTarget | Null = js.native
  def target: org.scalajs.dom.EventTarget | Null = js.native
  def timeStamp: Double = js.native
  def `type`: "select" | "selectend" | "selectstart" | "squeeze" | "squeezeend" | "squeezestart" = js.native
  def composedPath(): js.Array[org.scalajs.dom.EventTarget] = js.native
  def initEvent(`type`: String, bubbles: js.UndefOr[Boolean] = js.undefined, cancelable: js.UndefOr[Boolean] = js.undefined): Unit = js.native
  def preventDefault(): Unit = js.native
  def stopImmediatePropagation(): Unit = js.native
  def stopPropagation(): Unit = js.native

object XRInputSourceEvent:
  def of(
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
    frame: XRFrame,
    initEvent: js.Function3[String, js.UndefOr[Boolean], js.UndefOr[Boolean], Unit],
    inputSource: XRInputSource,
    isTrusted: Boolean,
    NONE: 0,
    preventDefault: js.Function0[Unit],
    returnValue: Boolean,
    srcElement: org.scalajs.dom.EventTarget | Null,
    stopImmediatePropagation: js.Function0[Unit],
    stopPropagation: js.Function0[Unit],
    target: org.scalajs.dom.EventTarget | Null,
    timeStamp: Double,
    `type`: "select" | "selectend" | "selectstart" | "squeeze" | "squeezeend" | "squeezestart"
  ): XRInputSourceEvent =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
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
      "frame" -> frame.asInstanceOf[js.Any],
      "initEvent" -> initEvent.asInstanceOf[js.Any],
      "inputSource" -> inputSource.asInstanceOf[js.Any],
      "isTrusted" -> isTrusted.asInstanceOf[js.Any],
      "NONE" -> NONE.asInstanceOf[js.Any],
      "preventDefault" -> preventDefault.asInstanceOf[js.Any],
      "returnValue" -> returnValue.asInstanceOf[js.Any],
      "srcElement" -> srcElement.asInstanceOf[js.Any],
      "stopImmediatePropagation" -> stopImmediatePropagation.asInstanceOf[js.Any],
      "stopPropagation" -> stopPropagation.asInstanceOf[js.Any],
      "target" -> target.asInstanceOf[js.Any],
      "timeStamp" -> timeStamp.asInstanceOf[js.Any],
      "type" -> `type`.asInstanceOf[js.Any]
    ).asInstanceOf[XRInputSourceEvent]
