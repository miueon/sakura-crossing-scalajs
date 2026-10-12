// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "BufferAttribute")
class BufferAttribute[TEventMap <: BufferAttributeEventMap](array_1: js.typedarray.Float32Array | js.typedarray.Float64Array | js.typedarray.Int16Array | js.typedarray.Int32Array | js.typedarray.Int8Array | js.typedarray.Uint16Array | js.typedarray.Uint32Array | js.typedarray.Uint8Array | js.typedarray.Uint8ClampedArray, itemSize_1: Double, normalized_1: js.UndefOr[Boolean] = js.undefined) extends EventDispatcher[TEventMap]:
  var array: js.typedarray.Float32Array | js.typedarray.Float64Array | js.typedarray.Int16Array | js.typedarray.Int32Array | js.typedarray.Int8Array | js.typedarray.Uint16Array | js.typedarray.Uint32Array | js.typedarray.Uint8Array | js.typedarray.Uint8ClampedArray = js.native
  def count: Double = js.native
  var gpuType: 1013 | 1015 = js.native
  def id: Double = js.native
  def isBufferAttribute: true = js.native
  var itemSize: Double = js.native
  var name: String = js.native
  def needsUpdate_=(value: Boolean): Unit = js.native
  var normalized: Boolean = js.native
  var updateRanges: js.Array[BoxGeometryDrawRange] = js.native
  var usage: 35040 | 35041 | 35042 | 35044 | 35045 | 35046 | 35048 | 35049 | 35050 = js.native
  var version: Double = js.native
  def addUpdateRange(start: Double, count: Double): Unit = js.native
  @JSName("applyMatrix3")
  protected def applyMatrix3$raw(m: Matrix3): BufferAttribute[TEventMap] = js.native
  @JSName("applyMatrix4")
  protected def applyMatrix4$raw(m: Matrix4): BufferAttribute[TEventMap] = js.native
  @JSName("applyNormalMatrix")
  protected def applyNormalMatrix$raw(m: Matrix3): BufferAttribute[TEventMap] = js.native
  def clearUpdateRanges(): Unit = js.native
  override def clone(): BufferAttribute[BufferAttributeEventMap] = js.native
  @JSName("copy")
  protected def copy$raw(source: BufferAttribute[BufferAttributeEventMap]): BufferAttribute[TEventMap] = js.native
  @JSName("copyArray")
  protected def copyArray$raw(array: facadecontract.std.JSArrayLike[Double]): BufferAttribute[TEventMap] = js.native
  @JSName("copyAt")
  protected def copyAt$raw(index1: Double, attribute: BufferAttribute[BufferAttributeEventMap], index2: Double): BufferAttribute[TEventMap] = js.native
  def dispose(): Unit = js.native
  def getComponent(index: Double, component: Double): Double = js.native
  def getW(index: Double): Double = js.native
  def getX(index: Double): Double = js.native
  def getY(index: Double): Double = js.native
  def getZ(index: Double): Double = js.native
  @JSName("onUpload")
  protected def onUpload$raw(callback: js.Function0[Unit]): BufferAttribute[TEventMap] = js.native
  def onUploadCallback(): Unit = js.native
  @JSName("set")
  protected def set$raw(value: facadecontract.std.JSArrayLike[Double] | js.typedarray.ArrayBufferView, offset: js.UndefOr[Double] = js.undefined): BufferAttribute[TEventMap] = js.native
  def setComponent(index: Double, component: Double, value: Double): Unit = js.native
  @JSName("setUsage")
  protected def setUsage$raw(usage: 35040 | 35041 | 35042 | 35044 | 35045 | 35046 | 35048 | 35049 | 35050): BufferAttribute[TEventMap] = js.native
  @JSName("setW")
  protected def setW$raw(index: Double, z: Double): BufferAttribute[TEventMap] = js.native
  @JSName("setX")
  protected def setX$raw(index: Double, x: Double): BufferAttribute[TEventMap] = js.native
  @JSName("setXY")
  protected def setXY$raw(index: Double, x: Double, y: Double): BufferAttribute[TEventMap] = js.native
  @JSName("setXYZ")
  protected def setXYZ$raw(index: Double, x: Double, y: Double, z: Double): BufferAttribute[TEventMap] = js.native
  @JSName("setXYZW")
  protected def setXYZW$raw(index: Double, x: Double, y: Double, z: Double, w: Double): BufferAttribute[TEventMap] = js.native
  @JSName("setY")
  protected def setY$raw(index: Double, y: Double): BufferAttribute[TEventMap] = js.native
  @JSName("setZ")
  protected def setZ$raw(index: Double, z: Double): BufferAttribute[TEventMap] = js.native
  def toJSON(): BufferAttributeJSON = js.native
  @JSName("transformDirection")
  protected def transformDirection$raw(m: Matrix4): BufferAttribute[TEventMap] = js.native

object BufferAttribute:
  extension [TEventMap <: BufferAttributeEventMap, Self <: BufferAttribute[TEventMap]](self: Self)
    inline def onUpload(callback: js.Function0[Unit]): Self = self.onUpload$raw(callback).asInstanceOf[Self]
    inline def setUsage(usage: 35040 | 35041 | 35042 | 35044 | 35045 | 35046 | 35048 | 35049 | 35050): Self = self.setUsage$raw(usage).asInstanceOf[Self]
    inline def copy(source: BufferAttribute[BufferAttributeEventMap]): Self = self.copy$raw(source).asInstanceOf[Self]
    inline def copyAt(index1: Double, attribute: BufferAttribute[BufferAttributeEventMap], index2: Double): Self = self.copyAt$raw(index1, attribute, index2).asInstanceOf[Self]
    inline def copyArray(array: facadecontract.std.JSArrayLike[Double]): Self = self.copyArray$raw(array).asInstanceOf[Self]
    inline def applyMatrix3(m: Matrix3): Self = self.applyMatrix3$raw(m).asInstanceOf[Self]
    inline def applyMatrix4(m: Matrix4): Self = self.applyMatrix4$raw(m).asInstanceOf[Self]
    inline def applyNormalMatrix(m: Matrix3): Self = self.applyNormalMatrix$raw(m).asInstanceOf[Self]
    inline def transformDirection(m: Matrix4): Self = self.transformDirection$raw(m).asInstanceOf[Self]
    inline def set(value: facadecontract.std.JSArrayLike[Double] | js.typedarray.ArrayBufferView, offset: js.UndefOr[Double] = js.undefined): Self = self.set$raw(value, offset).asInstanceOf[Self]
    inline def setX(index: Double, x: Double): Self = self.setX$raw(index, x).asInstanceOf[Self]
    inline def setY(index: Double, y: Double): Self = self.setY$raw(index, y).asInstanceOf[Self]
    inline def setZ(index: Double, z: Double): Self = self.setZ$raw(index, z).asInstanceOf[Self]
    inline def setW(index: Double, z: Double): Self = self.setW$raw(index, z).asInstanceOf[Self]
    inline def setXY(index: Double, x: Double, y: Double): Self = self.setXY$raw(index, x, y).asInstanceOf[Self]
    inline def setXYZ(index: Double, x: Double, y: Double, z: Double): Self = self.setXYZ$raw(index, x, y, z).asInstanceOf[Self]
    inline def setXYZW(index: Double, x: Double, y: Double, z: Double, w: Double): Self = self.setXYZW$raw(index, x, y, z, w).asInstanceOf[Self]
