// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

// #WARN: heritage application of 'BufferGeometry' widens the argument of binder 'Attributes' to the substituted bound (argument-conformance-unproven); a checker-proven structural relation materialized as synthetic heritage is the lane that keeps such an argument exact
@js.native
@JSImport("three", "LatheGeometry")
class LatheGeometry(points: js.UndefOr[js.Array[Vector2]] = js.undefined, segments: js.UndefOr[Double] = js.undefined, phiStart: js.UndefOr[Double] = js.undefined, phiLength: js.UndefOr[Double] = js.undefined) extends BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap]:
  def parameters: LatheGeometryParameters = js.native
  override def `type`: String = js.native

@js.native
@JSImport("three", "LatheGeometry")
object LatheGeometry extends js.Object:
  def fromJSON(data: js.Any): LatheGeometry = js.native
