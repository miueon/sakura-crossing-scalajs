// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait WebGLInfoRender extends js.Object:
  var calls: Double = js.native
  var frame: Double = js.native
  var lines: Double = js.native
  var points: Double = js.native
  var triangles: Double = js.native

object WebGLInfoRender:
  def apply(
    calls: Double,
    frame: Double,
    lines: Double,
    points: Double,
    triangles: Double
  ): WebGLInfoRender =
    js.Dynamic.literal(
      "calls" -> calls.asInstanceOf[js.Any],
      "frame" -> frame.asInstanceOf[js.Any],
      "lines" -> lines.asInstanceOf[js.Any],
      "points" -> points.asInstanceOf[js.Any],
      "triangles" -> triangles.asInstanceOf[js.Any]
    ).asInstanceOf[WebGLInfoRender]
