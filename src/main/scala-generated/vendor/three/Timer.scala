// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Timer")
class Timer() extends js.Object:
  def connect(document: org.scalajs.dom.Document): Unit = js.native
  def disconnect(): Unit = js.native
  def dispose(): Unit = js.native
  def getDelta(): Double = js.native
  def getElapsed(): Double = js.native
  def getTimescale(): Double = js.native
  @JSName("reset")
  protected def reset$raw(): Timer = js.native
  @JSName("setTimescale")
  protected def setTimescale$raw(timescale: Double): Timer = js.native
  @JSName("update")
  protected def update$raw(timestamp: js.UndefOr[Double] = js.undefined): Timer = js.native

object Timer:
  extension [Self <: Timer](self: Self)
    inline def setTimescale(timescale: Double): Self = self.setTimescale$raw(timescale).asInstanceOf[Self]
    inline def reset(): Self = self.reset$raw().asInstanceOf[Self]
    inline def update(timestamp: js.UndefOr[Double] = js.undefined): Self = self.update$raw(timestamp).asInstanceOf[Self]
