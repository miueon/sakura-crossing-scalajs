// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Plane")
class Plane(normal_1: js.UndefOr[Vector3] = js.undefined, constant_1: js.UndefOr[Double] = js.undefined) extends js.Object:
  var constant: Double = js.native
  def isPlane: true = js.native
  var normal: Vector3 = js.native
  def applyMatrix4(matrix: Matrix4, optionalNormalMatrix: js.UndefOr[Matrix3] = js.undefined): Plane = js.native
  @JSName("clone")
  protected def clone$raw(): Plane = js.native
  def coplanarPoint(target: Vector3): Vector3 = js.native
  @JSName("copy")
  protected def copy$raw(plane: Plane): Plane = js.native
  def distanceToPoint(point: Vector3): Double = js.native
  def distanceToSphere(sphere: Sphere): Double = js.native
  // #WARN: Plane.equals collides with the pinned platform member 'java.lang.Object#equals' only at the Scala source name; the JS wire key 'equals' is preserved with @JSName and the member is emitted under a derived collision-free name (the inherited member stays callable under Scala semantics)
  @JSName("equals")
  def equals_1(plane: Plane): Boolean = js.native
  def intersectLine(line: Line3, target: Vector3, clampToLine: js.UndefOr[Boolean] = js.undefined): Vector3 | Null = js.native
  def intersectsBox(box: Box3): Boolean = js.native
  def intersectsLine(line: Line3): Boolean = js.native
  def intersectsSphere(sphere: Sphere): Boolean = js.native
  def isIntersectionLine(l: js.Any): js.Any = js.native
  def negate(): Plane = js.native
  def normalize(): Plane = js.native
  def projectPoint(point: Vector3, target: Vector3): Vector3 = js.native
  def set(normal: Vector3, constant: Double): Plane = js.native
  def setComponents(x: Double, y: Double, z: Double, w: Double): Plane = js.native
  def setFromCoplanarPoints(a: Vector3, b: Vector3, c: Vector3): Plane = js.native
  def setFromNormalAndCoplanarPoint(normal: Vector3, point: Vector3): Plane = js.native
  def translate(offset: Vector3): Plane = js.native

object Plane:
  extension [Self <: Plane](self: Self)
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]
    inline def copy(plane: Plane): Self = self.copy$raw(plane).asInstanceOf[Self]
