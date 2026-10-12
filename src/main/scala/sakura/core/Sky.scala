package sakura.core

import sakura.{ShaderUniformValue, ThreeInterop}
import ultraviolet.syntax.*
import vendor.three.*

import scala.annotation.nowarn
import scala.math

/** The painted sky: a three-stop gradient dome plus deterministic cel clouds and distant
  * hill silhouettes, ported 1:1 from upstream `src/core/sky.js`.
  */
object Sky:

  final case class BuiltSky(dome: Mesh[?, ?, ?], clouds: Group[Object3DEventMap])

  final private case class DomeVertexEnv(
    modelMatrix: mat4,
    viewMatrix: mat4,
    projectionMatrix: mat4,
    position: vec3,
    var gl_Position: vec4
  )

  final private case class DomeFragmentEnv(
    uTop: vec3,
    uMid: vec3,
    uHaze: vec3,
    uBands: Float,
    var gl_FragColor: vec4
  )

  @nowarn("msg=unused")
  @nowarn("msg=mutated but not read")
  private val domeVertexGlsl: String =
    Shader[DomeVertexEnv] { env =>
      @out var vWorld: vec3 = vec3(0.0f)

      def main(): Unit =
        val world: vec4 = env.modelMatrix * vec4(env.position, 1.0f)
        vWorld = world.xyz
        env.gl_Position = env.projectionMatrix * env.viewMatrix * world
    }.toGLSL100.code

  @nowarn("msg=unused")
  private val domeFragmentGlsl: String =
    Shader[DomeFragmentEnv] { env =>
      @in val vWorld: vec3 = vec3(0.0f)

      def main(): Unit =
        val h: Float     = normalize(vWorld).y
        val t: Float     = clamp(h * 1.15f + 0.02f, 0.0f, 1.0f)
        val q: Float     = floor(t * env.uBands) / env.uBands
        val bandT: Float = mix(t, q, 0.35f)
        val base: vec3   = mix(env.uHaze, env.uMid, smoothstep(0.0f, 0.30f, bandT))
        val blue: vec3   = mix(base, env.uTop, smoothstep(0.26f, 0.92f, bandT))
        val warm: Float  = smoothstep(0.12f, -0.05f, h) * 0.6f
        env.gl_FragColor = vec4(mix(blue, env.uHaze, warm), 1.0f)
    }.toGLSL100.code

  // Ultraviolet references uniforms bare; the declarations arrive with the patch prefix.
  private val domeUniformDeclarations: String =
    "uniform vec3 uTop;\nuniform vec3 uMid;\nuniform vec3 uHaze;\nuniform float uBands;\n"

  /** A three-stop painted gradient dome plus a handful of flat cel clouds. Slight banding is
    * intentional -- it reads as airbrushed background art rather than a physical sky.
    */
  def buildSky(scene: Scene[Object3DEventMap], radius: Double = 500.0, cloudTex: () => Texture[?, ?]): BuiltSky =
    val geometry = new SphereGeometry(radius, 32.0, 20.0)
    val material = ThreeInterop.shaderMaterialRaw(
      vertexShader = domeVertexGlsl,
      fragmentShader = domeUniformDeclarations + domeFragmentGlsl,
      uniforms = List(
        "uTop"   -> ShaderUniformValue.ThreeColor(Pal.skyTop),
        "uMid"   -> ShaderUniformValue.ThreeColor(Pal.skyMid),
        "uHaze"  -> ShaderUniformValue.ThreeColor(Pal.skyHaze),
        "uBands" -> ShaderUniformValue.Scalar(26.0)
      ),
      side = 1.0,
      transparent = false,
      depthWrite = true,
      fog = false
    )
    val dome = new Mesh(geometry, material)
    dome.frustumCulled = false
    dome.renderOrder = -10.0
    ThreeInterop.addChild(scene, dome)

    // flat clouds: two rings of billboarded puffs, no depth writes
    val texture = cloudTex()
    val rng     = Util.rngKit(7781)
    val clouds  = new Group()
    val matA = Toon.flat(Toon.FlatOptions(
      color = Pal.cloud, map = Some(texture), transparent = true, opacity = 0.62,
      depthWrite = Some(false), fog = false, cache = false
    ))
    val matB = Toon.flat(Toon.FlatOptions(
      color = Pal.cloudShade, map = Some(texture), transparent = true, opacity = 0.34,
      depthWrite = Some(false), fog = false, cache = false
    ))
    val map = matA.map
    if map != null then
      map.wrapS = 1001
      map.wrapT = 1001

    (0 until 22).foreach { _ =>
      val distance = rng.range(220.0, 350.0)
      val angle    = rng.range(0.0, Util.TAU)
      val width    = rng.range(90.0, 210.0)
      val height   = width * rng.range(0.24, 0.34)
      val y        = rng.range(46.0, 140.0)
      val puff     = new Group()
      val back     = new Mesh(new PlaneGeometry(width, height), matB)
      back.position.set(2.0, -height * 0.1, -1.5)
      val front = new Mesh(new PlaneGeometry(width, height), matA)
      ThreeInterop.addChild(puff, back)
      ThreeInterop.addChild(puff, front)
      puff.position.set(math.cos(angle) * distance, y, math.sin(angle) * distance)
      puff.lookAt(0.0, y * 0.55, 0.0)
      puff.renderOrder = -9.0
      ThreeInterop.addChild(clouds, puff)
    }
    clouds.frustumCulled = false
    ThreeInterop.addChild(scene, clouds)

    BuiltSky(dome, clouds)
  end buildSky

  final private case class HillLayer(z: Double, height: Double, color: Int, width: Double, bumps: Int, y: Double)

  /** Pale layered ridge lines on the horizon. Pure silhouette, unlit, so they read as painted
    * background flats.
    */
  def buildDistantHills(scene: Scene[Object3DEventMap]): Group[Object3DEventMap] =
    val group = new Group()
    val rng   = Util.rngKit(4242)

    val layers = Vector(
      HillLayer(z = -330.0, height = 46.0, color = Pal.hillFar, width = 900.0, bumps = 9, y = -6.0),
      HillLayer(z = -250.0, height = 34.0, color = Pal.hill, width = 760.0, bumps = 7, y = -4.0)
    )

    layers.foreach { layer =>
      val n = 90
      val pts = (0 to n).map { index =>
        val t = index.toDouble / n
        val x = (t - 0.5) * layer.width
        var y = 0.0
        (1 to layer.bumps).foreach { bump =>
          y += math.sin(t * math.Pi * bump * 1.7 + bump * 2.1) * (layer.height / (bump * 1.25))
        }
        new Vector2(x, math.max(2.0, y * 0.55 + layer.height * 0.55))
      }
      val shape = new Shape()
      shape.moveTo(pts(0).x, -60.0)
      pts.foreach(point => shape.lineTo(point.x, point.y))
      shape.lineTo(pts.last.x, -60.0)
      shape.closePath()
      val mesh = new Mesh(
        new ShapeGeometry(shape),
        Toon.flat(Toon.FlatOptions(color = layer.color, fog = false))
      )
      mesh.position.set(rng.range(-30.0, 30.0), layer.y, layer.z)
      mesh.renderOrder = -8.0
      ThreeInterop.addChild(group, mesh)

      // mirror a copy behind the camera so turning around still reads as a valley
      val back = mesh.clone()
      back.position.set(rng.range(-40.0, 40.0), layer.y, -layer.z * 1.15)
      back.rotation.y = math.Pi
      ThreeInterop.addChild(group, back)
    }
    ThreeInterop.addChild(scene, group)
    group
  end buildDistantHills
end Sky
