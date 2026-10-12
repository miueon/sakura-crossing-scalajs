// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "DataArrayTexture")
class DataArrayTexture(data: js.UndefOr[js.typedarray.Float32Array | js.typedarray.Float64Array | js.typedarray.Int16Array | js.typedarray.Int32Array | js.typedarray.Int8Array | js.typedarray.Uint16Array | js.typedarray.Uint32Array | js.typedarray.Uint8Array | js.typedarray.Uint8ClampedArray | Null] = js.undefined, width: js.UndefOr[Double] = js.undefined, height: js.UndefOr[Double] = js.undefined, depth: js.UndefOr[Double] = js.undefined) extends Texture[DataArrayTextureImageData, TextureEventMap](js.native: js.UndefOr[DataArrayTextureImageData], js.native: js.UndefOr[300 | 303 | 304], js.native: js.UndefOr[1000 | 1001 | 1002], js.native: js.UndefOr[1000 | 1001 | 1002], js.native: js.UndefOr[1003 | 1006], js.native: js.UndefOr[1003 | 1004 | 1005 | 1006 | 1007 | 1008], js.native: js.UndefOr[1021 | 1022 | 1023 | 1026 | 1027 | 1028 | 1029 | 1030 | 1031 | 1032 | 1033], js.native: js.UndefOr[1009 | 1010 | 1011 | 1012 | 1013 | 1014 | 1015 | 1016 | 1017 | 1018 | 1020 | 35899 | 35902], js.native: js.UndefOr[Double], js.native: js.UndefOr["" | "srgb" | "srgb-linear"]):
  override def flipY: Boolean = js.native
  override def flipY_=(value: Boolean): Unit = js.native
  override def generateMipmaps: Boolean = js.native
  override def generateMipmaps_=(value: Boolean): Unit = js.native
  def isDataArrayTexture: true = js.native
  var layerUpdates: js.Object = js.native
  override def magFilter: 1003 | 1006 = js.native
  override def magFilter_=(value: 1003 | 1006): Unit = js.native
  override def minFilter: 1003 | 1004 | 1005 | 1006 | 1007 | 1008 = js.native
  override def minFilter_=(value: 1003 | 1004 | 1005 | 1006 | 1007 | 1008): Unit = js.native
  override def unpackAlignment: Double = js.native
  override def unpackAlignment_=(value: Double): Unit = js.native
  var wrapR: Boolean = js.native
  def addLayerUpdate(layerIndex: Double): Unit = js.native
  def clearLayerUpdates(): Unit = js.native
