// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait UniformsLibLightsDirectionalLightsProperties extends js.Object:
  var color: js.Any = js.native
  var direction: js.Any = js.native

object UniformsLibLightsDirectionalLightsProperties:
  def apply(
    direction: js.Any,
    color: js.Any
  ): UniformsLibLightsDirectionalLightsProperties =
    js.Dynamic.literal(
      "direction" -> direction.asInstanceOf[js.Any],
      "color" -> color.asInstanceOf[js.Any]
    ).asInstanceOf[UniformsLibLightsDirectionalLightsProperties]
