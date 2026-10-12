// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait XRViewport extends js.Object:
  val height: Double
  val width: Double
  val x: Double
  val y: Double

object XRViewport:
  def apply(
    x: Double,
    y: Double,
    width: Double,
    height: Double
  ): XRViewport =
    js.Dynamic.literal(
      "x" -> x.asInstanceOf[js.Any],
      "y" -> y.asInstanceOf[js.Any],
      "width" -> width.asInstanceOf[js.Any],
      "height" -> height.asInstanceOf[js.Any]
    ).asInstanceOf[XRViewport]
