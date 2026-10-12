// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "CubeTextureLoader")
class CubeTextureLoader(manager: js.UndefOr[LoadingManager] = js.undefined) extends Loader[CubeTexture[js.Any], facadecontract.std.JSReadonlyArray[String]](js.native: js.UndefOr[LoadingManager]):
  override def load(url: facadecontract.std.JSReadonlyArray[String], onLoad: js.Function1[CubeTexture[js.Any], Unit], onProgress: js.UndefOr[js.Function1[js.Object, Unit]] = js.undefined, onError: js.UndefOr[js.Function1[js.Any, Unit]] = js.undefined): Unit = js.native
