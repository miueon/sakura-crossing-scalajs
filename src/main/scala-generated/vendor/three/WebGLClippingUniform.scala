// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait WebGLClippingUniform extends js.Object:
  var needsUpdate: Boolean = js.native
  var value: js.Any = js.native

object WebGLClippingUniform:
  def apply(
    value: js.Any,
    needsUpdate: Boolean
  ): WebGLClippingUniform =
    js.Dynamic.literal(
      "value" -> value.asInstanceOf[js.Any],
      "needsUpdate" -> needsUpdate.asInstanceOf[js.Any]
    ).asInstanceOf[WebGLClippingUniform]
