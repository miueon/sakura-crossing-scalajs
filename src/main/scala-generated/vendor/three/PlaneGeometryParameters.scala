// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait PlaneGeometryParameters extends js.Object:
  val height: Double
  val heightSegments: Double
  val width: Double
  val widthSegments: Double

object PlaneGeometryParameters:
  def apply(
    width: Double,
    height: Double,
    widthSegments: Double,
    heightSegments: Double
  ): PlaneGeometryParameters =
    js.Dynamic.literal(
      "width" -> width.asInstanceOf[js.Any],
      "height" -> height.asInstanceOf[js.Any],
      "widthSegments" -> widthSegments.asInstanceOf[js.Any],
      "heightSegments" -> heightSegments.asInstanceOf[js.Any]
    ).asInstanceOf[PlaneGeometryParameters]
