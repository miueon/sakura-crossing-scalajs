// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait RGB extends js.Object:
  var b: Double = js.native
  var g: Double = js.native
  var r: Double = js.native

object RGB:
  def apply(
    r: Double,
    g: Double,
    b: Double
  ): RGB =
    js.Dynamic.literal(
      "r" -> r.asInstanceOf[js.Any],
      "g" -> g.asInstanceOf[js.Any],
      "b" -> b.asInstanceOf[js.Any]
    ).asInstanceOf[RGB]
