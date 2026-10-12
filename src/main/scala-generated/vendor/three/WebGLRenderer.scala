// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "WebGLRenderer")
class WebGLRenderer(parameters: js.UndefOr[WebGLRendererParameters] = js.undefined) extends js.Object:
  var autoClear: Boolean = js.native
  var autoClearColor: Boolean = js.native
  var autoClearDepth: Boolean = js.native
  var autoClearStencil: Boolean = js.native
  var capabilities: WebGLCapabilities = js.native
  var clippingPlanes: js.Array[Plane] = js.native
  def coordinateSystem: 2000 = js.native
  var debug: WebGLDebug = js.native
  var domElement: org.scalajs.dom.HTMLCanvasElement = js.native
  var extensions: WebGLExtensions = js.native
  var info: WebGLInfo = js.native
  var localClippingEnabled: Boolean = js.native
  var outputColorSpace: String = js.native
  var properties: WebGLProperties = js.native
  var renderLists: WebGLRenderLists = js.native
  var shadowMap: WebGLShadowMap = js.native
  var sortObjects: Boolean = js.native
  var state: WebGLState = js.native
  var toneMapping: 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 = js.native
  var toneMappingExposure: Double = js.native
  var transmissionResolutionScale: Double = js.native
  var xr: WebXRManager[WebXRManagerEventMap] = js.native
  def clear(color: js.UndefOr[Boolean] = js.undefined, depth: js.UndefOr[Boolean] = js.undefined, stencil: js.UndefOr[Boolean] = js.undefined): Unit = js.native
  def clearColor(): Unit = js.native
  def clearDepth(): Unit = js.native
  def clearStencil(): Unit = js.native
  def compile(scene: Object3D[Object3DEventMap], camera: Camera, targetScene: js.UndefOr[Scene[Object3DEventMap] | Null] = js.undefined): js.Object = js.native
  def compileAsync(scene: Object3D[Object3DEventMap], camera: Camera, targetScene: js.UndefOr[Scene[Object3DEventMap] | Null] = js.undefined): js.Promise[Object3D[Object3DEventMap]] = js.native
  def copyFramebufferToTexture(texture: Texture[js.Any, TextureEventMap], position: js.UndefOr[Vector2 | Null] = js.undefined, level: js.UndefOr[Double] = js.undefined): Unit = js.native
  def copyTextureToTexture(srcTexture: Texture[js.Any, TextureEventMap], dstTexture: Texture[js.Any, TextureEventMap], srcRegion: js.UndefOr[Box2 | Box3 | Null] = js.undefined, dstPosition: js.UndefOr[Vector2 | Vector3 | Null] = js.undefined, srcLevel: js.UndefOr[Double] = js.undefined, dstLevel: js.UndefOr[Double] = js.undefined): Unit = js.native
  def dispose(): Unit = js.native
  def forceContextLoss(): Unit = js.native
  def forceContextRestore(): Unit = js.native
  def getActiveCubeFace(): Double = js.native
  def getActiveMipmapLevel(): Double = js.native
  def getClearAlpha(): Double = js.native
  def getClearColor(target: Color): Color = js.native
  def getContext(): js.Object = js.native
  def getContextAttributes(): js.Object = js.native
  def getCurrentViewport(target: Vector4): Vector4 = js.native
  def getDrawingBufferSize(target: Vector2): Vector2 = js.native
  def getPixelRatio(): Double = js.native
  def getRenderTarget(): WebGLRenderTarget[Texture[js.Any, TextureEventMap]] | Null = js.native
  def getScissor(target: Vector4): Vector4 = js.native
  def getScissorTest(): Boolean = js.native
  def getSize(target: Vector2): Vector2 = js.native
  def getViewport(target: Vector4): Vector4 = js.native
  def initRenderTarget(target: WebGLRenderTarget[Texture[js.Any, TextureEventMap]]): Unit = js.native
  def initTexture(texture: Texture[js.Any, TextureEventMap]): Unit = js.native
  def readRenderTargetPixels(renderTarget: WebGLRenderTarget[Texture[js.Any, TextureEventMap]] | WebGLRenderTarget[js.Array[Texture[js.Any, TextureEventMap]]], x: Double, y: Double, width: Double, height: Double, buffer: js.typedarray.Float32Array | js.typedarray.Float64Array | js.typedarray.Int16Array | js.typedarray.Int32Array | js.typedarray.Int8Array | js.typedarray.Uint16Array | js.typedarray.Uint32Array | js.typedarray.Uint8Array | js.typedarray.Uint8ClampedArray, activeCubeFaceIndex: js.UndefOr[Double] = js.undefined, textureIndex: js.UndefOr[Double] = js.undefined): Unit = js.native
  def readRenderTargetPixelsAsync(renderTarget: WebGLRenderTarget[Texture[js.Any, TextureEventMap]] | WebGLRenderTarget[js.Array[Texture[js.Any, TextureEventMap]]], x: Double, y: Double, width: Double, height: Double, buffer: js.typedarray.Float32Array | js.typedarray.Float64Array | js.typedarray.Int16Array | js.typedarray.Int32Array | js.typedarray.Int8Array | js.typedarray.Uint16Array | js.typedarray.Uint32Array | js.typedarray.Uint8Array | js.typedarray.Uint8ClampedArray, activeCubeFaceIndex: js.UndefOr[Double] = js.undefined, textureIndex: js.UndefOr[Double] = js.undefined): js.Promise[js.typedarray.Float32Array | js.typedarray.Float64Array | js.typedarray.Int16Array | js.typedarray.Int32Array | js.typedarray.Int8Array | js.typedarray.Uint16Array | js.typedarray.Uint32Array | js.typedarray.Uint8Array | js.typedarray.Uint8ClampedArray] = js.native
  def render(scene: Object3D[Object3DEventMap], camera: Camera): Unit = js.native
  def renderBufferDirect(camera: Camera, scene: Scene[Object3DEventMap], geometry: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], material: Material[MaterialEventMap], `object`: Object3D[Object3DEventMap], group: GeometryGroup): Unit = js.native
  def resetState(): Unit = js.native
  def setAnimationLoop(callback: js.Function2[Double, XRFrame, Unit] | Null): Unit = js.native
  def setClearAlpha(alpha: Double): Unit = js.native
  def setClearColor(color: String | Double | Color, alpha: js.UndefOr[Double] = js.undefined): Unit = js.native
  def setDrawingBufferSize(width: Double, height: Double, pixelRatio: Double): Unit = js.native
  def setEffects(effects: js.Array[Effect] | Null): Unit = js.native
  def setNodesHandler(nodesHandler: NodesHandler): Unit = js.native
  def setOpaqueSort(method: js.Function2[js.Any, js.Any, Double] | Null): Unit = js.native
  def setPixelRatio(value: Double): Unit = js.native
  def setRenderTarget(renderTarget: WebGLRenderTarget[Texture[js.Any, TextureEventMap]] | WebGLRenderTarget[js.Array[Texture[js.Any, TextureEventMap]]] | Null, activeCubeFace: js.UndefOr[Double] = js.undefined, activeMipmapLevel: js.UndefOr[Double] = js.undefined): Unit = js.native
  def setScissor(x: Double | Vector4, y: js.UndefOr[Double] = js.undefined, width: js.UndefOr[Double] = js.undefined, height: js.UndefOr[Double] = js.undefined): Unit = js.native
  def setScissorTest(enable: Boolean): Unit = js.native
  def setSize(width: Double, height: Double, updateStyle: js.UndefOr[Boolean] = js.undefined): Unit = js.native
  def setTransparentSort(method: js.Function2[js.Any, js.Any, Double] | Null): Unit = js.native
  def setViewport(x: Double | Vector4, y: js.UndefOr[Double] = js.undefined, width: js.UndefOr[Double] = js.undefined, height: js.UndefOr[Double] = js.undefined): Unit = js.native
