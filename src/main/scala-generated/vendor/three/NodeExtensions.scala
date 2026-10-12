// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait NodeExtensions[TNodeType] extends js.Object:
  def builtinAOContext(aoValue: NodeClass[NodeClassEventMap] & NodeElements & js.Any): ContextNodeInterface[TNodeType] & NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any = js.native
  def builtinShadowContext(shadowNode: NodeClass[NodeClassEventMap] & NodeElements & js.Any, light: Light): ContextNodeInterface[TNodeType] & NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any = js.native
  def context(context: js.UndefOr[js.Any] = js.undefined): ContextNodeInterface[TNodeType] & NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any = js.native
  def label(label: String): NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any = js.native
  def setName(label: String): NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any = js.native
  def toArray(count: Double): NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any & TempNodeInterface & ArrayNodeInterface[TNodeType] = js.native
  def toConst(name: js.UndefOr[String | Null] = js.undefined): NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any & VarNodeInterface[NodeExtensions[TNodeType]] = js.native
  def toVar(name: js.UndefOr[String | Null] = js.undefined): NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any & VarNodeInterface[NodeExtensions[TNodeType]] = js.native
  def toVarIntent(): NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any & VarNodeInterface[NodeExtensions[TNodeType]] = js.native
  def toVarying(name: js.UndefOr[String] = js.undefined): VaryingNodeInterface & NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any = js.native
  def toVertexStage(): VaryingNodeInterface & NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any = js.native
  def uniformFlow(): ContextNodeInterface[TNodeType] & NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any = js.native

object NodeExtensions:
  def of[TNodeType](
    builtinAOContext: js.Function1[NodeClass[NodeClassEventMap] & NodeElements & js.Any, ContextNodeInterface[TNodeType] & NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any],
    builtinShadowContext: js.Function2[NodeClass[NodeClassEventMap] & NodeElements & js.Any, Light, ContextNodeInterface[TNodeType] & NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any],
    context: js.Function1[js.UndefOr[js.Any], ContextNodeInterface[TNodeType] & NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any],
    label: js.Function1[String, NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any],
    setName: js.Function1[String, NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any],
    toArray: js.Function1[Double, NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any & TempNodeInterface & ArrayNodeInterface[TNodeType]],
    toConst: js.Function1[js.UndefOr[String | Null], NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any & VarNodeInterface[NodeExtensions[TNodeType]]],
    toVar: js.Function1[js.UndefOr[String | Null], NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any & VarNodeInterface[NodeExtensions[TNodeType]]],
    toVarIntent: js.Function0[NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any & VarNodeInterface[NodeExtensions[TNodeType]]],
    toVarying: js.Function1[js.UndefOr[String], VaryingNodeInterface & NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any],
    toVertexStage: js.Function0[VaryingNodeInterface & NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any],
    uniformFlow: js.Function0[ContextNodeInterface[TNodeType] & NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any]
  ): NodeExtensions[TNodeType] =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "builtinAOContext" -> builtinAOContext.asInstanceOf[js.Any],
      "builtinShadowContext" -> builtinShadowContext.asInstanceOf[js.Any],
      "context" -> context.asInstanceOf[js.Any],
      "label" -> label.asInstanceOf[js.Any],
      "setName" -> setName.asInstanceOf[js.Any],
      "toArray" -> toArray.asInstanceOf[js.Any],
      "toConst" -> toConst.asInstanceOf[js.Any],
      "toVar" -> toVar.asInstanceOf[js.Any],
      "toVarIntent" -> toVarIntent.asInstanceOf[js.Any],
      "toVarying" -> toVarying.asInstanceOf[js.Any],
      "toVertexStage" -> toVertexStage.asInstanceOf[js.Any],
      "uniformFlow" -> uniformFlow.asInstanceOf[js.Any]
    ).asInstanceOf[NodeExtensions[TNodeType]]
