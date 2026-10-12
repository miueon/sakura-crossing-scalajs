// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "PMREMGenerator")
class PMREMGenerator(renderer: WebGLRenderer) extends js.Object:
  def compileCubemapShader(): Unit = js.native
  def compileEquirectangularShader(): Unit = js.native
  def dispose(): Unit = js.native
  def fromCubemap(cubemap: CubeTexture[js.Any], renderTarget: js.UndefOr[WebGLRenderTarget[Texture[js.Any, TextureEventMap]] | Null] = js.undefined): WebGLRenderTarget[Texture[js.Any, TextureEventMap]] = js.native
  def fromEquirectangular(equirectangular: Texture[js.Any, TextureEventMap], renderTarget: js.UndefOr[WebGLRenderTarget[Texture[js.Any, TextureEventMap]] | Null] = js.undefined): WebGLRenderTarget[Texture[js.Any, TextureEventMap]] = js.native
  def fromScene(scene: Scene[Object3DEventMap], sigma: js.UndefOr[Double] = js.undefined, near: js.UndefOr[Double] = js.undefined, far: js.UndefOr[Double] = js.undefined, options: js.UndefOr[PMREMGeneratorOptions] = js.undefined): WebGLRenderTarget[Texture[js.Any, TextureEventMap]] = js.native
