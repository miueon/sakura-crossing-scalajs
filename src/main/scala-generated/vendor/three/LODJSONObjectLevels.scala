// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait LODJSONObjectLevels extends js.Object:
  var distance: Double = js.native
  var hysteresis: Double = js.native
  var `object`: String = js.native

object LODJSONObjectLevels:
  def apply(
    `object`: String,
    distance: Double,
    hysteresis: Double
  ): LODJSONObjectLevels =
    js.Dynamic.literal(
      "object" -> `object`.asInstanceOf[js.Any],
      "distance" -> distance.asInstanceOf[js.Any],
      "hysteresis" -> hysteresis.asInstanceOf[js.Any]
    ).asInstanceOf[LODJSONObjectLevels]
