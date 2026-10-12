// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "ImageUtils")
class ImageUtils() extends js.Object

// #WARN: Disambiguated erasure-colliding overload 'sRGBToLinear' with @targetName("sRGBToLinear_1") and @JSName("sRGBToLinear")
@js.native
@JSImport("three", "ImageUtils")
object ImageUtils extends js.Object:
  def getDataURL(image: js.Object | org.scalajs.dom.HTMLCanvasElement | org.scalajs.dom.HTMLImageElement | org.scalajs.dom.OffscreenCanvas, `type`: js.UndefOr[String] = js.undefined): String = js.native
  def sRGBToLinear(image: js.Object | org.scalajs.dom.HTMLCanvasElement | org.scalajs.dom.HTMLImageElement): org.scalajs.dom.HTMLCanvasElement = js.native
  // #WARN: Disambiguated erasure-colliding overload 'sRGBToLinear' with @targetName("sRGBToLinear_1") and @JSName("sRGBToLinear")
  @scala.annotation.targetName("sRGBToLinear_1")
  @JSName("sRGBToLinear")
  def sRGBToLinear(image: js.Object): ImageUtilsCompanionSRGBToLinearReturn = js.native
