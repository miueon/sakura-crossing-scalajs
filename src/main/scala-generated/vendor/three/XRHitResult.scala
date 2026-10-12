// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRHitResult extends js.Object:
  var hitMatrix: js.typedarray.Float32Array = js.native

object XRHitResult:
  def apply(
    hitMatrix: js.typedarray.Float32Array
  ): XRHitResult =
    js.Dynamic.literal("hitMatrix" -> hitMatrix.asInstanceOf[js.Any]).asInstanceOf[XRHitResult]
