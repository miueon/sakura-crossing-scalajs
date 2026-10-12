// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait ArrayElementNodeInterface[TNodeType] extends js.Object:
  var indexNode: NodeClass[NodeClassEventMap] & NodeElements & js.Any = js.native
  var node: NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any & TempNodeInterface & ArrayNodeInterface[TNodeType] = js.native

object ArrayElementNodeInterface:
  def apply[TNodeType](
    node: NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any & TempNodeInterface & ArrayNodeInterface[TNodeType],
    indexNode: NodeClass[NodeClassEventMap] & NodeElements & js.Any
  ): ArrayElementNodeInterface[TNodeType] =
    js.Dynamic.literal(
      "node" -> node.asInstanceOf[js.Any],
      "indexNode" -> indexNode.asInstanceOf[js.Any]
    ).asInstanceOf[ArrayElementNodeInterface[TNodeType]]
