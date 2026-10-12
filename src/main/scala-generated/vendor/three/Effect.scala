// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait Effect extends js.Object:
  def render(renderer: WebGLRenderer, writeBuffer: WebGLRenderTarget[Texture[js.Any, TextureEventMap]], readBuffer: WebGLRenderTarget[Texture[js.Any, TextureEventMap]], deltaTime: Double, maskActive: Boolean): Unit = js.native
  def setSize(width: Double, height: Double): Unit = js.native

object Effect:
  def of(
    render: js.Function5[WebGLRenderer, WebGLRenderTarget[Texture[js.Any, TextureEventMap]], WebGLRenderTarget[Texture[js.Any, TextureEventMap]], Double, Boolean, Unit],
    setSize: js.Function2[Double, Double, Unit]
  ): Effect =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "render" -> render.asInstanceOf[js.Any],
      "setSize" -> setSize.asInstanceOf[js.Any]
    ).asInstanceOf[Effect]
