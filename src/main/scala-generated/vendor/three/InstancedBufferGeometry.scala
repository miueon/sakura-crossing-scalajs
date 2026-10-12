// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

// #WARN: heritage application of 'BufferGeometry' widens the argument of binder 'Attributes' to the substituted bound (argument-conformance-unproven); a checker-proven structural relation materialized as synthetic heritage is the lane that keeps such an argument exact
@js.native
@JSImport("three", "InstancedBufferGeometry")
class InstancedBufferGeometry() extends BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap]:
  var instanceCount: Double = js.native
  def isInstancedBufferGeometry: true = js.native
  override def `type`: String = js.native
  def `type_=`(value: String): Unit = js.native
  @JSName("copy")
  protected override def copy$raw(source: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap]): InstancedBufferGeometry = js.native

object InstancedBufferGeometry:
  extension [Self <: InstancedBufferGeometry](self: Self)
    inline def copy(source: InstancedBufferGeometry): Self = self.copy$raw(source).asInstanceOf[Self]
