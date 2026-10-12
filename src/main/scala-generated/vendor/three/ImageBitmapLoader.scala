// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "ImageBitmapLoader")
class ImageBitmapLoader(manager: js.UndefOr[LoadingManager] = js.undefined) extends Loader[js.Object, String](js.native: js.UndefOr[LoadingManager]):
  def isImageBitmapLoader: true = js.native
  var options: js.Object = js.native
  override def load(url: String, onLoad: js.Function1[js.Object, Unit], onProgress: js.UndefOr[js.Function1[js.Object, Unit]] = js.undefined, onError: js.UndefOr[js.Function1[js.Any, Unit]] = js.undefined): Unit = js.native
  @JSName("setOptions")
  protected def setOptions$raw(options: js.Object): ImageBitmapLoader = js.native

object ImageBitmapLoader:
  extension [Self <: ImageBitmapLoader](self: Self)
    inline def setOptions(options: js.Object): Self = self.setOptions$raw(options).asInstanceOf[Self]
