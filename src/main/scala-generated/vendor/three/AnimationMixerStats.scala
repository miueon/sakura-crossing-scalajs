// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait AnimationMixerStats extends js.Object:
  var actions: AnimationMixerStatsActions = js.native
  var bindings: AnimationMixerStatsActions = js.native
  var controlInterpolants: AnimationMixerStatsActions = js.native

object AnimationMixerStats:
  def apply(
    actions: AnimationMixerStatsActions,
    bindings: AnimationMixerStatsActions,
    controlInterpolants: AnimationMixerStatsActions
  ): AnimationMixerStats =
    js.Dynamic.literal(
      "actions" -> actions.asInstanceOf[js.Any],
      "bindings" -> bindings.asInstanceOf[js.Any],
      "controlInterpolants" -> controlInterpolants.asInstanceOf[js.Any]
    ).asInstanceOf[AnimationMixerStats]
