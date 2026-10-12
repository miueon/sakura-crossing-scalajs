// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait UniformsLibEnvmap extends js.Object:
  var envMap: IUniform[js.Any] = js.native
  var envMapRotation: IUniform[Matrix3] = js.native
  var ior: IUniform[Double] = js.native
  var reflectivity: IUniform[Double] = js.native
  var refractRatio: IUniform[Double] = js.native

object UniformsLibEnvmap:
  def apply(
    envMap: IUniform[js.Any],
    envMapRotation: IUniform[Matrix3],
    reflectivity: IUniform[Double],
    ior: IUniform[Double],
    refractRatio: IUniform[Double]
  ): UniformsLibEnvmap =
    js.Dynamic.literal(
      "envMap" -> envMap.asInstanceOf[js.Any],
      "envMapRotation" -> envMapRotation.asInstanceOf[js.Any],
      "reflectivity" -> reflectivity.asInstanceOf[js.Any],
      "ior" -> ior.asInstanceOf[js.Any],
      "refractRatio" -> refractRatio.asInstanceOf[js.Any]
    ).asInstanceOf[UniformsLibEnvmap]
