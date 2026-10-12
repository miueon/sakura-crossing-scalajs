// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "WebGLProgram")
class WebGLProgram(renderer: WebGLRenderer, cacheKey_1: String, parameters: js.Object) extends js.Object:
  var cacheKey: String = js.native
  var fragmentShader: js.Object = js.native
  var id: Double = js.native
  var name: String = js.native
  var program: facadecontract.std.JSUnknown = js.native
  var usedTimes: Double = js.native
  var vertexShader: js.Object = js.native
  def destroy(): Unit = js.native
  def getAttributes(): facadecontract.std.JSUnknown = js.native
  def getUniforms(): WebGLUniforms = js.native
