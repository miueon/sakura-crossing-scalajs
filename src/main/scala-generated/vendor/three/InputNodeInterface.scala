// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
trait InputNodeInterface[TValue] extends js.Object:
  var isInputNode: true = js.native
  var precision: "high" | "low" | "medium" | Null = js.native
  var value: TValue = js.native
  def getInputType(builder: default_b44adefb): String | Null = js.native
  @JSName("setPrecision")
  protected def setPrecision$raw(precision: "high" | "low" | "medium"): InputNodeInterface[TValue] = js.native

object InputNodeInterface:
  def of[TValue](
    getInputType: js.Function1[default_b44adefb, String | Null],
    isInputNode: true,
    precision: "high" | "low" | "medium" | Null,
    setPrecision: js.Function1["high" | "low" | "medium", InputNodeInterface[TValue]],
    value: TValue
  ): InputNodeInterface[TValue] =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "getInputType" -> getInputType.asInstanceOf[js.Any],
      "isInputNode" -> isInputNode.asInstanceOf[js.Any],
      "precision" -> precision.asInstanceOf[js.Any],
      "setPrecision" -> setPrecision.asInstanceOf[js.Any],
      "value" -> value.asInstanceOf[js.Any]
    ).asInstanceOf[InputNodeInterface[TValue]]

  extension [TValue, Self <: InputNodeInterface[TValue]](self: Self)
    inline def setPrecision(precision: "high" | "low" | "medium"): Self = self.setPrecision$raw(precision).asInstanceOf[Self]
