// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait ShaderMaterialJSONUniformsDictionaryV3 extends js.Object:
  var `type`: "v3" = js.native
  var value: js.Tuple3[Double, Double, Double] = js.native

object ShaderMaterialJSONUniformsDictionaryV3:
  def apply(
    `type`: "v3" = "v3",
    value: js.Tuple3[Double, Double, Double]
  ): ShaderMaterialJSONUniformsDictionaryV3 =
    js.Dynamic.literal(
      "type" -> `type`.asInstanceOf[js.Any],
      "value" -> value.asInstanceOf[js.Any]
    ).asInstanceOf[ShaderMaterialJSONUniformsDictionaryV3]
