// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "ArrowHelper")
class ArrowHelper(dir: js.UndefOr[Vector3] = js.undefined, origin: js.UndefOr[Vector3] = js.undefined, length: js.UndefOr[Double] = js.undefined, color: js.UndefOr[String | Double | Color] = js.undefined, headLength: js.UndefOr[Double] = js.undefined, headWidth: js.UndefOr[Double] = js.undefined) extends Object3D[Object3DEventMap]:
  var cone: Mesh[BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], Material[MaterialEventMap] | js.Array[Material[MaterialEventMap]], Object3DEventMap] = js.native
  var line: Line[BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], Material[MaterialEventMap] | js.Array[Material[MaterialEventMap]], Object3DEventMap] = js.native
  @JSName("copy")
  protected override def copy$raw(`object`: Object3D[Object3DEventMap], recursive: js.UndefOr[Boolean] = js.undefined): ArrowHelper = js.native
  def dispose(): Unit = js.native
  def setColor(color: String | Double | Color): Unit = js.native
  def setDirection(dir: Vector3): Unit = js.native
  def setLength(length: Double, headLength: js.UndefOr[Double] = js.undefined, headWidth: js.UndefOr[Double] = js.undefined): Unit = js.native

object ArrowHelper:
  extension [Self <: ArrowHelper](self: Self)
    inline def copy(source: ArrowHelper): Self = self.copy$raw(source, js.undefined).asInstanceOf[Self]
