// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait BufferGeometryJSONMetadata extends js.Object:
  var generator: String = js.native
  var `type`: String = js.native
  var version: Double = js.native

object BufferGeometryJSONMetadata:
  def apply(
    version: Double,
    `type`: String,
    generator: String
  ): BufferGeometryJSONMetadata =
    js.Dynamic.literal(
      "version" -> version.asInstanceOf[js.Any],
      "type" -> `type`.asInstanceOf[js.Any],
      "generator" -> generator.asInstanceOf[js.Any]
    ).asInstanceOf[BufferGeometryJSONMetadata]
