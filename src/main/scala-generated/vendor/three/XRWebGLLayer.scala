// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRWebGLLayer extends js.Object:
  def antialias: Boolean = js.native
  var fixedFoveation: js.UndefOr[Double] = js.native
  def framebuffer: js.Object = js.native
  def framebufferHeight: Double = js.native
  def framebufferWidth: Double = js.native
  def ignoreDepthValues: Boolean = js.native
  def addEventListener(`type`: String, callback: js.Object | Null, options: js.UndefOr[Boolean | js.Object] = js.undefined): Unit = js.native
  def dispatchEvent(event: org.scalajs.dom.Event): Boolean = js.native
  def getViewport(view: XRView): js.UndefOr[XRViewport] = js.native
  def removeEventListener(`type`: String, callback: js.Object | Null, options: js.UndefOr[Boolean | js.Object] = js.undefined): Unit = js.native

object XRWebGLLayer:
  def of(
    addEventListener: js.Function3[String, js.Object | Null, js.UndefOr[Boolean | js.Object], Unit],
    antialias: Boolean,
    dispatchEvent: js.Function1[org.scalajs.dom.Event, Boolean],
    framebuffer: js.Object,
    framebufferHeight: Double,
    framebufferWidth: Double,
    getViewport: js.Function1[XRView, js.UndefOr[XRViewport]],
    ignoreDepthValues: Boolean,
    removeEventListener: js.Function3[String, js.Object | Null, js.UndefOr[Boolean | js.Object], Unit],
    fixedFoveation: Option[js.UndefOr[Double]] = None
  ): XRWebGLLayer =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    val _res = js.Dynamic.literal(
      "addEventListener" -> addEventListener.asInstanceOf[js.Any],
      "antialias" -> antialias.asInstanceOf[js.Any],
      "dispatchEvent" -> dispatchEvent.asInstanceOf[js.Any],
      "framebuffer" -> framebuffer.asInstanceOf[js.Any],
      "framebufferHeight" -> framebufferHeight.asInstanceOf[js.Any],
      "framebufferWidth" -> framebufferWidth.asInstanceOf[js.Any],
      "getViewport" -> getViewport.asInstanceOf[js.Any],
      "ignoreDepthValues" -> ignoreDepthValues.asInstanceOf[js.Any],
      "removeEventListener" -> removeEventListener.asInstanceOf[js.Any]
    )
    fixedFoveation.foreach(v => _res.updateDynamic("fixedFoveation")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[XRWebGLLayer]
