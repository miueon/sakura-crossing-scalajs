package sakura

import org.scalajs.dom
import sakura.ThreeInterop.*
import vendor.three.*

import scala.math
import scala.scalajs.js

/** Canvas2D synthesis for every painted surface of the scene.
  *
  * Each generator is a pure pattern model plus a draw pass that emits it: band phases, the grain
  * lattice and the plate layout are decided in the model, so they are testable without a DOM and
  * the painted pixels cannot drift from the tested values. Nothing here loads an image, which is
  * PRD-81's zero-asset constraint.
  */
object ProceduralTextures:

  val WarningYellow: String = "#f0b800"
  val WarningBlack: String  = "#222426"
  val RoadMarking: String   = "#f2f0ec"

  val TomareRoadKey: String         = "road-tomare"
  val ManholeKey: String            = "road-manhole"
  val TactilePavingKey: String      = "tactile-paving"
  val TactilePavingNorthKey: String = "tactile-paving-north"
  val AozoraSignKey: String         = "aozora-sign"
  val SoraTeaHeaderKey: String      = "vending-sora-tea"
  val NagomiHeaderKey: String       = "vending-nagomi"
  val UtilityWarningKey: String     = "utility-warning"
  val RoadShadowKey: String         = "road-shadow"
  val VendingCanKey: String         = "vending-can"

  object Tomare:
    val Glyphs: Vector[String]           = Vector("止", "ま", "れ")
    val RoadTextureOrder: Vector[String] = Glyphs.reverse
    val FontSize: Int                    = 126

    def glyphCenters(height: Int): Vector[Double] =
      Glyphs.indices.map(index => height.toDouble * (index + 1) / (Glyphs.size + 1)).toVector
  end Tomare

  /** 45° hazard banding in warning yellow and black.
    *
    * The pattern is a function of the `x - y` phase, so a tile whose sides are whole multiples of
    * [[Period]] repeats without a seam in both directions. One model therefore serves the mast, the
    * road and the sign's border at three different scales.
    */
  object HazardStripes:
    val Period: Int    = 64
    val BandWidth: Int = 32

    /** Stroke width whose 45° band spans exactly [[BandWidth]] of the phase: a stroked line covers
      * `lineWidth / 2` perpendicular, and the phase advances one unit per perpendicular unit.
      */
    val StrokeWidth: Double = BandWidth / math.sqrt(2.0)

    final case class Segment(x0: Double, y0: Double, x1: Double, y1: Double)

    def phase(x: Double, y: Double): Double =
      val raw = (x - y) % Period.toDouble
      if raw < 0.0 then raw + Period.toDouble else raw

    def isBlack(x: Double, y: Double): Boolean = phase(x, y) < BandWidth.toDouble

    /** A tile of this size repeats seamlessly only when its sides are whole numbers of periods. */
    def tiles(width: Int, height: Int): Boolean = width % Period == 0 && height % Period == 0

    /** The bands as stroke segments covering a `width` x `height` tile, each running corner to
      * corner at exactly 45°. `offset` shifts every band along the phase, so a black banding over
      * yellow and its yellow complement over black come out of the one model.
      */
    def segments(width: Int, height: Int, offset: Double = 0.0): Vector[Segment] =
      val tileHeight = height.toDouble
      val first      = math.floor(-(tileHeight + BandWidth.toDouble) / Period.toDouble).toInt * Period
      val count      = ((width.toDouble + tileHeight) / Period.toDouble).ceil.toInt + 2
      (0 until count)
        .map(index => first + index.toDouble * Period + BandWidth / 2.0 + offset)
        .filter(center => center <= width.toDouble && center + tileHeight >= 0.0)
        .map(center => Segment(center, 0.0, center + tileHeight, tileHeight))
        .toVector
  end HazardStripes

  /** Deterministic asphalt grain: a lattice hash rather than a generator, so the same road is drawn
    * on every load without carrying RNG state through the scene.
    */
  object Asphalt:
    final case class Rgb(red: Int, green: Int, blue: Int)

    val Base: Rgb = Rgb(
      (Palette.asphalt >> 16) & 0xff,
      (Palette.asphalt >> 8) & 0xff,
      Palette.asphalt & 0xff
    )
    val FineCell: Int        = 3
    val CoarseCell: Int      = 32
    val FineShade: Int       = 12
    val CoarseShade: Int     = 9
    private val FineSalt     = 0x51
    private val CoarseSalt   = 0x2f
    private val LatticeRange = 127

    /** Grain offset from [[Base]] for one pixel, bounded by `FineShade + CoarseShade`. */
    def shadeAt(x: Int, y: Int): Int =
      val fine   = FineShade * lattice(x / FineCell, y / FineCell, FineSalt)
      val coarse = CoarseShade * lattice(x / CoarseCell, y / CoarseCell, CoarseSalt)
      (fine + coarse) / LatticeRange

    private def lattice(column: Int, row: Int, salt: Int): Int =
      var hash = column * 0x1f1f1f1f ^ row * 0x27d4eb2d ^ salt * 0x2545f491
      hash = (hash ^ (hash >>> 15)) * 0x2545f491
      hash = hash ^ (hash >>> 13)
      ((hash >>> 8) & 0xff) - LatticeRange
  end Asphalt

  /** Painted road markings. The lane line sits on the tile's left edge, so neighbouring repeats
    * join it into one unbroken line; the stop line stays inside the tile so it reads as a marking
    * rather than as a band across the road.
    */
  object RoadMarkings:
    final case class Marking(x: Double, y: Double, width: Double, height: Double)

    val LaneLineWidth: Double = 12.0
    val StopLineDepth: Double = 22.0
    val StopLineTop: Double   = 196.0

    def laneLine(height: Int): Marking = Marking(0.0, 0.0, LaneLineWidth, height.toDouble)

    def stopLine(width: Int): Marking = Marking(0.0, StopLineTop, width.toDouble, StopLineDepth)

  /** The 踏切注意 plate: a hazard-banded frame, the crossbuck emblem across the field, and the
    * wording on a clean band through the point where the blades cross.
    */
  object CrossbuckSign:
    val FontSize: Double = 36.0
    val Font: String =
      s"bold ${FontSize.toInt}px 'Yu Gothic', 'Meiryo', 'Hiragino Kaku Gothic ProN', 'Noto Sans CJK JP', sans-serif"
    val Wording: String = "踏切注意"

    val BladeWidth: Double      = 44.0
    val FrameWidth: Double      = 18.0
    val WordBandHeight: Double  = 62.0
    val WordBandInset: Double   = 10.0
    val WordBandOutline: Double = 5.0
    val BladeAngle: Double      = math.Pi / 4.0

    final case class Rect(x: Double, y: Double, width: Double, height: Double)

    def field(width: Int, height: Int): Rect =
      Rect(FrameWidth, FrameWidth, width - 2.0 * FrameWidth, height - 2.0 * FrameWidth)

    def wordBand(width: Int, height: Int): Rect =
      val plate = field(width, height)
      Rect(plate.x + WordBandInset, (height.toDouble - WordBandHeight) / 2.0, plate.width - 2.0 * WordBandInset,
        WordBandHeight)

    /** Long enough that both blades reach the corners of the field, which is what makes the emblem
      * read as a crossbuck rather than as a fat plus sign.
      */
    def bladeLength(width: Int, height: Int): Double =
      val plate = field(width, height)
      math.hypot(plate.width, plate.height)
  end CrossbuckSign

  /** A noren's hanging panels: the cloth is slit into three, and a name of one to three
    * characters is set one glyph to a panel so no slit can cut through a glyph. Two characters
    * take the outer panels, which is where a real two-character noren carries them.
    */
  object Noren:
    val SlitHalfWidth: Double = 4.0
    val SlitStart: Double     = 0.30

    def slitCenters(width: Double): Vector[Double] =
      Vector(width / 3.0, width * 2.0 / 3.0)

    def panelCenters(width: Double): Vector[Double] =
      Vector(width / 6.0, width / 2.0, width * 5.0 / 6.0)

    /** Glyph centres for a name of any length: one per panel up to three, then a centred row the
      * slits may slice, exactly as long real noren are.
      */
    def glyphXs(text: String, width: Double): Vector[Double] =
      val panels = panelCenters(width)
      text.length match
        case 1 => Vector(panels(1))
        case 2 => Vector(panels(0), panels(2))
        case 3 => panels
        case _ => Vector.tabulate(text.length)(index => width / 2.0 + (index - (text.length - 1) / 2.0) * 118.0)
  end Noren

  /** Layout of a painted fascia board: frame rules at the rails, the name left of centre and the
    * trade in small type at the right-hand column, which is where an owned shop puts it.
    */
  object FasciaBoard:
    val TopRuleHeight: Double    = 10.0
    val BottomRuleHeight: Double = 16.0
    val TitleCenterX: Double     = 0.38
    val SubLeftX: Double         = 0.72

    def titleCenterX(width: Double): Double     = width * TitleCenterX
    def subLeftX(width: Double): Double         = width * SubLeftX
    def insetRuleY(height: Double): Double      = 22.0
    def insetRuleHeight(height: Double): Double = 3.0
  end FasciaBoard

  /** A vertical blade board's glyph column: up to four glyphs, stacked from a head margin down
    * the board at a fixed pitch.
    */
  object BladeBoard:
    val HeadMargin: Double = 0.13
    val GlyphPitch: Double = 1.30

    def glyphYs(text: String, height: Double, glyphSize: Double): Vector[Double] =
      val head = height * HeadMargin
      Vector.tabulate(text.length)(index => head + glyphSize / 2.0 + index * glyphSize * GlyphPitch)
  end BladeBoard

  /** Paper lantern wrap: ribs down the barrel and dark bands at the caps, which is what reads as
    * paper at street distance.
    */
  object LanternPaper:
    val RibCount: Int    = 9
    val CapBand: Double  = 0.08
    val TopInset: Double = 24.0

    def ribYs(height: Double): Vector[Double] =
      val step = (height - 2.0 * TopInset) / (RibCount - 1).toDouble
      Vector.tabulate(RibCount)(index => TopInset + index * step)
  end LanternPaper

  /** Deterministic petal litter: an integer hash per index scatters petals over a rectangle with
    * no RNG state, so the same lane is littered the same way on every load.
    */
  object PetalScatter:
    def placements(
      count: Int,
      x0: Double,
      z0: Double,
      width: Double,
      depth: Double,
      seed: Int
    ): Vector[(Double, Double)] =
      Vector.tabulate(count) { index =>
        val hash = mix(seed, index)
        val u    = (hash & 0xffff).toDouble / 65536.0
        val v    = ((hash >>> 16) & 0xffff).toDouble / 65536.0
        (x0 + u * width, z0 + v * depth)
      }

    private def mix(seed: Int, index: Int): Int =
      var hash = seed * 0x27d4eb2d ^ index * 0x165667b1
      hash = (hash ^ (hash >>> 15)) * 0x2545f491
      hash ^ (hash >>> 13)
  end PetalScatter

  def warningStripeTexture(width: Int = 256, height: Int = 256): CanvasTextureRef =
    requireSeamlessTile(width, height)
    val canvas  = createCanvas(width, height)
    val context = canvasContext(canvas)
    context.fillStyle = WarningYellow
    context.fillRect(0.0, 0.0, width.toDouble, height.toDouble)
    paintBands(context, width, height, WarningBlack, offset = 0.0)
    canvasTexture(canvas, repeatWrap = true)

  def tomareRoadTexture(width: Int = 256, height: Int = 512): CanvasTextureRef =
    val canvas  = createCanvas(width, height)
    val context = canvasContext(canvas)
    context.clearRect(0.0, 0.0, width.toDouble, height.toDouble)
    context.fillStyle = RoadMarking
    context.font =
      s"bold ${Tomare.FontSize}px 'Yu Gothic', 'Meiryo', 'Hiragino Kaku Gothic ProN', 'Noto Sans CJK JP', sans-serif"
    context.textAlign = "center"
    context.textBaseline = "middle"
    Tomare.RoadTextureOrder.zip(Tomare.glyphCenters(height)).foreach { case (glyph, y) =>
      context.fillText(glyph, width / 2.0, y)
    }
    canvasTexture(canvas, repeatWrap = false)

  def roadShadowTexture(size: Int = 512): CanvasTextureRef =
    val canvas  = createCanvas(size, size)
    val context = canvasContext(canvas)
    context.clearRect(0.0, 0.0, size.toDouble, size.toDouble)
    context.fillStyle = "rgba(77, 74, 101, 0.34)"
    List((0.28, 0.42, 0.25, 0.32), (0.55, 0.55, 0.31, 0.23), (0.78, 0.39, 0.18, 0.38)).foreach {
      case (x, y, rx, ry) =>
        context.beginPath()
        context.ellipse(x * size, y * size, rx * size, ry * size, -0.22, 0.0, math.Pi * 2.0)
        context.fill()
    }
    canvasTexture(canvas, repeatWrap = false)

  def vendingCanTexture(width: Int = 128, height: Int = 256): CanvasTextureRef =
    val canvas  = createCanvas(width, height)
    val context = canvasContext(canvas)
    context.fillStyle = "#f3eee3"
    context.fillRect(0.0, 0.0, width.toDouble, height.toDouble)
    context.fillStyle = "#d8382c"
    context.fillRect(0.0, height * 0.18, width.toDouble, height * 0.22)
    context.fillStyle = "#2e9a98"
    context.fillRect(0.0, height * 0.62, width.toDouble, height * 0.18)
    context.fillStyle = "#fff8e8"
    context.font = "bold 36px 'Yu Gothic', 'Meiryo', sans-serif"
    context.textAlign = "center"
    context.textBaseline = "middle"
    context.fillText("茶", width / 2.0, height * 0.29)
    context.fillText("水", width / 2.0, height * 0.71)
    context.fillStyle = "#c5c1c8"
    context.fillRect(width * 0.16, height * 0.91, width * 0.68, 5.0)
    canvasTexture(canvas, repeatWrap = false)

  def manholeTexture(size: Int = 256): CanvasTextureRef =
    val canvas  = createCanvas(size, size)
    val context = canvasContext(canvas)
    val center  = size / 2.0
    context.clearRect(0.0, 0.0, size.toDouble, size.toDouble)
    context.fillStyle = "#777487"
    context.beginPath()
    context.arc(center, center, size * 0.43, 0.0, math.Pi * 2.0)
    context.fill()
    context.strokeStyle = "#383744"
    context.lineWidth = size * 0.035
    context.beginPath()
    context.arc(center, center, size * 0.43, 0.0, math.Pi * 2.0)
    context.stroke()
    context.strokeStyle = "#a7a3b0"
    context.lineWidth = size * 0.018
    context.beginPath()
    context.arc(center, center, size * 0.34, 0.0, math.Pi * 2.0)
    context.stroke()
    context.strokeStyle = "#4d4b59"
    context.lineWidth = size * 0.018
    (0 until 8).foreach { index =>
      val angle = index * math.Pi / 4.0
      context.beginPath()
      context.moveTo(center + math.cos(angle) * size * 0.08, center + math.sin(angle) * size * 0.08)
      context.lineTo(center + math.cos(angle) * size * 0.29, center + math.sin(angle) * size * 0.29)
      context.stroke()
    }
    canvasTexture(canvas, repeatWrap = false)
  end manholeTexture

  def tactilePavingTexture(size: Int = 128): CanvasTextureRef =
    val canvas  = createCanvas(size, size)
    val context = canvasContext(canvas)
    context.fillStyle = "#e6bd42"
    context.fillRect(0.0, 0.0, size.toDouble, size.toDouble)
    context.fillStyle = "#f5d66d"
    (0 until 4).foreach { row =>
      (0 until 4).foreach { column =>
        context.beginPath()
        context.arc(size * (column + 0.5) / 4.0, size * (row + 0.5) / 4.0, size * 0.075, 0.0, math.Pi * 2.0)
        context.fill()
      }
    }
    canvasTexture(canvas, repeatWrap = true)

  def aozoraSignTexture(width: Int = 512, height: Int = 144): CanvasTextureRef =
    val canvas  = createCanvas(width, height)
    val context = canvasContext(canvas)
    context.fillStyle = "#fff8e8"
    context.fillRect(0.0, 0.0, width.toDouble, height.toDouble)
    context.strokeStyle = "#d8382c"
    context.lineWidth = 10.0
    context.strokeRect(5.0, 5.0, width - 10.0, height - 10.0)
    context.fillStyle = "#254f91"
    context.font =
      s"bold ${height * 0.54}px 'Yu Gothic', 'Meiryo', 'Hiragino Kaku Gothic ProN', 'Noto Sans CJK JP', sans-serif"
    context.textAlign = "center"
    context.textBaseline = "middle"
    context.fillText("青空商店", width * 0.48, height * 0.49)
    context.fillStyle = "#6e6a72"
    context.font = s"600 ${height * 0.15}px sans-serif"
    context.fillText("AOZORA STORE", width * 0.5, height * 0.84)
    canvasTexture(canvas, repeatWrap = false)

  def vendingHeaderTexture(brand: String, background: String, foreground: String): CanvasTextureRef =
    val width   = 512
    val height  = 160
    val canvas  = createCanvas(width, height)
    val context = canvasContext(canvas)
    context.fillStyle = background
    context.fillRect(0.0, 0.0, width.toDouble, height.toDouble)
    context.fillStyle = foreground
    context.font = "bold 104px 'Yu Gothic', 'Meiryo', 'Hiragino Kaku Gothic ProN', 'Noto Sans CJK JP', sans-serif"
    context.textAlign = "center"
    context.textBaseline = "middle"
    context.fillText(brand, width * 0.42, height * 0.52)
    context.beginPath()
    context.arc(width * 0.82, height * 0.5, 43.0, 0.0, math.Pi * 2.0)
    context.fill()
    context.fillStyle = background
    context.beginPath()
    context.arc(width * 0.82, height * 0.5, 24.0, 0.0, math.Pi * 2.0)
    context.fill()
    canvasTexture(canvas, repeatWrap = false)
  end vendingHeaderTexture

  def utilityWarningTexture(width: Int = 128, height: Int = 384): CanvasTextureRef =
    val canvas  = createCanvas(width, height)
    val context = canvasContext(canvas)
    context.fillStyle = "#d8382c"
    context.fillRect(0.0, 0.0, width.toDouble, height.toDouble)
    context.strokeStyle = "#f8f2e8"
    context.lineWidth = 6.0
    context.strokeRect(5.0, 5.0, width - 10.0, height - 10.0)
    context.fillStyle = "#fff8e8"
    context.font = "bold 58px 'Yu Gothic', 'Meiryo', 'Hiragino Kaku Gothic ProN', 'Noto Sans CJK JP', sans-serif"
    context.textAlign = "center"
    context.textBaseline = "middle"
    context.fillText("注", width / 2.0, height * 0.35)
    context.fillText("意", width / 2.0, height * 0.68)
    canvasTexture(canvas, repeatWrap = false)

  def crossbuckSignTexture(width: Int = 256, height: Int = 256): CanvasTextureRef =
    val canvas  = createCanvas(width, height)
    val context = canvasContext(canvas)
    context.fillStyle = WarningBlack
    context.fillRect(0.0, 0.0, width.toDouble, height.toDouble)
    paintBands(context, width, height, WarningYellow, offset = HazardStripes.BandWidth.toDouble)
    paintCrossbuck(context, width, height)
    paintWording(context, width, height)
    canvasTexture(canvas, repeatWrap = false)

  def asphaltRoadTexture(width: Int = 256, height: Int = 256): CanvasTextureRef =
    val canvas  = createCanvas(width, height)
    val context = canvasContext(canvas)
    paintAsphaltGrain(context, width, height)
    paintRoadMarkings(context, width, height)
    canvasTexture(canvas, repeatWrap = true)

  /** Cloth noren for the soba shop: a deep green field, the name one glyph to a panel, and the
    * panel slits cut through with destination-out so they read as cloth gaps, not painted lines.
    */
  def sobaNorenTexture(width: Int = 512, height: Int = 256): CanvasTextureRef =
    val canvas  = createCanvas(width, height)
    val context = canvasContext(canvas)
    context.fillStyle = "#2f5540"
    context.fillRect(0.0, 0.0, width.toDouble, height.toDouble)
    paintGlyphRow(context, "そば", Noren.glyphXs("そば", width.toDouble), height * 0.46,
      glyphSize = 116.0, color = "#eef3e6")
    cutPanelSlits(context, width, height)
    canvasTexture(canvas, repeatWrap = false)

  /** Painted shop fascia shared by both districts' tenants; only the palette and wording differ. */
  def paintedFasciaTexture(
    background: String,
    ink: String,
    rule: String,
    title: String,
    trade: String,
    latin: String,
    width: Int = 1024,
    height: Int = 224
  ): CanvasTextureRef =
    val canvas  = createCanvas(width, height)
    val context = canvasContext(canvas)
    context.fillStyle = background
    context.fillRect(0.0, 0.0, width.toDouble, height.toDouble)
    paintRule(context, 0.0, 0.0, width.toDouble, FasciaBoard.TopRuleHeight, rule)
    paintRule(context, 0.0, height - FasciaBoard.BottomRuleHeight, width.toDouble, FasciaBoard.BottomRuleHeight, rule)
    context.globalAlpha = 0.35
    paintRule(context, FasciaBoard.insetRuleY(height), FasciaBoard.insetRuleY(height),
      width - 2.0 * FasciaBoard.insetRuleY(height),
      FasciaBoard.insetRuleHeight(height), ink)
    paintRule(context, FasciaBoard.insetRuleY(height),
      height - FasciaBoard.insetRuleY(height) - FasciaBoard.insetRuleHeight(height),
      width - 2.0 * FasciaBoard.insetRuleY(height), FasciaBoard.insetRuleHeight(height), ink)
    context.globalAlpha = 1.0
    paintCentered(context, title, FasciaBoard.titleCenterX(width), height * 0.48, glyphSize = 112.0, color = ink,
      weight = 900, maxWidth = width * 0.60)
    paintCentered(context, trade, FasciaBoard.subLeftX(width) + 90.0, height * 0.36, glyphSize = 34.0, color = ink,
      weight = 600, align = "left")
    paintCentered(context, latin, FasciaBoard.subLeftX(width) + 74.0, height * 0.68, glyphSize = 25.0, color = ink,
      weight = 500, align = "left")
    canvasTexture(canvas, repeatWrap = false)
  end paintedFasciaTexture

  /** Vertical projecting blade sign. `grain` draws the bare-timber look the onsen street's boards
    * have; a shopping street's blade is enamel-painted and takes the plain field.
    */
  def bladeSignTexture(
    title: String,
    background: String,
    ink: String,
    grain: Boolean,
    width: Int = 192,
    height: Int = 768
  ): CanvasTextureRef =
    val canvas  = createCanvas(width, height)
    val context = canvasContext(canvas)
    context.fillStyle = background
    context.fillRect(0.0, 0.0, width.toDouble, height.toDouble)
    if grain then paintWoodGrain(context, width, height)
    paintRule(context, 0.0, 0.0, width.toDouble, 14.0, if grain then "#8a6a44" else ink)
    paintRule(context, 0.0, height - 14.0, width.toDouble, 14.0, if grain then "#8a6a44" else ink)
    val glyphSize = 104.0
    val glyphYs   = BladeBoard.glyphYs(title, height.toDouble, glyphSize)
    paintGlyphRow(context, title, Vector.fill(glyphYs.size)(width / 2.0), 0.0,
      glyphSize = glyphSize, color = ink, ys = glyphYs)
    canvasTexture(canvas, repeatWrap = false)
  end bladeSignTexture

  /** Paper lantern wrap: cream paper, ribs, the festival marking, and dark cap bands so the
    * cylinder's ends read as closed.
    */
  def lanternTexture(
    wording: String,
    ink: String,
    paper: String,
    width: Int = 256,
    height: Int = 256
  ): CanvasTextureRef =
    val canvas  = createCanvas(width, height)
    val context = canvasContext(canvas)
    context.fillStyle = paper
    context.fillRect(0.0, 0.0, width.toDouble, height.toDouble)
    context.fillStyle = "rgba(180,160,124,0.5)"
    LanternPaper.ribYs(height.toDouble).foreach { y =>
      context.fillRect(0.0, y, width.toDouble, 3.0)
    }
    paintCentered(context, wording, width / 2.0, height * 0.5, glyphSize = if wording.length == 1 then 150.0 else 92.0,
      color = ink, weight = 800)
    context.fillStyle = "#3a3540"
    context.fillRect(0.0, 0.0, width.toDouble, height * LanternPaper.CapBand)
    context.fillRect(0.0, height * (1.0 - LanternPaper.CapBand), width.toDouble, height * LanternPaper.CapBand)
    canvasTexture(canvas, repeatWrap = true)
  end lanternTexture

  /** The sweets window sign: a small cream plate with the trade in plum. */
  def kanmiSignTexture(width: Int = 256, height: Int = 128): CanvasTextureRef =
    val canvas  = createCanvas(width, height)
    val context = canvasContext(canvas)
    context.fillStyle = "#efe2c8"
    context.fillRect(0.0, 0.0, width.toDouble, height.toDouble)
    paintCentered(context, "甘味", width / 2.0, height * 0.5, glyphSize = 72.0, color = "#8a4a62", weight = 800)
    canvasTexture(canvas, repeatWrap = false)

  /** Vending machine front: brand band, lit product grid, coin slot. */
  def vendingPanelTexture(width: Int = 256, height: Int = 384): CanvasTextureRef =
    val canvas  = createCanvas(width, height)
    val context = canvasContext(canvas)
    context.fillStyle = "#2c3a52"
    context.fillRect(0.0, 0.0, width.toDouble, height.toDouble)
    context.fillStyle = "#f4c033"
    context.fillRect(0.0, 0.0, width.toDouble, 54.0)
    paintCentered(context, "つめた〜い", width / 2.0, 27.0, glyphSize = 30.0, color = "#2c3a52", weight = 800)
    val columns = List("#e0453f", "#3d6ec4", "#f4c033", "#2f9c9a", "#e86f9c", "#ef8a3c")
    for
      row    <- 0 until 4
      column <- 0 until 3
    do
      val x = 24.0 + column * 74.0
      val y = 74.0 + row * 62.0
      context.fillStyle = "#f6f2e8"
      context.fillRect(x, y, 58.0, 46.0)
      context.fillStyle = columns((row * 3 + column) % columns.size)
      context.fillRect(x + 7.0, y + 8.0, 44.0, 30.0)
    context.fillStyle = "#1d2636"
    context.fillRect(24.0, 330.0, 208.0, 34.0)
    context.fillStyle = "#88929e"
    context.fillRect(96.0, 340.0, 64.0, 12.0)
    canvasTexture(canvas, repeatWrap = false)
  end vendingPanelTexture

  /** Kei commercial plate: yellow field, black hiragana and number. */
  def keiPlateTexture(width: Int = 256, height: Int = 128): CanvasTextureRef =
    val canvas  = createCanvas(width, height)
    val context = canvasContext(canvas)
    context.fillStyle = "#f4c033"
    context.fillRect(0.0, 0.0, width.toDouble, height.toDouble)
    context.strokeStyle = "#222426"
    context.lineWidth = 8.0
    context.strokeRect(4.0, 4.0, width - 8.0, height - 8.0)
    paintCentered(context, "さ 21-08", width / 2.0, height * 0.52, glyphSize = 58.0, color = "#222426", weight = 800)
    canvasTexture(canvas, repeatWrap = false)

  def shopFlagTexture(variant: Int, width: Int = 128, height: Int = 256): CanvasTextureRef =
    val styles = Vector(
      ("#b5322f", "#fdf6ec", "アイス"), ("#3d6ec4", "#fdf6ec", "つめたい"), ("#f4c033", "#3c3a46", "しんはつばい"),
      ("#2f9c9a", "#fdf6ec", "おべんとう")
    )
    val (background, foreground, wording) = styles(variant % styles.size)
    val canvas                            = createCanvas(width, height)
    val context                           = canvasContext(canvas)
    context.fillStyle = background
    context.fillRect(0.0, 0.0, width.toDouble, height.toDouble)
    context.fillStyle = "#fdf8f0"
    context.fillRect(10.0, 10.0, width - 20.0, height - 20.0)
    context.fillStyle = background
    context.fillRect(10.0, 10.0, width - 20.0, 70.0)
    val glyphSize = 52.0
    paintGlyphRow(context, wording, Vector.fill(wording.length)(width / 2.0), 0.0, glyphSize = glyphSize,
      color = foreground, ys = BladeBoard.glyphYs(wording, height.toDouble, glyphSize))
    canvasTexture(canvas, repeatWrap = false)

  def iceFlagTexture(width: Int = 128, height: Int = 256): CanvasTextureRef =
    shopFlagTexture(0, width, height)

  def freezerIceTexture(width: Int = 256, height: Int = 128): CanvasTextureRef =
    val canvas  = createCanvas(width, height)
    val context = canvasContext(canvas)
    context.fillStyle = "#fffdf7"
    context.fillRect(0.0, 0.0, width.toDouble, height.toDouble)
    context.fillStyle = "#b5322f"
    context.fillRect(0.0, height * 0.76, width.toDouble, height * 0.24)
    context.strokeStyle = "#b5322f"
    context.lineWidth = 7.0
    context.strokeRect(4.0, 4.0, width - 8.0, height - 8.0)
    paintCentered(context, "アイス", width / 2.0, height * 0.43, glyphSize = 56.0, color = "#b5322f", weight = 800)
    canvasTexture(canvas, repeatWrap = false)

  def gachaponFasciaTexture(width: Int = 256, height: Int = 256): CanvasTextureRef =
    val canvas  = createCanvas(width, height)
    val context = canvasContext(canvas)
    context.fillStyle = "#f6f2ea"
    context.fillRect(0.0, 0.0, width.toDouble, height.toDouble)
    context.fillStyle = "#e89428"
    context.fillRect(0.0, 0.0, width.toDouble, 48.0)
    paintCentered(context, "ガチャ", width / 2.0, 24.0, glyphSize = 34.0, color = "#fdf6ec", weight = 800)
    val capsuleColors = Vector("#e0453f", "#f4c033", "#3d6ec4", "#2f9c9a", "#ef8a3c", "#e86f9c")
    (0 until 18).foreach { index =>
      val x = 28.0 + (index * 47 % 200)
      val y = 76.0 + (index * 61 % 128)
      context.fillStyle = capsuleColors(index % capsuleColors.size)
      context.beginPath()
      context.arc(x, y, 15.0, 0.0, math.Pi * 2.0)
      context.fill()
      context.strokeStyle = "rgba(42, 40, 38, 0.6)"
      context.lineWidth = 2.0
      context.beginPath()
      context.moveTo(x - 14.0, y)
      context.lineTo(x + 14.0, y)
      context.stroke()
    }
    context.fillStyle = "#d8d4dc"
    context.fillRect(0.0, height - 30.0, width.toDouble, 30.0)
    paintCentered(context, "一回 三〇〇円", width / 2.0, height - 15.0, glyphSize = 18.0, color = "#4b4757",
      weight = 600)
    canvasTexture(canvas, repeatWrap = false)
  end gachaponFasciaTexture

  def cloudTexture(width: Int = 256, height: Int = 128): CanvasTextureRef =
    val canvas  = createCanvas(width, height)
    val context = canvasContext(canvas)
    context.clearRect(0.0, 0.0, width.toDouble, height.toDouble)
    context.fillStyle = "#ffffff"
    List(
      (0.22, 0.62, 0.15), (0.36, 0.46, 0.20), (0.52, 0.40, 0.24), (0.68, 0.50, 0.19), (0.82, 0.63, 0.14),
      (0.45, 0.66, 0.20), (0.60, 0.68, 0.17)
    ).foreach { case (x, y, radius) =>
      context.beginPath()
      context.ellipse(
        x * width,
        y * height,
        radius * width * 0.55,
        radius * height * 1.1,
        0.0,
        0.0,
        math.Pi * 2.0
      )
      context.fill()
    }
    context.globalCompositeOperation = "destination-out"
    context.fillRect(0.0, height * 0.78, width.toDouble, height * 0.22)
    context.globalCompositeOperation = "source-over"
    canvasTexture(canvas, repeatWrap = false)
  end cloudTexture

  private def paintBands(
    context: dom.CanvasRenderingContext2D,
    width: Int,
    height: Int,
    colour: String,
    offset: Double
  ): Unit =
    context.strokeStyle = colour
    context.lineWidth = HazardStripes.StrokeWidth
    HazardStripes.segments(width, height, offset).foreach { segment =>
      context.beginPath()
      context.moveTo(segment.x0, segment.y0)
      context.lineTo(segment.x1, segment.y1)
      context.stroke()
    }

  private def paintCrossbuck(context: dom.CanvasRenderingContext2D, width: Int, height: Int): Unit =
    val plate  = CrossbuckSign.field(width, height)
    val length = CrossbuckSign.bladeLength(width, height)
    context.save()
    context.beginPath()
    context.rect(plate.x, plate.y, plate.width, plate.height)
    context.clip()
    context.fillStyle = WarningYellow
    context.fillRect(plate.x, plate.y, plate.width, plate.height)
    context.translate(width / 2.0, height / 2.0)
    context.fillStyle = WarningBlack
    context.rotate(CrossbuckSign.BladeAngle)
    context.fillRect(-length / 2.0, -CrossbuckSign.BladeWidth / 2.0, length, CrossbuckSign.BladeWidth)
    context.rotate(-2.0 * CrossbuckSign.BladeAngle)
    context.fillRect(-length / 2.0, -CrossbuckSign.BladeWidth / 2.0, length, CrossbuckSign.BladeWidth)
    context.restore()

  private def paintWording(context: dom.CanvasRenderingContext2D, width: Int, height: Int): Unit =
    val band    = CrossbuckSign.wordBand(width, height)
    val outline = CrossbuckSign.WordBandOutline
    context.fillStyle = WarningYellow
    context.fillRect(band.x, band.y, band.width, band.height)
    context.strokeStyle = WarningBlack
    context.lineWidth = outline
    context.strokeRect(band.x + outline / 2.0, band.y + outline / 2.0, band.width - outline, band.height - outline)
    context.fillStyle = WarningBlack
    context.font = CrossbuckSign.Font
    context.textAlign = "center"
    context.textBaseline = "middle"
    context.fillText(CrossbuckSign.Wording, band.x + band.width / 2.0, band.y + band.height / 2.0, band.width)

  private def paintAsphaltGrain(context: dom.CanvasRenderingContext2D, width: Int, height: Int): Unit =
    val image = context.createImageData(width.toDouble, height.toDouble)
    val data  = image.data
    for
      row    <- 0 until height
      column <- 0 until width
    do
      val offset = (row * width + column) * 4
      val shade  = Asphalt.shadeAt(column, row)
      data(offset) = Asphalt.Base.red + shade
      data(offset + 1) = Asphalt.Base.green + shade
      data(offset + 2) = Asphalt.Base.blue + shade
      data(offset + 3) = 255
    context.putImageData(image, 0.0, 0.0)

  private def paintRoadMarkings(context: dom.CanvasRenderingContext2D, width: Int, height: Int): Unit =
    context.fillStyle = RoadMarking
    List(RoadMarkings.laneLine(height), RoadMarkings.stopLine(width)).foreach { marking =>
      context.fillRect(marking.x, marking.y, marking.width, marking.height)
    }

  val JpFontStack: String =
    "'Yu Gothic', 'Meiryo', 'Hiragino Kaku Gothic ProN', 'Noto Sans CJK JP', sans-serif"

  private def jpFont(weight: Int, pixels: Double): String = s"$weight ${pixels.toInt}px $JpFontStack"

  private def paintRule(
    context: dom.CanvasRenderingContext2D,
    x: Double,
    y: Double,
    width: Double,
    height: Double,
    color: String
  ): Unit =
    context.fillStyle = color
    context.fillRect(x, y, width, height)

  /** Centred single-line text; `align` selects the small-type column's flush edge and `maxWidth`
    * squeezes the glyphs the way a real sign painter would for a long name.
    */
  private def paintCentered(
    context: dom.CanvasRenderingContext2D,
    text: String,
    centerX: Double,
    centerY: Double,
    glyphSize: Double,
    color: String,
    weight: Int,
    align: String = "center",
    maxWidth: Double = 0.0
  ): Unit =
    context.font = jpFont(weight, glyphSize)
    context.fillStyle = color
    context.textAlign = align
    context.textBaseline = "middle"
    if maxWidth > 0.0 then context.fillText(text, centerX, centerY, maxWidth)
    else context.fillText(text, centerX, centerY)

  /** Glyphs at explicit x/y slots: the form every vertical board and panel-split noren needs. */
  private def paintGlyphRow(
    context: dom.CanvasRenderingContext2D,
    text: String,
    xs: Vector[Double],
    defaultY: Double,
    glyphSize: Double,
    color: String,
    ys: Vector[Double] = Vector.empty
  ): Unit =
    context.font = jpFont(900, glyphSize)
    context.fillStyle = color
    context.textAlign = "center"
    context.textBaseline = "middle"
    text.zip(xs).zipWithIndex.foreach { case ((glyph, x), index) =>
      val y = if ys.isEmpty then defaultY else ys(index)
      context.fillText(glyph.toString, x, y)
    }

  private def cutPanelSlits(context: dom.CanvasRenderingContext2D, width: Int, height: Int): Unit =
    context.globalCompositeOperation = "destination-out"
    Noren.slitCenters(width.toDouble).foreach { x =>
      context.fillRect(x - Noren.SlitHalfWidth, height * Noren.SlitStart, Noren.SlitHalfWidth * 2.0, height)
    }
    context.globalCompositeOperation = "source-over"

  /** Bezier grain strokes: the two tones that make a blade read as bare timber. */
  private def paintWoodGrain(context: dom.CanvasRenderingContext2D, width: Int, height: Int): Unit =
    context.strokeStyle = "rgba(122,92,60,0.30)"
    context.lineWidth = 3.0
    (0 until 7).foreach { index =>
      val x = 16.0 + index * 26.0
      context.beginPath()
      context.moveTo(x, 0.0)
      context.bezierCurveTo(x + 9.0, height * 0.3, x - 9.0, height * 0.66, x + 4.0, height.toDouble)
      context.stroke()
    }

  private def requireSeamlessTile(width: Int, height: Int): Unit =
    if !HazardStripes.tiles(width, height) then
      throw new IllegalArgumentException(
        s"sakura-crossing: a $width x $height hazard tile is not a whole number of ${HazardStripes.Period} px periods, so it would repeat with a seam"
      )

  private[sakura] def createCanvas(width: Int, height: Int): dom.HTMLCanvasElement =
    dom.document.createElement("canvas") match
      case canvas: dom.HTMLCanvasElement =>
        canvas.width = width
        canvas.height = height
        canvas
      case _ =>
        throw new IllegalStateException("sakura-crossing: canvas creation returned a foreign element")

  private[sakura] def canvasContext(canvas: dom.HTMLCanvasElement): dom.CanvasRenderingContext2D =
    val context = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D | Null]
    context match
      case typed: dom.CanvasRenderingContext2D => typed
      case null                                => throw new IllegalStateException("sakura-crossing: the canvas has no 2D context")

  private[sakura] def canvasTexture(canvas: dom.HTMLCanvasElement, repeatWrap: Boolean): CanvasTextureRef =
    val texture = new CanvasTexture(canvas)
    texture.colorSpace = SRGBColorSpace
    if repeatWrap then
      // RepeatWrapping is not writable through its exported constant: the wrap field's union arms
      // lower to Int literals while the constant lowers to a Double literal, so the matching literal
      // is the writable form.
      texture.wrapS = 1000
      texture.wrapT = 1000
    texture
end ProceduralTextures
