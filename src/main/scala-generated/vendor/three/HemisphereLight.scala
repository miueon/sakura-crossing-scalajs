// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "HemisphereLight")
class HemisphereLight(skyColor: js.UndefOr[String | Double | Color] = js.undefined, groundColor_1: js.UndefOr[String | Double | Color] = js.undefined, intensity: js.UndefOr[Double] = js.undefined) extends Light:
  var groundColor: Color = js.native
  def isHemisphereLight: Boolean = js.native
  @JSName("copy")
  protected override def copy$raw(`object`: Object3D[Object3DEventMap], recursive: js.UndefOr[Boolean] = js.undefined): HemisphereLight = js.native

object HemisphereLight:
  extension [Self <: HemisphereLight](self: Self)
    inline def copy(source: Object3D[Object3DEventMap], recursive: js.UndefOr[Boolean] = js.undefined): Self = self.copy$raw(source, recursive).asInstanceOf[Self]
