// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait XRRigidTransform extends js.Object:
  val inverse: XRRigidTransform
  val matrix: js.typedarray.Float32Array
  val orientation: js.Object
  val position: js.Object

object XRRigidTransform:
  def apply(
    position: js.Object,
    orientation: js.Object,
    matrix: js.typedarray.Float32Array,
    inverse: XRRigidTransform
  ): XRRigidTransform =
    js.Dynamic.literal(
      "position" -> position.asInstanceOf[js.Any],
      "orientation" -> orientation.asInstanceOf[js.Any],
      "matrix" -> matrix.asInstanceOf[js.Any],
      "inverse" -> inverse.asInstanceOf[js.Any]
    ).asInstanceOf[XRRigidTransform]
