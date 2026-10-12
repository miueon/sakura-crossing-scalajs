// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Material")
class Material[TEventMap <: MaterialEventMap]() extends EventDispatcher[TEventMap] with MaterialProperties:
  def defines: js.UndefOr[js.Dictionary[js.Any]] = js.native
  def defines_=(value: js.UndefOr[js.Dictionary[js.Any]]): Unit = js.native
  def isMaterial: Boolean = js.native
  def needsUpdate_=(value: Boolean): Unit = js.native
  var `type`: String = js.native
  def uuid: String = js.native
  def version: Double = js.native
  @JSName("clone")
  protected def clone$raw(): Material[TEventMap] = js.native
  @JSName("copy")
  protected def copy$raw(source: Material[MaterialEventMap]): Material[TEventMap] = js.native
  def customProgramCacheKey(): String = js.native
  def dispose(): Unit = js.native
  @JSName("fromJSON")
  protected def fromJSON$raw(json: MaterialJSON, textures: js.Dictionary[Texture[js.Any, TextureEventMap]]): Material[TEventMap] = js.native
  def onBeforeCompile(parameters: WebGLProgramParametersWithUniforms, renderer: WebGLRenderer): Unit = js.native
  def onBeforeRender(renderer: WebGLRenderer, scene: Scene[Object3DEventMap], camera: Camera, geometry: BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap], `object`: Object3D[Object3DEventMap], group: Group[Object3DEventMap]): Unit = js.native
  def setValues(values: js.UndefOr[MaterialParameters] = js.undefined): Unit = js.native
  def toJSON(meta: js.UndefOr[JSONMeta] = js.undefined): MaterialJSON = js.native

object Material:
  extension [TEventMap <: MaterialEventMap, Self <: Material[TEventMap]](self: Self)
    inline def fromJSON(json: MaterialJSON, textures: js.Dictionary[Texture[js.Any, TextureEventMap]]): Self = self.fromJSON$raw(json, textures).asInstanceOf[Self]
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]
    inline def copy(source: Material[MaterialEventMap]): Self = self.copy$raw(source).asInstanceOf[Self]
