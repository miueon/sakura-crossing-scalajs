// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "InstancedMesh")
class InstancedMesh[TGeometry <: BufferGeometry[?, ?], TMaterial <: Material[?] | js.Array[Material[MaterialEventMap]], TEventMap <: InstancedMeshEventMap](geometry: js.UndefOr[TGeometry], material: js.UndefOr[TMaterial], count_1: Double) extends Mesh[TGeometry, TMaterial, TEventMap](js.native: js.UndefOr[TGeometry], js.native: js.UndefOr[TMaterial]):
  var boundingBox: Box3 | Null = js.native
  var boundingSphere: Sphere | Null = js.native
  override def count: Double = js.native
  override def count_=(value: js.UndefOr[Double]): Unit = js.native
  var instanceColor: InstancedBufferAttribute | Null = js.native
  var instanceMatrix: InstancedBufferAttribute = js.native
  def isInstancedMesh: true = js.native
  var morphTexture: DataTexture | Null = js.native
  def computeBoundingBox(): Unit = js.native
  def computeBoundingSphere(): Unit = js.native
  def dispose(): Unit = js.native
  def getColorAt(index: Double, color: Color): Color = js.native
  def getMatrixAt(index: Double, matrix: Matrix4): Matrix4 = js.native
  def getMorphAt(index: Double, mesh: Mesh[BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], Material[MaterialEventMap] | js.Array[Material[MaterialEventMap]], Object3DEventMap]): Unit = js.native
  @JSName("setColorAt")
  protected def setColorAt$raw(index: Double, color: Color): InstancedMesh[TGeometry, TMaterial, TEventMap] = js.native
  @JSName("setMatrixAt")
  protected def setMatrixAt$raw(index: Double, matrix: Matrix4): InstancedMesh[TGeometry, TMaterial, TEventMap] = js.native
  @JSName("setMorphAt")
  protected def setMorphAt$raw(index: Double, mesh: Mesh[BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], Material[MaterialEventMap] | js.Array[Material[MaterialEventMap]], Object3DEventMap]): InstancedMesh[TGeometry, TMaterial, TEventMap] = js.native
  override def updateMorphTargets(): Unit = js.native

object InstancedMesh:
  extension [TGeometry <: BufferGeometry[?, ?], TMaterial <: Material[?] | js.Array[Material[MaterialEventMap]], TEventMap <: InstancedMeshEventMap, Self <: InstancedMesh[TGeometry, TMaterial, TEventMap]](self: Self)
    inline def setColorAt(index: Double, color: Color): Self = self.setColorAt$raw(index, color).asInstanceOf[Self]
    inline def setMatrixAt(index: Double, matrix: Matrix4): Self = self.setMatrixAt$raw(index, matrix).asInstanceOf[Self]
    inline def setMorphAt(index: Double, mesh: Mesh[BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], Material[MaterialEventMap] | js.Array[Material[MaterialEventMap]], Object3DEventMap]): Self = self.setMorphAt$raw(index, mesh).asInstanceOf[Self]
