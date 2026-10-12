// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "WebGLIndexedBufferRenderer")
class WebGLIndexedBufferRenderer(gl: js.Object, extensions: WebGLExtensions, info: WebGLInfo) extends js.Object:
  def render(start: Double, count: Double): Unit = js.native
  def renderInstances(start: Double, count: Double, primcount: Double): Unit = js.native
  def renderMultiDraw(starts: js.typedarray.Int32Array, counts: js.typedarray.Int32Array, drawCount: Double): Unit = js.native
  def setIndex(value: WebGLAttribute): Unit = js.native
  def setMode(value: Double): Unit = js.native
