// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait WebXRManagerEventMap extends js.Object:
  var planeadded: WebXRManagerEventMapPlaneadded = js.native
  var planechanged: WebXRManagerEventMapPlaneadded = js.native
  var planeremoved: WebXRManagerEventMapPlaneadded = js.native
  var planesdetected: WebXRManagerEventMapPlanesdetected = js.native
  var sessionend: js.Any = js.native
  var sessionstart: js.Any = js.native

object WebXRManagerEventMap:
  def apply(
    sessionstart: js.Any,
    sessionend: js.Any,
    planeadded: WebXRManagerEventMapPlaneadded,
    planeremoved: WebXRManagerEventMapPlaneadded,
    planechanged: WebXRManagerEventMapPlaneadded,
    planesdetected: WebXRManagerEventMapPlanesdetected
  ): WebXRManagerEventMap =
    js.Dynamic.literal(
      "sessionstart" -> sessionstart.asInstanceOf[js.Any],
      "sessionend" -> sessionend.asInstanceOf[js.Any],
      "planeadded" -> planeadded.asInstanceOf[js.Any],
      "planeremoved" -> planeremoved.asInstanceOf[js.Any],
      "planechanged" -> planechanged.asInstanceOf[js.Any],
      "planesdetected" -> planesdetected.asInstanceOf[js.Any]
    ).asInstanceOf[WebXRManagerEventMap]
