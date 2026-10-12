// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "PointLight")
class PointLight(color: js.UndefOr[String | Double | Color] = js.undefined, intensity: js.UndefOr[Double] = js.undefined, distance_1: js.UndefOr[Double] = js.undefined, decay_1: js.UndefOr[Double] = js.undefined) extends Light:
  var decay: Double = js.native
  var distance: Double = js.native
  def isPointLight: Boolean = js.native
  var power: Double = js.native
  var shadow: PointLightShadow = js.native
  @JSName("copy")
  protected override def copy$raw(`object`: Object3D[Object3DEventMap], recursive: js.UndefOr[Boolean] = js.undefined): PointLight = js.native

object PointLight:
  extension [Self <: PointLight](self: Self)
    inline def copy(source: Object3D[Object3DEventMap], recursive: js.UndefOr[Boolean] = js.undefined): Self = self.copy$raw(source, recursive).asInstanceOf[Self]
