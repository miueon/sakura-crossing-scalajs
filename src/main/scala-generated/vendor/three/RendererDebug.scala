// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait RendererDebug extends js.Object:
  var checkShaderErrors: Boolean = js.native
  var onShaderError: js.Object | Null = js.native
  def getShaderAsync(scene: Scene[Object3DEventMap], camera: Camera, `object`: Object3D[Object3DEventMap]): js.Promise[RendererDebugGetShaderAsyncReturnPromise] = js.native

object RendererDebug:
  def of(
    checkShaderErrors: Boolean,
    getShaderAsync: js.Function3[Scene[Object3DEventMap], Camera, Object3D[Object3DEventMap], js.Promise[RendererDebugGetShaderAsyncReturnPromise]],
    onShaderError: js.Object | Null
  ): RendererDebug =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "checkShaderErrors" -> checkShaderErrors.asInstanceOf[js.Any],
      "getShaderAsync" -> getShaderAsync.asInstanceOf[js.Any],
      "onShaderError" -> onShaderError.asInstanceOf[js.Any]
    ).asInstanceOf[RendererDebug]
