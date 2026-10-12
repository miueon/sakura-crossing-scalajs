// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "WebXRController")
class WebXRController() extends js.Object:
  @JSName("connect")
  protected def connect$raw(inputSource: XRInputSource): WebXRController = js.native
  @JSName("disconnect")
  protected def disconnect$raw(inputSource: XRInputSource): WebXRController = js.native
  @JSName("dispatchEvent")
  protected def dispatchEvent$raw(event: WebXRControllerDispatchEventEvent): WebXRController = js.native
  def getGripSpace(): XRGripSpace = js.native
  def getHandSpace(): XRHandSpace = js.native
  def getTargetRaySpace(): XRTargetRaySpace = js.native
  @JSName("update")
  protected def update$raw(inputSource: XRInputSource, frame: XRFrame, referenceSpace: XRReferenceSpace): WebXRController = js.native

object WebXRController:
  extension [Self <: WebXRController](self: Self)
    inline def dispatchEvent(event: WebXRControllerDispatchEventEvent): Self = self.dispatchEvent$raw(event).asInstanceOf[Self]
    inline def connect(inputSource: XRInputSource): Self = self.connect$raw(inputSource).asInstanceOf[Self]
    inline def disconnect(inputSource: XRInputSource): Self = self.disconnect$raw(inputSource).asInstanceOf[Self]
    inline def update(inputSource: XRInputSource, frame: XRFrame, referenceSpace: XRReferenceSpace): Self = self.update$raw(inputSource, frame, referenceSpace).asInstanceOf[Self]
