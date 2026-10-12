// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait BoxGeometryMorphAttributes extends js.Object:
  var color: js.UndefOr[js.Array[BufferAttribute[BufferAttributeEventMap] | InterleavedBufferAttribute]] = js.native
  var normal: js.UndefOr[js.Array[BufferAttribute[BufferAttributeEventMap] | InterleavedBufferAttribute]] = js.native
  var position: js.UndefOr[js.Array[BufferAttribute[BufferAttributeEventMap] | InterleavedBufferAttribute]] = js.native

object BoxGeometryMorphAttributes:
  def apply(
    position: Option[js.UndefOr[js.Array[BufferAttribute[BufferAttributeEventMap] | InterleavedBufferAttribute]]] = None,
    normal: Option[js.UndefOr[js.Array[BufferAttribute[BufferAttributeEventMap] | InterleavedBufferAttribute]]] = None,
    color: Option[js.UndefOr[js.Array[BufferAttribute[BufferAttributeEventMap] | InterleavedBufferAttribute]]] = None
  ): BoxGeometryMorphAttributes =
    val _res = js.Dynamic.literal()
    position.foreach(v => _res.updateDynamic("position")(v.asInstanceOf[js.Any]))
    normal.foreach(v => _res.updateDynamic("normal")(v.asInstanceOf[js.Any]))
    color.foreach(v => _res.updateDynamic("color")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[BoxGeometryMorphAttributes]
