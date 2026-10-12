// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Sprite")
class Sprite[TEventMap <: Object3DEventMap](material_1: js.UndefOr[SpriteMaterial] = js.undefined) extends Object3D[TEventMap]:
  override def castShadow: false = js.native
  override def castShadow_=(value: Boolean | false): Unit = js.native
  var center: Vector2 = js.native
  override def count: Double = js.native
  override def count_=(value: js.UndefOr[Double]): Unit = js.native
  var geometry: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap] = js.native
  def isSprite: true = js.native
  var material: SpriteMaterial = js.native
  override def `type`: String = js.native
