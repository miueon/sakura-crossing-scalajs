package sakura.world

import sakura.{glslFloatConst, glslVec3Const, MaterialTexture, Palette, ThreeInterop}
import ultraviolet.syntax.*
import vendor.three.*

import scala.math
import scala.scalajs.js
import scala.util.Random

object SkyDome:
  private val CloudCount   = 22
  private val HillSegments = 90
  private val DomeRadius   = 500.0

  final private case class HillLayer(
    z: Double,
    height: Double,
    color: Int,
    width: Double,
    bumps: Int,
    y: Double
  )

  final private case class SkyVertexEnv(
    position: vec3,
    modelMatrix: mat4,
    viewMatrix: mat4,
    projectionMatrix: mat4,
    var gl_Position: vec4
  )

  final private case class SkyFragmentEnv(
    uTop: vec3,
    uMid: vec3,
    uHaze: vec3,
    uBands: Float,
    var gl_FragColor: vec4
  )

  private val vertexGlsl =
    Shader[SkyVertexEnv] { env =>
      @out var vWorld: vec3 = vec3(0.0f)

      def main(): Unit =
        val world: vec4 = env.modelMatrix * vec4(env.position, 1.0f)
        vWorld = world.xyz
        env.gl_Position = env.projectionMatrix * env.viewMatrix * world
    }.toGLSL100.code

  private val fragmentGlsl =
    Shader[SkyFragmentEnv] { env =>
      @in val vWorld: vec3 = vec3(0.0f)

      def main(): Unit =
        val h: Float        = normalize(vWorld).y
        val t: Float        = clamp(h * 1.15f + 0.02f, 0.0f, 1.0f)
        val q: Float        = floor(t * env.uBands) / env.uBands
        val softBand: Float = mix(t, q, 0.35f)
        val base: vec3      = mix(env.uHaze, env.uMid, smoothstep(0.0f, 0.30f, softBand))
        val blue: vec3      = mix(base, env.uTop, smoothstep(0.26f, 0.92f, softBand))
        val warm: Float     = smoothstep(0.12f, -0.05f, h) * 0.6f
        env.gl_FragColor = vec4(mix(blue, env.uHaze, warm), 1.0f)
    }.toGLSL100.code

  private[sakura] val skyVertexShader: String = vertexGlsl
  private[sakura] val skyFragmentShader: String =
    List(
      glslVec3Const("uTop", Palette.skyTop),
      glslVec3Const("uMid", Palette.skyMid),
      glslVec3Const("uHaze", Palette.skyHaze),
      glslFloatConst("uBands", 26.0)
    ).mkString("\n") + "\n" + fragmentGlsl

  def build(cloudTexture: MaterialTexture): SceneAssembly =
    val sky   = buildSky(cloudTexture)
    val hills = buildDistantHills()
    val group = new Group()
    ThreeInterop.addChild(group, sky.group)
    ThreeInterop.addChild(group, hills.group)
    SceneAssembly(
      group,
      sky.geometries ++ hills.geometries,
      sky.materials ++ hills.materials
    )

  private def buildSky(cloudTexture: MaterialTexture): SceneAssembly =
    val domeGeometry = new SphereGeometry(DomeRadius, 32.0, 20.0)
    val domeMaterial = new ShaderMaterial(
      ShaderMaterialParameters(
        vertexShader = js.defined(skyVertexShader),
        fragmentShader = js.defined(skyFragmentShader),
        side = js.defined(1)
      )
    )
    val dome           = new Mesh(domeGeometry, domeMaterial)
    val cloudGeometry  = new PlaneGeometry(1.0, 1.0)
    val shadowMaterial = cloudMaterial(cloudTexture, Palette.cloudShade, 0.34)
    val frontMaterial  = cloudMaterial(cloudTexture, Palette.cloud, 0.62)
    val shadows        = new InstancedMesh(cloudGeometry, shadowMaterial, CloudCount.toDouble)
    val fronts         = new InstancedMesh(cloudGeometry, frontMaterial, CloudCount.toDouble)
    val rng            = new Random(7781L)
    (0 until CloudCount).foreach { index =>
      val radius    = between(rng, 220.0, 350.0)
      val angle     = between(rng, 0.0, math.Pi * 2.0)
      val width     = between(rng, 90.0, 210.0)
      val height    = width * between(rng, 0.24, 0.34)
      val y         = between(rng, 46.0, 140.0)
      val x         = math.cos(angle) * radius
      val z         = math.sin(angle) * radius
      val center    = new Vector3(x, y, z)
      val billboard = new Group()
      billboard.position.set(x, y, z)
      billboard.lookAt(0.0, y * 0.55, 0.0)
      val rotation = billboard.rotation
      val turn     = new Quaternion().setFromEuler(rotation)
      val scale    = new Vector3(width, height, 1.0)
      fronts.setMatrixAt(index, new Matrix4().compose(center, turn, scale))

      val shadowOffset = rotateBillboardOffset(2.0, -height * 0.1, -1.5, rotation.x, rotation.y, rotation.z)
      val shadowCenter = new Vector3(x, y, z).add(ThreeInterop.asVector3Like(shadowOffset))
      shadows.setMatrixAt(index, new Matrix4().compose(shadowCenter, turn, scale))
    }
    ThreeInterop.markForUpload(shadows.instanceMatrix)
    ThreeInterop.markForUpload(fronts.instanceMatrix)

    val group = new Group()
    group.add(dome)
    ThreeInterop.addChild(group, shadows)
    ThreeInterop.addChild(group, fronts)
    SceneAssembly(group, List(domeGeometry, cloudGeometry), List(domeMaterial, shadowMaterial, frontMaterial))
  end buildSky

  private def cloudMaterial(texture: MaterialTexture, color: Int, opacity: Double): MeshBasicMaterial =
    val material = new MeshBasicMaterial(MeshBasicMaterialParameters(map = js.defined(texture)))
    material.color.setHex(color)
    material.transparent = true
    material.opacity = opacity
    material.depthWrite = false
    material

  private def buildDistantHills(): SceneAssembly =
    val layers = List(
      HillLayer(330.0, 46.0, Palette.hillFar, 900.0, 9, -6.0),
      HillLayer(250.0, 34.0, Palette.hillNear, 760.0, 7, -4.0)
    )
    val rng        = new Random(4242L)
    val group      = new Group()
    val geometries = layers.map(hillGeometry)
    val materials = layers.map { layer =>
      val material = new MeshBasicMaterial()
      material.color.setHex(layer.color)
      material
    }

    layers.zip(geometries.zip(materials)).foreach { case (layer, (geometry, material)) =>
      val towardCamera = new Mesh(geometry, material)
      towardCamera.position.set(between(rng, -30.0, 30.0), layer.y, layer.z)
      towardCamera.rotation.y = math.Pi
      group.add(towardCamera)

      val behindCamera = new Mesh(geometry, material)
      behindCamera.position.set(between(rng, -40.0, 40.0), layer.y, -layer.z * 1.15)
      group.add(behindCamera)
    }

    SceneAssembly(group, geometries, materials)
  end buildDistantHills

  private def hillGeometry(layer: HillLayer): BufferGeometry[?, ?] =
    val points = (0 to HillSegments).map { index =>
      val t = index.toDouble / HillSegments
      val ridge = (1 to layer.bumps).map { bump =>
        math.sin(t * math.Pi * bump * 1.7 + bump * 2.1) * (layer.height / (bump * 1.25))
      }.sum
      ((t - 0.5) * layer.width, math.max(2.0, ridge * 0.55 + layer.height * 0.55))
    }
    val vertices = (0 until HillSegments).flatMap { index =>
      val (x0, y0) = points(index)
      val (x1, y1) = points(index + 1)
      List(
        x0, y0, 0.0,
        x0, -60.0, 0.0,
        x1, y1, 0.0,
        x0, -60.0, 0.0,
        x1, -60.0, 0.0,
        x1, y1, 0.0
      )
    }
    val position = new Float32BufferAttribute(vertices.size.toDouble, itemSize = 3)
    vertices.grouped(3).zipWithIndex.foreach { case (point, index) =>
      position.setXYZ(index, point(0), point(1), point(2))
    }
    val geometry = new BufferGeometry()
    geometry.setAttribute("position", position)
    geometry
  end hillGeometry

  private def rotateBillboardOffset(
    x: Double,
    y: Double,
    z: Double,
    pitch: Double,
    yaw: Double,
    roll: Double
  ): Vector3 =
    val rollX = math.cos(roll) * x - math.sin(roll) * y
    val rollY = math.sin(roll) * x + math.cos(roll) * y
    val yawX  = math.cos(yaw) * rollX + math.sin(yaw) * z
    val yawZ  = -math.sin(yaw) * rollX + math.cos(yaw) * z
    new Vector3(
      yawX,
      math.cos(pitch) * rollY - math.sin(pitch) * yawZ,
      math.sin(pitch) * rollY + math.cos(pitch) * yawZ
    )

  private def between(random: Random, low: Double, high: Double): Double =
    low + random.nextDouble() * (high - low)
end SkyDome
