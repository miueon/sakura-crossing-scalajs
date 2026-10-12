// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait CubicInterpolantSettings extends js.Object:
  var endingEnd: 2400 | 2401 | 2402 = js.native
  var endingStart: 2400 | 2401 | 2402 = js.native

object CubicInterpolantSettings:
  def apply(
    endingStart: 2400 | 2401 | 2402,
    endingEnd: 2400 | 2401 | 2402
  ): CubicInterpolantSettings =
    js.Dynamic.literal(
      "endingStart" -> endingStart.asInstanceOf[js.Any],
      "endingEnd" -> endingEnd.asInstanceOf[js.Any]
    ).asInstanceOf[CubicInterpolantSettings]
