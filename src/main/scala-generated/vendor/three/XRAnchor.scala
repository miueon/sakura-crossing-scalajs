// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRAnchor extends js.Object:
  def anchorSpace: XRSpace = js.native
  def delete(): Unit = js.native
  def requestPersistentHandle: js.UndefOr[js.Function0[js.Promise[String]]] = js.native

object XRAnchor:
  def of(
    anchorSpace: XRSpace,
    delete: js.Function0[Unit],
    requestPersistentHandle: js.UndefOr[js.Function0[js.Promise[String]]] = js.undefined
  ): XRAnchor =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    val _res = js.Dynamic.literal(
      "anchorSpace" -> anchorSpace.asInstanceOf[js.Any],
      "delete" -> delete.asInstanceOf[js.Any]
    )
    requestPersistentHandle.foreach(v => _res.updateDynamic("requestPersistentHandle")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[XRAnchor]
