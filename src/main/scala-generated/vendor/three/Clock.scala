// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Clock")
class Clock(autoStart_1: js.UndefOr[Boolean] = js.undefined) extends js.Object:
  var autoStart: Boolean = js.native
  var elapsedTime: Double = js.native
  var oldTime: Double = js.native
  var running: Boolean = js.native
  var startTime: Double = js.native
  def getDelta(): Double = js.native
  def getElapsedTime(): Double = js.native
  def start(): Unit = js.native
  def stop(): Unit = js.native
