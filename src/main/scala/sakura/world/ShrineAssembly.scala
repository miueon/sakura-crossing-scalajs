package sakura.world

import sakura.*
import vendor.three.*

import scala.math
import scala.scalajs.js

/** 桜守神社, the community shrine, ported from upstream `shrine.js`: a vermilion torii with its
  * 注連縄 and 紙垂 standing at the mouth of a walled 2.9 m alley, an eleven-tread stone flight
  * climbing 2.09 m to a terraced precinct enclosed by retaining walls, a second plain torii at
  * the terrace head, the main hall under its upswept chigi roof, and stone lanterns flanking the
  * approach. Everything cel-shaded joins the shared [[CelBatch]] with its world transform baked
  * into the geometry, including the lanterns' open fire boxes and dark cores.
  *
  * The flight is fixed geometry, not terrain-following: eleven treads of rise 0.19 and run 0.46
  * from a level alley, exactly the upstream proportions.
  */
object ShrineAssembly:

  /** The shrine's vermilion, the base colour of the torii timbers and the hall's posts. */
  val Vermilion: Int             = 0xc83228
  private val VermilionDeep: Int = 0xa02a22
  private val GroveGreen: Int    = 0x4a6a52

  val OriginX: Double = -27.9
  val OriginZ: Double = 20.5
  val Facing: Double  = 0.0

  private[sakura] val StairRun: Double     = 0.46
  private[sakura] val StairRise: Double    = 0.19
  private[sakura] val StairTreadCount: Int = 11
  private[sakura] val StairWidth: Double   = 3.4
  private[sakura] val TerraceTop: Double   = StairRise * StairTreadCount

  final private case class BambooClump(
    ahead: Double,
    side: Double,
    count: Int,
    spread: Double,
    scale: Double,
    seed: Int,
    onTerrace: Boolean
  )

  final private case class DappleCluster(
    ahead: Double,
    side: Double,
    radius: Double,
    spread: Double,
    count: Int,
    onTerrace: Boolean
  )

  private val bambooClumps = List(
    BambooClump(12.1, -7.3, 13, 1.5, 1.15, 821, onTerrace = true),
    BambooClump(14.9, 6.9, 11, 1.3, 1.05, 822, onTerrace = true),
    BambooClump(3.9, 3.1, 9, 1.2, 1.0, 823, onTerrace = false),
    BambooClump(0.5, -3.7, 10, 1.4, 1.1, 824, onTerrace = false),
    BambooClump(6.9, -6.5, 8, 1.1, 1.0, 825, onTerrace = true)
  )

  final private class Mulberry32(seed: Int):
    private var state = seed

    def next(): Double =
      state += 0x6d2b79f5L.toInt
      var value = state
      value = (value ^ (value >>> 15)) * (value | 1)
      value = value ^ (value + (value ^ (value >>> 7)) * (value | 61))
      ((value ^ (value >>> 14)).toLong & 0xffffffffL).toDouble / 4294967296.0

    def range(minimum: Double, maximum: Double): Double =
      minimum + (maximum - minimum) * next()

  private val terraceX0     = -36.0
  private val terraceX1     = -20.0
  private val terraceZ0     = 25.4
  private val terraceZ1     = 40.0
  private val cheekOffset   = 1.85
  private val lanternOffset = 2.55

  /** The upstream establishing shot: standing in the alley 5.9 m before the torii, looking up
    * the stone flight through the gate to the hall — yaw π, pitch 0.10, from the `torii` frame.
    */
  val Viewpoint: DistrictViewpoint = DistrictViewpoint(
    eyeX = OriginX,
    eyeZ = 14.0,
    eyeHeight = 1.62,
    targetX = OriginX,
    targetZ = 33.9,
    targetHeight = 3.53
  )

  final private[sakura] case class StepPlan(
    centerX: Double,
    centerZ: Double,
    terrainY: Double,
    topY: Double,
    bottomY: Double
  )

  /** Builds the shrine with its torii at `(originX, originZ)` and the stair climbing `facing`
    * radians from +z towards the hall.
    */
  def addTo(
    batch: CelBatch,
    originX: Double,
    originZ: Double,
    facing: Double,
    curvature: PlanetCurvature,
    textures: TextureCache
  ): SceneAssembly =
    val frame = ShrineFrame(originX, originZ, facing)

    approach(batch, frame)
    torii(batch, frame, ahead = -0.6, width = 3.5, height = 3.4, plain = false,
      base = TerrainHeightmap.groundY(frame.worldZ(-0.6, 0.0)))
    stoneSteps(batch, frame)
    terrace(batch, frame)
    torii(batch, frame, ahead = 6.4, width = 2.7, height = 2.7, plain = true, base = TerraceTop)
    lanterns(batch, frame)
    hall(batch, frame)
    temizuya(batch, frame)
    foxStatues(batch, frame)
    emaRack(batch, frame)
    omikujiStand(batch, frame)
    noticePost(batch, frame)
    bambooClusters(batch, frame)
    grove(batch, frame)
    fallenPetals(batch, frame)
    decorations(frame, textures, curvature)
  end addTo

  /** The shrine's own frame: `ahead` runs from the stair foot up the approach, `side` across it. */
  final private case class ShrineFrame(originX: Double, originZ: Double, facing: Double):
    def worldX(ahead: Double, side: Double): Double =
      originX + side * math.cos(facing) + ahead * math.sin(facing)

    def worldZ(ahead: Double, side: Double): Double =
      originZ - side * math.sin(facing) + ahead * math.cos(facing)

    def toAhead(worldZ: Double): Double = worldZ - originZ

  /** Bakes a local-frame geometry into the world through the shrine frame. */
  private def placeInFrame(
    batch: CelBatch,
    geometry: BufferGeometry[?, ?],
    frame: ShrineFrame,
    ahead: Double,
    baseY: Double,
    side: Double = 0.0,
    outlined: Boolean = false,
    hex: Int
  ): Unit =
    geometry.translate(side, 0.0, ahead)
    geometry.rotateY(frame.facing)
    geometry.translate(frame.worldX(0.0, 0.0), 0.0, frame.worldZ(0.0, 0.0))
    liftTo(batch, geometry, frame, baseY, outlined, hex)

  /** Authoring keeps `y` local (0 at the seat plane); the world seat is the terrain at the
    * part's own world position, so each part follows the ground independently.
    */
  private def liftTo(
    batch: CelBatch,
    geometry: BufferGeometry[?, ?],
    frame: ShrineFrame,
    baseY: Double,
    outlined: Boolean,
    hex: Int
  ): Unit =
    geometry.translate(0.0, baseY, 0.0)
    batch.add(geometry, hex, outlined)

  /** The alley: stone lane up between the houses, the forecourt pad at its head, and the low
    * walls closing its sides off from the gardens.
    */
  private def approach(batch: CelBatch, frame: ShrineFrame): Unit =
    def groundAt(z: Double): Double = TerrainHeightmap.groundY(z)

    val lane = new BoxGeometry(2.9, 0.08, 15.6)
    placeInFrame(batch, lane, frame, ahead = frame.toAhead(11.8), baseY = groundAt(11.8) + 0.04,
      hex = Palette.concrete)

    val forecourt = new BoxGeometry(6.4, 0.08, 4.6)
    placeInFrame(batch, forecourt, frame, ahead = frame.toAhead(18.0), baseY = groundAt(18.0) + 0.04,
      hex = Palette.concrete)

    val eastWall = new BoxGeometry(0.22, 1.15, 11.0)
    placeInFrame(batch, eastWall, frame, ahead = frame.toAhead(11.5), baseY = groundAt(11.5),
      side = 1.75, hex = Palette.stoneDark)
    val eastCap = new BoxGeometry(0.3, 0.08, 11.0)
    placeInFrame(batch, eastCap, frame, ahead = frame.toAhead(11.5), baseY = groundAt(11.5) + 1.15,
      side = 1.75, hex = Palette.concrete)

    val westWall = new BoxGeometry(0.22, 1.05, 8.0)
    placeInFrame(batch, westWall, frame, ahead = frame.toAhead(10.0), baseY = groundAt(10.0),
      side = -1.75, hex = Palette.stoneDark)
    val westCap = new BoxGeometry(0.3, 0.08, 8.0)
    placeInFrame(batch, westCap, frame, ahead = frame.toAhead(10.0), baseY = groundAt(10.0) + 1.05,
      side = -1.75, hex = Palette.concrete)
  end approach

  /** The 鳥居: leaning tapered pillars on stone plinths, the 貫 and 島木 beams, the three-segment
    * 笠木 whose outer tips lift, and — on the outer gate only — the 注連縄 with its bound tufts
    * and hanging 紙垂.
    */
  private def torii(
    batch: CelBatch,
    frame: ShrineFrame,
    ahead: Double,
    width: Double,
    height: Double,
    plain: Boolean,
    base: Double
  ): Unit =
    val pillarRadius = 0.15 * (width / 3.4)
    val red          = if plain then VermilionDeep else Vermilion

    List(-1.0, 1.0).foreach { side =>
      val pillar = new CylinderGeometry(pillarRadius * 0.9, pillarRadius, height, 10.0)
      pillar.rotateZ(side * 0.018)
      pillar.translate(side * width / 2.0, height / 2.0, 0.0)
      placeInFrame(batch, pillar, frame, ahead, base, outlined = true, hex = red)

      val plinth = new CylinderGeometry(pillarRadius * 1.5, pillarRadius * 1.7, 0.24, 10.0)
      plinth.translate(side * width / 2.0, 0.12, 0.0)
      placeInFrame(batch, plinth, frame, ahead, base, hex = Palette.stoneDark)
    }

    def beam(w: Double, h: Double, d: Double, centerY: Double, hex: Int): Unit =
      val geometry = new BoxGeometry(w, h, d)
      geometry.translate(0.0, centerY, 0.0)
      placeInFrame(batch, geometry, frame, ahead, base, hex = hex)

    beam(width + 0.5, 0.15, pillarRadius * 1.5, height * 0.72, VermilionDeep)
    beam(width + 1.0, 0.19, pillarRadius * 2.1, height - 0.16, VermilionDeep)
    beam(width * 0.6, 0.2, pillarRadius * 2.5, height + 0.04, red)

    val tipLength = (width + 1.15 - width * 0.6) / 2.0
    List(-1.0, 1.0).foreach { side =>
      val tip = new BoxGeometry(tipLength, 0.2, pillarRadius * 2.5)
      tip.rotateZ(-side * 0.19)
      tip.translate(side * (width * 0.3 + tipLength / 2.0), height + 0.04 + tipLength * 0.09, 0.0)
      placeInFrame(batch, tip, frame, ahead, base, outlined = true, hex = red)
    }

    beam(0.28, height * 0.24, pillarRadius * 1.7, height * 0.85, red)

    if !plain then
      shimenawa(batch, frame, ahead, base + height * 0.62, width - 0.3,
        pillarRadius * 1.6)
  end torii

  /** The 注連縄: a sagging straw rope slung between the pillars, with bound tufts and the 紙垂
    * paper zigzags hanging from it.
    */
  private def shimenawa(
    batch: CelBatch,
    frame: ShrineFrame,
    ahead: Double,
    centerY: Double,
    span: Double,
    ropeOffset: Double
  ): Unit =
    val sag  = 0.12
    val rope = 0.09

    def yAt(u: Double): Double = centerY - sag * math.sin(math.Pi * u)

    (0 until 8).foreach { index =>
      val u0      = index.toDouble / 8
      val u1      = (index + 1).toDouble / 8
      val x0      = -span / 2.0 + span * u0
      val x1      = -span / 2.0 + span * u1
      val slope   = math.atan2(yAt(u1) - yAt(u0), x1 - x0)
      val length  = math.hypot(x1 - x0, yAt(u1) - yAt(u0)) * 1.14
      val segment = new BoxGeometry(length, rope, rope)
      segment.rotateZ(slope)
      segment.translate((x0 + x1) / 2.0, (yAt(u0) + yAt(u1)) / 2.0, ropeOffset)
      geometryTo(batch, segment, frame)
    }

    List(0.25, 0.5, 0.75).foreach { u =>
      val tuft = new CylinderGeometry(rope * 1.35, rope * 1.35, rope * 0.7, 8.0)
      tuft.rotateZ(math.Pi / 2.0)
      tuft.translate(-span / 2.0 + span * u, yAt(u), ropeOffset)
      geometryTo(batch, tuft, frame, hex = 0xdfd2b4)
    }

    (0 until 4).foreach { index =>
      val u  = (index + 0.5) / 4.0
      val px = -span / 2.0 + span * u
      (0 until 6).foreach { fold =>
        val paper = new BoxGeometry(0.085, 0.075, 0.02)
        paper.rotateY(if fold % 2 == 0 then 0.6 else -0.6)
        paper.translate(px + (if fold % 2 == 0 then 0.022 else -0.022),
          yAt(u) - rope - 0.04 - fold * 0.062, ropeOffset)
        geometryTo(batch, paper, frame, hex = 0xfbf8f0)
      }
    }
  end shimenawa

  /** Places a fully world-authored local geometry (already carrying its own `y`) into the frame. */
  private def geometryTo(
    batch: CelBatch,
    geometry: BufferGeometry[?, ?],
    frame: ShrineFrame,
    ahead: Double = 0.0,
    hex: Int = Palette.rope
  ): Unit =
    geometry.translate(0.0, 0.0, ahead)
    geometry.rotateY(frame.facing)
    geometry.translate(frame.worldX(0.0, 0.0), 0.0, frame.worldZ(0.0, 0.0))
    batch.add(geometry, hex)

  /** The 石段: eleven solid treads of fixed rise and run, stone cheeks stacking up the sides,
    * and the pipe handrail up the west cheek.
    */
  private def stoneSteps(batch: CelBatch, frame: ShrineFrame): Unit =
    stairPlan(frame, TerrainHeightmap.hillAt).foreach { step =>
      val tread = new BoxGeometry(StairWidth, step.topY - step.bottomY, StairRun + 0.02)
      tread.rotateY(frame.facing)
      tread.translate(step.centerX, (step.topY + step.bottomY) / 2.0, step.centerZ)
      batch.add(tread, Palette.concrete)

      val nosing = new BoxGeometry(StairWidth + 0.04, 0.035, 0.06)
      nosing.translate(0.0, step.topY - 0.015, StairRun / 2.0 - 0.03)
      nosing.rotateY(frame.facing)
      nosing.translate(step.centerX, 0.0, step.centerZ)
      batch.add(nosing, Palette.stoneDark)
    }

    (0 until StairTreadCount).foreach { index =>
      val cheekHeight = StairRise * (index + 1) + 0.22
      List(-cheekOffset, cheekOffset).foreach { side =>
        val cheek = new BoxGeometry(0.3, cheekHeight, StairRun)
        cheek.translate(side, cheekHeight / 2.0, StairRun * (index + 0.5))
        placeInFrame(batch, cheek, frame, ahead = 0.0, baseY = 0.0, hex = Palette.stoneDark)
      }
    }

    val flightLength = StairRun * StairTreadCount
    val rail = new CylinderGeometry(0.032, 0.032,
      math.hypot(flightLength, TerraceTop) + 0.3, 7.0)
    rail.rotateX(math.Pi / 2.0 - math.atan2(TerraceTop, flightLength))
    rail.translate(-cheekOffset, TerraceTop / 2.0 + 0.95, flightLength / 2.0)
    geometryTo(batch, rail, frame, hex = 0x4c4a58)
    (0 to 3).foreach { index =>
      val t    = index / 3.0
      val post = new CylinderGeometry(0.03, 0.03, 0.95, 6.0)
      post.translate(-cheekOffset, StairRise * StairTreadCount * t + 0.47, flightLength * t)
      geometryTo(batch, post, frame, hex = 0x4c4a58)
    }
  end stoneSteps

  /** The fixed upstream flight: tread `i` tops out at `rise * (i + 1)` above the alley grade. */
  private[sakura] def stairPlan(
    originX: Double,
    originZ: Double,
    facing: Double,
    groundAt: (Double, Double) => Double
  ): Vector[StepPlan] =
    stairPlan(ShrineFrame(originX, originZ, facing), groundAt)

  private def stairPlan(frame: ShrineFrame, groundAt: (Double, Double) => Double): Vector[StepPlan] =
    (0 until StairTreadCount).toVector.map { index =>
      val centerX = frame.worldX(StairRun * (index + 0.5), 0.0)
      val centerZ = frame.worldZ(StairRun * (index + 0.5), 0.0)
      val terrain = groundAt(centerX, centerZ)
      StepPlan(centerX, centerZ, terrain, StairRise * (index + 1), terrain)
    }

  /** The terraced precinct: the stone pad at the top of the flight and the retaining walls with
    * their coping that enclose it, the south wall split for the stair mouth and the north wall
    * leaving the back gate to the onsen lane.
    */
  private def terrace(batch: CelBatch, frame: ShrineFrame): Unit =
    val pad = new BoxGeometry(terraceX1 - terraceX0, 0.07, terraceZ1 - terraceZ0)
    pad.translate((terraceX0 + terraceX1) / 2.0, TerraceTop - 0.035, (terraceZ0 + terraceZ1) / 2.0)
    batch.add(pad, 0x9aa0a8)

    val wallHeight = 2.9
    val wallBase   = TerraceTop + 0.5 - wallHeight
    def wall(x0: Double, x1: Double, z0: Double, z1: Double): Unit =
      val wallBox = new BoxGeometry(x1 - x0, wallHeight, z1 - z0)
      wallBox.translate((x0 + x1) / 2.0, wallBase + wallHeight / 2.0, (z0 + z1) / 2.0)
      batch.add(wallBox, Palette.stoneDark)
      val cap = new BoxGeometry(x1 - x0 + 0.1, 0.12, z1 - z0 + 0.1)
      cap.translate((x0 + x1) / 2.0, wallBase + wallHeight + 0.06, (z0 + z1) / 2.0)
      batch.add(cap, Palette.concrete)

    wall(terraceX0, -30.0, terraceZ0, terraceZ0 + 0.34)
    wall(-25.9, terraceX1, terraceZ0, terraceZ0 + 0.34)
    wall(terraceX0, terraceX0 + 0.34, terraceZ0, terraceZ1)
    wall(terraceX1 - 0.34, terraceX1, terraceZ0, terraceZ1)
    wall(terraceX0, -23.8, terraceZ1 - 0.34, terraceZ1)
    wall(-21.2, terraceX1, terraceZ1 - 0.34, terraceZ1)
  end terrace

  private def lanterns(batch: CelBatch, frame: ShrineFrame): Unit =
    val seats = List((-1.1, 1.0, false), (7.5, 0.95, true), (12.5, 1.05, true))
    seats.foreach { (ahead, scale, onTerrace) =>
      val base = if onTerrace then TerraceTop else TerrainHeightmap.groundY(frame.worldZ(ahead, 0.0))
      List(-lanternOffset, lanternOffset).foreach { side =>
        val x = frame.worldX(ahead, side)
        val z = frame.worldZ(ahead, side)

        lanternPart(batch, new CylinderGeometry(0.34 * scale, 0.4 * scale, 0.2 * scale, 8.0),
          x, base, z, scale, 0.1, Palette.concrete)
        lanternPart(batch, new CylinderGeometry(0.15 * scale, 0.19 * scale, 0.9 * scale, 8.0),
          x, base, z, scale, 0.65, Palette.concrete)
        lanternPart(batch, new CylinderGeometry(0.3 * scale, 0.22 * scale, 0.14 * scale, 8.0),
          x, base, z, scale, 1.17, Palette.concrete)

        (0 until 4).foreach { index =>
          val angle = index / 4.0 * math.Pi * 2.0 + math.Pi / 4.0
          val post  = new BoxGeometry(0.1 * scale, 0.42 * scale, 0.1 * scale)
          post.rotateY(-angle)
          post.translate(x + math.cos(angle) * 0.2 * scale,
            base + 1.45 * scale, z + math.sin(angle) * 0.2 * scale)
          batch.add(post, Palette.concrete)
        }
        lanternPart(batch, new CylinderGeometry(0.3 * scale, 0.3 * scale, 0.07 * scale, 8.0),
          x, base, z, scale, 1.69, Palette.concrete)
        val roof = new ConeGeometry(0.42 * scale, 0.34 * scale, 8.0)
        lanternPart(batch, roof, x, base, z, scale, 1.89, Palette.concrete)
        lanternPart(batch, new SphereGeometry(0.09 * scale, 8.0, 6.0),
          x, base, z, scale, 2.1, Palette.concrete)
        lanternPart(batch, new CylinderGeometry(0.16 * scale, 0.16 * scale, 0.4 * scale, 8.0),
          x, base, z, scale, 1.45, Palette.lanternGlow)
      }
    }
  end lanterns

  private def lanternPart(
    batch: CelBatch,
    geometry: BufferGeometry[?, ?],
    x: Double,
    base: Double,
    z: Double,
    scale: Double,
    height: Double,
    color: Int
  ): Unit =
    geometry.translate(x, base + height * scale, z)
    batch.add(geometry, color)

  /** The 参拝案内 post by the torii: a dark timber post carrying its notice plate. */
  private def noticePost(batch: CelBatch, frame: ShrineFrame): Unit =
    val post = new CylinderGeometry(0.045, 0.05, 1.9, 8.0)
    post.translate(0.0, 0.95, 0.0)
    placeInFrame(batch, post, frame, ahead = -1.5,
      baseY =
        TerrainHeightmap.groundY(frame.worldZ(-1.5, 2.9)),
      side = 2.9, hex = Palette.darkTimber)
    val plate = new BoxGeometry(0.06, 0.66, 0.5)
    plate.translate(0.0, 1.4, 0.0)
    placeInFrame(batch, plate, frame, ahead = -1.5,
      baseY =
        TerrainHeightmap.groundY(frame.worldZ(-1.5, 2.9)),
      side = 2.9, hex = Palette.plaster)
  end noticePost

  private def temizuya(batch: CelBatch, frame: ShrineFrame): Unit =
    val width   = 2.7
    val depth   = 2.2
    val ahead   = 8.9
    val side    = -5.3
    val centerX = frame.worldX(ahead, side)
    val centerZ = frame.worldZ(ahead, side)
    val placer  = WorldPlacer(batch, centerX, TerraceTop, centerZ, frame.facing)

    val stone = new GeometryBuilder
    stone.addBox(0.0, 0.08, 0.0, width + 0.6, 0.16, depth + 0.6)
    List(-1.0, 1.0).foreach { sx =>
      List(-1.0, 1.0).foreach { sz =>
        stone.addCylinder(sx * width / 2.0, 0.25, sz * depth / 2.0, 0.17, 0.19, 0.18, 8.0)
      }
    }
    stone.addBox(0.0, 0.47, 0.0, 1.6, 0.62, 0.9)
    stone.addBox(0.0, 0.78, 0.0, 1.74, 0.09, 1.04)
    stone.addBox(0.0, 0.70, 0.0, 1.34, 0.1, 0.64)
    placer(stone.result(), Palette.stone, (0.0, 0.0, 0.0), outlined = true)

    val timber = new GeometryBuilder
    List(-1.0, 1.0).foreach { sx =>
      List(-1.0, 1.0).foreach { sz =>
        timber.addCylinder(sx * width / 2.0, 0.16 + 2.35 / 2.0, sz * depth / 2.0,
          0.11, 0.12, 2.35, 8.0)
      }
      timber.addBox(sx * width / 2.0, 2.43, 0.0, 0.13, 0.16, depth + 0.2)
    }
    timber.addBox(0.0, 2.43, 0.0, width + 0.3, 0.15, 0.13)
    timber.addBox(0.0, 2.61, 0.0, width + 0.9, 0.2, depth + 0.9)
    placer(timber.result(), Palette.shrineWood, (0.0, 0.0, 0.0), outlined = true)

    val roof = new ConeGeometry(1.0, 1.0, 4.0)
    roof.rotateY(math.Pi / 4.0)
    roof.scale((width + 1.3) / 2.0, 0.7, (depth + 1.3) / 2.0)
    roof.translate(0.0, 3.31, 0.0)
    placer(roof, Palette.shrineRoof, (0.0, 0.0, 0.0), outlined = true)

    val bamboo = new GeometryBuilder
    bamboo.addBeam(0.0, 1.2, -0.86, 0.0, 1.07, -0.24, 0.045)
    bamboo.addBeam(0.0, 1.02, -0.55, 0.0, 0.82, -0.55, 0.05)
    List(-1.0, 1.0).foreach { sx =>
      bamboo.addCylinder(sx * 0.59, 0.98, 0.0, 0.026, 0.028, 0.3, 6.0)
    }
    bamboo.addBeam(-0.59, 1.08, 0.06, 0.59, 1.08, 0.06, 0.028)
    (0 until 3).foreach { index =>
      val x = -0.48 + index * 0.48
      bamboo.addBeam(x, 1.08, 0.1, x, 1.08, -0.16, 0.017)
      bamboo.addCylinder(x, 1.04, -0.16, 0.07, 0.085, 0.04, 8.0)
    }
    placer(bamboo.result(), Palette.bamboo, (0.0, 0.0, 0.0))
  end temizuya

  private def foxStatues(batch: CelBatch, frame: ShrineFrame): Unit =
    List(-1.0, 1.0).foreach { side =>
      val centerX = frame.worldX(13.1, side * 2.9)
      val centerZ = frame.worldZ(13.1, side * 2.9)
      val facing  = frame.facing + math.Pi + (if side > 0.0 then -0.4 else 0.4)
      val placer  = WorldPlacer(batch, centerX, TerraceTop, centerZ, facing)
      placer(new BoxGeometry(0.56, 0.62, 0.46), Palette.stoneDark, (0.0, 0.31, 0.0), outlined = true)
      placer(new BoxGeometry(0.64, 0.07, 0.54), Palette.stone, (0.0, 0.65, 0.0))
      placer(new CylinderGeometry(0.11, 0.2, 0.5, 8.0), Palette.stone, (0.0, 0.93, 0.0))

      val chest = new SphereGeometry(0.14, 10.0, 7.0)
      chest.scale(1.0, 0.9, 1.1)
      placer(chest, Palette.stone, (0.0, 0.86, 0.11))
      val head = new SphereGeometry(0.12, 10.0, 8.0)
      head.scale(1.0, 0.95, 1.05)
      placer(head, Palette.stone, (0.0, 1.26, 0.03))
      val muzzle = new ConeGeometry(0.07, 0.2, 6.0)
      muzzle.rotateX(math.Pi / 2.0 + 0.12)
      placer(muzzle, Palette.stone, (0.0, 1.22, 0.17))

      List(-1.0, 1.0).foreach { earSide =>
        val ear = new ConeGeometry(0.055, 0.16, 4.0)
        ear.rotateZ(earSide * 0.16)
        placer(ear, Palette.stone, (earSide * 0.07, 1.4, -0.01))
        placer(new SphereGeometry(0.017, 6.0, 5.0), Palette.stoneDark, (earSide * 0.05, 1.28, 0.11))
        placer(new CylinderGeometry(0.035, 0.042, 0.34, 6.0), Palette.stone, (earSide * 0.075, 0.86, 0.15))
      }
      val tail = new TorusGeometry(0.16, 0.055, 5.0, 12.0, math.Pi * 1.1)
      tail.rotateX(math.Pi / 2.0)
      tail.rotateZ(-0.5)
      placer(tail, Palette.stone, (0.0, 1.06, -0.2))
      val bib = new PlaneGeometry(0.22, 0.2)
      bib.rotateX(0.2)
      placer(bib, Palette.shrineBib, (0.0, 1.06, 0.15), bothFaces = true)
      placer(new BoxGeometry(0.2, 0.03, 0.03), VermilionDeep, (0.0, 1.16, 0.1))
    }
  end foxStatues

  private def emaRack(batch: CelBatch, frame: ShrineFrame): Unit =
    val length  = 3.2
    val height  = 1.85
    val centerX = frame.worldX(11.1, 5.3)
    val centerZ = frame.worldZ(11.1, 5.3)
    val placer  = WorldPlacer(batch, centerX, TerraceTop, centerZ, frame.facing - math.Pi / 2.0)

    val rack = new GeometryBuilder
    List(-1.0, 1.0).foreach { side =>
      rack.addBox(side * length / 2.0, height / 2.0, 0.0, 0.13, height, 0.13)
      rack.addBox(side * length / 2.0, 0.08, 0.0, 0.2, 0.16, 0.5)
    }
    List(height - 0.08, height * 0.55).foreach { y =>
      rack.addBox(0.0, y, 0.0, length + 0.3, 0.1, 0.12)
    }
    rack.addBox(0.0, height + 0.12, 0.06, length + 0.5, 0.08, 0.6)
    placer(rack.result(), Palette.shrineWoodDark, (0.0, 0.0, 0.0), outlined = true)

    val plaques = new GeometryBuilder
    val writing = new GeometryBuilder
    val random  = new Mulberry32(7)
    val count   = math.floor((length - 0.4) / 0.24).toInt
    (0 until 2).foreach { row =>
      (0 until count).foreach { index =>
        if random.next() >= 0.12 then
          val x = -(length - 0.4) / 2.0 + index * 0.24 + random.range(-0.015, 0.015)
          val y = (if row == 0 then height * 0.55 - 0.24 else height - 0.24) + random.range(-0.01, 0.01)
          plaques.addBox(x, y, 0.06, 0.2, 0.15, 0.02)
          writing.addBox(x, y + 0.018, 0.046, 0.11, 0.012, 0.008)
          writing.addBox(x + 0.025, y - 0.018, 0.046, 0.055, 0.01, 0.008)
      }
    }
    placer(plaques.result(), Palette.shrineWood, (0.0, 0.0, 0.0))
    placer(writing.result(), Palette.darkTimber, (0.0, 0.0, 0.0))
  end emaRack

  private def omikujiStand(batch: CelBatch, frame: ShrineFrame): Unit =
    val x      = frame.worldX(7.5, 4.7)
    val z      = frame.worldZ(7.5, 4.7)
    val placer = WorldPlacer(batch, x, TerraceTop, z, frame.facing - math.Pi / 2.0)
    val wood   = new GeometryBuilder
    wood.addBox(0.0, 0.08, 0.0, 0.7, 0.16, 0.5)
    wood.addBox(0.0, 0.55, 0.0, 0.12, 0.9, 0.12)
    wood.addBox(0.0, 1.02, 0.0, 0.9, 0.1, 0.12)
    placer(wood.result(), Palette.shrineWoodDark, (0.0, 0.0, 0.0), outlined = true)
    val papers = new GeometryBuilder
    (0 until 5).foreach { index =>
      papers.addBox(-0.28 + index * 0.14, 0.84, 0.04, 0.09, 0.34, 0.025)
    }
    placer(papers.result(), Palette.plaster, (0.0, 0.0, 0.0))
  end omikujiStand

  private def decorations(
    frame: ShrineFrame,
    textures: TextureCache,
    curvature: PlanetCurvature
  ): SceneAssembly =
    val group  = new Group()
    val waterX = frame.worldX(8.9, -5.3)
    val waterZ = frame.worldZ(8.9, -5.3)

    val waterGeometry = new PlaneGeometry(1.32, 0.64)
    waterGeometry.rotateX(-math.Pi / 2.0)
    waterGeometry.rotateY(frame.facing)
    waterGeometry.translate(waterX, TerraceTop + 0.16 + 0.62 - 0.02, waterZ)
    WorldGeometry.bend(waterGeometry, curvature)
    val waterMaterial = new MeshBasicMaterial()
    waterMaterial.color.setHex(Palette.water.toDouble)
    waterMaterial.transparent = true
    waterMaterial.opacity = 0.78
    waterMaterial.depthWrite = false
    val water = new Mesh(waterGeometry, waterMaterial)
    water.renderOrder = 1.0
    ThreeInterop.setUserDataFlag(water, "noOutline")
    ThreeInterop.addChild(group, water)

    val highlightGeometry = new PlaneGeometry(0.52, 0.16)
    highlightGeometry.rotateX(-math.Pi / 2.0)
    highlightGeometry.rotateY(frame.facing + 0.4)
    highlightGeometry.translate(waterX - 0.24, TerraceTop + 0.16 + 0.62 - 0.019, waterZ - 0.1)
    WorldGeometry.bend(highlightGeometry, curvature)
    val highlightMaterial = new MeshBasicMaterial()
    highlightMaterial.color.setHex(0xfbf8f0.toDouble)
    highlightMaterial.transparent = true
    highlightMaterial.opacity = 0.4
    highlightMaterial.depthWrite = false
    val highlight = new Mesh(highlightGeometry, highlightMaterial)
    highlight.renderOrder = 2.0
    ThreeInterop.setUserDataFlag(highlight, "noOutline")
    ThreeInterop.addChild(group, highlight)

    val noticeX        = frame.worldX(-1.5, 2.9)
    val noticeZ        = frame.worldZ(-1.5, 2.9)
    val noticeGeometry = new PlaneGeometry(0.46, 0.66)
    noticeGeometry.rotateY(frame.facing + math.Pi)
    noticeGeometry.translate(noticeX, TerrainHeightmap.groundY(noticeZ) + 1.4, noticeZ)
    WorldGeometry.bend(noticeGeometry, curvature)
    val noticeTexture  = textures.get(ProceduralTextures.UtilityWarningKey)
    val noticeMaterial = new MeshBasicMaterial(MeshBasicMaterialParameters(map = js.defined(noticeTexture)))
    val notice         = new Mesh(noticeGeometry, noticeMaterial)
    ThreeInterop.setUserDataFlag(notice, "noOutline")
    ThreeInterop.addChild(group, notice)

    val clusters = List(
      DappleCluster(12.5, -5.1, 2.2, 2.4, 8, onTerrace = true),
      DappleCluster(13.9, 3.9, 2.0, 2.4, 8, onTerrace = true),
      DappleCluster(7.1, -2.1, 1.8, 2.4, 8, onTerrace = true),
      DappleCluster(16.5, -7.1, 2.4, 2.4, 8, onTerrace = true),
      DappleCluster(8.5, 5.9, 1.6, 2.4, 8, onTerrace = true),
      DappleCluster(-4.1, 0.0, 1.9, 2.6, 7, onTerrace = false)
    )
    val dappleGeometry = new CircleGeometry(1.0, 10.0)
    dappleGeometry.rotateX(-math.Pi / 2.0)
    val dappleMaterial = new MeshBasicMaterial()
    dappleMaterial.color.setHex(0x8a7fae.toDouble)
    dappleMaterial.transparent = true
    dappleMaterial.opacity = 0.14
    dappleMaterial.depthWrite = false
    val dapple    = new InstancedMesh(dappleGeometry, dappleMaterial, clusters.map(_.count).sum.toDouble)
    val transform = new Object3D()
    val random    = new Mulberry32(2903)
    var index     = 0
    clusters.foreach { cluster =>
      (0 until cluster.count).foreach { _ =>
        val radius = cluster.radius * random.range(0.55, 1.35)
        val x      = frame.worldX(cluster.ahead, cluster.side) + random.range(-cluster.spread, cluster.spread)
        val z      = frame.worldZ(cluster.ahead, cluster.side) + random.range(-cluster.spread, cluster.spread)
        val base   = if cluster.onTerrace then TerraceTop else TerrainHeightmap.groundY(z)
        transform.position.set(x, curvature.bend(x, base + 0.03, z), z)
        transform.rotation.set(0.0, random.range(0.0, 3.0), 0.0)
        transform.scale.set(radius, 1.0, radius * random.range(0.7, 1.1))
        transform.updateMatrix()
        dapple.setMatrixAt(index, transform.matrix)
        index += 1
      }
    }
    ThreeInterop.markForUpload(dapple.instanceMatrix)
    ThreeInterop.setUserDataFlag(dapple, "noOutline")
    dapple.renderOrder = 1.0
    ThreeInterop.addChild(group, dapple)

    val lanternWindows = new GeometryBuilder
    List((-1.1, 1.0, false), (7.5, 0.95, true), (12.5, 1.05, true)).foreach {
      case (ahead, scale, onTerrace) =>
        val base = if onTerrace then TerraceTop else TerrainHeightmap.groundY(frame.worldZ(ahead, 0.0))
        List(-lanternOffset, lanternOffset).foreach { side =>
          val x      = frame.worldX(ahead + 0.205 * scale, side)
          val z      = frame.worldZ(ahead + 0.205 * scale, side)
          val window = new PlaneGeometry(0.14 * scale, 0.22 * scale)
          window.rotateY(frame.facing)
          window.translate(x, base + 1.45 * scale, z)
          lanternWindows.addMerged(window)
          window.dispose()
        }
    }
    val lanternGeometry = lanternWindows.result()
    WorldGeometry.bend(lanternGeometry, curvature)
    val lanternMaterial = new MeshBasicMaterial()
    lanternMaterial.color.setHex(Palette.lanternGlow.toDouble)
    lanternMaterial.toneMapped = false
    val lanternGlow = new Mesh(lanternGeometry, lanternMaterial)
    ThreeInterop.setUserDataFlag(lanternGlow, "noOutline")
    ThreeInterop.addChild(group, lanternGlow)

    SceneAssembly(
      group,
      List(waterGeometry, highlightGeometry, noticeGeometry, dappleGeometry, lanternGeometry),
      List(waterMaterial, highlightMaterial, noticeMaterial, dappleMaterial, lanternMaterial)
    )
  end decorations

  /** The 社殿: a stone plinth with three front steps, an open-fronted timber hall with lattice
    * side panels and a dark interior recess, under the deep two-slab upswept roof that carries
    * its 鰹木 billets and crossed 千木 finials. Authored facing +z and turned to face the steps.
    */
  private def hall(batch: CelBatch, frame: ShrineFrame): Unit =
    val width      = 5.4
    val depth      = 4.4
    val plinth     = 0.66
    val wallHeight = 2.5
    val ahead      = 15.7
    val centerX    = frame.worldX(ahead, 0.0)
    val centerZ    = frame.worldZ(ahead, 0.0)
    val placer     = WorldPlacer(batch, centerX, TerraceTop, centerZ, frame.facing + math.Pi)

    placer(new BoxGeometry(width + 0.9, plinth, depth + 0.9), Palette.concrete, (0.0, plinth / 2.0, 0.0))
    placer(new BoxGeometry(width + 1.1, 0.1, depth + 1.1), Palette.concrete, (0.0, plinth + 0.05, 0.0))
    (0 until 3).foreach { step =>
      val height = plinth * (3 - step) / 3.0
      placer(new BoxGeometry(2.6, height, 0.34), Palette.concrete,
        (0.0, height / 2.0, (depth + 0.9) / 2.0 + 0.17 + step * 0.34))
    }

    val floorTop = plinth + 0.1
    List(-1.0, 1.0).foreach { sx =>
      List(-1.0, 1.0).foreach { sz =>
        placer(new CylinderGeometry(0.15, 0.16, wallHeight, 8.0), Palette.darkTimber,
          (sx * (width / 2.0 - 0.15), floorTop + wallHeight / 2.0, sz * (depth / 2.0 - 0.15)))
      }
      placer(new CylinderGeometry(0.13, 0.14, wallHeight, 8.0), Palette.darkTimber,
        (sx * 0.95, floorTop + wallHeight / 2.0, depth / 2.0 - 0.15))
    }
    placer(new BoxGeometry(width - 0.2, wallHeight, 0.16), Palette.darkTimber,
      (0.0, floorTop + wallHeight / 2.0, -(depth / 2.0 - 0.1)), outlined = true)
    List(-1.0, 1.0).foreach { sx =>
      placer(new BoxGeometry(0.16, wallHeight, depth - 0.2), Palette.darkTimber,
        (sx * (width / 2.0 - 0.1), floorTop + wallHeight / 2.0, 0.0), outlined = true)
    }

    placer(new BoxGeometry(width, 0.22, 0.2), Palette.darkTimber, (0.0, floorTop + wallHeight - 0.11, depth / 2.0 - 0.14))
    placer(new BoxGeometry(width, 0.16, 0.24), Palette.darkTimber, (0.0, floorTop + 0.08, depth / 2.0 - 0.14))
    List(-1.0, 1.0).foreach { sx =>
      placer(new BoxGeometry(1.3, wallHeight - 0.4, 0.1), Palette.timber,
        (sx * 1.85, floorTop + wallHeight / 2.0, depth / 2.0 - 0.14))
      (0 until 7).foreach { batten =>
        placer(new BoxGeometry(0.05, wallHeight - 0.5, 0.05), Palette.darkTimber,
          (sx * 1.85 - 0.55 + batten * 0.18, floorTop + wallHeight / 2.0, depth / 2.0 - 0.07))
      }
    }
    placer(new BoxGeometry(1.9, wallHeight - 0.4, 0.08), 0x4c4656,
      (0.0, floorTop + wallHeight / 2.0 - 0.1, depth / 2.0 - 0.7))
    placer(new CylinderGeometry(0.24, 0.24, 0.05, 16.0), 0xb8b0a0, (0.0, floorTop + 1.35, depth / 2.0 - 0.62),
      tilt = math.Pi / 2.0)
    placer(new BoxGeometry(0.1, 0.5, 0.1), Palette.darkTimber, (0.0, floorTop + 0.95, depth / 2.0 - 0.62))

    val eave      = 1.15
    val roofWidth = width + eave * 2.0
    val roofDepth = depth + eave * 2.0
    val roofRise  = 1.55
    val slope     = math.atan2(roofRise, roofWidth / 2.0)
    val slab      = math.hypot(roofWidth / 2.0, roofRise) + 0.1
    val roofBase  = floorTop + wallHeight

    placer(new BoxGeometry(width + 0.7, 0.26, depth + 0.7), Palette.darkTimber, (0.0, roofBase + 0.13, 0.0))
    (0 until 13).foreach { rafter =>
      placer(new BoxGeometry(0.08, 0.14, roofDepth - 0.4), Palette.darkTimber,
        (-roofWidth / 2.0 + 0.4 + rafter * (roofWidth - 0.8) / 12.0, roofBase + 0.34, 0.0))
    }
    List(-1.0, 1.0).foreach { sx =>
      val slabPart = new BoxGeometry(slab, 0.3, roofDepth)
      slabPart.rotateZ(-sx * slope)
      placer(slabPart, Palette.roofTile, (sx * (roofWidth / 4.0), roofBase + 0.4 + roofRise / 2.0, 0.0), outlined = true)
      val tip = new BoxGeometry(0.8, 0.26, roofDepth)
      tip.rotateZ(-sx * (slope - 0.34))
      placer(tip, Palette.roofTile, (sx * (roofWidth / 2.0 + 0.24), roofBase + 0.46, 0.0))
    }
    placer(new BoxGeometry(0.5, 0.34, roofDepth + 0.2), Palette.roofTile, (0.0, roofBase + 0.4 + roofRise + 0.1, 0.0))
    (0 until 4).foreach { billet =>
      val log = new CylinderGeometry(0.11, 0.11, 0.62, 8.0)
      log.rotateZ(math.Pi / 2.0)
      placer(log, Palette.darkTimber,
        (0.0, roofBase + 0.4 + roofRise + 0.34, -roofDepth / 2.0 + 1.2 + billet * (roofDepth - 2.4) / 3.0))
    }
    List(-1.0, 1.0).foreach { gableSide =>
      List(-1.0, 1.0).foreach { sx =>
        val finial = new BoxGeometry(0.11, 1.5, 0.11)
        finial.rotateZ(sx * 0.28)
        placer(finial, Palette.darkTimber,
          (sx * 0.34, roofBase + 0.4 + roofRise + 0.5, gableSide * (roofDepth / 2.0 - 0.3)))
      }
    }
  end hall

  private def bambooClusters(batch: CelBatch, frame: ShrineFrame): Unit =
    bambooClumps.foreach { clump =>
      val rng     = new Mulberry32(clump.seed)
      val centerX = frame.worldX(clump.ahead, clump.side)
      val centerZ = frame.worldZ(clump.ahead, clump.side)
      val baseY   = if clump.onTerrace then TerraceTop else TerrainHeightmap.groundY(centerZ)
      (0 until clump.count).foreach { index =>
        val height        = (4.2 + rng.range(-1.0, 1.4)) * clump.scale
        val radius        = 0.045 * clump.scale * rng.range(0.85, 1.2)
        val x             = centerX + rng.range(-1.0, 1.0) * clump.spread
        val z             = centerZ + rng.range(-1.0, 1.0) * clump.spread
        val lean          = rng.range(-0.07, 0.07)
        val leanDirection = rng.range(0.0, 6.3)
        addBambooPart(batch, new CylinderGeometry(1.0, 1.16, 1.0, 5.0, 1.0),
          x, baseY + height / 2.0, z, lean * math.sin(leanDirection), 0.0,
          -lean * math.cos(leanDirection), radius, height, radius,
          if index % 2 == 0 then Palette.bamboo else Palette.bambooDeep)

        (1 to 4).foreach { node =>
          val fraction = node / 5.0
          addBambooPart(batch, new CylinderGeometry(1.25, 1.25, 1.0, 5.0, 1.0),
            x + lean * math.sin(leanDirection) * (height * fraction - height / 2.0),
            baseY + height * fraction,
            z - lean * math.cos(leanDirection) * (height * fraction - height / 2.0),
            0.0, 0.0, 0.0, radius, 0.035 * clump.scale, radius, Palette.bambooNode)
        }

        (0 until 4).foreach { leaf =>
          val leafY      = baseY + height * rng.range(0.66, 1.0)
          val leafRadius = 0.34 * clump.scale * rng.range(0.7, 1.2)
          val leafX      = x + rng.range(-0.5, 0.5) * clump.scale
          val leafZ      = z + rng.range(-0.5, 0.5) * clump.scale
          val rotationX  = rng.range(0.0, 3.0)
          val rotationY  = rng.range(0.0, 3.0)
          val rotationZ  = rng.range(0.0, 3.0)
          addBambooPart(batch, new IcosahedronGeometry(1.0, 0.0),
            leafX, leafY, leafZ, rotationX, rotationY, rotationZ,
            leafRadius, leafRadius * 0.42, leafRadius * 1.25,
            if leaf % 2 == 0 then Palette.bambooLeaf else Palette.bambooDeep)
        }
      }
    }

  private def addBambooPart(
    batch: CelBatch,
    geometry: BufferGeometry[?, ?],
    x: Double,
    y: Double,
    z: Double,
    rotationX: Double,
    rotationY: Double,
    rotationZ: Double,
    scaleX: Double,
    scaleY: Double,
    scaleZ: Double,
    color: Int
  ): Unit =
    geometry.scale(scaleX, scaleY, scaleZ)
    geometry.rotateX(rotationX)
    geometry.rotateY(rotationY)
    geometry.rotateZ(rotationZ)
    geometry.translate(x, y, z)
    batch.add(geometry, color)
  end addBambooPart

  /** The evergreen grove around the precinct: broad dark canopies on short trunks, the cool
    * backdrop the shrine is planted against.
    */
  private def grove(batch: CelBatch, frame: ShrineFrame): Unit =
    val trees = List(
      (-20.8, 43.2, 2.5), (-26.6, 45.8, 1.9), (-31.4, 42.6, 1.6), (-36.6, 42.0, 1.45), (-39.6, 46.6, 1.8),
      (-17.2, 34.8, 1.7), (-16.8, 27.6, 1.5), (-23.6, 21.2, 1.6), (-32.6, 16.8, 1.6), (-40.8, 22.4, 1.8)
    )
    trees.foreach { (x, z, scale) =>
      val ground = TerrainHeightmap.groundY(z)
      val trunk  = new CylinderGeometry(0.22 * scale, 0.3 * scale, 3.4 * scale, 7.0)
      trunk.translate(x, ground + 1.7 * scale, z)
      batch.add(trunk, Palette.timber)
      List((0.0, 4.6), (1.3, 4.1), (-1.2, 4.2), (0.5, 5.4)).foreach { (dx, dy) =>
        val canopy = new SphereGeometry(1.7 * scale, 9.0, 7.0)
        canopy.scale(1.0, 0.8, 1.0)
        canopy.translate(x + dx * scale, ground + dy * scale, z + dx * 0.4 * scale)
        batch.add(canopy, GroveGreen)
      }
    }

  /** A drift of fallen blossom on the alley and the terrace edge. */
  private def fallenPetals(batch: CelBatch, frame: ShrineFrame): Unit =
    val spots = ProceduralTextures.PetalScatter.placements(48, OriginX - 1.6, 15.0, 3.2, 8.0,
      seed = 0x5a17)
    spots.foreach { (x, z) =>
      val petal = new BoxGeometry(0.11, 0.012, 0.09)
      petal.translate(x, TerrainHeightmap.groundY(z) + 0.045, z)
      batch.add(petal, Palette.sakuraDeep)
    }
    val terraceSpots = ProceduralTextures.PetalScatter.placements(36, OriginX - 2.4, 21.8, 4.8,
      4.4, seed = 0x5a18)
    terraceSpots.foreach { (x, z) =>
      val petal = new BoxGeometry(0.11, 0.012, 0.09)
      petal.translate(x, TerraceTop + 0.038, z)
      batch.add(petal, Palette.sakuraDeep)
    }
  end fallenPetals

end ShrineAssembly
