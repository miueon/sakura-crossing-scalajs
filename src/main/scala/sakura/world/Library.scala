package sakura.world

import org.scalajs.dom
import sakura.ThreeInterop
import sakura.core.{Outline, Pal, Textures, Toon, Util}
import sakura.core.Outline.OutlineOptions
import sakura.core.Toon.{CelOptions, FlatOptions}
import vendor.three.*

import scala.collection.mutable
import scala.math

/** 1:1 port of upstream `src/world/library.js`: ひばり台図書館 and the street corner beside it.
  *
  * A branch library is the one kind of civic building that is deliberately unassertive, so it does
  * what no other volume in this world can -- it gives the north end of the street a calm,
  * horizontal, well-kept mass to sit against. The frontage is read in three parts (a pier, a glazed
  * entrance bay set back 0.8 m, and three tall windows), everything at the top has a thickness, and
  * the entrance is a sequence: forecourt, ramp and steps, terrace, canopy, doors.
  */
object Library:

  private val X0        = 11.0
  private val X1        = 20.6
  private val Z0        = 50.6 // frontage line, facing -z
  private val Z1        = 57.6
  private val H1        = 3.4  // ground storey
  private val H2        = 2.9  // upper storey
  private val H         = H1 + H2
  private val Rec       = 0.8  // depth of the entrance recess
  private val Bay0      = 12.0 // the entrance bay
  private val Bay1      = 16.2
  private val Rise      = 0.30 // floor level above the forecourt paving
  private val VoidDepth = 2.2  // how far the entrance bay is hollow

  private val Fx0 = 9.6 // the forecourt, and the corner
  private val Fx1 = 22.2
  private val Fz0 = 46.9
  private val Fz1 = 50.7

  private object M:
    val wall        = Toon.cel(CelOptions(color = 0xf6f2e8, bands = 3, tint = 0x6f6790))
    val wallAlt     = Toon.cel(CelOptions(color = 0xe8e6de, bands = 3, tint = 0x6f6790))
    val band        = Toon.cel(CelOptions(color = 0xd6d2ca, bands = 3, tint = 0x6a6288))
    val wood        = Toon.cel(CelOptions(color = 0xd2bb98, bands = 3, tint = 0x6f5680))
    val woodDark    = Toon.cel(CelOptions(color = 0xa88a64, bands = 3, tint = 0x5c5680))
    val roof        = Toon.cel(CelOptions(color = Pal.roofSlate, bands = 3, tint = 0x514b70))
    val trim        = Toon.cel(CelOptions(color = Pal.trim, bands = 3, tint = 0x5c5680))
    val metal       = Toon.cel(CelOptions(color = Pal.metal, bands = 3, tint = 0x666090))
    val metalDark   = Toon.cel(CelOptions(color = Pal.metalDark, bands = 3, tint = 0x5c5680))
    val concrete    = Toon.cel(CelOptions(color = Pal.concrete, bands = 3, tint = 0x6f6790))
    val concreteMid = Toon.cel(CelOptions(color = Pal.concreteMid, bands = 3, tint = 0x6a6288))
    val glass = Toon.flat(
      FlatOptions(color = Pal.glass, transparent = true, opacity = 0.24, depthWrite = Some(false), cache = false)
    )

  /* ------------------------------------------------------------------ *
   * The canvases this district needs. Upstream keeps them in `textures.js`;
   * `Textures.scala` is owned by the traffic/matsuri lane at the moment, so the four
   * this file and `NorthBlock.scala` draw are transcribed here with the same helpers.
   * ------------------------------------------------------------------ */

  private val JpFont =
    "'Yu Gothic', 'Yu Gothic UI', 'Meiryo', 'MS Gothic', 'Hiragino Kaku Gothic ProN', sans-serif"

  private val canvases = mutable.HashMap.empty[String, CanvasTexture[dom.HTMLCanvasElement]]

  private def context2d(canvas: dom.HTMLCanvasElement): dom.CanvasRenderingContext2D =
    val context = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D | Null]
    if context == null then throw new IllegalStateException("sakura-crossing: the canvas has no 2D context")
    context

  private def make(
    width: Int,
    height: Int,
    draw: (dom.CanvasRenderingContext2D, Double, Double) => Unit
  ): CanvasTexture[dom.HTMLCanvasElement] =
    val canvas = dom.document.createElement("canvas").asInstanceOf[dom.HTMLCanvasElement]
    canvas.width = width
    canvas.height = height
    val context = context2d(canvas)
    context.imageSmoothingEnabled = true
    draw(context, width.toDouble, height.toDouble)
    val texture = new CanvasTexture(canvas)
    texture.colorSpace = SRGBColorSpace
    texture.anisotropy = 4.0
    ThreeInterop.markForUpload(texture)
    texture

  private def cached(
    key: String
  )(
    build: => CanvasTexture[dom.HTMLCanvasElement]
  ): CanvasTexture[dom.HTMLCanvasElement] =
    canvases.getOrElseUpdate(key, build)

  private def rule(c: dom.CanvasRenderingContext2D, x: Double, y: Double, w: Double, h: Double, color: String): Unit =
    c.fillStyle = color
    c.fillRect(x, y, w, h)

  private def fitText(
    c: dom.CanvasRenderingContext2D,
    text: String,
    maxW: Double,
    size: Double,
    weight: String
  ): Double =
    var fitted  = size
    var running = true
    while running do
      c.font = s"$weight ${fitted}px $JpFont"
      if c.measureText(text).width <= maxW then running = false
      else
        fitted -= 2.0
        if fitted <= 6.0 then running = false
    fitted

  private def centered(
    c: dom.CanvasRenderingContext2D,
    text: String,
    x: Double,
    y: Double,
    maxW: Double,
    size: Double,
    color: String,
    weight: String
  ): Double =
    val fitted = fitText(c, text, maxW, size, weight)
    c.fillStyle = color
    c.textAlign = "center"
    c.font = s"$weight ${fitted}px $JpFont"
    c.fillText(text, x, y)
    fitted

  private def libraryName(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("libraryName") {
      make(1024, 176, (c, w, h) =>
        c.fillStyle = "#f7f4ec"
        c.fillRect(0.0, 0.0, w, h)
        rule(c, 0.0, h - 12.0, w, 12.0, "#22736f")
        centered(c, "ひばり台 図書館", w * 0.42, h * 0.48, w * 0.66, 92.0, "#2f5b52", "bold")
        c.font = s"600 26px $JpFont"
        c.fillStyle = "#8a8b82"
        c.textAlign = "left"
        c.textBaseline = "middle"
        c.fillText("HIBARIDAI  LIBRARY", w * 0.74, h * 0.42)
        c.font = s"500 22px $JpFont"
        c.fillText("町立 第二分館", w * 0.74, h * 0.68)
      )
    }

  private def libraryHours(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("libraryHours") {
      make(384, 512, (c, w, h) =>
        c.fillStyle = "#fbfaf4"
        c.fillRect(0.0, 0.0, w, h)
        rule(c, 0.0, 0.0, w, 22.0, "#2f9c9a")
        centered(c, "開館時間", w / 2.0, 76.0, w - 60.0, 58.0, "#2f5b52", "bold")
        c.globalAlpha = 0.4
        rule(c, 40.0, 118.0, w - 80.0, 3.0, "#9aa39a")
        c.globalAlpha = 1.0
        val rows = Vector(("平日", "九時 - 十九時"), ("土日", "九時 - 十七時"), ("休館", "毎週月曜"))
        c.textBaseline = "middle"
        rows.zipWithIndex.foreach { case ((left, right), i) =>
          c.font = s"600 30px $JpFont"
          c.fillStyle = if i == 2 then "#a3531c" else "#4b4757"
          c.textAlign = "left"
          c.fillText(left, 34.0, 168.0 + i * 54.0)
          c.textAlign = "right"
          c.fillText(right, w - 34.0, 168.0 + i * 54.0)
        }
        c.globalAlpha = 0.4
        rule(c, 40.0, 344.0, w - 80.0, 3.0, "#9aa39a")
        c.globalAlpha = 1.0
        centered(c, "返却は ポストへ", w / 2.0, 392.0, w - 50.0, 30.0, "#4b4757", "600")
        centered(c, "館内では おしずかに", w / 2.0, 444.0, w - 50.0, 30.0, "#8a8696", "600")
      )
    }

  /** The library reading room, seen through glass: a run of tall shelving, a long table under a row
    * of lamps, and the pale rectangle of a window on the far wall. Nobody is in it.
    */
  private def libraryInterior(variant: Int): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"libraryInterior$variant") {
      make(512, 320, (c, w, h) =>
        val v = variant % 3
        c.fillStyle = Vector("#e6e2d4", "#e8e2d0", "#e0ded2")(v)
        c.fillRect(0.0, 0.0, w, h)
        rule(c, 0.0, 0.0, w, 34.0, "#f6efdc")
        (0 until 3).foreach(i => rule(c, 40.0 + i * 160.0, 8.0, 110.0, 16.0, "#fff2d4"))
        rule(c, 0.0, h - 66.0, w, 66.0, "#c4bcac")
        if v == 0 then
          (0 until 5).foreach { i =>
            val x = 18.0 + i * 100.0
            rule(c, x, 52.0, 86.0, h - 130.0, "#c0ab8c")
            (0 until 4).foreach { r =>
              rule(c, x + 5.0, 60.0 + r * 44.0, 76.0, 32.0,
                Vector("#b08f62", "#a89078", "#bda07c", "#9c8a72")(r % 4))
              c.fillStyle = "rgba(70,64,56,0.22)"
              (0 until 9).foreach(k => c.fillRect(x + 7.0 + k * 8.0, 60.0 + r * 44.0, 3.0, 32.0))
            }
            rule(c, x, 52.0, 86.0, 6.0, "#8a7558")
          }
          // the long table, its row of lamps, and the far window
          rule(c, 40.0, 236.0, 250.0, 18.0, "#b09a76")
          rule(c, 52.0, 254.0, 10.0, 60.0, "#a08a68")
          rule(c, 268.0, 254.0, 10.0, 60.0, "#a08a68")
          (0 until 4).foreach(i => rule(c, 60.0 + i * 60.0, 196.0, 34.0, 12.0, "#fff2d4"))
          rule(c, 320.0, 74.0, 150.0, 150.0, "#dfe6da")
          rule(c, 320.0, 74.0, 150.0, 8.0, "#b8c0b2")
          rule(c, 320.0, 216.0, 150.0, 8.0, "#b8c0b2")
        else if v == 1 then
          rule(c, 30.0, 120.0, w - 60.0, 12.0, "#cbb794")
          (0 until 6).foreach(i => rule(c, 44.0 + i * 74.0, 132.0, 58.0, h - 210.0, "#bfa884"))
          rule(c, 0.0, h - 130.0, w, 12.0, "#b8b0a0")
        else
          rule(c, 0.0, 70.0, 120.0, h - 170.0, "#d8d2c2")
          rule(c, 140.0, 90.0, 330.0, 150.0, "#e2dcc8")
          rule(c, 150.0, 100.0, 310.0, 130.0, "#dfe6da")
        end if
      )
    }

  /** The community library, and the street corner beside it. */
  def buildLibrary(ctx: WorldCtx): Planting =
    val gm     = Ground.groundMats()
    val sakura = mutable.ArrayBuffer.empty[SakuraSpot]
    val shrubs = mutable.ArrayBuffer.empty[ShrubSpot]
    val grove  = mutable.ArrayBuffer.empty[GroveSpot]
    val petals = mutable.ArrayBuffer.empty[PetalPatch]

    val y  = Street.groundY((Z0 + Z1) / 2.0)
    val fy = y + 0.07  // the forecourt's paved surface
    val fl = fy + Rise // the library floor

    /* the forecourt: paving out to the north lane, with the two existing cherries left standing in
     * it. A public forecourt reads as public because it is swept and has a kerb. */
    Ground.pad(ctx,
      Ground.PadOptions(
        x = (Fx0 + Fx1) / 2.0, z = (Fz0 + Fz1) / 2.0, w = Fx1 - Fx0, d = Fz1 - Fz0,
        y = y, h = 0.07, mat = Some(gm.concrete), name = "libraryForecourt"
      ))
    Ground.pad(ctx,
      Ground.PadOptions(
        x = (Fx0 + Fx1) / 2.0, z = Fz1 - 0.55, w = Fx1 - Fx0, d = 1.1,
        y = y + 0.005, h = 0.07, mat = Some(gm.stoneWarm), name = "libraryApron"
      ))

    buildBuilding(ctx, y, fl)
    buildEntrance(ctx, y, fy, fl)
    dressForecourt(ctx, y, fy, shrubs, petals)
    buildCorner(ctx)

    /* planting: one dark green mass off each end, to stop the cornice line running out into sky.
     * Neither is on the entrance axis. */
    grove += GroveSpot(x = 22.9, z = 53.4, y = Street.groundY(53.4), scale = 1.75, seed = 5510, spread = 1.15)
    grove += GroveSpot(x = 8.6, z = 53.8, y = Street.groundY(53.8), scale = 1.6, seed = 5511, spread = 1.1)
    sakura += SakuraSpot(x = 25.3, z = 53.2, y = Street.groundY(53.2), scale = 1.2, seed = 5512, lean = 0.1,
      leanDir = Some(2.2))
    (0 until 3).foreach { i =>
      shrubs += ShrubSpot(x = 8.2 + i * 1.5, z = 49.6, y = y, r = 0.5, count = 3, spread = 1.2, seed = 5520 + i)
    }
    Planting(sakura = sakura.toVector, shrubs = shrubs.toVector, grove = grove.toVector, petals = petals.toVector)
  end buildLibrary

  /* ------------------------------------------------------------------ *
   * The building.
   * ------------------------------------------------------------------ */

  private def buildBuilding(ctx: WorldCtx, y: Double, fl: Double): Unit =
    val g = new Group()
    g.name = "library"
    ctx.add(g)

    val w  = X1 - X0
    val d  = Z1 - Z0
    val cx = (X0 + X1) / 2.0
    val cz = (Z0 + Z1) / 2.0
    val parts = Vector("wall", "alt", "band", "wood", "woodDark", "roof", "trim", "metal", "metalDark")
      .map(name => (name, mutable.ArrayBuffer.empty[Util.BakePart]))
      .toMap
    def push(kind: String, geometry: BufferGeometry[?, ?], matrix: Matrix4): Unit =
      parts(kind) += Util.BakePart(geometry, Some(matrix))

    /* the mass: three volumes on the ground floor, because the entrance bay is set back and hollow
     * behind its glazing. The upper storey spans the lot, carried over the recess on a header. */
    push("band", new BoxGeometry(w + 0.24, 0.34, d + 0.24), Util.trs(cx, y + 0.17, cz))
    push("wall", new BoxGeometry(Bay0 - X0, H1, d), Util.trs((X0 + Bay0) / 2.0, y + H1 / 2.0, cz))
    push("wall", new BoxGeometry(X1 - Bay1, H1, d), Util.trs((Bay1 + X1) / 2.0, y + H1 / 2.0, cz))
    val bz = Z0 + Rec       // the glazing line
    val vz = bz + VoidDepth // the back of the void
    push("wall", new BoxGeometry(Bay1 - Bay0, H1, Z1 - vz), Util.trs((Bay0 + Bay1) / 2.0, y + H1 / 2.0, (vz + Z1) / 2.0))
    Vector((Bay0, Bay0 + 0.3), (Bay1 - 0.3, Bay1)).foreach { (xa, xb) =>
      push("wall", new BoxGeometry(xb - xa, H1, VoidDepth), Util.trs((xa + xb) / 2.0, y + H1 / 2.0, (bz + vz) / 2.0))
    }
    /* The header over the glazing and the soffit of the recess, both in pale wood rather than
     * render: the sun sits at (-52, 62, 56), so this frontage faces away from it and everything on
     * it is lit by the cool bounce alone. A warm-toned surround, a warm soffit and a lit name box
     * give the entrance the only three warm surfaces on the elevation. */
    push("wood", new BoxGeometry(Bay1 - Bay0, H1 - 2.92, Rec + VoidDepth),
      Util.trs((Bay0 + Bay1) / 2.0, y + H1 - (H1 - 2.92) / 2.0, (Z0 + vz) / 2.0))
    push("wood", new BoxGeometry(Bay1 - Bay0 + 0.02, 0.08, Rec), Util.trs((Bay0 + Bay1) / 2.0, y + 2.88, Z0 + Rec / 2.0))
    Vector((Bay0, Bay0 + 0.16), (Bay1 - 0.16, Bay1)).foreach { (xa, xb) =>
      push("wood", new BoxGeometry(xb - xa, 2.9, Rec), Util.trs((xa + xb) / 2.0, y + 1.45, Z0 + Rec / 2.0 - 0.01))
    }
    ThreeInterop.addChild(g,
      Util.box(Bay1 - Bay0 - 1.4, 0.05, 0.26, Toon.flat(FlatOptions(color = 0xfff1d4)),
        (Bay0 + Bay1) / 2.0, y + 2.82, Z0 + 0.3))

    /* the recess floor, and the vestibule floor behind the glazing */
    push("band", new BoxGeometry(Bay1 - Bay0, 0.1, Rec + VoidDepth + 0.1),
      Util.trs((Bay0 + Bay1) / 2.0, fl - 0.05, (Z0 + vz) / 2.0 + 0.05))
    ctx.platform(Bay0 + 0.05, Bay1 - 0.05, Z0 - 0.1, bz + 0.05, fl)

    /* the string course at first-floor level, the upper storey, and the cornice */
    push("band", new BoxGeometry(w + 0.16, 0.16, d + 0.16), Util.trs(cx, y + H1, cz))
    push("wall", new BoxGeometry(w, H2, d), Util.trs(cx, y + H1 + H2 / 2.0, cz))
    push("wood", new BoxGeometry(w + 0.06, 0.62, d + 0.06), Util.trs(cx, y + H1 + 0.42, cz))
    push("band", new BoxGeometry(w + 0.5, 0.28, d + 0.5), Util.trs(cx, y + H + 0.14, cz))
    /* the parapet: four runs and a cap. The cap does not cast -- a 60 mm overhang is two texels of
     * this cascade and lands as sawtooth. */
    Vector(-1.0, 1.0).foreach { s =>
      push("wall", new BoxGeometry(w + 0.3, 0.45, 0.18), Util.trs(cx, y + H + 0.51, cz + s * ((d + 0.3) / 2.0 - 0.09)))
      push("wall", new BoxGeometry(0.18, 0.45, d + 0.3), Util.trs(cx + s * ((w + 0.3) / 2.0 - 0.09), y + H + 0.51, cz))
    }
    {
      val caps = mutable.ArrayBuffer.empty[Util.BakePart]
      Vector(-1.0, 1.0).foreach { s =>
        caps += Util.BakePart(new BoxGeometry(w + 0.42, 0.07, 0.3),
          Some(Util.trs(cx, y + H + 0.76, cz + s * ((d + 0.3) / 2.0 - 0.09))))
        caps += Util.BakePart(new BoxGeometry(0.3, 0.07, d + 0.42),
          Some(Util.trs(cx + s * ((w + 0.3) / 2.0 - 0.09), y + H + 0.76, cz)))
      }
      val cm = new Mesh(Util.bake(caps.toList), M.band)
      cm.castShadow = false
      cm.receiveShadow = true
      ThreeInterop.addChild(g, cm)
    }
    push("roof", new BoxGeometry(w + 0.2, 0.12, d + 0.2), Util.trs(cx, y + H + 0.34, cz))

    /* rooftop plant: two condensers, a vent and the ladder hoop */
    Vector(-1.9, -0.6).foreach { dx =>
      push("trim", new BoxGeometry(1.0, 0.7, 0.44), Util.trs(cx + dx, y + H + 0.75, cz + 1.6))
      push("metal", new BoxGeometry(1.04, 0.05, 0.5), Util.trs(cx + dx, y + H + 1.12, cz + 1.6))
    }
    push("metal", new CylinderGeometry(0.16, 0.16, 0.9, 10.0), Util.trs(cx + 2.4, y + H + 0.85, cz + 1.2))
    push("metal", new CylinderGeometry(0.2, 0.2, 0.08, 10.0), Util.trs(cx + 2.4, y + H + 1.32, cz + 1.2))
    (0 until 4).foreach { i =>
      push("metalDark", new BoxGeometry(0.04, 0.04, 0.36), Util.trs(X1 - 0.5, y + H1 + 0.6 + i * 0.6, Z1 + 0.16))
    }

    /* One helper, four layers: a recessed reveal, the pane, the mullion and transom in front of it,
     * and a sill that projects. `n` is the outward normal, so the same helper serves the frontage
     * and both flanks. */
    def win(
      x: Double,
      y0: Double,
      z: Double,
      ww: Double,
      hh: Double,
      n: (Double, Double) = (0.0, -1.0),
      interior: Int,
      curtain: Boolean = false,
      transom: Boolean = true,
      mull: Int = 1
    ): Unit =
      val (nx, nz)                             = n
      val along                                = nx != 0.0 // the opening runs along z
      def at(offset: Double): (Double, Double) = (x + nx * offset, z + nz * offset)
      def bx(t: Double, boxW: Double, boxH: Double): BoxGeometry =
        if along then new BoxGeometry(t, boxH, boxW) else new BoxGeometry(boxW, boxH, t)
      val ry = math.atan2(nx, nz)

      /* The reveal is set 20 mm *into* the wall, so its front face lands at at(0.05) and everything
       * after it stacks in front of that. */
      {
        val (px, pz) = at(-0.02)
        push("trim", bx(0.14, ww + 0.16, hh + 0.16), Util.trs(px, y0, pz))
      }
      {
        val (px, pz) = at(0.055)
        val inner = new Mesh(new PlaneGeometry(ww, hh),
          Toon.flat(FlatOptions(color = 0x9a94a4, map = Some(libraryInterior(interior)), cache = false)))
        inner.position.set(px, y0, pz)
        inner.rotation.y = ry
        ThreeInterop.setUserDataFlag(inner, "noOutline")
        ThreeInterop.addChild(g, inner)
        if curtain then
          val cur = new Mesh(new PlaneGeometry(ww * 0.42, hh * 0.92),
            Toon.flat(FlatOptions(color = 0xeee8da, map = Some(Textures.curtainTex(1)), cache = false)))
          val (cx2, cz2) = at(0.062)
          cur.position.set(cx2 + (if along then 0.0 else ww * 0.26), y0, cz2 + (if along then ww * 0.26 else 0.0))
          cur.rotation.y = ry
          ThreeInterop.setUserDataFlag(cur, "noOutline")
          ThreeInterop.addChild(g, cur)
      }
      {
        val (px, pz) = at(0.085)
        val pane     = new Mesh(bx(0.04, ww, hh), M.glass)
        pane.position.set(px, y0, pz)
        ThreeInterop.setUserDataFlag(pane, "noOutline")
        ThreeInterop.setUserDataFlag(pane, "noShadow")
        ThreeInterop.addChild(g, pane)
      }
      {
        val (px, pz) = at(0.1)
        (1 to mull).foreach { i =>
          val t = -ww / 2.0 + (ww * i) / (mull + 1.0)
          push("metal", bx(0.09, 0.06, hh), Util.trs(px + (if along then 0.0 else t), y0, pz + (if along then t else 0.0)))
        }
        if transom then push("metal", bx(0.09, ww, 0.07), Util.trs(px, y0 + hh * 0.22, pz))
        push("metal", bx(0.1, ww + 0.12, 0.08), Util.trs(px, y0 + hh / 2.0 + 0.04, pz))
      }
      {
        val (px, pz) = at(0.11)
        push("band", bx(0.24, ww + 0.24, 0.1), Util.trs(px, y0 - hh / 2.0 - 0.1, pz))
      }
    end win

    // ground floor, east run: three tall windows on a 1.42 m pier rhythm
    (0 until 3).foreach { i =>
      win(Bay1 + 0.95 + i * 1.28, y + 1.95, Z0, 1.0, 2.1, interior = (if i == 1 then 1 else 0), mull = 1, transom = true)
    }
    // and one on the west pier's flank, which is what the west garden sees
    win(X0, y + 1.95, Z0 + 2.6, 1.0, 2.0, n = (-1.0, 0.0), interior = 0, mull = 1)

    /* upper floor: five windows, evenly spaced, all the same -- a regular rhythm up here is exactly
     * right, because the ground floor is where the hierarchy lives */
    (0 until 5).foreach { i =>
      win(X0 + (w * (i + 1)) / 6.0, y + H1 + 1.72, Z0, 1.34, 1.72,
        interior = (if i % 2 == 0 then 1 else 0), curtain = (i == 1 || i == 4), mull = 1)
    }
    Vector(Z0 + 2.2, Z0 + 4.8).foreach { zc =>
      win(X1, y + H1 + 1.72, zc, 1.3, 1.7, n = (1.0, 0.0), interior = 0)
      win(X0, y + H1 + 1.72, zc, 1.3, 1.7, n = (-1.0, 0.0), interior = 1)
    }

    /* the glazed front: four sliding leaves and their frame, at the back of the recess. The two
     * middle ones are the doors -- they get their own thicker stiles and a pull. */
    {
      val ow = Bay1 - Bay0 - 0.6
      val ox = (Bay0 + Bay1) / 2.0
      val oh = 2.42
      val oy = fl + oh / 2.0
      push("metal", new BoxGeometry(ow + 0.16, 0.12, 0.16), Util.trs(ox, fl + oh + 0.06, bz))
      push("metal", new BoxGeometry(ow + 0.16, 0.1, 0.16), Util.trs(ox, fl + 0.02, bz))
      (0 to 4).foreach { i =>
        val t     = -ow / 2.0 + (ow / 4.0) * i
        val thick = if i == 1 || i == 3 then 0.1 else 0.06
        push("metal", new BoxGeometry(thick, oh, 0.14), Util.trs(ox + t, oy, bz))
      }
      val pane = Util.box(ow, oh, 0.04, M.glass, ox, oy, bz + 0.02)
      ThreeInterop.setUserDataFlag(pane, "noOutline")
      ThreeInterop.setUserDataFlag(pane, "noShadow")
      ThreeInterop.addChild(g, pane)
      Vector(-1.0, 1.0).foreach { s =>
        push("metalDark", new CylinderGeometry(0.018, 0.018, 0.9, 6.0),
          Util.trs(ox + s * (ow / 8.0 + 0.06), fl + 1.15, bz - 0.09))
        Vector(0.72, 1.58).foreach { dy =>
          push("metalDark", new BoxGeometry(0.05, 0.035, 0.08), Util.trs(ox + s * (ow / 8.0 + 0.06), fl + dy, bz - 0.06))
        }
      }
      // one angled highlight, the way glass is painted
      val hi = new Mesh(new PlaneGeometry(ow * 0.22, oh * 1.1),
        Toon.flat(FlatOptions(color = 0xf4f9ff, transparent = true, opacity = 0.16, depthWrite = Some(false),
          cache = false)))
      hi.position.set(ox - ow * 0.24, oy, bz - 0.05)
      hi.rotation.z = 0.28
      ThreeInterop.setUserDataFlag(hi, "noOutline")
      ThreeInterop.addChild(g, hi)
    }

    /* the vestibule: the one part of the building the player stands close enough to read, so it
     * gets the four things a library entrance actually has. */
    {
      val bx = (Bay0 + Bay1) / 2.0
      // turned to face out of the void: `flat()` is single-sided and the viewer is at -z
      val inner = new Mesh(new PlaneGeometry(Bay1 - Bay0 - 0.6, 2.5),
        Toon.flat(FlatOptions(color = 0xa8a2ae, map = Some(libraryInterior(2)), cache = false)))
      inner.position.set(bx, fl + 1.3, vz - 0.03)
      inner.rotation.y = math.Pi
      ThreeInterop.setUserDataFlag(inner, "noOutline")
      ThreeInterop.addChild(g, inner)
      push("band", new BoxGeometry(Bay1 - Bay0 - 0.6, 0.08, VoidDepth - 0.1),
        Util.trs(bx, fl + 2.6, bz + VoidDepth / 2.0))
      ThreeInterop.addChild(g,
        Util.box(1.5, 0.04, 0.34, Toon.flat(FlatOptions(color = 0xfff4e0)), bx, fl + 2.54, bz + 0.9))
      push("wood", new BoxGeometry(2.4, 1.02, 0.5), Util.trs(bx + 0.5, fl + 0.51, vz - 0.4))
      push("band", new BoxGeometry(2.56, 0.07, 0.62), Util.trs(bx + 0.5, fl + 1.05, vz - 0.4))
      ThreeInterop.addChild(g,
        Util.box(0.3, 0.22, 0.24,
          Toon.cel(CelOptions(color = 0xe8e2d2, bands = 3,
            tint = 0x6f6790)),
          bx + 1.3, fl + 1.2, vz - 0.42))
      Vector(-1.0, 1.0).foreach { s =>
        val sx2 = bx + s * ((Bay1 - Bay0) / 2.0 - 0.5)
        push("woodDark", new BoxGeometry(0.36, 1.9, 1.5), Util.trs(sx2, fl + 0.95, bz + 1.0))
        (0 until 4).foreach { r =>
          push("wood", new BoxGeometry(0.4, 0.05, 1.5), Util.trs(sx2, fl + 0.34 + r * 0.44, bz + 1.0))
          ThreeInterop.addChild(g,
            Util.box(0.3, 0.32, 1.34,
              Toon.cel(CelOptions(color = Vector(0xd8cdb4, 0xc9b89c, 0xdcd2b8)(r % 3), bands = 3, tint = 0x6f6790)),
              sx2, fl + 0.53 + r * 0.44, bz + 1.0))
        }
      }
      // the trolley: a two-tier frame with a leaning stack on it
      val tx = bx - 1.3
      push("metal", new BoxGeometry(0.7, 0.04, 0.42), Util.trs(tx, fl + 0.32, bz + 0.55))
      push("metal", new BoxGeometry(0.7, 0.04, 0.42), Util.trs(tx, fl + 0.66, bz + 0.55))
      Vector(-1.0, 1.0).foreach { sx2 =>
        push("metal", new BoxGeometry(0.04, 0.72, 0.04), Util.trs(tx + sx2 * 0.32, fl + 0.36, bz + 0.55))
      }
      val stack = Util.box(0.44, 0.24, 0.3, Toon.cel(CelOptions(color = 0xc9b89c, bands = 3, tint = 0x6f6790)),
        tx, fl + 0.8, bz + 0.55)
      stack.rotation.z = 0.09
      ThreeInterop.addChild(g, stack)
    }

    /* the name: an internally-lit box rather than a painted board -- a dark case, a warm face and a
     * rim. `flat()` is unlit, so the whole effect is the case around it. */
    {
      val bw   = 4.0
      val bx   = (Bay0 + Bay1) / 2.0
      val bz2  = Z0 - 0.13
      val side = Toon.flat(FlatOptions(color = 0x3c3947))
      val board = new Mesh(new BoxGeometry(bw, 0.62, 0.24),
        Props.matArray(side, side, Toon.flat(FlatOptions(color = 0x4a4653)), side,
          Toon.flat(FlatOptions(color = 0xfff0d2, map = Some(libraryName()), cache = false)), side))
      board.position.set(bx, y + 3.18, bz2)
      board.castShadow = true
      ThreeInterop.addChild(g, board)
      Outline.hullOutline(board, OutlineOptions(thickness = 0.003))
      push("metalDark", new BoxGeometry(bw + 0.14, 0.82, 0.18), Util.trs(bx, y + 3.18, bz2 + 0.05))
      Vector(-1.0, 1.0).foreach { s =>
        push("metalDark", new BoxGeometry(0.2, 0.07, 0.42), Util.trs(bx + s * 1.5, y + 3.66, bz2 - 0.06))
        ThreeInterop.addChild(g,
          Util.box(0.16, 0.04, 0.16, Toon.flat(FlatOptions(color = 0xfff3d8)),
            bx + s * 1.5, y + 3.62, bz2 - 0.24))
      }
    }

    /* merge and collide */
    val matFor = Map(
      "wall" -> M.wall, "alt"  -> M.wallAlt, "band" -> M.band, "wood"       -> M.wood, "woodDark" -> M.woodDark,
      "roof" -> M.roof, "trim" -> M.trim, "metal"   -> M.metal, "metalDark" -> M.metalDark
    )
    parts.foreach { (key, baked) =>
      if baked.nonEmpty then
        val mesh = new Mesh(Util.bake(baked.toList), matFor(key))
        mesh.castShadow = true
        mesh.receiveShadow = true
        ThreeInterop.addChild(g, mesh)
        if key == "wall" || key == "roof" then Outline.hullOutline(mesh, OutlineOptions(thickness = 0.0032))
    }
    /* Three colliders, not one: the entrance recess has to stay walkable, so the bay's own box
     * starts at the glazing line rather than at the frontage. */
    ctx.collide(X0 - 0.1, Z0 - 0.1, Bay0, Z1 + 0.1, y + H)
    ctx.collide(Bay1, Z0 - 0.1, X1 + 0.1, Z1 + 0.1, y + H)
    ctx.collide(Bay0, Z0 + Rec - 0.05, Bay1, Z1 + 0.1, y + H)
  end buildBuilding

  /* ------------------------------------------------------------------ *
   * The way in: terrace, steps, ramp, canopy.
   * ------------------------------------------------------------------ */

  private def buildEntrance(ctx: WorldCtx, y: Double, fy: Double, fl: Double): Unit =
    val gm = Ground.groundMats()
    val g  = new Group()
    g.name = "libraryEntrance"
    ctx.add(g)

    /* The terrace: a step up in front of the recess, running east past the bay so the ramp has
     * somewhere to land. It is as deep as the ramp is wide, and that is the whole of the geometry
     * here -- a shallower terrace and the two kerb lines jog sideways in the middle of the picture.
     */
    val rampW = 1.3
    val rampZ = 49.9        // the ramp's centreline
    val tx0   = 11.7
    val tx1   = 17.8
    val tz1   = Z0 + 0.225  // 50.825: it runs on into the recess
    val tz0   = tz1 - rampW // 49.525
    Ground.pad(ctx,
      Ground.PadOptions(
        x = (tx0 + tx1) / 2.0, z = (tz0 + tz1) / 2.0, w = tx1 - tx0, d = tz1 - tz0,
        y = fl - 0.1, h = 0.1, mat = Some(gm.stoneWarm), name = "libraryTerrace"
      ))
    /* Its riser stands 55 mm proud of the slab: flush, the two share a face over the slab's whole
     * thickness and two coplanar faces in different materials is a coin toss the render loses. */
    ThreeInterop.addChild(g, Util.box(tx1 - tx0, Rise, 0.12, M.band, (tx0 + tx1) / 2.0, fy + Rise / 2.0, tz0 + 0.005))

    /* the steps: the flight matches too, `rampW` wide on the doormat's own axis, and its top tread
     * overlaps the terrace's platform by 0.05 m -- two platforms that merely meet are a hole. */
    val stepW   = rampW
    val stepX   = (Bay0 + Bay1) / 2.0        // 14.1 -- the doormat's own axis
    val stepRun = 0.44
    val stepZ   = tz0 - 2.0 * stepRun + 0.05 // 48.695
    Ground.steps(ctx,
      Ground.StepsOptions(
        x = stepX, z = stepZ, axis = "z", dir = 1, n = 2, rise = Rise / 2.0, run = stepRun,
        w = stepW, y = fy, mat = Some(gm.stoneWarm), lipMat = Some(gm.concreteMid)
      ))
    Vector(-1.0, 1.0).foreach { s =>
      val c = Util.box(0.16, Rise + 0.06, 0.94, M.band, stepX + s * (stepW / 2.0 + 0.08),
        fy + (Rise + 0.06) / 2.0, stepZ + stepRun)
      c.castShadow = true
      c.receiveShadow = true
      ThreeInterop.addChild(g, c)
    }

    /* the ramp: 1 : 10.7 from the forecourt up to the terrace, running east to west along the
     * frontage rather than straight at it -- there is a street cherry standing in the paving on the
     * west side, and it was here first. The slab is one sloped box; the walking surface is a run of
     * seven platform boxes under it, because `heightAt` only understands flat tops. */
    {
      val rw   = rampW
      val rz   = rampZ
      val rx0  = 20.6      // the low end
      val rx1  = tx1 - 0.4 // the top, on the terrace
      val len  = rx0 - rx1
      val tilt = math.atan2(Rise, len)
      val slab = Util.box(len / math.cos(tilt), 0.12, rw, M.concrete, (rx0 + rx1) / 2.0, fy + Rise / 2.0 - 0.02, rz)
      slab.rotation.z = -tilt
      slab.castShadow = true
      slab.receiveShadow = true
      ThreeInterop.addChild(g, slab)
      val n = 7
      (0 until n).foreach { i =>
        val xa = rx0 - (len * i) / n
        val xb = rx0 - (len * (i + 1)) / n
        ctx.platform(xb - 0.12, xa + 0.12, rz - rw / 2.0, rz + rw / 2.0, fy + (Rise * (i + 0.85)) / n)
      }
      // the kerb along its outer edge, and the pipe handrail over that
      val kerb = Util.box(len / math.cos(tilt), 0.2, 0.14, M.band, (rx0 + rx1) / 2.0, fy + Rise / 2.0 + 0.06,
        rz - rw / 2.0 - 0.07)
      kerb.rotation.z = -tilt
      kerb.castShadow = true
      ThreeInterop.addChild(g, kerb)
      {
        val railParts = mutable.ArrayBuffer.empty[Util.BakePart]
        val rh        = 0.88
        (0 to 4).foreach { i =>
          val t = i / 4.0
          railParts += Util.BakePart(new CylinderGeometry(0.026, 0.03, rh, 6.0),
            Some(Util.trs(rx0 - len * t, fy + Rise * t + rh / 2.0, rz - rw / 2.0 - 0.07)))
        }
        Vector(rh - 0.02, rh * 0.52).foreach { dy =>
          railParts += Util.BakePart(new CylinderGeometry(0.028, 0.028, math.hypot(len, Rise) + 0.1, 6.0),
            Some(Util.trs((rx0 + rx1) / 2.0, fy + Rise / 2.0 + dy, rz - rw / 2.0 - 0.07, 0.0, 0.0,
              math.Pi / 2.0 - tilt)))
        }
        val rm = new Mesh(Util.bake(railParts.toList), M.metal)
        rm.castShadow = true
        ThreeInterop.addChild(g, rm)
        ctx.collide(rx1, rz - rw / 2.0 - 0.16, rx0, rz - rw / 2.0 + 0.02, fy + Rise + rh)
      }
    }

    /* the canopy: a flat slab on two slender columns, with a fascia and a downpipe. Its shadow
     * across the terrace is most of why the entrance reads as an entrance in a still frame. */
    {
      val cw   = Bay1 - Bay0 + 0.5
      val cxm  = (Bay0 + Bay1) / 2.0
      val cz0  = 49.3
      val slab = Util.box(cw, 0.14, Z0 - cz0 + 0.1, M.band, cxm, y + 2.86, (cz0 + Z0) / 2.0)
      slab.castShadow = true
      slab.receiveShadow = true
      ThreeInterop.addChild(g, slab)
      Outline.hullOutline(slab, OutlineOptions(thickness = 0.003))
      ThreeInterop.addChild(g, Util.box(cw - 0.16, 0.04, Z0 - cz0 - 0.06, M.wood, cxm, y + 2.78, (cz0 + Z0) / 2.0))
      ThreeInterop.addChild(g, Util.box(cw + 0.06, 0.24, 0.1, M.wall, cxm, y + 2.82, cz0 - 0.02))
      Vector(-1.0, 1.0).foreach { s =>
        val col = Util.box(0.12, 2.8, 0.12, M.metal, cxm + s * (cw / 2.0 - 0.22), y + 1.4, cz0 + 0.14)
        col.castShadow = true
        ThreeInterop.addChild(g, col)
        ThreeInterop.addChild(g, Util.box(0.22, 0.1, 0.22, M.band, cxm + s * (cw / 2.0 - 0.22), y + 0.05, cz0 + 0.14))
        ctx.collide(cxm + s * (cw / 2.0 - 0.22) - 0.1, cz0 + 0.04, cxm + s * (cw / 2.0 - 0.22) + 0.1, cz0 + 0.24,
          y + 2.8)
      }
      // the downpipe off its west corner, and the gully it lands in
      ThreeInterop.addChild(g, Util.cyl(0.045, 0.045, 2.8, 6, M.metal, cxm - cw / 2.0 + 0.06, y + 1.4, cz0 + 0.06))
      ThreeInterop.addChild(g, Util.box(0.24, 0.05, 0.24, M.concreteMid, cxm - cw / 2.0 + 0.06, y + 0.055, cz0 + 0.06))
    }

    /* porch fittings. The refuse point is *not* in the porch: it goes where a library's actually
     * is, round the east flank past the end of the forecourt. */
    {
      val bx = (Bay0 + Bay1) / 2.0
      ctx.add(Props.makeDoormat(Props.DoormatOpts(x = bx, z = Z0 + Rec - 0.5, y = fl, w = 1.3, d = 0.7, color = 0x6e6a78)))
      ctx.add(
        Props.makeUmbrellaStand(Props.UmbrellaStandOpts(x = Bay0 + 0.62, z = Z0 + Rec - 0.3, y = fl, n = 4, seed = 5531))
      )
      ctx.add(Props.makeBins(Props.BinsOpts(x = 21.0, z = 51.5, y = ctx.groundAt(21.0, 51.5), ry = -math.Pi / 2.0)))
      // the hours plate beside the door, and a poster on the pier
      val hours = new Mesh(new PlaneGeometry(0.42, 0.56),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(libraryHours()), cache = false)))
      hours.position.set(Bay0 + 0.16, fl + 1.5, Z0 + Rec - 0.02)
      hours.rotation.y = math.Pi / 2.0
      ThreeInterop.setUserDataFlag(hours, "noOutline")
      ctx.add(hours)
      val posterMesh = new Mesh(new PlaneGeometry(0.4, 0.55),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.clubPoster(1)), cache = false)))
      posterMesh.position.set(Bay1 - 0.16, fl + 1.5, Z0 + Rec - 0.02)
      posterMesh.rotation.y = -math.Pi / 2.0
      ThreeInterop.setUserDataFlag(posterMesh, "noOutline")
      ctx.add(posterMesh)
    }
  end buildEntrance

  /* ------------------------------------------------------------------ *
   * The forecourt: the return post, the board, the bikes and the planting.
   * ------------------------------------------------------------------ */

  private def dressForecourt(
    ctx: WorldCtx,
    y: Double,
    fy: Double,
    shrubs: mutable.ArrayBuffer[ShrubSpot],
    petals: mutable.ArrayBuffer[PetalPatch]
  ): Unit =
    val gm = Ground.groundMats()

    /* The after-hours book drop, hard by the entrance where it belongs. Everything in this
     * forecourt faces -z, because that is the side everybody arrives from. */
    ctx.add(Props.makeReturnPost(Props.ReturnPostOpts(x = 11.35, z = 50.1, y = fy, ry = -0.1)))
    ctx.collide(11.0, 49.85, 11.7, 50.4, fy + 1.2)

    /* the notice board, turned into the forecourt's north-west corner. The three pinned sheets are
     * part of it upstream (`makeNoticeBoard`'s `sheets` option), so the board is built here rather
     * than through `Props`, whose version carries no sheets. */
    ctx.add(noticeBoard(
      x = 10.0, z = 49.8, y = fy, ry = 2.6, w = 2.0, h = 1.15, y0 = 0.85,
      sheets = Vector(
        NoticeSheet(map = Textures.poster(1), x = -0.62, y = 0.02, w = 0.42, h = 0.58, tilt = -0.02),
        NoticeSheet(map = Textures.clubPoster(0), x = -0.02, y = 0.04, w = 0.42, h = 0.58, tilt = 0.015),
        NoticeSheet(map = Textures.poster(3), x = 0.6, y = 0.0, w = 0.4, h = 0.55, tilt = 0.02)
      )
    ))
    ctx.collide(9.3, 49.15, 10.8, 50.5, fy + 2.1)

    /* bicycle parking against the west edge, marked out on the paving the way it always is. Four
     * stands, not eight: the point is that somebody rides here, not that a crowd does. */
    ctx.add(Props.makeBikeRack(Props.BikeRackOpts(x = 10.9, z = 47.7, y = fy, n = 4, spacing = 0.66, ry = 0.0, seed = 55)))
    {
      val bars = mutable.ArrayBuffer.empty[Util.BakePart]
      (0 to 4).foreach { i =>
        bars += Util.BakePart(new BoxGeometry(0.08, 0.02, 1.5), Some(Util.trs(10.9, 0.0, 46.72 + i * 0.66)))
      }
      val bm = new Mesh(Util.bake(bars.toList), gm.white)
      bm.position.y = fy + 0.012
      ThreeInterop.setUserDataFlag(bm, "noOutline")
      ctx.add(bm)
    }

    /* benches facing out into the forecourt, a lamp, and one long bed on the east edge so twelve
     * metres of paving has a green end to it */
    ctx.add(Props.makeBench(Props.BenchOpts(x = 14.0, z = 47.4, y = fy, ry = 0.0, len = 1.8)))
    ctx.add(Props.makeBench(Props.BenchOpts(x = 16.4, z = 47.4, y = fy, ry = 0.0, len = 1.8)))
    ctx.add(Props.makeFlowerBed(Props.FlowerBedOpts(x = 21.75, z = 48.6, w = 0.9, d = 3.4, y = fy, seed = 5540)))
    Vector((12.3, 50.2, 0.26), (17.0, 47.0, 0.22)).foreach { (px, pz, r) =>
      ctx.add(Props.makePlanter(Props.PlanterOpts(x = px, z = pz, y = fy, r = r, flower = true,
        seed = 5550 + math.round(px * 10).toInt, n = 5)))
    }

    /* the lamp standard: the forecourt's only light, and a vertical the flat frontage needs off to
     * one side */
    {
      val lx   = 18.7
      val lz   = 47.5
      val post = Util.cyl(0.055, 0.07, 4.0, 8, M.metalDark, lx, fy + 2.0, lz)
      post.castShadow = true
      ctx.add(post)
      Outline.hullOutline(post, OutlineOptions(thickness = 0.0032))
      ctx.add(Util.cyl(0.13, 0.16, 0.18, 8, M.concreteMid, lx, fy + 0.09, lz))
      ctx.add(Util.box(0.06, 0.06, 0.62, M.metalDark, lx, fy + 3.96, lz - 0.28))
      val shade = new Mesh(new ConeGeometry(0.28, 0.22, 12.0, 1.0, true), M.metal)
      shade.position.set(lx, fy + 3.86, lz - 0.56)
      ctx.add(shade)
      ctx.add(Util.box(0.22, 0.05, 0.22, Toon.flat(FlatOptions(color = 0xfff2d0)), lx, fy + 3.73, lz - 0.56))
      ctx.collide(lx - 0.18, lz - 0.18, lx + 0.18, lz + 0.18, fy + 4.0)
    }

    /* the tree pits: the two street cherries stand *in* the paving, and a trunk coming straight out
     * of a concrete slab is the one thing that gives away that the slab was laid afterwards */
    Vector((12.6, 48.0), (20.4, 48.4)).foreach { (tx, tz) =>
      val soil = Util.box(1.1, 0.06, 1.1, Toon.cel(CelOptions(color = 0x8f7a62, bands = 3, tint = 0x615a80)),
        tx, fy + 0.012, tz)
      soil.receiveShadow = true
      ctx.add(soil)
      val rim = mutable.ArrayBuffer.empty[Util.BakePart]
      Vector(-1.0, 1.0).foreach { s =>
        rim += Util.BakePart(new BoxGeometry(1.24, 0.1, 0.14), Some(Util.trs(tx, fy + 0.03, tz + s * 0.62)))
        rim += Util.BakePart(new BoxGeometry(0.14, 0.1, 1.24), Some(Util.trs(tx + s * 0.62, fy + 0.03, tz)))
      }
      ctx.add(new Mesh(Util.bake(rim.toList), gm.concreteMid))
    }

    ctx.add(Props.makeLoosePaper(propCtx(ctx),
      Vector(
        Props.PaperSpot(x = 18.2, z = 46.9, y = fy + 0.01, ry = 0.7),
        Props.PaperSpot(x = 13.2, z = 46.6, y = fy + 0.01, ry = -0.5)
      )))
    // z 48.1 and 2.4 deep, not 48.4 and 3.0: the terrace's front line came out to the ramp's at
    // 49.525, and the last 0.6 m of the old band is under its slab
    petals += PetalPatch(x = 15.6, z = 48.1, w = 11.0, d = 2.4, y = fy + 0.02, n = 130)
    shrubs += ShrubSpot(x = 22.0, z = 50.2, y = y, r = 0.5, count = 3, spread = 1.2, seed = 5560)
  end dressForecourt

  /* ------------------------------------------------------------------ *
   * The corner. The triangle of paving where the north lane meets the main road gets the three
   * things a Japanese street corner has: a public telephone box, a district guide board, and the
   * recycling box the residents' association keeps there.
   * ------------------------------------------------------------------ */

  private def buildCorner(ctx: WorldCtx): Unit =
    val gm = Ground.groundMats()
    val y  = Street.groundY(48.0)
    val fy = y + 0.07

    Ground.pad(ctx,
      Ground.PadOptions(x = 4.2, z = 48.1, w = 5.0, d = 2.6, y = y, h = 0.07,
        mat = Some(gm.concrete), name = "cornerPad"))
    // the gully at the kerb, which is what tells you which way the corner drains
    {
      val gully = Util.box(0.3, 0.05, 0.7, Toon.cel(CelOptions(color = Pal.drain, bands = 3, tint = 0x5d5878)),
        2.05, fy + 0.005, 47.6)
      gully.receiveShadow = true
      ctx.add(gully)
    }

    ctx.add(Props.makePhoneBooth(Props.PhoneBoothOpts(x = 3.5, z = 48.6, y = fy, ry = 0.34)))
    ctx.collide(2.85, 47.95, 4.15, 49.25, fy + 2.7)

    ctx.add(Props.makeGuideBoard(Props.GuideBoardOpts(x = 5.8, z = 48.9, y = fy, ry = math.Pi, w = 1.5, h = 1.05)))
    ctx.collide(4.9, 48.75, 6.7, 49.05, fy + 2.1)

    ctx.add(Props.makeRecycleBox(Props.RecycleBoxOpts(x = 6.3, z = 47.5, y = fy, ry = math.Pi - 0.31)))
    ctx.collide(5.85, 47.2, 6.75, 47.8, fy + 0.7)

    /* a short length of guardrail on the road edge, and the lamp above it */
    ctx.add(Props.makeGuardrail(Props.GuardrailOpts(x = 2.0, z = 48.4, y = fy, ry = math.Pi / 2.0, len = 3.4)))
    {
      val lx   = 6.9
      val lz   = 46.9
      val post = Util.cyl(0.05, 0.065, 3.6, 8, M.metalDark, lx, fy + 1.8, lz)
      post.castShadow = true
      ctx.add(post)
      ctx.add(Util.cyl(0.12, 0.15, 0.16, 8, M.concreteMid, lx, fy + 0.08, lz))
      ctx.add(Util.box(0.06, 0.06, 0.56, M.metalDark, lx, fy + 3.56, lz - 0.26))
      val shade = new Mesh(new ConeGeometry(0.26, 0.2, 12.0, 1.0, true), M.metal)
      shade.position.set(lx, fy + 3.48, lz - 0.5)
      ctx.add(shade)
      ctx.add(Util.box(0.2, 0.05, 0.2, Toon.flat(FlatOptions(color = 0xfff2d0)), lx, fy + 3.36, lz - 0.5))
      ctx.collide(lx - 0.16, lz - 0.16, lx + 0.16, lz + 0.16, fy + 3.6)
    }
    ctx.add(Props.makePlanter(Props.PlanterOpts(x = 2.5, z = 47.2, y = fy, r = 0.24, flower = true, seed = 5570, n = 5)))
    ctx.add(Props.makePlanter(Props.PlanterOpts(x = 2.4, z = 49.6, y = fy, r = 0.2, flower = false, seed = 5571, n = 4)))
    ctx.add(Props.makeLoosePaper(propCtx(ctx), Vector(Props.PaperSpot(x = 5.0, z = 47.3, y = fy + 0.01, ry = 1.1))))
  end buildCorner

  /** A pinned sheet on the notice board. */
  final private case class NoticeSheet(
    map: Texture[?, ?],
    x: Double,
    y: Double,
    w: Double,
    h: Double,
    tilt: Double
  )

  /** Upstream `props.js` `makeNoticeBoard`: two posts, a cork panel with an ink shell, its frame and
    * rain hood, then the pinned sheets with a drawing pin each.
    */
  private def noticeBoard(
    x: Double,
    z: Double,
    y: Double,
    ry: Double,
    w: Double,
    h: Double,
    y0: Double,
    sheets: Vector[NoticeSheet]
  ): Group[Object3DEventMap] =
    val g       = new Group()
    val woodMat = Toon.cel(CelOptions(color = 0x8a6f52, bands = 3, tint = 0x5c5680))
    Vector(-1.0, 1.0).foreach { s =>
      val p = Util.box(0.11, y0 + h + 0.1, 0.11, woodMat, (s * (w - 0.2)) / 2.0, (y0 + h + 0.1) / 2.0, 0.0)
      p.castShadow = true
      ThreeInterop.addChild(g, p)
    }
    val panel = new Mesh(new BoxGeometry(w, h, 0.09),
      Props.matArray(woodMat, woodMat, woodMat, woodMat,
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(Textures.corkBoard()), cache = false)), woodMat))
    panel.position.set(0.0, y0 + h / 2.0, 0.0)
    panel.castShadow = true
    panel.receiveShadow = true
    ThreeInterop.addChild(g, panel)
    Outline.hullOutline(panel, OutlineOptions(thickness = 0.003))
    // frame and a shallow rain hood
    ThreeInterop.addChild(g, Util.box(w + 0.1, 0.09, 0.14, woodMat, 0.0, y0 + h + 0.04, 0.0))
    val hood = Util.box(w + 0.24, 0.06, 0.34,
      Toon.cel(CelOptions(color = Pal.roofSlate, bands = 3, tint = 0x514b70)), 0.0, y0 + h + 0.16, 0.1)
    hood.rotation.x = -0.2
    hood.castShadow = true
    ThreeInterop.addChild(g, hood)

    sheets.zipWithIndex.foreach { (sheet, i) =>
      val p = new Mesh(new PlaneGeometry(sheet.w, sheet.h),
        Toon.flat(FlatOptions(color = 0xffffff, map = Some(sheet.map), cache = false)))
      p.position.set(sheet.x, y0 + h / 2.0 + sheet.y, 0.052)
      p.rotation.z = sheet.tilt
      ThreeInterop.setUserDataFlag(p, "noOutline")
      ThreeInterop.addChild(g, p)
      // a drawing pin
      ThreeInterop.addChild(g,
        Util.box(0.03, 0.03, 0.02,
          Toon.flat(FlatOptions(color = (if i % 2 == 1 then Pal.red else Pal.yellow))),
          sheet.x, y0 + h / 2.0 + sheet.y + sheet.h / 2.0 - 0.04, 0.062))
    }
    g.position.set(x, y, z)
    g.rotation.y = ry
    g
  end noticeBoard

  private def propCtx(ctx: WorldCtx): PropCtx =
    PropCtx(
      add = obj => ctx.add(obj),
      collide = (x0, z0, x1, z1, top) => ctx.collide(x0, z0, x1, z1, top),
      groundAt = (px, pz) => ctx.groundAt(px, pz)
    )
end Library
