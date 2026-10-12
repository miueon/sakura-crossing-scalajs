// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRBoundedReferenceSpace extends XRReferenceSpace:
  def boundsGeometry: js.Array[js.Object] = js.native

object XRBoundedReferenceSpace:
  def of(
    boundsGeometry: js.Array[js.Object],
    getOffsetReferenceSpace: js.Function1[XRRigidTransform, XRReferenceSpace],
    onreset: js.Function1[XRReferenceSpaceEvent, js.Any]
  ): XRBoundedReferenceSpace =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "boundsGeometry" -> boundsGeometry.asInstanceOf[js.Any],
      "getOffsetReferenceSpace" -> getOffsetReferenceSpace.asInstanceOf[js.Any],
      "onreset" -> onreset.asInstanceOf[js.Any]
    ).asInstanceOf[XRBoundedReferenceSpace]
