// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait LightShadowJSON extends js.Object:
  var bias: js.UndefOr[Double] = js.native
  var camera: OmitObject3DJSONObjectMatrix = js.native
  var intensity: js.UndefOr[Double] = js.native
  var mapSize: js.UndefOr[js.Tuple2[Double, Double]] = js.native
  var normalBias: js.UndefOr[Double] = js.native
  var radius: js.UndefOr[Double] = js.native

object LightShadowJSON:
  def apply(
    intensity: js.UndefOr[Double] = js.undefined,
    bias: js.UndefOr[Double] = js.undefined,
    normalBias: js.UndefOr[Double] = js.undefined,
    radius: js.UndefOr[Double] = js.undefined,
    mapSize: js.UndefOr[js.Tuple2[Double, Double]] = js.undefined,
    camera: OmitObject3DJSONObjectMatrix
  ): LightShadowJSON =
    val _res = js.Dynamic.literal()
    intensity.foreach(v => _res.updateDynamic("intensity")(v.asInstanceOf[js.Any]))
    bias.foreach(v => _res.updateDynamic("bias")(v.asInstanceOf[js.Any]))
    normalBias.foreach(v => _res.updateDynamic("normalBias")(v.asInstanceOf[js.Any]))
    radius.foreach(v => _res.updateDynamic("radius")(v.asInstanceOf[js.Any]))
    mapSize.foreach(v => _res.updateDynamic("mapSize")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("camera")(camera.asInstanceOf[js.Any])
    _res.asInstanceOf[LightShadowJSON]
