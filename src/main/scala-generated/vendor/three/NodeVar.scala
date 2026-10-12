// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait NodeVar extends js.Object:
  var count: Double | Null = js.native
  def isNodeVar: Boolean = js.native
  var name: String = js.native
  var readOnly: Boolean = js.native
  var `type`: String = js.native

object NodeVar:
  def apply(
    isNodeVar: Boolean,
    name: String,
    `type`: String,
    readOnly: Boolean,
    count: Double | Null
  ): NodeVar =
    js.Dynamic.literal(
      "isNodeVar" -> isNodeVar.asInstanceOf[js.Any],
      "name" -> name.asInstanceOf[js.Any],
      "type" -> `type`.asInstanceOf[js.Any],
      "readOnly" -> readOnly.asInstanceOf[js.Any],
      "count" -> count.asInstanceOf[js.Any]
    ).asInstanceOf[NodeVar]
