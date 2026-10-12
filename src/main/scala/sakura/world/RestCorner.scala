package sakura.world

import Ground.{PadOptions, WallOptions}
import sakura.ThreeInterop
import sakura.core.{Outline, Pal, Textures, Util}
import sakura.core.Outline.OutlineOptions
import sakura.core.Toon
import sakura.core.Toon.{CelOptions, FlatOptions}
import vendor.three.*

import scala.collection.mutable

/** 1:1 port of upstream `src/world/restcorner.js`: 自販機の休み処, the vending rest pocket wedged
  * between the blank east flank of 青空商店 and the west flank of the house behind it.
  *
  * Three things make it work as a place to stop rather than a place to pass: it is a slot between
  * two tall walls, so it is half in shadow; the machines face the light so their glazed fronts read
  * as the saturated accent they are meant to be; and the south end stops at the lineside fence, so
  * somebody sitting on the bench is looking at the track.  Everything here is close-range -- it is
  * the one part of the district the player can stand two metres from.
  */
object RestCorner:

  // the pocket
  private val X0    = 12.10 // 青空商店's east flank
  private val X1    = 18.25 // the house's west flank
  private val Z0    = 4.30  // the lineside fence end
  private val Z1    = 13.30 // the alley end
  private val WallX = 17.55 // the low concrete wall the machines stand against

  private object M:
    lazy val concrete    = Toon.cel(CelOptions(color = Pal.concrete, bands = 3, tint = 0x6f6790))
    lazy val concreteMid = Toon.cel(CelOptions(color = Pal.concreteMid, bands = 3, tint = 0x6a6288))
    lazy val metal       = Toon.cel(CelOptions(color = Pal.metal, bands = 3, tint = 0x666090))
    lazy val metalDark   = Toon.cel(CelOptions(color = Pal.metalDark, bands = 3, tint = 0x5c5680))
    lazy val drain       = Toon.cel(CelOptions(color = Pal.drain, bands = 3, tint = 0x5d5878))
    // the canopy is corrugated steel gone chalky, not the crisp pale grey the bridge uses: this
    // corner is meant to look older than everything round it
    lazy val canopy      = Toon.cel(CelOptions(color = 0xbfc4bb, bands = 3, tint = 0x64607f))
    lazy val canopyFrame = Toon.cel(CelOptions(color = 0x8a8f86, bands = 3, tint = 0x5a5678))
    lazy val wood        = Toon.cel(CelOptions(color = 0x9a7f5c, bands = 3, tint = 0x5c5680))
    lazy val woodWorn    = Toon.cel(CelOptions(color = 0x7f6a4e, bands = 3, tint = 0x554e74))

  def buildRestCorner(ctx: WorldCtx): Planting =
    val gm      = Ground.groundMats()
    val rng     = Util.rngKit(8801)
    val sakura  = mutable.ArrayBuffer.empty[SakuraSpot]
    val shrubs  = mutable.ArrayBuffer.empty[ShrubSpot]
    val petals  = mutable.ArrayBuffer.empty[PetalPatch]
    val pocketY = Street.groundY((Z0 + Z1) / 2.0)

    /* the ground: two surfaces, not one -- worn concrete slabs where people stand and a strip of
     * coarser asphalt patch along the west side where the shop's downpipes discharge.  A single
     * flat pad over eight metres reads as a car park.
     */
    Ground.pad(ctx,
      PadOptions(x = (X0 + X1) / 2.0 + 0.1, z = (Z0 + Z1) / 2.0, w = X1 - X0 - 0.2,
        d = Z1 - Z0, y = pocketY, h = 0.08, mat = Some(gm.concrete), name = "restCornerPad"))
    Ground.pad(ctx,
      PadOptions(x = X0 + 0.85, z = (Z0 + Z1) / 2.0 + 0.6, w = 1.5, d = Z1 - Z0 - 2.4,
        y = pocketY, h = 0.09, mat = Some(gm.asphaltWorn), name = "restCornerPatch"))
    // slab joints, so the paving has a grain
    val joints = mutable.ArrayBuffer.empty[Util.BakePart]
    var jointZ = Z0 + 1.2
    while jointZ < Z1 do
      joints += Util.BakePart(new BoxGeometry(X1 - X0 - 0.2, 0.02, 0.05),
        Some(Util.trs((X0 + X1) / 2.0 + 0.1, 0.0, jointZ)))
      jointZ += 1.2
    val jointMesh = new Mesh(Util.bake(joints.toList), M.concreteMid)
    jointMesh.position.y = pocketY + 0.085
    ThreeInterop.setUserDataFlag(jointMesh, "noOutline")
    ctx.add(jointMesh)

    /* the drain: a slotted channel down the middle-west, falling to a gully at the south end.  It
     * explains the fall, gives the ground a dark line, and is where the water stain has somewhere
     * to go.
     */
    val drainX     = X0 + 1.9
    val drainParts = mutable.ArrayBuffer.empty[Util.BakePart]
    val drainCount = math.round((Z1 - Z0 - 1.0) / 0.62).toInt
    (0 until drainCount).foreach { i =>
      drainParts += Util.BakePart(new BoxGeometry(0.26, 0.04, 0.5),
        Some(Util.trs(drainX, 0.0, Z0 + 0.7 + i * 0.62)))
    }
    val channel = new Mesh(Util.bake(drainParts.toList), M.drain)
    channel.position.y = pocketY + 0.075
    channel.receiveShadow = true
    ctx.add(channel)
    // the kerb either side of the channel, and the gully at the bottom
    Vector(-1.0, 1.0).foreach { s =>
      ctx.add(Util.box(0.07, 0.06, Z1 - Z0 - 1.0, M.concreteMid, drainX + s * 0.165,
        pocketY + 0.09, (Z0 + Z1) / 2.0 + 0.35))
    }
    ctx.add(Util.box(0.4, 0.05, 0.4, M.metalDark, drainX, pocketY + 0.075, Z0 + 0.45))
    (0 until 4).foreach { i =>
      ctx.add(Util.box(0.34, 0.03, 0.04, M.drain, drainX, pocketY + 0.095, Z0 + 0.32 + i * 0.09))
    }
    // the water stain: flat, and only just darker -- used, not dirty
    val stain = new Mesh(new CircleGeometry(0.78, 12),
      Toon.flat(FlatOptions(color = 0x9a94a8, transparent = true, opacity = 0.34,
        depthWrite = Some(false), cache = false)))
    stain.rotation.x = -math.Pi / 2.0
    stain.scale.set(1.0, 0.62, 1.0)
    stain.position.set(drainX + 0.1, pocketY + 0.092, Z0 + 0.75)
    ThreeInterop.setUserDataFlag(stain, "noOutline")
    ctx.add(stain)

    /* the low concrete wall: the machines need a back, and the house behind is 0.7 m further east
     * than they are.  1.15 m -- high enough to back a machine, low enough that the house's flank
     * shows over it, which is what gives the corner two planes of wall instead of one.
     */
    Ground.wallRun(ctx,
      WallOptions(axis = "z", at = WallX, from = Z0 + 0.6, to = Z1 - 0.4,
        y = pocketY, h = 1.15, t = 0.2, panel = 2.4, mat = Some(M.concreteMid),
        capMat = Some(M.concrete)))
    ctx.collide(WallX - 0.16, Z0 + 0.6, WallX + 0.16, Z1 - 0.4, pocketY + 1.15)

    /* the vending machines: three, in the order a real bank of them ends up -- the big white one
     * that pays the rent, the red one beside it, and an older teal one squeezed in at the end,
     * facing west into the pocket and into the light.  `addVending` gives each one its collider, its
     * hitbox and the can that drops.
     */
    val machineX = WallX - 0.52
    val machineZ = Vector(Z0 + 2.15, Z0 + 3.35, Z0 + 4.55)
    Vector(0, 1, 2).foreach { i =>
      Vending.addVending(ctx,
        Vending.VendingOptions(x = machineX, z = machineZ(i),
          y = pocketY + 0.08, ry = -math.Pi / 2.0, variant = i, seed = 60 + i))
      /* Contact shadow: the machines stand on four levelling feet with a 30 mm gap under the plinth,
       * and at this cascade size the shadow map cannot resolve that gap -- without a patch on the
       * paving the machine reads as hovering a centimetre off it.
       */
      val patch = new Mesh(new PlaneGeometry(0.8, 1.18),
        Toon.flat(FlatOptions(color = 0x6f6889, transparent = true, opacity = 0.42,
          depthWrite = Some(false), cache = false)))
      patch.rotation.x = -math.Pi / 2.0
      patch.position.set(machineX + 0.05, pocketY + 0.087, machineZ(i))
      ThreeInterop.setUserDataFlag(patch, "noOutline")
      ctx.add(patch)
    }

    /* the canopy: a lean-to over the machines -- four posts, a head beam, purlins, a corrugated
     * sheet falling west, and a gutter with a downpipe at the south end.  The fall and the downpipe
     * are the point: a flat roof with no way for the water to leave is what makes a shelter look
     * drawn rather than built.
     */
    val canopyZA   = Z0 + 1.45
    val canopyZB   = Z0 + 5.30
    val xHigh      = WallX - 0.1
    val xLow       = WallX - 2.55
    val yHigh      = pocketY + 2.72
    val yLow       = pocketY + 2.46
    val frameParts = mutable.ArrayBuffer.empty[Util.BakePart]
    val sheetParts = mutable.ArrayBuffer.empty[Util.BakePart]

    // posts: two tall against the wall, two short out in the pocket
    Vector(canopyZA, canopyZB).foreach { pz =>
      frameParts += Util.BakePart(new CylinderGeometry(0.045, 0.05, yHigh - pocketY - 0.02, 7),
        Some(Util.trs(xHigh, (pocketY + yHigh) / 2.0, pz)))
      frameParts += Util.BakePart(new CylinderGeometry(0.05, 0.055, yLow - pocketY - 0.02, 7),
        Some(Util.trs(xLow, (pocketY + yLow) / 2.0, pz)))
      // base plates, so the posts sit on something
      frameParts += Util.BakePart(new BoxGeometry(0.16, 0.03, 0.16),
        Some(Util.trs(xLow, pocketY + 0.1, pz)))
    }
    // head beams along both edges
    frameParts += Util.BakePart(new BoxGeometry(0.08, 0.1, canopyZB - canopyZA + 0.5),
      Some(Util.trs(xHigh, yHigh, (canopyZA + canopyZB) / 2.0)))
    frameParts += Util.BakePart(new BoxGeometry(0.09, 0.11, canopyZB - canopyZA + 0.5),
      Some(Util.trs(xLow, yLow, (canopyZA + canopyZB) / 2.0)))
    // purlins across the fall
    val runX     = xHigh - xLow
    val dropY    = yHigh - yLow
    val slope    = math.atan2(dropY, runX)
    val sheetLen = math.hypot(runX, dropY) + 0.3
    val purlins  = math.round((canopyZB - canopyZA) / 1.25).toInt
    (0 to purlins).foreach { i =>
      val pz = canopyZA + ((canopyZB - canopyZA) / purlins) * i
      frameParts += Util.BakePart(new BoxGeometry(sheetLen - 0.2, 0.06, 0.06),
        Some(Util.trs((xHigh + xLow) / 2.0, (yHigh + yLow) / 2.0 - 0.06, pz, 0.0, 0.0, slope)))
    }

    /* the sheet, with a real thickness: corrugation as geometry rather than a map, because under
     * cel shading a painted rib flattens out and a modelled one keeps its edge.
     */
    val ribs = math.round((canopyZB - canopyZA + 0.5) / 0.19).toInt
    (0 until ribs).foreach { i =>
      val pz = canopyZA - 0.25 + (i + 0.5) * ((canopyZB - canopyZA + 0.5) / ribs)
      sheetParts += Util.BakePart(
        new BoxGeometry(sheetLen, if i % 2 != 0 then 0.05 else 0.035,
          ((canopyZB - canopyZA + 0.5) / ribs) * 0.86),
        Some(Util.trs((xHigh + xLow) / 2.0, (yHigh + yLow) / 2.0 + 0.03, pz, 0.0, 0.0, slope))
      )
    }

    val frameMesh = new Mesh(Util.bake(frameParts.toList), M.canopyFrame)
    val sheetMesh = new Mesh(Util.bake(sheetParts.toList), M.canopy)
    frameMesh.castShadow = true
    sheetMesh.castShadow = true
    sheetMesh.receiveShadow = true
    /* Upstream writes `ctx.add(fm, sm)`, and its `add` takes one object: the corrugated sheet never
     * enters the scene, so the canopy is the frame alone.  Reproduced deliberately -- the trace, the
     * ink shell below and the render all follow from it.
     */
    ctx.add(frameMesh)
    Outline.hullOutline(sheetMesh, OutlineOptions(thickness = 0.003))

    // gutter along the low edge, and the downpipe that drops into the channel
    ctx.add(Util.box(0.13, 0.09, canopyZB - canopyZA + 0.5, M.metal, xLow - 0.12, yLow - 0.07,
      (canopyZA + canopyZB) / 2.0))
    ctx.add(Util.cyl(0.035, 0.035, yLow - pocketY - 0.15, 6, M.metal, xLow - 0.12,
      (pocketY + yLow) / 2.0 - 0.02, canopyZA - 0.18))
    ctx.add(Util.box(0.16, 0.06, 0.16, M.metalDark, xLow - 0.12, pocketY + 0.13, canopyZA - 0.18))
    Vector(canopyZA, canopyZB).foreach { pz =>
      ctx.collide(xLow - 0.14, pz - 0.12, xLow + 0.1, pz + 0.12, yLow)
    }

    /* the bench: against the shop's flank, facing the machines and the track.  It gets its own worn
     * colour blocks rather than the district's standard bench -- this one has been out here a long
     * time.
     */
    val benchX = X0 + 0.62
    val benchZ = Z0 + 3.4
    val bench  = new Group[Object3DEventMap]()
    // three seat boards with a gap, which is what a slatted bench actually is
    (0 until 3).foreach { i =>
      val seat = Util.box(0.42, 0.05, 1.86, if i == 1 then M.woodWorn else M.wood, 0.0, 0.44, 0.0)
      seat.position.set(i * 0.16 - 0.16, 0.44, 0.0)
      seat.castShadow = true
      seat.receiveShadow = true
      ThreeInterop.addChild(bench, seat)
    }
    // back rail, two boards on cast ends
    (0 until 2).foreach { i =>
      val board = Util.box(0.045, 0.16, 1.86, if i != 0 then M.wood else M.woodWorn, -0.24, 0.66 + i * 0.2, 0.0)
      board.castShadow = true
      ThreeInterop.addChild(bench, board)
    }
    Vector(-1.0, 1.0).foreach { s =>
      val leg = Util.box(0.5, 0.44, 0.07, M.metalDark, 0.0, 0.22, s * 0.82)
      leg.castShadow = true
      ThreeInterop.addChild(bench, leg)
      ThreeInterop.addChild(bench, Util.box(0.06, 0.42, 0.07, M.metalDark, -0.24, 0.6, s * 0.82))
      ThreeInterop.addChild(bench, Util.box(0.56, 0.05, 0.12, M.metalDark, 0.0, 0.03, s * 0.82))
    }
    bench.position.set(benchX, pocketY + 0.08, benchZ)
    ctx.add(bench)
    // upstream outlines `g.children[1]` -- the worn middle seat board
    ThreeInterop.asMesh(bench.children(1)).foreach(mesh =>
      Outline.hullOutline(mesh, OutlineOptions(thickness = 0.003))
    )
    ctx.collide(benchX - 0.3, benchZ - 1.0, benchX + 0.3, benchZ + 1.0, pocketY + 0.5)

    /* the lamp: one bracket lamp on the shop's flank rather than a post -- there is no room for a
     * post, and a bracket is what a back alley gets.  It is off, it is the middle of the afternoon,
     * but the shade and the conduit read.
     */
    val lampX = X0 + 0.14
    val lampZ = Z0 + 6.4
    ctx.add(Util.box(0.1, 0.5, 0.1, M.metalDark, lampX, pocketY + 3.3, lampZ))
    ctx.add(Util.box(0.62, 0.07, 0.07, M.metal, lampX + 0.32, pocketY + 3.52, lampZ))
    val shade = new Mesh(new ConeGeometry(0.28, 0.2, 12, 1, true), M.canopyFrame)
    shade.position.set(lampX + 0.6, pocketY + 3.42, lampZ)
    shade.castShadow = true
    ctx.add(shade)
    ctx.add(Util.cyl(0.2, 0.2, 0.02, 12, Toon.flat(FlatOptions(color = 0xf2ead6)), lampX + 0.6,
      pocketY + 3.31, lampZ))
    // the conduit down the wall to a switch box
    ctx.add(Util.box(0.045, 2.0, 0.045, M.metal, lampX, pocketY + 2.3, lampZ + 0.1))
    ctx.add(Util.box(0.16, 0.22, 0.09, M.metalDark, lampX + 0.05, pocketY + 1.35, lampZ + 0.1))

    /* the shop's flank, dressed: the west side of the pocket is eight metres of blank cream render
     * five and a half metres tall, and that is the largest single surface in the corner.  It gets
     * the outdoor unit, the gas meter and its pipe run, a name plate, two posters nobody has taken
     * down, and the umbrella stand that lives by the back door.
     */
    val flankX = X0 + 0.1
    /* Both units hang on the render, which is at x = 12.02 -- 0.18 west of `flankX`.  On brackets,
     * with the back face 90 mm off the wall, which is what the brackets span.
     */
    val acX = 12.02 + 0.15 + 0.09
    ctx.add(Props.makeAircon(Props.AirconOpts(x = acX, z = Z0 + 8.0, y = pocketY + 1.55,
      ry = math.Pi / 2.0, w = 0.84, h = 0.6, feet = false)))
    ctx.add(Props.makeAircon(Props.AirconOpts(x = acX, z = Z0 + 1.5, y = pocketY + 2.35,
      ry = math.Pi / 2.0, w = 0.7, h = 0.5, feet = false)))
    // the gas meter and its pipe: up the wall, along, and down to the ground
    val meterFace = Toon.flat(FlatOptions(color = 0xd8d4cc))
    val meter = new Mesh(new BoxGeometry(0.1, 0.46, 0.32),
      Props.matArray(meterFace, meterFace, meterFace, meterFace, meterFace,
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.meterBox()), cache = false))))
    meter.position.set(flankX + 0.06, pocketY + 1.15, Z0 + 5.3)
    ctx.add(meter)
    val pipeParts = mutable.ArrayBuffer.empty[Util.BakePart]
    pipeParts += Util.BakePart(new CylinderGeometry(0.03, 0.03, 1.0, 6),
      Some(Util.trs(flankX + 0.06, pocketY + 0.55, Z0 + 5.12)))
    pipeParts += Util.BakePart(new CylinderGeometry(0.03, 0.03, 3.1, 6),
      Some(Util.trs(flankX + 0.06, pocketY + 1.9, Z0 + 5.12, math.Pi / 2.0, 0.0, 0.0)))
    pipeParts += Util.BakePart(new CylinderGeometry(0.03, 0.03, 1.4, 6),
      Some(Util.trs(flankX + 0.06, pocketY + 2.6, Z0 + 6.66)))
    (0 until 5).foreach { i =>
      pipeParts += Util.BakePart(new BoxGeometry(0.07, 0.05, 0.07),
        Some(Util.trs(flankX + 0.08, pocketY + 0.7 + i * 0.42, Z0 + 5.12)))
    }
    // and the shop's two downpipes, which is why there is a drain here at all
    Vector(Z0 + 0.9, Z0 + 7.4).foreach { pz =>
      pipeParts += Util.BakePart(new CylinderGeometry(0.055, 0.055, 5.3, 6),
        Some(Util.trs(flankX + 0.08, pocketY + 2.65, pz)))
      pipeParts += Util.BakePart(new BoxGeometry(0.14, 0.09, 0.14),
        Some(Util.trs(flankX + 0.1, pocketY + 0.18, pz)))
    }
    val pipes = new Mesh(Util.bake(pipeParts.toList), M.metal)
    pipes.castShadow = true
    ctx.add(pipes)

    // paper: a name plate by the back door and two faded posters
    val namePlate = new Mesh(new PlaneGeometry(0.34, 0.17),
      Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.namePlate(1)), cache = false)))
    namePlate.position.set(flankX + 0.03, pocketY + 1.62, Z0 + 6.9)
    namePlate.rotation.y = math.Pi / 2.0
    ctx.add(namePlate)
    Vector((Z0 + 2.5, 1.72, 0, 0.82), (Z0 + 2.98, 1.6, 3, 0.7)).foreach { (pz, py, variant, opacity) =>
      val poster = new Mesh(new PlaneGeometry(0.4, 0.55),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.poster(variant)),
          transparent = true, opacity = opacity, cache = false)))
      poster.position.set(flankX + 0.03, pocketY + py, pz)
      poster.rotation.set(0.0, math.Pi / 2.0, rng.range(-0.03, 0.03))
      ThreeInterop.setUserDataFlag(poster, "noOutline")
      ctx.add(poster)
    }
    ctx.add(Props.makeUmbrellaStand(Props.UmbrellaStandOpts(
      x = flankX + 0.42, z = Z0 + 7.0, y = pocketY + 0.08, n = 3, seed = 8811
    )))
    ctx.add(Props.makePostBox(Props.PostBoxOpts(
      x = flankX + 0.44, z = Z0 + 6.1, y = pocketY + 0.08, ry = math.Pi / 2.0
    )))

    /* the corner's own signage: one invented plate on the low wall, naming the place -- the thing
     * that turns a gap between two buildings into somewhere with a name.
     */
    val art        = Textures.cornerPlate()
    val cornerFace = Toon.flat(FlatOptions(color = 0xffffff, map = Some(art), cache = false))
    val cornerSide = Toon.flat(FlatOptions(color = Pal.wallGray))
    val cornerBoard = new Mesh(new BoxGeometry(0.07, 0.34, 1.15),
      Props.matArray(cornerFace, cornerFace, cornerSide, cornerSide, cornerSide, cornerSide))
    cornerBoard.position.set(WallX - 0.14, pocketY + 1.42, Z1 - 2.1)
    cornerBoard.castShadow = true
    ctx.add(cornerBoard)
    Outline.hullOutline(cornerBoard, OutlineOptions(thickness = 0.003))
    Vector(-1.0, 1.0).foreach { s =>
      ctx.add(Util.box(0.05, 0.34, 0.05, M.metalDark, WallX - 0.11, pocketY + 1.15, Z1 - 2.1 + s * 0.5))
    }

    // the clutter
    ctx.add(Props.makeVendBin(Props.VendBinOpts(x = machineX - 0.05, z = Z0 + 5.55,
      y = pocketY + 0.08, ry = -math.Pi / 2.0, color = Pal.bin)))
    ctx.add(Props.makeCrates(Props.CratesOpts(x = WallX - 0.5, z = Z0 + 7.4, y = pocketY + 0.08,
      n = 3, seed = 8821, ry = 0.16)))
    ctx.add(Props.makeCrates(Props.CratesOpts(x = WallX - 0.55, z = Z0 + 8.2, y = pocketY + 0.08,
      n = 2, seed = 8822, ry = -0.22)))
    ctx.add(Props.makeBucket(Props.BucketOpts(x = X0 + 1.0, z = Z0 + 1.3, y = pocketY + 0.09,
      ry = 0.6, tilt = 0.06)))
    ctx.add(Props.makePlanter(Props.PlanterOpts(x = X0 + 0.7, z = Z0 + 5.2, y = pocketY + 0.08,
      r = 0.24, flower = true, seed = 8823, n = 5)))
    ctx.add(Props.makePlanter(Props.PlanterOpts(x = X0 + 0.66, z = Z0 + 5.85, y = pocketY + 0.08,
      r = 0.19, flower = false, seed = 8824, n = 4)))
    Props.makeLoosePaper(District.propCtx(ctx),
      Vector(
        Props.PaperSpot(x = X0 + 2.6, z = Z0 + 6.8, y = pocketY + 0.1, ry = 0.8),
        Props.PaperSpot(x = X0 + 3.4, z = Z0 + 1.9, y = pocketY + 0.1, ry = -0.5)
      ))

    /* the cherry: at the north end, where the pocket opens into the alley, so it is seen against
     * the sky from inside the corner and it frames the way out.  Its fallen blossom is what the
     * bench is under.
     */
    sakura += SakuraSpot(x = X0 + 2.5, z = Z1 - 1.1, y = pocketY, scale = 1.06, seed = 8831,
      lean = 0.13, leanDir = Some(1.1))
    shrubs += ShrubSpot(x = WallX - 0.7, z = Z1 - 1.4, y = pocketY + 0.08, r = 0.34, count = 3,
      spread = 0.9, seed = 8832)

    /* fallen petals and a scatter of dry leaves, kept to the edges: the middle of the pocket is
     * swept, because somebody sweeps it.
     */
    petals += PetalPatch(x = (X0 + X1) / 2.0, z = Z1 - 2.4, w = 4.6, d = 3.4, y = pocketY + 0.09, n = 90)
    petals += PetalPatch(x = X0 + 1.1, z = Z0 + 2.6, w = 1.6, d = 4.0, y = pocketY + 0.1, n = 45)
    val leafGeometry = new CircleGeometry(0.055, 5)
    val leaves       = mutable.ArrayBuffer.empty[Util.BakePart]
    (0 until 22).foreach { _ =>
      val lx = rng.range(X0 + 0.4, X1 - 0.7)
      val lz = rng.range(Z0 + 0.5, Z1 - 0.5)
      // only near the walls, where they collect
      val edge = math.min(lx - X0, X1 - lx) < 1.4 || math.min(lz - Z0, Z1 - lz) < 1.2
      if edge then
        leaves += Util.BakePart(leafGeometry,
          Some(Util.trs(lx, 0.0, lz, -math.Pi / 2.0, rng.range(0, 3), 0.0)))
    }
    val leafMesh = new Mesh(Util.bake(leaves.toList), Toon.flat(FlatOptions(color = 0xbfa478)))
    leafMesh.position.y = pocketY + 0.093
    ThreeInterop.setUserDataFlag(leafMesh, "noOutline")
    ctx.add(leafMesh)
    leafGeometry.dispose()

    Planting(sakura = sakura.toVector, shrubs = shrubs.toVector, petals = petals.toVector)
  end buildRestCorner

end RestCorner
