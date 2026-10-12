// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Cylindrical")
class Cylindrical(radius_1: js.UndefOr[Double] = js.undefined, theta_1: js.UndefOr[Double] = js.undefined, y_1: js.UndefOr[Double] = js.undefined) extends js.Object:
  var radius: Double = js.native
  var theta: Double = js.native
  var y: Double = js.native
  @JSName("clone")
  protected def clone$raw(): Cylindrical = js.native
  @JSName("copy")
  protected def copy$raw(other: Cylindrical): Cylindrical = js.native
  @JSName("set")
  protected def set$raw(radius: Double, theta: Double, y: Double): Cylindrical = js.native
  @JSName("setFromCartesianCoords")
  protected def setFromCartesianCoords$raw(x: Double, y: Double, z: Double): Cylindrical = js.native
  @JSName("setFromVector3")
  protected def setFromVector3$raw(vec3: Vector3): Cylindrical = js.native

object Cylindrical:
  extension [Self <: Cylindrical](self: Self)
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]
    inline def copy(other: Cylindrical): Self = self.copy$raw(other).asInstanceOf[Self]
    inline def set(radius: Double, theta: Double, y: Double): Self = self.set$raw(radius, theta, y).asInstanceOf[Self]
    inline def setFromVector3(vec3: Vector3): Self = self.setFromVector3$raw(vec3).asInstanceOf[Self]
    inline def setFromCartesianCoords(x: Double, y: Double, z: Double): Self = self.setFromCartesianCoords$raw(x, y, z).asInstanceOf[Self]
