// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait XRRay extends js.Object:
  val direction: js.Object
  val matrix: js.typedarray.Float32Array
  val origin: js.Object

object XRRay:
  def apply(
    origin: js.Object,
    direction: js.Object,
    matrix: js.typedarray.Float32Array
  ): XRRay =
    js.Dynamic.literal(
      "origin" -> origin.asInstanceOf[js.Any],
      "direction" -> direction.asInstanceOf[js.Any],
      "matrix" -> matrix.asInstanceOf[js.Any]
    ).asInstanceOf[XRRay]
