// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait AnimationMixerStatsActions extends js.Object:
  val inUse: Double
  val total: Double

object AnimationMixerStatsActions:
  def apply(
    total: Double,
    inUse: Double
  ): AnimationMixerStatsActions =
    js.Dynamic.literal(
      "total" -> total.asInstanceOf[js.Any],
      "inUse" -> inUse.asInstanceOf[js.Any]
    ).asInstanceOf[AnimationMixerStatsActions]
