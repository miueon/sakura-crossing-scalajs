// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait WebXRSpaceEventMapPinchend extends js.Object:
  var handedness: "left" | "none" | "right" = js.native
  var target: WebXRController = js.native

object WebXRSpaceEventMapPinchend:
  def apply(
    handedness: "left" | "none" | "right",
    target: WebXRController
  ): WebXRSpaceEventMapPinchend =
    js.Dynamic.literal(
      "handedness" -> handedness.asInstanceOf[js.Any],
      "target" -> target.asInstanceOf[js.Any]
    ).asInstanceOf[WebXRSpaceEventMapPinchend]
