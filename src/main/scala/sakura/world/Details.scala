package sakura.world

import District.NoticeSheet
import sakura.ThreeInterop
import sakura.core.{Outline, Pal, Textures, Util}
import sakura.core.Outline.OutlineOptions
import sakura.core.Toon
import sakura.core.Toon.{CelOptions, FlatOptions}
import vendor.three.*

import scala.collection.mutable

/** 1:1 port of upstream `src/world/details.js`: the environmental storytelling pass.
  *
  * Nobody appears anywhere in this world, so everything the place has to say about itself has to be
  * said by what has been left out, pinned up, parked, knocked over or forgotten.  This is the last
  * pass and it is deliberately small: a handful of legible objects, each in a spot the eye already
  * goes to, beats a hundred scattered ones.  The one moving part is the cloth -- a noren, a shop
  * banner and a towel that breathe on a slow cycle.
  */
object Details:

  final case class Result(planting: Planting, cloths: Int)

  /** One hung piece of cloth: the pivot the wind turns, and the parameters of its cycle. */
  final private case class Cloth(obj: Object3D[Object3DEventMap], amp: Double, rate: Double, phase: Double)

  final private case class HangOptions(
    x: Double,
    y: Double,
    z: Double,
    ry: Double,
    w: Double,
    h: Double,
    map: Texture[?, ?],
    amp: Double = 0.09,
    rate: Double = 0.7,
    phase: Double = 0.0,
    rail: Boolean = true
  )

  private object M:
    lazy val metal      = Toon.cel(CelOptions(color = Pal.metal, bands = 3, tint = 0x666090))
    lazy val metalDark  = Toon.cel(CelOptions(color = Pal.metalDark, bands = 3, tint = 0x5c5680))
    lazy val cabinet    = Toon.cel(CelOptions(color = Pal.cabinet, bands = 3, tint = 0x6f6890))
    lazy val cabinetTop = Toon.cel(CelOptions(color = Pal.cabinetTop, bands = 3, tint = 0x6a6288))
    lazy val wood       = Toon.cel(CelOptions(color = 0x9c7f5e, bands = 3, tint = 0x5c5680))

  def buildDetails(ctx: WorldCtx): Result =
    val cloths = mutable.ArrayBuffer.empty[Cloth]

    /* the station: a board full of club recruitment on an unstaffed platform is the single most
     * economical way to say that a school exists up the hill and that term is running.
     */
    val deckH = 0.98  // platform deck height, from railway.js
    val deckZ = -5.62 // back edge of the deck
    ctx.add(District.noticeBoardWithSheets(
      x = 22.4, z = deckZ + 0.42, y = deckH, ry = 0.0, w = 2.4, h = 1.28, y0 = 0.75,
      sheets = Vector(
        NoticeSheet(map = Textures.clubPoster(0), x = -0.78, y = 0.05, w = 0.46, h = 0.62, tilt = 0.02),
        NoticeSheet(map = Textures.clubPoster(1), x = -0.26, y = -0.03, w = 0.46, h = 0.62, tilt = -0.015),
        NoticeSheet(map = Textures.clubPoster(3), x = 0.26, y = 0.04, w = 0.46, h = 0.62, tilt = 0.01),
        NoticeSheet(map = Textures.poster(1), x = 0.78, y = -0.02, w = 0.42, h = 0.56, tilt = -0.02)
      )
    ))
    ctx.collide(21.2, deckZ + 0.2, 23.6, deckZ + 0.7, deckH + 2.1)
    // somebody's umbrella, left against the fence after the rain
    ctx.add(Props.makeUmbrellaStand(Props.UmbrellaStandOpts(
      x = 30.4, z = deckZ + 0.5, y = deckH, n = 2, seed = 9901
    )))
    // and a crate of returns waiting to go somewhere
    ctx.add(Props.makeCrates(Props.CratesOpts(
      x = 34.6, z = deckZ + 0.7, y = deckH, n = 3, seed = 9902, ry = 0.2
    )))
    Props.makeLoosePaper(District.propCtx(ctx),
      Vector(
        Props.PaperSpot(x = 26.8, z = -3.4, y = deckH, ry = 0.7),
        Props.PaperSpot(x = 27.5, z = -3.0, y = deckH, ry = -1.2)
      ))

    /* crows on the wires: the wires over the crossing are the emptiest part of the frame and the
     * one nothing else can occupy.  Three crows, unevenly spaced, because two reads as a pair and
     * four as a flock -- plus the fourth up on the shopping street.
     */
    ctx.add(Props.makeCrow(Props.CrowOpts(
      x = -2.1, y = Street.groundY(9.4) + 8.28, z = 9.4, ry = 0.4
    )))
    ctx.add(Props.makeCrow(Props.CrowOpts(
      x = -1.4, y = Street.groundY(9.4) + 8.24, z = 9.7, ry = -0.9
    )))
    ctx.add(Props.makeCrow(Props.CrowOpts(
      x = 5.6, y = Street.groundY(-19.0) + 7.9, z = -19.0, ry = 2.6
    )))
    ctx.add(Props.makeCrow(Props.CrowOpts(
      x = 21.8, y = Street.groundY(29.0) + 4.86, z = 29.0, ry = -0.5
    )))

    /* lineside kit: relay cabinets and warning plates along the fence, the railway is the reason
     * this town exists and it should look maintained by somebody.  Set back off the near-side
     * footpath, not on it: that path is 1.15 m wide and it is the only way west to the shrine.
     */
    Vector((-16.4, 5.8, math.Pi / 2.0), (17.0, 5.4, -math.Pi / 2.0), (-24.0, -3.9, math.Pi))
      .foreach { (x, z, ry) =>
        val y   = Street.groundY(z)
        val cab = Util.box(0.62, 0.98, 0.4, M.cabinet, x, y + 0.49, z)
        cab.castShadow = true
        cab.receiveShadow = true
        ctx.add(cab)
        Outline.hullOutline(cab, OutlineOptions(thickness = 0.003))
        ctx.add(Util.box(0.7, 0.07, 0.48, M.cabinetTop, x, y + 1.01, z))
        val plate = new Mesh(new PlaneGeometry(0.24, 0.48),
          Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.warningPlate(1)), cache = false)))
        plate.position.set(x + math.sin(ry) * 0.32, y + 0.6, z + math.cos(ry) * 0.32)
        plate.rotation.y = ry
        ctx.add(plate)
        ctx.collide(x - 0.36, z - 0.26, x + 0.36, z + 0.26, y + 1.05)
      }

    /* cloth in the wind: eight pieces, each hung on a real bracket, all driven by one updater.
     * They lift and settle on slightly different periods so the street never looks metronomic.
     *
     * Two nested groups, and the nesting is the whole point.  The pivot is the rail, so a small
     * rotation about it reads as a lift; the outer group owns the placement and is the only thing
     * the planet bake touches, while the inner pivot stays the identity and is what the wind turns.
     * Driving `rotation.x` on the outer group would discard the quarter-turn that faces a hang
     * across the street and roll the cloth ninety degrees about its own normal.
     */
    def hang(o: HangOptions): Group[Object3DEventMap] =
      val g   = new Group()
      val piv = new Group()
      ThreeInterop.addChild(g, piv)
      val cloth = new Mesh(
        new PlaneGeometry(o.w, o.h),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(o.map), side = 2, cache = false))
      )
      cloth.position.y = -o.h / 2.0
      cloth.castShadow = true
      ThreeInterop.addChild(piv, cloth)
      if o.rail then
        ThreeInterop.addChild(g, Util.box(o.w + 0.16, 0.05, 0.05, M.metalDark, 0.0, 0.02, 0.0))
      g.position.set(o.x, o.y, o.z)
      g.rotation.y = o.ry
      ThreeInterop.setUserDataFlag(g, "planetRigid")
      ctx.add(g)
      cloths += Cloth(piv, o.amp, o.rate, o.phase)
      g
    end hang

    // the ramen shop's doorway curtain, on the shopping street
    hang(HangOptions(x = 24.9, y = Street.groundY(19.6) + 2.5, z = 19.6, ry = -math.Pi / 2.0,
      w = 2.2, h = 0.74, map = Textures.norenTex(Textures.NorenKind.Ramen), amp = 0.1, rate = 0.62))
    // a soba banner further up the same side
    hang(HangOptions(x = 24.9, y = Street.groundY(24.8) + 2.4, z = 24.8, ry = -math.Pi / 2.0,
      w = 1.6, h = 0.66, map = Textures.norenTex(Textures.NorenKind.Soba), amp = 0.11, rate = 0.78,
      phase = 1.7))
    // two promotional flags outside the stationery shop
    hang(HangOptions(x = 24.7, y = Street.groundY(32.6) + 2.2, z = 32.6, ry = -math.Pi / 2.0,
      w = 0.44, h = 1.0, map = Textures.flagTex(2), amp = 0.16, rate = 1.1, phase = 0.6))
    hang(HangOptions(x = 19.5, y = Street.groundY(21.2) + 2.2, z = 21.2, ry = math.Pi / 2.0,
      w = 0.44, h = 1.0, map = Textures.flagTex(1), amp = 0.15, rate = 1.24, phase = 2.4))
    // washing over the corner shop's balcony, and on a house up the street
    hang(HangOptions(x = 4.9, y = Street.groundY(9.2) + 4.5, z = 9.2, ry = -math.Pi / 2.0,
      w = 0.46, h = 0.68, map = Textures.curtainTex(0), amp = 0.13, rate = 0.9, phase = 3.1))
    hang(HangOptions(x = -5.6, y = Street.groundY(23.2) + 4.4, z = 22.4, ry = math.Pi / 2.0,
      w = 0.5, h = 0.7, map = Textures.curtainTex(1), amp = 0.12, rate = 0.82, phase = 1.1))
    // the bathhouse curtain
    hang(HangOptions(x = 25.0, y = Street.groundY(39.4) + 2.6, z = 39.4, ry = -math.Pi / 2.0,
      w = 2.6, h = 0.8, map = Textures.norenTex(Textures.NorenKind.Sento), amp = 0.07, rate = 0.5,
      phase = 2.0))
    // and one at the mouth of the alley, so the turn has something moving in it
    hang(HangOptions(x = 13.4, y = Street.groundY(15.1) + 2.5, z = 16.2, ry = 0.0,
      w = 1.3, h = 0.6, map = Textures.norenTex(Textures.NorenKind.Wagashi), amp = 0.12, rate = 0.94,
      phase = 0.3))

    var elapsed = 0.0
    ctx.update { dt =>
      elapsed += dt
      cloths.foreach { cloth =>
        // a slow lift with a small flutter riding on it
        val sway = math.sin(elapsed * cloth.rate + cloth.phase) * 0.75 +
          math.sin(elapsed * cloth.rate * 3.3 + cloth.phase) * 0.25
        cloth.obj.rotation.x = sway * cloth.amp
      }
    }

    /* odds and ends, each in a place the eye already goes */
    // a page off the school notice board, blown across the crossing
    Props.makeLoosePaper(District.propCtx(ctx),
      Vector(
        Props.PaperSpot(x = -1.4, z = 1.9, y = 0.34, ry = 0.9),
        Props.PaperSpot(x = -0.6, z = -2.2, y = 0.34, ry = -0.5),
        Props.PaperSpot(x = 2.2, z = 6.4, y = Street.groundY(6.4), ry = 1.4)
      ))
    // a cat asleep in a box on the shady side of the crossing shop
    ctx.add(Props.makeCatBox(Props.CatBoxOpts(
      x = 4.3, z = 4.0, y = Street.groundY(4.0) + Street.WalkH, ry = -0.8, cat = 0xc7b49c
    )))
    ctx.add(Props.makePetBowl(Props.PetBowlOpts(
      x = 4.9, z = 3.4, y = Street.groundY(3.4) + Street.WalkH, ry = 0.5
    )))
    // a bucket left out at the shrine steps, and a bike dumped at the alley
    ctx.add(Props.makeBucket(Props.BucketOpts(
      x = -26.3, z = 18.9, y = Street.groundY(18.9), ry = 0.4, tilt = 0.05
    )))
    ctx.add(Props.makeBicycle(Props.BicycleOpts(
      x = -26.85, z = 6.4, y = ctx.groundAt(-26.85, 6.4) + 0.04,
      ry = math.Pi / 2.0 + 0.15, lean = 0.08, color = 0x4f8f6a
    )))
    // and the 徐行 plate on the near-side lineside path, facing the walk
    ctx.add(District.signPostWithPlates(
      x = -9.8, y = 0.0, z = 5.5, ry = math.Pi, h = 1.7,
      plates = Vector(District.SignPlate(map = Textures.warningPlate(0), w = 0.3, h = 0.6,
        y = Some(1.35)))
    ))

    Result(Planting(), cloths.length)
  end buildDetails

end Details
