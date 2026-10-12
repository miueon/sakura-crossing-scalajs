// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "ObjectLoader")
class ObjectLoader(manager: js.UndefOr[LoadingManager] = js.undefined) extends Loader[Object3D[Object3DEventMap], String](js.native: js.UndefOr[LoadingManager]):
  override def load(url: String, onLoad: js.Function1[Object3D[Object3DEventMap], Unit], onProgress: js.UndefOr[js.Function1[js.Object, Unit]] = js.undefined, onError: js.UndefOr[js.Function1[js.Any, Unit]] = js.undefined): Unit = js.native
  def parse(json: js.Any, onLoad: js.UndefOr[js.Function1[Object3D[Object3DEventMap], Unit]] = js.undefined): Object3D[Object3DEventMap] = js.native
  def parseAnimations(json: js.Any): js.Dictionary[AnimationClip] = js.native
  def parseAsync(json: js.Any): js.Promise[Object3D[Object3DEventMap]] = js.native
  def parseGeometries(json: js.Any): js.Dictionary[BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap] | InstancedBufferGeometry] = js.native
  def parseImages(json: js.Any, onLoad: js.UndefOr[js.Function0[Unit]] = js.undefined): js.Dictionary[Source[js.Any]] = js.native
  def parseImagesAsync(json: js.Any): js.Promise[js.Dictionary[Source[js.Any]]] = js.native
  def parseMaterials(json: js.Any, textures: js.Dictionary[Texture[js.Any, TextureEventMap]]): js.Dictionary[Material[MaterialEventMap]] = js.native
  def parseObject(data: js.Any, geometries: js.Dictionary[BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap] | InstancedBufferGeometry], materials: js.Dictionary[Material[MaterialEventMap]], animations: js.Dictionary[AnimationClip]): Object3D[Object3DEventMap] = js.native
  def parseTextures(json: js.Any, images: js.Dictionary[Source[js.Any]]): js.Dictionary[Texture[js.Any, TextureEventMap]] = js.native
