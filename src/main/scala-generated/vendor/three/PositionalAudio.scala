// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

// #WARN: heritage application of 'Audio' widens the argument of binder 'TNode' to the substituted bound (argument-conformance-unproven); a checker-proven structural relation materialized as synthetic heritage is the lane that keeps such an argument exact
@js.native
@JSImport("three", "PositionalAudio")
class PositionalAudio(listener: AudioListener) extends Audio[AudioNode](js.native: AudioListener):
  def panner: js.Object = js.native
  @JSName("disconnect")
  protected def disconnect$raw(): PositionalAudio = js.native
  def getDistanceModel(): "exponential" | "inverse" | "linear" = js.native
  def getMaxDistance(): Double = js.native
  def getRefDistance(): Double = js.native
  def getRolloffFactor(): Double = js.native
  @JSName("setDirectionalCone")
  protected def setDirectionalCone$raw(coneInnerAngle: Double, coneOuterAngle: Double, coneOuterGain: Double): PositionalAudio = js.native
  @JSName("setDistanceModel")
  protected def setDistanceModel$raw(value: "exponential" | "inverse" | "linear"): PositionalAudio = js.native
  @JSName("setMaxDistance")
  protected def setMaxDistance$raw(value: Double): PositionalAudio = js.native
  @JSName("setRefDistance")
  protected def setRefDistance$raw(value: Double): PositionalAudio = js.native
  @JSName("setRolloffFactor")
  protected def setRolloffFactor$raw(value: Double): PositionalAudio = js.native

object PositionalAudio:
  extension [Self <: PositionalAudio](self: Self)
    inline def setRefDistance(value: Double): Self = self.setRefDistance$raw(value).asInstanceOf[Self]
    inline def setRolloffFactor(value: Double): Self = self.setRolloffFactor$raw(value).asInstanceOf[Self]
    inline def setDistanceModel(value: "exponential" | "inverse" | "linear"): Self = self.setDistanceModel$raw(value).asInstanceOf[Self]
    inline def setMaxDistance(value: Double): Self = self.setMaxDistance$raw(value).asInstanceOf[Self]
    inline def setDirectionalCone(coneInnerAngle: Double, coneOuterAngle: Double, coneOuterGain: Double): Self = self.setDirectionalCone$raw(coneInnerAngle, coneOuterAngle, coneOuterGain).asInstanceOf[Self]
