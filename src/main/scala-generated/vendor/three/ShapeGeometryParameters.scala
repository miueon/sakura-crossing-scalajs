// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait ShapeGeometryParameters extends js.Object:
  val curveSegments: Double
  val shapes: Shape | js.Array[Shape]

object ShapeGeometryParameters:
  def apply(
    shapes: Shape | js.Array[Shape],
    curveSegments: Double
  ): ShapeGeometryParameters =
    js.Dynamic.literal(
      "shapes" -> shapes.asInstanceOf[js.Any],
      "curveSegments" -> curveSegments.asInstanceOf[js.Any]
    ).asInstanceOf[ShapeGeometryParameters]
