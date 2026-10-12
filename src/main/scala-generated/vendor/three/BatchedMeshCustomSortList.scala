// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait BatchedMeshCustomSortList extends js.Object:
  var count: Double = js.native
  var start: Double = js.native
  var z: Double = js.native

object BatchedMeshCustomSortList:
  def apply(
    start: Double,
    count: Double,
    z: Double
  ): BatchedMeshCustomSortList =
    js.Dynamic.literal(
      "start" -> start.asInstanceOf[js.Any],
      "count" -> count.asInstanceOf[js.Any],
      "z" -> z.asInstanceOf[js.Any]
    ).asInstanceOf[BatchedMeshCustomSortList]
