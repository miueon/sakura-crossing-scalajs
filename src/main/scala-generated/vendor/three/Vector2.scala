// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

// #WARN: Disambiguated erasure-colliding overload 'toArray' with @targetName("toArray_1") and @JSName("toArray")
// #WARN: Disambiguated erasure-colliding overload 'toArray' with @targetName("toArray_2") and @JSName("toArray")
@js.native
@JSImport("three", "Vector2")
class Vector2(x_1: js.UndefOr[Double] = js.undefined, y_1: js.UndefOr[Double] = js.undefined) extends js.Object:
  var height: Double = js.native
  def isVector2: true = js.native
  var width: Double = js.native
  var x: Double = js.native
  var y: Double = js.native
  @JSName("add")
  protected def add$raw(v: Vector2Like): Vector2 = js.native
  @JSName("addScalar")
  protected def addScalar$raw(s: Double): Vector2 = js.native
  @JSName("addScaledVector")
  protected def addScaledVector$raw(v: Vector2Like, s: Double): Vector2 = js.native
  @JSName("addVectors")
  protected def addVectors$raw(a: Vector2Like, b: Vector2Like): Vector2 = js.native
  def angle(): Double = js.native
  def angleTo(v: Vector2): Double = js.native
  @JSName("applyMatrix3")
  protected def applyMatrix3$raw(m: Matrix3): Vector2 = js.native
  @JSName("ceil")
  protected def ceil$raw(): Vector2 = js.native
  @JSName("clamp")
  protected def clamp$raw(min: Vector2Like, max: Vector2Like): Vector2 = js.native
  @JSName("clampLength")
  protected def clampLength$raw(min: Double, max: Double): Vector2 = js.native
  @JSName("clampScalar")
  protected def clampScalar$raw(min: Double, max: Double): Vector2 = js.native
  @JSName("clone")
  protected def clone$raw(): Vector2 = js.native
  @JSName("copy")
  protected def copy$raw(v: Vector2Like): Vector2 = js.native
  def cross(v: Vector2Like): Double = js.native
  def distanceTo(v: Vector2Like): Double = js.native
  def distanceToSquared(v: Vector2Like): Double = js.native
  @JSName("divide")
  protected def divide$raw(v: Vector2Like): Vector2 = js.native
  @JSName("divideScalar")
  protected def divideScalar$raw(s: Double): Vector2 = js.native
  def dot(v: Vector2Like): Double = js.native
  // #WARN: Vector2.equals collides with the pinned platform member 'java.lang.Object#equals' only at the Scala source name; the JS wire key 'equals' is preserved with @JSName and the member is emitted under a derived collision-free name (the inherited member stays callable under Scala semantics)
  @JSName("equals")
  def equals_1(v: Vector2Like): Boolean = js.native
  @JSName("floor")
  protected def floor$raw(): Vector2 = js.native
  @JSName("fromArray")
  protected def fromArray$raw(array: facadecontract.std.JSArrayLike[Double] | js.Array[Double], offset: js.UndefOr[Double] = js.undefined): Vector2 = js.native
  @JSName("fromBufferAttribute")
  protected def fromBufferAttribute$raw(attribute: BufferAttribute[BufferAttributeEventMap], index: Double): Vector2 = js.native
  def getComponent(index: Double): Double = js.native
  @JSName(js.Symbol.iterator)
  def jsIterator(): js.Iterator[Double] = js.native
  def length(): Double = js.native
  def lengthSq(): Double = js.native
  @JSName("lerp")
  protected def lerp$raw(v: Vector2Like, alpha: Double): Vector2 = js.native
  @JSName("lerpVectors")
  protected def lerpVectors$raw(v1: Vector2Like, v2: Vector2Like, alpha: Double): Vector2 = js.native
  def manhattanDistanceTo(v: Vector2Like): Double = js.native
  def manhattanLength(): Double = js.native
  @JSName("max")
  protected def max$raw(v: Vector2Like): Vector2 = js.native
  @JSName("min")
  protected def min$raw(v: Vector2Like): Vector2 = js.native
  @JSName("multiply")
  protected def multiply$raw(v: Vector2Like): Vector2 = js.native
  @JSName("multiplyScalar")
  protected def multiplyScalar$raw(scalar: Double): Vector2 = js.native
  @JSName("negate")
  protected def negate$raw(): Vector2 = js.native
  @JSName("normalize")
  protected def normalize$raw(): Vector2 = js.native
  @JSName("random")
  protected def random$raw(): Vector2 = js.native
  @JSName("rotateAround")
  protected def rotateAround$raw(center: Vector2Like, angle: Double): Vector2 = js.native
  @JSName("round")
  protected def round$raw(): Vector2 = js.native
  @JSName("roundToZero")
  protected def roundToZero$raw(): Vector2 = js.native
  @JSName("set")
  protected def set$raw(x: Double, y: Double): Vector2 = js.native
  @JSName("setComponent")
  protected def setComponent$raw(index: Double, value: Double): Vector2 = js.native
  @JSName("setLength")
  protected def setLength$raw(length: Double): Vector2 = js.native
  @JSName("setScalar")
  protected def setScalar$raw(scalar: Double): Vector2 = js.native
  @JSName("setX")
  protected def setX$raw(x: Double): Vector2 = js.native
  @JSName("setY")
  protected def setY$raw(y: Double): Vector2 = js.native
  @JSName("sub")
  protected def sub$raw(v: Vector2Like): Vector2 = js.native
  @JSName("subScalar")
  protected def subScalar$raw(s: Double): Vector2 = js.native
  @JSName("subVectors")
  protected def subVectors$raw(a: Vector2Like, b: Vector2Like): Vector2 = js.native
  def toArray(array: js.UndefOr[js.Array[Double]] = js.undefined, offset: js.UndefOr[Double] = js.undefined): js.Array[Double] = js.native
  // #WARN: Disambiguated erasure-colliding overload 'toArray' with @targetName("toArray_1") and @JSName("toArray")
  @scala.annotation.targetName("toArray_1")
  @JSName("toArray")
  def toArray(array: js.UndefOr[js.Tuple2[Double, Double]], offset: js.UndefOr[0]): js.Tuple2[Double, Double] = js.native
  // #WARN: Disambiguated erasure-colliding overload 'toArray' with @targetName("toArray_2") and @JSName("toArray")
  @scala.annotation.targetName("toArray_2")
  @JSName("toArray")
  def toArray(array: facadecontract.std.JSArrayLike[Double], offset: js.UndefOr[Double]): facadecontract.std.JSArrayLike[Double] = js.native

object Vector2:
  extension [Self <: Vector2](self: Self)
    inline def set(x: Double, y: Double): Self = self.set$raw(x, y).asInstanceOf[Self]
    inline def setScalar(scalar: Double): Self = self.setScalar$raw(scalar).asInstanceOf[Self]
    inline def setX(x: Double): Self = self.setX$raw(x).asInstanceOf[Self]
    inline def setY(y: Double): Self = self.setY$raw(y).asInstanceOf[Self]
    inline def setComponent(index: Double, value: Double): Self = self.setComponent$raw(index, value).asInstanceOf[Self]
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]
    inline def copy(v: Vector2Like): Self = self.copy$raw(v).asInstanceOf[Self]
    inline def add(v: Vector2Like): Self = self.add$raw(v).asInstanceOf[Self]
    inline def addScalar(s: Double): Self = self.addScalar$raw(s).asInstanceOf[Self]
    inline def addVectors(a: Vector2Like, b: Vector2Like): Self = self.addVectors$raw(a, b).asInstanceOf[Self]
    inline def addScaledVector(v: Vector2Like, s: Double): Self = self.addScaledVector$raw(v, s).asInstanceOf[Self]
    inline def sub(v: Vector2Like): Self = self.sub$raw(v).asInstanceOf[Self]
    inline def subScalar(s: Double): Self = self.subScalar$raw(s).asInstanceOf[Self]
    inline def subVectors(a: Vector2Like, b: Vector2Like): Self = self.subVectors$raw(a, b).asInstanceOf[Self]
    inline def multiply(v: Vector2Like): Self = self.multiply$raw(v).asInstanceOf[Self]
    inline def multiplyScalar(scalar: Double): Self = self.multiplyScalar$raw(scalar).asInstanceOf[Self]
    inline def divide(v: Vector2Like): Self = self.divide$raw(v).asInstanceOf[Self]
    inline def divideScalar(s: Double): Self = self.divideScalar$raw(s).asInstanceOf[Self]
    inline def applyMatrix3(m: Matrix3): Self = self.applyMatrix3$raw(m).asInstanceOf[Self]
    inline def min(v: Vector2Like): Self = self.min$raw(v).asInstanceOf[Self]
    inline def max(v: Vector2Like): Self = self.max$raw(v).asInstanceOf[Self]
    inline def clamp(min: Vector2Like, max: Vector2Like): Self = self.clamp$raw(min, max).asInstanceOf[Self]
    inline def clampScalar(min: Double, max: Double): Self = self.clampScalar$raw(min, max).asInstanceOf[Self]
    inline def clampLength(min: Double, max: Double): Self = self.clampLength$raw(min, max).asInstanceOf[Self]
    inline def floor(): Self = self.floor$raw().asInstanceOf[Self]
    inline def ceil(): Self = self.ceil$raw().asInstanceOf[Self]
    inline def round(): Self = self.round$raw().asInstanceOf[Self]
    inline def roundToZero(): Self = self.roundToZero$raw().asInstanceOf[Self]
    inline def negate(): Self = self.negate$raw().asInstanceOf[Self]
    inline def normalize(): Self = self.normalize$raw().asInstanceOf[Self]
    inline def setLength(length: Double): Self = self.setLength$raw(length).asInstanceOf[Self]
    inline def lerp(v: Vector2Like, alpha: Double): Self = self.lerp$raw(v, alpha).asInstanceOf[Self]
    inline def lerpVectors(v1: Vector2Like, v2: Vector2Like, alpha: Double): Self = self.lerpVectors$raw(v1, v2, alpha).asInstanceOf[Self]
    inline def fromArray(array: facadecontract.std.JSArrayLike[Double] | js.Array[Double], offset: js.UndefOr[Double] = js.undefined): Self = self.fromArray$raw(array, offset).asInstanceOf[Self]
    inline def fromBufferAttribute(attribute: BufferAttribute[BufferAttributeEventMap], index: Double): Self = self.fromBufferAttribute$raw(attribute, index).asInstanceOf[Self]
    inline def rotateAround(center: Vector2Like, angle: Double): Self = self.rotateAround$raw(center, angle).asInstanceOf[Self]
    inline def random(): Self = self.random$raw().asInstanceOf[Self]
