// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait AnimationMixerEventMap extends js.Object:
  var finished: AnimationMixerEventMapFinished = js.native
  var loop: AnimationMixerEventMapLoop = js.native

object AnimationMixerEventMap:
  def apply(
    loop: AnimationMixerEventMapLoop,
    finished: AnimationMixerEventMapFinished
  ): AnimationMixerEventMap =
    js.Dynamic.literal(
      "loop" -> loop.asInstanceOf[js.Any],
      "finished" -> finished.asInstanceOf[js.Any]
    ).asInstanceOf[AnimationMixerEventMap]
