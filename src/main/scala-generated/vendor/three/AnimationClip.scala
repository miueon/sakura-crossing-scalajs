// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "AnimationClip")
class AnimationClip(name_1: js.UndefOr[String] = js.undefined, duration_1: js.UndefOr[Double] = js.undefined, tracks_1: js.UndefOr[js.Array[KeyframeTrack]] = js.undefined, blendMode_1: js.UndefOr[2500 | 2501] = js.undefined) extends js.Object:
  var blendMode: 2500 | 2501 = js.native
  var duration: Double = js.native
  var name: String = js.native
  var tracks: js.Array[KeyframeTrack] = js.native
  var userData: js.Dictionary[js.Any] = js.native
  def uuid: String = js.native
  @JSName("clone")
  protected def clone$raw(): AnimationClip = js.native
  def optimize(): AnimationClip = js.native
  def resetDuration(): AnimationClip = js.native
  def toJSON(): AnimationClipJSON = js.native
  def trim(): AnimationClip = js.native
  def validate(): Boolean = js.native


object AnimationClip:
  extension [Self <: AnimationClip](self: Self)
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]

  @js.native
  @JSImport("three", "AnimationClip.CreateClipsFromMorphTargetSequences")
  def CreateClipsFromMorphTargetSequences(morphTargets: js.Array[MorphTarget], fps: Double, noLoop: Boolean): js.Array[AnimationClip] = js.native
  @js.native
  @JSImport("three", "AnimationClip.CreateFromMorphTargetSequence")
  def CreateFromMorphTargetSequence(name: String, morphTargetSequence: js.Array[MorphTarget], fps: Double, noLoop: Boolean): AnimationClip = js.native
  @js.native
  @JSImport("three", "AnimationClip.findByName")
  def findByName(objectOrClipArray: Object3D[Object3DEventMap] | js.Array[AnimationClip], name: String): AnimationClip | Null = js.native
  @js.native
  @JSImport("three", "AnimationClip.parse")
  def parse(json: AnimationClipJSON): AnimationClip = js.native
  @js.native
  @JSImport("three", "AnimationClip.toJSON")
  def toJSON(clip: AnimationClip): AnimationClipJSON = js.native
