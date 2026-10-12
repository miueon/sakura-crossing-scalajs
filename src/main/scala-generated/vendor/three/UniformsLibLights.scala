// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait UniformsLibLights extends js.Object:
  var ambientLightColor: IUniform[js.Array[js.Any]] = js.native
  var directionalLights: UniformsLibLightsDirectionalLights = js.native
  var directionalLightShadows: UniformsLibLightsDirectionalLightShadows = js.native
  var directionalShadowMatrix: IUniform[js.Array[js.Any]] = js.native
  var hemisphereLights: UniformsLibLightsHemisphereLights = js.native
  var lightProbe: IUniform[js.Array[js.Any]] = js.native
  var ltc_1: IUniform[js.Any] = js.native
  var ltc_2: IUniform[js.Any] = js.native
  var pointLights: UniformsLibLightsPointLights = js.native
  var pointLightShadows: UniformsLibLightsPointLightShadows = js.native
  var pointShadowMatrix: IUniform[js.Array[js.Any]] = js.native
  var probesMax: IUniform[Vector3] = js.native
  var probesMin: IUniform[Vector3] = js.native
  var probesResolution: IUniform[Vector3] = js.native
  var probesSH: IUniform[js.Any] = js.native
  var rectAreaLights: UniformsLibLightsRectAreaLights = js.native
  var spotLightMap: IUniform[js.Array[js.Any]] = js.native
  var spotLightMatrix: IUniform[js.Array[js.Any]] = js.native
  var spotLights: UniformsLibLightsSpotLights = js.native
  var spotLightShadows: UniformsLibLightsDirectionalLightShadows = js.native

object UniformsLibLights:
  def apply(
    ambientLightColor: IUniform[js.Array[js.Any]],
    lightProbe: IUniform[js.Array[js.Any]],
    directionalLights: UniformsLibLightsDirectionalLights,
    directionalLightShadows: UniformsLibLightsDirectionalLightShadows,
    directionalShadowMatrix: IUniform[js.Array[js.Any]],
    spotLights: UniformsLibLightsSpotLights,
    spotLightShadows: UniformsLibLightsDirectionalLightShadows,
    spotLightMap: IUniform[js.Array[js.Any]],
    spotLightMatrix: IUniform[js.Array[js.Any]],
    pointLights: UniformsLibLightsPointLights,
    pointLightShadows: UniformsLibLightsPointLightShadows,
    pointShadowMatrix: IUniform[js.Array[js.Any]],
    hemisphereLights: UniformsLibLightsHemisphereLights,
    rectAreaLights: UniformsLibLightsRectAreaLights,
    ltc_1: IUniform[js.Any],
    ltc_2: IUniform[js.Any],
    probesSH: IUniform[js.Any],
    probesMin: IUniform[Vector3],
    probesMax: IUniform[Vector3],
    probesResolution: IUniform[Vector3]
  ): UniformsLibLights =
    js.Dynamic.literal(
      "ambientLightColor" -> ambientLightColor.asInstanceOf[js.Any],
      "lightProbe" -> lightProbe.asInstanceOf[js.Any],
      "directionalLights" -> directionalLights.asInstanceOf[js.Any],
      "directionalLightShadows" -> directionalLightShadows.asInstanceOf[js.Any],
      "directionalShadowMatrix" -> directionalShadowMatrix.asInstanceOf[js.Any],
      "spotLights" -> spotLights.asInstanceOf[js.Any],
      "spotLightShadows" -> spotLightShadows.asInstanceOf[js.Any],
      "spotLightMap" -> spotLightMap.asInstanceOf[js.Any],
      "spotLightMatrix" -> spotLightMatrix.asInstanceOf[js.Any],
      "pointLights" -> pointLights.asInstanceOf[js.Any],
      "pointLightShadows" -> pointLightShadows.asInstanceOf[js.Any],
      "pointShadowMatrix" -> pointShadowMatrix.asInstanceOf[js.Any],
      "hemisphereLights" -> hemisphereLights.asInstanceOf[js.Any],
      "rectAreaLights" -> rectAreaLights.asInstanceOf[js.Any],
      "ltc_1" -> ltc_1.asInstanceOf[js.Any],
      "ltc_2" -> ltc_2.asInstanceOf[js.Any],
      "probesSH" -> probesSH.asInstanceOf[js.Any],
      "probesMin" -> probesMin.asInstanceOf[js.Any],
      "probesMax" -> probesMax.asInstanceOf[js.Any],
      "probesResolution" -> probesResolution.asInstanceOf[js.Any]
    ).asInstanceOf[UniformsLibLights]
