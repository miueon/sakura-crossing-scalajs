// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "KeyframeTrack")
class KeyframeTrack(name_1: String, times_1: facadecontract.std.JSArrayLike[Double], values_1: facadecontract.std.JSArrayLike[String | Double | Boolean], interpolation: js.UndefOr[2300 | 2301 | 2302 | 2303] = js.undefined) extends js.Object:
  var DefaultInterpolation: 2300 | 2301 | 2302 | 2303 = js.native
  var name: String = js.native
  var TimeBufferType: js.Object = js.native
  var times: js.typedarray.Float32Array = js.native
  var ValueBufferType: js.Object = js.native
  var values: js.typedarray.Float32Array = js.native
  var ValueTypeName: String = js.native
  @JSName("clone")
  protected def clone$raw(): KeyframeTrack = js.native
  def getInterpolation(): 2300 | 2301 | 2302 | 2303 = js.native
  def getValueSize(): Double = js.native
  def InterpolantFactoryMethodBezier(result: js.UndefOr[js.typedarray.Float32Array | js.typedarray.Float64Array | js.typedarray.Int16Array | js.typedarray.Int32Array | js.typedarray.Int8Array | js.typedarray.Uint16Array | js.typedarray.Uint32Array | js.typedarray.Uint8Array | js.typedarray.Uint8ClampedArray] = js.undefined): BezierInterpolant = js.native
  def InterpolantFactoryMethodDiscrete(result: js.UndefOr[js.typedarray.Float32Array | js.typedarray.Float64Array | js.typedarray.Int16Array | js.typedarray.Int32Array | js.typedarray.Int8Array | js.typedarray.Uint16Array | js.typedarray.Uint32Array | js.typedarray.Uint8Array | js.typedarray.Uint8ClampedArray] = js.undefined): DiscreteInterpolant = js.native
  def InterpolantFactoryMethodLinear(result: js.UndefOr[js.typedarray.Float32Array | js.typedarray.Float64Array | js.typedarray.Int16Array | js.typedarray.Int32Array | js.typedarray.Int8Array | js.typedarray.Uint16Array | js.typedarray.Uint32Array | js.typedarray.Uint8Array | js.typedarray.Uint8ClampedArray] = js.undefined): LinearInterpolant = js.native
  def InterpolantFactoryMethodSmooth(result: js.UndefOr[js.typedarray.Float32Array | js.typedarray.Float64Array | js.typedarray.Int16Array | js.typedarray.Int32Array | js.typedarray.Int8Array | js.typedarray.Uint16Array | js.typedarray.Uint32Array | js.typedarray.Uint8Array | js.typedarray.Uint8ClampedArray] = js.undefined): CubicInterpolant = js.native
  @JSName("optimize")
  protected def optimize$raw(): KeyframeTrack = js.native
  def scale(timeScale: Double): KeyframeTrack = js.native
  def setInterpolation(interpolation: 2300 | 2301 | 2302 | 2303): KeyframeTrack = js.native
  def shift(timeOffset: Double): KeyframeTrack = js.native
  def trim(startTime: Double, endTime: Double): KeyframeTrack = js.native
  def validate(): Boolean = js.native


object KeyframeTrack:
  extension [Self <: KeyframeTrack](self: Self)
    inline def optimize(): Self = self.optimize$raw().asInstanceOf[Self]
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]

  @js.native
  @JSImport("three", "KeyframeTrack.toJSON")
  def toJSON(track: KeyframeTrack): KeyframeTrackJSON = js.native
