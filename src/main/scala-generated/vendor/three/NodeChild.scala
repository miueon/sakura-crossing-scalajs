// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait NodeChild extends js.Object:
  var childNode: NodeClass[NodeClassEventMap] & NodeElements & js.Any = js.native
  var index: js.UndefOr[String | Double] = js.native
  var property: String = js.native

object NodeChild:
  def apply(
    property: String,
    index: js.UndefOr[String | Double] = js.undefined,
    childNode: NodeClass[NodeClassEventMap] & NodeElements & js.Any
  ): NodeChild =
    val _res = js.Dynamic.literal("property" -> property.asInstanceOf[js.Any])
    index.foreach(v => _res.updateDynamic("index")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("childNode")(childNode.asInstanceOf[js.Any])
    _res.asInstanceOf[NodeChild]
