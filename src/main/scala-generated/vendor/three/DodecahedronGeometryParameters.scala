// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

trait DodecahedronGeometryParameters extends js.Object:
  val detail: Double
  val indices: js.Array[Double]
  val radius: Double
  val vertices: js.Array[Double]

object DodecahedronGeometryParameters:
  def apply(
    vertices: js.Array[Double],
    indices: js.Array[Double],
    radius: Double,
    detail: Double
  ): DodecahedronGeometryParameters =
    js.Dynamic.literal(
      "vertices" -> vertices.asInstanceOf[js.Any],
      "indices" -> indices.asInstanceOf[js.Any],
      "radius" -> radius.asInstanceOf[js.Any],
      "detail" -> detail.asInstanceOf[js.Any]
    ).asInstanceOf[DodecahedronGeometryParameters]
