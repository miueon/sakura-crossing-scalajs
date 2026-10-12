// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "MathUtils")
object MathUtils extends js.Object:
  var DEG2RAD: Double = js.native
  var RAD2DEG: Double = js.native
  def ceilPowerOfTwo(value: Double): Double = js.native
  def clamp(value: Double, min: Double, max: Double): Double = js.native
  def damp(x: Double, y: Double, lambda: Double, dt: Double): Double = js.native
  def degToRad(degrees: Double): Double = js.native
  def denormalize(value: Double, array: js.typedarray.Float32Array | js.typedarray.Int16Array | js.typedarray.Int32Array | js.typedarray.Int8Array | js.typedarray.Uint16Array | js.typedarray.Uint32Array | js.typedarray.Uint8Array): Double = js.native
  def euclideanModulo(n: Double, m: Double): Double = js.native
  def floorPowerOfTwo(value: Double): Double = js.native
  def generateUUID(): String = js.native
  def inverseLerp(x: Double, y: Double, t: Double): Double = js.native
  def isPowerOfTwo(value: Double): Boolean = js.native
  def lerp(x: Double, y: Double, t: Double): Double = js.native
  def mapLinear(x: Double, a1: Double, a2: Double, b1: Double, b2: Double): Double = js.native
  def normalize(value: Double, array: js.typedarray.Float32Array | js.typedarray.Int16Array | js.typedarray.Int32Array | js.typedarray.Int8Array | js.typedarray.Uint16Array | js.typedarray.Uint32Array | js.typedarray.Uint8Array): Double = js.native
  def pingpong(x: Double, length: js.UndefOr[Double] = js.undefined): Double = js.native
  def radToDeg(radians: Double): Double = js.native
  def randFloat(low: Double, high: Double): Double = js.native
  def randFloatSpread(range: Double): Double = js.native
  def randInt(low: Double, high: Double): Double = js.native
  def seededRandom(seed: js.UndefOr[Double] = js.undefined): Double = js.native
  def setQuaternionFromProperEuler(q: Quaternion, a: Double, b: Double, c: Double, order: String): Unit = js.native
  def smootherstep(x: Double, min: Double, max: Double): Double = js.native
  def smoothstep(x: Double, min: Double, max: Double): Double = js.native
