// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait QuaternionLike extends js.Object:
  val w: Double
  val x: Double
  val y: Double
  val z: Double

object QuaternionLike:
  def apply(
    x: Double,
    y: Double,
    z: Double,
    w: Double
  ): QuaternionLike =
    js.Dynamic.literal(
      "x" -> x.asInstanceOf[js.Any],
      "y" -> y.asInstanceOf[js.Any],
      "z" -> z.asInstanceOf[js.Any],
      "w" -> w.asInstanceOf[js.Any]
    ).asInstanceOf[QuaternionLike]
