package sakura.world

import sakura.ThreeInterop
import sakura.core.{Outline, Pal, Textures, Toon, Util}
import sakura.core.Outline.OutlineOptions
import sakura.core.Toon.{CelOptions, FlatOptions}
import vendor.three.*

import scala.collection.mutable
import scala.math
import scala.scalajs.js

/** 1:1 port of upstream `src/world/buildings.js`: the low-rise house generator and the three
  * garden-boundary builders, baked per material so a street of houses costs a handful of draws.
  */
object Buildings:

  enum Face:
    case XMinus, XPlus, ZMinus, ZPlus

  enum Axis:
    case X, Z

  enum RoofKind:
    case Gable, Hip, Shed, Flat

  private val Walls = Vector(
    Pal.wallWhite, Pal.wallCream, Pal.wallBlue, Pal.wallBeige, Pal.wallGray, Pal.wallPink,
    Pal.wallTea, Pal.wallSage
  )
  private val Roofs = Vector(Pal.roofSlate, Pal.roofBlue, Pal.roofBrown, Pal.roofTeal)

  private def BakePart(geo: BufferGeometry[?, ?], mx: Matrix4): Util.BakePart =
    Util.BakePart(geo, Some(mx))

  def faceRy(face: Face): Double = face match
    case Face.ZPlus  => 0.0
    case Face.ZMinus => math.Pi
    case Face.XPlus  => math.Pi / 2.0
    case Face.XMinus => -math.Pi / 2.0

  private object M:
    lazy val walls       = Walls.map(c => Toon.cel(CelOptions(color = c, bands = 3, tint = 0x6f6790)))
    lazy val roofs       = Roofs.map(c => Toon.cel(CelOptions(color = c, bands = 3, tint = 0x514b70)))
    lazy val trim        = Toon.cel(CelOptions(color = Pal.trim, bands = 3, tint = 0x5c5680))
    lazy val metal       = Toon.cel(CelOptions(color = Pal.metal, bands = 3, tint = 0x666090))
    lazy val metalDark   = Toon.cel(CelOptions(color = Pal.metalDark, bands = 3, tint = 0x5c5680))
    lazy val glass       = Toon.flat(FlatOptions(color = Pal.glassDark))
    lazy val concrete    = Toon.cel(CelOptions(color = Pal.concrete, bands = 3, tint = 0x6f6790))
    lazy val concreteMid = Toon.cel(CelOptions(color = Pal.concreteMid, bands = 3, tint = 0x6a6288))
    lazy val door        = Toon.cel(CelOptions(color = 0x8a6f5c, bands = 3, tint = 0x5c5680))

  final case class HouseOpts(
    x: Double = 0.0,
    z: Double = 0.0,
    y: Double = 0.0,
    w: Double = 6.2,
    d: Double = 7.0,
    face: Face = Face.XMinus,
    floors: Int = 2,
    seed: Int = 7,
    wall: Option[Int] = None,
    roof: Option[Int] = None,
    roofKind: Option[RoofKind] = None,
    shedDir: Option[Int] = None,
    shutters: Boolean = false,
    porch: Boolean = false
  )

  def house(o: HouseOpts): Group[Object3DEventMap] =
    val rng     = Util.rngKit(o.seed)
    val g       = new Group()
    val w       = o.w
    val d       = o.d
    val fh      = 2.72
    val H       = fh * o.floors
    val wallMat = M.walls(o.wall.getOrElse(rng.int(0, Walls.length - 1)))
    val roofMat = M.roofs(o.roof.getOrElse(rng.int(0, Roofs.length - 1)))
    val roofKind = o.roofKind.getOrElse(
      rng.pick(Vector(RoofKind.Gable, RoofKind.Hip, RoofKind.Gable, RoofKind.Flat, RoofKind.Hip))
    )

    val (fx, fz) = o.face match
      case Face.XMinus => (-1.0, 0.0)
      case Face.XPlus  => (1.0, 0.0)
      case Face.ZMinus => (0.0, -1.0)
      case Face.ZPlus  => (0.0, 1.0)
    val frontIsX  = fx != 0.0
    val frontHalf = if frontIsX then w / 2.0 else d / 2.0
    val sideHalf  = if frontIsX then d / 2.0 else w / 2.0

    val parts = mutable.LinkedHashMap(
      "wall"      -> mutable.ArrayBuffer[Util.BakePart](),
      "roof"      -> mutable.ArrayBuffer[Util.BakePart](),
      "trim"      -> mutable.ArrayBuffer[Util.BakePart](),
      "metal"     -> mutable.ArrayBuffer[Util.BakePart](),
      "metalDark" -> mutable.ArrayBuffer[Util.BakePart](),
      "glass"     -> mutable.ArrayBuffer[Util.BakePart](),
      "concrete"  -> mutable.ArrayBuffer[Util.BakePart](),
      "door"      -> mutable.ArrayBuffer[Util.BakePart]()
    )
    def push(key: String, geo: BufferGeometry[?, ?], mx: Matrix4): Unit =
      parts(key) += BakePart(geo, mx)

    push("wall", new BoxGeometry(w, H, d), Util.trs(0, H / 2.0, 0))
    push("concrete", new BoxGeometry(w + 0.14, 0.42, d + 0.14), Util.trs(0, 0.21, 0))
    if o.floors == 2 then
      push("trim", new BoxGeometry(w + 0.08, 0.12, d + 0.08), Util.trs(0, fh, 0))

    val eave = 0.42
    val rw   = w + eave * 2.0
    val rd   = d + eave * 2.0
    roofKind match
      case RoofKind.Gable =>
        val rh      = 1.15 + rng.range(0, 0.5)
        val alongZ  = d >= w
        val span    = if alongZ then rw else rd
        val len     = if alongZ then rd else rw
        val slope   = math.atan2(rh, span / 2.0)
        val slabLen = math.hypot(span / 2.0, rh) + 0.08
        Vector(-1.0, 1.0).foreach { s =>
          val geo = new BoxGeometry(
            (if alongZ then slabLen else len), 0.14, (if alongZ then len else slabLen)
          )
          val mx =
            if alongZ then Util.trs(s * (span / 4.0), H + rh / 2.0, 0, 0, 0, -s * slope)
            else Util.trs(0, H + rh / 2.0, s * (span / 4.0), s * slope, 0, 0)
          push("roof", geo, mx)
        }
        val tri = new Shape()
        tri.moveTo(-span / 2.0 + eave, 0)
        tri.lineTo(span / 2.0 - eave, 0)
        tri.lineTo(0, rh * (1 - (eave * 2.0) / span))
        tri.closePath()
        val triGeo = new ExtrudeGeometry(tri, ExtrudeGeometryOptions(depth = Some(0.16), bevelEnabled = Some(false)))
        triGeo.translate(0.0, 0.0, -0.08)
        Vector(-1.0, 1.0).foreach { s =>
          val mx =
            if alongZ then Util.trs(0, H, s * (len / 2.0 - eave - 0.02))
            else Util.trs(s * (len / 2.0 - eave - 0.02), H, 0, 0, math.Pi / 2.0, 0)
          push("wall", triGeo, mx)
        }
        push("roof",
          new BoxGeometry((if alongZ then 0.22 else len), 0.17, (if alongZ then len else 0.22)),
          Util.trs(0, H + rh + 0.04, 0))
        triGeo.dispose()
      case RoofKind.Hip =>
        val rh  = 1.05 + rng.range(0, 0.4)
        val pyr = new ConeGeometry(math.sqrt(0.5), 1.0, 4.0, 1.0)
        pyr.rotateY(math.Pi / 4.0)
        push("roof", pyr, Util.trs(0, H + rh / 2.0 + 0.06, 0, 0, 0, 0, rw, rh, rd))
        push("roof", new BoxGeometry(rw, 0.16, rd), Util.trs(0, H + 0.06, 0))
        pyr.dispose()
      case RoofKind.Shed =>
        val rh      = 0.9 + rng.range(0, 0.5)
        val alongZ  = d >= w
        val span    = if alongZ then rw else rd
        val len     = if alongZ then rd else rw
        val dir     = o.shedDir.getOrElse(1).toDouble
        val slope   = math.atan2(rh, span)
        val slabLen = math.hypot(span, rh) + 0.06
        push("roof",
          new BoxGeometry((if alongZ then slabLen else len), 0.15, (if alongZ then len else slabLen)),
          if alongZ then Util.trs(0, H + rh / 2.0 + 0.07, 0, 0, 0, dir * slope)
          else Util.trs(0, H + rh / 2.0 + 0.07, 0, -dir * slope, 0, 0))
        val tri = new Shape()
        tri.moveTo(-span / 2.0 + eave, 0)
        tri.lineTo(span / 2.0 - eave, 0)
        tri.lineTo(dir * (span / 2.0 - eave), rh * (1 - (eave * 2.0) / span))
        tri.closePath()
        val triGeo = new ExtrudeGeometry(tri, ExtrudeGeometryOptions(depth = Some(0.16), bevelEnabled = Some(false)))
        triGeo.translate(0.0, 0.0, -0.08)
        Vector(-1.0, 1.0).foreach { s =>
          val mx =
            if alongZ then Util.trs(0, H, s * (len / 2.0 - eave - 0.02))
            else Util.trs(s * (len / 2.0 - eave - 0.02), H, 0, 0, math.Pi / 2.0, 0)
          push("wall", triGeo, mx)
        }
        triGeo.dispose()
        push("trim",
          new BoxGeometry((if alongZ then 0.2 else len), 0.2, (if alongZ then len else 0.2)),
          if alongZ then Util.trs(dir * span / 2.0, H + rh + 0.1, 0)
          else Util.trs(0, H + rh + 0.1, -dir * span / 2.0))
        push("metal",
          new BoxGeometry((if alongZ then 0.14 else len), 0.12, (if alongZ then len else 0.14)),
          if alongZ then Util.trs(-dir * (span / 2.0 + 0.04), H + 0.06, 0)
          else Util.trs(0, H + 0.06, dir * (span / 2.0 + 0.04)))
      case RoofKind.Flat =>
        push("roof", new BoxGeometry(w + 0.24, 0.18, d + 0.24), Util.trs(0, H + 0.09, 0))
        Vector(-1.0, 1.0).foreach { s =>
          push("roof", new BoxGeometry(w + 0.24, 0.34, 0.14), Util.trs(0, H + 0.28, s * (d / 2.0 + 0.05)))
          push("roof", new BoxGeometry(0.14, 0.34, d + 0.24), Util.trs(s * (w / 2.0 + 0.05), H + 0.28, 0))
        }
    end match

    val winW = 1.5
    val cols = math.max(1, math.floor((frontHalf * 2.0 - 1.0) / 2.1).toInt)
    def placeFront(u: Double, y: Double, ww: Double, wh: Double, isDoor: Boolean = false): Unit =
      val px = if frontIsX then fx * (frontHalf + 0.02) else u
      val pz = if frontIsX then u else fz * (frontHalf + 0.02)
      if !isDoor then
        val dx = if frontIsX then 0.14 else ww
        val dz = if frontIsX then ww else 0.14
        push("trim", new BoxGeometry(dx, wh + 0.14, dz + (if frontIsX then 0.14 else 0.0)),
          Util.trs(px - fx * 0.02, y, pz - fz * 0.02))
        val gx = if frontIsX then 0.06 else ww - 0.12
        val gz = if frontIsX then ww - 0.12 else 0.06
        push("glass", new BoxGeometry(gx, wh, gz), Util.trs(px + fx * 0.05, y, pz + fz * 0.05))
        push("metal",
          new BoxGeometry((if frontIsX then 0.07 else 0.06), wh, (if frontIsX then 0.06 else 0.07)),
          Util.trs(px + fx * 0.07, y, pz + fz * 0.07))
        push("trim",
          new BoxGeometry((if frontIsX then 0.2 else ww + 0.2), 0.08, (if frontIsX then ww + 0.2 else 0.2)),
          Util.trs(px + fx * 0.03, y - wh / 2.0 - 0.09, pz + fz * 0.03))
      else
        val dx = if frontIsX then 0.14 else ww
        val dz = if frontIsX then ww else 0.14
        push("door", new BoxGeometry(dx, wh, dz), Util.trs(px + fx * 0.02, y, pz + fz * 0.02))
        push("trim",
          new BoxGeometry((if frontIsX then 0.16 else ww + 0.18), wh + 0.16,
            (if frontIsX then ww + 0.18 else 0.16)),
          Util.trs(px - fx * 0.03, y, pz - fz * 0.03))
      end if
    end placeFront

    def spread(i: Int, n: Int): Double = -frontHalf + (frontHalf * 2.0 * (i + 1)) / (n + 1)
    val doorIdx                        = rng.int(0, cols - 1)
    var doorU                          = 0.0
    (0 until cols).foreach { i =>
      val u = spread(i, cols)
      if i == doorIdx then
        doorU = u
        placeFront(u, 1.05, 1.05, 2.05, isDoor = true)
      else
        placeFront(u, 1.42, winW, 1.25)
        if o.shutters then
          val px = if frontIsX then fx * (frontHalf + 0.14) else u
          val pz = if frontIsX then u else fz * (frontHalf + 0.14)
          push("metal",
            new BoxGeometry((if frontIsX then 0.2 else winW + 0.24), 0.24,
              (if frontIsX then winW + 0.24 else 0.2)),
            Util.trs(px, 2.22, pz))
          push("metalDark",
            new BoxGeometry((if frontIsX then 0.22 else winW + 0.3), 0.05,
              (if frontIsX then winW + 0.3 else 0.22)),
            Util.trs(px, 2.36, pz))
    }

    if o.porch then
      val out = 0.85
      val px  = if frontIsX then fx * (frontHalf + out / 2.0) else doorU
      val pz  = if frontIsX then doorU else fz * (frontHalf + out / 2.0)
      push("trim", new BoxGeometry((if frontIsX then out else 1.9), 0.1, (if frontIsX then 1.9 else out)),
        Util.trs(px, 2.36, pz))
      push("metal",
        new BoxGeometry((if frontIsX then out + 0.08 else 2.0), 0.06, (if frontIsX then 2.0 else out + 0.08)),
        Util.trs(px, 2.29, pz))
      Vector(-1.0, 1.0).foreach { s =>
        val bx = if frontIsX then fx * (frontHalf + 0.2) else doorU + s * 0.82
        val bz = if frontIsX then doorU + s * 0.82 else fz * (frontHalf + 0.2)
        push("metal",
          new BoxGeometry((if frontIsX then 0.42 else 0.05), 0.05,
            (if frontIsX then 0.05 else 0.42)),
          Util.trs(bx, 2.18, bz))
        push("metal", new BoxGeometry(0.05, 0.34, 0.05), Util.trs(bx, 2.06, bz))
      }
      (0 until 2).foreach { i =>
        val t   = 0.42 - i * 0.21
        val sw  = 1.5 - i * 0.14
        val sd  = 0.34
        val sx2 = if frontIsX then fx * (frontHalf + sd * (i + 0.5)) else doorU
        val sz2 = if frontIsX then doorU else fz * (frontHalf + sd * (i + 0.5))
        push("concrete",
          new BoxGeometry((if frontIsX then sd + 0.04 else sw), t,
            (if frontIsX then sw else sd + 0.04)),
          Util.trs(sx2, t / 2.0, sz2))
      }
    end if

    if o.floors == 2 then
      (0 until cols).foreach(i => placeFront(spread(i, cols), fh + 1.5, winW, 1.3))
      if rng.chance(0.7) then
        val bu = spread(rng.int(0, cols - 1), cols)
        val bw = math.min(2.6, frontHalf * 1.4)
        val px = if frontIsX then fx * (frontHalf + 0.45) else bu
        val pz = if frontIsX then bu else fz * (frontHalf + 0.45)
        push("concrete",
          new BoxGeometry((if frontIsX then 0.9 else bw), 0.1,
            (if frontIsX then bw else 0.9)),
          Util.trs(px, fh + 0.42, pz))
        push("metal",
          new BoxGeometry((if frontIsX then 0.08 else bw), 0.07,
            (if frontIsX then bw else 0.08)),
          Util.trs(px + fx * 0.42, fh + 1.42, pz + fz * 0.42))
        push("metalDark",
          new BoxGeometry((if frontIsX then 0.06 else bw), 0.95,
            (if frontIsX then bw else 0.06)),
          Util.trs(px + fx * 0.42, fh + 0.95, pz + fz * 0.42))
        val n = math.round(bw / 0.26).toInt
        (0 until n).foreach { i =>
          val t = -bw / 2.0 + (bw / (n - 1).toDouble) * i
          push("metal", new BoxGeometry(0.04, 0.95, 0.04),
            Util.trs(px + fx * 0.42 + (if frontIsX then 0.0 else t), fh + 0.95,
              pz + fz * 0.42 + (if frontIsX then t else 0.0)))
        }
        push("metal",
          new BoxGeometry((if frontIsX then 0.05 else bw * 0.8), 0.05,
            (if frontIsX then bw * 0.8 else 0.05)),
          Util.trs(px + fx * 0.2, fh + 1.75, pz + fz * 0.2))
        if rng.chance(0.75) then
          val towelMat = Toon.cel(CelOptions(
            color = rng.pick(Vector(Pal.wallBlue, Pal.blossom, Pal.wallWhite, Pal.yellow)),
            bands = 2, side = 2, tint = 0x6f6790
          ))
          val towel = new Mesh(new PlaneGeometry(0.42, 0.62), towelMat)
          towel.position.set(
            px + fx * 0.2 + (if frontIsX then 0.0 else rng.range(-0.4, 0.4)), fh + 1.44,
            pz + fz * 0.2 + (if frontIsX then rng.range(-0.4, 0.4) else 0.0)
          )
          towel.rotation.y = if frontIsX then math.Pi / 2.0 else 0.0
          towel.castShadow = true
          ThreeInterop.addChild(g, towel)
      end if
    end if

    val s1  = rng.sign.toDouble
    val dpx = if frontIsX then fx * (frontHalf + 0.07) else s1 * (sideHalf - 0.15)
    val dpz = if frontIsX then s1 * (sideHalf - 0.15) else fz * (frontHalf + 0.07)
    push("metal", new CylinderGeometry(0.055, 0.055, H, 6), Util.trs(dpx, H / 2.0, dpz))
    push("metal",
      new BoxGeometry((if frontIsX then 0.12 else w * 0.9), 0.1,
        (if frontIsX then d * 0.9 else 0.12)),
      Util.trs(if frontIsX then dpx else 0.0, H + 0.02, if frontIsX then 0.0 else dpz))

    val s2  = rng.sign.toDouble
    val apx = if frontIsX then fx * (frontHalf + 0.4) else s2 * (sideHalf * 0.5)
    val apz = if frontIsX then s2 * (sideHalf * 0.5) else fz * (frontHalf + 0.4)
    push("trim", new BoxGeometry((if frontIsX then 0.7 else 0.86), 0.6, (if frontIsX then 0.86 else 0.7)),
      Util.trs(apx, if o.floors == 2 then fh + 0.4 else 1.9, apz))

    val u0  = spread(0, cols) + 0.9
    val mpx = if frontIsX then fx * (frontHalf + 0.06) else u0
    val mpz = if frontIsX then u0 else fz * (frontHalf + 0.06)
    val plate = new Mesh(
      new BoxGeometry((if frontIsX then 0.12 else 0.34), 0.46, (if frontIsX then 0.34 else 0.12)),
      Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.meterBox()), cache = false))
    )
    plate.position.set(mpx, 1.9, mpz)
    ThreeInterop.addChild(g, plate)

    if rng.chance(0.55) then
      val ax  = rng.range(-w * 0.25, w * 0.25)
      val az  = rng.range(-d * 0.25, d * 0.25)
      val top = H + (if roofKind == RoofKind.Flat then 0.3 else 1.4)
      push("metalDark", new CylinderGeometry(0.03, 0.03, 2.1, 5), Util.trs(ax, top + 1.05, az))
      (0 until 5).foreach(i =>
        push("metalDark", new BoxGeometry(0.02, 0.02, 0.62 - i * 0.06),
          Util.trs(ax, top + 1.2 + i * 0.17, az))
      )

    val matFor: Map[String, Material[?]] = Map(
      "wall"      -> wallMat, "roof"      -> roofMat, "trim"     -> M.trim, "metal"    -> M.metal,
      "metalDark" -> M.metalDark, "glass" -> M.glass, "concrete" -> M.concrete, "door" -> M.door
    )
    parts.foreach { (key, bucket) =>
      if bucket.nonEmpty then
        val mesh = new Mesh(Util.bake(bucket.toList), matFor(key))
        mesh.castShadow = true
        mesh.receiveShadow = true
        if key == "wall" || key == "roof" then Outline.hullOutline(mesh, OutlineOptions(thickness = 0.0032))
        ThreeInterop.addChild(g, mesh)
    }

    g.position.set(o.x, o.y, o.z)
    g
  end house

  final case class WallOpts(
    x: Double = 0.0,
    z: Double = 0.0,
    y: Double = 0.0,
    len: Double,
    axis: Axis = Axis.X,
    h: Double = 0.55,
    fence: Boolean = false,
    fenceH: Double = 0.85
  )

  def wall(o: WallOpts): Group[Object3DEventMap] =
    val g        = new Group()
    val vertical = o.axis == Axis.Z
    val body = Util.box(
      (if vertical then 0.2 else o.len), o.h, (if vertical then o.len else 0.2),
      M.concreteMid, 0.0, o.h / 2.0, 0.0
    )
    body.castShadow = true
    body.receiveShadow = true
    ThreeInterop.addChild(g, body)
    ThreeInterop.addChild(g,
      Util.box(
        (if vertical then 0.28 else o.len + 0.06), 0.08, (if vertical then o.len + 0.06 else 0.28),
        M.concrete, 0.0, o.h + 0.04, 0.0
      ))
    if o.fence then
      val fh    = o.fenceH
      val parts = mutable.ArrayBuffer[Util.BakePart]()
      val n     = math.floor(o.len / 0.9).toInt
      (0 to n).foreach { i =>
        val t = -o.len / 2.0 + (o.len / n) * i
        parts += BakePart(new BoxGeometry(0.06, fh, 0.06),
          Util.trs(if vertical then 0.0 else t, o.h + fh / 2.0, if vertical then t else 0.0))
      }
      Vector(o.h + 0.12, o.h + fh - 0.06).foreach { y =>
        parts += BakePart(
          new BoxGeometry((if vertical then 0.05 else o.len), 0.05, (if vertical then o.len else 0.05)),
          Util.trs(0.0, y, 0.0)
        )
      }
      val nb = math.floor(o.len / 0.22).toInt
      (0 to nb).foreach { i =>
        val t = -o.len / 2.0 + (o.len / nb) * i
        parts += BakePart(new BoxGeometry(0.03, fh - 0.2, 0.03),
          Util.trs(if vertical then 0.0 else t, o.h + fh / 2.0, if vertical then t else 0.0))
      }
      val fence = new Mesh(Util.bake(parts.toList), M.metal)
      fence.castShadow = true
      ThreeInterop.addChild(g, fence)
    end if
    g.position.set(o.x, o.y, o.z)
    g.userData("top") = (o.h + (if o.fence then o.fenceH else 0.0)).asInstanceOf[js.Any]
    g
  end wall

  final case class FenceOpts(
    x: Double = 0.0,
    z: Double = 0.0,
    y: Double = 0.0,
    len: Double,
    axis: Axis = Axis.X,
    h: Double = 0.4,
    fenceH: Double = 0.95
  )

  def timberFence(o: FenceOpts): Group[Object3DEventMap] =
    val g                                   = new Group()
    val vertical                            = o.axis == Axis.Z
    def along(t: Double): (Double, Double)  = if vertical then (0.0, t) else (t, 0.0)
    def across(v: Double): (Double, Double) = if vertical then (v, 0.0) else (0.0, v)

    val plinth = Util.box(
      (if vertical then 0.22 else o.len), o.h, (if vertical then o.len else 0.22),
      M.concreteMid, 0.0, o.h / 2.0, 0.0
    )
    plinth.castShadow = true
    plinth.receiveShadow = true
    ThreeInterop.addChild(g, plinth)

    val frame = mutable.ArrayBuffer[Util.BakePart]()
    val slats = mutable.ArrayBuffer[Util.BakePart]()
    val np    = math.max(1, math.round(o.len / 1.8).toInt)
    (0 to np).foreach { i =>
      val (px, pz) = along(-o.len / 2.0 + (o.len / np) * i)
      frame += BakePart(
        new BoxGeometry((if vertical then 0.14 else 0.13), o.fenceH + 0.14,
          (if vertical then 0.13 else 0.14)),
        Util.trs(px, o.h + (o.fenceH + 0.14) / 2.0, pz)
      )
    }
    Vector(o.h + o.fenceH * 0.22, o.h + o.fenceH * 0.8).foreach { v =>
      val (ax, az) = across(0.055)
      frame += BakePart(
        new BoxGeometry((if vertical then 0.05 else o.len), 0.07, (if vertical then o.len else 0.05)),
        Util.trs(ax, v, az)
      )
    }
    frame += BakePart(
      new BoxGeometry((if vertical then 0.26 else o.len + 0.1), 0.07,
        (if vertical then o.len + 0.1 else 0.26)),
      Util.trs(0.0, o.h + o.fenceH + 0.12, 0.0)
    )

    val ns    = math.max(4, math.round(o.len / 0.17).toInt)
    val pitch = o.len / ns
    (0 until ns).foreach { i =>
      val (sx, sz) = along(-o.len / 2.0 + pitch * (i + 0.5))
      slats += BakePart(
        new BoxGeometry((if vertical then 0.05 else pitch * 0.72), o.fenceH,
          (if vertical then pitch * 0.72 else 0.05)),
        Util.trs(sx, o.h + o.fenceH / 2.0, sz)
      )
    }
    val fm = new Mesh(Util.bake(frame.toList), M.door)
    val sm = new Mesh(Util.bake(slats.toList),
      Toon.cel(CelOptions(color = 0xb09a76, bands = 3, tint = 0x6f6790)))
    fm.castShadow = true
    sm.castShadow = true
    fm.receiveShadow = true
    sm.receiveShadow = true
    ThreeInterop.addChild(g, fm)
    ThreeInterop.addChild(g, sm)
    g.position.set(o.x, o.y, o.z)
    g.userData("top") = (o.h + o.fenceH + 0.16).asInstanceOf[js.Any]
    g
  end timberFence

  def blockFence(o: FenceOpts): Group[Object3DEventMap] =
    val g                                  = new Group()
    val vertical                           = o.axis == Axis.Z
    def along(t: Double): (Double, Double) = if vertical then (0.0, t) else (t, 0.0)
    val h                                  = o.h
    val bh                                 = 0.4
    val TH                                 = 0.19

    val body = Util.box((if vertical then TH else o.len), h, (if vertical then o.len else TH),
      M.concreteMid, 0.0, h / 2.0, 0.0)
    body.castShadow = true
    body.receiveShadow = true
    ThreeInterop.addChild(g, body)

    val joints = mutable.ArrayBuffer[Util.BakePart]()
    val grout  = Toon.cel(CelOptions(color = 0xa9a3b2, bands = 3, tint = 0x625b7d))
    var v      = 0.19
    while v < h - 0.02 do
      joints += BakePart(
        new BoxGeometry((if vertical then TH + 0.02 else o.len), 0.025,
          (if vertical then o.len else TH + 0.02)),
        Util.trs(0.0, v, 0.0)
      )
      v += 0.19
    val nj = math.max(1, math.round(o.len / 0.39).toInt)
    (1 until nj).foreach { i =>
      val (jx, jz) = along(-o.len / 2.0 + (o.len / nj) * i)
      joints += BakePart(
        new BoxGeometry((if vertical then TH + 0.02 else 0.025), h,
          (if vertical then 0.025 else TH + 0.02)),
        Util.trs(jx, h / 2.0, jz)
      )
    }
    ThreeInterop.addChild(g, new Mesh(Util.bake(joints.toList), grout))

    val frames = mutable.ArrayBuffer[Util.BakePart]()
    val nb     = math.max(2, math.round(o.len / 0.39).toInt)
    val bw     = o.len / nb
    val rim    = math.min(0.085, bw * 0.2)
    (0 until nb).foreach { i =>
      val c = -o.len / 2.0 + bw * (i + 0.5)
      Vector(-1.0, 1.0).foreach { s =>
        val (fxp, fzp) = along(c)
        frames += BakePart(
          new BoxGeometry((if vertical then TH else bw), rim, (if vertical then bw else TH)),
          Util.trs(fxp, h + bh / 2.0 + s * (bh - rim) / 2.0, fzp)
        )
      }
      Vector(-1.0, 1.0).foreach { s =>
        val (fxp, fzp) = along(c + s * (bw - rim) / 2.0)
        frames += BakePart(
          new BoxGeometry((if vertical then TH else rim), bh - rim * 2.0,
            (if vertical then rim else TH)),
          Util.trs(fxp, h + bh / 2.0, fzp)
        )
      }
    }
    val bm = new Mesh(Util.bake(frames.toList), M.concrete)
    bm.castShadow = true
    bm.receiveShadow = true
    ThreeInterop.addChild(g, bm)

    val cap = Util.box(
      (if vertical then TH + 0.08 else o.len + 0.06), 0.08,
      (if vertical then o.len + 0.06 else TH + 0.08), M.concrete, 0.0, h + bh + 0.04, 0.0
    )
    cap.receiveShadow = true
    ThreeInterop.addChild(g, cap)

    g.position.set(o.x, o.y, o.z)
    g.userData("top") = (h + bh + 0.08).asInstanceOf[js.Any]
    g
  end blockFence
end Buildings
