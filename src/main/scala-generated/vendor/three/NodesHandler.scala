// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait NodesHandler extends js.Object:
  def build(material: Material[MaterialEventMap], `object`: Object3D[Object3DEventMap], parameters: WebGLProgramParametersWithUniforms): Unit = js.native
  def renderEnd(): Unit = js.native
  def renderStart(scene: Object3D[Object3DEventMap], camera: Camera): Unit = js.native
  def setRenderer(renderer: WebGLRenderer): Unit = js.native

object NodesHandler:
  def of(
    build: js.Function3[Material[MaterialEventMap], Object3D[Object3DEventMap], WebGLProgramParametersWithUniforms, Unit],
    renderEnd: js.Function0[Unit],
    renderStart: js.Function2[Object3D[Object3DEventMap], Camera, Unit],
    setRenderer: js.Function1[WebGLRenderer, Unit]
  ): NodesHandler =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "build" -> build.asInstanceOf[js.Any],
      "renderEnd" -> renderEnd.asInstanceOf[js.Any],
      "renderStart" -> renderStart.asInstanceOf[js.Any],
      "setRenderer" -> setRenderer.asInstanceOf[js.Any]
    ).asInstanceOf[NodesHandler]
