// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait WebGLLightsState extends js.Object:
  var ambient: js.Array[Double] = js.native
  var directional: js.Array[facadecontract.std.JSUnknown] = js.native
  var directionalShadow: js.Array[facadecontract.std.JSUnknown] = js.native
  var directionalShadowMap: js.Array[facadecontract.std.JSUnknown] = js.native
  var directionalShadowMatrix: js.Array[facadecontract.std.JSUnknown] = js.native
  var hash: WebGLLightsStateHash = js.native
  var hemi: js.Array[facadecontract.std.JSUnknown] = js.native
  var numLightProbes: Double = js.native
  var numSpotLightShadowsWithMaps: Double = js.native
  var point: js.Array[facadecontract.std.JSUnknown] = js.native
  var pointShadow: js.Array[facadecontract.std.JSUnknown] = js.native
  var pointShadowMap: js.Array[facadecontract.std.JSUnknown] = js.native
  var pointShadowMatrix: js.Array[facadecontract.std.JSUnknown] = js.native
  var probe: js.Array[facadecontract.std.JSUnknown] = js.native
  var rectArea: js.Array[facadecontract.std.JSUnknown] = js.native
  var spot: js.Array[facadecontract.std.JSUnknown] = js.native
  var spotShadow: js.Array[facadecontract.std.JSUnknown] = js.native
  var spotShadowMap: js.Array[facadecontract.std.JSUnknown] = js.native
  var spotShadowMatrix: js.Array[facadecontract.std.JSUnknown] = js.native
  var version: Double = js.native

object WebGLLightsState:
  def apply(
    version: Double,
    hash: WebGLLightsStateHash,
    ambient: js.Array[Double],
    probe: js.Array[facadecontract.std.JSUnknown],
    directional: js.Array[facadecontract.std.JSUnknown],
    directionalShadow: js.Array[facadecontract.std.JSUnknown],
    directionalShadowMap: js.Array[facadecontract.std.JSUnknown],
    directionalShadowMatrix: js.Array[facadecontract.std.JSUnknown],
    spot: js.Array[facadecontract.std.JSUnknown],
    spotShadow: js.Array[facadecontract.std.JSUnknown],
    spotShadowMap: js.Array[facadecontract.std.JSUnknown],
    spotShadowMatrix: js.Array[facadecontract.std.JSUnknown],
    rectArea: js.Array[facadecontract.std.JSUnknown],
    point: js.Array[facadecontract.std.JSUnknown],
    pointShadow: js.Array[facadecontract.std.JSUnknown],
    pointShadowMap: js.Array[facadecontract.std.JSUnknown],
    pointShadowMatrix: js.Array[facadecontract.std.JSUnknown],
    hemi: js.Array[facadecontract.std.JSUnknown],
    numSpotLightShadowsWithMaps: Double,
    numLightProbes: Double
  ): WebGLLightsState =
    js.Dynamic.literal(
      "version" -> version.asInstanceOf[js.Any],
      "hash" -> hash.asInstanceOf[js.Any],
      "ambient" -> ambient.asInstanceOf[js.Any],
      "probe" -> probe.asInstanceOf[js.Any],
      "directional" -> directional.asInstanceOf[js.Any],
      "directionalShadow" -> directionalShadow.asInstanceOf[js.Any],
      "directionalShadowMap" -> directionalShadowMap.asInstanceOf[js.Any],
      "directionalShadowMatrix" -> directionalShadowMatrix.asInstanceOf[js.Any],
      "spot" -> spot.asInstanceOf[js.Any],
      "spotShadow" -> spotShadow.asInstanceOf[js.Any],
      "spotShadowMap" -> spotShadowMap.asInstanceOf[js.Any],
      "spotShadowMatrix" -> spotShadowMatrix.asInstanceOf[js.Any],
      "rectArea" -> rectArea.asInstanceOf[js.Any],
      "point" -> point.asInstanceOf[js.Any],
      "pointShadow" -> pointShadow.asInstanceOf[js.Any],
      "pointShadowMap" -> pointShadowMap.asInstanceOf[js.Any],
      "pointShadowMatrix" -> pointShadowMatrix.asInstanceOf[js.Any],
      "hemi" -> hemi.asInstanceOf[js.Any],
      "numSpotLightShadowsWithMaps" -> numSpotLightShadowsWithMaps.asInstanceOf[js.Any],
      "numLightProbes" -> numLightProbes.asInstanceOf[js.Any]
    ).asInstanceOf[WebGLLightsState]
