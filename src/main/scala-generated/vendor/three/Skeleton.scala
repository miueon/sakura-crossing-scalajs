// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Skeleton")
class Skeleton(bones_1: js.UndefOr[js.Array[Bone[Object3DEventMap]]] = js.undefined, boneInverses_1: js.UndefOr[js.Array[Matrix4]] = js.undefined) extends js.Object:
  var boneInverses: js.Array[Matrix4] = js.native
  var boneMatrices: js.typedarray.Float32Array | Null = js.native
  var bones: js.Array[Bone[Object3DEventMap]] = js.native
  var boneTexture: DataTexture | Null = js.native
  var frame: Double = js.native
  var uuid: String = js.native
  def calculateInverses(): Unit = js.native
  override def clone(): Skeleton = js.native
  @JSName("computeBoneTexture")
  protected def computeBoneTexture$raw(): Skeleton = js.native
  def dispose(): Unit = js.native
  def fromJSON(json: SkeletonJSON, bones: js.Dictionary[Bone[Object3DEventMap]]): Unit = js.native
  def getBoneByName(name: String): js.UndefOr[Bone[Object3DEventMap]] = js.native
  def init(): Unit = js.native
  def pose(): Unit = js.native
  def toJSON(): SkeletonJSON = js.native
  def update(): Unit = js.native

object Skeleton:
  extension [Self <: Skeleton](self: Self)
    inline def computeBoneTexture(): Self = self.computeBoneTexture$raw().asInstanceOf[Self]
