package sakura

import ultraviolet.syntax.*
import vendor.three.*

import scala.annotation.nowarn
import scala.collection.mutable
import scala.math
import scala.scalajs.js.JSConverters.*

final private[sakura] case class OutlineAssets(
  geometry: BufferGeometry[?, ?],
  material: ShaderMaterial
)

object Outline:
  private val VertexTolerance = 1e-4
  private val resolutions     = mutable.ListBuffer.empty[Vector2]

  final private case class VertexKey(x: Long, y: Long, z: Long)

  final private case class HullVertexEnv(
    uPlanetRadius: Float,
    uThickness: Float,
    uResolution: vec2,
    position: vec3,
    normal: vec3,
    modelMatrix: mat4,
    viewMatrix: mat4,
    projectionMatrix: mat4,
    normalMatrix: mat3,
    var gl_Position: vec4
  )

  final private case class InstancedHullVertexEnv(
    uPlanetRadius: Float,
    uThickness: Float,
    uResolution: vec2,
    position: vec3,
    normal: vec3,
    modelMatrix: mat4,
    instanceMatrix: mat4,
    viewMatrix: mat4,
    projectionMatrix: mat4,
    normalMatrix: mat3,
    var gl_Position: vec4
  )

  final private case class HullFragmentEnv(
    uColor: vec3,
    uOpacity: Float,
    var gl_FragColor: vec4
  )

  @nowarn("msg=unused")
  private val hullVertexGlsl: String =
    Shader[HullVertexEnv] { env =>
      def bendToPlanet(point: vec3): vec3 = bendToPlanetImpl(point, env.uPlanetRadius)

      def main(): Unit =
        val world: vec3        = (env.modelMatrix * vec4(env.position, 1.0f)).xyz
        val bent: vec3         = bendToPlanet(world)
        val viewPosition: vec4 = env.viewMatrix * vec4(bent, 1.0f)
        val viewNormal: vec3   = normalize(env.normalMatrix * env.normal)
        val clip: vec4         = env.projectionMatrix * viewPosition
        val clipNormal: vec3   = normalize((env.projectionMatrix * vec4(viewNormal, 0.0f)).xyz)
        val aspect: vec2       = vec2(env.uResolution.y / env.uResolution.x, 1.0f)
        val offset: vec2       = clipNormal.xy * aspect * env.uThickness * clip.w * 0.5f
        env.gl_Position = vec4(clip.x + offset.x, clip.y + offset.y, clip.z, clip.w)
    }.toGLSL100.code

  @nowarn("msg=unused")
  private val instancedHullVertexGlsl: String =
    Shader[InstancedHullVertexEnv] { env =>
      def bendToPlanet(point: vec3): vec3 = bendToPlanetImpl(point, env.uPlanetRadius)

      def main(): Unit =
        val instancePosition: vec4 = env.instanceMatrix * vec4(env.position, 1.0f)
        val world: vec3            = (env.modelMatrix * instancePosition).xyz
        val bent: vec3             = bendToPlanet(world)
        val viewPosition: vec4     = env.viewMatrix * vec4(bent, 1.0f)
        val instanceNormal: vec3   = (env.instanceMatrix * vec4(env.normal, 0.0f)).xyz
        val viewNormal: vec3       = normalize(env.normalMatrix * instanceNormal)
        val clip: vec4             = env.projectionMatrix * viewPosition
        val clipNormal: vec3       = normalize((env.projectionMatrix * vec4(viewNormal, 0.0f)).xyz)
        val aspect: vec2           = vec2(env.uResolution.y / env.uResolution.x, 1.0f)
        val offset: vec2           = clipNormal.xy * aspect * env.uThickness * clip.w * 0.5f
        env.gl_Position = vec4(clip.x + offset.x, clip.y + offset.y, clip.z, clip.w)
    }.toGLSL100.code

  @nowarn("msg=unused")
  private val hullFragmentGlsl: String =
    Shader[HullFragmentEnv] { env =>
      def main(): Unit = env.gl_FragColor = vec4(env.uColor, env.uOpacity)
    }.toGLSL100.code

  def smoothedGeometry(geometry: BufferGeometry[?, ?]): BufferGeometry[?, ?] =
    val cloned = geometry.clone()
    val nonIndexed = Option(cloned.getIndex()) match
      case Some(_) => cloned.toNonIndexed()
      case None    => cloned
    val position = nonIndexed.getAttribute("position") match
      case attribute: BufferAttribute[?] => attribute
      case _                             => throw new IllegalStateException("sakura-crossing: outline source has no position attribute")
    val vertexCount = position.count.toInt
    if vertexCount % 3 != 0 then
      throw new IllegalStateException("sakura-crossing: outline source is not a triangle list")

    val uniquePositions = mutable.ArrayBuffer.empty[Double]
    val indices         = mutable.ArrayBuffer.empty[Double]
    val vertices        = mutable.HashMap.empty[VertexKey, Int]
    (0 until vertexCount).foreach { index =>
      val x     = position.getX(index)
      val y     = position.getY(index)
      val z     = position.getZ(index)
      val key   = vertexKey(x, y, z)
      val remap = vertices.getOrElseUpdate(key, appendVertex(x, y, z, uniquePositions))
      indices += remap.toDouble
    }

    val result         = new BufferGeometry()
    val positionOutput = new Float32BufferAttribute(uniquePositions.size.toDouble, itemSize = 3)
    uniquePositions.grouped(3).zipWithIndex.foreach { case (vertex, index) =>
      positionOutput.setXYZ(index, vertex(0), vertex(1), vertex(2))
    }
    result.setAttribute("position", positionOutput)
    result.setIndex(indices.toJSArray)
    result.computeVertexNormals()
    result
  end smoothedGeometry

  def outlineMaterial(
    thickness: Double,
    curvature: PlanetCurvature,
    instanced: Boolean = false,
    color: Int = Palette.ink,
    opacity: Double = 1.0
  ): ShaderMaterial =
    val resolution = new Vector2(1920.0, 1080.0)
    resolutions += resolution
    ThreeInterop.shaderMaterial(
      vertexShader = "uniform float uThickness;\nuniform vec2 uResolution;\n" +
        glslFloatConst("uPlanetRadius", curvature.radius.value) + "\n" +
        (if instanced then instancedHullVertexGlsl else hullVertexGlsl),
      fragmentShader = "uniform vec3 uColor;\nuniform float uOpacity;\n" + hullFragmentGlsl,
      uniforms = List(
        "uThickness"  -> ShaderUniformValue.Scalar(thickness),
        "uResolution" -> ShaderUniformValue.Vector(resolution),
        "uColor"      -> ShaderUniformValue.ColorHex(color),
        "uOpacity"    -> ShaderUniformValue.Scalar(opacity)
      ),
      side = 1.0,
      transparent = opacity < 1.0,
      opacity = opacity,
      depthTest = true,
      depthWrite = true
    )
  end outlineMaterial

  def hullOutline(
    mesh: Mesh[?, ?, ?],
    thickness: Double,
    curvature: PlanetCurvature,
    color: Int = Palette.ink,
    opacity: Double = 1.0
  ): OutlineAssets =
    val sourceInstances = ThreeInterop.asInstancedMesh(mesh)
    val geometry        = smoothedGeometry(mesh.geometry)
    val material        = outlineMaterial(thickness, curvature, sourceInstances.nonEmpty, color, opacity)
    val shell = sourceInstances match
      case Some(instanced) =>
        val shell = new InstancedMesh(geometry, material, instanced.count)
        shell.instanceMatrix = instanced.instanceMatrix
        shell.count = instanced.count
        shell
      case None => new Mesh(geometry, material)
    shell.castShadow = false
    shell.receiveShadow = false
    shell.renderOrder = mesh.renderOrder - 1.0
    shell.frustumCulled = mesh.frustumCulled
    ThreeInterop.setUserDataFlag(mesh, "hasOutline")
    ThreeInterop.setUserDataFlag(shell, "isOutline")
    ThreeInterop.addChild(mesh, shell)
    OutlineAssets(geometry, material)
  end hullOutline

  def hullOutlineTree(
    root: Object3D[?],
    thickness: Double,
    curvature: PlanetCurvature,
    color: Int = Palette.ink,
    opacity: Double = 1.0
  ): List[OutlineAssets] =
    ThreeInterop.meshes(root)
      .filter(ThreeInterop.hasShaderMaterial)
      .filterNot(mesh => ThreeInterop.hasUserDataFlag(mesh, "noOutline"))
      .filterNot(mesh => ThreeInterop.hasUserDataFlag(mesh, "isOutline"))
      .filterNot(mesh => ThreeInterop.hasUserDataFlag(mesh, "hasOutline"))
      .map(mesh => hullOutline(mesh, thickness, curvature, color, opacity))

  def setResolution(width: Double, height: Double): Unit =
    resolutions.foreach(_.set(width, height))

  def clearResolutionTargets(): Unit = resolutions.clear()

  private def vertexKey(x: Double, y: Double, z: Double): VertexKey =
    VertexKey(
      math.round(x / VertexTolerance),
      math.round(y / VertexTolerance),
      math.round(z / VertexTolerance)
    )

  private def appendVertex(
    x: Double,
    y: Double,
    z: Double,
    positions: mutable.ArrayBuffer[Double]
  ): Int =
    val index = positions.size / 3
    positions += x
    positions += y
    positions += z
    index
end Outline
