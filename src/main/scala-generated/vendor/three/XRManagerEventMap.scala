// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRManagerEventMap extends js.Object:
  var planesdetected: XRManagerEventMapPlanesdetected = js.native
  var sessionend: js.Any = js.native
  var sessionstart: js.Any = js.native

object XRManagerEventMap:
  def apply(
    sessionstart: js.Any,
    sessionend: js.Any,
    planesdetected: XRManagerEventMapPlanesdetected
  ): XRManagerEventMap =
    js.Dynamic.literal(
      "sessionstart" -> sessionstart.asInstanceOf[js.Any],
      "sessionend" -> sessionend.asInstanceOf[js.Any],
      "planesdetected" -> planesdetected.asInstanceOf[js.Any]
    ).asInstanceOf[XRManagerEventMap]
