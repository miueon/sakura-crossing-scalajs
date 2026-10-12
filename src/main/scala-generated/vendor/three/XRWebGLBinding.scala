// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRWebGLBinding extends js.Object:
  def nativeProjectionScaleFactor: Double = js.native
  def createCubeLayer(init: js.UndefOr[XRCubeLayerInit] = js.undefined): XRCubeLayer = js.native
  def createCylinderLayer(init: js.UndefOr[XRCylinderLayerInit] = js.undefined): XRCylinderLayer = js.native
  def createEquirectLayer(init: js.UndefOr[XREquirectLayerInit] = js.undefined): XREquirectLayer = js.native
  def createProjectionLayer(init: js.UndefOr[XRProjectionLayerInit] = js.undefined): XRProjectionLayer = js.native
  def createQuadLayer(init: js.UndefOr[XRQuadLayerInit] = js.undefined): XRQuadLayer = js.native
  def getDepthInformation(view: XRView): js.UndefOr[XRWebGLDepthInformation | Null] = js.native
  def getSubImage(layer: XRCompositionLayer, frame: XRFrame, eye: js.UndefOr["left" | "none" | "right"] = js.undefined): XRWebGLSubImage = js.native
  def getViewSubImage(layer: XRProjectionLayer, view: XRView): XRWebGLSubImage = js.native

object XRWebGLBinding:
  def of(
    createCubeLayer: js.Function1[js.UndefOr[XRCubeLayerInit], XRCubeLayer],
    createCylinderLayer: js.Function1[js.UndefOr[XRCylinderLayerInit], XRCylinderLayer],
    createEquirectLayer: js.Function1[js.UndefOr[XREquirectLayerInit], XREquirectLayer],
    createProjectionLayer: js.Function1[js.UndefOr[XRProjectionLayerInit], XRProjectionLayer],
    createQuadLayer: js.Function1[js.UndefOr[XRQuadLayerInit], XRQuadLayer],
    getDepthInformation: js.Function1[XRView, js.UndefOr[XRWebGLDepthInformation | Null]],
    getSubImage: js.Function3[XRCompositionLayer, XRFrame, js.UndefOr["left" | "none" | "right"], XRWebGLSubImage],
    getViewSubImage: js.Function2[XRProjectionLayer, XRView, XRWebGLSubImage],
    nativeProjectionScaleFactor: Double
  ): XRWebGLBinding =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "createCubeLayer" -> createCubeLayer.asInstanceOf[js.Any],
      "createCylinderLayer" -> createCylinderLayer.asInstanceOf[js.Any],
      "createEquirectLayer" -> createEquirectLayer.asInstanceOf[js.Any],
      "createProjectionLayer" -> createProjectionLayer.asInstanceOf[js.Any],
      "createQuadLayer" -> createQuadLayer.asInstanceOf[js.Any],
      "getDepthInformation" -> getDepthInformation.asInstanceOf[js.Any],
      "getSubImage" -> getSubImage.asInstanceOf[js.Any],
      "getViewSubImage" -> getViewSubImage.asInstanceOf[js.Any],
      "nativeProjectionScaleFactor" -> nativeProjectionScaleFactor.asInstanceOf[js.Any]
    ).asInstanceOf[XRWebGLBinding]
