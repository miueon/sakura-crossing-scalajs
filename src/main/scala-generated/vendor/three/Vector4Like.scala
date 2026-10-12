// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait Vector4Like extends js.Object:
  val w: Double
  val x: Double
  val y: Double
  val z: Double

object Vector4Like:
  def apply(
    x: Double,
    y: Double,
    z: Double,
    w: Double
  ): Vector4Like =
    js.Dynamic.literal(
      "x" -> x.asInstanceOf[js.Any],
      "y" -> y.asInstanceOf[js.Any],
      "z" -> z.asInstanceOf[js.Any],
      "w" -> w.asInstanceOf[js.Any]
    ).asInstanceOf[Vector4Like]
