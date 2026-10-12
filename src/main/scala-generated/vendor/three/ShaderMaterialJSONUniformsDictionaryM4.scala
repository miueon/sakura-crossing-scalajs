// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait ShaderMaterialJSONUniformsDictionaryM4 extends js.Object:
  var `type`: "m4" = js.native
  var value: js.Tuple16[Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double] = js.native

object ShaderMaterialJSONUniformsDictionaryM4:
  def apply(
    `type`: "m4" = "m4",
    value: js.Tuple16[Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double]
  ): ShaderMaterialJSONUniformsDictionaryM4 =
    js.Dynamic.literal(
      "type" -> `type`.asInstanceOf[js.Any],
      "value" -> value.asInstanceOf[js.Any]
    ).asInstanceOf[ShaderMaterialJSONUniformsDictionaryM4]
