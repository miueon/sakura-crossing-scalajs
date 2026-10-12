// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait XRPose extends js.Object:
  val angularVelocity: js.UndefOr[js.Object] = js.undefined
  val emulatedPosition: Boolean
  val linearVelocity: js.UndefOr[js.Object] = js.undefined
  val transform: XRRigidTransform

object XRPose:
  def apply(
    transform: XRRigidTransform,
    linearVelocity: Option[js.UndefOr[js.Object]] = None,
    angularVelocity: Option[js.UndefOr[js.Object]] = None,
    emulatedPosition: Boolean
  ): XRPose =
    val _res = js.Dynamic.literal("transform" -> transform.asInstanceOf[js.Any])
    linearVelocity.foreach(v => _res.updateDynamic("linearVelocity")(v.asInstanceOf[js.Any]))
    angularVelocity.foreach(v => _res.updateDynamic("angularVelocity")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("emulatedPosition")(emulatedPosition.asInstanceOf[js.Any])
    _res.asInstanceOf[XRPose]
