// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait PerspectiveCameraJSON extends Object3DJSON:
  override def `object`: PerspectiveCameraJSONObject
  override def `object_=`(value: LODJSONObject | MeshJSONObject | Object3DJSONObject | OrthographicCameraJSONObject | PerspectiveCameraJSONObject | SceneJSONObject | SkinnedMeshJSONObject): Unit

object PerspectiveCameraJSON:
  def apply(
    `object`: PerspectiveCameraJSONObject,
    metadata: js.UndefOr[BufferGeometryJSONMetadata] = js.undefined
  ): PerspectiveCameraJSON =
    val _res = js.Dynamic.literal("object" -> `object`.asInstanceOf[js.Any])
    metadata.foreach(v => _res.updateDynamic("metadata")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[PerspectiveCameraJSON]
