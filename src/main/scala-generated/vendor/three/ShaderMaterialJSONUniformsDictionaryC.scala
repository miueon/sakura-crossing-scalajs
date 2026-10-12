// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait ShaderMaterialJSONUniformsDictionaryC extends js.Object:
  var `type`: "c" = js.native
  var value: Double = js.native

object ShaderMaterialJSONUniformsDictionaryC:
  def apply(
    `type`: "c" = "c",
    value: Double
  ): ShaderMaterialJSONUniformsDictionaryC =
    js.Dynamic.literal(
      "type" -> `type`.asInstanceOf[js.Any],
      "value" -> value.asInstanceOf[js.Any]
    ).asInstanceOf[ShaderMaterialJSONUniformsDictionaryC]
