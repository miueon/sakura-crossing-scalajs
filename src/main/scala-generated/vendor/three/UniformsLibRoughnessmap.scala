// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait UniformsLibRoughnessmap extends js.Object:
  var roughnessMap: IUniform[js.Any] = js.native
  var roughnessMapTransform: IUniform[Matrix3] = js.native

object UniformsLibRoughnessmap:
  def apply(
    roughnessMap: IUniform[js.Any],
    roughnessMapTransform: IUniform[Matrix3]
  ): UniformsLibRoughnessmap =
    js.Dynamic.literal(
      "roughnessMap" -> roughnessMap.asInstanceOf[js.Any],
      "roughnessMapTransform" -> roughnessMapTransform.asInstanceOf[js.Any]
    ).asInstanceOf[UniformsLibRoughnessmap]
