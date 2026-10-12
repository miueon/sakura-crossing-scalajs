// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait TorusGeometryParameters extends js.Object:
  val arc: Double
  val radialSegments: Double
  val radius: Double
  val thetaLength: Double
  val thetaStart: Double
  val tube: Double
  val tubularSegments: Double

object TorusGeometryParameters:
  def apply(
    radius: Double,
    tube: Double,
    radialSegments: Double,
    tubularSegments: Double,
    arc: Double,
    thetaStart: Double,
    thetaLength: Double
  ): TorusGeometryParameters =
    js.Dynamic.literal(
      "radius" -> radius.asInstanceOf[js.Any],
      "tube" -> tube.asInstanceOf[js.Any],
      "radialSegments" -> radialSegments.asInstanceOf[js.Any],
      "tubularSegments" -> tubularSegments.asInstanceOf[js.Any],
      "arc" -> arc.asInstanceOf[js.Any],
      "thetaStart" -> thetaStart.asInstanceOf[js.Any],
      "thetaLength" -> thetaLength.asInstanceOf[js.Any]
    ).asInstanceOf[TorusGeometryParameters]
