// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait UniformsLibSpecularmap extends js.Object:
  var specularMap: IUniform[js.Any] = js.native
  var specularMapTransform: IUniform[Matrix3] = js.native

object UniformsLibSpecularmap:
  def apply(
    specularMap: IUniform[js.Any],
    specularMapTransform: IUniform[Matrix3]
  ): UniformsLibSpecularmap =
    js.Dynamic.literal(
      "specularMap" -> specularMap.asInstanceOf[js.Any],
      "specularMapTransform" -> specularMapTransform.asInstanceOf[js.Any]
    ).asInstanceOf[UniformsLibSpecularmap]
