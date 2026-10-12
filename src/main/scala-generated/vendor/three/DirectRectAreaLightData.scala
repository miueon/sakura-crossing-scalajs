// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait DirectRectAreaLightData extends js.Object:
  var halfHeight: NodeClass[NodeClassEventMap] & NodeElements & js.Any = js.native
  var halfWidth: NodeClass[NodeClassEventMap] & NodeElements & js.Any = js.native
  var lightColor: NodeClass[NodeClassEventMap] & NodeElements & js.Any = js.native
  var lightPosition: NodeClass[NodeClassEventMap] & NodeElements & js.Any = js.native
  var ltc_1: NodeClass[NodeClassEventMap] & NodeElements & js.Any = js.native
  var ltc_2: NodeClass[NodeClassEventMap] & NodeElements & js.Any = js.native

object DirectRectAreaLightData:
  def apply(
    lightColor: NodeClass[NodeClassEventMap] & NodeElements & js.Any,
    lightPosition: NodeClass[NodeClassEventMap] & NodeElements & js.Any,
    halfWidth: NodeClass[NodeClassEventMap] & NodeElements & js.Any,
    halfHeight: NodeClass[NodeClassEventMap] & NodeElements & js.Any,
    ltc_1: NodeClass[NodeClassEventMap] & NodeElements & js.Any,
    ltc_2: NodeClass[NodeClassEventMap] & NodeElements & js.Any
  ): DirectRectAreaLightData =
    js.Dynamic.literal(
      "lightColor" -> lightColor.asInstanceOf[js.Any],
      "lightPosition" -> lightPosition.asInstanceOf[js.Any],
      "halfWidth" -> halfWidth.asInstanceOf[js.Any],
      "halfHeight" -> halfHeight.asInstanceOf[js.Any],
      "ltc_1" -> ltc_1.asInstanceOf[js.Any],
      "ltc_2" -> ltc_2.asInstanceOf[js.Any]
    ).asInstanceOf[DirectRectAreaLightData]
