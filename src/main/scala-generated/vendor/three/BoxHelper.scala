// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "BoxHelper")
class BoxHelper(object_1: Object3D[Object3DEventMap], color: js.UndefOr[String | Double | Color] = js.undefined) extends LineSegments[BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], LineBasicMaterial, Object3DEventMap](js.native: js.UndefOr[BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap]], js.native: js.UndefOr[LineBasicMaterial]):
  var `object`: Object3D[Object3DEventMap] = js.native
  @JSName("copy")
  protected override def copy$raw(`object`: Object3D[Object3DEventMap], recursive: js.UndefOr[Boolean] = js.undefined): BoxHelper = js.native
  def dispose(): Unit = js.native
  def setFromObject(`object`: Object3D[Object3DEventMap]): BoxHelper = js.native
  def update(): Unit = js.native

object BoxHelper:
  extension [Self <: BoxHelper](self: Self)
    inline def copy(source: BoxHelper, recursive: js.UndefOr[Boolean] = js.undefined): Self = self.copy$raw(source, recursive).asInstanceOf[Self]
