// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait NodeJSONMeta extends js.Object:
  var images: js.Dictionary[js.Any] = js.native
  var nodes: js.Dictionary[NodeJSONIntermediateOutputData] = js.native
  var textures: js.Dictionary[js.Any] = js.native

object NodeJSONMeta:
  def apply(
    textures: js.Dictionary[js.Any],
    images: js.Dictionary[js.Any],
    nodes: js.Dictionary[NodeJSONIntermediateOutputData]
  ): NodeJSONMeta =
    js.Dynamic.literal(
      "textures" -> textures.asInstanceOf[js.Any],
      "images" -> images.asInstanceOf[js.Any],
      "nodes" -> nodes.asInstanceOf[js.Any]
    ).asInstanceOf[NodeJSONMeta]
