// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRLayerInit extends js.Object:
  var colorFormat: js.UndefOr[Double] = js.native
  var depthFormat: js.UndefOr[Double] = js.native
  var isStatic: js.UndefOr[Boolean] = js.native
  var layout: js.UndefOr["default" | "mono" | "stereo" | "stereo-left-right" | "stereo-top-bottom"] = js.native
  var mipLevels: js.UndefOr[Double] = js.native
  var space: XRSpace = js.native
  var viewPixelHeight: Double = js.native
  var viewPixelWidth: Double = js.native

object XRLayerInit:
  def apply(
    mipLevels: Option[js.UndefOr[Double]] = None,
    viewPixelWidth: Double,
    viewPixelHeight: Double,
    isStatic: Option[js.UndefOr[Boolean]] = None,
    colorFormat: Option[js.UndefOr[Double]] = None,
    depthFormat: Option[js.UndefOr[Double]] = None,
    space: XRSpace,
    layout: Option[js.UndefOr["default" | "mono" | "stereo" | "stereo-left-right" | "stereo-top-bottom"]] = None
  ): XRLayerInit =
    val _res = js.Dynamic.literal()
    mipLevels.foreach(v => _res.updateDynamic("mipLevels")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("viewPixelWidth")(viewPixelWidth.asInstanceOf[js.Any])
    _res.updateDynamic("viewPixelHeight")(viewPixelHeight.asInstanceOf[js.Any])
    isStatic.foreach(v => _res.updateDynamic("isStatic")(v.asInstanceOf[js.Any]))
    colorFormat.foreach(v => _res.updateDynamic("colorFormat")(v.asInstanceOf[js.Any]))
    depthFormat.foreach(v => _res.updateDynamic("depthFormat")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("space")(space.asInstanceOf[js.Any])
    layout.foreach(v => _res.updateDynamic("layout")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[XRLayerInit]
