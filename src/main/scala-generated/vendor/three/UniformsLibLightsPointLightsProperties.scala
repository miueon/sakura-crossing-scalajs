// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait UniformsLibLightsPointLightsProperties extends js.Object:
  var color: js.Any = js.native
  var decay: js.Any = js.native
  var distance: js.Any = js.native
  var position: js.Any = js.native

object UniformsLibLightsPointLightsProperties:
  def apply(
    color: js.Any,
    position: js.Any,
    decay: js.Any,
    distance: js.Any
  ): UniformsLibLightsPointLightsProperties =
    js.Dynamic.literal(
      "color" -> color.asInstanceOf[js.Any],
      "position" -> position.asInstanceOf[js.Any],
      "decay" -> decay.asInstanceOf[js.Any],
      "distance" -> distance.asInstanceOf[js.Any]
    ).asInstanceOf[UniformsLibLightsPointLightsProperties]
