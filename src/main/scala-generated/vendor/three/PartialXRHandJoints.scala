// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait PartialXRHandJoints extends js.Object:
  var `index-finger-metacarpal`: js.UndefOr[XRJointSpace] = js.native
  var `index-finger-phalanx-distal`: js.UndefOr[XRJointSpace] = js.native
  var `index-finger-phalanx-intermediate`: js.UndefOr[XRJointSpace] = js.native
  var `index-finger-phalanx-proximal`: js.UndefOr[XRJointSpace] = js.native
  var `index-finger-tip`: js.UndefOr[XRJointSpace] = js.native
  var `middle-finger-metacarpal`: js.UndefOr[XRJointSpace] = js.native
  var `middle-finger-phalanx-distal`: js.UndefOr[XRJointSpace] = js.native
  var `middle-finger-phalanx-intermediate`: js.UndefOr[XRJointSpace] = js.native
  var `middle-finger-phalanx-proximal`: js.UndefOr[XRJointSpace] = js.native
  var `middle-finger-tip`: js.UndefOr[XRJointSpace] = js.native
  var `pinky-finger-metacarpal`: js.UndefOr[XRJointSpace] = js.native
  var `pinky-finger-phalanx-distal`: js.UndefOr[XRJointSpace] = js.native
  var `pinky-finger-phalanx-intermediate`: js.UndefOr[XRJointSpace] = js.native
  var `pinky-finger-phalanx-proximal`: js.UndefOr[XRJointSpace] = js.native
  var `pinky-finger-tip`: js.UndefOr[XRJointSpace] = js.native
  var `ring-finger-metacarpal`: js.UndefOr[XRJointSpace] = js.native
  var `ring-finger-phalanx-distal`: js.UndefOr[XRJointSpace] = js.native
  var `ring-finger-phalanx-intermediate`: js.UndefOr[XRJointSpace] = js.native
  var `ring-finger-phalanx-proximal`: js.UndefOr[XRJointSpace] = js.native
  var `ring-finger-tip`: js.UndefOr[XRJointSpace] = js.native
  var `thumb-metacarpal`: js.UndefOr[XRJointSpace] = js.native
  var `thumb-phalanx-distal`: js.UndefOr[XRJointSpace] = js.native
  var `thumb-phalanx-proximal`: js.UndefOr[XRJointSpace] = js.native
  var `thumb-tip`: js.UndefOr[XRJointSpace] = js.native
  var wrist: js.UndefOr[XRJointSpace] = js.native

object PartialXRHandJoints:
  def apply(
    wrist: js.UndefOr[XRJointSpace] = js.undefined,
    `thumb-metacarpal`: js.UndefOr[XRJointSpace] = js.undefined,
    `thumb-phalanx-proximal`: js.UndefOr[XRJointSpace] = js.undefined,
    `thumb-phalanx-distal`: js.UndefOr[XRJointSpace] = js.undefined,
    `thumb-tip`: js.UndefOr[XRJointSpace] = js.undefined,
    `index-finger-metacarpal`: js.UndefOr[XRJointSpace] = js.undefined,
    `index-finger-phalanx-proximal`: js.UndefOr[XRJointSpace] = js.undefined,
    `index-finger-phalanx-intermediate`: js.UndefOr[XRJointSpace] = js.undefined,
    `index-finger-phalanx-distal`: js.UndefOr[XRJointSpace] = js.undefined,
    `index-finger-tip`: js.UndefOr[XRJointSpace] = js.undefined,
    `middle-finger-metacarpal`: js.UndefOr[XRJointSpace] = js.undefined,
    `middle-finger-phalanx-proximal`: js.UndefOr[XRJointSpace] = js.undefined,
    `middle-finger-phalanx-intermediate`: js.UndefOr[XRJointSpace] = js.undefined,
    `middle-finger-phalanx-distal`: js.UndefOr[XRJointSpace] = js.undefined,
    `middle-finger-tip`: js.UndefOr[XRJointSpace] = js.undefined,
    `ring-finger-metacarpal`: js.UndefOr[XRJointSpace] = js.undefined,
    `ring-finger-phalanx-proximal`: js.UndefOr[XRJointSpace] = js.undefined,
    `ring-finger-phalanx-intermediate`: js.UndefOr[XRJointSpace] = js.undefined,
    `ring-finger-phalanx-distal`: js.UndefOr[XRJointSpace] = js.undefined,
    `ring-finger-tip`: js.UndefOr[XRJointSpace] = js.undefined,
    `pinky-finger-metacarpal`: js.UndefOr[XRJointSpace] = js.undefined,
    `pinky-finger-phalanx-proximal`: js.UndefOr[XRJointSpace] = js.undefined,
    `pinky-finger-phalanx-intermediate`: js.UndefOr[XRJointSpace] = js.undefined,
    `pinky-finger-phalanx-distal`: js.UndefOr[XRJointSpace] = js.undefined,
    `pinky-finger-tip`: js.UndefOr[XRJointSpace] = js.undefined
  ): PartialXRHandJoints =
    val _res = js.Dynamic.literal()
    wrist.foreach(v => _res.updateDynamic("wrist")(v.asInstanceOf[js.Any]))
    `thumb-metacarpal`.foreach(v => _res.updateDynamic("thumb-metacarpal")(v.asInstanceOf[js.Any]))
    `thumb-phalanx-proximal`.foreach(v => _res.updateDynamic("thumb-phalanx-proximal")(v.asInstanceOf[js.Any]))
    `thumb-phalanx-distal`.foreach(v => _res.updateDynamic("thumb-phalanx-distal")(v.asInstanceOf[js.Any]))
    `thumb-tip`.foreach(v => _res.updateDynamic("thumb-tip")(v.asInstanceOf[js.Any]))
    `index-finger-metacarpal`.foreach(v => _res.updateDynamic("index-finger-metacarpal")(v.asInstanceOf[js.Any]))
    `index-finger-phalanx-proximal`.foreach(v => _res.updateDynamic("index-finger-phalanx-proximal")(v.asInstanceOf[js.Any]))
    `index-finger-phalanx-intermediate`.foreach(v => _res.updateDynamic("index-finger-phalanx-intermediate")(v.asInstanceOf[js.Any]))
    `index-finger-phalanx-distal`.foreach(v => _res.updateDynamic("index-finger-phalanx-distal")(v.asInstanceOf[js.Any]))
    `index-finger-tip`.foreach(v => _res.updateDynamic("index-finger-tip")(v.asInstanceOf[js.Any]))
    `middle-finger-metacarpal`.foreach(v => _res.updateDynamic("middle-finger-metacarpal")(v.asInstanceOf[js.Any]))
    `middle-finger-phalanx-proximal`.foreach(v => _res.updateDynamic("middle-finger-phalanx-proximal")(v.asInstanceOf[js.Any]))
    `middle-finger-phalanx-intermediate`.foreach(v => _res.updateDynamic("middle-finger-phalanx-intermediate")(v.asInstanceOf[js.Any]))
    `middle-finger-phalanx-distal`.foreach(v => _res.updateDynamic("middle-finger-phalanx-distal")(v.asInstanceOf[js.Any]))
    `middle-finger-tip`.foreach(v => _res.updateDynamic("middle-finger-tip")(v.asInstanceOf[js.Any]))
    `ring-finger-metacarpal`.foreach(v => _res.updateDynamic("ring-finger-metacarpal")(v.asInstanceOf[js.Any]))
    `ring-finger-phalanx-proximal`.foreach(v => _res.updateDynamic("ring-finger-phalanx-proximal")(v.asInstanceOf[js.Any]))
    `ring-finger-phalanx-intermediate`.foreach(v => _res.updateDynamic("ring-finger-phalanx-intermediate")(v.asInstanceOf[js.Any]))
    `ring-finger-phalanx-distal`.foreach(v => _res.updateDynamic("ring-finger-phalanx-distal")(v.asInstanceOf[js.Any]))
    `ring-finger-tip`.foreach(v => _res.updateDynamic("ring-finger-tip")(v.asInstanceOf[js.Any]))
    `pinky-finger-metacarpal`.foreach(v => _res.updateDynamic("pinky-finger-metacarpal")(v.asInstanceOf[js.Any]))
    `pinky-finger-phalanx-proximal`.foreach(v => _res.updateDynamic("pinky-finger-phalanx-proximal")(v.asInstanceOf[js.Any]))
    `pinky-finger-phalanx-intermediate`.foreach(v => _res.updateDynamic("pinky-finger-phalanx-intermediate")(v.asInstanceOf[js.Any]))
    `pinky-finger-phalanx-distal`.foreach(v => _res.updateDynamic("pinky-finger-phalanx-distal")(v.asInstanceOf[js.Any]))
    `pinky-finger-tip`.foreach(v => _res.updateDynamic("pinky-finger-tip")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[PartialXRHandJoints]
