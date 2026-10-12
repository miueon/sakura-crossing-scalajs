// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "WebGLArrayRenderTarget")
class WebGLArrayRenderTarget(width: js.UndefOr[Double] = js.undefined, height: js.UndefOr[Double] = js.undefined, depth: js.UndefOr[Double] = js.undefined, options: js.UndefOr[RenderTargetOptions] = js.undefined) extends WebGLRenderTarget[Texture[js.Any, TextureEventMap]](js.native: js.UndefOr[Double], js.native: js.UndefOr[Double], js.native: js.UndefOr[RenderTargetOptions]):
  def isWebGLArrayRenderTarget: true = js.native
  override def texture: DataArrayTexture = js.native
  override def texture_=(value: js.Any): Unit = js.native
  override def textures: js.Array[DataArrayTexture] = js.native
  override def textures_=(value: js.Any): Unit = js.native
