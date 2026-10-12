package sakura.world

import sakura.ThreeInterop
import sakura.core.{Pal, Textures, Toon, Util}
import vendor.three.*

import scala.collection.mutable

/** Ground works, ported 1:1 from upstream `src/world/ground.js`.
  *
  * The outlying districts all need the same civil engineering: paved pads,
  * narrow lanes, block boundary walls, mesh fencing, pipe railings and flights
  * of steps. Doing them once here is what keeps the districts reading as one
  * town rather than four separate models.
  */
object Ground:

  final case class GroundMaterials(
    concrete: MeshToonMaterial,
    concreteMid: MeshToonMaterial,
    concreteDark: MeshToonMaterial,
    asphalt: MeshToonMaterial,
    asphaltWorn: MeshToonMaterial,
    curb: MeshToonMaterial,
    sidewalk: MeshToonMaterial,
    sidewalkAlt: MeshToonMaterial,
    metal: MeshToonMaterial,
    metalDark: MeshToonMaterial,
    white: MeshToonMaterial,
    dirt: MeshToonMaterial,
    clay: MeshToonMaterial,
    stone: MeshToonMaterial,
    stoneDark: MeshToonMaterial,
    stoneWarm: MeshToonMaterial,
    grass: MeshToonMaterial,
    gravel: MeshToonMaterial
  )

  private var materialsCache: Option[GroundMaterials] = None

  def groundMats(): GroundMaterials =
    materialsCache.getOrElse {
      val materials = GroundMaterials(
        concrete = Toon.cel(Toon.CelOptions(color = Pal.concrete, bands = 3, tint = 0x6f6790)),
        concreteMid = Toon.cel(Toon.CelOptions(color = Pal.concreteMid, bands = 3, tint = 0x6a6288)),
        concreteDark = Toon.cel(Toon.CelOptions(color = Pal.concreteDark, bands = 3, tint = 0x655d84)),
        asphalt = Toon.cel(Toon.CelOptions(color = Pal.road, bands = 3, tint = 0x6a608f)),
        asphaltWorn = Toon.cel(Toon.CelOptions(color = Pal.roadWorn, bands = 3, tint = 0x6a608f)),
        curb = Toon.cel(Toon.CelOptions(color = Pal.curb, bands = 3, tint = 0x6f6790)),
        sidewalk = Toon.cel(Toon.CelOptions(color = Pal.sidewalk, bands = 3, tint = 0x7d74a0)),
        sidewalkAlt = Toon.cel(Toon.CelOptions(color = Pal.sidewalkAlt, bands = 3, tint = 0x7d74a0)),
        metal = Toon.cel(Toon.CelOptions(color = Pal.metal, bands = 3, tint = 0x666090)),
        metalDark = Toon.cel(Toon.CelOptions(color = Pal.metalDark, bands = 3, tint = 0x5c5680)),
        white = Toon.cel(Toon.CelOptions(color = Pal.lineWhite, bands = 2, tint = 0x8e86ad)),
        dirt = Toon.cel(Toon.CelOptions(color = Pal.dirt, bands = 3, tint = 0x7a7396)),
        clay = Toon.cel(Toon.CelOptions(color = Pal.clay, bands = 3, tint = 0x7d6f8f)),
        stone = Toon.cel(Toon.CelOptions(color = Pal.stone, bands = 3, tint = 0x655d80)),
        stoneDark = Toon.cel(Toon.CelOptions(color = Pal.stoneDark, bands = 3, tint = 0x605878)),
        stoneWarm = Toon.cel(Toon.CelOptions(color = Pal.stoneWarm, bands = 3, tint = 0x655d80)),
        grass = Toon.cel(Toon.CelOptions(color = Pal.grass, bands = 3, tint = 0x5b6f8c)),
        gravel = Toon.cel(Toon.CelOptions(color = Pal.gravel, bands = 3, tint = 0x6a6288))
      )
      materialsCache = Some(materials)
      materials
    }

  final case class PadOptions(
    x: Double,
    z: Double,
    w: Double,
    d: Double,
    h: Double = 0.07,
    y: Double = 0.0,
    mat: Option[Material[?]] = None,
    ry: Double = 0.0,
    name: String = "pad",
    platform: Boolean = true
  )

  /** A paved pad, built as a shallow slab rather than a plane so its edge catches
    * the ink pass; that thin line round a forecourt is most of what makes paving
    * read as paving.
    */
  def pad(ctx: WorldCtx, options: PadOptions): Mesh[?, ?, ?] =
    val m = groundMats()
    val h = options.h
    val slab = Util.box(options.w, h, options.d, options.mat.getOrElse(m.concrete),
      options.x, options.y + h / 2.0, options.z)
    slab.receiveShadow = true
    if options.ry != 0.0 then slab.rotation.y = options.ry
    slab.name = options.name
    ctx.add(slab)
    if options.platform then
      ctx.platform(
        x0 = options.x - options.w / 2.0, x1 = options.x + options.w / 2.0,
        z0 = options.z - options.d / 2.0, z1 = options.z + options.d / 2.0,
        top = options.y + h
      )
    slab

  final case class LaneOptions(
    axis: String,
    from: Double,
    to: Double,
    at: Double,
    w: Double = 3.6,
    rise: Double = 0.05,
    y: Double = Double.NaN,
    mat: Option[Material[?]] = None,
    kerb: Boolean = true,
    platform: Boolean = true,
    name: String = "lane"
  )

  /** A narrow lane. `axis` "x" runs along X at a fixed z (level ground, one box);
    * "z" runs along Z and is swept with `makeStrip` so it follows the street's
    * slope.
    */
  def lane(ctx: WorldCtx, options: LaneOptions): Group[Object3DEventMap] =
    val m    = groundMats()
    val mat  = options.mat.getOrElse(m.asphalt)
    val w    = options.w
    val rise = options.rise
    val g    = new Group()

    if options.axis == "z" then
      // makeStrip's winding assumes ascending z; sweeping the other way puts the
      // front faces underground
      val z0   = math.min(options.from, options.to)
      val z1   = math.max(options.from, options.to)
      val a    = (z: Double) => Street.StripEdge(options.at - w / 2.0, Street.groundY(z) + rise)
      val b    = (z: Double) => Street.StripEdge(options.at + w / 2.0, Street.groundY(z) + rise)
      val mesh = new Mesh(Street.makeStrip(Street.StripOptions(z0 = z0, z1 = z1, a = a, b = b, step = 1.4)), mat)
      mesh.receiveShadow = true
      ThreeInterop.addChild(g, mesh)
      if options.kerb then
        Vector(-1.0, 1.0).foreach { s =>
          val ka = (z: Double) => Street.StripEdge(options.at + s * (w / 2.0), Street.groundY(z) + rise)
          val kb = (z: Double) => Street.StripEdge(options.at + s * (w / 2.0 + 0.26), Street.groundY(z) + rise + 0.1)
          val km = new Mesh(
            Street.makeStrip(Street.StripOptions(z0 = z0, z1 = z1, a = ka, b = kb,
              step = 1.4, flip = s < 0)),
            m.curb
          )
          km.receiveShadow = true
          ThreeInterop.addChild(g, km)
        }
    else
      val y    = if options.y.isNaN then Street.groundY(options.at) else options.y
      val len  = math.abs(options.to - options.from)
      val cx   = (options.from + options.to) / 2.0
      val mesh = Util.box(len, 0.06, w, mat, cx, y + rise, options.at)
      mesh.receiveShadow = true
      ThreeInterop.addChild(g, mesh)
      if options.kerb then
        Vector(-1.0, 1.0).foreach { s =>
          val km = Util.box(len, 0.16, 0.26, m.curb, cx, y + rise + 0.02, options.at + s * (w / 2.0 + 0.13))
          km.receiveShadow = true
          ThreeInterop.addChild(g, km)
        }
    end if
    g.name = options.name
    ctx.add(g)

    if options.platform && options.axis != "z" then
      val y = if options.y.isNaN then Street.groundY(options.at) else options.y
      ctx.platform(
        x0 = math.min(options.from, options.to), x1 = math.max(options.from, options.to),
        z0 = options.at - w / 2.0, z1 = options.at + w / 2.0, top = y + rise + 0.06
      )
    g
  end lane

  final case class LineOptions(
    axis: String,
    from: Double,
    to: Double,
    at: Double,
    dash: Double = 0.0,
    y: Double = 0.09,
    mat: Option[Material[?]] = None
  )

  /** A dashed or solid painted line on a lane. */
  def laneLine(ctx: WorldCtx, options: LineOptions): Mesh[?, ?, ?] =
    val m    = groundMats()
    val len  = math.abs(options.to - options.from)
    val dash = options.dash
    val parts =
      if dash != 0.0 then
        val n = math.max(1.0, math.floor(len / (dash * 2.0))).toInt
        (0 until n).map { i =>
          val t = options.from + (options.to - options.from) * ((i + 0.25) / n)
          Util.BakePart(
            geometry = new BoxGeometry(if options.axis == "x" then dash else 0.1, 0.02,
              if options.axis == "x" then 0.1 else dash),
            matrix = Some(if options.axis == "x" then Util.trs(t, 0.0, options.at) else Util.trs(options.at, 0.0, t))
          )
        }.toList
      else
        List(Util.BakePart(
          geometry = new BoxGeometry(if options.axis == "x" then len else 0.1, 0.02,
            if options.axis == "x" then 0.1 else len),
          matrix = Some(if options.axis == "x" then Util.trs((options.from + options.to) / 2.0, 0.0, options.at)
          else Util.trs(options.at, 0.0, (options.from + options.to) / 2.0))
        ))
    val mesh = new Mesh(Util.bake(parts), options.mat.getOrElse(m.white))
    mesh.position.y = options.y
    ThreeInterop.setUserDataFlag(mesh, "noOutline")
    ctx.add(mesh)
    mesh
  end laneLine

  final case class StepsOptions(
    x: Double,
    z: Double,
    n: Int = 8,
    rise: Double = 0.19,
    run: Double = 0.42,
    w: Double = 2.6,
    y: Double = 0.0,
    dir: Int = -1,
    axis: String = "z",
    mat: Option[Material[?]] = None,
    lipMat: Option[Material[?]] = None
  )

  final case class StepsResult(group: Group[Object3DEventMap], top: Double, end: Double)

  /** A flight of steps climbing along one axis. Each tread is a block rising
    * from the base, so the flight reads as cut stone rather than a floating
    * staircase, and each one registers a platform so the height query carries
    * the player up. The treads overlap by 40 mm: `heightAt`'s platform test is
    * strictly exclusive on all four sides, so a query on the joint between two
    * treads must not fall through to the natural grade.
    */
  def steps(ctx: WorldCtx, options: StepsOptions): StepsResult =
    val m     = groundMats()
    val n     = options.n
    val rise  = options.rise
    val run   = options.run
    val w     = options.w
    val y     = options.y
    val dir   = options.dir
    val axis  = options.axis
    val mat   = options.mat.getOrElse(m.stone)
    val parts = mutable.ArrayBuffer.empty[Util.BakePart]
    val edge  = mutable.ArrayBuffer.empty[Util.BakePart]

    (0 until n).foreach { i =>
      val h = rise * (i + 1)
      val t = run * (i + 0.5) * dir
      val along =
        if axis == "z" then Util.trs(options.x, h / 2.0, options.z + t) else Util.trs(options.x + t, h / 2.0, options.z)
      parts += Util.BakePart(
        geometry = new BoxGeometry(if axis == "z" then w else run, h, if axis == "z" then run else w),
        matrix = Some(along)
      )
      // nosing: a slightly lighter lip so each tread separates tonally
      val lipT = run * (i + 1) * dir - 0.03 * dir
      edge += Util.BakePart(
        geometry = new BoxGeometry(if axis == "z" then w + 0.04 else 0.06, 0.035, if axis == "z" then 0.06 else w + 0.04),
        matrix = Some(if axis == "z" then Util.trs(options.x, h - 0.015, options.z + lipT)
        else Util.trs(options.x + lipT, h - 0.015, options.z))
      )

      val pad = 0.02
      val t0  = run * i * dir - pad * dir
      val t1  = run * (i + 1) * dir + pad * dir
      if axis == "z" then
        ctx.platform(
          x0 = options.x - w / 2.0, x1 = options.x + w / 2.0,
          z0 = math.min(options.z + t0, options.z + t1), z1 = math.max(options.z + t0, options.z + t1),
          top = y + h
        )
      else
        ctx.platform(
          x0 = math.min(options.x + t0, options.x + t1), x1 = math.max(options.x + t0, options.x + t1),
          z0 = options.z - w / 2.0, z1 = options.z + w / 2.0,
          top = y + h
        )
    }

    val g    = new Group()
    val body = new Mesh(Util.bake(parts.toList), mat)
    body.castShadow = true
    body.receiveShadow = true
    ThreeInterop.addChild(g, body)
    val lip = new Mesh(Util.bake(edge.toList), options.lipMat.getOrElse(m.stoneDark))
    ThreeInterop.setUserDataFlag(lip, "noOutline")
    ThreeInterop.addChild(g, lip)
    g.position.y = y
    ctx.add(g)

    StepsResult(g, top = y + rise * n,
      end = if axis == "z" then options.z + run * n * dir else options.x + run * n * dir)
  end steps

  final case class WallOptions(
    axis: String = "x",
    from: Double,
    to: Double,
    at: Double,
    h: Double = 2.1,
    t: Double = 0.28,
    panel: Double = 4.0,
    y: Double = Double.NaN,
    mat: Option[Material[?]] = None,
    capMat: Option[Material[?]] = None,
    collide: Boolean = true,
    name: String = "wall"
  )

  /** A concrete block boundary wall, built as a run of short panels each seated
    * at its own local ground height: that is how a Japanese block wall copes
    * with a slope. The coping does not cast -- it overhangs the wall by 60 mm,
    * and its own shadow lands as sawtooth rather than as a line.
    */
  def wallRun(ctx: WorldCtx, options: WallOptions): Group[Object3DEventMap] =
    val m    = groundMats()
    val h    = options.h
    val t    = options.t
    val axis = options.axis
    val from = math.min(options.from, options.to)
    val to   = math.max(options.from, options.to)
    val n    = math.max(1.0, math.round((to - from) / options.panel)).toInt
    val step = (to - from) / n
    val body = mutable.ArrayBuffer.empty[Util.BakePart]
    val caps = mutable.ArrayBuffer.empty[Util.BakePart]

    (0 until n).foreach { i =>
      val c  = from + step * (i + 0.5)
      val lz = if axis == "z" then c else options.at
      val dy = (if options.y.isNaN then Street.groundY(lz) else options.y) - 0.05
      body += Util.BakePart(
        geometry = new BoxGeometry(if axis == "z" then t else step + 0.02, h, if axis == "z" then step + 0.02 else t),
        matrix = Some(
          if axis == "z" then Util.trs(options.at, dy + h / 2.0, c) else Util.trs(c, dy + h / 2.0, options.at)
        )
      )
      caps += Util.BakePart(
        geometry = new BoxGeometry(if axis == "z" then t + 0.12 else step + 0.02, 0.1,
          if axis == "z" then step + 0.02 else t + 0.12),
        matrix = Some(
          if axis == "z" then Util.trs(options.at, dy + h + 0.05, c) else Util.trs(c, dy + h + 0.05, options.at)
        )
      )
    }

    val g  = new Group()
    val bm = new Mesh(Util.bake(body.toList), options.mat.getOrElse(m.concreteMid))
    bm.castShadow = true
    bm.receiveShadow = true
    ThreeInterop.addChild(g, bm)
    val cm = new Mesh(Util.bake(caps.toList), options.capMat.getOrElse(m.concrete))
    cm.castShadow = false
    cm.receiveShadow = true
    ThreeInterop.addChild(g, cm)
    g.name = options.name
    ctx.add(g)

    if options.collide then
      val y =
        if options.y.isNaN then Street.groundY(if axis == "z" then (from + to) / 2.0 else options.at) else options.y
      if axis == "z" then ctx.collide(options.at - t / 2.0 - 0.05, from, options.at + t / 2.0 + 0.05, to, y + h)
      else ctx.collide(from, options.at - t / 2.0 - 0.05, to, options.at + t / 2.0 + 0.05, y + h)
    g
  end wallRun

  final case class FenceOptions(
    axis: String = "x",
    from: Double,
    to: Double,
    at: Double,
    h: Double = 1.9,
    y: Double = 0.0,
    spacing: Double = 2.4,
    mid: Boolean = false,
    mat: Option[Material[?]] = None,
    meshColor: Int = 0xc2c8d0,
    collide: Boolean = true
  )

  /** Mesh (chain-link) fencing: posts, rails and a real drawn lattice panel --
   * not a flat translucent sheet, which would read as tinted glass.
   */
  def meshFence(ctx: WorldCtx, options: FenceOptions): Group[Object3DEventMap] =
    val m     = groundMats()
    val h     = options.h
    val axis  = options.axis
    val from  = math.min(options.from, options.to)
    val to    = math.max(options.from, options.to)
    val len   = to - from
    val y     = options.y
    val parts = mutable.ArrayBuffer.empty[Util.BakePart]
    val n     = math.max(1.0, math.round(len / options.spacing)).toInt

    (0 to n).foreach { i =>
      val c = from + (len / n) * i
      parts += Util.BakePart(
        geometry = new CylinderGeometry(0.045, 0.045, h, 6.0),
        matrix = Some(if axis == "z" then Util.trs(options.at, h / 2.0, c) else Util.trs(c, h / 2.0, options.at))
      )
    }
    val railHeights = List(h - 0.05, if options.mid then h * 0.5 else -1.0, 0.12)
    railHeights.foreach { ry =>
      if ry >= 0.0 then
        parts += Util.BakePart(
          geometry = new BoxGeometry(if axis == "z" then 0.05 else len, 0.05, if axis == "z" then len else 0.05),
          matrix = Some(if axis == "z" then Util.trs(options.at, ry, (from + to) / 2.0)
          else Util.trs((from + to) / 2.0, ry, options.at))
        )
    }

    val g     = new Group()
    val frame = new Mesh(Util.bake(parts.toList), options.mat.getOrElse(m.metal))
    frame.castShadow = true
    ThreeInterop.addChild(g, frame)

    val tex = Textures.chainLinkTex().clone()
    tex.wrapS = 1001
    tex.wrapT = 1001
    tex.repeat.set(len / 0.42, (h - 0.2) / 0.42)
    ThreeInterop.markForUpload(tex)
    val panel = new Mesh(
      new PlaneGeometry(len, h - 0.2),
      Toon.flat(Toon.FlatOptions(color = options.meshColor,
        map = Some(tex), transparent = true, opacity = 0.82,
        side = 2, depthWrite = Some(false), cache = false))
    )
    panel.position.set(if axis == "z" then options.at else (from + to) / 2.0, (h - 0.2) / 2.0 + 0.1,
      if axis == "z" then (from + to) / 2.0 else options.at)
    if axis == "z" then panel.rotation.y = math.Pi / 2.0
    ThreeInterop.setUserDataFlag(panel, "noOutline")
    ThreeInterop.setUserDataFlag(panel, "noShadow")
    ThreeInterop.addChild(g, panel)

    g.position.y = y
    ctx.add(g)
    if options.collide then
      if axis == "z" then ctx.collide(options.at - 0.1, from, options.at + 0.1, to, y + h)
      else ctx.collide(from, options.at - 0.1, to, options.at + 0.1, y + h)
    g
  end meshFence

  final case class RailingOptions(
    axis: String = "x",
    from: Double,
    to: Double,
    at: Double,
    h: Double = 1.05,
    y: Double = 0.0,
    spacing: Double = 1.9,
    mat: Option[Material[?]] = None,
    collide: Boolean = true
  )

  /** Painted steel pipe railing -- canal banks, bridge parapets, roof edges. */
  def railing(ctx: WorldCtx, options: RailingOptions): Group[Object3DEventMap] =
    val m     = groundMats()
    val h     = options.h
    val axis  = options.axis
    val from  = math.min(options.from, options.to)
    val to    = math.max(options.from, options.to)
    val len   = to - from
    val y     = options.y
    val parts = mutable.ArrayBuffer.empty[Util.BakePart]
    val n     = math.max(1.0, math.round(len / options.spacing)).toInt

    (0 to n).foreach { i =>
      val c = from + (len / n) * i
      parts += Util.BakePart(
        geometry = new CylinderGeometry(0.038, 0.042, h, 7.0),
        matrix = Some(if axis == "z" then Util.trs(options.at, h / 2.0, c) else Util.trs(c, h / 2.0, options.at))
      )
    }
    List(h - 0.02, h * 0.52).foreach { ry =>
      parts += Util.BakePart(
        geometry = new CylinderGeometry(0.042, 0.042, len, 7.0),
        matrix = Some(if axis == "z" then Util.trs(options.at, ry, (from + to) / 2.0, math.Pi / 2.0, 0.0, 0.0)
        else Util.trs((from + to) / 2.0, ry, options.at, 0.0, 0.0, math.Pi / 2.0))
      )
    }
    val g    = new Group()
    val mesh = new Mesh(Util.bake(parts.toList), options.mat.getOrElse(m.white))
    mesh.castShadow = true
    ThreeInterop.addChild(g, mesh)
    g.position.y = y
    ctx.add(g)
    if options.collide then
      if axis == "z" then ctx.collide(options.at - 0.09, from, options.at + 0.09, to, y + h)
      else ctx.collide(from, options.at - 0.09, to, options.at + 0.09, y + h)
    g
  end railing

  final case class DappleOptions(
    x: Double,
    z: Double,
    y: Double = 0.0,
    n: Int = 7,
    r: Double = 1.4,
    spread: Double,
    rng: Util.RngKit,
    color: Int = 0x8a7fae,
    opacity: Double = 0.13
  )

  /** A soft patch of shade on the ground: a few low-opacity violet quads break
    * the ground up under a dense canopy far more cheaply than more leaves, and
    * they stay out of the depth buffer so the ink pass ignores them.
    */
  def dapple(ctx: WorldCtx, options: DappleOptions): Group[Object3DEventMap] =
    val g   = new Group()
    val n   = options.n
    val geo = new CircleGeometry(1.0, 10.0)
    geo.rotateX(-math.Pi / 2.0)
    val mat = Toon.flat(Toon.FlatOptions(color = options.color, transparent = true,
      opacity = options.opacity, depthWrite = Some(false), cache = false))
    val inst = new InstancedMesh(geo, mat, n.toDouble)
    val d    = new Object3D()
    (0 until n).foreach { i =>
      val r = options.r * options.rng.range(0.55, 1.35)
      d.position.set(options.x + options.rng.range(-options.spread, options.spread),
        options.y + 0.03, options.z + options.rng.range(-options.spread, options.spread))
      d.rotation.set(0.0, options.rng.range(0.0, 3.0), 0.0)
      d.scale.set(r, 1.0, r * options.rng.range(0.7, 1.1))
      d.updateMatrix()
      inst.setMatrixAt(i.toDouble, d.matrix)
    }
    ThreeInterop.setUserDataFlag(inst, "noOutline")
    inst.renderOrder = 1.0
    ThreeInterop.addChild(g, inst)
    ctx.add(g)
    g
  end dapple
end Ground
