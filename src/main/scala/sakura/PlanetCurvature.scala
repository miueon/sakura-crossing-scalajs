package sakura

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.constraint.any.DescribedAs
import ultraviolet.syntax.*

import scala.math

type PositiveLengthConstraint = Greater[0.0] DescribedAs "Must be a positive length"

type PlanetRadius = PlanetRadius.T
object PlanetRadius extends RefinedSubtype[Double, PositiveLengthConstraint]

type ChordTolerance = ChordTolerance.T
object ChordTolerance extends RefinedSubtype[Double, PositiveLengthConstraint]

/** Spherical embedding of the flat plane the simulation runs on: `y' = y - (x^2 + z^2) / 2R`.
  *
  * Gameplay never sees the bend — positions, collisions and elevation are flat — so the planet
  * curvature exists only here, in the vertex-shader chunk the ground material compiles in.
  */
final case class PlanetCurvature(radius: PlanetRadius):

  /** Host-side twin of [[bendChunk]]; used wherever host code needs a bent coordinate. */
  def bend(x: Double, y: Double, z: Double): Double =
    y - (x * x + z * z) / (2.0 * radius.value)

  /** Segments a straight `span` needs so no single chord departs from the bend by more than
    * `tolerance`.
    *
    * Along any straight line of the flat plane the bend is a parabola of constant second
    * derivative `1 / R`, and a chord of length `h` over such a curve misses it by at most
    * `h^2 / 8R`; solving `h^2 / 8R <= tolerance` gives the step below.
    */
  def subdivisions(span: Double, tolerance: ChordTolerance): Int =
    val maxChord = math.sqrt(8.0 * radius.value * tolerance.value)
    math.max(1, math.ceil(span / maxChord).toInt)

  /** GLSL chunk bending flat coordinates onto the planet; the radius is baked into the const
    * line above the Ultraviolet-compiled body, so the shader and [[bend]] cannot drift apart.
    */
  def bendChunk: String =
    glslFloatConst("uPlanetRadius", radius.value) + "\n" + bendToPlanetGlsl
end PlanetCurvature

object PlanetCurvature:

  val Default: PlanetCurvature = PlanetCurvature(PlanetRadius(250.0))

/** Shader blocks reference their runtime parameters through env members: Ultraviolet emits the
  * member as a bare GLSL identifier and declares nothing, so the const line prepended to the
  * compiled body is the declaration. Three.js ShaderMaterial prefixes its own declarations for
  * the built-ins (`projectionMatrix`, `position`, ...), which is why those live in envs too and
  * must never be declared by the shader source itself.
  */
private[sakura] case class BendEnv(uPlanetRadius: Float)

/** Single source of truth for the bend math every vertex shader compiles in; the host-side twin
  * is [[PlanetCurvature.bend]].
  */
inline private[sakura] def bendToPlanetImpl(p: vec3, planetRadius: Float): vec3 =
  val d2: Float = p.x * p.x + p.z * p.z
  vec3(p.x, p.y - d2 / (2.0f * planetRadius), p.z)

private[sakura] val bendToPlanetGlsl: String =
  Shader[BendEnv] { env =>
    def bendToPlanet(p: vec3): vec3 = bendToPlanetImpl(p, env.uPlanetRadius)
  }.toGLSL100.code

/** GLSL has no implicit int-to-float widening, and Scala.js `Double.toString` drops the `.0` that
  * makes a whole number a `float` literal: `250` is not a valid operand for a `float`.
  */
private[sakura] def glslFloat(value: Double): String =
  val text = value.toString
  if text.exists(c => c == '.' || c == 'e' || c == 'E') then text else s"$text.0"

private[sakura] def glslFloatConst(name: String, value: Double): String =
  s"const float $name = ${glslFloat(value)};"

private[sakura] def glslVec3Const(name: String, hex: Int): String =
  s"const vec3 $name = ${Palette.glslRgb(hex)};"
