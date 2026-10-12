// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRDOMOverlayState extends js.Object:
  var `type`: "floating" | "head-locked" | "screen" = js.native

object XRDOMOverlayState:
  def apply(
    `type`: "floating" | "head-locked" | "screen"
  ): XRDOMOverlayState =
    js.Dynamic.literal("type" -> `type`.asInstanceOf[js.Any]).asInstanceOf[XRDOMOverlayState]
