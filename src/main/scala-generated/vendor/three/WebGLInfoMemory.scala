// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait WebGLInfoMemory extends js.Object:
  var geometries: Double = js.native
  var textures: Double = js.native

object WebGLInfoMemory:
  def apply(
    geometries: Double,
    textures: Double
  ): WebGLInfoMemory =
    js.Dynamic.literal(
      "geometries" -> geometries.asInstanceOf[js.Any],
      "textures" -> textures.asInstanceOf[js.Any]
    ).asInstanceOf[WebGLInfoMemory]
