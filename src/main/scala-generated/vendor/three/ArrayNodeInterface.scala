// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait ArrayNodeInterface[TNodeType] extends js.Object:
  var count: Double = js.native
  def isArrayNode: true = js.native
  var values: js.Array[NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any] | Null = js.native
  def element(indexNode: Double | NodeClass[NodeClassEventMap] & NodeElements & js.Any): NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any & ArrayElementNodeInterface[TNodeType] = js.native

object ArrayNodeInterface:
  def of[TNodeType](
    count: Double,
    element: js.Function1[Double | NodeClass[NodeClassEventMap] & NodeElements & js.Any, NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any & ArrayElementNodeInterface[TNodeType]],
    isArrayNode: true,
    values: js.Array[NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any] | Null
  ): ArrayNodeInterface[TNodeType] =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "count" -> count.asInstanceOf[js.Any],
      "element" -> element.asInstanceOf[js.Any],
      "isArrayNode" -> isArrayNode.asInstanceOf[js.Any],
      "values" -> values.asInstanceOf[js.Any]
    ).asInstanceOf[ArrayNodeInterface[TNodeType]]
