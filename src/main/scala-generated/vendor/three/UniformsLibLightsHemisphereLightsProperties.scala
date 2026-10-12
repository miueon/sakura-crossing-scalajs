// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait UniformsLibLightsHemisphereLightsProperties extends js.Object:
  var direction: js.Any = js.native
  var groundColor: js.Any = js.native
  var skycolor: js.Any = js.native

object UniformsLibLightsHemisphereLightsProperties:
  def apply(
    direction: js.Any,
    skycolor: js.Any,
    groundColor: js.Any
  ): UniformsLibLightsHemisphereLightsProperties =
    js.Dynamic.literal(
      "direction" -> direction.asInstanceOf[js.Any],
      "skycolor" -> skycolor.asInstanceOf[js.Any],
      "groundColor" -> groundColor.asInstanceOf[js.Any]
    ).asInstanceOf[UniformsLibLightsHemisphereLightsProperties]
