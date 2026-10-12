// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Scene")
class Scene[TEventMap <: Object3DEventMap]() extends Object3D[TEventMap]:
  var background: Color | Texture[js.Any, TextureEventMap] | Null = js.native
  var backgroundBlurriness: Double = js.native
  var backgroundIntensity: Double = js.native
  var backgroundNode: js.UndefOr[NodeClass[NodeClassEventMap] & NodeElements & js.Any | Null] = js.native
  var backgroundRotation: Euler = js.native
  var environment: Texture[js.Any, TextureEventMap] | Null = js.native
  var environmentIntensity: Double = js.native
  var environmentNode: js.UndefOr[NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["vec3"] & NumOrBoolVec3Extensions["float"] & Vec3Extensions & NumVec3Extensions["float"] & FloatVecExtensions["vec3"] & FloatOrVecExtensions["vec3"] & js.Any | Null] = js.native
  var environmentRotation: Euler = js.native
  var fog: Fog | FogExp2 | Null = js.native
  var fogNode: js.UndefOr[NodeClass[NodeClassEventMap] & NodeElements & js.Any | Null] = js.native
  def isScene: Boolean = js.native
  var overrideMaterial: Material[MaterialEventMap] | Null = js.native
  @JSName("copy")
  protected override def copy$raw(`object`: Object3D[Object3DEventMap], recursive: js.UndefOr[Boolean] = js.undefined): Scene[TEventMap] = js.native
  override def toJSON(meta: js.UndefOr[JSONMeta] = js.undefined): SceneJSON = js.native

object Scene:
  extension [TEventMap <: Object3DEventMap, Self <: Scene[TEventMap]](self: Self)
    inline def copy(source: Scene[Object3DEventMap], recursive: js.UndefOr[Boolean] = js.undefined): Self = self.copy$raw(source, recursive).asInstanceOf[Self]
