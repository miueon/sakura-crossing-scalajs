// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Raycaster")
class Raycaster(origin: js.UndefOr[Vector3] = js.undefined, direction: js.UndefOr[Vector3] = js.undefined, near_1: js.UndefOr[Double] = js.undefined, far_1: js.UndefOr[Double] = js.undefined) extends js.Object:
  var camera: Camera = js.native
  var far: Double = js.native
  var layers: Layers = js.native
  var near: Double = js.native
  var params: RaycasterParameters = js.native
  var ray: Ray = js.native
  def intersectObject[TIntersected <: Object3D[?]](`object`: Object3D[Object3DEventMap], recursive: js.UndefOr[Boolean] = js.undefined, optionalTarget: js.UndefOr[js.Array[Intersection[TIntersected]]] = js.undefined): js.Array[Intersection[TIntersected]] = js.native
  def intersectObjects[TIntersected <: Object3D[?]](objects: js.Array[Object3D[Object3DEventMap]], recursive: js.UndefOr[Boolean] = js.undefined, optionalTarget: js.UndefOr[js.Array[Intersection[TIntersected]]] = js.undefined): js.Array[Intersection[TIntersected]] = js.native
  def set(origin: Vector3, direction: Vector3): Unit = js.native
  def setFromCamera(coords: Vector2, camera: Camera): Unit = js.native
  @JSName("setFromXRController")
  protected def setFromXRController$raw(controller: XRTargetRaySpace): Raycaster = js.native

object Raycaster:
  extension [Self <: Raycaster](self: Self)
    inline def setFromXRController(controller: XRTargetRaySpace): Self = self.setFromXRController$raw(controller).asInstanceOf[Self]
