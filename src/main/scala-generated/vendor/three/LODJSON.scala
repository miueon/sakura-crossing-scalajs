// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait LODJSON extends Object3DJSON:
  override def `object`: LODJSONObject
  override def `object_=`(value: LODJSONObject | MeshJSONObject | Object3DJSONObject | OrthographicCameraJSONObject | PerspectiveCameraJSONObject | SceneJSONObject | SkinnedMeshJSONObject): Unit

object LODJSON:
  def apply(
    `object`: LODJSONObject,
    metadata: js.UndefOr[BufferGeometryJSONMetadata] = js.undefined
  ): LODJSON =
    val _res = js.Dynamic.literal("object" -> `object`.asInstanceOf[js.Any])
    metadata.foreach(v => _res.updateDynamic("metadata")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[LODJSON]
