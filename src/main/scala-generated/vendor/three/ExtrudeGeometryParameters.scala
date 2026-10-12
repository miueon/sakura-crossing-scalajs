// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait ExtrudeGeometryParameters extends js.Object:
  val options: ExtrudeGeometryOptions
  val shapes: Shape | js.Array[Shape]

object ExtrudeGeometryParameters:
  def apply(
    shapes: Shape | js.Array[Shape],
    options: ExtrudeGeometryOptions
  ): ExtrudeGeometryParameters =
    js.Dynamic.literal(
      "shapes" -> shapes.asInstanceOf[js.Any],
      "options" -> options.asInstanceOf[js.Any]
    ).asInstanceOf[ExtrudeGeometryParameters]
