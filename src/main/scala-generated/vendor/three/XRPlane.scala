// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRPlane extends js.Object:
  var lastChangedTime: Double = js.native
  var orientation: "horizontal" | "vertical" = js.native
  var planeSpace: XRSpace = js.native
  var polygon: js.Array[js.Object] = js.native
  var semanticLabel: js.UndefOr[String] = js.native

object XRPlane:
  def apply(
    orientation: "horizontal" | "vertical",
    planeSpace: XRSpace,
    polygon: js.Array[js.Object],
    lastChangedTime: Double,
    semanticLabel: js.UndefOr[String] = js.undefined
  ): XRPlane =
    val _res = js.Dynamic.literal(
      "orientation" -> orientation.asInstanceOf[js.Any],
      "planeSpace" -> planeSpace.asInstanceOf[js.Any],
      "polygon" -> polygon.asInstanceOf[js.Any],
      "lastChangedTime" -> lastChangedTime.asInstanceOf[js.Any]
    )
    semanticLabel.foreach(v => _res.updateDynamic("semanticLabel")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[XRPlane]
