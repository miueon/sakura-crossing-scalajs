// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "MaterialLoader")
class MaterialLoader(manager: js.UndefOr[LoadingManager] = js.undefined) extends Loader[Material[MaterialEventMap], String](js.native: js.UndefOr[LoadingManager]):
  var textures: js.Dictionary[Texture[js.Any, TextureEventMap]] = js.native
  def createMaterialFromType(`type`: String): Material[MaterialEventMap] = js.native
  def parse(json: js.Any): Material[MaterialEventMap] = js.native
  @JSName("setTextures")
  protected def setTextures$raw(textures: js.Dictionary[Texture[js.Any, TextureEventMap]]): MaterialLoader = js.native


object MaterialLoader:
  extension [Self <: MaterialLoader](self: Self)
    inline def setTextures(textures: js.Dictionary[Texture[js.Any, TextureEventMap]]): Self = self.setTextures$raw(textures).asInstanceOf[Self]

  @js.native
  @JSImport("three", "MaterialLoader.createMaterialFromType")
  def createMaterialFromType(`type`: String): Material[MaterialEventMap] = js.native
  @js.native
  @JSImport("three", "MaterialLoader.registerMaterial")
  def registerMaterial(`type`: String, materialClass: js.Any): Unit = js.native
