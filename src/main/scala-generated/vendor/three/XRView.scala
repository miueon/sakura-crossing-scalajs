// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRView extends js.Object:
  def eye: "left" | "none" | "right" = js.native
  def projectionMatrix: js.typedarray.Float32Array = js.native
  def recommendedViewportScale: js.UndefOr[Double] = js.native
  def transform: XRRigidTransform = js.native
  def requestViewportScale(scale: Double): Unit = js.native

object XRView:
  def of(
    eye: "left" | "none" | "right",
    projectionMatrix: js.typedarray.Float32Array,
    requestViewportScale: js.Function1[Double, Unit],
    transform: XRRigidTransform,
    recommendedViewportScale: Option[js.UndefOr[Double]] = None
  ): XRView =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    val _res = js.Dynamic.literal(
      "eye" -> eye.asInstanceOf[js.Any],
      "projectionMatrix" -> projectionMatrix.asInstanceOf[js.Any],
      "requestViewportScale" -> requestViewportScale.asInstanceOf[js.Any],
      "transform" -> transform.asInstanceOf[js.Any]
    )
    recommendedViewportScale.foreach(v => _res.updateDynamic("recommendedViewportScale")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[XRView]
