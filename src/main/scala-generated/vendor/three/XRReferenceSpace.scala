// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRReferenceSpace extends XRSpace:
  def getOffsetReferenceSpace(originOffset: XRRigidTransform): XRReferenceSpace = js.native
  def onreset(event: XRReferenceSpaceEvent): js.Any = js.native

object XRReferenceSpace:
  def of(
    getOffsetReferenceSpace: js.Function1[XRRigidTransform, XRReferenceSpace],
    onreset: js.Function1[XRReferenceSpaceEvent, js.Any]
  ): XRReferenceSpace =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "getOffsetReferenceSpace" -> getOffsetReferenceSpace.asInstanceOf[js.Any],
      "onreset" -> onreset.asInstanceOf[js.Any]
    ).asInstanceOf[XRReferenceSpace]
