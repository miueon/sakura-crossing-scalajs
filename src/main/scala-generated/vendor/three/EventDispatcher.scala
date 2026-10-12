// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "EventDispatcher")
class EventDispatcher[TEventMap]() extends js.Object:
  def addEventListener[T](`type`: T, listener: js.Function1[js.Object & Event[String, EventDispatcher[TEventMap]], Unit]): Unit = js.native
  def dispatchEvent[T](event: BaseEvent[String] & js.Any): Unit = js.native
  def hasEventListener[T](`type`: T, listener: js.Function1[js.Object & Event[String, EventDispatcher[TEventMap]], Unit]): Boolean = js.native
  def removeEventListener[T](`type`: T, listener: js.Function1[js.Object & Event[String, EventDispatcher[TEventMap]], Unit]): Unit = js.native
