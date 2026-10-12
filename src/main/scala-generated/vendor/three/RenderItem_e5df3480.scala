// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait RenderItem_e5df3480 extends js.Object:
  var clippingContext: ClippingContext | Null = js.native
  var geometry: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap] | Null = js.native
  var group: GeometryGroup | Null = js.native
  var groupOrder: Double | Null = js.native
  var id: Double | Null = js.native
  var material: Material[MaterialEventMap] | Null = js.native
  var `object`: Object3D[Object3DEventMap] | Null = js.native
  var renderOrder: Double | Null = js.native
  var z: Double | Null = js.native

object RenderItem_e5df3480:
  def apply(
    id: Double | Null,
    `object`: Object3D[Object3DEventMap] | Null,
    geometry: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap] | Null,
    material: Material[MaterialEventMap] | Null,
    groupOrder: Double | Null,
    renderOrder: Double | Null,
    z: Double | Null,
    group: GeometryGroup | Null,
    clippingContext: ClippingContext | Null
  ): RenderItem_e5df3480 =
    js.Dynamic.literal(
      "id" -> id.asInstanceOf[js.Any],
      "object" -> `object`.asInstanceOf[js.Any],
      "geometry" -> geometry.asInstanceOf[js.Any],
      "material" -> material.asInstanceOf[js.Any],
      "groupOrder" -> groupOrder.asInstanceOf[js.Any],
      "renderOrder" -> renderOrder.asInstanceOf[js.Any],
      "z" -> z.asInstanceOf[js.Any],
      "group" -> group.asInstanceOf[js.Any],
      "clippingContext" -> clippingContext.asInstanceOf[js.Any]
    ).asInstanceOf[RenderItem_e5df3480]
