// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait ArcCurveComputeFrenetFramesReturn extends js.Object:
  var binormals: js.Array[Vector3] = js.native
  var normals: js.Array[Vector3] = js.native
  var tangents: js.Array[Vector3] = js.native

object ArcCurveComputeFrenetFramesReturn:
  def apply(
    tangents: js.Array[Vector3],
    normals: js.Array[Vector3],
    binormals: js.Array[Vector3]
  ): ArcCurveComputeFrenetFramesReturn =
    js.Dynamic.literal(
      "tangents" -> tangents.asInstanceOf[js.Any],
      "normals" -> normals.asInstanceOf[js.Any],
      "binormals" -> binormals.asInstanceOf[js.Any]
    ).asInstanceOf[ArcCurveComputeFrenetFramesReturn]
