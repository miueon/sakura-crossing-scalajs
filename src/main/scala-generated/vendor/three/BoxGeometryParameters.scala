// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait BoxGeometryParameters extends js.Object:
  val depth: Double
  val depthSegments: Double
  val height: Double
  val heightSegments: Double
  val width: Double
  val widthSegments: Double

object BoxGeometryParameters:
  def apply(
    width: Double,
    height: Double,
    depth: Double,
    widthSegments: Double,
    heightSegments: Double,
    depthSegments: Double
  ): BoxGeometryParameters =
    js.Dynamic.literal(
      "width" -> width.asInstanceOf[js.Any],
      "height" -> height.asInstanceOf[js.Any],
      "depth" -> depth.asInstanceOf[js.Any],
      "widthSegments" -> widthSegments.asInstanceOf[js.Any],
      "heightSegments" -> heightSegments.asInstanceOf[js.Any],
      "depthSegments" -> depthSegments.asInstanceOf[js.Any]
    ).asInstanceOf[BoxGeometryParameters]
