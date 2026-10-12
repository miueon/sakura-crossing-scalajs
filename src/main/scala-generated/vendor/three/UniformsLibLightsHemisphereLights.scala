// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait UniformsLibLightsHemisphereLights extends js.Object:
  var properties: UniformsLibLightsHemisphereLightsProperties = js.native
  var value: js.Array[facadecontract.std.JSUnknown] = js.native

object UniformsLibLightsHemisphereLights:
  def apply(
    value: js.Array[facadecontract.std.JSUnknown],
    properties: UniformsLibLightsHemisphereLightsProperties
  ): UniformsLibLightsHemisphereLights =
    js.Dynamic.literal(
      "value" -> value.asInstanceOf[js.Any],
      "properties" -> properties.asInstanceOf[js.Any]
    ).asInstanceOf[UniformsLibLightsHemisphereLights]
