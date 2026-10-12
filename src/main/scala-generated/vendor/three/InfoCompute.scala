// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait InfoCompute extends js.Object:
  var calls: Double = js.native
  var frameCalls: Double = js.native
  var timestamp: Double = js.native

object InfoCompute:
  def apply(
    calls: Double,
    frameCalls: Double,
    timestamp: Double
  ): InfoCompute =
    js.Dynamic.literal(
      "calls" -> calls.asInstanceOf[js.Any],
      "frameCalls" -> frameCalls.asInstanceOf[js.Any],
      "timestamp" -> timestamp.asInstanceOf[js.Any]
    ).asInstanceOf[InfoCompute]
