// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait LatheGeometryParameters extends js.Object:
  val phiLength: Double
  val phiStart: Double
  val points: js.Array[Vector2]
  val segments: Double

object LatheGeometryParameters:
  def apply(
    points: js.Array[Vector2],
    segments: Double,
    phiStart: Double,
    phiLength: Double
  ): LatheGeometryParameters =
    js.Dynamic.literal(
      "points" -> points.asInstanceOf[js.Any],
      "segments" -> segments.asInstanceOf[js.Any],
      "phiStart" -> phiStart.asInstanceOf[js.Any],
      "phiLength" -> phiLength.asInstanceOf[js.Any]
    ).asInstanceOf[LatheGeometryParameters]
