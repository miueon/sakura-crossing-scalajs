// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait RawShaderMaterialDefaultAttributeValues extends js.Object:
  var color: js.Tuple3[Double, Double, Double] = js.native
  var uv: js.Tuple2[Double, Double] = js.native
  var uv1: js.Tuple2[Double, Double] = js.native

object RawShaderMaterialDefaultAttributeValues:
  def apply(
    color: js.Tuple3[Double, Double, Double],
    uv: js.Tuple2[Double, Double],
    uv1: js.Tuple2[Double, Double]
  ): RawShaderMaterialDefaultAttributeValues =
    js.Dynamic.literal(
      "color" -> color.asInstanceOf[js.Any],
      "uv" -> uv.asInstanceOf[js.Any],
      "uv1" -> uv1.asInstanceOf[js.Any]
    ).asInstanceOf[RawShaderMaterialDefaultAttributeValues]
