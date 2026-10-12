// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "WebXRDepthSensing")
class WebXRDepthSensing() extends js.Object:
  var depthFar: Double = js.native
  var depthNear: Double = js.native
  var mesh: Mesh[BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], Material[MaterialEventMap] | js.Array[Material[MaterialEventMap]], Object3DEventMap] | Null = js.native
  var texture: ExternalTexture | Null = js.native
  def getDepthTexture(): ExternalTexture | Null = js.native
  def getMesh(cameraXR: OmitArrayCameraCameras & WebXRDepthSensingGetMeshCameraXR): Mesh[BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], Material[MaterialEventMap] | js.Array[Material[MaterialEventMap]], Object3DEventMap] | Null = js.native
  def init(depthData: XRWebGLDepthInformation, renderState: XRRenderState): Unit = js.native
  def reset(): Unit = js.native
