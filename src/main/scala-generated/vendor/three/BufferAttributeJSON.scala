// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait BufferAttributeJSON extends js.Object:
  var array: js.Array[Double] = js.native
  var itemSize: Double = js.native
  var name: js.UndefOr[String] = js.native
  var normalized: Boolean = js.native
  var `type`: String = js.native
  var usage: js.UndefOr[35040 | 35041 | 35042 | 35044 | 35045 | 35046 | 35048 | 35049 | 35050] = js.native

object BufferAttributeJSON:
  def apply(
    itemSize: Double,
    `type`: String,
    array: js.Array[Double],
    normalized: Boolean,
    name: js.UndefOr[String] = js.undefined,
    usage: js.UndefOr[35040 | 35041 | 35042 | 35044 | 35045 | 35046 | 35048 | 35049 | 35050] = js.undefined
  ): BufferAttributeJSON =
    val _res = js.Dynamic.literal(
      "itemSize" -> itemSize.asInstanceOf[js.Any],
      "type" -> `type`.asInstanceOf[js.Any],
      "array" -> array.asInstanceOf[js.Any],
      "normalized" -> normalized.asInstanceOf[js.Any]
    )
    name.foreach(v => _res.updateDynamic("name")(v.asInstanceOf[js.Any]))
    usage.foreach(v => _res.updateDynamic("usage")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[BufferAttributeJSON]
