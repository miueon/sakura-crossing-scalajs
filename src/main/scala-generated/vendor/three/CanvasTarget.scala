// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait CanvasTarget[TEventMap <: CanvasTargetEventMap] extends js.Object:
  var colorTexture: FramebufferTexture = js.native
  var depthTexture: DepthTexture[DepthTextureImageData] = js.native
  var domElement: org.scalajs.dom.HTMLCanvasElement | org.scalajs.dom.OffscreenCanvas = js.native
  def addEventListener[T](`type`: T, listener: js.Function1[js.Object & Event[String, CanvasTarget[TEventMap]], Unit]): Unit = js.native
  def dispatchEvent[T](event: BaseEvent[String] & js.Any): Unit = js.native
  def dispose(): Unit = js.native
  def getDrawingBufferSize(target: Vector2): Vector2 = js.native
  def getPixelRatio(): Double = js.native
  def getScissor(target: Vector4): Vector4 = js.native
  def getScissorTest(): Boolean = js.native
  def getSize(target: Vector2): Vector2 = js.native
  def getViewport(target: Vector4): Vector4 = js.native
  def hasEventListener[T](`type`: T, listener: js.Function1[js.Object & Event[String, CanvasTarget[TEventMap]], Unit]): Boolean = js.native
  def removeEventListener[T](`type`: T, listener: js.Function1[js.Object & Event[String, CanvasTarget[TEventMap]], Unit]): Unit = js.native
  def setDrawingBufferSize(width: Double, height: Double, pixelRatio: Double): Unit = js.native
  def setPixelRatio(value: js.UndefOr[Double] = js.undefined): Unit = js.native
  def setScissor(x: Vector4): Unit = js.native
  def setScissor(x: Double, y: Double, width: Double, height: Double): Unit = js.native
  def setScissorTest(boolean: Boolean): Unit = js.native
  def setSize(width: Double, height: Double, updateStyle: js.UndefOr[Boolean] = js.undefined): Unit = js.native
  def setViewport(x: Vector4): Unit = js.native
  def setViewport(x: Double, y: Double, width: Double, height: Double, minDepth: js.UndefOr[Double] = js.undefined, maxDepth: js.UndefOr[Double] = js.undefined): Unit = js.native
// #WARN: SHIM-065: Native facade trait 'CanvasTarget' cannot receive a builder companion: JS property 'setScissor' is written by 2 members
