package sakura.core

import org.scalajs.dom
import vendor.three.{CanvasTexture, ClampToEdgeWrapping, RepeatWrapping}

import scala.scalajs.js

/** Upstream texture metadata parity for the 1:1 port of `src/core/textures.js`: canvas size,
  * wrapping, repeat, anisotropy, colour space, per-key cache identity, and the mirrored-clone
  * flip. Ground truth extracted by running the upstream module against a recording canvas stub
  * (upstream three@0.185.1); the no-op canvas stub here only lets `CanvasTexture` construct under
  * NodeJSEnv, and pixel parity is deferred to the browser harness.
  */
class TexturesParitySuite extends munit.FunSuite:

  override def beforeAll(): Unit = TextureCanvasStub.install()

  final private case class Row(
    label: String,
    build: () => CanvasTexture[dom.HTMLCanvasElement],
    width: Int,
    height: Int,
    repeated: Boolean,
    srgb: Boolean
  )

  private def row(
    label: String,
    build: () => CanvasTexture[dom.HTMLCanvasElement],
    width: Int,
    height: Int,
    repeated: Boolean = false,
    srgb: Boolean = true
  ): Row = Row(label, build, width, height, repeated, srgb)

  private def indices(
    label: String,
    count: Int,
    build: Int => CanvasTexture[dom.HTMLCanvasElement],
    width: Int,
    height: Int
  ): Vector[Row] =
    (0 until count).toVector.map(index => row(s"$label.$index", () => build(index), width, height))

  private val rows: Vector[Row] =
    Vector(
      row("cloudTex", () => Textures.cloudTex(), 512, 256, srgb = false),
      row("petalTex", () => Textures.petalTex(), 128, 128, srgb = false),
      row("tactileTex.dots", () => Textures.tactileTex(bars = false), 128, 128, repeated = true),
      row("tactileTex.bars", () => Textures.tactileTex(bars = true), 128, 128, repeated = true),
      row("drainTex", () => Textures.drainTex(), 128, 128),
      row("alleyPlate", () => Textures.alleyPlate(), 512, 128),
      row("noParking", () => Textures.noParking(), 256, 256)
    ) ++ Textures.RoadPaintKind.values.toVector.map(kind =>
      row(s"roadPaint.${kind.key}", () => Textures.roadPaint(kind), 512, 512)
    ) ++ Vector(
      row("crossingSign", () => Textures.crossingSign(), 512, 256),
      row("stationSign", () => Textures.stationSign(), 768, 192),
      row("trainDest", () => Textures.trainDest(), 512, 128),
      row("trainNumber", () => Textures.trainNumber(), 256, 96),
      row("shopSign", () => Textures.shopSign(), 1024, 256),
      row("shopBanner", () => Textures.shopBanner(), 192, 768),
      row("meterBox", () => Textures.meterBox(), 256, 256),
      row("vendPrice", () => Textures.vendPrice(), 512, 96),
      row("vendSlot", () => Textures.vendSlot(), 256, 64),
      row("shrinePlate", () => Textures.shrinePlate(), 128, 256),
      row("platePlate", () => Textures.platePlate(), 256, 128),
      row("corkBoard", () => Textures.corkBoard(), 512, 256),
      row("paperSheet", () => Textures.paperSheet(), 256, 352),
      row("phoneBoxSign", () => Textures.phoneBoxSign(), 512, 160),
      row("guideBoard", () => Textures.guideBoard(), 640, 448),
      row("returnPlate", () => Textures.returnPlate(), 384, 192),
      row("chainLinkTex", () => Textures.chainLinkTex(), 128, 128, srgb = false),
      row("menuBoard", () => Textures.menuBoard(), 384, 512),
      row("gachaTex", () => Textures.gachaTex(), 256, 256),
      row("sentoFuji", () => Textures.sentoFuji(), 1024, 340),
      row("busTimetable", () => Textures.busTimetable(), 320, 480),
      row("houraiFuji", () => Textures.houraiFuji(), 1024, 340),
      row("ashiyuPlate", () => Textures.ashiyuPlate(), 384, 512),
      row("yunosakaBoard", () => Textures.yunosakaBoard(), 640, 400),
      row("shrineName", () => Textures.shrineName(), 256, 768),
      row("omikujiTex", () => Textures.omikujiTex(), 256, 320),
      row("sanpaiNotice", () => Textures.sanpaiNotice(), 384, 512)
    ) ++ indices("warningPlate", 4, Textures.warningPlate, 256, 512)
      ++ indices("poster", 4, Textures.poster, 320, 448)
      ++ Vector(20, 22, 24, 26).map(slats =>
        row(s"shutterTex.$slats", () => Textures.shutterTex(slats), 64, 512)
      )
      ++ indices("vendHeader", 3, Textures.vendHeader, 512, 160)
      ++ Vector(false -> "cold", true -> "hot").map((hot, label) =>
        row(s"vendCold.$label", () => Textures.vendCold(hot), 256, 96)
      )
      ++ indices("litWindowTex", 3, Textures.litWindowTex, 256, 256)
      ++ indices("curtainTex", 2, Textures.curtainTex, 256, 256)
      ++ indices("namePlate", 12, Textures.namePlate, 256, 128)
      ++ indices("gomiPlate", 2, Textures.gomiPlate, 384, 288)
      ++ indices("phoneNotice", 2, Textures.phoneNotice, 256, 352)
      ++ Textures.ShopKind.values.toVector.map(kind =>
        row(s"shopFascia.${kind.key}", () => Textures.shopFascia(kind), 1024, 224)
      )
      ++ Textures.ShopKind.values.toVector.map(kind =>
        row(s"shopBlade.${kind.key}", () => Textures.shopBlade(kind), 192, 768)
      )
      ++ Textures.NorenKind.values.toVector.map(kind =>
        row(s"norenTex.${kind.key}", () => Textures.norenTex(kind), 512, 256)
      )
      ++ indices("lanternTex", 5, Textures.lanternTex, 256, 256)
      ++ indices("flagTex", 4, Textures.flagTex, 256, 384)
      ++ indices("shopInterior", 4, Textures.shopInterior, 512, 320)
      ++ indices("showaInterior", 2, Textures.showaInterior, 512, 320)
      ++ indices("chalkNotice", 2, Textures.chalkNotice, 320, 384)
      ++ indices("sleeveTex", 4, Textures.sleeveTex, 256, 256)
      ++ indices("busStopPlate", 2, Textures.busStopPlate, 384, 384)
      ++ Textures.OnsenKind.values.toVector.map(kind =>
        row(s"onsenFascia.${kind.key}", () => Textures.onsenFascia(kind), 1024, 220)
      )
      ++ Textures.OnsenKind.values.toVector.map(kind =>
        row(s"onsenBlade.${kind.key}", () => Textures.onsenBlade(kind), 192, 768)
      )
      ++ Textures.OnsenNorenKind.values.toVector.map(kind =>
        row(s"onsenNoren.${kind.key}", () => Textures.onsenNoren(kind), 512, 256)
      )
      ++ indices("tatamiRoom", 2, Textures.tatamiRoom, 512, 320)
      ++ indices("onsenLanternTex", 3, Textures.onsenLanternTex, 256, 256)
      ++ indices("emaTex", 5, Textures.emaTex, 256, 192)

  rows.foreach { row =>
    test(s"upstream texture metadata parity: ${row.label}") {
      val texture = row.build()
      assertEquals(texture.image.width, row.width, s"width of ${row.label}")
      assertEquals(texture.image.height, row.height, s"height of ${row.label}")
      val expectedWrap = if row.repeated then RepeatWrapping else ClampToEdgeWrapping
      assert(texture.wrapS.toDouble == expectedWrap, s"wrapS of ${row.label}")
      assert(texture.wrapT.toDouble == expectedWrap, s"wrapT of ${row.label}")
      assertEquals(texture.repeat.x, 1.0, s"repeat.x of ${row.label}")
      assertEquals(texture.repeat.y, 1.0, s"repeat.y of ${row.label}")
      assertEquals(texture.anisotropy, 4.0, s"anisotropy of ${row.label}")
      assertEquals(texture.colorSpace, if row.srgb then "srgb" else "", s"colour space of ${row.label}")
      assert(row.build() eq texture, s"a second ${row.label} call must return the cached texture")
    }
  }

  test("mirrored clones share the image, flip wrapping, and cache per source") {
    val source  = Textures.cloudTex()
    val flipped = Textures.mirrored(source)
    assert(flipped ne source)
    assert(Textures.mirrored(source) eq flipped)
    assert(flipped.image eq source.image)
    assert(flipped.wrapS.toDouble == RepeatWrapping)
    assert(flipped.wrapT.toDouble == ClampToEdgeWrapping)
    assertEquals(flipped.repeat.x, -1.0)
    assertEquals(flipped.repeat.y, 1.0)
    assertEquals(flipped.offset.x, 1.0)
    assertEquals(flipped.anisotropy, 4.0)
    assertEquals(flipped.colorSpace, source.colorSpace)
  }

  test("mirrored keys its cache by the source texture") {
    val cloudFlip = Textures.mirrored(Textures.cloudTex())
    val petalFlip = Textures.mirrored(Textures.petalTex())
    assert(cloudFlip ne petalFlip)
  }
end TexturesParitySuite

/** The minimal `document.createElement("canvas")` shim the library needs under NodeJSEnv: a
  * no-op 2D context so every generator runs deterministically and `CanvasTexture` sees real
  * width/height fields. It asserts nothing itself.
  */
private[core] object TextureCanvasStub:

  def install(): Unit =
    js.Dynamic.global.globalThis.updateDynamic("document")(
      js.Dynamic.literal(
        createElement = (
          (tag: String) =>
            if tag == "canvas" then new StubCanvas
            else throw new IllegalStateException(s"sakura-crossing: unexpected element: $tag")
        ): js.Function1[String, StubCanvas]
      )
    )
end TextureCanvasStub

final private[core] class StubTextMetrics(val width: Double) extends js.Object

final private[core] class StubGradient extends js.Object:
  def addColorStop(offset: Double, color: String): Unit = ()

final private[core] class StubContext2d extends js.Object:
  var fillStyle: String                = ""
  var strokeStyle: String              = ""
  var lineWidth: Double                = 1.0
  var font: String                     = ""
  var textAlign: String                = "start"
  var textBaseline: String             = "alphabetic"
  var globalAlpha: Double              = 1.0
  var globalCompositeOperation: String = "source-over"
  var lineCap: String                  = "butt"
  var imageSmoothingEnabled: Boolean   = true

  def fillRect(x: Double, y: Double, w: Double, h: Double): Unit   = ()
  def clearRect(x: Double, y: Double, w: Double, h: Double): Unit  = ()
  def strokeRect(x: Double, y: Double, w: Double, h: Double): Unit = ()
  def beginPath(): Unit                                            = ()
  def closePath(): Unit                                            = ()
  def moveTo(x: Double, y: Double): Unit                           = ()
  def lineTo(x: Double, y: Double): Unit                           = ()
  def arc(
    x: Double,
    y: Double,
    radius: Double,
    startAngle: Double,
    endAngle: Double,
    anticlockwise: Boolean = false
  ): Unit = ()
  def ellipse(
    x: Double,
    y: Double,
    radiusX: Double,
    radiusY: Double,
    rotation: Double,
    startAngle: Double,
    endAngle: Double,
    counterclockwise: Boolean = false
  ): Unit = ()
  def bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double): Unit = ()
  def quadraticCurveTo(cpx: Double, cpy: Double, x: Double, y: Double): Unit                            = ()
  def fill(): Unit                                                                                      = ()
  def stroke(): Unit                                                                                    = ()
  def fillText(text: String, x: Double, y: Double): Unit                                                = ()
  def measureText(text: String): StubTextMetrics                                                        = new StubTextMetrics(0.0)
  def save(): Unit                                                                                      = ()
  def restore(): Unit                                                                                   = ()
  def translate(x: Double, y: Double): Unit                                                             = ()
  def rotate(angle: Double): Unit                                                                       = ()
  def setLineDash(segments: js.Array[Double]): Unit                                                     = ()
  def createLinearGradient(x0: Double, y0: Double, x1: Double, y1: Double): StubGradient =
    new StubGradient
  def createRadialGradient(x0: Double, y0: Double, r0: Double, x1: Double, y1: Double, r1: Double): StubGradient =
    new StubGradient
end StubContext2d

final private[core] class StubCanvas extends js.Object:
  var width: Int                     = 0
  var height: Int                    = 0
  private val context: StubContext2d = new StubContext2d

  def getContext(contextId: String): StubContext2d =
    if contextId != "2d" then
      throw new IllegalStateException(s"sakura-crossing: unexpected context kind: $contextId")
    context
end StubCanvas
