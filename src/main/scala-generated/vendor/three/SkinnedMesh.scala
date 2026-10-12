// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

// #WARN: Disambiguated erasure-colliding overload 'applyBoneTransform' with @targetName("applyBoneTransform_1") and @JSName("applyBoneTransform")
// #WARN: Disambiguated erasure-colliding overload 'applyBoneTransform' with @targetName("applyBoneTransform_2") and @JSName("applyBoneTransform")
@js.native
@JSImport("three", "SkinnedMesh")
class SkinnedMesh[TGeometry <: BufferGeometry[?, ?], TMaterial <: Material[?] | js.Array[Material[MaterialEventMap]], TEventMap <: Object3DEventMap](geometry: js.UndefOr[TGeometry] = js.undefined, material: js.UndefOr[TMaterial] = js.undefined, useVertexTexture: js.UndefOr[Boolean] = js.undefined) extends Mesh[TGeometry, TMaterial, TEventMap](js.native: js.UndefOr[TGeometry], js.native: js.UndefOr[TMaterial]):
  var bindMatrix: Matrix4 = js.native
  var bindMatrixInverse: Matrix4 = js.native
  var bindMode: "attached" | "detached" = js.native
  var boundingBox: Box3 = js.native
  var boundingSphere: Sphere = js.native
  def isSkinnedMesh: true = js.native
  var skeleton: Skeleton = js.native
  override def `type`: String = js.native
  def applyBoneTransform(index: Double, vector: Vector3): Vector3 = js.native
  // #WARN: Disambiguated erasure-colliding overload 'applyBoneTransform' with @targetName("applyBoneTransform_1") and @JSName("applyBoneTransform")
  @scala.annotation.targetName("applyBoneTransform_1")
  @JSName("applyBoneTransform")
  def applyBoneTransform(index: Double, vector: Vector4): Vector4 = js.native
  // #WARN: Disambiguated erasure-colliding overload 'applyBoneTransform' with @targetName("applyBoneTransform_2") and @JSName("applyBoneTransform")
  @scala.annotation.targetName("applyBoneTransform_2")
  @JSName("applyBoneTransform")
  def applyBoneTransform(index: Double, vector: Vector3 | Vector4): Vector3 | Vector4 = js.native
  def bind(skeleton: Skeleton, bindMatrix: js.UndefOr[Matrix4] = js.undefined): Unit = js.native
  def computeBoundingBox(): Unit = js.native
  def computeBoundingSphere(): Unit = js.native
  def normalizeSkinWeights(): Unit = js.native
  def pose(): Unit = js.native
