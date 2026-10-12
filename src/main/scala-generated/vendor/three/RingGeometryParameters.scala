// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait RingGeometryParameters extends js.Object:
  val innerRadius: Double
  val outerRadius: Double
  val phiSegments: Double
  val thetaLength: Double
  val thetaSegments: Double
  val thetaStart: Double

object RingGeometryParameters:
  def apply(
    innerRadius: Double,
    outerRadius: Double,
    thetaSegments: Double,
    phiSegments: Double,
    thetaStart: Double,
    thetaLength: Double
  ): RingGeometryParameters =
    js.Dynamic.literal(
      "innerRadius" -> innerRadius.asInstanceOf[js.Any],
      "outerRadius" -> outerRadius.asInstanceOf[js.Any],
      "thetaSegments" -> thetaSegments.asInstanceOf[js.Any],
      "phiSegments" -> phiSegments.asInstanceOf[js.Any],
      "thetaStart" -> thetaStart.asInstanceOf[js.Any],
      "thetaLength" -> thetaLength.asInstanceOf[js.Any]
    ).asInstanceOf[RingGeometryParameters]
