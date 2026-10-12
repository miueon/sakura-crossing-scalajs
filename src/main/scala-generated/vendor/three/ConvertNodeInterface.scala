// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait ConvertNodeInterface extends js.Object:
  var convertTo: String = js.native
  var node: NodeClass[NodeClassEventMap] & NodeElements & js.Any = js.native

object ConvertNodeInterface:
  def apply(
    node: NodeClass[NodeClassEventMap] & NodeElements & js.Any,
    convertTo: String
  ): ConvertNodeInterface =
    js.Dynamic.literal(
      "node" -> node.asInstanceOf[js.Any],
      "convertTo" -> convertTo.asInstanceOf[js.Any]
    ).asInstanceOf[ConvertNodeInterface]
