// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait ShaderMaterialJSONUniformsDictionaryV4 extends js.Object:
  var `type`: "v4" = js.native
  var value: js.Tuple4[Double, Double, Double, Double] = js.native

object ShaderMaterialJSONUniformsDictionaryV4:
  def apply(
    `type`: "v4" = "v4",
    value: js.Tuple4[Double, Double, Double, Double]
  ): ShaderMaterialJSONUniformsDictionaryV4 =
    js.Dynamic.literal(
      "type" -> `type`.asInstanceOf[js.Any],
      "value" -> value.asInstanceOf[js.Any]
    ).asInstanceOf[ShaderMaterialJSONUniformsDictionaryV4]
