// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

// #WARN: Unified parameter slots for overload 'texSubImage2D' with JVM erasure 9:(D,D,D,D,D,D,D,D,java.lang.Object) via union
// #WARN: Unified parameter slots for overload 'texImage2D' with JVM erasure 9:(D,D,D,D,D,D,D,D,java.lang.Object) via union
// #WARN: Unified parameter slots for overload 'texImage3D' with JVM erasure 10:(D,D,D,D,D,D,D,D,D,java.lang.Object) via union
@js.native
@JSImport("three", "WebGLState")
class WebGLState(gl: js.Object, extensions: WebGLExtensions) extends js.Object:
  var buffers: WebGLStateBuffers = js.native
  def activeTexture(webglSlot: Double): Unit = js.native
  def bindFramebuffer(target: Double, framebuffer: js.Object | Null): Unit = js.native
  def bindTexture(webglType: Double, webglTexture: js.Object, webglSlot: js.UndefOr[Double] = js.undefined): Unit = js.native
  def compressedTexImage2D(target: Double, level: Double, internalformat: Double, width: Double, height: Double, border: Double, imageSize: Double, offset: Double): Unit = js.native
  def compressedTexImage2D(target: Double, level: Double, internalformat: Double, width: Double, height: Double, border: Double, srcData: js.typedarray.ArrayBufferView, srcOffset: js.UndefOr[Double] = js.undefined, srcLengthOverride: js.UndefOr[Double] = js.undefined): Unit = js.native
  def compressedTexImage3D(target: Double, level: Double, internalformat: Double, width: Double, height: Double, depth: Double, border: Double, imageSize: Double, offset: Double): Unit = js.native
  def compressedTexImage3D(target: Double, level: Double, internalformat: Double, width: Double, height: Double, depth: Double, border: Double, srcData: js.typedarray.ArrayBufferView, srcOffset: js.UndefOr[Double] = js.undefined, srcLengthOverride: js.UndefOr[Double] = js.undefined): Unit = js.native
  def compressedTexSubImage2D(target: Double, level: Double, xoffset: Double, yoffset: Double, width: Double, height: Double, format: Double, imageSize: Double, offset: Double): Unit = js.native
  def compressedTexSubImage2D(target: Double, level: Double, xoffset: Double, yoffset: Double, width: Double, height: Double, format: Double, srcData: js.typedarray.ArrayBufferView, srcOffset: js.UndefOr[Double] = js.undefined, srcLengthOverride: js.UndefOr[Double] = js.undefined): Unit = js.native
  def compressedTexSubImage3D(target: Double, level: Double, xoffset: Double, yoffset: Double, zoffset: Double, width: Double, height: Double, depth: Double, format: Double, imageSize: Double, offset: Double): Unit = js.native
  def compressedTexSubImage3D(target: Double, level: Double, xoffset: Double, yoffset: Double, zoffset: Double, width: Double, height: Double, depth: Double, format: Double, srcData: js.typedarray.ArrayBufferView, srcOffset: js.UndefOr[Double] = js.undefined, srcLengthOverride: js.UndefOr[Double] = js.undefined): Unit = js.native
  def disable(id: Double): Unit = js.native
  def drawBuffers(renderTarget: WebGLRenderTarget[Texture[js.Any, TextureEventMap]] | Null, framebuffer: js.Object | Null): Unit = js.native
  def enable(id: Double): Unit = js.native
  def getParameter(name: Double): facadecontract.std.JSUnknown = js.native
  def pixelStorei(name: Double, value: Double | Boolean): Unit = js.native
  def reset(): Unit = js.native
  def scissor(scissor: Vector4): Unit = js.native
  def setBlending(blending: 0 | 1 | 2 | 3 | 4 | 5 | 6, blendEquation: js.UndefOr[100 | 101 | 102 | 103 | 104] = js.undefined, blendSrc: js.UndefOr[200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 208 | 209 | 210 | 211 | 212 | 213 | 214] = js.undefined, blendDst: js.UndefOr[200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 208 | 209 | 211 | 212 | 213 | 214] = js.undefined, blendEquationAlpha: js.UndefOr[100 | 101 | 102 | 103 | 104] = js.undefined, blendSrcAlpha: js.UndefOr[200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 208 | 209 | 210 | 211 | 212 | 213 | 214] = js.undefined, blendDstAlpha: js.UndefOr[200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 208 | 209 | 211 | 212 | 213 | 214] = js.undefined, premultiplyAlpha: js.UndefOr[Boolean] = js.undefined): Unit = js.native
  def setCullFace(cullFace: 0 | 1 | 2 | 3): Unit = js.native
  def setFlipSided(flipSided: Boolean): Unit = js.native
  def setLineWidth(width: Double): Unit = js.native
  def setMaterial(material: Material[MaterialEventMap], frontFaceCW: Boolean, hardwareClippingPlanes: Double): Unit = js.native
  def setPolygonOffset(polygonoffset: Boolean, factor: js.UndefOr[Double] = js.undefined, units: js.UndefOr[Double] = js.undefined): Unit = js.native
  def setScissorTest(scissorTest: Boolean): Unit = js.native
  def texImage2D(target: Double, level: Double, internalformat: Double, format: Double, `type`: Double, source: js.Object | org.scalajs.dom.HTMLCanvasElement | org.scalajs.dom.HTMLImageElement | org.scalajs.dom.OffscreenCanvas): Unit = js.native
  // #WARN: Unified parameter slots for overload 'texImage2D' with JVM erasure 9:(D,D,D,D,D,D,D,D,java.lang.Object) via union
  def texImage2D(target: Double, level: Double, internalformat: Double, width: Double, height: Double, border: Double, format: Double, `type`: Double, pixels: js.typedarray.ArrayBufferView | Null | js.Object | org.scalajs.dom.HTMLCanvasElement | org.scalajs.dom.HTMLImageElement | org.scalajs.dom.OffscreenCanvas): Unit = js.native
  def texImage2D(target: Double, level: Double, internalformat: Double, width: Double, height: Double, border: Double, format: Double, `type`: Double, pboOffset: Double): Unit = js.native
  def texImage2D(target: Double, level: Double, internalformat: Double, width: Double, height: Double, border: Double, format: Double, `type`: Double, srcData: js.typedarray.ArrayBufferView, srcOffset: Double): Unit = js.native
  def texImage3D(target: Double, level: Double, internalformat: Double, width: Double, height: Double, depth: Double, border: Double, format: Double, `type`: Double, pboOffset: Double): Unit = js.native
  // #WARN: Unified parameter slots for overload 'texImage3D' with JVM erasure 10:(D,D,D,D,D,D,D,D,D,java.lang.Object) via union
  def texImage3D(target: Double, level: Double, internalformat: Double, width: Double, height: Double, depth: Double, border: Double, format: Double, `type`: Double, source: js.Object | org.scalajs.dom.HTMLCanvasElement | org.scalajs.dom.HTMLImageElement | org.scalajs.dom.OffscreenCanvas | js.typedarray.ArrayBufferView | Null): Unit = js.native
  def texImage3D(target: Double, level: Double, internalformat: Double, width: Double, height: Double, depth: Double, border: Double, format: Double, `type`: Double, srcData: js.typedarray.ArrayBufferView, srcOffset: Double): Unit = js.native
  def texStorage2D(target: Double, levels: Double, internalformat: Double, width: Double, height: Double): Unit = js.native
  def texStorage3D(target: Double, levels: Double, internalformat: Double, width: Double, height: Double, depth: Double): Unit = js.native
  def texSubImage2D(target: Double, level: Double, xoffset: Double, yoffset: Double, format: Double, `type`: Double, source: js.Object | org.scalajs.dom.HTMLCanvasElement | org.scalajs.dom.HTMLImageElement | org.scalajs.dom.OffscreenCanvas): Unit = js.native
  // #WARN: Unified parameter slots for overload 'texSubImage2D' with JVM erasure 9:(D,D,D,D,D,D,D,D,java.lang.Object) via union
  def texSubImage2D(target: Double, level: Double, xoffset: Double, yoffset: Double, width: Double, height: Double, format: Double, `type`: Double, pixels: js.typedarray.ArrayBufferView | Null | js.Object | org.scalajs.dom.HTMLCanvasElement | org.scalajs.dom.HTMLImageElement | org.scalajs.dom.OffscreenCanvas): Unit = js.native
  def texSubImage2D(target: Double, level: Double, xoffset: Double, yoffset: Double, width: Double, height: Double, format: Double, `type`: Double, pboOffset: Double): Unit = js.native
  def texSubImage2D(target: Double, level: Double, xoffset: Double, yoffset: Double, width: Double, height: Double, format: Double, `type`: Double, srcData: js.typedarray.ArrayBufferView, srcOffset: Double): Unit = js.native
  def texSubImage3D(target: Double, level: Double, xoffset: Double, yoffset: Double, zoffset: Double, width: Double, height: Double, depth: Double, format: Double, `type`: Double, pboOffset: Double): Unit = js.native
  def texSubImage3D(target: Double, level: Double, xoffset: Double, yoffset: Double, zoffset: Double, width: Double, height: Double, depth: Double, format: Double, `type`: Double, source: js.Object | org.scalajs.dom.HTMLCanvasElement | org.scalajs.dom.HTMLImageElement | org.scalajs.dom.OffscreenCanvas): Unit = js.native
  def texSubImage3D(target: Double, level: Double, xoffset: Double, yoffset: Double, zoffset: Double, width: Double, height: Double, depth: Double, format: Double, `type`: Double, srcData: js.typedarray.ArrayBufferView | Null, srcOffset: js.UndefOr[Double] = js.undefined): Unit = js.native
  def unbindTexture(): Unit = js.native
  def useProgram(program: js.Object): Boolean = js.native
  def viewport(viewport: Vector4): Unit = js.native
