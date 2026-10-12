// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait UniformsLibMetalnessmap extends js.Object:
  var metalnessMap: IUniform[js.Any] = js.native
  var metalnessMapTransform: IUniform[Matrix3] = js.native

object UniformsLibMetalnessmap:
  def apply(
    metalnessMap: IUniform[js.Any],
    metalnessMapTransform: IUniform[Matrix3]
  ): UniformsLibMetalnessmap =
    js.Dynamic.literal(
      "metalnessMap" -> metalnessMap.asInstanceOf[js.Any],
      "metalnessMapTransform" -> metalnessMapTransform.asInstanceOf[js.Any]
    ).asInstanceOf[UniformsLibMetalnessmap]
