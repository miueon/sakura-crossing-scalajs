package sakura.world

import sakura.ThreeInterop
import sakura.core.{Outline, Pal, Textures, Toon, Util}
import sakura.core.Outline.OutlineOptions
import sakura.core.Toon.{CelOptions, FlatOptions}
import vendor.three.*

import scala.collection.mutable
import scala.math

/** 1:1 port of upstream `src/world/onsen.js`: 湯の坂 -- the onsen street on the shelf behind
  * the shrine, its channel and bridge, the inn, the bathhouse, the footbath, three tenants, the
  * viewing deck, and the warm lights coming on one at a time.
  */
object Onsen:

  private val Y        = 3.20
  private val TerX0    = -46.8
  private val TerX1    = -18.6
  private val TerZ0    = 40.6
  private val TerZ1    = 59.6
  private val StZ0     = 46.4
  private val StZ1     = 51.2
  private val GateX    = -22.5
  private val GateHalf = 1.3

  private val ChX    = -34.2
  private val ChHalf = 0.72
  private val ChZ0   = 41.0
  private val ChZ1   = 58.6
  private val ChW    = Y - 0.62
  private val ChBed  = Y - 0.86

  private object M:
    lazy val wood       = Toon.cel(CelOptions(color = Pal.onsenWood, bands = 3, tint = 0x5c5680))
    lazy val woodDark   = Toon.cel(CelOptions(color = Pal.onsenWoodDark, bands = 3, tint = 0x50466a))
    lazy val woodPale   = Toon.cel(CelOptions(color = Pal.onsenWoodPale, bands = 3, tint = 0x6f5680))
    lazy val plaster    = Toon.cel(CelOptions(color = Pal.onsenPlaster, bands = 3, tint = 0x6f6790))
    lazy val plasterAlt = Toon.cel(CelOptions(color = Pal.onsenPlasterAlt, bands = 3, tint = 0x6f6790))
    lazy val tile       = Toon.cel(CelOptions(color = Pal.onsenTile, bands = 3, tint = 0x494268))
    lazy val tileEdge   = Toon.cel(CelOptions(color = Pal.onsenTileEdge, bands = 3, tint = 0x494268))
    lazy val stone      = Toon.cel(CelOptions(color = Pal.stone, bands = 3, tint = 0x655d80))
    lazy val stoneDark  = Toon.cel(CelOptions(color = Pal.stoneDark, bands = 3, tint = 0x605878))
    lazy val stoneWarm  = Toon.cel(CelOptions(color = Pal.stoneWarm, bands = 3, tint = 0x655d80))
    lazy val slab       = Toon.cel(CelOptions(color = Pal.onsenSlab, bands = 3, tint = 0x6a6288))
    lazy val metal      = Toon.cel(CelOptions(color = Pal.metal, bands = 3, tint = 0x666090))
    lazy val metalDark  = Toon.cel(CelOptions(color = Pal.metalDark, bands = 3, tint = 0x5c5680))
    lazy val indigo     = Toon.cel(CelOptions(color = Pal.onsenIndigo, bands = 3, tint = 0x4a4468))
    lazy val glass = Toon.cel(CelOptions(color = Pal.glass, bands = 2, transparent = true,
      opacity = 0.5, tint = 0x6f6790))
    lazy val bamboo     = Toon.cel(CelOptions(color = Pal.bamboo, bands = 3, tint = 0x5b6f8c))
    lazy val bambooDeep = Toon.cel(CelOptions(color = Pal.bambooDeep, bands = 3, tint = 0x5b6f8c))
    lazy val pine       = Toon.cel(CelOptions(color = Pal.cedar, bands = 3, tint = 0x4a5f7a))
    lazy val pineDeep   = Toon.cel(CelOptions(color = Pal.cedarDeep, bands = 3, tint = 0x4a5f7a))
    lazy val water = Toon.flat(FlatOptions(color = Pal.onsenWater, transparent = true,
      opacity = 0.9, cache = false))
    lazy val waterDeep = Toon.flat(FlatOptions(color = Pal.onsenWaterDeep, cache = false))
    lazy val trunkDark = Toon.cel(CelOptions(color = Pal.trunkDark, bands = 3, tint = 0x5c5680))
    lazy val drain     = Toon.cel(CelOptions(color = Pal.drain, bands = 3, tint = 0x5d5878))
  end M

  private def bakePart(geo: BufferGeometry[?, ?], mx: Matrix4): Util.BakePart =
    Util.BakePart(geo, Some(mx))

  /** A warm light on the dusk ramp: its material, how far west it stands, and the two colours. */
  final private class WarmLight(val material: MeshBasicMaterial, val along: Double, val hasMap: Boolean):
    var at: Double = 0.0
    var on: Color  = null
    var off: Color = null

  final private case class Drift(y0: Double, ph: Double, sp: Double)

  final private case class VentPuff(mesh: Mesh[?, ?, ?], y0: Double, ph: Double)

  final private case class OnsenUnitOptions(
    x: Double,
    z: Double,
    y: Double,
    w: Double,
    d: Double,
    face: Buildings.Face,
    kind: Textures.OnsenKind,
    floors: Int = 2,
    h1: Double = 3.0,
    h2: Double = 2.6,
    hip: Boolean = false,
    roofH: Option[Double] = None,
    eave: Double = 0.62,
    hollow: Double = 0.0,
    doorW: Double = 1.5,
    doorAt: Double = 0.0,
    noren: Option[Textures.OnsenNorenKind] = None,
    blade: Boolean = false,
    bladeSide: Double = 1.0,
    bladeH: Double = 1.9,
    balcony: Boolean = false,
    windows: Int = 2,
    rooms: Boolean = true,
    awning: Boolean = true,
    awningOut: Double = 0.9,
    fasciaH: Double = 0.52,
    interior: Option[Int] = None,
    wallAlt: Boolean = false,
    seed: Int = 41
  )

  final private case class OnsenUnit(group: Group[Object3DEventMap], lit: Vector[MeshBasicMaterial])

  private def latticeInto(
    parts: mutable.LinkedHashMap[String, mutable.ArrayBuffer[Util.BakePart]],
    cx: Double,
    lw: Double,
    sill: Double,
    head: Double,
    zBack: Double,
    zBatt: Double
  ): Unit =
    if lw >= 0.3 then
      parts("woodDark") += bakePart(new BoxGeometry(lw, head - sill, 0.08), Util.trs(cx, (sill + head) / 2.0, zBack))
      val n = math.max(2, math.round(lw / 0.135).toInt)
      (0 until n).foreach { i =>
        parts("wood") += bakePart(new BoxGeometry(0.05, head - sill - 0.06, 0.09),
          Util.trs(cx - lw / 2.0 + (lw * (i + 0.5)) / n, (sill + head) / 2.0, zBatt))
      }
      parts("wood") += bakePart(new BoxGeometry(lw, 0.06, 0.1), Util.trs(cx, sill + (head - sill) * 0.62, zBatt + 0.02))

  private def makeOnsenUnit(ctx: WorldCtx, o: OnsenUnitOptions): OnsenUnit =
    val g = new Group()
    g.name = "onsen_" + o.kind.key
    val w       = o.w
    val d       = o.d
    val floors  = o.floors
    val h1      = o.h1
    val h2      = o.h2
    val h       = if floors == 2 then h1 + h2 else h1
    val front   = d / 2.0
    val wallMat = if o.wallAlt then M.plasterAlt else M.plaster

    val parts = mutable.LinkedHashMap(
      "wall"     -> mutable.ArrayBuffer.empty[Util.BakePart],
      "wood"     -> mutable.ArrayBuffer.empty[Util.BakePart],
      "woodDark" -> mutable.ArrayBuffer.empty[Util.BakePart],
      "tile"     -> mutable.ArrayBuffer.empty[Util.BakePart],
      "tileEdge" -> mutable.ArrayBuffer.empty[Util.BakePart],
      "stone"    -> mutable.ArrayBuffer.empty[Util.BakePart],
      "metal"    -> mutable.ArrayBuffer.empty[Util.BakePart]
    )
    val lit = mutable.ArrayBuffer.empty[MeshBasicMaterial]

    val hol = o.hollow
    parts("stone") += bakePart(new BoxGeometry(w + 0.2, 0.36, d + 0.2 - hol), Util.trs(0.0, 0.18, -hol / 2.0))
    parts("wall") += bakePart(new BoxGeometry(w, h1 - 0.36, d - hol),
      Util.trs(0.0, 0.36 + (h1 - 0.36) / 2.0, -hol / 2.0))
    if floors == 2 then
      parts("wall") += bakePart(new BoxGeometry(w, h2, d - 0.24), Util.trs(0.0, h1 + h2 / 2.0, -0.12))
      parts("wood") += bakePart(new BoxGeometry(w + 0.18, 0.16, d + 0.06), Util.trs(0.0, h1 + 0.02, 0.0))

    {
      val eave = o.eave
      val rw   = w + eave * 2.0
      val rd   = (if floors == 2 then d - 0.24 else d) + eave * 2.0
      val rh   = o.roofH.getOrElse(if o.hip then 1.05 else 1.3)
      val zc   = if floors == 2 then -0.12 else 0.0
      if o.hip then
        val slope = math.atan2(rh, rd / 2.0)
        Vector(-1.0, 1.0).foreach { s =>
          parts("tile") += bakePart(new BoxGeometry(rw * 0.82, 0.16, math.hypot(rd / 2.0, rh) + 0.1),
            Util.trs(0.0, h + rh / 2.0, zc + s * (rd / 4.0), s * slope, 0.0, 0.0))
        }
        val sx = math.atan2(rh, rw / 2.0)
        Vector(-1.0, 1.0).foreach { s =>
          parts("tile") += bakePart(new BoxGeometry(math.hypot(rw / 2.0, rh) + 0.1, 0.16, rd * 0.5),
            Util.trs(s * (rw / 4.0), h + rh / 2.0, zc, 0.0, 0.0, -s * sx))
        }
        parts("tileEdge") += bakePart(new BoxGeometry(rw * 0.44, 0.22, 0.3), Util.trs(0.0, h + rh + 0.03, zc))
      else
        val slope = math.atan2(rh, rd / 2.0)
        Vector(-1.0, 1.0).foreach { s =>
          parts("tile") += bakePart(new BoxGeometry(rw, 0.16, math.hypot(rd / 2.0, rh) + 0.1),
            Util.trs(0.0, h + rh / 2.0, zc + s * (rd / 4.0), s * slope, 0.0, 0.0))
        }
        parts("tileEdge") += bakePart(new BoxGeometry(rw + 0.08, 0.24, 0.32), Util.trs(0.0, h + rh + 0.04, zc))
        val tri = new Shape()
        tri.moveTo(-w / 2.0, 0.0)
        tri.lineTo(w / 2.0, 0.0)
        tri.lineTo(0.0, rh * 0.86)
        tri.closePath()
        val tg = new ExtrudeGeometry(tri,
          ExtrudeGeometryOptions(depth = Some(0.14), bevelEnabled = Some(false)))
        tg.translate(0.0, 0.0, -0.07)
        Vector(-1.0, 1.0).foreach { s =>
          parts("wood") += bakePart(tg, Util.trs(0.0, h, zc + s * ((if floors == 2 then d - 0.24 else d) / 2.0 - 0.06)))
        }
        tg.dispose()
      end if
      Vector(-1.0, 1.0).foreach { s =>
        parts("tileEdge") += bakePart(new BoxGeometry(rw + 0.06, 0.13, 0.16),
          Util.trs(0.0, h - 0.04, zc + s * (rd / 2.0 - 0.02)))
      }
    }

    val sill   = 0.42
    val head   = h1 - 0.52
    val post   = 0.16
    val doorW  = o.doorW
    val doorC  = o.doorAt * w
    val zBack  = front + 0.04
    val zBatt  = front + 0.12
    val zFrame = front + 0.08

    parts("woodDark") += bakePart(new BoxGeometry(w, 0.18, 0.3), Util.trs(0.0, sill, zFrame))
    parts("wood") += bakePart(new BoxGeometry(w, 0.22, 0.3), Util.trs(0.0, head + 0.11, zFrame))
    Vector(-w / 2.0 + post / 2.0, w / 2.0 - post / 2.0).foreach { px =>
      parts("woodDark") += bakePart(new BoxGeometry(post, h1, 0.3), Util.trs(px, h1 / 2.0, zFrame))
    }
    val dL = doorC - doorW / 2.0
    val dR = doorC + doorW / 2.0
    latticeInto(parts, (-w / 2.0 + post + dL) / 2.0, dL - (-w / 2.0 + post), sill, head, zBack, zBatt)
    latticeInto(parts, (dR + w / 2.0 - post) / 2.0, (w / 2.0 - post) - dR, sill, head, zBack, zBatt)

    if hol != 0.0 then
      val pL = dL - (-w / 2.0)
      val pR = (w / 2.0) - dR
      if pL > 0.1 then
        parts("wall") += bakePart(new BoxGeometry(pL, h1 - 0.36, hol),
          Util.trs(-w / 2.0 + pL / 2.0, 0.36 + (h1 - 0.36) / 2.0, front - hol / 2.0))
      if pR > 0.1 then
        parts("wall") += bakePart(new BoxGeometry(pR, h1 - 0.36, hol),
          Util.trs(w / 2.0 - pR / 2.0, 0.36 + (h1 - 0.36) / 2.0, front - hol / 2.0))
      parts("wall") += bakePart(new BoxGeometry(doorW, h1 - head + 0.18, hol),
        Util.trs(doorC, (h1 + head - 0.18) / 2.0, front - hol / 2.0))

    if hol == 0.0 then
      parts("woodDark") += bakePart(new BoxGeometry(doorW, head - 0.18, 0.06),
        Util.trs(doorC, (head - 0.18) / 2.0 + 0.09, front + 0.03))

    o.interior.foreach { interior =>
      val iw = doorW - 0.14
      val ih = head - 0.5
      val room = new Mesh(new PlaneGeometry(iw, ih),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.tatamiRoom((interior - 1) % 2)),
          cache = false)))
      room.position.set(doorC, ih / 2.0 + 0.16, front + 0.11)
      ThreeInterop.setUserDataFlag(room, "noOutline")
      ThreeInterop.addChild(g, room)
      lit += flatMaterialOf(room)
    }
    parts("stone") += bakePart(new BoxGeometry(doorW + 0.5, 0.16, 0.62), Util.trs(doorC, 0.24, front + 0.36))
    parts("wood") += bakePart(new BoxGeometry(doorW + 0.12, 0.16, 0.2), Util.trs(doorC, head - 0.02, front + 0.12))

    {
      val fh  = o.fasciaH
      val fw  = w - 0.5
      val art = Textures.onsenFascia(o.kind)
      val board = new Mesh(new BoxGeometry(fw, fh, 0.12),
        Props.matArray(
          Toon.flat(FlatOptions(color = Pal.onsenWoodDark)), Toon.flat(FlatOptions(color = Pal.onsenWoodDark)),
          Toon.flat(FlatOptions(color = Pal.onsenWoodDark)), Toon.flat(FlatOptions(color = Pal.onsenWoodDark)),
          Toon.flat(FlatOptions(color = 0xffffff, map = Some(art), cache = false)),
          Toon.flat(FlatOptions(color = Pal.onsenWoodDark))
        ))
      board.position.set(0.0, head + fh / 2.0 + 0.24, front + 0.14)
      board.castShadow = true
      ThreeInterop.addChild(g, board)
      Outline.hullOutline(board, OutlineOptions(thickness = 0.0032))
    }

    if o.awning then
      val aw   = w + 0.24
      val out  = o.awningOut
      val tilt = 0.26
      parts("tile") += bakePart(new BoxGeometry(aw, 0.1, out / math.cos(tilt)),
        Util.trs(0.0, head + 0.94, front + out / 2.0, -tilt, 0.0, 0.0))
      parts("tileEdge") += bakePart(new BoxGeometry(aw + 0.05, 0.12, 0.12),
        Util.trs(0.0, head + 0.94 - math.sin(tilt) * out / 2.0 - 0.04, front + out))
      Vector(-1.0, 1.0).foreach { s =>
        parts("wood") += bakePart(new BoxGeometry(0.09, 0.09, out),
          Util.trs(s * (aw / 2.0 - 0.14), head + 0.9 + math.sin(tilt) * out / 2.0, front + out / 2.0, -tilt, 0.0, 0.0))
        parts("wood") += bakePart(new BoxGeometry(0.08, 0.5, 0.08),
          Util.trs(s * (aw / 2.0 - 0.14), head + 0.66, front + 0.16, -0.7, 0.0, 0.0))
      }

    if floors == 2 then
      val wy    = h1 + h2 * 0.52
      val n     = o.windows
      val spanW = w - 1.0
      (0 until n).foreach { i =>
        val cx = -spanW / 2.0 + (spanW * (i + 0.5)) / n
        val ww = math.min(1.5, spanW / n - 0.35)
        parts("wall") += bakePart(new BoxGeometry(ww + 0.22, 1.32, 0.12), Util.trs(cx, wy, front - 0.18))
        val room = new Mesh(new PlaneGeometry(ww, 1.18),
          Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.tatamiRoom(i % 2)), cache = false)))
        room.position.set(cx, wy, front - 0.117)
        ThreeInterop.setUserDataFlag(room, "noOutline")
        ThreeInterop.addChild(g, room)
        if o.rooms then lit += flatMaterialOf(room)
        ThreeInterop.addChild(g, Util.box(ww, 1.18, 0.04, M.glass, cx, wy, front - 0.09))
        parts("wood") += bakePart(new BoxGeometry(ww + 0.16, 0.09, 0.09), Util.trs(cx, wy + 0.63, front - 0.07))
        parts("wood") += bakePart(new BoxGeometry(ww + 0.16, 0.09, 0.09), Util.trs(cx, wy - 0.63, front - 0.07))
        parts("wood") += bakePart(new BoxGeometry(0.07, 1.28, 0.08), Util.trs(cx, wy, front - 0.07))
        Vector(-1.0, 1.0).foreach { s =>
          parts("wood") += bakePart(new BoxGeometry(0.09, 1.3, 0.09),
            Util.trs(cx + s * (ww / 2.0 + 0.04), wy, front - 0.07))
        }
      }
      if o.balcony then
        val bw = w - 0.6
        val bd = 0.62
        parts("wood") += bakePart(new BoxGeometry(bw, 0.1, bd), Util.trs(0.0, h1 + 0.42, front + bd / 2.0 - 0.14))
        parts("woodDark") += bakePart(new BoxGeometry(bw + 0.06, 0.12, 0.1), Util.trs(0.0, h1 + 0.47, front + bd - 0.16))
        (0 to 5).foreach { i =>
          parts("wood") += bakePart(new BoxGeometry(0.06, 0.86, 0.06),
            Util.trs(-bw / 2.0 + (bw * i) / 5.0, h1 + 0.9, front + bd - 0.16))
        }
        Vector(h1 + 1.3, h1 + 0.86).foreach { yy =>
          parts("wood") += bakePart(new BoxGeometry(bw + 0.1, 0.07, 0.08), Util.trs(0.0, yy, front + bd - 0.16))
        }
        Vector(-1.0, 1.0).foreach { s =>
          parts("wood") += bakePart(new BoxGeometry(0.07, 0.86, bd),
            Util.trs(s * bw / 2.0, h1 + 0.9, front + bd / 2.0 - 0.14))
        }
    end if

    if o.blade then
      val bs  = o.bladeSide
      val bh  = o.bladeH
      val art = Textures.onsenBlade(o.kind)
      val plate = new Mesh(new BoxGeometry(0.13, bh, 0.42),
        Props.matArray(
          Toon.flat(FlatOptions(color = 0xffffff, map = Some(art), cache = false)),
          Toon.flat(FlatOptions(color = 0xffffff, map = Some(art), cache = false)),
          Toon.flat(FlatOptions(color = Pal.onsenWoodDark)), Toon.flat(FlatOptions(color = Pal.onsenWoodDark)),
          Toon.flat(FlatOptions(color = Pal.onsenWoodDark)), Toon.flat(FlatOptions(color = Pal.onsenWoodDark))
        ))
      plate.position.set(bs * (w / 2.0 - 0.12), h1 * 0.5 + 0.5, front + 0.32)
      plate.castShadow = true
      ThreeInterop.addChild(g, plate)
      Outline.hullOutline(plate, OutlineOptions(thickness = 0.0032))
      parts("metal") += bakePart(new BoxGeometry(0.05, 0.05, 0.3),
        Util.trs(bs * (w / 2.0 - 0.12), h1 * 0.5 + 1.36, front + 0.14))
      parts("metal") += bakePart(new BoxGeometry(0.05, 0.05, 0.3),
        Util.trs(bs * (w / 2.0 - 0.12), h1 * 0.5 - 0.36, front + 0.14))

    o.noren.foreach { noren =>
      val nw = doorW + 0.16
      val nh = 0.62
      val cloth = new Mesh(new PlaneGeometry(nw, nh),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.onsenNoren(noren)),
          transparent = true, side = 2, cache = false)))
      cloth.position.set(doorC, head - 0.1 - nh / 2.0, front + 0.26)
      ThreeInterop.setUserDataFlag(cloth, "noOutline")
      ThreeInterop.addChild(g, cloth)
      parts("wood") += bakePart(new BoxGeometry(nw + 0.14, 0.06, 0.06), Util.trs(doorC, head - 0.06, front + 0.26))
    }

    val matFor: Map[String, Material[?]] = Map(
      "wall"     -> wallMat, "wood"     -> M.wood, "woodDark"   -> M.woodDark, "tile" -> M.tile,
      "tileEdge" -> M.tileEdge, "stone" -> M.stoneWarm, "metal" -> M.metalDark
    )
    parts.foreach { (key, bucket) =>
      if bucket.nonEmpty then
        val mesh = new Mesh(Util.bake(bucket.toList), matFor(key))
        mesh.castShadow = true
        mesh.receiveShadow = true
        ThreeInterop.addChild(g, mesh)
        if key == "wall" || key == "tile" then Outline.hullOutline(mesh, OutlineOptions(thickness = 0.0032))
    }

    g.position.set(o.x, o.y, o.z)
    g.rotation.y = Buildings.faceRy(o.face)
    ctx.add(g)

    val fx = o.face == Buildings.Face.XPlus || o.face == Buildings.Face.XMinus
    val hw = (if fx then d else w) / 2.0 + 0.1
    val hd = (if fx then w else d) / 2.0 + 0.1
    ctx.collide(o.x - hw, o.z - hd, o.x + hw, o.z + hd, o.y + h + 1.4)
    OnsenUnit(g, lit.toVector)
  end makeOnsenUnit

  private def flatMaterialOf(mesh: Mesh[?, ?, ?]): MeshBasicMaterial =
    mesh.material match
      case basic: MeshBasicMaterial => basic
      case _                        => throw new IllegalStateException("sakura-crossing: warm light must be an unlit material")

  final private case class Glowing(group: Group[Object3DEventMap], glow: MeshBasicMaterial)

  private def propCtx(ctx: WorldCtx): PropCtx =
    PropCtx(
      add = obj => ctx.add(obj),
      collide = (x0, z0, x1, z1, top) => ctx.collide(x0, z0, x1, z1, top),
      groundAt = (px, pz) => ctx.groundAt(px, pz)
    )

  /** 石灯籠: base, shaft, table, fire box with its two openings, and the cap. */
  private def makeStoneLantern(x: Double, z: Double, y: Double, ry: Double, scale: Double): Glowing =
    val g = new Group()
    val s = scale
    ThreeInterop.addChild(g, Util.cyl(0.24 * s, 0.3 * s, 0.16 * s, 8, M.stoneDark, 0.0, 0.08 * s, 0.0))
    ThreeInterop.addChild(g, Util.cyl(0.1 * s, 0.12 * s, 0.72 * s, 8, M.stone, 0.0, 0.52 * s, 0.0))
    ThreeInterop.addChild(g, Util.cyl(0.26 * s, 0.2 * s, 0.1 * s, 8, M.stoneDark, 0.0, 0.93 * s, 0.0))
    val fire = new Mesh(new CylinderGeometry(0.21 * s, 0.23 * s, 0.3 * s, 6), M.stone)
    fire.position.y = 1.13 * s
    fire.castShadow = true
    ThreeInterop.addChild(g, fire)
    var glowMat: MeshBasicMaterial = null
    Vector(0.0, math.Pi / 2.0).foreach { windowRy =>
      val win = new Mesh(new PlaneGeometry(0.15 * s, 0.17 * s),
        Toon.flat(FlatOptions(color = 0xf6dfae, side = 2, cache = false)))
      win.position.set(math.sin(windowRy) * 0.2 * s, 1.13 * s, math.cos(windowRy) * 0.2 * s)
      win.rotation.y = windowRy
      ThreeInterop.setUserDataFlag(win, "noOutline")
      ThreeInterop.addChild(g, win)
      glowMat = flatMaterialOf(win)
    }
    val cap = new Mesh(new ConeGeometry(0.36 * s, 0.24 * s, 6), M.stoneDark)
    cap.position.y = 1.4 * s
    cap.castShadow = true
    ThreeInterop.addChild(g, cap)
    ThreeInterop.addChild(g, Util.cyl(0.05 * s, 0.07 * s, 0.14 * s, 6, M.stoneDark, 0.0, 1.57 * s, 0.0))
    g.position.set(x, y, z)
    g.rotation.y = ry
    Glowing(g, glowMat)
  end makeStoneLantern

  /** A clipped garden pine: a leaning trunk and three flat cushions of needle. */
  private def makePine(
    x: Double,
    z: Double,
    y: Double,
    ry: Double,
    scale: Double,
    lean: Double,
    deep: Boolean
  ): Group[Object3DEventMap] =
    val g     = new Group()
    val s     = scale
    val trunk = Util.cyl(0.05 * s, 0.09 * s, 1.0 * s, 6, M.trunkDark, 0.0, 0.5 * s, 0.0)
    trunk.rotation.z = lean
    trunk.castShadow = true
    ThreeInterop.addChild(g, trunk)
    val tip = new Vector3(0.0, 0.5 * s, 0.0).applyEuler(new Euler(0.0, 0.0, lean))
    val cushions = Vector(
      (tip.x - 0.42 * s, tip.y * 0.62, 0.1 * s, 0.46 * s), (tip.x + 0.34 * s, tip.y * 1.02, -0.16 * s, 0.4 * s),
      (tip.x * 1.2, tip.y * 1.42, 0.06 * s, 0.34 * s)
    )
    cushions.foreach { case (cx, cy, cz, cr) =>
      val blob = new Mesh(new SphereGeometry(cr, 7, 5), if deep then M.pineDeep else M.pine)
      blob.position.set(cx, cy, cz)
      blob.scale.y = 0.52
      blob.castShadow = true
      ThreeInterop.addChild(g, blob)
      ThreeInterop.addChild(g, Util.cyl(0.022 * s, 0.03 * s, 0.3 * s, 5, M.trunkDark, cx * 0.6, cy - 0.1 * s, cz * 0.6))
    }
    g.position.set(x, y, z)
    g.rotation.y = ry
    g
  end makePine

  /** 竹垣: split canes on two rails, tied off, with a post at each end. */
  private def makeBambooFence(
    ctx: WorldCtx,
    axis: String,
    at: Double,
    from: Double,
    to: Double,
    y: Double,
    h: Double
  ): Unit =
    val lo    = math.min(from, to)
    val hi    = math.max(from, to)
    val len   = hi - lo
    val canes = mutable.ArrayBuffer.empty[Util.BakePart]
    val rails = mutable.ArrayBuffer.empty[Util.BakePart]
    val n     = math.max(2, math.round(len / 0.11).toInt)
    (0 until n).foreach { i =>
      val c       = lo + (len * (i + 0.5)) / n
      val cluster = new CylinderGeometry(0.042, 0.042, h, 5)
      canes += Util.BakePart(cluster,
        Some(if axis == "z" then Util.trs(at, h / 2.0, c) else Util.trs(c, h / 2.0, at)))
    }
    Vector(h * 0.24, h * 0.74).foreach { railY =>
      val rail = new CylinderGeometry(0.05, 0.05, len, 6)
      rails += Util.BakePart(rail,
        Some(
          if axis == "z" then Util.trs(at + 0.06, railY, (lo + hi) / 2.0, math.Pi / 2.0, 0.0, 0.0)
          else Util.trs((lo + hi) / 2.0, railY, at + 0.06, 0.0, 0.0, math.Pi / 2.0)
        ))
    }
    Vector(lo, hi).foreach { c =>
      val postGeo = new CylinderGeometry(0.07, 0.07, h + 0.16, 6)
      rails += Util.BakePart(postGeo,
        Some(
          if axis == "z" then Util.trs(at, (h + 0.16) / 2.0, c) else Util.trs(c, (h + 0.16) / 2.0, at)
        ))
    }
    val g  = new Group()
    val cm = new Mesh(Util.bake(canes.toList), M.bamboo)
    cm.castShadow = true
    cm.receiveShadow = true
    ThreeInterop.addChild(g, cm)
    val rm = new Mesh(Util.bake(rails.toList), M.bambooDeep)
    rm.castShadow = true
    ThreeInterop.addChild(g, rm)
    g.position.y = y
    ctx.add(g)
    if axis == "z" then ctx.collide(at - 0.12, lo, at + 0.12, hi, y + h)
    else ctx.collide(lo, at - 0.12, hi, at + 0.12, y + h)
  end makeBambooFence

  /** A landscape rock: one low-poly lump, squashed and turned. */
  private def makeRock(
    x: Double,
    z: Double,
    y: Double,
    r: Double,
    ry: Double,
    dark: Boolean,
    flat: Double = 0.62,
    long: Double = 1.25,
    tilt: Double = 0.1,
    roll: Double = 0.08
  ): Mesh[?, ?, ?] =
    val rock = new Mesh(new DodecahedronGeometry(r, 0.0), if dark then M.stoneDark else M.stone)
    rock.scale.set(1.0, flat, long)
    rock.rotation.set(tilt, ry, roll)
    rock.position.set(x, y + r * flat * 0.62, z)
    rock.castShadow = true
    rock.receiveShadow = true
    rock

  /** The old platform scale in the bath's changing lobby. */
  private def makeScale(x: Double, z: Double, y: Double, ry: Double): Group[Object3DEventMap] =
    val g = new Group()
    ThreeInterop.addChild(g, Util.box(0.42, 0.1, 0.34, M.metalDark, 0.0, 0.05, 0.0))
    ThreeInterop.addChild(g,
      Util.box(0.36, 0.04, 0.28,
        Toon.cel(CelOptions(color = 0x8a6a44, bands = 3, tint = 0x5c5680)), 0.0, 0.12, 0.0))
    ThreeInterop.addChild(g, Util.cyl(0.022, 0.022, 1.0, 6, M.metal, 0.0, 0.6, -0.1))
    val dial = new Mesh(new CylinderGeometry(0.17, 0.17, 0.06, 14), M.metal)
    dial.rotation.x = math.Pi / 2.0 - 0.3
    dial.position.set(0.0, 1.12, -0.08)
    dial.castShadow = true
    ThreeInterop.addChild(g, dial)
    val face = new Mesh(new CircleGeometry(0.14, 14), Toon.flat(FlatOptions(color = 0xf4efe2)))
    face.rotation.x = -0.3
    face.position.set(0.0, 1.13, 0.0)
    ThreeInterop.setUserDataFlag(face, "noOutline")
    ThreeInterop.addChild(g, face)
    g.position.set(x, y, z)
    g.rotation.y = ry
    g
  end makeScale

  /** A rack of 下駄 outside a ryokan door: a slatted frame with pairs on it. */
  private def makeGetaRack(
    x: Double,
    z: Double,
    y: Double,
    ry: Double,
    w: Double,
    n: Int,
    seed: Int
  ): Group[Object3DEventMap] =
    val rng = Util.rngKit(seed)
    val g   = new Group()
    ThreeInterop.addChild(g, Util.box(w, 0.06, 0.42, M.woodPale, 0.0, 0.3, 0.0))
    ThreeInterop.addChild(g, Util.box(w, 0.06, 0.42, M.woodPale, 0.0, 0.06, 0.0))
    Vector(-1.0, 1.0).foreach { s =>
      ThreeInterop.addChild(g, Util.box(0.07, 0.36, 0.07, M.woodDark, s * (w / 2.0 - 0.05), 0.18, -0.16))
      ThreeInterop.addChild(g, Util.box(0.07, 0.36, 0.07, M.woodDark, s * (w / 2.0 - 0.05), 0.18, 0.16))
    }
    val geta = mutable.ArrayBuffer.empty[Util.BakePart]
    (0 until n).foreach { i =>
      val cx = -w / 2.0 + (w * (i + 0.5)) / n
      Vector(-1.0, 1.0).foreach { s =>
        geta += bakePart(new BoxGeometry(0.1, 0.035, 0.24),
          Util.trs(cx + s * 0.06, 0.355, rng.range(-0.05, 0.05), 0.0, rng.range(-0.14, 0.14), 0.0))
        Vector(-0.07, 0.07).foreach { t =>
          geta += bakePart(new BoxGeometry(0.08, 0.05, 0.03), Util.trs(cx + s * 0.06, 0.31, t))
        }
      }
    }
    val gm = new Mesh(Util.bake(geta.toList), Toon.cel(CelOptions(color = 0xb99a72, bands = 3, tint = 0x6f5680)))
    gm.castShadow = true
    ThreeInterop.addChild(g, gm)
    g.position.set(x, y, z)
    g.rotation.y = ry
    g
  end makeGetaRack

  /** A warm door lamp on a wall bracket: the one saturated light on the street. */
  private def makeDoorLamp(x: Double, y: Double, z: Double, ry: Double, out: Double): Glowing =
    val g = new Group()
    ThreeInterop.addChild(g, Util.box(0.08, 0.2, 0.06, M.metalDark, 0.0, 0.0, 0.03))
    ThreeInterop.addChild(g, Util.box(0.05, 0.05, out, M.metalDark, 0.0, 0.06, out / 2.0))
    val body = new Mesh(new BoxGeometry(0.2, 0.26, 0.2), M.woodDark)
    body.position.set(0.0, -0.06, out)
    body.castShadow = true
    ThreeInterop.addChild(g, body)
    ThreeInterop.addChild(g, Util.box(0.28, 0.05, 0.28, M.metalDark, 0.0, 0.09, out))
    val paper = new Mesh(new BoxGeometry(0.16, 0.2, 0.16), Toon.flat(FlatOptions(color = 0xf6dfae, cache = false)))
    paper.position.set(0.0, -0.06, out)
    ThreeInterop.setUserDataFlag(paper, "noOutline")
    ThreeInterop.addChild(g, paper)
    g.position.set(x, y, z)
    g.rotation.y = ry
    Glowing(g, flatMaterialOf(paper))

  /** A paper lantern in the onsen street's own paper. */
  private def makeOnsenLantern(
    x: Double,
    y: Double,
    z: Double,
    r: Double,
    variant: Int,
    drop: Double,
    ry: Double
  ): Glowing =
    val g = new Group()
    val body = new Mesh(new CylinderGeometry(r, r, r * 2.2, 12, 1, true),
      Toon.flat(FlatOptions(color = Pal.lanternLit, map = Some(Textures.onsenLanternTex(variant)),
        side = 2, cache = false)))
    body.position.y = -r * 1.1
    ThreeInterop.addChild(g, body)
    Vector(0.0, -r * 2.2).foreach { yy =>
      ThreeInterop.addChild(g, Util.cyl(r * 0.42, r * 0.42, 0.045, 10, M.woodDark, 0.0, yy, 0.0))
    }
    ThreeInterop.addChild(g, Util.cyl(0.014, 0.014, drop, 4, M.woodDark, 0.0, drop / 2.0, 0.0))
    g.position.set(x, y, z)
    g.rotation.y = ry
    Glowing(g, flatMaterialOf(body))
  end makeOnsenLantern

  final private case class SignPlate(
    map: Texture[?, ?],
    w: Double = 0.5,
    h: Double = 0.5,
    y: Option[Double] = None,
    ry: Double = 0.0,
    double: Boolean = false,
    back: Option[Int] = None
  )

  /** Port of upstream `props.js` makeSignPost -- Lane 2's carries no plates, and this street
    * hangs notices on four of them.
    */
  private def makeSignPost(
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
      val side = Toon.flat(FlatOptions(color = plate.back.getOrElse(Pal.wallGray)))
      val face = Toon.flat(FlatOptions(color = 0xffffff, map = Some(plate.map), cache = false))
      val back = if plate.double then face else side
      val t    = if plate.double then 0.12 else 0.05
      val board = new Mesh(new BoxGeometry(plate.w, plate.h, t),
        Props.matArray(side, side, side, side, face, back))
      board.position.set(0.0, plate.y.getOrElse(h - 0.4), if plate.double then 0.0 else 0.058)
      board.rotation.y = plate.ry
      board.castShadow = true
      ThreeInterop.addChild(g, board)
      Outline.hullOutline(board, OutlineOptions(thickness = 0.0028))
    }
    g.position.set(x, y, z)
    g.rotation.y = ry
    g
  end makeSignPost

  def buildOnsen(ctx: WorldCtx): Planting =
    val gm     = Ground.groundMats()
    val rng    = Util.rngKit(9701)
    val sakura = mutable.ArrayBuffer.empty[SakuraSpot]
    val shrubs = mutable.ArrayBuffer.empty[ShrubSpot]
    val grove  = mutable.ArrayBuffer.empty[GroveSpot]
    val bamboo = mutable.ArrayBuffer.empty[BambooClump]
    val petals = mutable.ArrayBuffer.empty[PetalPatch]
    val warm   = mutable.ArrayBuffer.empty[WarmLight]

    def glow(material: MeshBasicMaterial, along: Double): Unit =
      warm += new WarmLight(material, along, material.map != null)

    def lantern(
      x: Double,
      y: Double,
      z: Double,
      r: Double,
      variant: Int,
      drop: Double,
      along: Double,
      ry: Double
    ): Group[Object3DEventMap] =
      val lit = makeOnsenLantern(x, y, z, r, variant, drop, ry)
      glow(lit.glow, along)
      lit.group

    buildTerrace(ctx, gm)
    buildApproach(ctx, lantern)
    buildChannel(ctx, rng)
    buildBridge(ctx, lantern, petals)
    buildRyokan(ctx, gm, rng, sakura, shrubs, bamboo, glow)
    buildBath(ctx, lantern)
    buildAshiyu(ctx, glow)
    buildShops(ctx, rng, glow, lantern)
    buildDeck(ctx, lantern, sakura)
    dressStreet(ctx, rng, glow, lantern, sakura, shrubs, grove, petals)

    {
      val off     = new Color(0xb6afbe)
      val offRoom = new Color(0x9a94a6)
      val hold    = 22.0
      val spread  = 78.0
      val fade    = 1.9
      val sorted  = warm.sortBy(_.along)
      val span    = math.max(1, sorted.length - 1)
      sorted.zipWithIndex.foreach { (light, i) =>
        light.at = hold + (spread * i) / span
        light.on = light.material.color.clone()
        light.off = if light.hasMap then offRoom else off
        light.material.color.copy(light.off)
      }
      var elapsed = 0.0
      var done    = false
      ctx.update { dt =>
        if !done then
          elapsed += dt
          if elapsed >= hold + spread + fade then
            sorted.foreach(light => light.material.color.copy(light.on))
            done = true
          else
            sorted.foreach { light =>
              val k = math.min(1.0, math.max(0.0, (elapsed - light.at) / fade))
              light.material.color.lerpColors(light.off, light.on, k * k * (3 - 2 * k))
            }
      }
    }

    Planting(sakura = sakura.toVector, shrubs = shrubs.toVector, bamboo = bamboo.toVector,
      grove = grove.toVector, petals = petals.toVector)
  end buildOnsen

  /** Three pads rather than one: the flight up from the shrine lands inside the terrace
    * footprint, and the height query takes the max over platforms.
    */
  private def buildTerrace(ctx: WorldCtx, gm: Ground.GroundMaterials): Unit =
    val notchL = GateX - GateHalf - 0.5
    val notchR = GateX + GateHalf + 0.5
    val stairZ = 43.6
    val cl     = ChX - ChHalf
    val cr     = ChX + ChHalf
    val blocks = Vector(
      (TerX0, cl, TerZ0, TerZ1), (cr, notchL, TerZ0, 52.5), (cr, notchL, 54.3, TerZ1), (-32.5, notchL, 52.5, 54.3),
      (notchR, TerX1, TerZ0, TerZ1), (notchL, notchR, stairZ, TerZ1), (cl, cr, TerZ0, ChZ0), (cl, cr, ChZ1, TerZ1)
    )
    blocks.foreach { case (bx0, bx1, bz0, bz1) =>
      Ground.pad(ctx,
        Ground.PadOptions(x = (bx0 + bx1) / 2.0, z = (bz0 + bz1) / 2.0,
          w = bx1 - bx0, d = bz1 - bz0, y = Y - 0.09, h = 0.09, mat = Some(gm.stoneWarm),
          name = "onsenTerrace"))
    }

    val h = Y + 0.55
    val runs = Vector(
      ("x", TerZ0, TerX0, GateX - GateHalf), ("x", TerZ0, GateX + GateHalf, TerX1), ("z", TerX0, TerZ0, TerZ1),
      ("z", TerX1, TerZ0, 47.6), ("z", TerX1, 50.2, TerZ1), ("x", TerZ1, TerX0, -27.0), ("x", TerZ1, -19.6, TerX1)
    )
    runs.foreach { case (axis, at, from, to) =>
      Ground.wallRun(ctx,
        Ground.WallOptions(axis = axis, at = at, from = from, to = to,
          y = 0.0, h = h, t = 0.4, panel = 3.8, mat = Some(M.stoneDark), capMat = Some(M.stoneWarm)))
    }

    Vector((GateX - GateHalf - 0.2, TerZ0), (GateX + GateHalf + 0.2, TerZ0), (TerX1, 47.4), (TerX1, 50.4)).foreach {
      case (px, pz) =>
        val pier = Util.box(0.72, h + 0.34, 0.72, M.stoneDark, px, (h + 0.34) / 2.0 - 0.05, pz)
        pier.castShadow = true
        pier.receiveShadow = true
        ctx.add(pier)
        ctx.add(Util.box(0.9, 0.14, 0.9, M.stoneWarm, px, h + 0.36, pz))
        ctx.collide(px - 0.4, pz - 0.4, px + 0.4, pz + 0.4, h + 0.4)
    }

    {
      val x0      = TerX0 + 0.6
      val x1      = TerX1 - 0.6
      val n       = math.round((x1 - x0) / 0.92).toInt
      val slabs   = mutable.ArrayBuffer.empty[Util.BakePart]
      val jointed = mutable.ArrayBuffer.empty[Util.BakePart]
      (0 until n).foreach { i =>
        val cx = x0 + ((x1 - x0) * (i + 0.5)) / n
        slabs += bakePart(new BoxGeometry((x1 - x0) / n - 0.06, 0.05, StZ1 - StZ0 - 0.24),
          Util.trs(cx, 0.0, (StZ0 + StZ1) / 2.0))
        Vector(-1.0, 1.0).foreach { s =>
          jointed += bakePart(new BoxGeometry((x1 - x0) / n - 0.06, 0.05, 0.34),
            Util.trs(cx, 0.0, (StZ0 + StZ1) / 2.0 + s * ((StZ1 - StZ0) / 2.0 - 0.13)))
        }
      }
      val sm = new Mesh(Util.bake(slabs.toList), M.slab)
      sm.position.y = Y + 0.025
      sm.receiveShadow = true
      sm.name = "onsenSlabs"
      ctx.add(sm)
      val jm = new Mesh(Util.bake(jointed.toList), M.stoneDark)
      jm.position.y = Y + 0.022
      jm.receiveShadow = true
      ThreeInterop.setUserDataFlag(jm, "noOutline")
      ctx.add(jm)
    }
  end buildTerrace

  private def buildApproach(
    ctx: WorldCtx,
    lantern: (Double, Double, Double, Double, Int, Double, Double, Double) => Group[Object3DEventMap]
  ): Unit =
    val rise = 0.1855
    val n    = 6
    val run  = 0.5
    val z0   = 40.6
    Ground.steps(ctx,
      Ground.StepsOptions(x = GateX, z = z0, axis = "z", dir = 1, n = n,
        rise = rise, run = run, w = 2.4, y = 2.09, mat = Some(M.stone), lipMat = Some(M.stoneDark)))
    Vector(-1.0, 1.0).foreach { s =>
      val cheekParts = (0 until n).map { i =>
        val h = rise * (i + 1) + 0.24
        Util.BakePart(new BoxGeometry(0.3, h, run),
          Some(Util.trs(GateX + s * 1.35, 2.09 + h / 2.0, z0 + run * (i + 0.5))))
      }
      val cheek = new Mesh(Util.bake(cheekParts.toList), M.stoneDark)
      cheek.castShadow = true
      cheek.receiveShadow = true
      ctx.add(cheek)
      ctx.collide(GateX + s * 1.35 - 0.2, z0, GateX + s * 1.35 + 0.2, z0 + run * n, Y + 0.3)
    }
    ctx.add(Util.box(2.9, 2.2, run * n + 0.3, M.stoneDark, GateX, 1.0, z0 + (run * n) / 2.0))

    {
      val shrineTop = 2.09
      val lz0       = 40.0
      val lz1       = z0 - 0.13
      Ground.pad(ctx,
        Ground.PadOptions(x = GateX, z = (lz0 + lz1) / 2.0, w = 2.4, d = lz1 - lz0,
          y = shrineTop - 0.07, h = 0.07, mat = Some(M.stone), name = "onsenGateLanding",
          platform = false))
      ctx.platform(GateX - 1.2, GateX + 1.2, lz0 - 0.1, lz1 + 0.23, shrineTop)
    }

    {
      val h = 2.9
      Vector(-1.0, 1.0).foreach { s =>
        val px   = GateX + s * (GateHalf + 0.02)
        val post = Util.box(0.26, h, 0.3, M.woodDark, px, 2.09 + h / 2.0, z0 - 0.1)
        post.castShadow = true
        ctx.add(post)
        ctx.add(Util.box(0.4, 0.12, 0.44, M.tileEdge, px, 2.09 + h + 0.06, z0 - 0.1))
      }
      val lintel = Util.box(GateHalf * 2 + 0.6, 0.28, 0.24, M.woodDark, GateX, 2.09 + h - 0.3, z0 - 0.1)
      lintel.castShadow = true
      ctx.add(lintel)
      Outline.hullOutline(lintel, OutlineOptions(thickness = 0.0032))
      Vector(-1.0, 1.0).foreach { s =>
        ctx.add(lantern(GateX + s * (GateHalf - 0.24), 2.09 + h - 0.46, z0 - 0.02, 0.15, 1, 0.2, -0.6, 0.0))
      }
    }

    {
      val bx = GateX + 1.95
      val bz = 44.4
      Vector(-1.0, 1.0).foreach { s =>
        ctx.add(Util.box(0.09, 1.5, 0.09, M.woodDark, bx + s * 0.5, Y + 0.75, bz))
      }
      val board = new Mesh(new BoxGeometry(1.28, 0.8, 0.09),
        Props.matArray(
          Toon.flat(FlatOptions(color = Pal.onsenWoodDark)), Toon.flat(FlatOptions(color = Pal.onsenWoodDark)),
          Toon.flat(FlatOptions(color = Pal.onsenWoodDark)), Toon.flat(FlatOptions(color = Pal.onsenWoodDark)),
          Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.yunosakaBoard()), cache = false)),
          Toon.flat(FlatOptions(color = Pal.onsenWoodDark))
        ))
      board.position.set(bx, Y + 1.4, bz - 0.02)
      board.rotation.y = math.Pi
      board.castShadow = true
      ctx.add(board)
      val hood = Util.box(1.5, 0.09, 0.34, M.tileEdge, bx, Y + 1.86, bz - 0.06)
      hood.rotation.x = 0.2
      hood.castShadow = true
      ctx.add(hood)
      ctx.collide(bx - 0.7, bz - 0.16, bx + 0.7, bz + 0.16, Y + 1.9)
    }

    {
      val rise2 = 0.1852
      val n2    = 15
      val base  = 0.44
      Ground.steps(ctx,
        Ground.StepsOptions(x = TerX1 + 6.9, z = 48.9, axis = "x", dir = -1, n = n2,
          rise = rise2, run = 0.46, w = 2.3, y = base, mat = Some(M.stone), lipMat = Some(M.stoneDark)))
      Vector(-1.0, 1.0).foreach { s =>
        val cheekParts = (0 until n2).map { i =>
          val h = rise2 * (i + 1) + 0.2
          Util.BakePart(new BoxGeometry(0.46, h, 0.3),
            Some(Util.trs(TerX1 + 6.9 - 0.46 * (i + 0.5), base + h / 2.0, 48.9 + s * 1.3)))
        }
        val cheek = new Mesh(Util.bake(cheekParts.toList), M.stoneDark)
        cheek.castShadow = true
        cheek.receiveShadow = true
        ctx.add(cheek)
        ctx.collide(TerX1, 48.9 + s * 1.3 - 0.2, TerX1 + 6.9, 48.9 + s * 1.3 + 0.2, Y + 0.3)
      }
      val l     = 0.46 * n2
      val rh    = rise2 * n2
      val cut   = 0.75
      val runL  = l - cut
      val riseL = rh * (runL / l)
      val rail = Util.cyl(0.032, 0.032, math.hypot(runL, riseL) + 0.3, 7, M.metalDark,
        TerX1 + 6.9 - runL / 2.0, base + riseL / 2.0 + 0.95, 50.2)
      rail.rotation.z = math.Pi / 2.0 - math.atan2(riseL, runL)
      rail.castShadow = true
      ctx.add(rail)
      (0 to 4).foreach { i =>
        val t = i / 4.0
        ctx.add(Util.cyl(0.03, 0.03, 0.95, 6, M.metalDark,
          TerX1 + 6.9 - runL * t, base + riseL * t + 0.47, 50.2))
      }
    }
  end buildApproach

  private def buildChannel(ctx: WorldCtx, rng: Util.RngKit): Unit =
    val wall = mutable.ArrayBuffer.empty[Util.BakePart]
    val bed  = mutable.ArrayBuffer.empty[Util.BakePart]
    Vector(-1.0, 1.0).foreach { s =>
      wall += bakePart(new BoxGeometry(0.34, 1.0, ChZ1 - ChZ0),
        Util.trs(ChX + s * (ChHalf + 0.17), Y - 0.5, (ChZ0 + ChZ1) / 2.0))
      wall += bakePart(new BoxGeometry(0.5, 0.12, ChZ1 - ChZ0),
        Util.trs(ChX + s * (ChHalf + 0.19), Y + 0.03, (ChZ0 + ChZ1) / 2.0))
    }
    bed += bakePart(new BoxGeometry(ChHalf * 2.0, 0.14, ChZ1 - ChZ0),
      Util.trs(ChX, ChBed - 0.07, (ChZ0 + ChZ1) / 2.0))
    val wm = new Mesh(Util.bake(wall.toList), M.stoneDark)
    wm.castShadow = true
    wm.receiveShadow = true
    ctx.add(wm)
    Outline.hullOutline(wm, OutlineOptions(thickness = 0.003))
    ctx.add(new Mesh(Util.bake(bed.toList), M.stoneWarm))

    {
      val geo = new PlaneGeometry(ChHalf * 2.0 - 0.06, ChZ1 - ChZ0 - 0.1)
      geo.rotateX(-math.Pi / 2.0)
      val surf = new Mesh(geo, M.water)
      surf.position.set(ChX, ChW, (ChZ0 + ChZ1) / 2.0)
      ThreeInterop.setUserDataFlag(surf, "noOutline")
      surf.renderOrder = 1.0
      ctx.add(surf)
      val streak = mutable.ArrayBuffer.empty[Util.BakePart]
      (0 until 22).foreach { i =>
        val zc = ChZ0 + 0.6 + ((ChZ1 - ChZ0 - 1.2) * i) / 21.0
        streak += bakePart(new BoxGeometry(rng.range(0.1, 0.34), 0.01, rng.range(0.3, 0.9)),
          Util.trs(ChX + rng.range(-0.4, 0.4), 0.0, zc))
      }
      val st = new Mesh(Util.bake(streak.toList),
        Toon.flat(FlatOptions(color = 0xe8f2f0,
          transparent = true, opacity = 0.5, depthWrite = Some(false), cache = false)))
      st.position.y = ChW + 0.012
      ThreeInterop.setUserDataFlag(st, "noOutline")
      st.renderOrder = 2.0
      ctx.add(st)
    }
    ctx.platform(ChX - ChHalf, ChX + ChHalf, ChZ0, ChZ1, ChW)

    {
      val sz   = ChZ1 + 0.1
      val head = Util.box(1.5, 1.1, 0.7, M.stoneDark, ChX, Y + 0.05, sz + 0.3)
      head.castShadow = true
      head.receiveShadow = true
      ctx.add(head)
      ctx.add(Util.box(1.7, 0.14, 0.86, M.stoneWarm, ChX, Y + 0.67, sz + 0.3))
      ctx.collide(ChX - 0.85, sz - 0.1, ChX + 0.85, sz + 0.75, Y + 0.7)
      val spout = Util.cyl(0.06, 0.06, 0.6, 7, M.bamboo, ChX, Y + 0.3, sz - 0.12)
      spout.rotation.x = math.Pi / 2.0 - 0.22
      ctx.add(spout)
      val fall = Util.box(0.09, 0.42, 0.05,
        Toon.flat(FlatOptions(color = 0xdcecea, transparent = true,
          opacity = 0.62, depthWrite = Some(false), cache = false)),
        ChX, Y + 0.06, sz - 0.34)
      ThreeInterop.setUserDataFlag(fall, "noOutline")
      ctx.add(fall)
      ctx.add(makeSignPost(x = ChX + 1.5, z = sz + 0.2, y = Y, ry = -math.Pi / 2.0, h = 1.5,
        postMat = M.woodDark, plates = Vector(SignPlate(map = Textures.paperSheet(), w = 0.34, h = 0.26, y = Some(1.22)))))
    }

    {
      val arch = new Mesh(new CylinderGeometry(ChHalf + 0.1, ChHalf + 0.1, 0.7, 12, 1, false, 0.0, math.Pi),
        M.stoneDark)
      arch.rotation.set(math.Pi / 2.0, 0.0, 0.0)
      arch.position.set(ChX, ChW - 0.02, ChZ0 - 0.15)
      ctx.add(arch)
      ctx.add(Util.box(ChHalf * 2.0 + 0.9, 0.9, 0.3, M.stoneDark, ChX, Y - 0.42, ChZ0 - 0.02))
    }

    val landZ = 53.4
    Vector(-1.0, 1.0).foreach { s =>
      val rx = ChX + s * (ChHalf + 0.42)
      val spans =
        if s < 0 then Vector((ChZ0 + 0.4, StZ0 - 0.5), (StZ1 + 0.5, ChZ1 - 0.3))
        else Vector((ChZ0 + 0.4, StZ0 - 0.5), (StZ1 + 0.5, landZ - 1.0), (landZ + 1.0, ChZ1 - 0.3))
      spans.foreach { case (a, b) =>
        if b - a >= 0.5 then
          Ground.railing(ctx,
            Ground.RailingOptions(axis = "z", at = rx, from = a, to = b,
              y = Y, h = 0.92, spacing = 1.5, mat = Some(M.metalDark)))
      }
    }

    {
      val lx = ChX + ChHalf + 0.5
      Ground.steps(ctx,
        Ground.StepsOptions(x = lx + 0.9, z = landZ, axis = "x", dir = -1, n = 3,
          rise = 0.19, run = 0.36, w = 1.5, y = ChW + 0.05, mat = Some(M.stone), lipMat = Some(M.stoneDark)))
      ctx.add(Util.box(0.9, 0.1, 1.5, M.stone, lx - 0.35, ChW + 0.05, landZ))
      ctx.platform(lx - 0.8, lx + 0.1, landZ - 0.75, landZ + 0.75, ChW + 0.1)
    }

    val puffs = mutable.ArrayBuffer.empty[(Mesh[?, ?, ?], Drift)]
    {
      val geo = new PlaneGeometry(1.0, 1.0)
      val mat = Toon.flat(FlatOptions(color = Pal.onsenSteam, transparent = true, opacity = 0.17,
        side = 2, depthWrite = Some(false), cache = false))
      (0 until 7).foreach { _ =>
        val p = new Mesh(geo, mat)
        val s = rng.range(1.0, 1.9)
        p.scale.set(s, s * rng.range(0.5, 0.8), 1.0)
        p.position.set(ChX + rng.range(-0.3, 0.3), ChW + rng.range(0.35, 0.95),
          ChZ0 + 1.5 + rng.range(0, ChZ1 - ChZ0 - 3))
        p.rotation.y = rng.range(0, math.Pi)
        ThreeInterop.setUserDataFlag(p, "noOutline")
        p.renderOrder = 3.0
        ThreeInterop.setUserDataFlag(p, "planetRigid")
        val drift = Drift(p.position.y, rng.range(0, 6.3), rng.range(0.09, 0.2))
        ctx.add(p)
        puffs += ((p, drift))
      }
    }
    var t = 0.0
    ctx.update { dt =>
      t += dt
      puffs.foreach { case (p, drift) =>
        p.position.y = drift.y0 + math.sin(t * drift.sp + drift.ph) * 0.16
        flatMaterialOf(p).opacity = 0.13 + 0.06 * (0.5 + 0.5 * math.sin(t * drift.sp * 1.7 + drift.ph))
      }
    }
  end buildChannel

  private def buildBridge(
    ctx: WorldCtx,
    lantern: (Double, Double, Double, Double, Int, Double, Double, Double) => Group[Object3DEventMap],
    petals: mutable.ArrayBuffer[PetalPatch]
  ): Unit =
    val w    = StZ1 - StZ0 + 0.4
    val zc   = (StZ0 + StZ1) / 2.0
    val span = ChHalf * 2.0 + 1.9
    val n    = 9
    val deck = mutable.ArrayBuffer.empty[Util.BakePart]
    (0 until n).foreach { i =>
      val t      = (i + 0.5) / n
      val camber = math.sin(t * math.Pi) * 0.11
      deck += bakePart(new BoxGeometry(span / n + 0.01, 0.14, w),
        Util.trs(ChX - span / 2.0 + span * t, Y + 0.06 + camber, zc))
      ctx.platform(ChX - span / 2.0 + (span * i) / n - 0.05, ChX - span / 2.0 + (span * (i + 1)) / n + 0.05,
        zc - w / 2.0, zc + w / 2.0, Y + 0.13 + camber)
    }
    val dm = new Mesh(Util.bake(deck.toList), M.wood)
    dm.castShadow = true
    dm.receiveShadow = true
    dm.name = "onsenBridgeDeck"
    ctx.add(dm)
    Outline.hullOutline(dm, OutlineOptions(thickness = 0.0032))
    Vector(-1.0, 1.0).foreach { s =>
      ctx.add(Util.box(span, 0.16, 0.18, M.woodDark, ChX, Y - 0.04, zc + s * (w / 2.0 - 0.3)))
    }

    val rails = mutable.ArrayBuffer.empty[Util.BakePart]
    Vector(-1.0, 1.0).foreach { sx =>
      Vector(-1.0, 1.0).foreach { sz =>
        val px   = ChX + sx * (span / 2.0 - 0.16)
        val pz   = zc + sz * (w / 2.0 - 0.12)
        val post = Util.box(0.19, 1.06, 0.19, M.woodDark, px, Y + 0.62, pz)
        post.castShadow = true
        ctx.add(post)
        ctx.add(Util.cyl(0.11, 0.13, 0.14, 8, M.woodPale, px, Y + 1.2, pz))
      }
      val px = ChX + sx * (span / 2.0 - 0.16)
      Vector(Y + 0.98, Y + 0.62).foreach { yy =>
        rails += bakePart(new CylinderGeometry(0.05, 0.05, w - 0.24, 7),
          Util.trs(px, yy, zc, math.Pi / 2.0, 0.0, 0.0))
      }
    }
    val rm = new Mesh(Util.bake(rails.toList), M.wood)
    rm.castShadow = true
    ctx.add(rm)

    Vector(-1.0, 1.0).foreach { sx =>
      ctx.add(lantern(ChX + sx * (span / 2.0 - 0.16), Y + 1.62, zc - w / 2.0 + 0.12, 0.16, 0, 0.24,
        -ChX + sx * 0.4, 0.0))
    }
    petals += PetalPatch(x = ChX, z = zc, w = span, d = w, y = Y + 0.2, n = 34)
  end buildBridge

  private def buildRyokan(
    ctx: WorldCtx,
    gm: Ground.GroundMaterials,
    rng: Util.RngKit,
    sakura: mutable.ArrayBuffer[SakuraSpot],
    shrubs: mutable.ArrayBuffer[ShrubSpot],
    bamboo: mutable.ArrayBuffer[BambooClump],
    glow: (MeshBasicMaterial, Double) => Unit
  ): Unit =
    val x0     = -46.0
    val x1     = -36.6
    val cx     = (x0 + x1) / 2.0
    val courtZ = 51.2
    val frontZ = 54.4
    val d      = 5.0

    val unit = makeOnsenUnit(ctx,
      OnsenUnitOptions(
        x = cx, z = frontZ + d / 2.0, y = Y, w = x1 - x0, d = d, face = Buildings.Face.ZMinus,
        kind = Textures.OnsenKind.Yunoya, floors = 2, h1 = 3.2, h2 = 2.8, hip = true, roofH = Some(1.5), eave = 0.78,
        doorW = 2.0, doorAt = -0.149, noren = Some(Textures.OnsenNorenKind.Yunoya), blade = true, bladeSide = -1.0,
        bladeH = 2.2,
        balcony = true, windows = 3, awningOut = 1.1, interior = Some(1), seed = 9711
      ))
    unit.lit.foreach(material => glow(material, -cx + 2.0))

    Ground.pad(ctx,
      Ground.PadOptions(x = cx, z = (courtZ + frontZ) / 2.0, w = x1 - x0,
        d = frontZ - courtZ, y = Y, h = 0.05, mat = Some(gm.gravel), name = "ryokanCourt"))
    val gate = cx + 1.4
    makeBambooFence(ctx, "x", courtZ + 0.1, x0 + 0.2, gate - 0.85, Y + 0.05, 1.05)
    makeBambooFence(ctx, "x", courtZ + 0.1, gate + 0.85, x1 - 0.2, Y + 0.05, 1.05)

    Vector(-1.0, 1.0).foreach { s =>
      val px   = gate + s * 0.85
      val post = Util.box(0.2, 2.2, 0.2, M.woodDark, px, Y + 1.15, courtZ + 0.1)
      post.castShadow = true
      ctx.add(post)
      ctx.collide(px - 0.14, courtZ - 0.04, px + 0.14, courtZ + 0.24, Y + 2.2)
    }
    ctx.add(Util.box(2.34, 0.16, 0.5, M.tileEdge, gate, Y + 2.3, courtZ + 0.1))
    {
      val plate = new Mesh(new PlaneGeometry(0.2, 1.0),
        Toon.flat(
          FlatOptions(color = 0xffffff, map = Some(Textures.onsenBlade(Textures.OnsenKind.Yunoya)), cache = false)
        ))
      plate.position.set(gate - 0.85, Y + 1.4, courtZ - 0.02)
      plate.rotation.y = math.Pi
      ThreeInterop.setUserDataFlag(plate, "noOutline")
      ctx.add(plate)
    }

    {
      val stones = (0 until 6).map { i =>
        val t = i / 5.0
        Util.BakePart(new BoxGeometry(rng.range(0.44, 0.6), 0.07, rng.range(0.36, 0.5)),
          Some(Util.trs(gate + math.sin(t * 2.2) * 0.55, 0.0,
            courtZ + 0.45 + t * (frontZ - courtZ - 1.1), 0.0, rng.range(-0.3, 0.3), 0.0)))
      }
      val sm = new Mesh(Util.bake(stones.toList), M.stoneWarm)
      sm.position.y = Y + 0.07
      sm.receiveShadow = true
      ctx.add(sm)
    }

    {
      val px = cx + 1.4
      val w  = 3.4
      val dd = 1.5
      Ground.steps(ctx,
        Ground.StepsOptions(x = px, z = frontZ - 0.9, axis = "z", dir = 1, n = 2,
          rise = 0.14, run = 0.42, w = 2.6, y = Y, mat = Some(M.stone), lipMat = Some(M.stoneDark)))
      val posts = Vector(-1.0, 1.0).map { s =>
        Util.BakePart(new BoxGeometry(0.17, 2.7, 0.17),
          Some(Util.trs(px + s * (w / 2.0 - 0.1), Y + 1.35, frontZ - dd + 0.1)))
      }
      val pm = new Mesh(Util.bake(posts.toList), M.woodDark)
      pm.castShadow = true
      ctx.add(pm)
      ctx.add(Util.box(w + 0.7, 0.14, dd + 0.5, M.tile, px, Y + 2.78, frontZ - dd / 2.0 - 0.1))
      ctx.add(Util.box(w + 0.8, 0.16, 0.18, M.tileEdge, px, Y + 2.72, frontZ - dd - 0.34))
      ctx.add(Util.box(w + 0.4, 0.1, dd + 0.2, M.woodPale, px, Y + 2.66, frontZ - dd / 2.0 - 0.1))
      Vector(-1.0, 1.0).foreach { s =>
        val lamp = makeDoorLamp(px + s * (w / 2.0 - 0.14), Y + 2.3, frontZ - 0.12, math.Pi, 0.3)
        glow(lamp.glow, -cx + 1.0)
        ctx.add(lamp.group)
      }
      ctx.add(Props.makeDoormat(Props.DoormatOpts(x = px, z = frontZ - 0.55, y = Y + 0.28,
        w = 1.3, d = 0.66, color = 0x4a4050)))
      ctx.add(Props.makeUmbrellaStand(Props.UmbrellaStandOpts(x = px - 1.5, z = frontZ - 0.5,
        y = Y + 0.28, n = 4, seed = 9712)))
      ctx.add(makeGetaRack(x = px + 1.45, z = frontZ - 0.62, y = Y + 0.28, ry = -0.1, w = 1.1, n = 3, seed = 9713))
      ctx.add(Props.makeBucket(Props.BucketOpts(x = px - 2.4, z = frontZ - 0.4, y = Y, ry = 0.5, water = true)))
    }

    val gx = x0 + 2.2
    {
      val px = gx
      val pz = 52.6
      val rim = (0 until 10).map { i =>
        val a = (i / 10.0) * math.Pi * 2.0
        Util.BakePart(new BoxGeometry(0.42, 0.16, 0.3),
          Some(Util.trs(px + math.cos(a) * 1.06, 0.0, pz + math.sin(a) * 0.78, 0.0, -a, 0.0)))
      }
      val rm = new Mesh(Util.bake(rim.toList), M.stoneDark)
      rm.position.y = Y + 0.08
      rm.castShadow = true
      rm.receiveShadow = true
      ctx.add(rm)
      val geo = new CircleGeometry(1.0, 14)
      geo.rotateX(-math.Pi / 2.0)
      val water = new Mesh(geo,
        Toon.flat(FlatOptions(color = Pal.onsenWaterDeep, transparent = true,
          opacity = 0.85, cache = false)))
      water.scale.set(1.0, 1.0, 0.74)
      water.position.set(px, Y + 0.07, pz)
      ThreeInterop.setUserDataFlag(water, "noOutline")
      ctx.add(water)
      val sky = new Mesh(new CircleGeometry(0.34, 12),
        Toon.flat(FlatOptions(color = Pal.waterSky,
          transparent = true, opacity = 0.5, depthWrite = Some(false), cache = false)))
      sky.rotation.x = -math.Pi / 2.0
      sky.scale.set(1.0, 1.0, 0.5)
      sky.position.set(px - 0.3, Y + 0.08, pz + 0.16)
      ThreeInterop.setUserDataFlag(sky, "noOutline")
      ctx.add(sky)
      ctx.collide(px - 1.3, pz - 1.0, px + 1.3, pz + 1.0, Y + 0.24)
    }
    {
      val lamp = makeStoneLantern(gx + 1.5, 51.9, Y + 0.05, 0.4, 1.05)
      glow(lamp.glow, -cx + 0.4)
      ctx.add(lamp.group)
      ctx.collide(gx + 1.2, 51.6, gx + 1.8, 52.2, Y + 1.7)
    }
    ctx.add(makeRock(gx - 1.5, 51.9, Y + 0.05, 0.46, 0.6, dark = false))
    ctx.add(makeRock(gx - 0.9, 53.7, Y + 0.05, 0.3, 2.1, dark = true))
    ctx.add(makeRock(gx + 1.7, 53.5, Y + 0.05, 0.36, 4.0, dark = false))
    ctx.add(makePine(gx - 1.9, 53.2, Y + 0.05, 0.7, 1.5, 0.2, deep = false))
    ctx.add(makePine(gx + 0.4, 51.75, Y + 0.05, 2.4, 1.05, -0.14, deep = true))
    sakura += SakuraSpot(x = gx - 0.2, z = 54.0, y = Y + 0.05, scale = 1.06, seed = 9721,
      lean = 0.12, leanDir = Some(2.2))
    shrubs += ShrubSpot(x = gx + 2.4, z = 53.9, y = Y + 0.05, r = 0.4, count = 3, spread = 0.9, seed = 9722)
    bamboo += BambooClump(x = x0 + 0.7, z = 56.6, y = Y, n = 9, spread = 1.3, scale = 1.0, seed = 9723)
    Ground.dapple(ctx,
      Ground.DappleOptions(x = gx - 0.4, z = 53.4, y = Y + 0.05, r = 1.1,
        spread = 1.4, n = 6, rng = rng))

    ctx.add(Props.makePotShelf(Props.PotShelfOpts(x = x1 - 0.9, z = 52.0, y = Y + 0.05,
      ry = math.Pi, w = 1.2, n = 5, seed = 9724)))
    ctx.add(Props.makeBroom(Props.BroomOpts(x = x1 - 0.5, z = 53.6, y = Y + 0.05,
      tilt = 0.08, roll = -0.2, ry = 1.2)))

    val vent = mutable.ArrayBuffer.empty[VentPuff]
    {
      val vx = cx + 3.0
      val vz = frontZ + d + 0.1
      ctx.add(Util.box(0.7, 0.5, 0.3, M.metalDark, vx, Y + 2.6, vz))
      val geo = new PlaneGeometry(1.0, 1.0)
      val mat = Toon.flat(FlatOptions(color = Pal.onsenSteam, transparent = true, opacity = 0.15,
        side = 2, depthWrite = Some(false), cache = false))
      (0 until 3).foreach { i =>
        val p = new Mesh(geo, mat)
        val s = 1.1 + i * 0.5
        p.scale.set(s, s * 0.7, 1.0)
        p.position.set(vx + rng.range(-0.2, 0.2), Y + 3.2 + i * 0.7, vz + 0.3)
        p.rotation.y = rng.range(0, 3)
        ThreeInterop.setUserDataFlag(p, "noOutline")
        ThreeInterop.setUserDataFlag(p, "planetRigid")
        p.renderOrder = 3.0
        ctx.add(p)
        vent += VentPuff(p, p.position.y, i * 2.1)
      }
    }
    var t = 0.0
    ctx.update { dt =>
      t += dt
      vent.foreach { v =>
        v.mesh.position.y = v.y0 + math.sin(t * 0.16 + v.ph) * 0.2
        flatMaterialOf(v.mesh).opacity = 0.11 + 0.05 * (0.5 + 0.5 * math.sin(t * 0.24 + v.ph))
      }
    }
  end buildRyokan

  private def buildBath(
    ctx: WorldCtx,
    lantern: (Double, Double, Double, Double, Int, Double, Double, Double) => Group[Object3DEventMap]
  ): Unit =
    val x0 = -46.0
    val x1 = -38.2
    val cx = (x0 + x1) / 2.0
    val d  = 5.2
    val zc = StZ0 - d / 2.0
    val _ = makeOnsenUnit(ctx,
      OnsenUnitOptions(
        x = cx, z = zc, y = Y, w = x1 - x0, d = d, face = Buildings.Face.ZPlus,
        kind = Textures.OnsenKind.Hourai, floors = 1, h1 = 4.3, roofH = Some(1.5), eave = 0.72,
        doorW = 2.9, doorAt = 0.0, noren = None, blade = false, awningOut = 1.2,
        wallAlt = true, hollow = 2.0, seed = 9731
      ))

    {
      val kw = 3.9
      val n  = 14
      val pts = (0 to n).map { i =>
        val t  = i.toDouble / n
        val bx = (t - 0.5) * kw
        val by = math.cos((t - 0.5) * math.Pi) * 0.42 + math.cos((t - 0.5) * math.Pi * 3) * 0.1
        (bx, by)
      }
      val parts = (0 until n).map { i =>
        val (ax, ay)   = pts(i)
        val (bx2, by2) = pts(i + 1)
        val len        = math.hypot(bx2 - ax, by2 - ay)
        Util.BakePart(new BoxGeometry(len + 0.02, 0.14, 1.5),
          Some(Util.trs((ax + bx2) / 2.0, (ay + by2) / 2.0, 0.0, 0.0, 0.0, math.atan2(by2 - ay, bx2 - ax))))
      }
      val km = new Mesh(Util.bake(parts.toList), M.tile)
      km.position.set(cx, Y + 3.5, zc + d / 2.0 + 0.68)
      km.castShadow = true
      ctx.add(km)
      Outline.hullOutline(km, OutlineOptions(thickness = 0.0032))
      Vector(-1.0, 1.0).foreach { s =>
        ctx.add(Util.box(0.14, 1.5, 0.14, M.woodDark, cx + s * (kw / 2.0 - 0.2), Y + 2.7, zc + d / 2.0 + 1.28))
      }
    }

    {
      val bw    = 4.6
      val bh    = 1.52
      val art   = Textures.houraiFuji()
      val frame = Util.box(bw + 0.18, bh + 0.18, 0.1, M.woodDark, cx, Y + 3.05, StZ0 + 0.03)
      frame.castShadow = true
      ctx.add(frame)
      val board = new Mesh(new BoxGeometry(bw, bh, 0.1),
        Props.matArray(
          Toon.flat(FlatOptions(color = Pal.wallGray)), Toon.flat(FlatOptions(color = Pal.wallGray)),
          Toon.flat(FlatOptions(color = Pal.wallGray)), Toon.flat(FlatOptions(color = Pal.wallGray)),
          Toon.flat(FlatOptions(color = 0xffffff, map = Some(art), cache = false)),
          Toon.flat(FlatOptions(color = Pal.wallGray))
        ))
      board.position.set(cx, Y + 3.05, StZ0 + 0.09)
      board.castShadow = true
      ctx.add(board)
      Outline.hullOutline(board, OutlineOptions(thickness = 0.0032))
      Vector(-1.0, 1.0).foreach { s =>
        ctx.add(Util.box(0.3, 0.09, 0.44, M.metalDark, cx + s * 1.5, Y + 3.95, StZ0 + 0.24))
      }
    }

    Vector((-1.0, Textures.OnsenNorenKind.Male), (1.0, Textures.OnsenNorenKind.Female)).foreach { case (s, kind) =>
      val nx = cx + s * 0.78
      val cloth = new Mesh(new PlaneGeometry(1.34, 0.68),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.onsenNoren(kind)),
          transparent = true, side = 2, cache = false)))
      cloth.position.set(nx, Y + 2.1, StZ0 + 0.24)
      ThreeInterop.setUserDataFlag(cloth, "noOutline")
      ctx.add(cloth)
      ctx.add(Util.box(1.5, 0.07, 0.07, M.wood, nx, Y + 2.45, StZ0 + 0.24))
    }

    {
      val chx = cx - 2.2
      val chz = zc - d / 2.0 + 0.9
      val stack = Util.cyl(0.44, 0.56, 10.5, 10,
        Toon.cel(CelOptions(color = 0xa8756a, bands = 3, tint = 0x6f5680)), chx, Y + 5.25, chz)
      stack.castShadow = true
      stack.receiveShadow = true
      ctx.add(stack)
      Outline.hullOutline(stack, OutlineOptions(thickness = 0.0034))
      ctx.add(Util.cyl(0.66, 0.66, 0.32, 10, M.stoneDark, chx, Y + 10.6, chz))
      (0 until 4).foreach { i =>
        ctx.add(Util.cyl(0.58, 0.58, 0.15, 10,
          Toon.cel(CelOptions(color = 0x8f625c, bands = 3, tint = 0x6f5680)), chx, Y + 1.9 + i * 2.3, chz))
      }
      ctx.collide(chx - 0.6, chz - 0.6, chx + 0.6, chz + 0.6, Y + 10.8)
    }

    {
      val fz        = StZ0
      val lockerMat = Toon.cel(CelOptions(color = 0xb99a72, bands = 3, tint = 0x6f5680))
      val step = Util.box(3.6, 0.16, 0.44,
        Toon.cel(CelOptions(color = 0xd8cdb8, bands = 3, tint = 0x6a6288)), cx, Y + 0.08, fz - 0.68)
      step.receiveShadow = true
      ctx.add(step)
      ctx.add(Util.box(3.6, 0.2, 0.06, M.wood, cx, Y + 0.06, fz - 0.47))
      val bodies = mutable.ArrayBuffer.empty[Util.BakePart]
      val trim   = mutable.ArrayBuffer.empty[Util.BakePart]
      (0 until 3).foreach { i =>
        val lx = cx - 1.7 + i * 0.62
        bodies += bakePart(new BoxGeometry(0.58, 1.6, 0.34), Util.trs(lx, Y + 1.0, fz - 1.7))
        (0 until 4).foreach { r =>
          trim += bakePart(new BoxGeometry(0.52, 0.035, 0.04), Util.trs(lx, Y + 0.42 + r * 0.39, fz - 1.52))
          trim += bakePart(new BoxGeometry(0.05, 0.1, 0.03), Util.trs(lx + 0.19, Y + 0.36 + r * 0.39, fz - 1.52))
        }
      }
      val lm = new Mesh(Util.bake(bodies.toList), lockerMat)
      lm.castShadow = true
      lm.receiveShadow = true
      ctx.add(lm)
      ctx.add(new Mesh(Util.bake(trim.toList), Toon.cel(CelOptions(color = 0x8a6f52, bands = 2, tint = 0x5c5680))))
      ctx.add(makeScale(cx + 1.6, fz - 1.5, Y + 0.16, -0.3))
      ctx.add(Props.makeBench(Props.BenchOpts(x = cx + 0.65, z = fz - 0.55, y = Y + 0.24,
        ry = math.Pi, len = 1.5, back = false)))
    }

    ctx.add(Props.makeMilkCrate(Props.MilkCrateOpts(x = x1 - 0.7, z = StZ0 + 0.4, y = Y, n = 2, ry = math.Pi)))
    Vending.addVending(ctx,
      Vending.VendingOptions(x = x1 - 1.9, z = StZ0 + 0.55, y = Y, ry = 0.0,
        variant = 1, seed = 9741))
    ctx.add(Props.makeVendBin(Props.VendBinOpts(x = x1 - 3.15, z = StZ0 + 1.0, y = Y + 0.05, ry = 0.0,
      color = Pal.vendTeal)))
    ctx.add(Props.makeBench(Props.BenchOpts(x = x0 + 1.5, z = StZ0 + 0.6, y = Y, ry = 0.0, len = 1.3)))
    ctx.add(Props.makePlanter(Props.PlanterOpts(x = x0 + 0.5, z = StZ0 + 0.5, y = Y, r = 0.26,
      flower = true, seed = 9742, n = 5)))
    ctx.add(lantern(x1 - 0.2, Y + 2.9, StZ0 + 0.35, 0.19, 2, 0.3, -x0 + 1, 0.0))
    ctx.add(Util.box(0.5, 0.07, 0.07, M.metalDark, x1 - 0.42, Y + 3.18, StZ0 + 0.2))
  end buildBath

  private def buildAshiyu(ctx: WorldCtx, glow: (MeshBasicMaterial, Double) => Unit): Unit =
    val cx = -36.4
    val cz = StZ0 - 1.5
    val w  = 3.0
    val d  = 2.6
    Ground.pad(ctx,
      Ground.PadOptions(x = cx, z = cz, w = w + 0.6, d = d + 0.5, y = Y, h = 0.05,
        mat = Some(Ground.groundMats().stoneWarm), name = "ashiyuPad"))

    {
      val tw = 2.4
      val td = 1.0
      val th = 0.46
      val ring = Vector(
        (0.0, -td / 2.0, tw, 0.22), (0.0, td / 2.0, tw, 0.22), (-tw / 2.0, 0.0, 0.22, td + 0.22),
        (tw / 2.0, 0.0, 0.22, td + 0.22)
      ).map { case (ox, oz, sw, sd) =>
        Util.BakePart(new BoxGeometry(sw, th, sd), Some(Util.trs(cx + ox, th / 2.0, cz - 0.3 + oz)))
      }
      val rm = new Mesh(Util.bake(ring.toList), M.stoneDark)
      rm.position.y = Y + 0.05
      rm.castShadow = true
      rm.receiveShadow = true
      ctx.add(rm)
      Outline.hullOutline(rm, OutlineOptions(thickness = 0.003))
      ctx.add(Util.box(tw - 0.2, 0.1, td - 0.1, M.stoneWarm, cx, Y + 0.1, cz - 0.3))
      val geo = new PlaneGeometry(tw - 0.24, td - 0.14)
      geo.rotateX(-math.Pi / 2.0)
      val water = new Mesh(geo,
        Toon.flat(FlatOptions(color = Pal.onsenWater, transparent = true,
          opacity = 0.9, cache = false)))
      water.position.set(cx, Y + 0.4, cz - 0.3)
      ThreeInterop.setUserDataFlag(water, "noOutline")
      ctx.add(water)
      ctx.collide(cx - tw / 2.0 - 0.1, cz - 0.3 - td / 2.0 - 0.2, cx + tw / 2.0 + 0.1,
        cz - 0.3 + td / 2.0 + 0.2, Y + 0.5)
      val wet = new Mesh(new PlaneGeometry(tw + 0.5, 0.7),
        Toon.flat(FlatOptions(color = 0x9aa0a8,
          transparent = true, opacity = 0.3, depthWrite = Some(false), cache = false)))
      wet.rotation.x = -math.Pi / 2.0
      wet.position.set(cx, Y + 0.055, cz + 0.55)
      ThreeInterop.setUserDataFlag(wet, "noOutline")
      ctx.add(wet)
    }

    {
      val sx = cx + 1.35
      ctx.add(Util.box(0.3, 0.8, 0.3, M.stoneDark, sx, Y + 0.45, cz - 0.3))
      val pipe = Util.cyl(0.055, 0.055, 0.9, 7, M.bamboo, sx - 0.4, Y + 0.9, cz - 0.3)
      pipe.rotation.z = math.Pi / 2.0 - 0.2
      ctx.add(pipe)
      ctx.add(Util.cyl(0.06, 0.06, 0.55, 7, M.bambooDeep, sx, Y + 0.62, cz - 0.3))
      val fall = Util.box(0.06, 0.42, 0.05,
        Toon.flat(FlatOptions(color = 0xdcecea, transparent = true,
          opacity = 0.6, depthWrite = Some(false), cache = false)),
        sx - 0.78, Y + 0.6, cz - 0.3)
      ThreeInterop.setUserDataFlag(fall, "noOutline")
      ctx.add(fall)
    }

    {
      val h     = 2.5
      val posts = mutable.ArrayBuffer.empty[Util.BakePart]
      Vector(-1.0, 1.0).foreach { sx =>
        Vector(-1.0, 1.0).foreach { sz =>
          posts += bakePart(new BoxGeometry(0.14, h, 0.14),
            Util.trs(cx + sx * (w / 2.0 - 0.1), h / 2.0, cz + sz * (d / 2.0 - 0.1)))
        }
      }
      Vector(-1.0, 1.0).foreach { sz =>
        posts += bakePart(new BoxGeometry(w, 0.14, 0.12), Util.trs(cx, h - 0.08, cz + sz * (d / 2.0 - 0.1)))
      }
      val pm = new Mesh(Util.bake(posts.toList), M.woodDark)
      pm.position.y = Y + 0.05
      pm.castShadow = true
      ctx.add(pm)
      Vector(-1.0, 1.0).foreach { sx =>
        Vector(-1.0, 1.0).foreach { sz =>
          ctx.collide(cx + sx * (w / 2.0 - 0.1) - 0.12, cz + sz * (d / 2.0 - 0.1) - 0.12,
            cx + sx * (w / 2.0 - 0.1) + 0.12, cz + sz * (d / 2.0 - 0.1) + 0.12, Y + h)
        }
      }
      val tilt = 0.2
      val roof = Util.box(w + 0.7, 0.12, (d + 0.8) / math.cos(tilt), M.tile, cx, Y + h + 0.24, cz)
      roof.rotation.x = -tilt
      roof.castShadow = true
      ctx.add(roof)
      ctx.add(Util.box(w + 0.8, 0.14, 0.14, M.tileEdge, cx,
        Y + h + 0.24 - math.sin(tilt) * (d + 0.8) / 2.0 - 0.04, cz + (d + 0.8) / 2.0))
      val lamp = makeDoorLamp(cx - 0.9, Y + h - 0.05, cz - d / 2.0 + 0.14, 0.0, 0.0)
      glow(lamp.glow, -cx)
      ctx.add(lamp.group)
    }

    {
      val rx = cx - w / 2.0 + 0.1
      Vector(-0.7, 0.7).foreach { dz =>
        ctx.add(Util.cyl(0.035, 0.035, 1.0, 6, M.woodPale, rx - 0.24, Y + 0.55, cz + dz))
      }
      val bar = Util.cyl(0.035, 0.035, 1.5, 6, M.woodPale, rx - 0.24, Y + 1.02, cz)
      bar.rotation.x = math.Pi / 2.0
      ctx.add(bar)
      val towels = Vector((-0.42, 0.34, 0.5), (0.1, 0.3, 0.44), (0.55, 0.32, 0.54)).map {
        case (dz, w0, h0) =>
          Util.BakePart(new PlaneGeometry(w0, h0), Some(Util.trs(rx - 0.24, Y + 1.0 - h0 / 2.0, cz + dz)))
      }
      val tm = new Mesh(Util.bake(towels.toList),
        Toon.cel(CelOptions(color = 0xf2ece0, bands = 2, side = 2, tint = 0x6f6790)))
      tm.rotation.y = math.Pi / 2.0
      tm.castShadow = true
      ctx.add(tm)
      ctx.add(Props.makeBucket(Props.BucketOpts(x = rx - 0.1, z = cz + 1.05, y = Y + 0.05,
        ry = -0.6, tilt = 0.1)))
    }
    {
      val bx = cx + w / 2.0 + 0.35
      val bz = cz + 0.5
      Vector(-1.0, 1.0).foreach { s =>
        ctx.add(Util.box(0.08, 1.3, 0.08, M.woodDark, bx, Y + 0.65, bz + s * 0.28))
      }
      val plate = new Mesh(new PlaneGeometry(0.62, 0.82),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.ashiyuPlate()), cache = false)))
      plate.position.set(bx - 0.05, Y + 1.28, bz)
      plate.rotation.y = -math.Pi / 2.0
      ThreeInterop.setUserDataFlag(plate, "noOutline")
      ctx.add(plate)
      ctx.add(Util.box(0.1, 0.9, 0.72, M.woodPale, bx, Y + 1.28, bz))
      ctx.add(Util.box(0.16, 0.08, 0.86, M.tileEdge, bx, Y + 1.76, bz))
      ctx.collide(bx - 0.14, bz - 0.44, bx + 0.14, bz + 0.44, Y + 1.8)
    }

    {
      val geo = new PlaneGeometry(1.0, 1.0)
      val mat = Toon.flat(FlatOptions(color = Pal.onsenSteam, transparent = true, opacity = 0.14,
        side = 2, depthWrite = Some(false), cache = false))
      (0 until 2).foreach { i =>
        val p = new Mesh(geo, mat)
        p.scale.set(1.3, 0.7, 1.0)
        p.position.set(cx + (if i == 1 then 0.6 else -0.5), Y + 0.85 + i * 0.2, cz - 0.3)
        p.rotation.y = if i == 1 then 0.6 else -0.4
        ThreeInterop.setUserDataFlag(p, "noOutline")
        ThreeInterop.setUserDataFlag(p, "planetRigid")
        p.renderOrder = 3.0
        ctx.add(p)
      }
    }
  end buildAshiyu

  private def buildShops(
    ctx: WorldCtx,
    rng: Util.RngKit,
    glow: (MeshBasicMaterial, Double) => Unit,
    lantern: (Double, Double, Double, Double, Int, Double, Double, Double) => Group[Object3DEventMap]
  ): Unit =
    {
      val x0 = -32.4
      val x1 = -27.8
      val cx = (x0 + x1) / 2.0
      val d  = 4.8
      val u = makeOnsenUnit(ctx,
        OnsenUnitOptions(
          x = cx, z = StZ1 + d / 2.0, y = Y, w = x1 - x0, d = d, face = Buildings.Face.ZMinus,
          kind = Textures.OnsenKind.Sakuraan, floors = 2, h1 = 2.9, h2 = 2.3, roofH = Some(1.15), hip = false,
          doorW = 1.4, doorAt = -0.12, noren = Some(Textures.OnsenNorenKind.Kanmi), blade = true, bladeSide = 1.0,
          balcony = true, windows = 2, wallAlt = true, interior = Some(2), seed = 9751
        ))
      u.lit.foreach(material => glow(material, -cx + 0.6))
      ctx.add(Props.makeCrates(Props.CratesOpts(x = x1 - 0.5, z = StZ1 - 0.55, y = Y, n = 2,
        seed = 9752, ry = math.Pi + 0.2)))
      ctx.add(Props.makePlanter(Props.PlanterOpts(x = x0 + 0.45, z = StZ1 - 0.5, y = Y, r = 0.24,
        flower = true, seed = 9753, n = 5)))
      ctx.add(lantern(x0 + 0.35, Y + 2.55, StZ1 - 0.4, 0.15, 0, 0.24, -cx - 1, 0.0))
      ctx.add(lantern(x1 - 0.35, Y + 2.55, StZ1 - 0.4, 0.15, 0, 0.24, -cx + 1, 0.0))
      ctx.add(Props.makeCatBox(Props.CatBoxOpts(x = x1 + 0.3, z = StZ1 - 0.7, y = Y, ry = -0.7,
        cat = 0xd8c9b4)))
    }

    {
      val x0 = -33.0
      val x1 = -28.6
      val cx = (x0 + x1) / 2.0
      val d  = 4.6
      val u = makeOnsenUnit(ctx,
        OnsenUnitOptions(
          x = cx, z = StZ0 - d / 2.0, y = Y, w = x1 - x0, d = d, face = Buildings.Face.ZPlus,
          kind = Textures.OnsenKind.Yunoka, floors = 2, h1 = 2.9, h2 = 2.2, roofH = Some(1.1),
          doorW = 1.2, doorAt = 0.24, noren = Some(Textures.OnsenNorenKind.Kissa), blade = true, bladeSide = -1.0,
          windows = 2, awningOut = 1.0, seed = 9761
        ))
      u.lit.foreach(material => glow(material, -cx))
      {
        val wx = cx - 0.7
        val ww = 1.9
        val wh = 1.25
        val wy = Y + 1.5
        ctx.add(Util.box(ww + 0.2, wh + 0.2, 0.14, M.woodDark, wx, wy, StZ0 - 0.1))
        val room = new Mesh(new PlaneGeometry(ww, wh),
          Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.tatamiRoom(1)), cache = false)))
        room.position.set(wx, wy, StZ0 - 0.035)
        room.rotation.y = math.Pi
        ThreeInterop.setUserDataFlag(room, "noOutline")
        ctx.add(room)
        glow(flatMaterialOf(room), -cx - 0.2)
        ctx.add(Util.box(ww, wh, 0.04, M.glass, wx, wy, StZ0 - 0.005))
        ctx.add(Util.box(0.06, wh, 0.07, M.wood, wx, wy, StZ0 + 0.02))
        ctx.add(Util.box(ww + 0.14, 0.07, 0.08, M.wood, wx, wy + wh / 2.0, StZ0 + 0.02))
        ctx.add(Util.box(ww + 0.14, 0.07, 0.08, M.wood, wx, wy - wh / 2.0, StZ0 + 0.02))
      }
      ctx.add(makeSignPost(x = x1 - 0.4, z = StZ0 + 0.7, y = Y, ry = math.Pi, h = 1.3,
        postMat = M.woodDark, plates = Vector(SignPlate(map = Textures.poster(2), w = 0.42, h = 0.56, y = Some(1.0)))))
      ctx.add(Props.makePlanter(Props.PlanterOpts(x = x0 + 0.4, z = StZ0 + 0.45, y = Y, r = 0.22,
        flower = false, seed = 9762, n = 4)))
    }

    {
      val x0 = -28.2
      val x1 = -24.2
      val cx = (x0 + x1) / 2.0
      val d  = 4.4
      val _ = makeOnsenUnit(ctx,
        OnsenUnitOptions(
          x = cx, z = StZ0 - d / 2.0, y = Y, w = x1 - x0, d = d, face = Buildings.Face.ZPlus,
          kind = Textures.OnsenKind.Kokeshi, floors = 1, h1 = 3.1, roofH = Some(1.1),
          doorW = 1.6, noren = None, blade = true, bladeSide = 1.0, awningOut = 1.15,
          wallAlt = true, seed = 9771
        ))
      {
        val tx = cx
        val tz = StZ0 + 0.62
        ctx.add(Util.box(3.0, 0.09, 0.72, M.woodPale, tx, Y + 0.78, tz))
        Vector(-1.0, 1.0).foreach { s =>
          ctx.add(Util.box(0.09, 0.78, 0.09, M.woodDark, tx + s * 1.35, Y + 0.39, tz - 0.26))
          ctx.add(Util.box(0.09, 0.78, 0.09, M.woodDark, tx + s * 1.35, Y + 0.39, tz + 0.26))
        }
        val colors = Vector(0xb5322f, 0xf4c033, 0x2c3a52, 0xefe2c8, 0x8a4a62, 0x4f8f6a)
        (0 until 11).foreach { i =>
          val gx     = tx - 1.24 + (i % 6) * 0.5
          val gz     = tz + (if i < 6 then -0.16 else 0.18)
          val hh     = rng.range(0.18, 0.3)
          val matrix = Util.trs(gx, Y + 0.83 + hh / 2.0, gz)
          val mesh = new Mesh(new CylinderGeometry(0.055, 0.075, hh, 8),
            Toon.cel(CelOptions(color = colors(i % colors.length), bands = 3, tint = 0x6f6790)))
          mesh.applyMatrix4(matrix)
          mesh.castShadow = true
          ctx.add(mesh)
          val head = new Mesh(new SphereGeometry(0.062, 7, 5),
            Toon.cel(CelOptions(color = 0xf0e2cc, bands = 3, tint = 0x6f6790)))
          head.position.setFromMatrixPosition(matrix)
          head.position.y += 0.11
          ctx.add(head)
        }
        ctx.collide(tx - 1.6, tz - 0.45, tx + 1.6, tz + 0.45, Y + 0.9)
      }
      ctx.add(lantern(x0 + 0.35, Y + 2.5, StZ0 + 0.4, 0.15, 1, 0.24, -cx, 0.0))
      ctx.add(Props.makeBins(Props.BinsOpts(x = -24.0, z = StZ1 + 0.55, y = Y, ry = 0.0)))
    }
  end buildShops

  private def buildDeck(
    ctx: WorldCtx,
    lantern: (Double, Double, Double, Double, Int, Double, Double, Double) => Group[Object3DEventMap],
    sakura: mutable.ArrayBuffer[SakuraSpot]
  ): Unit =
    val dx = -21.6
    val dz = 55.4
    val dy = Y + 4.2
    val w  = 3.6
    val d  = 3.0

    val posts = mutable.ArrayBuffer.empty[Util.BakePart]
    Vector(-1.0, 1.0).foreach { sx =>
      Vector(-1.0, 1.0).foreach { sz =>
        val px = dx + sx * (w / 2.0 - 0.22)
        val pz = dz + sz * (d / 2.0 - 0.22)
        posts += bakePart(new BoxGeometry(0.22, dy - Y, 0.22), Util.trs(px, (dy - Y) / 2.0 + Y, pz))
        ctx.collide(px - 0.16, pz - 0.16, px + 0.16, pz + 0.16, dy)
      }
      posts += bakePart(new BoxGeometry(0.12, 0.12, d - 0.44), Util.trs(dx + sx * (w / 2.0 - 0.22), Y + 1.9, dz))
    }
    Vector(-1.0, 1.0).foreach { sz =>
      posts += bakePart(new BoxGeometry(w - 0.44, 0.12, 0.12),
        Util.trs(dx, Y + 1.9, dz + sz * (d / 2.0 - 0.22)))
      posts += bakePart(new BoxGeometry(math.hypot(w - 0.44, 2.0), 0.1, 0.1),
        Util.trs(dx, Y + 0.95, dz + sz * (d / 2.0 - 0.22), 0.0, 0.0, math.atan2(2.0, w - 0.44) * sz))
    }
    val pm = new Mesh(Util.bake(posts.toList), M.woodDark)
    pm.castShadow = true
    pm.receiveShadow = true
    ctx.add(pm)

    {
      val n = 9
      val boards = (0 until n).map { i =>
        Util.BakePart(new BoxGeometry(w / n - 0.03, 0.1, d),
          Some(Util.trs(dx - w / 2.0 + (w * (i + 0.5)) / n, 0.0, dz)))
      }
      val bm = new Mesh(Util.bake(boards.toList), M.wood)
      bm.position.y = dy - 0.05
      bm.castShadow = true
      bm.receiveShadow = true
      ctx.add(bm)
      Outline.hullOutline(bm, OutlineOptions(thickness = 0.0032))
      ctx.platform(dx - w / 2.0, dx + w / 2.0, dz - d / 2.0, dz + d / 2.0, dy)

      val rails = mutable.ArrayBuffer.empty[Util.BakePart]
      val sides = Vector(
        ("x", dz + d / 2.0 - 0.06, dx - w / 2.0, dx + w / 2.0), ("z", dx + w / 2.0 - 0.06, dz - d / 2.0, dz + d / 2.0),
        ("x", dz - d / 2.0 + 0.06, dx + 0.4, dx + w / 2.0), ("z", dx - w / 2.0 + 0.06, dz - d / 2.0, dz - 0.5)
      )
      sides.foreach { case (axis, at, from, to) =>
        val len = to - from
        val np  = math.max(2, math.round(len / 0.7).toInt)
        (0 to np).foreach { i =>
          val c = from + (len * i) / np
          rails += bakePart(new BoxGeometry(0.06, 0.94, 0.06),
            if axis == "z" then Util.trs(at, 0.47, c) else Util.trs(c, 0.47, at))
        }
        Vector(0.92, 0.5).foreach { yy =>
          rails += bakePart(
            new BoxGeometry(if axis == "z" then 0.08 else len, 0.07,
              if axis == "z" then len else 0.08),
            if axis == "z" then Util.trs(at, yy, (from + to) / 2.0)
            else Util.trs((from + to) / 2.0, yy, at)
          )
        }
        if axis == "z" then ctx.collide(at - 0.1, from, at + 0.1, to, dy + 0.95)
        else ctx.collide(from, at - 0.1, to, at + 0.1, dy + 0.95)
      }
      val rm = new Mesh(Util.bake(rails.toList), M.woodPale)
      rm.position.y = dy
      rm.castShadow = true
      ctx.add(rm)
      ctx.add(lantern(dx + w / 2.0 - 0.06, dy + 1.5, dz - d / 2.0 + 0.06, 0.16, 1, 0.26, 40.0, 0.0))
    }

    val rise = 0.21
    val run1 = 0.26
    val run2 = 0.30
    val n1   = 12
    val n2   = 8
    val mid  = Y + n1 * rise
    val f1x  = -19.60
    val f2z  = dz - d / 2.0 - 0.70
    val lz0  = f2z - 0.75
    val f2x0 = f1x - 0.75
    Ground.steps(ctx,
      Ground.StepsOptions(x = f1x, z = lz0 - n1 * run1, axis = "z", dir = 1, n = n1,
        rise = rise, run = run1, w = 1.5, y = Y, mat = Some(M.wood), lipMat = Some(M.woodDark)))
    ctx.platform(f2x0, f1x + 0.75, lz0 - 0.1, f2z + 0.75, mid)
    ctx.add(Util.box(1.5, 0.12, 1.5, M.wood, f1x, mid - 0.06, f2z))
    Ground.steps(ctx,
      Ground.StepsOptions(x = f2x0, z = f2z, axis = "x", dir = -1, n = n2,
        rise = rise, run = run2, w = 1.5, y = mid, mat = Some(M.wood), lipMat = Some(M.woodDark)))
    ctx.platform(f2x0 - n2 * run2, f2x0 - n2 * run2 + 1.0, f2z + 0.6, dz - d / 2.0 + 0.2, dy)

    {
      val rh   = 0.95
      val f1z0 = lz0 - n1 * run1
      val legs = mutable.ArrayBuffer.empty[Util.BakePart]
      (0 to n1 by 2).foreach { i =>
        Vector(-1.0, 1.0).foreach { s =>
          legs += bakePart(new BoxGeometry(0.1, rise * i + 0.4, 0.1),
            Util.trs(f1x + s * 0.72, Y + (rise * i + 0.4) / 2.0, f1z0 + i * run1))
        }
      }
      (0 to n2 by 2).foreach { i =>
        val h = mid + rise * i
        legs += bakePart(new BoxGeometry(0.1, h - Y + 0.2, 0.1),
          Util.trs(f2x0 - i * run2, (h + Y) / 2.0 + 0.1, f2z - 0.72))
      }
      val lm = new Mesh(Util.bake(legs.toList), M.woodDark)
      lm.castShadow = true
      ctx.add(lm)

      val rails = mutable.ArrayBuffer.empty[Util.BakePart]
      (0 to n1).foreach { i =>
        rails += bakePart(new CylinderGeometry(0.028, 0.03, rh, 6),
          Util.trs(f1x - 0.72, Y + rise * i + rh / 2.0, f1z0 + i * run1))
      }
      {
        val run     = n1 * run1
        val riseAll = n1 * rise
        rails += bakePart(new CylinderGeometry(0.035, 0.035, math.hypot(run, riseAll) + 0.3, 7),
          Util.trs(f1x - 0.72, Y + riseAll / 2.0 + rh, f1z0 + run / 2.0,
            math.Pi / 2.0 - math.atan2(riseAll, run), 0.0, 0.0))
      }
      (0 to n2).foreach { i =>
        rails += bakePart(new CylinderGeometry(0.028, 0.03, rh, 6),
          Util.trs(f2x0 - i * run2, mid + rise * i + rh / 2.0, f2z - 0.72))
      }
      {
        val run     = n2 * run2
        val riseAll = n2 * rise
        rails += bakePart(new CylinderGeometry(0.035, 0.035, math.hypot(run, riseAll) + 0.3, 7),
          Util.trs(f2x0 - run / 2.0, mid + riseAll / 2.0 + rh, f2z - 0.72,
            0.0, 0.0, math.Pi / 2.0 - math.atan2(riseAll, run)))
      }
      val rm = new Mesh(Util.bake(rails.toList), M.woodPale)
      rm.castShadow = true
      ctx.add(rm)

      ctx.collide(f1x - 0.82, f1z0 - 0.2, f1x - 0.62, lz0, mid + 1.0)
      (0 until n2 by 2).foreach { i =>
        val x1 = f2x0 - i * run2
        val x0 = f2x0 - math.min(i + 2, n2) * run2
        ctx.collide(x0, f2z - 0.82, x1, f2z - 0.62, mid + rise * i + 0.95)
      }
    }

    sakura += SakuraSpot(x = dx - 9.0, z = 58.4, y = Y, scale = 1.16, seed = 9781,
      lean = 0.12, leanDir = Some(5.1))
  end buildDeck

  private def dressStreet(
    ctx: WorldCtx,
    rng: Util.RngKit,
    glow: (MeshBasicMaterial, Double) => Unit,
    lantern: (Double, Double, Double, Double, Int, Double, Double, Double) => Group[Object3DEventMap],
    sakura: mutable.ArrayBuffer[SakuraSpot],
    shrubs: mutable.ArrayBuffer[ShrubSpot],
    grove: mutable.ArrayBuffer[GroveSpot],
    petals: mutable.ArrayBuffer[PetalPatch]
  ): Unit =
    Vector((-44.2, 1.0), (-40.6, 1.0), (-31.4, 1.0), (-28.4, 1.0), (-25.4, 1.0), (-44.6, -1.0), (-40.2, -1.0),
      (-31.2, -1.0), (-27.4, -1.0)).foreach { case (lx, side) =>
      val lz = if side > 0 then StZ0 + 0.3 else StZ1 - 0.3
      ctx.add(lantern(lx, Y + 3.0, lz, 0.16, rng.int(0, 1), 0.26, -lx, 0.0))
      ctx.add(Util.box(0.06, 0.06, 0.42, M.metalDark, lx, Y + 3.3, lz + side * 0.2))
    }

    {
      val lx     = TerX0 + 0.9
      val lz     = (StZ0 + StZ1) / 2.0
      val plinth = Util.box(1.5, 1.1, 1.5, M.stoneDark, lx, Y + 0.55, lz)
      plinth.castShadow = true
      plinth.receiveShadow = true
      ctx.add(plinth)
      ctx.add(Util.box(1.74, 0.16, 1.74, M.stoneWarm, lx, Y + 1.18, lz))
      val big = makeStoneLantern(lx, lz, Y + 1.26, 0.35, 2.0)
      glow(big.glow, -lx)
      ctx.add(big.group)
      ctx.collide(lx - 0.85, lz - 0.85, lx + 0.85, lz + 0.85, Y + 4.4)
      Vector(-1.0, 1.0).foreach { s =>
        ctx.add(Util.box(0.22, 0.9, 0.22, M.woodDark, lx + 1.4, Y + 0.45, lz + s * 1.6))
        ctx.add(Util.box(0.3, 0.1, 0.3, M.tileEdge, lx + 1.4, Y + 0.94, lz + s * 1.6))
      }
    }

    ctx.add(Props.makeBench(Props.BenchOpts(x = -25.6, z = StZ1 - 0.75, y = Y, ry = math.Pi, len = 1.8)))
    ctx.add(Props.makeBench(Props.BenchOpts(x = -23.4, z = StZ1 - 0.75, y = Y, ry = math.Pi, len = 1.8)))
    ctx.add(Props.makePlanter(Props.PlanterOpts(x = -22.0, z = StZ1 - 0.7, y = Y, r = 0.26,
      flower = true, seed = 9791, n = 5)))
    ctx.add(makeSignPost(x = -19.9, z = 48.9, y = Y, ry = -math.Pi / 2.0, h = 1.8,
      postMat = M.woodDark,
      plates = Vector(SignPlate(map = Textures.paperSheet(), w = 0.4, h = 0.3, y = Some(1.5), double = true))))
    Props.makeLoosePaper(propCtx(ctx),
      Vector(
        Props.PaperSpot(x = -29.6, z = StZ0 + 0.7, y = Y + 0.06, ry = 0.8),
        Props.PaperSpot(x = -38.9, z = StZ1 - 0.8, y = Y + 0.06, ry = -0.5)
      ))

    {
      val grates = (0 until 13).map { i =>
        Util.BakePart(new BoxGeometry(0.3, 0.04, 0.62),
          Some(Util.trs(TerX0 + 2.4 + i * 2.0, 0.0, (StZ0 + StZ1) / 2.0)))
      }
      val gmesh = new Mesh(Util.bake(grates.toList), M.drain)
      gmesh.position.y = Y + 0.055
      gmesh.receiveShadow = true
      ctx.add(gmesh)
    }

    sakura += SakuraSpot(x = -35.0, z = StZ1 + 0.9, y = Y, scale = 1.1, seed = 9801,
      lean = 0.11, leanDir = Some(1.4))
    sakura += SakuraSpot(x = -24.0, z = 44.4, y = Y, scale = 1.18, seed = 9802,
      lean = 0.1, leanDir = Some(3.9))
    (0 until 5).foreach { i =>
      grove += GroveSpot(x = TerX0 - 2.6 - (i % 2) * 2.2, z = 43.0 + i * 3.6, y = 0.4,
        scale = 1.45 + (i                     % 3) * 0.15, seed = 9810 + i, spread = 1.15, lean = 0.06,
        leanDir = Some(i * 1.3))
    }
    (0 until 4).foreach { i =>
      grove += GroveSpot(x = TerX1 + 3.4 + (i % 2) * 2.4, z = 52.0 + i * 3.4, y = 0.45,
        scale = 1.35 + (i                     % 2) * 0.2, seed = 9830 + i, spread = 1.1)
    }
    shrubs += ShrubSpot(x = -19.6, z = 52.6, y = Y, r = 0.46, count = 3, spread = 1.1, seed = 9840)
    shrubs += ShrubSpot(x = -46.0, z = 44.4, y = Y, r = 0.5, count = 3, spread = 1.2, seed = 9841)
    petals += PetalPatch(x = -35.0, z = (StZ0 + StZ1) / 2.0, w = 22.0, d = 4.4, y = Y + 0.06, n = 150)
    petals += PetalPatch(x = -42.0, z = 52.8, w = 8.0, d = 3.0, y = Y + 0.06, n = 70)
  end dressStreet
end Onsen
