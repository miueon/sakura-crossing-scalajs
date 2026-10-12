// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait XRTransientInputHitTestResult extends js.Object:
  val inputSource: XRInputSource
  val results: facadecontract.std.JSReadonlyArray[XRHitTestResult]

object XRTransientInputHitTestResult:
  def apply(
    inputSource: XRInputSource,
    results: facadecontract.std.JSReadonlyArray[XRHitTestResult]
  ): XRTransientInputHitTestResult =
    js.Dynamic.literal(
      "inputSource" -> inputSource.asInstanceOf[js.Any],
      "results" -> results.asInstanceOf[js.Any]
    ).asInstanceOf[XRTransientInputHitTestResult]
