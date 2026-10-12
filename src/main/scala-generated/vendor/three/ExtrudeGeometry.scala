// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

// #WARN: heritage application of 'BufferGeometry' widens the argument of binder 'Attributes' to the substituted bound (argument-conformance-unproven); a checker-proven structural relation materialized as synthetic heritage is the lane that keeps such an argument exact
@js.native
@JSImport("three", "ExtrudeGeometry")
class ExtrudeGeometry(shapes: js.UndefOr[Shape | js.Array[Shape]] = js.undefined, options: js.UndefOr[ExtrudeGeometryOptions] = js.undefined) extends BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap]:
  def parameters: ExtrudeGeometryParameters = js.native
  override def `type`: String = js.native
  @JSName("copy")
  protected override def copy$raw(source: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap]): ExtrudeGeometry = js.native

object ExtrudeGeometry:
  extension [Self <: ExtrudeGeometry](self: Self)
    inline def copy(source: ExtrudeGeometry): Self = self.copy$raw(source).asInstanceOf[Self]
