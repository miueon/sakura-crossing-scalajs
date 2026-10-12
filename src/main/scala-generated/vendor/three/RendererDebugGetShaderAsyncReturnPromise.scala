// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait RendererDebugGetShaderAsyncReturnPromise extends js.Object:
  var fragmentShader: String | Null = js.native
  var vertexShader: String | Null = js.native

object RendererDebugGetShaderAsyncReturnPromise:
  def apply(
    fragmentShader: String | Null,
    vertexShader: String | Null
  ): RendererDebugGetShaderAsyncReturnPromise =
    js.Dynamic.literal(
      "fragmentShader" -> fragmentShader.asInstanceOf[js.Any],
      "vertexShader" -> vertexShader.asInstanceOf[js.Any]
    ).asInstanceOf[RendererDebugGetShaderAsyncReturnPromise]
