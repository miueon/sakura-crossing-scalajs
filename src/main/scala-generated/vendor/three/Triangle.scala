// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

// #WARN: Disambiguated erasure-colliding overload 'getInterpolation' with @targetName("getInterpolation_1") and @JSName("getInterpolation")
// #WARN: Disambiguated erasure-colliding overload 'getInterpolation' with @targetName("getInterpolation_2") and @JSName("getInterpolation")
@js.native
@JSImport("three", "Triangle")
class Triangle(a_1: js.UndefOr[Vector3] = js.undefined, b_1: js.UndefOr[Vector3] = js.undefined, c_1: js.UndefOr[Vector3] = js.undefined) extends js.Object:
  var a: Vector3 = js.native
  var b: Vector3 = js.native
  var c: Vector3 = js.native
  @JSName("clone")
  protected def clone$raw(): Triangle = js.native
  def closestPointToPoint(point: Vector3, target: Vector3): Vector3 = js.native
  def containsPoint(point: Vector3): Boolean = js.native
  @JSName("copy")
  protected def copy$raw(triangle: Triangle): Triangle = js.native
  // #WARN: Triangle.equals collides with the pinned platform member 'java.lang.Object#equals' only at the Scala source name; the JS wire key 'equals' is preserved with @JSName and the member is emitted under a derived collision-free name (the inherited member stays callable under Scala semantics)
  @JSName("equals")
  def equals_1(triangle: Triangle): Boolean = js.native
  def getArea(): Double = js.native
  def getBarycoord(point: Vector3, target: Vector3): Vector3 | Null = js.native
  def getInterpolation(point: Vector3, v1: Vector2, v2: Vector2, v3: Vector2, target: Vector2): Vector2 | Null = js.native
  // #WARN: Disambiguated erasure-colliding overload 'getInterpolation' with @targetName("getInterpolation_1") and @JSName("getInterpolation")
  @scala.annotation.targetName("getInterpolation_1")
  @JSName("getInterpolation")
  def getInterpolation(point: Vector3, v1: Vector3, v2: Vector3, v3: Vector3, target: Vector3): Vector3 | Null = js.native
  // #WARN: Disambiguated erasure-colliding overload 'getInterpolation' with @targetName("getInterpolation_2") and @JSName("getInterpolation")
  @scala.annotation.targetName("getInterpolation_2")
  @JSName("getInterpolation")
  def getInterpolation(point: Vector3, v1: Vector4, v2: Vector4, v3: Vector4, target: Vector4): Vector4 | Null = js.native
  def getMidpoint(target: Vector3): Vector3 = js.native
  def getNormal(target: Vector3): Vector3 = js.native
  def getPlane(target: Plane): Plane = js.native
  def intersectsBox(box: Box3): Boolean = js.native
  def isFrontFacing(direction: Vector3): Boolean = js.native
  def set(a: Vector3, b: Vector3, c: Vector3): Triangle = js.native
  @JSName("setFromAttributeAndIndices")
  protected def setFromAttributeAndIndices$raw(attribute: BufferAttribute[BufferAttributeEventMap] | InterleavedBufferAttribute, i0: Double, i1: Double, i2: Double): Triangle = js.native
  @JSName("setFromPointsAndIndices")
  protected def setFromPointsAndIndices$raw(points: js.Array[Vector3], i0: Double, i1: Double, i2: Double): Triangle = js.native

// #WARN: Disambiguated erasure-colliding overload 'getInterpolation' with @targetName("getInterpolation_1") and @JSName("getInterpolation")
// #WARN: Disambiguated erasure-colliding overload 'getInterpolation' with @targetName("getInterpolation_2") and @JSName("getInterpolation")
// #WARN: Disambiguated erasure-colliding overload 'getInterpolatedAttribute' with @targetName("getInterpolatedAttribute_1") and @JSName("getInterpolatedAttribute")
// #WARN: Disambiguated erasure-colliding overload 'getInterpolatedAttribute' with @targetName("getInterpolatedAttribute_2") and @JSName("getInterpolatedAttribute")

object Triangle:
  extension [Self <: Triangle](self: Self)
    inline def setFromPointsAndIndices(points: js.Array[Vector3], i0: Double, i1: Double, i2: Double): Self = self.setFromPointsAndIndices$raw(points, i0, i1, i2).asInstanceOf[Self]
    inline def setFromAttributeAndIndices(attribute: BufferAttribute[BufferAttributeEventMap] | InterleavedBufferAttribute, i0: Double, i1: Double, i2: Double): Self = self.setFromAttributeAndIndices$raw(attribute, i0, i1, i2).asInstanceOf[Self]
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]
    inline def copy(triangle: Triangle): Self = self.copy$raw(triangle).asInstanceOf[Self]

  @js.native
  @JSImport("three", "Triangle.containsPoint")
  def containsPoint(point: Vector3, a: Vector3, b: Vector3, c: Vector3): Boolean = js.native
  @js.native
  @JSImport("three", "Triangle.getBarycoord")
  def getBarycoord(point: Vector3, a: Vector3, b: Vector3, c: Vector3, target: Vector3): Vector3 | Null = js.native
  @js.native
  @JSImport("three", "Triangle.getInterpolatedAttribute")
  def getInterpolatedAttribute(attr: BufferAttribute[BufferAttributeEventMap] | InterleavedBufferAttribute, i1: Double, i2: Double, i3: Double, barycoord: Vector3, target: Vector2): Vector2 = js.native
  @js.native
  @JSImport("three", "Triangle.getInterpolatedAttribute")
  // #WARN: Disambiguated erasure-colliding overload 'getInterpolatedAttribute' with @targetName("getInterpolatedAttribute_1") and @JSName("getInterpolatedAttribute")
  @scala.annotation.targetName("getInterpolatedAttribute_1")
  def getInterpolatedAttribute(attr: BufferAttribute[BufferAttributeEventMap] | InterleavedBufferAttribute, i1: Double, i2: Double, i3: Double, barycoord: Vector3, target: Vector3): Vector3 = js.native
  @js.native
  @JSImport("three", "Triangle.getInterpolatedAttribute")
  // #WARN: Disambiguated erasure-colliding overload 'getInterpolatedAttribute' with @targetName("getInterpolatedAttribute_2") and @JSName("getInterpolatedAttribute")
  @scala.annotation.targetName("getInterpolatedAttribute_2")
  def getInterpolatedAttribute(attr: BufferAttribute[BufferAttributeEventMap] | InterleavedBufferAttribute, i1: Double, i2: Double, i3: Double, barycoord: Vector3, target: Vector4): Vector4 = js.native
  @js.native
  @JSImport("three", "Triangle.getInterpolation")
  def getInterpolation(point: Vector3, p1: Vector3, p2: Vector3, p3: Vector3, v1: Vector2, v2: Vector2, v3: Vector2, target: Vector2): Vector2 | Null = js.native
  @js.native
  @JSImport("three", "Triangle.getInterpolation")
  // #WARN: Disambiguated erasure-colliding overload 'getInterpolation' with @targetName("getInterpolation_1") and @JSName("getInterpolation")
  @scala.annotation.targetName("getInterpolation_1")
  def getInterpolation(point: Vector3, p1: Vector3, p2: Vector3, p3: Vector3, v1: Vector3, v2: Vector3, v3: Vector3, target: Vector3): Vector3 | Null = js.native
  @js.native
  @JSImport("three", "Triangle.getInterpolation")
  // #WARN: Disambiguated erasure-colliding overload 'getInterpolation' with @targetName("getInterpolation_2") and @JSName("getInterpolation")
  @scala.annotation.targetName("getInterpolation_2")
  def getInterpolation(point: Vector3, p1: Vector3, p2: Vector3, p3: Vector3, v1: Vector4, v2: Vector4, v3: Vector4, target: Vector4): Vector4 | Null = js.native
  @js.native
  @JSImport("three", "Triangle.getNormal")
  def getNormal(a: Vector3, b: Vector3, c: Vector3, target: Vector3): Vector3 = js.native
  @js.native
  @JSImport("three", "Triangle.isFrontFacing")
  def isFrontFacing(a: Vector3, b: Vector3, c: Vector3, direction: Vector3): Boolean = js.native
