// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait UniformsLibEmissivemap extends js.Object:
  var emissiveMap: IUniform[js.Any] = js.native
  var emissiveMapTransform: IUniform[Matrix3] = js.native

object UniformsLibEmissivemap:
  def apply(
    emissiveMap: IUniform[js.Any],
    emissiveMapTransform: IUniform[Matrix3]
  ): UniformsLibEmissivemap =
    js.Dynamic.literal(
      "emissiveMap" -> emissiveMap.asInstanceOf[js.Any],
      "emissiveMapTransform" -> emissiveMapTransform.asInstanceOf[js.Any]
    ).asInstanceOf[UniformsLibEmissivemap]
