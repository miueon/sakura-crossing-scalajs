// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Spherical")
class Spherical(radius_1: js.UndefOr[Double] = js.undefined, phi_1: js.UndefOr[Double] = js.undefined, theta_1: js.UndefOr[Double] = js.undefined) extends js.Object:
  var phi: Double = js.native
  var radius: Double = js.native
  var theta: Double = js.native
  @JSName("clone")
  protected def clone$raw(): Spherical = js.native
  @JSName("copy")
  protected def copy$raw(other: Spherical): Spherical = js.native
  @JSName("makeSafe")
  protected def makeSafe$raw(): Spherical = js.native
  @JSName("set")
  protected def set$raw(radius: Double, phi: Double, theta: Double): Spherical = js.native
  @JSName("setFromCartesianCoords")
  protected def setFromCartesianCoords$raw(x: Double, y: Double, z: Double): Spherical = js.native
  @JSName("setFromVector3")
  protected def setFromVector3$raw(v: Vector3): Spherical = js.native

object Spherical:
  extension [Self <: Spherical](self: Self)
    inline def set(radius: Double, phi: Double, theta: Double): Self = self.set$raw(radius, phi, theta).asInstanceOf[Self]
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]
    inline def copy(other: Spherical): Self = self.copy$raw(other).asInstanceOf[Self]
    inline def makeSafe(): Self = self.makeSafe$raw().asInstanceOf[Self]
    inline def setFromVector3(v: Vector3): Self = self.setFromVector3$raw(v).asInstanceOf[Self]
    inline def setFromCartesianCoords(x: Double, y: Double, z: Double): Self = self.setFromCartesianCoords$raw(x, y, z).asInstanceOf[Self]
