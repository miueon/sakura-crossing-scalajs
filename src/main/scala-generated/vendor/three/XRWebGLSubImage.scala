// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRWebGLSubImage extends XRSubImage:
  def colorTexture: js.Object = js.native
  def depthStencilTexture: js.Object = js.native
  def imageIndex: Double = js.native
  def textureHeight: Double = js.native
  def textureWidth: Double = js.native

object XRWebGLSubImage:
  def apply(
    colorTexture: js.Object,
    depthStencilTexture: js.Object,
    imageIndex: Double,
    textureWidth: Double,
    textureHeight: Double,
    viewport: XRViewport
  ): XRWebGLSubImage =
    js.Dynamic.literal(
      "colorTexture" -> colorTexture.asInstanceOf[js.Any],
      "depthStencilTexture" -> depthStencilTexture.asInstanceOf[js.Any],
      "imageIndex" -> imageIndex.asInstanceOf[js.Any],
      "textureWidth" -> textureWidth.asInstanceOf[js.Any],
      "textureHeight" -> textureHeight.asInstanceOf[js.Any],
      "viewport" -> viewport.asInstanceOf[js.Any]
    ).asInstanceOf[XRWebGLSubImage]
