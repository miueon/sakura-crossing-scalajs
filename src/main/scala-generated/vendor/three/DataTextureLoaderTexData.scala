// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait DataTextureLoaderTexData extends js.Object:
  var anisotropy: js.UndefOr[Double] = js.native
  var colorSpace: js.UndefOr["" | "srgb" | "srgb-linear"] = js.native
  var data: js.UndefOr[js.typedarray.Float32Array | js.typedarray.Float64Array | js.typedarray.Int16Array | js.typedarray.Int32Array | js.typedarray.Int8Array | js.typedarray.Uint16Array | js.typedarray.Uint32Array | js.typedarray.Uint8Array | js.typedarray.Uint8ClampedArray] = js.native
  var flipY: js.UndefOr[Boolean] = js.native
  var format: js.UndefOr[1021 | 1022 | 1023 | 1026 | 1027 | 1028 | 1029 | 1030 | 1031 | 1032 | 1033] = js.native
  var generateMipmaps: js.UndefOr[Boolean] = js.native
  var height: js.UndefOr[Double] = js.native
  var image: js.UndefOr[DataTextureImageData] = js.native
  var magFilter: js.UndefOr[1003 | 1006] = js.native
  var minFilter: js.UndefOr[1003 | 1004 | 1005 | 1006 | 1007 | 1008] = js.native
  var mipmapCount: js.UndefOr[Double] = js.native
  var mipmaps: js.UndefOr[js.Array[CompressedTextureMipmap] | js.Array[CubeTexture[js.Any]] | js.Array[org.scalajs.dom.HTMLCanvasElement]] = js.native
  var `type`: js.UndefOr[1009 | 1010 | 1011 | 1012 | 1013 | 1014 | 1015 | 1016 | 1017 | 1018 | 1020 | 35899 | 35902] = js.native
  var width: js.UndefOr[Double] = js.native
  var wrapS: js.UndefOr[1000 | 1001 | 1002] = js.native
  var wrapT: js.UndefOr[1000 | 1001 | 1002] = js.native

object DataTextureLoaderTexData:
  def apply(
    image: Option[js.UndefOr[DataTextureImageData]] = None,
    width: Option[js.UndefOr[Double]] = None,
    height: Option[js.UndefOr[Double]] = None,
    data: Option[js.UndefOr[js.typedarray.Float32Array | js.typedarray.Float64Array | js.typedarray.Int16Array | js.typedarray.Int32Array | js.typedarray.Int8Array | js.typedarray.Uint16Array | js.typedarray.Uint32Array | js.typedarray.Uint8Array | js.typedarray.Uint8ClampedArray]] = None,
    format: Option[js.UndefOr[1021 | 1022 | 1023 | 1026 | 1027 | 1028 | 1029 | 1030 | 1031 | 1032 | 1033]] = None,
    `type`: Option[js.UndefOr[1009 | 1010 | 1011 | 1012 | 1013 | 1014 | 1015 | 1016 | 1017 | 1018 | 1020 | 35899 | 35902]] = None,
    flipY: Option[js.UndefOr[Boolean]] = None,
    wrapS: Option[js.UndefOr[1000 | 1001 | 1002]] = None,
    wrapT: Option[js.UndefOr[1000 | 1001 | 1002]] = None,
    anisotropy: Option[js.UndefOr[Double]] = None,
    generateMipmaps: Option[js.UndefOr[Boolean]] = None,
    colorSpace: Option[js.UndefOr["" | "srgb" | "srgb-linear"]] = None,
    magFilter: Option[js.UndefOr[1003 | 1006]] = None,
    minFilter: Option[js.UndefOr[1003 | 1004 | 1005 | 1006 | 1007 | 1008]] = None,
    mipmaps: Option[js.UndefOr[js.Array[CompressedTextureMipmap] | js.Array[CubeTexture[js.Any]] | js.Array[org.scalajs.dom.HTMLCanvasElement]]] = None,
    mipmapCount: Option[js.UndefOr[Double]] = None
  ): DataTextureLoaderTexData =
    val _res = js.Dynamic.literal()
    image.foreach(v => _res.updateDynamic("image")(v.asInstanceOf[js.Any]))
    width.foreach(v => _res.updateDynamic("width")(v.asInstanceOf[js.Any]))
    height.foreach(v => _res.updateDynamic("height")(v.asInstanceOf[js.Any]))
    data.foreach(v => _res.updateDynamic("data")(v.asInstanceOf[js.Any]))
    format.foreach(v => _res.updateDynamic("format")(v.asInstanceOf[js.Any]))
    `type`.foreach(v => _res.updateDynamic("type")(v.asInstanceOf[js.Any]))
    flipY.foreach(v => _res.updateDynamic("flipY")(v.asInstanceOf[js.Any]))
    wrapS.foreach(v => _res.updateDynamic("wrapS")(v.asInstanceOf[js.Any]))
    wrapT.foreach(v => _res.updateDynamic("wrapT")(v.asInstanceOf[js.Any]))
    anisotropy.foreach(v => _res.updateDynamic("anisotropy")(v.asInstanceOf[js.Any]))
    generateMipmaps.foreach(v => _res.updateDynamic("generateMipmaps")(v.asInstanceOf[js.Any]))
    colorSpace.foreach(v => _res.updateDynamic("colorSpace")(v.asInstanceOf[js.Any]))
    magFilter.foreach(v => _res.updateDynamic("magFilter")(v.asInstanceOf[js.Any]))
    minFilter.foreach(v => _res.updateDynamic("minFilter")(v.asInstanceOf[js.Any]))
    mipmaps.foreach(v => _res.updateDynamic("mipmaps")(v.asInstanceOf[js.Any]))
    mipmapCount.foreach(v => _res.updateDynamic("mipmapCount")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[DataTextureLoaderTexData]
