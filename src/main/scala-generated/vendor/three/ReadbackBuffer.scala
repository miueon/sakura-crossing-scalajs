// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait ReadbackBuffer[TEventMap <: ReadbackBufferEventMap] extends js.Object:
  var buffer: js.typedarray.ArrayBuffer | Null = js.native
  def isReadbackBuffer: Boolean = js.native
  var maxByteLength: Double = js.native
  var name: String = js.native
  def addEventListener[T](`type`: T, listener: js.Function1[js.Object & Event[String, ReadbackBuffer[TEventMap]], Unit]): Unit = js.native
  def dispatchEvent[T](event: BaseEvent[String] & js.Any): Unit = js.native
  def dispose(): Unit = js.native
  def hasEventListener[T](`type`: T, listener: js.Function1[js.Object & Event[String, ReadbackBuffer[TEventMap]], Unit]): Boolean = js.native
  def release(): Unit = js.native
  def removeEventListener[T](`type`: T, listener: js.Function1[js.Object & Event[String, ReadbackBuffer[TEventMap]], Unit]): Unit = js.native
// #WARN: SHIM-065: Native facade trait 'ReadbackBuffer' cannot receive a builder companion: method 'addEventListener' declares method-level type parameters [T]
