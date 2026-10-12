// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait InspectorBase[TEventMap <: InspectorBaseEventMap] extends js.Object:
  var currentFrame: facadecontract.std.JSUnknown = js.native
  def nodeFrame: default_4bde67a8 = js.native
  def addEventListener[T](`type`: T, listener: js.Function1[js.Object & Event[String, InspectorBase[TEventMap]], Unit]): Unit = js.native
  def begin(): Unit = js.native
  def beginCompute(uid: String, computeNode: default_5e5b9ffc): Unit = js.native
  def beginRender(uid: String, scene: Scene[Object3DEventMap], camera: Camera, renderTarget: RenderTarget[Texture[js.Any, TextureEventMap], RenderTargetEventMap]): Unit = js.native
  def computeAsync(computeNode: default_5e5b9ffc, dispatchSizeOrCount: Double | js.Array[Double]): Unit = js.native
  def copyFramebufferToTexture(framebufferTexture: Texture[js.Any, TextureEventMap]): Unit = js.native
  def copyTextureToTexture(srcTexture: Texture[js.Any, TextureEventMap], dstTexture: Texture[js.Any, TextureEventMap]): Unit = js.native
  def dispatchEvent[T](event: BaseEvent[String] & js.Any): Unit = js.native
  def finish(): Unit = js.native
  def finishCompute(uid: String): Unit = js.native
  def finishRender(uid: String): Unit = js.native
  def getRenderer(): Renderer = js.native
  def hasEventListener[T](`type`: T, listener: js.Function1[js.Object & Event[String, InspectorBase[TEventMap]], Unit]): Boolean = js.native
  def init(): Unit = js.native
  def inspect(node: NodeClass[NodeClassEventMap] & NodeElements & js.Any): Unit = js.native
  def removeEventListener[T](`type`: T, listener: js.Function1[js.Object & Event[String, InspectorBase[TEventMap]], Unit]): Unit = js.native
  def setRenderer(renderer: Renderer): InspectorBase[InspectorBaseEventMap] = js.native
// #WARN: SHIM-065: Native facade trait 'InspectorBase' cannot receive a builder companion: method 'addEventListener' declares method-level type parameters [T]
