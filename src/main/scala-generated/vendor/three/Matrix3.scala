// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Matrix3")
class Matrix3() extends js.Object:
  def this(n11: Double, n12: Double, n13: Double, n21: Double, n22: Double, n23: Double, n31: Double, n32: Double, n33: Double) = this()
  var elements: js.Tuple9[Double, Double, Double, Double, Double, Double, Double, Double, Double] = js.native
  def isMatrix3: true = js.native
  @JSName("clone")
  protected def clone$raw(): Matrix3 = js.native
  @JSName("copy")
  protected def copy$raw(m: Matrix3): Matrix3 = js.native
  def determinant(): Double = js.native
  // #WARN: Matrix3.equals collides with the pinned platform member 'java.lang.Object#equals' only at the Scala source name; the JS wire key 'equals' is preserved with @JSName and the member is emitted under a derived collision-free name (the inherited member stays callable under Scala semantics)
  @JSName("equals")
  def equals_1(matrix: Matrix3): Boolean = js.native
  @JSName("extractBasis")
  protected def extractBasis$raw(xAxis: Vector3, yAxis: Vector3, zAxis: Vector3): Matrix3 = js.native
  @JSName("fromArray")
  protected def fromArray$raw(array: facadecontract.std.JSArrayLike[Double], offset: js.UndefOr[Double] = js.undefined): Matrix3 = js.native
  @JSName("getNormalMatrix")
  protected def getNormalMatrix$raw(matrix4: Matrix4): Matrix3 = js.native
  @JSName("identity")
  protected def identity$raw(): Matrix3 = js.native
  @JSName("invert")
  protected def invert$raw(): Matrix3 = js.native
  @JSName("makeRotation")
  protected def makeRotation$raw(theta: Double): Matrix3 = js.native
  @JSName("makeScale")
  protected def makeScale$raw(x: Double, y: Double): Matrix3 = js.native
  @JSName("makeTranslation")
  protected def makeTranslation$raw(v: Vector2): Matrix3 = js.native
  @JSName("makeTranslation")
  protected def makeTranslation$raw(x: Double, y: Double): Matrix3 = js.native
  @JSName("multiply")
  protected def multiply$raw(m: Matrix3): Matrix3 = js.native
  @JSName("multiplyMatrices")
  protected def multiplyMatrices$raw(a: Matrix3, b: Matrix3): Matrix3 = js.native
  @JSName("multiplyScalar")
  protected def multiplyScalar$raw(s: Double): Matrix3 = js.native
  @JSName("premultiply")
  protected def premultiply$raw(m: Matrix3): Matrix3 = js.native
  @JSName("rotate")
  protected def rotate$raw(theta: Double): Matrix3 = js.native
  @JSName("scale")
  protected def scale$raw(sx: Double, sy: Double): Matrix3 = js.native
  def set(n11: Double, n12: Double, n13: Double, n21: Double, n22: Double, n23: Double, n31: Double, n32: Double, n33: Double): Matrix3 = js.native
  def setFromMatrix4(m: Matrix4): Matrix3 = js.native
  @JSName("setUvTransform")
  protected def setUvTransform$raw(tx: Double, ty: Double, sx: Double, sy: Double, rotation: Double, cx: Double, cy: Double): Matrix3 = js.native
  def toArray(): js.Tuple9[Double, Double, Double, Double, Double, Double, Double, Double, Double] = js.native
  def toArray[TArray <: facadecontract.std.JSArrayLike[Double]](array: TArray, offset: js.UndefOr[Double] = js.undefined): TArray = js.native
  @JSName("translate")
  protected def translate$raw(tx: Double, ty: Double): Matrix3 = js.native
  @JSName("transpose")
  protected def transpose$raw(): Matrix3 = js.native
  @JSName("transposeIntoArray")
  protected def transposeIntoArray$raw(r: js.Array[Double]): Matrix3 = js.native

object Matrix3:
  extension [Self <: Matrix3](self: Self)
    inline def identity(): Self = self.identity$raw().asInstanceOf[Self]
    inline def copy(m: Matrix3): Self = self.copy$raw(m).asInstanceOf[Self]
    inline def extractBasis(xAxis: Vector3, yAxis: Vector3, zAxis: Vector3): Self = self.extractBasis$raw(xAxis, yAxis, zAxis).asInstanceOf[Self]
    inline def multiply(m: Matrix3): Self = self.multiply$raw(m).asInstanceOf[Self]
    inline def premultiply(m: Matrix3): Self = self.premultiply$raw(m).asInstanceOf[Self]
    inline def multiplyMatrices(a: Matrix3, b: Matrix3): Self = self.multiplyMatrices$raw(a, b).asInstanceOf[Self]
    inline def multiplyScalar(s: Double): Self = self.multiplyScalar$raw(s).asInstanceOf[Self]
    inline def invert(): Self = self.invert$raw().asInstanceOf[Self]
    inline def transpose(): Self = self.transpose$raw().asInstanceOf[Self]
    inline def getNormalMatrix(matrix4: Matrix4): Self = self.getNormalMatrix$raw(matrix4).asInstanceOf[Self]
    inline def transposeIntoArray(r: js.Array[Double]): Self = self.transposeIntoArray$raw(r).asInstanceOf[Self]
    inline def setUvTransform(tx: Double, ty: Double, sx: Double, sy: Double, rotation: Double, cx: Double, cy: Double): Self = self.setUvTransform$raw(tx, ty, sx, sy, rotation, cx, cy).asInstanceOf[Self]
    inline def scale(sx: Double, sy: Double): Self = self.scale$raw(sx, sy).asInstanceOf[Self]
    inline def rotate(theta: Double): Self = self.rotate$raw(theta).asInstanceOf[Self]
    inline def translate(tx: Double, ty: Double): Self = self.translate$raw(tx, ty).asInstanceOf[Self]
    inline def makeTranslation(v: Vector2): Self = self.makeTranslation$raw(v).asInstanceOf[Self]
    inline def makeTranslation(x: Double, y: Double): Self = self.makeTranslation$raw(x, y).asInstanceOf[Self]
    inline def makeRotation(theta: Double): Self = self.makeRotation$raw(theta).asInstanceOf[Self]
    inline def makeScale(x: Double, y: Double): Self = self.makeScale$raw(x, y).asInstanceOf[Self]
    inline def fromArray(array: facadecontract.std.JSArrayLike[Double], offset: js.UndefOr[Double] = js.undefined): Self = self.fromArray$raw(array, offset).asInstanceOf[Self]
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]
