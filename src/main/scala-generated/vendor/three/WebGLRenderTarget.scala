// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "WebGLRenderTarget")
class WebGLRenderTarget[TTexture <: Texture[?, ?] | js.Array[Texture[js.Any, TextureEventMap]]](width: js.UndefOr[Double] = js.undefined, height: js.UndefOr[Double] = js.undefined, options: js.UndefOr[RenderTargetOptions] = js.undefined) extends RenderTarget[TTexture, RenderTargetEventMap](js.native: js.UndefOr[Double], js.native: js.UndefOr[Double], js.native: js.UndefOr[RenderTargetOptions]):
  def isWebGLRenderTarget: true = js.native
