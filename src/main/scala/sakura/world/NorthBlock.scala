package sakura.world

import org.scalajs.dom
import sakura.ThreeInterop
import sakura.core.{Outline, Pal, Textures, Toon, Util}
import sakura.core.Outline.OutlineOptions
import sakura.core.Toon.{CelOptions, FlatOptions}
import vendor.three.*

import scala.collection.mutable
import scala.math

/** 1:1 port of upstream `src/world/northblock.js`: ひばり台三丁目, one of every plot type on one
  * 3.2 m lane off the north end of the shopping street.
  *
  * The district's housing was twenty-five detached houses and one walk-up, and that is not what a
  * Japanese suburb looks like: it looks like five or six *types* interleaved, because the plots were
  * sold off at different times to different buyers. So this block is deliberately one of each -- a
  * corner shop with the family's flat over it, a coin parking, the lane itself, an attic house, a
  * three-storey walk-up, a terrace of three, and a single-storey house under one falling roof -- on
  * a lane that is the first street in the world that is *only* residential.
  */
object NorthBlock:

  private val LnX  = 32.4
  private val LnW  = 3.2
  private val LnZ0 = 46.4
  private val LnZ1 = 63.0

  /* Every plot here is set back off the lane far enough for its own boundary: the first pass had
   * the walk-up's gallery 0.1 m off the carriageway and the attic house's frontage 0.2 m off it, so
   * both garden walls stood *in* the lane. A 3.2 m lane needs its buildings 1.5 m back. */
  private val ParkX0 = 23.2
  private val ParkX1 = 30.4
  private val ParkZ0 = 47.3
  private val ParkZ1 = 52.0

  private val AtticX    = 39.2  // faces -x, so w runs in z
  private val AtticZ    = 51.4
  private val AtticW    = 7.2
  private val AtticD    = 6.6
  private val AtticGate = 34.05 // its 板塀, on the lane edge

  private val WalkX = 26.4 // faces +x
  private val WalkZ = 58.2
  private val WalkW = 8.0
  private val WalkD = 7.0

  private val TerrX     = 42.3
  private val TerrZ     = 60.4
  private val TerrUnits = 3
  private val TerrUnitW = 2.9
  private val TerrD     = 6.2

  /* 51.7 and not 50.6: `makeBlockGarden` puts this plot's boundary 1.9 m in front of the frontage,
   * and at z = 50.6 that landed the wall 0.9 m *inside* the north lane's east arm. */
  private val ShedX = 45.8
  private val ShedZ = 51.7
  private val ShedW = 5.6
  private val ShedD = 5.4

  private val ShopX = 4.5
  private val ShopZ = 51.9
  private val ShopW = 5.4
  private val ShopD = 5.0

  private val JpFont =
    "'Yu Gothic', 'Yu Gothic UI', 'Meiryo', 'MS Gothic', 'Hiragino Kaku Gothic ProN', sans-serif"

  private object M:
    val concrete    = Toon.cel(CelOptions(color = Pal.concrete, bands = 3, tint = 0x6f6790))
    val concreteMid = Toon.cel(CelOptions(color = Pal.concreteMid, bands = 3, tint = 0x6a6288))
    val metal       = Toon.cel(CelOptions(color = Pal.metal, bands = 3, tint = 0x666090))
    val metalDark   = Toon.cel(CelOptions(color = Pal.metalDark, bands = 3, tint = 0x5c5680))
    val drain       = Toon.cel(CelOptions(color = Pal.drain, bands = 3, tint = 0x5d5878))
    val wood        = Toon.cel(CelOptions(color = 0x9c7f5e, bands = 3, tint = 0x5c5680))

  /** ひばり駐車場's plate, transcribed from upstream `parkingSign`. `Textures.scala` is owned by the
    * traffic/matsuri lane at the moment, so the canvas this district draws lives here.
    */
  private lazy val parkingSign: CanvasTexture[dom.HTMLCanvasElement] =
    val canvas = dom.document.createElement("canvas").asInstanceOf[dom.HTMLCanvasElement]
    canvas.width = 384
    canvas.height = 512
    val c = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D | Null]
    if c == null then throw new IllegalStateException("sakura-crossing: the canvas has no 2D context")
    val w = 384.0
    val h = 512.0
    c.imageSmoothingEnabled = true
    def rule(x: Double, y: Double, w0: Double, h0: Double, color: String): Unit =
      c.fillStyle = color
      c.fillRect(x, y, w0, h0)
    c.fillStyle = "#fbfaf6"
    c.fillRect(0.0, 0.0, w, h)
    rule(0.0, 0.0, w, 96.0, "#3d6ec4")
    c.fillStyle = "#fdf8f0"
    c.textAlign = "center"
    c.font = s"bold 54px $JpFont"
    c.fillText("ひばり駐車場", w / 2.0, 48.0)
    c.fillStyle = "#b5322f"
    c.font = s"bold 128px $JpFont"
    c.fillText("２４", w / 2.0, 176.0)
    c.fillStyle = "#4b4757"
    c.font = s"bold 56px $JpFont"
    c.fillText("時間", w / 2.0, 262.0)
    c.globalAlpha = 0.4
    rule(40.0, 310.0, w - 80.0, 3.0, "#9a94a6")
    c.globalAlpha = 1.0
    val rows = Vector(("８時 - ２０時", "２０分 １００円"), ("２０時 - ８時", "６０分 １００円"))
    c.textBaseline = "middle"
    rows.zipWithIndex.foreach { case ((a, b), i) =>
      c.font = s"600 28px $JpFont"
      c.fillStyle = "#4b4757"
      c.textAlign = "left"
      c.fillText(a, 40.0, 356.0 + i * 52.0)
      c.textAlign = "right"
      c.fillText(b, w - 40.0, 356.0 + i * 52.0)
    }
    c.textAlign = "center"
    c.fillStyle = "#2f9c9a"
    c.font = s"600 30px $JpFont"
    c.fillText("前払い  ・  空車", w / 2.0, 470.0)
    val texture = new CanvasTexture(canvas)
    texture.colorSpace = SRGBColorSpace
    texture.anisotropy = 4.0
    ThreeInterop.markForUpload(texture)
    texture
  end parkingSign

  /** ひばり台三丁目: the lane, the plots, and the furniture that makes it lived in. */
  def buildNorthBlock(ctx: WorldCtx): Planting =
    val gm     = Ground.groundMats()
    val sakura = mutable.ArrayBuffer.empty[SakuraSpot]
    val shrubs = mutable.ArrayBuffer.empty[ShrubSpot]
    val grove  = mutable.ArrayBuffer.empty[GroveSpot]
    val petals = mutable.ArrayBuffer.empty[PetalPatch]

    val y = Street.groundY(52.0) // 0.45, and flat right across this block

    /* the lane and its surfaces: the north lane in `shotengai.js` runs out at x = 40, which was the
     * edge of the world when it was built. It gets an eastern arm here so the block has a road, and
     * the new lane hangs off that. */
    Ground.pad(ctx,
      Ground.PadOptions(
        x = 44.3, z = 45.2, w = 8.6, d = 3.4, y = Street.groundY(45.2), h = 0.09,
        mat = Some(gm.asphaltWorn), name = "northLaneEast"
      ))
    Ground.laneLine(ctx,
      Ground.LineOptions(
        axis = "x", at = 45.2, from = 40.2, to = 48.4, y = Street.groundY(45.2) + 0.11, dash = 1.1
      ))
    Ground.lane(ctx,
      Ground.LaneOptions(
        axis = "z", at = LnX, from = LnZ0, to = LnZ1, w = LnW,
        mat = Some(gm.asphaltWorn), kerb = false, rise = 0.05, name = "northBlockLane"
      ))
    /* the slotted channel down one side, and a manhole: what a lane this narrow has instead of
     * gutters */
    {
      val parts = mutable.ArrayBuffer.empty[Util.BakePart]
      val n     = math.round((LnZ1 - LnZ0) / 0.9).toInt
      (0 until n).foreach { i =>
        val z = LnZ0 + (i + 0.5) * ((LnZ1 - LnZ0) / n)
        parts += Util.BakePart(new BoxGeometry(0.3, 0.04, 0.7),
          Some(Util.trs(LnX - LnW / 2.0 + 0.24, Street.groundY(z) + 0.055, z)))
      }
      val ch = new Mesh(Util.bake(parts.toList), M.drain)
      ch.receiveShadow = true
      ctx.add(ch)
      Vector(49.6, 57.4).foreach { z =>
        val mh = Util.cyl(0.3, 0.3, 0.04, 12, Toon.cel(CelOptions(color = Pal.metalDark, bands = 3)),
          LnX + 0.5, Street.groundY(z) + 0.07, z)
        mh.receiveShadow = true
        ctx.add(mh)
      }
      // two patched squares in the asphalt, from when the water main was done
      Vector((LnX - 0.5, 53.4, 1.5, 1.9), (LnX + 0.6, 60.2, 1.2, 1.4)).foreach { (px, pz, pw, pd) =>
        val p = Util.box(pw, 0.02, pd, gm.asphalt, px, Street.groundY(pz) + 0.065, pz)
        p.receiveShadow = true
        ThreeInterop.setUserDataFlag(p, "noOutline")
        ctx.add(p)
      }
    }

    buildCornerShop(ctx)
    buildCoinPark(ctx)

    /* ------------------------------ 二階半の家 ------------------------------ */
    {
      val h = Housing.makeAtticHouse(Housing.AtticHouseOpts(
        x = AtticX, z = AtticZ, y = y, w = AtticW, d = AtticD, face = Buildings.Face.XMinus,
        seed = 7711, wall = 6, roof = 0, door = 2, nameVariant = 0, lit = true, litDormer = false
      ))
      ctx.add(h)
      // world extents: `w` runs along z once the group is turned a quarter circle
      val x0 = AtticX - AtticD / 2.0
      val x1 = AtticX + AtticD / 2.0
      val z0 = AtticZ - AtticW / 2.0
      val z1 = AtticZ + AtticW / 2.0
      ctx.collide(x0 - 0.1, z0 - 0.1, x1 + 0.1, z1 + 0.1, y + ThreeInterop.userDataNumber(h, "top"))

      /* its front garden, between the frontage and the lane: a 板塀, a gate gap, stepping stones,
       * the pot shelf and the tap */
      val gx = AtticGate
      makeTimberGarden(ctx, x0 = gx, x1 = x0, z0 = z0, z1 = z1, y = y, gapZ = AtticZ - 0.4)
      ctx.add(Props.makePotShelf(Props.PotShelfOpts(x = x0 - 0.7, z = z0 + 1.3, y = y, ry = -math.Pi / 2.0,
        w = 1.2, n = 6, seed = 7713)))
      ctx.add(Props.makeTapPost(Props.TapPostOpts(x = x0 - 0.55, z = z1 - 1.0, y = y, ry = -math.Pi / 2.0)))
      ctx.add(Props.makeBucket(Props.BucketOpts(x = x0 - 1.0, z = z1 - 1.3, y = y, ry = 0.6, water = true)))
      ctx.add(Props.makePostBox(Props.PostBoxOpts(x = gx + 0.35, z = AtticZ - 1.3, y = y, ry = -1.3)))
      ctx.add(Props.makeDoormat(Props.DoormatOpts(x = x0 - 0.5, z = AtticZ + 1.55, y = y,
        ry = math.Pi / 2.0, w = 0.56, d = 0.34)))
      /* Left against the outside of the 板塀, along it: the front garden is only 1.85 m from the
       * fence to the frontage and a bicycle is 1.73 m long, so turned across it -- which is what
       * ry = 0.3 did -- it went through the boards at one end and the render at the other. */
      ctx.add(Props.makeBicycle(Props.BicycleOpts(x = gx - 0.45, z = z1 - 1.2, y = y + 0.10,
        ry = math.Pi / 2.0 + 0.1, lean = 0.08, color = 0x4f8f6a)))
      ctx.add(Props.makeUmbrellaStand(Props.UmbrellaStandOpts(x = x0 - 0.55, z = AtticZ + 2.1, y = y, n = 3,
        seed = 7714)))
      shrubs += ShrubSpot(x = gx + 0.55, z = z1 - 1.4, y = y, r = 0.46, count = 3, spread = 1.1, seed = 7715)
      // in the front garden's north corner, clear of the stepping stones to the door
      sakura += SakuraSpot(x = gx + 0.95, z = z0 + 1.0, y = y, scale = 1.16, seed = 7716, lean = 0.1,
        leanDir = Some(3.6))
    }

    /* ------------------------------ ひばり台コーポ ------------------------------ */
    {
      val b = Housing.makeWalkup(Housing.WalkupOpts(
        x = WalkX, z = WalkZ, y = y, w = WalkW, d = WalkD, face = Buildings.Face.XPlus,
        floors = 3, units = 4, seed = 7721, wall = 4, roof = 0, door = 4, plate = 0
      ))
      ctx.add(b)
      val x0 = WalkX - WalkD / 2.0
      val x1 = WalkX + WalkD / 2.0
      val z0 = WalkZ - WalkW / 2.0
      val z1 = WalkZ + WalkW / 2.0
      ctx.collide(x0 - 0.3, z0 - 0.2, x1 + 0.2, z1 + 0.2, y + ThreeInterop.userDataNumber(b, "top"))
      // the open stair sticks out past the +z end once the block is turned
      ctx.collide(x0 - 0.2, z1 + 0.1, x1 + 0.2, z1 + 1.8, y + 1.2)

      /* everything a block of four flats keeps outside its own front door */
      ctx.add(Props.makeMailboxBank(Props.MailboxBankOpts(x = x1 + 0.55, z = z0 + 1.2, y = y,
        ry = -math.Pi / 2.0, cols = 4, rows = 2)))
      ctx.collide(x1 + 0.4, z0 + 0.7, x1 + 0.75, z0 + 1.7, y + 1.3)
      ctx.add(Housing.makeBikeShelter(Housing.BikeShelterOpts(x = x1 + 1.2, z = z0 + 4.6, y = y,
        ry = math.Pi / 2.0, w = 4.0, d = 1.8, h = 2.05)))
      /* Keep the first three bicycles in place: the fourth reached the utility pole at the end of
       * the shelter, so the shortened row is re-centred to preserve the three clear positions. */
      ctx.add(Props.makeBikeRack(Props.BikeRackOpts(x = x1 + 1.88, z = z0 + 4.6, y = y + 0.09, n = 3,
        spacing = 0.66, ry = math.Pi / 2.0, seed = 77)))
      /* `z1 - 1.0`, not `z1 - 0.5`: `makeBins` lays its three bins along local +x, so at
       * ry = -PI/2 the run climbs in z from the anchor, and one anchored at 61.7 straddled the rail
       * closing the lane's dead end. */
      ctx.add(Props.makeBins(Props.BinsOpts(x = x1 + 0.7, z = z1 - 1.0, y = y, ry = -math.Pi / 2.0)))
      ctx.add(signPost(x = x1 + 0.6, z = z1 - 1.6, y = y, ry = -math.Pi / 2.0, h = 1.5, postMat = M.metal,
        plates = Vector(SignPlate(map = Textures.gomiPlate(0), w = 0.44, h = 0.34, y = Some(1.25)))))
      ctx.add(Props.makeDeliveryBox(Props.DeliveryBoxOpts(x = x1 + 0.5, z = z0 + 2.2, y = y,
        ry = -math.Pi / 2.0)))
      /* Balcony-side outdoor units, on the -x flank: the grille has to face *away* from the block,
       * so `ry` is -PI/2. At +PI/2 they were pointing their fans into the render. */
      Vector(z0 + 1.4, z0 + 4.0).foreach { az =>
        ctx.add(Props.makeAircon(Props.AirconOpts(x = x0 - 0.25, z = az, y = y + 0.4,
          ry = -math.Pi / 2.0, w = 0.9, h = 0.62)))
      }
      ctx.add(Props.makePlanter(Props.PlanterOpts(x = x1 + 0.5, z = z0 + 0.3, y = y, r = 0.26,
        flower = true, seed = 7722, n = 5)))
      /* The cat box is off the lane, and turned along the wall to fit: at `x1 + 0.9` with ry = 0.7
       * its footprint straddled the lane's west edge *and* the slotted channel inside it, and
       * `lane({axis:'z'})` registers no platform, so its asphalt is drawn 0.05 m above what
       * `heightAt` answers. Turned a quarter circle its x extent is 0.53 m and it fits. */
      ctx.add(Props.makeCatBox(Props.CatBoxOpts(x = x1 + 0.45, z = z1 - 2.6, y = y,
        ry = math.Pi / 2.0 + 0.12)))
      ctx.add(Props.makeLoosePaper(propCtx(ctx),
        Vector(Props.PaperSpot(x = x1 + 1.1, z = z0 + 2.9, y = y + 0.01, ry = 0.5))))
      /* No boundary wall on the gallery side: one went in and it stood 0.1 m inside the lane, which
       * walled the lane off -- and a walk-up's ground floor being open to the street is right. */
    }

    /* -------------------------------- 連棟 三戸 -------------------------------- */
    {
      val t = Housing.makeTerrace(Housing.TerraceOpts(
        x = TerrX, z = TerrZ, y = y, units = TerrUnits, unitW = TerrUnitW, d = TerrD,
        face = Buildings.Face.XMinus, seed = 7731, wall = 7, roof = 1
      ))
      ctx.add(t)
      val w  = TerrUnits * TerrUnitW
      val x0 = TerrX - TerrD / 2.0
      val x1 = TerrX + TerrD / 2.0
      val z0 = TerrZ - w / 2.0
      val z1 = TerrZ + w / 2.0
      ctx.collide(x0 - 0.1, z0 - 0.1, x1 + 0.1, z1 + 0.1, y + ThreeInterop.userDataNumber(t, "top"))

      /* the bays: one apron, three marked stalls, three wheel stops, one carport. Not three
       * carports -- the whole read is that one household built a roof over theirs and two did not. */
      val a0 = LnX + LnW / 2.0
      Ground.pad(ctx,
        Ground.PadOptions(
          x = (a0 + x0) / 2.0, z = TerrZ, w = x0 - a0, d = w, y = y, h = 0.07,
          mat = Some(gm.asphaltWorn), name = "terraceApron"
        ))
      (0 to TerrUnits).foreach { i =>
        Ground.laneLine(ctx,
          Ground.LineOptions(
            axis = "z", at = z0 + i * TerrUnitW, from = a0 + 0.3, to = x0 - 0.2, y = y + 0.09
          ))
      }
      (0 until TerrUnits).foreach { i =>
        val bz = z0 + TerrUnitW * (i + 0.5)
        ctx.add(Util.box(0.16, 0.11, 1.3, M.concreteMid, x0 - 0.7, y + 0.12, bz))
      }
      ctx.add(Housing.makeCarport(Housing.CarportOpts(x = x0 - 2.6, z = z0 + TerrUnitW * 1.5, y = y,
        ry = math.Pi / 2.0, w = 2.7, d = 4.6, h = 2.3)))
      Vector(0, 2).foreach { s =>
        val bz    = z0 + TerrUnitW * (s + 0.5)
        val bikeX = x0 - 1.0
        val bikeZ = bz + (if s == 0 then 0.9 else -0.9)
        // the north unit's bike goes on the *other* side of its bay: the tin store stands at
        // z1 - 0.7 and the two were inside each other
        ctx.add(Props.makeBicycle(Props.BicycleOpts(x = bikeX, z = bikeZ, y = ctx.groundAt(bikeX, bikeZ) + 0.04,
          ry = 1.4, lean = 0.06, color = (if s == 0 then 0x9c5a4a else 0xd8a03c))))
      }
      ctx.add(Props.makePlanter(Props.PlanterOpts(x = x0 - 0.45, z = z0 + 0.35, y = y, r = 0.24,
        flower = true, seed = 7732, n = 5)))
      ctx.add(Props.makePlanter(Props.PlanterOpts(x = x0 - 0.45, z = z1 - 0.4, y = y, r = 0.2,
        flower = false, seed = 7733, n = 4)))
      ctx.add(Props.makeDoormat(Props.DoormatOpts(x = x0 - 0.55, z = z0 + 0.78, y = y, ry = math.Pi / 2.0)))
      ctx.add(Props.makeDoormat(Props.DoormatOpts(x = x0 - 0.55, z = z0 + 0.78 + TerrUnitW * 2, y = y,
        ry = math.Pi / 2.0, color = 0x6a6a58)))
      ctx.add(Props.makePostBox(Props.PostBoxOpts(x = a0 + 0.6, z = z0 + 1.0, y = y, ry = -1.4)))
      ctx.add(Props.makeBins(Props.BinsOpts(x = a0 + 0.7, z = z1 - 0.6, y = y, ry = -math.Pi / 2.0)))
      ctx.add(Props.makeStorageShed(Props.StorageShedOpts(x = x0 - 1.0, z = z1 - 0.7, y = y,
        ry = -math.Pi / 2.0, w = 1.2, d = 0.68, h = 1.55)))
      ctx.collide(x0 - 1.4, z1 - 1.1, x0 - 0.6, z1 - 0.3, y + 1.7)
      ctx.add(Props.makeUmbrellaStand(Props.UmbrellaStandOpts(x = x0 - 0.5, z = z0 + TerrUnitW + 0.9, y = y,
        n = 2, seed = 7734)))
      shrubs += ShrubSpot(x = a0 + 1.2, z = z1 + 0.8, y = y, r = 0.5, count = 3, spread = 1.2, seed = 7735)
    }

    /* ------------------------------ 片流れの平屋 ------------------------------ *
     * One storey under a single falling roof, which is the most modern thing on the block and
     * deliberately so: the lane has to look like it was built over fifty years, not in one go. */
    {
      val h = Buildings.house(Buildings.HouseOpts(
        x = ShedX, z = ShedZ, y = y, w = ShedW, d = ShedD, face = Buildings.Face.ZMinus, floors = 1,
        seed = 7741, wall = Some(5), roof = Some(3), roofKind = Some(Buildings.RoofKind.Shed),
        shedDir = Some(-1), porch = true, shutters = true
      ))
      ctx.add(h)
      ctx.collide(ShedX - ShedW / 2.0 - 0.1, ShedZ - ShedD / 2.0 - 0.1,
        ShedX + ShedW / 2.0 + 0.1, ShedZ + ShedD / 2.0 + 0.1, y + 2.72 + 1.4)
      val fz = ShedZ - ShedD / 2.0
      /* 1.7 m of front garden, not 1.9: `makeBlockFence` is 0.28 thick about the line it is given,
       * so at 1.9 the boundary reached z = 46.96 against a lane whose paving ends at 46.90. */
      makeBlockGarden(ctx, x0 = ShedX - ShedW / 2.0 - 0.4, x1 = ShedX + ShedW / 2.0 + 0.4,
        z0 = fz - 1.7, z1 = fz, y = y, gapX = ShedX + 1.2)
      ctx.add(Props.makePostBox(Props.PostBoxOpts(x = ShedX + 2.0, z = fz - 1.35, y = y, ry = 0.2)))
      ctx.add(Props.makeDeliveryBox(Props.DeliveryBoxOpts(x = ShedX + 2.6, z = fz - 1.5, y = y, ry = -0.3)))
      ctx.add(Props.makePlanter(Props.PlanterOpts(x = ShedX - 1.9, z = fz - 0.6, y = y, r = 0.26,
        flower = true, seed = 7743, n = 5)))
      ctx.add(Props.makePlanter(Props.PlanterOpts(x = ShedX - 1.3, z = fz - 0.7, y = y, r = 0.21,
        flower = false, seed = 7744, n = 4)))
      ctx.add(Props.makeLaundryPole(Props.LaundryPoleOpts(x = ShedX + 0.6, z = ShedZ + ShedD / 2.0 + 0.9,
        y = y, ry = 0.0, len = 2.4, n = 4, seed = 7745)))
      // west flank: outward normal is -x, so the grille turns -PI/2
      ctx.add(Props.makeAircon(Props.AirconOpts(x = ShedX - ShedW / 2.0 - 0.25, z = ShedZ + 0.6, y = y,
        ry = -math.Pi / 2.0, w = 0.8, h = 0.58)))
      /* No cherry east of this house: it stood at (50.5, fz - 1.2) and moving the house back 1.1 m
       * carried it nine tenths of a metre inside 六丁目's carriageway. The block already carries
       * eight cherries and a grove of four. */
    }

    /* --------------------------- the lane's own furniture --------------------------- */
    {
      // one lamp, on a pole, halfway down
      val px   = LnX - LnW / 2.0 - 0.35
      val pz   = 54.6
      val post = Util.cyl(0.055, 0.075, 4.6, 8, M.metalDark, px, y + 2.3, pz)
      post.castShadow = true
      ctx.add(post)
      Outline.hullOutline(post, OutlineOptions(thickness = 0.0032))
      ctx.add(Util.cyl(0.13, 0.16, 0.18, 8, M.concreteMid, px, y + 0.09, pz))
      ctx.add(Util.box(0.06, 0.06, 0.72, M.metalDark, px, y + 4.56, pz + 0.34))
      val shade = new Mesh(new ConeGeometry(0.28, 0.22, 12.0, 1.0, true), M.metal)
      shade.position.set(px, y + 4.46, pz + 0.66)
      ctx.add(shade)
      ctx.add(Util.box(0.22, 0.05, 0.22, Toon.flat(FlatOptions(color = 0xfff2d0)), px, y + 4.33, pz + 0.66))
      ctx.collide(px - 0.18, pz - 0.18, px + 0.18, pz + 0.18, y + 4.6)

      // a mirror where the lane meets the north lane, and the 徐行 plate
      ctx.add(signPost(x = LnX + LnW / 2.0 + 0.5, z = 47.4, y = y, ry = -0.4, h = 2.1, postMat = M.metal,
        plates = Vector(SignPlate(map = Textures.warningPlate(2), w = 0.44, h = 0.6, y = Some(1.7), double = true))))
      // three bollards at the mouth, which is what stops a lane being a road
      (0 until 3).foreach { i =>
        val bx = LnX - LnW / 2.0 + 0.5 + i * 1.0
        val b  = Util.cyl(0.055, 0.065, 0.78, 8, M.metal, bx, Street.groundY(46.9) + 0.39, 46.9)
        b.castShadow = true
        ctx.add(b)
        ctx.add(Util.cyl(0.06, 0.06, 0.12, 8, Toon.cel(CelOptions(color = Pal.red, bands = 3, tint = 0x7a4060)),
          bx, Street.groundY(46.9) + 0.7, 46.9))
      }
      // and the dead end at the far south, closed with a rail and a thicket
      Ground.railing(ctx,
        Ground.RailingOptions(
          axis = "x", at = LnZ1 - 0.2, from = LnX - LnW / 2.0 - 0.3, to = LnX + LnW / 2.0 + 0.3,
          y = Street.groundY(LnZ1) + 0.05, h = 0.92, spacing = 1.4, mat = Some(M.metal)
        ))
      (0 until 4).foreach { i =>
        grove += GroveSpot(
          x = LnX - 2.4 + i * 2.0, z = LnZ1 + 2.2 + (i % 2) * 1.6, y = Street.groundY(LnZ1 + 3.0),
          scale = 1.45 + (i                            % 3) * 0.18, seed = 7750 + i, spread = 1.1
        )
      }
    }

    /* ------------------------------ poles and cabling ------------------------------ *
     * Two poles down the lane's west side with a service drop into the walk-up. A residential lane
     * with no wires over it looks like a render, not a street. */
    {
      val px = LnX - LnW / 2.0 - 0.3
      val defs = Vector(
        (px, 48.6, 8.4), (px, 58.4, 8.2)
      )
      val tops = defs.map { (dx, dz, dh) =>
        val base = Street.groundY(dz)
        ctx.add(makePoleLite(x = dx, y = base, z = dz, h = dh, armDir = -1, lamp = false))
        ctx.collide(dx - 0.2, dz - 0.2, dx + 0.2, dz + 0.2, base + dh)
        new Vector3(dx, base + dh - 0.7, dz)
      }
      def offset(base: Vector3, dx: Double, dy: Double, dz: Double): Vector3 =
        new Vector3(base.x + dx, base.y + dy, base.z + dz)

      val wires = mutable.ArrayBuffer.empty[(Vector3, Vector3)]
      Vector(-0.5, 0.3).foreach { dz =>
        wires += ((offset(tops(0), 0.0, dz * 0.6, dz), offset(tops(1), 0.0, dz * 0.6, dz)))
      }
      wires += ((offset(tops(0), 0.0, -1.6, 0.0), new Vector3(23.6, Street.groundY(45.2) + 7.4, 45.6)))
      wires += ((offset(tops(1), 0.0, -1.5, 0.0), new Vector3(WalkX + WalkD / 2.0 - 0.3, y + 7.4, WalkZ - 2.4)))
      val geos = wires.toVector.map { (a, b) =>
        new TubeGeometry(Util.sagCurve(a, b, 0.5, 14), 16.0, 0.022, 4.0, false)
      }
      val mesh = new Mesh(Util.bake(geos.map(geometry => Util.BakePart(geometry)).toList),
        Toon.cel(CelOptions(color = 0x4c4658, bands = 2, tint = 0x413c58)))
      ctx.add(mesh)
      geos.foreach(_.dispose())
    }

    /* --------------------------------- planting --------------------------------- */
    sakura += SakuraSpot(x = LnX - LnW / 2.0 - 1.6, z = 51.6, y = y, scale = 1.2, seed = 7770,
      lean = 0.12, leanDir = Some(4.6))
    // south of the lane, not in it: a trunk in a 3.4 m carriageway blocks it
    sakura += SakuraSpot(x = 45.0, z = 42.6, y = Street.groundY(42.6), scale = 1.14, seed = 7771,
      lean = 0.1, leanDir = Some(2.0))
    grove += GroveSpot(x = 21.6, z = 62.2, y = Street.groundY(62.2), scale = 1.7, seed = 7772, spread = 1.15)
    grove += GroveSpot(x = 47.6, z = 58.6, y = Street.groundY(58.6), scale = 1.6, seed = 7773, spread = 1.1)
    petals += PetalPatch(x = LnX, z = 52.0, w = 2.8, d = 8.0, y = y + 0.06, n = 90)
    petals += PetalPatch(x = 44.0, z = 46.4, w = 7.0, d = 2.6, y = Street.groundY(46.0) + 0.06, n = 70)
    Planting(sakura = sakura.toVector, shrubs = shrubs.toVector, grove = grove.toVector, petals = petals.toVector)
  end buildNorthBlock

  /* ------------------------------------------------------------------ *
   * 米・酒 なかの -- the corner shop with the flat over it: the one mixed-use building in the world,
   * a shop on the ground floor and the family living above it. It reads as *domestic over
   * commercial* because the upper storey keeps its washing, its curtains and its aerial while the
   * ground floor keeps its shutter, its crates and its flag.
   * ------------------------------------------------------------------ */

  private def buildCornerShop(ctx: WorldCtx): Unit =
    val gm = Ground.groundMats()
    val y  = Street.groundY(ShopZ)

    Shops.makeShop(propCtx(ctx),
      Shops.ShopOpts(
        x = ShopX, z = ShopZ, y = y, w = ShopW, d = ShopD, face = Buildings.Face.XMinus,
        kind = Some(Textures.ShopKind.Kokuya), floors = 2, h1 = 3.1, h2 = 2.6,
        wall = Some(1), roof = Some(2), roofKind = Some(Buildings.RoofKind.Gable), roofH = Some(1.3),
        interior = Some(3), openW = Some(3.6), awning = Some(3), awningOut = Some(1.3),
        blade = Some(Textures.ShopKind.Kokuya), bladeSide = -1, shutter = Some(0.28),
        lit = true, balcony = true, seed = 7781
      ))

    val fx = ShopX - ShopD / 2.0 // the frontage, facing -x
    /* the apron out to the pavement, and the goods that live on it */
    Ground.pad(ctx,
      Ground.PadOptions(
        x = fx - 0.85, z = ShopZ, w = 1.7, d = ShopW + 0.6, y = y, h = 0.07,
        mat = Some(gm.concrete), name = "kokuyaApron"
      ))
    ctx.add(Shops.makeProduceStack(Shops.ProduceStackOpts(x = fx - 0.75, z = ShopZ - 1.7, y = y + 0.07,
      ry = -math.Pi / 2.0, seed = 7782)))
    ctx.add(Props.makeCrates(Props.CratesOpts(x = fx - 0.55, z = ShopZ + 1.9, y = y + 0.07, n = 3, seed = 7783,
      ry = -0.2)))
    ctx.add(Shops.makeShopFlag(Shops.ShopFlagOpts(x = fx - 0.7, z = ShopZ - 2.4, y = y + 0.07, variant = 1,
      ry = -math.Pi / 2.0)))
    Vending.addVending(ctx,
      Vending.VendingOptions(x = fx - 0.72, z = ShopZ + 0.9, y = y + 0.07,
        ry = -math.Pi / 2.0, variant = 1, seed = 77))
    ctx.add(Props.makeVendBin(Props.VendBinOpts(x = fx - 0.7, z = ShopZ + 0.05, y = y + 0.07,
      ry = -math.Pi / 2.0, color = Pal.vendTeal)))
    // The lean takes the tyre a little below the group's authored ground plane, so give this apron
    // bicycle enough clearance to keep both wheels visible.
    ctx.add(Props.makeBicycle(Props.BicycleOpts(x = fx - 1.3, z = ShopZ + 2.1, y = y + 0.10, ry = 1.5,
      lean = 0.06, color = 0x3f6f9c)))
    ctx.add(Props.makeBroom(Props.BroomOpts(x = fx - 0.28, z = ShopZ - 2.05, y = y + 0.07, tilt = -0.04,
      roll = -0.16, ry = -1.4)))
    // the blackboard by the door: the shop is open, somebody chalked it this morning
    {
      val b = new Mesh(new BoxGeometry(0.06, 0.66, 0.5),
        Props.matArray(
          Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.chalkNotice(0)), cache = false)),
          Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.chalkNotice(0)), cache = false)),
          M.wood, M.wood, M.wood, M.wood
        ))
      b.position.set(fx - 0.42, y + 0.66, ShopZ + 2.4)
      b.rotation.z = 0.06
      b.castShadow = true
      ctx.add(b)
    }
    // the family's side: an outside stair up to the flat, on the +z flank
    {
      val sz    = ShopZ + ShopW / 2.0 + 0.5
      val parts = mutable.ArrayBuffer.empty[Util.BakePart]
      (0 until 12).foreach { i =>
        parts += Util.BakePart(new BoxGeometry(1.1, 0.14, 0.28),
          Some(Util.trs(ShopX - 1.2 + i * 0.2, 0.28 + i * 0.27, sz)))
      }
      val st = new Mesh(Util.bake(parts.toList), M.concreteMid)
      st.position.y = y
      st.castShadow = true
      st.receiveShadow = true
      ctx.add(st)
      val rail = mutable.ArrayBuffer.empty[Util.BakePart]
      (0 to 5).foreach { i =>
        rail += Util.BakePart(new CylinderGeometry(0.028, 0.03, 0.92, 6.0),
          Some(Util.trs(ShopX - 1.2 + i * 0.44, 0.4 + i * 0.594 + 0.46, sz + 0.5)))
      }
      rail += Util.BakePart(new CylinderGeometry(0.03, 0.03, math.hypot(2.2, 3.0) + 0.2, 6.0),
        Some(Util.trs(ShopX - 0.1, 1.9 + 0.46, sz + 0.5, 0.0, 0.0, math.Pi / 2.0 - math.atan2(3.0, 2.2))))
      val rm = new Mesh(Util.bake(rail.toList), M.metal)
      rm.position.y = y
      rm.castShadow = true
      ctx.add(rm)
      ctx.add(Props.makeLaundryPole(Props.LaundryPoleOpts(x = ShopX + 0.4, z = sz + 1.0, y = y, ry = 0.0,
        len = 2.2, n = 3, seed = 7784)))
      ctx.add(Props.makePotShelf(Props.PotShelfOpts(x = ShopX + 1.6, z = sz + 0.3, y = y, ry = 0.0, w = 1.0,
        n = 5, seed = 7785)))
    }
    ctx.add(Props.makeAircon(Props.AirconOpts(x = ShopX + ShopD / 2.0 + 0.24, z = ShopZ - 1.4,
      y = y + 0.3, ry = math.Pi / 2.0, w = 0.86, h = 0.6)))
  end buildCornerShop

  /* ------------------------------------------------------------------ *
   * ひばり駐車場, moved up from the shopping street.
   * ------------------------------------------------------------------ */

  private def buildCoinPark(ctx: WorldCtx): Unit =
    val gm    = Ground.groundMats()
    val y     = Street.groundY((ParkZ0 + ParkZ1) / 2.0)
    val bays  = 3
    val w     = ParkX1 - ParkX0
    val pitch = (w - 0.4) / bays

    Ground.pad(ctx,
      Ground.PadOptions(
        x = (ParkX0 + ParkX1) / 2.0, z = (ParkZ0 + ParkZ1) / 2.0, w = w, d = ParkZ1 - ParkZ0,
        y = y, h = 0.06, mat = Some(gm.gravel), name = "coinPark"
      ))
    (0 to bays).foreach { i =>
      Ground.laneLine(ctx,
        Ground.LineOptions(
          axis = "z", at = ParkX0 + 0.2 + i * pitch, from = ParkZ0 + 0.3, to = ParkZ1 - 0.4, y = y + 0.08
        ))
    }
    (0 until bays).foreach { i =>
      val bx = ParkX0 + 0.2 + (i + 0.5) * pitch
      // wheel stop at the far end of each bay, and the coin plate that rises by it
      ctx.add(Util.box(1.4, 0.11, 0.16, M.concreteMid, bx, y + 0.12, ParkZ1 - 0.9))
      val plate = Util.box(0.62, 0.16, 0.5, Toon.cel(CelOptions(color = 0xc8ccd4, bands = 3, tint = 0x6a6288)),
        bx, y + 0.14, ParkZ1 - 1.7)
      plate.rotation.x = 0.5
      plate.castShadow = true
      ctx.add(plate)
      ctx.add(Util.box(0.5, 0.1, 0.3, M.metalDark, bx, y + 0.06, ParkZ1 - 2.1))
    }
    // the payment machine and its sign, at the mouth off the lane
    {
      val mx = ParkX1 - 0.7
      val mz = ParkZ0 + 0.7
      val mach = Util.box(0.44, 1.4, 0.5, Toon.cel(CelOptions(color = 0xe4e2e6, bands = 3, tint = 0x6f6790)),
        mx, y + 0.7, mz)
      mach.castShadow = true
      mach.receiveShadow = true
      ctx.add(mach)
      Outline.hullOutline(mach, OutlineOptions(thickness = 0.0032))
      ctx.add(Util.box(0.03, 0.4, 0.34, Toon.flat(FlatOptions(color = 0x3a3744)), mx - 0.24, y + 1.0, mz))
      ctx.collide(mx - 0.3, mz - 0.3, mx + 0.3, mz + 0.3, y + 1.4)
      ctx.add(signPost(x = ParkX1 - 0.5, z = ParkZ0 + 2.0, y = y, ry = -math.Pi / 2.0, h = 2.7,
        postMat = M.metal,
        plates = Vector(SignPlate(map = parkingSign, w = 0.62, h = 0.82, y = Some(2.1), double = true))))
    }
    // the low chain fence along the back, and the block wall on the west boundary
    (0 to bays).foreach { i =>
      ctx.add(Util.cyl(0.05, 0.055, 0.6, 7, M.metalDark, ParkX0 + 0.2 + i * pitch, y + 0.3, ParkZ1 - 0.35))
    }
    Ground.wallRun(ctx,
      Ground.WallOptions(
        axis = "z", at = ParkX0 - 0.2, from = ParkZ0, to = ParkZ1, y = y, h = 1.35, t = 0.2, panel = 2.3,
        mat = Some(M.concreteMid)
      ))
    ctx.add(Props.makeIvy(Props.IvyOpts(x = ParkX0 - 0.2, z = (ParkZ0 + ParkZ1) / 2.0, y = y,
      ry = math.Pi / 2.0, len = 4.0, top = 1.44, drop = 0.8, seed = 7791)))
  end buildCoinPark

  /* ------------------------------------------------------------------ *
   * Two garden boundaries.
   * ------------------------------------------------------------------ */

  /** 板塀 round a front garden, with a gap where the gate is. */
  private def makeTimberGarden(
    ctx: WorldCtx,
    x0: Double,
    x1: Double,
    z0: Double,
    z1: Double,
    y: Double,
    gapZ: Double
  ): Unit =
    val runs = Vector(
      ("z", x0, z0, gapZ - 0.6), ("z", x0, gapZ + 0.6, z1), ("x", z0, x0, x1), ("x", z1, x0, x1)
    )
    runs.foreach { (axis, at, from, to) =>
      if math.abs(to - from) >= 0.4 then
        val g = Buildings.timberFence(Buildings.FenceOpts(
          x = (if axis == "z" then at else (from + to) / 2.0),
          z = (if axis == "z" then (from + to) / 2.0 else at),
          len = math.abs(to - from), axis = (if axis == "z" then Buildings.Axis.Z else Buildings.Axis.X),
          h = 0.36, fenceH = 0.86, y = y
        ))
        ctx.add(g)
        val top = ThreeInterop.userDataNumber(g, "top")
        if axis == "z" then ctx.collide(at - 0.14, from, at + 0.14, to, y + top)
        else ctx.collide(from, at - 0.14, to, at + 0.14, y + top)
    }
    // the two gate posts, and the stepping stones from the gap to the door
    Vector(-1.0, 1.0).foreach { s =>
      val p = Util.box(0.14, 1.4, 0.14, Toon.cel(CelOptions(color = 0x74563f, bands = 3, tint = 0x554e74)),
        x0, y + 0.7, gapZ + s * 0.6)
      p.castShadow = true
      ctx.add(p)
    }
    val stones = mutable.ArrayBuffer.empty[Util.BakePart]
    (0 until 4).foreach { i =>
      val r = 0.22 + (i % 2) * 0.03
      stones += Util.BakePart(new CylinderGeometry(r, r, 0.08, 7.0),
        Some(Util.trs(x0 + 0.5 + i * 0.42, 0.04, gapZ + (if i % 2 == 0 then -0.1 else 0.28) + i * 0.22)))
    }
    val sm = new Mesh(Util.bake(stones.toList), Toon.cel(CelOptions(color = Pal.stone, bands = 3, tint = 0x655d80)))
    sm.position.y = y
    sm.receiveShadow = true
    ctx.add(sm)
  end makeTimberGarden

  /** ブロック塀 with a 透かし course, round a front garden, gapped for the gate. */
  private def makeBlockGarden(
    ctx: WorldCtx,
    x0: Double,
    x1: Double,
    z0: Double,
    z1: Double,
    y: Double,
    gapX: Double
  ): Unit =
    val runs = Vector(
      ("x", z0, x0, gapX - 0.6), ("x", z0, gapX + 0.6, x1), ("z", x0, z0, z1), ("z", x1, z0, z1)
    )
    runs.foreach { (axis, at, from, to) =>
      if math.abs(to - from) >= 0.4 then
        val g = Buildings.blockFence(Buildings.FenceOpts(
          x = (if axis == "z" then at else (from + to) / 2.0),
          z = (if axis == "z" then (from + to) / 2.0 else at),
          len = math.abs(to - from), axis = (if axis == "z" then Buildings.Axis.Z else Buildings.Axis.X),
          h = 0.62, y = y
        ))
        ctx.add(g)
        val top = ThreeInterop.userDataNumber(g, "top")
        if axis == "z" then ctx.collide(at - 0.14, from, at + 0.14, to, y + top)
        else ctx.collide(from, at - 0.14, to, at + 0.14, y + top)
    }
  end makeBlockGarden

  /** Upstream `makePoleLite` (plots.js): the reduced utility pole the residential blocks reach for.
    * It is inlined here because `Plots.scala` does not carry it yet.
    */
  private def makePoleLite(
    x: Double,
    y: Double,
    z: Double,
    h: Double,
    armDir: Int,
    lamp: Boolean
  ): Group[Object3DEventMap] =
    val g = new Group()
    val parts = Vector("pole", "dark", "white", "metal")
      .map(kind => (kind, mutable.ArrayBuffer.empty[Util.BakePart]))
      .toMap
    def push(kind: String, geometry: BufferGeometry[?, ?], matrix: Matrix4): Unit =
      parts(kind) += Util.BakePart(geometry, Some(matrix))

    push("pole", new CylinderGeometry(0.1, 0.17, h, 8.0), Util.trs(0.0, h / 2.0, 0.0))
    push("pole", new CylinderGeometry(0.22, 0.26, 0.2, 8.0), Util.trs(0.0, 0.1, 0.0))
    val dir = armDir.toDouble
    Vector(h - 0.6, h - 1.5).zipWithIndex.foreach { (armY, ai) =>
      val len = if ai == 0 then 1.9 else 1.5
      push("dark", new BoxGeometry(0.08, 0.09, len), Util.trs(0.0, armY, 0.0))
      push("metal", new BoxGeometry(0.05, 0.44, 0.05), Util.trs(0.0, armY - 0.27, 0.0))
      Vector(-1.0, 0.0, 1.0).foreach { i =>
        if !(i == 0.0 && ai == 1) then
          push("white", new CylinderGeometry(0.055, 0.07, 0.15, 7.0), Util.trs(0.0, armY + 0.12, (i * len) / 2.4))
      }
    }
    push("dark", new CylinderGeometry(0.04, 0.04, h - 1.6, 5.0), Util.trs(dir * 0.125, (h - 1.6) / 2.0, 0.055))
    if lamp then
      // the one-arm street lamp a lane gets, on the side the arm points
      push("metal", new BoxGeometry(0.06, 0.06, 0.8), Util.trs(0.0, h - 2.6, dir * 0.4))
      push("metal", new CylinderGeometry(0.055, 0.055, 0.5, 8.0), Util.trs(0.0, h - 2.6, dir * 0.78))
      val shade = new Mesh(new ConeGeometry(0.26, 0.2, 12.0, 1.0, true), M.metal)
      shade.position.set(0.0, h - 2.72, dir * 0.78)
      ThreeInterop.addChild(g, shade)
      val lens = Util.box(0.2, 0.05, 0.2, Toon.flat(FlatOptions(color = 0xfff2d0)), 0.0, h - 2.84, dir * 0.78)
      ThreeInterop.setUserDataFlag(lens, "noOutline")
      ThreeInterop.addChild(g, lens)
    val matFor = Map(
      "pole"  -> Toon.cel(CelOptions(color = 0xd6d2d8, bands = 3, tint = 0x6a6288)),
      "dark"  -> Toon.cel(CelOptions(color = Pal.black, bands = 2, tint = 0x4b4560)),
      "white" -> Toon.cel(CelOptions(color = Pal.wallWhite, bands = 3, tint = 0x6f6790)),
      "metal" -> M.metal
    )
    parts.foreach { (kind, baked) =>
      if baked.nonEmpty then
        val mesh = new Mesh(Util.bake(baked.toList), matFor(kind))
        mesh.castShadow = true
        ThreeInterop.addChild(g, mesh)
        if kind == "pole" then Outline.hullOutline(mesh, OutlineOptions(thickness = 0.0034))
    }
    g.position.set(x, y, z)
    g
  end makePoleLite

  private def propCtx(ctx: WorldCtx): PropCtx =
    PropCtx(
      add = obj => ctx.add(obj),
      collide = (x0, z0, x1, z1, top) => ctx.collide(x0, z0, x1, z1, top),
      groundAt = (px, pz) => ctx.groundAt(px, pz)
    )

  /** A plate on a sign post: the same map both ways unless `double` asks for a printed back. */
  final private case class SignPlate(
    map: Texture[?, ?],
    w: Double = 0.5,
    h: Double = 0.5,
    y: Option[Double] = None,
    ry: Double = 0.0,
    double: Boolean = false,
    back: Option[Int] = None
  )

  /** The lane's sign posts carry plates, which `Props.makeSignPost` does not, so this is upstream
    * `props.js`'s plated post transcribed here -- the same shape `Shrine.scala` uses.
    */
  private def signPost(
    x: Double,
    z: Double,
    y: Double,
    ry: Double,
    h: Double,
    postMat: Material[?],
    plates: Vector[SignPlate]
  ): Group[Object3DEventMap] =
    val g    = new Group()
    val post = Util.cyl(0.045, 0.05, h, 8, postMat, 0.0, h / 2.0, 0.0)
    post.castShadow = true
    ThreeInterop.addChild(g, post)
    ThreeInterop.addChild(g, Util.cyl(0.1, 0.12, 0.14, 8, Ground.groundMats().concreteMid, 0.0, 0.07, 0.0))
    plates.foreach { plate =>
      val side  = Toon.flat(FlatOptions(color = plate.back.getOrElse(Pal.wallGray)))
      val face  = Toon.flat(FlatOptions(color = 0xffffff, map = Some(plate.map), cache = false))
      val back  = if plate.double then face else side
      val t     = if plate.double then 0.12 else 0.05
      val board = new Mesh(new BoxGeometry(plate.w, plate.h, t), Props.matArray(side, side, side, side, face, back))
      board.position.set(0.0, plate.y.getOrElse(h - 0.4), if plate.double then 0.0 else 0.058)
      board.rotation.y = plate.ry
      board.castShadow = true
      ThreeInterop.addChild(g, board)
      Outline.hullOutline(board, OutlineOptions(thickness = 0.0028))
    }
    g.position.set(x, y, z)
    g.rotation.y = ry
    g
  end signPost
end NorthBlock
