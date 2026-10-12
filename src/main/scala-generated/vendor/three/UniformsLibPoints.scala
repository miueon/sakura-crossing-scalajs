// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait UniformsLibPoints extends js.Object:
  var alphaMap: IUniform[js.Any] = js.native
  var alphaTest: IUniform[Double] = js.native
  var diffuse: IUniform[Color] = js.native
  var map: IUniform[js.Any] = js.native
  var opacity: IUniform[Double] = js.native
  var scale: IUniform[Double] = js.native
  var size: IUniform[Double] = js.native
  var uvTransform: IUniform[Matrix3] = js.native

object UniformsLibPoints:
  def apply(
    diffuse: IUniform[Color],
    opacity: IUniform[Double],
    size: IUniform[Double],
    scale: IUniform[Double],
    map: IUniform[js.Any],
    alphaMap: IUniform[js.Any],
    alphaTest: IUniform[Double],
    uvTransform: IUniform[Matrix3]
  ): UniformsLibPoints =
    js.Dynamic.literal(
      "diffuse" -> diffuse.asInstanceOf[js.Any],
      "opacity" -> opacity.asInstanceOf[js.Any],
      "size" -> size.asInstanceOf[js.Any],
      "scale" -> scale.asInstanceOf[js.Any],
      "map" -> map.asInstanceOf[js.Any],
      "alphaMap" -> alphaMap.asInstanceOf[js.Any],
      "alphaTest" -> alphaTest.asInstanceOf[js.Any],
      "uvTransform" -> uvTransform.asInstanceOf[js.Any]
    ).asInstanceOf[UniformsLibPoints]
