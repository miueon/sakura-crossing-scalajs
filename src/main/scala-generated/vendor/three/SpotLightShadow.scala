// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "SpotLightShadow")
class SpotLightShadow() extends LightShadow[PerspectiveCamera]:
  var aspect: Double = js.native
  var focus: Double = js.native
  def isSpotLightShadow: Boolean = js.native
  @JSName("copy")
  protected override def copy$raw(source: LightShadow[Camera]): SpotLightShadow = js.native

object SpotLightShadow:
  extension [Self <: SpotLightShadow](self: Self)
    inline def copy(source: LightShadow[Camera]): Self = self.copy$raw(source).asInstanceOf[Self]
