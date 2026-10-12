// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait ShapeJSON extends PathJSON:
  var holes: js.Array[PathJSON] = js.native
  var uuid: String = js.native

object ShapeJSON:
  def apply(
    uuid: String,
    holes: js.Array[PathJSON],
    currentPoint: js.Tuple2[Double, Double],
    autoClose: Boolean,
    curves: js.Array[CurveJSON],
    metadata: BufferGeometryJSONMetadata,
    arcLengthDivisions: Double,
    `type`: String
  ): ShapeJSON =
    js.Dynamic.literal(
      "uuid" -> uuid.asInstanceOf[js.Any],
      "holes" -> holes.asInstanceOf[js.Any],
      "currentPoint" -> currentPoint.asInstanceOf[js.Any],
      "autoClose" -> autoClose.asInstanceOf[js.Any],
      "curves" -> curves.asInstanceOf[js.Any],
      "metadata" -> metadata.asInstanceOf[js.Any],
      "arcLengthDivisions" -> arcLengthDivisions.asInstanceOf[js.Any],
      "type" -> `type`.asInstanceOf[js.Any]
    ).asInstanceOf[ShapeJSON]
