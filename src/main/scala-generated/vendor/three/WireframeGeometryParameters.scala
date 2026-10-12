// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait WireframeGeometryParameters[TBufferGeometry <: BufferGeometry[?, ?]] extends js.Object:
  val geometry: TBufferGeometry

object WireframeGeometryParameters:
  def apply[TBufferGeometry <: BufferGeometry[?, ?]](
    geometry: TBufferGeometry
  ): WireframeGeometryParameters[TBufferGeometry] =
    js.Dynamic.literal("geometry" -> geometry.asInstanceOf[js.Any]).asInstanceOf[WireframeGeometryParameters[TBufferGeometry]]
