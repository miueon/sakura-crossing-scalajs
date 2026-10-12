package sakura.world

import sakura.ThreeInterop
import sakura.core.{Outline, Pal, Textures, Toon, Util}
import sakura.core.Outline.OutlineOptions
import sakura.core.Toon.{CelOptions, FlatOptions}
import vendor.three.*

import scala.collection.mutable
import scala.math
import scala.scalajs.js

/** 1:1 port of upstream `src/world/housing.js`: the attic house, the walk-up block, the terrace
  * and the two lean-tos. All authored facing +Z and rotated into place by `face`.
  */
object Housing:

  private val Walls = Vector(
    Pal.wallWhite, Pal.wallCream, Pal.wallBlue, Pal.wallBeige, Pal.wallGray, Pal.wallPink,
    Pal.wallTea, Pal.wallSage
  )
  private val Roofs = Vector(Pal.roofSlate, Pal.roofBlue, Pal.roofBrown, Pal.roofTeal)
  private val Doors = Vector(0x8a6f5c, 0x5f6f7a, 0x7a5a52, 0x4f6b58, 0x8f7a52, 0x6a5f70)

  private object M:
    lazy val walls       = Walls.map(c => Toon.cel(CelOptions(color = c, bands = 3, tint = 0x6f6790)))
    lazy val roofs       = Roofs.map(c => Toon.cel(CelOptions(color = c, bands = 3, tint = 0x514b70)))
    lazy val doors       = Doors.map(c => Toon.cel(CelOptions(color = c, bands = 3, tint = 0x5c5680)))
    lazy val trim        = Toon.cel(CelOptions(color = Pal.trim, bands = 3, tint = 0x5c5680))
    lazy val metal       = Toon.cel(CelOptions(color = Pal.metal, bands = 3, tint = 0x666090))
    lazy val metalDark   = Toon.cel(CelOptions(color = Pal.metalDark, bands = 3, tint = 0x5c5680))
    lazy val glass       = Toon.flat(FlatOptions(color = Pal.glassDark))
    lazy val concrete    = Toon.cel(CelOptions(color = Pal.concrete, bands = 3, tint = 0x6f6790))
    lazy val concreteMid = Toon.cel(CelOptions(color = Pal.concreteMid, bands = 3, tint = 0x6a6288))
    lazy val board       = Toon.cel(CelOptions(color = 0xa88f6e, bands = 3, tint = 0x5c5680))
    lazy val boardDark   = Toon.cel(CelOptions(color = 0x7d6146, bands = 3, tint = 0x554e74))
    lazy val tile        = Toon.cel(CelOptions(color = 0x5a5f6e, bands = 3, tint = 0x4a4468))

  type Parts = mutable.LinkedHashMap[String, mutable.ArrayBuffer[Util.BakePart]]

  private def newParts(keys: String*): Parts =
    mutable.LinkedHashMap(keys.map(k => k -> mutable.ArrayBuffer[Util.BakePart]())*)

  private def push(parts: Parts)(key: String, geo: BufferGeometry[?, ?], mx: Matrix4): Unit =
    parts(key) += Util.BakePart(geo, Some(mx))

  /** A sliding window: reveal, pane, mullion, sill. Facing +z, in local space. */
  private def slider(
    parts: Parts,
    g: Group[Object3DEventMap],
    x: Double,
    y: Double,
    z: Double,
    w: Double,
    h: Double,
    lit: Boolean = false,
    curtain: Boolean = false,
    variant: Int
  ): Unit =
    push(parts)("trim", new BoxGeometry(w + 0.16, h + 0.16, 0.14), Util.trs(x, y, z - 0.03))
    val paneMat =
      if lit then
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.litWindowTex(variant % 3)),
          cache = false))
      else M.glass
    ThreeInterop.addChild(g, Util.box(w, h, 0.05, paneMat, x, y, z + 0.045))
    push(parts)("metal", new BoxGeometry(0.06, h, 0.08), Util.trs(x, y, z + 0.07))
    push(parts)("metal", new BoxGeometry(w + 0.1, 0.06, 0.08), Util.trs(x, y + h / 2.0 + 0.03, z + 0.07))
    push(parts)("trim", new BoxGeometry(w + 0.24, 0.08, 0.2), Util.trs(x, y - h / 2.0 - 0.08, z + 0.06))
    if curtain then
      val cur = new Mesh(new PlaneGeometry(w * 0.44, h * 0.9),
        Toon.flat(FlatOptions(color = 0xeae2d4, map = Some(Textures.curtainTex(variant % 2)),
          cache = false)))
      cur.position.set(x + w * 0.25, y, z + 0.052)
      ThreeInterop.setUserDataFlag(cur, "noOutline")
      ThreeInterop.addChild(g, cur)
  end slider

  private val blockNames = Vector(
    ("ひばり台コーポ", "HIBARIDAI  CORP"), ("メゾン さくら坂", "MAISON  SAKURAZAKA"), ("ハイツ ひばり", "HEIGHTS  HIBARI"),
    ("コーポ みなみ", "CORP  MINAMI"), ("グリーンハイツ", "GREEN  HEIGHTS"), ("さくら荘", "SAKURA  SO"), ("コーポ ひがし", "CORP  HIGASHI"),
    ("ハイツ みのり", "HEIGHTS  MINORI"), ("第二 さくら荘", "SAKURA  SO  II"), ("コーポ さかえ", "CORP  SAKAE")
  )

  private val blockPlateCache = mutable.HashMap.empty[Int, CanvasTexture[?]]

  private def blockPlateTexture(variant: Int): CanvasTexture[?] =
    blockPlateCache.getOrElseUpdate(
      variant, {
        val canvas = org.scalajs.dom.document.createElement("canvas")
          .asInstanceOf[org.scalajs.dom.HTMLCanvasElement]
        canvas.width = 512
        canvas.height = 152
        val c = canvas.getContext("2d").asInstanceOf[org.scalajs.dom.CanvasRenderingContext2D | Null]
        if c == null then
          throw new IllegalStateException("sakura-crossing: the canvas has no 2D context")
        c.fillStyle = "#f4f2ea"
        c.fillRect(0.0, 0.0, 512.0, 152.0)
        c.fillStyle = f"#${Pal.trim}%06x"
        c.fillRect(0.0, 139.0, 512.0, 13.0)
        val (name, latin) = blockNames(variant % blockNames.length)
        Housing.centeredText(c, name, 256.0, 57.76, 452.0, 66.0, "#4b4757", "bold", 6.0)
        Housing.centeredText(c, latin, 256.0, 115.52, 422.0, 24.0, "#8f8a9c", "600", 5.0)
        val texture = new CanvasTexture(canvas)
        texture.colorSpace = SRGBColorSpace
        texture
      }
    )

  private def centeredText(
    c: org.scalajs.dom.CanvasRenderingContext2D,
    text: String,
    x: Double,
    y: Double,
    maxW: Double,
    size: Double,
    color: String,
    weight: String,
    spacing: Double
  ): Unit =
    var fitted  = size
    var running = true
    while running do
      c.font = s"$weight ${fitted}px $JpFont"
      if c.measureText(text).width <= maxW then running = false
      else
        fitted -= 2.0
        if fitted <= 6.0 then running = false
    c.fillStyle = color
    c.textAlign = if spacing != 0.0 then "left" else "center"
    c.textBaseline = "middle"
    if spacing != 0.0 then
      val chars = text.toList
      val total = chars.foldLeft(-spacing)((sum, ch) => sum + c.measureText(ch.toString).width + spacing)
      var cx    = x - total / 2.0
      chars.foreach { ch =>
        c.fillText(ch.toString, cx, y)
        cx += c.measureText(ch.toString).width + spacing
      }
    else c.fillText(text, x, y, maxW)
  end centeredText

  private val JpFont =
    "'Yu Gothic', 'Yu Gothic UI', 'Meiryo', 'MS Gothic', 'Hiragino Kaku Gothic ProN', sans-serif"

  final case class AtticHouseOpts(
    x: Double = 0.0,
    z: Double = 0.0,
    y: Double = 0.0,
    w: Double = 6.6,
    d: Double = 7.2,
    face: Buildings.Face = Buildings.Face.ZPlus,
    seed: Int = 41,
    wall: Int = 1,
    roof: Int = 0,
    roofH: Double = 2.4,
    door: Int = 0,
    nameVariant: Int = 3,
    lit: Boolean = false,
    litDormer: Boolean = false
  )

  def makeAtticHouse(o: AtticHouseOpts): Group[Object3DEventMap] =
    val rng     = Util.rngKit(o.seed)
    val g       = new Group()
    val w       = o.w
    val d       = o.d
    val FH      = 2.55
    val H       = FH * 2
    val RH      = o.roofH
    val EAVE    = 0.62
    val wallMat = M.walls(o.wall)
    val roofMat = M.roofs(o.roof)
    val parts = newParts("wall", "board", "boardDark", "roof", "trim", "metal", "metalDark",
      "concrete", "door")

    push(parts)("concrete", new BoxGeometry(w + 0.2, 0.46, d + 0.2), Util.trs(0.0, 0.23, 0.0))
    push(parts)("board", new BoxGeometry(w, FH - 0.1, d),
      Util.trs(0.0, 0.46 + (FH - 0.1) / 2.0, 0.0))
    push(parts)("wall", new BoxGeometry(w, H - FH + 0.1, d),
      Util.trs(0.0, 0.46 + FH + (H - FH) / 2.0 - 0.05, 0.0))
    push(parts)("boardDark", new BoxGeometry(w + 0.1, 0.14, d + 0.1),
      Util.trs(0.0, 0.46 + FH - 0.04, 0.0))
    val ribs  = mutable.ArrayBuffer[Util.BakePart]()
    val nRibs = math.round((FH - 0.2) / 0.24).toInt
    (0 until nRibs).foreach { i =>
      ribs += Util.BakePart(new BoxGeometry(w + 0.05, 0.05, d + 0.05),
        Some(Util.trs(0.0, 0.54 + i * 0.24, 0.0)))
    }
    val ribMesh = new Mesh(Util.bake(ribs.toList), M.boardDark)
    ribMesh.castShadow = true
    ThreeInterop.addChild(g, ribMesh)
    Vector(-1.0, 1.0).foreach { sx =>
      Vector(-1.0, 1.0).foreach { sz =>
        push(parts)("boardDark", new BoxGeometry(0.16, FH, 0.16),
          Util.trs(sx * (w / 2.0 - 0.04), 0.46 + FH / 2.0, sz * (d / 2.0 - 0.04)))
      }
    }

    val yr    = 0.46 + H
    val rw    = w + EAVE * 2.0
    val rd    = d + EAVE * 2.0
    val slope = math.atan2(RH, rw / 2.0)
    val slab  = math.hypot(rw / 2.0, RH) + 0.1
    Vector(-1.0, 1.0).foreach { s =>
      push(parts)("roof", new BoxGeometry(slab, 0.17, rd),
        Util.trs(s * (rw / 4.0), yr + RH / 2.0, 0.0, 0.0, 0.0, -s * slope))
    }
    push(parts)("roof", new BoxGeometry(0.3, 0.22, rd + 0.1), Util.trs(0.0, yr + RH + 0.05, 0.0))
    val tri = new Shape()
    tri.moveTo(-w / 2.0, 0)
    tri.lineTo(w / 2.0, 0)
    tri.lineTo(0.0, RH * (1 - (EAVE * 2.0) / rw))
    tri.closePath()
    val triGeo = new ExtrudeGeometry(tri,
      ExtrudeGeometryOptions(depth = Some(0.18), bevelEnabled = Some(false)))
    triGeo.translate(0.0, 0.0, -0.09)
    Vector(-1.0, 1.0).foreach { s =>
      push(parts)("board", triGeo, Util.trs(0.0, yr, s * (d / 2.0 - 0.02)))
    }
    triGeo.dispose()
    val raf = mutable.ArrayBuffer[Util.BakePart]()
    val nr  = math.round(rd / 0.46).toInt
    (0 until nr).foreach { i =>
      val rz = -rd / 2.0 + 0.3 + i * ((rd - 0.6) / (nr - 1))
      Vector(-1.0, 1.0).foreach { s =>
        raf += Util.BakePart(new BoxGeometry(EAVE + 0.16, 0.11, 0.09),
          Some(Util.trs(s * (w / 2.0 + EAVE / 2.0 - 0.04),
            yr + 0.16 + (EAVE / 2.0) * math.tan(slope) * 0.5, rz, 0.0, 0.0, -s * slope)))
      }
    }
    val rafMesh = new Mesh(Util.bake(raf.toList), M.boardDark)
    rafMesh.castShadow = true
    ThreeInterop.addChild(g, rafMesh)
    Vector(-1.0, 1.0).foreach { s =>
      push(parts)("metal", new BoxGeometry(0.12, 0.12, rd),
        Util.trs(s * (rw / 2.0 - 0.02), yr + 0.06, 0.0))
    }

    val dw = 1.5
    val dd = 1.15
    val dh = 1.15
    val dz = d / 2.0 - 1.6
    val dx = -w / 4.0
    push(parts)("wall", new BoxGeometry(dw, dh, dd), Util.trs(dx, yr + 0.5 + dh / 2.0, dz))
    val dtri = new Shape()
    dtri.moveTo(-dw / 2.0 - 0.12, 0)
    dtri.lineTo(dw / 2.0 + 0.12, 0)
    dtri.lineTo(0.0, 0.44)
    dtri.closePath()
    val dtriGeo = new ExtrudeGeometry(dtri,
      ExtrudeGeometryOptions(depth = Some(0.14), bevelEnabled = Some(false)))
    dtriGeo.translate(0.0, 0.0, -0.07)
    push(parts)("wall", dtriGeo, Util.trs(dx, yr + 0.5 + dh, dz - dd / 2.0 + 0.07))
    dtriGeo.dispose()
    Vector(-1.0, 1.0).foreach { s =>
      push(parts)("roof",
        new BoxGeometry(math.hypot(dw / 2.0 + 0.2, 0.44) + 0.06, 0.11, dd + 0.3),
        Util.trs(dx + s * ((dw / 2.0 + 0.2) / 2.0), yr + 0.5 + dh + 0.22, dz,
          0.0, 0.0, -s * math.atan2(0.44, dw / 2.0 + 0.2)))
    }
    push(parts)("roof", new BoxGeometry(0.14, 0.13, dd + 0.34),
      Util.trs(dx, yr + 0.5 + dh + 0.44, dz))
    slider(parts, g, dx, yr + 0.5 + dh / 2.0 + 0.06, dz - dd / 2.0, 1.0, 0.78,
      lit = o.litDormer, variant = 2)

    val zf = d / 2.0
    val ex = w / 2.0 - 1.15
    push(parts)("door", new BoxGeometry(1.0, 2.0, 0.09), Util.trs(ex, 0.46 + 1.0, zf - 0.02))
    push(parts)("boardDark", new BoxGeometry(1.28, 2.2, 0.14),
      Util.trs(ex, 0.46 + 1.05, zf - 0.07))
    push(parts)("metal", new BoxGeometry(0.05, 0.9, 0.05), Util.trs(ex + 0.36, 0.46 + 1.0, zf + 0.04))
    push(parts)("boardDark", new BoxGeometry(1.9, 0.12, 0.95), Util.trs(ex, 0.46 + 2.32, zf + 0.4))
    push(parts)("roof", new BoxGeometry(2.0, 0.09, 1.05), Util.trs(ex, 0.46 + 2.4, zf + 0.42))
    Vector(-1.0, 1.0).foreach { s =>
      push(parts)("boardDark", new BoxGeometry(0.09, 0.5, 0.09),
        Util.trs(ex + s * 0.8, 0.46 + 2.02, zf + 0.72))
    }
    (0 until 2).foreach { i =>
      val t = 0.46 - i * 0.23
      push(parts)("concrete", new BoxGeometry(1.5 - i * 0.16, t, 0.36),
        Util.trs(ex, t / 2.0, zf + 0.18 + i * 0.36))
    }
    slider(parts, g, -w / 2.0 + 1.5, 0.46 + 1.45, zf, 1.7, 1.3, curtain = true, variant = 0)
    slider(parts, g, -w / 2.0 + 1.5, 0.46 + FH + 1.5, zf, 1.7, 1.3, lit = o.lit, variant = 1)
    slider(parts, g, ex, 0.46 + FH + 1.5, zf, 1.5, 1.3, curtain = true, variant = 1)
    val by = 0.46 + FH + 0.42
    val bw = 2.3
    push(parts)("concrete", new BoxGeometry(bw, 0.11, 0.9), Util.trs(ex, by, zf + 0.45))
    push(parts)("metal", new BoxGeometry(bw, 0.06, 0.06), Util.trs(ex, by + 1.0, zf + 0.88))
    push(parts)("metalDark", new BoxGeometry(bw, 0.42, 0.05), Util.trs(ex, by + 0.34, zf + 0.88))
    val nb = math.round(bw / 0.24).toInt
    (0 to nb).foreach { i =>
      push(parts)("metal", new BoxGeometry(0.04, 0.92, 0.04),
        Util.trs(ex - bw / 2.0 + (bw / nb) * i, by + 0.52, zf + 0.88))
    }
    push(parts)("metal", new BoxGeometry(bw * 0.8, 0.05, 0.05), Util.trs(ex, by + 1.4, zf + 0.6))
    if rng.chance(0.8) then
      (0 until 2).foreach { i =>
        val t = new Mesh(new PlaneGeometry(0.4, 0.6),
          Toon.cel(CelOptions(
            color = rng.pick(Vector(Pal.wallBlue, Pal.blossom, Pal.wallWhite, Pal.yellow)),
            bands = 2, side = 2, tint = 0x6f6790
          )))
        t.position.set(ex - 0.5 + i * 0.9, by + 1.08, zf + 0.6)
        t.castShadow = true
        ThreeInterop.addChild(g, t)
      }
    val plate = new Mesh(new BoxGeometry(0.32, 0.44, 0.1),
      Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.meterBox()), cache = false)))
    plate.position.set(-w / 2.0 + 0.5, 1.86, zf + 0.03)
    ThreeInterop.addChild(g, plate)
    push(parts)("metal", new BoxGeometry(0.3, 0.42, 0.22), Util.trs(w / 2.0 - 0.34, 0.72, zf + 0.1))
    push(parts)("metalDark", new BoxGeometry(0.34, 0.05, 0.26),
      Util.trs(w / 2.0 - 0.34, 0.95, zf + 0.1))
    val np = new Mesh(new PlaneGeometry(0.3, 0.15),
      Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.namePlate(o.nameVariant)),
        cache = false)))
    np.position.set(ex - 0.72, 1.5, zf + 0.045)
    ThreeInterop.setUserDataFlag(np, "noOutline")
    ThreeInterop.addChild(g, np)

    push(parts)("metal", new CylinderGeometry(0.055, 0.055, H + 0.4, 6),
      Util.trs(w / 2.0 - 0.12, (H + 0.4) / 2.0, -d / 2.0 + 0.2))
    push(parts)("metal", new CylinderGeometry(0.05, 0.05, H, 6),
      Util.trs(-w / 2.0 + 0.12, H / 2.0, d / 2.0 - 0.2))
    push(parts)("trim", new BoxGeometry(0.78, 0.58, 0.32), Util.trs(w / 2.0 + 0.16, 1.0, -0.6))
    push(parts)("metalDark", new CylinderGeometry(0.03, 0.03, 2.0, 5),
      Util.trs(w * 0.2, yr + RH + 1.1, -d * 0.2))
    (0 until 5).foreach { i =>
      push(parts)("metalDark", new BoxGeometry(0.02, 0.02, 0.6 - i * 0.06),
        Util.trs(w * 0.2, yr + RH + 0.5 + i * 0.16, -d * 0.2))
    }

    val matFor: Map[String, Material[?]] = Map(
      "wall"     -> wallMat, "board"      -> M.board, "boardDark" -> M.boardDark, "roof" -> roofMat,
      "trim"     -> M.trim, "metal"       -> M.metal, "metalDark" -> M.metalDark,
      "concrete" -> M.concreteMid, "door" -> M.doors(o.door)
    )
    parts.foreach { (key, bucket) =>
      if bucket.nonEmpty then
        val mesh = new Mesh(Util.bake(bucket.toList), matFor(key))
        mesh.castShadow = true
        mesh.receiveShadow = true
        ThreeInterop.addChild(g, mesh)
        if key == "wall" || key == "roof" || key == "board" then
          Outline.hullOutline(mesh, OutlineOptions(thickness = 0.0032))
    }
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = Buildings.faceRy(o.face)
    g.userData("top") = (0.46 + H + RH + 0.3).asInstanceOf[js.Any]
    g
  end makeAtticHouse

  final case class WalkupOpts(
    x: Double = 0.0,
    z: Double = 0.0,
    y: Double = 0.0,
    w: Double = 8.0,
    d: Double = 7.0,
    face: Buildings.Face = Buildings.Face.ZPlus,
    floors: Int = 3,
    units: Int = 4,
    fh: Double = 2.7,
    seed: Int = 61,
    wall: Int = 4,
    roof: Int = 0,
    door: Int = 1,
    plate: Int = 0
  )

  def makeWalkup(o: WalkupOpts): Group[Object3DEventMap] =
    val rng     = Util.rngKit(o.seed)
    val g       = new Group()
    val w       = o.w
    val d       = o.d
    val FLOORS  = o.floors
    val FH      = o.fh
    val H       = FH * FLOORS
    val UNITS   = o.units
    val GAL     = 1.35
    val wallMat = M.walls(o.wall)
    val parts   = newParts("wall", "trim", "roof", "metal", "metalDark", "concrete", "door")
    val litA = Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.litWindowTex(0)),
      cache = false))
    val litB = Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.litWindowTex(1)),
      cache = false))

    push(parts)("concrete", new BoxGeometry(w + 0.44, 0.44, d + 0.44), Util.trs(0.0, 0.22, 0.0))
    push(parts)("wall", new BoxGeometry(w, H, d - GAL), Util.trs(0.0, H / 2.0, -GAL / 2.0))
    (1 to FLOORS).foreach { k =>
      push(parts)("trim", new BoxGeometry(w + 0.26, 0.22, d + 0.26 - GAL),
        Util.trs(0.0, k * FH, -GAL / 2.0))
    }
    push(parts)("roof", new BoxGeometry(w + 0.5, 0.2, d + 0.5 - GAL),
      Util.trs(0.0, H + 0.1, -GAL / 2.0 + 0.08))
    Vector(-1.0, 1.0).foreach { s =>
      push(parts)("roof", new BoxGeometry(w + 0.5, 0.42, 0.16),
        Util.trs(0.0, H + 0.41, -GAL / 2.0 + 0.08 + s * ((d + 0.5 - GAL) / 2.0)))
      push(parts)("roof", new BoxGeometry(0.16, 0.42, d + 0.5 - GAL),
        Util.trs(s * ((w + 0.5) / 2.0), H + 0.41, -GAL / 2.0 + 0.08))
    }
    Vector(-1.6, -0.3).foreach { dx =>
      push(parts)("trim", new BoxGeometry(0.9, 0.62, 0.4), Util.trs(dx, H + 0.53, -1.4))
      push(parts)("metal", new BoxGeometry(0.94, 0.05, 0.46), Util.trs(dx, H + 0.86, -1.4))
    }
    push(parts)("metal", new CylinderGeometry(0.14, 0.14, 0.8, 10),
      Util.trs(w / 2.0 - 1.1, H + 0.6, -1.9))
    push(parts)("metalDark", new CylinderGeometry(0.028, 0.028, 1.9, 5),
      Util.trs(-w / 2.0 + 0.8, H + 1.15, -1.6))

    val gz = d / 2.0
    (0 until FLOORS).foreach { k =>
      val y = k * FH
      push(parts)("concrete", new BoxGeometry(w, 0.22, GAL), Util.trs(0.0, y + 0.11, gz - GAL / 2.0))
      push(parts)("metal", new BoxGeometry(w, 0.07, 0.07), Util.trs(0.0, y + 1.16, gz - 0.07))
      push(parts)("metalDark", new BoxGeometry(w, 0.07, 0.07), Util.trs(0.0, y + 0.36, gz - 0.07))
      val nb = math.round(w / 0.22).toInt
      (0 to nb).foreach { i =>
        push(parts)("metal", new BoxGeometry(0.04, 0.84, 0.04),
          Util.trs(-w / 2.0 + (w / nb) * i, y + 0.74, gz - 0.07))
      }
      push(parts)("trim", new BoxGeometry(w, 0.1, GAL + 0.1),
        Util.trs(0.0, y + FH - 0.05, gz - GAL / 2.0 + 0.05))
      (0 until UNITS).foreach { i =>
        val dx = -w / 2.0 + (w / UNITS) * (i + 0.5)
        push(parts)("trim", new BoxGeometry(1.0, 2.14, 0.14),
          Util.trs(dx - 0.32, y + 1.3, gz - GAL + 0.07))
        push(parts)("door", new BoxGeometry(0.86, 2.0, 0.06),
          Util.trs(dx - 0.32, y + 1.26, gz - GAL + 0.14))
        push(parts)("metal", new BoxGeometry(0.05, 0.3, 0.05),
          Util.trs(dx - 0.02, y + 1.2, gz - GAL + 0.18))
        push(parts)("metal", new BoxGeometry(0.3, 0.4, 0.12),
          Util.trs(dx + 0.66, y + 1.94, gz - GAL + 0.08))
        val isLit = rng.chance(0.4)
        ThreeInterop.addChild(g,
          Util.box(0.52, 0.6, 0.05,
            if isLit then (if i % 2 == 1 then litB else litA) else M.glass,
            dx + 0.66, y + 1.3, gz - GAL + 0.14))
        push(parts)("metal", new BoxGeometry(0.62, 0.7, 0.1),
          Util.trs(dx + 0.66, y + 1.3, gz - GAL + 0.09))
        val np = new Mesh(new PlaneGeometry(0.14, 0.09),
          Toon.flat(FlatOptions(color = 0xf2efe4)))
        np.position.set(dx - 0.32, y + 2.28, gz - GAL + 0.15)
        ThreeInterop.setUserDataFlag(np, "noOutline")
        ThreeInterop.addChild(g, np)
      }
    }
    push(parts)("metal", new CylinderGeometry(0.075, 0.075, H, 6),
      Util.trs(w / 2.0 - 0.22, H / 2.0, gz - GAL + 0.16))
    (0 until FLOORS).foreach { k =>
      push(parts)("metal", new BoxGeometry(w * 0.5, 0.07, 0.07),
        Util.trs(w * 0.24, k * FH + 0.55, gz - GAL + 0.16))
      push(parts)("metalDark", new BoxGeometry(0.12, 0.06, 0.12),
        Util.trs(w / 2.0 - 0.22, k * FH + 0.3, gz - GAL + 0.16))
    }

    val sx = -w / 2.0 - 1.6
    (0 until FLOORS).foreach { k =>
      val y = k * FH
      push(parts)("concrete", new BoxGeometry(1.6, 0.2, GAL + 0.4),
        Util.trs(sx + 0.8, y + 0.1, gz - GAL / 2.0 + 0.2))
      (0 until 9).foreach { i =>
        push(parts)("concrete", new BoxGeometry(1.35, 0.14, 0.32),
          Util.trs(sx + 0.8, y + 0.26 + (i * FH) / 9.0, gz - GAL / 2.0 + 0.4 - i * 0.26))
      }
      push(parts)("metal", new BoxGeometry(0.07, 0.07, GAL + 0.4),
        Util.trs(sx + 0.04, y + 1.16, gz - GAL / 2.0 + 0.2))
      (0 to 8).foreach { i =>
        push(parts)("metal", new BoxGeometry(0.04, 0.9, 0.04),
          Util.trs(sx + 0.04, y + 0.72 + (i * FH) / 9.0, gz - GAL / 2.0 + 0.4 - i * 0.26))
      }
    }
    push(parts)("roof", new BoxGeometry(1.95, 0.16, GAL + 0.8),
      Util.trs(sx + 0.8, H + 0.08, gz - GAL / 2.0 + 0.3))

    (1 until FLOORS).foreach { k =>
      val y  = k * FH
      val bz = -d / 2.0
      push(parts)("concrete", new BoxGeometry(w - 0.7, 0.18, 0.95),
        Util.trs(0.0, y + 0.09, bz - 0.48))
      push(parts)("metal", new BoxGeometry(w - 0.7, 0.06, 0.06), Util.trs(0.0, y + 1.08, bz - 0.94))
      push(parts)("metalDark", new BoxGeometry(w - 0.7, 0.46, 0.05),
        Util.trs(0.0, y + 0.58, bz - 0.94))
      val nb = math.round((w - 0.7) / 0.24).toInt
      (0 to nb).foreach { i =>
        push(parts)("metal", new BoxGeometry(0.04, 0.94, 0.04),
          Util.trs(-(w - 0.7) / 2.0 + ((w - 0.7) / nb) * i, y + 0.62, bz - 0.94))
      }
      push(parts)("metal", new BoxGeometry(w - 1.4, 0.05, 0.05), Util.trs(0.0, y + 1.5, bz - 0.7))
      (0 until UNITS - 1).foreach { i =>
        val dx = -w / 2.0 + (w / (UNITS - 1)) * (i + 0.5)
        slider(parts, g, dx, y + 1.1, bz - 0.02, 1.4, 1.8,
          lit = rng.chance(0.45), curtain = rng.chance(0.6), variant = i)
        if i > 0 then
          push(parts)("trim", new BoxGeometry(0.06, 1.0, 0.9),
            Util.trs(-w / 2.0 + (w / (UNITS - 1)) * i, y + 0.68, bz - 0.5))
      }
      if k != 2 then
        (0 until 3).foreach { i =>
          val t = new Mesh(
            new PlaneGeometry(rng.range(0.34, 0.5), rng.range(0.5, 0.72)),
            Toon.cel(CelOptions(
              color = rng.pick(Vector(Pal.wallBlue, Pal.blossom, Pal.wallWhite, Pal.yellow, 0xa8cfe0)),
              bands = 2, side = 2, tint = 0x6f6790
            ))
          )
          t.position.set(-2.2 + i * 1.9, y + 1.12, bz - 0.7)
          t.rotation.y = rng.range(-0.12, 0.12)
          t.castShadow = true
          ThreeInterop.addChild(g, t)
        }
    }

    val grayMat = Toon.flat(FlatOptions(color = Pal.wallGray))
    val plateMats = Props.matArray(
      grayMat, grayMat, grayMat, grayMat,
      Toon.flat(FlatOptions(color = 0xffffff, map = Some(blockPlateTexture(o.plate)),
        cache = false)),
      grayMat
    )
    val plate = new Mesh(new BoxGeometry(1.4, 0.42, 0.1), plateMats)
    plate.position.set(-w / 2.0 + 1.1, 2.3, gz + 0.06)
    plate.castShadow = true
    ThreeInterop.addChild(g, plate)
    Outline.hullOutline(plate, OutlineOptions(thickness = 0.003))

    val matFor: Map[String, Material[?]] = Map(
      "wall"      -> wallMat, "trim"         -> M.trim, "roof"     -> M.roofs(o.roof), "metal" -> M.metal,
      "metalDark" -> M.metalDark, "concrete" -> M.concrete, "door" -> M.doors(o.door)
    )
    parts.foreach { (key, bucket) =>
      if bucket.nonEmpty then
        val mesh = new Mesh(Util.bake(bucket.toList), matFor(key))
        mesh.castShadow = true
        mesh.receiveShadow = true
        ThreeInterop.addChild(g, mesh)
        if key == "wall" || key == "roof" then Outline.hullOutline(mesh, OutlineOptions(thickness = 0.0032))
    }
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = Buildings.faceRy(o.face)
    g.userData("top") = (H + 0.62).asInstanceOf[js.Any]
    g.userData("stairOut") = 1.6.asInstanceOf[js.Any]
    g
  end makeWalkup

  final case class TerraceOpts(
    x: Double = 0.0,
    z: Double = 0.0,
    y: Double = 0.0,
    units: Int = 3,
    unitW: Double = 2.9,
    d: Double = 6.2,
    face: Buildings.Face = Buildings.Face.ZPlus,
    seed: Int = 71,
    wall: Int = 0,
    roof: Int = 1
  )

  def makeTerrace(o: TerraceOpts): Group[Object3DEventMap] =
    val rng     = Util.rngKit(o.seed)
    val g       = new Group()
    val N       = o.units
    val uw      = o.unitW
    val w       = uw * N
    val d       = o.d
    val FH      = 2.62
    val H       = FH * 2
    val wallMat = M.walls(o.wall)
    val parts   = newParts("wall", "trim", "roof", "metal", "metalDark", "concrete", "door")

    push(parts)("concrete", new BoxGeometry(w + 0.18, 0.4, d + 0.18), Util.trs(0.0, 0.2, 0.0))
    push(parts)("wall", new BoxGeometry(w, H, d), Util.trs(0.0, H / 2.0, 0.0))
    push(parts)("trim", new BoxGeometry(w + 0.1, 0.14, d + 0.1), Util.trs(0.0, FH, 0.0))

    val eave  = 0.36
    val rw    = w + eave * 2.0
    val rd    = d + eave * 2.0
    val rh    = 1.0
    val slope = math.atan2(rh, rd / 2.0)
    val slab  = math.hypot(rd / 2.0, rh) + 0.06
    Vector(-1.0, 1.0).foreach { s =>
      push(parts)("roof", new BoxGeometry(rw, 0.14, slab),
        Util.trs(0.0, H + rh / 2.0, s * (rd / 4.0), s * slope, 0.0, 0.0))
    }
    push(parts)("roof", new BoxGeometry(rw + 0.06, 0.17, 0.24), Util.trs(0.0, H + rh + 0.04, 0.0))
    val tri = new Shape()
    tri.moveTo(-rd / 2.0 + eave, 0)
    tri.lineTo(rd / 2.0 - eave, 0)
    tri.lineTo(0.0, rh * (1 - (eave * 2.0) / rd))
    tri.closePath()
    val triGeo = new ExtrudeGeometry(tri,
      ExtrudeGeometryOptions(depth = Some(0.16), bevelEnabled = Some(false)))
    triGeo.translate(0.0, 0.0, -0.08)
    Vector(-1.0, 1.0).foreach { s =>
      push(parts)("wall", triGeo, Util.trs(s * (w / 2.0 - 0.02), H, 0.0, 0.0, math.Pi / 2.0, 0.0))
    }
    triGeo.dispose()
    push(parts)("metal", new BoxGeometry(rw, 0.11, 0.11), Util.trs(0.0, H + 0.04, rd / 2.0 - 0.02))

    val zf = d / 2.0
    (0 until N).foreach { i =>
      val cx = -w / 2.0 + uw * (i + 0.5)
      if i > 0 then
        push(parts)("trim", new BoxGeometry(0.16, H, d + 0.06),
          Util.trs(-w / 2.0 + uw * i, H / 2.0, 0.0))
      val dm   = M.doors((i * 2 + o.seed) % Doors.length)
      val dx   = cx - uw / 2.0 + 0.78
      val door = Util.box(0.9, 2.0, 0.08, dm, dx, 0.4 + 1.0, zf + 0.01)
      door.castShadow = true
      door.receiveShadow = true
      ThreeInterop.addChild(g, door)
      push(parts)("trim", new BoxGeometry(1.16, 2.2, 0.14), Util.trs(dx, 0.4 + 1.04, zf - 0.05))
      push(parts)("metal", new BoxGeometry(0.05, 0.34, 0.05), Util.trs(dx + 0.32, 0.4 + 1.0, zf + 0.06))
      push(parts)("trim", new BoxGeometry(1.5, 0.1, 0.62), Util.trs(dx, 0.4 + 2.3, zf + 0.28))
      push(parts)("metal", new BoxGeometry(1.56, 0.05, 0.66), Util.trs(dx, 0.4 + 2.36, zf + 0.29))
      (0 until 2).foreach { k =>
        val t = 0.4 - k * 0.2
        push(parts)("concrete", new BoxGeometry(1.24 - k * 0.14, t, 0.32),
          Util.trs(dx, t / 2.0, zf + 0.16 + k * 0.32))
      }
      val wx = cx + uw / 2.0 - 0.85
      slider(parts, g, wx, 0.4 + 1.4, zf, 1.2, 1.2, curtain = i != 1, variant = i)
      if i == 1 then
        push(parts)("metal", new BoxGeometry(1.34, 0.62, 0.07), Util.trs(wx, 0.4 + 1.7, zf + 0.08))
        push(parts)("metalDark", new BoxGeometry(1.36, 0.06, 0.09),
          Util.trs(wx, 0.4 + 1.38, zf + 0.09))
      slider(parts, g, cx, FH + 1.5, zf, 1.7, 1.34, lit = i == 2, curtain = i == 0,
        variant = i + 1)
      val by = FH + 0.44
      push(parts)("concrete", new BoxGeometry(uw - 0.16, 0.1, 0.82), Util.trs(cx, by, zf + 0.41))
      push(parts)("metal", new BoxGeometry(uw - 0.16, 0.06, 0.06), Util.trs(cx, by + 0.98, zf + 0.8))
      push(parts)("metalDark", new BoxGeometry(uw - 0.16, 0.4, 0.05),
        Util.trs(cx, by + 0.32, zf + 0.8))
      val nb = math.round((uw - 0.16) / 0.23).toInt
      (0 to nb).foreach { k =>
        push(parts)("metal", new BoxGeometry(0.04, 0.9, 0.04),
          Util.trs(cx - (uw - 0.16) / 2.0 + ((uw - 0.16) / nb) * k, by + 0.5, zf + 0.8))
      }
      if i != 1 then
        push(parts)("metal", new BoxGeometry(uw * 0.7, 0.05, 0.05),
          Util.trs(cx, by + 1.36, zf + 0.56))
        (0 until 2).foreach { k =>
          val t = new Mesh(new PlaneGeometry(0.38, 0.58),
            Toon.cel(CelOptions(
              color = rng.pick(Vector(Pal.wallBlue, Pal.blossom, Pal.wallWhite, Pal.yellow)),
              bands = 2, side = 2, tint = 0x6f6790
            )))
          t.position.set(cx - 0.42 + k * 0.84, by + 1.05, zf + 0.56)
          t.castShadow = true
          ThreeInterop.addChild(g, t)
        }
      if i > 0 then
        push(parts)("trim", new BoxGeometry(0.05, 0.98, 0.78),
          Util.trs(-w / 2.0 + uw * i, by + 0.6, zf + 0.44))
      val meter = new Mesh(new BoxGeometry(0.26, 0.38, 0.09),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.meterBox()), cache = false)))
      meter.position.set(cx - uw / 2.0 + 0.24, 1.8, zf + 0.03)
      ThreeInterop.addChild(g, meter)
      val np = new Mesh(new PlaneGeometry(0.28, 0.14),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.namePlate(i + 1)),
          cache = false)))
      np.position.set(dx - 0.62, 1.48, zf + 0.045)
      ThreeInterop.setUserDataFlag(np, "noOutline")
      ThreeInterop.addChild(g, np)
      if i > 0 then
        push(parts)("metal", new CylinderGeometry(0.05, 0.05, H, 6),
          Util.trs(-w / 2.0 + uw * i, H / 2.0, zf + 0.1))
    }
    push(parts)("metal", new CylinderGeometry(0.055, 0.055, H, 6),
      Util.trs(-w / 2.0 + 0.14, H / 2.0, -zf + 0.14))

    val matFor: Map[String, Material[?]] = Map(
      "wall"      -> wallMat, "trim"         -> M.trim, "roof"        -> M.roofs(o.roof), "metal" -> M.metal,
      "metalDark" -> M.metalDark, "concrete" -> M.concreteMid, "door" -> M.doors(0)
    )
    parts.foreach { (key, bucket) =>
      if bucket.nonEmpty then
        val mesh = new Mesh(Util.bake(bucket.toList), matFor(key))
        mesh.castShadow = true
        mesh.receiveShadow = true
        ThreeInterop.addChild(g, mesh)
        if key == "wall" || key == "roof" then Outline.hullOutline(mesh, OutlineOptions(thickness = 0.0032))
    }
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = Buildings.faceRy(o.face)
    g.userData("top") = (H + 1.1).asInstanceOf[js.Any]
    g.userData("width") = w.asInstanceOf[js.Any]
    g
  end makeTerrace

  private def leanTo(
    g: Group[Object3DEventMap],
    w: Double,
    d: Double,
    h: Double,
    fall: Double,
    sheetColor: Int,
    opacity: Double
  ): Unit =
    val parts = mutable.ArrayBuffer[Util.BakePart]()
    Vector(-1.0, 1.0).foreach { sx =>
      Vector(-1.0, 1.0).foreach { sz =>
        val hy = h + (if sz > 0 then 0.0 else fall)
        parts += Util.BakePart(new CylinderGeometry(0.045, 0.05, hy, 7),
          Some(Util.trs(sx * (w / 2.0 - 0.12), hy / 2.0, sz * (d / 2.0 - 0.1))))
        parts += Util.BakePart(new CylinderGeometry(0.1, 0.12, 0.12, 8),
          Some(Util.trs(sx * (w / 2.0 - 0.12), 0.06, sz * (d / 2.0 - 0.1))))
      }
    }
    val tilt = math.atan2(fall, d)
    Vector(-1.0, 1.0).foreach { sx =>
      parts += Util.BakePart(new BoxGeometry(0.06, 0.06, d / math.cos(tilt)),
        Some(Util.trs(sx * (w / 2.0 - 0.12), h + fall / 2.0 + 0.02, 0.0, tilt, 0.0, 0.0)))
    }
    parts += Util.BakePart(new BoxGeometry(w, 0.07, 0.09),
      Some(Util.trs(0.0, h + 0.02, d / 2.0 - 0.08)))
    parts += Util.BakePart(new BoxGeometry(w, 0.09, 0.11),
      Some(Util.trs(0.0, h + fall - 0.02, -d / 2.0 + 0.06)))
    val fm = new Mesh(Util.bake(parts.toList), M.metalDark)
    fm.castShadow = true
    ThreeInterop.addChild(g, fm)

    val sheet = new Mesh(new BoxGeometry(w + 0.16, 0.03, d / math.cos(tilt) + 0.2),
      Toon.flat(FlatOptions(color = sheetColor, transparent = true, opacity = opacity,
        depthWrite = Some(false), cache = false)))
    sheet.position.set(0.0, h + fall / 2.0 + 0.07, 0.0)
    sheet.rotation.x = tilt
    ThreeInterop.setUserDataFlag(sheet, "noOutline")
    ThreeInterop.setUserDataFlag(sheet, "noShadow")
    ThreeInterop.addChild(g, sheet)
    val ribs = mutable.ArrayBuffer[Util.BakePart]()
    val n    = math.round(w / 0.24).toInt
    (0 until n).foreach { i =>
      ribs += Util.BakePart(new BoxGeometry(0.05, 0.05, d / math.cos(tilt) + 0.16),
        Some(Util.trs(-w / 2.0 + 0.1 + i * ((w - 0.2) / (n - 1)), h + fall / 2.0 + 0.09, 0.0,
          tilt, 0.0, 0.0)))
    }
    ThreeInterop.addChild(g, new Mesh(Util.bake(ribs.toList), M.metal))
  end leanTo

  final case class CarportOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    w: Double = 2.7,
    d: Double = 5.0,
    h: Double = 2.25
  )

  def makeCarport(o: CarportOpts): Group[Object3DEventMap] =
    val g = new Group()
    leanTo(g, o.w, o.d, o.h, 0.22, 0x9cb8d0, 0.62)
    ThreeInterop.addChild(g,
      Util.box(o.w - 0.9, 0.11, 0.16, M.concreteMid, 0.0, 0.055, -o.d / 2.0 + 0.7))
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g

  final case class BikeShelterOpts(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0,
    ry: Double = 0.0,
    w: Double = 4.2,
    d: Double = 1.9,
    h: Double = 2.05
  )

  def makeBikeShelter(o: BikeShelterOpts): Group[Object3DEventMap] =
    val g = new Group()
    leanTo(g, o.w, o.d, o.h, 0.18, 0xb0c4d0, 0.58)
    g.position.set(o.x, o.y, o.z)
    g.rotation.y = o.ry
    g
end Housing
