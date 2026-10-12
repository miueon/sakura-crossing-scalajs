// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Box2")
class Box2(min_1: js.UndefOr[Vector2] = js.undefined, max_1: js.UndefOr[Vector2] = js.undefined) extends js.Object:
  var max: Vector2 = js.native
  var min: Vector2 = js.native
  def clampPoint(point: Vector2, target: Vector2): Vector2 = js.native
  @JSName("clone")
  protected def clone$raw(): Box2 = js.native
  def containsBox(box: Box2): Boolean = js.native
  def containsPoint(point: Vector2): Boolean = js.native
  @JSName("copy")
  protected def copy$raw(box: Box2): Box2 = js.native
  def distanceToPoint(point: Vector2): Double = js.native
  def empty(): js.Any = js.native
  // #WARN: Box2.equals collides with the pinned platform member 'java.lang.Object#equals' only at the Scala source name; the JS wire key 'equals' is preserved with @JSName and the member is emitted under a derived collision-free name (the inherited member stays callable under Scala semantics)
  @JSName("equals")
  def equals_1(box: Box2): Boolean = js.native
  def expandByPoint(point: Vector2Like): Box2 = js.native
  def expandByScalar(scalar: Double): Box2 = js.native
  def expandByVector(vector: Vector2): Box2 = js.native
  def getCenter(target: Vector2): Vector2 = js.native
  def getParameter(point: Vector2, target: Vector2): Vector2 = js.native
  def getSize(target: Vector2): Vector2 = js.native
  def intersect(box: Box2): Box2 = js.native
  def intersectsBox(box: Box2): Boolean = js.native
  def isEmpty(): Boolean = js.native
  def isIntersectionBox(b: js.Any): js.Any = js.native
  def makeEmpty(): Box2 = js.native
  def set(min: Vector2, max: Vector2): Box2 = js.native
  def setFromCenterAndSize(center: Vector2, size: Vector2): Box2 = js.native
  def setFromPoints(points: js.Array[Vector2Like]): Box2 = js.native
  def translate(offset: Vector2): Box2 = js.native
  def union(box: Box2): Box2 = js.native

object Box2:
  extension [Self <: Box2](self: Self)
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]
    inline def copy(box: Box2): Self = self.copy$raw(box).asInstanceOf[Self]
