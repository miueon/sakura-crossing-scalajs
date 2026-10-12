// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait CurveJSON extends js.Object:
  var arcLengthDivisions: Double = js.native
  var metadata: BufferGeometryJSONMetadata = js.native
  var `type`: String = js.native

object CurveJSON:
  def apply(
    metadata: BufferGeometryJSONMetadata,
    arcLengthDivisions: Double,
    `type`: String
  ): CurveJSON =
    js.Dynamic.literal(
      "metadata" -> metadata.asInstanceOf[js.Any],
      "arcLengthDivisions" -> arcLengthDivisions.asInstanceOf[js.Any],
      "type" -> `type`.asInstanceOf[js.Any]
    ).asInstanceOf[CurveJSON]
