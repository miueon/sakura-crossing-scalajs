// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRViewerPose extends XRPose:
  def views: facadecontract.std.JSReadonlyArray[XRView] = js.native

object XRViewerPose:
  def apply(
    views: facadecontract.std.JSReadonlyArray[XRView],
    transform: XRRigidTransform,
    linearVelocity: Option[js.UndefOr[js.Object]] = None,
    angularVelocity: Option[js.UndefOr[js.Object]] = None,
    emulatedPosition: Boolean
  ): XRViewerPose =
    val _res = js.Dynamic.literal(
      "views" -> views.asInstanceOf[js.Any],
      "transform" -> transform.asInstanceOf[js.Any]
    )
    linearVelocity.foreach(v => _res.updateDynamic("linearVelocity")(v.asInstanceOf[js.Any]))
    angularVelocity.foreach(v => _res.updateDynamic("angularVelocity")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("emulatedPosition")(emulatedPosition.asInstanceOf[js.Any])
    _res.asInstanceOf[XRViewerPose]
