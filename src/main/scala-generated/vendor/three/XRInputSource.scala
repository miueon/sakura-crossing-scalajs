// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait XRInputSource extends js.Object:
  val gamepad: js.UndefOr[js.Object] = js.undefined
  val gripSpace: js.UndefOr[XRSpace] = js.undefined
  val hand: js.UndefOr[XRHand] = js.undefined
  val handedness: "left" | "none" | "right"
  val profiles: js.Array[String]
  val targetRayMode: "gaze" | "screen" | "tracked-pointer" | "transient-pointer"
  val targetRaySpace: XRSpace

object XRInputSource:
  def apply(
    handedness: "left" | "none" | "right",
    targetRayMode: "gaze" | "screen" | "tracked-pointer" | "transient-pointer",
    targetRaySpace: XRSpace,
    gripSpace: Option[js.UndefOr[XRSpace]] = None,
    gamepad: Option[js.UndefOr[js.Object]] = None,
    profiles: js.Array[String],
    hand: Option[js.UndefOr[XRHand]] = None
  ): XRInputSource =
    val _res = js.Dynamic.literal(
      "handedness" -> handedness.asInstanceOf[js.Any],
      "targetRayMode" -> targetRayMode.asInstanceOf[js.Any],
      "targetRaySpace" -> targetRaySpace.asInstanceOf[js.Any]
    )
    gripSpace.foreach(v => _res.updateDynamic("gripSpace")(v.asInstanceOf[js.Any]))
    gamepad.foreach(v => _res.updateDynamic("gamepad")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("profiles")(profiles.asInstanceOf[js.Any])
    hand.foreach(v => _res.updateDynamic("hand")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[XRInputSource]
