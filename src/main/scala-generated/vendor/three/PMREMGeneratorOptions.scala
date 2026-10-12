// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait PMREMGeneratorOptions extends js.Object:
  var position: js.UndefOr[Vector3] = js.native
  var size: js.UndefOr[Double] = js.native

object PMREMGeneratorOptions:
  def apply(
    size: Option[js.UndefOr[Double]] = None,
    position: Option[js.UndefOr[Vector3]] = None
  ): PMREMGeneratorOptions =
    val _res = js.Dynamic.literal()
    size.foreach(v => _res.updateDynamic("size")(v.asInstanceOf[js.Any]))
    position.foreach(v => _res.updateDynamic("position")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[PMREMGeneratorOptions]
