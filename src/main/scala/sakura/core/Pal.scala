package sakura.core

/** One place for every colour in the scene, ported 1:1 from upstream `src/core/palette.js`.
  * The palette is deliberately narrow: warm off-whites, a gray-purple road, teal-leaning greens,
  * pale pinks, and four saturated accents reserved for focal objects.
  */
object Pal:
  // --- sky & atmosphere ---
  val skyTop: Int     = 0x8fbdea
  val skyMid: Int     = 0xd4e8fa
  val skyHaze: Int    = 0xfbe7e9
  val cloud: Int      = 0xfdfaf8
  val cloudShade: Int = 0xe6e6f2
  val fog: Int        = 0xe6ecf7
  val hill: Int       = 0xc6cfe6
  val hillFar: Int    = 0xd8dded

  // --- light ---
  val sun: Int        = 0xfff1d8
  val fill: Int       = 0xa9bdf5
  val hemiSky: Int    = 0xdcecff
  val hemiGround: Int = 0xb6a6c6

  // --- ink ---
  val ink: Int     = 0x39324f
  val inkSoft: Int = 0x4a4468

  // --- ground ---
  val road: Int         = 0x8e8a9c
  val roadWorn: Int     = 0x9a95a6
  val roadDark: Int     = 0x7b7689
  val lineWhite: Int    = 0xf4f2f6
  val lineYellow: Int   = 0xf0c341
  val tactile: Int      = 0xf2c53d
  val sidewalk: Int     = 0xdcd8e2
  val sidewalkAlt: Int  = 0xe7e2e6
  val curb: Int         = 0xc7c2d0
  val concrete: Int     = 0xd9d5dd
  val concreteMid: Int  = 0xc2bdc8
  val concreteDark: Int = 0xa7a2b0
  val gutter: Int       = 0xbdb8c4
  val drain: Int        = 0x6d687a
  val dirt: Int         = 0xc9bfae
  val gravel: Int       = 0xa9a3ab
  val ballast: Int      = 0x7d7686

  // --- buildings ---
  val wallWhite: Int    = 0xfaf6ef
  val wallCream: Int    = 0xf2e7d3
  val wallBlue: Int     = 0xd6e3ee
  val wallBeige: Int    = 0xe7dbc4
  val wallGray: Int     = 0xdedee6
  val wallPink: Int     = 0xf0dcda
  val wallTea: Int      = 0xdccdb6
  val wallSage: Int     = 0xdde2d6
  val roofSlate: Int    = 0x59617a
  val roofBlue: Int     = 0x4d5c78
  val roofBrown: Int    = 0x6b585c
  val roofTeal: Int     = 0x4f6b70
  val trim: Int         = 0x8b8496
  val glass: Int        = 0x9dc0d4
  val glassDark: Int    = 0x53627a
  val shutter: Int      = 0x6e6a7a
  val shutterLight: Int = 0x847f92

  // --- accents ---
  val red: Int        = 0xe0453f
  val redDeep: Int    = 0xb5322f
  val redSoft: Int    = 0xef6a60
  val yellow: Int     = 0xf4c033
  val yellowDeep: Int = 0xd39c1f
  val black: Int      = 0x322e3b
  val blackSoft: Int  = 0x453f4f
  val teal: Int       = 0x2f9c9a
  val tealDeep: Int   = 0x22736f
  val blue: Int       = 0x3d6ec4
  val blueDeep: Int   = 0x2a4f97
  val orange: Int     = 0xef8a3c
  val purple: Int     = 0x8f6fb5

  // --- vegetation ---
  val leaf: Int      = 0x5aa578
  val leafDeep: Int  = 0x3f7f60
  val leafPale: Int  = 0x84bd97
  val grass: Int     = 0x86ab84
  val trunk: Int     = 0x9a8082
  val trunkDark: Int = 0x765f62

  // --- cherry blossom ---
  val blossom: Int      = 0xfbc6d8
  val blossomLight: Int = 0xfff0f4
  val blossomWarm: Int  = 0xfedde2
  val blossomDeep: Int  = 0xf0a3c0
  val petal: Int        = 0xfcd9e4
  val petalDeep: Int    = 0xf6bccf

  // --- railway ---
  val railMetal: Int    = 0x6b6472
  val railHead: Int     = 0xc2bcc4
  val sleeper: Int      = 0x6d6576
  val sleeperLight: Int = 0x847b8c
  val gateYellow: Int   = 0xf4c033
  val gateBlack: Int    = 0x322e3b
  val signalRed: Int    = 0xf2453c
  val signalOff: Int    = 0x6a3b44
  val cabinet: Int      = 0xd8d5da
  val cabinetTop: Int   = 0xb6b2bc

  // --- train ---
  val trainBody: Int      = 0xf7f2e6
  val trainBodyShade: Int = 0xe6dfd0
  val trainStripe: Int    = 0x2f7fd0
  val trainStripe2: Int   = 0x3fae9a
  val trainWindow: Int    = 0x3a4258
  val trainWindowLit: Int = 0x6b7794
  val trainSkirt: Int     = 0x9aa0ad
  val trainRoof: Int      = 0xbdb8bd
  val trainDoor: Int      = 0xeae4d8

  // --- metal / misc props ---
  val metal: Int          = 0xb8bcc6
  val metalDark: Int      = 0x878b96
  val metalWarm: Int      = 0xc9c0b4
  val mirrorBack: Int     = 0xe4a83c
  val mirrorFace: Int     = 0xc8d8e4
  val vendWhite: Int      = 0xf8f5f0
  val vendRed: Int        = 0xdb4038
  val vendTeal: Int       = 0x2e9a98
  val crate: Int          = 0x3f7fbf
  val crateAlt: Int       = 0xe25a4a
  val basket: Int         = 0xdb5a4a
  val bin: Int            = 0x5d8fb8
  val taxiYellow: Int     = 0xf5be2a
  val taxiYellowDeep: Int = 0xdc9f18
  val cat: Int            = 0xf0e6da
  val catDark: Int        = 0x6a5f63
  val umbrella: Int       = 0xd8ecf4
  val shrineStone: Int    = 0xcfcad2
  val shrineBib: Int      = 0xd8453f

  // --- the wider district: ground ---
  val clay: Int     = 0xcfb59c
  val clayLine: Int = 0xe8dcc8
  val sand: Int     = 0xdccaa6
  val moss: Int     = 0x7d9c74

  // --- water ---
  val water: Int      = 0x93b8ce
  val waterDeep: Int  = 0x6d90ad
  val waterSky: Int   = 0xcadff0
  val waterPetal: Int = 0xf3cada

  // --- stone ---
  val stone: Int     = 0xc6c0cb
  val stoneDark: Int = 0xa39daf
  val stoneWarm: Int = 0xcfc6bc

  // --- school ---
  val schoolWall: Int     = 0xf7f3ea
  val schoolWallAlt: Int  = 0xe4ebf2
  val schoolWallBlue: Int = 0xd3e0ec
  val schoolTrim: Int     = 0xcfd6de
  val schoolRoof: Int     = 0x4d5468
  val gymWall: Int        = 0xedeff4
  val gymRoof: Int        = 0x59606f
  val blackboard: Int     = 0x3d5148
  val deskTop: Int        = 0xd8c29c
  val locker: Int         = 0xb7c7d5
  val curtain: Int        = 0xf4ead9
  val corridor: Int       = 0xd8d2c6

  // --- shrine ---
  val torii: Int          = 0xd8412f
  val toriiDeep: Int      = 0xa72f23
  val shrineWood: Int     = 0xa9744f
  val shrineWoodDark: Int = 0x8a604a
  val shrineRoof: Int     = 0x69707e
  val rope: Int           = 0xf0e5ca
  val ema: Int            = 0xe9d9b6
  val bamboo: Int         = 0x94b06b
  val bambooDeep: Int     = 0x6f8c50
  val cedar: Int          = 0x3f6b52
  val cedarDeep: Int      = 0x2f5540
  val cedarLit: Int       = 0x64906b
  val cedarBark: Int      = 0x7e6150

  // --- shopping street ---
  val awningGreen: Int  = 0x4f8f6a
  val awningOrange: Int = 0xe08a3c
  val awningBlue: Int   = 0x4a7fae
  val awningCream: Int  = 0xefe0c2
  val lantern: Int      = 0xf6e2c0
  val lanternLit: Int   = 0xffd9a0
  val noren: Int        = 0x2f4a72
  val norenRed: Int     = 0xb5322f
  val norenCream: Int   = 0xf2e8d6
  val freezer: Int      = 0xd8e6ee

  // --- 湯の坂, the onsen street ---
  val onsenWood: Int       = 0x8a6647
  val onsenWoodDark: Int   = 0x513a28
  val onsenWoodPale: Int   = 0xc4a074
  val onsenPlaster: Int    = 0xe8dfd0
  val onsenPlasterAlt: Int = 0xd9cdba
  val onsenTile: Int       = 0x454452
  val onsenTileEdge: Int   = 0x5c5a6a
  val onsenSlab: Int       = 0xcac4c6
  val onsenWater: Int      = 0xb9d0d4
  val onsenWaterDeep: Int  = 0x8fb0b6
  val onsenSteam: Int      = 0xf4eef0
  val onsenIndigo: Int     = 0x2c3a52

  // --- 裏山, the back hills ---
  val hillGrassSun: Int   = 0xb4c98e
  val hillGrass: Int      = 0x9fbc90
  val hillGrassDeep: Int  = 0x7a9c78
  val hillBracken: Int    = 0xc6bf86
  val hillLitter: Int     = 0x7e8163
  val hillEarth: Int      = 0xbdb2a2
  val hillRock: Int       = 0xb4aeb6
  val hillMoss: Int       = 0x83a06d
  val hillPath: Int       = 0xc8b69a
  val hillPathStone: Int  = 0xc0bcc4
  val tunnelFace: Int     = 0xc7c2ca
  val tunnelFaceDark: Int = 0xaba7b3
  val tunnelBore: Int     = 0x565269
  val tunnelBoreDeep: Int = 0x322f42

  // --- ひばり湖 ---
  val lakeSky: Int       = 0xcfe3f2
  val lakeShallow: Int   = 0x9dc4bd
  val lakeWater: Int     = 0x7ba6bd
  val lakeDeep: Int      = 0x5f83a4
  val lakeGlint: Int     = 0xf2f7fa
  val lakeHillEcho: Int  = 0x86a8a8
  val lakeBloomEcho: Int = 0xe6c3cf
  val lakeBed: Int       = 0x9aae9e
  val lakeShore: Int     = 0xcfc6b4
  val willow: Int        = 0xa8c489
  val boatWhite: Int     = 0xf0ece2
  val boatBlue: Int      = 0xa8c6d8
  val boatYellow: Int    = 0xe8d295
  val boatRed: Int       = 0xcf6a5e
  val boatTeal: Int      = 0x77b3ad
  val boatDeck: Int      = 0xc9b492
  val tentCream: Int     = 0xe4dcc6
  val tentGreen: Int     = 0xb8c4a6
  val tentBlue: Int      = 0xb4c6d2
  val tentOchre: Int     = 0xcbb08a

  /** Bright can/bottle colours for vending machine shelves. */
  val drinks: Vector[Int] = Vector(
    0xe0453f, 0xf4c033, 0x3d6ec4, 0x2f9c9a, 0xef8a3c, 0x8f6fb5,
    0x5aa578, 0xf4f2f6, 0xe86f9c, 0x44b4d8, 0xc94f7a, 0x9dbb3c
  )
end Pal
