package sakura.world

import Ground.{PadOptions, RailingOptions}
import sakura.ThreeInterop
import sakura.core.{Outline, Pal, Textures, Util}
import sakura.core.Outline.OutlineOptions
import sakura.core.Toon
import sakura.core.Toon.{CelOptions, FlatOptions}
import vendor.three.*

import scala.collection.mutable

/** 1:1 port of upstream `src/world/district.js`: the neighbourhood sweep -- ひばり台第二児童公園,
  * the three-storey walk-up ひばり荘, the traditional timber house below the shrine, and the pass
  * over every existing house frontage.
  */
object District:

  /** The planting a district module hands back for the shared tree, shrub and petal passes to
    * merge, exactly as upstream `index.js` collects `d.sakura ?? []` and friends.
    */
  /** One `houseDefs` entry from upstream `index.js`: the footprint the sweep dresses plus the
    * per-plot slots that nudge its clutter off a neighbour's.
    */
  /** The interface `props.js` reads for the few builders that add their own mesh themselves --
    * `makeLoosePaper`, `makeWires`, the tyre and chalk marks -- expressed over the shared world
    * context.  The world context's `collide` takes an optional bottom; the props' does not.
    */
  def propCtx(ctx: WorldCtx): PropCtx =
    PropCtx(
      add = obj => ctx.add(obj),
      collide = (x0, z0, x1, z1, top) => ctx.collide(x0, z0, x1, z1, top),
      groundAt = (x, z) => ctx.groundAt(x, z)
    )

  /** One plate on a sign post, in the shape upstream `props.js` reads it. */
  final case class SignPlate(
    map: Texture[?, ?],
    w: Double = 0.5,
    h: Double = 0.5,
    y: Option[Double] = None,
    ry: Double = 0.0,
    double: Boolean = false,
    back: Option[Int] = None
  )

  /** One sheet pinned to a notice board, in the shape upstream `props.js` reads it. */
  final case class NoticeSheet(
    map: Texture[?, ?],
    x: Double,
    y: Double = 0.0,
    w: Double = 0.42,
    h: Double = 0.58,
    tilt: Double = 0.0
  )

  /** `Props.makeSignPost` carries the post and its base; the plates are composed here because the
    * landed port of `props.js` stops at the bare post.  Every call site in these neighbourhood
    * modules passes `postMat: m.metal`, which is the material `Props.makeSignPost` already cuts the
    * post from, so the post material is not a parameter.
    *
    * The plate's thickness and offset are load-bearing: a two-sided plate is 0.12 thick and centred
    * on the post, a single-sided one is 0.05 and sits in front of it, because at 0.04 sitting 0.03
    * forward the post came through the printed face.
    */
  def signPostWithPlates(
    x: Double,
    y: Double,
    z: Double,
    ry: Double,
    h: Double,
    plates: Vector[SignPlate]
  ): Group[Object3DEventMap] =
    val g = Props.makeSignPost(Props.SignPostOpts(x = x, y = y, z = z, ry = ry, h = h))
    plates.foreach { plate =>
      val side = Toon.flat(FlatOptions(color = plate.back.getOrElse(Pal.wallGray)))
      val face = Toon.flat(FlatOptions(color = 0xffffff, map = Some(plate.map), cache = false))
      val back = if plate.double then face else side
      val board = new Mesh(
        new BoxGeometry(plate.w, plate.h, if plate.double then 0.12 else 0.05),
        Props.matArray(side, side, side, side, face, back)
      )
      board.position.set(0.0, plate.y.getOrElse(h - 0.4), if plate.double then 0.0 else 0.058)
      board.rotation.y = plate.ry
      board.castShadow = true
      ThreeInterop.addChild(g, board)
      Outline.hullOutline(board, OutlineOptions(thickness = 0.0028))
    }
    g
  end signPostWithPlates

  /** The sheets and their drawing pins on a notice board, composed here for the same reason as the
    * sign plates: `Props.makeNoticeBoard` builds the board, the frame and the rain hood, and is
    * where upstream pins the notices on afterwards.
    */
  def noticeBoardWithSheets(
    x: Double,
    y: Double,
    z: Double,
    ry: Double,
    w: Double,
    h: Double,
    y0: Double,
    sheets: Vector[NoticeSheet]
  ): Group[Object3DEventMap] =
    val g = Props.makeNoticeBoard(Props.NoticeBoardOpts(x = x, y = y, z = z, ry = ry, w = w, h = h, y0 = y0))
    sheets.zipWithIndex.foreach { (sheet, index) =>
      val panel = new Mesh(
        new PlaneGeometry(sheet.w, sheet.h),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(sheet.map), cache = false))
      )
      panel.position.set(sheet.x, y0 + h / 2.0 + sheet.y, 0.052)
      panel.rotation.z = sheet.tilt
      ThreeInterop.setUserDataFlag(panel, "noOutline")
      ThreeInterop.addChild(g, panel)
      ThreeInterop.addChild(g,
        Util.box(0.03, 0.03, 0.02,
          Toon.flat(FlatOptions(color = if index % 2 != 0 then Pal.red else Pal.yellow)),
          sheet.x, y0 + h / 2.0 + sheet.y + sheet.h / 2.0 - 0.04, 0.062))
    }
    g
  end noticeBoardWithSheets

  private object M:
    lazy val wall        = Toon.cel(CelOptions(color = Pal.wallWhite, bands = 3, tint = 0x6f6790))
    lazy val wallAlt     = Toon.cel(CelOptions(color = Pal.wallGray, bands = 3, tint = 0x6f6790))
    lazy val wallCream   = Toon.cel(CelOptions(color = Pal.wallCream, bands = 3, tint = 0x6f6790))
    lazy val roof        = Toon.cel(CelOptions(color = Pal.roofSlate, bands = 3, tint = 0x514b70))
    lazy val roofBrown   = Toon.cel(CelOptions(color = Pal.roofBrown, bands = 3, tint = 0x514b70))
    lazy val trim        = Toon.cel(CelOptions(color = Pal.trim, bands = 3, tint = 0x5c5680))
    lazy val metal       = Toon.cel(CelOptions(color = Pal.metal, bands = 3, tint = 0x666090))
    lazy val metalDark   = Toon.cel(CelOptions(color = Pal.metalDark, bands = 3, tint = 0x5c5680))
    lazy val concrete    = Toon.cel(CelOptions(color = Pal.concrete, bands = 3, tint = 0x6f6790))
    lazy val concreteMid = Toon.cel(CelOptions(color = Pal.concreteMid, bands = 3, tint = 0x6a6288))
    lazy val glass       = Toon.flat(FlatOptions(color = Pal.glassDark))
    lazy val wood        = Toon.cel(CelOptions(color = 0x9c7f5e, bands = 3, tint = 0x5c5680))
    lazy val woodDark    = Toon.cel(CelOptions(color = 0x74563f, bands = 3, tint = 0x554e74))
    lazy val tileRoof    = Toon.cel(CelOptions(color = 0x5a5f6e, bands = 3, tint = 0x4a4468))
    lazy val sand        = Toon.cel(CelOptions(color = Pal.sand, bands = 3, tint = 0x7d74a0))
    lazy val dirt        = Toon.cel(CelOptions(color = Pal.dirt, bands = 3, tint = 0x7a7396))
    lazy val paint = Vector(
      Toon.cel(CelOptions(color = Pal.red, bands = 3, tint = 0x7a4060)),
      Toon.cel(CelOptions(color = Pal.blue, bands = 3, tint = 0x4a4a92)),
      Toon.cel(CelOptions(color = Pal.yellow, bands = 3, tint = 0x8f7050)),
      Toon.cel(CelOptions(color = Pal.teal, bands = 3, tint = 0x4a6a80))
    )
  end M

  def buildDistrict(ctx: WorldCtx, houses: Vector[HouseDef]): Planting =
    val sakura = mutable.ArrayBuffer.empty[SakuraSpot]
    val shrubs = mutable.ArrayBuffer.empty[ShrubSpot]
    val grove  = mutable.ArrayBuffer.empty[GroveSpot]
    val petals = mutable.ArrayBuffer.empty[PetalPatch]

    buildChildrensPark(ctx, sakura, shrubs, petals)
    buildApartment(ctx)
    buildTimberHouse(ctx, shrubs)
    dressHousing(ctx, houses)

    grove += GroveSpot(x = 44.4, z = 24.0, y = Street.groundY(24), scale = 1.7, seed = 1201, spread = 1.15)
    grove += GroveSpot(x = 45.6, z = 33.6, y = Street.groundY(33.6), scale = 1.85, seed = 1202, spread = 1.2)
    /* Slid south-east off the north lane, which now carries on east to the new block: a grove tree
     * at scale 1.75 collides with a 1.42 m box, so this one stood squarely across a 3.4 m lane.
     */
    grove += GroveSpot(x = 45.8, z = 41.0, y = Street.groundY(41), scale = 1.75, seed = 1203, spread = 1.15)
    grove += GroveSpot(x = -15.4, z = 21.0, y = Street.groundY(21), scale = 1.55, seed = 1204, spread = 1.1)

    Planting(sakura = sakura.toVector, shrubs = shrubs.toVector, grove = grove.toVector,
      petals = petals.toVector)
  end buildDistrict

  /** ひばり台第二児童公園: an empty playground at four in the afternoon does more for the mood of a
    * place than any amount of architecture.
    */
  private def buildChildrensPark(
    ctx: WorldCtx,
    sakura: mutable.ArrayBuffer[SakuraSpot],
    shrubs: mutable.ArrayBuffer[ShrubSpot],
    petals: mutable.ArrayBuffer[PetalPatch]
  ): Unit =
    val gm    = Ground.groundMats()
    val rng   = Util.rngKit(6101)
    val x0    = 31.8
    val x1    = 43.6
    val z0    = 17.4
    val z1    = 30.6
    val cx    = (x0 + x1) / 2.0
    val cz    = (z0 + z1) / 2.0
    val parkY = Street.groundY(cz)

    // the surface: rolled dirt, with a paved apron at the gate
    Ground.pad(ctx,
      PadOptions(x = cx, z = cz, w = x1 - x0, d = z1 - z0, y = parkY, h = 0.06,
        mat = Some(M.dirt), name = "parkGround"))
    Ground.pad(ctx,
      PadOptions(x = x0 + 2.2, z = 28.8, w = 4.4, d = 2.4, y = parkY, h = 0.08,
        mat = Some(gm.concrete), name = "parkApron"))

    /* A low pipe fence all the way round, with the entrance on the west and a second one on the
     * east: the park used to back onto a field, and there is a street down that side now.
     */
    Vector(
      ("x", z0, x0, x1), ("x", z1, x0, x1), ("z", x1, z0, 23.4), ("z", x1, 25.0, z1), ("z", x0, z0, 27.5),
      ("z", x0, 30.1, z1)
    ).foreach { (axis, at, from, to) =>
      Ground.railing(ctx,
        RailingOptions(axis = axis, at = at, from = from, to = to,
          y = parkY + 0.06, h = 0.92, spacing = 2.1, mat = Some(M.metal)))
    }
    ctx.add(signPostWithPlates(x = x0 - 0.5, z = 30.6, y = parkY, ry = -math.Pi / 2.0, h = 2.1,
      plates = Vector(SignPlate(map = Textures.parkSign(), w = 0.92, h = 0.7, y = Some(1.6)))))

    /* the swings: two seats hanging dead still.  The chains are the whole detail -- without them a
     * swing frame is just an A of pipes.
     */
    val swingX     = x0 + 3.4
    val swingZ     = z0 + 3.2
    val swingH     = 2.35
    val span       = 3.2
    val legLength  = swingH + 0.2
    val legAngle   = 0.28
    val legOffset  = math.sin(legAngle) * legLength / 2.0
    val swingParts = mutable.ArrayBuffer.empty[Util.BakePart]
    Vector(-1.0, 1.0).foreach { s =>
      Vector(-1.0, 1.0).foreach { t =>
        /* CylinderGeometry grows along local Y: offset the centre by the projected half-length and
         * lean it back in, so both upper ends meet at z = swingZ while the feet spread naturally.
         */
        swingParts += Util.BakePart(new CylinderGeometry(0.055, 0.06, legLength, 8),
          Some(Util.trs(swingX + s * (span / 2.0), legLength / 2.0 - 0.1,
            swingZ + t * legOffset, -t * legAngle, 0.0, 0.0)))
      }
    }
    swingParts += Util.BakePart(new CylinderGeometry(0.06, 0.06, span + 0.4, 8),
      Some(Util.trs(swingX, swingH, swingZ, 0.0, 0.0, math.Pi / 2.0)))
    val swingFrame = new Mesh(Util.bake(swingParts.toList), M.paint(1))
    swingFrame.position.y = parkY + 0.06
    swingFrame.castShadow = true
    ctx.add(swingFrame)
    Outline.hullOutline(swingFrame, OutlineOptions(thickness = 0.0032))
    Vector(-1.0, 1.0).foreach { s =>
      val seatX = swingX + s * 0.72
      Vector(-0.2, 0.2).foreach { c =>
        ctx.add(Util.cyl(0.014, 0.014, 1.55, 4, M.metalDark, seatX + c,
          parkY + 0.06 + swingH - 0.78, swingZ))
      }
      val seat = Util.box(0.5, 0.055, 0.2, M.paint(2), seatX, parkY + 0.06 + swingH - 1.56, swingZ)
      seat.castShadow = true
      ctx.add(seat)
    }
    Vector(-1.0, 1.0).foreach { s =>
      ctx.collide(swingX + s * (span / 2.0) - 0.14, swingZ - 0.9, swingX + s * (span / 2.0) + 0.14,
        swingZ + 0.9, parkY + swingH)
    }

    /* the slide */
    val slideX     = x1 - 3.6
    val slideZ     = z0 + 3.6
    val platformH  = 1.6
    val railLength = 1.5
    val railAngle  = 0.7
    val railHalf   = railLength / 2.0
    val slideParts = mutable.ArrayBuffer.empty[Util.BakePart]
    Vector(-1.0, 1.0).foreach { sx =>
      Vector(-1.0, 1.0).foreach { sz =>
        slideParts += Util.BakePart(new CylinderGeometry(0.05, 0.055, platformH, 7),
          Some(Util.trs(slideX + sx * 0.52, platformH / 2.0, slideZ + sz * 0.52)))
      }
    }
    // ladder
    (0 until 5).foreach { i =>
      slideParts += Util.BakePart(new BoxGeometry(1.0, 0.05, 0.09),
        Some(Util.trs(slideX, 0.28 + i * 0.3, slideZ + 0.52)))
    }
    Vector(-1.0, 1.0).foreach { sx =>
      slideParts += Util.BakePart(new CylinderGeometry(0.04, 0.04, railLength, 6),
        Some(Util.trs(slideX + sx * 0.52, platformH + 0.5, slideZ + 0.52)))
      /* Join the rear upright exactly, then bring the lower end down to the deck and the head of
       * the chute instead of leaving both ends suspended in air.
       */
      slideParts += Util.BakePart(new CylinderGeometry(0.04, 0.04, railLength, 6),
        Some(Util.trs(slideX + sx * 0.52, platformH + math.cos(railAngle) * railHalf,
          slideZ + 0.52 - math.sin(railAngle) * railHalf, railAngle, 0.0, 0.0)))
    }
    val slideFrame = new Mesh(Util.bake(slideParts.toList), M.paint(3))
    slideFrame.position.y = parkY + 0.06
    slideFrame.castShadow = true
    ctx.add(slideFrame)
    Outline.hullOutline(slideFrame, OutlineOptions(thickness = 0.0032))

    val deck = Util.box(1.15, 0.08, 1.15, M.paint(2), slideX, parkY + 0.06 + platformH, slideZ)
    deck.castShadow = true
    deck.receiveShadow = true
    ctx.add(deck)
    // the chute, with a raised lip either side
    val chuteLen = 3.1
    val chute = Util.box(0.86, 0.07, chuteLen, M.paint(0), slideX,
      parkY + 0.06 + platformH / 2.0, slideZ - 0.5 - chuteLen / 2.0 * 0.9)
    chute.rotation.x = -0.52
    chute.castShadow = true
    chute.receiveShadow = true
    ctx.add(chute)
    Outline.hullOutline(chute, OutlineOptions(thickness = 0.0032))
    Vector(-1.0, 1.0).foreach { sx =>
      val lip = Util.box(0.08, 0.16, chuteLen, M.paint(2), slideX + sx * 0.45,
        parkY + 0.06 + platformH / 2.0 + 0.06, slideZ - 0.5 - chuteLen / 2.0 * 0.9)
      lip.rotation.x = -0.52
      ctx.add(lip)
    }
    ctx.collide(slideX - 0.7, slideZ - 0.7, slideX + 0.7, slideZ + 0.7, parkY + platformH)

    /* the sandpit */
    val pitX = x0 + 3.6
    val pitZ = z1 - 3.4
    val pitW = 3.4
    val pitD = 2.8
    val sand = Util.box(pitW, 0.14, pitD, M.sand, pitX, parkY + 0.1, pitZ)
    sand.receiveShadow = true
    ctx.add(sand)
    Vector(-1.0, 1.0).foreach { s =>
      ctx.add(Util.box(pitW + 0.3, 0.26, 0.15, M.wood, pitX, parkY + 0.13, pitZ + s * (pitD / 2.0 + 0.07)))
      ctx.add(Util.box(0.15, 0.26, pitD + 0.3, M.wood, pitX + s * (pitW / 2.0 + 0.07), parkY + 0.13, pitZ))
    }
    // a bucket and a small mound left behind
    ctx.add(Props.makeBucket(Props.BucketOpts(x = pitX + 0.7, z = pitZ - 0.5, y = parkY + 0.17,
      ry = 0.4, color = Pal.yellow)))
    val mound = new Mesh(new ConeGeometry(0.28, 0.2, 8),
      Toon.cel(CelOptions(color = 0xcfbc97, bands = 3, tint = 0x7d74a0)))
    mound.position.set(pitX - 0.5, parkY + 0.24, pitZ + 0.4)
    mound.castShadow = true
    ctx.add(mound)

    /* animal spring riders */
    Vector((x1 - 3.2, z1 - 3.0, 0), (x1 - 4.9, z1 - 3.6, 3)).foreach { (riderX, riderZ, hue) =>
      val rider = new Group[Object3DEventMap]()
      ThreeInterop.addChild(rider, Util.cyl(0.11, 0.13, 0.1, 8, M.concreteMid, 0.0, 0.05, 0.0))
      // the spring, as three stacked rings
      (0 until 3).foreach { i =>
        val ring = new Mesh(new TorusGeometry(0.085, 0.024, 4.0, 10.0), M.metalDark)
        ring.rotation.x = math.Pi / 2.0
        ring.position.y = 0.14 + i * 0.09
        ThreeInterop.addChild(rider, ring)
      }
      // the animal: a rounded body, a head, two ears, a stub tail
      val body = new Mesh(new SphereGeometry(0.24, 10, 8), M.paint(hue))
      body.scale.set(0.85, 0.8, 1.3)
      body.position.y = 0.62
      body.castShadow = true
      ThreeInterop.addChild(rider, body)
      val head = new Mesh(new SphereGeometry(0.17, 10, 8), M.paint(hue))
      head.position.set(0.0, 0.8, 0.24)
      head.castShadow = true
      ThreeInterop.addChild(rider, head)
      Vector(-1.0, 1.0).foreach { s =>
        val ear = new Mesh(new SphereGeometry(0.07, 8, 6), M.paint(hue))
        ear.position.set(s * 0.12, 0.93, 0.2)
        ThreeInterop.addChild(rider, ear)
        val eye = new Mesh(new SphereGeometry(0.024, 6, 5),
          Toon.cel(CelOptions(color = Pal.black, bands = 2)))
        eye.position.set(s * 0.07, 0.83, 0.38)
        ThreeInterop.addChild(rider, eye)
        // handle
        ThreeInterop.addChild(rider,
          Util.cyl(0.02, 0.02, 0.3, 6, M.metal, s * 0.16, 0.78, 0.02).rotateZ(math.Pi / 2.0))
      }
      val snout = new Mesh(new SphereGeometry(0.075, 8, 6),
        Toon.cel(CelOptions(color = 0xf2e0d0, bands = 2, tint = 0x8f86ad)))
      snout.position.set(0.0, 0.76, 0.38)
      ThreeInterop.addChild(rider, snout)
      rider.position.set(riderX, parkY + 0.06, riderZ)
      rider.rotation.y = rng.range(-0.6, 0.6)
      ctx.add(rider)
      ctx.collide(riderX - 0.3, riderZ - 0.3, riderX + 0.3, riderZ + 0.3, parkY + 0.9)
    }

    /* benches, tap, bins, planting */
    ctx.add(Props.makeBench(Props.BenchOpts(x = x0 + 2.4, z = 25.2, y = parkY + 0.06,
      ry = math.Pi / 2.0, len = 1.8)))
    ctx.add(Props.makeBench(Props.BenchOpts(x = x0 + 2.4, z = 22.6, y = parkY + 0.06,
      ry = math.Pi / 2.0, len = 1.8)))
    ctx.add(Props.makeBins(Props.BinsOpts(x = x0 + 1.4, z = 27.2, y = parkY + 0.06,
      ry = -math.Pi / 2.0)))

    /* the drinking fountain: a concrete stand and a bright tap */
    val fountainX = cx + 1.0
    val fountainZ = z1 - 1.9
    val stand     = Util.box(0.42, 0.82, 0.42, M.concrete, fountainX, parkY + 0.47, fountainZ)
    stand.castShadow = true
    stand.receiveShadow = true
    ctx.add(stand)
    Outline.hullOutline(stand, OutlineOptions(thickness = 0.003))
    val basin = new Mesh(new CylinderGeometry(0.28, 0.22, 0.12, 12), M.concreteMid)
    basin.position.set(fountainX, parkY + 0.94, fountainZ)
    ctx.add(basin)
    ctx.add(Util.cyl(0.24, 0.24, 0.02, 12, Toon.flat(FlatOptions(color = 0x8fb4c8)), fountainX,
      parkY + 0.97, fountainZ))
    val spout = Util.cyl(0.022, 0.022, 0.2, 6, M.metal, fountainX, parkY + 1.08, fountainZ - 0.12)
    spout.rotation.x = 0.5
    ctx.add(spout)
    ctx.add(Util.box(0.1, 0.06, 0.1, M.paint(0), fountainX, parkY + 1.15, fountainZ - 0.16))
    ctx.collide(fountainX - 0.3, fountainZ - 0.3, fountainX + 0.3, fountainZ + 0.3, parkY + 1.1)

    ctx.add(Props.makeFlowerBed(Props.FlowerBedOpts(x = x1 - 1.3, z = 24.0, w = 1.2, d = 5.0,
      y = parkY + 0.06, seed = 6110)))
    ctx.add(Props.makeCatBox(Props.CatBoxOpts(x = x0 + 1.5, z = 20.2, y = parkY + 0.06, ry = 0.6)))
    ctx.add(Props.makeBicycle(Props.BicycleOpts(x = x0 + 1.2, z = 29.6, y = parkY + 0.08, ry = 0.2,
      lean = 0.07, color = 0xd8a03c)))

    Vector((x0 + 1.6, z0 + 1.6, 1.2, 6120), (x1 - 1.5, z0 + 1.4, 1.14, 6121), (x1 - 1.6, z1 - 1.5, 1.22, 6122),
      (cx + 3.0, cz + 0.6, 1.1, 6123))
      .foreach { (tx, tz, scale, seed) =>
        sakura += SakuraSpot(x = tx, z = tz, y = parkY, scale = scale, seed = seed, lean = 0.1,
          leanDir = Some((seed % 6).toDouble))
      }
    (0 until 4).foreach { i =>
      shrubs += ShrubSpot(x = x0 + 1.2 + i * 3.4, z = z0 + 1.0, y = parkY + 0.06, r = 0.48,
        count = 3, spread = 1.3, seed = 6130 + i)
    }
    petals += PetalPatch(x = cx, z = cz, w = x1 - x0 - 1, d = z1 - z0 - 1, y = parkY + 0.07, n = 150)
  end buildChildrensPark

  /** ひばり荘: the access gallery and the open stair on the outside are what make this building type
    * instantly legible, so they get the front elevation and the balconies go round the back.
    */
  private def buildApartment(ctx: WorldCtx): Unit =
    val rng = Util.rngKit(6301)
    val g   = new Group[Object3DEventMap]()
    g.name = "apartment"
    ctx.add(g)

    val x0          = 34.4
    val x1          = 42.4
    val z0          = 36.0
    val z1          = 43.4
    val w           = x1 - x0
    val d           = z1 - z0
    val cx          = (x0 + x1) / 2.0
    val cz          = (z0 + z1) / 2.0
    val apartmentY  = Street.groundY(cz)
    val floorHeight = 2.7
    val floors      = 3
    val h           = floorHeight * floors
    val gallery     = 1.3 // gallery depth, on the +z face

    val parts = mutable.LinkedHashMap(
      "wall"      -> mutable.ArrayBuffer[Util.BakePart](),
      "trim"      -> mutable.ArrayBuffer[Util.BakePart](),
      "roof"      -> mutable.ArrayBuffer[Util.BakePart](),
      "metal"     -> mutable.ArrayBuffer[Util.BakePart](),
      "metalDark" -> mutable.ArrayBuffer[Util.BakePart](),
      "concrete"  -> mutable.ArrayBuffer[Util.BakePart]()
    )
    def push(key: String, geo: BufferGeometry[?, ?], mx: Matrix4): Unit =
      parts(key) += Util.BakePart(geo, Some(mx))

    push("concrete", new BoxGeometry(w + 0.4, 0.42, d + 0.4),
      Util.trs(cx, apartmentY + 0.21, cz))
    push("wall", new BoxGeometry(w, h, d - gallery),
      Util.trs(cx, apartmentY + h / 2.0, cz - gallery / 2.0))
    (1 to floors).foreach { k =>
      push("trim", new BoxGeometry(w + 0.24, 0.2, d + 0.24),
        Util.trs(cx, apartmentY + k * floorHeight, cz - gallery / 2.0))
    }
    push("roof", new BoxGeometry(w + 0.5, 0.22, d + 0.5),
      Util.trs(cx, apartmentY + h + 0.11, cz - gallery / 2.0 + 0.1))
    Vector(-1.0, 1.0).foreach { s =>
      push("roof", new BoxGeometry(w + 0.5, 0.4, 0.16),
        Util.trs(cx, apartmentY + h + 0.4, cz - gallery / 2.0 + 0.1 + s * ((d + 0.5) / 2.0)))
      push("roof", new BoxGeometry(0.16, 0.4, d + 0.5),
        Util.trs(cx + s * ((w + 0.5) / 2.0), apartmentY + h + 0.4, cz - gallery / 2.0 + 0.1))
    }

    // the gallery: a slab, a rail and four doors per floor
    val litMat  = Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.litWindowTex(0)), cache = false))
    val litMat2 = Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.litWindowTex(1)), cache = false))
    (0 until floors).foreach { k =>
      val y = apartmentY + k * floorHeight
      push("concrete", new BoxGeometry(w, 0.22, gallery), Util.trs(cx, y + 0.11, z1 - gallery / 2.0))
      push("metal", new BoxGeometry(w, 0.07, 0.07), Util.trs(cx, y + 1.14, z1 - 0.08))
      push("metalDark", new BoxGeometry(w, 0.07, 0.07), Util.trs(cx, y + 0.34, z1 - 0.08))
      val nb = math.round(w / 0.22).toInt
      (0 to nb).foreach { i =>
        push("metal", new BoxGeometry(0.04, 0.86, 0.04),
          Util.trs(x0 + (w / nb) * i, y + 0.72, z1 - 0.08))
      }
      // doors, a meter box and a small window beside each
      (0 until 4).foreach { i =>
        val dx = x0 + 0.85 + i * ((w - 1.7) / 3.0)
        push("trim", new BoxGeometry(0.94, 2.1, 0.14), Util.trs(dx, y + 1.28, z1 - gallery + 0.07))
        push("metalDark", new BoxGeometry(0.82, 1.98, 0.06), Util.trs(dx, y + 1.24, z1 - gallery + 0.14))
        push("metal", new BoxGeometry(0.05, 0.3, 0.05), Util.trs(dx + 0.3, y + 1.2, z1 - gallery + 0.18))
        val lit     = rng.chance(0.45)
        val paneMat = if lit then (if i % 2 != 0 then litMat2 else litMat) else M.glass
        ThreeInterop.addChild(g, Util.box(0.5, 0.6, 0.05, paneMat, dx + 0.72, y + 1.7, z1 - gallery + 0.14))
        push("metal", new BoxGeometry(0.6, 0.7, 0.1), Util.trs(dx + 0.72, y + 1.7, z1 - gallery + 0.09))
      }
    }

    // the open stair at the west end
    val stairX = x0 - 1.5
    (0 until floors).foreach { k =>
      val y = apartmentY + k * floorHeight
      push("concrete", new BoxGeometry(1.5, 0.2, gallery + 0.3),
        Util.trs(stairX + 0.75, y + 0.1, z1 - gallery / 2.0 + 0.15))
      // the flight itself, as a run of treads
      (0 until 9).foreach { i =>
        push("concrete", new BoxGeometry(1.3, 0.13, 0.3),
          Util.trs(stairX + 0.75, y + 0.24 + i * floorHeight / 9.0,
            z1 - gallery / 2.0 + 0.3 - i * 0.24))
      }
      push("metal", new BoxGeometry(0.06, 0.06, gallery + 0.3),
        Util.trs(stairX + 0.05, y + 1.14, z1 - gallery / 2.0 + 0.15))
      (0 to 8).foreach { i =>
        push("metal", new BoxGeometry(0.04, 0.9, 0.04),
          Util.trs(stairX + 0.05, y + 0.7 + i * floorHeight / 9.0,
            z1 - gallery / 2.0 + 0.3 - i * 0.24))
      }
    }
    push("roof", new BoxGeometry(1.8, 0.16, gallery + 0.6),
      Util.trs(stairX + 0.75, apartmentY + h + 0.08, z1 - gallery / 2.0 + 0.2))

    // balconies on the quiet side, with the washing
    (1 until floors).foreach { k =>
      val y = apartmentY + k * floorHeight
      push("concrete", new BoxGeometry(w - 0.6, 0.18, 0.9), Util.trs(cx, y + 0.09, z0 - 0.45))
      push("metal", new BoxGeometry(w - 0.6, 0.06, 0.06), Util.trs(cx, y + 1.06, z0 - 0.88))
      push("metalDark", new BoxGeometry(w - 0.6, 0.5, 0.05), Util.trs(cx, y + 0.6, z0 - 0.88))
      val nb = math.round((w - 0.6) / 0.24).toInt
      (0 to nb).foreach { i =>
        push("metal", new BoxGeometry(0.04, 0.9, 0.04),
          Util.trs(x0 + 0.3 + ((w - 0.6) / nb) * i, y + 0.64, z0 - 0.88))
      }
      (0 until 3).foreach { i =>
        val dx = x0 + 1.4 + i * 2.6
        push("trim", new BoxGeometry(1.5, 1.9, 0.14), Util.trs(dx, y + 1.05, z0 + 0.07))
        val paneMat = if rng.chance(0.5) then litMat else M.glass
        ThreeInterop.addChild(g, Util.box(1.3, 1.7, 0.05, paneMat, dx, y + 1.05, z0 + 0.13))
        val curtain = new Mesh(new PlaneGeometry(0.6, 1.6),
          Toon.flat(FlatOptions(color = 0xe6dfd0, map = Some(Textures.curtainTex(i % 2)), cache = false)))
        curtain.position.set(dx + (if i % 2 != 0 then 0.32 else -0.32), y + 1.05, z0 + 0.16)
        curtain.rotation.y = math.Pi
        ThreeInterop.setUserDataFlag(curtain, "noOutline")
        ThreeInterop.addChild(g, curtain)
      }
      ctx.add(Props.makeLaundryPole(Props.LaundryPoleOpts(
        x = cx + (if k == 1 then -1.6 else 1.4), z = z0 - 0.7, y = y + 0.18, ry = math.Pi / 2.0,
        len = 2.2, n = 3, h = 1.5, seed = 6310 + k
      )))
    }

    // the name plate, the meter bank, and the ground-floor clutter
    val plateSide = Toon.flat(FlatOptions(color = Pal.wallGray))
    val plate = new Mesh(
      new BoxGeometry(1.5, 0.44, 0.08),
      Props.matArray(plateSide, plateSide, plateSide, plateSide,
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.apartmentPlate()), cache = false)),
        plateSide)
    )
    plate.position.set(x0 + 1.2, apartmentY + h + 0.34, z1 + 0.05)
    plate.castShadow = true
    ThreeInterop.addChild(g, plate)
    Outline.hullOutline(plate, OutlineOptions(thickness = 0.003))

    /* The umbrella stand stays tucked against the gallery.  The bins and the bicycles do not,
     * because there is no verge on this side to tuck them into -- the gallery's collider reaches
     * z = 43.6 and the north lane's paving starts at 43.5 -- so both go on the lane's north verge,
     * which is where a 集積所 serving this block actually is.
     */
    val laneSurface = Street.groundY(45.2) + 0.09
    val vergeY      = Street.groundY(47.4)
    ctx.add(Props.makeUmbrellaStand(Props.UmbrellaStandOpts(x = x0 + 0.4, z = z1 + 0.35,
      y = laneSurface, n = 3, seed = 6320)))
    ctx.add(Props.makeBins(Props.BinsOpts(x = 41.0, z = 47.35, y = vergeY, ry = math.Pi)))
    // z 47.40: the house's plinth is 0.1 m proud of its wall and reaches 47.702, and 0.55 m of
    // bicycle has 0.25 m of slack in a 0.8 m verge
    ctx.add(Props.makeBicycle(Props.BicycleOpts(x = 38.2, z = 47.40, y = vergeY + 0.06, ry = 0.0,
      lean = 0.07, color = 0x4c6a86)))
    Vector((x1 + 0.24, z0 + 1.6), (x1 + 0.24, z0 + 4.4)).foreach { (ax, az) =>
      ctx.add(Props.makeAircon(Props.AirconOpts(x = ax, z = az, y = apartmentY + 0.42,
        ry = math.Pi / 2.0, w = 0.86, h = 0.6)))
    }

    val matFor: Map[String, Material[?]] = Map(
      "wall"      -> M.wallAlt, "trim"       -> M.trim, "roof" -> M.roof, "metal" -> M.metal,
      "metalDark" -> M.metalDark, "concrete" -> M.concrete
    )
    parts.foreach { (key, bucket) =>
      if bucket.nonEmpty then
        val mesh = new Mesh(Util.bake(bucket.toList), matFor(key))
        mesh.castShadow = true
        mesh.receiveShadow = true
        ThreeInterop.addChild(g, mesh)
        if key == "wall" || key == "roof" then
          Outline.hullOutline(mesh, OutlineOptions(thickness = 0.0032))
    }
    ctx.collide(x0 - 0.3, z0 - 1.0, x1 + 0.3, z1 + 0.2, apartmentY + h)
  end buildApartment

  /** A traditional timber house below the shrine: dark boarding under a heavy tiled roof with a
    * deep eave, which is what the street needs to stop reading as one material.
    */
  private def buildTimberHouse(ctx: WorldCtx, shrubs: mutable.ArrayBuffer[ShrubSpot]): Unit =
    val g = new Group[Object3DEventMap]()
    g.name = "timberHouse"
    ctx.add(g)

    val x0         = -24.4
    val x1         = -17.2
    val z0         = 17.8
    val z1         = 25.2
    val w          = x1 - x0
    val d          = z1 - z0
    val cx         = (x0 + x1) / 2.0
    val cz         = (z0 + z1) / 2.0
    val timberY    = Street.groundY(cz)
    val wallHeight = 2.75

    val parts = mutable.LinkedHashMap(
      "wood"  -> mutable.ArrayBuffer[Util.BakePart](),
      "dark"  -> mutable.ArrayBuffer[Util.BakePart](),
      "roof"  -> mutable.ArrayBuffer[Util.BakePart](),
      "stone" -> mutable.ArrayBuffer[Util.BakePart](),
      "metal" -> mutable.ArrayBuffer[Util.BakePart]()
    )
    def push(key: String, geo: BufferGeometry[?, ?], mx: Matrix4): Unit =
      parts(key) += Util.BakePart(geo, Some(mx))

    // stone base, boarded walls, and the corner posts that break them up
    push("stone", new BoxGeometry(w + 0.3, 0.44, d + 0.3), Util.trs(cx, timberY + 0.22, cz))
    push("wood", new BoxGeometry(w, wallHeight, d),
      Util.trs(cx, timberY + 0.44 + wallHeight / 2.0, cz))
    Vector(-1.0, 1.0).foreach { sx =>
      Vector(-1.0, 1.0).foreach { sz =>
        push("dark", new BoxGeometry(0.18, wallHeight, 0.18),
          Util.trs(cx + sx * (w / 2.0 - 0.05), timberY + 0.44 + wallHeight / 2.0,
            cz + sz * (d / 2.0 - 0.05)))
      }
    }
    // horizontal boarding: a run of shallow ribs, which is what 下見板 is
    val ribs     = mutable.ArrayBuffer.empty[Util.BakePart]
    val ribCount = math.round(wallHeight / 0.26).toInt
    (0 until ribCount).foreach { i =>
      val y = timberY + 0.5 + i * 0.26
      ribs += Util.BakePart(new BoxGeometry(w + 0.05, 0.05, d + 0.05), Some(Util.trs(cx, y, cz)))
    }
    val ribMesh = new Mesh(Util.bake(ribs.toList), M.woodDark)
    ribMesh.castShadow = true
    ThreeInterop.addChild(g, ribMesh)

    // the roof: a heavy hip with a deep eave and a ridge tile
    val eave  = 1.05
    val roofW = w + eave * 2
    val roofD = d + eave * 2
    val roofH = 1.7
    val roofY = timberY + 0.44 + wallHeight
    push("dark", new BoxGeometry(w + 0.5, 0.2, d + 0.5), Util.trs(cx, roofY + 0.1, cz))
    val pyramid = new ConeGeometry(math.sqrt(0.5), 1.0, 4, 1)
    pyramid.rotateY(math.Pi / 4.0)
    push("roof", pyramid,
      Util.trs(cx, roofY + 0.2 + roofH / 2.0, cz, 0.0, 0.0, 0.0,
        roofW, roofH, roofD))
    push("roof", new BoxGeometry(roofW, 0.24, roofD), Util.trs(cx, roofY + 0.28, cz))
    pyramid.dispose()
    // a short ridge and the end tiles
    push("roof", new BoxGeometry(2.2, 0.3, 0.4), Util.trs(cx, roofY + 0.2 + roofH - 0.02, cz))
    // rafter ends showing under the eave
    val rafters     = mutable.ArrayBuffer.empty[Util.BakePart]
    val rafterCount = math.round(roofW / 0.5).toInt
    (0 until rafterCount).foreach { i =>
      rafters += Util.BakePart(new BoxGeometry(0.1, 0.12, roofD - 0.2),
        Some(Util.trs(cx - roofW / 2.0 + 0.25 + i * (roofW - 0.5) / (rafterCount - 1),
          roofY + 0.16, cz)))
    }
    val rafterMesh = new Mesh(Util.bake(rafters.toList), M.woodDark)
    ThreeInterop.addChild(g, rafterMesh)

    // 縁側 -- the veranda along the west face, with paper screens behind it
    val verandaX = x0 - 0.85
    val deck     = Util.box(1.7, 0.16, d - 1.0, M.wood, verandaX, timberY + 0.5, cz)
    deck.castShadow = true
    deck.receiveShadow = true
    ThreeInterop.addChild(g, deck)
    Vector(-1.0, 1.0).foreach { sz =>
      push("dark", new BoxGeometry(0.12, 0.5, 0.12),
        Util.trs(verandaX - 0.7, timberY + 0.25, cz + sz * (d / 2.0 - 0.7)))
    }
    // shoji: warm panels with a lattice over them
    val shoji = Util.box(0.06, 1.9, d - 1.2, Toon.flat(FlatOptions(color = 0xf4ead6)),
      x0 + 0.03, timberY + 1.5, cz)
    ThreeInterop.addChild(g, shoji)
    val lattice        = mutable.ArrayBuffer.empty[Util.BakePart]
    val latticeColumns = math.round((d - 1.2) / 0.34).toInt
    (0 to latticeColumns).foreach { i =>
      lattice += Util.BakePart(new BoxGeometry(0.05, 1.9, 0.05),
        Some(Util.trs(x0 + 0.01, timberY + 1.5,
          cz - (d - 1.2) / 2.0 + ((d - 1.2) / latticeColumns) * i)))
    }
    (0 until 5).foreach { i =>
      lattice += Util.BakePart(new BoxGeometry(0.05, 0.05, d - 1.2),
        Some(Util.trs(x0 + 0.01, timberY + 0.66 + i * 0.42, cz)))
    }
    val latticeMesh = new Mesh(Util.bake(lattice.toList), M.woodDark)
    ThreeInterop.addChild(g, latticeMesh)
    push("dark", new BoxGeometry(0.16, 0.14, d - 1.0), Util.trs(x0 + 0.02, timberY + 0.62, cz))
    push("dark", new BoxGeometry(0.16, 0.14, d - 1.0), Util.trs(x0 + 0.02, timberY + 2.5, cz))

    // a small window on the street face, and the entrance
    push("dark", new BoxGeometry(1.4, 1.2, 0.16), Util.trs(cx - 1.6, timberY + 1.85, z0 + 0.02))
    ThreeInterop.addChild(g,
      Util.box(1.2, 1.0, 0.06,
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.litWindowTex(2)), cache = false)),
        cx - 1.6, timberY + 1.85, z0 - 0.03))
    push("dark", new BoxGeometry(1.3, 2.1, 0.16), Util.trs(cx + 1.5, timberY + 1.5, z0 + 0.02))
    ThreeInterop.addChild(g,
      Util.box(1.1, 1.9, 0.06, Toon.flat(FlatOptions(color = 0xe8dcc2)),
        cx + 1.5, timberY + 1.5, z0 - 0.03))
    push("metal", new BoxGeometry(1.7, 0.1, 0.5), Util.trs(cx + 1.5, timberY + 2.66, z0 - 0.2))

    /* the garden: a bamboo fence, a stepping-stone path, a stone and a lantern */
    val fenceZ     = z0 - 2.4
    val fenceParts = mutable.ArrayBuffer.empty[Util.BakePart]
    val fenceCount = math.round(w / 0.16).toInt
    (0 until fenceCount).foreach { i =>
      fenceParts += Util.BakePart(new CylinderGeometry(0.05, 0.05, 1.5, 6),
        Some(Util.trs(x0 + 0.2 + i * ((w - 0.4) / (fenceCount - 1)), 0.75, fenceZ)))
    }
    Vector(0.35, 1.3).foreach { y =>
      fenceParts += Util.BakePart(new CylinderGeometry(0.05, 0.05, w - 0.2, 6),
        Some(Util.trs(cx, y, fenceZ, 0.0, 0.0, math.Pi / 2.0)))
    }
    val fenceMesh = new Mesh(Util.bake(fenceParts.toList),
      Toon.cel(CelOptions(color = 0xb09468, bands = 3, tint = 0x6f6790)))
    fenceMesh.position.y = timberY
    fenceMesh.castShadow = true
    ThreeInterop.addChild(g, fenceMesh)
    ctx.collide(x0, fenceZ - 0.12, x1, fenceZ + 0.12, timberY + 1.5)

    // stepping stones from the gate up to the veranda
    val stones = mutable.ArrayBuffer.empty[Util.BakePart]
    (0 until 5).foreach { i =>
      val r = 0.24 + (i % 2) * 0.04
      stones += Util.BakePart(new CylinderGeometry(r, r, 0.09, 7),
        Some(Util.trs(x0 + 1.6 - i * 0.55, 0.045, fenceZ + 0.5 + i * 0.42)))
    }
    val stoneMesh = new Mesh(Util.bake(stones.toList),
      Toon.cel(CelOptions(color = Pal.stone, bands = 3, tint = 0x655d80)))
    stoneMesh.position.y = timberY
    stoneMesh.receiveShadow = true
    ThreeInterop.addChild(g, stoneMesh)

    val rock = new Mesh(new IcosahedronGeometry(0.42, 0),
      Toon.cel(CelOptions(color = 0x9a94a2, bands = 3, tint = 0x605878)))
    rock.scale.set(1.0, 0.72, 0.85)
    rock.position.set(x0 + 3.6, timberY + 0.28, z0 - 1.2)
    rock.castShadow = true
    ThreeInterop.addChild(g, rock)
    ctx.add(Props.makePlanter(Props.PlanterOpts(x = x0 + 0.4, z = z0 - 1.5, y = timberY, r = 0.28,
      flower = true, seed = 6401, n = 5)))
    shrubs += ShrubSpot(x = x0 + 2.2, z = z0 - 1.7, y = timberY, r = 0.6, count = 4, spread = 1.3,
      seed = 6402)
    shrubs += ShrubSpot(x = x1 + 1.0, z = cz - 1.0, y = timberY, r = 0.55, count = 4, spread = 1.4,
      seed = 6403)

    /* Gate, name plate and post box, in the *garden* fence: the lane belongs to the shrine, and
     * the house's own frontage is its south fence.
     */
    val gateX = x0 + 1.75
    val gateZ = z0 - 2.4
    Vector(-1.0, 1.0).foreach { s =>
      val gatePost = Util.box(0.2, 1.7, 0.2, M.woodDark, gateX + s * 0.75, timberY + 0.85, gateZ)
      gatePost.castShadow = true
      ThreeInterop.addChild(g, gatePost)
    }
    val namePlateMesh = new Mesh(new PlaneGeometry(0.34, 0.17),
      Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.namePlate(2)), cache = false)))
    namePlateMesh.position.set(gateX - 0.75, timberY + 1.3, gateZ - 0.12)
    namePlateMesh.rotation.y = math.Pi
    ThreeInterop.addChild(g, namePlateMesh)
    ctx.add(Props.makePostBox(Props.PostBoxOpts(x = gateX + 1.4, z = gateZ - 0.35, y = timberY,
      ry = 0.3)))

    val matFor: Map[String, Material[?]] = Map(
      "wood"  -> M.wood, "dark" -> M.woodDark, "roof" -> M.tileRoof, "stone" -> M.concreteMid,
      "metal" -> M.metal
    )
    parts.foreach { (key, bucket) =>
      if bucket.nonEmpty then
        val mesh = new Mesh(Util.bake(bucket.toList), matFor(key))
        mesh.castShadow = true
        mesh.receiveShadow = true
        ThreeInterop.addChild(g, mesh)
        if key == "roof" || key == "wood" then
          Outline.hullOutline(mesh, OutlineOptions(thickness = 0.0034))
    }
    ctx.collide(x0 - 1.8, z0 - 0.2, x1 + 0.2, z1 + 0.2, timberY + 0.44 + wallHeight)
  end buildTimberHouse

  /** The sweep over the existing housing: the houses were always modular but never *inhabited* --
    * same volume, same render, nothing outside the front door.  This walks the same definitions the
    * house generator was given and puts washing, a post box, a name plate, a bicycle, planters, a
    * refuse point and the occasional lit window where they belong, and only where there is actually
    * room between the house and the kerb.
    */
  private def dressHousing(ctx: WorldCtx, houses: Vector[HouseDef]): Unit =
    val rng  = Util.rngKit(8801)
    var gomi = 0

    houses.foreach { house =>
      val (fx, fz) = house.face match
        case Buildings.Face.XMinus => (-1.0, 0.0)
        case Buildings.Face.XPlus  => (1.0, 0.0)
        case Buildings.Face.ZMinus => (0.0, -1.0)
        case Buildings.Face.ZPlus  => (0.0, 1.0)
      val frontIsX  = fx != 0.0
      val frontHalf = if frontIsX then house.w / 2.0 else house.d / 2.0
      val sideHalf  = if frontIsX then house.d / 2.0 else house.w / 2.0
      val y         = Street.groundY(house.z)

      // the point just outside the front door, and the two axes to work on
      val px = house.x + fx * (frontHalf + 0.55)
      val pz = house.z + fz * (frontHalf + 0.55)
      // lateral unit vector along the frontage
      val lx = if frontIsX then 0.0 else 1.0
      val lz = if frontIsX then 1.0 else 0.0
      def at(t: Double, out: Double): (Double, Double) =
        (px + lx * t + fx * out, pz + lz * t + fz * out)

      /* Skip anything that would land in the carriageway: the frontage sits anywhere from hard
       * against the pavement to four metres back, so this has to be checked rather than assumed.
       */
      def clear(x: Double, z: Double): Boolean =
        math.abs(x - Street.centerX(z)) > Street.RoadHalf + Street.WalkW - 0.35
      def put(obj: Object3D[?], x: Double, z: Double): Unit =
        if clear(x, z) then ctx.add(obj)
      /* Anything standing on the ground is seated with `ctx.groundAt`, not with `y`: half of these
       * plots front onto a 0.135 m footway or onto made ground a third of a metre below the natural
       * grade, so a prop placed at `y` is buried or in the air by that much.
       */
      def gy(x: Double, z: Double): Double = ctx.groundAt(x, z)

      // name plate and post box beside the door
      val plateT = math.min(sideHalf - 0.5, 1.5)
      val nameX  = house.x + fx * (frontHalf + 0.045) + lx * plateT
      val nameZ  = house.z + fz * (frontHalf + 0.045) + lz * plateT
      if clear(nameX, nameZ) then
        val plate = new Mesh(new PlaneGeometry(0.32, 0.16),
          Toon.flat(FlatOptions(color = 0xffffff,
            map = Some(Textures.namePlate(rng.int(0, 5))), cache = false)))
        plate.position.set(nameX, y + 1.42, nameZ)
        plate.rotation.y =
          if frontIsX then fx * math.Pi / 2.0 else (if fz > 0 then 0.0 else math.Pi)
        ctx.add(plate)
      val (boxX, boxZ) = at(-math.min(sideHalf - 0.6, 1.7) + house.postBoxAlong.getOrElse(0.0),
        house.postBoxOut.getOrElse(0.0))
      put(
        Props.makePostBox(Props.PostBoxOpts(x = boxX, z = boxZ,
          y = gy(boxX, boxZ) - house.postBoxSink.getOrElse(0.0), ry = rng.range(-0.4, 0.4))),
        boxX, boxZ
      )

      // planters and a bicycle
      if rng.chance(0.8) then
        val t              = rng.range(-sideHalf * 0.5, sideHalf * 0.5) + house.planterAlong.getOrElse(0.0)
        val planterOut     = house.planterOut.getOrElse(-0.15)
        val planterSpacing = house.planterSpacing.getOrElse(0.65)
        val (ax, az)       = at(t, planterOut)
        put(
          Props.makePlanter(Props.PlanterOpts(x = ax, z = az,
            y = gy(ax, az) - house.planterSink.getOrElse(0.0), r = rng.range(0.18, 0.26),
            flower = rng.chance(0.6), seed = 8810 + house.seed, n = 5)),
          ax, az
        )
        val (cx2, cz2) = at(t + planterSpacing, planterOut)
        put(
          Props.makePlanter(Props.PlanterOpts(x = cx2, z = cz2,
            y = gy(cx2, cz2) - house.planterSink.getOrElse(0.0), r = 0.19, flower = false,
            seed = 8820 + house.seed, n = 4)),
          cx2, cz2
        )
      /* A bicycle stands *along* the frontage, not nose-on to it: a bicycle is 1.73 m long about its
       * own origin, so at a 0.80 m standoff a wheelbase pointed at the wall buries its back wheel and
       * half its rack inside the render.
       */
      val hasBike = rng.chance(0.55)
      if hasBike && !house.skipBike then
        val (bikeX, bikeZ) = at(rng.range(-sideHalf * 0.7, sideHalf * 0.7), house.bikeOut.getOrElse(0.25))
        put(
          Props.makeBicycle(Props.BicycleOpts(x = bikeX, z = bikeZ,
            y = gy(bikeX, bikeZ) + house.bikeLift.getOrElse(0.0),
            ry = (if frontIsX then math.Pi / 2.0 else 0.0) + rng.range(-0.3, 0.3),
            lean = rng.range(-0.1, 0.1),
            color = rng.pick(Vector(0x3f6f9c, 0xd8a03c, 0x9c5a4a, 0x4f8f6a, 0x8f6fb5)))),
          bikeX, bikeZ
        )

      // washing, on the side away from the street
      if house.floors == 2 && rng.chance(0.7) then
        val s  = rng.sign
        val wx = house.x + (if frontIsX then -fx * 0.6 else s * (sideHalf - 1.2))
        val wz = house.z + (if frontIsX then s * (sideHalf - 1.2) else -fz * 0.6)
        ctx.add(Props.makeLaundryPole(Props.LaundryPoleOpts(x = wx, z = wz, y = gy(wx, wz),
          ry = if frontIsX then 0.0 else math.Pi / 2.0, len = 2.2, n = 3, seed = 8830 + house.seed)))

      /* An air-conditioning unit and an umbrella stand.  `ry` is the same expression the name plate
       * above uses: the grille is on the unit's +z face, so it has to be turned to the wall's
       * *outward* normal -- `frontIsX ? PI/2 : 0` is only right for the houses that face +x and +z.
       */
      if rng.chance(0.6) then
        val s  = rng.sign
        val ax = house.x + (if frontIsX then fx * (frontHalf + 0.28) else s * (sideHalf - 0.9))
        val az = house.z + (if frontIsX then s * (sideHalf - 0.9) else fz * (frontHalf + 0.28))
        put(
          Props.makeAircon(Props.AirconOpts(x = ax, z = az, y = gy(ax, az), w = 0.78, h = 0.56,
            ry = if frontIsX then fx * math.Pi / 2.0 else (if fz > 0 then 0.0 else math.Pi))),
          ax, az
        )
      if rng.chance(0.35) then
        val (ux, uz) = at(math.min(sideHalf - 0.9, 1.2), -0.1)
        put(
          Props.makeUmbrellaStand(Props.UmbrellaStandOpts(x = ux, z = uz,
            y = gy(ux, uz) - house.umbrellaSink.getOrElse(0.0), n = rng.int(2, 4),
            seed = 8840 + house.seed)),
          ux, uz
        )
      if rng.chance(0.28) then
        val (bowlX, bowlZ) = at(-math.min(sideHalf - 1.1, 1.1), -0.1)
        put(
          Props.makePetBowl(Props.PetBowlOpts(x = bowlX, z = bowlZ, y = gy(bowlX, bowlZ),
            ry = rng.range(0, 3))),
          bowlX, bowlZ
        )

      /* Refuse points are a neighbourhood thing, not a per-house one: three of them, on corners,
       * each with the collection-day plate.
       */
      if gomi < 3 && rng.chance(0.22) then
        val (refuseX, refuseZ) = at(sideHalf + 0.4, house.gomiOut.getOrElse(-0.4))
        if clear(refuseX, refuseZ) then
          gomi += 1
          ctx.add(Props.makeBins(Props.BinsOpts(x = refuseX, z = refuseZ,
            y = gy(refuseX, refuseZ) - house.gomiSink.getOrElse(0.0),
            ry = if frontIsX then math.Pi / 2.0 else 0.0)))
          ctx.add(signPostWithPlates(
            x = refuseX + (if frontIsX then 0.0 else -0.9),
            z = refuseZ + (if frontIsX then house.gomiSignAlong.getOrElse(-0.9) else 0.0),
            y = gy(refuseX, refuseZ),
            ry = if frontIsX then math.Pi / 2.0 else 0.0, h = 1.5,
            plates = Vector(SignPlate(map = Textures.gomiPlate(0), w = 0.44, h = 0.34,
              y = Some(1.25)))
          ))

      /* A warm window on about a third of them.  Nothing behind the glass but shelves and a
       * lampshade -- there is nobody in this world.
       */
      if rng.chance(0.34) then
        val yy = y + (if house.floors == 2 then 2.72 + 1.5 else 1.42)
        val u  = rng.range(-sideHalf * 0.5, sideHalf * 0.5)
        val wx = house.x + fx * (frontHalf + 0.06) + lx * u
        val wz = house.z + fz * (frontHalf + 0.06) + lz * u
        val win = new Mesh(new PlaneGeometry(1.3, 1.1),
          Toon.flat(FlatOptions(color = 0xffffff,
            map = Some(Textures.litWindowTex(rng.int(0, 2))), cache = false)))
        win.position.set(wx, yy, wz)
        win.rotation.y = if frontIsX then fx * math.Pi / 2.0 else (if fz > 0 then 0.0 else math.Pi)
        ThreeInterop.setUserDataFlag(win, "noOutline")
        ctx.add(win)
    }
  end dressHousing

end District
