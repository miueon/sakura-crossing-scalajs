// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "CameraHelper")
class CameraHelper(camera_1: Camera) extends LineSegments[BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], Material[MaterialEventMap] | js.Array[Material[MaterialEventMap]], Object3DEventMap](js.native: js.UndefOr[BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap]], js.native: js.UndefOr[Material[MaterialEventMap] | js.Array[Material[MaterialEventMap]]]):
  var camera: Camera = js.native
  var pointMap: js.Dictionary[js.Array[Double]] = js.native
  def dispose(): Unit = js.native
  def setColors(frustum: Color, cone: Color, up: Color, target: Color, cross: Color): CameraHelper = js.native
  def update(): Unit = js.native
