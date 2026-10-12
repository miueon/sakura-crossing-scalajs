// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Sphere")
class Sphere(center_1: js.UndefOr[Vector3] = js.undefined, radius_1: js.UndefOr[Double] = js.undefined) extends js.Object:
  var center: Vector3 = js.native
  def isSphere: true = js.native
  var radius: Double = js.native
  def applyMatrix4(matrix: Matrix4): Sphere = js.native
  def clampPoint(point: Vector3, target: Vector3): Vector3 = js.native
  @JSName("clone")
  protected def clone$raw(): Sphere = js.native
  def containsPoint(point: Vector3): Boolean = js.native
  @JSName("copy")
  protected def copy$raw(sphere: Sphere): Sphere = js.native
  def distanceToPoint(point: Vector3): Double = js.native
  def empty(): js.Any = js.native
  // #WARN: Sphere.equals collides with the pinned platform member 'java.lang.Object#equals' only at the Scala source name; the JS wire key 'equals' is preserved with @JSName and the member is emitted under a derived collision-free name (the inherited member stays callable under Scala semantics)
  @JSName("equals")
  def equals_1(sphere: Sphere): Boolean = js.native
  @JSName("expandByPoint")
  protected def expandByPoint$raw(point: Vector3): Sphere = js.native
  @JSName("fromJSON")
  protected def fromJSON$raw(json: SphereJSON): Sphere = js.native
  def getBoundingBox(target: Box3): Box3 = js.native
  def intersectsBox(box: Box3): Boolean = js.native
  def intersectsPlane(plane: Plane): Boolean = js.native
  def intersectsSphere(sphere: Sphere): Boolean = js.native
  def isEmpty(): Boolean = js.native
  @JSName("makeEmpty")
  protected def makeEmpty$raw(): Sphere = js.native
  def set(center: Vector3, radius: Double): Sphere = js.native
  def setFromPoints(points: js.Array[Vector3], optionalCenter: js.UndefOr[Vector3] = js.undefined): Sphere = js.native
  def toJSON(): SphereJSON = js.native
  def translate(offset: Vector3): Sphere = js.native
  @JSName("union")
  protected def union$raw(sphere: Sphere): Sphere = js.native

object Sphere:
  extension [Self <: Sphere](self: Self)
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]
    inline def copy(sphere: Sphere): Self = self.copy$raw(sphere).asInstanceOf[Self]
    inline def expandByPoint(point: Vector3): Self = self.expandByPoint$raw(point).asInstanceOf[Self]
    inline def makeEmpty(): Self = self.makeEmpty$raw().asInstanceOf[Self]
    inline def union(sphere: Sphere): Self = self.union$raw(sphere).asInstanceOf[Self]
    inline def fromJSON(json: SphereJSON): Self = self.fromJSON$raw(json).asInstanceOf[Self]
