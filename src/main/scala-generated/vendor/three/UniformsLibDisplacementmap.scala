// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait UniformsLibDisplacementmap extends js.Object:
  var displacementBias: IUniform[Double] = js.native
  var displacementMap: IUniform[js.Any] = js.native
  var displacementMapTransform: IUniform[Matrix3] = js.native
  var displacementScale: IUniform[Double] = js.native

object UniformsLibDisplacementmap:
  def apply(
    displacementMap: IUniform[js.Any],
    displacementMapTransform: IUniform[Matrix3],
    displacementScale: IUniform[Double],
    displacementBias: IUniform[Double]
  ): UniformsLibDisplacementmap =
    js.Dynamic.literal(
      "displacementMap" -> displacementMap.asInstanceOf[js.Any],
      "displacementMapTransform" -> displacementMapTransform.asInstanceOf[js.Any],
      "displacementScale" -> displacementScale.asInstanceOf[js.Any],
      "displacementBias" -> displacementBias.asInstanceOf[js.Any]
    ).asInstanceOf[UniformsLibDisplacementmap]
