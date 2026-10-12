// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait XRDepthInformation extends js.Object:
  val height: Double
  val normDepthBufferFromNormView: XRRigidTransform
  val projectionMatrix: js.UndefOr[js.typedarray.Float32Array] = js.undefined
  val rawValueToMeters: Double
  val transform: js.UndefOr[XRRigidTransform] = js.undefined
  val width: Double

object XRDepthInformation:
  def apply(
    width: Double,
    height: Double,
    normDepthBufferFromNormView: XRRigidTransform,
    rawValueToMeters: Double,
    transform: Option[js.UndefOr[XRRigidTransform]] = None,
    projectionMatrix: Option[js.UndefOr[js.typedarray.Float32Array]] = None
  ): XRDepthInformation =
    val _res = js.Dynamic.literal(
      "width" -> width.asInstanceOf[js.Any],
      "height" -> height.asInstanceOf[js.Any],
      "normDepthBufferFromNormView" -> normDepthBufferFromNormView.asInstanceOf[js.Any],
      "rawValueToMeters" -> rawValueToMeters.asInstanceOf[js.Any]
    )
    transform.foreach(v => _res.updateDynamic("transform")(v.asInstanceOf[js.Any]))
    projectionMatrix.foreach(v => _res.updateDynamic("projectionMatrix")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[XRDepthInformation]
