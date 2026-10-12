// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "LightShadow")
abstract class LightShadow[TCamera <: Camera]() extends js.Object:
  var autoUpdate: Boolean = js.native
  var bias: Double = js.native
  var biasNode: NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["float"] & NumOrBoolExtensions["float"] & FloatExtensions & NumExtensions["float"] & FloatOrVecExtensions["float"] & js.Any | Null = js.native
  var blurSamples: Double = js.native
  var camera: TCamera = js.native
  var intensity: Double = js.native
  var map: RenderTarget[Texture[js.Any, TextureEventMap], RenderTargetEventMap] | Null = js.native
  var mapPass: RenderTarget[Texture[js.Any, TextureEventMap], RenderTargetEventMap] | Null = js.native
  var mapSize: Vector2 = js.native
  var mapType: 1009 | 1010 | 1011 | 1012 | 1013 | 1014 | 1015 | 1016 | 1017 | 1018 | 1020 | 35899 | 35902 = js.native
  var matrix: Matrix4 = js.native
  var needsUpdate: Boolean = js.native
  var normalBias: Double = js.native
  var radius: Double = js.native
  var shadowNode: js.UndefOr[NodeClass[NodeClassEventMap] & NodeElements & js.Any] = js.native
  @JSName("clone")
  protected def clone$raw(): LightShadow[TCamera] = js.native
  @JSName("copy")
  protected def copy$raw(source: LightShadow[Camera]): LightShadow[TCamera] = js.native
  def dispose(): Unit = js.native
  def getFrameExtents(): Vector2 = js.native
  def getFrustum(): Frustum = js.native
  def getViewport(viewportIndex: Double): Vector4 = js.native
  def getViewportCount(): Double = js.native
  def toJSON(): LightShadowJSON = js.native
  def updateMatrices(light: Light): Unit = js.native

object LightShadow:
  extension [TCamera <: Camera, Self <: LightShadow[TCamera]](self: Self)
    inline def copy(source: LightShadow[Camera]): Self = self.copy$raw(source).asInstanceOf[Self]
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]
