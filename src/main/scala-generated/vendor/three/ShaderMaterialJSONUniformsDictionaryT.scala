// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait ShaderMaterialJSONUniformsDictionaryT extends js.Object:
  var `type`: "t" = js.native
  var value: String = js.native

object ShaderMaterialJSONUniformsDictionaryT:
  def apply(
    `type`: "t" = "t",
    value: String
  ): ShaderMaterialJSONUniformsDictionaryT =
    js.Dynamic.literal(
      "type" -> `type`.asInstanceOf[js.Any],
      "value" -> value.asInstanceOf[js.Any]
    ).asInstanceOf[ShaderMaterialJSONUniformsDictionaryT]
