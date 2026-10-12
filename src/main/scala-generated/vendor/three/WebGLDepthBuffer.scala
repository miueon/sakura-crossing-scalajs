// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "WebGLDepthBuffer")
class WebGLDepthBuffer() extends js.Object:
  def getReversed(): Boolean = js.native
  def reset(): Unit = js.native
  def setClear(depth: Double): Unit = js.native
  def setFunc(depthFunc: 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7): Unit = js.native
  def setLocked(lock: Boolean): Unit = js.native
  def setMask(depthMask: Boolean): Unit = js.native
  def setReversed(value: Boolean): Unit = js.native
  def setTest(depthTest: Boolean): Unit = js.native
