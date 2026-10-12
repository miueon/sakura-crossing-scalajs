// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait NodeJSONInputDataMeta extends js.Object:
  var nodes: js.Dictionary[NodeClass[NodeClassEventMap] & NodeElements & js.Any] = js.native
  var textures: js.Dictionary[js.Any] = js.native

object NodeJSONInputDataMeta:
  def apply(
    textures: js.Dictionary[js.Any],
    nodes: js.Dictionary[NodeClass[NodeClassEventMap] & NodeElements & js.Any]
  ): NodeJSONInputDataMeta =
    js.Dynamic.literal(
      "textures" -> textures.asInstanceOf[js.Any],
      "nodes" -> nodes.asInstanceOf[js.Any]
    ).asInstanceOf[NodeJSONInputDataMeta]
