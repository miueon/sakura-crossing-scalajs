// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait BufferGeometryJSONDataBoundingSphere extends js.Object:
  var center: js.Tuple3[Double, Double, Double] = js.native
  var radius: Double = js.native

object BufferGeometryJSONDataBoundingSphere:
  def apply(
    center: js.Tuple3[Double, Double, Double],
    radius: Double
  ): BufferGeometryJSONDataBoundingSphere =
    js.Dynamic.literal(
      "center" -> center.asInstanceOf[js.Any],
      "radius" -> radius.asInstanceOf[js.Any]
    ).asInstanceOf[BufferGeometryJSONDataBoundingSphere]
