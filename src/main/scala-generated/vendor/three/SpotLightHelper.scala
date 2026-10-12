// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "SpotLightHelper")
class SpotLightHelper(light_1: SpotLight, color_1: js.UndefOr[String | Double | Color] = js.undefined) extends Object3D[Object3DEventMap]:
  var color: js.UndefOr[String | Double | Color] = js.native
  var cone: LineSegments[BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], Material[MaterialEventMap] | js.Array[Material[MaterialEventMap]], Object3DEventMap] = js.native
  var light: SpotLight = js.native
  def dispose(): Unit = js.native
  def update(): Unit = js.native
