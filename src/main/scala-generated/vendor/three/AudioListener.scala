// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "AudioListener")
class AudioListener() extends Object3D[Object3DEventMap]:
  def context: js.Object = js.native
  def filter: js.Object | Null = js.native
  def gain: js.Object = js.native
  def timeDelta: Double = js.native
  def getFilter(): js.Object | Null = js.native
  def getInput(): js.Object = js.native
  def getMasterVolume(): Double = js.native
  @JSName("removeFilter")
  protected def removeFilter$raw(): AudioListener = js.native
  @JSName("setFilter")
  protected def setFilter$raw(value: js.Object): AudioListener = js.native
  @JSName("setMasterVolume")
  protected def setMasterVolume$raw(value: Double): AudioListener = js.native

object AudioListener:
  extension [Self <: AudioListener](self: Self)
    inline def removeFilter(): Self = self.removeFilter$raw().asInstanceOf[Self]
    inline def setFilter(value: js.Object): Self = self.setFilter$raw(value).asInstanceOf[Self]
    inline def setMasterVolume(value: Double): Self = self.setMasterVolume$raw(value).asInstanceOf[Self]
