package sakura

import ultraviolet.syntax.*
import vendor.three.*

import scala.scalajs.js

/** Cel-shading and inverted-hull line art for the prototype props.
  *
  * Band thresholds and sun direction are baked into the shader source instead of passed as uniforms,
  * because the facade exposes no uniform map. Single-colour materials also bake their base colour;
  * the merged neighborhood material reads its base colours from the vertex attribute. Both passes
  * bend through the shared `bendToPlanetImpl`, keeping outline hulls attached to the curved surface.
  */
object ToonMaterial:

  private val highlightFloor      = 0.60
  private val midtoneFloor        = 0.15
  private val midtoneFactor       = 0.76
  private val shadowFactor        = 0.52
  private val shadowTint          = 0.25
  private val defaultSunDirection = (-0.52, 0.62, 0.56)
  private val fillLightDirection  = (48.0, 26.0, -44.0)
  private val bounceDirection     = (10.0, -18.0, 40.0)

  /** Blossom stays high-key: a big self-shading canopy drops most of its camera-facing surface
    * into the away-from-sun band, so the soft ramp keeps every band inside the pink family — a
    * darker shadow turns whole trees into the violet lumps the reference explicitly avoids.
    */
  private val blossomRamp = CelRamp(0.30, -0.25, 0.88, 0.85, 0.05)

  final private[sakura] case class CelRamp(
    highlightFloor: Double,
    midtoneFloor: Double,
    midtoneFactor: Double,
    shadowFactor: Double,
    shadowTint: Double
  )

  def celMaterial(baseColorHex: Int, curvature: PlanetCurvature): ShaderMaterial =
    celMaterialWithSunDirection(baseColorHex, curvature, defaultSunDirection)

  def celMaterialWithSunDirection(
    baseColorHex: Int,
    curvature: PlanetCurvature,
    sunDirection: (Double, Double, Double)
  ): ShaderMaterial =
    new ShaderMaterial(
      ShaderMaterialParameters(
        vertexShader = js.defined(celVertexShader(curvature, instanced = false)),
        fragmentShader = js.defined(celFragmentShader(baseColorHex, propRamp, sunDirection))
      )
    )

  def vertexColorCelMaterial(curvature: PlanetCurvature): ShaderMaterial =
    new ShaderMaterial(
      ShaderMaterialParameters(
        vertexShader = js.defined(vertexColorCelVertexShader(curvature)),
        fragmentShader = js.defined(vertexColorCelFragmentShader)
      )
    )

  /** Instanced cel shading for the batched foliage. three.js declares `attribute mat4
    * instanceMatrix` in the generated program prefix for any material rendered on an
    * InstancedMesh, so it needs no declaration here; folding it into the world position before
    * the bend seats every instance vertex on the curved ground individually, where bending only
    * the instance translation would tilt long limbs and fence rails off it.
    */
  def instancedCelMaterial(baseColorHex: Int, curvature: PlanetCurvature): ShaderMaterial =
    new ShaderMaterial(
      ShaderMaterialParameters(
        vertexShader = js.defined(celVertexShader(curvature, instanced = true)),
        fragmentShader = js.defined(celFragmentShader(baseColorHex, propRamp))
      )
    )

  def instancedBlossomMaterial(baseColorHex: Int, curvature: PlanetCurvature): ShaderMaterial =
    new ShaderMaterial(
      ShaderMaterialParameters(
        vertexShader = js.defined(celVertexShader(curvature, instanced = true)),
        fragmentShader = js.defined(celFragmentShader(baseColorHex, blossomRamp))
      )
    )

  def outlineMaterial(thickness: Double, curvature: PlanetCurvature): ShaderMaterial =
    Outline.outlineMaterial(thickness, curvature)

  def celMeshWithOutline(
    geometry: BufferGeometry[?, ?],
    baseColorHex: Int,
    outlineThickness: Double,
    curvature: PlanetCurvature
  ): (Group[?], List[BufferGeometry[?, ?]], List[Material[?]]) =
    val cel     = celMaterial(baseColorHex, curvature)
    val group   = new Group()
    val mesh    = new Mesh(geometry, cel)
    val outline = Outline.hullOutline(mesh, outlineThickness, curvature)
    ThreeInterop.addChild(group, mesh)
    (group, List(geometry, outline.geometry), List(cel, outline.material))

  /** Unlit solid colour that still bends onto the planet. For parts that ride a moving or
    * rotating group — train window glass riding the consist, barrier booms sweeping on their
    * pivots — host-side bending cannot follow the transform, so the bend has to live in the
    * shader like the cel pass it sits next to.
    */
  def flatMaterial(baseColorHex: Int, curvature: PlanetCurvature): ShaderMaterial =
    new ShaderMaterial(
      ShaderMaterialParameters(
        vertexShader = js.defined(flatVertexShader(curvature)),
        fragmentShader = js.defined(flatFragmentShader(baseColorHex))
      )
    )

  private[sakura] val propRamp = CelRamp(highlightFloor, midtoneFloor, midtoneFactor, shadowFactor, shadowTint)

  /** Env members surface as GLSL identifiers with no declaration of their own: the three.js
    * built-ins (`position`, `modelMatrix`, ...) are already declared by the ShaderMaterial prefix,
    * and `u*` members are declared by the `const` lines the assembly methods prepend.
    */
  final private case class CelVertexEnv(
    uPlanetRadius: Float,
    position: vec3,
    normal: vec3,
    modelMatrix: mat4,
    viewMatrix: mat4,
    projectionMatrix: mat4,
    instanceMatrix: mat4,
    var gl_Position: vec4
  )

  final private case class CelFragmentEnv(
    uBaseColor: vec3,
    uSunHaze: vec3,
    uShadowViolet: vec3,
    uSunDirection: vec3,
    uFillColor: vec3,
    uFillDirection: vec3,
    uBounceColor: vec3,
    uBounceDirection: vec3,
    uHemiSky: vec3,
    uHemiGround: vec3,
    uFogColor: vec3,
    uFogNear: Float,
    uFogFar: Float,
    uHighlightFloor: Float,
    uMidtoneFloor: Float,
    uMidtoneFactor: Float,
    uShadowFactor: Float,
    uShadowTint: Float,
    var gl_FragColor: vec4
  )

  final private case class VertexColorCelVertexEnv(
    uPlanetRadius: Float,
    position: vec3,
    normal: vec3,
    color: vec3,
    modelMatrix: mat4,
    viewMatrix: mat4,
    projectionMatrix: mat4,
    var gl_Position: vec4
  )

  final private case class VertexColorCelFragmentEnv(
    uSunHaze: vec3,
    uShadowViolet: vec3,
    uSunDirection: vec3,
    uFillColor: vec3,
    uFillDirection: vec3,
    uBounceColor: vec3,
    uBounceDirection: vec3,
    uHemiSky: vec3,
    uHemiGround: vec3,
    uFogColor: vec3,
    uFogNear: Float,
    uFogFar: Float,
    uHighlightFloor: Float,
    uMidtoneFloor: Float,
    uMidtoneFactor: Float,
    uShadowFactor: Float,
    uShadowTint: Float,
    var gl_FragColor: vec4
  )

  final private case class FlatVertexEnv(
    uPlanetRadius: Float,
    position: vec3,
    modelMatrix: mat4,
    viewMatrix: mat4,
    projectionMatrix: mat4,
    var gl_Position: vec4
  )

  final private case class FlatFragmentEnv(
    uBaseColor: vec3,
    uFogColor: vec3,
    uFogNear: Float,
    uFogFar: Float,
    var gl_FragColor: vec4
  )

  /** Authored flat; the shader is what puts the prop on the planet. `mat3(m) * v` and the
    * `(m * vec4(v, 0.0)).xyz` here are the same direction transform; Ultraviolet's surface has no
    * `mat3(mat4)` constructor.
    */
  private val plainCelVertexGlsl: String =
    Shader[CelVertexEnv] { env =>
      def bendToPlanet(p: vec3): vec3 = bendToPlanetImpl(p, env.uPlanetRadius)

      @out var vNormal: vec3    = vec3(0.0f)
      @out var vFogDepth: Float = 0.0f

      def main(): Unit =
        val world: vec3 = (env.modelMatrix * vec4(env.position, 1.0f)).xyz
        val bent: vec3  = bendToPlanet(world)
        vNormal = normalize((env.modelMatrix * vec4(env.normal, 0.0f)).xyz)
        vFogDepth = length((env.viewMatrix * vec4(bent, 1.0f)).xyz)
        env.gl_Position = env.projectionMatrix * env.viewMatrix * vec4(bent, 1.0f)
    }.toGLSL100.code

  private val instancedCelVertexGlsl: String =
    Shader[CelVertexEnv] { env =>
      def bendToPlanet(p: vec3): vec3 = bendToPlanetImpl(p, env.uPlanetRadius)

      @out var vNormal: vec3    = vec3(0.0f)
      @out var vFogDepth: Float = 0.0f

      def main(): Unit =
        val world: vec3 = (env.modelMatrix * env.instanceMatrix * vec4(env.position, 1.0f)).xyz
        val bent: vec3  = bendToPlanet(world)
        vNormal = normalize((env.modelMatrix * env.instanceMatrix * vec4(env.normal, 0.0f)).xyz)
        vFogDepth = length((env.viewMatrix * vec4(bent, 1.0f)).xyz)
        env.gl_Position = env.projectionMatrix * env.viewMatrix * vec4(bent, 1.0f)
    }.toGLSL100.code

  private val vertexColorCelVertexGlsl: String =
    Shader[VertexColorCelVertexEnv] { env =>
      def bendToPlanet(p: vec3): vec3 = bendToPlanetImpl(p, env.uPlanetRadius)

      @out var vNormal: vec3    = vec3(0.0f)
      @out var vColor: vec3     = vec3(1.0f)
      @out var vFogDepth: Float = 0.0f

      def main(): Unit =
        val world: vec3 = (env.modelMatrix * vec4(env.position, 1.0f)).xyz
        val bent: vec3  = bendToPlanet(world)
        vNormal = normalize((env.modelMatrix * vec4(env.normal, 0.0f)).xyz)
        vColor = env.color
        vFogDepth = length((env.viewMatrix * vec4(bent, 1.0f)).xyz)
        env.gl_Position = env.projectionMatrix * env.viewMatrix * vec4(bent, 1.0f)
    }.toGLSL100.code

  private val celFragmentGlsl: String =
    Shader[CelFragmentEnv] { env =>
      @in val vNormal: vec3    = vec3(0.0f)
      @in val vFogDepth: Float = 0.0f

      def main(): Unit =
        val normal: vec3 = normalize(vNormal)
        val d: Float     = dot(normal, normalize(env.uSunDirection))
        val base: vec3   = env.uBaseColor
        val band: vec3 =
          if d > env.uHighlightFloor then base * env.uSunHaze
          else if d >= env.uMidtoneFloor then base * env.uMidtoneFactor
          else mix(base * env.uShadowFactor, env.uShadowViolet, env.uShadowTint)
        val fill: Float       = max(dot(normal, normalize(env.uFillDirection)), 0.0f)
        val bounce: Float     = max(dot(normal, normalize(env.uBounceDirection)), 0.0f)
        val hemisphere: Float = clamp(normal.y * 0.5f + 0.5f, 0.0f, 1.0f)
        val fillLight: vec3   = env.uFillColor * (fill * 0.12f)
        val bounceLight: vec3 = env.uBounceColor * (bounce * 0.06f)
        val ambient: vec3     = mix(env.uHemiGround, env.uHemiSky, hemisphere) * 0.12f
        val lit: vec3         = clamp(band + base * (fillLight + bounceLight + ambient), vec3(0.0f), vec3(1.0f))
        val fog: Float        = smoothstep(env.uFogNear, env.uFogFar, vFogDepth)
        env.gl_FragColor = vec4(mix(lit, env.uFogColor, fog), 1.0f)
    }.toGLSL100.code

  private val vertexColorCelFragmentGlsl: String =
    Shader[VertexColorCelFragmentEnv] { env =>
      @in val vNormal: vec3    = vec3(0.0f)
      @in val vColor: vec3     = vec3(1.0f)
      @in val vFogDepth: Float = 0.0f

      def main(): Unit =
        val normal: vec3 = normalize(vNormal)
        val d: Float     = dot(normal, normalize(env.uSunDirection))
        val base: vec3   = vColor
        val band: vec3 =
          if d > env.uHighlightFloor then base * env.uSunHaze
          else if d >= env.uMidtoneFloor then base * env.uMidtoneFactor
          else mix(base * env.uShadowFactor, env.uShadowViolet, env.uShadowTint)
        val fill: Float       = max(dot(normal, normalize(env.uFillDirection)), 0.0f)
        val bounce: Float     = max(dot(normal, normalize(env.uBounceDirection)), 0.0f)
        val hemisphere: Float = clamp(normal.y * 0.5f + 0.5f, 0.0f, 1.0f)
        val fillLight: vec3   = env.uFillColor * (fill * 0.12f)
        val bounceLight: vec3 = env.uBounceColor * (bounce * 0.06f)
        val ambient: vec3     = mix(env.uHemiGround, env.uHemiSky, hemisphere) * 0.12f
        val lit: vec3         = clamp(band + base * (fillLight + bounceLight + ambient), vec3(0.0f), vec3(1.0f))
        val fog: Float        = smoothstep(env.uFogNear, env.uFogFar, vFogDepth)
        env.gl_FragColor = vec4(mix(lit, env.uFogColor, fog), 1.0f)
    }.toGLSL100.code

  private val flatVertexGlsl: String =
    Shader[FlatVertexEnv] { env =>
      def bendToPlanet(p: vec3): vec3 = bendToPlanetImpl(p, env.uPlanetRadius)

      @out var vFogDepth: Float = 0.0f

      def main(): Unit =
        val world: vec3 = (env.modelMatrix * vec4(env.position, 1.0f)).xyz
        val bent: vec3  = bendToPlanet(world)
        vFogDepth = length((env.viewMatrix * vec4(bent, 1.0f)).xyz)
        env.gl_Position = env.projectionMatrix * env.viewMatrix * vec4(bent, 1.0f)
    }.toGLSL100.code

  private val flatFragmentGlsl: String =
    Shader[FlatFragmentEnv] { env =>
      @in val vFogDepth: Float = 0.0f

      def main(): Unit =
        val fog: Float = smoothstep(env.uFogNear, env.uFogFar, vFogDepth)
        env.gl_FragColor = vec4(mix(env.uBaseColor, env.uFogColor, fog), 1.0f)
    }.toGLSL100.code

  private[sakura] def celVertexShader(curvature: PlanetCurvature, instanced: Boolean): String =
    glslFloatConst("uPlanetRadius", curvature.radius.value) + "\n" +
      (if instanced then instancedCelVertexGlsl else plainCelVertexGlsl)

  private[sakura] def celFragmentShader(
    baseColorHex: Int,
    ramp: CelRamp,
    sunDirection: (Double, Double, Double) = defaultSunDirection
  ): String =
    (glslVec3Const("uBaseColor", baseColorHex) :: celLightingConstants(ramp, sunDirection)).mkString("\n") +
      "\n" + celFragmentGlsl

  private def vertexColorCelVertexShader(curvature: PlanetCurvature): String =
    "attribute vec3 color;\n" + glslFloatConst("uPlanetRadius", curvature.radius.value) + "\n" +
      vertexColorCelVertexGlsl

  private def vertexColorCelFragmentShader: String =
    celLightingConstants(propRamp, defaultSunDirection).mkString("\n") + "\n" + vertexColorCelFragmentGlsl

  private def celLightingConstants(
    ramp: CelRamp,
    sunDirection: (Double, Double, Double)
  ): List[String] =
    List(
      glslVec3Const("uSunHaze", Palette.sunHaze),
      glslDirectionConst("uSunDirection", sunDirection),
      glslVec3Const("uShadowViolet", Palette.shadowViolet),
      glslVec3Const("uFillColor", Palette.fill),
      glslDirectionConst("uFillDirection", fillLightDirection),
      glslVec3Const("uBounceColor", Palette.bounce),
      glslDirectionConst("uBounceDirection", bounceDirection),
      glslVec3Const("uHemiSky", Palette.hemiSky),
      glslVec3Const("uHemiGround", Palette.hemiGround),
      glslVec3Const("uFogColor", Palette.fog),
      glslFloatConst("uFogNear", 44.0),
      glslFloatConst("uFogFar", 205.0),
      glslFloatConst("uHighlightFloor", ramp.highlightFloor),
      glslFloatConst("uMidtoneFloor", ramp.midtoneFloor),
      glslFloatConst("uMidtoneFactor", ramp.midtoneFactor),
      glslFloatConst("uShadowFactor", ramp.shadowFactor),
      glslFloatConst("uShadowTint", ramp.shadowTint)
    )

  private def glslDirectionConst(name: String, direction: (Double, Double, Double)): String =
    val (x, y, z) = direction
    s"const vec3 $name = vec3(${glslFloat(x)}, ${glslFloat(y)}, ${glslFloat(z)});"

  private[sakura] def flatVertexShader(curvature: PlanetCurvature): String =
    glslFloatConst("uPlanetRadius", curvature.radius.value) + "\n" + flatVertexGlsl

  private[sakura] def flatFragmentShader(baseColorHex: Int): String =
    List(
      glslVec3Const("uBaseColor", baseColorHex),
      glslVec3Const("uFogColor", Palette.fog),
      glslFloatConst("uFogNear", 44.0),
      glslFloatConst("uFogFar", 205.0)
    ).mkString("\n") + "\n" + flatFragmentGlsl
end ToonMaterial
