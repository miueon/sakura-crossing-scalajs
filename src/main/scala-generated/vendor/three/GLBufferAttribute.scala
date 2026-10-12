// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "GLBufferAttribute")
class GLBufferAttribute(buffer_1: js.Object, type_1: Double, itemSize_1: Double, elementSize_1: 1 | 2 | 4, count_1: Double, normalized_1: js.UndefOr[Boolean] = js.undefined) extends js.Object:
  var buffer: js.Object = js.native
  var count: Double = js.native
  var elementSize: 1 | 2 | 4 = js.native
  def isGLBufferAttribute: true = js.native
  var itemSize: Double = js.native
  var name: String = js.native
  def needsUpdate_=(value: Boolean): Unit = js.native
  var normalized: Boolean = js.native
  var `type`: Double = js.native
  var version: Double = js.native
  @JSName("setBuffer")
  protected def setBuffer$raw(buffer: js.Object): GLBufferAttribute = js.native
  @JSName("setCount")
  protected def setCount$raw(count: Double): GLBufferAttribute = js.native
  @JSName("setItemSize")
  protected def setItemSize$raw(itemSize: Double): GLBufferAttribute = js.native
  @JSName("setType")
  protected def setType$raw(`type`: Double, elementSize: 1 | 2 | 4): GLBufferAttribute = js.native

object GLBufferAttribute:
  extension [Self <: GLBufferAttribute](self: Self)
    inline def setBuffer(buffer: js.Object): Self = self.setBuffer$raw(buffer).asInstanceOf[Self]
    inline def setType(`type`: Double, elementSize: 1 | 2 | 4): Self = self.setType$raw(`type`, elementSize).asInstanceOf[Self]
    inline def setItemSize(itemSize: Double): Self = self.setItemSize$raw(itemSize).asInstanceOf[Self]
    inline def setCount(count: Double): Self = self.setCount$raw(count).asInstanceOf[Self]
