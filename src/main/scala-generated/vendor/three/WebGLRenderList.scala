// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "WebGLRenderList")
class WebGLRenderList(properties: WebGLProperties) extends js.Object:
  var `opaque`: js.Array[RenderItem] = js.native
  var transmissive: js.Array[RenderItem] = js.native
  var `transparent`: js.Array[RenderItem] = js.native
  def finish(): Unit = js.native
  def init(): Unit = js.native
  def push(`object`: Object3D[Object3DEventMap], geometry: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap] | Null, material: Material[MaterialEventMap], groupOrder: Double, z: Double, group: Group[Object3DEventMap] | Null): Unit = js.native
  def sort(opaqueSort: js.Function2[js.Any, js.Any, Double], transparentSort: js.Function2[js.Any, js.Any, Double], reversedDepth: Boolean): Unit = js.native
  def unshift(`object`: Object3D[Object3DEventMap], geometry: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap] | Null, material: Material[MaterialEventMap], groupOrder: Double, z: Double, group: Group[Object3DEventMap] | Null): Unit = js.native
