// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait RenderItem extends js.Object:
  var geometry: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap] | Null = js.native
  var group: Group[Object3DEventMap] | Null = js.native
  var groupOrder: Double = js.native
  var id: Double = js.native
  var material: Material[MaterialEventMap] = js.native
  var materialVariant: Double = js.native
  var `object`: Object3D[Object3DEventMap] = js.native
  var renderOrder: Double = js.native
  var z: Double = js.native

object RenderItem:
  def apply(
    id: Double,
    `object`: Object3D[Object3DEventMap],
    geometry: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap] | Null,
    material: Material[MaterialEventMap],
    materialVariant: Double,
    groupOrder: Double,
    renderOrder: Double,
    z: Double,
    group: Group[Object3DEventMap] | Null
  ): RenderItem =
    js.Dynamic.literal(
      "id" -> id.asInstanceOf[js.Any],
      "object" -> `object`.asInstanceOf[js.Any],
      "geometry" -> geometry.asInstanceOf[js.Any],
      "material" -> material.asInstanceOf[js.Any],
      "materialVariant" -> materialVariant.asInstanceOf[js.Any],
      "groupOrder" -> groupOrder.asInstanceOf[js.Any],
      "renderOrder" -> renderOrder.asInstanceOf[js.Any],
      "z" -> z.asInstanceOf[js.Any],
      "group" -> group.asInstanceOf[js.Any]
    ).asInstanceOf[RenderItem]
