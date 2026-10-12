// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait Object3DJSON extends js.Object:
  var metadata: js.UndefOr[BufferGeometryJSONMetadata] = js.native
  def `object`: LODJSONObject | MeshJSONObject | Object3DJSONObject | OrthographicCameraJSONObject | PerspectiveCameraJSONObject | SceneJSONObject | SkinnedMeshJSONObject
  def `object_=`(value: LODJSONObject | MeshJSONObject | Object3DJSONObject | OrthographicCameraJSONObject | PerspectiveCameraJSONObject | SceneJSONObject | SkinnedMeshJSONObject): Unit

object Object3DJSON:
  def apply(
    metadata: js.UndefOr[BufferGeometryJSONMetadata] = js.undefined,
    `object`: LODJSONObject | MeshJSONObject | Object3DJSONObject | OrthographicCameraJSONObject | PerspectiveCameraJSONObject | SceneJSONObject | SkinnedMeshJSONObject
  ): Object3DJSON =
    val _res = js.Dynamic.literal()
    metadata.foreach(v => _res.updateDynamic("metadata")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("object")(`object`.asInstanceOf[js.Any])
    _res.asInstanceOf[Object3DJSON]
