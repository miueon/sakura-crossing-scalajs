// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait RendererShadowMap extends js.Object:
  var enabled: Boolean = js.native
  var transmitted: Boolean = js.native
  var `type`: 0 | 1 | 2 | 3 = js.native

object RendererShadowMap:
  def apply(
    enabled: Boolean,
    transmitted: Boolean,
    `type`: 0 | 1 | 2 | 3
  ): RendererShadowMap =
    js.Dynamic.literal(
      "enabled" -> enabled.asInstanceOf[js.Any],
      "transmitted" -> transmitted.asInstanceOf[js.Any],
      "type" -> `type`.asInstanceOf[js.Any]
    ).asInstanceOf[RendererShadowMap]
