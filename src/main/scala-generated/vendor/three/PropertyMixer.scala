// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "PropertyMixer")
class PropertyMixer(binding_1: PropertyBinding, typeName: String, valueSize_1: Double) extends js.Object:
  var binding: PropertyBinding = js.native
  var buffer: js.Array[facadecontract.std.JSUnknown] | js.typedarray.Float64Array = js.native
  var cumulativeWeight: Double = js.native
  var cumulativeWeightAdditive: Double = js.native
  var referenceCount: Double = js.native
  var useCount: Double = js.native
  var valueSize: Double = js.native
  def accumulate(accuIndex: Double, weight: Double): Unit = js.native
  def accumulateAdditive(weight: Double): Unit = js.native
  def apply(accuIndex: Double): Unit = js.native
  def restoreOriginalState(): Unit = js.native
  def saveOriginalState(): Unit = js.native
