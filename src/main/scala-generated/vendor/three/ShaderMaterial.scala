// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "ShaderMaterial")
class ShaderMaterial(parameters: js.UndefOr[ShaderMaterialParameters] = js.undefined) extends Material[MaterialEventMap] with ShaderMaterialProperties:
  override def defines: js.Dictionary[js.Any] = js.native
  override def defines_=(value: js.UndefOr[js.Dictionary[js.Any]]): Unit = js.native
  def isShaderMaterial: Boolean = js.native
  @JSName("fromJSON")
  protected override def fromJSON$raw(json: MaterialJSON, textures: js.Dictionary[Texture[js.Any, TextureEventMap]]): ShaderMaterial = js.native
  override def setValues(values: js.UndefOr[MaterialParameters] = js.undefined): Unit = js.native
  override def toJSON(meta: js.UndefOr[JSONMeta] = js.undefined): ShaderMaterialJSON = js.native

object ShaderMaterial:
  extension [Self <: ShaderMaterial](self: Self)
    inline def fromJSON(json: MaterialJSON, textures: js.Dictionary[Texture[js.Any, TextureEventMap]]): Self = self.fromJSON$raw(json, textures).asInstanceOf[Self]
