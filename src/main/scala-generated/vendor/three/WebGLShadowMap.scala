// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "WebGLShadowMap")
class WebGLShadowMap(_renderer: WebGLRenderer, _objects: WebGLObjects, _capabilities: WebGLCapabilities) extends js.Object:
  var autoUpdate: Boolean = js.native
  var enabled: Boolean = js.native
  var needsUpdate: Boolean = js.native
  var `type`: 0 | 1 | 2 | 3 = js.native
  def render(shadowsArray: js.Array[Light], scene: Scene[Object3DEventMap], camera: Camera): Unit = js.native
