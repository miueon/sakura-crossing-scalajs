// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRProjectionLayerInit extends js.Object:
  var colorFormat: js.UndefOr[Double] = js.native
  var depthFormat: js.UndefOr[Double] = js.native
  var scaleFactor: js.UndefOr[Double] = js.native
  var textureType: js.UndefOr["texture" | "texture-array"] = js.native

object XRProjectionLayerInit:
  def apply(
    scaleFactor: Option[js.UndefOr[Double]] = None,
    textureType: Option[js.UndefOr["texture" | "texture-array"]] = None,
    colorFormat: Option[js.UndefOr[Double]] = None,
    depthFormat: Option[js.UndefOr[Double]] = None
  ): XRProjectionLayerInit =
    val _res = js.Dynamic.literal()
    scaleFactor.foreach(v => _res.updateDynamic("scaleFactor")(v.asInstanceOf[js.Any]))
    textureType.foreach(v => _res.updateDynamic("textureType")(v.asInstanceOf[js.Any]))
    colorFormat.foreach(v => _res.updateDynamic("colorFormat")(v.asInstanceOf[js.Any]))
    depthFormat.foreach(v => _res.updateDynamic("depthFormat")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[XRProjectionLayerInit]
