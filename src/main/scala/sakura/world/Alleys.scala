package sakura.world

import District.SignPlate
import Ground.{LaneOptions, PadOptions, WallOptions}
import sakura.ThreeInterop
import sakura.core.{Pal, Textures, Util}
import sakura.core.Toon
import sakura.core.Toon.{CelOptions, FlatOptions}
import vendor.three.*

import scala.collection.mutable

/** 1:1 port of upstream `src/world/alleys.js`: the two back streets -- さくら坂裏路地 behind the
  * shopping street's west row and 駅裏の小径 on the far side of the tracks.
  *
  * Every district in this world had a front.  What it never had is the other half of a Japanese
  * suburb: the 1.8 to 3 metre gaps behind the frontages where the extract fans, the shared tap,
  * the tin store, the drying pole and somebody's dead bicycle actually live.  Both paths obey one
  * rule -- nothing stands in the middle, everything is against a wall.
  */
object Alleys:

  final private case class Run(x: Double, w: Double, z0: Double, z1: Double)

  /** さくら坂裏路地: the narrow stretch, then the widening where the plots run out. */
  private val Narrow   = Run(x = 13.5, w = 2.1, z0 = 15.9, z1 = 33.8)
  private val Widened  = Run(x = 12.7, w = 3.4, z0 = 33.4, z1 = 43.9)
  private val ShopBack = 14.6

  /** 駅裏の小径. */
  private val Station = Run(x = 15.1, w = 2.6, z0 = -19.5, z1 = -5.8)

  private object M:
    lazy val concrete    = Toon.cel(CelOptions(color = Pal.concrete, bands = 3, tint = 0x6f6790))
    lazy val concreteMid = Toon.cel(CelOptions(color = Pal.concreteMid, bands = 3, tint = 0x6a6288))
    lazy val metal       = Toon.cel(CelOptions(color = Pal.metal, bands = 3, tint = 0x666090))
    lazy val metalDark   = Toon.cel(CelOptions(color = Pal.metalDark, bands = 3, tint = 0x5c5680))
    lazy val drain       = Toon.cel(CelOptions(color = Pal.drain, bands = 3, tint = 0x5d5878))
    lazy val wood        = Toon.cel(CelOptions(color = 0x9c7f5e, bands = 3, tint = 0x5c5680))
    lazy val dark        = Toon.cel(CelOptions(color = Pal.blackSoft, bands = 2, tint = 0x4b4560))

  def buildAlleys(ctx: WorldCtx): Planting =
    val shrubs = mutable.ArrayBuffer.empty[ShrubSpot]
    val petals = mutable.ArrayBuffer.empty[PetalPatch]
    buildShopBackAlley(ctx, shrubs, petals)
    buildStationAlley(ctx, shrubs, petals)
    Planting(shrubs = shrubs.toVector, petals = petals.toVector)
  end buildAlleys

  private def buildShopBackAlley(
    ctx: WorldCtx,
    shrubs: mutable.ArrayBuffer[ShrubSpot],
    petals: mutable.ArrayBuffer[PetalPatch]
  ): Unit =
    val gm  = Ground.groundMats()
    val rng = Util.rngKit(8801)

    /* surfaces: two runs, because the alley is not one width.  `lane` sweeps with the street's own
     * slope, which matters here -- the ground climbs 0.36 m over the twenty-eight metres and a flat
     * slab would stand a hand's width proud of it at the top.  Both `axis = "z"` runs skip the
     * platform registration, so the height query answers with the street's own graded surface.
     */
    Ground.lane(ctx,
      LaneOptions(axis = "z", at = Narrow.x, from = Narrow.z0, to = Narrow.z1,
        w = Narrow.w, mat = Some(gm.concrete), kerb = false, rise = 0.05, name = "backAlley"))
    Ground.lane(ctx,
      LaneOptions(axis = "z", at = Widened.x, from = Widened.z0, to = Widened.z1,
        w = Widened.w, mat = Some(gm.concrete), kerb = false, rise = 0.05, name = "backAlleyWide"))

    // the slotted channel down the west side, the covers, and the patches
    val drainParts = mutable.ArrayBuffer.empty[Util.BakePart]
    var z          = Narrow.z0 + 0.5
    while z < Widened.z1 do
      val wide = z > Widened.z0
      val cx   = (if wide then Widened.x - Widened.w / 2.0 else Narrow.x - Narrow.w / 2.0) + 0.22
      drainParts += Util.BakePart(new BoxGeometry(0.28, 0.04, 0.7),
        Some(Util.trs(cx, Street.groundY(z) + 0.055, z)))
      z += 0.9
    val channel = new Mesh(Util.bake(drainParts.toList), M.drain)
    channel.receiveShadow = true
    ThreeInterop.addChild(ctx.root, channel)
    Vector(19.4, 28.6, 38.2).foreach { coverZ =>
      val wide = coverZ > Widened.z0
      val cx   = if wide then Widened.x + 0.6 else Narrow.x + 0.4
      val cover = Util.cyl(0.28, 0.28, 0.04, 12,
        Toon.cel(CelOptions(color = Pal.metalDark, bands = 3)), cx, Street.groundY(coverZ) + 0.065, coverZ)
      cover.receiveShadow = true
      ThreeInterop.addChild(ctx.root, cover)
    }
    Vector((22.6, 1.2, 1.5), (31.4, 1.0, 1.2), (40.8, 1.6, 1.8)).foreach { (patchZ, patchW, patchD) =>
      val wide = patchZ > Widened.z0
      val patch = Util.box(patchW, 0.02, patchD, gm.asphaltWorn,
        (if wide then Widened.x else Narrow.x) + 0.15, Street.groundY(patchZ) + 0.062, patchZ)
      patch.receiveShadow = true
      ThreeInterop.setUserDataFlag(patch, "noOutline")
      ThreeInterop.addChild(ctx.root, patch)
    }

    /* the shop backs: the east wall of the alley is the rear of five shopfronts, and a shop's back
     * is nothing like its front -- a service door, a gas meter, a pipe stack, an extract fan, a
     * spot lamp and whatever will not fit inside.
     */
    val backs = mutable.LinkedHashMap(
      "metal"     -> mutable.ArrayBuffer[Util.BakePart](),
      "metalDark" -> mutable.ArrayBuffer[Util.BakePart](),
      "dark"      -> mutable.ArrayBuffer[Util.BakePart](),
      "wood"      -> mutable.ArrayBuffer[Util.BakePart]()
    )
    def push(key: String, geo: BufferGeometry[?, ?], mx: Matrix4): Unit =
      backs(key) += Util.BakePart(geo, Some(mx))

    val backX = ShopBack - 0.02
    Vector((19.0, true), (24.6, false), (29.4, true), (34.6, false), (39.8, true)).foreach { (zc, isDoor) =>
      val y = Street.groundY(zc)
      if isDoor then
        push("wood", new BoxGeometry(0.08, 1.98, 0.86), Util.trs(backX, y + 1.0, zc))
        push("metal", new BoxGeometry(0.12, 2.12, 1.0), Util.trs(backX + 0.05, y + 1.02, zc))
        push("metalDark", new BoxGeometry(0.06, 0.06, 0.24), Util.trs(backX - 0.06, y + 1.0, zc - 0.32))
        // the concrete step out of it, and a light over it
        push("metal", new BoxGeometry(0.36, 0.06, 0.6), Util.trs(backX - 0.2, y + 2.28, zc))
        push("dark", new BoxGeometry(0.18, 0.14, 0.18), Util.trs(backX - 0.16, y + 2.18, zc))
      else
        push("metal", new CylinderGeometry(0.19, 0.19, 0.14, 12),
          Util.trs(backX - 0.06, y + 2.3, zc, 0.0, 0.0, math.Pi / 2.0))
        push("dark", new CylinderGeometry(0.15, 0.15, 0.06, 12),
          Util.trs(backX - 0.14, y + 2.3, zc, 0.0, 0.0, math.Pi / 2.0))
        push("metal", new BoxGeometry(0.1, 0.44, 0.34), Util.trs(backX, y + 1.5, zc))
      // the pipe stack and its branches, on every bay
      push("metal", new CylinderGeometry(0.055, 0.055, 3.2, 6), Util.trs(backX - 0.08, y + 1.6, zc + 1.6))
      (0 until 3).foreach { i =>
        push("metalDark", new BoxGeometry(0.1, 0.05, 0.1),
          Util.trs(backX - 0.08, y + 0.7 + i * 1.0, zc + 1.6))
      }
    }
    // the gas meters, in a run at the height they always are
    Vector(21.4, 27.2, 32.8, 37.6).foreach { zc =>
      val y = Street.groundY(zc)
      val plate = new Mesh(new BoxGeometry(0.1, 0.42, 0.3),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.meterBox()), cache = false)))
      plate.position.set(backX - 0.05, y + 1.62, zc)
      ThreeInterop.addChild(ctx.root, plate)
      push("metal", new BoxGeometry(0.2, 0.36, 0.26), Util.trs(backX - 0.08, y + 0.78, zc + 0.5))
      push("metalDark", new BoxGeometry(0.24, 0.05, 0.3), Util.trs(backX - 0.08, y + 0.98, zc + 0.5))
    }
    val backMatFor: Map[String, Material[?]] = Map(
      "metal" -> M.metal, "metalDark" -> M.metalDark, "wood" -> M.wood, "dark" -> M.dark
    )
    backs.foreach { (key, bucket) =>
      if bucket.nonEmpty then
        val mesh = new Mesh(Util.bake(bucket.toList), backMatFor(key))
        mesh.castShadow = true
        mesh.receiveShadow = true
        ThreeInterop.addChild(ctx.root, mesh)
    }
    // two posters nobody took down, at the height they get pasted
    Vector((23.2, 0), (36.4, 2)).foreach { (zc, variant) =>
      val poster = new Mesh(new PlaneGeometry(0.38, 0.52),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.poster(variant)), cache = false)))
      poster.position.set(backX - 0.06, Street.groundY(zc) + 1.66, zc)
      poster.rotation.y = -math.Pi / 2.0
      poster.rotation.z = rng.range(-0.03, 0.03)
      ThreeInterop.setUserDataFlag(poster, "noOutline")
      ThreeInterop.addChild(ctx.root, poster)
    }
    // the outdoor units, on brackets off the wall
    Vector((20.2, 0.86), (26.0, 0.78), (31.8, 0.9), (41.0, 0.82)).foreach { (zc, unitW) =>
      ThreeInterop.addChild(ctx.root,
        Props.makeAircon(Props.AirconOpts(
          x = backX - 0.24, z = zc, y = Street.groundY(zc) + (if zc > 30 then 0.34 else 1.5),
          ry = -math.Pi / 2.0, w = unitW, h = 0.58, feet = zc > 30
        )))
    }

    /* the west side: the backs of three houses, so the boundary is a low block wall with a service
     * gate in it -- and where the plots run out at the north end, the wall gives way to a tin store
     * and the drying ground behind the last house.
     */
    val westX = Narrow.x - Narrow.w / 2.0 - 0.02
    Ground.wallRun(ctx,
      WallOptions(axis = "z", at = westX, from = Narrow.z0 + 0.6, to = 24.0,
        y = Street.groundY(19), h = 1.55, t = 0.2, panel = 2.4, mat = Some(M.concreteMid)))
    Ground.wallRun(ctx,
      WallOptions(axis = "z", at = westX, from = 25.4, to = 33.2,
        y = Street.groundY(29), h = 1.55, t = 0.2, panel = 2.4, mat = Some(M.concreteMid)))
    // the gate in the gap, and the ivy over the run beside it
    Vector(-1.0, 1.0).foreach { s =>
      val gatePost = Util.box(0.16, 1.7, 0.16, M.concreteMid, westX, Street.groundY(24.7) + 0.85,
        24.7 + s * 0.7)
      gatePost.castShadow = true
      ThreeInterop.addChild(ctx.root, gatePost)
    }
    ThreeInterop.addChild(ctx.root,
      Util.box(0.06, 1.36, 1.3, M.metal, westX + 0.02,
        Street.groundY(24.7) + 0.72, 24.7))
    ThreeInterop.addChild(ctx.root,
      Props.makeIvy(Props.IvyOpts(x = westX, z = 21.0,
        y = Street.groundY(21), ry = math.Pi / 2.0, len = 3.6, top = 1.66, drop = 0.9, seed = 8811, face = 1.0)))
    ThreeInterop.addChild(ctx.root,
      Props.makeIvy(Props.IvyOpts(x = westX, z = 30.4,
        y = Street.groundY(30), ry = math.Pi / 2.0, len = 3.0, top = 1.66, drop = 0.8, seed = 8812, face = 1.0)))

    /* what the alley is actually for: all of it against one wall or the other, read as a walk from
     * the shopping street's alley mouth northwards.
     */
    val propWest                  = Narrow.x - Narrow.w / 2.0 + 0.35
    val propEast                  = ShopBack - 0.45
    def propY(at: Double): Double = Street.groundY(at) + 0.05

    /* the 掲示板, bolted flat to the west wall rather than standing on posts: a freestanding board
     * whose collider was 1.4 m wide in a 2.1 m alley sealed the only way in.  Centred at 1.05
     * rather than 1.3 because the wall behind it is 1.55 m to the top of its cap.
     */
    val boardX = Narrow.x - Narrow.w / 2.0 - 0.01
    val board = new Mesh(
      new BoxGeometry(0.07, 0.8, 1.5),
      Props.matArray(
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.corkBoard()), cache = false)),
        Toon.flat(FlatOptions(color = 0x7d6348)),
        Toon.flat(FlatOptions(color = 0x8a6f52)),
        Toon.flat(FlatOptions(color = 0x7d6348)),
        Toon.flat(FlatOptions(color = 0x7d6348)),
        Toon.flat(FlatOptions(color = 0x7d6348))
      )
    )
    board.position.set(boardX + 0.04, propY(17.4) + 1.05, 17.4)
    board.castShadow = true
    ThreeInterop.addChild(ctx.root, board)
    val hood = Util.box(0.3, 0.05, 1.66, M.wood, boardX + 0.14, propY(17.4) + 1.5, 17.4)
    hood.rotation.z = -0.16
    hood.castShadow = true
    ThreeInterop.addChild(ctx.root, hood)
    Vector((-0.44, Textures.poster(1), 0.5), (0.06, Textures.paperSheet(), 0.42),
      (0.52, Textures.poster(3), 0.46)).foreach { (dz, art, sheetH) =>
      val sheet = new Mesh(new PlaneGeometry(sheetH * 0.72, sheetH),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(art), cache = false)))
      sheet.position.set(boardX + 0.08, propY(17.4) + 1.05 + (if dz == 0.06 then 0.04 else 0.0), 17.4 + dz)
      sheet.rotation.y = math.Pi / 2.0
      sheet.rotation.z = rng.range(-0.03, 0.03)
      ThreeInterop.setUserDataFlag(sheet, "noOutline")
      ThreeInterop.addChild(ctx.root, sheet)
    }

    // the shared tap and its bucket, on the west wall
    ThreeInterop.addChild(ctx.root,
      Props.makeTapPost(Props.TapPostOpts(
        x = propWest, z = 18.6, y = propY(18.6), ry = math.Pi / 2.0
      )))
    ThreeInterop.addChild(ctx.root,
      Props.makeBucket(Props.BucketOpts(
        x = propWest + 0.1, z = 19.2, y = propY(19.2), ry = 0.4
      )))
    ThreeInterop.addChild(ctx.root,
      Props.makeBucket(Props.BucketOpts(
        x = propWest + 0.02, z = 19.6, y = propY(19.6), ry = -0.9, tilt = 0.12, roll = 0.08
      )))

    /* somebody's bicycle, left long enough to have a flat tyre.  Both lie along the alley: at
     * ry ~ 0 and ry ~ PI the wheelbase points straight across a 2.1 m passage, and 0.86 m of
     * bicycle ends up inside the wall it leans on.
     */
    ThreeInterop.addChild(ctx.root,
      Props.makeBicycle(Props.BicycleOpts(
        x = propWest + 0.15, z = 21.8, y = propY(21.8), ry = math.Pi / 2.0 + 0.06, lean = -0.16,
        color = 0x8f6fb5
      )))
    ThreeInterop.addChild(ctx.root,
      Props.makeBicycle(Props.BicycleOpts(
        x = propEast + 0.1, z = 25.4, y = propY(25.4), ry = -math.Pi / 2.0 + 0.1, lean = -0.14,
        color = 0x6a7f6a
      )))

    // the crates the shops stack out here, and the recycling
    ThreeInterop.addChild(ctx.root,
      Props.makeCrates(Props.CratesOpts(
        x = propEast, z = 23.6, y = propY(23.6), n = 4, seed = 8821, ry = -1.5
      )))
    ThreeInterop.addChild(ctx.root,
      Props.makeCrates(Props.CratesOpts(
        x = propEast + 0.1, z = 28.2, y = propY(28.2), n = 3, seed = 8822, ry = -1.6
      )))
    ThreeInterop.addChild(ctx.root,
      Props.makeRecycleBox(Props.RecycleBoxOpts(
        x = propEast - 0.1, z = 30.4, y = propY(30.4), ry = -math.Pi / 2.0, color = 0x6f8f6a
      )))
    // `propEast` is already the wall-side centreline; subtracting another 0.7 m left the refuse row
    // in the alley instead of tucked against the shops.
    ThreeInterop.addChild(ctx.root,
      Props.makeBins(Props.BinsOpts(
        x = propEast, z = 33.0, y = propY(33.0), ry = -math.Pi / 2.0
      )))
    ThreeInterop.addChild(ctx.root,
      District.signPostWithPlates(
        x = propEast - 0.2, z = 34.4, y = propY(34.4), ry = -math.Pi / 2.0, h = 1.5,
        plates = Vector(SignPlate(map = Textures.gomiPlate(0), w = 0.42, h = 0.32, y = Some(1.24)))
      ))

    // the pot shelf and the plants that always end up in an alley
    ThreeInterop.addChild(ctx.root,
      Props.makePotShelf(Props.PotShelfOpts(
        x = propWest + 0.05, z = 27.4, y = propY(27.4), ry = math.Pi / 2.0, w = 1.2, n = 6, seed = 8823
      )))
    ThreeInterop.addChild(ctx.root,
      Props.makePlanter(Props.PlanterOpts(
        x = propWest, z = 29.0, y = propY(29.0), r = 0.25, flower = true, seed = 8824, n = 5
      )))
    ThreeInterop.addChild(ctx.root,
      Props.makePlanter(Props.PlanterOpts(
        x = propWest - 0.05, z = 29.6, y = propY(29.6), r = 0.2, flower = false, seed = 8825, n = 4
      )))
    ThreeInterop.addChild(ctx.root,
      Props.makePetBowl(Props.PetBowlOpts(
        x = propWest + 0.1, z = 31.4, y = propY(31.4), ry = 0.5
      )))
    ThreeInterop.addChild(ctx.root,
      Props.makeCatBox(Props.CatBoxOpts(
        x = propWest + 0.2, z = 32.4, y = propY(32.4), ry = 1.2
      )))

    // and a bit of paper the wind put against the wall
    Props.makeLoosePaper(District.propCtx(ctx),
      Vector(
        Props.PaperSpot(x = propWest + 0.2, z = 24.2, y = propY(24.2) + 0.01, ry = 0.7),
        Props.PaperSpot(x = propEast - 0.4, z = 31.0, y = propY(31.0) + 0.01, ry = -1.1),
        Props.PaperSpot(x = Widened.x, z = 37.4, y = propY(37.4) + 0.01, ry = 0.3)
      ))

    /* the widening: the drying ground.  Where the plots run out the alley opens to 3.4 m, and what
     * fills it is the one thing a Japanese back street always has and this world did not -- a place
     * to hang washing, with a tin store beside it and a bicycle shelter over the end.  It is also
     * the alley's destination: a 2 m passage needs somewhere to arrive.
     */
    val yardX = Widened.x
    val yardY = Street.groundY(38.5)
    Ground.pad(ctx,
      PadOptions(x = yardX - 0.2, z = 38.4, w = Widened.w - 0.2, d = 3.4,
        y = yardY - 0.02, h = 0.06, mat = Some(gm.gravel), name = "alleyYard", platform = false))
    ThreeInterop.addChild(ctx.root,
      Props.makeLaundryPole(Props.LaundryPoleOpts(
        x = yardX - 0.5, z = 37.4, y = yardY, ry = 0.0, len = 2.4, n = 4, seed = 8831
      )))
    ThreeInterop.addChild(ctx.root,
      Props.makeLaundryPole(Props.LaundryPoleOpts(
        x = yardX - 0.5, z = 39.4, y = yardY, ry = 0.0, len = 2.4, n = 3, seed = 8832
      )))
    ThreeInterop.addChild(ctx.root,
      Props.makeStorageShed(Props.StorageShedOpts(
        x = yardX - 1.0, z = 41.2, y = yardY, ry = 0.0, w = 1.3, d = 0.72, h = 1.62
      )))
    ctx.collide(yardX - 1.7, 40.8, yardX - 0.3, 41.7, yardY + 1.75)
    ThreeInterop.addChild(ctx.root,
      Props.makeStorageShed(Props.StorageShedOpts(
        x = yardX + 0.6, z = 41.3, y = yardY, ry = 0.06, w = 1.0, d = 0.66, h = 1.42, color = 0xc4c8c0
      )))
    ctx.collide(yardX + 0.05, 40.9, yardX + 1.15, 41.7, yardY + 1.55)
    ThreeInterop.addChild(ctx.root,
      Housing.makeBikeShelter(Housing.BikeShelterOpts(
        x = yardX + 0.1, z = 35.6, y = yardY, ry = 0.0, w = 3.0, d = 1.7, h = 2.0
      )))
    /* Two bikes under the shelter, side by side down its 3 m width -- not nose to nose along it:
     * at ry ~ 0 both wheelbases ran along x and the two of them were 0.8 m apart.
     */
    ThreeInterop.addChild(ctx.root,
      Props.makeBicycle(Props.BicycleOpts(
        x = yardX - 0.6, z = 35.6, y = yardY, ry = math.Pi / 2.0 + 0.1, lean = 0.05, color = 0xd8a03c
      )))
    ThreeInterop.addChild(ctx.root,
      Props.makeBicycle(Props.BicycleOpts(
        x = yardX + 0.5, z = 35.6, y = yardY, ry = math.Pi / 2.0 - 0.08, lean = -0.06, color = 0x3f6f9c
      )))
    ThreeInterop.addChild(ctx.root,
      Props.makeUmbrellaStand(Props.UmbrellaStandOpts(
        x = yardX + 1.3, z = 39.8, y = yardY, n = 3, seed = 8833
      )))
    ThreeInterop.addChild(ctx.root,
      Props.makeBroom(Props.BroomOpts(
        x = yardX + 1.45, z = 40.6, y = yardY, tilt = 0.06, roll = -0.18, ry = 0.4
      )))
    ThreeInterop.addChild(ctx.root,
      Props.makeDoormat(Props.DoormatOpts(
        x = yardX - 1.0, z = 40.3, y = yardY, ry = 0.0, w = 0.7, d = 0.4, color = 0x7a7060
      )))
    ThreeInterop.addChild(ctx.root,
      Props.makeDeliveryBox(Props.DeliveryBoxOpts(
        x = yardX + 1.3, z = 42.6, y = yardY, ry = 0.2
      )))
    shrubs += ShrubSpot(x = yardX + 1.4, z = 37.6, y = yardY, r = 0.42, count = 3, spread = 1.0, seed = 8834)
    petals += PetalPatch(x = yardX, z = 39.0, w = 3.0, d = 5.0, y = yardY + 0.05, n = 70)
    petals += PetalPatch(x = Narrow.x, z = 24.0, w = 1.9, d = 14.0,
      y = Street.groundY(24) + 0.06, n = 80)
  end buildShopBackAlley

  private def buildStationAlley(
    ctx: WorldCtx,
    shrubs: mutable.ArrayBuffer[ShrubSpot],
    petals: mutable.ArrayBuffer[PetalPatch]
  ): Unit =
    val gm = Ground.groundMats()

    Ground.lane(ctx,
      LaneOptions(axis = "z", at = Station.x, from = Station.z0, to = Station.z1,
        w = Station.w, mat = Some(gm.concrete), kerb = false, rise = 0.05, name = "stationAlley"))

    /* Block walls both sides, which is what actually makes it a path rather than a gap: the houses
     * either side are 0.2 m and 1.3 m away and their flanks are blank, so without a boundary this
     * is two buildings with a strip of concrete between them.
     */
    Vector(-1.0, 1.0).foreach { s =>
      val at = Station.x + s * (Station.w / 2.0 + 0.1)
      Ground.wallRun(ctx,
        WallOptions(axis = "z", at = at, from = Station.z0 + 0.4, to = -12.6,
          y = Street.groundY(-16), h = 1.5, t = 0.19, panel = 2.4, mat = Some(M.concreteMid)))
      Ground.wallRun(ctx,
        WallOptions(axis = "z", at = at, from = -11.6, to = Station.z1 - 0.6,
          y = Street.groundY(-9), h = 1.5, t = 0.19, panel = 2.4, mat = Some(M.concreteMid)))
    }
    ThreeInterop.addChild(ctx.root,
      Props.makeIvy(Props.IvyOpts(
        x = Station.x - Station.w / 2.0 - 0.1, z = -15.4, y = Street.groundY(-15.4),
        ry = math.Pi / 2.0, len = 4.0, top = 1.6, drop = 0.9, seed = 8911, face = 1.0
      )))
    ThreeInterop.addChild(ctx.root,
      Props.makeIvy(Props.IvyOpts(
        x = Station.x + Station.w / 2.0 + 0.1, z = -8.6, y = Street.groundY(-8.6),
        ry = -math.Pi / 2.0, len = 3.2, top = 1.6, drop = 0.8, seed = 8912, face = 1.0
      )))

    // the channel, the covers and one patch, same grammar as the other alley
    val drainParts = mutable.ArrayBuffer.empty[Util.BakePart]
    var z          = Station.z0 + 0.5
    while z < Station.z1 do
      drainParts += Util.BakePart(new BoxGeometry(0.28, 0.04, 0.7),
        Some(Util.trs(Station.x + Station.w / 2.0 - 0.24, Street.groundY(z) + 0.055, z)))
      z += 0.9
    val channel = new Mesh(Util.bake(drainParts.toList), M.drain)
    channel.receiveShadow = true
    ThreeInterop.addChild(ctx.root, channel)
    Vector(-17.0, -10.2).foreach { coverZ =>
      val cover = Util.cyl(0.28, 0.28, 0.04, 12,
        Toon.cel(CelOptions(color = Pal.metalDark, bands = 3)), Station.x - 0.5,
        Street.groundY(coverZ) + 0.065, coverZ)
      cover.receiveShadow = true
      ThreeInterop.addChild(ctx.root, cover)
    }
    val patch = Util.box(1.4, 0.02, 1.7, gm.asphaltWorn, Station.x + 0.2,
      Street.groundY(-13.4) + 0.062, -13.4)
    patch.receiveShadow = true
    ThreeInterop.setUserDataFlag(patch, "noOutline")
    ThreeInterop.addChild(ctx.root, patch)

    /* the dressing: sparser than the shopping street's alley on purpose.  This one is a route, and
     * its job is to get somebody to the platform steps without anything at eye height in the way,
     * so the middle third is deliberately empty -- which is what makes the tunnel of it read.
     */
    val pathWest                  = Station.x - Station.w / 2.0 + 0.4
    val pathEast                  = Station.x + Station.w / 2.0 - 0.4
    def pathY(at: Double): Double = Street.groundY(at) + 0.05

    // the canal end: a plate telling you where the path goes, and a bollard pair
    ThreeInterop.addChild(ctx.root,
      District.signPostWithPlates(
        x = pathEast + 0.1, z = Station.z0 + 0.9, y = pathY(Station.z0 + 0.9), ry = -math.Pi / 2.0,
        h = 1.9,
        plates = Vector(SignPlate(map = Textures.warningPlate(0), w = 0.34, h = 0.66, y = Some(1.5), double = true))
      ))
    Vector(-1.0, 1.0).foreach { s =>
      val bx = Station.x + s * 0.7
      val bollard = Util.cyl(0.05, 0.06, 0.72, 8, M.metal, bx, Street.groundY(Station.z0 + 0.2) + 0.41,
        Station.z0 + 0.2)
      bollard.castShadow = true
      ThreeInterop.addChild(ctx.root, bollard)
      ThreeInterop.addChild(ctx.root,
        Util.cyl(0.055, 0.055, 0.1, 8,
          Toon.cel(CelOptions(color = Pal.red, bands = 3, tint = 0x7a4060)), bx,
          Street.groundY(Station.z0 + 0.2) + 0.7, Station.z0 + 0.2))
    }

    // the back gate of the house on the west, with its refuse point beside it
    ThreeInterop.addChild(ctx.root,
      Props.makeBins(Props.BinsOpts(
        x = pathWest - 0.05, z = -17.8, y = pathY(-17.8), ry = math.Pi / 2.0
      )))
    ThreeInterop.addChild(ctx.root,
      District.signPostWithPlates(
        x = pathWest - 0.1, z = -16.6, y = pathY(-16.6), ry = math.Pi / 2.0, h = 1.45,
        plates = Vector(SignPlate(map = Textures.gomiPlate(0), w = 0.4, h = 0.31, y = Some(1.2)))
      ))
    ThreeInterop.addChild(ctx.root,
      Props.makeTapPost(Props.TapPostOpts(
        x = pathEast, z = -16.0, y = pathY(-16.0), ry = -math.Pi / 2.0, h = 0.8
      )))
    ThreeInterop.addChild(ctx.root,
      Props.makeCrates(Props.CratesOpts(
        x = pathEast + 0.05, z = -18.6, y = pathY(-18.6), n = 3, seed = 8921, ry = 1.5
      )))

    // one bicycle, one pot, and a store against the far wall -- then nothing.  Along the path, for
    // the same reason as the two in the other alley.
    ThreeInterop.addChild(ctx.root,
      Props.makeBicycle(Props.BicycleOpts(
        x = pathWest, z = -14.4, y = pathY(-14.4), ry = math.Pi / 2.0 + 0.04, lean = -0.13,
        color = 0x9c5a4a
      )))
    ThreeInterop.addChild(ctx.root,
      Props.makePlanter(Props.PlanterOpts(
        x = pathEast + 0.05, z = -12.4, y = pathY(-12.4), r = 0.24, flower = true, seed = 8922, n = 5
      )))
    ThreeInterop.addChild(ctx.root,
      Props.makeStorageShed(Props.StorageShedOpts(
        x = pathWest + 0.2, z = -8.4, y = pathY(-8.4), ry = math.Pi / 2.0, w = 1.1, d = 0.62, h = 1.45,
        color = 0xc9cec6
      )))
    ctx.collide(pathWest - 0.2, -9.0, pathWest + 0.6, -7.8, pathY(-8.4) + 1.6)
    // the render this hangs on is the house flank at x = 16.9, not the path edge
    ThreeInterop.addChild(ctx.root,
      Props.makeAircon(Props.AirconOpts(
        x = 16.9 - 0.24, z = -10.6, y = pathY(-10.6) + 1.3, ry = -math.Pi / 2.0, w = 0.8, h = 0.56,
        feet = false
      )))
    Props.makeLoosePaper(District.propCtx(ctx),
      Vector(
        Props.PaperSpot(x = pathWest + 0.3, z = -11.4, y = pathY(-11.4) + 0.01, ry = 0.9),
        Props.PaperSpot(x = pathEast - 0.3, z = -7.2, y = pathY(-7.2) + 0.01, ry = -0.4)
      ))

    /* the station end: the path opens out at the foot of the platform steps, so it gets the one
     * thing that says "this is the way to the trains" -- a rack, because everybody rides here and
     * walks up.
     */
    val apronY = Street.groundY(-6.6) + 0.05
    Ground.pad(ctx,
      PadOptions(x = Station.x - 0.1, z = -6.6, w = Station.w + 0.9, d = 1.9,
        y = Street.groundY(-6.6) - 0.01, h = 0.06, mat = Some(gm.concrete), name = "stationAlleyApron",
        platform = false))
    ThreeInterop.addChild(ctx.root,
      District.signPostWithPlates(
        x = Station.x - Station.w / 2.0 - 0.5, z = -6.9, y = apronY, ry = math.Pi / 2.0, h = 2.0,
        plates = Vector(SignPlate(map = Textures.warningPlate(1), w = 0.32, h = 0.62, y = Some(1.55), double = true))
      ))

    /* The west flank of this passage is the two-storey side wall of the house at (10, -16.2),
     * 0.2 m away and completely blank above the boundary wall.  It gets what a flank like that
     * always has: a stack, a branch at each floor, and an outdoor unit high up on brackets.  Both
     * stacks and both units are inside z -19.8 .. -12.6, which is where that flank actually is.
     */
    val flankX     = Station.x - Station.w / 2.0 - 0.26
    val stackParts = mutable.ArrayBuffer.empty[Util.BakePart]
    Vector(-18.4, -13.4).foreach { zc =>
      stackParts += Util.BakePart(new CylinderGeometry(0.055, 0.055, 5.2, 6),
        Some(Util.trs(flankX, Street.groundY(zc) + 2.6, zc)))
      (0 until 3).foreach { i =>
        stackParts += Util.BakePart(new BoxGeometry(0.12, 0.05, 0.12),
          Some(Util.trs(flankX, Street.groundY(zc) + 0.9 + i * 1.7, zc)))
      }
    }
    val stackMesh = new Mesh(Util.bake(stackParts.toList), M.metal)
    stackMesh.castShadow = true
    ThreeInterop.addChild(ctx.root, stackMesh)
    /* The wall is at x = 13.4 and its outward normal is +x, so these face +PI/2 and their backs go
     * on the render: at -PI/2 and flankX + 0.24 they were turned round, fan into the wall, hanging
     * half a metre out over the path.
     */
    ThreeInterop.addChild(ctx.root,
      Props.makeAircon(Props.AirconOpts(
        x = flankX + 0.10, z = -14.6, y = Street.groundY(-14.6) + 2.75, ry = math.Pi / 2.0,
        w = 0.86, h = 0.6, feet = false
      )))
    ThreeInterop.addChild(ctx.root,
      Props.makeAircon(Props.AirconOpts(
        x = flankX + 0.10, z = -17.4, y = Street.groundY(-17.4) + 2.6, ry = math.Pi / 2.0,
        w = 0.78, h = 0.56, feet = false
      )))

    // clear of the path itself: at x = 16.4 this clump stood a metre inside it
    shrubs += ShrubSpot(x = 13.0, z = -6.5, y = Street.groundY(-6.5), r = 0.45, count = 3,
      spread = 1.0, seed = 8931)
    petals += PetalPatch(x = Station.x, z = -13.0, w = 2.4, d = 12.0,
      y = Street.groundY(-13) + 0.06, n = 80)
  end buildStationAlley

end Alleys
