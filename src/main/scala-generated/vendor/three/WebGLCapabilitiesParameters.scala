// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait WebGLCapabilitiesParameters extends js.Object:
  var logarithmicDepthBuffer: js.UndefOr[Boolean] = js.native
  var precision: js.UndefOr[String] = js.native
  var reversedDepthBuffer: js.UndefOr[Boolean] = js.native

object WebGLCapabilitiesParameters:
  def apply(
    precision: Option[js.UndefOr[String]] = None,
    logarithmicDepthBuffer: Option[js.UndefOr[Boolean]] = None,
    reversedDepthBuffer: Option[js.UndefOr[Boolean]] = None
  ): WebGLCapabilitiesParameters =
    val _res = js.Dynamic.literal()
    precision.foreach(v => _res.updateDynamic("precision")(v.asInstanceOf[js.Any]))
    logarithmicDepthBuffer.foreach(v => _res.updateDynamic("logarithmicDepthBuffer")(v.asInstanceOf[js.Any]))
    reversedDepthBuffer.foreach(v => _res.updateDynamic("reversedDepthBuffer")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[WebGLCapabilitiesParameters]
