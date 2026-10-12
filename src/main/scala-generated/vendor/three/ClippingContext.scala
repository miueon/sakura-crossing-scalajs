// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait ClippingContext extends js.Object:
  var cacheKey: String = js.native
  var clipIntersection: Boolean | Null = js.native
  var clippingGroupContexts: js.Object = js.native
  var intersectionPlanes: js.Array[Vector4] = js.native
  def parentVersion: Double | Null = js.native
  var shadowPass: Boolean = js.native
  def unionClippingCount: Double = js.native
  var unionPlanes: js.Array[Vector4] = js.native
  def version: Double = js.native
  var viewMatrix: Matrix4 = js.native
  var viewNormalMatrix: Matrix3 = js.native
  def getGroupContext(clippingGroup: ClippingGroup): ClippingContext = js.native
  def projectPlanes(source: js.Array[Plane], destination: js.Array[Vector4], offset: Double): Unit = js.native
  def update(parentContext: ClippingContext, clippingGroup: ClippingGroup): Unit = js.native
  def updateGlobal(scene: Scene[Object3DEventMap], camera: Camera): Unit = js.native

object ClippingContext:
  def of(
    cacheKey: String,
    clipIntersection: Boolean | Null,
    clippingGroupContexts: js.Object,
    getGroupContext: js.Function1[ClippingGroup, ClippingContext],
    intersectionPlanes: js.Array[Vector4],
    parentVersion: Double | Null,
    projectPlanes: js.Function3[js.Array[Plane], js.Array[Vector4], Double, Unit],
    shadowPass: Boolean,
    unionClippingCount: Double,
    unionPlanes: js.Array[Vector4],
    update: js.Function2[ClippingContext, ClippingGroup, Unit],
    updateGlobal: js.Function2[Scene[Object3DEventMap], Camera, Unit],
    version: Double,
    viewMatrix: Matrix4,
    viewNormalMatrix: Matrix3
  ): ClippingContext =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "cacheKey" -> cacheKey.asInstanceOf[js.Any],
      "clipIntersection" -> clipIntersection.asInstanceOf[js.Any],
      "clippingGroupContexts" -> clippingGroupContexts.asInstanceOf[js.Any],
      "getGroupContext" -> getGroupContext.asInstanceOf[js.Any],
      "intersectionPlanes" -> intersectionPlanes.asInstanceOf[js.Any],
      "parentVersion" -> parentVersion.asInstanceOf[js.Any],
      "projectPlanes" -> projectPlanes.asInstanceOf[js.Any],
      "shadowPass" -> shadowPass.asInstanceOf[js.Any],
      "unionClippingCount" -> unionClippingCount.asInstanceOf[js.Any],
      "unionPlanes" -> unionPlanes.asInstanceOf[js.Any],
      "update" -> update.asInstanceOf[js.Any],
      "updateGlobal" -> updateGlobal.asInstanceOf[js.Any],
      "version" -> version.asInstanceOf[js.Any],
      "viewMatrix" -> viewMatrix.asInstanceOf[js.Any],
      "viewNormalMatrix" -> viewNormalMatrix.asInstanceOf[js.Any]
    ).asInstanceOf[ClippingContext]
