// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "InterleavedBufferAttribute")
class InterleavedBufferAttribute(interleavedBuffer: InterleavedBuffer, itemSize_1: Double, offset_1: Double, normalized_1: js.UndefOr[Boolean] = js.undefined) extends js.Object:
  def array: js.typedarray.Float32Array | js.typedarray.Float64Array | js.typedarray.Int16Array | js.typedarray.Int32Array | js.typedarray.Int8Array | js.typedarray.Uint16Array | js.typedarray.Uint32Array | js.typedarray.Uint8Array | js.typedarray.Uint8ClampedArray = js.native
  def count: Double = js.native
  var data: InterleavedBuffer = js.native
  def isInterleavedBufferAttribute: true = js.native
  var itemSize: Double = js.native
  var name: String = js.native
  def needsUpdate_=(value: Boolean): Unit = js.native
  var normalized: Boolean = js.native
  var offset: Double = js.native
  @JSName("applyMatrix4")
  protected def applyMatrix4$raw(m: Matrix4): InterleavedBufferAttribute = js.native
  @JSName("applyNormalMatrix")
  protected def applyNormalMatrix$raw(m: Matrix3): InterleavedBufferAttribute = js.native
  def clone(data: js.UndefOr[js.Any] = js.undefined): BufferAttribute[BufferAttributeEventMap] = js.native
  def getComponent(index: Double, component: Double): Double = js.native
  def getW(index: Double): Double = js.native
  def getX(index: Double): Double = js.native
  def getY(index: Double): Double = js.native
  def getZ(index: Double): Double = js.native
  @JSName("setComponent")
  protected def setComponent$raw(index: Double, component: Double, value: Double): InterleavedBufferAttribute = js.native
  @JSName("setW")
  protected def setW$raw(index: Double, z: Double): InterleavedBufferAttribute = js.native
  @JSName("setX")
  protected def setX$raw(index: Double, x: Double): InterleavedBufferAttribute = js.native
  @JSName("setXY")
  protected def setXY$raw(index: Double, x: Double, y: Double): InterleavedBufferAttribute = js.native
  @JSName("setXYZ")
  protected def setXYZ$raw(index: Double, x: Double, y: Double, z: Double): InterleavedBufferAttribute = js.native
  @JSName("setXYZW")
  protected def setXYZW$raw(index: Double, x: Double, y: Double, z: Double, w: Double): InterleavedBufferAttribute = js.native
  @JSName("setY")
  protected def setY$raw(index: Double, y: Double): InterleavedBufferAttribute = js.native
  @JSName("setZ")
  protected def setZ$raw(index: Double, z: Double): InterleavedBufferAttribute = js.native
  def toJSON(data: js.UndefOr[js.Any] = js.undefined): InterleavedBufferAttributeToJSONReturn = js.native
  @JSName("transformDirection")
  protected def transformDirection$raw(m: Matrix4): InterleavedBufferAttribute = js.native

object InterleavedBufferAttribute:
  extension [Self <: InterleavedBufferAttribute](self: Self)
    inline def applyMatrix4(m: Matrix4): Self = self.applyMatrix4$raw(m).asInstanceOf[Self]
    inline def applyNormalMatrix(m: Matrix3): Self = self.applyNormalMatrix$raw(m).asInstanceOf[Self]
    inline def transformDirection(m: Matrix4): Self = self.transformDirection$raw(m).asInstanceOf[Self]
    inline def setComponent(index: Double, component: Double, value: Double): Self = self.setComponent$raw(index, component, value).asInstanceOf[Self]
    inline def setX(index: Double, x: Double): Self = self.setX$raw(index, x).asInstanceOf[Self]
    inline def setY(index: Double, y: Double): Self = self.setY$raw(index, y).asInstanceOf[Self]
    inline def setZ(index: Double, z: Double): Self = self.setZ$raw(index, z).asInstanceOf[Self]
    inline def setW(index: Double, z: Double): Self = self.setW$raw(index, z).asInstanceOf[Self]
    inline def setXY(index: Double, x: Double, y: Double): Self = self.setXY$raw(index, x, y).asInstanceOf[Self]
    inline def setXYZ(index: Double, x: Double, y: Double, z: Double): Self = self.setXYZ$raw(index, x, y, z).asInstanceOf[Self]
    inline def setXYZW(index: Double, x: Double, y: Double, z: Double, w: Double): Self = self.setXYZW$raw(index, x, y, z, w).asInstanceOf[Self]
