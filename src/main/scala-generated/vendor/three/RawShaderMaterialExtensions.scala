// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait RawShaderMaterialExtensions extends js.Object:
  var clipCullDistance: Boolean = js.native
  var multiDraw: Boolean = js.native

object RawShaderMaterialExtensions:
  def apply(
    clipCullDistance: Boolean,
    multiDraw: Boolean
  ): RawShaderMaterialExtensions =
    js.Dynamic.literal(
      "clipCullDistance" -> clipCullDistance.asInstanceOf[js.Any],
      "multiDraw" -> multiDraw.asInstanceOf[js.Any]
    ).asInstanceOf[RawShaderMaterialExtensions]
