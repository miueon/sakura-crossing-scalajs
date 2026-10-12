// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait SphereGeometryParameters extends js.Object:
  val heightSegments: Double
  val phiLength: Double
  val phiStart: Double
  val radius: Double
  val thetaLength: Double
  val thetaStart: Double
  val widthSegments: Double

object SphereGeometryParameters:
  def apply(
    radius: Double,
    widthSegments: Double,
    heightSegments: Double,
    phiStart: Double,
    phiLength: Double,
    thetaStart: Double,
    thetaLength: Double
  ): SphereGeometryParameters =
    js.Dynamic.literal(
      "radius" -> radius.asInstanceOf[js.Any],
      "widthSegments" -> widthSegments.asInstanceOf[js.Any],
      "heightSegments" -> heightSegments.asInstanceOf[js.Any],
      "phiStart" -> phiStart.asInstanceOf[js.Any],
      "phiLength" -> phiLength.asInstanceOf[js.Any],
      "thetaStart" -> thetaStart.asInstanceOf[js.Any],
      "thetaLength" -> thetaLength.asInstanceOf[js.Any]
    ).asInstanceOf[SphereGeometryParameters]
