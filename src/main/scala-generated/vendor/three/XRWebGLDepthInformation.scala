// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRWebGLDepthInformation extends XRDepthInformation:
  def imageIndex: js.UndefOr[Double | Null] = js.native
  def texture: js.Object = js.native
  def textureType: "texture" | "texture-array" = js.native

object XRWebGLDepthInformation:
  def apply(
    texture: js.Object,
    textureType: "texture" | "texture-array",
    imageIndex: Option[js.UndefOr[Double | Null]] = None,
    width: Double,
    height: Double,
    normDepthBufferFromNormView: XRRigidTransform,
    rawValueToMeters: Double,
    transform: Option[js.UndefOr[XRRigidTransform]] = None,
    projectionMatrix: Option[js.UndefOr[js.typedarray.Float32Array]] = None
  ): XRWebGLDepthInformation =
    val _res = js.Dynamic.literal(
      "texture" -> texture.asInstanceOf[js.Any],
      "textureType" -> textureType.asInstanceOf[js.Any]
    )
    imageIndex.foreach(v => _res.updateDynamic("imageIndex")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("width")(width.asInstanceOf[js.Any])
    _res.updateDynamic("height")(height.asInstanceOf[js.Any])
    _res.updateDynamic("normDepthBufferFromNormView")(normDepthBufferFromNormView.asInstanceOf[js.Any])
    _res.updateDynamic("rawValueToMeters")(rawValueToMeters.asInstanceOf[js.Any])
    transform.foreach(v => _res.updateDynamic("transform")(v.asInstanceOf[js.Any]))
    projectionMatrix.foreach(v => _res.updateDynamic("projectionMatrix")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[XRWebGLDepthInformation]
