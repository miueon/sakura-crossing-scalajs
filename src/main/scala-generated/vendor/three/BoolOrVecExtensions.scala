// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait BoolOrVecExtensions[TNodeType] extends js.Object:
  def all(): NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["bool"] & NumOrBoolExtensions["bool"] & BoolExtensions & BoolOrVecExtensions["bool"] & js.Any = js.native
  def any(): NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["bool"] & NumOrBoolExtensions["bool"] & BoolExtensions & BoolOrVecExtensions["bool"] & js.Any = js.native
  def not(): NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any = js.native

object BoolOrVecExtensions:
  def of[TNodeType](
    all: js.Function0[NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["bool"] & NumOrBoolExtensions["bool"] & BoolExtensions & BoolOrVecExtensions["bool"] & js.Any],
    any: js.Function0[NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["bool"] & NumOrBoolExtensions["bool"] & BoolExtensions & BoolOrVecExtensions["bool"] & js.Any],
    not: js.Function0[NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any]
  ): BoolOrVecExtensions[TNodeType] =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "all" -> all.asInstanceOf[js.Any],
      "any" -> any.asInstanceOf[js.Any],
      "not" -> not.asInstanceOf[js.Any]
    ).asInstanceOf[BoolOrVecExtensions[TNodeType]]
