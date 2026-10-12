// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait NodeJSONIntermediateOutputData extends js.Object:
  var images: js.UndefOr[js.Array[facadecontract.std.JSUnknown]] = js.native
  var inputNodes: js.UndefOr[NodeJSONInputNodes] = js.native
  var meta: js.UndefOr[NodeJSONMeta] = js.native
  var metadata: js.UndefOr[NodeJSONMetadata] = js.native
  var nodes: js.UndefOr[js.Array[NodeJSONIntermediateOutputData]] = js.native
  var textures: js.UndefOr[js.Array[facadecontract.std.JSUnknown]] = js.native
  var `type`: js.UndefOr[String] = js.native
  var uuid: String = js.native

object NodeJSONIntermediateOutputData:
  def apply(
    uuid: String,
    `type`: js.UndefOr[String],
    meta: Option[js.UndefOr[NodeJSONMeta]] = None,
    metadata: js.UndefOr[NodeJSONMetadata] = js.undefined,
    inputNodes: Option[js.UndefOr[NodeJSONInputNodes]] = None,
    textures: js.UndefOr[js.Array[facadecontract.std.JSUnknown]] = js.undefined,
    images: js.UndefOr[js.Array[facadecontract.std.JSUnknown]] = js.undefined,
    nodes: js.UndefOr[js.Array[NodeJSONIntermediateOutputData]] = js.undefined
  ): NodeJSONIntermediateOutputData =
    val _res = js.Dynamic.literal(
      "uuid" -> uuid.asInstanceOf[js.Any],
      "type" -> `type`.asInstanceOf[js.Any]
    )
    meta.foreach(v => _res.updateDynamic("meta")(v.asInstanceOf[js.Any]))
    metadata.foreach(v => _res.updateDynamic("metadata")(v.asInstanceOf[js.Any]))
    inputNodes.foreach(v => _res.updateDynamic("inputNodes")(v.asInstanceOf[js.Any]))
    textures.foreach(v => _res.updateDynamic("textures")(v.asInstanceOf[js.Any]))
    images.foreach(v => _res.updateDynamic("images")(v.asInstanceOf[js.Any]))
    nodes.foreach(v => _res.updateDynamic("nodes")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[NodeJSONIntermediateOutputData]
