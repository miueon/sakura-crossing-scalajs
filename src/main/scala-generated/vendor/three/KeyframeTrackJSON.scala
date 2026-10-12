// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait KeyframeTrackJSON extends js.Object:
  var interpolation: js.UndefOr[2300 | 2301 | 2302 | 2303] = js.native
  var name: String = js.native
  var times: js.Array[Double] = js.native
  var `type`: String = js.native
  var values: js.Array[Double] = js.native

object KeyframeTrackJSON:
  def apply(
    name: String,
    times: js.Array[Double],
    values: js.Array[Double],
    interpolation: js.UndefOr[2300 | 2301 | 2302 | 2303] = js.undefined,
    `type`: String
  ): KeyframeTrackJSON =
    val _res = js.Dynamic.literal(
      "name" -> name.asInstanceOf[js.Any],
      "times" -> times.asInstanceOf[js.Any],
      "values" -> values.asInstanceOf[js.Any]
    )
    interpolation.foreach(v => _res.updateDynamic("interpolation")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("type")(`type`.asInstanceOf[js.Any])
    _res.asInstanceOf[KeyframeTrackJSON]
