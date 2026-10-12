// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait UniformsLibLightsPointLightShadowsProperties extends js.Object:
  var shadowBias: js.Any = js.native
  var shadowCameraFar: js.Any = js.native
  var shadowCameraNear: js.Any = js.native
  var shadowIntensity: Double = js.native
  var shadowMapSize: js.Any = js.native
  var shadowNormalBias: js.Any = js.native
  var shadowRadius: js.Any = js.native

object UniformsLibLightsPointLightShadowsProperties:
  def apply(
    shadowIntensity: Double,
    shadowBias: js.Any,
    shadowNormalBias: js.Any,
    shadowRadius: js.Any,
    shadowMapSize: js.Any,
    shadowCameraNear: js.Any,
    shadowCameraFar: js.Any
  ): UniformsLibLightsPointLightShadowsProperties =
    js.Dynamic.literal(
      "shadowIntensity" -> shadowIntensity.asInstanceOf[js.Any],
      "shadowBias" -> shadowBias.asInstanceOf[js.Any],
      "shadowNormalBias" -> shadowNormalBias.asInstanceOf[js.Any],
      "shadowRadius" -> shadowRadius.asInstanceOf[js.Any],
      "shadowMapSize" -> shadowMapSize.asInstanceOf[js.Any],
      "shadowCameraNear" -> shadowCameraNear.asInstanceOf[js.Any],
      "shadowCameraFar" -> shadowCameraFar.asInstanceOf[js.Any]
    ).asInstanceOf[UniformsLibLightsPointLightShadowsProperties]
