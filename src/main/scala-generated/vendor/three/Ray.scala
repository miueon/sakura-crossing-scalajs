// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Ray")
class Ray(origin_1: js.UndefOr[Vector3] = js.undefined, direction_1: js.UndefOr[Vector3] = js.undefined) extends js.Object:
  var direction: Vector3 = js.native
  var origin: Vector3 = js.native
  def applyMatrix4(matrix4: Matrix4): Ray = js.native
  def at(t: Double, target: Vector3): Vector3 = js.native
  @JSName("clone")
  protected def clone$raw(): Ray = js.native
  def closestPointToPoint(point: Vector3, target: Vector3): Vector3 = js.native
  @JSName("copy")
  protected def copy$raw(ray: Ray): Ray = js.native
  def distanceSqToPoint(point: Vector3): Double = js.native
  def distanceSqToSegment(v0: Vector3, v1: Vector3, optionalPointOnRay: js.UndefOr[Vector3] = js.undefined, optionalPointOnSegment: js.UndefOr[Vector3] = js.undefined): Double = js.native
  def distanceToPlane(plane: Plane): Double = js.native
  def distanceToPoint(point: Vector3): Double = js.native
  // #WARN: Ray.equals collides with the pinned platform member 'java.lang.Object#equals' only at the Scala source name; the JS wire key 'equals' is preserved with @JSName and the member is emitted under a derived collision-free name (the inherited member stays callable under Scala semantics)
  @JSName("equals")
  def equals_1(ray: Ray): Boolean = js.native
  def intersectBox(box: Box3, target: Vector3): Vector3 | Null = js.native
  def intersectPlane(plane: Plane, target: Vector3): Vector3 | Null = js.native
  def intersectsBox(box: Box3): Boolean = js.native
  def intersectSphere(sphere: Sphere, target: Vector3): Vector3 | Null = js.native
  def intersectsPlane(plane: Plane): Boolean = js.native
  def intersectsSphere(sphere: Sphere): Boolean = js.native
  def intersectTriangle(a: Vector3, b: Vector3, c: Vector3, backfaceCulling: Boolean, target: Vector3): Vector3 | Null = js.native
  def isIntersectionBox(b: js.Any): js.Any = js.native
  def isIntersectionPlane(p: js.Any): js.Any = js.native
  def isIntersectionSphere(s: js.Any): js.Any = js.native
  def lookAt(v: Vector3): Ray = js.native
  def recast(t: Double): Ray = js.native
  def set(origin: Vector3, direction: Vector3): Ray = js.native

object Ray:
  extension [Self <: Ray](self: Self)
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]
    inline def copy(ray: Ray): Self = self.copy$raw(ray).asInstanceOf[Self]
