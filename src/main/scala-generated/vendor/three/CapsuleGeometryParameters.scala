// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait CapsuleGeometryParameters extends js.Object:
  val capSegments: Double
  val height: Double
  val heightSegments: Double
  val radialSegments: Double
  val radius: Double

object CapsuleGeometryParameters:
  def apply(
    radius: Double,
    height: Double,
    capSegments: Double,
    radialSegments: Double,
    heightSegments: Double
  ): CapsuleGeometryParameters =
    js.Dynamic.literal(
      "radius" -> radius.asInstanceOf[js.Any],
      "height" -> height.asInstanceOf[js.Any],
      "capSegments" -> capSegments.asInstanceOf[js.Any],
      "radialSegments" -> radialSegments.asInstanceOf[js.Any],
      "heightSegments" -> heightSegments.asInstanceOf[js.Any]
    ).asInstanceOf[CapsuleGeometryParameters]
