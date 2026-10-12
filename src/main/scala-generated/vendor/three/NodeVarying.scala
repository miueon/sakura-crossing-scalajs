// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait NodeVarying extends js.Object:
  var count: Double | Null = js.native
  var interpolationSampling: "centroid" | "either" | "first" | "normal" | "sample" | Null = js.native
  var interpolationType: "flat" | "linear" | "perspective" | Null = js.native
  def isNodeVar: Boolean = js.native
  def isNodeVarying: Boolean = js.native
  var name: String = js.native
  var needsInterpolation: Boolean = js.native
  var readOnly: Boolean = js.native
  var `type`: String = js.native

object NodeVarying:
  def apply(
    needsInterpolation: Boolean,
    isNodeVarying: Boolean,
    interpolationType: "flat" | "linear" | "perspective" | Null,
    interpolationSampling: "centroid" | "either" | "first" | "normal" | "sample" | Null,
    isNodeVar: Boolean,
    name: String,
    `type`: String,
    readOnly: Boolean,
    count: Double | Null
  ): NodeVarying =
    js.Dynamic.literal(
      "needsInterpolation" -> needsInterpolation.asInstanceOf[js.Any],
      "isNodeVarying" -> isNodeVarying.asInstanceOf[js.Any],
      "interpolationType" -> interpolationType.asInstanceOf[js.Any],
      "interpolationSampling" -> interpolationSampling.asInstanceOf[js.Any],
      "isNodeVar" -> isNodeVar.asInstanceOf[js.Any],
      "name" -> name.asInstanceOf[js.Any],
      "type" -> `type`.asInstanceOf[js.Any],
      "readOnly" -> readOnly.asInstanceOf[js.Any],
      "count" -> count.asInstanceOf[js.Any]
    ).asInstanceOf[NodeVarying]
