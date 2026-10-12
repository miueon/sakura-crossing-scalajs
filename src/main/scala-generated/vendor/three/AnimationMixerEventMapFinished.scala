// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait AnimationMixerEventMapFinished extends js.Object:
  var action: AnimationAction = js.native
  var direction: Double = js.native

object AnimationMixerEventMapFinished:
  def apply(
    action: AnimationAction,
    direction: Double
  ): AnimationMixerEventMapFinished =
    js.Dynamic.literal(
      "action" -> action.asInstanceOf[js.Any],
      "direction" -> direction.asInstanceOf[js.Any]
    ).asInstanceOf[AnimationMixerEventMapFinished]
