// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Fog")
class Fog(color_1: String | Double | Color, near_1: js.UndefOr[Double] = js.undefined, far_1: js.UndefOr[Double] = js.undefined) extends js.Object:
  var color: Color = js.native
  var far: Double = js.native
  def isFog: Boolean = js.native
  var name: String = js.native
  var near: Double = js.native
  override def clone(): Fog = js.native
  def toJSON(): FogJSON = js.native
