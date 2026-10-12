// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

// #WARN: Disambiguated erasure-colliding overload 'mul' with @targetName("mul_1") and @JSName("mul")
// #WARN: Disambiguated erasure-colliding overload 'mul' with @targetName("mul_2") and @JSName("mul")
// #WARN: Unified parameter slots for overload 'mulAssign' with JVM erasure 1:(java.lang.Object) via union
@js.native
trait Mat2Extensions extends js.Object:
  def add(b: Matrix2 | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["mat2"] & Mat2Extensions & MatExtensions["mat2"] & js.Any): NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["mat2"] & Mat2Extensions & MatExtensions["mat2"] & js.Any = js.native
  def addAssign(b: Matrix2 | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["mat2"] & Mat2Extensions & MatExtensions["mat2"] & js.Any): js.Function1[Matrix2 | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["mat2"] & Mat2Extensions & MatExtensions["mat2"] & js.Any, AddSubMulMat2AssignExtensions] = js.native
  def mul(b: Matrix2 | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["mat2"] & Mat2Extensions & MatExtensions["mat2"] & js.Any): NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["mat2"] & Mat2Extensions & MatExtensions["mat2"] & js.Any = js.native
  // #WARN: Disambiguated erasure-colliding overload 'mul' with @targetName("mul_1") and @JSName("mul")
  @scala.annotation.targetName("mul_1")
  @JSName("mul")
  def mul(b: Double | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["float"] & NumOrBoolExtensions["float"] & FloatExtensions & NumExtensions["float"] & FloatOrVecExtensions["float"] & js.Any | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["int"] & NumOrBoolExtensions["int"] & IntExtensions & NumExtensions["int"] & IntegerExtensions["int"] & IntOrVecExtensions["int"] & js.Any | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["uint"] & NumOrBoolExtensions["uint"] & UintExtensions & NumExtensions["uint"] & IntegerExtensions["uint"] & IntOrVecExtensions["uint"] & js.Any): NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["mat2"] & Mat2Extensions & MatExtensions["mat2"] & js.Any = js.native
  // #WARN: Disambiguated erasure-colliding overload 'mul' with @targetName("mul_2") and @JSName("mul")
  @scala.annotation.targetName("mul_2")
  @JSName("mul")
  def mul(b: Color | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["color"] & ColorExtensions & js.Any | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["vec2"] & NumOrBoolVec2Extensions["float"] & Vec2Extensions & NumVec2Extensions["float"] & FloatVecExtensions["vec2"] & FloatOrVecExtensions["vec2"] & js.Any | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["vec3"] & NumOrBoolVec3Extensions["float"] & Vec3Extensions & NumVec3Extensions["float"] & FloatVecExtensions["vec3"] & FloatOrVecExtensions["vec3"] & js.Any | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["vec4"] & NumOrBoolVec4Extensions["float"] & Vec4Extensions & NumVec4Extensions["float"] & FloatVecExtensions["vec4"] & FloatOrVecExtensions["vec4"] & js.Any | Vector2 | Vector3 | Vector4): NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["vec2"] & NumOrBoolVec2Extensions["float"] & Vec2Extensions & NumVec2Extensions["float"] & FloatVecExtensions["vec2"] & FloatOrVecExtensions["vec2"] & js.Any = js.native
  // #WARN: Unified parameter slots for overload 'mulAssign' with JVM erasure 1:(java.lang.Object) via union
  def mulAssign(b: Matrix2 | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["mat2"] & Mat2Extensions & MatExtensions["mat2"] & js.Any | Double | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["float"] & NumOrBoolExtensions["float"] & FloatExtensions & NumExtensions["float"] & FloatOrVecExtensions["float"] & js.Any | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["int"] & NumOrBoolExtensions["int"] & IntExtensions & NumExtensions["int"] & IntegerExtensions["int"] & IntOrVecExtensions["int"] & js.Any | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["uint"] & NumOrBoolExtensions["uint"] & UintExtensions & NumExtensions["uint"] & IntegerExtensions["uint"] & IntOrVecExtensions["uint"] & js.Any): js.Function1[Matrix2 | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["mat2"] & Mat2Extensions & MatExtensions["mat2"] & js.Any, MulMat2AssignExtensions] = js.native
  def sub(b: Matrix2 | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["mat2"] & Mat2Extensions & MatExtensions["mat2"] & js.Any): NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["mat2"] & Mat2Extensions & MatExtensions["mat2"] & js.Any = js.native
  def subAssign(b: Matrix2 | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["mat2"] & Mat2Extensions & MatExtensions["mat2"] & js.Any): js.Function1[Matrix2 | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["mat2"] & Mat2Extensions & MatExtensions["mat2"] & js.Any, AddSubMulMat2AssignExtensions] = js.native
// #WARN: SHIM-065: Native facade trait 'Mat2Extensions' cannot receive a builder companion: JS property 'mul' is written by 3 members
