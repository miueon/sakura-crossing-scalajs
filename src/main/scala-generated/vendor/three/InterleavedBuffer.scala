// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "InterleavedBuffer")
class InterleavedBuffer(array_1: js.typedarray.Float32Array | js.typedarray.Float64Array | js.typedarray.Int16Array | js.typedarray.Int32Array | js.typedarray.Int8Array | js.typedarray.Uint16Array | js.typedarray.Uint32Array | js.typedarray.Uint8Array | js.typedarray.Uint8ClampedArray, stride_1: Double) extends js.Object:
  var array: js.typedarray.Float32Array | js.typedarray.Float64Array | js.typedarray.Int16Array | js.typedarray.Int32Array | js.typedarray.Int8Array | js.typedarray.Uint16Array | js.typedarray.Uint32Array | js.typedarray.Uint8Array | js.typedarray.Uint8ClampedArray = js.native
  var count: Double = js.native
  def isInterleavedBuffer: true = js.native
  def needsUpdate_=(value: Boolean): Unit = js.native
  var stride: Double = js.native
  var updateRanges: js.Array[BoxGeometryDrawRange] = js.native
  var usage: 35040 | 35041 | 35042 | 35044 | 35045 | 35046 | 35048 | 35049 | 35050 = js.native
  var uuid: String = js.native
  var version: Double = js.native
  def addUpdateRange(start: Double, count: Double): Unit = js.native
  def clearUpdateRanges(): Unit = js.native
  def clone(data: js.Any): InterleavedBuffer = js.native
  @JSName("copy")
  protected def copy$raw(source: InterleavedBuffer): InterleavedBuffer = js.native
  @JSName("copyAt")
  protected def copyAt$raw(index1: Double, attribute: InterleavedBufferAttribute, index2: Double): InterleavedBuffer = js.native
  @JSName("onUpload")
  protected def onUpload$raw(callback: js.Function0[Unit]): InterleavedBuffer = js.native
  def onUploadCallback(): Unit = js.native
  @JSName("set")
  protected def set$raw(value: facadecontract.std.JSArrayLike[Double], offset: js.UndefOr[Double] = js.undefined): InterleavedBuffer = js.native
  @JSName("setUsage")
  protected def setUsage$raw(value: 35040 | 35041 | 35042 | 35044 | 35045 | 35046 | 35048 | 35049 | 35050): InterleavedBuffer = js.native
  def toJSON(data: js.Any): InstancedInterleavedBufferToJSONReturn = js.native

object InterleavedBuffer:
  extension [Self <: InterleavedBuffer](self: Self)
    inline def onUpload(callback: js.Function0[Unit]): Self = self.onUpload$raw(callback).asInstanceOf[Self]
    inline def set(value: facadecontract.std.JSArrayLike[Double], offset: js.UndefOr[Double] = js.undefined): Self = self.set$raw(value, offset).asInstanceOf[Self]
    inline def setUsage(value: 35040 | 35041 | 35042 | 35044 | 35045 | 35046 | 35048 | 35049 | 35050): Self = self.setUsage$raw(value).asInstanceOf[Self]
    inline def copy(source: InterleavedBuffer): Self = self.copy$raw(source).asInstanceOf[Self]
    inline def copyAt(index1: Double, attribute: InterleavedBufferAttribute, index2: Double): Self = self.copyAt$raw(index1, attribute, index2).asInstanceOf[Self]
