// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait default_3d62c4f0 extends js.Object:
  def coordinateSystem: 2000 | 2001 = js.native
  var domElement: org.scalajs.dom.HTMLCanvasElement | org.scalajs.dom.OffscreenCanvas | Null = js.native
  var renderer: Renderer | Null = js.native
  def getDomElement(): org.scalajs.dom.HTMLCanvasElement | org.scalajs.dom.OffscreenCanvas = js.native
  def init(renderer: Renderer): Unit = js.native

object default_3d62c4f0:
  def of(
    coordinateSystem: 2000 | 2001,
    domElement: org.scalajs.dom.HTMLCanvasElement | org.scalajs.dom.OffscreenCanvas | Null,
    getDomElement: js.Function0[org.scalajs.dom.HTMLCanvasElement | org.scalajs.dom.OffscreenCanvas],
    init: js.Function1[Renderer, Unit],
    renderer: Renderer | Null
  ): default_3d62c4f0 =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "coordinateSystem" -> coordinateSystem.asInstanceOf[js.Any],
      "domElement" -> domElement.asInstanceOf[js.Any],
      "getDomElement" -> getDomElement.asInstanceOf[js.Any],
      "init" -> init.asInstanceOf[js.Any],
      "renderer" -> renderer.asInstanceOf[js.Any]
    ).asInstanceOf[default_3d62c4f0]
