// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "AnimationUtils")
class AnimationUtils() extends js.Object

@js.native
@JSImport("three", "AnimationUtils")
object AnimationUtils extends js.Object:
  def convertArray(array: js.Array[Double] | js.typedarray.Float32Array | js.typedarray.Float64Array | js.typedarray.Int16Array | js.typedarray.Int32Array | js.typedarray.Int8Array | js.typedarray.Uint16Array | js.typedarray.Uint32Array | js.typedarray.Uint8Array | js.typedarray.Uint8ClampedArray, `type`: js.Object): js.Array[Double] | js.typedarray.Float32Array | js.typedarray.Float64Array | js.typedarray.Int16Array | js.typedarray.Int32Array | js.typedarray.Int8Array | js.typedarray.Uint16Array | js.typedarray.Uint32Array | js.typedarray.Uint8Array | js.typedarray.Uint8ClampedArray = js.native
  def flattenJSON(jsonKeys: js.Array[Double], times: js.Array[Double], values: js.Array[Double], valuePropertyName: String): Unit = js.native
  def getKeyframeOrder(times: js.Array[Double]): js.Array[Double] = js.native
  def isTypedArray(`object`: js.Any): Boolean = js.native
  def makeClipAdditive(targetClip: AnimationClip, referenceFrame: js.UndefOr[Double] = js.undefined, referenceClip: js.UndefOr[AnimationClip] = js.undefined, fps: js.UndefOr[Double] = js.undefined): AnimationClip = js.native
  def sortedArray(values: js.Array[Double], stride: Double, order: js.Array[Double]): js.Array[Double] = js.native
  def subclip(sourceClip: AnimationClip, name: String, startFrame: Double, endFrame: Double, fps: js.UndefOr[Double] = js.undefined): AnimationClip = js.native
