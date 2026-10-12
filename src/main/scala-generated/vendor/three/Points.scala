// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Points")
class Points[TGeometry <: BufferGeometry[NormalOrGLBufferAttributes, ?], TMaterial <: Material[?] | js.Array[Material[MaterialEventMap]], TEventMap <: Object3DEventMap](geometry_1: js.UndefOr[TGeometry] = js.undefined, material_1: js.UndefOr[TMaterial] = js.undefined) extends Object3D[TEventMap]:
  var geometry: TGeometry = js.native
  def isPoints: true = js.native
  var material: TMaterial = js.native
  var morphTargetDictionary: js.UndefOr[js.Dictionary[Double]] = js.native
  var morphTargetInfluences: js.UndefOr[js.Array[Double]] = js.native
  override def `type`: String = js.native
  def updateMorphTargets(): Unit = js.native
