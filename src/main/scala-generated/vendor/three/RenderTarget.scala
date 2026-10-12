// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "RenderTarget")
class RenderTarget[TTexture <: Texture[?, ?] | js.Array[Texture[js.Any, TextureEventMap]], TEventMap <: RenderTargetEventMap](width_1: js.UndefOr[Double] = js.undefined, height_1: js.UndefOr[Double] = js.undefined, options: js.UndefOr[RenderTargetOptions] = js.undefined) extends EventDispatcher[TEventMap]:
  var depth: Double = js.native
  var depthBuffer: Boolean = js.native
  var depthTexture: DepthTexture[DepthTextureImageData] | Null = js.native
  var height: Double = js.native
  def isRenderTarget: true = js.native
  var multiview: Boolean = js.native
  var resolveDepthBuffer: Boolean = js.native
  var resolveStencilBuffer: Boolean = js.native
  var samples: Double = js.native
  var scissor: Vector4 = js.native
  var scissorTest: Boolean = js.native
  var stencilBuffer: Boolean = js.native
  def texture: CubeTexture[js.Any] | Data3DTexture | DataArrayTexture | TTexture = js.native
  def texture_=(value: js.Any): Unit = js.native
  def textures: js.Array[CubeTexture[js.Any]] | js.Array[Data3DTexture] | js.Array[DataArrayTexture] | js.Array[TTexture] = js.native
  def textures_=(value: js.Any): Unit = js.native
  var useArrayDepthTexture: Boolean = js.native
  var viewport: Vector4 = js.native
  var width: Double = js.native
  @JSName("clone")
  protected def clone$raw(): RenderTarget[TTexture, TEventMap] = js.native
  @JSName("copy")
  protected def copy$raw(source: RenderTarget[Texture[js.Any, TextureEventMap], RenderTargetEventMap]): RenderTarget[TTexture, TEventMap] = js.native
  def dispose(): Unit = js.native
  def setSize(width: Double, height: Double, depth: js.UndefOr[Double] = js.undefined): Unit = js.native

object RenderTarget:
  extension [TTexture <: Texture[?, ?] | js.Array[Texture[js.Any, TextureEventMap]], TEventMap <: RenderTargetEventMap, Self <: RenderTarget[TTexture, TEventMap]](self: Self)
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]
    inline def copy(source: RenderTarget[Texture[js.Any, TextureEventMap], RenderTargetEventMap]): Self = self.copy$raw(source).asInstanceOf[Self]
