// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait PointLightJSON extends LightJSON:
  var decay: Double = js.native
  var distance: Double = js.native
  var shadow: LightShadowJSON = js.native

object PointLightJSON:
  def apply(
    distance: Double,
    decay: Double,
    shadow: LightShadowJSON,
    color: Double,
    intensity: Double,
    metadata: js.UndefOr[BufferGeometryJSONMetadata] = js.undefined,
    `object`: Object3DJSONObject
  ): PointLightJSON =
    val _res = js.Dynamic.literal(
      "distance" -> distance.asInstanceOf[js.Any],
      "decay" -> decay.asInstanceOf[js.Any],
      "shadow" -> shadow.asInstanceOf[js.Any],
      "color" -> color.asInstanceOf[js.Any],
      "intensity" -> intensity.asInstanceOf[js.Any]
    )
    metadata.foreach(v => _res.updateDynamic("metadata")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("object")(`object`.asInstanceOf[js.Any])
    _res.asInstanceOf[PointLightJSON]
