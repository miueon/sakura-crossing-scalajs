// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait HSL extends js.Object:
  var h: Double = js.native
  var l: Double = js.native
  var s: Double = js.native

object HSL:
  def apply(
    h: Double,
    s: Double,
    l: Double
  ): HSL =
    js.Dynamic.literal(
      "h" -> h.asInstanceOf[js.Any],
      "s" -> s.asInstanceOf[js.Any],
      "l" -> l.asInstanceOf[js.Any]
    ).asInstanceOf[HSL]
