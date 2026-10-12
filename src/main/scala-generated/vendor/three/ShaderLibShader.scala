// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait ShaderLibShader extends js.Object:
  var fragmentShader: String = js.native
  var uniforms: js.Dictionary[IUniform[js.Any]] = js.native
  var vertexShader: String = js.native

object ShaderLibShader:
  def apply(
    uniforms: js.Dictionary[IUniform[js.Any]],
    vertexShader: String,
    fragmentShader: String
  ): ShaderLibShader =
    js.Dynamic.literal(
      "uniforms" -> uniforms.asInstanceOf[js.Any],
      "vertexShader" -> vertexShader.asInstanceOf[js.Any],
      "fragmentShader" -> fragmentShader.asInstanceOf[js.Any]
    ).asInstanceOf[ShaderLibShader]
