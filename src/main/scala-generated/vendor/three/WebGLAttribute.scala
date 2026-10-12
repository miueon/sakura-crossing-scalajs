// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait WebGLAttribute extends js.Object:
  var buffer: js.Object = js.native
  var bytesPerElement: Double = js.native
  var size: Double = js.native
  var `type`: Double = js.native
  var version: Double = js.native

object WebGLAttribute:
  def apply(
    buffer: js.Object,
    `type`: Double,
    bytesPerElement: Double,
    version: Double,
    size: Double
  ): WebGLAttribute =
    js.Dynamic.literal(
      "buffer" -> buffer.asInstanceOf[js.Any],
      "type" -> `type`.asInstanceOf[js.Any],
      "bytesPerElement" -> bytesPerElement.asInstanceOf[js.Any],
      "version" -> version.asInstanceOf[js.Any],
      "size" -> size.asInstanceOf[js.Any]
    ).asInstanceOf[WebGLAttribute]
