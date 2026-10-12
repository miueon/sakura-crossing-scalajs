// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "LightProbe")
class LightProbe(sh_1: js.UndefOr[SphericalHarmonics3] = js.undefined, intensity: js.UndefOr[Double] = js.undefined) extends Light:
  def isLightProbe: Boolean = js.native
  var sh: SphericalHarmonics3 = js.native
  @JSName("copy")
  protected override def copy$raw(`object`: Object3D[Object3DEventMap], recursive: js.UndefOr[Boolean] = js.undefined): LightProbe = js.native

object LightProbe:
  extension [Self <: LightProbe](self: Self)
    inline def copy(source: Object3D[Object3DEventMap]): Self = self.copy$raw(source, js.undefined).asInstanceOf[Self]
