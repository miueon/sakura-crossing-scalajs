// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait ShaderMaterialJSONUniformsDictionaryV2 extends js.Object:
  var `type`: "v2" = js.native
  var value: js.Tuple2[Double, Double] = js.native

object ShaderMaterialJSONUniformsDictionaryV2:
  def apply(
    `type`: "v2" = "v2",
    value: js.Tuple2[Double, Double]
  ): ShaderMaterialJSONUniformsDictionaryV2 =
    js.Dynamic.literal(
      "type" -> `type`.asInstanceOf[js.Any],
      "value" -> value.asInstanceOf[js.Any]
    ).asInstanceOf[ShaderMaterialJSONUniformsDictionaryV2]
