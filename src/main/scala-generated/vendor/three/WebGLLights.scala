// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "WebGLLights")
class WebGLLights(extensions: WebGLExtensions) extends js.Object:
  var state: WebGLLightsState = js.native
  def setup(lights: js.Array[Light]): Unit = js.native
  def setupView(lights: js.Array[Light], camera: Camera): Unit = js.native
