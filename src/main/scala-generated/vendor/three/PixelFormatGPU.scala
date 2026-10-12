// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three


opaque type PixelFormatGPU = String

object PixelFormatGPU:
  val ALPHA: PixelFormatGPU = "ALPHA"
  val DEPTH24STENCIL8: PixelFormatGPU = "DEPTH24_STENCIL8"
  val DEPTH32FSTENCIL8: PixelFormatGPU = "DEPTH32F_STENCIL8"
  val DEPTHCOMPONENT16: PixelFormatGPU = "DEPTH_COMPONENT16"
  val DEPTHCOMPONENT24: PixelFormatGPU = "DEPTH_COMPONENT24"
  val DEPTHCOMPONENT32F: PixelFormatGPU = "DEPTH_COMPONENT32F"
  val LUMINANCE: PixelFormatGPU = "LUMINANCE"
  val LUMINANCEALPHA: PixelFormatGPU = "LUMINANCE_ALPHA"
  val R11FG11FB10F: PixelFormatGPU = "R11F_G11F_B10F"
  val R16F: PixelFormatGPU = "R16F"
  val R16I: PixelFormatGPU = "R16I"
  val R16UI: PixelFormatGPU = "R16UI"
  val R32F: PixelFormatGPU = "R32F"
  val R32I: PixelFormatGPU = "R32I"
  val R32UI: PixelFormatGPU = "R32UI"
  val R8: PixelFormatGPU = "R8"
  val R8I: PixelFormatGPU = "R8I"
  val R8UI: PixelFormatGPU = "R8UI"
  val R8SNORM: PixelFormatGPU = "R8_SNORM"
  val REDINTEGER: PixelFormatGPU = "RED_INTEGER"
  val RG16F: PixelFormatGPU = "RG16F"
  val RG16I: PixelFormatGPU = "RG16I"
  val RG16UI: PixelFormatGPU = "RG16UI"
  val RG32F: PixelFormatGPU = "RG32F"
  val RG32I: PixelFormatGPU = "RG32I"
  val RG32UI: PixelFormatGPU = "RG32UI"
  val RG8: PixelFormatGPU = "RG8"
  val RG8I: PixelFormatGPU = "RG8I"
  val RG8UI: PixelFormatGPU = "RG8UI"
  val RG8SNORM: PixelFormatGPU = "RG8_SNORM"
  val RGB: PixelFormatGPU = "RGB"
  val RGB10A2: PixelFormatGPU = "RGB10_A2"
  val RGB10A2UI: PixelFormatGPU = "RGB10_A2UI"
  val RGB16F: PixelFormatGPU = "RGB16F"
  val RGB16I: PixelFormatGPU = "RGB16I"
  val RGB16UI: PixelFormatGPU = "RGB16UI"
  val RGB32F: PixelFormatGPU = "RGB32F"
  val RGB32I: PixelFormatGPU = "RGB32I"
  val RGB32UI: PixelFormatGPU = "RGB32UI"
  val RGB565: PixelFormatGPU = "RGB565"
  val RGB5A1: PixelFormatGPU = "RGB5_A1"
  val RGB8: PixelFormatGPU = "RGB8"
  val RGB8I: PixelFormatGPU = "RGB8I"
  val RGB8UI: PixelFormatGPU = "RGB8UI"
  val RGB8SNORM: PixelFormatGPU = "RGB8_SNORM"
  val RGB9E5: PixelFormatGPU = "RGB9_E5"
  val RGBA: PixelFormatGPU = "RGBA"
  val RGBA16F: PixelFormatGPU = "RGBA16F"
  val RGBA16I: PixelFormatGPU = "RGBA16I"
  val RGBA16UI: PixelFormatGPU = "RGBA16UI"
  val RGBA32F: PixelFormatGPU = "RGBA32F"
  val RGBA32I: PixelFormatGPU = "RGBA32I"
  val RGBA32UI: PixelFormatGPU = "RGBA32UI"
  val RGBA4: PixelFormatGPU = "RGBA4"
  val RGBA8: PixelFormatGPU = "RGBA8"
  val RGBA8I: PixelFormatGPU = "RGBA8I"
  val RGBA8UI: PixelFormatGPU = "RGBA8UI"
  val RGBA8SNORM: PixelFormatGPU = "RGBA8_SNORM"
  val SRGB8: PixelFormatGPU = "SRGB8"
  val SRGB8_4254d008: PixelFormatGPU = "SRGB8"
  val SRGB8ALPHA8: PixelFormatGPU = "SRGB8_ALPHA8"

  val all: Set[PixelFormatGPU] = Set(
    ALPHA,
    DEPTH24STENCIL8,
    DEPTH32FSTENCIL8,
    DEPTHCOMPONENT16,
    DEPTHCOMPONENT24,
    DEPTHCOMPONENT32F,
    LUMINANCE,
    LUMINANCEALPHA,
    R11FG11FB10F,
    R16F,
    R16I,
    R16UI,
    R32F,
    R32I,
    R32UI,
    R8,
    R8I,
    R8UI,
    R8SNORM,
    REDINTEGER,
    RG16F,
    RG16I,
    RG16UI,
    RG32F,
    RG32I,
    RG32UI,
    RG8,
    RG8I,
    RG8UI,
    RG8SNORM,
    RGB,
    RGB10A2,
    RGB10A2UI,
    RGB16F,
    RGB16I,
    RGB16UI,
    RGB32F,
    RGB32I,
    RGB32UI,
    RGB565,
    RGB5A1,
    RGB8,
    RGB8I,
    RGB8UI,
    RGB8SNORM,
    RGB9E5,
    RGBA,
    RGBA16F,
    RGBA16I,
    RGBA16UI,
    RGBA32F,
    RGBA32I,
    RGBA32UI,
    RGBA4,
    RGBA8,
    RGBA8I,
    RGBA8UI,
    RGBA8SNORM,
    SRGB8,
    SRGB8_4254d008,
    SRGB8ALPHA8,
  )

  def fromString(s: String): Option[PixelFormatGPU] =
    if all.contains(s) then Some(s) else None
