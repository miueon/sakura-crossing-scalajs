// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait TubeGeometryParameters extends js.Object:
  val closed: Boolean
  val path: Curve[Vector3]
  val radialSegments: Double
  val radius: Double
  val tubularSegments: Double

object TubeGeometryParameters:
  def apply(
    path: Curve[Vector3],
    tubularSegments: Double,
    radius: Double,
    radialSegments: Double,
    closed: Boolean
  ): TubeGeometryParameters =
    js.Dynamic.literal(
      "path" -> path.asInstanceOf[js.Any],
      "tubularSegments" -> tubularSegments.asInstanceOf[js.Any],
      "radius" -> radius.asInstanceOf[js.Any],
      "radialSegments" -> radialSegments.asInstanceOf[js.Any],
      "closed" -> closed.asInstanceOf[js.Any]
    ).asInstanceOf[TubeGeometryParameters]
