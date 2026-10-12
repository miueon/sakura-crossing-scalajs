// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "WebXRManager")
class WebXRManager[TEventMap <: WebXRManagerEventMap](renderer: WebGLRenderer, gl: js.Object) extends EventDispatcher[TEventMap]:
  var cameraAutoUpdate: Boolean = js.native
  var enabled: Boolean = js.native
  var isPresenting: Boolean = js.native
  def dispose(): Unit = js.native
  def getBaseLayer(): XRProjectionLayer | XRWebGLLayer = js.native
  def getBinding(): XRWebGLBinding = js.native
  def getCamera(): OmitArrayCameraCameras & WebXRDepthSensingGetMeshCameraXR = js.native
  def getCameraTexture(xrCamera: PerspectiveCamera & WebXRArrayCameraCameras0): js.UndefOr[ExternalTexture] = js.native
  def getController(index: Double): XRTargetRaySpace = js.native
  def getControllerGrip(index: Double): XRGripSpace = js.native
  def getDepthSensingMesh(): Mesh[BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], Material[MaterialEventMap] | js.Array[Material[MaterialEventMap]], Object3DEventMap] | Null = js.native
  def getDepthTexture(): ExternalTexture | Null = js.native
  def getEnvironmentBlendMode(): js.UndefOr["additive" | "alpha-blend" | "opaque"] = js.native
  def getFoveation(): js.UndefOr[Double] = js.native
  def getFrame(): XRFrame = js.native
  def getHand(index: Double): XRHandSpace = js.native
  def getReferenceSpace(): XRReferenceSpace | Null = js.native
  def getSession(): XRSession | Null = js.native
  def hasDepthSensing(): Boolean = js.native
  def setAnimationLoop(callback: js.Function2[Double, XRFrame, Unit] | Null): Unit = js.native
  def setFoveation(value: Double): Unit = js.native
  def setFramebufferScaleFactor(value: Double): Unit = js.native
  def setReferenceSpace(value: XRReferenceSpace): Unit = js.native
  def setReferenceSpaceType(value: "bounded-floor" | "local" | "local-floor" | "unbounded" | "viewer"): Unit = js.native
  def setSession(value: XRSession | Null): js.Promise[Unit] = js.native
  def updateCamera(camera: PerspectiveCamera): Unit = js.native
