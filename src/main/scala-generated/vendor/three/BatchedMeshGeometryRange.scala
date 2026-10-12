// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait BatchedMeshGeometryRange extends js.Object:
  var count: Double = js.native
  var indexCount: Double = js.native
  var indexStart: Double = js.native
  var reservedIndexCount: Double = js.native
  var reservedVertexCount: Double = js.native
  var start: Double = js.native
  var vertexCount: Double = js.native
  var vertexStart: Double = js.native

object BatchedMeshGeometryRange:
  def apply(
    vertexStart: Double,
    vertexCount: Double,
    reservedVertexCount: Double,
    indexStart: Double,
    indexCount: Double,
    reservedIndexCount: Double,
    start: Double,
    count: Double
  ): BatchedMeshGeometryRange =
    js.Dynamic.literal(
      "vertexStart" -> vertexStart.asInstanceOf[js.Any],
      "vertexCount" -> vertexCount.asInstanceOf[js.Any],
      "reservedVertexCount" -> reservedVertexCount.asInstanceOf[js.Any],
      "indexStart" -> indexStart.asInstanceOf[js.Any],
      "indexCount" -> indexCount.asInstanceOf[js.Any],
      "reservedIndexCount" -> reservedIndexCount.asInstanceOf[js.Any],
      "start" -> start.asInstanceOf[js.Any],
      "count" -> count.asInstanceOf[js.Any]
    ).asInstanceOf[BatchedMeshGeometryRange]
