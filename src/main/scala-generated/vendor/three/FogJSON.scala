// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait FogJSON extends js.Object:
  var color: Double = js.native
  var far: Double = js.native
  var name: String = js.native
  var near: Double = js.native
  var `type`: String = js.native

object FogJSON:
  def apply(
    `type`: String,
    name: String,
    color: Double,
    near: Double,
    far: Double
  ): FogJSON =
    js.Dynamic.literal(
      "type" -> `type`.asInstanceOf[js.Any],
      "name" -> name.asInstanceOf[js.Any],
      "color" -> color.asInstanceOf[js.Any],
      "near" -> near.asInstanceOf[js.Any],
      "far" -> far.asInstanceOf[js.Any]
    ).asInstanceOf[FogJSON]
