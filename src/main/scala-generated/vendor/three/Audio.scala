// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Audio")
class Audio[TNode <: AudioNode](listener_1: AudioListener) extends Object3D[Object3DEventMap]:
  var autoplay: Boolean = js.native
  def buffer: js.Object | Null = js.native
  def context: js.Object = js.native
  def detune: Double = js.native
  var duration: js.UndefOr[Double] = js.native
  def filters: js.Array[js.Object] = js.native
  def gain: js.Object = js.native
  def hasPlaybackControl: Boolean = js.native
  def isPlaying: Boolean = js.native
  def listener: AudioListener = js.native
  def loop: Boolean = js.native
  var loopEnd: Double = js.native
  var loopStart: Double = js.native
  var offset: Double = js.native
  def playbackRate: Double = js.native
  def source: js.Object | Null = js.native
  def sourceType: "audioNode" | "buffer" | "empty" | "mediaNode" | "mediaStreamNode" = js.native
  @JSName("clone")
  protected override def clone$raw(recursive: js.UndefOr[Boolean] = js.undefined): Audio[TNode] = js.native
  @JSName("connect")
  protected def connect$raw(): Audio[TNode] = js.native
  @JSName("copy")
  protected override def copy$raw(`object`: Object3D[Object3DEventMap], recursive: js.UndefOr[Boolean] = js.undefined): Audio[TNode] = js.native
  def disconnect(): js.UndefOr[Audio[TNode]] = js.native
  def getDetune(): Double = js.native
  def getFilter(): js.UndefOr[js.Object] = js.native
  def getFilters(): js.Array[js.Object] = js.native
  def getLoop(): Boolean = js.native
  def getOutput(): TNode = js.native
  def getPlaybackRate(): Double = js.native
  def getVolume(): Double = js.native
  def onEnded(): Unit = js.native
  def pause(): js.UndefOr[Audio[TNode]] = js.native
  def play(delay: js.UndefOr[Double] = js.undefined): js.UndefOr[Audio[TNode]] = js.native
  @JSName("setBuffer")
  protected def setBuffer$raw(audioBuffer: js.Object): Audio[TNode] = js.native
  @JSName("setDetune")
  protected def setDetune$raw(value: Double): Audio[TNode] = js.native
  @JSName("setFilter")
  protected def setFilter$raw(filter: js.UndefOr[js.Object] = js.undefined): Audio[TNode] = js.native
  @JSName("setFilters")
  protected def setFilters$raw(value: js.UndefOr[js.Array[js.Object]] = js.undefined): Audio[TNode] = js.native
  def setLoop(value: Boolean): js.UndefOr[Audio[TNode]] = js.native
  @JSName("setLoopEnd")
  protected def setLoopEnd$raw(value: Double): Audio[TNode] = js.native
  @JSName("setLoopStart")
  protected def setLoopStart$raw(value: Double): Audio[TNode] = js.native
  @JSName("setMediaElementSource")
  protected def setMediaElementSource$raw(mediaElement: js.Object): Audio[TNode] = js.native
  @JSName("setMediaStreamSource")
  protected def setMediaStreamSource$raw(mediaStream: js.Object): Audio[TNode] = js.native
  @JSName("setNodeSource")
  protected def setNodeSource$raw(audioNode: js.Object): Audio[TNode] = js.native
  def setPlaybackRate(value: js.UndefOr[Double] = js.undefined): js.UndefOr[Audio[TNode]] = js.native
  @JSName("setVolume")
  protected def setVolume$raw(value: Double): Audio[TNode] = js.native
  def stop(delay: js.UndefOr[Double] = js.undefined): js.UndefOr[Audio[TNode]] = js.native

object Audio:
  extension [TNode <: AudioNode, Self <: Audio[TNode]](self: Self)
    inline def setNodeSource(audioNode: js.Object): Self = self.setNodeSource$raw(audioNode).asInstanceOf[Self]
    inline def setMediaElementSource(mediaElement: js.Object): Self = self.setMediaElementSource$raw(mediaElement).asInstanceOf[Self]
    inline def setMediaStreamSource(mediaStream: js.Object): Self = self.setMediaStreamSource$raw(mediaStream).asInstanceOf[Self]
    inline def setBuffer(audioBuffer: js.Object): Self = self.setBuffer$raw(audioBuffer).asInstanceOf[Self]
    inline def connect(): Self = self.connect$raw().asInstanceOf[Self]
    inline def setFilters(value: js.UndefOr[js.Array[js.Object]] = js.undefined): Self = self.setFilters$raw(value).asInstanceOf[Self]
    inline def setDetune(value: Double): Self = self.setDetune$raw(value).asInstanceOf[Self]
    inline def setFilter(filter: js.UndefOr[js.Object] = js.undefined): Self = self.setFilter$raw(filter).asInstanceOf[Self]
    inline def setLoopStart(value: Double): Self = self.setLoopStart$raw(value).asInstanceOf[Self]
    inline def setLoopEnd(value: Double): Self = self.setLoopEnd$raw(value).asInstanceOf[Self]
    inline def setVolume(value: Double): Self = self.setVolume$raw(value).asInstanceOf[Self]
    inline def copy(source: Audio[AudioNode], recursive: js.UndefOr[Boolean] = js.undefined): Self = self.copy$raw(source, recursive).asInstanceOf[Self]
    inline def clone(recursive: js.UndefOr[Boolean] = js.undefined): Self = self.clone$raw(recursive).asInstanceOf[Self]
