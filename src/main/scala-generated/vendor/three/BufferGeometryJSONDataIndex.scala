// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait BufferGeometryJSONDataIndex extends js.Object:
  var array: js.Array[Double] = js.native
  var `type`: String = js.native

object BufferGeometryJSONDataIndex:
  def apply(
    `type`: String,
    array: js.Array[Double]
  ): BufferGeometryJSONDataIndex =
    js.Dynamic.literal(
      "type" -> `type`.asInstanceOf[js.Any],
      "array" -> array.asInstanceOf[js.Any]
    ).asInstanceOf[BufferGeometryJSONDataIndex]
