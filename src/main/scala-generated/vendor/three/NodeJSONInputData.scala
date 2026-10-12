// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait NodeJSONInputData extends js.Object:
  var inputNodes: js.UndefOr[NodeJSONInputNodes] = js.native
  var meta: NodeJSONInputDataMeta = js.native

object NodeJSONInputData:
  def apply(
    inputNodes: Option[js.UndefOr[NodeJSONInputNodes]] = None,
    meta: NodeJSONInputDataMeta
  ): NodeJSONInputData =
    val _res = js.Dynamic.literal()
    inputNodes.foreach(v => _res.updateDynamic("inputNodes")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("meta")(meta.asInstanceOf[js.Any])
    _res.asInstanceOf[NodeJSONInputData]
