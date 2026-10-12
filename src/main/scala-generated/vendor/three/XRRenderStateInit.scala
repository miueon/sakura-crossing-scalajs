// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRRenderStateInit extends js.Object:
  var baseLayer: js.UndefOr[XRWebGLLayer] = js.native
  var depthFar: js.UndefOr[Double] = js.native
  var depthNear: js.UndefOr[Double] = js.native
  var inlineVerticalFieldOfView: js.UndefOr[Double] = js.native
  var layers: js.UndefOr[js.Array[XRLayer]] = js.native

object XRRenderStateInit:
  def apply(
    baseLayer: Option[js.UndefOr[XRWebGLLayer]] = None,
    depthFar: Option[js.UndefOr[Double]] = None,
    depthNear: Option[js.UndefOr[Double]] = None,
    inlineVerticalFieldOfView: Option[js.UndefOr[Double]] = None,
    layers: Option[js.UndefOr[js.Array[XRLayer]]] = None
  ): XRRenderStateInit =
    val _res = js.Dynamic.literal()
    baseLayer.foreach(v => _res.updateDynamic("baseLayer")(v.asInstanceOf[js.Any]))
    depthFar.foreach(v => _res.updateDynamic("depthFar")(v.asInstanceOf[js.Any]))
    depthNear.foreach(v => _res.updateDynamic("depthNear")(v.asInstanceOf[js.Any]))
    inlineVerticalFieldOfView.foreach(v => _res.updateDynamic("inlineVerticalFieldOfView")(v.asInstanceOf[js.Any]))
    layers.foreach(v => _res.updateDynamic("layers")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[XRRenderStateInit]
