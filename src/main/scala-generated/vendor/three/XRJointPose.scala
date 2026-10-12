// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRJointPose extends XRPose:
  def radius: js.UndefOr[Double] = js.native

object XRJointPose:
  def apply(
    radius: js.UndefOr[Double],
    transform: XRRigidTransform,
    linearVelocity: Option[js.UndefOr[js.Object]] = None,
    angularVelocity: Option[js.UndefOr[js.Object]] = None,
    emulatedPosition: Boolean
  ): XRJointPose =
    val _res = js.Dynamic.literal(
      "radius" -> radius.asInstanceOf[js.Any],
      "transform" -> transform.asInstanceOf[js.Any]
    )
    linearVelocity.foreach(v => _res.updateDynamic("linearVelocity")(v.asInstanceOf[js.Any]))
    angularVelocity.foreach(v => _res.updateDynamic("angularVelocity")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("emulatedPosition")(emulatedPosition.asInstanceOf[js.Any])
    _res.asInstanceOf[XRJointPose]
