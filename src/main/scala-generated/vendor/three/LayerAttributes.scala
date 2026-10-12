// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait LayerAttributes extends js.Object:
  var stencil: js.UndefOr[Boolean] = js.native

object LayerAttributes:
  def apply(
    stencil: Option[js.UndefOr[Boolean]] = None
  ): LayerAttributes =
    val _res = js.Dynamic.literal()
    stencil.foreach(v => _res.updateDynamic("stencil")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[LayerAttributes]
