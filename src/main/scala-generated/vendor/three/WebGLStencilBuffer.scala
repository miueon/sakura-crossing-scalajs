// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "WebGLStencilBuffer")
class WebGLStencilBuffer() extends js.Object:
  def reset(): Unit = js.native
  def setClear(stencil: Double): Unit = js.native
  def setFunc(stencilFunc: Double, stencilRef: Double, stencilMask: Double): Unit = js.native
  def setLocked(lock: Boolean): Unit = js.native
  def setMask(stencilMask: Double): Unit = js.native
  def setOp(stencilFail: Double, stencilZFail: Double, stencilZPass: Double): Unit = js.native
  def setTest(stencilTest: Boolean): Unit = js.native
