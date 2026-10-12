package sakura.core

import sakura.ThreeInterop
import ultraviolet.syntax.*
import vendor.three.*

import scala.annotation.nowarn
import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.JSConverters.*

/** Cel shading ported 1:1 from upstream `src/core/toon.js`.
  *
  * Everything in the scene uses `MeshToonMaterial` with a hand-authored gradient ramp, so direct
  * sunlight is quantised into flat bands instead of a smooth falloff. On top of that the toon
  * BRDF is patched so the darker bands are tinted toward a cool violet rather than simply being a
  * darker version of the base colour -- that hue shift in shadow is most of what separates
  * "anime cel" from "low-poly 3D".
  */
object Toon:

  private val Ramps2     = Array(96, 255)
  private val Ramps3     = Array(92, 178, 255)
  private val Ramps4     = Array(80, 142, 202, 255)
  private val Ramps5     = Array(74, 124, 172, 214, 255)
  private val RampsSoft  = Array(180, 255)
  private val RampsSoft3 = Array(172, 214, 255)

  private def rampStops(bands: Int | String): Array[Int] = bands match
    case 2       => Ramps2
    case 3       => Ramps3
    case 4       => Ramps4
    case 5       => Ramps5
    case "soft"  => RampsSoft
    case "soft3" => RampsSoft3
    case _       => Ramps3

  private val rampCache = mutable.HashMap.empty[Any, DataTexture]

  def gradientMap(bands: Int | String = 3): DataTexture =
    rampCache.get(bands) match
      case Some(cached) => cached
      case None =>
        val stops = rampStops(bands)
        val data  = new js.typedarray.Uint8Array(stops.length * 4)
        stops.zipWithIndex.foreach { case (stop, index) =>
          data(index * 4) = stop.toByte
          data(index * 4 + 1) = stop.toByte
          data(index * 4 + 2) = stop.toByte
          data(index * 4 + 3) = 255.toByte
        }
        val texture = new DataTexture(data, stops.length, 1.0, 1023)
        texture.minFilter = 1003
        texture.magFilter = 1003
        texture.generateMipmaps = false
        ThreeInterop.markForUpload(texture)
        rampCache.update(bands, texture)
        texture

  final case class CelOptions(
    color: Int = 0xffffff,
    bands: Int | String = 3,
    tint: Int = 0x6c5f8c,
    map: Option[Texture[?, ?]] = None,
    alphaMap: Option[Texture[?, ?]] = None,
    emissive: Option[Int] = None,
    emissiveIntensity: Double = 1.0,
    transparent: Boolean = false,
    opacity: Double = 1.0,
    side: Int = 0,
    alphaTest: Double = 0.0,
    depthWrite: Option[Boolean] = None,
    fog: Boolean = true,
    vertexColors: Boolean = false,
    cache: Boolean = true
  )

  private val matCache = mutable.HashMap.empty[String, MeshToonMaterial]

  /** Cel-shaded material factory. Results are cached by parameter signature so the whole street
    * ends up sharing a few dozen shader programs.
    */
  def cel(opts: CelOptions = CelOptions()): MeshToonMaterial =
    val key =
      if opts.cache && opts.map.isEmpty && opts.alphaMap.isEmpty then Some(celCacheKey(opts))
      else None
    key.flatMap(matCache.get) match
      case Some(cached) => cached
      case None =>
        val parameters = MeshToonMaterialParameters(
          color = opts.color,
          gradientMap = ThreeInterop.asMaterialTexture(gradientMap(opts.bands)),
          map = opts.map.map(texture => ThreeInterop.asMaterialTexture(texture)).orUndefined,
          alphaMap = opts.alphaMap.map(texture => ThreeInterop.asMaterialTexture(texture)).orUndefined,
          emissive = opts.emissive.getOrElse(0x000000),
          emissiveIntensity = opts.emissiveIntensity,
          transparent = opts.transparent,
          opacity = opts.opacity,
          side = sideLiteral(opts.side),
          alphaTest = opts.alphaTest,
          fog = opts.fog,
          vertexColors = opts.vertexColors
        )
        val material = new MeshToonMaterial(parameters)
        opts.depthWrite.foreach(material.depthWrite = _)
        applyShadowTint(material, opts.tint)
        key.foreach(cacheKey => matCache.update(cacheKey, material))
        material
    end match
  end cel

  private def celCacheKey(opts: CelOptions): String =
    List[Any](
      opts.color, opts.bands, opts.tint, opts.emissive, opts.emissiveIntensity,
      opts.transparent, opts.opacity, opts.side, opts.alphaTest, opts.depthWrite,
      opts.fog, opts.vertexColors
    ).mkString("|")

  final case class FlatOptions(
    color: Int = 0xffffff,
    map: Option[Texture[?, ?]] = None,
    transparent: Boolean = false,
    opacity: Double = 1.0,
    side: Int = 0,
    alphaTest: Double = 0.0,
    depthWrite: Option[Boolean] = None,
    fog: Boolean = true,
    cache: Boolean = true,
    toneMapped: Boolean = true
  )

  private val flatCache = mutable.HashMap.empty[String, MeshBasicMaterial]

  /** Unlit flat colour -- for sky, distant silhouettes, glowing panels, glass. */
  def flat(opts: FlatOptions = FlatOptions()): MeshBasicMaterial =
    val key =
      if opts.cache && opts.map.isEmpty then Some(flatCacheKey(opts)) else None
    key.flatMap(flatCache.get) match
      case Some(cached) => cached
      case None =>
        val parameters = MeshBasicMaterialParameters(
          color = opts.color,
          map = opts.map.map(texture => ThreeInterop.asMaterialTexture(texture)).orUndefined,
          transparent = opts.transparent,
          opacity = opts.opacity,
          side = sideLiteral(opts.side),
          alphaTest = opts.alphaTest,
          fog = opts.fog,
          toneMapped = opts.toneMapped
        )
        val material = new MeshBasicMaterial(parameters)
        opts.depthWrite.foreach(material.depthWrite = _)
        key.foreach(cacheKey => flatCache.update(cacheKey, material))
        material
  end flat

  private def flatCacheKey(opts: FlatOptions): String =
    List[Any](
      opts.color, opts.transparent, opts.opacity, opts.side, opts.alphaTest,
      opts.depthWrite, opts.fog, opts.toneMapped
    ).mkString("|")

  private def sideLiteral(side: Int): 0 | 1 | 2 = side match
    case 1 => 1
    case 2 => 2
    case _ => 0

  /** Shared material shorthands used all over the world builders. */
  object MAT:
    def ink: MeshBasicMaterial       = flat(FlatOptions(color = Pal.ink, fog = false))
    def glassDark: MeshBasicMaterial = flat(FlatOptions(color = Pal.glassDark))

  /* ------------------------------------------------------------------ *
   * The shadow-tint patch. Upstream rewrites `lights_toon_pars_fragment`
   * so the toon irradiance is multiplied by a violet-tinted band; the
   * helper function itself is emitted by Ultraviolet so the arithmetic
   * stays type-checked GLSL instead of a pasted string.
   * ------------------------------------------------------------------ */

  private val ToonChunk = "lights_toon_pars_fragment"
  private val ToonLine =
    "vec3 irradiance = getGradientIrradiance( geometryNormal, directLight.direction ) * directLight.color;"

  final private case class CelTintEnv(uShadowTint: vec3)

  @nowarn("msg=unused")
  private val celTintHelperGlsl: String =
    val emitted = Shader[CelTintEnv, Unit] { env =>
      def applyCelShadowTint(celBand: vec3, lightColor: vec3): vec3 =
        celBand * mix(env.uShadowTint, vec3(1.0f), celBand) * lightColor

      def main(): Unit = ()
    }.toGLSL100.code
    // The block needs a main to compile; the emitted trailing `void main(){}` would clash
    // with the host shader's own main, so it is asserted away rather than silently stripped.
    val trailing = "void main(){}"
    if !emitted.endsWith(trailing) then
      throw new IllegalStateException(
        s"sakura-crossing: ultraviolet changed the cel-tint helper emission shape: $emitted"
      )
    emitted.stripSuffix(trailing)

  private def countOccurrences(source: String, needle: String): Int =
    source.split(java.util.regex.Pattern.quote(needle), -1).length - 1

  /** Tint the shadow side of a toon material toward a cool hue. Fails loudly when the upstream
    * toon chunk or the include directive is not found exactly once -- never silently unpatched.
    */
  private def applyShadowTint(mat: MeshToonMaterial, tint: Int): mat.type =
    val source = ShaderChunk.lights_toon_pars_fragment
    val lines  = countOccurrences(source, ToonLine)
    if lines != 1 then
      throw new IllegalStateException(
        s"sakura-crossing: expected exactly one toon irradiance line in $ToonChunk, found $lines"
      )
    val patchedChunk =
      "uniform vec3 uShadowTint;\n" + celTintHelperGlsl +
        source.replace(
          ToonLine,
          "vec3 celBand = getGradientIrradiance( geometryNormal, directLight.direction );\n" +
            "\tvec3 irradiance = applyCelShadowTint( celBand, directLight.color );"
        )
    ThreeInterop.setUserDataColorHolder(mat, "shadowTint", tint)
    ThreeInterop.setOnBeforeCompile(
      mat,
      scope =>
        scope.setUniformColor("uShadowTint", new Color(tint))
        scope.replaceFragmentInclude(ToonChunk, patchedChunk)
    )
    val hex = new Color(tint).getHexString()
    ThreeInterop.setCustomProgramCacheKey(mat, () => s"celTint_$hex")
    mat
  end applyShadowTint
end Toon
