// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Layers")
class Layers() extends js.Object:
  var mask: Double = js.native
  def disable(layer: Double): Unit = js.native
  def disableAll(): Unit = js.native
  def enable(layer: Double): Unit = js.native
  def enableAll(): Unit = js.native
  def isEnabled(layer: Double): Boolean = js.native
  def set(layer: Double): Unit = js.native
  def test(layers: Layers): Boolean = js.native
  def toggle(layer: Double): Unit = js.native
