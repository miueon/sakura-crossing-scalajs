// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "ImageLoader")
class ImageLoader(manager: js.UndefOr[LoadingManager] = js.undefined) extends Loader[org.scalajs.dom.HTMLImageElement, String](js.native: js.UndefOr[LoadingManager]):
  override def load(url: String, onLoad: js.Function1[org.scalajs.dom.HTMLImageElement, Unit], onProgress: js.UndefOr[js.Function1[js.Object, Unit]] = js.undefined, onError: js.UndefOr[js.Function1[js.Any, Unit]] = js.undefined): Unit = js.native
