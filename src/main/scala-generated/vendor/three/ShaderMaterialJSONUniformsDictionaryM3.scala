// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait ShaderMaterialJSONUniformsDictionaryM3 extends js.Object:
  var `type`: "m3" = js.native
  var value: js.Tuple9[Double, Double, Double, Double, Double, Double, Double, Double, Double] = js.native

object ShaderMaterialJSONUniformsDictionaryM3:
  def apply(
    `type`: "m3" = "m3",
    value: js.Tuple9[Double, Double, Double, Double, Double, Double, Double, Double, Double]
  ): ShaderMaterialJSONUniformsDictionaryM3 =
    js.Dynamic.literal(
      "type" -> `type`.asInstanceOf[js.Any],
      "value" -> value.asInstanceOf[js.Any]
    ).asInstanceOf[ShaderMaterialJSONUniformsDictionaryM3]
