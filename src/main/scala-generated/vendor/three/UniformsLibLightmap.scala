// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait UniformsLibLightmap extends js.Object:
  var lightMap: IUniform[Double] = js.native
  var lightMapIntensity: IUniform[Double] = js.native
  var lightMapTransform: IUniform[Matrix3] = js.native

object UniformsLibLightmap:
  def apply(
    lightMap: IUniform[Double],
    lightMapIntensity: IUniform[Double],
    lightMapTransform: IUniform[Matrix3]
  ): UniformsLibLightmap =
    js.Dynamic.literal(
      "lightMap" -> lightMap.asInstanceOf[js.Any],
      "lightMapIntensity" -> lightMapIntensity.asInstanceOf[js.Any],
      "lightMapTransform" -> lightMapTransform.asInstanceOf[js.Any]
    ).asInstanceOf[UniformsLibLightmap]
