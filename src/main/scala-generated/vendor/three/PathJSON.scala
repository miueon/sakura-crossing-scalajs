// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait PathJSON extends CurvePathJSON:
  var currentPoint: js.Tuple2[Double, Double] = js.native

object PathJSON:
  def apply(
    currentPoint: js.Tuple2[Double, Double],
    autoClose: Boolean,
    curves: js.Array[CurveJSON],
    metadata: BufferGeometryJSONMetadata,
    arcLengthDivisions: Double,
    `type`: String
  ): PathJSON =
    js.Dynamic.literal(
      "currentPoint" -> currentPoint.asInstanceOf[js.Any],
      "autoClose" -> autoClose.asInstanceOf[js.Any],
      "curves" -> curves.asInstanceOf[js.Any],
      "metadata" -> metadata.asInstanceOf[js.Any],
      "arcLengthDivisions" -> arcLengthDivisions.asInstanceOf[js.Any],
      "type" -> `type`.asInstanceOf[js.Any]
    ).asInstanceOf[PathJSON]
