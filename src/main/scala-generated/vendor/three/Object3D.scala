// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Object3D")
class Object3D[TEventMap <: Object3DEventMap]() extends EventDispatcher[TEventMap]:
  var animations: js.Array[AnimationClip] = js.native
  def castShadow: Boolean = js.native
  def castShadow_=(value: Boolean | false): Unit = js.native
  var children: js.Array[Object3D[Object3DEventMap]] = js.native
  def count: js.UndefOr[Double] = js.native
  def count_=(value: js.UndefOr[Double]): Unit = js.native
  var customDepthMaterial: js.UndefOr[Material[MaterialEventMap]] = js.native
  var customDistanceMaterial: js.UndefOr[Material[MaterialEventMap]] = js.native
  var frustumCulled: Boolean = js.native
  def id: Double = js.native
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
  protected def add$raw(`object`: Object3D[Object3DEventMap]*): Object3D[TEventMap] = js.native
  def applyMatrix4(matrix: Matrix4): Unit = js.native
  @JSName("applyQuaternion")
  protected def applyQuaternion$raw(quaternion: Quaternion): Object3D[TEventMap] = js.native
  @JSName("attach")
  protected def attach$raw(`object`: Object3D[Object3DEventMap]): Object3D[TEventMap] = js.native
  @JSName("clear")
  protected def clear$raw(): Object3D[TEventMap] = js.native
  @JSName("clone")
  protected def clone$raw(recursive: js.UndefOr[Boolean] = js.undefined): Object3D[TEventMap] = js.native
  @JSName("copy")
  protected def copy$raw(`object`: Object3D[Object3DEventMap], recursive: js.UndefOr[Boolean] = js.undefined): Object3D[TEventMap] = js.native
  def getObjectById(id: Double): js.UndefOr[Object3D[Object3DEventMap]] = js.native
  def getObjectByName(name: String): js.UndefOr[Object3D[Object3DEventMap]] = js.native
  def getObjectByProperty(name: String, value: js.Any): js.UndefOr[Object3D[Object3DEventMap]] = js.native
  def getObjectsByProperty(name: String, value: js.Any, optionalTarget: js.UndefOr[js.Array[Object3D[Object3DEventMap]]] = js.undefined): js.Array[Object3D[Object3DEventMap]] = js.native
  def getWorldDirection(target: Vector3): Vector3 = js.native
  def getWorldPosition(target: Vector3): Vector3 = js.native
  def getWorldQuaternion(target: Quaternion): Quaternion = js.native
  def getWorldScale(target: Vector3): Vector3 = js.native
  def localToWorld(vector: Vector3): Vector3 = js.native
  def lookAt(vector: Vector3): Unit = js.native
  def lookAt(x: Double, y: Double, z: Double): Unit = js.native
  def onAfterRender(renderer: WebGLRenderer, scene: Scene[Object3DEventMap], camera: Camera, geometry: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], material: Material[MaterialEventMap], group: Group[Object3DEventMap]): Unit = js.native
  def onAfterShadow(renderer: WebGLRenderer, scene: Scene[Object3DEventMap], camera: Camera, shadowCamera: Camera, geometry: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], depthMaterial: Material[MaterialEventMap], group: Group[Object3DEventMap]): Unit = js.native
  def onBeforeRender(renderer: WebGLRenderer, scene: Scene[Object3DEventMap], camera: Camera, geometry: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], material: Material[MaterialEventMap], group: Group[Object3DEventMap]): Unit = js.native
  def onBeforeShadow(renderer: WebGLRenderer, scene: Scene[Object3DEventMap], camera: Camera, shadowCamera: Camera, geometry: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], depthMaterial: Material[MaterialEventMap], group: Group[Object3DEventMap]): Unit = js.native
  def raycast(raycaster: Raycaster, intersects: js.Array[Intersection[Object3D[Object3DEventMap]]]): Unit = js.native
  @JSName("remove")
  protected def remove$raw(`object`: Object3D[Object3DEventMap]*): Object3D[TEventMap] = js.native
  @JSName("removeFromParent")
  protected def removeFromParent$raw(): Object3D[TEventMap] = js.native
  @JSName("rotateOnAxis")
  protected def rotateOnAxis$raw(axis: Vector3, angle: Double): Object3D[TEventMap] = js.native
  @JSName("rotateOnWorldAxis")
  protected def rotateOnWorldAxis$raw(axis: Vector3, angle: Double): Object3D[TEventMap] = js.native
  @JSName("rotateX")
  protected def rotateX$raw(angle: Double): Object3D[TEventMap] = js.native
  @JSName("rotateY")
  protected def rotateY$raw(angle: Double): Object3D[TEventMap] = js.native
  @JSName("rotateZ")
  protected def rotateZ$raw(angle: Double): Object3D[TEventMap] = js.native
  def setRotationFromAxisAngle(axis: Vector3, angle: Double): Unit = js.native
  def setRotationFromEuler(euler: Euler): Unit = js.native
  def setRotationFromMatrix(m: Matrix4): Unit = js.native
  def setRotationFromQuaternion(q: Quaternion): Unit = js.native
  def toJSON(meta: js.UndefOr[JSONMeta] = js.undefined): Object3DJSON = js.native
  @JSName("translateOnAxis")
  protected def translateOnAxis$raw(axis: Vector3, distance: Double): Object3D[TEventMap] = js.native
  @JSName("translateX")
  protected def translateX$raw(distance: Double): Object3D[TEventMap] = js.native
  @JSName("translateY")
  protected def translateY$raw(distance: Double): Object3D[TEventMap] = js.native
  @JSName("translateZ")
  protected def translateZ$raw(distance: Double): Object3D[TEventMap] = js.native
  def traverse(callback: js.Function1[Object3D[Object3DEventMap], Unit]): Unit = js.native
  def traverseAncestors(callback: js.Function1[Object3D[Object3DEventMap], Unit]): Unit = js.native
  def traverseVisible(callback: js.Function1[Object3D[Object3DEventMap], Unit]): Unit = js.native
  def updateMatrix(): Unit = js.native
  def updateMatrixWorld(force: js.UndefOr[Boolean] = js.undefined): Unit = js.native
  def updateWorldMatrix(updateParents: Boolean, updateChildren: Boolean, force: js.UndefOr[Boolean] = js.undefined): Unit = js.native
  def worldToLocal(vector: Vector3): Vector3 = js.native


object Object3D:
  extension [TEventMap <: Object3DEventMap, Self <: Object3D[TEventMap]](self: Self)
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

  // #WARN: static 'DEFAULT_MATRIX_AUTO_UPDATE' is declared read-write upstream; the member-level native publication cannot express a JS setter, so its write direction is withheld
  @js.native
  @JSImport("three", "Object3D.DEFAULT_MATRIX_AUTO_UPDATE")
  def DEFAULT_MATRIX_AUTO_UPDATE: Boolean = js.native
  // #WARN: static 'DEFAULT_MATRIX_WORLD_AUTO_UPDATE' is declared read-write upstream; the member-level native publication cannot express a JS setter, so its write direction is withheld
  @js.native
  @JSImport("three", "Object3D.DEFAULT_MATRIX_WORLD_AUTO_UPDATE")
  def DEFAULT_MATRIX_WORLD_AUTO_UPDATE: Boolean = js.native
  // #WARN: static 'DEFAULT_UP' is declared read-write upstream; the member-level native publication cannot express a JS setter, so its write direction is withheld
  @js.native
  @JSImport("three", "Object3D.DEFAULT_UP")
  def DEFAULT_UP: Vector3 = js.native
