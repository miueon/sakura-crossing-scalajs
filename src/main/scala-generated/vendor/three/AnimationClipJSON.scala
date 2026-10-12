// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait AnimationClipJSON extends js.Object:
  var blendMode: 2500 | 2501 = js.native
  var duration: Double = js.native
  var name: String = js.native
  var tracks: js.Array[KeyframeTrackJSON] = js.native
  var uuid: String = js.native

object AnimationClipJSON:
  def apply(
    name: String,
    duration: Double,
    tracks: js.Array[KeyframeTrackJSON],
    uuid: String,
    blendMode: 2500 | 2501
  ): AnimationClipJSON =
    js.Dynamic.literal(
      "name" -> name.asInstanceOf[js.Any],
      "duration" -> duration.asInstanceOf[js.Any],
      "tracks" -> tracks.asInstanceOf[js.Any],
      "uuid" -> uuid.asInstanceOf[js.Any],
      "blendMode" -> blendMode.asInstanceOf[js.Any]
    ).asInstanceOf[AnimationClipJSON]
