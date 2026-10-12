// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait Object3DEventMap extends js.Object:
  var added: js.Any = js.native
  var childadded: AmbientLightAddEventListenerChildadded = js.native
  var childremoved: AmbientLightAddEventListenerChildadded = js.native
  var removed: js.Any = js.native

object Object3DEventMap:
  def apply(
    added: js.Any,
    removed: js.Any,
    childadded: AmbientLightAddEventListenerChildadded,
    childremoved: AmbientLightAddEventListenerChildadded
  ): Object3DEventMap =
    js.Dynamic.literal(
      "added" -> added.asInstanceOf[js.Any],
      "removed" -> removed.asInstanceOf[js.Any],
      "childadded" -> childadded.asInstanceOf[js.Any],
      "childremoved" -> childremoved.asInstanceOf[js.Any]
    ).asInstanceOf[Object3DEventMap]
