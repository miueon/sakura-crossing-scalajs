// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

// #WARN: Disambiguated erasure-colliding overload 'toArray' with @targetName("toArray_1") and @JSName("toArray")
@js.native
@JSImport("three", "SphericalHarmonics3")
class SphericalHarmonics3() extends js.Object:
  var coefficients: js.Array[Vector3] = js.native
  def isSphericalHarmonics3: true = js.native
  def add(sh: SphericalHarmonics3): SphericalHarmonics3 = js.native
  def addScaledSH(sh: SphericalHarmonics3, s: Double): SphericalHarmonics3 = js.native
  @JSName("clone")
  protected def clone$raw(): SphericalHarmonics3 = js.native
  def copy(sh: SphericalHarmonics3): SphericalHarmonics3 = js.native
  // #WARN: SphericalHarmonics3.equals collides with the pinned platform member 'java.lang.Object#equals' only at the Scala source name; the JS wire key 'equals' is preserved with @JSName and the member is emitted under a derived collision-free name (the inherited member stays callable under Scala semantics)
  @JSName("equals")
  def equals_1(sh: SphericalHarmonics3): Boolean = js.native
  @JSName("fromArray")
  protected def fromArray$raw(array: facadecontract.std.JSArrayLike[Double] | js.Array[Double], offset: js.UndefOr[Double] = js.undefined): SphericalHarmonics3 = js.native
  def getAt(normal: Vector3, target: Vector3): Vector3 = js.native
  def getIrradianceAt(normal: Vector3, target: Vector3): Vector3 = js.native
  def lerp(sh: SphericalHarmonics3, alpha: Double): SphericalHarmonics3 = js.native
  def scale(s: Double): SphericalHarmonics3 = js.native
  def set(coefficients: js.Array[Vector3]): SphericalHarmonics3 = js.native
  def toArray(array: js.UndefOr[js.Array[Double]] = js.undefined, offset: js.UndefOr[Double] = js.undefined): js.Array[Double] = js.native
  // #WARN: Disambiguated erasure-colliding overload 'toArray' with @targetName("toArray_1") and @JSName("toArray")
  @scala.annotation.targetName("toArray_1")
  @JSName("toArray")
  def toArray(array: facadecontract.std.JSArrayLike[Double], offset: js.UndefOr[Double]): facadecontract.std.JSArrayLike[Double] = js.native
  def zero(): SphericalHarmonics3 = js.native


object SphericalHarmonics3:
  extension [Self <: SphericalHarmonics3](self: Self)
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]
    inline def fromArray(array: facadecontract.std.JSArrayLike[Double] | js.Array[Double], offset: js.UndefOr[Double] = js.undefined): Self = self.fromArray$raw(array, offset).asInstanceOf[Self]

  @js.native
  @JSImport("three", "SphericalHarmonics3.getBasisAt")
  def getBasisAt(normal: Vector3, shBasis: js.Array[Double]): Unit = js.native
