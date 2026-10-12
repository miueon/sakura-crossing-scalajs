// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait RenderTargetOptions extends TextureParameters:
  var count: js.UndefOr[Double] = js.native
  var depth: js.UndefOr[Double] = js.native
  var depthBuffer: js.UndefOr[Boolean] = js.native
  var depthTexture: js.UndefOr[DepthTexture[DepthTextureImageData] | Null] = js.native
  var multiview: js.UndefOr[Boolean] = js.native
  var resolveDepthBuffer: js.UndefOr[Boolean] = js.native
  var resolveStencilBuffer: js.UndefOr[Boolean] = js.native
  var samples: js.UndefOr[Double] = js.native
  var stencilBuffer: js.UndefOr[Boolean] = js.native
  var useArrayDepthTexture: js.UndefOr[Boolean] = js.native

object RenderTargetOptions:
  def apply(
    depthBuffer: Option[js.UndefOr[Boolean]] = None,
    stencilBuffer: Option[js.UndefOr[Boolean]] = None,
    resolveDepthBuffer: Option[js.UndefOr[Boolean]] = None,
    resolveStencilBuffer: Option[js.UndefOr[Boolean]] = None,
    depthTexture: Option[js.UndefOr[DepthTexture[DepthTextureImageData] | Null]] = None,
    samples: Option[js.UndefOr[Double]] = None,
    count: Option[js.UndefOr[Double]] = None,
    depth: Option[js.UndefOr[Double]] = None,
    multiview: Option[js.UndefOr[Boolean]] = None,
    useArrayDepthTexture: Option[js.UndefOr[Boolean]] = None,
    mapping: Option[js.UndefOr[300 | 301 | 302 | 303 | 304 | 306]] = None,
    wrapS: Option[js.UndefOr[1000 | 1001 | 1002]] = None,
    wrapT: Option[js.UndefOr[1000 | 1001 | 1002]] = None,
    wrapR: Option[js.UndefOr[1000 | 1001 | 1002]] = None,
    format: Option[js.UndefOr[1021 | 1022 | 1023 | 1026 | 1027 | 1028 | 1029 | 1030 | 1031 | 1032 | 1033]] = None,
    internalFormat: Option[js.UndefOr[(("ALPHA" | "DEPTH24_STENCIL8" | "DEPTH32F_STENCIL8" | "DEPTH_COMPONENT16" | "DEPTH_COMPONENT24" | "DEPTH_COMPONENT32F" | "LUMINANCE" | "LUMINANCE_ALPHA" | "R11F_G11F_B10F" | "R16F" | "R16I" | "R16UI" | "R32F" | "R32I" | "R32UI" | "R8" | "R8I" | "R8UI" | "R8_SNORM" | "RED_INTEGER" | "RG16F" | "RG16I" | "RG16UI" | "RG32F" | "RG32I" | "RG32UI" | "RG8" | "RG8I" | "RG8UI" | "RG8_SNORM") | ("RGB" | "RGB10_A2" | "RGB10_A2UI" | "RGB16F" | "RGB16I" | "RGB16UI" | "RGB32F" | "RGB32I" | "RGB32UI" | "RGB565" | "RGB5_A1" | "RGB8" | "RGB8I" | "RGB8UI" | "RGB8_SNORM" | "RGB9_E5" | "RGBA" | "RGBA16F" | "RGBA16I" | "RGBA16UI" | "RGBA32F" | "RGBA32I" | "RGBA32UI" | "RGBA4" | "RGBA8" | "RGBA8I" | "RGBA8UI" | "RGBA8_SNORM" | "SRGB8" | "SRGB8_ALPHA8")) | Null]] = None,
    `type`: Option[js.UndefOr[1009 | 1010 | 1011 | 1012 | 1013 | 1014 | 1015 | 1016 | 1017 | 1018 | 1020 | 35899 | 35902]] = None,
    colorSpace: Option[js.UndefOr["" | "srgb" | "srgb-linear"]] = None,
    magFilter: Option[js.UndefOr[1003 | 1006]] = None,
    minFilter: Option[js.UndefOr[1003 | 1004 | 1005 | 1006 | 1007 | 1008]] = None,
    anisotropy: Option[js.UndefOr[Double]] = None,
    flipY: Option[js.UndefOr[Boolean]] = None,
    generateMipmaps: Option[js.UndefOr[Boolean]] = None
  ): RenderTargetOptions =
    val _res = js.Dynamic.literal()
    depthBuffer.foreach(v => _res.updateDynamic("depthBuffer")(v.asInstanceOf[js.Any]))
    stencilBuffer.foreach(v => _res.updateDynamic("stencilBuffer")(v.asInstanceOf[js.Any]))
    resolveDepthBuffer.foreach(v => _res.updateDynamic("resolveDepthBuffer")(v.asInstanceOf[js.Any]))
    resolveStencilBuffer.foreach(v => _res.updateDynamic("resolveStencilBuffer")(v.asInstanceOf[js.Any]))
    depthTexture.foreach(v => _res.updateDynamic("depthTexture")(v.asInstanceOf[js.Any]))
    samples.foreach(v => _res.updateDynamic("samples")(v.asInstanceOf[js.Any]))
    count.foreach(v => _res.updateDynamic("count")(v.asInstanceOf[js.Any]))
    depth.foreach(v => _res.updateDynamic("depth")(v.asInstanceOf[js.Any]))
    multiview.foreach(v => _res.updateDynamic("multiview")(v.asInstanceOf[js.Any]))
    useArrayDepthTexture.foreach(v => _res.updateDynamic("useArrayDepthTexture")(v.asInstanceOf[js.Any]))
    mapping.foreach(v => _res.updateDynamic("mapping")(v.asInstanceOf[js.Any]))
    wrapS.foreach(v => _res.updateDynamic("wrapS")(v.asInstanceOf[js.Any]))
    wrapT.foreach(v => _res.updateDynamic("wrapT")(v.asInstanceOf[js.Any]))
    wrapR.foreach(v => _res.updateDynamic("wrapR")(v.asInstanceOf[js.Any]))
    format.foreach(v => _res.updateDynamic("format")(v.asInstanceOf[js.Any]))
    internalFormat.foreach(v => _res.updateDynamic("internalFormat")(v.asInstanceOf[js.Any]))
    `type`.foreach(v => _res.updateDynamic("type")(v.asInstanceOf[js.Any]))
    colorSpace.foreach(v => _res.updateDynamic("colorSpace")(v.asInstanceOf[js.Any]))
    magFilter.foreach(v => _res.updateDynamic("magFilter")(v.asInstanceOf[js.Any]))
    minFilter.foreach(v => _res.updateDynamic("minFilter")(v.asInstanceOf[js.Any]))
    anisotropy.foreach(v => _res.updateDynamic("anisotropy")(v.asInstanceOf[js.Any]))
    flipY.foreach(v => _res.updateDynamic("flipY")(v.asInstanceOf[js.Any]))
    generateMipmaps.foreach(v => _res.updateDynamic("generateMipmaps")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[RenderTargetOptions]
