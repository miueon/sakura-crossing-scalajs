// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait Event[TEventType <: String, TTarget] extends js.Object:
  val target: TTarget
  val `type`: TEventType

object Event:
  def apply[TEventType <: String, TTarget](
    `type`: TEventType,
    target: TTarget
  ): Event[TEventType, TTarget] =
    js.Dynamic.literal(
      "type" -> `type`.asInstanceOf[js.Any],
      "target" -> target.asInstanceOf[js.Any]
    ).asInstanceOf[Event[TEventType, TTarget]]
