// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait EventListener[TEventData, TEventType <: String, TTarget] extends js.Object:
  def apply(event: TEventData & Event[TEventType, TTarget]): Unit = js.native
// #WARN: SHIM-065: Native facade trait 'EventListener' cannot receive a builder companion: trait declares call signatures; call-signature traits require function objects at runtime
