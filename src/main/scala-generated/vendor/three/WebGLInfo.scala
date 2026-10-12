// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "WebGLInfo")
class WebGLInfo(gl: js.Object) extends js.Object:
  var autoReset: Boolean = js.native
  var memory: WebGLInfoMemory = js.native
  var programs: js.Array[WebGLProgram] | Null = js.native
  var render: WebGLInfoRender = js.native
  def reset(): Unit = js.native
  def update(count: Double, mode: Double, instanceCount: Double): Unit = js.native
