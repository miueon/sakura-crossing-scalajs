// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait XRRenderState extends js.Object:
  val baseLayer: js.UndefOr[XRWebGLLayer] = js.undefined
  val depthFar: Double
  val depthNear: Double
  val inlineVerticalFieldOfView: js.UndefOr[Double] = js.undefined
  val layers: js.UndefOr[js.Array[XRLayer]] = js.undefined

object XRRenderState:
  def apply(
    baseLayer: Option[js.UndefOr[XRWebGLLayer]] = None,
    depthFar: Double,
    depthNear: Double,
    inlineVerticalFieldOfView: Option[js.UndefOr[Double]] = None,
    layers: Option[js.UndefOr[js.Array[XRLayer]]] = None
  ): XRRenderState =
    val _res = js.Dynamic.literal()
    baseLayer.foreach(v => _res.updateDynamic("baseLayer")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("depthFar")(depthFar.asInstanceOf[js.Any])
    _res.updateDynamic("depthNear")(depthNear.asInstanceOf[js.Any])
    inlineVerticalFieldOfView.foreach(v => _res.updateDynamic("inlineVerticalFieldOfView")(v.asInstanceOf[js.Any]))
    layers.foreach(v => _res.updateDynamic("layers")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[XRRenderState]
