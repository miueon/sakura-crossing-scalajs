// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
trait VarNodeInterface[TNode] extends js.Object:
  var intent: Boolean = js.native
  def isVarNode: true = js.native
  var name: String | Null = js.native
  var node: TNode = js.native
  var readOnly: Boolean = js.native
  def getIntent(): Boolean = js.native
  @JSName("setIntent")
  protected def setIntent$raw(value: Boolean): VarNodeInterface[TNode] = js.native

object VarNodeInterface:
  def of[TNode](
    getIntent: js.Function0[Boolean],
    intent: Boolean,
    isVarNode: true,
    name: String | Null,
    node: TNode,
    readOnly: Boolean,
    setIntent: js.Function1[Boolean, VarNodeInterface[TNode]]
  ): VarNodeInterface[TNode] =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "getIntent" -> getIntent.asInstanceOf[js.Any],
      "intent" -> intent.asInstanceOf[js.Any],
      "isVarNode" -> isVarNode.asInstanceOf[js.Any],
      "name" -> name.asInstanceOf[js.Any],
      "node" -> node.asInstanceOf[js.Any],
      "readOnly" -> readOnly.asInstanceOf[js.Any],
      "setIntent" -> setIntent.asInstanceOf[js.Any]
    ).asInstanceOf[VarNodeInterface[TNode]]

  extension [TNode, Self <: VarNodeInterface[TNode]](self: Self)
    inline def setIntent(value: Boolean): Self = self.setIntent$raw(value).asInstanceOf[Self]
