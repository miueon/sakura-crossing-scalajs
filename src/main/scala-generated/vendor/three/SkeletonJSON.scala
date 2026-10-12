// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait SkeletonJSON extends js.Object:
  var boneInverses: js.Array[js.Tuple16[Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double]] = js.native
  var bones: js.Array[String] = js.native
  var metadata: BufferGeometryJSONMetadata = js.native
  var uuid: String = js.native

object SkeletonJSON:
  def apply(
    metadata: BufferGeometryJSONMetadata,
    bones: js.Array[String],
    boneInverses: js.Array[js.Tuple16[Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double]],
    uuid: String
  ): SkeletonJSON =
    js.Dynamic.literal(
      "metadata" -> metadata.asInstanceOf[js.Any],
      "bones" -> bones.asInstanceOf[js.Any],
      "boneInverses" -> boneInverses.asInstanceOf[js.Any],
      "uuid" -> uuid.asInstanceOf[js.Any]
    ).asInstanceOf[SkeletonJSON]
