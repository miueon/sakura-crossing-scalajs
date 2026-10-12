// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait CurvePathJSON extends CurveJSON:
  var autoClose: Boolean = js.native
  var curves: js.Array[CurveJSON] = js.native

object CurvePathJSON:
  def apply(
    autoClose: Boolean,
    curves: js.Array[CurveJSON],
    metadata: BufferGeometryJSONMetadata,
    arcLengthDivisions: Double,
    `type`: String
  ): CurvePathJSON =
    js.Dynamic.literal(
      "autoClose" -> autoClose.asInstanceOf[js.Any],
      "curves" -> curves.asInstanceOf[js.Any],
      "metadata" -> metadata.asInstanceOf[js.Any],
      "arcLengthDivisions" -> arcLengthDivisions.asInstanceOf[js.Any],
      "type" -> `type`.asInstanceOf[js.Any]
    ).asInstanceOf[CurvePathJSON]
