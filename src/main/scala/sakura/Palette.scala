package sakura

/** Anime background palette for the crossing, taken from the reference environment's own palette
  * module.
  *
  * Values are 24-bit `0xRRGGBB` because that is the form both consumers speak: three.js
  * `Color.setHex` and, through [[glslRgb]], a GLSL `vec3` literal.
  */
object Palette:

  val sunHaze: Int = 0xfff1d8
  val fog: Int     = 0xe8dff0

  val sun: Int        = 0xfff1d8
  val fill: Int       = 0xa9bdf5
  val hemiSky: Int    = 0xdcecff
  val hemiGround: Int = 0xb6a6c6
  val bounce: Int     = 0xd8cbe8

  val gradeShadowTint: Int = 0xada8d0
  val gradeLightTint: Int  = 0xfff7e8

  val skyTop: Int     = 0x74a8dc
  val skyMid: Int     = 0xb5d4ee
  val skyHaze: Int    = 0xf6e4d4
  val cloud: Int      = 0xfffaf2
  val cloudShade: Int = 0xd8d0e2
  val hillFar: Int    = 0xb8c8dc
  val hillNear: Int   = 0x94aec8

  val hillLight: Int  = 0x5b8c4c
  val hillMid: Int    = 0x447239
  val hillShadow: Int = 0x2a4f23
  val hillLitter: Int = 0x3e3529
  val bamboo: Int     = 0x94b06b
  val bambooDeep: Int = 0x6f8c50
  val bambooLeaf: Int = 0xa6c078
  val bambooNode: Int = 0xb8c88a
  val sakuraPink: Int = 0xfde8ee
  val sakuraDeep: Int = 0xf5a5bc

  val asphalt: Int             = 0x8e8a9c
  val warningYellow: Int       = 0xf0b800
  val warningBlack: Int        = 0x222426
  val railSteel: Int           = 0x88929e
  val metal: Int               = 0xb8bcc6
  val metalDark: Int           = 0x878b96
  val bicycleShelterSheet: Int = 0xb0c4d0
  val timber: Int              = 0x40362e
  val rope: Int                = 0xf0e5ca
  val metalWarm: Int           = 0xc9c0b4
  val crate: Int               = 0x3f7fbf
  val crateAlt: Int            = 0xe25a4a
  val basket: Int              = 0xdb5a4a
  val bin: Int                 = 0x5d8fb8
  val water: Int               = 0x93b8ce
  val lanternGlow: Int         = 0xffe4a0
  val shrineBib: Int           = 0xd8453f
  val vermilion: Int           = 0xd8382c

  val roofTile: Int       = 0x364858
  val plaster: Int        = 0xefeee8
  val darkTimber: Int     = 0x4a3c30
  val concrete: Int       = 0x888e94
  val stone: Int          = 0xc6c0cb
  val stoneDark: Int      = 0xa39daf
  val stoneWarm: Int      = 0xcfc6bc
  val shrineWood: Int     = 0xa9744f
  val shrineWoodDark: Int = 0x8a604a
  val shrineRoof: Int     = 0x69707e

  val trainBody: Int      = 0xf2edd9
  val trainBodyShade: Int = 0xe2d8c8
  val trainStripe: Int    = 0x1b5fa8
  val trainStripe2: Int   = 0x4a78b8
  val trainRoof: Int      = 0xc8ccd4
  val trainSkirt: Int     = 0x5c606c
  val trainWindow: Int    = 0x2c384a
  val trainDoor: Int      = 0xe8e0d2

  val ink: Int          = 0x39324f
  val shadowViolet: Int = 0x2a2b40

  /** Channel triplet in the 0..1 range a three.js `Color` or a `vec3` literal is built from. */
  def rgb(hex: Int): (Double, Double, Double) =
    (((hex >> 16) & 0xff) / 255.0, ((hex >> 8) & 0xff) / 255.0, (hex & 0xff) / 255.0)

  def glslRgb(hex: Int): String =
    val (red, green, blue) = rgb(hex)
    s"vec3(${glslFloat(red)}, ${glslFloat(green)}, ${glslFloat(blue)})"
end Palette
