// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

// #WARN: Disambiguated erasure-colliding overload 'getArrayBufferAsync' with @targetName("getArrayBufferAsync_1") and @JSName("getArrayBufferAsync")
@js.native
trait Renderer extends js.Object:
  var alpha: Boolean = js.native
  var autoClear: Boolean = js.native
  var autoClearColor: Boolean = js.native
  var autoClearDepth: Boolean = js.native
  var autoClearStencil: Boolean = js.native
  var backend: default_3d62c4f0 = js.native
  def compile: js.Function3[Object3D[Object3DEventMap], Camera, js.UndefOr[Scene[Object3DEventMap] | Null], js.Promise[Unit]] = js.native
  var contextNode: ContextNodeInterface[js.Any] & NodeClass[NodeClassEventMap] & NodeElements & js.Any = js.native
  def coordinateSystem: 2000 | 2001 = js.native
  def currentColorSpace: String = js.native
  def currentSamples: Double = js.native
  def currentToneMapping: 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 = js.native
  var debug: RendererDebug = js.native
  var depth: Boolean = js.native
  def domElement: org.scalajs.dom.HTMLCanvasElement = js.native
  var highPrecision: Boolean = js.native
  var info: Info = js.native
  def initialized: Boolean = js.native
  var inspector: InspectorBase[InspectorBaseEventMap] = js.native
  def isOutputTarget: Boolean = js.native
  def isRenderer: Boolean = js.native
  var library: NodeLibrary = js.native
  var lighting: Lighting = js.native
  def logarithmicDepthBuffer: Boolean = js.native
  def needsFrameBufferTarget: Boolean = js.native
  var `opaque`: Boolean = js.native
  var outputColorSpace: String = js.native
  def reversedDepthBuffer: Boolean = js.native
  def samples: Double = js.native
  var shadowMap: RendererShadowMap = js.native
  var sortObjects: Boolean = js.native
  var stencil: Boolean = js.native
  var toneMapping: 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 = js.native
  var toneMappingExposure: Double = js.native
  var `transparent`: Boolean = js.native
  var xr: XRManager[XRManagerEventMap] = js.native
  def clear(color: js.UndefOr[Boolean] = js.undefined, depth: js.UndefOr[Boolean] = js.undefined, stencil: js.UndefOr[Boolean] = js.undefined): Unit = js.native
  def clearAsync(color: js.UndefOr[Boolean] = js.undefined, depth: js.UndefOr[Boolean] = js.undefined, stencil: js.UndefOr[Boolean] = js.undefined): js.Promise[Unit] = js.native
  def clearColor(): Unit = js.native
  def clearColorAsync(): js.Promise[Unit] = js.native
  def clearDepth(): Unit = js.native
  def clearDepthAsync(): js.Promise[Unit] = js.native
  def clearStencil(): Unit = js.native
  def clearStencilAsync(): js.Promise[Unit] = js.native
  def compileAsync(scene: Object3D[Object3DEventMap], camera: Camera, targetScene: js.UndefOr[Scene[Object3DEventMap] | Null] = js.undefined): js.Promise[Unit] = js.native
  def compute(computeNodes: default_5e5b9ffc | js.Array[default_5e5b9ffc], dispatchSize: js.UndefOr[Double | IndirectStorageBufferAttribute | js.Array[Double]] = js.undefined): js.UndefOr[js.Promise[Unit]] = js.native
  def computeAsync(computeNodes: default_5e5b9ffc | js.Array[default_5e5b9ffc], dispatchSize: js.UndefOr[Double | IndirectStorageBufferAttribute | js.Array[Double]] = js.undefined): js.Promise[Unit] = js.native
  def copyFramebufferToTexture(framebufferTexture: FramebufferTexture, rectangle: js.UndefOr[Vector2 | Vector4 | Null] = js.undefined): Unit = js.native
  def copyTextureToTexture(srcTexture: Texture[js.Any, TextureEventMap], dstTexture: Texture[js.Any, TextureEventMap], srcRegion: js.UndefOr[Box2 | Box3 | Null] = js.undefined, dstPosition: js.UndefOr[Vector2 | Vector3 | Null] = js.undefined, srcLevel: js.UndefOr[Double] = js.undefined, dstLevel: js.UndefOr[Double] = js.undefined): Unit = js.native
  def dispose(): Unit = js.native
  def getActiveCubeFace(): Double = js.native
  def getActiveMipmapLevel(): Double = js.native
  def getAnimationLoop(): js.Function2[Double, js.UndefOr[XRFrame], Unit] | Null = js.native
  def getArrayBufferAsync(attribute: BufferAttribute[BufferAttributeEventMap], target: js.UndefOr[Null] = js.undefined, offset: js.UndefOr[Double] = js.undefined, count: js.UndefOr[Double] = js.undefined): js.Promise[js.typedarray.ArrayBuffer] = js.native
  // #WARN: Disambiguated erasure-colliding overload 'getArrayBufferAsync' with @targetName("getArrayBufferAsync_1") and @JSName("getArrayBufferAsync")
  @scala.annotation.targetName("getArrayBufferAsync_1")
  @JSName("getArrayBufferAsync")
  def getArrayBufferAsync[TTarget <: ReadbackBuffer[?] | js.typedarray.ArrayBuffer](attribute: BufferAttribute[BufferAttributeEventMap], target: TTarget, offset: js.UndefOr[Double], count: js.UndefOr[Double]): js.Promise[TTarget] = js.native
  def getCanvasTarget(): CanvasTarget[CanvasTargetEventMap] = js.native
  def getClearAlpha(): Double = js.native
  def getClearColor(target: Color): Color = js.native
  def getClearDepth(): Double = js.native
  def getClearStencil(): Double = js.native
  def getColorBufferType(): 1009 | 1010 | 1011 | 1012 | 1013 | 1014 | 1015 | 1016 | 1017 | 1018 | 1020 | 35899 | 35902 = js.native
  def getContext(): facadecontract.std.JSUnknown = js.native
  def getDrawingBufferSize(target: Vector2): Vector2 = js.native
  def getMaxAnisotropy(): Double = js.native
  def getMRT(): MRTNode = js.native
  def getOutputBufferType(): 1009 | 1010 | 1011 | 1012 | 1013 | 1014 | 1015 | 1016 | 1017 | 1018 | 1020 | 35899 | 35902 = js.native
  def getOutputRenderTarget(): RenderTarget[Texture[js.Any, TextureEventMap], RenderTargetEventMap] | Null = js.native
  def getPixelRatio(): Double = js.native
  def getRenderObjectFunction(): js.Function9[Object3D[Object3DEventMap], Scene[Object3DEventMap], Camera, BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], Material[MaterialEventMap], GeometryGroup | Null, LightsNode, ClippingContext, js.UndefOr[String | Null], Unit] | Null = js.native
  def getRenderTarget(): RenderTarget[Texture[js.Any, TextureEventMap], RenderTargetEventMap] | Null = js.native
  def getScissor(target: Vector4): Vector4 = js.native
  def getScissorTest(): Boolean = js.native
  def getSize(target: Vector2): Vector2 = js.native
  def getViewport(target: Vector4): Vector4 = js.native
  def hasCompatibility(name: String): Boolean = js.native
  def hasFeature(name: String): Boolean = js.native
  def hasFeatureAsync(name: String): js.Promise[Boolean] = js.native
  def hasInitialized(): Boolean = js.native
  def init(): js.Promise[Renderer] = js.native
  def initRenderTarget(renderTarget: RenderTarget[Texture[js.Any, TextureEventMap], RenderTargetEventMap]): Unit = js.native
  def initTexture(texture: Texture[js.Any, TextureEventMap]): Unit = js.native
  def initTextureAsync(texture: Texture[js.Any, TextureEventMap]): js.Promise[Unit] = js.native
  def isOccluded(`object`: Object3D[Object3DEventMap]): Boolean = js.native
  def onDeviceLost(info: DeviceLostInfo): Unit = js.native
  def onError(errorMessage: String): Unit = js.native
  def readRenderTargetPixelsAsync(renderTarget: RenderTarget[Texture[js.Any, TextureEventMap], RenderTargetEventMap], x: Double, y: Double, width: Double, height: Double, textureIndex: js.UndefOr[Double] = js.undefined, faceIndex: js.UndefOr[Double] = js.undefined): js.Promise[js.typedarray.Float32Array | js.typedarray.Float64Array | js.typedarray.Int16Array | js.typedarray.Int32Array | js.typedarray.Int8Array | js.typedarray.Uint16Array | js.typedarray.Uint32Array | js.typedarray.Uint8Array | js.typedarray.Uint8ClampedArray] = js.native
  def render(scene: Object3D[Object3DEventMap], camera: Camera): Unit = js.native
  def renderAsync(scene: Object3D[Object3DEventMap], camera: Camera): js.Promise[Unit] = js.native
  def renderObject(`object`: Object3D[Object3DEventMap], scene: Scene[Object3DEventMap], camera: Camera, geometry: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], material: Material[MaterialEventMap], group: GeometryGroup | Null, lightsNode: LightsNode, clippingContext: js.UndefOr[ClippingContext | Null] = js.undefined, passId: js.UndefOr[String | Null] = js.undefined): Unit = js.native
  def resolveTimestampsAsync(`type`: js.UndefOr["compute" | "render"] = js.undefined): js.Promise[js.UndefOr[Double]] = js.native
  def setAnimationLoop(callback: js.Function2[Double, js.UndefOr[XRFrame], Unit] | Null): js.Promise[Unit] = js.native
  def setCanvasTarget(canvasTarget: CanvasTarget[CanvasTargetEventMap]): Unit = js.native
  def setClearAlpha(alpha: Double): Unit = js.native
  def setClearColor(color: String | Double | Color, alpha: js.UndefOr[Double] = js.undefined): Unit = js.native
  def setClearDepth(depth: Double): Unit = js.native
  def setClearStencil(stencil: Double): Unit = js.native
  def setDrawingBufferSize(width: Double, height: Double, pixelRatio: Double): Unit = js.native
  def setMRT(mrt: MRTNode | Null): Renderer = js.native
  def setOpaqueSort(method: js.Function2[RenderItem_e5df3480, RenderItem_e5df3480, Double] | Null): Unit = js.native
  def setOutputRenderTarget(renderTarget: RenderTarget[Texture[js.Any, TextureEventMap], RenderTargetEventMap] | Null): Unit = js.native
  def setPixelRatio(value: js.UndefOr[Double] = js.undefined): Unit = js.native
  def setRenderObjectFunction(renderObjectFunction: js.Function9[Object3D[Object3DEventMap], Scene[Object3DEventMap], Camera, BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], Material[MaterialEventMap], GeometryGroup | Null, LightsNode, ClippingContext, js.UndefOr[String | Null], Unit] | Null): Unit = js.native
  def setRenderTarget(renderTarget: RenderTarget[Texture[js.Any, TextureEventMap], RenderTargetEventMap] | Null, activeCubeFace: js.UndefOr[Double] = js.undefined, activeMipmapLevel: js.UndefOr[Double] = js.undefined): Unit = js.native
  def setScissor(x: Vector4): Unit = js.native
  def setScissor(x: Double, y: Double, width: Double, height: Double): Unit = js.native
  def setScissorTest(boolean: Boolean): Unit = js.native
  def setSize(width: Double, height: Double, updateStyle: js.UndefOr[Boolean] = js.undefined): Unit = js.native
  def setTransparentSort(method: js.Function2[RenderItem_e5df3480, RenderItem_e5df3480, Double] | Null): Unit = js.native
  def setViewport(x: Vector4): Unit = js.native
  def setViewport(x: Double, y: Double, width: Double, height: Double, minDepth: js.UndefOr[Double] = js.undefined, maxDepth: js.UndefOr[Double] = js.undefined): Unit = js.native
  def waitForGPU(): js.Promise[Unit] = js.native
// #WARN: SHIM-065: Native facade trait 'Renderer' cannot receive a builder companion: JS property 'getArrayBufferAsync' is written by 2 members
