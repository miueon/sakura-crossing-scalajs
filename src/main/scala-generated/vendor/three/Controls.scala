// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Controls")
abstract class Controls[TEventMap <: ControlsEventMap, TObject <: Object3D[?]]() extends EventDispatcher[TEventMap]:
  var domElement: js.Object | org.scalajs.dom.HTMLElement | Null = js.native
  var enabled: Boolean = js.native
  var `object`: TObject = js.native
  def connect(element: js.Object | org.scalajs.dom.HTMLElement): Unit = js.native
  def disconnect(): Unit = js.native
  def dispose(): Unit = js.native
  def update(delta: Double): Unit = js.native
