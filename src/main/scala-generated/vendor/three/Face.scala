// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait Face extends js.Object:
  var a: Double = js.native
  var b: Double = js.native
  var c: Double = js.native
  var materialIndex: Double = js.native
  var normal: Vector3 = js.native

object Face:
  def apply(
    a: Double,
    b: Double,
    c: Double,
    normal: Vector3,
    materialIndex: Double
  ): Face =
    js.Dynamic.literal(
      "a" -> a.asInstanceOf[js.Any],
      "b" -> b.asInstanceOf[js.Any],
      "c" -> c.asInstanceOf[js.Any],
      "normal" -> normal.asInstanceOf[js.Any],
      "materialIndex" -> materialIndex.asInstanceOf[js.Any]
    ).asInstanceOf[Face]
