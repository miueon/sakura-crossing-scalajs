// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait UniformsLibSprite extends js.Object:
  var alphaMap: IUniform[js.Any] = js.native
  var alphaTest: IUniform[Double] = js.native
  var center: IUniform[Vector2] = js.native
  var diffuse: IUniform[Color] = js.native
  var map: IUniform[js.Any] = js.native
  var mapTransform: IUniform[Matrix3] = js.native
  var opacity: IUniform[Double] = js.native
  var rotation: IUniform[Double] = js.native

object UniformsLibSprite:
  def apply(
    diffuse: IUniform[Color],
    opacity: IUniform[Double],
    center: IUniform[Vector2],
    rotation: IUniform[Double],
    map: IUniform[js.Any],
    mapTransform: IUniform[Matrix3],
    alphaMap: IUniform[js.Any],
    alphaTest: IUniform[Double]
  ): UniformsLibSprite =
    js.Dynamic.literal(
      "diffuse" -> diffuse.asInstanceOf[js.Any],
      "opacity" -> opacity.asInstanceOf[js.Any],
      "center" -> center.asInstanceOf[js.Any],
      "rotation" -> rotation.asInstanceOf[js.Any],
      "map" -> map.asInstanceOf[js.Any],
      "mapTransform" -> mapTransform.asInstanceOf[js.Any],
      "alphaMap" -> alphaMap.asInstanceOf[js.Any],
      "alphaTest" -> alphaTest.asInstanceOf[js.Any]
    ).asInstanceOf[UniformsLibSprite]
