// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "DataTextureLoader")
abstract class DataTextureLoader() extends Loader[DataTexture, String](js.native: js.UndefOr[LoadingManager]):
  def createDataTexture(buffer: js.typedarray.ArrayBuffer): DataTexture = js.native
  override def load(url: String, onLoad: js.Function1[DataTexture, Unit], onProgress: js.UndefOr[js.Function1[js.Object, Unit]] = js.undefined, onError: js.UndefOr[js.Function1[js.Any, Unit]] = js.undefined): Unit = js.native
  def parse(buffer: js.typedarray.ArrayBuffer): DataTextureLoaderTexData = js.native
