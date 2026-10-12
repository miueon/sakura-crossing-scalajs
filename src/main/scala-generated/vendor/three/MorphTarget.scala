// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait MorphTarget extends js.Object:
  var name: String = js.native
  var vertices: js.Array[Vector3] = js.native

object MorphTarget:
  def apply(
    name: String,
    vertices: js.Array[Vector3]
  ): MorphTarget =
    js.Dynamic.literal(
      "name" -> name.asInstanceOf[js.Any],
      "vertices" -> vertices.asInstanceOf[js.Any]
    ).asInstanceOf[MorphTarget]
