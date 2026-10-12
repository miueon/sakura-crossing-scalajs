package sakura

import org.scalajs.dom
import ultraviolet.syntax.*
import vendor.three.*

import scala.annotation.nowarn
import scala.math

final class PostPipeline(
  renderer: WebGLRenderer,
  scene: Scene[Object3DEventMap],
  camera: PerspectiveCamera
):
  private val PixelBudget = 4.6e6

  final private case class FullscreenVertexEnv(
    position: vec3,
    uv: vec2,
    var gl_Position: vec4
  )

  final private case class InkFragmentEnv(
    tDiffuse: sampler2D.type,
    tDepth: sampler2D.type,
    uTexel: vec2,
    uNear: Float,
    uFar: Float,
    uInk: vec3,
    uThickness: Float,
    uSens: Float,
    uConcave: Float,
    uConcaveAmount: Float,
    uFadeStart: Float,
    uFadeEnd: Float,
    uStrength: Float,
    uSkyDepth: Float,
    var gl_FragColor: vec4
  )

  final private case class GradeFragmentEnv(
    tDiffuse: sampler2D.type,
    uShadowTint: vec3,
    uLightTint: vec3,
    uSaturation: Float,
    uLift: Float,
    uVignette: Float,
    uWarmth: Float,
    var gl_FragColor: vec4
  )

  final private case class FxaaFragmentEnv(
    tDiffuse: sampler2D.type,
    uTexel: vec2,
    var gl_FragColor: vec4
  )

  @nowarn("msg=(unused|mutated but not read)")
  private val fullscreenVertexShader: String =
    Shader[FullscreenVertexEnv] { env =>
      @out var vUv: vec2 = vec2(0.0f)

      def main(): Unit =
        vUv = env.uv
        env.gl_Position = vec4(env.position.x, env.position.y, 0.0f, 1.0f)
    }.toGLSL100.code

  @nowarn("msg=unused")
  private val inkFragmentShader: String =
    Shader[InkFragmentEnv] { env =>
      @in val vUv: vec2 = vec2(0.0f)

      def sampleViewDepth(uv: vec2): Float =
        val depth: Float = texture2D(env.tDepth, uv).x
        (env.uNear * env.uFar) / ((env.uNear - env.uFar) * depth + env.uFar)

      def main(): Unit =
        val color: vec3         = texture2D(env.tDiffuse, vUv).xyz
        val texel: vec2         = env.uTexel * env.uThickness
        val center: Float       = sampleViewDepth(vUv)
        val left: Float         = sampleViewDepth(vUv - vec2(texel.x, 0.0f))
        val right: Float        = sampleViewDepth(vUv + vec2(texel.x, 0.0f))
        val up: Float           = sampleViewDepth(vUv + vec2(0.0f, texel.y))
        val down: Float         = sampleViewDepth(vUv - vec2(0.0f, texel.y))
        val dx: Float           = left + right - 2.0f * center
        val dy: Float           = up + down - 2.0f * center
        val sx: Float           = dx / center
        val sy: Float           = dy / center
        val convex: Float       = max(0.0f, sx) + max(0.0f, sy)
        val concave: Float      = max(0.0f, -sx) + max(0.0f, -sy)
        val convexEdge: Float   = smoothstep(env.uSens * 0.32f, env.uSens, convex)
        val concaveEdge: Float  = smoothstep(env.uConcave, env.uConcave * 3.4f, concave) * env.uConcaveAmount
        val distanceFade: Float = 1.0f - smoothstep(env.uFadeStart, env.uFadeEnd, center)
        val skyMask: Float      = 1.0f - step(env.uSkyDepth, center)
        val edge: Float         = max(convexEdge, concaveEdge) * distanceFade * env.uStrength * skyMask
        val line: vec3          = mix(env.uInk, color * 0.42f, 0.22f)
        val outputColor: vec3   = mix(color, line, clamp(edge, 0.0f, 1.0f))
        env.gl_FragColor = vec4(outputColor, 1.0f)
      end main
    }.toGLSL100.code

  /** Upstream's anime grade ends by encoding the linear scene buffer for the screen. The scene
    * target is a linear HalfFloat buffer (the world's materials are three.js materials and write
    * linear radiance into it), so without this the whole frame reads as if it were sRGB already --
    * roughly 45/255 darker at mid tone. The terms are bound to `val`s because the emitted GLSL
    * drops parentheses around compound expressions. Only the render target clamps the highlights
    * above 1.0, exactly as upstream leaves it.
    */
  @nowarn("msg=unused")
  private val gradeFragmentShader: String =
    Shader[GradeFragmentEnv] { env =>
      @in val vUv: vec2 = vec2(0.0f)

      def linearToSRGB(color: vec3): vec3 =
        val low: vec3  = color * 12.92f
        val high: vec3 = pow(max(color, vec3(0.0031308f)), vec3(1.0f / 2.4f)) * 1.055f - vec3(0.055f)
        mix(low, high, step(vec3(0.0031308f), color))

      def main(): Unit =
        val color: vec3     = texture2D(env.tDiffuse, vUv).xyz
        val luma: Float     = dot(color, vec3(0.2126f, 0.7152f, 0.0722f))
        val tone: Float     = smoothstep(0.02f, 0.55f, luma)
        val split: vec3     = color * mix(env.uShadowTint, env.uLightTint, tone)
        val warm: vec3      = split + vec3(env.uWarmth, env.uWarmth * 0.45f, 0.0f) * luma * 0.35f
        val lifted: vec3    = warm + vec3(env.uLift * (1.0f - tone))
        val saturated: vec3 = mix(vec3(luma), lifted, env.uSaturation)
        val radius: Float   = length(vUv - vec2(0.5f, 0.5f)) * 1.42f
        val vignette: Float = 1.0f - env.uVignette * pow(clamp(radius, 0.0f, 1.0f), 2.6f)
        env.gl_FragColor = vec4(linearToSRGB(max(saturated * vignette, vec3(0.0f))), 1.0f)
    }.toGLSL100.code

  @nowarn("msg=unused")
  private val fxaaFragmentShader: String =
    Shader[FxaaFragmentEnv] { env =>
      @in val vUv: vec2 = vec2(0.0f)

      def fxaaLuma(color: vec3): Float = dot(color, vec3(0.299f, 0.587f, 0.114f))

      def main(): Unit =
        val center: vec3         = texture2D(env.tDiffuse, vUv).xyz
        val northWest: vec3      = texture2D(env.tDiffuse, vUv + vec2(-env.uTexel.x, -env.uTexel.y)).xyz
        val northEast: vec3      = texture2D(env.tDiffuse, vUv + vec2(env.uTexel.x, -env.uTexel.y)).xyz
        val southWest: vec3      = texture2D(env.tDiffuse, vUv + vec2(-env.uTexel.x, env.uTexel.y)).xyz
        val southEast: vec3      = texture2D(env.tDiffuse, vUv + vec2(env.uTexel.x, env.uTexel.y)).xyz
        val lumaCenter: Float    = fxaaLuma(center)
        val lumaNorthWest: Float = fxaaLuma(northWest)
        val lumaNorthEast: Float = fxaaLuma(northEast)
        val lumaSouthWest: Float = fxaaLuma(southWest)
        val lumaSouthEast: Float = fxaaLuma(southEast)
        val lumaMin: Float       = min(lumaCenter, min(min(lumaNorthWest, lumaNorthEast), min(lumaSouthWest, lumaSouthEast)))
        val lumaMax: Float       = max(lumaCenter, max(max(lumaNorthWest, lumaNorthEast), max(lumaSouthWest, lumaSouthEast)))
        val dirX: Float          = (lumaSouthWest + lumaSouthEast) - (lumaNorthWest + lumaNorthEast)
        val dirY: Float          = (lumaNorthWest + lumaSouthWest) - (lumaNorthEast + lumaSouthEast)
        val direction: vec2      = vec2(dirX, dirY)
        val reduction: Float = max(
          (lumaNorthWest + lumaNorthEast + lumaSouthWest + lumaSouthEast) * 0.25f * 0.18f,
          1.0f / 128.0f
        )
        val reciprocal: Float = 1.0f / (min(abs(direction.x), abs(direction.y)) + reduction)
        val span: vec2        = clamp(direction * reciprocal, vec2(-8.0f), vec2(8.0f)) * env.uTexel
        val innerA: vec3      = texture2D(env.tDiffuse, vUv + span * (1.0f / 3.0f - 0.5f)).xyz
        val innerB: vec3      = texture2D(env.tDiffuse, vUv + span * (2.0f / 3.0f - 0.5f)).xyz
        val outerA: vec3      = texture2D(env.tDiffuse, vUv - span * 0.5f).xyz
        val outerB: vec3      = texture2D(env.tDiffuse, vUv + span * 0.5f).xyz
        val rgbA: vec3        = innerA * 0.5f + innerB * 0.5f
        val rgbB: vec3        = rgbA * 0.5f + outerA * 0.25f + outerB * 0.25f
        val lumaB: Float      = fxaaLuma(rgbB)
        val blend: Float      = clamp(step(lumaB, lumaMin) + step(lumaMax, lumaB), 0.0f, 1.0f)
        val result: vec3      = mix(rgbB, rgbA, blend)
        env.gl_FragColor = vec4(result, 1.0f)
      end main
    }.toGLSL100.code

  private val sceneTarget  = new WebGLRenderTarget[MaterialTexture](2.0, 2.0)
  private val inkTarget    = new WebGLRenderTarget[MaterialTexture](2.0, 2.0)
  private val gradeTarget  = new WebGLRenderTarget[MaterialTexture](2.0, 2.0)
  private val depthTexture = new DepthTexture[DepthTextureImageData](2.0, 2.0)

  private val inkTexel  = new Vector2(1.0, 1.0)
  private val fxaaTexel = new Vector2(1.0, 1.0)

  private val inkMaterial = ThreeInterop.shaderMaterial(
    vertexShader = fullscreenVertexShader,
    fragmentShader =
      "uniform sampler2D tDiffuse;\nuniform sampler2D tDepth;\nuniform vec2 uTexel;\n" +
        "uniform float uNear;\nuniform float uFar;\nuniform vec3 uInk;\n" +
        "uniform float uThickness;\nuniform float uSens;\nuniform float uConcave;\n" +
        "uniform float uConcaveAmount;\nuniform float uFadeStart;\nuniform float uFadeEnd;\n" +
        "uniform float uStrength;\nuniform float uSkyDepth;\n" + inkFragmentShader,
    uniforms = List(
      "tDiffuse"       -> ShaderUniformValue.TextureRef(sceneTarget.texture),
      "tDepth"         -> ShaderUniformValue.TextureRef(depthTexture),
      "uTexel"         -> ShaderUniformValue.Vector(inkTexel),
      "uNear"          -> ShaderUniformValue.Scalar(camera.near),
      "uFar"           -> ShaderUniformValue.Scalar(camera.far),
      "uInk"           -> ShaderUniformValue.ThreeColor(Palette.ink),
      "uThickness"     -> ShaderUniformValue.Scalar(1.35),
      "uSens"          -> ShaderUniformValue.Scalar(0.0042),
      "uConcave"       -> ShaderUniformValue.Scalar(0.026),
      "uConcaveAmount" -> ShaderUniformValue.Scalar(0.42),
      "uFadeStart"     -> ShaderUniformValue.Scalar(40.0),
      "uFadeEnd"       -> ShaderUniformValue.Scalar(98.0),
      "uStrength"      -> ShaderUniformValue.Scalar(1.0),
      "uSkyDepth"      -> ShaderUniformValue.Scalar(420.0)
    )
  )

  private val gradeMaterial = ThreeInterop.shaderMaterial(
    vertexShader = fullscreenVertexShader,
    fragmentShader =
      "uniform sampler2D tDiffuse;\nuniform vec3 uShadowTint;\nuniform vec3 uLightTint;\n" +
        "uniform float uSaturation;\nuniform float uLift;\nuniform float uVignette;\n" +
        "uniform float uWarmth;\n" + gradeFragmentShader,
    uniforms = List(
      "tDiffuse"    -> ShaderUniformValue.TextureRef(inkTarget.texture),
      "uShadowTint" -> ShaderUniformValue.ThreeColor(Palette.gradeShadowTint),
      "uLightTint"  -> ShaderUniformValue.ThreeColor(Palette.gradeLightTint),
      "uSaturation" -> ShaderUniformValue.Scalar(1.12),
      "uLift"       -> ShaderUniformValue.Scalar(0.032),
      "uVignette"   -> ShaderUniformValue.Scalar(0.15),
      "uWarmth"     -> ShaderUniformValue.Scalar(0.05)
    )
  )

  private val fxaaMaterial = ThreeInterop.shaderMaterial(
    vertexShader = fullscreenVertexShader,
    fragmentShader = "uniform sampler2D tDiffuse;\nuniform vec2 uTexel;\n" + fxaaFragmentShader,
    uniforms = List(
      "tDiffuse" -> ShaderUniformValue.TextureRef(gradeTarget.texture),
      "uTexel"   -> ShaderUniformValue.Vector(fxaaTexel)
    )
  )

  private val fullscreenGeometry = new PlaneGeometry(2.0, 2.0)
  private val fullscreenCamera   = new OrthographicCamera(-1.0, 1.0, 1.0, -1.0, 0.0, 1.0)
  private val inkScene           = new Scene()
  private val gradeScene         = new Scene()
  private val fxaaScene          = new Scene()
  private val inkQuad            = new Mesh(fullscreenGeometry, inkMaterial)
  private val gradeQuad          = new Mesh(fullscreenGeometry, gradeMaterial)
  private val fxaaQuad           = new Mesh(fullscreenGeometry, fxaaMaterial)

  inkQuad.frustumCulled = false
  gradeQuad.frustumCulled = false
  fxaaQuad.frustumCulled = false
  fullscreenCamera.position.z = 0.5
  fullscreenCamera.updateProjectionMatrix()
  ThreeInterop.addChild(inkScene, inkQuad)
  ThreeInterop.addChild(gradeScene, gradeQuad)
  ThreeInterop.addChild(fxaaScene, fxaaQuad)

  sceneTarget.texture.`type` = HalfFloatType.toInt
  sceneTarget.texture.minFilter = LinearFilter.toInt
  sceneTarget.texture.magFilter = LinearFilter.toInt
  sceneTarget.texture.colorSpace = NoColorSpace
  depthTexture.format = DepthFormat.toInt
  depthTexture.`type` = UnsignedIntType.toInt
  depthTexture.minFilter = NearestFilter.toInt
  depthTexture.magFilter = NearestFilter.toInt
  sceneTarget.depthTexture = depthTexture
  inkTarget.depthBuffer = false
  inkTarget.texture.`type` = HalfFloatType.toInt
  inkTarget.texture.minFilter = LinearFilter.toInt
  inkTarget.texture.magFilter = LinearFilter.toInt
  inkTarget.texture.colorSpace = NoColorSpace
  gradeTarget.depthBuffer = false
  gradeTarget.texture.`type` = UnsignedByteType.toInt
  gradeTarget.texture.minFilter = LinearFilter.toInt
  gradeTarget.texture.magFilter = LinearFilter.toInt
  gradeTarget.texture.colorSpace = NoColorSpace

  /** The capture harness pins the resolution scale so a shot is reproducible: upstream's `__shot`
    * assigns `opts.scale || 1`, and only while this is unset does `setSize` derive the scale from
    * the device (`dpr < 1.5 ? 1.5 : min(dpr, 2)`, then capped by the pixel budget).
    */
  var forceScale: Option[Double] = None

  /** Upstream exposes the ink and fxaa passes to its capture harness and to the O/G keys. Its
    * `grade` flag is inert -- `Pipeline.render` never reads it -- so there is no toggle for it
    * here: honouring one would render frames upstream cannot produce.
    */
  var isInkEnabled: Boolean  = true
  var isFxaaEnabled: Boolean = true

  def setSize(width: Double, height: Double): Unit =
    val deviceScale = dom.window.devicePixelRatio
    var scale       = forceScale.getOrElse(if deviceScale < 1.5 then 1.5 else math.min(deviceScale, 2.0))
    if width * height * scale * scale > PixelBudget then
      scale = math.max(1.0, math.sqrt(PixelBudget / (width * height)))
    val pixelWidth  = math.max(2.0, math.floor(width * scale))
    val pixelHeight = math.max(2.0, math.floor(height * scale))
    renderer.setPixelRatio(1.0)
    renderer.setSize(width, height, true)
    sceneTarget.setSize(pixelWidth, pixelHeight)
    inkTarget.setSize(pixelWidth, pixelHeight)
    gradeTarget.setSize(pixelWidth, pixelHeight)
    inkTexel.set(1.0 / pixelWidth, 1.0 / pixelHeight)
    fxaaTexel.set(1.0 / pixelWidth, 1.0 / pixelHeight)
    ThreeInterop.setShaderUniform(inkMaterial, "uNear", ShaderUniformValue.Scalar(camera.near))
    ThreeInterop.setShaderUniform(inkMaterial, "uFar", ShaderUniformValue.Scalar(camera.far))
    ThreeInterop.setShaderUniform(
      inkMaterial,
      "uThickness",
      ShaderUniformValue.Scalar(1.05 + 0.55 * scale)
    )
    core.Outline.setOutlineResolution(pixelWidth, pixelHeight)
  end setSize

  /* Each pass reads whatever the pass before it wrote, so the texture bindings follow the enabled
   * set rather than being fixed at construction: skipping the ink pass hands the grade the scene
   * buffer, and skipping fxaa hands the grade the screen. */
  def render(): Unit =
    renderer.setRenderTarget(sceneTarget)
    renderer.clear()
    renderer.render(scene, camera)

    var source: Texture[?, ?] = sceneTarget.texture
    if isInkEnabled then
      ThreeInterop.setShaderUniform(inkMaterial, "tDiffuse", ShaderUniformValue.TextureRef(source))
      renderer.setRenderTarget(inkTarget)
      renderer.render(inkScene, fullscreenCamera)
      source = inkTarget.texture

    ThreeInterop.setShaderUniform(gradeMaterial, "tDiffuse", ShaderUniformValue.TextureRef(source))
    if isFxaaEnabled then
      renderer.setRenderTarget(gradeTarget)
      renderer.render(gradeScene, fullscreenCamera)
      ThreeInterop.setShaderUniform(
        fxaaMaterial,
        "tDiffuse",
        ShaderUniformValue.TextureRef(gradeTarget.texture)
      )
      ThreeInterop.setScreenRenderTarget(renderer)
      renderer.render(fxaaScene, fullscreenCamera)
    else
      ThreeInterop.setScreenRenderTarget(renderer)
      renderer.render(gradeScene, fullscreenCamera)
  end render

  def dispose(): Unit =
    ThreeInterop.setScreenRenderTarget(renderer)
    depthTexture.dispose()
    sceneTarget.dispose()
    inkTarget.dispose()
    gradeTarget.dispose()
    inkMaterial.dispose()
    gradeMaterial.dispose()
    fxaaMaterial.dispose()
    fullscreenGeometry.dispose()
end PostPipeline
