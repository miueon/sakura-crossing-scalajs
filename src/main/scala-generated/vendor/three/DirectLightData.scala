// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait DirectLightData extends js.Object:
  var lightColor: NodeClass[NodeClassEventMap] & NodeElements & js.Any = js.native
  var lightDirection: NodeClass[NodeClassEventMap] & NodeElements & js.Any = js.native

object DirectLightData:
  def apply(
    lightDirection: NodeClass[NodeClassEventMap] & NodeElements & js.Any,
    lightColor: NodeClass[NodeClassEventMap] & NodeElements & js.Any
  ): DirectLightData =
    js.Dynamic.literal(
      "lightDirection" -> lightDirection.asInstanceOf[js.Any],
      "lightColor" -> lightColor.asInstanceOf[js.Any]
    ).asInstanceOf[DirectLightData]
