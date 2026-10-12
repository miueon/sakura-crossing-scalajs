// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "WebGLCubeRenderTarget")
class WebGLCubeRenderTarget(size: js.UndefOr[Double] = js.undefined, options: js.UndefOr[RenderTargetOptions] = js.undefined) extends WebGLRenderTarget[Texture[js.Any, TextureEventMap]](js.native: js.UndefOr[Double], js.native: js.UndefOr[Double], js.native: js.UndefOr[RenderTargetOptions]):
  override def texture: CubeTexture[js.Any] = js.native
  override def texture_=(value: js.Any): Unit = js.native
  override def textures: js.Array[CubeTexture[js.Any]] = js.native
  override def textures_=(value: js.Any): Unit = js.native
  def clear(renderer: WebGLRenderer, color: Boolean, depth: Boolean, stencil: Boolean): Unit = js.native
  @JSName("fromEquirectangularTexture")
  protected def fromEquirectangularTexture$raw(renderer: WebGLRenderer, texture: Texture[js.Any, TextureEventMap]): WebGLCubeRenderTarget = js.native

object WebGLCubeRenderTarget:
  extension [Self <: WebGLCubeRenderTarget](self: Self)
    inline def fromEquirectangularTexture(renderer: WebGLRenderer, texture: Texture[js.Any, TextureEventMap]): Self = self.fromEquirectangularTexture$raw(renderer, texture).asInstanceOf[Self]
