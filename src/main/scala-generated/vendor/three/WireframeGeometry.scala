// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

// #WARN: heritage application of 'BufferGeometry' widens the argument of binder 'Attributes' to the substituted bound (argument-conformance-unproven); a checker-proven structural relation materialized as synthetic heritage is the lane that keeps such an argument exact
@js.native
@JSImport("three", "WireframeGeometry")
class WireframeGeometry[TBufferGeometry <: BufferGeometry[?, ?]](geometry: js.UndefOr[TBufferGeometry] = js.undefined) extends BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap]:
  def parameters: WireframeGeometryParameters[TBufferGeometry] = js.native
  override def `type`: String = js.native
