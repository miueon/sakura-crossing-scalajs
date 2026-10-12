// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
trait OmitArrayCameraCameras extends js.Object:
  var add: js.Function = js.native
  var animations: js.Array[AnimationClip] = js.native
  var applyMatrix4: js.Function1[Matrix4, Unit] = js.native
  var applyQuaternion: js.Function1[Quaternion, ArrayCamera] = js.native
  var aspect: Double = js.native
  var attach: js.Function1[Object3D[Object3DEventMap], ArrayCamera] = js.native
  var castShadow: Boolean = js.native
  var children: js.Array[Object3D[Object3DEventMap]] = js.native
  var clear: js.Function0[ArrayCamera] = js.native
  var clearViewOffset: js.Function0[Unit] = js.native
  // #WARN: OmitArrayCameraCameras.clone collides with the pinned platform member 'java.lang.Object#clone' at the Scala source name only: the member is a function-valued property, so its read yields exactly the function the JS wire key 'clone' holds while the inherited member keeps its call; the wire key is preserved with @JSName and the member is emitted under a derived collision-free name
  @JSName("clone")
  var clone_1: js.Function0[ArrayCamera] = js.native
  var coordinateSystem: 2000 | 2001 = js.native
  var copy: js.Function2[PerspectiveCamera, js.UndefOr[Boolean], ArrayCamera] = js.native
  var count: js.UndefOr[js.UndefOr[Double]] = js.native
  var customDepthMaterial: js.UndefOr[js.UndefOr[Material[MaterialEventMap]]] = js.native
  var customDistanceMaterial: js.UndefOr[js.UndefOr[Material[MaterialEventMap]]] = js.native
  var far: Double = js.native
  var filmGauge: Double = js.native
  var filmOffset: Double = js.native
  var focus: Double = js.native
  var fov: Double = js.native
  var frustumCulled: Boolean = js.native
  var getEffectiveFOV: js.Function0[Double] = js.native
  var getFilmHeight: js.Function0[Double] = js.native
  var getFilmWidth: js.Function0[Double] = js.native
  var getFocalLength: js.Function0[Double] = js.native
  var getObjectById: js.Function1[Double, js.UndefOr[Object3D[Object3DEventMap]]] = js.native
  var getObjectByName: js.Function1[String, js.UndefOr[Object3D[Object3DEventMap]]] = js.native
  var getObjectByProperty: js.Function2[String, js.Any, js.UndefOr[Object3D[Object3DEventMap]]] = js.native
  var getObjectsByProperty: js.Function3[String, js.Any, js.UndefOr[js.Array[Object3D[Object3DEventMap]]], js.Array[Object3D[Object3DEventMap]]] = js.native
  var getViewBounds: js.Function3[Double, Vector2, Vector2, Unit] = js.native
  var getViewSize: js.Function2[Double, Vector2, Vector2] = js.native
  var getWorldDirection: js.Function1[Vector3, Vector3] = js.native
  var getWorldPosition: js.Function1[Vector3, Vector3] = js.native
  var getWorldQuaternion: js.Function1[Quaternion, Quaternion] = js.native
  var getWorldScale: js.Function1[Vector3, Vector3] = js.native
  def id: Double = js.native
  def isArrayCamera: Boolean = js.native
  def isCamera: Boolean = js.native
  def isMultiViewCamera: Boolean = js.native
  def isObject3D: true = js.native
  def isPerspectiveCamera: Boolean = js.native
  var layers: Layers = js.native
  var localToWorld: js.Function1[Vector3, Vector3] = js.native
  var lookAt: js.Function1[Vector3, Unit] = js.native
  var matrix: Matrix4 = js.native
  var matrixAutoUpdate: Boolean = js.native
  var matrixWorld: Matrix4 = js.native
  var matrixWorldAutoUpdate: Boolean = js.native
  var matrixWorldInverse: Matrix4 = js.native
  var matrixWorldNeedsUpdate: Boolean = js.native
  def modelViewMatrix: Matrix4 = js.native
  var name: String = js.native
  var near: Double = js.native
  def normalMatrix: Matrix3 = js.native
  var occlusionTest: js.UndefOr[js.UndefOr[Boolean]] = js.native
  var onAfterRender: js.Function6[WebGLRenderer, Scene[Object3DEventMap], Camera, BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], Material[MaterialEventMap], Group[Object3DEventMap], Unit] = js.native
  var onAfterShadow: js.Function7[WebGLRenderer, Scene[Object3DEventMap], Camera, Camera, BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], Material[MaterialEventMap], Group[Object3DEventMap], Unit] = js.native
  var onBeforeRender: js.Function6[WebGLRenderer, Scene[Object3DEventMap], Camera, BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], Material[MaterialEventMap], Group[Object3DEventMap], Unit] = js.native
  var onBeforeShadow: js.Function7[WebGLRenderer, Scene[Object3DEventMap], Camera, Camera, BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], Material[MaterialEventMap], Group[Object3DEventMap], Unit] = js.native
  var parent: Object3D[Object3DEventMap] | Null = js.native
  var pivot: Vector3 | Null = js.native
  def position: Vector3 = js.native
  var projectionMatrix: Matrix4 = js.native
  var projectionMatrixInverse: Matrix4 = js.native
  def quaternion: Quaternion = js.native
  var raycast: js.Function2[Raycaster, js.Array[Intersection[Object3D[Object3DEventMap]]], Unit] = js.native
  var receiveShadow: Boolean = js.native
  var remove: js.Function = js.native
  var removeFromParent: js.Function0[ArrayCamera] = js.native
  var renderOrder: Double = js.native
  def reversedDepth: Boolean = js.native
  var rotateOnAxis: js.Function2[Vector3, Double, ArrayCamera] = js.native
  var rotateOnWorldAxis: js.Function2[Vector3, Double, ArrayCamera] = js.native
  var rotateX: js.Function1[Double, ArrayCamera] = js.native
  var rotateY: js.Function1[Double, ArrayCamera] = js.native
  var rotateZ: js.Function1[Double, ArrayCamera] = js.native
  def rotation: Euler = js.native
  def scale: Vector3 = js.native
  var setFocalLength: js.Function1[Double, Unit] = js.native
  var setRotationFromAxisAngle: js.Function2[Vector3, Double, Unit] = js.native
  var setRotationFromEuler: js.Function1[Euler, Unit] = js.native
  var setRotationFromMatrix: js.Function1[Matrix4, Unit] = js.native
  var setRotationFromQuaternion: js.Function1[Quaternion, Unit] = js.native
  var setViewOffset: js.Function6[Double, Double, Double, Double, Double, Double, Unit] = js.native
  var static: Boolean = js.native
  var toJSON: js.Function1[js.UndefOr[JSONMeta], PerspectiveCameraJSON] = js.native
  var translateOnAxis: js.Function2[Vector3, Double, ArrayCamera] = js.native
  var translateX: js.Function1[Double, ArrayCamera] = js.native
  var translateY: js.Function1[Double, ArrayCamera] = js.native
  var translateZ: js.Function1[Double, ArrayCamera] = js.native
  var traverse: js.Function1[js.Function1[Object3D[Object3DEventMap], Unit], Unit] = js.native
  var traverseAncestors: js.Function1[js.Function1[Object3D[Object3DEventMap], Unit], Unit] = js.native
  var traverseVisible: js.Function1[js.Function1[Object3D[Object3DEventMap], Unit], Unit] = js.native
  def `type`: String = js.native
  var up: Vector3 = js.native
  var updateMatrix: js.Function0[Unit] = js.native
  var updateMatrixWorld: js.Function1[js.UndefOr[Boolean], Unit] = js.native
  var updateProjectionMatrix: js.Function0[Unit] = js.native
  var updateWorldMatrix: js.Function3[Boolean, Boolean, js.UndefOr[Boolean], Unit] = js.native
  var userData: js.Dictionary[js.Any] = js.native
  var uuid: String = js.native
  var view: ArrayCameraView | Null = js.native
  var viewport: js.UndefOr[Vector4] = js.native
  var visible: Boolean = js.native
  var worldToLocal: js.Function1[Vector3, Vector3] = js.native
  var zoom: Double = js.native

object OmitArrayCameraCameras:
  def apply(
    `type`: String,
    applyMatrix4: js.Function1[Matrix4, Unit],
    clone_1: js.Function0[ArrayCamera],
    toJSON: js.Function1[js.UndefOr[JSONMeta], PerspectiveCameraJSON],
    matrixAutoUpdate: Boolean,
    rotation: Euler,
    uuid: String,
    add: js.Function,
    copy: js.Function2[PerspectiveCamera, js.UndefOr[Boolean], ArrayCamera],
    count: Option[js.UndefOr[js.UndefOr[Double]]] = None,
    matrix: Matrix4,
    name: String,
    castShadow: Boolean,
    receiveShadow: Boolean,
    visible: Boolean,
    frustumCulled: Boolean,
    renderOrder: Double,
    static: Boolean,
    userData: js.Dictionary[js.Any],
    layers: Layers,
    up: Vector3,
    pivot: Vector3 | Null,
    children: js.Array[Object3D[Object3DEventMap]],
    animations: js.Array[AnimationClip],
    scale: Vector3,
    raycast: js.Function2[Raycaster, js.Array[Intersection[Object3D[Object3DEventMap]]], Unit],
    isArrayCamera: Boolean,
    isMultiViewCamera: Boolean,
    isPerspectiveCamera: Boolean,
    fov: Double,
    zoom: Double,
    near: Double,
    far: Double,
    focus: Double,
    aspect: Double,
    view: ArrayCameraView | Null,
    filmGauge: Double,
    filmOffset: Double,
    setFocalLength: js.Function1[Double, Unit],
    getFocalLength: js.Function0[Double],
    getEffectiveFOV: js.Function0[Double],
    getFilmWidth: js.Function0[Double],
    getFilmHeight: js.Function0[Double],
    getViewBounds: js.Function3[Double, Vector2, Vector2, Unit],
    getViewSize: js.Function2[Double, Vector2, Vector2],
    setViewOffset: js.Function6[Double, Double, Double, Double, Double, Double, Unit],
    clearViewOffset: js.Function0[Unit],
    updateProjectionMatrix: js.Function0[Unit],
    isCamera: Boolean,
    matrixWorldInverse: Matrix4,
    projectionMatrix: Matrix4,
    projectionMatrixInverse: Matrix4,
    coordinateSystem: 2000 | 2001,
    viewport: js.UndefOr[Vector4] = js.undefined,
    reversedDepth: Boolean,
    isObject3D: true = true,
    id: Double,
    parent: Object3D[Object3DEventMap] | Null,
    position: Vector3,
    quaternion: Quaternion,
    modelViewMatrix: Matrix4,
    normalMatrix: Matrix3,
    matrixWorld: Matrix4,
    matrixWorldAutoUpdate: Boolean,
    matrixWorldNeedsUpdate: Boolean,
    customDepthMaterial: Option[js.UndefOr[js.UndefOr[Material[MaterialEventMap]]]] = None,
    customDistanceMaterial: Option[js.UndefOr[js.UndefOr[Material[MaterialEventMap]]]] = None,
    onBeforeShadow: js.Function7[WebGLRenderer, Scene[Object3DEventMap], Camera, Camera, BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], Material[MaterialEventMap], Group[Object3DEventMap], Unit],
    onAfterShadow: js.Function7[WebGLRenderer, Scene[Object3DEventMap], Camera, Camera, BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], Material[MaterialEventMap], Group[Object3DEventMap], Unit],
    onBeforeRender: js.Function6[WebGLRenderer, Scene[Object3DEventMap], Camera, BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], Material[MaterialEventMap], Group[Object3DEventMap], Unit],
    onAfterRender: js.Function6[WebGLRenderer, Scene[Object3DEventMap], Camera, BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], Material[MaterialEventMap], Group[Object3DEventMap], Unit],
    applyQuaternion: js.Function1[Quaternion, ArrayCamera],
    setRotationFromAxisAngle: js.Function2[Vector3, Double, Unit],
    setRotationFromEuler: js.Function1[Euler, Unit],
    setRotationFromMatrix: js.Function1[Matrix4, Unit],
    setRotationFromQuaternion: js.Function1[Quaternion, Unit],
    rotateOnAxis: js.Function2[Vector3, Double, ArrayCamera],
    rotateOnWorldAxis: js.Function2[Vector3, Double, ArrayCamera],
    rotateX: js.Function1[Double, ArrayCamera],
    rotateY: js.Function1[Double, ArrayCamera],
    rotateZ: js.Function1[Double, ArrayCamera],
    translateOnAxis: js.Function2[Vector3, Double, ArrayCamera],
    translateX: js.Function1[Double, ArrayCamera],
    translateY: js.Function1[Double, ArrayCamera],
    translateZ: js.Function1[Double, ArrayCamera],
    localToWorld: js.Function1[Vector3, Vector3],
    worldToLocal: js.Function1[Vector3, Vector3],
    lookAt: js.Function1[Vector3, Unit],
    remove: js.Function,
    removeFromParent: js.Function0[ArrayCamera],
    clear: js.Function0[ArrayCamera],
    attach: js.Function1[Object3D[Object3DEventMap], ArrayCamera],
    getObjectById: js.Function1[Double, js.UndefOr[Object3D[Object3DEventMap]]],
    getObjectByName: js.Function1[String, js.UndefOr[Object3D[Object3DEventMap]]],
    getObjectByProperty: js.Function2[String, js.Any, js.UndefOr[Object3D[Object3DEventMap]]],
    getObjectsByProperty: js.Function3[String, js.Any, js.UndefOr[js.Array[Object3D[Object3DEventMap]]], js.Array[Object3D[Object3DEventMap]]],
    getWorldPosition: js.Function1[Vector3, Vector3],
    getWorldQuaternion: js.Function1[Quaternion, Quaternion],
    getWorldScale: js.Function1[Vector3, Vector3],
    getWorldDirection: js.Function1[Vector3, Vector3],
    traverse: js.Function1[js.Function1[Object3D[Object3DEventMap], Unit], Unit],
    traverseVisible: js.Function1[js.Function1[Object3D[Object3DEventMap], Unit], Unit],
    traverseAncestors: js.Function1[js.Function1[Object3D[Object3DEventMap], Unit], Unit],
    updateMatrix: js.Function0[Unit],
    updateMatrixWorld: js.Function1[js.UndefOr[Boolean], Unit],
    updateWorldMatrix: js.Function3[Boolean, Boolean, js.UndefOr[Boolean], Unit],
    occlusionTest: Option[js.UndefOr[js.UndefOr[Boolean]]] = None
  ): OmitArrayCameraCameras =
    val _res = js.Dynamic.literal(
      "type" -> `type`.asInstanceOf[js.Any],
      "applyMatrix4" -> applyMatrix4.asInstanceOf[js.Any],
      "clone" -> clone_1.asInstanceOf[js.Any],
      "toJSON" -> toJSON.asInstanceOf[js.Any],
      "matrixAutoUpdate" -> matrixAutoUpdate.asInstanceOf[js.Any],
      "rotation" -> rotation.asInstanceOf[js.Any],
      "uuid" -> uuid.asInstanceOf[js.Any],
      "add" -> add.asInstanceOf[js.Any],
      "copy" -> copy.asInstanceOf[js.Any]
    )
    count.foreach(v => _res.updateDynamic("count")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("matrix")(matrix.asInstanceOf[js.Any])
    _res.updateDynamic("name")(name.asInstanceOf[js.Any])
    _res.updateDynamic("castShadow")(castShadow.asInstanceOf[js.Any])
    _res.updateDynamic("receiveShadow")(receiveShadow.asInstanceOf[js.Any])
    _res.updateDynamic("visible")(visible.asInstanceOf[js.Any])
    _res.updateDynamic("frustumCulled")(frustumCulled.asInstanceOf[js.Any])
    _res.updateDynamic("renderOrder")(renderOrder.asInstanceOf[js.Any])
    _res.updateDynamic("static")(static.asInstanceOf[js.Any])
    _res.updateDynamic("userData")(userData.asInstanceOf[js.Any])
    _res.updateDynamic("layers")(layers.asInstanceOf[js.Any])
    _res.updateDynamic("up")(up.asInstanceOf[js.Any])
    _res.updateDynamic("pivot")(pivot.asInstanceOf[js.Any])
    _res.updateDynamic("children")(children.asInstanceOf[js.Any])
    _res.updateDynamic("animations")(animations.asInstanceOf[js.Any])
    _res.updateDynamic("scale")(scale.asInstanceOf[js.Any])
    _res.updateDynamic("raycast")(raycast.asInstanceOf[js.Any])
    _res.updateDynamic("isArrayCamera")(isArrayCamera.asInstanceOf[js.Any])
    _res.updateDynamic("isMultiViewCamera")(isMultiViewCamera.asInstanceOf[js.Any])
    _res.updateDynamic("isPerspectiveCamera")(isPerspectiveCamera.asInstanceOf[js.Any])
    _res.updateDynamic("fov")(fov.asInstanceOf[js.Any])
    _res.updateDynamic("zoom")(zoom.asInstanceOf[js.Any])
    _res.updateDynamic("near")(near.asInstanceOf[js.Any])
    _res.updateDynamic("far")(far.asInstanceOf[js.Any])
    _res.updateDynamic("focus")(focus.asInstanceOf[js.Any])
    _res.updateDynamic("aspect")(aspect.asInstanceOf[js.Any])
    _res.updateDynamic("view")(view.asInstanceOf[js.Any])
    _res.updateDynamic("filmGauge")(filmGauge.asInstanceOf[js.Any])
    _res.updateDynamic("filmOffset")(filmOffset.asInstanceOf[js.Any])
    _res.updateDynamic("setFocalLength")(setFocalLength.asInstanceOf[js.Any])
    _res.updateDynamic("getFocalLength")(getFocalLength.asInstanceOf[js.Any])
    _res.updateDynamic("getEffectiveFOV")(getEffectiveFOV.asInstanceOf[js.Any])
    _res.updateDynamic("getFilmWidth")(getFilmWidth.asInstanceOf[js.Any])
    _res.updateDynamic("getFilmHeight")(getFilmHeight.asInstanceOf[js.Any])
    _res.updateDynamic("getViewBounds")(getViewBounds.asInstanceOf[js.Any])
    _res.updateDynamic("getViewSize")(getViewSize.asInstanceOf[js.Any])
    _res.updateDynamic("setViewOffset")(setViewOffset.asInstanceOf[js.Any])
    _res.updateDynamic("clearViewOffset")(clearViewOffset.asInstanceOf[js.Any])
    _res.updateDynamic("updateProjectionMatrix")(updateProjectionMatrix.asInstanceOf[js.Any])
    _res.updateDynamic("isCamera")(isCamera.asInstanceOf[js.Any])
    _res.updateDynamic("matrixWorldInverse")(matrixWorldInverse.asInstanceOf[js.Any])
    _res.updateDynamic("projectionMatrix")(projectionMatrix.asInstanceOf[js.Any])
    _res.updateDynamic("projectionMatrixInverse")(projectionMatrixInverse.asInstanceOf[js.Any])
    _res.updateDynamic("coordinateSystem")(coordinateSystem.asInstanceOf[js.Any])
    viewport.foreach(v => _res.updateDynamic("viewport")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("reversedDepth")(reversedDepth.asInstanceOf[js.Any])
    _res.updateDynamic("isObject3D")(isObject3D.asInstanceOf[js.Any])
    _res.updateDynamic("id")(id.asInstanceOf[js.Any])
    _res.updateDynamic("parent")(parent.asInstanceOf[js.Any])
    _res.updateDynamic("position")(position.asInstanceOf[js.Any])
    _res.updateDynamic("quaternion")(quaternion.asInstanceOf[js.Any])
    _res.updateDynamic("modelViewMatrix")(modelViewMatrix.asInstanceOf[js.Any])
    _res.updateDynamic("normalMatrix")(normalMatrix.asInstanceOf[js.Any])
    _res.updateDynamic("matrixWorld")(matrixWorld.asInstanceOf[js.Any])
    _res.updateDynamic("matrixWorldAutoUpdate")(matrixWorldAutoUpdate.asInstanceOf[js.Any])
    _res.updateDynamic("matrixWorldNeedsUpdate")(matrixWorldNeedsUpdate.asInstanceOf[js.Any])
    customDepthMaterial.foreach(v => _res.updateDynamic("customDepthMaterial")(v.asInstanceOf[js.Any]))
    customDistanceMaterial.foreach(v => _res.updateDynamic("customDistanceMaterial")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("onBeforeShadow")(onBeforeShadow.asInstanceOf[js.Any])
    _res.updateDynamic("onAfterShadow")(onAfterShadow.asInstanceOf[js.Any])
    _res.updateDynamic("onBeforeRender")(onBeforeRender.asInstanceOf[js.Any])
    _res.updateDynamic("onAfterRender")(onAfterRender.asInstanceOf[js.Any])
    _res.updateDynamic("applyQuaternion")(applyQuaternion.asInstanceOf[js.Any])
    _res.updateDynamic("setRotationFromAxisAngle")(setRotationFromAxisAngle.asInstanceOf[js.Any])
    _res.updateDynamic("setRotationFromEuler")(setRotationFromEuler.asInstanceOf[js.Any])
    _res.updateDynamic("setRotationFromMatrix")(setRotationFromMatrix.asInstanceOf[js.Any])
    _res.updateDynamic("setRotationFromQuaternion")(setRotationFromQuaternion.asInstanceOf[js.Any])
    _res.updateDynamic("rotateOnAxis")(rotateOnAxis.asInstanceOf[js.Any])
    _res.updateDynamic("rotateOnWorldAxis")(rotateOnWorldAxis.asInstanceOf[js.Any])
    _res.updateDynamic("rotateX")(rotateX.asInstanceOf[js.Any])
    _res.updateDynamic("rotateY")(rotateY.asInstanceOf[js.Any])
    _res.updateDynamic("rotateZ")(rotateZ.asInstanceOf[js.Any])
    _res.updateDynamic("translateOnAxis")(translateOnAxis.asInstanceOf[js.Any])
    _res.updateDynamic("translateX")(translateX.asInstanceOf[js.Any])
    _res.updateDynamic("translateY")(translateY.asInstanceOf[js.Any])
    _res.updateDynamic("translateZ")(translateZ.asInstanceOf[js.Any])
    _res.updateDynamic("localToWorld")(localToWorld.asInstanceOf[js.Any])
    _res.updateDynamic("worldToLocal")(worldToLocal.asInstanceOf[js.Any])
    _res.updateDynamic("lookAt")(lookAt.asInstanceOf[js.Any])
    _res.updateDynamic("remove")(remove.asInstanceOf[js.Any])
    _res.updateDynamic("removeFromParent")(removeFromParent.asInstanceOf[js.Any])
    _res.updateDynamic("clear")(clear.asInstanceOf[js.Any])
    _res.updateDynamic("attach")(attach.asInstanceOf[js.Any])
    _res.updateDynamic("getObjectById")(getObjectById.asInstanceOf[js.Any])
    _res.updateDynamic("getObjectByName")(getObjectByName.asInstanceOf[js.Any])
    _res.updateDynamic("getObjectByProperty")(getObjectByProperty.asInstanceOf[js.Any])
    _res.updateDynamic("getObjectsByProperty")(getObjectsByProperty.asInstanceOf[js.Any])
    _res.updateDynamic("getWorldPosition")(getWorldPosition.asInstanceOf[js.Any])
    _res.updateDynamic("getWorldQuaternion")(getWorldQuaternion.asInstanceOf[js.Any])
    _res.updateDynamic("getWorldScale")(getWorldScale.asInstanceOf[js.Any])
    _res.updateDynamic("getWorldDirection")(getWorldDirection.asInstanceOf[js.Any])
    _res.updateDynamic("traverse")(traverse.asInstanceOf[js.Any])
    _res.updateDynamic("traverseVisible")(traverseVisible.asInstanceOf[js.Any])
    _res.updateDynamic("traverseAncestors")(traverseAncestors.asInstanceOf[js.Any])
    _res.updateDynamic("updateMatrix")(updateMatrix.asInstanceOf[js.Any])
    _res.updateDynamic("updateMatrixWorld")(updateMatrixWorld.asInstanceOf[js.Any])
    _res.updateDynamic("updateWorldMatrix")(updateWorldMatrix.asInstanceOf[js.Any])
    occlusionTest.foreach(v => _res.updateDynamic("occlusionTest")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[OmitArrayCameraCameras]
