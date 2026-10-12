// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait UniformsLibBumpmap extends js.Object:
  var bumpMap: IUniform[js.Any] = js.native
  var bumpMapTransform: IUniform[Matrix3] = js.native
  var bumpScale: IUniform[Double] = js.native

object UniformsLibBumpmap:
  def apply(
    bumpMap: IUniform[js.Any],
    bumpMapTransform: IUniform[Matrix3],
    bumpScale: IUniform[Double]
  ): UniformsLibBumpmap =
    js.Dynamic.literal(
      "bumpMap" -> bumpMap.asInstanceOf[js.Any],
      "bumpMapTransform" -> bumpMapTransform.asInstanceOf[js.Any],
      "bumpScale" -> bumpScale.asInstanceOf[js.Any]
    ).asInstanceOf[UniformsLibBumpmap]
