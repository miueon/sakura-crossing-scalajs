// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait AnimationMixerEventMapLoop extends js.Object:
  var action: AnimationAction = js.native
  var loopDelta: Double = js.native

object AnimationMixerEventMapLoop:
  def apply(
    action: AnimationAction,
    loopDelta: Double
  ): AnimationMixerEventMapLoop =
    js.Dynamic.literal(
      "action" -> action.asInstanceOf[js.Any],
      "loopDelta" -> loopDelta.asInstanceOf[js.Any]
    ).asInstanceOf[AnimationMixerEventMapLoop]
