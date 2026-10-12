// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "BufferGeometry")
class BufferGeometry[Attributes <: NormalOrGLBufferAttributes, TEventMap <: BufferGeometryEventMap]() extends EventDispatcher[TEventMap]:
  var attributes: Attributes = js.native
  var boundingBox: Box3 | Null = js.native
  var boundingSphere: Sphere | Null = js.native
  var drawRange: BoxGeometryDrawRange = js.native
  var groups: js.Array[GeometryGroup] = js.native
  var id: Double = js.native
  var index: BufferAttribute[BufferAttributeEventMap] | Null = js.native
  var indirect: IndirectStorageBufferAttribute | Null = js.native
  var indirectOffset: Double | js.Array[Double] = js.native
  def isBufferGeometry: true = js.native
  var morphAttributes: BoxGeometryMorphAttributes = js.native
  var morphTargetsRelative: Boolean = js.native
  var name: String = js.native
  def `type`: String = js.native
  var userData: js.Dictionary[js.Any] = js.native
  var uuid: String = js.native
  def addGroup(start: Double, count: Double, materialIndex: js.UndefOr[Double] = js.undefined): Unit = js.native
  @JSName("applyMatrix4")
  protected def applyMatrix4$raw(matrix: Matrix4): BufferGeometry[Attributes, TEventMap] = js.native
  @JSName("applyQuaternion")
  protected def applyQuaternion$raw(quaternion: Quaternion): BufferGeometry[Attributes, TEventMap] = js.native
  @JSName("center")
  protected def center$raw(): BufferGeometry[Attributes, TEventMap] = js.native
  def clearGroups(): Unit = js.native
  @JSName("clone")
  protected def clone$raw(): BufferGeometry[Attributes, TEventMap] = js.native
  def computeBoundingBox(): Unit = js.native
  def computeBoundingSphere(): Unit = js.native
  def computeTangents(): Unit = js.native
  def computeVertexNormals(): Unit = js.native
  @JSName("copy")
  protected def copy$raw(source: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap]): BufferGeometry[Attributes, TEventMap] = js.native
  @JSName("deleteAttribute")
  protected def deleteAttribute$raw(name: js.Any): BufferGeometry[Attributes, TEventMap] = js.native
  def dispose(): Unit = js.native
  def getAttribute[K](name: K): BufferAttribute[BufferAttributeEventMap] | GLBufferAttribute | InterleavedBufferAttribute = js.native
  def getIndex(): BufferAttribute[BufferAttributeEventMap] | Null = js.native
  def getIndirect(): IndirectStorageBufferAttribute | Null = js.native
  def hasAttribute(name: js.Any): Boolean = js.native
  @JSName("lookAt")
  protected def lookAt$raw(vector: Vector3): BufferGeometry[Attributes, TEventMap] = js.native
  def normalizeNormals(): Unit = js.native
  @JSName("rotateX")
  protected def rotateX$raw(angle: Double): BufferGeometry[Attributes, TEventMap] = js.native
  @JSName("rotateY")
  protected def rotateY$raw(angle: Double): BufferGeometry[Attributes, TEventMap] = js.native
  @JSName("rotateZ")
  protected def rotateZ$raw(angle: Double): BufferGeometry[Attributes, TEventMap] = js.native
  @JSName("scale")
  protected def scale$raw(x: Double, y: Double, z: Double): BufferGeometry[Attributes, TEventMap] = js.native
  @JSName("setAttribute")
  protected def setAttribute$raw[K](name: K, attribute: js.Any): BufferGeometry[Attributes, TEventMap] = js.native
  def setDrawRange(start: Double, count: Double): Unit = js.native
  @JSName("setFromPoints")
  protected def setFromPoints$raw(points: js.Array[Vector2] | js.Array[Vector3]): BufferGeometry[Attributes, TEventMap] = js.native
  @JSName("setIndex")
  protected def setIndex$raw(index: BufferAttribute[BufferAttributeEventMap] | js.Array[Double] | Null): BufferGeometry[Attributes, TEventMap] = js.native
  @JSName("setIndirect")
  protected def setIndirect$raw(indirect: IndirectStorageBufferAttribute | Null, indirectOffset: js.UndefOr[Double | js.Array[Double]] = js.undefined): BufferGeometry[Attributes, TEventMap] = js.native
  def toJSON(): BufferGeometryJSON = js.native
  def toNonIndexed(): BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap] = js.native
  @JSName("translate")
  protected def translate$raw(x: Double, y: Double, z: Double): BufferGeometry[Attributes, TEventMap] = js.native

object BufferGeometry:
  extension [Attributes <: NormalOrGLBufferAttributes, TEventMap <: BufferGeometryEventMap, Self <: BufferGeometry[Attributes, TEventMap]](self: Self)
    inline def setIndex(index: BufferAttribute[BufferAttributeEventMap] | js.Array[Double] | Null): Self = self.setIndex$raw(index).asInstanceOf[Self]
    inline def setIndirect(indirect: IndirectStorageBufferAttribute | Null, indirectOffset: js.UndefOr[Double | js.Array[Double]] = js.undefined): Self = self.setIndirect$raw(indirect, indirectOffset).asInstanceOf[Self]
    inline def setAttribute[K](name: K, attribute: js.Any): Self = self.setAttribute$raw[K](name, attribute).asInstanceOf[Self]
    inline def deleteAttribute(name: js.Any): Self = self.deleteAttribute$raw(name).asInstanceOf[Self]
    inline def applyMatrix4(matrix: Matrix4): Self = self.applyMatrix4$raw(matrix).asInstanceOf[Self]
    inline def applyQuaternion(quaternion: Quaternion): Self = self.applyQuaternion$raw(quaternion).asInstanceOf[Self]
    inline def rotateX(angle: Double): Self = self.rotateX$raw(angle).asInstanceOf[Self]
    inline def rotateY(angle: Double): Self = self.rotateY$raw(angle).asInstanceOf[Self]
    inline def rotateZ(angle: Double): Self = self.rotateZ$raw(angle).asInstanceOf[Self]
    inline def translate(x: Double, y: Double, z: Double): Self = self.translate$raw(x, y, z).asInstanceOf[Self]
    inline def scale(x: Double, y: Double, z: Double): Self = self.scale$raw(x, y, z).asInstanceOf[Self]
    inline def lookAt(vector: Vector3): Self = self.lookAt$raw(vector).asInstanceOf[Self]
    inline def center(): Self = self.center$raw().asInstanceOf[Self]
    inline def setFromPoints(points: js.Array[Vector2] | js.Array[Vector3]): Self = self.setFromPoints$raw(points).asInstanceOf[Self]
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]
    inline def copy(source: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap]): Self = self.copy$raw(source).asInstanceOf[Self]
