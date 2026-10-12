package sakura.world

import sakura.ThreeInterop
import sakura.core.{Pal, Util}
import sakura.core.Outline
import sakura.core.Outline.OutlineOptions
import sakura.core.Textures
import sakura.core.Toon
import sakura.core.Toon.{CelOptions, FlatOptions}
import vendor.three.*

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.JSConverters.*

/** 1:1 port of upstream `src/world/matsuri.js`: 夏まつり準備中, the festival ground west of the
  * shrine forecourt two days out.
  *
  * Nothing is trading: every stall is up and empty, the temporary things -- a cable snaking to a
  * distribution box, a rope on stakes, chalked-out pitch lines, a programme board with times on it
  * -- carry the story, the lanterns and flags are hung but unlit, and the layout goes round the
  * trees that were here first rather than clearing them.
  */
object Matsuri:

  private val X0 = -45.0
  private val X1 = -31.4
  private val Z0 = 13.8
  private val Z1 = 25.0
  private val Cx = (X0 + X1) / 2.0
  private val Cz = (Z0 + Z1) / 2.0
  private val Pi = math.Pi

  /** The three trees of the shrine's outer wood standing in the clearing (see `Shrine`). */
  private val Trees = Vector((-40.8, 22.4), (-37.6, 19.6), (-32.6, 16.8))

  private object M:
    lazy val metal       = Toon.cel(CelOptions(color = Pal.metal, bands = 3, tint = 0x666090))
    lazy val metalDark   = Toon.cel(CelOptions(color = Pal.metalDark, bands = 3, tint = 0x5c5680))
    lazy val dark        = Toon.cel(CelOptions(color = Pal.blackSoft, bands = 2, tint = 0x4b4560))
    lazy val wood        = Toon.cel(CelOptions(color = 0xb09a76, bands = 3, tint = 0x5c5680))
    lazy val woodDark    = Toon.cel(CelOptions(color = 0x8a6f52, bands = 3, tint = 0x554e74))
    lazy val cream       = Toon.cel(CelOptions(color = 0xefe6d2, bands = 3, tint = 0x6f6790))
    lazy val red         = Toon.cel(CelOptions(color = Pal.redDeep, bands = 3, tint = 0x7a4060))
    lazy val concrete    = Toon.cel(CelOptions(color = Pal.concrete, bands = 3, tint = 0x6f6790))
    lazy val concreteMid = Toon.cel(CelOptions(color = Pal.concreteMid, bands = 3, tint = 0x6a6288))
    lazy val tarp        = Toon.cel(CelOptions(color = 0xdcd4c0, bands = 3, side = 2, tint = 0x6f6790))
    lazy val wire        = Toon.cel(CelOptions(color = 0x4c4658, bands = 2, tint = 0x413c58))
    lazy val cable       = Toon.cel(CelOptions(color = 0x3f3a48, bands = 2, tint = 0x413c58))

  /** One folded stall's parameters: which paper sign, whether the canopy is still rolled, whether
    * the side sheets are up, and which piece of equipment stands beside it.
    */
  final private case class Stall(
    x: Double,
    z: Double,
    ry: Double,
    sign: Int,
    rolled: Boolean,
    sides: Boolean,
    extra: String
  )

  final private case class Endpoint(x: Double, z: Double, h: Double)

  final private case class Stage(x: Double, z: Double, top: Double)

  def buildMatsuri(ctx: WorldCtx): Planting =
    val gm     = Ground.groundMats()
    val rng    = Util.rngKit(9401)
    val sakura = mutable.ArrayBuffer.empty[SakuraSpot]
    val shrubs = mutable.ArrayBuffer.empty[ShrubSpot]
    val grove  = mutable.ArrayBuffer.empty[GroveSpot]
    val petals = mutable.ArrayBuffer.empty[PetalPatch]
    val y      = Street.groundY(Cz) // flat, and 0 across this whole clearing

    /* Rolled dirt with a gravel apron at the entrance, and the pitch lines chalked on it -- the
     * lines are what make it read as a *ground* rather than as a field.
     */
    Ground.pad(ctx,
      Ground.PadOptions(x = Cx, z = Cz, w = X1 - X0, d = Z1 - Z0, y = y, h = 0.06,
        mat = Some(gm.dirt), name = "matsuriGround"))
    Ground.pad(ctx,
      Ground.PadOptions(x = X1 - 1.6, z = 18.0, w = 3.2, d = 4.6, y = y + 0.005,
        h = 0.06, mat = Some(gm.gravel), name = "matsuriApron"))
    Vector(15.9, 23.1).foreach { z =>
      Ground.laneLine(ctx,
        Ground.LineOptions(axis = "x", at = z, from = X0 + 1.4, to = X1 - 2.4,
          y = y + 0.07, dash = 0.9))
    }
    Ground.laneLine(ctx,
      Ground.LineOptions(axis = "z", at = X0 + 1.2, from = Z0 + 1.4, to = Z1 - 1.4,
        y = y + 0.07, dash = 0.9))

    buildEntrance(ctx, y)
    buildStalls(ctx, y, rng)
    val stage = buildStage(ctx, y)
    buildLighting(ctx, y, stage, rng)
    buildTemporaries(ctx, y, rng)

    /* One more green mass on the west boundary so the clearing has a back to it, and a low hedge
     * along the south where the ground runs into the shrine wood.  No cherry: the one in the middle
     * of the ground is the shrine's, it was here first, and a second would close the clearing in.
     */
    grove += GroveSpot(x = X0 - 2.6, z = 17.2, y = y, scale = 1.75, seed = 9410, spread = 1.2)
    grove += GroveSpot(x = X0 - 1.8, z = 24.2, y = y, scale = 1.5, seed = 9411, spread = 1.1)
    (0 until 5).foreach { i =>
      shrubs += ShrubSpot(x = X0 + 1.0 + i * 3.1, z = Z1 + 0.8, y = y, r = 0.5, count = 3,
        spread = 1.3, seed = 9420 + i)
    }
    (0 until 3).foreach { i =>
      shrubs += ShrubSpot(x = X0 + 2.2 + i * 4.4, z = Z0 - 0.9, y = y, r = 0.46, count = 3,
        spread = 1.2, seed = 9430 + i)
    }
    petals += PetalPatch(x = Cx, z = Cz, w = X1 - X0 - 2, d = Z1 - Z0 - 2, y = y + 0.07, n = 160)
    Planting(sakura = sakura.toVector, shrubs = shrubs.toVector, grove = grove.toVector,
      petals = petals.toVector)
  end buildMatsuri

  /** The way in: the banner, and the programme board under it. */
  private def buildEntrance(ctx: WorldCtx, y: Double): Unit =
    val bx = X1 - 0.2
    val z0 = 15.7
    val z1 = 20.4
    val zs = Vector(z0, z1)

    /* Two scaffold poles and the banner between them.  It hangs from a top rail rather than being
     * nailed to the poles, and it sags -- a taut banner reads as a printed panel, a slack one reads
     * as cloth.
     */
    zs.foreach { z =>
      val pole = Util.cyl(0.07, 0.08, 4.0, 8, M.metal, bx, y + 2.0, z)
      pole.castShadow = true
      ctx.add(pole)
      Outline.hullOutline(pole, OutlineOptions(thickness = 0.0032))
      ctx.add(Util.cyl(0.2, 0.24, 0.16, 8, M.concreteMid, bx, y + 0.08, z))
      ctx.collide(bx - 0.2, z - 0.2, bx + 0.2, z + 0.2, y + 4.0)
      // the guy wire back to a stake, which is how a temporary pole stands up
      val stake = new Vector3(bx + 1.5, y + 0.05, z + (if z == z0 then -1.1 else 1.1))
      ctx.add(new Mesh(
        new TubeGeometry(Util.sagCurve(new Vector3(bx, y + 3.6, z), stake, 0.06, 8), 8.0, 0.012, 4.0, false),
        M.metalDark
      ))
      ctx.add(Util.cyl(0.02, 0.02, 0.3, 5, M.metalDark, stake.x, y + 0.12, stake.z))
    }
    ctx.add(Util.box(0.09, 0.09, z1 - z0 + 0.4, M.metal, bx, y + 3.72, (z0 + z1) / 2.0))
    locally {
      val width = z1 - z0 - 0.1
      val cloth = new Mesh(new PlaneGeometry(width, 0.72, 6.0, 1.0),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.matsuriBanner()), side = 2,
          cache = false)))
      /* Sag the middle of the cloth by hand: six segments, a cosine drop. */
      val positions = ThreeInterop.positionAttribute(cloth.geometry)
      (0 until positions.count.toInt).foreach { i =>
        val u = positions.getX(i.toDouble) / width
        positions.setY(i.toDouble,
          positions.getY(i.toDouble) - (1.0 - math.abs(u * 2.0)) * 0.09)
      }
      ThreeInterop.markNeedsUpdate(positions)()
      cloth.geometry.computeVertexNormals()
      cloth.position.set(bx - 0.02, y + 3.28, (z0 + z1) / 2.0)
      cloth.rotation.y = -Pi / 2.0
      cloth.castShadow = true
      ctx.add(cloth)
    }

    // the programme board, on a timber A-frame beside the entrance
    locally {
      val px = X1 - 1.3
      val pz = 21.9
      val boardFace = Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.matsuriBoard()),
        cache = false))
      val board = new Mesh(
        new BoxGeometry(0.1, 1.4, 1.02),
        Props.matArray(boardFace, boardFace, M.woodDark, M.woodDark, M.woodDark, M.woodDark)
      )
      board.position.set(px, y + 1.35, pz)
      board.rotation.y = -0.22
      board.castShadow = true
      ctx.add(board)
      Outline.hullOutline(board, OutlineOptions(thickness = 0.003))
      Vector(-1.0, 1.0).foreach { s =>
        val leg = Util.box(0.1, 1.7, 0.1, M.woodDark, px + 0.12, y + 0.85, pz + s * 0.44)
        leg.castShadow = true
        ctx.add(leg)
        val brace = Util.box(0.08, 1.1, 0.08, M.woodDark, px + 0.42, y + 0.55, pz + s * 0.44)
        brace.rotation.z = 0.5
        ctx.add(brace)
      }
      ctx.add(Util.box(0.1, 0.1, 1.1, M.woodDark, px + 0.12, y + 1.98, pz))
      ctx.collide(px - 0.2, pz - 0.6, px + 0.6, pz + 0.6, y + 2.0)
    }
  end buildEntrance

  /* Five stalls on the two chalked lines, all facing into the ground.  Two of the five still have
   * their canopies rolled, one has no side sheets yet, and every counter is empty -- which is the
   * difference between "before" and "during".
   */
  private def buildStalls(ctx: WorldCtx, y: Double, rng: Util.RngKit): Unit =
    val set = Vector(
      Stall(x = -43.4, z = 23.4, ry = Pi, sign = 0, rolled = false, sides = true, extra = "takoyaki"),
      Stall(x = -39.0, z = 23.4, ry = Pi, sign = 1, rolled = true, sides = true, extra = "kakigori"),
      Stall(x = -34.6, z = 23.4, ry = Pi, sign = 3, rolled = false, sides = false, extra = "prize"),
      Stall(x = -42.0, z = 15.4, ry = 0.0, sign = 2, rolled = false, sides = true, extra = "tank"),
      Stall(x = -35.8, z = 15.4, ry = 0.0, sign = 4, rolled = true, sides = false, extra = "none")
    )
    set.foreach { stall =>
      ctx.add(makeStall(StallOptions(x = stall.x, y = y, z = stall.z, ry = stall.ry,
        sign = stall.sign, rolled = stall.rolled, sides = stall.sides)))
      ctx.collide(stall.x - 1.3, stall.z - 0.6, stall.x + 1.3, stall.z + 0.6, y + 1.1)
      val frontZ = stall.z + (if stall.ry != 0.0 then -1.1 else 1.1)
      stall.extra match
        case "takoyaki" => buildGriddle(ctx, stall.x + 0.6, stall.z, y, stall.ry)
        case "kakigori" => buildShaver(ctx, stall.x - 0.5, stall.z, y, stall.ry)
        case "prize"    => ctx.add(makePrizeRack(x = stall.x, z = stall.z + 0.9, y = y, ry = stall.ry))
        case "tank" =>
          ctx.add(makeScoopTank(x = stall.x - 0.2, z = frontZ + 0.5, y = y, ry = stall.ry, seed = 9441))
        case _ => ()
      // the crates every stall keeps under and behind its counter
      ctx.add(Props.makeCrates(Props.CratesOpts(
        x = stall.x + (if rng.chance(0.5) then 1.6 else -1.6),
        z = stall.z + (if stall.ry != 0.0 then 0.9 else -0.9),
        y = y,
        n = rng.int(2, 3),
        seed = 9450 + math.round(stall.x).toInt,
        ry = rng.range(-0.3, 0.3)
      )))
    }
  end buildStalls

  /** One folding stall's parameters. `sign` picks the paper sign, `rolled` leaves the canopy on its
    * rail, `sides` hangs the side sheets; `w` and `d` default to the hired 2.6 x 1.1 frame.
    */
  /** Upstream's `g.traverse(n => { if (n.isMesh) ... })`: every mesh under the group, however
    * deeply nested.  A walk over direct children would miss the ten meshes inside the tank's five
    * scoop bundles, which is exactly what the oracle's shadow counts caught.
    */
  private def setShadowFlags(group: Group[Object3DEventMap], cast: Boolean, receive: Option[Boolean]): Unit =
    group.traverse { (node: Object3D[Object3DEventMap]) =>
      if ThreeInterop.isMesh(node) then
        node.castShadow = cast
        receive.foreach(value => node.receiveShadow = value)
    }

  final case class StallOptions(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    sign: Int = 0,
    rolled: Boolean = false,
    sides: Boolean = false,
    w: Double = 2.6,
    d: Double = 1.1
  )

  /** A 屋台 hired from the town association is a steel scaffold frame, a plywood counter, a striped
    * canopy and a paper sign wired to the front rail.  What makes it read as *folding* rather than
    * as a building is that you can see the frame through it: the counter stops short of the ground,
    * the side sheets are cloth and there is nothing behind it at all.
    */
  def makeStall(o: StallOptions): Group[Object3DEventMap] =
    val g        = new Group[Object3DEventMap]()
    val w        = o.w
    val d        = o.d
    val h        = 2.15
    val counterH = 0.9
    val parts = mutable.LinkedHashMap(
      "metal" -> mutable.ArrayBuffer.empty[Util.BakePart],
      "wood"  -> mutable.ArrayBuffer.empty[Util.BakePart],
      "dark"  -> mutable.ArrayBuffer.empty[Util.BakePart]
    )
    def push(key: String, geometry: BufferGeometry[?, ?], matrix: Matrix4): Unit =
      parts(key) += Util.BakePart(geometry, Some(matrix))

    // the frame: four legs, two counter rails, two head rails, and the diagonal brace a scaffold
    // frame always has at one end
    Vector(-1.0, 1.0).foreach { sx =>
      Vector(-1.0, 1.0).foreach { sz =>
        push("metal", new CylinderGeometry(0.032, 0.036, h, 7.0),
          Util.trs(sx * (w / 2.0 - 0.08), h / 2.0, sz * (d / 2.0 - 0.06)))
        push("dark", new BoxGeometry(0.11, 0.03, 0.11),
          Util.trs(sx * (w / 2.0 - 0.08), 0.015, sz * (d / 2.0 - 0.06)))
      }
      push("metal", new CylinderGeometry(0.028, 0.028, d, 6.0),
        Util.trs(sx * (w / 2.0 - 0.08), counterH, 0.0, Pi / 2.0, 0.0, 0.0))
    }
    Vector(counterH, h - 0.06).foreach { railY =>
      Vector(-1.0, 1.0).foreach { sz =>
        push("metal", new CylinderGeometry(0.028, 0.028, w - 0.16, 6.0),
          Util.trs(0.0, railY, sz * (d / 2.0 - 0.06), 0.0, 0.0, Pi / 2.0))
      }
    }
    push("metal", new CylinderGeometry(0.024, 0.024, math.hypot(w - 0.16, h - counterH), 6.0),
      Util.trs(0.0, (counterH + h) / 2.0, -(d / 2.0 - 0.06), 0.0, 0.0,
        Pi / 2.0 - math.atan2(h - counterH, w - 0.16)))

    // the counter: a plywood top with a fascia, and the shelf under it
    push("wood", new BoxGeometry(w, 0.05, d + 0.14), Util.trs(0.0, counterH + 0.025, 0.0))
    push("wood", new BoxGeometry(w + 0.04, 0.3, 0.04), Util.trs(0.0, counterH - 0.15, d / 2.0 + 0.06))
    push("wood", new BoxGeometry(w - 0.2, 0.04, d - 0.2), Util.trs(0.0, 0.38, 0.0))

    // the canopy: either out over the counter, or rolled up on its rail
    if o.rolled then
      val roll = Util.cyl(0.14, 0.14, w - 0.1, 10, M.tarp, 0.0, h + 0.06, d / 2.0 - 0.02)
      roll.rotation.z = Pi / 2.0
      roll.castShadow = true
      ThreeInterop.addChild(g, roll)
      Vector(-1.0, 1.0).foreach { sx =>
        ThreeInterop.addChild(g,
          Util.box(0.05, 0.06, 0.2, M.metalDark, sx * (w / 2.0 - 0.2), h + 0.06, d / 2.0 - 0.02))
      }
      // and the two ties hanging off it
      Vector(-0.5, 0.4).foreach { tieX =>
        ThreeInterop.addChild(g,
          Util.box(0.03, 0.34, 0.02,
            Toon.cel(CelOptions(color = Pal.redDeep, bands = 2, tint = 0x7a4060)),
            tieX, h - 0.11, d / 2.0 + 0.04))
      }
    else
      val out     = 1.0
      val drop    = 0.3
      val stripes = math.max(6, math.round(w / 0.4).toInt)
      val a       = mutable.ArrayBuffer.empty[Util.BakePart]
      val b       = mutable.ArrayBuffer.empty[Util.BakePart]
      val sw      = w / stripes
      (0 until stripes).foreach { i =>
        val matrix = Util.trs(-w / 2.0 + sw * (i + 0.5), 0.0, out / 2.0)
        (if i % 2 == 0 then a else b) += Util.BakePart(new BoxGeometry(sw, 0.05, out), Some(matrix))
      }
      val stripeGroup = new Group[Object3DEventMap]()
      stripeGroup.position.set(0.0, h + 0.02, d / 2.0)
      stripeGroup.rotation.x = math.atan2(drop, out)
      val meshA = new Mesh(Util.bake(a.toList), M.red)
      val meshB = new Mesh(Util.bake(b.toList), M.cream)
      meshA.castShadow = true
      meshB.castShadow = true
      ThreeInterop.addChild(stripeGroup, meshA)
      ThreeInterop.addChild(stripeGroup, meshB)
      ThreeInterop.addChild(g, stripeGroup)
      Outline.hullOutline(meshA, OutlineOptions(thickness = 0.003))
      // the roof back over the counter, and the two front stays
      push("metal", new CylinderGeometry(0.026, 0.026, w - 0.1, 6.0),
        Util.trs(0.0, h + 0.02, -d / 2.0 + 0.08, 0.0, 0.0, Pi / 2.0))
      Vector(-1.0, 1.0).foreach { sx =>
        val stay = Util.box(0.05, 0.05, out, M.metalDark, sx * (w / 2.0 - 0.14), h - 0.12,
          d / 2.0 + out / 2.0)
        stay.rotation.x = math.atan2(drop, out)
        ThreeInterop.addChild(g, stay)
      }
    end if

    // the paper sign on the front rail, and the two clips holding it
    locally {
      val sign = new Mesh(new PlaneGeometry(w - 0.4, 0.34),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.stallSign(o.sign)), side = 2,
          cache = false)))
      sign.position.set(0.0, h - 0.3, d / 2.0 + 0.07)
      sign.castShadow = true
      ThreeInterop.addChild(g, sign)
      Vector(-1.0, 1.0).foreach { sx =>
        ThreeInterop.addChild(g,
          Util.box(0.05, 0.04, 0.03, M.metalDark, sx * (w / 2.0 - 0.26), h - 0.13, d / 2.0 + 0.06))
      }
    }

    // the side sheets, if this one has been sheeted yet
    if o.sides then
      Vector(-1.0, 1.0).foreach { sx =>
        val sheet = new Mesh(new PlaneGeometry(d + 0.1, h - counterH - 0.12),
          Toon.cel(CelOptions(color = 0xe6dcc4, bands = 2, side = 2, tint = 0x6f6790)))
        sheet.position.set(sx * (w / 2.0 - 0.07), (counterH + h) / 2.0 - 0.02, 0.0)
        sheet.rotation.y = Pi / 2.0
        sheet.castShadow = true
        ThreeInterop.addChild(g, sheet)
      }

    val matFor: Map[String, Material[?]] =
      Map("metal" -> M.metal, "wood" -> M.wood, "dark" -> M.dark)
    parts.foreach { (key, bucket) =>
      if bucket.nonEmpty then
        val mesh = new Mesh(Util.bake(bucket.toList), matFor(key))
        mesh.castShadow = true
        mesh.receiveShadow = true
        ThreeInterop.addChild(g, mesh)
    }
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
  end makeStall

  /** The empty たこ焼き griddle: a plate, its burner box and the pick jar. */
  private def buildGriddle(ctx: WorldCtx, x: Double, z: Double, y: Double, ry: Double): Unit =
    val g = new Group[Object3DEventMap]()
    ThreeInterop.addChild(g, Util.box(0.62, 0.16, 0.44, M.metalDark, 0.0, 0.08, 0.0))
    ThreeInterop.addChild(g,
      Util.box(0.56, 0.05, 0.4,
        Toon.cel(CelOptions(color = 0x4a4552, bands = 2, tint = 0x413c58)), 0.0, 0.18, 0.0))
    // the dimples, as a grid of shallow dark discs -- empty, which is the point
    val holes = mutable.ArrayBuffer.empty[Util.BakePart]
    (0 until 4).foreach { i =>
      (0 until 3).foreach { k =>
        holes += Util.BakePart(new CylinderGeometry(0.035, 0.035, 0.02, 8.0),
          Some(Util.trs(-0.21 + i * 0.14, 0.2, -0.11 + k * 0.11)))
      }
    }
    ThreeInterop.addChild(g,
      new Mesh(Util.bake(holes.toList),
        Toon.cel(CelOptions(color = 0x33303c, bands = 2, tint = 0x413c58))))
    ThreeInterop.addChild(g, Util.cyl(0.05, 0.05, 0.14, 8, M.metal, 0.36, 0.28, 0.1))
    (0 until 4).foreach { i =>
      ThreeInterop.addChild(g,
        Util.cyl(0.006, 0.006, 0.2, 4, M.wood, 0.34 + (i % 2) * 0.03, 0.42,
          0.08 + (i / 2) * 0.03))
    }
    setShadowFlags(g, cast = true, receive = None)
    g.position.set(x, y + 0.95, z)
    g.rotation.y = ry
    ctx.add(g)
  end buildGriddle

  /** The かき氷 shaver: a column, a bowl clamp and a hand crank, and no ice. */
  private def buildShaver(ctx: WorldCtx, x: Double, z: Double, y: Double, ry: Double): Unit =
    val g = new Group[Object3DEventMap]()
    ThreeInterop.addChild(g, Util.cyl(0.15, 0.17, 0.05, 12, M.metalDark, 0.0, 0.025, 0.0))
    val column = Util.box(0.16, 0.52, 0.16,
      Toon.cel(CelOptions(color = 0xd8d2c0, bands = 3, tint = 0x6a6288)), 0.0, 0.31, -0.04)
    column.castShadow = true
    ThreeInterop.addChild(g, column)
    ThreeInterop.addChild(g, Util.cyl(0.12, 0.12, 0.1, 12, M.metal, 0.0, 0.62, -0.04))
    ThreeInterop.addChild(g,
      Util.cyl(0.02, 0.02, 0.22, 6, M.metal, 0.14, 0.66, -0.04)
        .rotateZ(Pi / 2.0))
    ThreeInterop.addChild(g, Util.cyl(0.025, 0.025, 0.06, 8, M.wood, 0.26, 0.66, -0.04))
    val bowl = new Mesh(new CylinderGeometry(0.1, 0.07, 0.06, 12.0),
      Toon.cel(CelOptions(color = 0xe8e2ee, bands = 2, tint = 0x6f6790)))
    bowl.position.set(0.0, 0.09, 0.1)
    ThreeInterop.addChild(g, bowl)
    // the two syrup bottles beside it, still capped
    Vector((0.24, 0xe0453f), (0.34, 0x5aa578)).foreach { (dx, color) =>
      ThreeInterop.addChild(g,
        Util.cyl(0.032, 0.036, 0.2, 8, Toon.cel(CelOptions(color = color, bands = 2, tint = 0x6f6790)),
          dx, 0.1, 0.14))
      ThreeInterop.addChild(g, Util.cyl(0.02, 0.02, 0.04, 8, M.cream, dx, 0.22, 0.14))
    }
    setShadowFlags(g, cast = true, receive = None)
    g.position.set(x, y + 0.95, z)
    g.rotation.y = ry
    ctx.add(g)
  end buildShaver

  /** The prize rack, three tiers and nothing on it yet. */
  private def makePrizeRack(x: Double, z: Double, y: Double, ry: Double): Group[Object3DEventMap] =
    val g = new Group[Object3DEventMap]()
    val w = 1.2
    val d = 0.36
    Vector(-1.0, 1.0).foreach { sx =>
      Vector(-1.0, 1.0).foreach { sz =>
        ThreeInterop.addChild(g,
          Util.cyl(0.018, 0.02, 1.28, 6, M.metal, sx * (w / 2.0 - 0.04), 0.64, sz * (d / 2.0 - 0.04)))
      }
    }
    Vector(0.34, 0.76, 1.18).foreach { shelfY =>
      ThreeInterop.addChild(g,
        Util.box(w, 0.03, d,
          Toon.cel(CelOptions(color = 0xd8cdb4, bands = 3, tint = 0x6f6790)), 0.0, shelfY, 0.0))
      Vector(-1.0, 1.0).foreach { sz =>
        ThreeInterop.addChild(g,
          Util.box(w, 0.03, 0.025, M.metal, 0.0, shelfY + 0.05,
            sz * (d / 2.0 - 0.03)))
      }
    }
    // one empty cardboard box on the bottom shelf, flaps open
    ThreeInterop.addChild(g,
      Util.box(0.4, 0.2, 0.28,
        Toon.cel(CelOptions(color = 0xc9a878, bands = 3, tint = 0x6f6790)), -0.3, 0.46, 0.0))
    val flap = Util.box(0.4, 0.02, 0.16,
      Toon.cel(CelOptions(color = 0xb08f62, bands = 3, tint = 0x6a6288)), -0.3, 0.56, 0.2)
    flap.rotation.x = -0.8
    ThreeInterop.addChild(g, flap)
    setShadowFlags(g, cast = true, receive = Some(true))
    g.position.set(x, y, z)
    g.rotation.y = ry
    g
  end makePrizeRack

  /** The 金魚すくい tank: up, levelled, and dry. */
  private def makeScoopTank(x: Double, z: Double, y: Double, ry: Double, seed: Int): Group[Object3DEventMap] =
    val rng = Util.rngKit(seed)
    val g   = new Group[Object3DEventMap]()
    val w   = 1.7
    val d   = 1.1
    val h   = 0.34
    // the trestles under it
    Vector(-1.0, 1.0).foreach { sx =>
      Vector(-1.0, 1.0).foreach { sz =>
        ThreeInterop.addChild(g,
          Util.box(0.07, 0.42, 0.07, M.wood, sx * (w / 2.0 - 0.16), 0.21, sz * (d / 2.0 - 0.14)))
      }
      ThreeInterop.addChild(g, Util.box(0.05, 0.05, d - 0.24, M.wood, sx * (w / 2.0 - 0.16), 0.36, 0.0))
    }
    // the tank: a shallow blue box, its rim, and the dry floor with one puddle
    val shell = Toon.cel(CelOptions(color = 0x4a7fae, bands = 3, tint = 0x4a4a92))
    Vector(-1.0, 1.0).foreach { sz =>
      ThreeInterop.addChild(g, Util.box(w, h, 0.05, shell, 0.0, 0.42 + h / 2.0, sz * (d / 2.0 - 0.025)))
    }
    Vector(-1.0, 1.0).foreach { sx =>
      ThreeInterop.addChild(g, Util.box(0.05, h, d, shell, sx * (w / 2.0 - 0.025), 0.42 + h / 2.0, 0.0))
    }
    ThreeInterop.addChild(g,
      Util.box(w, 0.05, d,
        Toon.cel(CelOptions(color = 0x3f6f9c, bands = 3, tint = 0x4a4a92)), 0.0, 0.44, 0.0))
    Vector(-1.0, 1.0).foreach { sz =>
      ThreeInterop.addChild(g, Util.box(w + 0.06, 0.04, 0.09, M.metal, 0.0, 0.42 + h, sz * (d / 2.0 - 0.02)))
    }
    val wet = new Mesh(new PlaneGeometry(0.5, 0.3),
      Toon.flat(FlatOptions(color = 0x8fb4c8, transparent = true, opacity = 0.5,
        depthWrite = Some(false), cache = false)))
    wet.rotation.x = -Pi / 2.0
    wet.position.set(0.2, 0.47, -0.1)
    ThreeInterop.setUserDataFlag(wet, "noOutline")
    ThreeInterop.addChild(g, wet)
    // the scoops, still bundled, and the bowl stack beside them
    (0 until 5).foreach { i =>
      val scoop = new Group[Object3DEventMap]()
      ThreeInterop.addChild(scoop, Util.cyl(0.008, 0.008, 0.24, 4, M.wood, 0.0, 0.12, 0.0))
      val ring = new Mesh(new TorusGeometry(0.045, 0.006, 4.0, 10.0), M.metal)
      ring.rotation.x = Pi / 2.0
      ring.position.y = 0.26
      ThreeInterop.addChild(scoop, ring)
      scoop.position.set(-w / 2.0 + 0.22 + (i % 3) * 0.04, 0.47, 0.22 + (i / 3) * 0.06)
      scoop.rotation.set(rng.range(0.9, 1.15), rng.range(0.0, 3.0), 0.0)
      ThreeInterop.addChild(g, scoop)
    }
    (0 until 4).foreach { i =>
      ThreeInterop.addChild(g,
        Util.cyl(0.09, 0.07, 0.045, 12,
          Toon.cel(CelOptions(color = 0xe8e2ee, bands = 2, tint = 0x6f6790)),
          w / 2.0 - 0.3, 0.5 + i * 0.04, -0.2))
    }
    setShadowFlags(g, cast = true, receive = Some(true))
    g.position.set(x, y, z)
    g.rotation.y = ry
    g
  end makeScoopTank

  /** The 太鼓台. */
  private def buildStage(ctx: WorldCtx, y: Double): Stage =
    val g = new Group[Object3DEventMap]()
    g.name = "matsuriStage"
    ctx.add(g)

    val cx = -42.4
    val cz = 19.4
    val w  = 3.6
    val h  = 0.82

    /* The deck: a boarded platform on a frame, and it is *walkable* -- the only thing on this
     * ground the player can get up onto, which is what makes it the ground's centre rather than
     * just its biggest prop.
     */
    locally {
      val legs = mutable.ArrayBuffer.empty[Util.BakePart]
      Vector(-1.0, 1.0).foreach { sx =>
        Vector(-1.0, 1.0).foreach { sz =>
          legs += Util.BakePart(new BoxGeometry(0.14, h, 0.14),
            Some(Util.trs(cx + sx * (w / 2.0 - 0.14), y + h / 2.0, cz + sz * (w / 2.0 - 0.14))))
        }
      }
      Vector(-1.0, 1.0).foreach { sz =>
        legs += Util.BakePart(new BoxGeometry(w - 0.2, 0.12, 0.12),
          Some(Util.trs(cx, y + h - 0.14, cz + sz * (w / 2.0 - 0.14))))
      }
      val legMesh = new Mesh(Util.bake(legs.toList), M.woodDark)
      legMesh.castShadow = true
      legMesh.receiveShadow = true
      ThreeInterop.addChild(g, legMesh)

      val boards = mutable.ArrayBuffer.empty[Util.BakePart]
      val n      = math.round(w / 0.3).toInt
      (0 until n).foreach { i =>
        boards += Util.BakePart(new BoxGeometry(w, 0.07, w / n - 0.02),
          Some(Util.trs(cx, y + h + 0.035, cz - w / 2.0 + (w / n) * (i + 0.5))))
      }
      val boardMesh = new Mesh(Util.bake(boards.toList), M.wood)
      boardMesh.castShadow = true
      boardMesh.receiveShadow = true
      ThreeInterop.addChild(g, boardMesh)
      Outline.hullOutline(boardMesh, OutlineOptions(thickness = 0.0032))
      ctx.platform(cx - w / 2.0, cx + w / 2.0, cz - w / 2.0, cz + w / 2.0, y + h + 0.07)
    }

    // the steps up the east side, and the rail round the other three
    Ground.steps(ctx,
      Ground.StepsOptions(x = cx + w / 2.0 + 0.55, z = cz, axis = "x", dir = -1,
        n = 3, rise = (h + 0.07) / 3.0, run = 0.34, w = 1.2, y = y, mat = Some(M.woodDark),
        lipMat = Some(M.wood)))
    locally {
      val rails = mutable.ArrayBuffer.empty[Util.BakePart]
      val railH = 0.9
      val posts = Vector(
        (cx - w / 2.0 + 0.1, cz - w / 2.0 + 0.1), (cx + w / 2.0 - 0.1, cz - w / 2.0 + 0.1),
        (cx - w / 2.0 + 0.1, cz + w / 2.0 - 0.1), (cx + w / 2.0 - 0.1, cz + w / 2.0 - 0.1), (cx - w / 2.0 + 0.1, cz),
        (cx, cz - w / 2.0 + 0.1), (cx, cz + w / 2.0 - 0.1)
      )
      posts.foreach { (px, pz) =>
        rails += Util.BakePart(new BoxGeometry(0.1, railH + 1.7, 0.1),
          Some(Util.trs(px, y + h + 0.07 + (railH + 1.7) / 2.0, pz)))
      }
      // the top rail on three sides, and the frame the lanterns hang from
      Vector(
        (cx - w / 2.0 + 0.1, cz, w - 0.2, 0.0), (cx, cz - w / 2.0 + 0.1, w - 0.2, Pi / 2.0),
        (cx, cz + w / 2.0 - 0.1, w - 0.2, Pi / 2.0)
      ).foreach { (ax, az, len, ry) =>
        Vector(y + h + 0.07 + railH, y + h + 0.07 + 0.44).foreach { railY =>
          rails += Util.BakePart(new BoxGeometry(0.08, 0.08, len),
            Some(Util.trs(ax, railY, az, 0.0, ry, 0.0)))
        }
        rails += Util.BakePart(new BoxGeometry(0.1, 0.1, len),
          Some(Util.trs(ax, y + h + 0.07 + railH + 1.7, az, 0.0, ry, 0.0)))
      }
      rails += Util.BakePart(new BoxGeometry(0.1, 0.1, w - 0.2),
        Some(Util.trs(cx, y + h + 0.07 + railH + 1.7, cz + w / 2.0 - 0.1, 0.0, 0.0, Pi / 2.0)))
      val railMesh = new Mesh(Util.bake(rails.toList), M.woodDark)
      railMesh.castShadow = true
      ThreeInterop.addChild(g, railMesh)
      posts.foreach { (px, pz) =>
        ctx.collide(px - 0.1, pz - 0.1, px + 0.1, pz + 0.1, y + h + 0.07 + railH + 1.8)
      }
    }

    /* The 紅白 cloth wrapped round the deck fascia -- the one saturated thing on the ground, and
     * the reason the stage reads from the far side of it.
     */
    locally {
      val strips = Vector(mutable.ArrayBuffer.empty[Util.BakePart], mutable.ArrayBuffer.empty[Util.BakePart])
      val n      = 12
      Vector(-1.0, 1.0).foreach { sz =>
        (0 until n).foreach { i =>
          strips(i % 2) += Util.BakePart(new BoxGeometry(w / n, 0.34, 0.05),
            Some(Util.trs(cx - w / 2.0 + (w / n) * (i + 0.5), y + h - 0.14, cz + sz * (w / 2.0 + 0.02))))
        }
      }
      (0 until n).foreach { i =>
        strips(i % 2) += Util.BakePart(new BoxGeometry(0.05, 0.34, w / n),
          Some(Util.trs(cx - w / 2.0 - 0.02, y + h - 0.14, cz - w / 2.0 + (w / n) * (i + 0.5))))
      }
      ThreeInterop.addChild(g, new Mesh(Util.bake(strips(0).toList), M.red))
      ThreeInterop.addChild(g, new Mesh(Util.bake(strips(1).toList), M.cream))
    }

    // the drum, on its stand, centre deck
    locally {
      val dy = y + h + 0.07
      val body = new Mesh(new CylinderGeometry(0.42, 0.42, 0.5, 16.0),
        Toon.cel(CelOptions(color = 0x9c5a3c, bands = 3, tint = 0x6f5680)))
      body.rotation.z = Pi / 2.0
      body.position.set(cx, dy + 0.72, cz)
      body.castShadow = true
      body.receiveShadow = true
      ThreeInterop.addChild(g, body)
      Outline.hullOutline(body, OutlineOptions(thickness = 0.0034))
      Vector(-1.0, 1.0).foreach { sx =>
        val head = new Mesh(new CylinderGeometry(0.44, 0.44, 0.03, 16.0),
          Toon.cel(CelOptions(color = 0xe8dcc0, bands = 2, tint = 0x8a7f9c)))
        head.rotation.z = Pi / 2.0
        head.position.set(cx + sx * 0.26, dy + 0.72, cz)
        ThreeInterop.addChild(g, head)
        // the tack ring round each head
        val ring = new Mesh(new TorusGeometry(0.4, 0.014, 4.0, 18.0), M.metalDark)
        ring.rotation.y = Pi / 2.0
        ring.position.set(cx + sx * 0.275, dy + 0.72, cz)
        ThreeInterop.addChild(g, ring)
      }
      // the X stand, and the two sticks laid across the deck
      Vector(-1.0, 1.0).foreach { s =>
        val leg = Util.box(0.07, 0.9, 0.07, M.woodDark, cx + s * 0.3, dy + 0.34, cz)
        leg.rotation.z = s * 0.42
        leg.castShadow = true
        ThreeInterop.addChild(g, leg)
      }
      ThreeInterop.addChild(g, Util.box(0.72, 0.06, 0.06, M.woodDark, cx, dy + 0.5, cz))
      Vector(-0.16, -0.24).foreach { dz =>
        val stick = Util.cyl(0.019, 0.021, 0.42, 6, M.wood, cx + 0.5, dy + 0.03, cz + dz)
        stick.rotation.z = Pi / 2.0
        stick.rotation.y = 0.2
        ThreeInterop.addChild(g, stick)
      }
    }
    Stage(x = cx, z = cz, top = y + 0.82 + 0.07 + 0.9 + 1.7)
  end buildStage

  /** The lanterns and the flags. */
  private def buildLighting(ctx: WorldCtx, y: Double, stage: Stage, rng: Util.RngKit): Unit =
    val lanterns = mutable.ArrayBuffer.empty[(Vector3, Int)]
    val wires    = mutable.ArrayBuffer.empty[BufferGeometry[?, ?]]

    /* Four posts round the ground, and the lantern runs strung between them, the stage frame and
     * the cherry in the middle.  Two lanterns per run at 2.6 m spacing: three read as a ceiling,
     * and a festival ground wants a *canopy*.
     */
    val posts = Vector(
      Endpoint(X0 + 1.2, Z0 + 1.4, 4.2), Endpoint(X0 + 1.2, Z1 - 1.4, 4.2),
      Endpoint(X1 - 2.6, Z0 + 1.4, 4.0), Endpoint(X1 - 2.6, Z1 - 1.4, 4.0)
    )
    posts.foreach { post =>
      val pole = Util.cyl(0.065, 0.08, post.h, 8, M.wood, post.x, y + post.h / 2.0, post.z)
      pole.castShadow = true
      ctx.add(pole)
      Outline.hullOutline(pole, OutlineOptions(thickness = 0.0032))
      ctx.add(Util.cyl(0.18, 0.22, 0.14, 8, M.concreteMid, post.x, y + 0.07, post.z))
      ctx.collide(post.x - 0.18, post.z - 0.18, post.x + 0.18, post.z + 0.18, y + post.h)
      // the two guys and their stakes
      Vector(-1.0, 1.0).foreach { s =>
        val stake = new Vector3(post.x + s * 1.2, y + 0.05, post.z + s * 0.5)
        ctx.add(new Mesh(
          new TubeGeometry(Util.sagCurve(new Vector3(post.x, y + post.h - 0.4, post.z), stake, 0.05, 8),
            8.0, 0.011, 4.0, false),
          M.metalDark
        ))
        ctx.add(Util.cyl(0.018, 0.018, 0.28, 5, M.metalDark, stake.x, y + 0.11, stake.z))
      }
    }

    def at(endpoint: Endpoint): Vector3 =
      new Vector3(endpoint.x, y + endpoint.h - 0.45, endpoint.z)

    val stageFrame = Endpoint(stage.x, stage.z, stage.top - y + 0.1)
    val tree       = Endpoint(Trees(1)._1, Trees(1)._2, 4.6)
    val runs = Vector(
      (posts(0), stageFrame), (posts(1), stageFrame), (posts(2), tree), (posts(3), tree), (stageFrame, tree),
      (posts(0), posts(1)), (posts(2), posts(3))
    )
    runs.foreach { (from, to) =>
      val pointA = at(from)
      val pointB = at(to)
      val dist   = pointA.distanceTo(ThreeInterop.asVector3Like(pointB))
      val sag    = math.min(0.55, dist * 0.045)
      val curve  = Util.sagCurve(pointA, pointB, sag, 12)
      wires += new TubeGeometry(curve, 14.0, 0.015, 4.0, false)
      val n = math.max(2, math.round(dist / 2.6).toInt)
      (1 to n).foreach { i =>
        val point = curve.getPoint(i / (n + 1.0), js.undefined)
        val shade = if (i + math.round(from.x)) % 2 != 0 then 3 else 4
        lanterns += ((point, shade))
      }
    }
    ctx.add(new Mesh(Util.bake(wires.toList.map(geometry => Util.BakePart(geometry, None))), M.wire))
    wires.foreach(_.dispose())
    lanterns.foreach { (point, variant) =>
      ctx.add(Shops.makePaperLantern(Shops.PaperLanternOpts(
        x = point.x, y = point.y - 0.16, z = point.z, variant = variant, r = 0.17, drop = 0.26,
        lit = false
      )))
    }

    /* The flags, on a line along the north edge -- goldfish, a firework and 祭, repeating, which is
     * the rhythm the brief asks for.
     */
    locally {
      val z     = Z0 + 0.5
      val curve = Util.sagCurve(new Vector3(X0 + 1.2, y + 2.9, z), new Vector3(X1 - 2.6, y + 2.9, z), 0.3, 12)
      ctx.add(new Mesh(new TubeGeometry(curve, 16.0, 0.013, 4.0, false), M.wire))
      val n = 11
      (1 to n).foreach { i =>
        val point = curve.getPoint(i / (n + 1.0), js.undefined)
        val flag = new Mesh(new PlaneGeometry(0.3, 0.42),
          Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.matsuriFlag(i % 3)), side = 2,
            cache = false)))
        flag.position.set(point.x, point.y - 0.24, point.z)
        flag.rotation.y = rng.range(-0.24, 0.24)
        flag.rotation.z = rng.range(-0.06, 0.06)
        flag.castShadow = true
        ctx.add(flag)
      }
    }
  end buildLighting

  /** The temporary works: cable, box, rope, cones, tables, benches. */
  private def buildTemporaries(ctx: WorldCtx, y: Double, rng: Util.RngKit): Unit =
    val props = District.propCtx(ctx)

    /* The distribution box, on a pallet, with the plate that tells you not to touch it -- the
     * single most "this is being set up" object on the ground.
     */
    locally {
      val bx = X1 - 3.4
      val bz = Z1 - 1.6
      ctx.add(Util.box(1.0, 0.09, 0.8, M.woodDark, bx, y + 0.045, bz))
      val body = Util.box(0.5, 0.7, 0.34,
        Toon.cel(CelOptions(color = 0xc8ccd4, bands = 3, tint = 0x6a6288)), bx, y + 0.44, bz)
      body.castShadow = true
      body.receiveShadow = true
      ctx.add(body)
      Outline.hullOutline(body, OutlineOptions(thickness = 0.0032))
      ctx.add(Util.box(0.56, 0.05, 0.4, M.metalDark, bx, y + 0.81, bz))
      ctx.add(Util.box(0.3, 0.24, 0.03,
        Toon.cel(CelOptions(color = 0x3a3744, bands = 2)), bx, y + 0.52, bz - 0.18))
      (0 until 3).foreach { i =>
        ctx.add(Util.cyl(0.028, 0.028, 0.06, 8, M.metalDark, bx - 0.14 + i * 0.14, y + 0.24,
          bz - 0.18).rotateX(Pi / 2.0))
      }
      ctx.collide(bx - 0.35, bz - 0.3, bx + 0.35, bz + 0.3, y + 0.85)
      ctx.add(District.signPostWithPlates(x = bx + 0.9, y = y, z = bz - 0.3, ry = -0.5, h = 1.5,
        plates = Vector(District.SignPlate(map = Textures.setupPlate(1), w = 0.4, h = 0.3,
          y = Some(1.24)))))

      /* The cable, snaking from the box out to the stalls.  Laid on the ground and taped at two
       * crossings, which is what an electrician actually does.
       */
      val path = Vector(
        new Vector3(bx - 0.3, y + 0.09, bz),
        new Vector3(bx - 2.2, y + 0.05, bz - 0.6),
        new Vector3(-38.6, y + 0.05, Z1 - 2.2),
        new Vector3(-39.0, y + 0.05, 23.0),
        new Vector3(-43.2, y + 0.05, 22.9)
      )
      val curve = new CatmullRomCurve3(path.toJSArray)
      val tube  = new Mesh(new TubeGeometry(curve, 40.0, 0.026, 5.0, false), M.cable)
      tube.castShadow = true
      ctx.add(tube)
      Vector(0.34, 0.72).foreach { t =>
        val point = curve.getPoint(t, js.undefined)
        val tape = Util.box(0.34, 0.01, 0.24,
          Toon.cel(CelOptions(color = Pal.yellow, bands = 2, tint = 0x8f7050)), point.x, y + 0.065,
          point.z)
        ThreeInterop.setUserDataFlag(tape, "noOutline")
        ctx.add(tape)
      }
      // a second coil, not yet run out
      locally {
        val rings = mutable.ArrayBuffer.empty[Util.BakePart]
        (0 until 4).foreach { i =>
          rings += Util.BakePart(new TorusGeometry(0.3 - i * 0.03, 0.026, 4.0, 14.0),
            Some(Util.trs(bx + 1.5, 0.03 + i * 0.055, bz + 0.9, Pi / 2.0, 0.0, 0.0)))
        }
        val coil = new Mesh(Util.bake(rings.toList), M.cable)
        coil.position.y = y
        coil.castShadow = true
        ctx.add(coil)
      }
    }

    /* The rope on stakes across the entrance apron, and the cones by it. */
    locally {
      val z     = 21.0
      val posts = mutable.ArrayBuffer.empty[Vector3]
      (0 to 3).foreach { i =>
        val px = X1 - 1.0 - i * 1.5
        posts += new Vector3(px, y + 0.9, z)
        val post = Util.cyl(0.03, 0.034, 0.95, 6, M.metal, px, y + 0.475, z)
        post.castShadow = true
        ctx.add(post)
        ctx.add(Util.cyl(0.13, 0.15, 0.06, 8, M.concreteMid, px, y + 0.03, z))
      }
      val ropes = mutable.ArrayBuffer.empty[Util.BakePart]
      (0 until posts.length - 1).foreach { i =>
        ropes += Util.BakePart(
          new TubeGeometry(Util.sagCurve(posts(i), posts(i + 1), 0.09, 8), 8.0, 0.014, 4.0, false),
          None
        )
      }
      ctx.add(new Mesh(Util.bake(ropes.toList),
        Toon.cel(CelOptions(color = 0xefe6d2, bands = 2, tint = 0x8a7f9c))))
      // the 紅白 tags tied along it
      (0 until 6).foreach { i =>
        val tag = Util.box(0.03, 0.2, 0.02, if i % 2 != 0 then M.red else M.cream,
          X1 - 1.4 - i * 0.75, y + 0.78, z)
        tag.rotation.z = rng.range(-0.15, 0.15)
        ctx.add(tag)
      }
      ctx.add(Props.makeCone(Props.ConeOpts(x = X1 - 1.2, z = z + 0.5, y = y, ry = 0.3)))
      ctx.add(Props.makeCone(Props.ConeOpts(x = X1 - 5.6, z = z + 0.4, y = y, ry = -0.5, tilt = 0.05)))
      ctx.add(District.signPostWithPlates(x = X1 - 3.2, y = y, z = z + 0.55, ry = Pi, h = 1.4,
        plates = Vector(District.SignPlate(map = Textures.setupPlate(0), w = 0.42, h = 0.32,
          y = Some(1.16), double = true))))
    }

    // the folding tables: one up, two folded and leaning against the stage
    ctx.add(makeFoldingTable(x = -36.4, z = 20.6, y = y, ry = 0.16, folded = false))
    ctx.add(makeFoldingTable(x = -40.2, z = 17.0, y = y, ry = -0.4, folded = true))
    ctx.add(makeFoldingTable(x = -40.0, z = 17.5, y = y, ry = -0.32, folded = true))

    // benches waiting to be put out, crates of drinks, a broom and a bucket
    ctx.add(Props.makeBench(Props.BenchOpts(x = -34.2, z = 19.6, y = y, ry = -1.5, len = 1.8, back = false)))
    ctx.add(Props.makeBench(Props.BenchOpts(x = -34.2, z = 17.6, y = y, ry = -1.5, len = 1.8, back = false)))
    ctx.add(Props.makeCrates(Props.CratesOpts(x = -37.0, z = 21.4, y = y, n = 4, seed = 9461, ry = 0.2)))
    ctx.add(Props.makeCrates(Props.CratesOpts(x = -36.2, z = 21.6, y = y, n = 3, seed = 9462, ry = -0.3)))
    ctx.add(Props.makeRecycleBox(Props.RecycleBoxOpts(x = -33.4, z = 22.4, y = y, ry = -0.6,
      color = 0x6f8f6a)))
    ctx.add(Props.makeBroom(Props.BroomOpts(x = -32.6, z = 20.6, y = y, tilt = 0.2, roll = 0.12, ry = 1.1)))
    ctx.add(Props.makeBucket(Props.BucketOpts(x = -33.1, z = 20.2, y = y, ry = 0.4, water = true)))
    ctx.add(Props.makePlanter(Props.PlanterOpts(x = X1 - 0.9, z = 16.4, y = y, r = 0.26, flower = true,
      seed = 9463, n = 5)))
    Props.makeLoosePaper(props,
      Vector(
        Props.PaperSpot(x = -35.2, z = 18.4, y = y + 0.07, ry = 0.8),
        Props.PaperSpot(x = -41.6, z = 15.9, y = y + 0.07, ry = -0.5),
        Props.PaperSpot(x = -38.2, z = 24.2, y = y + 0.07, ry = 1.2)
      ))
  end buildTemporaries

  /** A trestle table: either set up, or folded flat and leaning. */
  private def makeFoldingTable(x: Double, z: Double, y: Double, ry: Double, folded: Boolean): Group[Object3DEventMap] =
    val g = new Group[Object3DEventMap]()
    val w = 1.8
    val d = 0.6
    val h = 0.7
    if folded then
      /* Folded, its legs shut against the underside, leaning on its long edge.  The whole read is
       * the *angle* -- a flat panel on the ground is a panel, a panel at seventy degrees is a table
       * somebody has not put out yet.
       */
      val inner = new Group[Object3DEventMap]()
      val top   = Util.box(w, 0.05, d, M.cream, 0.0, 0.0, 0.0)
      top.castShadow = true
      top.receiveShadow = true
      ThreeInterop.addChild(inner, top)
      Vector(-1.0, 1.0).foreach { sx =>
        ThreeInterop.addChild(inner,
          Util.box(0.05, 0.05, d - 0.14, M.metal, sx * (w / 2.0 - 0.2),
            -0.05, 0.0))
        ThreeInterop.addChild(inner,
          Util.box(w - 0.5, 0.04, 0.04, M.metal, 0.0, -0.05,
            sx * (d / 2.0 - 0.12)))
      }
      inner.rotation.x = Pi / 2.0 - 0.28
      inner.position.y = d / 2.0 * math.cos(0.28) + 0.03
      ThreeInterop.addChild(g, inner)
    else
      val top = Util.box(w, 0.05, d, M.cream, 0.0, h, 0.0)
      top.castShadow = true
      top.receiveShadow = true
      ThreeInterop.addChild(g, top)
      Vector(-1.0, 1.0).foreach { sx =>
        Vector(-1.0, 1.0).foreach { sz =>
          ThreeInterop.addChild(g,
            Util.cyl(0.02, 0.024, h, 6, M.metal, sx * (w / 2.0 - 0.16),
              h / 2.0, sz * (d / 2.0 - 0.1)))
        }
        ThreeInterop.addChild(g,
          Util.box(0.04, 0.04, d - 0.2, M.metal, sx * (w / 2.0 - 0.16),
            h * 0.35, 0.0))
      }
      ThreeInterop.addChild(g, Util.box(w - 0.32, 0.04, 0.04, M.metal, 0.0, h - 0.08, 0.0))
      setShadowFlags(g, cast = true, receive = None)
    end if
    g.position.set(x, y, z)
    g.rotation.y = ry
    g
  end makeFoldingTable

end Matsuri
