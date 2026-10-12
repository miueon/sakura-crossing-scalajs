// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait HemisphereLightJSON extends LightJSON:
  var groundColor: Double = js.native

object HemisphereLightJSON:
  def apply(
    groundColor: Double,
    color: Double,
    intensity: Double,
    metadata: js.UndefOr[BufferGeometryJSONMetadata] = js.undefined,
    `object`: Object3DJSONObject
  ): HemisphereLightJSON =
    val _res = js.Dynamic.literal(
      "groundColor" -> groundColor.asInstanceOf[js.Any],
      "color" -> color.asInstanceOf[js.Any],
      "intensity" -> intensity.asInstanceOf[js.Any]
    )
    metadata.foreach(v => _res.updateDynamic("metadata")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("object")(`object`.asInstanceOf[js.Any])
    _res.asInstanceOf[HemisphereLightJSON]
