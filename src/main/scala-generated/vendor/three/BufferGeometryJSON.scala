// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait BufferGeometryJSON extends js.Object:
  var data: js.UndefOr[BufferGeometryJSONData] = js.native
  var metadata: js.UndefOr[BufferGeometryJSONMetadata] = js.native
  var name: js.UndefOr[String] = js.native
  var `type`: String = js.native
  var userData: js.UndefOr[js.Dictionary[js.Any]] = js.native
  var uuid: String = js.native

object BufferGeometryJSON:
  def apply(
    metadata: js.UndefOr[BufferGeometryJSONMetadata] = js.undefined,
    uuid: String,
    `type`: String,
    name: js.UndefOr[String] = js.undefined,
    userData: js.UndefOr[js.Dictionary[js.Any]] = js.undefined,
    data: js.UndefOr[BufferGeometryJSONData] = js.undefined
  ): BufferGeometryJSON =
    val _res = js.Dynamic.literal()
    metadata.foreach(v => _res.updateDynamic("metadata")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("uuid")(uuid.asInstanceOf[js.Any])
    _res.updateDynamic("type")(`type`.asInstanceOf[js.Any])
    name.foreach(v => _res.updateDynamic("name")(v.asInstanceOf[js.Any]))
    userData.foreach(v => _res.updateDynamic("userData")(v.asInstanceOf[js.Any]))
    data.foreach(v => _res.updateDynamic("data")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[BufferGeometryJSON]
