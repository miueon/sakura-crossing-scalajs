// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait TorusKnotGeometryParameters extends js.Object:
  val p: Double
  val q: Double
  val radialSegments: Double
  val radius: Double
  val tube: Double
  val tubularSegments: Double

object TorusKnotGeometryParameters:
  def apply(
    radius: Double,
    tube: Double,
    tubularSegments: Double,
    radialSegments: Double,
    p: Double,
    q: Double
  ): TorusKnotGeometryParameters =
    js.Dynamic.literal(
      "radius" -> radius.asInstanceOf[js.Any],
      "tube" -> tube.asInstanceOf[js.Any],
      "tubularSegments" -> tubularSegments.asInstanceOf[js.Any],
      "radialSegments" -> radialSegments.asInstanceOf[js.Any],
      "p" -> p.asInstanceOf[js.Any],
      "q" -> q.asInstanceOf[js.Any]
    ).asInstanceOf[TorusKnotGeometryParameters]
