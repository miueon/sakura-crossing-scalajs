// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Box3")
class Box3(min_1: js.UndefOr[Vector3] = js.undefined, max_1: js.UndefOr[Vector3] = js.undefined) extends js.Object:
  def isBox3: Boolean = js.native
  var max: Vector3 = js.native
  var min: Vector3 = js.native
  @JSName("applyMatrix4")
  protected def applyMatrix4$raw(matrix: Matrix4): Box3 = js.native
  def clampPoint(point: Vector3, target: Vector3): Vector3 = js.native
  @JSName("clone")
  protected def clone$raw(): Box3 = js.native
  def containsBox(box: Box3): Boolean = js.native
  def containsPoint(point: Vector3): Boolean = js.native
  @JSName("copy")
  protected def copy$raw(box: Box3): Box3 = js.native
  def distanceToPoint(point: Vector3): Double = js.native
  // #WARN: Box3.equals collides with the pinned platform member 'java.lang.Object#equals' only at the Scala source name; the JS wire key 'equals' is preserved with @JSName and the member is emitted under a derived collision-free name (the inherited member stays callable under Scala semantics)
  @JSName("equals")
  def equals_1(box: Box3): Boolean = js.native
  @JSName("expandByObject")
  protected def expandByObject$raw(`object`: Object3D[Object3DEventMap], precise: js.UndefOr[Boolean] = js.undefined): Box3 = js.native
  @JSName("expandByPoint")
  protected def expandByPoint$raw(point: Vector3Like): Box3 = js.native
  @JSName("expandByScalar")
  protected def expandByScalar$raw(scalar: Double): Box3 = js.native
  @JSName("expandByVector")
  protected def expandByVector$raw(vector: Vector3): Box3 = js.native
  @JSName("fromJSON")
  protected def fromJSON$raw(json: Box3JSON): Box3 = js.native
  def getBoundingSphere(target: Sphere): Sphere = js.native
  def getCenter(target: Vector3): Vector3 = js.native
  def getParameter(point: Vector3, target: Vector3): Vector3 = js.native
  def getSize(target: Vector3): Vector3 = js.native
  @JSName("intersect")
  protected def intersect$raw(box: Box3): Box3 = js.native
  def intersectsBox(box: Box3): Boolean = js.native
  def intersectsPlane(plane: Plane): Boolean = js.native
  def intersectsSphere(sphere: Sphere): Boolean = js.native
  def intersectsTriangle(triangle: Triangle): Boolean = js.native
  def isEmpty(): Boolean = js.native
  @JSName("makeEmpty")
  protected def makeEmpty$raw(): Box3 = js.native
  @JSName("set")
  protected def set$raw(min: Vector3, max: Vector3): Box3 = js.native
  @JSName("setFromArray")
  protected def setFromArray$raw(array: facadecontract.std.JSArrayLike[Double]): Box3 = js.native
  @JSName("setFromBufferAttribute")
  protected def setFromBufferAttribute$raw(attribute: BufferAttribute[BufferAttributeEventMap]): Box3 = js.native
  @JSName("setFromCenterAndSize")
  protected def setFromCenterAndSize$raw(center: Vector3, size: Vector3): Box3 = js.native
  @JSName("setFromObject")
  protected def setFromObject$raw(`object`: Object3D[Object3DEventMap], precise: js.UndefOr[Boolean] = js.undefined): Box3 = js.native
  @JSName("setFromPoints")
  protected def setFromPoints$raw(points: js.Array[Vector3Like]): Box3 = js.native
  def toJSON(): Box3JSON = js.native
  @JSName("translate")
  protected def translate$raw(offset: Vector3): Box3 = js.native
  @JSName("union")
  protected def union$raw(box: Box3): Box3 = js.native

object Box3:
  extension [Self <: Box3](self: Self)
    inline def set(min: Vector3, max: Vector3): Self = self.set$raw(min, max).asInstanceOf[Self]
    inline def setFromArray(array: facadecontract.std.JSArrayLike[Double]): Self = self.setFromArray$raw(array).asInstanceOf[Self]
    inline def setFromBufferAttribute(attribute: BufferAttribute[BufferAttributeEventMap]): Self = self.setFromBufferAttribute$raw(attribute).asInstanceOf[Self]
    inline def setFromPoints(points: js.Array[Vector3Like]): Self = self.setFromPoints$raw(points).asInstanceOf[Self]
    inline def setFromCenterAndSize(center: Vector3, size: Vector3): Self = self.setFromCenterAndSize$raw(center, size).asInstanceOf[Self]
    inline def setFromObject(`object`: Object3D[Object3DEventMap], precise: js.UndefOr[Boolean] = js.undefined): Self = self.setFromObject$raw(`object`, precise).asInstanceOf[Self]
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]
    inline def copy(box: Box3): Self = self.copy$raw(box).asInstanceOf[Self]
    inline def makeEmpty(): Self = self.makeEmpty$raw().asInstanceOf[Self]
    inline def expandByPoint(point: Vector3Like): Self = self.expandByPoint$raw(point).asInstanceOf[Self]
    inline def expandByVector(vector: Vector3): Self = self.expandByVector$raw(vector).asInstanceOf[Self]
    inline def expandByScalar(scalar: Double): Self = self.expandByScalar$raw(scalar).asInstanceOf[Self]
    inline def expandByObject(`object`: Object3D[Object3DEventMap], precise: js.UndefOr[Boolean] = js.undefined): Self = self.expandByObject$raw(`object`, precise).asInstanceOf[Self]
    inline def intersect(box: Box3): Self = self.intersect$raw(box).asInstanceOf[Self]
    inline def union(box: Box3): Self = self.union$raw(box).asInstanceOf[Self]
    inline def applyMatrix4(matrix: Matrix4): Self = self.applyMatrix4$raw(matrix).asInstanceOf[Self]
    inline def translate(offset: Vector3): Self = self.translate$raw(offset).asInstanceOf[Self]
    inline def fromJSON(json: Box3JSON): Self = self.fromJSON$raw(json).asInstanceOf[Self]
