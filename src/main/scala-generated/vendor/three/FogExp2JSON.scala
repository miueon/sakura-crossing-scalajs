// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait FogExp2JSON extends js.Object:
  var color: Double = js.native
  var density: Double = js.native
  var name: String = js.native
  var `type`: String = js.native

object FogExp2JSON:
  def apply(
    `type`: String,
    name: String,
    color: Double,
    density: Double
  ): FogExp2JSON =
    js.Dynamic.literal(
      "type" -> `type`.asInstanceOf[js.Any],
      "name" -> name.asInstanceOf[js.Any],
      "color" -> color.asInstanceOf[js.Any],
      "density" -> density.asInstanceOf[js.Any]
    ).asInstanceOf[FogExp2JSON]
