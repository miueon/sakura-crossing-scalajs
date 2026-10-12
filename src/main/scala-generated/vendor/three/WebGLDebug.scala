// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait WebGLDebug extends js.Object:
  var checkShaderErrors: Boolean = js.native
  var onShaderError: js.Object | Null = js.native

object WebGLDebug:
  def apply(
    checkShaderErrors: Boolean,
    onShaderError: js.Object | Null
  ): WebGLDebug =
    js.Dynamic.literal(
      "checkShaderErrors" -> checkShaderErrors.asInstanceOf[js.Any],
      "onShaderError" -> onShaderError.asInstanceOf[js.Any]
    ).asInstanceOf[WebGLDebug]
