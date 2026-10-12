// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRCompositionLayer extends XRLayer:
  var blendTextureSourceAlpha: Boolean = js.native
  var chromaticAberrationCorrection: js.UndefOr[Boolean] = js.native
  def layout: "default" | "mono" | "stereo" | "stereo-left-right" | "stereo-top-bottom" = js.native
  def mipLevels: Double = js.native
  def needsRedraw: Boolean = js.native
  var quality: "default" | "graphics-optimized" | "text-optimized" = js.native
  var space: XRSpace = js.native
  def destroy(): Unit = js.native
  def onredraw(evt: js.Any): js.Any = js.native
  def removeEventListener[K <: "redraw"](`type`: K, callback: js.Function1[XRCompositionLayerEventMapAt[K], Unit]): Unit = js.native
// #WARN: SHIM-065: Native facade trait 'XRCompositionLayer' cannot receive a builder companion: method 'removeEventListener' declares method-level type parameters [K]
