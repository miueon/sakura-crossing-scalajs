// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait LightJSON extends Object3DJSON:
  var color: Double = js.native
  var intensity: Double = js.native

object LightJSON:
  def apply(
    color: Double,
    intensity: Double,
    metadata: js.UndefOr[BufferGeometryJSONMetadata] = js.undefined,
    `object`: Object3DJSONObject
  ): LightJSON =
    val _res = js.Dynamic.literal(
      "color" -> color.asInstanceOf[js.Any],
      "intensity" -> intensity.asInstanceOf[js.Any]
    )
    metadata.foreach(v => _res.updateDynamic("metadata")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("object")(`object`.asInstanceOf[js.Any])
    _res.asInstanceOf[LightJSON]
