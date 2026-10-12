// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait CylinderGeometryParameters extends js.Object:
  val height: Double
  val heightSegments: Double
  val openEnded: Boolean
  val radialSegments: Double
  val radiusBottom: Double
  val radiusTop: Double
  val thetaLength: Double
  val thetaStart: Double

object CylinderGeometryParameters:
  def apply(
    radiusTop: Double,
    radiusBottom: Double,
    height: Double,
    radialSegments: Double,
    heightSegments: Double,
    openEnded: Boolean,
    thetaStart: Double,
    thetaLength: Double
  ): CylinderGeometryParameters =
    js.Dynamic.literal(
      "radiusTop" -> radiusTop.asInstanceOf[js.Any],
      "radiusBottom" -> radiusBottom.asInstanceOf[js.Any],
      "height" -> height.asInstanceOf[js.Any],
      "radialSegments" -> radialSegments.asInstanceOf[js.Any],
      "heightSegments" -> heightSegments.asInstanceOf[js.Any],
      "openEnded" -> openEnded.asInstanceOf[js.Any],
      "thetaStart" -> thetaStart.asInstanceOf[js.Any],
      "thetaLength" -> thetaLength.asInstanceOf[js.Any]
    ).asInstanceOf[CylinderGeometryParameters]
