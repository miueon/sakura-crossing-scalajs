// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Matrix2")
class Matrix2() extends js.Object:
  def this(n11: Double, n12: Double, n21: Double, n22: Double) = this()
  var elements: js.Tuple4[Double, Double, Double, Double] = js.native
  def isMatrix2: true = js.native
  @JSName("fromArray")
  protected def fromArray$raw(array: facadecontract.std.JSArrayLike[Double], offset: js.UndefOr[Double] = js.undefined): Matrix2 = js.native
  @JSName("identity")
  protected def identity$raw(): Matrix2 = js.native
  @JSName("set")
  protected def set$raw(n11: Double, n12: Double, n21: Double, n22: Double): Matrix2 = js.native

object Matrix2:
  extension [Self <: Matrix2](self: Self)
    inline def identity(): Self = self.identity$raw().asInstanceOf[Self]
    inline def fromArray(array: facadecontract.std.JSArrayLike[Double], offset: js.UndefOr[Double] = js.undefined): Self = self.fromArray$raw(array, offset).asInstanceOf[Self]
    inline def set(n11: Double, n12: Double, n21: Double, n22: Double): Self = self.set$raw(n11, n12, n21, n22).asInstanceOf[Self]
