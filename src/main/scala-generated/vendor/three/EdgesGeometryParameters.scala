// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait EdgesGeometryParameters[TBufferGeometry <: BufferGeometry[?, ?]] extends js.Object:
  val geometry: TBufferGeometry | Null
  val thresholdAngle: Double

object EdgesGeometryParameters:
  def apply[TBufferGeometry <: BufferGeometry[?, ?]](
    geometry: TBufferGeometry | Null,
    thresholdAngle: Double
  ): EdgesGeometryParameters[TBufferGeometry] =
    js.Dynamic.literal(
      "geometry" -> geometry.asInstanceOf[js.Any],
      "thresholdAngle" -> thresholdAngle.asInstanceOf[js.Any]
    ).asInstanceOf[EdgesGeometryParameters[TBufferGeometry]]
