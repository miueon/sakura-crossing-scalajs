// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRCylinderLayerInit extends XRLayerInit:
  var aspectRatio: js.UndefOr[Double] = js.native
  var centralAngle: js.UndefOr[Double] = js.native
  var radius: js.UndefOr[Double] = js.native
  var textureType: js.UndefOr["texture" | "texture-array"] = js.native
  var transform: XRRigidTransform = js.native

object XRCylinderLayerInit:
  def apply(
    textureType: Option[js.UndefOr["texture" | "texture-array"]] = None,
    transform: XRRigidTransform,
    radius: Option[js.UndefOr[Double]] = None,
    centralAngle: Option[js.UndefOr[Double]] = None,
    aspectRatio: Option[js.UndefOr[Double]] = None,
    mipLevels: Option[js.UndefOr[Double]] = None,
    viewPixelWidth: Double,
    viewPixelHeight: Double,
    isStatic: Option[js.UndefOr[Boolean]] = None,
    colorFormat: Option[js.UndefOr[Double]] = None,
    depthFormat: Option[js.UndefOr[Double]] = None,
    space: XRSpace,
    layout: Option[js.UndefOr["default" | "mono" | "stereo" | "stereo-left-right" | "stereo-top-bottom"]] = None
  ): XRCylinderLayerInit =
    val _res = js.Dynamic.literal()
    textureType.foreach(v => _res.updateDynamic("textureType")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("transform")(transform.asInstanceOf[js.Any])
    radius.foreach(v => _res.updateDynamic("radius")(v.asInstanceOf[js.Any]))
    centralAngle.foreach(v => _res.updateDynamic("centralAngle")(v.asInstanceOf[js.Any]))
    aspectRatio.foreach(v => _res.updateDynamic("aspectRatio")(v.asInstanceOf[js.Any]))
    mipLevels.foreach(v => _res.updateDynamic("mipLevels")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("viewPixelWidth")(viewPixelWidth.asInstanceOf[js.Any])
    _res.updateDynamic("viewPixelHeight")(viewPixelHeight.asInstanceOf[js.Any])
    isStatic.foreach(v => _res.updateDynamic("isStatic")(v.asInstanceOf[js.Any]))
    colorFormat.foreach(v => _res.updateDynamic("colorFormat")(v.asInstanceOf[js.Any]))
    depthFormat.foreach(v => _res.updateDynamic("depthFormat")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("space")(space.asInstanceOf[js.Any])
    layout.foreach(v => _res.updateDynamic("layout")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[XRCylinderLayerInit]
