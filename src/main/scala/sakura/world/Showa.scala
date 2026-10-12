package sakura.world

import sakura.ThreeInterop
import sakura.core.{Outline, Pal, Textures, Toon, Util}
import sakura.core.Outline.OutlineOptions
import sakura.core.Toon.{CelOptions, FlatOptions}
import vendor.three.*

import scala.math

/** 1:1 port of upstream `src/world/showa.js`: the two Showa units on さくら坂商店街 -- the
  * record shop and the electrical shop -- and the props that carry their age: stock right up to
  * the glass, goods out on the pavement, and a chalked board.
  */
object Showa:

  /** The frontage a unit occupies, in world z. */
  final case class Frontage(z0: Double, z1: Double)

  private object M:
    lazy val metal     = Toon.cel(CelOptions(color = Pal.metal, bands = 3, tint = 0x666090))
    lazy val metalDark = Toon.cel(CelOptions(color = Pal.metalDark, bands = 3, tint = 0x5c5680))
    lazy val dark      = Toon.cel(CelOptions(color = Pal.blackSoft, bands = 2, tint = 0x4b4560))
    lazy val wood      = Toon.cel(CelOptions(color = 0x9c7f5e, bands = 3, tint = 0x5c5680))
    lazy val woodDark  = Toon.cel(CelOptions(color = 0x74563f, bands = 3, tint = 0x554e74))
    lazy val card      = Toon.cel(CelOptions(color = 0xc9a878, bands = 3, tint = 0x6f6790))
    lazy val cream     = Toon.cel(CelOptions(color = 0xefe6d2, bands = 3, tint = 0x6f6790))
    lazy val concrete  = Toon.cel(CelOptions(color = Pal.concrete, bands = 3, tint = 0x6f6790))

  def dressShowa(ctx: WorldCtx, x: Double, record: Frontage, denki: Frontage): Unit =
    val edge = x + 0.15
    buildDenki(ctx, x, edge, denki)
    buildRecord(ctx, x, edge, record)

  private def propCtx(ctx: WorldCtx): PropCtx =
    PropCtx(
      add = obj => ctx.add(obj),
      collide = (x0, z0, x1, z1, top) => ctx.collide(x0, z0, x1, z1, top),
      groundAt = (px, pz) => ctx.groundAt(px, pz)
    )

  private def buildDenki(ctx: WorldCtx, fx: Double, edge: Double, u: Frontage): Unit =
    val zc = (u.z0 + u.z1) / 2.0
    val y  = Street.groundY(zc) + 0.13

    ctx.add(makeVertSign(x = fx + 0.22, z = u.z1 - 0.5, y = y + 1.55, kind = Textures.ShopKind.Denki, h = 1.25, ry = 0.0))
    ctx.add(
      Props.makeCrates(Props.CratesOpts(x = edge + 0.36, z = u.z0 + 0.55, y = y, n = 2, seed = 9311, ry = math.Pi / 2.0))
    )
    ctx.add(makeOldTv(x = edge + 0.4, z = u.z0 + 0.55, y = y + 0.56, ry = math.Pi / 2.0 - 0.18))
    ctx.add(makeFan(x = edge + 0.34, z = u.z0 + 1.5, y = y, ry = math.Pi / 2.0 + 0.3))
    ctx.add(Props.makeCrates(
      Props.CratesOpts(x = edge + 0.3, z = u.z1 - 1.5, y = y, n = 1, seed = 9312, ry = math.Pi / 2.0 - 0.1)
    ))
    ctx.add(makeRadio(x = edge + 0.32, z = u.z1 - 1.5, y = y + 0.28, ry = math.Pi / 2.0 + 0.24))
    ctx.add(makeBulbBoxes(x = edge + 0.3, z = u.z0 + 2.2, y = y, ry = math.Pi / 2.0, seed = 9313))
    ctx.add(makeChalkBoard(x = edge + 0.5, z = u.z1 - 0.75, y = y, ry = math.Pi / 2.0 + 0.1, variant = 1))
    ctx.add(Props.makeAircon(Props.AirconOpts(x = fx + 0.24, z = u.z0 + 0.35, y = y + 1.85, ry = math.Pi / 2.0, w = 0.74,
      h = 0.52, feet = false)))
    ctx.add(Props.makeBucket(Props.BucketOpts(x = edge + 0.16, z = u.z0 + 2.9, y = y, ry = -0.7, tilt = 0.08)))
    {
      val posterPane = new Mesh(new PlaneGeometry(0.36, 0.5),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.poster(1)), cache = false)))
      posterPane.position.set(fx + 0.04, y + 1.6, u.z0 + 0.34)
      posterPane.rotation.y = math.Pi / 2.0
      posterPane.rotation.z = 0.02
      ThreeInterop.setUserDataFlag(posterPane, "noOutline")
      ctx.add(posterPane)
    }
    Props.makeLoosePaper(propCtx(ctx), Vector(Props.PaperSpot(x = edge + 0.7, z = u.z0 + 2.7, y = y + 0.01, ry = 0.6)))
    ()
  end buildDenki

  private def buildRecord(ctx: WorldCtx, fx: Double, edge: Double, u: Frontage): Unit =
    val zc = (u.z0 + u.z1) / 2.0
    val y  = Street.groundY(zc) + 0.13

    ctx.add(
      makeVertSign(x = fx + 0.22, z = u.z0 + 0.5, y = y + 1.55, kind = Textures.ShopKind.Record, h = 1.25, ry = 0.0)
    )
    ctx.add(makeLpCrate(x = edge + 0.93, z = u.z1 - 0.9, y = y, ry = math.Pi / 2.0 - 0.12, seed = 9321))
    ctx.add(makeLpCrate(x = edge + 0.93, z = u.z1 - 1.9, y = y, ry = math.Pi / 2.0 + 0.08, seed = 9322))
    ctx.add(makeDisplayCase(x = edge + 0.44, z = u.z0 + 1.1, y = y, ry = math.Pi / 2.0))
    ctx.collide(edge - 0.05, u.z0 + 0.5, edge + 0.7, u.z0 + 1.7, y + 1.3)
    ctx.add(makeChalkBoard(x = edge + 0.5, z = u.z0 + 0.34, y = y, ry = math.Pi / 2.0 - 0.14, variant = 0))
    ctx.add(Props.makePlanter(
      Props.PlanterOpts(x = edge + 0.97, z = u.z1 - 2.6, y = y, r = 0.23, flower = false, seed = 9324, n = 4)
    ))
    ctx.add(Props.makeAircon(
      Props.AirconOpts(x = fx + 0.24, z = u.z1 - 0.35, y = y + 1.9, ry = math.Pi / 2.0, w = 0.7, h = 0.5, feet = false)
    ))
    ctx.add(Props.makeCrates(
      Props.CratesOpts(x = edge + 0.28, z = u.z0 + 2.35, y = y, n = 3, seed = 9325, ry = math.Pi / 2.0 + 0.15)
    ))
    {
      val posterPane = new Mesh(new PlaneGeometry(0.34, 0.48),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.poster(2)), cache = false)))
      posterPane.position.set(fx - 0.03, y + 1.58, u.z0 + 1.98)
      posterPane.rotation.y = math.Pi / 2.0
      posterPane.rotation.z = -0.025
      ThreeInterop.setUserDataFlag(posterPane, "noOutline")
      ctx.add(posterPane)
    }
  end buildRecord

  private def makeVertSign(
    x: Double,
    z: Double,
    y: Double,
    kind: Textures.ShopKind,
    h: Double,
    ry: Double
  ): Group[Object3DEventMap] =
    val g     = new Group()
    val art   = Textures.shopBlade(kind)
    val face  = Toon.flat(FlatOptions(color = 0xfff2d8, map = Some(art), cache = false))
    val side  = Toon.flat(FlatOptions(color = 0x3a3744))
    val board = new Mesh(new BoxGeometry(0.2, h, 0.38), Props.matArray(side, side, side, side, face, face))
    board.castShadow = true
    ThreeInterop.addChild(g, board)
    Outline.hullOutline(board, OutlineOptions(thickness = 0.003))
    ThreeInterop.addChild(g, Util.box(0.26, h + 0.14, 0.46, M.metalDark, -0.04, 0.0, 0.0))
    Vector(h / 2.0 - 0.1, -h / 2.0 + 0.1).foreach { dy =>
      ThreeInterop.addChild(g, Util.box(0.3, 0.05, 0.05, M.metalDark, -0.24, dy, 0.0))
    }
    g.position.set(x, y, z)
    g.rotation.y = ry
    g
  end makeVertSign

  private def makeOldTv(x: Double, z: Double, y: Double, ry: Double): Group[Object3DEventMap] =
    val g       = new Group()
    val w       = 0.5
    val h       = 0.42
    val d       = 0.42
    val caseMat = Toon.cel(CelOptions(color = 0x8a6a4c, bands = 3, tint = 0x5c5680))
    val body    = Util.box(w, h, d, caseMat, 0.0, h / 2.0 + 0.12, 0.0)
    body.castShadow = true
    body.receiveShadow = true
    ThreeInterop.addChild(g, body)
    Outline.hullOutline(body, OutlineOptions(thickness = 0.0034))
    ThreeInterop.addChild(g,
      Util.box(0.4, 0.34, 0.03,
        Toon.cel(CelOptions(color = 0xd8cdb8, bands = 3, tint = 0x6a6288)), -0.04, h / 2.0 + 0.13, d / 2.0 + 0.015))
    ThreeInterop.addChild(g,
      Util.box(0.33, 0.27, 0.02,
        Toon.cel(CelOptions(color = 0x4a4b58, bands = 2, tint = 0x413c58)), -0.04, h / 2.0 + 0.13, d / 2.0 + 0.03))
    val glass = new Mesh(new PlaneGeometry(0.1, 0.24),
      Toon.flat(FlatOptions(color = 0xd8e4ee, transparent = true, opacity = 0.3,
        depthWrite = Some(false), cache = false)))
    glass.position.set(-0.12, h / 2.0 + 0.14, d / 2.0 + 0.042)
    glass.rotation.z = 0.24
    ThreeInterop.setUserDataFlag(glass, "noOutline")
    ThreeInterop.addChild(g, glass)
    (0 until 3).foreach { i =>
      val knob = Util.cyl(0.035, 0.035, 0.03, 10, M.dark, 0.19, h / 2.0 + 0.26 - i * 0.09, d / 2.0 + 0.025)
      knob.rotateX(math.Pi / 2.0)
      ThreeInterop.addChild(g, knob)
    }
    ThreeInterop.addChild(g,
      Util.box(0.09, 0.1, 0.02,
        Toon.cel(CelOptions(color = 0x6a5f52, bands = 2, tint = 0x554e74)), 0.19, h / 2.0 - 0.03, d / 2.0 + 0.02))
    ThreeInterop.addChild(g,
      Util.box(0.16, 0.03, 0.015,
        Toon.flat(FlatOptions(color = 0xd8cdb8)), -0.06, 0.16, d / 2.0 + 0.02))
    Vector(-1.0, 1.0).foreach { sx =>
      Vector(-1.0, 1.0).foreach { sz =>
        val leg = Util.cyl(0.016, 0.022, 0.14, 6, M.woodDark, sx * (w / 2.0 - 0.06), 0.06, sz * (d / 2.0 - 0.06))
        leg.rotation.set(sz * 0.16, 0.0, -sx * 0.16)
        ThreeInterop.addChild(g, leg)
      }
    }
    g.position.set(x, y, z)
    g.rotation.y = ry
    g
  end makeOldTv

  private def makeFan(x: Double, z: Double, y: Double, ry: Double): Group[Object3DEventMap] =
    val g     = new Group()
    val cream = Toon.cel(CelOptions(color = 0xe4dcc8, bands = 3, tint = 0x6f6790))
    ThreeInterop.addChild(g, Util.cyl(0.16, 0.19, 0.05, 12, cream, 0.0, 0.025, 0.0))
    ThreeInterop.addChild(g, Util.cyl(0.05, 0.06, 0.02, 10, M.metalDark, 0.0, 0.06, 0.0))
    val column = Util.cyl(0.026, 0.03, 0.72, 8, M.metal, 0.0, 0.42, 0.0)
    column.castShadow = true
    ThreeInterop.addChild(g, column)
    ThreeInterop.addChild(g, Util.cyl(0.05, 0.05, 0.06, 8, cream, 0.0, 0.78, 0.0))
    val can = Util.cyl(0.075, 0.075, 0.16, 10, cream, 0.0, 0.9, -0.07)
    can.rotation.x = math.Pi / 2.0
    can.castShadow = true
    ThreeInterop.addChild(g, can)
    Vector((0.21, 0.02), (0.2, 0.09)).foreach { case (r, dz) =>
      val ring = new Mesh(new TorusGeometry(r, 0.011, 4, 16), M.metal)
      ring.position.set(0.0, 0.9, dz)
      ThreeInterop.addChild(g, ring)
    }
    (0 until 8).foreach { i =>
      val spoke = Util.box(0.4, 0.012, 0.012, M.metal, 0.0, 0.9, 0.055)
      spoke.rotation.z = (i / 8.0) * math.Pi
      ThreeInterop.addChild(g, spoke)
    }
    (0 until 3).foreach { i =>
      val blade = Util.box(0.3, 0.09, 0.012,
        Toon.cel(CelOptions(color = 0xd8d2c0, bands = 2, tint = 0x6a6288)), 0.0, 0.9, 0.0)
      blade.rotation.z = (i / 3.0) * math.Pi * 2.0
      ThreeInterop.addChild(g, blade)
    }
    g.position.set(x, y, z)
    g.rotation.y = ry
    g
  end makeFan

  private def makeRadio(x: Double, z: Double, y: Double, ry: Double): Group[Object3DEventMap] =
    val g       = new Group()
    val caseMat = Toon.cel(CelOptions(color = 0x7d5f44, bands = 3, tint = 0x5c5680))
    val body    = Util.box(0.34, 0.2, 0.16, caseMat, 0.0, 0.1, 0.0)
    body.castShadow = true
    body.receiveShadow = true
    ThreeInterop.addChild(g, body)
    Outline.hullOutline(body, OutlineOptions(thickness = 0.003))
    ThreeInterop.addChild(g,
      Util.box(0.14, 0.13, 0.01,
        Toon.cel(CelOptions(color = 0x8f7a5e, bands = 2, tint = 0x554e74)), -0.07, 0.11, 0.085))
    ThreeInterop.addChild(g,
      Util.box(0.12, 0.05, 0.01,
        Toon.flat(FlatOptions(color = 0xe8dcbc)), 0.08, 0.15, 0.085))
    Vector(0.05, 0.13).foreach { dx =>
      val knob = Util.cyl(0.024, 0.024, 0.02, 10, M.dark, dx, 0.06, 0.085)
      knob.rotateX(math.Pi / 2.0)
      ThreeInterop.addChild(g, knob)
    }
    val whip = Util.cyl(0.006, 0.006, 0.34, 4, M.metal, 0.15, 0.34, -0.05)
    whip.rotation.z = -0.24
    ThreeInterop.addChild(g, whip)
    g.position.set(x, y, z)
    g.rotation.y = ry
    g
  end makeRadio

  private def makeBulbBoxes(x: Double, z: Double, y: Double, ry: Double, seed: Int): Group[Object3DEventMap] =
    val rng  = Util.rngKit(seed)
    val g    = new Group()
    val cols = Vector(0xdcd2b0, 0xc9bfa0, 0xe2dcc0)
    (0 until 4).foreach { i =>
      val box = Util.box(0.26, 0.14, 0.2,
        Toon.cel(CelOptions(color = cols(i % 3), bands = 3, tint = 0x6f6790)),
        rng.range(-0.03, 0.03), 0.07 + i * 0.14, rng.range(-0.03, 0.03))
      box.rotation.y = rng.range(-0.12, 0.12)
      box.castShadow = true
      box.receiveShadow = true
      ThreeInterop.addChild(g, box)
      ThreeInterop.addChild(g,
        Util.box(0.2, 0.012, 0.14,
          Toon.cel(CelOptions(color = 0x9c8f6e, bands = 2)), box.position.x, 0.142 + i * 0.14, box.position.z))
    }
    Vector(-0.06, 0.05).foreach { dx =>
      val bulb = new Mesh(new SphereGeometry(0.035, 8, 6),
        Toon.flat(FlatOptions(color = 0xf2ecd8, transparent = true, opacity = 0.85, cache = false)))
      bulb.position.set(dx, 0.63, 0.04)
      ThreeInterop.addChild(g, bulb)
      ThreeInterop.addChild(g, Util.cyl(0.017, 0.017, 0.03, 7, M.metal, dx, 0.6, 0.04))
    }
    g.position.set(x, y, z)
    g.rotation.y = ry
    g
  end makeBulbBoxes

  private def makeLpCrate(x: Double, z: Double, y: Double, ry: Double, seed: Int): Group[Object3DEventMap] =
    val rng = Util.rngKit(seed)
    val g   = new Group()
    val w   = 0.66
    val d   = 0.38
    val h   = 0.34
    val leg = 0.42
    Vector(-1.0, 1.0).foreach { sx =>
      Vector(-1.0, 1.0).foreach { sz =>
        ThreeInterop.addChild(g,
          Util.box(0.05, leg, 0.05, M.woodDark,
            sx * (w / 2.0 - 0.05), leg / 2.0, sz * (d / 2.0 - 0.05)))
      }
    }
    ThreeInterop.addChild(g, Util.box(w, 0.04, d, M.woodDark, 0.0, leg, 0.0))
    Vector(-1.0, 1.0).foreach { sz =>
      ThreeInterop.addChild(g, Util.box(w, h, 0.035, M.wood, 0.0, leg + h / 2.0, sz * (d / 2.0 - 0.018)))
    }
    Vector(-1.0, 1.0).foreach { sx =>
      ThreeInterop.addChild(g, Util.box(0.035, h, d, M.wood, sx * (w / 2.0 - 0.018), leg + h / 2.0, 0.0))
    }
    ThreeInterop.addChild(g, Util.box(w - 0.07, 0.03, d - 0.07, M.wood, 0.0, leg + 0.02, 0.0))
    /* Only the crate itself takes shadows: the sleeves are single-sided paper planes and are
     * lit from behind, exactly as upstream leaves them. */
    Util.shadowify(g)
    val n = 9
    (0 until n).foreach { i =>
      val sleeve = new Mesh(new PlaneGeometry(0.31, 0.31),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.sleeveTex(rng.int(0, 3))),
          side = 2, cache = false)))
      sleeve.position.set(0.0, leg + 0.2, -d / 2.0 + 0.07 + i * ((d - 0.16) / (n - 1)))
      sleeve.rotation.x = -0.3 + rng.range(-0.05, 0.05)
      sleeve.castShadow = true
      ThreeInterop.addChild(g, sleeve)
    }
    val card = new Mesh(new PlaneGeometry(0.3, 0.34), Toon.flat(FlatOptions(color = 0xe8dcc0)))
    card.position.set(0.0, leg + 0.23, -d / 2.0 + 0.04)
    card.rotation.x = -0.3
    ThreeInterop.addChild(g, card)
    g.position.set(x, y, z)
    g.rotation.y = ry
    g
  end makeLpCrate

  private def makeDisplayCase(x: Double, z: Double, y: Double, ry: Double): Group[Object3DEventMap] =
    val g     = new Group()
    val w     = 1.0
    val d     = 0.44
    val h     = 0.62
    val leg   = 0.5
    val frame = Toon.cel(CelOptions(color = 0x7a6a58, bands = 3, tint = 0x5c5680))
    Vector(-1.0, 1.0).foreach { sx =>
      Vector(-1.0, 1.0).foreach { sz =>
        ThreeInterop.addChild(g, Util.box(0.06, leg, 0.06, frame, sx * (w / 2.0 - 0.06), leg / 2.0, sz * (d / 2.0 - 0.06)))
      }
    }
    ThreeInterop.addChild(g, Util.box(w, 0.05, d, frame, 0.0, leg, 0.0))
    ThreeInterop.addChild(g, Util.box(w, 0.06, d, frame, 0.0, leg + h, 0.0))
    Vector(-1.0, 1.0).foreach { sx =>
      ThreeInterop.addChild(g, Util.box(0.06, h, d, frame, sx * (w / 2.0 - 0.03), leg + h / 2.0, 0.0))
    }
    Vector(-1.0, 1.0).foreach { sz =>
      ThreeInterop.addChild(g, Util.box(w, 0.05, 0.06, frame, 0.0, leg + h - 0.03, sz * (d / 2.0 - 0.03)))
    }
    Util.shadowify(g)
    ThreeInterop.addChild(g,
      Util.box(w - 0.1, h - 0.1, 0.03,
        Toon.cel(CelOptions(color = 0xcfc2a8, bands = 3, tint = 0x6f6790)), 0.0, leg + h / 2.0, -d / 2.0 + 0.03))
    val pane = Util.box(w - 0.1, h - 0.12, 0.02,
      Toon.flat(FlatOptions(color = 0xdfeaf2, transparent = true, opacity = 0.32,
        depthWrite = Some(false), cache = false)),
      0.0, leg + h / 2.0, d / 2.0 - 0.03)
    ThreeInterop.setUserDataFlag(pane, "noOutline")
    ThreeInterop.setUserDataFlag(pane, "noShadow")
    ThreeInterop.addChild(g, pane)
    val highlight = new Mesh(new PlaneGeometry(0.2, h),
      Toon.flat(FlatOptions(color = 0xf6fbff, transparent = true, opacity = 0.2,
        depthWrite = Some(false), cache = false)))
    highlight.position.set(-0.2, leg + h / 2.0, d / 2.0 - 0.015)
    highlight.rotation.z = 0.3
    ThreeInterop.setUserDataFlag(highlight, "noOutline")
    ThreeInterop.addChild(g, highlight)
    (0 until 3).foreach { i =>
      val sleeve = new Mesh(new PlaneGeometry(0.28, 0.28),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.sleeveTex(i + 1)), cache = false)))
      sleeve.position.set(-0.3 + i * 0.3, leg + 0.22, -0.02)
      sleeve.rotation.x = -0.26
      ThreeInterop.addChild(g, sleeve)
    }
    ThreeInterop.addChild(g, Util.box(w - 0.16, 0.03, 0.03, frame, 0.0, leg + 0.08, 0.06))
    g.position.set(x, y, z)
    g.rotation.y = ry
    g
  end makeDisplayCase

  private def makeChalkBoard(x: Double, z: Double, y: Double, ry: Double, variant: Int): Group[Object3DEventMap] =
    val g   = new Group()
    val art = Textures.chalkNotice(variant)
    val board = new Mesh(new BoxGeometry(0.5, 0.62, 0.05),
      Props.matArray(M.wood, M.wood, M.wood, M.wood,
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(art), cache = false)), M.wood))
    board.position.set(0.0, 0.56, 0.0)
    board.rotation.x = -0.18
    board.castShadow = true
    ThreeInterop.addChild(g, board)
    Outline.hullOutline(board, OutlineOptions(thickness = 0.003))
    Vector(-1.0, 1.0).foreach { s =>
      val leg = Util.box(0.05, 0.72, 0.05, M.wood, s * 0.2, 0.36, -0.13)
      leg.rotation.x = 0.22
      leg.castShadow = true
      ThreeInterop.addChild(g, leg)
    }
    ThreeInterop.addChild(g, Util.box(0.42, 0.04, 0.04, M.wood, 0.0, 0.24, -0.1))
    g.position.set(x, y, z)
    g.rotation.y = ry
    g
  end makeChalkBoard
end Showa
