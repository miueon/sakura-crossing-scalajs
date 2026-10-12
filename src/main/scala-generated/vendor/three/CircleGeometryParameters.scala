// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait CircleGeometryParameters extends js.Object:
  val radius: Double
  val segments: Double
  val thetaLength: Double
  val thetaStart: Double

object CircleGeometryParameters:
  def apply(
    radius: Double,
    segments: Double,
    thetaStart: Double,
    thetaLength: Double
  ): CircleGeometryParameters =
    js.Dynamic.literal(
      "radius" -> radius.asInstanceOf[js.Any],
      "segments" -> segments.asInstanceOf[js.Any],
      "thetaStart" -> thetaStart.asInstanceOf[js.Any],
      "thetaLength" -> thetaLength.asInstanceOf[js.Any]
    ).asInstanceOf[CircleGeometryParameters]
