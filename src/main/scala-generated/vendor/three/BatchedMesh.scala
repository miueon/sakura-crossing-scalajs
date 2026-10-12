// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

// #WARN: Disambiguated erasure-colliding overload 'getColorAt' with @targetName("getColorAt_1") and @JSName("getColorAt")
// #WARN: Disambiguated erasure-colliding overload 'getColorAt' with @targetName("getColorAt_2") and @JSName("getColorAt")
@js.native
@JSImport("three", "BatchedMesh")
class BatchedMesh(maxInstanceCount_1: Double, maxVertexCount: Double, maxIndexCount: js.UndefOr[Double] = js.undefined, material: js.UndefOr[Material[MaterialEventMap]] = js.undefined) extends Mesh[BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], Material[MaterialEventMap], Object3DEventMap](js.native: js.UndefOr[BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap]], js.native: js.UndefOr[Material[MaterialEventMap]]):
  var boundingBox: Box3 | Null = js.native
  var boundingSphere: Sphere | Null = js.native
  var customSort: js.ThisFunction2[BatchedMesh, js.Array[BatchedMeshCustomSortList], Camera, Unit] | Null = js.native
  def instanceCount: Double = js.native
  def isBatchedMesh: true = js.native
  def maxInstanceCount: Double = js.native
  var perObjectFrustumCulled: Boolean = js.native
  var sortObjects: Boolean = js.native
  def unusedIndexCount: Double = js.native
  def unusedVertexCount: Double = js.native
  def addGeometry(geometry: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], reservedVertexRange: js.UndefOr[Double] = js.undefined, reservedIndexRange: js.UndefOr[Double] = js.undefined): Double = js.native
  def addInstance(geometryId: Double): Double = js.native
  def computeBoundingBox(): Unit = js.native
  def computeBoundingSphere(): Unit = js.native
  @JSName("deleteGeometry")
  protected def deleteGeometry$raw(geometryId: Double): BatchedMesh = js.native
  @JSName("deleteInstance")
  protected def deleteInstance$raw(instanceId: Double): BatchedMesh = js.native
  def dispose(): Unit = js.native
  def getBoundingBoxAt(geometryId: Double, target: Box3): Box3 | Null = js.native
  def getBoundingSphereAt(geometryId: Double, target: Sphere): Sphere | Null = js.native
  def getColorAt(instanceId: Double, color: Color): Color = js.native
  // #WARN: Disambiguated erasure-colliding overload 'getColorAt' with @targetName("getColorAt_1") and @JSName("getColorAt")
  @scala.annotation.targetName("getColorAt_1")
  @JSName("getColorAt")
  def getColorAt(instanceId: Double, color: Vector4): Vector4 = js.native
  // #WARN: Disambiguated erasure-colliding overload 'getColorAt' with @targetName("getColorAt_2") and @JSName("getColorAt")
  @scala.annotation.targetName("getColorAt_2")
  @JSName("getColorAt")
  def getColorAt(instanceId: Double, color: Color | Vector4): Color | Vector4 = js.native
  def getGeometryIdAt(instanceId: Double): Double = js.native
  def getGeometryRangeAt(geometryId: Double, target: js.UndefOr[BatchedMeshGeometryRange] = js.undefined): BatchedMeshGeometryRange | Null = js.native
  def getMatrixAt(instanceId: Double, target: Matrix4): Matrix4 = js.native
  def getVisibleAt(instanceId: Double): Boolean = js.native
  @JSName("optimize")
  protected def optimize$raw(): BatchedMesh = js.native
  def setColorAt(instanceId: Double, color: Color | Vector4): Unit = js.native
  @JSName("setCustomSort")
  protected def setCustomSort$raw(sortFunction: js.ThisFunction2[this.type, js.Array[js.Object], Camera, Unit] | Null): BatchedMesh = js.native
  def setGeometryAt(geometryId: Double, geometry: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap]): Double = js.native
  @JSName("setGeometryIdAt")
  protected def setGeometryIdAt$raw(instanceId: Double, geometryId: Double): BatchedMesh = js.native
  def setGeometrySize(maxVertexCount: Double, maxIndexCount: Double): Unit = js.native
  def setInstanceCount(maxInstanceCount: Double): Unit = js.native
  @JSName("setMatrixAt")
  protected def setMatrixAt$raw(instanceId: Double, matrix: Matrix4): BatchedMesh = js.native
  @JSName("setVisibleAt")
  protected def setVisibleAt$raw(instanceId: Double, visible: Boolean): BatchedMesh = js.native

object BatchedMesh:
  extension [Self <: BatchedMesh](self: Self)
    inline def setCustomSort(sortFunction: js.ThisFunction2[self.type, js.Array[js.Object], Camera, Unit] | Null): Self = self.setCustomSort$raw(sortFunction).asInstanceOf[Self]
    inline def setMatrixAt(instanceId: Double, matrix: Matrix4): Self = self.setMatrixAt$raw(instanceId, matrix).asInstanceOf[Self]
    inline def setVisibleAt(instanceId: Double, visible: Boolean): Self = self.setVisibleAt$raw(instanceId, visible).asInstanceOf[Self]
    inline def setGeometryIdAt(instanceId: Double, geometryId: Double): Self = self.setGeometryIdAt$raw(instanceId, geometryId).asInstanceOf[Self]
    inline def deleteGeometry(geometryId: Double): Self = self.deleteGeometry$raw(geometryId).asInstanceOf[Self]
    inline def deleteInstance(instanceId: Double): Self = self.deleteInstance$raw(instanceId).asInstanceOf[Self]
    inline def optimize(): Self = self.optimize$raw().asInstanceOf[Self]
