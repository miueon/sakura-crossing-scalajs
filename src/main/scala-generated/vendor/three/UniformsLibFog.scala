// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait UniformsLibFog extends js.Object:
  var fogColor: IUniform[Color] = js.native
  var fogDensity: IUniform[Double] = js.native
  var fogFar: IUniform[Double] = js.native
  var fogNear: IUniform[Double] = js.native

object UniformsLibFog:
  def apply(
    fogDensity: IUniform[Double],
    fogNear: IUniform[Double],
    fogFar: IUniform[Double],
    fogColor: IUniform[Color]
  ): UniformsLibFog =
    js.Dynamic.literal(
      "fogDensity" -> fogDensity.asInstanceOf[js.Any],
      "fogNear" -> fogNear.asInstanceOf[js.Any],
      "fogFar" -> fogFar.asInstanceOf[js.Any],
      "fogColor" -> fogColor.asInstanceOf[js.Any]
    ).asInstanceOf[UniformsLibFog]
