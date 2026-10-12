// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait FloatVecExtensions[TVec <: FloatVecType] extends js.Object:
  def dot(y: NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["vec2"] & NumOrBoolVec2Extensions["float"] & Vec2Extensions & NumVec2Extensions["float"] & FloatVecExtensions["vec2"] & FloatOrVecExtensions["vec2"] & js.Any | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["vec3"] & NumOrBoolVec3Extensions["float"] & Vec3Extensions & NumVec3Extensions["float"] & FloatVecExtensions["vec3"] & FloatOrVecExtensions["vec3"] & js.Any | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["vec4"] & NumOrBoolVec4Extensions["float"] & Vec4Extensions & NumVec4Extensions["float"] & FloatVecExtensions["vec4"] & FloatOrVecExtensions["vec4"] & js.Any): NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["float"] & NumOrBoolExtensions["float"] & FloatExtensions & NumExtensions["float"] & FloatOrVecExtensions["float"] & js.Any = js.native
  def lengthSq(): NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["float"] & NumOrBoolExtensions["float"] & FloatExtensions & NumExtensions["float"] & FloatOrVecExtensions["float"] & js.Any = js.native
  def normalize(): NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TVec] | js.Any) & (NumOrBoolVec2Extensions["float"] & Vec2Extensions & NumVec2Extensions["float"] & FloatVecExtensions["vec2"] & FloatOrVecExtensions["vec2"] | NumOrBoolVec3Extensions["float"] & Vec3Extensions & NumVec3Extensions["float"] & FloatVecExtensions["vec3"] & FloatOrVecExtensions["vec3"] | NumOrBoolVec4Extensions["float"] & Vec4Extensions & NumVec4Extensions["float"] & FloatVecExtensions["vec4"] & FloatOrVecExtensions["vec4"]) & js.Any = js.native

object FloatVecExtensions:
  def of[TVec <: FloatVecType](
    dot: js.Function1[NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["vec2"] & NumOrBoolVec2Extensions["float"] & Vec2Extensions & NumVec2Extensions["float"] & FloatVecExtensions["vec2"] & FloatOrVecExtensions["vec2"] & js.Any | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["vec3"] & NumOrBoolVec3Extensions["float"] & Vec3Extensions & NumVec3Extensions["float"] & FloatVecExtensions["vec3"] & FloatOrVecExtensions["vec3"] & js.Any | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["vec4"] & NumOrBoolVec4Extensions["float"] & Vec4Extensions & NumVec4Extensions["float"] & FloatVecExtensions["vec4"] & FloatOrVecExtensions["vec4"] & js.Any, NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["float"] & NumOrBoolExtensions["float"] & FloatExtensions & NumExtensions["float"] & FloatOrVecExtensions["float"] & js.Any],
    lengthSq: js.Function0[NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["float"] & NumOrBoolExtensions["float"] & FloatExtensions & NumExtensions["float"] & FloatOrVecExtensions["float"] & js.Any],
    normalize: js.Function0[NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TVec] | js.Any) & (NumOrBoolVec2Extensions["float"] & Vec2Extensions & NumVec2Extensions["float"] & FloatVecExtensions["vec2"] & FloatOrVecExtensions["vec2"] | NumOrBoolVec3Extensions["float"] & Vec3Extensions & NumVec3Extensions["float"] & FloatVecExtensions["vec3"] & FloatOrVecExtensions["vec3"] | NumOrBoolVec4Extensions["float"] & Vec4Extensions & NumVec4Extensions["float"] & FloatVecExtensions["vec4"] & FloatOrVecExtensions["vec4"]) & js.Any]
  ): FloatVecExtensions[TVec] =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "dot" -> dot.asInstanceOf[js.Any],
      "lengthSq" -> lengthSq.asInstanceOf[js.Any],
      "normalize" -> normalize.asInstanceOf[js.Any]
    ).asInstanceOf[FloatVecExtensions[TVec]]
