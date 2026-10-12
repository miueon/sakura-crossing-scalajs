// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRHitTestSource extends js.Object:
  def cancel(): Unit = js.native

object XRHitTestSource:
  def of(
    cancel: js.Function0[Unit]
  ): XRHitTestSource =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal("cancel" -> cancel.asInstanceOf[js.Any]).asInstanceOf[XRHitTestSource]
