// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Matrix4")
class Matrix4() extends js.Object:
  def this(n11: Double, n12: Double, n13: Double, n14: Double, n21: Double, n22: Double, n23: Double, n24: Double, n31: Double, n32: Double, n33: Double, n34: Double, n41: Double, n42: Double, n43: Double, n44: Double) = this()
  var elements: js.Tuple16[Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double] = js.native
  override def clone(): Matrix4 = js.native
  @JSName("compose")
  protected def compose$raw(position: Vector3, quaternion: Quaternion, scale: Vector3): Matrix4 = js.native
  @JSName("copy")
  protected def copy$raw(m: Matrix4): Matrix4 = js.native
  @JSName("copyPosition")
  protected def copyPosition$raw(m: Matrix4): Matrix4 = js.native
  @JSName("decompose")
  protected def decompose$raw(position: Vector3, quaternion: Quaternion, scale: Vector3): Matrix4 = js.native
  def determinant(): Double = js.native
  def determinantAffine(): Double = js.native
  // #WARN: Matrix4.equals collides with the pinned platform member 'java.lang.Object#equals' only at the Scala source name; the JS wire key 'equals' is preserved with @JSName and the member is emitted under a derived collision-free name (the inherited member stays callable under Scala semantics)
  @JSName("equals")
  def equals_1(matrix: Matrix4): Boolean = js.native
  @JSName("extractBasis")
  protected def extractBasis$raw(xAxis: Vector3, yAxis: Vector3, zAxis: Vector3): Matrix4 = js.native
  @JSName("extractRotation")
  protected def extractRotation$raw(m: Matrix4): Matrix4 = js.native
  @JSName("fromArray")
  protected def fromArray$raw(array: facadecontract.std.JSArrayLike[Double], offset: js.UndefOr[Double] = js.undefined): Matrix4 = js.native
  def getMaxScaleOnAxis(): Double = js.native
  @JSName("identity")
  protected def identity$raw(): Matrix4 = js.native
  @JSName("invert")
  protected def invert$raw(): Matrix4 = js.native
  @JSName("lookAt")
  protected def lookAt$raw(eye: Vector3, target: Vector3, up: Vector3): Matrix4 = js.native
  @JSName("makeBasis")
  protected def makeBasis$raw(xAxis: Vector3, yAxis: Vector3, zAxis: Vector3): Matrix4 = js.native
  @JSName("makeOrthographic")
  protected def makeOrthographic$raw(left: Double, right: Double, top: Double, bottom: Double, near: Double, far: Double, coordinateSystem: js.UndefOr[2000 | 2001] = js.undefined, reversedDepth: js.UndefOr[Boolean] = js.undefined): Matrix4 = js.native
  @JSName("makePerspective")
  protected def makePerspective$raw(left: Double, right: Double, top: Double, bottom: Double, near: Double, far: Double, coordinateSystem: js.UndefOr[2000 | 2001] = js.undefined, reversedDepth: js.UndefOr[Boolean] = js.undefined): Matrix4 = js.native
  @JSName("makeRotationAxis")
  protected def makeRotationAxis$raw(axis: Vector3, angle: Double): Matrix4 = js.native
  @JSName("makeRotationFromEuler")
  protected def makeRotationFromEuler$raw(euler: Euler): Matrix4 = js.native
  @JSName("makeRotationFromQuaternion")
  protected def makeRotationFromQuaternion$raw(q: Quaternion): Matrix4 = js.native
  @JSName("makeRotationX")
  protected def makeRotationX$raw(theta: Double): Matrix4 = js.native
  @JSName("makeRotationY")
  protected def makeRotationY$raw(theta: Double): Matrix4 = js.native
  @JSName("makeRotationZ")
  protected def makeRotationZ$raw(theta: Double): Matrix4 = js.native
  @JSName("makeScale")
  protected def makeScale$raw(x: Double, y: Double, z: Double): Matrix4 = js.native
  @JSName("makeShear")
  protected def makeShear$raw(xy: Double, xz: Double, yx: Double, yz: Double, zx: Double, zy: Double): Matrix4 = js.native
  @JSName("makeTranslation")
  protected def makeTranslation$raw(v: Vector3): Matrix4 = js.native
  @JSName("makeTranslation")
  protected def makeTranslation$raw(x: Double, y: Double, z: Double): Matrix4 = js.native
  @JSName("multiply")
  protected def multiply$raw(m: Matrix4): Matrix4 = js.native
  @JSName("multiplyMatrices")
  protected def multiplyMatrices$raw(a: Matrix4, b: Matrix4): Matrix4 = js.native
  @JSName("multiplyScalar")
  protected def multiplyScalar$raw(s: Double): Matrix4 = js.native
  @JSName("premultiply")
  protected def premultiply$raw(m: Matrix4): Matrix4 = js.native
  @JSName("scale")
  protected def scale$raw(v: Vector3): Matrix4 = js.native
  @JSName("set")
  protected def set$raw(n11: Double, n12: Double, n13: Double, n14: Double, n21: Double, n22: Double, n23: Double, n24: Double, n31: Double, n32: Double, n33: Double, n34: Double, n41: Double, n42: Double, n43: Double, n44: Double): Matrix4 = js.native
  @JSName("setFromMatrix3")
  protected def setFromMatrix3$raw(m: Matrix3): Matrix4 = js.native
  @JSName("setPosition")
  protected def setPosition$raw(v: Vector3): Matrix4 = js.native
  @JSName("setPosition")
  protected def setPosition$raw(x: Double, y: Double, z: Double): Matrix4 = js.native
  def toArray[TArray <: facadecontract.std.JSArrayLike[Double]](array: js.UndefOr[TArray] = js.undefined, offset: js.UndefOr[Double] = js.undefined): TArray = js.native
  @JSName("transpose")
  protected def transpose$raw(): Matrix4 = js.native

object Matrix4:
  extension [Self <: Matrix4](self: Self)
    inline def set(n11: Double, n12: Double, n13: Double, n14: Double, n21: Double, n22: Double, n23: Double, n24: Double, n31: Double, n32: Double, n33: Double, n34: Double, n41: Double, n42: Double, n43: Double, n44: Double): Self = self.set$raw(n11, n12, n13, n14, n21, n22, n23, n24, n31, n32, n33, n34, n41, n42, n43, n44).asInstanceOf[Self]
    inline def identity(): Self = self.identity$raw().asInstanceOf[Self]
    inline def copy(m: Matrix4): Self = self.copy$raw(m).asInstanceOf[Self]
    inline def copyPosition(m: Matrix4): Self = self.copyPosition$raw(m).asInstanceOf[Self]
    inline def setFromMatrix3(m: Matrix3): Self = self.setFromMatrix3$raw(m).asInstanceOf[Self]
    inline def extractBasis(xAxis: Vector3, yAxis: Vector3, zAxis: Vector3): Self = self.extractBasis$raw(xAxis, yAxis, zAxis).asInstanceOf[Self]
    inline def makeBasis(xAxis: Vector3, yAxis: Vector3, zAxis: Vector3): Self = self.makeBasis$raw(xAxis, yAxis, zAxis).asInstanceOf[Self]
    inline def extractRotation(m: Matrix4): Self = self.extractRotation$raw(m).asInstanceOf[Self]
    inline def makeRotationFromEuler(euler: Euler): Self = self.makeRotationFromEuler$raw(euler).asInstanceOf[Self]
    inline def makeRotationFromQuaternion(q: Quaternion): Self = self.makeRotationFromQuaternion$raw(q).asInstanceOf[Self]
    inline def lookAt(eye: Vector3, target: Vector3, up: Vector3): Self = self.lookAt$raw(eye, target, up).asInstanceOf[Self]
    inline def multiply(m: Matrix4): Self = self.multiply$raw(m).asInstanceOf[Self]
    inline def premultiply(m: Matrix4): Self = self.premultiply$raw(m).asInstanceOf[Self]
    inline def multiplyMatrices(a: Matrix4, b: Matrix4): Self = self.multiplyMatrices$raw(a, b).asInstanceOf[Self]
    inline def multiplyScalar(s: Double): Self = self.multiplyScalar$raw(s).asInstanceOf[Self]
    inline def transpose(): Self = self.transpose$raw().asInstanceOf[Self]
    inline def setPosition(v: Vector3): Self = self.setPosition$raw(v).asInstanceOf[Self]
    inline def setPosition(x: Double, y: Double, z: Double): Self = self.setPosition$raw(x, y, z).asInstanceOf[Self]
    inline def invert(): Self = self.invert$raw().asInstanceOf[Self]
    inline def scale(v: Vector3): Self = self.scale$raw(v).asInstanceOf[Self]
    inline def makeTranslation(v: Vector3): Self = self.makeTranslation$raw(v).asInstanceOf[Self]
    inline def makeTranslation(x: Double, y: Double, z: Double): Self = self.makeTranslation$raw(x, y, z).asInstanceOf[Self]
    inline def makeRotationX(theta: Double): Self = self.makeRotationX$raw(theta).asInstanceOf[Self]
    inline def makeRotationY(theta: Double): Self = self.makeRotationY$raw(theta).asInstanceOf[Self]
    inline def makeRotationZ(theta: Double): Self = self.makeRotationZ$raw(theta).asInstanceOf[Self]
    inline def makeRotationAxis(axis: Vector3, angle: Double): Self = self.makeRotationAxis$raw(axis, angle).asInstanceOf[Self]
    inline def makeScale(x: Double, y: Double, z: Double): Self = self.makeScale$raw(x, y, z).asInstanceOf[Self]
    inline def makeShear(xy: Double, xz: Double, yx: Double, yz: Double, zx: Double, zy: Double): Self = self.makeShear$raw(xy, xz, yx, yz, zx, zy).asInstanceOf[Self]
    inline def compose(position: Vector3, quaternion: Quaternion, scale: Vector3): Self = self.compose$raw(position, quaternion, scale).asInstanceOf[Self]
    inline def decompose(position: Vector3, quaternion: Quaternion, scale: Vector3): Self = self.decompose$raw(position, quaternion, scale).asInstanceOf[Self]
    inline def makePerspective(left: Double, right: Double, top: Double, bottom: Double, near: Double, far: Double, coordinateSystem: js.UndefOr[2000 | 2001] = js.undefined, reversedDepth: js.UndefOr[Boolean] = js.undefined): Self = self.makePerspective$raw(left, right, top, bottom, near, far, coordinateSystem, reversedDepth).asInstanceOf[Self]
    inline def makeOrthographic(left: Double, right: Double, top: Double, bottom: Double, near: Double, far: Double, coordinateSystem: js.UndefOr[2000 | 2001] = js.undefined, reversedDepth: js.UndefOr[Boolean] = js.undefined): Self = self.makeOrthographic$raw(left, right, top, bottom, near, far, coordinateSystem, reversedDepth).asInstanceOf[Self]
    inline def fromArray(array: facadecontract.std.JSArrayLike[Double], offset: js.UndefOr[Double] = js.undefined): Self = self.fromArray$raw(array, offset).asInstanceOf[Self]
