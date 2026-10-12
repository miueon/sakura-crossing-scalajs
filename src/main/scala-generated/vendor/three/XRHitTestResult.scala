// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRHitTestResult extends js.Object:
  def createAnchor: js.UndefOr[js.Function0[js.Promise[XRAnchor]]] = js.native
  def getPose(baseSpace: XRSpace): js.UndefOr[XRPose] = js.native

object XRHitTestResult:
  def of(
    getPose: js.Function1[XRSpace, js.UndefOr[XRPose]],
    createAnchor: js.UndefOr[js.Function0[js.Promise[XRAnchor]]] = js.undefined
  ): XRHitTestResult =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    val _res = js.Dynamic.literal("getPose" -> getPose.asInstanceOf[js.Any])
    createAnchor.foreach(v => _res.updateDynamic("createAnchor")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[XRHitTestResult]
