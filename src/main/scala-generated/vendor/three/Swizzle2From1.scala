// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait Swizzle2From1[TNumOrBool <: NumOrBoolType] extends js.Object:
  val rr: NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[String] | js.Any) & Swizzle2From1Xx[TNumOrBool] & js.Any
  val ss: NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[String] | js.Any) & Swizzle2From1Xx[TNumOrBool] & js.Any
  val xx: NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[String] | js.Any) & Swizzle2From1Xx[TNumOrBool] & js.Any

object Swizzle2From1:
  def apply[TNumOrBool <: NumOrBoolType](
    xx: NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[String] | js.Any) & Swizzle2From1Xx[TNumOrBool] & js.Any,
    rr: NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[String] | js.Any) & Swizzle2From1Xx[TNumOrBool] & js.Any,
    ss: NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[String] | js.Any) & Swizzle2From1Xx[TNumOrBool] & js.Any
  ): Swizzle2From1[TNumOrBool] =
    js.Dynamic.literal(
      "xx" -> xx.asInstanceOf[js.Any],
      "rr" -> rr.asInstanceOf[js.Any],
      "ss" -> ss.asInstanceOf[js.Any]
    ).asInstanceOf[Swizzle2From1[TNumOrBool]]
