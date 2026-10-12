// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Texture")
class Texture[TImage, TEventMap <: TextureEventMap](image_1: js.UndefOr[TImage] = js.undefined, mapping_1: js.UndefOr[300 | 303 | 304] = js.undefined, wrapS_1: js.UndefOr[1000 | 1001 | 1002] = js.undefined, wrapT_1: js.UndefOr[1000 | 1001 | 1002] = js.undefined, magFilter_1: js.UndefOr[1003 | 1006] = js.undefined, minFilter_1: js.UndefOr[1003 | 1004 | 1005 | 1006 | 1007 | 1008] = js.undefined, format_1: js.UndefOr[1021 | 1022 | 1023 | 1026 | 1027 | 1028 | 1029 | 1030 | 1031 | 1032 | 1033] = js.undefined, type_1: js.UndefOr[1009 | 1010 | 1011 | 1012 | 1013 | 1014 | 1015 | 1016 | 1017 | 1018 | 1020 | 35899 | 35902] = js.undefined, anisotropy_1: js.UndefOr[Double] = js.undefined, colorSpace_1: js.UndefOr["" | "srgb" | "srgb-linear"] = js.undefined) extends EventDispatcher[TEventMap]:
  def this(image: TImage, mapping: 300 | 303 | 304, wrapS: 1000 | 1001 | 1002, wrapT: 1000 | 1001 | 1002, magFilter: 1003 | 1006, minFilter: 1003 | 1004 | 1005 | 1006 | 1007 | 1008, format: 1021 | 1022 | 1023 | 1026 | 1027 | 1028 | 1029 | 1030 | 1031 | 1032 | 1033, `type`: 1009 | 1010 | 1011 | 1012 | 1013 | 1014 | 1015 | 1016 | 1017 | 1018 | 1020 | 35899 | 35902, anisotropy: Double) = this(js.native: js.UndefOr[TImage], js.native: js.UndefOr[300 | 303 | 304], js.native: js.UndefOr[1000 | 1001 | 1002], js.native: js.UndefOr[1000 | 1001 | 1002], js.native: js.UndefOr[1003 | 1006], js.native: js.UndefOr[1003 | 1004 | 1005 | 1006 | 1007 | 1008], js.native: js.UndefOr[1021 | 1022 | 1023 | 1026 | 1027 | 1028 | 1029 | 1030 | 1031 | 1032 | 1033], js.native: js.UndefOr[1009 | 1010 | 1011 | 1012 | 1013 | 1014 | 1015 | 1016 | 1017 | 1018 | 1020 | 35899 | 35902], js.native: js.UndefOr[Double], js.native: js.UndefOr["" | "srgb" | "srgb-linear"])
  var anisotropy: Double = js.native
  var center: Vector2 = js.native
  var channel: Double = js.native
  var colorSpace: String = js.native
  def depth: Double = js.native
  def flipY: Boolean = js.native
  def flipY_=(value: Boolean): Unit = js.native
  def format: ((1021 | 1022 | 1023 | 1026 | 1027 | 1028 | 1029 | 1030 | 1031 | 1032 | 1033 | 33776 | 33777 | 33778 | 33779 | 35840 | 35841 | 35842 | 35843 | 36196 | 36283 | 36284 | 36285) | (36286 | 36492 | 36494 | 36495 | 37488 | 37489 | 37490 | 37491 | 37492 | 37496 | 37808 | 37809 | 37810 | 37811 | 37812 | 37813 | 37814 | 37815 | 37816 | 37817 | 37818 | 37819 | 37820 | 37821)) = js.native
  def format_=(value: ((1021 | 1022 | 1023 | 1026 | 1027 | 1028 | 1029 | 1030 | 1031 | 1032 | 1033 | 33776 | 33777 | 33778 | 33779 | 35840 | 35841 | 35842 | 35843 | 36196 | 36283 | 36284 | 36285) | (36286 | 36492 | 36494 | 36495 | 37488 | 37489 | 37490 | 37491 | 37492 | 37496 | 37808 | 37809 | 37810 | 37811 | 37812 | 37813 | 37814 | 37815 | 37816 | 37817 | 37818 | 37819 | 37820 | 37821))): Unit = js.native
  def generateMipmaps: Boolean = js.native
  def generateMipmaps_=(value: Boolean): Unit = js.native
  def height: Double = js.native
  def id: Double = js.native
  var image: TImage = js.native
  var internalFormat: (("ALPHA" | "DEPTH24_STENCIL8" | "DEPTH32F_STENCIL8" | "DEPTH_COMPONENT16" | "DEPTH_COMPONENT24" | "DEPTH_COMPONENT32F" | "LUMINANCE" | "LUMINANCE_ALPHA" | "R11F_G11F_B10F" | "R16F" | "R16I" | "R16UI" | "R32F" | "R32I" | "R32UI" | "R8" | "R8I" | "R8UI" | "R8_SNORM" | "RED_INTEGER" | "RG16F" | "RG16I" | "RG16UI" | "RG32F" | "RG32I" | "RG32UI" | "RG8" | "RG8I" | "RG8UI" | "RG8_SNORM") | ("RGB" | "RGB10_A2" | "RGB10_A2UI" | "RGB16F" | "RGB16I" | "RGB16UI" | "RGB32F" | "RGB32I" | "RGB32UI" | "RGB565" | "RGB5_A1" | "RGB8" | "RGB8I" | "RGB8UI" | "RGB8_SNORM" | "RGB9_E5" | "RGBA" | "RGBA16F" | "RGBA16I" | "RGBA16UI" | "RGBA32F" | "RGBA32I" | "RGBA32UI" | "RGBA4" | "RGBA8" | "RGBA8I" | "RGBA8UI" | "RGBA8_SNORM" | "SRGB8" | "SRGB8_ALPHA8")) | Null = js.native
  var isArrayTexture: Boolean = js.native
  var isRenderTargetTexture: Boolean = js.native
  def isTexture: true = js.native
  def magFilter: 1003 | 1006 = js.native
  def magFilter_=(value: 1003 | 1006): Unit = js.native
  def mapping: 300 | 301 | 302 | 303 | 304 | 306 = js.native
  def mapping_=(value: 300 | 301 | 302 | 303 | 304 | 306): Unit = js.native
  var matrix: Matrix3 = js.native
  var matrixAutoUpdate: Boolean = js.native
  def minFilter: 1003 | 1004 | 1005 | 1006 | 1007 | 1008 = js.native
  def minFilter_=(value: 1003 | 1004 | 1005 | 1006 | 1007 | 1008): Unit = js.native
  def mipmaps: js.Array[CompressedTextureMipmap] | js.Array[CubeTexture[js.Any]] | js.Array[org.scalajs.dom.HTMLCanvasElement] = js.native
  def mipmaps_=(value: js.Array[CompressedTextureMipmap] | js.Array[CubeTexture[js.Any]] | js.Array[org.scalajs.dom.HTMLCanvasElement]): Unit = js.native
  var name: String = js.native
  def needsPMREMUpdate_=(value: Boolean): Unit = js.native
  def needsUpdate_=(value: Boolean): Unit = js.native
  var normalized: Boolean = js.native
  var offset: Vector2 = js.native
  var onUpdate: js.Function1[Texture[js.Any, TextureEventMap], Unit] | Null = js.native
  var pmremVersion: Double = js.native
  var premultiplyAlpha: Boolean = js.native
  var renderTarget: RenderTarget[Texture[js.Any, TextureEventMap], RenderTargetEventMap] | Null = js.native
  var repeat: Vector2 = js.native
  var rotation: Double = js.native
  var source: Source[TImage] = js.native
  def `type`: 1009 | 1010 | 1011 | 1012 | 1013 | 1014 | 1015 | 1016 | 1017 | 1018 | 1020 | 35899 | 35902 = js.native
  def `type_=`(value: 1009 | 1010 | 1011 | 1012 | 1013 | 1014 | 1015 | 1016 | 1017 | 1018 | 1020 | 35899 | 35902): Unit = js.native
  def unpackAlignment: Double = js.native
  def unpackAlignment_=(value: Double): Unit = js.native
  var updateRanges: js.Array[BoxGeometryDrawRange] = js.native
  var userData: js.Dictionary[js.Any] = js.native
  var uuid: String = js.native
  var version: Double = js.native
  def width: Double = js.native
  var wrapS: 1000 | 1001 | 1002 = js.native
  var wrapT: 1000 | 1001 | 1002 = js.native
  def addUpdateRange(start: Double, count: Double): Unit = js.native
  def clearUpdateRanges(): Unit = js.native
  @JSName("clone")
  protected def clone$raw(): Texture[TImage, TEventMap] = js.native
  @JSName("copy")
  protected def copy$raw(source: Texture[TImage, TextureEventMap]): Texture[TImage, TEventMap] = js.native
  def dispose(): Unit = js.native
  def setValues(values: TextureParameters): Unit = js.native
  def toJSON(meta: js.UndefOr[String | js.Any] = js.undefined): TextureJSON = js.native
  def transformUv(uv: Vector2): Vector2 = js.native
  def updateMatrix(): Unit = js.native


object Texture:
  extension [TImage, TEventMap <: TextureEventMap, Self <: Texture[TImage, TEventMap]](self: Self)
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]
    inline def copy(source: Texture[TImage, TextureEventMap]): Self = self.copy$raw(source).asInstanceOf[Self]

  // #WARN: static 'DEFAULT_ANISOTROPY' is declared read-write upstream; the member-level native publication cannot express a JS setter, so its write direction is withheld
  @js.native
  @JSImport("three", "Texture.DEFAULT_ANISOTROPY")
  def DEFAULT_ANISOTROPY: Double = js.native
  // #WARN: static 'DEFAULT_IMAGE' is declared read-write upstream; the member-level native publication cannot express a JS setter, so its write direction is withheld
  @js.native
  @JSImport("three", "Texture.DEFAULT_IMAGE")
  def DEFAULT_IMAGE: Null = js.native
  // #WARN: static 'DEFAULT_MAPPING' is declared read-write upstream; the member-level native publication cannot express a JS setter, so its write direction is withheld
  @js.native
  @JSImport("three", "Texture.DEFAULT_MAPPING")
  def DEFAULT_MAPPING: 300 | 303 | 304 = js.native
