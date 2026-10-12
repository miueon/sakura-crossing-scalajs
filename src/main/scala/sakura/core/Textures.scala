package sakura.core

import org.scalajs.dom
import sakura.ThreeInterop
import vendor.three.*

import scala.collection.mutable
import scala.math
import scala.scalajs.js

/** 1:1 port of upstream `src/core/textures.js`: the whole procedural Canvas2D texture library
  * (helpers, cache, mirror flip, and every factory), whose observable texture metadata is pinned
  * by `TexturesParitySuite` against ground truth extracted from the upstream module.
  */
object Textures:

  enum RoadPaintKind(val key: String):
    case Stop    extends RoadPaintKind("stop")
    case Slow    extends RoadPaintKind("slow")
    case Go      extends RoadPaintKind("go")
    case Arrow   extends RoadPaintKind("arrow")
    case Diamond extends RoadPaintKind("diamond")

  enum ShopKind(val key: String):
    case Conbini  extends ShopKind("conbini")
    case Ramen    extends ShopKind("ramen")
    case Wagashi  extends ShopKind("wagashi")
    case Hana     extends ShopKind("hana")
    case Kosho    extends ShopKind("kosho")
    case Cleaning extends ShopKind("cleaning")
    case Bunbo    extends ShopKind("bunbo")
    case Bakery   extends ShopKind("bakery")
    case Sento    extends ShopKind("sento")
    case Sozai    extends ShopKind("sozai")
    case Record   extends ShopKind("record")
    case Denki    extends ShopKind("denki")
    case Kokuya   extends ShopKind("kokuya")
    case Clinic   extends ShopKind("clinic")
    case Yakkyoku extends ShopKind("yakkyoku")
    case Laundry  extends ShopKind("laundry")
    case Fudosan  extends ShopKind("fudosan")
    case Bungu    extends ShopKind("bungu")
    case Ringyo   extends ShopKind("ringyo")
    case Bento    extends ShopKind("bento")
    case Zakka    extends ShopKind("zakka")
  end ShopKind

  enum NorenKind(val key: String):
    case Ramen   extends NorenKind("ramen")
    case Sento   extends NorenKind("sento")
    case Wagashi extends NorenKind("wagashi")
    case Soba    extends NorenKind("soba")
    case Record  extends NorenKind("record")
    case Bento   extends NorenKind("bento")

  enum OnsenKind(val key: String):
    case Yunoya   extends OnsenKind("yunoya")
    case Hourai   extends OnsenKind("hourai")
    case Sakuraan extends OnsenKind("sakuraan")
    case Yunoka   extends OnsenKind("yunoka")
    case Kokeshi  extends OnsenKind("kokeshi")

  enum OnsenNorenKind(val key: String):
    case Male   extends OnsenNorenKind("male")
    case Female extends OnsenNorenKind("female")
    case Yunoya extends OnsenNorenKind("yunoya")
    case Kanmi  extends OnsenNorenKind("kanmi")
    case Kissa  extends OnsenNorenKind("kissa")

  /** The PAL hex constants of upstream `src/core/palette.js` that this library draws with. */
  private object Pal:
    val blue        = "#3d6ec4"
    val red         = "#e0453f"
    val purple      = "#8f6fb5"
    val leafDeep    = "#3f7f60"
    val blossomDeep = "#f0a3c0"
    val orange      = "#ef8a3c"
    val roofBrown   = "#6b585c"
    val teal        = "#2f9c9a"
    val tealDeep    = "#22736f"
    val yellow      = "#f4c033"
    val black       = "#322e3b"
    val redDeep     = "#b5322f"
    val blueDeep    = "#2a4f97"
    val tactile     = "#f2c53d"
    val norenRed    = "#b5322f"
    val noren       = "#2f4a72"
    val norenCream  = "#f2e8d6"

  private val JpFont =
    "'Yu Gothic', 'Yu Gothic UI', 'Meiryo', 'MS Gothic', 'Hiragino Kaku Gothic ProN', sans-serif"

  private val cache = mutable.HashMap.empty[String, CanvasTexture[dom.HTMLCanvasElement]]

  private val flipCache =
    mutable.HashMap.empty[CanvasTexture[dom.HTMLCanvasElement], CanvasTexture[dom.HTMLCanvasElement]]

  private def context2d(canvas: dom.HTMLCanvasElement): dom.CanvasRenderingContext2D =
    val context = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D | Null]
    if context == null then
      throw new IllegalStateException("sakura-crossing: the canvas has no 2D context")
    context

  private def make(
    width: Int,
    height: Int,
    draw: (dom.CanvasRenderingContext2D, Double, Double) => Unit,
    srgb: Boolean = true,
    repeat: Option[(Double, Double)] = None,
    anisotropy: Double = 4.0
  ): CanvasTexture[dom.HTMLCanvasElement] =
    val canvas = dom.document.createElement("canvas").asInstanceOf[dom.HTMLCanvasElement]
    canvas.width = width
    canvas.height = height
    val context = context2d(canvas)
    context.imageSmoothingEnabled = true
    draw(context, width.toDouble, height.toDouble)
    val texture = new CanvasTexture(canvas)
    if srgb then texture.colorSpace = SRGBColorSpace
    texture.anisotropy = anisotropy
    repeat.foreach { case (rx, ry) =>
      // The wrap field's union arms lower to Int literals while the exported constant lowers to a
      // Double literal, so the Int literal is the writable form (ProceduralTextures, same quirk).
      texture.wrapS = 1000
      texture.wrapT = 1000
      texture.repeat.set(rx, ry)
    }
    // The facade hides the needsUpdate setter; the public version counter drives the same upload.
    ThreeInterop.markForUpload(texture)
    texture
  end make

  private def cached(
    key: String,
    build: () => CanvasTexture[dom.HTMLCanvasElement]
  ): CanvasTexture[dom.HTMLCanvasElement] =
    cache.getOrElseUpdate(key, build())

  def mirrored(texture: CanvasTexture[dom.HTMLCanvasElement]): CanvasTexture[dom.HTMLCanvasElement] =
    flipCache.getOrElseUpdate(
      texture, {
        val flipped = texture.clone()
        flipped.wrapS = 1000
        flipped.repeat.x = -1.0
        flipped.offset.x = 1.0
        ThreeInterop.markForUpload(flipped)
        flipped
      }
    )

  /** JS `[...text]` spreads code points; split the same way so an astral glyph stays one glyph. */
  private def glyphs(text: String): List[String] =
    val parsed = List.newBuilder[String]
    var index  = 0
    while index < text.length do
      val high = text.charAt(index)
      val paired = index + 1 < text.length &&
        Character.isHighSurrogate(high) && Character.isLowSurrogate(text.charAt(index + 1))
      parsed += text.substring(index, if paired then index + 2 else index + 1)
      index += (if paired then 2 else 1)
    parsed.result()

  private def fitText(
    c: dom.CanvasRenderingContext2D,
    text: String,
    maxW: Double,
    size: Double,
    font: String,
    weight: String
  ): Double =
    var fitted  = size
    var running = true
    while running do
      c.font = s"$weight ${fitted}px $font"
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
    weight: String = "bold",
    spacing: Double = 0.0
  ): Double =
    val fitted = fitText(c, text, maxW, size, JpFont, weight)
    c.fillStyle = color
    c.textAlign = if spacing != 0.0 then "left" else "center"
    c.textBaseline = "middle"
    if spacing != 0.0 then
      val chars = glyphs(text)
      val total = chars.foldLeft(-spacing)((sum, ch) => sum + c.measureText(ch).width + spacing)
      var cx    = x - total / 2.0
      chars.foreach { ch =>
        c.fillText(ch, cx, y)
        cx += c.measureText(ch).width + spacing
      }
    else c.fillText(text, x, y)
    fitted
  end centered

  private def vertical(
    c: dom.CanvasRenderingContext2D,
    text: String,
    x: Double,
    y0: Double,
    step: Double,
    size: Double,
    color: String
  ): Unit =
    c.font = s"bold ${size}px $JpFont"
    c.fillStyle = color
    c.textAlign = "center"
    c.textBaseline = "middle"
    glyphs(text).zipWithIndex.foreach((ch, i) => c.fillText(ch, x, y0 + i * step))

  private def rule(
    c: dom.CanvasRenderingContext2D,
    x: Double,
    y: Double,
    w: Double,
    h: Double,
    color: String
  ): Unit =
    c.fillStyle = color
    c.fillRect(x, y, w, h)

  def shopSign(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("shopSign",
      () =>
        make(1024, 256, (c, w, h) =>
            c.fillStyle = "#fbfaf6"
            c.fillRect(0.0, 0.0, w, h)
            c.fillStyle = Pal.blue
            c.fillRect(0.0, h - 16.0, w, 16.0)
            c.fillRect(0.0, 0.0, w, 10.0)
            centered(c, "青空商店", w * 0.42, h * 0.5, w * 0.6, 150.0, "#20509e", "bold", 14.0)
            c.font = s"600 40px $JpFont"
            c.fillStyle = "#7f8798"
            c.textAlign = "left"
            c.textBaseline = "middle"
            c.fillText("AOZORA  STORE", w * 0.7, h * 0.42)
            c.font = s"500 30px $JpFont"
            c.fillStyle = "#a8adba"
            c.fillText("たばこ ・ 氷 ・ 弁当", w * 0.7, h * 0.68)
        ))

  def shopBanner(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("shopBanner",
      () =>
        make(192, 768, (c, w, h) =>
            c.fillStyle = Pal.red
            c.fillRect(0.0, 0.0, w, h)
            c.fillStyle = "#fdf6ec"
            c.fillRect(10.0, 10.0, w - 20.0, h - 20.0)
            c.fillStyle = Pal.red
            c.fillRect(10.0, 10.0, w - 20.0, 90.0)
            centered(c, "氷", w / 2.0, 55.0, w - 40.0, 70.0, "#fdf6ec")
            vertical(c, "つめたい", w / 2.0, 180.0, 96.0, 76.0, "#1e4f96")
            vertical(c, "のみもの", w / 2.0, 560.0, 96.0, 62.0, "#3c3a46")
        ))

  def poster(variant: Int): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"poster$variant",
      () =>
        make(320, 448, (c, w, h) =>
            val sets = Vector(
              ("#fdf7e8", Pal.red, "さくら祭", "四月五日"), ("#eef6fd", Pal.blue, "町内会", "そうじ当番"),
              ("#fdeef1", Pal.purple, "春の便り", "ひばり台"), ("#f4fbef", Pal.leafDeep, "野菜市", "毎週日曜")
            )
            val (bg, bar, title, sub) = sets(variant % sets.length)
            c.fillStyle = bg
            c.fillRect(0.0, 0.0, w, h)
            c.fillStyle = bar
            c.fillRect(0.0, 0.0, w, 26.0)
            c.fillRect(0.0, h - 20.0, w, 20.0)
            centered(c, title, w / 2.0, 120.0, w - 40.0, 96.0, bar, "bold", 6.0)
            centered(c, sub, w / 2.0, 222.0, w - 60.0, 52.0, "#4b4757")
            c.fillStyle = "#e9e3d8"
            c.fillRect(40.0, 270.0, w - 80.0, 120.0)
            c.fillStyle = Pal.blossomDeep
            (0 until 5).foreach { i =>
              c.beginPath()
              c.arc(70.0 + i * 45.0, 330.0 + (i % 2) * 22.0, 16.0, 0.0, math.Pi * 2.0)
              c.fill()
            }
        ))

  def shutterTex(rows: Int): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"shutter$rows",
      () =>
        make(64, 512, (c, w, h) =>
            val step = h / rows.toDouble
            (0 until rows).foreach { i =>
              c.fillStyle = if i % 2 == 1 then "#c9c4d2" else "#eae6ee"
              c.fillRect(0.0, i * step, w, step)
              c.fillStyle = "#9a94a6"
              c.fillRect(0.0, i * step + step - 2.5, w, 2.5)
            }
        ))

  def vendHeader(variant: Int): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"vendHeader$variant",
      () =>
        make(512, 160, (c, w, h) =>
            val sets = Vector(
              ("#ffffff", Pal.red, "そら茶"), ("#e0453f", "#ffffff", "ハレ水"), ("#2e9a98", "#ffffff", "なごみ")
            )
            val (bg, fg, title) = sets(variant % sets.length)
            c.fillStyle = bg
            c.fillRect(0.0, 0.0, w, h)
            centered(c, title, w * 0.36, h / 2.0, w * 0.5, 106.0, fg, "bold", 8.0)
            c.fillStyle = fg
            c.globalAlpha = 0.85
            c.beginPath()
            c.arc(w * 0.78, h / 2.0, 44.0, 0.0, math.Pi * 2.0)
            c.fill()
            c.globalAlpha = 1.0
            c.fillStyle = bg
            c.beginPath()
            c.arc(w * 0.78, h / 2.0, 26.0, 0.0, math.Pi * 2.0)
            c.fill()
        ))

  def vendPrice(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("vendPrice",
      () =>
        make(512, 96, (c, w, h) =>
            c.fillStyle = "#f6f3ee"
            c.fillRect(0.0, 0.0, w, h)
            val n = 6
            (0 until n).foreach { i =>
              val x = i.toDouble * w / n.toDouble
              c.fillStyle = if i % 3 == 0 then "#e0453f" else "#2f6fc4"
              c.fillRect(x + 6.0, 14.0, w / n.toDouble - 12.0, 44.0)
              c.fillStyle = "#ffffff"
              c.font = s"bold 30px $JpFont"
              c.textAlign = "center"
              c.textBaseline = "middle"
              c.fillText("150", x + w / n.toDouble / 2.0, 38.0)
              c.fillStyle = "#8b8696"
              c.fillRect(x + 18.0, 68.0, w / n.toDouble - 36.0, 8.0)
            }
        ))

  def vendCold(hot: Boolean): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"vendCold$hot",
      () =>
        make(256, 96, (c, w, h) =>
            c.fillStyle = if hot then "#d8453f" else "#2f6fc4"
            c.fillRect(0.0, 0.0, w, h)
            centered(c, if hot then "あたたかい" else "つめたい", w / 2.0, h / 2.0, w - 24.0, 58.0, "#ffffff",
              "bold", 2.0)
        ))

  def vendSlot(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("vendSlot",
      () =>
        make(256, 64, (c, w, h) =>
            c.fillStyle = "#33313c"
            c.fillRect(0.0, 0.0, w, h)
            c.fillStyle = "#4b4856"
            c.fillRect(0.0, 0.0, w, 8.0)
            centered(c, "取出口", w / 2.0, h * 0.32, w - 80.0, 22.0, "#cbc5bb")
        ))

  def crossingSign(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("crossingSign",
      () =>
        make(512, 256, (c, w, h) =>
            c.fillStyle = "#fbf8f2"
            c.fillRect(0.0, 0.0, w, h)
            c.strokeStyle = Pal.black
            c.lineWidth = 12.0
            c.strokeRect(6.0, 6.0, w - 12.0, h - 12.0)
            centered(c, "踏切注意", w / 2.0, h * 0.36, w - 60.0, 96.0, Pal.redDeep, "bold", 6.0)
            centered(c, "とまれ  みよ  きけ", w / 2.0, h * 0.74, w - 80.0, 46.0, Pal.black, "bold", 2.0)
        ))

  def stationSign(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("stationSign",
      () =>
        make(768, 192, (c, w, h) =>
            c.fillStyle = "#fbfaf6"
            c.fillRect(0.0, 0.0, w, h)
            c.fillStyle = Pal.teal
            c.fillRect(0.0, h - 22.0, w, 22.0)
            centered(c, "ひばり台", w / 2.0, h * 0.42, w * 0.7, 104.0, "#2b3346", "bold", 12.0)
            c.font = s"600 34px $JpFont"
            c.fillStyle = "#8a8fa0"
            c.textAlign = "center"
            c.fillText("HIBARIDAI", w / 2.0, h * 0.82)
        ))

  def warningPlate(variant: Int): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"warnPlate$variant",
      () =>
        make(256, 512, (c, w, h) =>
            val sets = Vector(
              (Pal.yellow, Pal.black, "防犯カメラ"), (Pal.red, "#fdf8f0", "立入禁止"), ("#fdf8f0", Pal.blueDeep, "徐行"),
              ("#fdf8f0", Pal.redDeep, "転回場")
            )
            val (bg, fg, title) = sets(variant % sets.length)
            c.fillStyle = bg
            c.fillRect(0.0, 0.0, w, h)
            c.fillStyle = fg
            c.fillRect(12.0, 12.0, w - 24.0, 6.0)
            c.fillRect(12.0, h - 18.0, w - 24.0, 6.0)
            vertical(c, title, w / 2.0, 90.0, 88.0, 74.0, fg)
        ))

  def trainDest(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("trainDest",
      () =>
        make(512, 128, (c, w, h) =>
            c.fillStyle = "#1d2230"
            c.fillRect(0.0, 0.0, w, h)
            c.fillStyle = "#f2e6b0"
            c.fillRect(10.0, 22.0, 110.0, 84.0)
            centered(c, "各停", 65.0, 64.0, 96.0, 56.0, "#1d2230")
            centered(c, "ひばり台", w * 0.62, h / 2.0, w * 0.55, 78.0, "#f2e6b0", "bold", 6.0)
        ))

  def trainNumber(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("trainNumber",
      () =>
        make(256, 96, (c, w, h) =>
            c.fillStyle = "#f7f2e6"
            c.fillRect(0.0, 0.0, w, h)
            centered(c, "クハ 2104", w / 2.0, h / 2.0, w - 20.0, 50.0, "#4a4657")
        ))

  def tactileTex(bars: Boolean): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"tactile$bars",
      () =>
        make(
          128,
          128,
          (c, w, h) =>
            c.fillStyle = Pal.tactile
            c.fillRect(0.0, 0.0, w, h)
            c.fillStyle = "#d9a91f"
            if bars then (0 until 4).foreach(i => c.fillRect(10.0, 12.0 + i * 30.0, w - 20.0, 16.0))
            else
              for
                y <- 0 until 4
                x <- 0 until 4
              do
                c.beginPath()
                c.arc(20.0 + x * 29.0, 20.0 + y * 29.0, 9.0, 0.0, math.Pi * 2.0)
                c.fill()
          ,
          repeat = Some((1.0, 1.0))
        )
    )

  def roadPaint(kind: RoadPaintKind): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"roadPaint${kind.key}",
      () =>
        make(512, 512, (c, w, h) =>
            c.clearRect(0.0, 0.0, w, h)
            c.fillStyle = "rgba(244, 242, 246, 0.94)"
            kind match
              case RoadPaintKind.Stop | RoadPaintKind.Slow =>
                c.save()
                c.translate(w / 2.0, h / 2.0)
                c.font = s"bold 168px $JpFont"
                c.textAlign = "center"
                c.textBaseline = "middle"
                if kind == RoadPaintKind.Slow then
                  c.fillText("徐", 0.0, -40.0)
                  c.fillText("行", 0.0, 130.0)
                else
                  c.fillText("止", 0.0, -95.0)
                  c.fillText("ま", 0.0, 75.0)
                  c.fillText("れ", 0.0, 235.0)
                c.restore()
              case RoadPaintKind.Go =>
                c.beginPath()
                c.moveTo(w / 2.0, 44.0)
                c.lineTo(w / 2.0 + 132.0, 236.0)
                c.lineTo(w / 2.0 + 54.0, 236.0)
                c.lineTo(w / 2.0 + 54.0, h - 44.0)
                c.lineTo(w / 2.0 - 54.0, h - 44.0)
                c.lineTo(w / 2.0 - 54.0, 236.0)
                c.lineTo(w / 2.0 - 132.0, 236.0)
                c.closePath()
                c.fill()
              case _ =>
                c.lineWidth = 40.0
                c.strokeStyle = "rgba(244, 242, 246, 0.94)"
                c.beginPath()
                c.moveTo(w / 2.0, 40.0)
                c.lineTo(w - 60.0, h / 2.0)
                c.lineTo(w / 2.0, h - 40.0)
                c.lineTo(60.0, h / 2.0)
                c.closePath()
                c.stroke()
            end match
        ))

  def drainTex(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("drainTex",
      () =>
        make(128, 128, (c, w, h) =>
            c.fillStyle = "#6d687a"
            c.fillRect(0.0, 0.0, w, h)
            c.fillStyle = "#8a849a"
            (0 until 7).foreach(i => c.fillRect(12.0, 10.0 + i * 16.0, w - 24.0, 8.0))
        ))

  def platePlate(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("platePlate",
      () =>
        make(256, 128, (c, w, h) =>
            c.fillStyle = "#f6f4f0"
            c.fillRect(0.0, 0.0, w, h)
            c.strokeStyle = "#4f5a72"
            c.lineWidth = 8.0
            c.strokeRect(8.0, 8.0, w - 16.0, h - 16.0)
            centered(c, "さ 21-08", w / 2.0, h / 2.0, w - 40.0, 56.0, "#2f3646")
        ))

  def noParking(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("noParking",
      () =>
        make(256, 256, (c, w, h) =>
            c.fillStyle = "#fbfaf6"
            c.fillRect(0.0, 0.0, w, h)
            c.strokeStyle = Pal.blue
            c.lineWidth = 22.0
            c.beginPath()
            c.arc(w / 2.0, h / 2.0, 96.0, 0.0, math.Pi * 2.0)
            c.stroke()
            c.strokeStyle = Pal.red
            c.lineWidth = 20.0
            c.beginPath()
            c.moveTo(48.0, 208.0)
            c.lineTo(208.0, 48.0)
            c.stroke()
        ))

  def petalTex(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("petalTex",
      () =>
        make(128, 128, (c, w, h) =>
            c.clearRect(0.0, 0.0, w, h)
            c.translate(w / 2.0, h / 2.0)
            c.fillStyle = "#ffffff"
            c.beginPath()
            c.moveTo(0.0, 52.0)
            c.bezierCurveTo(38.0, 34.0, 46.0, -14.0, 14.0, -48.0)
            c.bezierCurveTo(6.0, -38.0, 2.0, -34.0, 0.0, -30.0)
            c.bezierCurveTo(-2.0, -34.0, -6.0, -38.0, -14.0, -48.0)
            c.bezierCurveTo(-46.0, -14.0, -38.0, 34.0, 0.0, 52.0)
            c.closePath()
            c.fill()
          , srgb = false))

  def cloudTex(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("cloudTex",
      () =>
        make(512, 256, (c, w, h) =>
            c.clearRect(0.0, 0.0, w, h)
            val puffs = Vector(
              (0.22, 0.62, 0.15), (0.36, 0.46, 0.2), (0.52, 0.4, 0.24), (0.68, 0.5, 0.19), (0.82, 0.63, 0.14),
              (0.45, 0.66, 0.2), (0.6, 0.68, 0.17)
            )
            c.fillStyle = "#ffffff"
            puffs.foreach { (px, py, pr) =>
              c.beginPath()
              c.ellipse(px * w, py * h, pr * w * 0.55, pr * h * 1.1, 0.0, 0.0, math.Pi * 2.0)
              c.fill()
            }
            c.globalCompositeOperation = "destination-out"
            c.fillRect(0.0, h * 0.78, w, h * 0.22)
            c.globalCompositeOperation =
              "source-over"
          , srgb = false))

  def meterBox(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("meterBox",
      () =>
        make(256, 256, (c, w, h) =>
            c.fillStyle = "#eceaf0"
            c.fillRect(0.0, 0.0, w, h)
            c.fillStyle = "#cfccd6"
            c.fillRect(20.0, 20.0, w - 40.0, 90.0)
            c.fillStyle = "#3a3744"
            c.fillRect(40.0, 40.0, w - 80.0, 50.0)
            c.fillStyle = "#8b8696"
            c.fillRect(20.0, 140.0, w - 40.0, 12.0)
            c.fillRect(20.0, 172.0, w - 90.0, 12.0)
        ))

  def shrinePlate(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("shrinePlate",
      () =>
        make(128, 256, (c, w, h) =>
            c.fillStyle = "#e8e2ea"
            c.fillRect(0.0, 0.0, w, h)
            vertical(c, "地蔵", w / 2.0, 70.0, 70.0, 56.0, "#5a5468")
        ))

  def alleyPlate(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("alleyPlate",
      () =>
        make(512, 128, (c, w, h) =>
            c.fillStyle = "#fbfaf6"
            c.fillRect(0.0, 0.0, w, h)
            c.fillStyle = Pal.blossomDeep
            c.fillRect(0.0, h - 14.0, w, 14.0)
            centered(c, "さくら通り", w / 2.0, h * 0.45, w - 60.0, 72.0, "#4a4657", "bold", 6.0)
        ))

  def chainLinkTex(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("chainLink",
      () =>
        make(128, 128, (c, w, h) =>
            c.clearRect(0.0, 0.0, w, h)
            c.strokeStyle = "rgba(255,255,255,0.92)"
            c.lineWidth = 7.0
            c.lineCap = "square"
            (-1 to 2).foreach { i =>
              val diagonal = i.toDouble
              c.beginPath()
              c.moveTo(diagonal * w, 0.0)
              c.lineTo(diagonal * w + w, h)
              c.stroke()
              c.beginPath()
              c.moveTo(diagonal * w, h)
              c.lineTo(diagonal * w + w, 0.0)
              c.stroke()
            }
          , srgb = false))

  def corkBoard(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("corkBoard",
      () =>
        make(512, 256, (c, w, h) =>
            c.fillStyle = "#c3a279"
            c.fillRect(0.0, 0.0, w, h)
            c.fillStyle = "#b8946b"
            (0 until 90).foreach { i =>
              val x = (i * 137) % w.toInt
              val y = (i * 89)  % h.toInt
              c.fillRect(x.toDouble, y.toDouble, 7.0, 4.0)
            }
            c.strokeStyle = "#7d6348"
            c.lineWidth = 10.0
            c.strokeRect(5.0, 5.0, w - 10.0, h - 10.0)
        ))

  final private case class ShopStyle(
    bg: String,
    bar: String,
    fg: String,
    title: String,
    sub: String,
    latin: String
  )

  private val shops: Map[ShopKind, ShopStyle] = Map(
    ShopKind.Conbini  -> ShopStyle("#fcfbf6", Pal.teal, "#1f6f6d", "ひばりマート", "２４じかん", "HIBARI MART"),
    ShopKind.Ramen    -> ShopStyle("#b5322f", Pal.yellow, "#fdf6ec", "らーめん 一心", "しお しょうゆ みそ", "ISSHIN"),
    ShopKind.Wagashi  -> ShopStyle("#f7ede1", Pal.blossomDeep, "#8a4a62", "和菓子 さくら堂", "だいふく どらやき", "SAKURADO"),
    ShopKind.Hana     -> ShopStyle("#f2f7ee", Pal.leafDeep, "#37684b", "花の店 みどり", "きりばな はちうえ", "MIDORI"),
    ShopKind.Kosho    -> ShopStyle("#eae3d0", Pal.roofBrown, "#5b4335", "古本 つばめ書房", "こしょ かいとり", "TSUBAME BOOKS"),
    ShopKind.Cleaning -> ShopStyle("#eef4fb", Pal.blue, "#2a4f97", "クリーニング しらゆき", "あさだし ゆうがた", "SHIRAYUKI"),
    ShopKind.Bunbo    -> ShopStyle("#fdf3e0", Pal.orange, "#a3531c", "文具とゲーム ほしの", "ガチャ ノート カード", "HOSHINO"),
    ShopKind.Bakery   -> ShopStyle("#fdf1dc", "#d8a03c", "#8a5a20", "パン工房 こむぎ", "やきたて まいあさ", "KOMUGI"),
    ShopKind.Sento    -> ShopStyle("#f6f3ea", Pal.blue, "#20509e", "松 の 湯", "あさ六時 - よる十一時", "MATSU-NO-YU"),
    ShopKind.Sozai    -> ShopStyle("#fdf4e2", Pal.teal, "#1d6a58", "そうざい ひなた", "おべんとう からあげ", "HINATA"),
    ShopKind.Record   -> ShopStyle("#e6dcc4", "#9c4a3f", "#7a3f38", "レコード ほしぞら", "えるぴー ・ どうなつばん", "HOSHIZORA RECORD"),
    ShopKind.Denki    -> ShopStyle("#dfe4dc", "#36527f", "#2c4a72", "電器 たかの", "しゅうり ・ でんきゅう", "TAKANO DENKI"),
    ShopKind.Kokuya   -> ShopStyle("#f2ead2", Pal.leafDeep, "#4a5f35", "米 ・ 酒 なかの", "こめ さけ たばこ", "NAKANO"),
    ShopKind.Clinic   -> ShopStyle("#f4f7fa", Pal.blue, "#23508f", "ひばり台内科", "ないか しょうにか", "CLINIC"),
    ShopKind.Yakkyoku -> ShopStyle("#fbf7ec", Pal.leafDeep, "#37684b", "くすり さかい", "ちょうざい", "SAKAI"),
    ShopKind.Laundry  -> ShopStyle("#edf2f5", Pal.teal, "#1f6f6d", "コインランドリー ひばり", "六時 - よる十二時", "LAUNDRY"),
    ShopKind.Fudosan  -> ShopStyle("#fbf6e6", Pal.orange, "#a3531c", "ひばり不動産", "あきま ちゅうしゃ", "ESTATE"),
    ShopKind.Bungu    -> ShopStyle("#fdf6e4", "#c08a3e", "#8a5a20", "文具 ひばり堂", "ノート えんぴつ", "HIBARIDO"),
    ShopKind.Ringyo   -> ShopStyle("#eef2ec", "#4a6f58", "#37684b", "ひばり輪業", "しゅうり ・ ぱんく", "RINGYO"),
    ShopKind.Bento    -> ShopStyle("#fdf2e0", "#c4713a", "#a3531c", "お弁当 のはら", "できたて まいにち", "NOHARA"),
    ShopKind.Zakka    -> ShopStyle("#f4f1e6", Pal.purple, "#5c5480", "雑貨 まるみ", "にちようひん", "MARUMI")
  )

  def shopFascia(kind: ShopKind): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"fascia${kind.key}",
      () =>
        make(1024, 224, (c, w, h) =>
            val st = shops(kind)
            c.fillStyle = st.bg
            c.fillRect(0.0, 0.0, w, h)
            rule(c, 0.0, h - 16.0, w, 16.0, st.bar)
            rule(c, 0.0, 0.0, w, 8.0, st.bar)
            centered(c, st.title, w * 0.4, h * 0.46, w * 0.62, 118.0, st.fg, "bold", 10.0)
            c.font = s"600 34px $JpFont"
            c.fillStyle = st.fg
            c.globalAlpha = 0.72
            c.textAlign = "left"
            c.textBaseline = "middle"
            c.fillText(st.latin, w * 0.76, h * 0.36)
            c.font = s"500 28px $JpFont"
            c.globalAlpha = 0.55
            c.fillText(st.sub, w * 0.76, h * 0.66)
            c.globalAlpha = 1.0
        ))

  def shopBlade(kind: ShopKind): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"blade${kind.key}",
      () =>
        make(192, 768, (c, w, h) =>
            val st = shops(kind)
            c.fillStyle = st.bg
            c.fillRect(0.0, 0.0, w, h)
            rule(c, 0.0, 0.0, w, 12.0, st.bar)
            rule(c, 0.0, h - 12.0, w, 12.0, st.bar)
            val label = st.title.split(' ').last
            vertical(c, label, w / 2.0, 110.0, 108.0, 88.0, st.fg)
        ))

  private val norenSets: Map[NorenKind, (String, String, String)] = Map(
    NorenKind.Ramen   -> ((Pal.norenRed, "#f6ecdc", "らーめん")),
    NorenKind.Sento   -> ((Pal.noren, "#f6ecdc", "ゆ")),
    NorenKind.Wagashi -> ((Pal.norenCream, "#8a4a62", "和菓子")),
    NorenKind.Soba    -> (("#2f5540", "#eef3e6", "そば")),
    NorenKind.Record  -> (("#3f4a68", "#eee2c8", "レコード")),
    NorenKind.Bento   -> (("#b8763a", "#fdf4e4", "お弁当"))
  )

  def norenTex(kind: NorenKind): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"noren${kind.key}",
      () =>
        make(512, 256, (c, w, h) =>
            val (bg, fg, title) = norenSets(kind)
            c.fillStyle = bg
            c.fillRect(0.0, 0.0, w, h)
            if glyphs(title).length == 1 then
              centered(c, title, w / 2.0, h * 0.46, w / 3.0 - 20.0, 130.0, fg, "bold")
            else centered(c, title, w / 2.0, h * 0.46, w - 90.0, 118.0, fg, "bold", 10.0)
            c.globalCompositeOperation = "destination-out"
            Vector(w * 0.33, w * 0.66).foreach { x =>
              c.fillRect(x - 4.0, h * 0.3, 8.0, h)
            }
            c.globalCompositeOperation = "source-over"
        ))

  def menuBoard(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("menuBoard",
      () =>
        make(384, 512, (c, w, h) =>
            c.fillStyle = "#3a4148"
            c.fillRect(0.0, 0.0, w, h)
            c.strokeStyle = "#a8845c"
            c.lineWidth = 20.0
            c.strokeRect(10.0, 10.0, w - 20.0, h - 20.0)
            centered(c, "本日の", w / 2.0, 84.0, w - 90.0, 52.0, "#f0e6cc")
            centered(c, "おすすめ", w / 2.0, 142.0, w - 80.0, 60.0, "#f4c033")
            val rows = Vector(
              ("しおらーめん", "七二〇"), ("みそらーめん", "七八〇"), ("ぎょうざ", "三六〇"), ("めんま", "一八〇")
            )
            c.textBaseline = "middle"
            rows.zipWithIndex.foreach { case ((left, right), i) =>
              c.font = s"600 38px $JpFont"
              c.fillStyle = "#e8e2d2"
              c.textAlign = "left"
              c.fillText(left, 46.0, 232.0 + i * 62.0)
              c.textAlign = "right"
              c.fillText(right, w - 46.0, 232.0 + i * 62.0)
              c.globalAlpha = 0.35
              rule(c, 46.0, 258.0 + i * 62.0, w - 92.0, 2.0, "#cfc8b6")
              c.globalAlpha = 1.0
            }
        ))

  def lanternTex(variant: Int): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"lantern$variant",
      () =>
        make(256, 256, (c, w, h) =>
            c.fillStyle = if variant == 1 then "#f2ddb8" else "#f8ecd6"
            c.fillRect(0.0, 0.0, w, h)
            c.fillStyle = "rgba(180,160,124,0.5)"
            (0 until 9).foreach(i => c.fillRect(0.0, 12.0 + i * 28.0, w, 3.0))
            val words     = Vector("桜坂", "商店街", "ゆ", "祭", "奉納")
            val word      = words(variant % words.length)
            val glyphSize = if variant >= 3 then 128.0 else 92.0
            centered(c, word, w / 2.0, h / 2.0, w - 70.0, glyphSize,
              if variant == 2 then "#20509e" else "#b5322f", "bold", 6.0)
        ))

  def flagTex(variant: Int): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"flag$variant",
      () =>
        make(256, 384, (c, w, h) =>
            val sets = Vector(
              (Pal.red, "#fdf6ec", "アイス"), (Pal.blue, "#fdf6ec", "つめたい"), (Pal.yellow, "#3c3a46", "しんはつばい"),
              (Pal.teal, "#fdf6ec", "おべんとう")
            )
            val (bg, fg, title) = sets(variant % sets.length)
            c.fillStyle = bg
            c.fillRect(0.0, 0.0, w, h)
            c.fillStyle = "#fdf8f0"
            c.fillRect(10.0, 10.0, w - 20.0, h - 20.0)
            c.fillStyle = bg
            c.fillRect(10.0, 10.0, w - 20.0, 70.0)
            vertical(c, title, w / 2.0, 140.0, 62.0, 52.0, if fg == "#3c3a46" then "#3c3a46" else bg)
        ))

  def gachaTex(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("gachaTex",
      () =>
        make(256, 256, (c, w, h) =>
            c.fillStyle = "#f6f2ea"
            c.fillRect(0.0, 0.0, w, h)
            rule(c, 0.0, 0.0, w, 46.0, Pal.orange)
            centered(c, "ガチャ", w / 2.0, 24.0, w - 40.0, 36.0, "#fdf6ec")
            val capsuleColors = Vector("#e0453f", "#f4c033", "#3d6ec4", "#2f9c9a", "#ef8a3c", "#e86f9c")
            (0 until 22).foreach { i =>
              c.fillStyle = capsuleColors(i % capsuleColors.length)
              c.beginPath()
              c.arc(34.0 + (i * 53) % 190, 88.0 + (i * 71) % 130, 21.0, 0.0, math.Pi * 2.0)
              c.fill()
            }
            rule(c, 0.0, h - 30.0, w, 30.0, "#d8d4dc")
            centered(c, "一回 三〇〇円", w / 2.0, h - 15.0, w - 40.0, 26.0, "#4b4757", "600")
        ))

  def sentoFuji(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("sentoFuji",
      () =>
        make(1024, 340, (c, w, h) =>
            val waterline = h * 0.74
            val apexX     = w * 0.585
            val apexY     = h * 0.115
            val half      = w * 0.325
            def poly(points: List[(Double, Double)], fill: String): Unit =
              c.fillStyle = fill
              c.beginPath()
              points.zipWithIndex.foreach((pt, i) =>
                if i == 0 then c.moveTo(pt._1, pt._2) else c.lineTo(pt._1, pt._2)
              )
              c.closePath()
              c.fill()
            def pine(x: Double, scale: Double): Unit =
              rule(c, x - 5.0 * scale, waterline - 42.0 * scale, 10.0 * scale, 46.0 * scale, "#38291f")
              (0 until 3).foreach { i =>
                val tierY    = waterline - 34.0 * scale - i * 32.0 * scale
                val tierHalf = (86.0 - i * 20.0) * scale
                poly(
                  List((x - tierHalf, tierY), (x + tierHalf, tierY), (x, tierY - 40.0 * scale)),
                  if i == 1 then "#1d3a30" else "#264639"
                )
              }
            c.fillStyle = "#d7e9f5"
            c.fillRect(0.0, 0.0, w, h)
            poly(List((apexX - half, waterline), (apexX, apexY), (apexX + half, waterline)), "#6d95bd")
            poly(List((apexX, apexY), (apexX + half, waterline), (apexX + half * 0.17, waterline)), "#4d719b")
            poly(
              List(
                (apexX, apexY), (apexX + w * 0.112, h * 0.415), (apexX + w * 0.073, h * 0.345),
                (apexX + w * 0.032, h * 0.400), (apexX - w * 0.012, h * 0.340), (apexX - w * 0.052, h * 0.408),
                (apexX - w * 0.090, h * 0.352), (apexX - w * 0.112, h * 0.415)
              ),
              "#f6f9fb"
            )
            rule(c, 0.0, waterline, w, h - waterline, "#4f7ba3")
            rule(c, 0.0, waterline, w, 5.0, "#e4eff7")
            pine(w * 0.075, 1.05)
            pine(w * 0.945, 0.86)
            centered(c, "松の湯", w * 0.245, h * 0.33, w * 0.30, 104.0, "#16314f", "bold", 10.0)
            c.font = s"600 30px $JpFont"
            c.fillStyle = "#3a5c86"
            c.textAlign = "center"
            c.textBaseline = "middle"
            c.fillText("ま つ の ゆ", w * 0.245, h * 0.56)
        ))

  def shrineName(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("shrineName",
      () =>
        make(256, 768, (c, w, h) =>
            c.fillStyle = "#e6dcc4"
            c.fillRect(0.0, 0.0, w, h)
            rule(c, 16.0, 16.0, w - 32.0, 6.0, "#8a7350")
            rule(c, 16.0, h - 22.0, w - 32.0, 6.0, "#8a7350")
            vertical(c, "桜守神社", w / 2.0, 140.0, 150.0, 122.0, "#4a4034")
        ))

  def emaTex(variant: Int): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"ema$variant",
      () =>
        make(256, 192, (c, w, h) =>
            c.fillStyle = "#e9d9b6"
            c.fillRect(0.0, 0.0, w, h)
            rule(c, 0.0, 0.0, w, 12.0, "#b5322f")
            val wishes = Vector("合格祈願", "家内安全", "交通安全", "無病息災", "学業成就")
            centered(c, wishes(variant % wishes.length), w / 2.0, h * 0.5, w - 40.0, 62.0, "#4a4034", "bold",
              4.0)
            c.globalAlpha = 0.5
            rule(c, 40.0, h * 0.74, w - 80.0, 3.0, "#8a7350")
            c.globalAlpha = 1.0
        ))

  def omikujiTex(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("omikujiTex",
      () =>
        make(256, 320, (c, w, h) =>
            c.fillStyle = "#f2e8d2"
            c.fillRect(0.0, 0.0, w, h)
            rule(c, 0.0, 0.0, w, 54.0, "#b5322f")
            centered(c, "おみくじ", w / 2.0, 27.0, w - 30.0, 40.0, "#fdf6ec")
            centered(c, "一回", w / 2.0, 118.0, w - 90.0, 56.0, "#4a4034")
            centered(c, "百円", w / 2.0, 186.0, w - 90.0, 62.0, "#b5322f")
            c.globalAlpha = 0.45
            rule(c, 34.0, 244.0, w - 68.0, 3.0, "#8a7350")
            c.globalAlpha = 1.0
            centered(c, "ご自由に", w / 2.0, 282.0, w - 60.0, 30.0, "#6a6153", "600")
        ))

  def sanpaiNotice(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("sanpaiNotice",
      () =>
        make(384, 512, (c, w, h) =>
            c.fillStyle = "#f6f2e6"
            c.fillRect(0.0, 0.0, w, h)
            rule(c, 0.0, 0.0, w, 24.0, "#b5322f")
            centered(c, "参拝のしかた", w / 2.0, 84.0, w - 60.0, 62.0, "#4a4034", "bold", 4.0)
            val lines = Vector("一、手水で きよめる", "二、二礼", "三、二拍手", "四、一礼", "", "氏子会")
            c.textAlign = "left"
            c.textBaseline = "middle"
            lines.zipWithIndex.foreach((line, i) =>
              if line.nonEmpty then
                c.font = s"600 ${if i == 5 then 28 else 36}px $JpFont"
                c.fillStyle = if i == 5 then "#8a8172" else "#4b4436"
                c.fillText(line, 46.0, 176.0 + i * 54.0)
            )
        ))

  def busStopPlate(variant: Int): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"busStopPlate$variant",
      () =>
        make(384, 384, (c, w, _) =>
            val sets = Vector(
              ("図書館前", "１日 ４便"), ("六丁目", "終点  ・  ここで折返し")
            )
            val (stop, foot) = sets(variant % sets.length)
            c.fillStyle = "#fbfaf6"
            c.beginPath()
            c.arc(w / 2.0, w / 2.0, w / 2.0 - 6.0, 0.0, math.Pi * 2.0)
            c.fill()
            c.strokeStyle = Pal.blueDeep
            c.lineWidth = 14.0
            c.stroke()
            centered(c, "ひばり台", w / 2.0, 92.0, w - 130.0, 40.0, Pal.blueDeep, "600")
            centered(c, stop, w / 2.0, 168.0, w - 90.0, 62.0, "#3b3846", "bold", 2.0)
            c.globalAlpha = 0.45
            rule(c, 96.0, 226.0, w - 192.0, 3.0, "#8a84a0")
            c.globalAlpha = 1.0
            centered(c, "ふれあい号", w / 2.0, 268.0, w - 120.0, 34.0, Pal.teal, "600")
            centered(c, foot, w / 2.0, 316.0, w - 130.0, 26.0, "#6f6a80", "600")
        ))

  def busTimetable(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("busTimetable",
      () =>
        make(320, 480, (c, w, h) =>
            c.fillStyle = "#fdfbf6"
            c.fillRect(0.0, 0.0, w, h)
            rule(c, 0.0, 0.0, w, 56.0, Pal.blueDeep)
            centered(c, "時刻表", w / 2.0, 28.0, w - 40.0, 32.0, "#fdf8f0", "bold", 2.0)
            val columns = Vector(
              ("右まわり", Vector("7:40", "10:20", "13:50", "16:30")), ("左まわり", Vector("8:15", "11:05", "14:35", "17:10"))
            )
            columns.zipWithIndex.foreach { case ((head, times), i) =>
              val x = w * (if i == 1 then 0.74 else 0.26)
              centered(c, head, x, 96.0, w * 0.44, 24.0, Pal.teal, "600")
              c.globalAlpha = 0.4
              rule(c, x - w * 0.2, 122.0, w * 0.4, 2.0, "#9a94a6")
              c.globalAlpha = 1.0
              times.zipWithIndex.foreach((time, j) =>
                centered(c, time, x, 164.0 + j * 62.0, w * 0.4, 34.0, "#4b4757", "600")
              )
            }
            centered(c, "日祝は運休", w / 2.0, 436.0, w - 60.0, 22.0, "#8a8696", "600")
        ))

  def returnPlate(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("returnPlate",
      () =>
        make(384, 192, (c, w, h) =>
            c.fillStyle = "#e8eae4"
            c.fillRect(0.0, 0.0, w, h)
            rule(c, 0.0, 0.0, w, 16.0, Pal.tealDeep)
            centered(c, "返却ポスト", w / 2.0, h * 0.44, w - 60.0, 56.0, "#2f5b52", "bold", 5.0)
            centered(c, "閉館中も ご利用できます", w / 2.0, h * 0.78, w - 40.0, 24.0, "#8a8b82", "600")
        ))

  def phoneBoxSign(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("phoneBoxSign",
      () =>
        make(512, 160, (c, w, h) =>
            c.fillStyle = "#f4f2ea"
            c.fillRect(0.0, 0.0, w, h)
            rule(c, 0.0, 0.0, w, 10.0, "#2f6b52")
            rule(c, 0.0, h - 10.0, w, 10.0, "#2f6b52")
            centered(c, "公衆電話", w * 0.46, h * 0.44, w * 0.6, 82.0, "#245a44", "bold", 8.0)
            c.font = s"600 22px $JpFont"
            c.fillStyle = "#7f8f84"
            c.textAlign = "center"
            c.textBaseline = "middle"
            c.fillText("TELEPHONE", w * 0.46, h * 0.78)
            c.strokeStyle = "#245a44"
            c.lineWidth = 13.0
            c.lineCap = "round"
            c.beginPath()
            c.moveTo(w * 0.83, h * 0.32)
            c.lineTo(w * 0.9, h * 0.68)
            c.stroke()
            c.lineWidth = 24.0
            c.beginPath()
            c.moveTo(w * 0.805, h * 0.28)
            c.lineTo(w * 0.855, h * 0.28)
            c.stroke()
            c.beginPath()
            c.moveTo(w * 0.875, h * 0.72)
            c.lineTo(w * 0.925, h * 0.72)
            c.stroke()
        ))

  def phoneNotice(variant: Int): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"phoneNotice$variant",
      () =>
        make(256, 352, (c, w, h) =>
            val sets = Vector(
              (Pal.redDeep, "緊急通報", Vector("１１０ ・ １１９", "は 無料です", "", "受話器を上げて", "ボタンを押す")),
              (Pal.blue, "ご案内", Vector("１０円 ・ ５０円", "テレホンカード", "", "つり銭は", "でません"))
            )
            val (bar, title, lines) = sets(variant % sets.length)
            c.fillStyle = "#fbfaf4"
            c.fillRect(0.0, 0.0, w, h)
            rule(c, 0.0, 0.0, w, 18.0, bar)
            centered(c, title, w / 2.0, 56.0, w - 40.0, 44.0, bar, "bold", 4.0)
            c.textAlign = "center"
            c.textBaseline = "middle"
            lines.zipWithIndex.foreach((line, i) =>
              if line.nonEmpty then
                c.font = s"600 ${if i == 0 then 28 else 24}px $JpFont"
                c.fillStyle = if i == 0 then "#4b4757" else "#6a6577"
                c.fillText(line, w / 2.0, 112.0 + i * 44.0)
            )
        ))

  def guideBoard(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("guideBoard",
      () =>
        make(640, 448, (c, w, h) =>
            c.fillStyle = "#f6f4ec"
            c.fillRect(0.0, 0.0, w, h)
            rule(c, 0.0, 0.0, w, 44.0, Pal.blueDeep)
            centered(c, "ひばり台 一丁目 案内図", w / 2.0, 22.0, w - 60.0, 30.0, "#f4f8ff", "bold", 3.0)
            c.fillStyle = "#e2e6dc"
            val blocks = Vector(
              (24.0, 70.0, 150.0, 96.0), (200.0, 70.0, 118.0, 70.0), (344.0, 70.0, 130.0, 96.0),
              (500.0, 70.0, 116.0, 130.0), (24.0, 200.0, 118.0, 110.0), (172.0, 226.0, 146.0, 84.0),
              (344.0, 200.0, 130.0, 110.0), (24.0, 344.0, 200.0, 76.0), (252.0, 344.0, 130.0, 76.0),
              (412.0, 300.0, 204.0, 120.0)
            )
            blocks.foreach((x, y, bw, bh) => c.fillRect(x, y, bw, bh))
            c.fillStyle = "#cfd8c8"
            blocks.zipWithIndex.collect { case (block, i) if i % 3 == 0 => block }
              .foreach((x, y, bw, bh) => c.fillRect(x, y, bw, bh))
            c.strokeStyle = "#ffffff"
            c.lineWidth = 18.0
            c.beginPath()
            c.moveTo(0.0, 186.0)
            c.lineTo(w, 186.0)
            c.moveTo(332.0, 44.0)
            c.lineTo(332.0, h)
            c.stroke()
            c.strokeStyle = "#9aa3b4"
            c.lineWidth = 3.0
            c.setLineDash(js.Array(12.0, 9.0))
            c.beginPath()
            c.moveTo(0.0, 292.0)
            c.lineTo(w, 292.0)
            c.stroke()
            c.setLineDash(js.Array())
            c.font = s"600 19px $JpFont"
            c.fillStyle = "#5a6472"
            c.textAlign = "center"
            c.textBaseline = "middle"
            c.fillText("図書館", 96.0, 118.0)
            c.fillText("商店街", 560.0, 132.0)
            c.fillText("小学校", 118.0, 254.0)
            c.fillText("駅", 470.0, 258.0)
            c.fillStyle = Pal.redDeep
            c.beginPath()
            c.arc(332.0, 220.0, 15.0, 0.0, math.Pi * 2.0)
            c.fill()
            c.font = s"bold 20px $JpFont"
            c.fillText("現在地", 396.0, 220.0)
        ))

  def showaInterior(variant: Int): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"showaInterior$variant",
      () =>
        make(512, 320, (c, w, h) =>
            if variant % 2 == 0 then
              c.fillStyle = "#d8cfba"
              c.fillRect(0.0, 0.0, w, h)
              rule(c, 0.0, 0.0, w, 34.0, "#e8dcc0")
              rule(c, 0.0, h - 62.0, w, 62.0, "#a8977c")
              val spines = Vector("#b5322f", "#2f4a72", "#d39c1f", "#3f7a5e", "#8a4a62", "#4a4a92", "#a3531c")
              (0 until 3).foreach { r =>
                val y = 52.0 + r * 78.0
                rule(c, 16.0, y + 56.0, w - 32.0, 10.0, "#8a7558")
                (0 until 48).foreach { i =>
                  c.fillStyle = spines((i * 3 + r) % spines.length)
                  c.globalAlpha = 0.85
                  c.fillRect(20.0 + i * 10.0, y + (i * 7) % 5, 7.0, 54.0)
                }
                c.globalAlpha = 1.0
              }
              rule(c, 300.0, 214.0, 190.0, 22.0, "#b59a76")
              rule(c, 300.0, 236.0, 190.0, 24.0, "#94805f")
              c.fillStyle = "#4b4757"
              c.beginPath()
              c.arc(360.0, 206.0, 26.0, 0.0, math.Pi * 2.0)
              c.fill()
              c.fillStyle = "#d8d4dc"
              c.beginPath()
              c.arc(360.0, 206.0, 7.0, 0.0, math.Pi * 2.0)
              c.fill()
              rule(c, 400.0, 190.0, 66.0, 22.0, "#cfc4a8")
            else
              c.fillStyle = "#d2d6d0"
              c.fillRect(0.0, 0.0, w, h)
              rule(c, 0.0, 0.0, w, 30.0, "#e4e8e0")
              rule(c, 0.0, h - 58.0, w, 58.0, "#a4a89e")
              (0 until 4).foreach { r =>
                val y = 44.0 + r * 58.0
                rule(c, 14.0, y + 44.0, 300.0, 9.0, "#8f9a92")
                (0 until 6).foreach { i =>
                  rule(c, 20.0 + i * 50.0, y, 42.0, 42.0,
                    Vector("#dcd2b8", "#c9c2ae", "#e2dcc6", "#cfd4cc")((i + r) % 4))
                  c.fillStyle = "rgba(70,74,80,0.25)"
                  c.fillRect(24.0 + i * 50.0, y + 8.0, 34.0, 6.0)
                }
              }
              rule(c, 330.0, 44.0, 168.0, 200.0, "#b9beb6")
              c.fillStyle = "#9aa09a"
              for
                r <- 0 until 6
                i <- 0 until 3
              do c.fillRect(338.0 + i * 56.0, 50.0 + r * 33.0, 48.0, 26.0)
              rule(c, 60.0, 8.0, 200.0, 12.0, "#fff6dc")
            end if
            c.fillStyle = "rgba(112,108,132,0.2)"
            c.fillRect(0.0, 0.0, w, h)
        ))

  def chalkNotice(variant: Int): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"chalkNotice$variant",
      () =>
        make(320, 384, (c, w, h) =>
            val sets = Vector(
              ("入荷", Vector("しました", "", "中古 えるぴー", "五〇〇円 より")), ("修理", Vector("承ります", "", "テレビ ・ せんぷうき", "ラジオ ・ とけい"))
            )
            val (title, lines) = sets(variant % sets.length)
            c.fillStyle = "#39413c"
            c.fillRect(0.0, 0.0, w, h)
            c.strokeStyle = "#9c7f52"
            c.lineWidth = 18.0
            c.strokeRect(9.0, 9.0, w - 18.0, h - 18.0)
            centered(c, title, w / 2.0, 92.0, w - 90.0, 76.0, "#f4c033", "bold", 8.0)
            c.textAlign = "center"
            c.textBaseline = "middle"
            lines.zipWithIndex.foreach((line, i) =>
              if line.nonEmpty then
                c.font = s"600 ${if i == 0 then 40 else 30}px $JpFont"
                c.fillStyle = if i == 0 then "#eee6d2" else "#c8c0ac"
                c.fillText(line, w / 2.0, 168.0 + i * 52.0)
            )
        ))

  def sleeveTex(variant: Int): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"sleeve$variant",
      () =>
        make(256, 256, (c, w, h) =>
            val sets = Vector(
              ("#b5322f", "#f2e8d6", "#f4c033"), ("#2f4a72", "#d8e4f0", "#e08a3c"), ("#e8dcc0", "#3f7a5e", "#b5322f"),
              ("#3f3a4a", "#efe0b4", "#8f6fb5")
            )
            val (bg, accentA, accentB) = sets(variant % sets.length)
            c.fillStyle = bg
            c.fillRect(0.0, 0.0, w, h)
            c.fillStyle = accentA
            c.fillRect(20.0, 20.0, w - 40.0, 40.0)
            c.beginPath()
            c.arc(w * 0.62, h * 0.58, 52.0, 0.0, math.Pi * 2.0)
            c.fill()
            c.fillStyle = accentB
            c.fillRect(24.0, h - 74.0, 108.0, 22.0)
            c.beginPath()
            c.arc(w * 0.62, h * 0.58, 20.0, 0.0, math.Pi * 2.0)
            c.fill()
            c.fillStyle = "rgba(255,255,255,0.16)"
            c.fillRect(0.0, 0.0, 14.0, h)
        ))

  def namePlate(variant: Int): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"namePlate$variant",
      () =>
        make(256, 128, (c, w, h) =>
            val names = Vector("森田", "白石", "東", "小谷", "中根", "若宮",
              "瀬川", "大野", "朝倉", "柏木", "津田", "室井")
            c.fillStyle = Vector("#f2ece0", "#e4e8ee", "#efe6e6")(variant % 3)
            c.fillRect(0.0, 0.0, w, h)
            c.strokeStyle = "#a49eae"
            c.lineWidth = 5.0
            c.strokeRect(8.0, 8.0, w - 16.0, h - 16.0)
            centered(c, names(variant % names.length), w / 2.0, h / 2.0, w - 50.0, 62.0, "#43404f", "bold", 8.0)
        ))

  def gomiPlate(variant: Int): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"gomiPlate$variant",
      () =>
        make(384, 288, (c, w, h) =>
            c.fillStyle = "#fbfaf4"
            c.fillRect(0.0, 0.0, w, h)
            if variant % 2 == 1 then
              rule(c, 0.0, 0.0, w, 22.0, Pal.teal)
              centered(c, "分別のお願い", w / 2.0, 70.0, w - 50.0, 52.0, "#1f6f6d", "bold", 4.0)
              c.globalAlpha = 0.4
              rule(c, 34.0, 106.0, w - 68.0, 3.0, "#9aa39a")
              c.globalAlpha = 1.0
              val rows = Vector(
                ("もえるごみ", "生ごみ ・ 紙くず"), ("プラ", "すすいで つぶす"), ("びん ・ かん", "ふたを はずす"), ("粗大ごみ", "町内会に 連絡")
              )
              c.textBaseline = "middle"
              rows.zipWithIndex.foreach { case ((category, note), i) =>
                val y = 142.0 + i * 38.0
                c.font = s"600 26px $JpFont"
                c.fillStyle = "#4b4757"
                c.textAlign = "left"
                c.fillText(category, 32.0, y)
                c.font = s"500 21px $JpFont"
                c.fillStyle = "#7a7588"
                c.textAlign = "right"
                c.fillText(note, w - 32.0, y)
                if i < 3 then
                  c.globalAlpha = 0.3
                  rule(c, 32.0, y + 19.0, w - 64.0, 2.0, "#9a94a6")
                  c.globalAlpha = 1.0
              }
            else
              rule(c, 0.0, 0.0, w, 22.0, Pal.leafDeep)
              centered(c, "ごみ集積所", w / 2.0, 74.0, w - 50.0, 58.0, "#37684b", "bold", 4.0)
              val rows = Vector(
                ("もえるごみ", "月・木"), ("しげん", "水"), ("びん・かん", "第二金")
              )
              c.textBaseline = "middle"
              rows.zipWithIndex.foreach { case ((category, day), i) =>
                c.font = s"600 32px $JpFont"
                c.fillStyle = "#4b4757"
                c.textAlign = "left"
                c.fillText(category, 36.0, 148.0 + i * 50.0)
                c.textAlign = "right"
                c.fillText(day, w - 36.0, 148.0 + i * 50.0)
              }
            end if
        ))

  def paperSheet(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("paperSheet",
      () =>
        make(256, 352, (c, w, h) =>
            c.fillStyle = "#fbfaf4"
            c.fillRect(0.0, 0.0, w, h)
            rule(c, 0.0, 0.0, w, 12.0, Pal.blue)
            c.fillStyle = "#b8b3c0"
            (0 until 9).foreach(i => c.fillRect(28.0, 60.0 + i * 30.0, w - 56.0 - (i * 37) % 70, 8.0))
            c.fillStyle = "#d8d4dc"
            c.fillRect(28.0, 260.0, w - 56.0, 60.0)
        ))

  def shopInterior(variant: Int): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"shopInterior$variant",
      () =>
        make(512, 320, (c, w, h) =>
            val v = variant % 4
            c.fillStyle = Vector("#e2ddd2", "#e6dcc8", "#dcd8d0", "#e8ded0")(v)
            c.fillRect(0.0, 0.0, w, h)
            rule(c, 0.0, 0.0, w, 40.0, "#f2eee4")
            rule(c, 0.0, h - 60.0, w, 60.0, "#bfb6a6")
            if v == 3 then
              rule(c, 0.0, h - 60.0, w, 14.0, "#a89a86")
              rule(c, 24.0, 96.0, 150.0, h - 168.0, "#cfc0a8")
              c.fillStyle = "#b6a68c"
              (0 until 4).foreach(r => c.fillRect(30.0, 108.0 + r * 42.0, 138.0, 6.0))
              rule(c, 182.0, 118.0, 42.0, h - 190.0, "#b99a72")
              Vector(236.0 -> (("男湯", "#2f4a72")), 376.0 -> (("女湯", "#8a3f56"))).foreach {
                (x0, labelAndTone) =>
                  val (label, tone) = labelAndTone
                  rule(c, x0, 74.0, 118.0, h - 148.0, "#6f6656")
                  rule(c, x0, 74.0, 118.0, 74.0, tone)
                  c.fillStyle = "rgba(232,238,246,0.92)"
                  c.font = s"bold 30px $JpFont"
                  c.textAlign = "center"
                  c.textBaseline = "middle"
                  glyphs(label).zipWithIndex.foreach((ch, i) => c.fillText(ch, x0 + 34.0 + i * 50.0, 112.0))
                  c.fillStyle = "#6f6656"
                  c.fillRect(x0 + 57.0, 104.0, 5.0, 44.0)
              }
            else if v == 0 then
              c.fillStyle = "#c6d8e0"
              c.fillRect(20.0, 56.0, 190.0, 200.0)
              c.fillStyle = "#eaf2f6"
              (0 until 4).foreach(r => c.fillRect(28.0, 68.0 + r * 48.0, 174.0, 8.0))
              val cols = Vector("#e0453f", "#f4c033", "#3d6ec4", "#2f9c9a", "#ef8a3c", "#8fbf4a")
              for
                r <- 0 until 4
                i <- 0 until 9
              do
                c.fillStyle = cols((r * 3 + i) % cols.length)
                c.fillRect(32.0 + i * 19.0, 76.0 + r * 48.0, 13.0, 30.0)
              c.fillStyle = "#d8d2c4"
              c.fillRect(238.0, 120.0, 250.0, 20.0)
              c.fillRect(238.0, 176.0, 250.0, 20.0)
              c.fillStyle = "#b6ad9c"
              (0 until 8).foreach(i => c.fillRect(246.0 + i * 30.0, 96.0, 22.0, 24.0))
              (0 until 8).foreach(i => c.fillRect(246.0 + i * 30.0, 150.0, 22.0, 26.0))
            else if v == 1 then
              c.fillStyle = "#a8825c"
              c.fillRect(0.0, 168.0, w, 30.0)
              c.fillStyle = "#8e6a48"
              c.fillRect(0.0, 198.0, w, 62.0)
              c.fillStyle = "#b5322f"
              c.fillRect(0.0, 40.0, w, 44.0)
              c.fillStyle = "#f2e8d6"
              (0 until 6).foreach(i => c.fillRect(28.0 + i * 82.0, 50.0, 44.0, 24.0))
              c.fillStyle = "#6a6153"
              (0 until 6).foreach(i => c.fillRect(40.0 + i * 82.0, 200.0, 34.0, 12.0))
            else
              c.fillStyle = "#cdc3b0"
              (0 until 4).foreach(i => c.fillRect(24.0 + i * 124.0, 52.0, 100.0, 210.0))
              c.fillStyle = "#b3a894"
              for
                i <- 0 until 4
                r <- 0 until 4
              do c.fillRect(24.0 + i * 124.0, 52.0 + r * 52.0, 100.0, 8.0)
              val boxColors = Vector("#efe0c4", "#dcc9a8", "#e8d4b6", "#cbb894")
              for
                i <- 0 until 4
                r <- 0 until 4
              do
                c.fillStyle = boxColors((i + r) % 4)
                c.fillRect(32.0 + i * 124.0, 62.0 + r * 52.0, 84.0, 38.0)
            end if
            c.fillStyle = "rgba(120,116,140,0.16)"
            c.fillRect(0.0, 0.0, w, h)
        ))

  def curtainTex(variant: Int): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"curtain$variant",
      () =>
        make(256, 256, (c, w, h) =>
            c.fillStyle = if variant == 1 then "#eef2f6" else "#f4ead9"
            c.fillRect(0.0, 0.0, w, h)
            c.fillStyle = "rgba(150,144,164,0.28)"
            (0 until 12).foreach { i =>
              val x = i * 21.0 + (i % 3) * 3.0
              c.fillRect(x, 0.0, 7.0, h)
            }
            c.fillStyle = "rgba(255,255,255,0.5)"
            c.fillRect(0.0, 0.0, w, 18.0)
        ))

  def litWindowTex(variant: Int): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"litWindow$variant",
      () =>
        make(256, 256, (c, w, h) =>
            c.fillStyle = Vector("#f7e2b8", "#f2dcc0", "#f6e6c8")(variant % 3)
            c.fillRect(0.0, 0.0, w, h)
            c.fillStyle = "rgba(160,124,86,0.3)"
            if variant % 3 == 0 then
              c.fillRect(24.0, 150.0, 208.0, 14.0)
              c.fillRect(40.0, 96.0, 34.0, 54.0)
              c.fillRect(88.0, 110.0, 26.0, 40.0)
            else if variant % 3 == 1 then
              c.fillRect(0.0, 176.0, w, 80.0)
              c.fillRect(150.0, 60.0, 70.0, 116.0)
            else
              c.fillRect(30.0, 40.0, 90.0, 12.0)
              c.fillRect(30.0, 70.0, 60.0, 12.0)
              c.fillRect(0.0, 200.0, w, 56.0)
            c.fillStyle = "rgba(255,246,224,0.4)"
            c.fillRect(0.0, 0.0, w, h)
        ))

  final private case class OnsenStyle(
    bg: String,
    fg: String,
    bar: String,
    title: String,
    sub: String,
    latin: String
  )

  private val onsenStyles: Map[OnsenKind, OnsenStyle] = Map(
    OnsenKind.Yunoya   -> OnsenStyle("#2c3a52", "#f0e4c8", "#1a2334", "湯乃屋", "ゆのや", "YUNOYA"),
    OnsenKind.Hourai   -> OnsenStyle("#25506b", "#f2ece0", "#173648", "蓬莱湯", "ほうらいゆ", "PUBLIC BATH"),
    OnsenKind.Sakuraan -> OnsenStyle("#f0e3cc", "#8a4a62", "#c9a98c", "さくら庵", "かんみどころ", "WAGASHI"),
    OnsenKind.Yunoka   -> OnsenStyle("#6b4632", "#f4e6c8", "#4a2f21", "ゆのか", "じゅんきっさ", "COFFEE"),
    OnsenKind.Kokeshi  -> OnsenStyle("#b5322f", "#f8ecd8", "#82211f", "こけし堂", "みやげもの", "SOUVENIR")
  )

  def onsenFascia(kind: OnsenKind): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"onsenFascia${kind.key}",
      () =>
        make(1024, 220, (c, w, h) =>
            val st = onsenStyles(kind)
            c.fillStyle = st.bg
            c.fillRect(0.0, 0.0, w, h)
            rule(c, 0.0, 0.0, w, 9.0, st.bar)
            rule(c, 0.0, h - 13.0, w, 13.0, st.bar)
            c.globalAlpha = 0.35
            rule(c, 22.0, 22.0, w - 44.0, 3.0, st.fg)
            rule(c, 22.0, h - 27.0, w - 44.0, 3.0, st.fg)
            c.globalAlpha = 1.0
            centered(c, st.title, w * 0.38, h * 0.5, w * 0.52, 118.0, st.fg, "bold", 14.0)
            c.textAlign = "left"
            c.textBaseline = "middle"
            c.fillStyle = st.fg
            c.globalAlpha = 0.7
            c.font = s"600 32px $JpFont"
            c.fillText(st.sub, w * 0.72, h * 0.38)
            c.globalAlpha = 0.45
            c.font = s"500 25px $JpFont"
            c.fillText(st.latin, w * 0.72, h * 0.68)
            c.globalAlpha = 1.0
        ))

  def onsenBlade(kind: OnsenKind): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"onsenBlade${kind.key}",
      () =>
        make(192, 768, (c, w, h) =>
            val st = onsenStyles(kind)
            c.fillStyle = "#c8a878"
            c.fillRect(0.0, 0.0, w, h)
            c.strokeStyle = "rgba(122,92,60,0.30)"
            c.lineWidth = 3.0
            (0 until 7).foreach { i =>
              val x = 16.0 + i * 26.0
              c.beginPath()
              c.moveTo(x, 0.0)
              c.bezierCurveTo(x + 9.0, h * 0.3, x - 9.0, h * 0.66, x + 4.0, h)
              c.stroke()
            }
            rule(c, 0.0, 0.0, w, 14.0, "#8a6a44")
            rule(c, 0.0, h - 14.0, w, 14.0, "#8a6a44")
            vertical(c, glyphs(st.title).take(4).mkString, w / 2.0, 132.0, 128.0, 104.0, "#3a2a20")
        ))

  private val onsenNorenSets: Map[OnsenNorenKind, (String, String, String)] = Map(
    OnsenNorenKind.Male   -> (("#24425e", "#f2ece0", "男湯")),
    OnsenNorenKind.Female -> (("#a33a4c", "#f8ece4", "女湯")),
    OnsenNorenKind.Yunoya -> (("#2c3a52", "#f0e4c8", "湯乃屋")),
    OnsenNorenKind.Kanmi  -> (("#efe2c8", "#8a4a62", "甘味")),
    OnsenNorenKind.Kissa  -> (("#6b4632", "#f4e6c8", "珈琲"))
  )

  def onsenNoren(kind: OnsenNorenKind): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"onsenNoren${kind.key}",
      () =>
        make(512, 256, (c, w, h) =>
            val (bg, fg, title) = onsenNorenSets(kind)
            c.fillStyle = bg
            c.fillRect(0.0, 0.0, w, h)
            val chars = glyphs(title)
            if chars.length == 2 then
              centered(c, chars(0), w * 0.165, h * 0.46, w / 3.0 - 26.0, 116.0, fg)
              centered(c, chars(1), w * 0.835, h * 0.46, w / 3.0 - 26.0, 116.0, fg)
            else if chars.length == 3 then
              chars.zipWithIndex.foreach((ch, i) =>
                centered(c, ch, w * (0.165 + i * 0.335), h * 0.46, w / 3.0 - 26.0, 104.0, fg)
              )
            else centered(c, title, w / 2.0, h * 0.46, w - 90.0, 112.0, fg, "bold", 8.0)
            c.globalCompositeOperation = "destination-out"
            Vector(w * 0.33, w * 0.66).foreach { x =>
              c.fillRect(x - 4.0, h * 0.26, 8.0, h)
            }
            c.globalCompositeOperation = "source-over"
        ))

  def houraiFuji(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("houraiFuji",
      () =>
        make(1024, 340, (c, w, h) =>
            val waterline = h * 0.70
            val apexX     = w * 0.36
            val apexY     = h * 0.10
            val half      = w * 0.30
            def poly(points: List[(Double, Double)], fill: String): Unit =
              c.fillStyle = fill
              c.beginPath()
              points.zipWithIndex.foreach((pt, i) =>
                if i == 0 then c.moveTo(pt._1, pt._2) else c.lineTo(pt._1, pt._2)
              )
              c.closePath()
              c.fill()
            c.fillStyle = "#f6dcc8"
            c.fillRect(0.0, 0.0, w, h * 0.42)
            c.fillStyle = "#e3ccd2"
            c.fillRect(0.0, h * 0.42, w, waterline - h * 0.42)
            poly(List((apexX - half, waterline), (apexX, apexY), (apexX + half, waterline)), "#7b7ba0")
            poly(List((apexX, apexY), (apexX - half, waterline), (apexX - half * 0.2, waterline)), "#5d5c85")
            poly(
              List(
                (apexX, apexY), (apexX + w * 0.100, h * 0.40), (apexX + w * 0.062, h * 0.33),
                (apexX + w * 0.024, h * 0.39), (apexX - w * 0.018, h * 0.325), (apexX - w * 0.056, h * 0.395),
                (apexX - w * 0.096, h * 0.34)
              ),
              "#fbf4f2"
            )
            rule(c, 0.0, waterline, w, h - waterline, "#5f7fa4")
            poly(
              List((apexX - half * 0.62, waterline), (apexX, h * 0.94), (apexX + half * 0.62, waterline)),
              "#6f8db0"
            )
            rule(c, 0.0, waterline, w, 5.0, "#eddfe0")
            (0 until 4).foreach(i =>
              rule(c, w * (0.05 + i * 0.23), waterline + 26.0 + i * 16.0, w * 0.11, 5.0, "#8fa8c4")
            )
            centered(c, "蓬莱湯", w * 0.795, h * 0.34, w * 0.30, 100.0, "#20364f", "bold", 10.0)
            c.font = s"600 30px $JpFont"
            c.fillStyle = "#456484"
            c.textAlign = "center"
            c.textBaseline = "middle"
            c.fillText("ほ う ら い ゆ", w * 0.795, h * 0.56)
        ))

  def ashiyuPlate(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("ashiyuPlate",
      () =>
        make(384, 512, (c, w, h) =>
            c.fillStyle = "#efe6d2"
            c.fillRect(0.0, 0.0, w, h)
            c.strokeStyle = "#8a6a44"
            c.lineWidth = 14.0
            c.strokeRect(7.0, 7.0, w - 14.0, h - 14.0)
            centered(c, "足湯", w / 2.0, 96.0, w - 90.0, 96.0, "#25506b", "bold", 12.0)
            rule(c, 54.0, 152.0, w - 108.0, 4.0, "#b9a988")
            centered(c, "ご自由にどうぞ", w / 2.0, 204.0, w - 70.0, 44.0, "#4a4050")
            c.textAlign = "left"
            c.textBaseline = "middle"
            c.font = s"500 30px $JpFont"
            c.fillStyle = "#5a5262"
            Vector("りようじかん", "六時 — 二十二時", "湯温 四十一度").zipWithIndex.foreach((line, i) =>
              c.fillText(line, 52.0, 282.0 + i * 52.0)
            )
            c.globalAlpha = 0.5
            centered(c, "湯の坂町内会", w / 2.0, h - 58.0, w - 90.0, 28.0, "#6a6070", "500")
            c.globalAlpha = 1.0
        ))

  def tatamiRoom(variant: Int): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"tatamiRoom$variant",
      () =>
        make(512, 320, (c, w, h) =>
            val floorY = h * 0.62
            c.fillStyle = if variant == 1 then "#c9b489" else "#cbbc93"
            c.fillRect(0.0, 0.0, w, h)
            c.fillStyle = "#e8dcc0"
            c.fillRect(w * 0.08, h * 0.06, w * 0.84, floorY - h * 0.06)
            c.fillStyle = "#a08d68"
            (0 to 6).foreach(i =>
              c.fillRect(w * 0.08 + i * w * 0.84 / 6.0 - 3.0, h * 0.06, 6.0, floorY - h * 0.06)
            )
            (0 to 4).foreach(i =>
              c.fillRect(w * 0.08, h * 0.06 + i * (floorY - h * 0.06) / 4.0 - 3.0, w * 0.84, 6.0)
            )
            c.fillStyle = "#b6b184"
            c.fillRect(0.0, floorY, w, h - floorY)
            c.fillStyle = "#a8a377"
            (0 until 4).foreach(i =>
              c.fillRect(i * (w / 4.0) + 5.0, floorY + 6.0, w / 4.0 - 10.0, h - floorY - 12.0)
            )
            c.fillStyle = "#5b5340"
            (1 until 4).foreach(i => c.fillRect(i * (w / 4.0) - 3.0, floorY, 6.0, h - floorY))
            c.fillRect(0.0, floorY - 4.0, w, 7.0)
            c.fillStyle = "#6b4a33"
            c.fillRect(w * 0.30, floorY + 26.0, w * 0.40, 26.0)
            c.fillStyle = "#54382a"
            c.fillRect(w * 0.33, floorY + 52.0, 14.0, 34.0)
            c.fillRect(w * 0.65, floorY + 52.0, 14.0, 34.0)
            c.fillStyle = if variant == 1 then "#8a6a7a" else "#4f6b70"
            c.fillRect(w * 0.19, floorY + 44.0, 62.0, 24.0)
            c.fillRect(w * 0.72, floorY + 44.0, 62.0, 24.0)
            val lampX = if variant == 1 then w * 0.16 else w * 0.84
            val bloom = c.createRadialGradient(lampX, h * 0.34, 8.0, lampX, h * 0.34, 150.0)
            bloom.addColorStop(0.0, "rgba(255,226,170,0.95)")
            bloom.addColorStop(1.0, "rgba(255,226,170,0)")
            c.fillStyle = bloom
            c.fillRect(lampX - 150.0, h * 0.34 - 150.0, 300.0, 300.0)
            c.fillStyle = "#f6dfae"
            c.fillRect(lampX - 26.0, h * 0.30, 52.0, 60.0)
            c.fillStyle = "#3f3a44"
            c.fillRect(lampX - 30.0, h * 0.30 - 10.0, 60.0, 12.0)
        ))

  def onsenLanternTex(variant: Int): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"onsenLantern$variant",
      () =>
        make(256, 256, (c, w, h) =>
            c.fillStyle = if variant == 1 then "#f6e6c6" else "#faf0dc"
            c.fillRect(0.0, 0.0, w, h)
            c.fillStyle = "rgba(176,152,112,0.45)"
            (0 until 9).foreach(i => c.fillRect(0.0, 12.0 + i * 28.0, w, 3.0))
            val words = Vector("湯", "湯の坂", "蓬莱湯")
            val word  = words(variant % words.length)
            centered(c, word, w / 2.0, h / 2.0, w - 66.0, if word.length == 1 then 150.0 else 90.0,
              if variant == 1 then "#2c3a52" else "#b5322f", "bold", 6.0)
        ))

  def yunosakaBoard(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("yunosakaBoard",
      () =>
        make(640, 400, (c, w, h) =>
            c.fillStyle = "#e8dcc2"
            c.fillRect(0.0, 0.0, w, h)
            c.strokeStyle = "#7a6144"
            c.lineWidth = 12.0
            c.strokeRect(6.0, 6.0, w - 12.0, h - 12.0)
            centered(c, "湯の坂", w / 2.0, 74.0, w - 140.0, 76.0, "#2c3a52", "bold", 14.0)
            c.font = s"600 26px $JpFont"
            c.fillStyle = "#6a5f70"
            c.textAlign = "center"
            c.textBaseline = "middle"
            c.fillText("ゆ の さ か", w / 2.0, 122.0)
            rule(c, 70.0, 150.0, w - 140.0, 3.0, "#b9a988")
            rule(c, 80.0, 246.0, w - 160.0, 12.0, "#b3aec0")
            rule(c, 300.0, 176.0, 10.0, 152.0, "#8fb6cc")
            c.fillStyle = "#8d8598"
            Vector(
              (96.0, 96.0, true), (212.0, 62.0, true), (330.0, 78.0, true), (120.0, 74.0, false), (230.0, 58.0, false),
              (336.0, 70.0, false), (426.0, 62.0, false)
            ).foreach { (x, blockWidth, upper) =>
              c.fillRect(x, if upper then 196.0 else 264.0, blockWidth, 42.0)
            }
            c.font = s"600 22px $JpFont"
            c.fillStyle = "#4a4050"
            c.fillText("現在地", 500.0, 302.0)
            c.fillStyle = "#b5322f"
            c.beginPath()
            c.arc(500.0, 264.0, 11.0, 0.0, math.Pi * 2.0)
            c.fill()
            c.globalAlpha = 0.55
            centered(c, "足元にご注意ください", w / 2.0, h - 44.0, w - 120.0, 26.0, "#6a6070", "500")
            c.globalAlpha = 1.0
        ))
  final private case class ClubPosterStyle(
    bg: String,
    bar: String,
    title: String,
    sub: String,
    foot: String
  )

  /** Club recruitment sheets pinned to the station notice board: one flat illustration band of
    * bars and dots, never a figure, so nothing here reads as a person.
    */
  def clubPoster(variant: Int): CanvasTexture[dom.HTMLCanvasElement] =
    val sets = Vector(
      ClubPosterStyle("#fdf6e6", "#e0453f", "吹奏楽部", "新入部員 募集中", "毎週 火・木・土"),
      ClubPosterStyle("#eef5fd", "#3d6ec4", "天文観測会", "四月十九日 夜", "屋上にて  雨天中止"),
      ClubPosterStyle("#f2fbef", "#3f7f60", "園芸部", "花壇の当番表", "水やりを わすれずに"),
      ClubPosterStyle("#fdeef1", "#8f6fb5", "春季大会", "応援よろしく", "第二グラウンド"),
      ClubPosterStyle("#fdf1e2", "#ef8a3c", "図書委員会", "返却は 期限までに", "貸出 三冊まで"),
      ClubPosterStyle("#eefaf8", "#2f9c9a", "剣道部", "見学 いつでも", "体育館 第二フロア")
    )
    cached(s"clubPoster$variant",
      () =>
        make(384, 544, (c, w, h) =>
            val st = sets(variant % sets.length)
            c.fillStyle = st.bg
            c.fillRect(0.0, 0.0, w, h)
            rule(c, 0.0, 0.0, w, 30.0, st.bar)
            rule(c, 0.0, h - 22.0, w, 22.0, st.bar)
            centered(c, st.title, w / 2.0, 128.0, w - 46.0, 92.0, st.bar, "bold", 6.0)
            centered(c, st.sub, w / 2.0, 226.0, w - 60.0, 50.0, "#4b4757")
            c.fillStyle = "#e7e1d6"
            c.fillRect(38.0, 272.0, w - 76.0, 150.0)
            c.fillStyle = st.bar
            c.globalAlpha = 0.7
            (0 until 4).foreach { i =>
              c.fillRect(58.0 + i * 74.0, 300.0 + (i % 2) * 26.0, 46.0, 96.0 - (i % 2) * 30.0)
            }
            c.globalAlpha = 1.0
            centered(c, st.foot, w / 2.0, 470.0, w - 60.0, 38.0, "#6a6577", "600")
        ))
  end clubPoster

  /** The 児童公園 notice board: the rules of a small park, in the voice of the 町内会. */
  def parkSign(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("parkSign",
      () =>
        make(512, 384, (c, w, h) =>
            c.fillStyle = "#fbfaf4"
            c.fillRect(0.0, 0.0, w, h)
            rule(c, 0.0, 0.0, w, 26.0, "#3f7f60")
            centered(c, "ひばり台 第二児童公園", w / 2.0, 96.0, w - 50.0, 62.0, "#37684b", "bold", 4.0)
            c.globalAlpha = 0.4
            rule(c, 50.0, 140.0, w - 100.0, 3.0, "#9aa39a")
            c.globalAlpha = 1.0
            val lines = Vector("・ボール遊びは ひろばで", "・花火は きんし", "・ゴミは もちかえりましょう")
            c.textAlign = "left"
            c.textBaseline = "middle"
            lines.zipWithIndex.foreach { case (text, i) =>
              c.font = s"600 34px $JpFont"
              c.fillStyle = "#4b4757"
              c.fillText(text, 44.0, 196.0 + i * 54.0)
            }
            centered(c, "町内会", w / 2.0, 352.0, w - 80.0, 26.0, "#8a8696", "600")
        ))

  /** ひばり荘's entrance plate, the walk-up's only name. */
  def apartmentPlate(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("apartmentPlate",
      () =>
        make(512, 160, (c, w, h) =>
            c.fillStyle = "#f6f3ee"
            c.fillRect(0.0, 0.0, w, h)
            rule(c, 0.0, h - 14.0, w, 14.0, "#8f6fb5")
            centered(c, "ひばり荘", w * 0.4, h * 0.48, w * 0.5, 82.0, "#4b4757", "bold", 8.0)
            c.font = s"600 26px $JpFont"
            c.fillStyle = "#8f8a9c"
            c.textAlign = "left"
            c.textBaseline = "middle"
            c.fillText("HIBARI  SO", w * 0.68, h * 0.48)
        ))

  /** さくら坂 いっぷく処: the corner sign over the vending pocket. */
  def cornerPlate(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("cornerPlate",
      () =>
        make(640, 190, (c, w, h) =>
            c.fillStyle = "#f4f1e6"
            c.fillRect(0.0, 0.0, w, h)
            rule(c, 0.0, 0.0, w, 12.0, "#2f9c9a")
            rule(c, 0.0, h - 12.0, w, 12.0, "#2f9c9a")
            rule(c, 22.0, 30.0, 8.0, h - 60.0, "#d8d2c4")
            centered(c, "さくら坂 いっぷく処", w * 0.52, h * 0.42, w - 90.0, 62.0, "#2f5b52", "bold", 5.0)
            c.font = s"500 26px $JpFont"
            c.fillStyle = "#8a8b82"
            c.textAlign = "center"
            c.textBaseline = "middle"
            c.fillText("ごみは お持ちかえりください", w * 0.52, h * 0.72)
        ))

  /** ひばり台 夏まつり: the banner over the festival ground's entrance. */
  def matsuriBanner(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("matsuriBanner",
      () =>
        make(1536, 224, (c, w, h) =>
            c.fillStyle = "#f6efdc"
            c.fillRect(0.0, 0.0, w, h)
            rule(c, 0.0, 0.0, w, 18.0, "#b5322f")
            rule(c, 0.0, h - 18.0, w, 18.0, "#b5322f")
            centered(c, "ひばり台 夏まつり", w * 0.42, h * 0.5, w * 0.56, 118.0, "#a5302c", "bold", 12.0)
            c.font = s"bold 46px $JpFont"
            c.fillStyle = "#2f5b52"
            c.textAlign = "left"
            c.textBaseline = "middle"
            c.fillText("八月十日  ゆうがた 五時", w * 0.7, h * 0.42)
            c.font = s"600 30px $JpFont"
            c.fillStyle = "#8a8696"
            c.fillText("桜守神社  ・  ひばり台町内会", w * 0.7, h * 0.7)
        ))

  /** The timber programme board, brush-written down the side of the ground. */
  def matsuriBoard(): CanvasTexture[dom.HTMLCanvasElement] =
    cached("matsuriBoard",
      () =>
        make(512, 704, (c, w, h) =>
            c.fillStyle = "#e9dcbc"
            c.fillRect(0.0, 0.0, w, h)
            c.strokeStyle = "#a8845c"
            c.lineWidth = 14.0
            c.strokeRect(7.0, 7.0, w - 14.0, h - 14.0)
            rule(c, 0.0, 0.0, w, 20.0, "#b5322f")
            centered(c, "夏まつり 次第", w / 2.0, 74.0, w - 70.0, 62.0, "#a5302c", "bold", 6.0)
            c.globalAlpha = 0.4
            rule(c, 48.0, 116.0, w - 96.0, 3.0, "#9c8f6e")
            c.globalAlpha = 1.0
            val rows = Vector(
              ("五時", "たいこ"), ("五時半", "よてん ひらく"), ("六時", "ぼんおどり"), ("七時", "きんぎょすくい"), ("八時", "はなび"), ("九時", "おひらき")
            )
            c.textBaseline = "middle"
            rows.zipWithIndex.foreach { case ((left, right), i) =>
              c.font = s"600 34px $JpFont"
              c.fillStyle = "#4b4335"
              c.textAlign = "left"
              c.fillText(left, 44.0, 172.0 + i * 68.0)
              c.textAlign = "right"
              c.fillText(right, w - 44.0, 172.0 + i * 68.0)
              c.globalAlpha = 0.3
              rule(c, 44.0, 200.0 + i * 68.0, w - 88.0, 2.0, "#9c8f6e")
              c.globalAlpha = 1.0
            }
            centered(c, "あめの ばあいは 翌日", w / 2.0, 640.0, w - 60.0, 28.0, "#8a8069", "600")
        ))

  /** The strung flags: goldfish, a firework and 祭, one shape per flag. */
  def matsuriFlag(variant: Int): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"matsuriFlag$variant",
      () =>
        make(256, 320, (c, w, h) =>
            val v = variant % 3
            if v == 0 then
              c.fillStyle = "#f4f7fa"
              c.fillRect(0.0, 0.0, w, h)
              rule(c, 0.0, 0.0, w, 22.0, "#2f7fd0")
              (0 until 3).foreach { i =>
                val cx = 62.0 + (i % 2) * 96.0
                val cy = 96.0 + i * 74.0
                c.fillStyle = if i == 1 then "#e0574a" else "#ef8a3c"
                c.beginPath()
                c.ellipse(cx, cy, 34.0, 21.0, 0.0, 0.0, math.Pi * 2.0)
                c.fill()
                c.beginPath()
                c.moveTo(cx - 30.0, cy)
                c.lineTo(cx - 62.0, cy - 22.0)
                c.lineTo(cx - 56.0, cy)
                c.lineTo(cx - 62.0, cy + 22.0)
                c.closePath()
                c.fill()
                c.fillStyle = "#3c3a46"
                c.beginPath()
                c.arc(cx + 17.0, cy - 5.0, 4.5, 0.0, math.Pi * 2.0)
                c.fill()
              }
            else if v == 1 then
              c.fillStyle = "#2f3550"
              c.fillRect(0.0, 0.0, w, h)
              c.strokeStyle = "#f4c033"
              c.lineWidth = 5.0
              (0 until 16).foreach { i =>
                val a = (i / 16.0) * math.Pi * 2.0
                c.beginPath()
                c.moveTo(w / 2.0 + math.cos(a) * 26.0, h * 0.42 + math.sin(a) * 26.0)
                c.lineTo(w / 2.0 + math.cos(a) * 88.0, h * 0.42 + math.sin(a) * 88.0)
                c.stroke()
                c.fillStyle = if i % 2 != 0 then "#ef8a3c" else "#e86f9c"
                c.beginPath()
                c.arc(w / 2.0 + math.cos(a) * 98.0, h * 0.42 + math.sin(a) * 98.0, 7.0, 0.0, math.Pi * 2.0)
                c.fill()
              }
              centered(c, "はなび", w / 2.0, h * 0.86, w - 70.0, 44.0, "#f2e8d6", "bold", 6.0)
            else
              c.fillStyle = "#b5322f"
              c.fillRect(0.0, 0.0, w, h)
              c.fillStyle = "#f6efdc"
              c.fillRect(12.0, 12.0, w - 24.0, h - 24.0)
              centered(c, "祭", w / 2.0, h * 0.46, w - 70.0, 168.0, "#a5302c", "bold")
              centered(c, "ひばり台", w / 2.0, h * 0.84, w - 80.0, 34.0, "#8a4a44", "600", 3.0)
            end if
        ))

  /** The paper sign over a folding stall. */
  def stallSign(variant: Int): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"stallSign$variant",
      () =>
        make(640, 200, (c, w, h) =>
            val sets = Vector(
              ("#f6efdc", "#b5322f", "#a5302c", "たこ焼き", "一舟 四〇〇円"), ("#eaf4fa", "#2f7fd0", "#20509e", "かき氷", "いちご ・ めろん"),
              ("#fdf4e2", "#ef8a3c", "#a3531c", "金魚すくい", "一回 三〇〇円"), ("#f2f7ee", "#3f7f60", "#37684b", "射的", "たまは 五つ"),
              ("#fdeef1", "#f0a3c0", "#8a4a62", "わたあめ", "二〇〇円")
            )
            val (bg, bar, fg, title, sub) = sets(variant % sets.length)
            c.fillStyle = bg
            c.fillRect(0.0, 0.0, w, h)
            rule(c, 0.0, 0.0, w, 14.0, bar)
            rule(c, 0.0, h - 14.0, w, 14.0, bar)
            centered(c, title, w * 0.4, h * 0.5, w * 0.56, 104.0, fg, "bold", 8.0)
            c.font = s"600 34px $JpFont"
            c.fillStyle = "#6a6577"
            c.textAlign = "left"
            c.textBaseline = "middle"
            c.fillText(sub, w * 0.72, h * 0.52)
        ))

  /** Temporary works plate: the ground is being set up, not used. */
  def setupPlate(variant: Int): CanvasTexture[dom.HTMLCanvasElement] =
    cached(s"setupPlate$variant",
      () =>
        make(384, 288, (c, w, h) =>
            val sets = Vector(
              ("#f4c033", "準備中", Vector("たちいり", "ごえんりょ", "ください")), ("#36527f", "電源", Vector("さわらないで", "ください", "町内会"))
            )
            val (bar, title, lines) = sets(variant % sets.length)
            c.fillStyle = "#fbfaf4"
            c.fillRect(0.0, 0.0, w, h)
            rule(c, 0.0, 0.0, w, 30.0, bar)
            rule(c, 0.0, h - 14.0, w, 14.0, bar)
            centered(c, title, w / 2.0, 92.0, w - 60.0, 72.0, "#4b4757", "bold", 6.0)
            c.textAlign = "center"
            c.textBaseline = "middle"
            lines.zipWithIndex.foreach { case (text, i) =>
              c.font = s"600 30px $JpFont"
              c.fillStyle = "#6a6577"
              c.fillText(text, w / 2.0, 156.0 + i * 42.0)
            }
        ))

end Textures
