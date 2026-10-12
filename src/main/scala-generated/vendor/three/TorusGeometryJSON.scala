// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait TorusGeometryJSON extends js.Object:
  var arc: Double = js.native
  var radialSegments: Double = js.native
  var radius: Double = js.native
  var tube: Double = js.native
  var tubularSegments: Double = js.native

object TorusGeometryJSON:
  def apply(
    radius: Double,
    tube: Double,
    radialSegments: Double,
    tubularSegments: Double,
    arc: Double
  ): TorusGeometryJSON =
    js.Dynamic.literal(
      "radius" -> radius.asInstanceOf[js.Any],
      "tube" -> tube.asInstanceOf[js.Any],
      "radialSegments" -> radialSegments.asInstanceOf[js.Any],
      "tubularSegments" -> tubularSegments.asInstanceOf[js.Any],
      "arc" -> arc.asInstanceOf[js.Any]
    ).asInstanceOf[TorusGeometryJSON]
