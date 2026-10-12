// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
trait XRInputSourceArray extends js.Object:
  var length: Double = js.native
  def entries(): facadecontract.std.JSIterableIterator[js.Tuple2[Double, XRInputSource]] = js.native
  def forEach(callbackfn: js.Function3[XRInputSource, Double, js.Array[XRInputSource], Unit], thisArg: js.UndefOr[js.Any] = js.undefined): Unit = js.native
  @JSName(js.Symbol.iterator)
  def jsIterator(): facadecontract.std.JSIterableIterator[XRInputSource] = js.native
  def keys(): facadecontract.std.JSIterableIterator[Double] = js.native
  def values(): facadecontract.std.JSIterableIterator[XRInputSource] = js.native

object XRInputSourceArray:
  def of(
    entries: js.Function0[facadecontract.std.JSIterableIterator[js.Tuple2[Double, XRInputSource]]],
    forEach: js.Function2[js.Function3[XRInputSource, Double, js.Array[XRInputSource], Unit], js.UndefOr[js.Any], Unit],
    jsIterator: js.Function0[facadecontract.std.JSIterableIterator[XRInputSource]],
    keys: js.Function0[facadecontract.std.JSIterableIterator[Double]],
    length: Double,
    values: js.Function0[facadecontract.std.JSIterableIterator[XRInputSource]]
  ): XRInputSourceArray =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.special.objectLiteral(
      "entries" -> entries.asInstanceOf[js.Any],
      "forEach" -> forEach.asInstanceOf[js.Any],
      "keys" -> keys.asInstanceOf[js.Any],
      "length" -> length.asInstanceOf[js.Any],
      "values" -> values.asInstanceOf[js.Any],
      js.Symbol.iterator -> jsIterator.asInstanceOf[js.Any]
    ).asInstanceOf[XRInputSourceArray]
