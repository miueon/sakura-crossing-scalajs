package sakura.world

import sakura.ThreeInterop
import sakura.core.{Pal, Textures, Toon, Util}
import vendor.three.*

import scala.collection.mutable
import scala.scalajs.js

/** The railway, ported 1:1 from upstream `src/world/railway.js`.
  *
  * Single track running along X, crossing the street at x = 0, plus the level
  * crossing hardware, lineside fence, overhead catenary and a small station
  * platform off to the east. The track spans exactly one circumference, so once
  * the world is wrapped onto the planet its two ends meet on the equator and
  * the loop closes with no seam.
  */
object Railway:

  val RailGauge: Double = 1.44
  val RailTop: Double   = 0.30

  val XMin: Double     = -Planet.Circumference / 2.0
  val XMax: Double     = Planet.Circumference / 2.0
  val LocalMin: Double = -150.0
  val LocalMax: Double = 150.0

  private val matRail =
    () => Toon.cel(Toon.CelOptions(color = Pal.railMetal, bands = 3, tint = 0x5f5878))
  private val matHead =
    () => Toon.cel(Toon.CelOptions(color = Pal.railHead, bands = 2, tint = 0x6f6890))
  private val matSleeper =
    () => Toon.cel(Toon.CelOptions(color = Pal.sleeper, bands = 3, tint = 0x5d5878))
  private val matBallast =
    () => Toon.cel(Toon.CelOptions(color = Pal.ballast, bands = 3, tint = 0x655d84))
  private val matYellow =
    () => Toon.cel(Toon.CelOptions(color = Pal.gateYellow, bands = 3, tint = 0x8f7050))
  private val matBlack =
    () => Toon.cel(Toon.CelOptions(color = Pal.gateBlack, bands = 2, tint = 0x4b4560))
  private val matMetal =
    () => Toon.cel(Toon.CelOptions(color = Pal.metal, bands = 3, tint = 0x666090))
  private val matMetalDark =
    () => Toon.cel(Toon.CelOptions(color = Pal.metalDark, bands = 3, tint = 0x5c5680))
  private val matCabinet =
    () => Toon.cel(Toon.CelOptions(color = Pal.cabinet, bands = 3, tint = 0x6f6890))
  private val matConcrete =
    () => Toon.cel(Toon.CelOptions(color = Pal.concrete, bands = 3, tint = 0x6f6790))

  /* The two hill bores (from upstream hills.js): the lineside fence, masking
   * walls and catenary masts all take the union over them, so every run is cut
   * by every bore and by every maintenance gate. */
  final private case class Tunnel(x0: Double, x1: Double, walk: Double, gateX: Double)

  private val Tunnels = Vector(
    Tunnel(x0 = -132.0, x1 = -96.0, walk = -1.0, gateX = -84.5),
    Tunnel(x0 = 108.0, x1 = 138.0, walk = 1.0, gateX = 85.0)
  )

  final case class CrossingArm(pivot: Group[Object3DEventMap])

  final case class CrossingApi(
    group: Group[Object3DEventMap],
    arms: Vector[CrossingArm],
    var active: Boolean,
    var armT: Double,
    setLamps: (Boolean, Double) => Unit,
    setArms: Double => Unit,
    var request: () => Unit = () => ()
  )

  def buildRailway(ctx: WorldCtx): CrossingApi =
    val g = new Group()
    g.name = "railway"
    ctx.add(g)

    /* ------------------------------ ballast bed ------------------------------ */
    val shape = new Shape()
    shape.moveTo(-Street.TrackHalf - 0.9, 0.0)
    shape.lineTo(-Street.TrackHalf, 0.26)
    shape.lineTo(Street.TrackHalf, 0.26)
    shape.lineTo(Street.TrackHalf + 0.9, 0.0)
    shape.closePath()
    val ballastGeometry = new ExtrudeGeometry(shape,
      ExtrudeGeometryOptions(depth = Some(js.defined(XMax - XMin)), bevelEnabled = Some(js.defined(false))))
    ballastGeometry.rotateY(math.Pi / 2.0)
    ballastGeometry.translate(XMin, 0.0, 0.0)
    val ballast = new Mesh(ballastGeometry, matBallast())
    ballast.receiveShadow = true
    ballast.name = "ballast"
    ThreeInterop.addChild(g, ballast)

    /* -------------------------------- sleepers ------------------------------- */
    val sleeperGeometry = new BoxGeometry(0.24, 0.16, 2.5)
    val sleeperCount    = math.floor((XMax - XMin) / 0.62).toInt
    val sleepers        = new InstancedMesh(sleeperGeometry, matSleeper(), sleeperCount.toDouble)
    val sleeperDummy    = new Object3D()
    var sleeperIndex    = 0
    var x               = XMin
    while x < XMax do
      if !(math.abs(x) < Street.RoadHalf + Street.WalkW + 0.6) then
        sleeperDummy.position.set(x, 0.32, 0.0)
        sleeperDummy.rotation.set(0.0, 0.0, 0.0)
        sleeperDummy.updateMatrix()
        sleepers.setMatrixAt(sleeperIndex.toDouble, sleeperDummy.matrix)
        sleeperIndex += 1
      x += 0.62
    sleepers.count = sleeperIndex.toDouble
    sleepers.receiveShadow = true
    sleepers.castShadow = true
    ThreeInterop.addChild(g, sleepers)

    /* --------------------------------- rails ---------------------------------
     * Dark steel web with a thin polished strip on the running surface. */
    val railLength = XMax - XMin
    val webGeometry = Util.bake(List(
      Util.BakePart(new BoxGeometry(railLength, 0.055, 0.115), Some(Util.trs(0.0, 0.392, 0.0))),
      Util.BakePart(new BoxGeometry(railLength, 0.10, 0.05), Some(Util.trs(0.0, 0.325, 0.0))),
      Util.BakePart(new BoxGeometry(railLength, 0.03, 0.17), Some(Util.trs(0.0, 0.27, 0.0)))
    ))
    Vector(-1.0, 1.0).foreach { s =>
      val rail = new Mesh(webGeometry.clone(), matRail())
      rail.position.set((XMin + XMax) / 2.0, 0.0, (s * RailGauge) / 2.0)
      rail.castShadow = true
      rail.receiveShadow = true
      rail.name = "rail"
      ThreeInterop.addChild(g, rail)
      val head = Util.box(railLength, 0.016, 0.09, matHead(),
        (XMin + XMax) / 2.0, 0.424, (s * RailGauge) / 2.0)
      ThreeInterop.setUserDataFlag(head, "noOutline")
      ThreeInterop.addChild(g, head)
    }
    webGeometry.dispose()

    /* ---------------------- crossing deck through the road ---------------------- */
    val deckWidth = Street.RoadHalf * 2.0 + Street.WalkW * 2.0 + 1.0
    val deckCx    = Street.centerX(0.0)
    // deliberately lighter than the asphalt: seen head-on the crossing only
    // reads if the deck is a distinct tonal band
    val deck    = Toon.cel(Toon.CelOptions(color = 0xdad5df, bands = 3, tint = 0x6f6790))
    val between = Util.box(deckWidth, 0.28, RailGauge - 0.26, deck, deckCx, 0.18, 0.0)
    between.receiveShadow = true
    ThreeInterop.addChild(g, between)
    Vector(-1.0, 1.0).foreach { s =>
      val outer = Util.box(deckWidth, 0.28, 1.55, deck, deckCx, 0.18, s * (RailGauge / 2.0 + 0.12 + 0.78))
      outer.receiveShadow = true
      ThreeInterop.addChild(g, outer)
    }
    // rails continue across the deck, proud of the concrete, plus the flangeway
    // grooves either side -- that pair of dark lines makes a crossing legible
    Vector(-1.0, 1.0).foreach { s =>
      val zr         = (s * RailGauge) / 2.0
      val railAcross = Util.box(deckWidth, 0.12, 0.115, matRail(), deckCx, 0.36, zr)
      railAcross.receiveShadow = true
      ThreeInterop.addChild(g, railAcross)
      val headAcross = Util.box(deckWidth, 0.016, 0.09, matHead(), deckCx, 0.424, zr)
      ThreeInterop.setUserDataFlag(headAcross, "noOutline")
      ThreeInterop.addChild(g, headAcross)
      Vector(-1.0, 1.0).foreach { gs =>
        ThreeInterop.addChild(g,
          Util.box(deckWidth, 0.06, 0.055,
            Toon.cel(Toon.CelOptions(color = 0x4d4757, bands = 2, tint = 0x413c58)),
            deckCx, 0.33, zr + gs * 0.086))
      }
    }
    // yellow hazard bands painted on the road either side of the tracks, plus
    // short diagonal ticks between them
    val yellowMat = Toon.cel(Toon.CelOptions(color = Pal.lineYellow, bands = 2, tint = 0x8f7050))
    Vector(-1.0, 1.0).foreach { s =>
      Vector((0.42, 0.3), (0.92, 0.16)).foreach { case (off, wide) =>
        val bandGeometry = new PlaneGeometry(Street.RoadHalf * 2.0 - 0.15, wide)
        bandGeometry.rotateX(-math.Pi / 2.0)
        val band = new Mesh(bandGeometry, yellowMat)
        band.position.set(deckCx, 0.335, s * (Street.TrackHalf + off))
        ThreeInterop.setUserDataFlag(band, "noOutline")
        ThreeInterop.addChild(g, band)
      }
      (-5 to 5).foreach { i =>
        val tickGeometry = new PlaneGeometry(0.5, 0.11)
        tickGeometry.rotateX(-math.Pi / 2.0)
        tickGeometry.rotateY(math.Pi / 4.0)
        val tick = new Mesh(tickGeometry, yellowMat)
        tick.position.set(deckCx + i * 0.56, 0.333, s * (Street.TrackHalf + 0.67))
        ThreeInterop.setUserDataFlag(tick, "noOutline")
        ThreeInterop.addChild(g, tick)
      }
    }

    /* --------------------------- lineside fencing --------------------------- */
    val fenceZ     = Street.TrackHalf + 1.05
    val gap        = Street.RoadHalf + Street.WalkW + 0.5
    val fenceParts = mutable.ArrayBuffer.empty[Util.BakePart]
    val railGeoH   = new BoxGeometry(1.0, 0.06, 0.06)
    val postGeo    = new BoxGeometry(0.07, 1.12, 0.07)
    val barGeo     = new BoxGeometry(0.035, 0.5, 0.035)

    // every run is cut by every bore and every maintenance gate
    def trimRun(zf: Double, a: Double, b: Double): Vector[(Double, Double, Double)] =
      var segments = Vector((a, b))
      val cuts     = mutable.ArrayBuffer.empty[(Double, Double)]
      Tunnels.foreach { tunnel =>
        cuts += ((tunnel.x0, tunnel.x1))
        // the 1.8 m maintenance gate, on whichever side that bore's walkway is
        if math.signum(zf) == tunnel.walk then cuts += ((tunnel.gateX - 0.9, tunnel.gateX + 0.9))
      }
      cuts.foreach { case (c0, c1) =>
        val next = mutable.ArrayBuffer.empty[(Double, Double)]
        segments.foreach { case (s0, s1) =>
          if c1 <= s0 || c0 >= s1 then next += ((s0, s1))
          else
            if c0 - s0 > 0.6 then next += ((s0, c0))
            if s1 - c1 > 0.6 then next += ((c1, s1))
        }
        segments = next.toVector
      }
      segments.map { case (s0, s1) => (zf, s0, s1) }

    val runs = trimRun(fenceZ, LocalMin + 22.0, -gap) ++ trimRun(fenceZ, gap, LocalMax - 22.0) ++
      trimRun(-fenceZ, LocalMin + 22.0, -gap) ++ trimRun(-fenceZ, gap, 13.5) ++
      trimRun(-fenceZ, 39.5, LocalMax - 22.0)
    runs.foreach { case (zf, x0, x1) =>
      val len = x1 - x0
      val mid = (x0 + x1) / 2.0
      Vector(0.55, 1.02).foreach { yy =>
        fenceParts += Util.BakePart(railGeoH, Some(Util.trs(mid, yy, zf, 0.0, 0.0, 0.0, len, 1.0, 1.0)))
      }
      var px = x0
      while px <= x1 do
        fenceParts += Util.BakePart(postGeo, Some(Util.trs(px, 0.56, zf)))
        px += 2.4
      // vertical infill bars, sparse enough to stay graphic
      var bx = x0 + 0.3
      while bx <= x1 do
        fenceParts += Util.BakePart(barGeo, Some(Util.trs(bx, 0.78, zf)))
        bx += 0.32
      ctx.collide(x0, zf - 0.12, x1, zf + 0.12, 1.2)
    }
    val fence = new Mesh(Util.bake(fenceParts.toList), matMetal())
    fence.castShadow = true
    fence.name = "linesideFence"
    ThreeInterop.addChild(g, fence)

    /* -------------------------- overhead catenary -------------------------- */
    val mastMat   = matMetalDark()
    val mastParts = mutable.ArrayBuffer.empty[Util.BakePart]
    val mastStep  = Planet.Circumference / math.round(Planet.Circumference / 19.0)
    val wireZ     = 0.02
    var mastX     = XMin + mastStep
    while mastX <= XMax - mastStep * 0.5 do
      if !(math.abs(mastX) < 8.0) && !Tunnels.exists(tunnel => mastX > tunnel.x0 - 16.0 && mastX < tunnel.x1 + 16.0)
      then
        val zf = -(Street.TrackHalf + 1.45)
        mastParts += Util.BakePart(new CylinderGeometry(0.09, 0.13, 6.6, 6.0), Some(Util.trs(mastX, 3.3, zf)))
        mastParts += Util.BakePart(new BoxGeometry(0.1, 0.1, wireZ - zf),
          Some(Util.trs(mastX, 6.1, (zf + wireZ) * 0.5)))
        mastParts += Util.BakePart(new BoxGeometry(0.09, 1.0, 0.09), Some(Util.trs(mastX, 5.6, wireZ)))
        mastParts += Util.BakePart(new CylinderGeometry(0.05, 0.05, 0.28, 6.0), Some(Util.trs(mastX, 5.02, wireZ)))
      mastX += mastStep
    val masts = new Mesh(Util.bake(mastParts.toList), mastMat)
    masts.castShadow = true
    ThreeInterop.addChild(g, masts)

    // contact wire + messenger wire, carried the whole way round the equator
    val wireMat = Toon.cel(Toon.CelOptions(color = Pal.metalDark, bands = 2, tint = 0x4a4468))
    Vector((4.88, 0.022), (5.95, 0.026)).foreach { case (y, r) =>
      val pts = js.Array[Vector3]()
      var wx  = XMin
      while wx <= XMax do
        pts.push(new Vector3(wx, y - (if y > 5.0 then 0.12 else 0.0), wireZ))
        pts.push(new Vector3(wx + mastStep * 0.5, y, wireZ))
        wx += mastStep
      val curve = new CatmullRomCurve3(pts)
      val segs  = math.round(Planet.Circumference / 2.5)
      val tube  = new Mesh(new TubeGeometry(curve, segs.toDouble, r, 4.0, false), wireMat)
      tube.name = "catenaryWire"
      ThreeInterop.addChild(g, tube)
    }

    /* ------------------------------- crossing ------------------------------- */
    val crossing = buildCrossing(ctx, g)

    /* -------------------------------- station -------------------------------- */
    buildStation(ctx, g)

    /* ------ walls further down the line, masking where the train enters ------ */
    val wallMat = Toon.cel(Toon.CelOptions(color = Pal.concreteMid, bands = 3, tint = 0x6a6288))
    // the runs stop at +-80: the hills mask the line better than concrete does,
    // and the east approach has to stay open to the bore's gate and railside spot
    val wallRuns = Vector(
      (Street.TrackHalf + 2.6, -80.0, -30.0), (Street.TrackHalf + 2.6, 46.0, 80.0),
      (-(Street.TrackHalf + 2.6), -80.0, -30.0), (-(Street.TrackHalf + 2.6), 44.0, 80.0)
    )
    val capMat = Toon.cel(Toon.CelOptions(color = Pal.concrete, bands = 3, tint = 0x6f6790))
    wallRuns.foreach { case (zf, x0, x1) =>
      val wall = Util.box(x1 - x0, 2.2, 0.35, wallMat, (x0 + x1) / 2.0, 1.1, zf)
      wall.castShadow = true
      wall.receiveShadow = true
      ThreeInterop.addChild(g, wall)
      ctx.collide(x0, zf - 0.2, x1, zf + 0.2, 2.2)

      // a pier on both ends: a wall ends in a pier, not in nothing
      Vector(x0, x1).foreach { ex =>
        val inward = if ex == x0 then 1.0 else -1.0
        val px     = ex - inward * 0.22
        val pier   = Util.box(0.52, 2.52, 0.62, wallMat, px, 1.26, zf)
        pier.castShadow = true
        pier.receiveShadow = true
        ThreeInterop.addChild(g, pier)
        // the cap does not cast: a 50 mm overhang is a shadow-map texel and a half
        val cap = Util.box(0.62, 0.1, 0.72, capMat, px, 2.57, zf)
        cap.receiveShadow = true
        ThreeInterop.addChild(g, cap)
        ctx.collide(px - 0.31, zf - 0.36, px + 0.31, zf + 0.36, 2.62)
      }
    }

    crossing
  end buildRailway

  /* ------------------------------------------------------------------ *
   * Level crossing: four gate posts, two swinging arms, flashing lamps,
   * crossbucks, bell housings, relay cabinets, safety kerbs.
   * ------------------------------------------------------------------ */

  private def buildCrossing(ctx: WorldCtx, parent: Group[Object3DEventMap]): CrossingApi =
    val group = new Group()
    group.name = "crossing"
    ThreeInterop.addChild(parent, group)

    val cx        = Street.centerX(0.0)
    val yellow    = matYellow()
    val black     = matBlack()
    val metal     = matMetal()
    val metalDark = matMetalDark()
    val cabinet   = matCabinet()

    val lamps       = mutable.ArrayBuffer.empty[(Mesh[?, ?, ?], String, MeshBasicMaterial)]
    val arms        = mutable.ArrayBuffer.empty[CrossingArm]
    val armLampBase = Toon.flat(Toon.FlatOptions(color = Pal.signalOff))

    // striped arm geometry: alternating yellow / black blocks
    def makeArm(length: Double): Group[Object3DEventMap] =
      val arm    = new Group()
      val seg    = 0.52
      val n      = math.round(length / seg).toInt
      val segGeo = new BoxGeometry(seg, 0.17, 0.09)
      val yParts = mutable.ArrayBuffer.empty[Util.BakePart]
      val bParts = mutable.ArrayBuffer.empty[Util.BakePart]
      (0 until n).foreach { i =>
        val mx = Util.trs(seg * (i + 0.5), 0.0, 0.0)
        (if i % 2 == 0 then yParts else bParts) += Util.BakePart(segGeo, Some(mx))
      }
      val armYellow = new Mesh(Util.bake(yParts.toList), yellow)
      val armBlack  = new Mesh(Util.bake(bParts.toList), black)
      armYellow.castShadow = true
      armBlack.castShadow = true
      ThreeInterop.addChild(arm, armYellow)
      ThreeInterop.addChild(arm, armBlack)
      // hanging warning lamps under the arm
      var i = 1
      while i < n - 1 do
        val lampMaterial = armLampBase.clone()
        val lamp         = Util.box(0.11, 0.11, 0.07, lampMaterial, seg * (i + 0.5), -0.14, 0.06)
        lamp.userData("lamp") = "arm"
        lamps += ((lamp, "arm", lampMaterial))
        ThreeInterop.addChild(arm, lamp)
        ThreeInterop.addChild(arm, Util.box(0.03, 0.12, 0.03, metalDark, seg * (i + 0.5), -0.05, 0.06))
        i += 3
      sakura.core.Outline.hullOutline(armYellow, sakura.core.Outline.OutlineOptions(thickness = 0.0032))
      sakura.core.Outline.hullOutline(armBlack, sakura.core.Outline.OutlineOptions(thickness = 0.0032))
      arm
    end makeArm

    /** One corner assembly: a squat barrier machine whose boom pivots at waist
      * height, and a separate taller mast carrying the lamps, bell and crossbuck.
      */
    def corner(sx: Double, sz: Double, kind: String): Group[Object3DEventMap] =
      val grp = new Group()
      val x   = cx + sx * (Street.RoadHalf + 0.42)
      val z   = sz * Street.GateZ
      grp.position.set(x, Street.groundY(0.0), z)
      // the boom pivots have to outlive the planet bake
      ThreeInterop.setUserDataFlag(grp, "planetRigid")

      val base = Util.box(0.66, 0.2, 0.62, matConcrete(), 0.0, 0.1, 0.0)
      base.receiveShadow = true
      base.castShadow = true
      ThreeInterop.addChild(grp, base)

      if kind == "arm" then
        val machineHeight = 0.92
        val machine       = Util.box(0.46, machineHeight, 0.38, matCabinet(), 0.0, 0.2 + machineHeight / 2.0, 0.0)
        machine.castShadow = true
        machine.receiveShadow = true
        ThreeInterop.addChild(grp, machine)
        sakura.core.Outline.hullOutline(machine, sakura.core.Outline.OutlineOptions(thickness = 0.0034))
        ThreeInterop.addChild(grp,
          Util.box(0.54, 0.07, 0.46,
            Toon.cel(Toon.CelOptions(color = Pal.cabinetTop, bands = 3)), 0.0, 0.2 + machineHeight + 0.03, 0.0))
        // yellow / black hazard banding on the machine body
        (0 until 3).foreach { i =>
          ThreeInterop.addChild(grp,
            Util.box(0.48, 0.11, 0.4, if i % 2 == 1 then black else yellow,
              0.0, 0.34 + i * 0.24, 0.0))
        }

        // the boom stands vertical at rest and swings down across the road
        val pivot = new Group()
        pivot.position.set(0.0, 0.2 + machineHeight + 0.12, sz * 0.2)
        ThreeInterop.addChild(pivot, makeArm(Street.RoadHalf * 2.0 + 0.5))
        pivot.rotation.y = if sx > 0 then math.Pi else 0.0
        pivot.rotation.z = math.Pi / 2.0
        ThreeInterop.addChild(grp, pivot)
        arms += CrossingArm(pivot)
        ThreeInterop.addChild(grp, Util.box(0.2, 0.2, 0.14, metalDark, 0.0, 0.2 + machineHeight + 0.12, sz * 0.2))
      end if

      // signal mast
      val mastX = if kind == "arm" then sx * 0.44 else 0.0
      val mastH = 2.45
      val mast  = Util.cyl(0.075, 0.09, mastH, 8, yellow, mastX, 0.2 + mastH / 2.0, 0.0)
      mast.castShadow = true
      ThreeInterop.addChild(grp, mast)
      (0 until 4).foreach { i =>
        ThreeInterop.addChild(grp, Util.cyl(0.082, 0.082, 0.22, 8, black, mastX, 0.42 + i * 0.56, 0.0))
      }
      sakura.core.Outline.hullOutline(mast, sakura.core.Outline.OutlineOptions(thickness = 0.0032))
      if kind == "arm" then
        ThreeInterop.addChild(grp, Util.box(0.5, 0.09, 0.09, metalDark, mastX / 2.0, 0.2 + 0.5, 0.0))

      // signal head: two red lamps facing along the road
      val headGrp = new Group()
      headGrp.position.set(mastX, 0.2 + mastH + 0.02, sz * 0.02)
      headGrp.rotation.y = if sz > 0 then 0.0 else math.Pi
      ThreeInterop.addChild(headGrp, Util.box(0.86, 0.13, 0.1, black, 0.0, 0.06, 0.0))
      Vector(-0.28, 0.28).foreach { lx =>
        val hood = new Mesh(new CylinderGeometry(0.145, 0.16, 0.13, 12.0, 1.0, true), black)
        hood.rotation.x = math.Pi / 2.0
        hood.position.set(lx, -0.12, 0.07)
        ThreeInterop.addChild(headGrp, hood)
        val back = new Mesh(new CircleGeometry(0.16, 14.0), black)
        back.position.set(lx, -0.12, 0.0)
        ThreeInterop.addChild(headGrp, back)
        val lensMaterial = Toon.flat(Toon.FlatOptions(color = Pal.signalOff, cache = false)).clone()
        val lens         = new Mesh(new CircleGeometry(0.13, 14.0), lensMaterial)
        lens.position.set(lx, -0.12, 0.135)
        val lampKind = if lx < 0 then "a" else "b"
        lens.userData("lamp") = lampKind
        lamps += ((lens, lampKind, lensMaterial))
        ThreeInterop.addChild(headGrp, lens)
      }
      // bell housing above the lamps
      val bell = new Mesh(new SphereGeometry(0.13, 10.0, 7.0, 0.0, math.Pi * 2.0, 0.0, math.Pi / 2.0), metal)
      bell.rotation.x = math.Pi
      bell.position.set(0.0, 0.3, 0.0)
      ThreeInterop.addChild(headGrp, bell)
      ThreeInterop.addChild(grp, headGrp)

      // crossbuck and board
      if kind == "sign" then
        val boardMat = ThreeInterop.materialArray(
          Toon.flat(Toon.FlatOptions(color = Pal.wallWhite)),
          Toon.flat(Toon.FlatOptions(color = Pal.wallWhite)),
          Toon.flat(Toon.FlatOptions(color = Pal.wallWhite)),
          Toon.flat(Toon.FlatOptions(color = Pal.wallWhite)),
          Toon.flat(Toon.FlatOptions(color = 0xffffff, map = Some(Textures.crossingSign()), cache = false)),
          Toon.flat(Toon.FlatOptions(color = Pal.wallGray))
        )
        val board = new Mesh(new BoxGeometry(1.15, 0.58, 0.05), boardMat)
        board.position.set(0.0, 0.2 + mastH + 0.62, sz * 0.05)
        board.rotation.y = if sz > 0 then 0.0 else math.Pi
        board.castShadow = true
        ThreeInterop.addChild(grp, board)
        sakura.core.Outline.hullOutline(board, sakura.core.Outline.OutlineOptions(thickness = 0.003))

        Vector(0.72, -0.72).foreach { r =>
          val bar = Util.box(1.2, 0.15, 0.045, yellow, 0.0, 0.2 + mastH + 1.28, sz * 0.05)
          bar.rotation.z = r
          bar.castShadow = true
          ThreeInterop.addChild(grp, bar)
        }
        ThreeInterop.addChild(grp, Util.cyl(0.06, 0.07, 1.3, 8, yellow, 0.0, 0.2 + mastH + 0.65, 0.0))
      end if

      ThreeInterop.addChild(group, grp)
      ctx.collide(x - 0.4, z - 0.35, x + 0.4, z + 0.35, 2.4)
      grp
    end corner

    corner(-1.0, 1.0, "arm")
    corner(1.0, -1.0, "arm")
    corner(1.0, 1.0, "sign")
    corner(-1.0, -1.0, "sign")

    /* ----------------------------- lamp state ----------------------------- */
    val onColor  = new Color(Pal.signalRed.toDouble)
    val offColor = new Color(Pal.signalOff.toDouble)
    // each lens/lamp keeps its own material instance so colour can be driven
    val armLampMaterials = lamps.filter(_._2 == "arm").map(_._3)
    val lampAMaterials   = lamps.filter(_._2 == "a").map(_._3)
    val lampBMaterials   = lamps.filter(_._2 == "b").map(_._3)

    def setLamps(state: Boolean, blink: Double): Unit =
      val colorA = if state && blink < 0.5 then onColor else offColor
      val colorB = if state && blink >= 0.5 then onColor else offColor
      lampAMaterials.foreach(material => material.color.copy(colorA))
      lampBMaterials.foreach(material => material.color.copy(colorB))
      val colorArm = if state then (if blink < 0.5 then onColor else offColor) else offColor
      armLampMaterials.foreach(material => material.color.copy(colorArm))
      ()

    def setArms(t: Double): Unit =
      val eased = if t < 0.5 then 2.0 * t * t else 1.0 - math.pow(-2.0 * t + 2.0, 2.0) / 2.0
      arms.foreach(arm => arm.pivot.rotation.z = (1.0 - eased) * (math.Pi / 2.0) * 0.99 + 0.004)

    /* --- relay cabinets and a control box on the far corner --- */
    val zc = -Street.GateZ - 1.15
    val xc = cx - (Street.RoadHalf + 1.5)
    val c1 = Util.box(0.78, 1.32, 0.5, cabinet, xc, Street.groundY(0.0) + 0.66, zc)
    c1.castShadow = true
    c1.receiveShadow = true
    ThreeInterop.addChild(group, c1)
    sakura.core.Outline.hullOutline(c1, sakura.core.Outline.OutlineOptions(thickness = 0.003))
    ThreeInterop.addChild(group,
      Util.box(0.86, 0.08, 0.58,
        Toon.cel(Toon.CelOptions(color = Pal.cabinetTop, bands = 3)), xc, Street.groundY(0.0) + 1.36, zc))
    val c2 = Util.box(0.52, 0.9, 0.4, cabinet, xc - 0.9, Street.groundY(0.0) + 0.45, zc + 0.1)
    c2.castShadow = true
    c2.receiveShadow = true
    ThreeInterop.addChild(group, c2)
    ThreeInterop.addChild(group,
      Util.box(0.6, 0.06, 0.46,
        Toon.cel(Toon.CelOptions(color = Pal.cabinetTop, bands = 3)), xc - 0.9, Street.groundY(0.0) + 0.93, zc + 0.1))
    // door seam + a small red indicator
    ThreeInterop.addChild(group,
      Util.box(0.02, 1.0, 0.02,
        Toon.cel(Toon.CelOptions(color = Pal.metalDark, bands = 2)), xc, Street.groundY(0.0) + 0.66, zc - 0.26))
    ThreeInterop.addChild(group,
      Util.box(0.07, 0.07, 0.03,
        Toon.flat(Toon.FlatOptions(color = Pal.signalRed)), xc + 0.24, Street.groundY(0.0) + 1.16, zc - 0.26))
    ctx.collide(xc - 1.3, zc - 0.4, xc + 0.5, zc + 0.4, Street.groundY(0.0) + 1.4)

    // the box the player can press to call a train
    val button = Util.box(0.3, 0.4, 0.12,
      Toon.cel(Toon.CelOptions(color = Pal.yellow, bands = 3)), xc, Street.groundY(0.0) + 1.0, zc - 0.3)
    button.castShadow = true
    ThreeInterop.addChild(group, button)
    val hit = Util.box(0.9, 1.6, 0.9,
      Toon.flat(Toon.FlatOptions(color = 0xff0000, cache = false)), xc, Street.groundY(0.0) + 0.8, zc)
    hit.visible = false
    ThreeInterop.addChild(group, hit)
    /* Every lamp and both arms are registered by now, so the API the world drives can be built
     * here and the relay box can fetch a train through `request` -- see it set in `WorldIndex`.
     */
    val crossingApi = CrossingApi(group = group, arms = arms.toVector, active = false, armT = 0.0,
      setLamps = setLamps, setArms = setArms)
    ctx.interact(hit, "踏切スイッチ  ·  call a train", () => crossingApi.request())

    /* --- safety kerbs guiding pedestrians through the crossing --- */
    val kerbMat = Toon.cel(Toon.CelOptions(color = Pal.concrete, bands = 3, tint = 0x6f6790))
    Vector(-1.0, 1.0).foreach { sx =>
      val kerbX = cx + sx * (Street.RoadHalf + 0.15)
      val kerb  = Util.box(0.34, 0.16, Street.TrackHalf * 2.0 + 1.6, kerbMat, kerbX, Street.groundY(0.0) + 0.08, 0.0)
      kerb.receiveShadow = true
      kerb.castShadow = true
      ThreeInterop.addChild(group, kerb)
      // yellow tactile patch where the pavement meets the deck
      Vector(-1.0, 1.0).foreach { sz =>
        val tactile = new Mesh(
          new PlaneGeometry(1.35, 0.6),
          Toon.cel(Toon.CelOptions(color = 0xffffff, bands = 2,
            map = Some(Textures.tactileTex(true)), tint = 0x8f7050, cache = false))
        )
        tactile.rotation.x = -math.Pi / 2.0
        tactile.position.set(cx + sx * (Street.RoadHalf + 0.82),
          Street.groundY(sz * Street.CrossBand) + Street.WalkH + 0.016, sz * (Street.CrossBand + 0.32))
        ThreeInterop.addChild(group, tactile)
      }
    }

    setLamps(false, 0.0)
    setArms(0.0)
    crossingApi
  end buildCrossing

  /* ------------------------------------------------------------------ *
   * A small unstaffed platform down the line, mostly there to give the
   * background depth and a reason for the train to exist.
   * ------------------------------------------------------------------ */

  private def buildStation(ctx: WorldCtx, parent: Group[Object3DEventMap]): Group[Object3DEventMap] =
    val g = new Group()
    g.name = "station"
    ThreeInterop.addChild(parent, g)

    val x0           = 15.5
    val x1           = 38.0
    val zf           = -1.92
    val zb           = zf - 3.7
    val h            = 0.98
    val concrete     = Toon.cel(Toon.CelOptions(color = Pal.concrete, bands = 3, tint = 0x6f6790))
    val concreteDark = Toon.cel(Toon.CelOptions(color = Pal.concreteMid, bands = 3, tint = 0x6a6288))

    val deck = Util.box(x1 - x0, h, zf - zb, concrete, (x0 + x1) / 2.0, h / 2.0, (zf + zb) / 2.0)
    deck.castShadow = true
    deck.receiveShadow = true
    ThreeInterop.addChild(g, deck)
    ctx.platform(x0, x1, zb, zf, h)
    ctx.collide(x0 - 0.1, zb - 0.1, x1 + 0.1, zf + 0.1, h)

    // platform edge: dark band + yellow tactile line
    ThreeInterop.addChild(g, Util.box(x1 - x0, 0.06, 0.34, concreteDark, (x0 + x1) / 2.0, h - 0.02, zf - 0.17))
    val tactMat = Toon.cel(Toon.CelOptions(color = 0xffffff, bands = 2,
      map = Some(Textures.tactileTex(false)), tint = 0x8f7050, cache = false))
    val tactMap = tactMat.map
    if tactMap != null then
      tactMap.repeat.set((x1 - x0 - 1.0) / 0.46, 1.0)
      tactMap.wrapS = 1001
    val tact = new Mesh(new PlaneGeometry(x1 - x0 - 1.0, 0.46), tactMat)
    tact.rotation.x = -math.Pi / 2.0
    tact.position.set((x0 + x1) / 2.0, h + 0.012, zf - 0.62)
    ThreeInterop.addChild(g, tact)

    // canopy over the middle of the platform
    val canopyCx = (x0 + x1) / 2.0 + 1.5
    val roofW    = 9.5
    val roofD    = 3.2
    val posts    = mutable.ArrayBuffer.empty[Util.BakePart]
    Vector(canopyCx - roofW / 2.0 + 0.6, canopyCx + roofW / 2.0 - 0.6).foreach { px =>
      Vector(zf - 0.9, zb + 0.7).foreach { pz =>
        posts += Util.BakePart(new CylinderGeometry(0.075, 0.075, 2.6, 8.0), Some(Util.trs(px, h + 1.3, pz)))
      }
    }
    val postMesh = new Mesh(Util.bake(posts.toList), Toon.cel(Toon.CelOptions(color = Pal.metalDark, bands = 3)))
    postMesh.castShadow = true
    ThreeInterop.addChild(g, postMesh)
    val roof = Util.box(roofW, 0.16, roofD,
      Toon.cel(Toon.CelOptions(color = Pal.roofTeal, bands = 3, tint = 0x4a4468)),
      canopyCx, h + 2.66, (zf + zb) / 2.0 - 0.1)
    roof.castShadow = true
    roof.receiveShadow = true
    ThreeInterop.addChild(g, roof)
    ThreeInterop.addChild(g,
      Util.box(roofW + 0.3, 0.1, 0.14,
        Toon.cel(Toon.CelOptions(color = Pal.metal, bands = 3)), canopyCx, h + 2.56, (zf + zb) / 2.0 - 1.75))
    sakura.core.Outline.hullOutline(roof, sakura.core.Outline.OutlineOptions(thickness = 0.003))

    // benches under the canopy
    Vector(canopyCx - 2.2, canopyCx + 1.4).foreach { bx =>
      val bench = new Group()
      ThreeInterop.addChild(bench,
        Util.box(1.7, 0.08, 0.42,
          Toon.cel(Toon.CelOptions(color = Pal.wallCream, bands = 3)), 0.0, 0.42, 0.0))
      ThreeInterop.addChild(bench,
        Util.box(1.7, 0.5, 0.07,
          Toon.cel(Toon.CelOptions(color = Pal.wallCream, bands = 3)), 0.0, 0.66, -0.2))
      Vector(-0.7, 0.7).foreach { lx =>
        ThreeInterop.addChild(bench,
          Util.box(0.1, 0.42, 0.36,
            Toon.cel(Toon.CelOptions(color = Pal.metalDark, bands = 3)), lx, 0.21, 0.0))
      }
      bench.position.set(bx, h, zb + 0.95)
      bench.traverse((node: Object3D[Object3DEventMap]) =>
        if ThreeInterop.isMesh(node) then node.castShadow = true
      )
      ThreeInterop.addChild(g, bench)
    }

    // station name boards on posts, facing the track
    Vector(x0 + 4.0, x1 - 4.5).foreach { sx =>
      val post = Util.cyl(0.06, 0.06, 2.2, 8,
        Toon.cel(Toon.CelOptions(color = Pal.metalDark, bands = 3)), sx, h + 1.1, zf - 0.55)
      post.castShadow = true
      ThreeInterop.addChild(g, post)
      val board = new Mesh(
        new BoxGeometry(1.9, 0.5, 0.06),
        ThreeInterop.materialArray(
          Toon.flat(Toon.FlatOptions(color = Pal.wallWhite)),
          Toon.flat(Toon.FlatOptions(color = Pal.wallWhite)),
          Toon.flat(Toon.FlatOptions(color = Pal.wallWhite)),
          Toon.flat(Toon.FlatOptions(color = Pal.wallWhite)),
          Toon.flat(Toon.FlatOptions(color = 0xffffff, map = Some(Textures.stationSign()), cache = false)),
          Toon.flat(Toon.FlatOptions(color = Pal.wallGray))
        )
      )
      board.position.set(sx, h + 2.25, zf - 0.55)
      board.castShadow = true
      ThreeInterop.addChild(g, board)
      sakura.core.Outline.hullOutline(board, sakura.core.Outline.OutlineOptions(thickness = 0.003))
    }

    // back fence and a warning plate
    val fenceParts = mutable.ArrayBuffer.empty[Util.BakePart]
    var fx         = x0
    while fx <= x1 do
      fenceParts += Util.BakePart(new BoxGeometry(0.08, 1.2, 0.08), Some(Util.trs(fx, h + 0.6, zb + 0.06)))
      fx += 2.2
    Vector(h + 0.5, h + 1.1).foreach { y =>
      fenceParts += Util.BakePart(new BoxGeometry(x1 - x0, 0.06, 0.06),
        Some(Util.trs((x0 + x1) / 2.0, y, zb + 0.06)))
    }
    val fenceMesh = new Mesh(Util.bake(fenceParts.toList), Toon.cel(Toon.CelOptions(color = Pal.metal, bands = 3)))
    fenceMesh.castShadow = true
    ThreeInterop.addChild(g, fenceMesh)

    // steps down at the near end, padded so consecutive treads overlap
    val stepCount = 6
    (0 until stepCount).foreach { i =>
      val stepH = h * ((stepCount - i).toDouble / stepCount)
      val sx    = x0 - 0.18 - i * 0.36
      val step  = Util.box(0.36, stepH, 2.4, concrete, sx, stepH / 2.0, zb + 1.3)
      step.castShadow = true
      step.receiveShadow = true
      ThreeInterop.addChild(g, step)
      ctx.platform(sx - 0.21, sx + 0.21, zb + 0.1, zb + 2.5, stepH)
    }
    Vector(zb + 0.1, zb + 2.5).foreach { sz =>
      val rail = Util.box(2.4, 0.07, 0.07, Toon.cel(Toon.CelOptions(color = Pal.metal, bands = 3)),
        x0 - 1.2, 0.95, sz)
      rail.rotation.z = 0.36
      ThreeInterop.addChild(g, rail)
    }

    // lamps along the platform
    Vector(x0 + 2.5, (x0 + x1) / 2.0 - 3.5, x1 - 2.0).foreach { lx =>
      val post = Util.cyl(0.055, 0.055, 3.1, 8,
        Toon.cel(Toon.CelOptions(color = Pal.metalDark, bands = 3)), lx, h + 1.55, zb + 0.5)
      post.castShadow = true
      ThreeInterop.addChild(g, post)
      val shade = new Mesh(new ConeGeometry(0.26, 0.22, 10.0, 1.0, true),
        Toon.cel(Toon.CelOptions(color = Pal.wallGray, bands = 3)))
      shade.position.set(lx, h + 3.05, zb + 0.5)
      ThreeInterop.addChild(g, shade)
      ThreeInterop.addChild(g,
        Util.box(0.18, 0.05, 0.18, Toon.flat(Toon.FlatOptions(color = 0xfff3d4)),
          lx, h + 2.92, zb + 0.5))
    }

    // a warning plate on the fence, for texture
    val plate = new Mesh(
      new BoxGeometry(0.36, 0.72, 0.04),
      ThreeInterop.materialArray(
        Toon.flat(Toon.FlatOptions(color = Pal.wallGray)),
        Toon.flat(Toon.FlatOptions(color = Pal.wallGray)),
        Toon.flat(Toon.FlatOptions(color = Pal.wallGray)),
        Toon.flat(Toon.FlatOptions(color = Pal.wallGray)),
        Toon.flat(Toon.FlatOptions(color = 0xffffff, map = Some(Textures.warningPlate(1)), cache = false)),
        Toon.flat(Toon.FlatOptions(color = Pal.wallGray))
      )
    )
    plate.position.set(x0 + 8.5, h + 1.0, zb + 0.02)
    plate.rotation.y = math.Pi
    ThreeInterop.addChild(g, plate)

    g
  end buildStation
end Railway
