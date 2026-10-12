// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait Vector3Like extends js.Object:
  val x: Double
  val y: Double
  val z: Double

object Vector3Like:
  def apply(
    x: Double,
    y: Double,
    z: Double
  ): Vector3Like =
    js.Dynamic.literal(
      "x" -> x.asInstanceOf[js.Any],
      "y" -> y.asInstanceOf[js.Any],
      "z" -> z.asInstanceOf[js.Any]
    ).asInstanceOf[Vector3Like]
