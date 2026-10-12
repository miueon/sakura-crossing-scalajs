// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait RaycasterParameters extends js.Object:
  var Line: RaycasterParametersLine = js.native
  var Line2: js.UndefOr[RaycasterParametersLine] = js.native
  var LOD: js.Any = js.native
  var Mesh: js.Any = js.native
  var Points: RaycasterParametersLine = js.native
  var Sprite: js.Any = js.native

object RaycasterParameters:
  def apply(
    Mesh: js.Any,
    Line: RaycasterParametersLine,
    Line2: js.UndefOr[RaycasterParametersLine] = js.undefined,
    LOD: js.Any,
    Points: RaycasterParametersLine,
    Sprite: js.Any
  ): RaycasterParameters =
    val _res = js.Dynamic.literal(
      "Mesh" -> Mesh.asInstanceOf[js.Any],
      "Line" -> Line.asInstanceOf[js.Any]
    )
    Line2.foreach(v => _res.updateDynamic("Line2")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("LOD")(LOD.asInstanceOf[js.Any])
    _res.updateDynamic("Points")(Points.asInstanceOf[js.Any])
    _res.updateDynamic("Sprite")(Sprite.asInstanceOf[js.Any])
    _res.asInstanceOf[RaycasterParameters]
