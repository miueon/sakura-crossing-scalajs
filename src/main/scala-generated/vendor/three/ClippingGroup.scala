// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

// #WARN: Unified parameter slots for overload 'addEventListener' with JVM erasure 2:(java.lang.String,scala.scalajs.js.Function) via union
// #WARN: Unified parameter slots for overload 'hasEventListener' with JVM erasure 2:(java.lang.String,scala.scalajs.js.Function) via union
// #WARN: Unified parameter slots for overload 'removeEventListener' with JVM erasure 2:(java.lang.String,scala.scalajs.js.Function) via union
// #WARN: Unified parameter slots for overload 'dispatchEvent' with JVM erasure 1:(java.lang.Object) via union
@js.native
trait ClippingGroup extends js.Object:
  var animations: js.Array[AnimationClip] = js.native
  var castShadow: Boolean = js.native
  var children: js.Array[Object3D[Object3DEventMap]] = js.native
  var clipIntersection: Boolean = js.native
  var clippingPlanes: js.Array[Plane] = js.native
  var clipShadows: Boolean = js.native
  var count: js.UndefOr[Double] = js.native
  var customDepthMaterial: js.UndefOr[Material[MaterialEventMap]] = js.native
  var customDistanceMaterial: js.UndefOr[Material[MaterialEventMap]] = js.native
  var enabled: Boolean = js.native
  var frustumCulled: Boolean = js.native
  def id: Double = js.native
  def isClippingGroup: true = js.native
  def isGroup: true = js.native
  def isObject3D: true = js.native
  var layers: Layers = js.native
  var matrix: Matrix4 = js.native
  var matrixAutoUpdate: Boolean = js.native
  var matrixWorld: Matrix4 = js.native
  var matrixWorldAutoUpdate: Boolean = js.native
  var matrixWorldNeedsUpdate: Boolean = js.native
  def modelViewMatrix: Matrix4 = js.native
  var name: String = js.native
  def normalMatrix: Matrix3 = js.native
  var occlusionTest: js.UndefOr[Boolean] = js.native
  var parent: Object3D[Object3DEventMap] | Null = js.native
  var pivot: Vector3 | Null = js.native
  def position: Vector3 = js.native
  def quaternion: Quaternion = js.native
  var receiveShadow: Boolean = js.native
  var renderOrder: Double = js.native
  def rotation: Euler = js.native
  def scale: Vector3 = js.native
  var static: Boolean = js.native
  def `type`: String = js.native
  var up: Vector3 = js.native
  var userData: js.Dictionary[js.Any] = js.native
  var uuid: String = js.native
  var visible: Boolean = js.native
  @JSName("add")
  protected def add$raw(`object`: Object3D[Object3DEventMap]*): ClippingGroup = js.native
  // #WARN: Unified parameter slots for overload 'addEventListener' with JVM erasure 2:(java.lang.String,scala.scalajs.js.Function) via union
  def addEventListener(`type`: "added" | "removed" | "childadded" | "childremoved", listener: js.Function1[js.Any & Event["added", ClippingGroup], Unit] | js.Function1[js.Any & Event["removed", ClippingGroup], Unit] | js.Function1[AmbientLightAddEventListenerChildadded & Event["childadded", ClippingGroup], Unit] | js.Function1[AmbientLightAddEventListenerChildadded & Event["childremoved", ClippingGroup], Unit]): Unit = js.native
  def applyMatrix4(matrix: Matrix4): Unit = js.native
  @JSName("applyQuaternion")
  protected def applyQuaternion$raw(quaternion: Quaternion): ClippingGroup = js.native
  @JSName("attach")
  protected def attach$raw(`object`: Object3D[Object3DEventMap]): ClippingGroup = js.native
  @JSName("clear")
  protected def clear$raw(): ClippingGroup = js.native
  @JSName("clone")
  protected def clone$raw(recursive: js.UndefOr[Boolean] = js.undefined): ClippingGroup = js.native
  @JSName("copy")
  protected def copy$raw(`object`: Object3D[Object3DEventMap], recursive: js.UndefOr[Boolean] = js.undefined): ClippingGroup = js.native
  // #WARN: Unified parameter slots for overload 'dispatchEvent' with JVM erasure 1:(java.lang.Object) via union
  def dispatchEvent(event: BaseEvent["added"] & js.Any | BaseEvent["removed"] & js.Any | BaseEvent["childadded"] & AmbientLightAddEventListenerChildadded | BaseEvent["childremoved"] & AmbientLightAddEventListenerChildadded): Unit = js.native
  def getObjectById(id: Double): js.UndefOr[Object3D[Object3DEventMap]] = js.native
  def getObjectByName(name: String): js.UndefOr[Object3D[Object3DEventMap]] = js.native
  def getObjectByProperty(name: String, value: js.Any): js.UndefOr[Object3D[Object3DEventMap]] = js.native
  def getObjectsByProperty(name: String, value: js.Any, optionalTarget: js.UndefOr[js.Array[Object3D[Object3DEventMap]]] = js.undefined): js.Array[Object3D[Object3DEventMap]] = js.native
  def getWorldDirection(target: Vector3): Vector3 = js.native
  def getWorldPosition(target: Vector3): Vector3 = js.native
  def getWorldQuaternion(target: Quaternion): Quaternion = js.native
  def getWorldScale(target: Vector3): Vector3 = js.native
  // #WARN: Unified parameter slots for overload 'hasEventListener' with JVM erasure 2:(java.lang.String,scala.scalajs.js.Function) via union
  def hasEventListener(`type`: "added" | "removed" | "childadded" | "childremoved", listener: js.Function1[js.Any & Event["added", ClippingGroup], Unit] | js.Function1[js.Any & Event["removed", ClippingGroup], Unit] | js.Function1[AmbientLightAddEventListenerChildadded & Event["childadded", ClippingGroup], Unit] | js.Function1[AmbientLightAddEventListenerChildadded & Event["childremoved", ClippingGroup], Unit]): Boolean = js.native
  def localToWorld(vector: Vector3): Vector3 = js.native
  def lookAt(vector: Vector3): Unit = js.native
  def lookAt(x: Double, y: Double, z: Double): Unit = js.native
  def onAfterRender(renderer: WebGLRenderer, scene: Scene[Object3DEventMap], camera: Camera, geometry: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], material: Material[MaterialEventMap], group: Group[Object3DEventMap]): Unit = js.native
  def onAfterShadow(renderer: WebGLRenderer, scene: Scene[Object3DEventMap], camera: Camera, shadowCamera: Camera, geometry: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], depthMaterial: Material[MaterialEventMap], group: Group[Object3DEventMap]): Unit = js.native
  def onBeforeRender(renderer: WebGLRenderer, scene: Scene[Object3DEventMap], camera: Camera, geometry: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], material: Material[MaterialEventMap], group: Group[Object3DEventMap]): Unit = js.native
  def onBeforeShadow(renderer: WebGLRenderer, scene: Scene[Object3DEventMap], camera: Camera, shadowCamera: Camera, geometry: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], depthMaterial: Material[MaterialEventMap], group: Group[Object3DEventMap]): Unit = js.native
  def raycast(raycaster: Raycaster, intersects: js.Array[Intersection[Object3D[Object3DEventMap]]]): Unit = js.native
  @JSName("remove")
  protected def remove$raw(`object`: Object3D[Object3DEventMap]*): ClippingGroup = js.native
  // #WARN: Unified parameter slots for overload 'removeEventListener' with JVM erasure 2:(java.lang.String,scala.scalajs.js.Function) via union
  def removeEventListener(`type`: "added" | "removed" | "childadded" | "childremoved", listener: js.Function1[js.Any & Event["added", ClippingGroup], Unit] | js.Function1[js.Any & Event["removed", ClippingGroup], Unit] | js.Function1[AmbientLightAddEventListenerChildadded & Event["childadded", ClippingGroup], Unit] | js.Function1[AmbientLightAddEventListenerChildadded & Event["childremoved", ClippingGroup], Unit]): Unit = js.native
  @JSName("removeFromParent")
  protected def removeFromParent$raw(): ClippingGroup = js.native
  @JSName("rotateOnAxis")
  protected def rotateOnAxis$raw(axis: Vector3, angle: Double): ClippingGroup = js.native
  @JSName("rotateOnWorldAxis")
  protected def rotateOnWorldAxis$raw(axis: Vector3, angle: Double): ClippingGroup = js.native
  @JSName("rotateX")
  protected def rotateX$raw(angle: Double): ClippingGroup = js.native
  @JSName("rotateY")
  protected def rotateY$raw(angle: Double): ClippingGroup = js.native
  @JSName("rotateZ")
  protected def rotateZ$raw(angle: Double): ClippingGroup = js.native
  def setRotationFromAxisAngle(axis: Vector3, angle: Double): Unit = js.native
  def setRotationFromEuler(euler: Euler): Unit = js.native
  def setRotationFromMatrix(m: Matrix4): Unit = js.native
  def setRotationFromQuaternion(q: Quaternion): Unit = js.native
  def toJSON(meta: js.UndefOr[JSONMeta] = js.undefined): Object3DJSON = js.native
  @JSName("translateOnAxis")
  protected def translateOnAxis$raw(axis: Vector3, distance: Double): ClippingGroup = js.native
  @JSName("translateX")
  protected def translateX$raw(distance: Double): ClippingGroup = js.native
  @JSName("translateY")
  protected def translateY$raw(distance: Double): ClippingGroup = js.native
  @JSName("translateZ")
  protected def translateZ$raw(distance: Double): ClippingGroup = js.native
  def traverse(callback: js.Function1[Object3D[Object3DEventMap], Unit]): Unit = js.native
  def traverseAncestors(callback: js.Function1[Object3D[Object3DEventMap], Unit]): Unit = js.native
  def traverseVisible(callback: js.Function1[Object3D[Object3DEventMap], Unit]): Unit = js.native
  def updateMatrix(): Unit = js.native
  def updateMatrixWorld(force: js.UndefOr[Boolean] = js.undefined): Unit = js.native
  def updateWorldMatrix(updateParents: Boolean, updateChildren: Boolean, force: js.UndefOr[Boolean] = js.undefined): Unit = js.native
  def worldToLocal(vector: Vector3): Vector3 = js.native
// #WARN: SHIM-065: Native facade trait 'ClippingGroup' cannot receive a builder companion: JS property 'lookAt' is written by 2 members

object ClippingGroup:
  extension [Self <: ClippingGroup](self: Self)
    inline def applyQuaternion(quaternion: Quaternion): Self = self.applyQuaternion$raw(quaternion).asInstanceOf[Self]
    inline def rotateOnAxis(axis: Vector3, angle: Double): Self = self.rotateOnAxis$raw(axis, angle).asInstanceOf[Self]
    inline def rotateOnWorldAxis(axis: Vector3, angle: Double): Self = self.rotateOnWorldAxis$raw(axis, angle).asInstanceOf[Self]
    inline def rotateX(angle: Double): Self = self.rotateX$raw(angle).asInstanceOf[Self]
    inline def rotateY(angle: Double): Self = self.rotateY$raw(angle).asInstanceOf[Self]
    inline def rotateZ(angle: Double): Self = self.rotateZ$raw(angle).asInstanceOf[Self]
    inline def translateOnAxis(axis: Vector3, distance: Double): Self = self.translateOnAxis$raw(axis, distance).asInstanceOf[Self]
    inline def translateX(distance: Double): Self = self.translateX$raw(distance).asInstanceOf[Self]
    inline def translateY(distance: Double): Self = self.translateY$raw(distance).asInstanceOf[Self]
    inline def translateZ(distance: Double): Self = self.translateZ$raw(distance).asInstanceOf[Self]
    inline def add(`object`: Object3D[Object3DEventMap]*): Self = self.add$raw(`object`*).asInstanceOf[Self]
    inline def remove(`object`: Object3D[Object3DEventMap]*): Self = self.remove$raw(`object`*).asInstanceOf[Self]
    inline def removeFromParent(): Self = self.removeFromParent$raw().asInstanceOf[Self]
    inline def clear(): Self = self.clear$raw().asInstanceOf[Self]
    inline def attach(`object`: Object3D[Object3DEventMap]): Self = self.attach$raw(`object`).asInstanceOf[Self]
    inline def clone(recursive: js.UndefOr[Boolean] = js.undefined): Self = self.clone$raw(recursive).asInstanceOf[Self]
    inline def copy(`object`: Object3D[Object3DEventMap], recursive: js.UndefOr[Boolean] = js.undefined): Self = self.copy$raw(`object`, recursive).asInstanceOf[Self]
