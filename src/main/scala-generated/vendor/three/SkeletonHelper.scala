// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "SkeletonHelper")
class SkeletonHelper(`object`: Object3D[Object3DEventMap]) extends LineSegments[BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], Material[MaterialEventMap] | js.Array[Material[MaterialEventMap]], Object3DEventMap](js.native: js.UndefOr[BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap]], js.native: js.UndefOr[Material[MaterialEventMap] | js.Array[Material[MaterialEventMap]]]):
  var bones: js.Array[Bone[Object3DEventMap]] = js.native
  def isSkeletonHelper: Boolean = js.native
  var root: Object3D[Object3DEventMap] = js.native
  def dispose(): Unit = js.native
  def setColors(color1: Color, color2: Color): SkeletonHelper = js.native
