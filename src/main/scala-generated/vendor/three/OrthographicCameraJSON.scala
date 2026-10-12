// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait OrthographicCameraJSON extends Object3DJSON:
  override def `object`: OrthographicCameraJSONObject
  override def `object_=`(value: LODJSONObject | MeshJSONObject | Object3DJSONObject | OrthographicCameraJSONObject | PerspectiveCameraJSONObject | SceneJSONObject | SkinnedMeshJSONObject): Unit

object OrthographicCameraJSON:
  def apply(
    `object`: OrthographicCameraJSONObject,
    metadata: js.UndefOr[BufferGeometryJSONMetadata] = js.undefined
  ): OrthographicCameraJSON =
    val _res = js.Dynamic.literal("object" -> `object`.asInstanceOf[js.Any])
    metadata.foreach(v => _res.updateDynamic("metadata")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[OrthographicCameraJSON]
