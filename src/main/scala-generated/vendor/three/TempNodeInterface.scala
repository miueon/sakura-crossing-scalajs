// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait TempNodeInterface extends js.Object:
  var isTempNode: true = js.native
  def hasDependencies(builder: default_b44adefb): Boolean = js.native

object TempNodeInterface:
  def of(
    hasDependencies: js.Function1[default_b44adefb, Boolean],
    isTempNode: true
  ): TempNodeInterface =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "hasDependencies" -> hasDependencies.asInstanceOf[js.Any],
      "isTempNode" -> isTempNode.asInstanceOf[js.Any]
    ).asInstanceOf[TempNodeInterface]
