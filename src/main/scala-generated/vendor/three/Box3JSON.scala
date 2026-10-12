// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait Box3JSON extends js.Object:
  var max: js.Array[Double] = js.native
  var min: js.Array[Double] = js.native

object Box3JSON:
  def apply(
    min: js.Array[Double],
    max: js.Array[Double]
  ): Box3JSON =
    js.Dynamic.literal(
      "min" -> min.asInstanceOf[js.Any],
      "max" -> max.asInstanceOf[js.Any]
    ).asInstanceOf[Box3JSON]
