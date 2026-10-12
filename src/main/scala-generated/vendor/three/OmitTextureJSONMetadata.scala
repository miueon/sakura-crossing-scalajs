// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait OmitTextureJSONMetadata extends js.Object:
  var anisotropy: Double = js.native
  var center: js.Tuple2[Double, Double] = js.native
  var channel: Double = js.native
  var colorSpace: String = js.native
  var flipY: Boolean = js.native
  var format: ((1021 | 1022 | 1023 | 1026 | 1027 | 1028 | 1029 | 1030 | 1031 | 1032 | 1033 | 33776 | 33777 | 33778 | 33779 | 35840 | 35841 | 35842 | 35843 | 36196 | 36283 | 36284 | 36285) | (36286 | 36492 | 36494 | 36495 | 37488 | 37489 | 37490 | 37491 | 37492 | 37496 | 37808 | 37809 | 37810 | 37811 | 37812 | 37813 | 37814 | 37815 | 37816 | 37817 | 37818 | 37819 | 37820 | 37821)) = js.native
  var generateMipmaps: Boolean = js.native
  var image: String = js.native
  var internalFormat: (("ALPHA" | "DEPTH24_STENCIL8" | "DEPTH32F_STENCIL8" | "DEPTH_COMPONENT16" | "DEPTH_COMPONENT24" | "DEPTH_COMPONENT32F" | "LUMINANCE" | "LUMINANCE_ALPHA" | "R11F_G11F_B10F" | "R16F" | "R16I" | "R16UI" | "R32F" | "R32I" | "R32UI" | "R8" | "R8I" | "R8UI" | "R8_SNORM" | "RED_INTEGER" | "RG16F" | "RG16I" | "RG16UI" | "RG32F" | "RG32I" | "RG32UI" | "RG8" | "RG8I" | "RG8UI" | "RG8_SNORM") | ("RGB" | "RGB10_A2" | "RGB10_A2UI" | "RGB16F" | "RGB16I" | "RGB16UI" | "RGB32F" | "RGB32I" | "RGB32UI" | "RGB565" | "RGB5_A1" | "RGB8" | "RGB8I" | "RGB8UI" | "RGB8_SNORM" | "RGB9_E5" | "RGBA" | "RGBA16F" | "RGBA16I" | "RGBA16UI" | "RGBA32F" | "RGBA32I" | "RGBA32UI" | "RGBA4" | "RGBA8" | "RGBA8I" | "RGBA8UI" | "RGBA8_SNORM" | "SRGB8" | "SRGB8_ALPHA8")) | Null = js.native
  var magFilter: 1003 | 1006 = js.native
  var mapping: 300 | 301 | 302 | 303 | 304 | 306 = js.native
  var minFilter: 1003 | 1004 | 1005 | 1006 | 1007 | 1008 = js.native
  var name: String = js.native
  var offset: js.Tuple2[Double, Double] = js.native
  var premultiplyAlpha: Boolean = js.native
  var repeat: js.Tuple2[Double, Double] = js.native
  var rotation: Double = js.native
  var `type`: 1009 | 1010 | 1011 | 1012 | 1013 | 1014 | 1015 | 1016 | 1017 | 1018 | 1020 | 35899 | 35902 = js.native
  var unpackAlignment: Double = js.native
  var userData: js.UndefOr[js.Dictionary[js.Any]] = js.native
  var uuid: String = js.native
  var wrap: js.Tuple2[Double, Double] = js.native

object OmitTextureJSONMetadata:
  def apply(
    `type`: 1009 | 1010 | 1011 | 1012 | 1013 | 1014 | 1015 | 1016 | 1017 | 1018 | 1020 | 35899 | 35902,
    offset: js.Tuple2[Double, Double],
    repeat: js.Tuple2[Double, Double],
    mapping: 300 | 301 | 302 | 303 | 304 | 306,
    magFilter: 1003 | 1006,
    minFilter: 1003 | 1004 | 1005 | 1006 | 1007 | 1008,
    format: ((1021 | 1022 | 1023 | 1026 | 1027 | 1028 | 1029 | 1030 | 1031 | 1032 | 1033 | 33776 | 33777 | 33778 | 33779 | 35840 | 35841 | 35842 | 35843 | 36196 | 36283 | 36284 | 36285) | (36286 | 36492 | 36494 | 36495 | 37488 | 37489 | 37490 | 37491 | 37492 | 37496 | 37808 | 37809 | 37810 | 37811 | 37812 | 37813 | 37814 | 37815 | 37816 | 37817 | 37818 | 37819 | 37820 | 37821)),
    anisotropy: Double,
    rotation: Double,
    center: js.Tuple2[Double, Double],
    colorSpace: String,
    image: String,
    uuid: String,
    name: String,
    userData: js.UndefOr[js.Dictionary[js.Any]] = js.undefined,
    channel: Double,
    wrap: js.Tuple2[Double, Double],
    internalFormat: (("ALPHA" | "DEPTH24_STENCIL8" | "DEPTH32F_STENCIL8" | "DEPTH_COMPONENT16" | "DEPTH_COMPONENT24" | "DEPTH_COMPONENT32F" | "LUMINANCE" | "LUMINANCE_ALPHA" | "R11F_G11F_B10F" | "R16F" | "R16I" | "R16UI" | "R32F" | "R32I" | "R32UI" | "R8" | "R8I" | "R8UI" | "R8_SNORM" | "RED_INTEGER" | "RG16F" | "RG16I" | "RG16UI" | "RG32F" | "RG32I" | "RG32UI" | "RG8" | "RG8I" | "RG8UI" | "RG8_SNORM") | ("RGB" | "RGB10_A2" | "RGB10_A2UI" | "RGB16F" | "RGB16I" | "RGB16UI" | "RGB32F" | "RGB32I" | "RGB32UI" | "RGB565" | "RGB5_A1" | "RGB8" | "RGB8I" | "RGB8UI" | "RGB8_SNORM" | "RGB9_E5" | "RGBA" | "RGBA16F" | "RGBA16I" | "RGBA16UI" | "RGBA32F" | "RGBA32I" | "RGBA32UI" | "RGBA4" | "RGBA8" | "RGBA8I" | "RGBA8UI" | "RGBA8_SNORM" | "SRGB8" | "SRGB8_ALPHA8")) | Null,
    flipY: Boolean,
    generateMipmaps: Boolean,
    premultiplyAlpha: Boolean,
    unpackAlignment: Double
  ): OmitTextureJSONMetadata =
    val _res = js.Dynamic.literal(
      "type" -> `type`.asInstanceOf[js.Any],
      "offset" -> offset.asInstanceOf[js.Any],
      "repeat" -> repeat.asInstanceOf[js.Any],
      "mapping" -> mapping.asInstanceOf[js.Any],
      "magFilter" -> magFilter.asInstanceOf[js.Any],
      "minFilter" -> minFilter.asInstanceOf[js.Any],
      "format" -> format.asInstanceOf[js.Any],
      "anisotropy" -> anisotropy.asInstanceOf[js.Any],
      "rotation" -> rotation.asInstanceOf[js.Any],
      "center" -> center.asInstanceOf[js.Any],
      "colorSpace" -> colorSpace.asInstanceOf[js.Any],
      "image" -> image.asInstanceOf[js.Any],
      "uuid" -> uuid.asInstanceOf[js.Any],
      "name" -> name.asInstanceOf[js.Any]
    )
    userData.foreach(v => _res.updateDynamic("userData")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("channel")(channel.asInstanceOf[js.Any])
    _res.updateDynamic("wrap")(wrap.asInstanceOf[js.Any])
    _res.updateDynamic("internalFormat")(internalFormat.asInstanceOf[js.Any])
    _res.updateDynamic("flipY")(flipY.asInstanceOf[js.Any])
    _res.updateDynamic("generateMipmaps")(generateMipmaps.asInstanceOf[js.Any])
    _res.updateDynamic("premultiplyAlpha")(premultiplyAlpha.asInstanceOf[js.Any])
    _res.updateDynamic("unpackAlignment")(unpackAlignment.asInstanceOf[js.Any])
    _res.asInstanceOf[OmitTextureJSONMetadata]
