// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait WebGLStateBuffers extends js.Object:
  var color: WebGLColorBuffer = js.native
  var depth: WebGLDepthBuffer = js.native
  var stencil: WebGLStencilBuffer = js.native

object WebGLStateBuffers:
  def apply(
    color: WebGLColorBuffer,
    depth: WebGLDepthBuffer,
    stencil: WebGLStencilBuffer
  ): WebGLStateBuffers =
    js.Dynamic.literal(
      "color" -> color.asInstanceOf[js.Any],
      "depth" -> depth.asInstanceOf[js.Any],
      "stencil" -> stencil.asInstanceOf[js.Any]
    ).asInstanceOf[WebGLStateBuffers]
