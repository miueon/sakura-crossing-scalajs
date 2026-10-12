// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait UniformsLibNormalmap extends js.Object:
  var normalMap: IUniform[js.Any] = js.native
  var normalMapTransform: IUniform[Matrix3] = js.native
  var normalScale: IUniform[Vector2] = js.native

object UniformsLibNormalmap:
  def apply(
    normalMap: IUniform[js.Any],
    normalMapTransform: IUniform[Matrix3],
    normalScale: IUniform[Vector2]
  ): UniformsLibNormalmap =
    js.Dynamic.literal(
      "normalMap" -> normalMap.asInstanceOf[js.Any],
      "normalMapTransform" -> normalMapTransform.asInstanceOf[js.Any],
      "normalScale" -> normalScale.asInstanceOf[js.Any]
    ).asInstanceOf[UniformsLibNormalmap]
