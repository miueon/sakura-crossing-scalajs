// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait DirectionalLightJSON extends LightJSON:
  var shadow: LightShadowJSON = js.native
  var target: String = js.native

object DirectionalLightJSON:
  def apply(
    shadow: LightShadowJSON,
    target: String,
    color: Double,
    intensity: Double,
    metadata: js.UndefOr[BufferGeometryJSONMetadata] = js.undefined,
    `object`: Object3DJSONObject
  ): DirectionalLightJSON =
    val _res = js.Dynamic.literal(
      "shadow" -> shadow.asInstanceOf[js.Any],
      "target" -> target.asInstanceOf[js.Any],
      "color" -> color.asInstanceOf[js.Any],
      "intensity" -> intensity.asInstanceOf[js.Any]
    )
    metadata.foreach(v => _res.updateDynamic("metadata")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("object")(`object`.asInstanceOf[js.Any])
    _res.asInstanceOf[DirectionalLightJSON]
