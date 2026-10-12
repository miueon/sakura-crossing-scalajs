// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "AudioAnalyser")
class AudioAnalyser(audio: Audio[AudioNode], fftSize: js.UndefOr[Double] = js.undefined) extends js.Object:
  var analyser: js.Object = js.native
  var data: js.typedarray.Uint8Array = js.native
  def getAverageFrequency(): Double = js.native
  def getFrequencyData(): js.typedarray.Uint8Array = js.native
