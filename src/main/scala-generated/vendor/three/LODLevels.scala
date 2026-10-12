// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait LODLevels extends js.Object:
  var distance: Double = js.native
  var hysteresis: Double = js.native
  var `object`: Object3D[Object3DEventMap] = js.native

object LODLevels:
  def apply(
    `object`: Object3D[Object3DEventMap],
    distance: Double,
    hysteresis: Double
  ): LODLevels =
    js.Dynamic.literal(
      "object" -> `object`.asInstanceOf[js.Any],
      "distance" -> distance.asInstanceOf[js.Any],
      "hysteresis" -> hysteresis.asInstanceOf[js.Any]
    ).asInstanceOf[LODLevels]
