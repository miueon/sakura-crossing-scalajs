// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

// #WARN: Disambiguated erasure-colliding overload 'toArray' with @targetName("toArray_1") and @JSName("toArray")
// #WARN: Disambiguated erasure-colliding overload 'toArray' with @targetName("toArray_2") and @JSName("toArray")
@js.native
@JSImport("three", "Quaternion")
class Quaternion(x_1: js.UndefOr[Double] = js.undefined, y_1: js.UndefOr[Double] = js.undefined, z_1: js.UndefOr[Double] = js.undefined, w_1: js.UndefOr[Double] = js.undefined) extends js.Object:
  def isQuaternion: true = js.native
  var w: Double = js.native
  var x: Double = js.native
  var y: Double = js.native
  var z: Double = js.native
  @JSName("_onChange")
  protected def _onChange$raw(callback: js.Function0[Unit]): Quaternion = js.native
  def _onChangeCallback(): Unit = js.native
  def angleTo(q: Quaternion): Double = js.native
  @JSName("clone")
  protected def clone$raw(): Quaternion = js.native
  @JSName("conjugate")
  protected def conjugate$raw(): Quaternion = js.native
  @JSName("copy")
  protected def copy$raw(q: QuaternionLike): Quaternion = js.native
  def dot(v: Quaternion): Double = js.native
  // #WARN: Quaternion.equals collides with the pinned platform member 'java.lang.Object#equals' only at the Scala source name; the JS wire key 'equals' is preserved with @JSName and the member is emitted under a derived collision-free name (the inherited member stays callable under Scala semantics)
  @JSName("equals")
  def equals_1(v: Quaternion): Boolean = js.native
  @JSName("fromArray")
  protected def fromArray$raw(array: facadecontract.std.JSArrayLike[Double] | js.Array[Double], offset: js.UndefOr[Double] = js.undefined): Quaternion = js.native
  @JSName("fromBufferAttribute")
  protected def fromBufferAttribute$raw(attribute: BufferAttribute[BufferAttributeEventMap] | InterleavedBufferAttribute, index: Double): Quaternion = js.native
  @JSName("identity")
  protected def identity$raw(): Quaternion = js.native
  @JSName("invert")
  protected def invert$raw(): Quaternion = js.native
  @JSName(js.Symbol.iterator)
  def jsIterator(): js.Object = js.native
  def length(): Double = js.native
  def lengthSq(): Double = js.native
  @JSName("multiply")
  protected def multiply$raw(q: Quaternion): Quaternion = js.native
  @JSName("multiplyQuaternions")
  protected def multiplyQuaternions$raw(a: Quaternion, b: Quaternion): Quaternion = js.native
  @JSName("normalize")
  protected def normalize$raw(): Quaternion = js.native
  @JSName("premultiply")
  protected def premultiply$raw(q: Quaternion): Quaternion = js.native
  @JSName("random")
  protected def random$raw(): Quaternion = js.native
  @JSName("rotateTowards")
  protected def rotateTowards$raw(q: Quaternion, step: Double): Quaternion = js.native
  @JSName("set")
  protected def set$raw(x: Double, y: Double, z: Double, w: Double): Quaternion = js.native
  @JSName("setFromAxisAngle")
  protected def setFromAxisAngle$raw(axis: Vector3Like, angle: Double): Quaternion = js.native
  @JSName("setFromEuler")
  protected def setFromEuler$raw(euler: Euler, update: js.UndefOr[Boolean] = js.undefined): Quaternion = js.native
  @JSName("setFromRotationMatrix")
  protected def setFromRotationMatrix$raw(m: Matrix4): Quaternion = js.native
  @JSName("setFromUnitVectors")
  protected def setFromUnitVectors$raw(vFrom: Vector3, vTo: Vector3Like): Quaternion = js.native
  @JSName("slerp")
  protected def slerp$raw(qb: Quaternion, t: Double): Quaternion = js.native
  @JSName("slerpQuaternions")
  protected def slerpQuaternions$raw(qa: Quaternion, qb: Quaternion, t: Double): Quaternion = js.native
  def toArray(array: js.UndefOr[js.Array[Double]] = js.undefined, offset: js.UndefOr[Double] = js.undefined): js.Array[Double] = js.native
  // #WARN: Disambiguated erasure-colliding overload 'toArray' with @targetName("toArray_1") and @JSName("toArray")
  @scala.annotation.targetName("toArray_1")
  @JSName("toArray")
  def toArray(array: js.UndefOr[js.Tuple4[Double, Double, Double, Double]], offset: js.UndefOr[0]): js.Tuple4[Double, Double, Double, Double] = js.native
  // #WARN: Disambiguated erasure-colliding overload 'toArray' with @targetName("toArray_2") and @JSName("toArray")
  @scala.annotation.targetName("toArray_2")
  @JSName("toArray")
  def toArray(array: facadecontract.std.JSArrayLike[Double], offset: js.UndefOr[Double]): facadecontract.std.JSArrayLike[Double] = js.native
  def toJSON(): js.Tuple4[Double, Double, Double, Double] = js.native


object Quaternion:
  extension [Self <: Quaternion](self: Self)
    inline def set(x: Double, y: Double, z: Double, w: Double): Self = self.set$raw(x, y, z, w).asInstanceOf[Self]
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]
    inline def copy(q: QuaternionLike): Self = self.copy$raw(q).asInstanceOf[Self]
    inline def setFromEuler(euler: Euler, update: js.UndefOr[Boolean] = js.undefined): Self = self.setFromEuler$raw(euler, update).asInstanceOf[Self]
    inline def setFromAxisAngle(axis: Vector3Like, angle: Double): Self = self.setFromAxisAngle$raw(axis, angle).asInstanceOf[Self]
    inline def setFromRotationMatrix(m: Matrix4): Self = self.setFromRotationMatrix$raw(m).asInstanceOf[Self]
    inline def setFromUnitVectors(vFrom: Vector3, vTo: Vector3Like): Self = self.setFromUnitVectors$raw(vFrom, vTo).asInstanceOf[Self]
    inline def rotateTowards(q: Quaternion, step: Double): Self = self.rotateTowards$raw(q, step).asInstanceOf[Self]
    inline def identity(): Self = self.identity$raw().asInstanceOf[Self]
    inline def invert(): Self = self.invert$raw().asInstanceOf[Self]
    inline def conjugate(): Self = self.conjugate$raw().asInstanceOf[Self]
    inline def normalize(): Self = self.normalize$raw().asInstanceOf[Self]
    inline def multiply(q: Quaternion): Self = self.multiply$raw(q).asInstanceOf[Self]
    inline def premultiply(q: Quaternion): Self = self.premultiply$raw(q).asInstanceOf[Self]
    inline def multiplyQuaternions(a: Quaternion, b: Quaternion): Self = self.multiplyQuaternions$raw(a, b).asInstanceOf[Self]
    inline def slerp(qb: Quaternion, t: Double): Self = self.slerp$raw(qb, t).asInstanceOf[Self]
    inline def slerpQuaternions(qa: Quaternion, qb: Quaternion, t: Double): Self = self.slerpQuaternions$raw(qa, qb, t).asInstanceOf[Self]
    inline def fromArray(array: facadecontract.std.JSArrayLike[Double] | js.Array[Double], offset: js.UndefOr[Double] = js.undefined): Self = self.fromArray$raw(array, offset).asInstanceOf[Self]
    inline def fromBufferAttribute(attribute: BufferAttribute[BufferAttributeEventMap] | InterleavedBufferAttribute, index: Double): Self = self.fromBufferAttribute$raw(attribute, index).asInstanceOf[Self]
    inline def _onChange(callback: js.Function0[Unit]): Self = self._onChange$raw(callback).asInstanceOf[Self]
    inline def random(): Self = self.random$raw().asInstanceOf[Self]

  @js.native
  @JSImport("three", "Quaternion.multiplyQuaternionsFlat")
  def multiplyQuaternionsFlat(dst: js.Array[Double], dstOffset: Double, src0: js.Array[Double], srcOffset: Double, src1: js.Array[Double], stcOffset1: Double): js.Array[Double] = js.native
  @js.native
  @JSImport("three", "Quaternion.slerpFlat")
  def slerpFlat(dst: js.Array[Double], dstOffset: Double, src0: js.Array[Double], srcOffset: Double, src1: js.Array[Double], stcOffset1: Double, t: Double): Unit = js.native
