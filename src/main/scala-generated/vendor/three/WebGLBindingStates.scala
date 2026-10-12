// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "WebGLBindingStates")
class WebGLBindingStates(gl: js.Object, attributes: WebGLAttributes) extends js.Object:
  def disableUnusedAttributes(): Unit = js.native
  def dispose(): Unit = js.native
  def enableAttribute(attribute: Double): Unit = js.native
  def initAttributes(): Unit = js.native
  def releaseStatesOfGeometry(): Unit = js.native
  def releaseStatesOfProgram(): Unit = js.native
  def reset(): Unit = js.native
  def resetDefaultState(): Unit = js.native
  def setup(`object`: Object3D[Object3DEventMap], material: Material[MaterialEventMap], program: WebGLProgram, geometry: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], index: BufferAttribute[BufferAttributeEventMap]): Unit = js.native
