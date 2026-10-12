// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait NodeJSONMetadata extends js.Object:
  var generator: "Node.toJSON" = js.native
  var `type`: "Node" = js.native
  var version: Double = js.native

object NodeJSONMetadata:
  def apply(
    version: Double,
    `type`: "Node" = "Node",
    generator: "Node.toJSON" = "Node.toJSON"
  ): NodeJSONMetadata =
    js.Dynamic.literal(
      "version" -> version.asInstanceOf[js.Any],
      "type" -> `type`.asInstanceOf[js.Any],
      "generator" -> generator.asInstanceOf[js.Any]
    ).asInstanceOf[NodeJSONMetadata]
