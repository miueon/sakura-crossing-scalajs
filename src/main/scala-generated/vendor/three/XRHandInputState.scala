// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRHandInputState extends js.Object:
  var pinching: Boolean = js.native

object XRHandInputState:
  def apply(
    pinching: Boolean
  ): XRHandInputState =
    js.Dynamic.literal("pinching" -> pinching.asInstanceOf[js.Any]).asInstanceOf[XRHandInputState]
