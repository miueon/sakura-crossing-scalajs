// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "FileLoader")
class FileLoader[TData](manager: js.UndefOr[LoadingManager] = js.undefined) extends Loader[TData, String](js.native: js.UndefOr[LoadingManager]):
  var mimeType: String = js.native
  var responseType: String = js.native
  override def load(url: String, onLoad: js.Function1[TData, Unit], onProgress: js.UndefOr[js.Function1[js.Object, Unit]] = js.undefined, onError: js.UndefOr[js.Function1[js.Any, Unit]] = js.undefined): Unit = js.native
  @JSName("setMimeType")
  protected def setMimeType$raw(value: String): FileLoader[TData] = js.native
  @JSName("setResponseType")
  protected def setResponseType$raw(value: String): FileLoader[TData] = js.native

object FileLoader:
  extension [TData, Self <: FileLoader[TData]](self: Self)
    inline def setResponseType(value: String): Self = self.setResponseType$raw(value).asInstanceOf[Self]
    inline def setMimeType(value: String): Self = self.setMimeType$raw(value).asInstanceOf[Self]
