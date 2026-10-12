// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait BufferGeometryJSONData extends js.Object:
  var attributes: js.Dictionary[BufferAttributeJSON] = js.native
  var boundingSphere: js.UndefOr[BufferGeometryJSONDataBoundingSphere] = js.native
  var groups: js.UndefOr[js.Array[GeometryGroup]] = js.native
  var index: js.UndefOr[BufferGeometryJSONDataIndex] = js.native
  var morphAttributes: js.UndefOr[js.Dictionary[js.Array[BufferAttributeJSON]]] = js.native
  var morphTargetsRelative: js.UndefOr[Boolean] = js.native

object BufferGeometryJSONData:
  def apply(
    attributes: js.Dictionary[BufferAttributeJSON],
    index: js.UndefOr[BufferGeometryJSONDataIndex] = js.undefined,
    morphAttributes: js.UndefOr[js.Dictionary[js.Array[BufferAttributeJSON]]] = js.undefined,
    morphTargetsRelative: js.UndefOr[Boolean] = js.undefined,
    groups: js.UndefOr[js.Array[GeometryGroup]] = js.undefined,
    boundingSphere: js.UndefOr[BufferGeometryJSONDataBoundingSphere] = js.undefined
  ): BufferGeometryJSONData =
    val _res = js.Dynamic.literal("attributes" -> attributes.asInstanceOf[js.Any])
    index.foreach(v => _res.updateDynamic("index")(v.asInstanceOf[js.Any]))
    morphAttributes.foreach(v => _res.updateDynamic("morphAttributes")(v.asInstanceOf[js.Any]))
    morphTargetsRelative.foreach(v => _res.updateDynamic("morphTargetsRelative")(v.asInstanceOf[js.Any]))
    groups.foreach(v => _res.updateDynamic("groups")(v.asInstanceOf[js.Any]))
    boundingSphere.foreach(v => _res.updateDynamic("boundingSphere")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[BufferGeometryJSONData]
