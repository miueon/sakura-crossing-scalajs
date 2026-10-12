// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "DirectionalLight")
class DirectionalLight(color: js.UndefOr[String | Double | Color] = js.undefined, intensity: js.UndefOr[Double] = js.undefined) extends Light:
  def isDirectionalLight: Boolean = js.native
  var shadow: DirectionalLightShadow = js.native
  var target: Object3D[Object3DEventMap] = js.native
  @JSName("copy")
  protected override def copy$raw(`object`: Object3D[Object3DEventMap], recursive: js.UndefOr[Boolean] = js.undefined): DirectionalLight = js.native

object DirectionalLight:
  extension [Self <: DirectionalLight](self: Self)
    inline def copy(source: Object3D[Object3DEventMap]): Self = self.copy$raw(source, js.undefined).asInstanceOf[Self]
