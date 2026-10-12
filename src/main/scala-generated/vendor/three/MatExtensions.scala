// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait MatExtensions[TMat <: MatType] extends js.Object:
  def determinant(): NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["float"] & NumOrBoolExtensions["float"] & FloatExtensions & NumExtensions["float"] & FloatOrVecExtensions["float"] & js.Any = js.native
  def inverse(): NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TMat] | js.Any) & (Mat2Extensions & MatExtensions["mat2"] | Mat3Extensions & MatExtensions["mat3"] | Mat4Extensions & MatExtensions["mat4"]) & js.Any = js.native
  def toMat2(): NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["mat2"] & Mat2Extensions & MatExtensions["mat2"] & js.Any & VarNodeInterface[NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["mat2"] & Mat2Extensions & MatExtensions["mat2"] & js.Any & ConvertNodeInterface] = js.native
  def toMat3(): NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["mat3"] & Mat3Extensions & MatExtensions["mat3"] & js.Any & VarNodeInterface[NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["mat3"] & Mat3Extensions & MatExtensions["mat3"] & js.Any & ConvertNodeInterface] = js.native
  def toMat4(): NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["mat4"] & Mat4Extensions & MatExtensions["mat4"] & js.Any & VarNodeInterface[NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["mat4"] & Mat4Extensions & MatExtensions["mat4"] & js.Any & ConvertNodeInterface] = js.native
  def transpose(): NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TMat] | js.Any) & (Mat2Extensions & MatExtensions["mat2"] | Mat3Extensions & MatExtensions["mat3"] | Mat4Extensions & MatExtensions["mat4"]) & js.Any = js.native

object MatExtensions:
  def of[TMat <: MatType](
    determinant: js.Function0[NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["float"] & NumOrBoolExtensions["float"] & FloatExtensions & NumExtensions["float"] & FloatOrVecExtensions["float"] & js.Any],
    inverse: js.Function0[NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TMat] | js.Any) & (Mat2Extensions & MatExtensions["mat2"] | Mat3Extensions & MatExtensions["mat3"] | Mat4Extensions & MatExtensions["mat4"]) & js.Any],
    toMat2: js.Function0[NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["mat2"] & Mat2Extensions & MatExtensions["mat2"] & js.Any & VarNodeInterface[NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["mat2"] & Mat2Extensions & MatExtensions["mat2"] & js.Any & ConvertNodeInterface]],
    toMat3: js.Function0[NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["mat3"] & Mat3Extensions & MatExtensions["mat3"] & js.Any & VarNodeInterface[NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["mat3"] & Mat3Extensions & MatExtensions["mat3"] & js.Any & ConvertNodeInterface]],
    toMat4: js.Function0[NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["mat4"] & Mat4Extensions & MatExtensions["mat4"] & js.Any & VarNodeInterface[NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["mat4"] & Mat4Extensions & MatExtensions["mat4"] & js.Any & ConvertNodeInterface]],
    transpose: js.Function0[NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TMat] | js.Any) & (Mat2Extensions & MatExtensions["mat2"] | Mat3Extensions & MatExtensions["mat3"] | Mat4Extensions & MatExtensions["mat4"]) & js.Any]
  ): MatExtensions[TMat] =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "determinant" -> determinant.asInstanceOf[js.Any],
      "inverse" -> inverse.asInstanceOf[js.Any],
      "toMat2" -> toMat2.asInstanceOf[js.Any],
      "toMat3" -> toMat3.asInstanceOf[js.Any],
      "toMat4" -> toMat4.asInstanceOf[js.Any],
      "transpose" -> transpose.asInstanceOf[js.Any]
    ).asInstanceOf[MatExtensions[TMat]]
