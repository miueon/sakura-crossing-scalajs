// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Light")
abstract class Light() extends Object3D[LightEventMap]:
  var color: Color = js.native
  var intensity: Double = js.native
  def isLight: Boolean = js.native
  @JSName("copy")
  protected override def copy$raw(`object`: Object3D[Object3DEventMap], recursive: js.UndefOr[Boolean] = js.undefined): Light = js.native
  def dispose(): Unit = js.native
  override def toJSON(meta: js.UndefOr[JSONMeta] = js.undefined): LightJSON = js.native

object Light:
  extension [Self <: Light](self: Self)
    inline def copy(source: Object3D[Object3DEventMap], recursive: js.UndefOr[Boolean] = js.undefined): Self = self.copy$raw(source, recursive).asInstanceOf[Self]
