// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRManager[TEventMap <: XRManagerEventMap] extends js.Object:
  var cameraAutoUpdate: Boolean = js.native
  var enabled: Boolean = js.native
  def isPresenting: Boolean = js.native
  def addEventListener[T](`type`: T, listener: js.Function1[js.Object & Event[String, XRManager[TEventMap]], Unit]): Unit = js.native
  def createCylinderLayer(radius: Double, centralAngle: Double, aspectratio: Double, translation: Vector3, quaternion: Quaternion, pixelwidth: Double, pixelheight: Double, rendercall: js.Function0[Unit], attributes: js.UndefOr[LayerAttributes] = js.undefined): Mesh[CylinderGeometry, MeshBasicMaterial, Object3DEventMap] = js.native
  def createQuadLayer(width: Double, height: Double, translation: Vector3, quaternion: Quaternion, pixelwidth: Double, pixelheight: Double, rendercall: js.Function0[Unit], attributes: js.UndefOr[LayerAttributes] = js.undefined): Mesh[PlaneGeometry, MeshBasicMaterial, Object3DEventMap] = js.native
  def dispatchEvent[T](event: BaseEvent[String] & js.Any): Unit = js.native
  def foveateBoundTexture(renderTarget: RenderTarget[Texture[js.Any, TextureEventMap], RenderTargetEventMap]): Unit = js.native
  def getBaseLayer(): XRProjectionLayer | XRWebGLLayer | Null = js.native
  def getBinding(): XRWebGLBinding | Null = js.native
  def getCamera(): ArrayCamera = js.native
  def getController(index: Double): XRTargetRaySpace = js.native
  def getControllerGrip(index: Double): XRGripSpace = js.native
  def getEnvironmentBlendMode(): js.UndefOr["additive" | "alpha-blend" | "opaque"] = js.native
  def getFoveation(): js.UndefOr[Double] = js.native
  def getFrame(): XRFrame | Null = js.native
  def getFramebufferScaleFactor(): Double = js.native
  def getHand(index: Double): XRHandSpace = js.native
  def getReferenceSpace(): XRReferenceSpace = js.native
  def getReferenceSpaceType(): "bounded-floor" | "local" | "local-floor" | "unbounded" | "viewer" = js.native
  def getSession(): XRSession | Null = js.native
  def getWebGPUBinding(): XRGPUBinding | Null = js.native
  def hasEventListener[T](`type`: T, listener: js.Function1[js.Object & Event[String, XRManager[TEventMap]], Unit]): Boolean = js.native
  def removeEventListener[T](`type`: T, listener: js.Function1[js.Object & Event[String, XRManager[TEventMap]], Unit]): Unit = js.native
  def renderLayers(): Unit = js.native
  def setFoveation(foveation: Double): Unit = js.native
  def setFramebufferScaleFactor(factor: Double): Unit = js.native
  def setReferenceSpace(space: XRReferenceSpace): Unit = js.native
  def setReferenceSpaceType(`type`: "bounded-floor" | "local" | "local-floor" | "unbounded" | "viewer"): Unit = js.native
  def setSession(session: XRSession): js.Promise[Unit] = js.native
  def updateCamera(camera: PerspectiveCamera): Unit = js.native
  def useMultiview(): Boolean = js.native
// #WARN: SHIM-065: Native facade trait 'XRManager' cannot receive a builder companion: method 'addEventListener' declares method-level type parameters [T]
