// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XREquirectLayerInit extends XRLayerInit:
  var centralHorizontalAngle: js.UndefOr[Double] = js.native
  var lowerVerticalAngle: js.UndefOr[Double] = js.native
  var radius: js.UndefOr[Double] = js.native
  var textureType: js.UndefOr["texture" | "texture-array"] = js.native
  var transform: js.UndefOr[XRRigidTransform] = js.native
  var upperVerticalAngle: js.UndefOr[Double] = js.native

object XREquirectLayerInit:
  def apply(
    textureType: Option[js.UndefOr["texture" | "texture-array"]] = None,
    transform: Option[js.UndefOr[XRRigidTransform]] = None,
    radius: Option[js.UndefOr[Double]] = None,
    centralHorizontalAngle: Option[js.UndefOr[Double]] = None,
    upperVerticalAngle: Option[js.UndefOr[Double]] = None,
    lowerVerticalAngle: Option[js.UndefOr[Double]] = None,
    mipLevels: Option[js.UndefOr[Double]] = None,
    viewPixelWidth: Double,
    viewPixelHeight: Double,
    isStatic: Option[js.UndefOr[Boolean]] = None,
    colorFormat: Option[js.UndefOr[Double]] = None,
    depthFormat: Option[js.UndefOr[Double]] = None,
    space: XRSpace,
    layout: Option[js.UndefOr["default" | "mono" | "stereo" | "stereo-left-right" | "stereo-top-bottom"]] = None
  ): XREquirectLayerInit =
    val _res = js.Dynamic.literal()
    textureType.foreach(v => _res.updateDynamic("textureType")(v.asInstanceOf[js.Any]))
    transform.foreach(v => _res.updateDynamic("transform")(v.asInstanceOf[js.Any]))
    radius.foreach(v => _res.updateDynamic("radius")(v.asInstanceOf[js.Any]))
    centralHorizontalAngle.foreach(v => _res.updateDynamic("centralHorizontalAngle")(v.asInstanceOf[js.Any]))
    upperVerticalAngle.foreach(v => _res.updateDynamic("upperVerticalAngle")(v.asInstanceOf[js.Any]))
    lowerVerticalAngle.foreach(v => _res.updateDynamic("lowerVerticalAngle")(v.asInstanceOf[js.Any]))
    mipLevels.foreach(v => _res.updateDynamic("mipLevels")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("viewPixelWidth")(viewPixelWidth.asInstanceOf[js.Any])
    _res.updateDynamic("viewPixelHeight")(viewPixelHeight.asInstanceOf[js.Any])
    isStatic.foreach(v => _res.updateDynamic("isStatic")(v.asInstanceOf[js.Any]))
    colorFormat.foreach(v => _res.updateDynamic("colorFormat")(v.asInstanceOf[js.Any]))
    depthFormat.foreach(v => _res.updateDynamic("depthFormat")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("space")(space.asInstanceOf[js.Any])
    layout.foreach(v => _res.updateDynamic("layout")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[XREquirectLayerInit]
