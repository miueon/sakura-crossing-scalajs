package sakura.world

import sakura.ThreeInterop
import sakura.core.{Outline, Pal, Textures, Toon, Util}
import sakura.core.Outline.OutlineOptions
import sakura.core.Toon.{CelOptions, FlatOptions}
import vendor.three.*

import scala.collection.mutable
import scala.math
import scala.scalajs.js

/** 1:1 port of upstream `src/world/shrine.js`: the community shrine -- an alley approach, a
  * terrace 2.09 m up a flight of stone steps, the hall, the ablution pavilion, the torii, the
  * planting, and the kit somebody maintains it with.
  */
object Shrine:

  private object M:
    lazy val torii       = Toon.cel(CelOptions(color = Pal.torii, bands = 3, tint = 0x7a4060))
    lazy val toriiDeep   = Toon.cel(CelOptions(color = Pal.toriiDeep, bands = 3, tint = 0x7a4060))
    lazy val wood        = Toon.cel(CelOptions(color = Pal.shrineWood, bands = 3, tint = 0x5c5680))
    lazy val woodDark    = Toon.cel(CelOptions(color = Pal.shrineWoodDark, bands = 3, tint = 0x554e74))
    lazy val roof        = Toon.cel(CelOptions(color = Pal.shrineRoof, bands = 3, tint = 0x4a4468))
    lazy val stone       = Toon.cel(CelOptions(color = Pal.stone, bands = 3, tint = 0x655d80))
    lazy val stoneDark   = Toon.cel(CelOptions(color = Pal.stoneDark, bands = 3, tint = 0x605878))
    lazy val stoneWarm   = Toon.cel(CelOptions(color = Pal.stoneWarm, bands = 3, tint = 0x655d80))
    lazy val rope        = Toon.cel(CelOptions(color = Pal.rope, bands = 3, tint = 0x8a7f9c))
    lazy val paper       = Toon.cel(CelOptions(color = 0xfbf8f0, bands = 2, side = 2, tint = 0x8e86ad))
    lazy val metal       = Toon.cel(CelOptions(color = Pal.metal, bands = 3, tint = 0x666090))
    lazy val metalDark   = Toon.cel(CelOptions(color = Pal.metalDark, bands = 3, tint = 0x5c5680))
    lazy val bronze      = Toon.cel(CelOptions(color = 0x8f7448, bands = 3, tint = 0x6a5a80))
    lazy val water       = Toon.flat(FlatOptions(color = Pal.water, transparent = true, opacity = 0.78, cache = false))
    lazy val recess      = Toon.cel(CelOptions(color = 0x4c4656, bands = 2, tint = 0x453f5c))
    lazy val mirror      = Toon.cel(CelOptions(color = 0xb8b0a0, bands = 2, tint = 0x7d74a0))
    lazy val cord        = Toon.cel(CelOptions(color = 0xc8503c, bands = 3, tint = 0x7a4060))
    lazy val ring        = Toon.cel(CelOptions(color = 0xe0e0e0, bands = 2, tint = 0x8a7f9c))
    lazy val offertory   = Toon.cel(CelOptions(color = 0x3c3746, bands = 2, tint = 0x453f5c))
    lazy val tuft        = Toon.cel(CelOptions(color = 0xdfd2b4, bands = 2, tint = 0x8a7f9c))
    lazy val bambooMat   = Toon.cel(CelOptions(color = Pal.bamboo, bands = 3, tint = 0x5b6f8c))
    lazy val bambooDeep  = Toon.cel(CelOptions(color = Pal.bambooDeep, bands = 3, tint = 0x5b6f8c))
    lazy val troughInner = Toon.cel(CelOptions(color = 0x8f8a9c, bands = 2, tint = 0x605878))
    lazy val ladle       = Toon.cel(CelOptions(color = 0xb08f62, bands = 3, tint = 0x6f6790))
    lazy val lantern     = Toon.cel(CelOptions(color = 0x4a4454, bands = 2, tint = 0x453f5c))
    lazy val plate       = Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.alleyPlate()), cache = false))
    lazy val shrineName  = Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.shrineName()), cache = false))
    lazy val sanpai      = Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.sanpaiNotice()), cache = false))
    lazy val bib         = Toon.cel(CelOptions(color = Pal.shrineBib, bands = 2, side = 2, tint = 0x7a4060))
  end M

  private val LaneX  = -27.9
  private val AxisX  = -27.9
  private val StepZ0 = 20.5
  private val NSteps = 11
  private val Rise   = 0.19
  private val Run    = 0.46
  private val Top    = Rise * NSteps
  private val TerX0  = -36.0
  private val TerX1  = -20.0
  private val TerZ0  = 25.4
  private val TerZ1  = 40.0

  private def bakePart(geo: BufferGeometry[?, ?], mx: Matrix4): Util.BakePart =
    Util.BakePart(geo, Some(mx))

  private def shimenawa(parent: Object3D[?], y: Double, w: Double, z: Double, thick: Double, n: Int): Unit =
    val segments = 8
    val points = (0 to segments).map { index =>
      val u = index.toDouble / segments
      new Vector3(-w / 2.0 + w * u, y - math.sin(math.Pi * u) * 0.12, z)
    }
    val curve = new CatmullRomCurve3(js.Array(points*))
    val rope  = new Mesh(new TubeGeometry(curve, 14, thick, 6, false), M.rope)
    rope.castShadow = true
    ThreeInterop.addChild(parent, rope)
    (0 until 3).foreach { index =>
      val u = 0.25 + index * 0.25
      val tuft = Util.cyl(thick * 1.35, thick * 1.35, thick * 0.7, 8, M.tuft,
        -w / 2.0 + w * u, y - math.sin(math.Pi * u) * 0.12, z)
      tuft.rotateZ(math.Pi / 2.0)
      ThreeInterop.addChild(parent, tuft)
    }
    (0 until n).foreach { index =>
      val u     = (index + 0.5) / n
      val px    = -w / 2.0 + w * u
      val py    = y - math.sin(math.Pi * u) * 0.12 - thick
      val strip = new Group()
      (0 until 6).foreach { k =>
        val q = new Mesh(new PlaneGeometry(0.085, 0.075), M.paper)
        q.position.set(if k % 2 == 1 then 0.022 else -0.022, -0.04 - k * 0.062, 0.0)
        q.rotation.y = if k % 2 == 1 then 0.6 else -0.6
        ThreeInterop.addChild(strip, q)
      }
      strip.position.set(px, py, z)
      ThreeInterop.addChild(parent, strip)
    }
  end shimenawa

  private def buildTorii(
    ctx: WorldCtx,
    x: Double,
    z: Double,
    y: Double,
    w: Double,
    h: Double,
    plain: Boolean
  ): Group[Object3DEventMap] =
    val g          = new Group()
    val pr         = 0.15 * (w / 3.4)
    val redParts   = mutable.ArrayBuffer.empty[Util.BakePart]
    val deepParts  = mutable.ArrayBuffer.empty[Util.BakePart]
    val stoneParts = mutable.ArrayBuffer.empty[Util.BakePart]

    Vector(-1.0, 1.0).foreach { s =>
      redParts += bakePart(new CylinderGeometry(pr * 0.9, pr, h, 10),
        Util.trs(s * w / 2.0, h / 2.0, 0.0, 0.0, 0.0, s * 0.018))
      stoneParts += bakePart(new CylinderGeometry(pr * 1.5, pr * 1.7, 0.24, 10),
        Util.trs(s * w / 2.0, 0.12, 0.0))
    }
    deepParts += bakePart(new BoxGeometry(w + 0.5, 0.15, pr * 1.5), Util.trs(0.0, h * 0.72, 0.0))
    deepParts += bakePart(new BoxGeometry(w + 1.0, 0.19, pr * 2.1), Util.trs(0.0, h - 0.16, 0.0))
    redParts += bakePart(new BoxGeometry(w * 0.6, 0.2, pr * 2.5), Util.trs(0.0, h + 0.04, 0.0))
    Vector(-1.0, 1.0).foreach { s =>
      val seg = (w + 1.15 - w * 0.6) / 2.0
      redParts += bakePart(new BoxGeometry(seg, 0.2, pr * 2.5),
        Util.trs(s * (w * 0.3 + seg / 2.0), h + 0.04 + seg * 0.09, 0.0, 0.0, 0.0, -s * 0.19))
    }
    redParts += bakePart(new BoxGeometry(0.28, h * 0.24, pr * 1.7), Util.trs(0.0, h * 0.85, 0.0))

    val red = if plain then M.toriiDeep else M.torii
    Vector((redParts, red), (deepParts, M.toriiDeep), (stoneParts, M.stoneDark)).foreach {
      case (parts, material) =>
        if parts.nonEmpty then
          val mesh = new Mesh(Util.bake(parts.toList), material)
          mesh.castShadow = true
          mesh.receiveShadow = true
          ThreeInterop.addChild(g, mesh)
          Outline.hullOutline(mesh, OutlineOptions(thickness = 0.0036))
    }

    if !plain then shimenawa(g, y = h * 0.62, w = w - 0.3, z = pr * 1.6, thick = 0.09, n = 4)

    g.position.set(x, y, z)
    ctx.add(g)
    Vector(-1.0, 1.0).foreach { s =>
      ctx.collide(x + s * w / 2.0 - pr * 1.5, z - pr * 1.5, x + s * w / 2.0 + pr * 1.5, z + pr * 1.5, y + h)
    }
    g
  end buildTorii

  private def buildHaiden(ctx: WorldCtx, x: Double, z: Double, y: Double): Group[Object3DEventMap] =
    val g = new Group()
    g.name = "haiden"
    val w             = 5.4
    val d             = 4.4
    val ph            = 0.66
    val wh            = 2.5
    val woodParts     = mutable.ArrayBuffer.empty[Util.BakePart]
    val woodDarkParts = mutable.ArrayBuffer.empty[Util.BakePart]
    val roofParts     = mutable.ArrayBuffer.empty[Util.BakePart]
    val stoneParts    = mutable.ArrayBuffer.empty[Util.BakePart]

    stoneParts += bakePart(new BoxGeometry(w + 0.9, ph, d + 0.9), Util.trs(0.0, ph / 2.0, 0.0))
    stoneParts += bakePart(new BoxGeometry(w + 1.1, 0.1, d + 1.1), Util.trs(0.0, ph + 0.05, 0.0))
    (0 until 3).foreach { i =>
      val h = ph * ((3 - i) / 3.0)
      stoneParts += bakePart(new BoxGeometry(2.6, h, 0.34),
        Util.trs(0.0, h / 2.0, (d + 0.9) / 2.0 + 0.17 + i * 0.34))
    }

    val yb = ph + 0.1
    Vector(-1.0, 1.0).foreach { sx =>
      Vector(-1.0, 1.0).foreach { sz =>
        woodDarkParts += bakePart(new CylinderGeometry(0.15, 0.16, wh, 8),
          Util.trs(sx * (w / 2.0 - 0.15), yb + wh / 2.0, sz * (d / 2.0 - 0.15)))
      }
      woodDarkParts += bakePart(new CylinderGeometry(0.13, 0.14, wh, 8),
        Util.trs(sx * 0.95, yb + wh / 2.0, d / 2.0 - 0.15))
    }
    woodParts += bakePart(new BoxGeometry(w - 0.2, wh, 0.16), Util.trs(0.0, yb + wh / 2.0, -(d / 2.0 - 0.1)))
    Vector(-1.0, 1.0).foreach { sx =>
      woodParts += bakePart(new BoxGeometry(0.16, wh, d - 0.2),
        Util.trs(sx * (w / 2.0 - 0.1), yb + wh / 2.0, 0.0))
    }
    woodDarkParts += bakePart(new BoxGeometry(w, 0.22, 0.2), Util.trs(0.0, yb + wh - 0.11, d / 2.0 - 0.14))
    woodDarkParts += bakePart(new BoxGeometry(w, 0.16, 0.24), Util.trs(0.0, yb + 0.08, d / 2.0 - 0.14))
    Vector(-1.0, 1.0).foreach { sx =>
      woodParts += bakePart(new BoxGeometry(1.3, wh - 0.4, 0.1), Util.trs(sx * 1.85, yb + wh / 2.0, d / 2.0 - 0.14))
      (0 until 7).foreach { i =>
        woodDarkParts += bakePart(new BoxGeometry(0.05, wh - 0.5, 0.05),
          Util.trs(sx * 1.85 - 0.55 + i * 0.18, yb + wh / 2.0, d / 2.0 - 0.07))
      }
    }
    {
      val back = Util.box(1.9, wh - 0.4, 0.08, M.recess, 0.0, yb + wh / 2.0 - 0.1, d / 2.0 - 0.7)
      ThreeInterop.addChild(g, back)
      val mirror = new Mesh(new CylinderGeometry(0.24, 0.24, 0.05, 16), M.mirror)
      mirror.rotation.x = math.Pi / 2.0
      mirror.position.set(0.0, yb + 1.35, d / 2.0 - 0.62)
      ThreeInterop.addChild(g, mirror)
      ThreeInterop.addChild(g, Util.box(0.1, 0.5, 0.1, M.woodDark, 0.0, yb + 0.95, d / 2.0 - 0.62))
    }

    {
      val eave  = 1.15
      val rw    = w + eave * 2.0
      val rd    = d + eave * 2.0
      val rh    = 1.55
      val slope = math.atan2(rh, rw / 2.0)
      val slab  = math.hypot(rw / 2.0, rh) + 0.1
      val yr    = yb + wh
      woodDarkParts += bakePart(new BoxGeometry(w + 0.7, 0.26, d + 0.7), Util.trs(0.0, yr + 0.13, 0.0))
      val rafterParts = (0 until 13).map { i =>
        Util.BakePart(new BoxGeometry(0.08, 0.14, rd - 0.4),
          Some(Util.trs(-rw / 2.0 + 0.4 + (i * (rw - 0.8)) / 12.0, yr + 0.34, 0.0)))
      }
      ThreeInterop.addChild(g, new Mesh(Util.bake(rafterParts.toList), M.woodDark))

      Vector(-1.0, 1.0).foreach { s =>
        roofParts += bakePart(new BoxGeometry(slab, 0.3, rd),
          Util.trs(s * (rw / 4.0), yr + 0.4 + rh / 2.0, 0.0, 0.0, 0.0, -s * slope))
        roofParts += bakePart(new BoxGeometry(0.8, 0.26, rd),
          Util.trs(s * (rw / 2.0 + 0.24), yr + 0.4 + 0.06, 0.0, 0.0, 0.0, -s * (slope - 0.34)))
      }
      roofParts += bakePart(new BoxGeometry(0.5, 0.34, rd + 0.2), Util.trs(0.0, yr + 0.4 + rh + 0.1, 0.0))
      (0 until 4).foreach { i =>
        woodDarkParts += bakePart(new CylinderGeometry(0.11, 0.11, 0.62, 8),
          Util.trs(0.0, yr + 0.4 + rh + 0.34, -rd / 2.0 + 1.2 + i * ((rd - 2.4) / 3.0), 0.0, 0.0, math.Pi / 2.0))
      }
      Vector(-1.0, 1.0).foreach { s =>
        Vector(-1.0, 1.0).foreach { sx =>
          val finial = Util.box(0.11, 1.5, 0.11, M.woodDark, sx * 0.34, yr + 0.4 + rh + 0.5, s * (rd / 2.0 - 0.3))
          finial.rotation.z = sx * 0.28
          finial.castShadow = true
          ThreeInterop.addChild(g, finial)
        }
      }
      val tri = new Shape()
      tri.moveTo(-rw / 2.0 + 0.3, 0.0)
      tri.lineTo(rw / 2.0 - 0.3, 0.0)
      tri.lineTo(0.0, rh * 0.88)
      tri.closePath()
      val triGeo = new ExtrudeGeometry(tri,
        ExtrudeGeometryOptions(depth = Some(0.14), bevelEnabled = Some(false)))
      triGeo.translate(0.0, 0.0, -0.07)
      Vector(-1.0, 1.0).foreach { s =>
        woodParts += bakePart(triGeo, Util.trs(0.0, yr + 0.4, s * (rd / 2.0 - 0.07)))
      }
      triGeo.dispose()
    }

    shimenawa(g, y = yb + wh - 0.34, w = w - 1.0, z = d / 2.0 + 0.06, thick = 0.11, n = 5)
    {
      val bell = new Mesh(new SphereGeometry(0.19, 12, 9), M.bronze)
      bell.position.set(0.0, yb + wh - 0.78, d / 2.0 + 0.04)
      bell.castShadow = true
      ThreeInterop.addChild(g, bell)
      ThreeInterop.addChild(g, Util.cyl(0.03, 0.03, 0.4, 5, M.metalDark, 0.0, yb + wh - 0.52, d / 2.0 + 0.04))
      val cord = new Mesh(new CylinderGeometry(0.05, 0.06, 1.5, 6), M.cord)
      cord.position.set(0.0, yb + wh - 1.7, d / 2.0 + 0.04)
      cord.castShadow = true
      ThreeInterop.addChild(g, cord)
      (0 until 3).foreach { i =>
        ThreeInterop.addChild(g, Util.cyl(0.07, 0.07, 0.05, 8, M.ring, 0.0, yb + wh - 1.1 - i * 0.5, d / 2.0 + 0.04))
      }
    }
    {
      val bx   = 0.0
      val bz   = d / 2.0 + 1.15
      val bw   = 1.7
      val bd   = 0.66
      val bh   = 0.66
      val body = Util.box(bw, bh, bd, M.woodDark, bx, 0.33, bz)
      body.castShadow = true
      body.receiveShadow = true
      ThreeInterop.addChild(g, body)
      Outline.hullOutline(body, OutlineOptions(thickness = 0.0034))
      ThreeInterop.addChild(g, Util.box(bw + 0.12, 0.07, bd + 0.12, M.wood, bx, bh + 0.03, bz))
      ThreeInterop.addChild(g, Util.box(bw - 0.2, 0.06, bd - 0.2, M.offertory, bx, bh + 0.02, bz))
      val slats = mutable.ArrayBuffer.empty[Util.BakePart]
      val ns    = math.round((bw - 0.2) / 0.12).toInt
      (0 until ns).foreach { i =>
        slats += bakePart(new BoxGeometry(0.05, 0.1, bd - 0.16),
          Util.trs(bx - (bw - 0.25) / 2.0 + i * ((bw - 0.25) / (ns - 1)), bh + 0.04, bz))
      }
      val sm = new Mesh(Util.bake(slats.toList), M.wood)
      sm.castShadow = true
      ThreeInterop.addChild(g, sm)
      ctx.collide(x - bw / 2.0, z - bz - bd / 2.0, x + bw / 2.0, z - bz + bd / 2.0, y + bh)
    }
    {
      val board = new Mesh(
        new BoxGeometry(0.7, 1.9, 0.09),
        Props.matArray(M.wood, M.wood, M.wood, M.wood, M.shrineName, M.wood)
      )
      board.position.set(-2.05, yb + wh + 0.5, d / 2.0 + 0.3)
      board.castShadow = true
      ThreeInterop.addChild(g, board)
      Outline.hullOutline(board, OutlineOptions(thickness = 0.003))
    }

    val matFor: Vector[(mutable.ArrayBuffer[Util.BakePart], Material[?])] = Vector(
      (woodParts, M.wood), (woodDarkParts, M.woodDark), (roofParts, M.roof), (stoneParts, M.stone)
    )
    matFor.foreach { (parts, material) =>
      if parts.nonEmpty then
        val mesh = new Mesh(Util.bake(parts.toList), material)
        mesh.castShadow = true
        mesh.receiveShadow = true
        ThreeInterop.addChild(g, mesh)
        if material eq M.roof then Outline.hullOutline(mesh, OutlineOptions(thickness = 0.0034))
        else if material eq M.wood then Outline.hullOutline(mesh, OutlineOptions(thickness = 0.0034))
    }

    g.position.set(x, y, z)
    g.rotation.y = math.Pi
    ctx.add(g)
    ctx.collide(x - (w + 0.9) / 2.0, z - (d + 0.9) / 2.0, x + (w + 0.9) / 2.0, z + (d + 0.9) / 2.0, y + ph + wh)
    g
  end buildHaiden

  private def buildTemizuya(ctx: WorldCtx, x: Double, z: Double, y: Double): Group[Object3DEventMap] =
    val g = new Group()
    g.name = "temizuya"
    val w          = 2.7
    val d          = 2.2
    val ph         = 2.35
    val woodParts  = mutable.ArrayBuffer.empty[Util.BakePart]
    val roofParts  = mutable.ArrayBuffer.empty[Util.BakePart]
    val stoneParts = mutable.ArrayBuffer.empty[Util.BakePart]

    stoneParts += bakePart(new BoxGeometry(w + 0.6, 0.16, d + 0.6), Util.trs(0.0, 0.08, 0.0))
    Vector(-1.0, 1.0).foreach { sx =>
      Vector(-1.0, 1.0).foreach { sz =>
        woodParts += bakePart(new CylinderGeometry(0.11, 0.12, ph, 8),
          Util.trs(sx * (w / 2.0), 0.16 + ph / 2.0, sz * (d / 2.0)))
        stoneParts += bakePart(new CylinderGeometry(0.17, 0.19, 0.18, 8),
          Util.trs(sx * (w / 2.0), 0.16 + 0.09, sz * (d / 2.0)))
      }
    }
    Vector(-1.0, 1.0).foreach { sx =>
      woodParts += bakePart(new BoxGeometry(0.13, 0.16, d + 0.2), Util.trs(sx * (w / 2.0), 0.16 + ph - 0.08, 0.0))
    }
    woodParts += bakePart(new BoxGeometry(w + 0.3, 0.15, 0.13), Util.trs(0.0, 0.16 + ph - 0.08, 0.0))

    {
      val yr = 0.16 + ph
      woodParts += bakePart(new BoxGeometry(w + 0.9, 0.2, d + 0.9), Util.trs(0.0, yr + 0.1, 0.0))
      val pyramid = new ConeGeometry(math.sqrt(0.5), 1.0, 4, 1)
      pyramid.rotateY(math.Pi / 4.0)
      roofParts += bakePart(pyramid,
        Util.trs(0.0, yr + 0.2 + 0.55, 0.0, 0.0, 0.0, 0.0, w + 1.3, 1.1, d + 1.3))
      roofParts += bakePart(new BoxGeometry(w + 1.3, 0.18, d + 1.3), Util.trs(0.0, yr + 0.24, 0.0))
      pyramid.dispose()
      woodParts += bakePart(new CylinderGeometry(0.09, 0.09, 0.34, 8), Util.trs(0.0, yr + 1.42, 0.0))
    }

    {
      val tw = 1.6
      val td = 0.9
      val th = 0.62
      stoneParts += bakePart(new BoxGeometry(tw, th, td), Util.trs(0.0, 0.16 + th / 2.0, 0.0))
      stoneParts += bakePart(new BoxGeometry(tw + 0.14, 0.09, td + 0.14), Util.trs(0.0, 0.16 + th, 0.0))
      ThreeInterop.addChild(g, Util.box(tw - 0.26, 0.1, td - 0.26, M.troughInner, 0.0, 0.16 + th - 0.06, 0.0))
      val water = new Mesh(new PlaneGeometry(tw - 0.28, td - 0.28), M.water)
      water.rotation.x = -math.Pi / 2.0
      water.position.set(0.0, 0.16 + th - 0.02, 0.0)
      ThreeInterop.setUserDataFlag(water, "noOutline")
      ThreeInterop.addChild(g, water)
      val highlight = new Mesh(new PlaneGeometry((tw - 0.28) * 0.4, (td - 0.28) * 0.3),
        Toon.flat(FlatOptions(color = 0xffffff, transparent = true, opacity = 0.4,
          depthWrite = Some(false), cache = false)))
      highlight.rotation.x = -math.Pi / 2.0
      highlight.rotation.z = 0.4
      highlight.position.set(-0.24, 0.16 + th + 0.001, -0.1)
      ThreeInterop.setUserDataFlag(highlight, "noOutline")
      ThreeInterop.addChild(g, highlight)

      val spout = Util.cyl(0.045, 0.045, 0.7, 7, M.bambooMat, 0.0, 0.16 + th + 0.42, -0.34)
      spout.rotation.x = 1.3
      ThreeInterop.addChild(g, spout)
      ThreeInterop.addChild(g, Util.cyl(0.05, 0.05, 0.5, 7, M.bambooDeep, 0.0, 0.16 + th + 0.3, -0.5))

      Vector(-1.0, 1.0).foreach { s =>
        ThreeInterop.addChild(g,
          Util.cyl(0.026, 0.028, 0.30, 6, M.bambooMat,
            s * (tw / 2.0 - 0.14), 0.16 + th + 0.19, 0.06))
      }
      val rail = Util.cyl(0.028, 0.028, tw - 0.1, 6, M.bambooMat, 0.0, 0.16 + th + 0.3, 0.06)
      rail.rotateZ(math.Pi / 2.0)
      ThreeInterop.addChild(g, rail)
      (0 until 3).foreach { i =>
        val lx   = -0.48 + i * 0.48
        val bowl = new Mesh(new SphereGeometry(0.085, 10, 6, 0.0, math.Pi * 2.0, 0.0, math.Pi / 2.0), M.ladle)
        bowl.position.set(lx, 0.16 + th + 0.28, -0.14)
        ThreeInterop.addChild(g, bowl)
        val handle = Util.cyl(0.017, 0.017, 0.4, 5, M.bambooMat, lx, 0.16 + th + 0.3, 0.1)
        handle.rotation.x = math.Pi / 2.0
        ThreeInterop.addChild(g, handle)
      }
    }

    val matFor: Vector[(mutable.ArrayBuffer[Util.BakePart], Material[?])] =
      Vector((woodParts, M.wood), (roofParts, M.roof), (stoneParts, M.stone))
    matFor.foreach { (parts, material) =>
      if parts.nonEmpty then
        val mesh = new Mesh(Util.bake(parts.toList), material)
        mesh.castShadow = true
        mesh.receiveShadow = true
        ThreeInterop.addChild(g, mesh)
        if material eq M.roof then Outline.hullOutline(mesh, OutlineOptions(thickness = 0.0034))
    }

    g.position.set(x, y, z)
    ctx.add(g)
    ctx.collide(x - w / 2.0 - 0.2, z - d / 2.0 - 0.2, x + w / 2.0 + 0.2, z + d / 2.0 + 0.2, y + 0.8)
    g
  end buildTemizuya

  private def stoneLantern(ctx: WorldCtx, x: Double, z: Double, y: Double, scale: Double): Unit =
    val g     = new Group()
    val s     = scale
    val parts = mutable.ArrayBuffer.empty[Util.BakePart]
    parts += bakePart(new CylinderGeometry(0.34 * s, 0.4 * s, 0.2 * s, 8), Util.trs(0.0, 0.1 * s, 0.0))
    parts += bakePart(new CylinderGeometry(0.15 * s, 0.19 * s, 0.9 * s, 8), Util.trs(0.0, 0.65 * s, 0.0))
    parts += bakePart(new CylinderGeometry(0.3 * s, 0.22 * s, 0.14 * s, 8), Util.trs(0.0, 1.17 * s, 0.0))
    (0 until 4).foreach { i =>
      val a = (i / 4.0) * math.Pi * 2.0 + math.Pi / 4.0
      parts += bakePart(new BoxGeometry(0.1 * s, 0.42 * s, 0.1 * s),
        Util.trs(math.cos(a) * 0.2 * s, 1.45 * s, math.sin(a) * 0.2 * s, 0.0, -a, 0.0))
    }
    parts += bakePart(new CylinderGeometry(0.3 * s, 0.3 * s, 0.07 * s, 8), Util.trs(0.0, 1.69 * s, 0.0))
    parts += bakePart(new ConeGeometry(0.42 * s, 0.34 * s, 8), Util.trs(0.0, 1.89 * s, 0.0))
    parts += bakePart(new SphereGeometry(0.09 * s, 8, 6), Util.trs(0.0, 2.1 * s, 0.0))
    val mesh = new Mesh(Util.bake(parts.toList), M.stone)
    mesh.castShadow = true
    mesh.receiveShadow = true
    ThreeInterop.addChild(g, mesh)
    Outline.hullOutline(mesh, OutlineOptions(thickness = 0.0034))
    val core = new Mesh(new CylinderGeometry(0.16 * s, 0.16 * s, 0.4 * s, 8), M.lantern)
    core.position.y = 1.45 * s
    ThreeInterop.addChild(g, core)
    g.position.set(x, y, z)
    ctx.add(g)
    ctx.collide(x - 0.38 * s, z - 0.38 * s, x + 0.38 * s, z + 0.38 * s, y + 2.1 * s)
  end stoneLantern

  private def foxStatue(ctx: WorldCtx, x: Double, z: Double, y: Double, ry: Double): Unit =
    val g = new Group()
    ThreeInterop.addChild(g, Util.box(0.56, 0.62, 0.46, M.stoneDark, 0.0, 0.31, 0.0))
    ThreeInterop.addChild(g, Util.box(0.64, 0.07, 0.54, M.stone, 0.0, 0.65, 0.0))
    val body = new Mesh(new CylinderGeometry(0.11, 0.2, 0.5, 8), M.stone)
    body.position.set(0.0, 0.93, 0.0)
    body.castShadow = true
    ThreeInterop.addChild(g, body)
    val chest = new Mesh(new SphereGeometry(0.14, 10, 7), M.stone)
    chest.scale.set(1.0, 0.9, 1.1)
    chest.position.set(0.0, 0.86, 0.11)
    ThreeInterop.addChild(g, chest)
    val head = new Mesh(new SphereGeometry(0.12, 10, 8), M.stone)
    head.scale.set(1.0, 0.95, 1.05)
    head.position.set(0.0, 1.26, 0.03)
    head.castShadow = true
    ThreeInterop.addChild(g, head)
    val muzzle = new Mesh(new ConeGeometry(0.07, 0.2, 6), M.stone)
    muzzle.rotation.x = math.Pi / 2.0 + 0.12
    muzzle.position.set(0.0, 1.22, 0.17)
    ThreeInterop.addChild(g, muzzle)
    Vector(-1.0, 1.0).foreach { s =>
      val ear = new Mesh(new ConeGeometry(0.055, 0.16, 4), M.stone)
      ear.position.set(s * 0.07, 1.4, -0.01)
      ear.rotation.z = s * 0.16
      ThreeInterop.addChild(g, ear)
      val eye = new Mesh(new SphereGeometry(0.017, 6, 5), M.stoneDark)
      eye.position.set(s * 0.05, 1.28, 0.11)
      ThreeInterop.addChild(g, eye)
      ThreeInterop.addChild(g, Util.cyl(0.035, 0.042, 0.34, 6, M.stone, s * 0.075, 0.86, 0.15))
    }
    val tail = new Mesh(new TorusGeometry(0.16, 0.055, 5, 12, math.Pi * 1.1), M.stone)
    tail.rotation.set(math.Pi / 2.0, 0.0, -0.5)
    tail.position.set(0.0, 1.06, -0.2)
    tail.castShadow = true
    ThreeInterop.addChild(g, tail)
    val bib = new Mesh(new PlaneGeometry(0.22, 0.2), M.bib)
    bib.position.set(0.0, 1.06, 0.15)
    bib.rotation.x = 0.2
    ThreeInterop.addChild(g, bib)
    ThreeInterop.addChild(g, Util.box(0.2, 0.03, 0.03, M.bib, 0.0, 1.16, 0.1))
    g.position.set(x, y, z)
    g.rotation.y = ry
    ctx.add(g)
    ctx.collide(x - 0.34, z - 0.3, x + 0.34, z + 0.3, y + 1.4)
  end foxStatue

  private def emaRack(ctx: WorldCtx, x: Double, z: Double, y: Double, ry: Double, len: Double, seed: Int): Unit =
    val rng   = Util.rngKit(seed)
    val g     = new Group()
    val h     = 1.85
    val frame = mutable.ArrayBuffer.empty[Util.BakePart]
    Vector(-1.0, 1.0).foreach { s =>
      frame += bakePart(new BoxGeometry(0.13, h, 0.13), Util.trs((s * len) / 2.0, h / 2.0, 0.0))
      frame += bakePart(new BoxGeometry(0.2, 0.16, 0.5), Util.trs((s * len) / 2.0, 0.08, 0.0))
    }
    Vector(h - 0.08, h * 0.55).foreach { railY =>
      frame += bakePart(new BoxGeometry(len + 0.3, 0.1, 0.12), Util.trs(0.0, railY, 0.0))
    }
    frame += bakePart(new BoxGeometry(len + 0.5, 0.08, 0.6), Util.trs(0.0, h + 0.12, 0.06))
    val fm = new Mesh(Util.bake(frame.toList), M.woodDark)
    fm.castShadow = true
    fm.receiveShadow = true
    ThreeInterop.addChild(g, fm)
    Outline.hullOutline(fm, OutlineOptions(thickness = 0.0032))

    val plaqueGeo = Util.bake(List(
      Util.BakePart(new BoxGeometry(0.2, 0.15, 0.02)),
      Util.BakePart(new ConeGeometry(0.115, 0.07, 3), Some(Util.trs(0.0, 0.105, 0.0, 0.0, math.Pi / 6.0, 0.0)))
    ))
    val d = new Object3D()
    (0 until 5).foreach { v =>
      val matrices = mutable.ArrayBuffer.empty[Matrix4]
      val n        = math.floor((len - 0.4) / 0.24).toInt
      (0 until 2).foreach { row =>
        (0 until n).foreach { i =>
          val skip = i % 5 != v || rng.chance(0.12)
          if !skip then
            d.position.set(
              -(len - 0.4) / 2.0 + i * 0.24 + rng.range(-0.015, 0.015),
              (if row == 1 then h - 0.24 else h * 0.55 - 0.24) + rng.range(-0.01, 0.01),
              0.06
            )
            d.rotation.set(0.0, rng.range(-0.14, 0.14), rng.range(-0.06, 0.06))
            d.updateMatrix()
            matrices += d.matrix.clone()
        }
      }
      if matrices.nonEmpty then
        val inst = new InstancedMesh(plaqueGeo,
          Toon.cel(CelOptions(color = 0xffffff, bands = 2, map = Some(Textures.emaTex(v)),
            tint = 0x8a7f9c, cache = false)),
          matrices.length.toDouble)
        matrices.zipWithIndex.foreach { (matrix, k) => inst.setMatrixAt(k.toDouble, matrix) }
        inst.castShadow = true
        ThreeInterop.addChild(g, inst)
    }

    g.position.set(x, y, z)
    g.rotation.y = ry
    ctx.add(g)
    val swap = math.abs(math.abs(ry) - math.Pi / 2.0) < 0.5
    ctx.collide(
      x - (if swap then 0.3 else len / 2.0), z - (if swap then len / 2.0 else 0.3),
      x + (if swap then 0.3 else len / 2.0), z + (if swap then len / 2.0 else 0.3), y + h
    )
  end emaRack

  private def omikujiStand(ctx: WorldCtx, x: Double, z: Double, y: Double, ry: Double): Unit =
    val rng = Util.rngKit(15)
    val g   = new Group()
    val body = new Mesh(
      new BoxGeometry(0.56, 0.72, 0.42),
      Props.matArray(M.wood, M.wood, M.wood, M.wood,
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.omikujiTex()), cache = false)), M.wood)
    )
    body.position.set(0.0, 1.0, 0.0)
    body.castShadow = true
    body.receiveShadow = true
    ThreeInterop.addChild(g, body)
    Outline.hullOutline(body, OutlineOptions(thickness = 0.0032))
    Vector(-1.0, 1.0).foreach { sx =>
      Vector(-1.0, 1.0).foreach { sz =>
        ThreeInterop.addChild(g, Util.box(0.07, 0.64, 0.07, M.woodDark, sx * 0.24, 0.32, sz * 0.16))
      }
    }
    ThreeInterop.addChild(g, Util.box(0.62, 0.06, 0.48, M.woodDark, 0.0, 1.39, 0.0))
    val draw = new Mesh(new CylinderGeometry(0.11, 0.13, 0.3, 6),
      Toon.cel(CelOptions(color = 0xb5322f, bands = 3, tint = 0x7a4060)))
    draw.position.set(0.0, 1.57, 0.0)
    draw.castShadow = true
    ThreeInterop.addChild(g, draw)

    val rack = new Group()
    Vector(-1.0, 1.0).foreach { s =>
      ThreeInterop.addChild(rack, Util.cyl(0.03, 0.03, 1.4, 6, M.metalDark, s * 0.9, 0.7, 0.0))
    }
    val rail1 = Util.cyl(0.02, 0.02, 1.85, 5, M.metal, 0.0, 1.34, 0.0)
    rail1.rotateZ(math.Pi / 2.0)
    ThreeInterop.addChild(rack, rail1)
    val rail2 = Util.cyl(0.02, 0.02, 1.85, 5, M.metal, 0.0, 0.98, 0.0)
    rail2.rotateZ(math.Pi / 2.0)
    ThreeInterop.addChild(rack, rail2)
    val slipGeo = new PlaneGeometry(0.035, 0.2)
    val d       = new Object3D()
    val matrices = (0 until 34).map { i =>
      d.position.set(-0.84 + (i % 17) * 0.1, if i < 17 then 1.24 else 0.88, rng.range(-0.01, 0.01))
      d.rotation.set(0.0, rng.range(-0.3, 0.3), rng.range(-0.12, 0.12))
      d.updateMatrix()
      d.matrix.clone()
    }
    val inst = new InstancedMesh(slipGeo,
      Toon.flat(FlatOptions(color = 0xfbf8f0, side = 2, cache = false)), matrices.length.toDouble)
    matrices.zipWithIndex.foreach { (matrix, k) => inst.setMatrixAt(k.toDouble, matrix) }
    ThreeInterop.setUserDataFlag(inst, "noOutline")
    ThreeInterop.addChild(rack, inst)
    rack.position.set(0.0, 0.0, 0.85)
    ThreeInterop.addChild(g, rack)

    g.position.set(x, y, z)
    g.rotation.y = ry
    ctx.add(g)
    ctx.collide(x - 0.4, z - 0.4, x + 0.4, z + 0.4, y + 1.6)
  end omikujiStand

  /** Port of upstream `props.js` `makeSignPost`: a post, a concrete collar, and the plates bolted
    * to it. Lane 2's `Props.makeSignPost` carries no plates, and the shrine's 参拝 notice needs one.
    */
  private def signPost(
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
  end signPost

  /** A plate on a sign post: the same map both ways unless `double` asks for a printed back. */
  final private case class SignPlate(
    map: Texture[?, ?],
    w: Double = 0.5,
    h: Double = 0.5,
    y: Option[Double] = None,
    ry: Double = 0.0,
    double: Boolean = false,
    back: Option[Int] = None
  )

  def buildShrine(ctx: WorldCtx): Planting =
    val gm     = Ground.groundMats()
    val rng    = Util.rngKit(2903)
    val sakura = mutable.ArrayBuffer.empty[SakuraSpot]
    val shrubs = mutable.ArrayBuffer.empty[ShrubSpot]
    val grove  = mutable.ArrayBuffer.empty[GroveSpot]
    val bamboo = mutable.ArrayBuffer.empty[BambooClump]
    val petals = mutable.ArrayBuffer.empty[PetalPatch]

    Ground.lane(ctx,
      Ground.LaneOptions(axis = "z", at = LaneX, from = 4.0, to = 19.6, w = 2.9,
        mat = Some(gm.stone), kerb = false, rise = 0.04, platform = false, name = "shrineLane"))
    Ground.pad(ctx,
      Ground.PadOptions(x = AxisX, z = 18.0, w = 6.4, d = 4.6, y = Street.groundY(18.0),
        mat = Some(gm.stone), name = "shrineForecourt"))
    Ground.wallRun(ctx,
      Ground.WallOptions(axis = "z", at = LaneX + 1.75, from = 6.0, to = 17.0,
        y = 0.0, h = 1.15, t = 0.22, panel = 3.4))
    Ground.wallRun(ctx,
      Ground.WallOptions(axis = "z", at = LaneX - 1.75, from = 6.0, to = 14.0,
        y = 0.0, h = 1.05, t = 0.22, panel = 3.4))

    {
      val post = Util.box(0.24, 1.5, 0.24, M.stone, 0.0, 0.75, 0.0)
      post.castShadow = true
      post.receiveShadow = true
      post.position.set(LaneX - 1.85, 0.75, 5.4)
      ctx.add(post)
      ctx.add(Util.box(0.3, 0.07, 0.3, M.stoneWarm, LaneX - 1.85, 1.53, 5.4))
      Outline.hullOutline(post, OutlineOptions(thickness = 0.0032))
      ctx.collide(LaneX - 2.0, 5.2, LaneX - 1.7, 5.6, 1.5)
      Vector((math.Pi / 2.0, 0.13, 0.0), (math.Pi, 0.0, -0.13)).foreach { case (plateRy, dx, dz) =>
        val plate = new Mesh(new PlaneGeometry(0.46, 0.115), M.plate)
        plate.position.set(LaneX - 1.85 + dx, 1.16, 5.4 + dz)
        plate.rotation.y = plateRy
        ThreeInterop.setUserDataFlag(plate, "noOutline")
        ctx.add(plate)
      }
    }

    buildTorii(ctx, x = AxisX, z = 19.9, y = Street.groundY(19.9), w = 3.5, h = 3.4, plain = false)
    buildTorii(ctx, x = AxisX, z = TerZ0 + 1.5, y = Top, w = 2.7, h = 2.7, plain = true)

    {
      val st = Ground.steps(ctx,
        Ground.StepsOptions(x = AxisX, z = StepZ0, axis = "z", dir = 1,
          n = NSteps, rise = Rise, run = Run, w = 3.4, y = 0.0,
          mat = Some(gm.stone), lipMat = Some(gm.stoneDark)))
      Vector(-1.0, 1.0).foreach { s =>
        val cheekParts = (0 until NSteps).map { i =>
          val h = Rise * (i + 1) + 0.22
          Util.BakePart(new BoxGeometry(0.3, h, Run),
            Some(Util.trs(AxisX + s * 1.85, h / 2.0, StepZ0 + Run * (i + 0.5))))
        }
        val cheek = new Mesh(Util.bake(cheekParts.toList), gm.stoneDark)
        cheek.castShadow = true
        cheek.receiveShadow = true
        ctx.add(cheek)
        ctx.collide(AxisX + s * 1.85 - 0.2, StepZ0, AxisX + s * 1.85 + 0.2, StepZ0 + Run * NSteps, Top + 0.3)
      }
      val l = Run * NSteps
      val rail = Util.cyl(0.032, 0.032, math.hypot(l, Top) + 0.3, 7, M.metalDark,
        AxisX - 1.85, Top / 2.0 + 0.95, StepZ0 + l / 2.0)
      rail.rotation.set(math.Pi / 2.0 - math.atan2(Top, l), 0.0, 0.0)
      rail.castShadow = true
      ctx.add(rail)
      (0 to 3).foreach { i =>
        val t = i / 3.0
        ctx.add(Util.cyl(0.03, 0.03, 0.95, 6, M.metalDark, AxisX - 1.85, Rise * NSteps * t + 0.47, StepZ0 + l * t))
      }
      st.group.name = "shrineSteps"
    }

    Ground.pad(ctx,
      Ground.PadOptions(
        x = (TerX0 + TerX1) / 2.0, z = (TerZ0 + TerZ1) / 2.0,
        w = TerX1 - TerX0, d = TerZ1 - TerZ0, y = Top - 0.07, h = 0.07,
        mat = Some(gm.stoneWarm), name = "shrineTerrace"
      ))
    {
      val h     = 2.9
      val wallY = Top + 0.5 - h
      val runs = Vector(
        ("x", TerZ0, TerX0, AxisX - 2.1), ("x", TerZ0, AxisX + 2.1, TerX1), ("z", TerX0, TerZ0, TerZ1),
        ("z", TerX1, TerZ0, TerZ1), ("x", TerZ1, TerX0, -23.8), ("x", TerZ1, -21.2, TerX1)
      )
      runs.foreach { case (axis, at, from, to) =>
        Ground.wallRun(ctx,
          Ground.WallOptions(axis = axis, at = at, from = from, to = to,
            y = wallY, h = h, t = 0.34, panel = 3.6, mat = Some(gm.stoneDark), capMat = Some(gm.stone)))
      }
    }

    buildHaiden(ctx, x = AxisX, z = 36.2, y = Top)
    buildTemizuya(ctx, x = -33.2, z = 29.4, y = Top)

    Vector((19.4, 1.0), (TerZ0 + 2.6, 0.95), (33.0, 1.05)).foreach { case (zc, sc) =>
      Vector(-1.0, 1.0).foreach { s =>
        val yy = if zc < StepZ0 then Street.groundY(zc) else Top
        stoneLantern(ctx, x = AxisX + s * 2.55, z = zc, y = yy, scale = sc)
      }
    }
    Vector(-1.0, 1.0).foreach { s =>
      foxStatue(ctx, x = AxisX + s * 2.9, z = 33.6, y = Top, ry = if s > 0 then -0.4 else 0.4)
    }

    emaRack(ctx, x = -22.6, z = 31.6, y = Top, ry = -math.Pi / 2.0, len = 3.2, seed = 7)
    omikujiStand(ctx, x = -23.2, z = 28.0, y = Top, ry = -math.Pi / 2.0 + 0.2)

    ctx.add(signPost(x = AxisX + 2.9, z = 19.0, y = Street.groundY(19.0), ry = 0.5, h = 1.9,
      postMat = M.woodDark, plates = Vector(SignPlate(map = Textures.sanpaiNotice(), w = 0.5, h = 0.66, y = Some(1.4)))))
    ctx.add(Props.makeBroom(Props.BroomOpts(x = -30.4, z = 34.6, y = Top, ry = 0.6, tilt = 0.2, roll = 0.14)))
    ctx.add(Props.makeBucket(Props.BucketOpts(x = -30.9, z = 35.1, y = Top, water = true)))
    ctx.add(Props.makeBucket(Props.BucketOpts(x = -31.4, z = 34.5, y = Top, ry = 0.7, color = 0xa8b0bc)))
    ctx.add(Props.makeBench(Props.BenchOpts(x = -34.4, z = 34.6, y = Top, ry = math.Pi / 2.0, len = 1.6, wood = 0xb09272)))
    ctx.add(Props.makePetBowl(Props.PetBowlOpts(x = -34.6, z = 32.4, y = Top, ry = 0.4)))

    val GoshinX = -20.8
    val GoshinZ = 43.2
    grove += GroveSpot(x = GoshinX, z = GoshinZ, y = Street.groundY(GoshinZ), scale = 2.5, seed = 801,
      spread = 1.35, lean = 0.05, leanDir = Some(2.0))
    grove += GroveSpot(x = -26.6, z = 45.8, y = Street.groundY(45.8), scale = 1.9, seed = 802, spread = 1.2)
    grove += GroveSpot(x = -31.4, z = 42.6, y = Street.groundY(42.6), scale = 1.6, seed = 803, spread = 1.15)
    grove += GroveSpot(x = -36.6, z = 42.0, y = Street.groundY(42.0), scale = 1.45, seed = 804, spread = 1.1)
    grove += GroveSpot(x = -39.6, z = 46.6, y = Street.groundY(46.6), scale = 1.8, seed = 806, spread = 1.2)
    grove += GroveSpot(x = -17.2, z = 34.8, y = Street.groundY(34.8), scale = 1.7, seed = 807, spread = 1.15)
    grove += GroveSpot(x = -16.8, z = 27.6, y = Street.groundY(27.6), scale = 1.5, seed = 808, spread = 1.1)
    grove += GroveSpot(x = -23.6, z = 21.2, y = Street.groundY(21.2), scale = 1.6, seed = 809, spread = 1.15)
    grove += GroveSpot(x = -32.6, z = 16.8, y = Street.groundY(16.8), scale = 1.6, seed = 810, spread = 1.15)
    grove += GroveSpot(x = -40.8, z = 22.4, y = Street.groundY(22.4), scale = 1.8, seed = 811, spread = 1.2)

    bamboo += BambooClump(x = -35.2, z = 32.6, y = Top, n = 13, spread = 1.5, scale = 1.15, seed = 821)
    bamboo += BambooClump(x = -21.0, z = 35.4, y = Top, n = 11, spread = 1.3, scale = 1.05, seed = 822)
    bamboo += BambooClump(x = -24.8, z = 24.4, y = Street.groundY(24.4), n = 9, spread = 1.2, scale = 1.0, seed = 823)
    bamboo += BambooClump(x = -31.6, z = 21.0, y = Street.groundY(21.0), n = 10, spread = 1.4, scale = 1.1, seed = 824)
    bamboo += BambooClump(x = -34.4, z = 27.4, y = Top, n = 8, spread = 1.1, scale = 1.0, seed = 825)

    sakura += SakuraSpot(x = -19.2, z = 30.0, y = Street.groundY(30.0), scale = 1.5, seed = 815,
      lean = 0.16, leanDir = Some(4.7))
    sakura += SakuraSpot(x = -37.6, z = 19.6, y = Street.groundY(19.6), scale = 1.25, seed = 816,
      lean = 0.12, leanDir = Some(1.8))

    (0 until 6).foreach { i =>
      shrubs += ShrubSpot(x = TerX0 + 1.4 + (i % 2) * 13.2, z = 27.4 + math.floor(i / 2.0) * 4.4,
        y = Top, r = 0.55, count = 4, spread = 1.5, seed = 840 + i)
    }
    shrubs += ShrubSpot(x = LaneX + 2.4, z = 15.6, y = 0.0, r = 0.5, count = 3, spread = 1.2, seed = 850)
    shrubs += ShrubSpot(x = LaneX - 2.4, z = 16.6, y = 0.0, r = 0.5, count = 3, spread = 1.2, seed = 851)

    {
      val tx   = GoshinX
      val tz   = GoshinZ
      val ty   = Street.groundY(tz) + 1.7
      val ring = new Group()
      val rope = new Mesh(new TorusGeometry(0.62, 0.095, 6, 18), M.rope)
      rope.rotation.x = math.Pi / 2.0
      rope.castShadow = true
      ThreeInterop.addChild(ring, rope)
      (0 until 5).foreach { i =>
        val a     = (i / 5.0) * math.Pi * 2.0 + 0.4
        val strip = new Group()
        (0 until 5).foreach { k =>
          val q = new Mesh(new PlaneGeometry(0.085, 0.075), M.paper)
          q.position.set(if k % 2 == 1 then 0.022 else -0.022, -0.04 - k * 0.062, 0.0)
          q.rotation.y = if k % 2 == 1 then 0.6 else -0.6
          ThreeInterop.addChild(strip, q)
        }
        strip.position.set(math.cos(a) * 0.6, -0.1, math.sin(a) * 0.6)
        strip.rotation.y = -a
        ThreeInterop.addChild(ring, strip)
      }
      ring.position.set(tx, ty, tz)
      ctx.add(ring)
    }

    petals += PetalPatch(x = AxisX, z = 22.6, w = 3.6, d = 5.0, y = Top * 0.5, n = 60)
    petals += PetalPatch(x = (TerX0 + TerX1) / 2.0, z = 29.0, w = 15.0, d = 6.0, y = Top, n = 150)
    petals += PetalPatch(x = (TerX0 + TerX1) / 2.0, z = 36.0, w = 15.0, d = 6.0, y = Top, n = 120)
    petals += PetalPatch(x = AxisX, z = 17.0, w = 6.0, d = 4.0, y = 0.0, n = 60)

    Vector((-33.0, 33.0, 2.2), (-24.0, 34.4, 2.0), (-30.0, 27.6, 1.8), (-35.0, 37.0, 2.4), (-22.0, 29.0, 1.6)).foreach {
      case (dx, dz, r) =>
        Ground.dapple(ctx,
          Ground.DappleOptions(x = dx, z = dz, y = Top, r = r, spread = 2.4, n = 8,
            rng = rng, opacity = 0.14))
    }
    Ground.dapple(ctx,
      Ground.DappleOptions(x = AxisX, z = 16.4, y = 0.0, r = 1.9, spread = 2.6, n = 7,
        rng = rng, opacity = 0.13))

    Planting(sakura = sakura.toVector, shrubs = shrubs.toVector, bamboo = bamboo.toVector,
      grove = grove.toVector, petals = petals.toVector)
  end buildShrine
end Shrine
