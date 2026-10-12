// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
trait CubeRenderTarget extends js.Object:
  var depth: Double = js.native
  var depthBuffer: Boolean = js.native
  var depthTexture: DepthTexture[DepthTextureImageData] | Null = js.native
  var height: Double = js.native
  def isCubeRenderTarget: Boolean = js.native
  def isRenderTarget: true = js.native
  var multiview: Boolean = js.native
  var resolveDepthBuffer: Boolean = js.native
  var resolveStencilBuffer: Boolean = js.native
  var samples: Double = js.native
  var scissor: Vector4 = js.native
  var scissorTest: Boolean = js.native
  var stencilBuffer: Boolean = js.native
  var texture: CubeTexture[js.Any] = js.native
  var textures: js.Array[CubeTexture[js.Any]] = js.native
  var useArrayDepthTexture: Boolean = js.native
  var viewport: Vector4 = js.native
  var width: Double = js.native
  def addEventListener[T](`type`: T, listener: js.Function1[js.Any & Event[String, CubeRenderTarget], Unit]): Unit = js.native
  def clear(renderer: Renderer, color: js.UndefOr[Boolean] = js.undefined, depth: js.UndefOr[Boolean] = js.undefined, stencil: js.UndefOr[Boolean] = js.undefined): Unit = js.native
  @JSName("clone")
  protected def clone$raw(): CubeRenderTarget = js.native
  @JSName("copy")
  protected def copy$raw(source: RenderTarget[Texture[js.Any, TextureEventMap], RenderTargetEventMap]): CubeRenderTarget = js.native
  def dispatchEvent[T](event: BaseEvent[String] & js.Any): Unit = js.native
  def dispose(): Unit = js.native
  @JSName("fromEquirectangularTexture")
  protected def fromEquirectangularTexture$raw(renderer: Renderer, texture: Texture[js.Any, TextureEventMap]): CubeRenderTarget = js.native
  def hasEventListener[T](`type`: T, listener: js.Function1[js.Any & Event[String, CubeRenderTarget], Unit]): Boolean = js.native
  def removeEventListener[T](`type`: T, listener: js.Function1[js.Any & Event[String, CubeRenderTarget], Unit]): Unit = js.native
  def setSize(width: Double, height: Double, depth: js.UndefOr[Double] = js.undefined): Unit = js.native
// #WARN: SHIM-065: Native facade trait 'CubeRenderTarget' cannot receive a builder companion: method 'addEventListener' declares method-level type parameters [T]

object CubeRenderTarget:
  extension [Self <: CubeRenderTarget](self: Self)
    inline def fromEquirectangularTexture(renderer: Renderer, texture: Texture[js.Any, TextureEventMap]): Self = self.fromEquirectangularTexture$raw(renderer, texture).asInstanceOf[Self]
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]
    inline def copy(source: RenderTarget[Texture[js.Any, TextureEventMap], RenderTargetEventMap]): Self = self.copy$raw(source).asInstanceOf[Self]
