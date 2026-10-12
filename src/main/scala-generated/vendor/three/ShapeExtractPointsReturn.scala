// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait ShapeExtractPointsReturn extends js.Object:
  var holes: js.Array[js.Array[Vector2]] = js.native
  var shape: js.Array[Vector2] = js.native

object ShapeExtractPointsReturn:
  def apply(
    shape: js.Array[Vector2],
    holes: js.Array[js.Array[Vector2]]
  ): ShapeExtractPointsReturn =
    js.Dynamic.literal(
      "shape" -> shape.asInstanceOf[js.Any],
      "holes" -> holes.asInstanceOf[js.Any]
    ).asInstanceOf[ShapeExtractPointsReturn]
