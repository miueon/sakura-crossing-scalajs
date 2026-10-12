// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRCubeLayerInit extends XRLayerInit:
  var orientation: js.UndefOr[js.Object] = js.native

object XRCubeLayerInit:
  def apply(
    orientation: Option[js.UndefOr[js.Object]] = None,
    mipLevels: Option[js.UndefOr[Double]] = None,
    viewPixelWidth: Double,
    viewPixelHeight: Double,
    isStatic: Option[js.UndefOr[Boolean]] = None,
    colorFormat: Option[js.UndefOr[Double]] = None,
    depthFormat: Option[js.UndefOr[Double]] = None,
    space: XRSpace,
    layout: Option[js.UndefOr["default" | "mono" | "stereo" | "stereo-left-right" | "stereo-top-bottom"]] = None
  ): XRCubeLayerInit =
    val _res = js.Dynamic.literal()
    orientation.foreach(v => _res.updateDynamic("orientation")(v.asInstanceOf[js.Any]))
    mipLevels.foreach(v => _res.updateDynamic("mipLevels")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("viewPixelWidth")(viewPixelWidth.asInstanceOf[js.Any])
    _res.updateDynamic("viewPixelHeight")(viewPixelHeight.asInstanceOf[js.Any])
    isStatic.foreach(v => _res.updateDynamic("isStatic")(v.asInstanceOf[js.Any]))
    colorFormat.foreach(v => _res.updateDynamic("colorFormat")(v.asInstanceOf[js.Any]))
    depthFormat.foreach(v => _res.updateDynamic("depthFormat")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("space")(space.asInstanceOf[js.Any])
    layout.foreach(v => _res.updateDynamic("layout")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[XRCubeLayerInit]
