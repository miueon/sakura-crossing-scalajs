// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Loader")
class Loader[TData, TUrl](manager_1: js.UndefOr[LoadingManager] = js.undefined) extends js.Object:
  var crossOrigin: String = js.native
  var manager: LoadingManager = js.native
  var path: String = js.native
  var requestHeader: js.Dictionary[String] = js.native
  var resourcePath: String = js.native
  var withCredentials: Boolean = js.native
  @JSName("abort")
  protected def abort$raw(): Loader[TData, TUrl] = js.native
  def load(url: TUrl, onLoad: js.Function1[TData, Unit], onProgress: js.UndefOr[js.Function1[js.Object, Unit]] = js.undefined, onError: js.UndefOr[js.Function1[js.Any, Unit]] = js.undefined): Unit = js.native
  def loadAsync(url: TUrl, onProgress: js.UndefOr[js.Function1[js.Object, Unit]] = js.undefined): js.Promise[TData] = js.native
  @JSName("setCrossOrigin")
  protected def setCrossOrigin$raw(crossOrigin: String): Loader[TData, TUrl] = js.native
  @JSName("setPath")
  protected def setPath$raw(path: String): Loader[TData, TUrl] = js.native
  @JSName("setRequestHeader")
  protected def setRequestHeader$raw(requestHeader: js.Dictionary[String]): Loader[TData, TUrl] = js.native
  @JSName("setResourcePath")
  protected def setResourcePath$raw(resourcePath: String): Loader[TData, TUrl] = js.native
  @JSName("setWithCredentials")
  protected def setWithCredentials$raw(value: Boolean): Loader[TData, TUrl] = js.native


object Loader:
  extension [TData, TUrl, Self <: Loader[TData, TUrl]](self: Self)
    inline def setCrossOrigin(crossOrigin: String): Self = self.setCrossOrigin$raw(crossOrigin).asInstanceOf[Self]
    inline def setWithCredentials(value: Boolean): Self = self.setWithCredentials$raw(value).asInstanceOf[Self]
    inline def setPath(path: String): Self = self.setPath$raw(path).asInstanceOf[Self]
    inline def setResourcePath(resourcePath: String): Self = self.setResourcePath$raw(resourcePath).asInstanceOf[Self]
    inline def setRequestHeader(requestHeader: js.Dictionary[String]): Self = self.setRequestHeader$raw(requestHeader).asInstanceOf[Self]
    inline def abort(): Self = self.abort$raw().asInstanceOf[Self]

  // #WARN: static 'DEFAULT_MATERIAL_NAME' is declared read-write upstream; the member-level native publication cannot express a JS setter, so its write direction is withheld
  @js.native
  @JSImport("three", "Loader.DEFAULT_MATERIAL_NAME")
  def DEFAULT_MATERIAL_NAME: String = js.native
