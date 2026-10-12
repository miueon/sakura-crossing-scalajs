// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait CanvasTargetEventMap extends js.Object:
  var dispose: js.Any = js.native
  var resize: js.Any = js.native

object CanvasTargetEventMap:
  def apply(
    resize: js.Any,
    dispose: js.Any
  ): CanvasTargetEventMap =
    js.Dynamic.literal(
      "resize" -> resize.asInstanceOf[js.Any],
      "dispose" -> dispose.asInstanceOf[js.Any]
    ).asInstanceOf[CanvasTargetEventMap]
