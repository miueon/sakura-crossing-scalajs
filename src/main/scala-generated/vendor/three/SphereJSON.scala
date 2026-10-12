// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait SphereJSON extends js.Object:
  var center: js.Array[Double] = js.native
  var radius: Double = js.native

object SphereJSON:
  def apply(
    radius: Double,
    center: js.Array[Double]
  ): SphereJSON =
    js.Dynamic.literal(
      "radius" -> radius.asInstanceOf[js.Any],
      "center" -> center.asInstanceOf[js.Any]
    ).asInstanceOf[SphereJSON]
