// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRCPUDepthInformation extends XRDepthInformation:
  def data: js.typedarray.ArrayBuffer = js.native
  def getDepthInMeters(x: Double, y: Double): Double = js.native

object XRCPUDepthInformation:
  def of(
    data: js.typedarray.ArrayBuffer,
    getDepthInMeters: js.Function2[Double, Double, Double],
    height: Double,
    normDepthBufferFromNormView: XRRigidTransform,
    rawValueToMeters: Double,
    width: Double,
    projectionMatrix: Option[js.UndefOr[js.typedarray.Float32Array]] = None,
    transform: Option[js.UndefOr[XRRigidTransform]] = None
  ): XRCPUDepthInformation =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    val _res = js.Dynamic.literal(
      "data" -> data.asInstanceOf[js.Any],
      "getDepthInMeters" -> getDepthInMeters.asInstanceOf[js.Any],
      "height" -> height.asInstanceOf[js.Any],
      "normDepthBufferFromNormView" -> normDepthBufferFromNormView.asInstanceOf[js.Any],
      "rawValueToMeters" -> rawValueToMeters.asInstanceOf[js.Any],
      "width" -> width.asInstanceOf[js.Any]
    )
    projectionMatrix.foreach(v => _res.updateDynamic("projectionMatrix")(v.asInstanceOf[js.Any]))
    transform.foreach(v => _res.updateDynamic("transform")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[XRCPUDepthInformation]
