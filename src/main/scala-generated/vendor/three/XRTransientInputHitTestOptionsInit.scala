// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRTransientInputHitTestOptionsInit extends js.Object:
  var entityTypes: js.UndefOr[js.Array["mesh" | "plane" | "point"]] = js.native
  var offsetRay: js.UndefOr[XRRay] = js.native
  var profile: String = js.native

object XRTransientInputHitTestOptionsInit:
  def apply(
    profile: String,
    entityTypes: Option[js.UndefOr[js.Array["mesh" | "plane" | "point"]]] = None,
    offsetRay: Option[js.UndefOr[XRRay]] = None
  ): XRTransientInputHitTestOptionsInit =
    val _res = js.Dynamic.literal("profile" -> profile.asInstanceOf[js.Any])
    entityTypes.foreach(v => _res.updateDynamic("entityTypes")(v.asInstanceOf[js.Any]))
    offsetRay.foreach(v => _res.updateDynamic("offsetRay")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[XRTransientInputHitTestOptionsInit]
