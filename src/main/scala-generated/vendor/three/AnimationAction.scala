// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "AnimationAction")
class AnimationAction(mixer: AnimationMixer[AnimationMixerEventMap], clip: AnimationClip, localRoot: js.UndefOr[Object3D[Object3DEventMap] | Null] = js.undefined, blendMode_1: js.UndefOr[2500 | 2501] = js.undefined) extends js.Object:
  var blendMode: 2500 | 2501 = js.native
  var clampWhenFinished: Boolean = js.native
  var enabled: Boolean = js.native
  var loop: 2200 | 2201 | 2202 = js.native
  var paused: Boolean = js.native
  var repetitions: Double = js.native
  var time: Double = js.native
  var timeScale: Double = js.native
  var weight: Double = js.native
  var zeroSlopeAtEnd: Boolean = js.native
  var zeroSlopeAtStart: Boolean = js.native
  @JSName("_scheduleFading")
  protected def _scheduleFading$raw(duration: Double, weightNow: Double, weightThen: Double): AnimationAction = js.native
  def crossFadeFrom(fadeOutAction: AnimationAction, duration: Double, warp: js.UndefOr[Boolean] = js.undefined): AnimationAction = js.native
  def crossFadeTo(fadeInAction: AnimationAction, duration: Double, warp: js.UndefOr[Boolean] = js.undefined): AnimationAction = js.native
  def fadeIn(duration: Double): AnimationAction = js.native
  def fadeOut(duration: Double): AnimationAction = js.native
  def getClip(): AnimationClip = js.native
  def getEffectiveTimeScale(): Double = js.native
  def getEffectiveWeight(): Double = js.native
  def getMixer(): AnimationMixer[AnimationMixerEventMap] = js.native
  def getRoot(): Object3D[Object3DEventMap] = js.native
  def halt(duration: Double): AnimationAction = js.native
  def isRunning(): Boolean = js.native
  def isScheduled(): Boolean = js.native
  def play(): AnimationAction = js.native
  def reset(): AnimationAction = js.native
  def setDuration(duration: Double): AnimationAction = js.native
  def setEffectiveTimeScale(timeScale: Double): AnimationAction = js.native
  def setEffectiveWeight(weight: Double): AnimationAction = js.native
  def setLoop(mode: 2200 | 2201 | 2202, repetitions: Double): AnimationAction = js.native
  def startAt(time: Double): AnimationAction = js.native
  def stop(): AnimationAction = js.native
  def stopFading(): AnimationAction = js.native
  def stopWarping(): AnimationAction = js.native
  def syncWith(action: AnimationAction): AnimationAction = js.native
  def warp(startTimeScale: Double, endTimeScale: Double, duration: Double): AnimationAction = js.native

object AnimationAction:
  extension [Self <: AnimationAction](self: Self)
    inline def _scheduleFading(duration: Double, weightNow: Double, weightThen: Double): Self = self._scheduleFading$raw(duration, weightNow, weightThen).asInstanceOf[Self]
