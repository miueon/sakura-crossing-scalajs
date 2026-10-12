// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait IUniform[TValue] extends js.Object:
  var value: TValue = js.native

object IUniform:
  def apply[TValue](
    value: TValue
  ): IUniform[TValue] =
    js.Dynamic.literal("value" -> value.asInstanceOf[js.Any]).asInstanceOf[IUniform[TValue]]
