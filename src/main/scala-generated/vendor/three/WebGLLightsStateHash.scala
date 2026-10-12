// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait WebGLLightsStateHash extends js.Object:
  var directionalLength: Double = js.native
  var hemiLength: Double = js.native
  var numDirectionalShadows: Double = js.native
  var numLightProbes: Double = js.native
  var numPointShadows: Double = js.native
  var numSpotMaps: Double = js.native
  var numSpotShadows: Double = js.native
  var pointLength: Double = js.native
  var rectAreaLength: Double = js.native
  var spotLength: Double = js.native

object WebGLLightsStateHash:
  def apply(
    directionalLength: Double,
    pointLength: Double,
    spotLength: Double,
    rectAreaLength: Double,
    hemiLength: Double,
    numDirectionalShadows: Double,
    numPointShadows: Double,
    numSpotShadows: Double,
    numSpotMaps: Double,
    numLightProbes: Double
  ): WebGLLightsStateHash =
    js.Dynamic.literal(
      "directionalLength" -> directionalLength.asInstanceOf[js.Any],
      "pointLength" -> pointLength.asInstanceOf[js.Any],
      "spotLength" -> spotLength.asInstanceOf[js.Any],
      "rectAreaLength" -> rectAreaLength.asInstanceOf[js.Any],
      "hemiLength" -> hemiLength.asInstanceOf[js.Any],
      "numDirectionalShadows" -> numDirectionalShadows.asInstanceOf[js.Any],
      "numPointShadows" -> numPointShadows.asInstanceOf[js.Any],
      "numSpotShadows" -> numSpotShadows.asInstanceOf[js.Any],
      "numSpotMaps" -> numSpotMaps.asInstanceOf[js.Any],
      "numLightProbes" -> numLightProbes.asInstanceOf[js.Any]
    ).asInstanceOf[WebGLLightsStateHash]
