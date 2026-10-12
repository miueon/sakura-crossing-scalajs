package sakura.foliage

import org.scalajs.dom
import sakura.{MaterialTexture, PlanetCurvature, ProceduralTextures, TerrainHeightmap, ThreeInterop}
import vendor.three.*

import scala.math
import scala.scalajs.js
import scala.util.Random

/** Flat-plane petal simulation for a fixed pool: positions, sway phases, settle speeds and
  * tumbling angles in pre-allocated arrays. Pure state with no rendering, so recycling and drift
  * are testable outside a browser.
  *
  * Wind is a base drift plus two periodic gusts; petals settle near 0.35 m/s and, on reaching the
  * ground, respawn at canopy height anywhere in the spawn ring so the downwind drift cannot empty
  * the field.
  */
final class PetalField(
  val count: Int,
  innerRadius: Double,
  outerRadius: Double,
  seed: Long,
  centerX: Double = 0.0,
  centerZ: Double = 0.0
):
  require(innerRadius > 0.0 && outerRadius > innerRadius, "the spawn ring must be a real annulus")

  private val xs            = new Array[Double](count)
  private val ys            = new Array[Double](count)
  private val zs            = new Array[Double](count)
  private val swayPhase     = new Array[Double](count)
  private val swayAmplitude = new Array[Double](count)
  private val settleSpeed   = new Array[Double](count)
  private val tumbleXRate   = new Array[Double](count)
  private val tumbleYRate   = new Array[Double](count)
  private val tumbleZRate   = new Array[Double](count)
  private val angleX        = new Array[Double](count)
  private val angleY        = new Array[Double](count)
  private val angleZ        = new Array[Double](count)

  private val windBaseX        = 1.1
  private val windBaseZ        = 0.35
  private val gustXAmplitude   = 0.85
  private val gustXFrequency   = 0.5
  private val gustXPhase       = 0.6
  private val gustZAmplitude   = 0.55
  private val gustZFrequency   = 0.33
  private val gustZPhase       = 2.1
  private val swayFrequency    = 1.35
  private val respawnLiftFloor = 12.0
  private val respawnLiftSpan  = 8.0
  private val maxStepSeconds   = 0.05

  private val rng = new Random(seed)

  (0 until count).foreach { i =>
    val (x, z) = ringPosition()
    xs(i) = x
    zs(i) = z
    ys(i) = TerrainHeightmap.hillAt(x, z) + between(1.5, 20.0)
    swayPhase(i) = rng.nextDouble() * 2.0 * math.Pi
    swayAmplitude(i) = between(0.4, 1.1)
    settleSpeed(i) = between(0.28, 0.44)
    angleX(i) = rng.nextDouble() * 2.0 * math.Pi
    angleY(i) = rng.nextDouble() * 2.0 * math.Pi
    angleZ(i) = rng.nextDouble() * 2.0 * math.Pi
    tumbleXRate(i) = signedRate()
    tumbleYRate(i) = signedRate()
    tumbleZRate(i) = signedRate()
  }

  def x(i: Int): Double        = xs(i)
  def y(i: Int): Double        = ys(i)
  def z(i: Int): Double        = zs(i)
  def angleXOf(i: Int): Double = angleX(i)
  def angleYOf(i: Int): Double = angleY(i)
  def angleZOf(i: Int): Double = angleZ(i)

  def windX(timeSeconds: Double): Double =
    windBaseX + gustXAmplitude * math.sin(gustXFrequency * timeSeconds + gustXPhase)

  def windZ(timeSeconds: Double): Double =
    windBaseZ + gustZAmplitude * math.sin(gustZFrequency * timeSeconds + gustZPhase)

  /** One simulation tick; `dt` is clamped so a stalled tab's first frame back cannot tunnel petals
    * through the ground.
    */
  def step(timeSeconds: Double, dt: Double): Unit =
    val clamped    = math.min(math.max(dt, 0.0), maxStepSeconds)
    val windDriftX = windX(timeSeconds)
    val windDriftZ = windZ(timeSeconds)
    var i          = 0
    while i < count do
      val phase = swayPhase(i)
      xs(i) += (windDriftX + swayAmplitude(i) * math.sin(timeSeconds * swayFrequency + phase)) * clamped
      zs(i) += (windDriftZ + swayAmplitude(i) * math.cos(timeSeconds * 0.78 + phase * 1.7)) * clamped
      ys(i) -= settleSpeed(i) * clamped
      angleX(i) += tumbleXRate(i) * clamped
      angleY(i) += tumbleYRate(i) * clamped
      angleZ(i) += tumbleZRate(i) * clamped
      if ys(i) < TerrainHeightmap.hillAt(xs(i), zs(i)) then
        val (x, z) = ringPosition()
        xs(i) = x
        zs(i) = z
        ys(i) = TerrainHeightmap.hillAt(x, z) + respawnLiftFloor + rng.nextDouble() * respawnLiftSpan
      i += 1

  private def ringPosition(): (Double, Double) =
    val azimuth = rng.nextDouble() * 2.0 * math.Pi
    val radius =
      math.sqrt(innerRadius * innerRadius + rng.nextDouble() * (outerRadius * outerRadius - innerRadius * innerRadius))
    (centerX + radius * math.cos(azimuth), centerZ + radius * math.sin(azimuth))

  private def signedRate(): Double =
    (if rng.nextBoolean() then 1.0 else -1.0) * between(0.8, 3.6)

  private def between(low: Double, high: Double): Double =
    low + rng.nextDouble() * (high - low)
end PetalField

/** The 桜吹雪 field: every petal of a fixed pool rendered through one InstancedMesh.
  *
  * The shell owns the GPU objects; all per-frame state lives in the [[PetalField]] core and this
  * loop writes transforms through pre-allocated scratch objects, so stepping allocates nothing.
  */
final class PetalParticleSystem(
  val count: Int,
  innerRadius: Double,
  outerRadius: Double,
  curvature: PlanetCurvature,
  texture: MaterialTexture,
  centerX: Double = 0.0,
  centerZ: Double = 0.0
):

  val geometry = new PlaneGeometry(0.55, 0.4)
  val material = new MeshBasicMaterial(
    MeshBasicMaterialParameters(
      map = js.defined(texture),
      // The integer literal is the writable form of DoubleSide (see ToonMaterial's side note);
      // depthWrite stays off so the depth buffer is not speckled by the drifting quads.
      side = js.defined(2),
      transparent = js.defined(true),
      depthWrite = js.defined(false)
    )
  )
  val mesh = new InstancedMesh(geometry, material, count.toDouble)

  private val field = new PetalField(
    count,
    innerRadius,
    outerRadius,
    seed = 20251011L,
    centerX = centerX,
    centerZ = centerZ
  )
  private val position   = new Vector3()
  private val petalScale = new Vector3(1.0, 1.0, 1.0)
  private val euler      = new Euler()
  private val rotation   = new Quaternion()
  private val matrix     = new Matrix4()
  private val tint       = new Color()

  locally {
    val tintRng = new Random(7L)
    (0 until count).foreach { i =>
      val depth = tintRng.nextDouble()
      tint.setRGB(1.0 - 0.10 * depth, 1.0 - 0.16 * depth, 1.0 - 0.14 * depth)
      mesh.setColorAt(i, tint)
    }
    writeMatrices()
  }

  def step(timeSeconds: Double, dt: Double): Unit =
    field.step(timeSeconds, dt)
    writeMatrices()

  def batch: SceneBatch =
    SceneBatch(List(mesh), List(geometry), List(material))

  /** The instance translation is bent on the CPU — a petal is a few decimetres across, well under
    * the chord tolerance, and tumbling hides the residual — while the canopy and fence batches
    * bend per vertex in their shaders because their spans are metres wide.
    */
  private def writeMatrices(): Unit =
    var i = 0
    while i < count do
      val x = field.x(i)
      val z = field.z(i)
      position.set(x, curvature.bend(x, field.y(i), z), z)
      euler.set(field.angleXOf(i), field.angleYOf(i), field.angleZOf(i))
      rotation.setFromEuler(euler)
      matrix.compose(position, rotation, petalScale)
      mesh.setMatrixAt(i, matrix)
      i += 1
    ThreeInterop.markForUpload(mesh.instanceMatrix)
end PetalParticleSystem

object PetalParticleSystem:

  /** Soft blossom sprite with the notched tip sakura petals read by, drawn once and shared by
    * every instance; 2,000 tumbling quads shade it into the drifting field.
    */
  def petalTexture(): CanvasTexture[?] =
    val canvas = dom.document.createElement("canvas") match
      case canvas: dom.HTMLCanvasElement => canvas
      case _                             => throw new IllegalStateException("sakura-crossing: canvas creation returned a foreign element")
    canvas.width = 128
    canvas.height = 128
    val context = ProceduralTextures.canvasContext(canvas)
    context.clearRect(0.0, 0.0, 128.0, 128.0)
    context.strokeStyle = "rgba(245, 165, 188, 0.85)"
    context.lineWidth = 5.0
    val gradient = context.createRadialGradient(64.0, 64.0, 8.0, 64.0, 64.0, 60.0)
    gradient.addColorStop(0.0, "#fff8fa")
    gradient.addColorStop(1.0, "#f6c3d3")
    context.fillStyle = gradient
    drawPetalPath(context)
    context.fill()
    context.stroke()
    val texture = new CanvasTexture(canvas)
    texture.colorSpace = SRGBColorSpace
    texture
  end petalTexture

  private def drawPetalPath(context: dom.CanvasRenderingContext2D): Unit =
    context.beginPath()
    context.moveTo(64.0, 26.0)
    context.lineTo(55.0, 12.0)
    context.quadraticCurveTo(22.0, 26.0, 24.0, 64.0)
    context.quadraticCurveTo(26.0, 106.0, 64.0, 116.0)
    context.quadraticCurveTo(102.0, 106.0, 104.0, 64.0)
    context.quadraticCurveTo(106.0, 26.0, 73.0, 12.0)
    context.closePath()
end PetalParticleSystem
