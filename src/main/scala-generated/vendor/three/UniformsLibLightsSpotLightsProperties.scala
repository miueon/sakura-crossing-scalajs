// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait UniformsLibLightsSpotLightsProperties extends js.Object:
  var color: js.Any = js.native
  var coneCos: js.Any = js.native
  var decay: js.Any = js.native
  var direction: js.Any = js.native
  var distance: js.Any = js.native
  var penumbraCos: js.Any = js.native
  var position: js.Any = js.native

object UniformsLibLightsSpotLightsProperties:
  def apply(
    color: js.Any,
    position: js.Any,
    direction: js.Any,
    distance: js.Any,
    coneCos: js.Any,
    penumbraCos: js.Any,
    decay: js.Any
  ): UniformsLibLightsSpotLightsProperties =
    js.Dynamic.literal(
      "color" -> color.asInstanceOf[js.Any],
      "position" -> position.asInstanceOf[js.Any],
      "direction" -> direction.asInstanceOf[js.Any],
      "distance" -> distance.asInstanceOf[js.Any],
      "coneCos" -> coneCos.asInstanceOf[js.Any],
      "penumbraCos" -> penumbraCos.asInstanceOf[js.Any],
      "decay" -> decay.asInstanceOf[js.Any]
    ).asInstanceOf[UniformsLibLightsSpotLightsProperties]
