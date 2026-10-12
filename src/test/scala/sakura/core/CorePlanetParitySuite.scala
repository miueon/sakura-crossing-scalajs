package sakura.core

import sakura.ThreeInterop
import sakura.world.Planet
import vendor.three.*

/** Ground-truth parity pins for the upstream core engine and planet projection.
  *
  * Every expected value was extracted by executing the upstream JavaScript modules
  * (/tmp/upstream-sakura-crossing/src/core/{palette,util,toon,outline,sky}.js and
  * src/world/planet.js) under Node with three 0.185.1; none are hand-derived.
  */
class CorePlanetParitySuite extends munit.FunSuite:

  private def finite(values: Double*): Unit =
    values.foreach { value =>
      assert(!value.isNaN && !value.isInfinite, s"expected finite, got $value")
    }

  private def assertFiniteVector(v: Vector3): Unit = finite(v.x, v.y, v.z)

  test("the palette carries every upstream colour constant") {
    assertEquals(Pal.skyTop, 0x8fbdea)
    assertEquals(Pal.skyMid, 0xd4e8fa)
    assertEquals(Pal.skyHaze, 0xfbe7e9)
    assertEquals(Pal.cloud, 0xfdfaf8)
    assertEquals(Pal.cloudShade, 0xe6e6f2)
    assertEquals(Pal.fog, 0xe6ecf7)
    assertEquals(Pal.hill, 0xc6cfe6)
    assertEquals(Pal.hillFar, 0xd8dded)
    assertEquals(Pal.sun, 0xfff1d8)
    assertEquals(Pal.fill, 0xa9bdf5)
    assertEquals(Pal.hemiSky, 0xdcecff)
    assertEquals(Pal.hemiGround, 0xb6a6c6)
    assertEquals(Pal.ink, 0x39324f)
    assertEquals(Pal.inkSoft, 0x4a4468)
    assertEquals(Pal.road, 0x8e8a9c)
    assertEquals(Pal.roadWorn, 0x9a95a6)
    assertEquals(Pal.roadDark, 0x7b7689)
    assertEquals(Pal.lineWhite, 0xf4f2f6)
    assertEquals(Pal.lineYellow, 0xf0c341)
    assertEquals(Pal.tactile, 0xf2c53d)
    assertEquals(Pal.sidewalk, 0xdcd8e2)
    assertEquals(Pal.sidewalkAlt, 0xe7e2e6)
    assertEquals(Pal.curb, 0xc7c2d0)
    assertEquals(Pal.concrete, 0xd9d5dd)
    assertEquals(Pal.concreteMid, 0xc2bdc8)
    assertEquals(Pal.concreteDark, 0xa7a2b0)
    assertEquals(Pal.gutter, 0xbdb8c4)
    assertEquals(Pal.drain, 0x6d687a)
    assertEquals(Pal.dirt, 0xc9bfae)
    assertEquals(Pal.gravel, 0xa9a3ab)
    assertEquals(Pal.ballast, 0x7d7686)
    assertEquals(Pal.wallWhite, 0xfaf6ef)
    assertEquals(Pal.wallCream, 0xf2e7d3)
    assertEquals(Pal.wallBlue, 0xd6e3ee)
    assertEquals(Pal.wallBeige, 0xe7dbc4)
    assertEquals(Pal.wallGray, 0xdedee6)
    assertEquals(Pal.wallPink, 0xf0dcda)
    assertEquals(Pal.wallTea, 0xdccdb6)
    assertEquals(Pal.wallSage, 0xdde2d6)
    assertEquals(Pal.roofSlate, 0x59617a)
    assertEquals(Pal.roofBlue, 0x4d5c78)
    assertEquals(Pal.roofBrown, 0x6b585c)
    assertEquals(Pal.roofTeal, 0x4f6b70)
    assertEquals(Pal.trim, 0x8b8496)
    assertEquals(Pal.glass, 0x9dc0d4)
    assertEquals(Pal.glassDark, 0x53627a)
    assertEquals(Pal.shutter, 0x6e6a7a)
    assertEquals(Pal.shutterLight, 0x847f92)
    assertEquals(Pal.red, 0xe0453f)
    assertEquals(Pal.redDeep, 0xb5322f)
    assertEquals(Pal.redSoft, 0xef6a60)
    assertEquals(Pal.yellow, 0xf4c033)
    assertEquals(Pal.yellowDeep, 0xd39c1f)
    assertEquals(Pal.black, 0x322e3b)
    assertEquals(Pal.blackSoft, 0x453f4f)
    assertEquals(Pal.teal, 0x2f9c9a)
    assertEquals(Pal.tealDeep, 0x22736f)
    assertEquals(Pal.blue, 0x3d6ec4)
    assertEquals(Pal.blueDeep, 0x2a4f97)
    assertEquals(Pal.orange, 0xef8a3c)
    assertEquals(Pal.purple, 0x8f6fb5)
    assertEquals(Pal.leaf, 0x5aa578)
    assertEquals(Pal.leafDeep, 0x3f7f60)
    assertEquals(Pal.leafPale, 0x84bd97)
    assertEquals(Pal.grass, 0x86ab84)
    assertEquals(Pal.trunk, 0x9a8082)
    assertEquals(Pal.trunkDark, 0x765f62)
    assertEquals(Pal.blossom, 0xfbc6d8)
    assertEquals(Pal.blossomLight, 0xfff0f4)
    assertEquals(Pal.blossomWarm, 0xfedde2)
    assertEquals(Pal.blossomDeep, 0xf0a3c0)
    assertEquals(Pal.petal, 0xfcd9e4)
    assertEquals(Pal.petalDeep, 0xf6bccf)
    assertEquals(Pal.railMetal, 0x6b6472)
    assertEquals(Pal.railHead, 0xc2bcc4)
    assertEquals(Pal.sleeper, 0x6d6576)
    assertEquals(Pal.sleeperLight, 0x847b8c)
    assertEquals(Pal.gateYellow, 0xf4c033)
    assertEquals(Pal.gateBlack, 0x322e3b)
    assertEquals(Pal.signalRed, 0xf2453c)
    assertEquals(Pal.signalOff, 0x6a3b44)
    assertEquals(Pal.cabinet, 0xd8d5da)
    assertEquals(Pal.cabinetTop, 0xb6b2bc)
    assertEquals(Pal.trainBody, 0xf7f2e6)
    assertEquals(Pal.trainBodyShade, 0xe6dfd0)
    assertEquals(Pal.trainStripe, 0x2f7fd0)
    assertEquals(Pal.trainStripe2, 0x3fae9a)
    assertEquals(Pal.trainWindow, 0x3a4258)
    assertEquals(Pal.trainWindowLit, 0x6b7794)
    assertEquals(Pal.trainSkirt, 0x9aa0ad)
    assertEquals(Pal.trainRoof, 0xbdb8bd)
    assertEquals(Pal.trainDoor, 0xeae4d8)
    assertEquals(Pal.metal, 0xb8bcc6)
    assertEquals(Pal.metalDark, 0x878b96)
    assertEquals(Pal.metalWarm, 0xc9c0b4)
    assertEquals(Pal.mirrorBack, 0xe4a83c)
    assertEquals(Pal.mirrorFace, 0xc8d8e4)
    assertEquals(Pal.vendWhite, 0xf8f5f0)
    assertEquals(Pal.vendRed, 0xdb4038)
    assertEquals(Pal.vendTeal, 0x2e9a98)
    assertEquals(Pal.crate, 0x3f7fbf)
    assertEquals(Pal.crateAlt, 0xe25a4a)
    assertEquals(Pal.basket, 0xdb5a4a)
    assertEquals(Pal.bin, 0x5d8fb8)
    assertEquals(Pal.taxiYellow, 0xf5be2a)
    assertEquals(Pal.taxiYellowDeep, 0xdc9f18)
    assertEquals(Pal.cat, 0xf0e6da)
    assertEquals(Pal.catDark, 0x6a5f63)
    assertEquals(Pal.umbrella, 0xd8ecf4)
    assertEquals(Pal.shrineStone, 0xcfcad2)
    assertEquals(Pal.shrineBib, 0xd8453f)
    assertEquals(Pal.clay, 0xcfb59c)
    assertEquals(Pal.clayLine, 0xe8dcc8)
    assertEquals(Pal.sand, 0xdccaa6)
    assertEquals(Pal.moss, 0x7d9c74)
    assertEquals(Pal.water, 0x93b8ce)
    assertEquals(Pal.waterDeep, 0x6d90ad)
    assertEquals(Pal.waterSky, 0xcadff0)
    assertEquals(Pal.waterPetal, 0xf3cada)
    assertEquals(Pal.stone, 0xc6c0cb)
    assertEquals(Pal.stoneDark, 0xa39daf)
    assertEquals(Pal.stoneWarm, 0xcfc6bc)
    assertEquals(Pal.schoolWall, 0xf7f3ea)
    assertEquals(Pal.schoolWallAlt, 0xe4ebf2)
    assertEquals(Pal.schoolWallBlue, 0xd3e0ec)
    assertEquals(Pal.schoolTrim, 0xcfd6de)
    assertEquals(Pal.schoolRoof, 0x4d5468)
    assertEquals(Pal.gymWall, 0xedeff4)
    assertEquals(Pal.gymRoof, 0x59606f)
    assertEquals(Pal.blackboard, 0x3d5148)
    assertEquals(Pal.deskTop, 0xd8c29c)
    assertEquals(Pal.locker, 0xb7c7d5)
    assertEquals(Pal.curtain, 0xf4ead9)
    assertEquals(Pal.corridor, 0xd8d2c6)
    assertEquals(Pal.torii, 0xd8412f)
    assertEquals(Pal.toriiDeep, 0xa72f23)
    assertEquals(Pal.shrineWood, 0xa9744f)
    assertEquals(Pal.shrineWoodDark, 0x8a604a)
    assertEquals(Pal.shrineRoof, 0x69707e)
    assertEquals(Pal.rope, 0xf0e5ca)
    assertEquals(Pal.ema, 0xe9d9b6)
    assertEquals(Pal.bamboo, 0x94b06b)
    assertEquals(Pal.bambooDeep, 0x6f8c50)
    assertEquals(Pal.cedar, 0x3f6b52)
    assertEquals(Pal.cedarDeep, 0x2f5540)
    assertEquals(Pal.cedarLit, 0x64906b)
    assertEquals(Pal.cedarBark, 0x7e6150)
    assertEquals(Pal.awningGreen, 0x4f8f6a)
    assertEquals(Pal.awningOrange, 0xe08a3c)
    assertEquals(Pal.awningBlue, 0x4a7fae)
    assertEquals(Pal.awningCream, 0xefe0c2)
    assertEquals(Pal.lantern, 0xf6e2c0)
    assertEquals(Pal.lanternLit, 0xffd9a0)
    assertEquals(Pal.noren, 0x2f4a72)
    assertEquals(Pal.norenRed, 0xb5322f)
    assertEquals(Pal.norenCream, 0xf2e8d6)
    assertEquals(Pal.freezer, 0xd8e6ee)
    assertEquals(Pal.onsenWood, 0x8a6647)
    assertEquals(Pal.onsenWoodDark, 0x513a28)
    assertEquals(Pal.onsenWoodPale, 0xc4a074)
    assertEquals(Pal.onsenPlaster, 0xe8dfd0)
    assertEquals(Pal.onsenPlasterAlt, 0xd9cdba)
    assertEquals(Pal.onsenTile, 0x454452)
    assertEquals(Pal.onsenTileEdge, 0x5c5a6a)
    assertEquals(Pal.onsenSlab, 0xcac4c6)
    assertEquals(Pal.onsenWater, 0xb9d0d4)
    assertEquals(Pal.onsenWaterDeep, 0x8fb0b6)
    assertEquals(Pal.onsenSteam, 0xf4eef0)
    assertEquals(Pal.onsenIndigo, 0x2c3a52)
    assertEquals(Pal.hillGrassSun, 0xb4c98e)
    assertEquals(Pal.hillGrass, 0x9fbc90)
    assertEquals(Pal.hillGrassDeep, 0x7a9c78)
    assertEquals(Pal.hillBracken, 0xc6bf86)
    assertEquals(Pal.hillLitter, 0x7e8163)
    assertEquals(Pal.hillEarth, 0xbdb2a2)
    assertEquals(Pal.hillRock, 0xb4aeb6)
    assertEquals(Pal.hillMoss, 0x83a06d)
    assertEquals(Pal.hillPath, 0xc8b69a)
    assertEquals(Pal.hillPathStone, 0xc0bcc4)
    assertEquals(Pal.tunnelFace, 0xc7c2ca)
    assertEquals(Pal.tunnelFaceDark, 0xaba7b3)
    assertEquals(Pal.tunnelBore, 0x565269)
    assertEquals(Pal.tunnelBoreDeep, 0x322f42)
    assertEquals(Pal.lakeSky, 0xcfe3f2)
    assertEquals(Pal.lakeShallow, 0x9dc4bd)
    assertEquals(Pal.lakeWater, 0x7ba6bd)
    assertEquals(Pal.lakeDeep, 0x5f83a4)
    assertEquals(Pal.lakeGlint, 0xf2f7fa)
    assertEquals(Pal.lakeHillEcho, 0x86a8a8)
    assertEquals(Pal.lakeBloomEcho, 0xe6c3cf)
    assertEquals(Pal.lakeBed, 0x9aae9e)
    assertEquals(Pal.lakeShore, 0xcfc6b4)
    assertEquals(Pal.willow, 0xa8c489)
    assertEquals(Pal.boatWhite, 0xf0ece2)
    assertEquals(Pal.boatBlue, 0xa8c6d8)
    assertEquals(Pal.boatYellow, 0xe8d295)
    assertEquals(Pal.boatRed, 0xcf6a5e)
    assertEquals(Pal.boatTeal, 0x77b3ad)
    assertEquals(Pal.boatDeck, 0xc9b492)
    assertEquals(Pal.tentCream, 0xe4dcc6)
    assertEquals(Pal.tentGreen, 0xb8c4a6)
    assertEquals(Pal.tentBlue, 0xb4c6d2)
    assertEquals(Pal.tentOchre, 0xcbb08a)
  }

  test("the palette exposes the upstream vending drink accents") {
    assertEquals(
      Pal.drinks,
      Vector(
        0xe0453f, 0xf4c033, 0x3d6ec4, 0x2f9c9a, 0xef8a3c, 0x8f6fb5,
        0x5aa578, 0xf4f2f6, 0xe86f9c, 0x44b4d8, 0xc94f7a, 0x9dbb3c
      )
    )
  }

  test("mulberry32 reproduces the upstream bit-exact sequence") {
    val random = Util.mulberry32(42)
    assertEqualsDouble(random(), 0.6011037519201636, 0.0)
    assertEqualsDouble(random(), 0.44829055899754167, 0.0)
    assertEqualsDouble(random(), 0.8524657934904099, 0.0)
    assertEqualsDouble(random(), 0.6697340414393693, 0.0)
    assertEqualsDouble(random(), 0.17481389874592423, 0.0)
    assertEqualsDouble(random(), 0.5265925421845168, 0.0)
  }

  test("rngKit shares one PRNG stream across every helper") {
    val rng = Util.rngKit(1234)
    assertEqualsDouble(rng.next(), 0.07329497812315822, 0.0)
    assertEqualsDouble(rng.range(-3.0, 7.0), 4.034119898453355, 0.0)
    assertEquals(rng.int(2, 9), 9)
    assertEquals(rng.pick(Vector("a", "b", "c", "d")), "d")
    assertEquals(rng.chance(0.5), true)
    assertEquals(rng.sign, -1)
    assertEqualsDouble(rng.next(), 0.1617849813774228, 0.0)
    assertEqualsDouble(rng.range(0.5, 1.5), 1.302757018711418, 0.0)
    assertEquals(rng.int(-4, -1), -3)
    assertEquals(rng.chance(0.001), false)
    assertEquals(rng.pick(Vector(10, 20, 30)), 20)
  }

  test("rngKit zero seed runs the wrapped mulberry32 stream") {
    val rng = Util.rngKit(0)
    val expected = Vector(
      0.26642920868471265, 0.0003297457005828619, 0.2232720274478197,
      0.1462021479383111, 0.46732782293111086, 0.5450490827206522,
      0.6152513844426721, 0.6489853798411787
    )
    expected.foreach(value => assertEqualsDouble(rng.next(), value, 0.0))
  }

  test("rngKit pick repeats deterministically for one seed") {
    val rng = Util.rngKit(987654321)
    assertEquals(
      Vector.tabulate(5)(_ => rng.pick(Vector(7, 8, 9))),
      Vector(9, 7, 7, 9, 8)
    )
  }

  test("scalar helpers match upstream arithmetic") {
    assertEqualsDouble(Util.sstep(0.0, 1.0, 0.25), 0.15625, 0.0)
    assertEqualsDouble(Util.sstep(0.0, 1.0, 0.5), 0.5, 0.0)
    assertEqualsDouble(Util.sstep(-2.0, 4.0, -2.5), 0.0, 0.0)
    assertEqualsDouble(Util.sstep(4.0, -2.0, 3.0), 0.07407407407407407, 0.0)
    assertEqualsDouble(Util.sstep(0.0, 0.0, 5.0), 1.0, 0.0)
    assertEqualsDouble(Util.clamp(-1.0, 0.0, 1.0), 0.0, 0.0)
    assertEqualsDouble(Util.clamp(0.5, 0.0, 1.0), 0.5, 0.0)
    assertEqualsDouble(Util.clamp(2.0, 0.0, 1.0), 1.0, 0.0)
    assertEqualsDouble(Util.clamp(3.0, 5.0, 2.0), 5.0, 0.0)
    assertEqualsDouble(Util.lerp(0.0, 10.0, 0.25), 2.5, 0.0)
    assertEqualsDouble(Util.lerp(-5.0, 5.0, 1.5), 10.0, 0.0)
    assertEqualsDouble(Util.invLerp(0.0, 10.0, 2.5), 0.25, 0.0)
    assertEqualsDouble(Util.invLerp(0.0, 10.0, -1.0), -0.1, 0.0)
    assertEqualsDouble(Util.TAU, 6.283185307179586, 0.0)
    assertEqualsDouble(Util.DEG, 0.017453292519943295, 0.0)
  }

  test("trs composes the upstream placement matrix") {
    val m = Util.trs(1.0, 2.0, 3.0, 0.5, -0.25, 0.75, 2.0, 0.5, 1.5)
    val expected = Array(
      1.4178848678, 1.0228147456, 0.9713148295, 0.0,
      -0.3302241309, 0.3614838881, 0.1013973472, 0.0,
      -0.3711059389, -0.6967820395, 1.2754509679, 0.0,
      1.0, 2.0, 3.0, 1.0
    )
    val elements = ThreeInterop.matrixElements(m)
    (0 until 16).foreach(index => assertEqualsDouble(elements(index), expected(index), 1e-8))
    val identityElements = ThreeInterop.matrixElements(Util.trs())
    val identityExpected = Array(1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0)
    (0 until 16).foreach(index => assertEqualsDouble(identityElements(index), identityExpected(index), 0.0))
  }

  test("geometry helpers reproduce upstream mesh shapes") {
    val material = Toon.flat()
    val onGround = Util.boxOnGround(2.0, 3.0, 4.0, material)
    val geometry = onGround.geometry
    assertEqualsDouble(ThreeInterop.positionCount(geometry), 24.0, 0.0)
    geometry.computeBoundingBox()
    val min = geometry.boundingBox.min
    val max = geometry.boundingBox.max
    assertEqualsDouble(min.x, -1.0, 1e-9)
    assertEqualsDouble(min.y, 0.0, 1e-9)
    assertEqualsDouble(min.z, -2.0, 1e-9)
    assertEqualsDouble(max.x, 1.0, 1e-9)
    assertEqualsDouble(max.y, 3.0, 1e-9)
    assertEqualsDouble(max.z, 2.0, 1e-9)
    assertEquals(
      ThreeInterop.groups(geometry),
      Vector(
        (0.0, 6.0, Some(0.0)), (6.0, 6.0, Some(1.0)), (12.0, 6.0, Some(2.0)), (18.0, 6.0, Some(3.0)),
        (24.0, 6.0, Some(4.0)), (30.0, 6.0, Some(5.0))
      )
    )

    val boxMesh = Util.box(1.0, 1.0, 1.0, material, 5.0, -2.0, 7.0)
    assertEqualsDouble(boxMesh.position.x, 5.0, 0.0)
    assertEqualsDouble(boxMesh.position.y, -2.0, 0.0)
    assertEqualsDouble(boxMesh.position.z, 7.0, 0.0)
    assertEqualsDouble(ThreeInterop.positionCount(boxMesh.geometry), 24.0, 0.0)

    val cylinder = Util.cyl(0.5, 0.3, 2.0, 8, material, -1.0, 0.5, 2.0)
    assertEqualsDouble(cylinder.position.x, -1.0, 0.0)
    assertEqualsDouble(ThreeInterop.positionCount(cylinder.geometry), 52.0, 0.0)

    val plane = Util.plane(3.0, 2.0, material, 0.0, 1.0, -4.0)
    assertEqualsDouble(ThreeInterop.positionCount(plane.geometry), 4.0, 0.0)
  }

  test("shadowify skips transparent and flagged meshes only") {
    val opaque     = Util.box(1.0, 1.0, 1.0, Toon.flat())
    val seeThrough = Util.box(1.0, 1.0, 1.0, Toon.flat(Toon.FlatOptions(transparent = true)))
    val flagged    = Util.box(1.0, 1.0, 1.0, Toon.flat())
    ThreeInterop.setUserDataFlag(flagged, "noShadow")
    val parent = new Group()
    ThreeInterop.addChild(parent, opaque)
    ThreeInterop.addChild(parent, seeThrough)
    ThreeInterop.addChild(parent, flagged)
    Util.shadowify(parent, cast = true, receive = true)
    assertEquals(opaque.castShadow, true)
    assertEquals(opaque.receiveShadow, true)
    assertEquals(seeThrough.castShadow, false)
    assertEquals(seeThrough.receiveShadow, true)
    assertEquals(flagged.castShadow, false)
    assertEquals(flagged.receiveShadow, true)
  }

  test("sagCurve drapes a catenary between its endpoints") {
    val curve = Util.sagCurve(new Vector3(0.0, 5.0, 0.0), new Vector3(10.0, 5.0, 0.0), 2.0, 5)
    assertEquals(curve.points.length, 6)
    assertEqualsDouble(curve.points(2).x, 4.0, 1e-9)
    assertEqualsDouble(curve.points(2).y, 3.0978869674, 1e-9)
    assertEqualsDouble(curve.points(2).z, 0.0, 1e-9)
    assertEqualsDouble(curve.points(0).y, 5.0, 0.0)
    assertEqualsDouble(curve.points(5).y, 5.0, 0.0)
    assertEqualsDouble(curve.points(5).x, 10.0, 0.0)
  }

  test("bake merges mixed primitives into one buffer geometry") {
    val box      = new BoxGeometry(1.0, 1.0, 1.0)
    val cylinder = new CylinderGeometry(0.5, 0.5, 2.0, 6.0)
    val merged = Util.bake(List(
      Util.BakePart(box, Some(new Matrix4().makeTranslation(2.0, 0.0, 0.0))),
      Util.BakePart(cylinder)
    ))
    assertEqualsDouble(ThreeInterop.positionCount(merged), 64.0, 0.0)
    assert(ThreeInterop.hasAttribute(merged, "normal"))
    assert(ThreeInterop.hasAttribute(merged, "uv"))
    merged.computeBoundingBox()
    assertEqualsDouble(merged.boundingBox.min.x, -0.433013, 1e-5)
    assertEqualsDouble(merged.boundingBox.min.y, -1.0, 1e-5)
    assertEqualsDouble(merged.boundingBox.min.z, -0.5, 1e-5)
    assertEqualsDouble(merged.boundingBox.max.x, 2.5, 1e-5)
    assertEqualsDouble(merged.boundingBox.max.y, 1.0, 1e-5)
    assertEqualsDouble(merged.boundingBox.max.z, 0.5, 1e-5)
  }

  test("gradientMap builds the upstream ramps behind the nearest-filtered cache") {
    def dataOf(bands: Int | String): (Double, List[Int]) =
      val texture = Toon.gradientMap(bands)
      (texture.image.width, ThreeInterop.dataTextureBytes(texture).toList)
    assertEquals(dataOf(2), (2.0, List(96, 96, 96, 255, 255, 255, 255, 255)))
    assertEquals(dataOf(3), (3.0, List(92, 92, 92, 255, 178, 178, 178, 255, 255, 255, 255, 255)))
    assertEquals(dataOf(4), (4.0, List(80, 80, 80, 255, 142, 142, 142, 255, 202, 202, 202, 255, 255, 255, 255, 255)))
    assertEquals(dataOf(5), (5.0,
        List(74, 74, 74, 255, 124, 124, 124, 255, 172, 172, 172, 255,
          214, 214, 214, 255, 255, 255, 255, 255)))
    assertEquals(dataOf("soft"), (2.0, List(180, 180, 180, 255, 255, 255, 255, 255)))
    assertEquals(dataOf("soft3"), (3.0, List(172, 172, 172, 255, 214, 214, 214, 255, 255, 255, 255, 255)))
    val texture = Toon.gradientMap(3)
    assertEqualsDouble(texture.minFilter, 1003.0, 0.0)
    assertEqualsDouble(texture.magFilter, 1003.0, 0.0)
    assertEquals(texture.generateMipmaps, false)
    assert(Toon.gradientMap(3) eq texture, "the ramp cache must return the same texture")
  }

  test("cel builds a tinted toon material with the upstream cache key") {
    val material = Toon.cel(Toon.CelOptions(color = 0xff0000, bands = 2, tint = 0x112233))
    assert(material.isMeshToonMaterial)
    assertEqualsDouble(material.color.getHex(), 16711680.0, 0.0)
    assertEqualsDouble(ThreeInterop.gradientMapWidth(material), 2.0, 0.0)
    assertEquals(material.fog, true)
    assertEquals(material.transparent, false)
    assertEqualsDouble(material.opacity, 1.0, 0.0)
    assertEqualsDouble(material.emissive.getHex(), 0.0, 0.0)
    assertEqualsDouble(ThreeInterop.userDataColorHex(material, "shadowTint"), 1122867.0, 0.0)
    assertEquals(ThreeInterop.customProgramCacheKey(material), "celTint_112233")
    assert(Toon.cel(Toon.CelOptions(color = 0xff0000, bands = 2, tint = 0x112233)) eq material,
      "cel must reuse the cached material for an identical signature")
    val uncached = Toon.cel(Toon.CelOptions(color = 0xff0000, bands = 2, tint = 0x112233, cache = false))
    assert(!(uncached eq material))
  }

  test("the toon patch injects the ultraviolet shadow-tint helper") {
    val material = Toon.cel(Toon.CelOptions(color = 0xff0000, bands = 2, tint = 0x112233))
    val fragment = "#include <lights_toon_pars_fragment>\nvoid main() {}"
    val patched  = ThreeInterop.runOnBeforeCompile(material, fragment)
    assert(patched.uniformNames.contains("uShadowTint"), s"uniforms: ${patched.uniformNames}")
    assert(patched.fragment.startsWith("uniform vec3 uShadowTint;"))
    assert(patched.fragment.contains("applyCelShadowTint"))
    assert(patched.fragment.contains("mix(uShadowTint,vec3(1.0),celBand)"))
    assert(patched.fragment.contains("vec3 celBand = getGradientIrradiance( geometryNormal, directLight.direction )"))
    assert(patched.fragment.contains("applyCelShadowTint( celBand, directLight.color )"))
    assert(patched.fragment.contains("void main() {}"))
  }

  test("flat builds unlit materials with the upstream cache") {
    val material = Toon.flat(Toon.FlatOptions(color = 0x00ff00, transparent = true, opacity = 0.5,
      depthWrite = Some(false), toneMapped = false))
    assert(material.isMeshBasicMaterial)
    assertEqualsDouble(material.color.getHex(), 65280.0, 0.0)
    assertEquals(material.transparent, true)
    assertEqualsDouble(material.opacity, 0.5, 0.0)
    assertEquals(material.depthWrite, false)
    assertEquals(material.toneMapped, false)
    assertEquals(material.fog, true)
    assert(Toon.flat(Toon.FlatOptions(color = 0x00ff00, transparent = true, opacity = 0.5,
      depthWrite = Some(false), toneMapped = false)) eq material)
    assert(!Toon.flat(Toon.FlatOptions(color = 0x00ff00, transparent = true, opacity = 0.5,
      depthWrite = Some(false), toneMapped = false, cache = false))
      .eq(material))
    assert(Toon.MAT.ink.isMeshBasicMaterial)
    assertEqualsDouble(Toon.MAT.ink.color.getHex(), 3748431.0, 0.0)
    assertEqualsDouble(Toon.MAT.glassDark.color.getHex(), 5464698.0, 0.0)
  }

  test("hullOutline attaches the upstream back-side ink shell") {
    val mesh  = Util.box(2.0, 3.0, 4.0, Toon.flat())
    val shell = Outline.hullOutline(mesh, Outline.OutlineOptions(thickness = 0.005, color = 0x123456, opacity = 0.8))
    assert(shell.nonEmpty)
    val ink      = shell.get
    val children = ThreeInterop.children(mesh)
    assert(children.contains(ink))
    val material = ThreeInterop.asShaderMaterial(ink)
    assertEqualsDouble(material.side.toDouble, 1.0, 0.0)
    assertEqualsDouble(ThreeInterop.uniformScalar(material, "uThickness"), 0.005, 0.0)
    assertEqualsDouble(ThreeInterop.uniformColorHex(material, "uColor"), 1193046.0, 0.0)
    assertEqualsDouble(ThreeInterop.uniformScalar(material, "uOpacity"), 0.8, 0.0)
    assertEquals(material.transparent, true)
    assertEquals(material.depthWrite, true)
    assertEquals(ink.castShadow, false)
    assertEquals(ink.receiveShadow, false)
    assertEqualsDouble(ink.renderOrder, -1.0, 0.0)
    assertEqualsDouble(ThreeInterop.positionCount(ink.geometry), 24.0, 0.0)
    assert(!ThreeInterop.hasAttribute(ink.geometry, "uv"), "outline geometry keeps positions and normals only")

    /* Ultraviolet emits bodies without declarations, so the material source has to carry them: a
     * shader reading an undeclared uniform fails to compile, and three.js only reports that in the
     * browser console. `instanceMatrix` is the exception -- three.js declares it for an
     * InstancedMesh itself, and declaring it here would be a redefinition.
     */
    assert(material.vertexShader.startsWith("uniform float uThickness;\nuniform vec2 uResolution;\n"),
      "the ink shell's vertex stage must declare the uniforms it reads")
    assert(material.fragmentShader.startsWith("uniform vec3 uColor;\nuniform float uOpacity;\n"),
      "the ink shell's fragment stage must declare the uniforms it reads")

    val source    = Util.box(1.0, 1.0, 1.0, Toon.flat())
    val instances = new InstancedMesh(source.geometry, Toon.flat(), 3.0)
    val instancedShell = Outline.hullOutline(instances)
    assert(instancedShell.nonEmpty, "an InstancedMesh takes the instanced ink shell")
    val instancedMaterial = ThreeInterop.asShaderMaterial(instancedShell.get)
    assert(instancedMaterial.vertexShader.startsWith("uniform float uThickness;\nuniform vec2 uResolution;\n"),
      "the instanced ink shell's vertex stage must declare the uniforms it reads")
    assert(!instancedMaterial.vertexShader.contains("uniform mat4 instanceMatrix;"),
      "instanceMatrix is three.js's declaration, not the material's")

    Outline.setOutlineResolution(800.0, 600.0)
    val resolution = ThreeInterop.uniformVector2(material, "uResolution")
    assertEqualsDouble(resolution._1, 800.0, 1e-9)
    assertEqualsDouble(resolution._2, 600.0, 1e-9)
  }

  test("hullOutlineTree outlines every mesh that is not flagged") {
    val material = Toon.flat()
    val root     = new Group()
    val plain    = Util.box(1.0, 1.0, 1.0, material)
    val flagged  = Util.box(1.0, 1.0, 1.0, material)
    ThreeInterop.setUserDataFlag(flagged, "noOutline")
    ThreeInterop.addChild(root, plain)
    ThreeInterop.addChild(root, flagged)
    Outline.hullOutlineTree(root)
    assertEquals(plain.children.length, 1)
    assertEquals(flagged.children.length, 0)
    assert(ThreeInterop.hasUserDataFlag(plain.children(0), "isOutline"))
  }

  test("planet constants and mapping math match upstream") {
    assertEqualsDouble(Planet.R, 160.0, 0.0)
    assertEqualsDouble(Planet.Circumference, 1005.309649148734, 1e-11)
    assertEqualsDouble(Planet.Center.x, 0.0, 0.0)
    assertEqualsDouble(Planet.Center.y, -160.0, 0.0)
    assertEqualsDouble(Planet.Center.z, 0.0, 0.0)
    assertEqualsDouble(Planet.horizonFor(1.0), 17.9164728672, 1e-9)
    assertEqualsDouble(Planet.horizonFor(10.0), 57.4456264654, 1e-9)

    val normal = Planet.normalAt(10.0, 20.0)
    assertFiniteVector(normal)
    assertEqualsDouble(normal.x, 0.06197198946, 1e-11)
    assertEqualsDouble(normal.y, 0.990260411901, 1e-11)
    assertEqualsDouble(normal.z, 0.124674733385, 1e-11)

    val position = Planet.positionAt(10.0, 2.0, 20.0)
    assertFiniteVector(position)
    assertEqualsDouble(position.x, 10.0394622925, 1e-9)
    assertEqualsDouble(position.y, 0.4221867279, 1e-9)
    assertEqualsDouble(position.z, 20.1973068084, 1e-9)

    val origin = Planet.positionAt(0.0, 0.0, 0.0)
    assertEqualsDouble(origin.x, 0.0, 1e-11)
    assertEqualsDouble(origin.y, 0.0, 1e-11)
    assertEqualsDouble(origin.z, 0.0, 1e-11)

    val basis = Planet.basisAt(5.0, -3.0)
    assertEqualsDouble(basis.up.x, 0.031239421876, 1e-11)
    assertEqualsDouble(basis.up.y, 0.999336068206, 1e-11)
    assertEqualsDouble(basis.up.z, -0.018748901386, 1e-11)
    assertEqualsDouble(basis.east.x, 0.999511758485, 1e-11)
    assertEqualsDouble(basis.east.y, -0.031244913985, 1e-11)
    assertEqualsDouble(basis.east.z, 0.0, 1e-11)
    assertEqualsDouble(basis.north.x, 0.000585807811, 1e-11)
    assertEqualsDouble(basis.north.y, 0.018739747394, 1e-11)
    assertEqualsDouble(basis.north.z, 0.9998242239, 1e-11)

    val identityBasis = Planet.basisAt(0.0, 0.0)
    assertEqualsDouble(identityBasis.up.y, 1.0, 0.0)
    assertEqualsDouble(identityBasis.east.x, 1.0, 0.0)
    assertEqualsDouble(identityBasis.north.z, 1.0, 0.0)

    val frame = ThreeInterop.matrixElements(Planet.frameAt(8.0, -6.0, 1.5))
    val expected = Array(
      0.9987502604, -0.0499791693, 0.0, 0.0,
      0.0499440318, 0.9980480964, -0.0374912116, 0.0,
      0.0018737796, 0.0374443573, 0.9992969574, 0.0,
      8.0659611333, 1.18476757, -6.0548306662, 1.0
    )
    (0 until 16).foreach(index => assertEqualsDouble(frame(index), expected(index), 1e-9))

    val flat = Planet.flatAt(Planet.positionAt(10.0, 2.0, 20.0))
    assertEqualsDouble(flat.x, 10.0, 1e-9)
    assertEqualsDouble(flat.y, 2.0, 1e-9)
    assertEqualsDouble(flat.z, 20.0, 1e-9)

    assertEqualsDouble(Planet.wrapDelta(10.0, 1000.0), 15.309649148733797, 1e-9)
    assertEqualsDouble(Planet.wrapDelta(1000.0, -1000.0), -10.619298, 1e-5)
    assertEqualsDouble(Planet.wrapDelta(-5.0, 5.0), -10.0, 0.0)
    assertEqualsDouble(Planet.wrapX(1000.0), -5.3096491487, 1e-9)
    assertEqualsDouble(Planet.wrapX(Planet.Circumference + 3.0), 3.0, 1e-9)
    assertEqualsDouble(Planet.wrapX(-Planet.Circumference / 2.0 - 1.0), 501.6548245744, 1e-9)
    // The circumference folds through fmod, so even zero leaves a one-ulp residue — the exact
    // value upstream's Node run produces for wrapX(0).
    assertEqualsDouble(Planet.wrapX(0.0), -1.1368683772161603e-13, 0.0)

    assertEqualsDouble(Planet.reliefAt(0.0, 0.0), 0.0, 0.0)
    assertEqualsDouble(Planet.reliefAt(50.0, -30.0), 0.0, 0.0)
    assertEqualsDouble(Planet.reliefAt(1000.0, 500.0), 0.0, 0.0)
  }

  test("bakeToPlanet preserves every material group of a multi-material box") {
    val material = Toon.flat()
    val box      = new BoxGeometry(4.0, 2.0, 6.0)
    val mesh     = new Mesh(box, ThreeInterop.materialArray(material, material, material, material, material, material))
    mesh.position.set(3.0, 1.0, -2.0)
    val scene = new Scene()
    ThreeInterop.addChild(scene, mesh)
    val stats = Planet.bakeToPlanet(scene, maxEdge = 4.0)
    assertEquals(stats.wrapped, 1)
    assertEquals(stats.instanced, 0)
    assertEquals(stats.rigid, 0)
    assertEquals(stats.tris, 32)
    val geometry = mesh.geometry
    assertEquals(
      ThreeInterop.groups(geometry),
      Vector(
        (0.0, 18.0, Some(0.0)), (18.0, 18.0, Some(1.0)), (36.0, 18.0, Some(2.0)), (54.0, 18.0, Some(3.0)),
        (72.0, 12.0, Some(4.0)), (84.0, 12.0, Some(5.0))
      ),
      "all six material groups survive subdivision and wrapping"
    )
    assertEqualsDouble(ThreeInterop.positionCount(geometry), 96.0, 0.0)
    assertEqualsDouble(mesh.position.x, 0.0, 0.0)
    assertEqualsDouble(mesh.position.y, 0.0, 0.0)
    assertEqualsDouble(mesh.position.z, 0.0, 0.0)
    assertEqualsDouble(mesh.scale.x, 1.0, 0.0)
    assertEquals(mesh.frustumCulled, true)
    assert(ThreeInterop.hasAttribute(geometry, "normal"))
    val first = ThreeInterop.geometryPosition(geometry, 0)
    finite(first._1, first._2, first._3)
    assertEqualsDouble(first._1, 4.999089, 1e-5)
    assertEqualsDouble(first._2, -0.081242, 1e-5)
    assertEqualsDouble(first._3, 0.999994, 1e-5)
  }

  test("bakeToPlanet re-seats every instanced mesh on the surface") {
    val material   = Toon.flat()
    val geometry   = new BoxGeometry(0.5, 1.0, 0.5)
    val instanced  = new InstancedMesh(geometry, material, 3.0)
    val placements = Vector((2.0, 0.0, 4.0), (-6.0, 0.0, 1.0), (9.0, 0.0, -7.0))
    placements.zipWithIndex.foreach { case ((x, y, z), index) =>
      instanced.setMatrixAt(index.toDouble, new Matrix4().makeTranslation(x, y, z))
    }
    val scene = new Scene()
    ThreeInterop.addChild(scene, instanced)
    val stats = Planet.bakeToPlanet(scene, maxEdge = 4.0)
    assertEquals(stats.instanced, 3)
    assertEquals(instanced.frustumCulled, false)
    assertEqualsDouble(ThreeInterop.positionCount(instanced.geometry), 24.0, 0.0)
    val expected = Vector(
      (1.99932301, -0.06249333, 3.99958324), (-5.99847651, -0.11560961, 0.9999935),
      (8.98664761, -0.40591669, -6.99776697)
    )
    (0 until 3).foreach { index =>
      val transform = ThreeInterop.instanceTransform(instanced, index)
      finite(transform.position._1, transform.position._2, transform.position._3)
      assertEqualsDouble(transform.position._1, expected(index)._1, 1e-7)
      assertEqualsDouble(transform.position._2, expected(index)._2, 1e-7)
      assertEqualsDouble(transform.position._3, expected(index)._3, 1e-7)
      assertEqualsDouble(transform.scale._1, 1.0, 1e-7)
      assertEqualsDouble(transform.scale._2, 1.0, 1e-7)
      assertEqualsDouble(transform.scale._3, 1.0, 1e-7)
    }
  }

  test("bakeToPlanet re-seats planetRigid rigs without bending their children") {
    val material = Toon.flat()
    val rig      = new Group()
    ThreeInterop.setUserDataFlag(rig, "planetRigid")
    val part = Util.box(1.0, 1.0, 1.0, material, 0.3, 0.2, -0.4)
    ThreeInterop.addChild(rig, part)
    rig.position.set(12.0, 0.0, 5.0)
    rig.rotation.y = 0.7
    val scene = new Scene()
    ThreeInterop.addChild(scene, rig)
    val stats = Planet.bakeToPlanet(scene, maxEdge = 4.0)
    assertEquals(stats.rigid, 1)
    assertEquals(stats.wrapped, 0)
    assertEqualsDouble(part.position.x, 0.3, 0.0)
    assertEqualsDouble(part.position.y, 0.2, 0.0)
    assertEqualsDouble(part.position.z, -0.4, 0.0)
    assertEqualsDouble(ThreeInterop.positionCount(part.geometry), 24.0, 0.0)
    assertEqualsDouble(rig.position.x, 11.98289976, 1e-7)
    assertEqualsDouble(rig.position.y, -0.52768814, 1e-7)
    assertEqualsDouble(rig.position.z, 4.99918624, 1e-7)
  }

  test("buildPlanet seats the land sphere under the graded terrain") {
    val scene = new Scene()
    val built = Planet.buildPlanet(scene)
    assertEquals(built.group.name, "planet")
    assertEquals(scene.children.length, 1)
    val land = built.land
    assertEquals(land.name, "planetLand")
    assertEquals(land.receiveShadow, true)
    assertEquals(land.castShadow, false)
    assertEquals(land.frustumCulled, false)
    assert(ThreeInterop.isMeshToonMaterial(land))
    assertEqualsDouble(ThreeInterop.positionCount(land.geometry), 57126.0, 0.0)
    assert(ThreeInterop.hasAttribute(land.geometry, "normal"))
    val radii = ThreeInterop.geometryRadii(land.geometry, 0.0, -160.0, 0.0)
    finite(radii.min, radii.max)
    assertEqualsDouble(radii.min, 159.919985, 1e-5)
    assertEqualsDouble(radii.max, 160.968964, 1e-5)
  }

  test("seatLight pins a light direction to the local surface frame") {
    val light  = new DirectionalLight(Pal.sun.toDouble, 2.25)
    val target = new Object3D()
    light.target = target
    val basis  = Planet.basisAt(10.0, 5.0)
    val origin = Planet.positionAt(10.0, 0.0, 5.0)
    Outline.seatLight(light, (-52.0, 62.0, 56.0), basis, origin)
    assertFiniteVector(light.position)
    assertFiniteVector(target.position)
    assertEqualsDouble(light.position.x, -38.1485579537, 1e-9)
    assertEqualsDouble(light.position.y, 62.9599550335, 1e-9)
    assertEqualsDouble(light.position.z, 62.9090293799, 1e-9)
    assertEqualsDouble(target.position.x, 9.9886116177, 1e-9)
    assertEqualsDouble(target.position.y, -0.3903644045, 1e-9)
    assertEqualsDouble(target.position.z, 4.9991862377, 1e-9)
  }

  test("buildSky paints the dome and seats the deterministic cloud rings") {
    val scene = new Scene()
    val sky   = Sky.buildSky(scene, 500.0, () => new Texture())
    assertEquals(scene.children.length, 2)
    assertEqualsDouble(ThreeInterop.domeRadius(sky.dome), 500.0, 0.0)
    assertEquals(sky.dome.frustumCulled, false)
    assertEqualsDouble(sky.dome.renderOrder, -10.0, 0.0)
    val domeMaterial = ThreeInterop.asShaderMaterial(sky.dome)
    assertEqualsDouble(ThreeInterop.uniformScalar(domeMaterial, "uBands"), 26.0, 0.0)
    assertEqualsDouble(ThreeInterop.uniformColorHex(domeMaterial, "uTop"), 9420266.0, 0.0)
    assertEquals(sky.clouds.frustumCulled, false)
    assertEquals(sky.clouds.children.length, 22)
    val first = sky.clouds.children(0)
    assertEqualsDouble(first.renderOrder, -9.0, 0.0)
    assertEquals(first.children.length, 2)
    assertFiniteVector(first.position)
    assertEqualsDouble(first.position.x, -47.0245477, 1e-7)
    assertEqualsDouble(first.position.y, 130.32607269, 1e-7)
    assertEqualsDouble(first.position.z, -277.9393089, 1e-7)
    assertEqualsDouble(ThreeInterop.meshMaterialOpacity(first.children(0)), 0.34, 1e-9)
    assertEqualsDouble(ThreeInterop.meshMaterialOpacity(first.children(1)), 0.62, 1e-9)
    assertEqualsDouble(ThreeInterop.meshMapWrapS(first.children(1)), 1001.0, 0.0)
  }

  test("buildDistantHills mirrors two silhouette ridges per layer") {
    val scene = new Scene()
    val hills = Sky.buildDistantHills(scene)
    assertEquals(hills.children.length, 4)
    assertEqualsDouble(hills.children(0).renderOrder, -8.0, 0.0)
    val first = hills.children(0).position
    finite(first.x, first.y, first.z)
    assertEqualsDouble(first.x, 2.80236801, 1e-7)
    assertEqualsDouble(first.y, -6.0, 1e-9)
    assertEqualsDouble(first.z, -330.0, 1e-9)
  }
end CorePlanetParitySuite
