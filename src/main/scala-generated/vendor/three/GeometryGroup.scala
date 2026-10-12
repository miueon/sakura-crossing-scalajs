// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait GeometryGroup extends js.Object:
  var count: Double = js.native
  var materialIndex: js.UndefOr[Double] = js.native
  var start: Double = js.native

object GeometryGroup:
  def apply(
    start: Double,
    count: Double,
    materialIndex: Option[js.UndefOr[Double]] = None
  ): GeometryGroup =
    val _res = js.Dynamic.literal(
      "start" -> start.asInstanceOf[js.Any],
      "count" -> count.asInstanceOf[js.Any]
    )
    materialIndex.foreach(v => _res.updateDynamic("materialIndex")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[GeometryGroup]
