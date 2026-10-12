// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

// #WARN: Disambiguated erasure-colliding overload 'clipAction' with @targetName("clipAction_1") and @JSName("clipAction")
@js.native
@JSImport("three", "AnimationMixer")
class AnimationMixer[TEventMap <: AnimationMixerEventMap](root: AnimationObjectGroup | Object3D[Object3DEventMap]) extends EventDispatcher[TEventMap]:
  var stats: AnimationMixerStats = js.native
  var time: Double = js.native
  var timeScale: Double = js.native
  def clipAction(clip: AnimationClip, optionalRoot: js.UndefOr[AnimationObjectGroup | Object3D[Object3DEventMap]] = js.undefined, blendMode: js.UndefOr[2500 | 2501] = js.undefined): AnimationAction = js.native
  // #WARN: Disambiguated erasure-colliding overload 'clipAction' with @targetName("clipAction_1") and @JSName("clipAction")
  @scala.annotation.targetName("clipAction_1")
  @JSName("clipAction")
  def clipAction(clip: String | AnimationClip, optionalRoot: js.UndefOr[AnimationObjectGroup | Object3D[Object3DEventMap]], blendMode: js.UndefOr[2500 | 2501]): AnimationAction | Null = js.native
  def existingAction(clip: String | AnimationClip, optionalRoot: js.UndefOr[AnimationObjectGroup | Object3D[Object3DEventMap]] = js.undefined): AnimationAction | Null = js.native
  def getRoot(): AnimationObjectGroup | Object3D[Object3DEventMap] = js.native
  def setTime(time: Double): AnimationMixer[AnimationMixerEventMap] = js.native
  def stopAllAction(): AnimationMixer[AnimationMixerEventMap] = js.native
  def uncacheAction(clip: String | AnimationClip, optionalRoot: js.UndefOr[AnimationObjectGroup | Object3D[Object3DEventMap]] = js.undefined): Unit = js.native
  def uncacheClip(clip: AnimationClip): Unit = js.native
  def uncacheRoot(root: AnimationObjectGroup | Object3D[Object3DEventMap]): Unit = js.native
  def update(deltaTime: Double): AnimationMixer[AnimationMixerEventMap] = js.native
