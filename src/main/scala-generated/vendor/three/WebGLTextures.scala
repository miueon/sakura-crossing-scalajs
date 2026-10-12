// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "WebGLTextures")
class WebGLTextures(gl: js.Object, extensions: WebGLExtensions, state: WebGLState, properties: WebGLProperties, capabilities: WebGLCapabilities, utils: WebGLUtils, info: WebGLInfo) extends js.Object:
  def allocateTextureUnit(): Unit = js.native
  def resetTextureUnits(): Unit = js.native
  def setTexture2D(texture: Texture[js.Any, TextureEventMap], slot: Double): Unit = js.native
  def setTexture2DArray(texture: Texture[js.Any, TextureEventMap], slot: Double): Unit = js.native
  def setTexture3D(texture: Texture[js.Any, TextureEventMap], slot: Double): Unit = js.native
  def setTextureCube(texture: Texture[js.Any, TextureEventMap], slot: Double): Unit = js.native
  def setupRenderTarget(renderTarget: RenderTarget[Texture[js.Any, TextureEventMap], RenderTargetEventMap]): Unit = js.native
  def updateMultisampleRenderTarget(renderTarget: RenderTarget[Texture[js.Any, TextureEventMap], RenderTargetEventMap]): Unit = js.native
  def updateRenderTargetMipmap(renderTarget: RenderTarget[Texture[js.Any, TextureEventMap], RenderTargetEventMap]): Unit = js.native
