// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait ReadbackBufferEventMap extends js.Object:
  var dispose: js.Any = js.native
  var release: js.Any = js.native

object ReadbackBufferEventMap:
  def apply(
    release: js.Any,
    dispose: js.Any
  ): ReadbackBufferEventMap =
    js.Dynamic.literal(
      "release" -> release.asInstanceOf[js.Any],
      "dispose" -> dispose.asInstanceOf[js.Any]
    ).asInstanceOf[ReadbackBufferEventMap]
