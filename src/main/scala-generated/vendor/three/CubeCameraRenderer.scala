// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait CubeCameraRenderer extends js.Object:
  var coordinateSystem: 2000 | 2001 = js.native
  var xr: CubeCameraRendererXr = js.native
  def getActiveCubeFace(): Double = js.native
  def getActiveMipmapLevel(): Double = js.native
  def getRenderTarget(): RenderTarget[Texture[js.Any, TextureEventMap], RenderTargetEventMap] | Null = js.native
  def render(scene: Object3D[Object3DEventMap], camera: Camera): Unit = js.native
  def setRenderTarget(renderTarget: WebGLCubeRenderTarget | Null, activeCubeFace: js.UndefOr[Double] = js.undefined, activeMipmapLevel: js.UndefOr[Double] = js.undefined): Unit = js.native

object CubeCameraRenderer:
  def of(
    coordinateSystem: 2000 | 2001,
    getActiveCubeFace: js.Function0[Double],
    getActiveMipmapLevel: js.Function0[Double],
    getRenderTarget: js.Function0[RenderTarget[Texture[js.Any, TextureEventMap], RenderTargetEventMap] | Null],
    render: js.Function2[Object3D[Object3DEventMap], Camera, Unit],
    setRenderTarget: js.Function3[WebGLCubeRenderTarget | Null, js.UndefOr[Double], js.UndefOr[Double], Unit],
    xr: CubeCameraRendererXr
  ): CubeCameraRenderer =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "coordinateSystem" -> coordinateSystem.asInstanceOf[js.Any],
      "getActiveCubeFace" -> getActiveCubeFace.asInstanceOf[js.Any],
      "getActiveMipmapLevel" -> getActiveMipmapLevel.asInstanceOf[js.Any],
      "getRenderTarget" -> getRenderTarget.asInstanceOf[js.Any],
      "render" -> render.asInstanceOf[js.Any],
      "setRenderTarget" -> setRenderTarget.asInstanceOf[js.Any],
      "xr" -> xr.asInstanceOf[js.Any]
    ).asInstanceOf[CubeCameraRenderer]
