// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait UniformsLibAomap extends js.Object:
  var aoMap: IUniform[js.Any] = js.native
  var aoMapIntensity: IUniform[Double] = js.native
  var aoMapTransform: IUniform[Matrix3] = js.native

object UniformsLibAomap:
  def apply(
    aoMap: IUniform[js.Any],
    aoMapIntensity: IUniform[Double],
    aoMapTransform: IUniform[Matrix3]
  ): UniformsLibAomap =
    js.Dynamic.literal(
      "aoMap" -> aoMap.asInstanceOf[js.Any],
      "aoMapIntensity" -> aoMapIntensity.asInstanceOf[js.Any],
      "aoMapTransform" -> aoMapTransform.asInstanceOf[js.Any]
    ).asInstanceOf[UniformsLibAomap]
