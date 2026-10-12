// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "RectAreaLight")
class RectAreaLight(color: js.UndefOr[String | Double | Color] = js.undefined, intensity: js.UndefOr[Double] = js.undefined, width_1: js.UndefOr[Double] = js.undefined, height_1: js.UndefOr[Double] = js.undefined) extends Light:
  var height: Double = js.native
  def isRectAreaLight: Boolean = js.native
  var power: Double = js.native
  var width: Double = js.native
  @JSName("copy")
  protected override def copy$raw(`object`: Object3D[Object3DEventMap], recursive: js.UndefOr[Boolean] = js.undefined): RectAreaLight = js.native

object RectAreaLight:
  extension [Self <: RectAreaLight](self: Self)
    inline def copy(source: Object3D[Object3DEventMap]): Self = self.copy$raw(source, js.undefined).asInstanceOf[Self]
