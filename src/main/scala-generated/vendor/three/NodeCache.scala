// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait NodeCache extends js.Object:
  def id: Double = js.native
  var nodesData: js.Object = js.native
  var parent: NodeCache | Null = js.native
  def getData(node: NodeClass[NodeClassEventMap] & NodeElements & js.Any): facadecontract.std.JSUnknown = js.native
  def setData(node: NodeClass[NodeClassEventMap] & NodeElements & js.Any, data: js.Any): Unit = js.native

object NodeCache:
  def of(
    getData: js.Function1[NodeClass[NodeClassEventMap] & NodeElements & js.Any, facadecontract.std.JSUnknown],
    id: Double,
    nodesData: js.Object,
    parent: NodeCache | Null,
    setData: js.Function2[NodeClass[NodeClassEventMap] & NodeElements & js.Any, js.Any, Unit]
  ): NodeCache =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "getData" -> getData.asInstanceOf[js.Any],
      "id" -> id.asInstanceOf[js.Any],
      "nodesData" -> nodesData.asInstanceOf[js.Any],
      "parent" -> parent.asInstanceOf[js.Any],
      "setData" -> setData.asInstanceOf[js.Any]
    ).asInstanceOf[NodeCache]
