// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait default_b44adefb extends js.Object:
  var context: facadecontract.std.JSUnknown = js.native
  var geometry: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap] = js.native
  var material: Material[MaterialEventMap] = js.native
  var `object`: Object3D[Object3DEventMap] = js.native
  var renderer: Renderer = js.native

object default_b44adefb:
  def apply(
    `object`: Object3D[Object3DEventMap],
    material: Material[MaterialEventMap],
    geometry: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap],
    renderer: Renderer,
    context: facadecontract.std.JSUnknown
  ): default_b44adefb =
    js.Dynamic.literal(
      "object" -> `object`.asInstanceOf[js.Any],
      "material" -> material.asInstanceOf[js.Any],
      "geometry" -> geometry.asInstanceOf[js.Any],
      "renderer" -> renderer.asInstanceOf[js.Any],
      "context" -> context.asInstanceOf[js.Any]
    ).asInstanceOf[default_b44adefb]
