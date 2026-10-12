// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait WebGLRendererParameters extends WebGLCapabilitiesParameters:
  var alpha: js.UndefOr[Boolean] = js.native
  var antialias: js.UndefOr[Boolean] = js.native
  var canvas: js.UndefOr[OffscreenCanvas | org.scalajs.dom.HTMLCanvasElement] = js.native
  var context: js.UndefOr[js.Object] = js.native
  var depth: js.UndefOr[Boolean] = js.native
  var failIfMajorPerformanceCaveat: js.UndefOr[Boolean] = js.native
  var outputBufferType: js.UndefOr[1009 | 1010 | 1011 | 1012 | 1013 | 1014 | 1015 | 1016 | 1017 | 1018 | 1020 | 35899 | 35902] = js.native
  var powerPreference: js.UndefOr["default" | "high-performance" | "low-power"] = js.native
  var premultipliedAlpha: js.UndefOr[Boolean] = js.native
  var preserveDrawingBuffer: js.UndefOr[Boolean] = js.native
  var stencil: js.UndefOr[Boolean] = js.native

object WebGLRendererParameters:
  def apply(
    canvas: Option[js.UndefOr[OffscreenCanvas | org.scalajs.dom.HTMLCanvasElement]] = None,
    context: Option[js.UndefOr[js.Object]] = None,
    alpha: Option[js.UndefOr[Boolean]] = None,
    premultipliedAlpha: Option[js.UndefOr[Boolean]] = None,
    antialias: Option[js.UndefOr[Boolean]] = None,
    stencil: Option[js.UndefOr[Boolean]] = None,
    preserveDrawingBuffer: Option[js.UndefOr[Boolean]] = None,
    powerPreference: Option[js.UndefOr["default" | "high-performance" | "low-power"]] = None,
    depth: Option[js.UndefOr[Boolean]] = None,
    failIfMajorPerformanceCaveat: Option[js.UndefOr[Boolean]] = None,
    outputBufferType: Option[js.UndefOr[1009 | 1010 | 1011 | 1012 | 1013 | 1014 | 1015 | 1016 | 1017 | 1018 | 1020 | 35899 | 35902]] = None,
    precision: Option[js.UndefOr[String]] = None,
    logarithmicDepthBuffer: Option[js.UndefOr[Boolean]] = None,
    reversedDepthBuffer: Option[js.UndefOr[Boolean]] = None
  ): WebGLRendererParameters =
    val _res = js.Dynamic.literal()
    canvas.foreach(v => _res.updateDynamic("canvas")(v.asInstanceOf[js.Any]))
    context.foreach(v => _res.updateDynamic("context")(v.asInstanceOf[js.Any]))
    alpha.foreach(v => _res.updateDynamic("alpha")(v.asInstanceOf[js.Any]))
    premultipliedAlpha.foreach(v => _res.updateDynamic("premultipliedAlpha")(v.asInstanceOf[js.Any]))
    antialias.foreach(v => _res.updateDynamic("antialias")(v.asInstanceOf[js.Any]))
    stencil.foreach(v => _res.updateDynamic("stencil")(v.asInstanceOf[js.Any]))
    preserveDrawingBuffer.foreach(v => _res.updateDynamic("preserveDrawingBuffer")(v.asInstanceOf[js.Any]))
    powerPreference.foreach(v => _res.updateDynamic("powerPreference")(v.asInstanceOf[js.Any]))
    depth.foreach(v => _res.updateDynamic("depth")(v.asInstanceOf[js.Any]))
    failIfMajorPerformanceCaveat.foreach(v => _res.updateDynamic("failIfMajorPerformanceCaveat")(v.asInstanceOf[js.Any]))
    outputBufferType.foreach(v => _res.updateDynamic("outputBufferType")(v.asInstanceOf[js.Any]))
    precision.foreach(v => _res.updateDynamic("precision")(v.asInstanceOf[js.Any]))
    logarithmicDepthBuffer.foreach(v => _res.updateDynamic("logarithmicDepthBuffer")(v.asInstanceOf[js.Any]))
    reversedDepthBuffer.foreach(v => _res.updateDynamic("reversedDepthBuffer")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[WebGLRendererParameters]
