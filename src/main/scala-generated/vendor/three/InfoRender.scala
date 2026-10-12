// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait InfoRender extends js.Object:
  var calls: Double = js.native
  var drawCalls: Double = js.native
  var frameCalls: Double = js.native
  var lines: Double = js.native
  var points: Double = js.native
  var timestamp: Double = js.native
  var triangles: Double = js.native

object InfoRender:
  def apply(
    calls: Double,
    frameCalls: Double,
    drawCalls: Double,
    triangles: Double,
    points: Double,
    lines: Double,
    timestamp: Double
  ): InfoRender =
    js.Dynamic.literal(
      "calls" -> calls.asInstanceOf[js.Any],
      "frameCalls" -> frameCalls.asInstanceOf[js.Any],
      "drawCalls" -> drawCalls.asInstanceOf[js.Any],
      "triangles" -> triangles.asInstanceOf[js.Any],
      "points" -> points.asInstanceOf[js.Any],
      "lines" -> lines.asInstanceOf[js.Any],
      "timestamp" -> timestamp.asInstanceOf[js.Any]
    ).asInstanceOf[InfoRender]
