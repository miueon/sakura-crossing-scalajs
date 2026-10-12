// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait SpotLightJSON extends LightJSON:
  var angle: Double = js.native
  var decay: Double = js.native
  var distance: Double = js.native
  var map: js.UndefOr[String] = js.native
  var penumbra: Double = js.native
  var shadow: LightShadowJSON = js.native
  var target: String = js.native

object SpotLightJSON:
  def apply(
    distance: Double,
    angle: Double,
    decay: Double,
    penumbra: Double,
    target: String,
    map: Option[js.UndefOr[String]] = None,
    shadow: LightShadowJSON,
    color: Double,
    intensity: Double,
    metadata: js.UndefOr[BufferGeometryJSONMetadata] = js.undefined,
    `object`: Object3DJSONObject
  ): SpotLightJSON =
    val _res = js.Dynamic.literal(
      "distance" -> distance.asInstanceOf[js.Any],
      "angle" -> angle.asInstanceOf[js.Any],
      "decay" -> decay.asInstanceOf[js.Any],
      "penumbra" -> penumbra.asInstanceOf[js.Any],
      "target" -> target.asInstanceOf[js.Any]
    )
    map.foreach(v => _res.updateDynamic("map")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("shadow")(shadow.asInstanceOf[js.Any])
    _res.updateDynamic("color")(color.asInstanceOf[js.Any])
    _res.updateDynamic("intensity")(intensity.asInstanceOf[js.Any])
    metadata.foreach(v => _res.updateDynamic("metadata")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("object")(`object`.asInstanceOf[js.Any])
    _res.asInstanceOf[SpotLightJSON]
