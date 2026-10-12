// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait LightEventMap extends Object3DEventMap:
  var dispose: js.Any = js.native

object LightEventMap:
  def apply(
    dispose: js.Any,
    added: js.Any,
    removed: js.Any,
    childadded: AmbientLightAddEventListenerChildadded,
    childremoved: AmbientLightAddEventListenerChildadded
  ): LightEventMap =
    js.Dynamic.literal(
      "dispose" -> dispose.asInstanceOf[js.Any],
      "added" -> added.asInstanceOf[js.Any],
      "removed" -> removed.asInstanceOf[js.Any],
      "childadded" -> childadded.asInstanceOf[js.Any],
      "childremoved" -> childremoved.asInstanceOf[js.Any]
    ).asInstanceOf[LightEventMap]
