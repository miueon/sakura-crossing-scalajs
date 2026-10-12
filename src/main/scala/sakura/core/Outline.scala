package sakura.core

import sakura.{ShaderUniformValue, ThreeInterop}
import sakura.world.PlanetBasis
import ultraviolet.syntax.*
import vendor.three.*

import scala.annotation.nowarn
import scala.collection.mutable
import scala.scalajs.js

/** Inverted-hull outlines for hero props, ported 1:1 from upstream `src/core/outline.js`
  * for geometry that has already been baked onto the planet.
  *
  * The screen-space ink pass draws every line in the scene, but a few objects -- the train, the
  * crossing gates, the vending machines -- want a heavier, deliberately drawn contour. These get
  * a back-faced shell pushed outward along smoothed normals, offset in clip space so the line
  * keeps a constant pixel width no matter how far away it is.
  */
object Outline:

  final private case class HullVertexEnv(
    uThickness: Float,
    uResolution: vec2,
    position: vec3,
    normal: vec3,
    modelViewMatrix: mat4,
    projectionMatrix: mat4,
    normalMatrix: mat3,
    var gl_Position: vec4
  )

  final private case class InstancedHullVertexEnv(
    uThickness: Float,
    uResolution: vec2,
    position: vec3,
    normal: vec3,
    modelViewMatrix: mat4,
    instanceMatrix: mat4,
    projectionMatrix: mat4,
    normalMatrix: mat3,
    var gl_Position: vec4
  )

  final private case class HullFragmentEnv(
    uColor: vec3,
    uOpacity: Float,
    var gl_FragColor: vec4
  )

  /* Ultraviolet emits a shader body and keeps its declarations in the shader's metadata, so a
   * material that is handed `.code` directly has to declare its own uniforms. three.js's prefix
   * declares only the built-ins this shader reads (`modelViewMatrix`, `projectionMatrix`,
   * `normalMatrix`) and, for an InstancedMesh, `instanceMatrix`. The declarations live with the
   * source so every material that uses it inherits them.
   */
  private val hullVertexDeclarations: String =
    "uniform float uThickness;\nuniform vec2 uResolution;\n"

  private val hullFragmentDeclarations: String =
    "uniform vec3 uColor;\nuniform float uOpacity;\n"

  @nowarn("msg=unused")
  private val hullVertexGlsl: String =
    hullVertexDeclarations + Shader[HullVertexEnv] { env =>
      def main(): Unit =
        val view: vec4       = env.modelViewMatrix * vec4(env.position, 1.0f)
        val viewNormal: vec3 = normalize(env.normalMatrix * env.normal)
        val clip: vec4       = env.projectionMatrix * view
        val clipNormal: vec3 = normalize((env.projectionMatrix * vec4(viewNormal, 0.0f)).xyz)
        val aspect: vec2     = vec2(env.uResolution.y / env.uResolution.x, 1.0f)
        val weight: Float    = env.uThickness * clip.w * 0.5f
        val offset: vec2     = clipNormal.xy * aspect * weight
        env.gl_Position = vec4(clip.x + offset.x, clip.y + offset.y, clip.z, clip.w)
    }.toGLSL100.code

  @nowarn("msg=unused")
  private val instancedHullVertexGlsl: String =
    hullVertexDeclarations + Shader[InstancedHullVertexEnv] { env =>
      def main(): Unit =
        val placed: vec4     = env.instanceMatrix * vec4(env.position, 1.0f)
        val view: vec4       = env.modelViewMatrix * placed
        val bentNormal: vec3 = (env.instanceMatrix * vec4(env.normal, 0.0f)).xyz
        val viewNormal: vec3 = normalize(env.normalMatrix * bentNormal)
        val clip: vec4       = env.projectionMatrix * view
        val clipNormal: vec3 = normalize((env.projectionMatrix * vec4(viewNormal, 0.0f)).xyz)
        val aspect: vec2     = vec2(env.uResolution.y / env.uResolution.x, 1.0f)
        val weight: Float    = env.uThickness * clip.w * 0.5f
        val offset: vec2     = clipNormal.xy * aspect * weight
        env.gl_Position = vec4(clip.x + offset.x, clip.y + offset.y, clip.z, clip.w)
    }.toGLSL100.code

  @nowarn("msg=unused")
  private val hullFragmentGlsl: String =
    hullFragmentDeclarations + Shader[HullFragmentEnv] { env =>
      def main(): Unit = env.gl_FragColor = vec4(env.uColor, env.uOpacity)
    }.toGLSL100.code

  private val resolution = new Vector2(1920.0, 1080.0)
  private val materials  = mutable.ListBuffer.empty[ShaderMaterial]

  def setOutlineResolution(w: Double, h: Double): Unit =
    resolution.set(w, h)
    materials.foreach(material => ThreeInterop.updateVector2Uniform(material, "uResolution", w, h))

  private val VertexTolerance = 1e-4

  /** The upstream shells share smoothed normals across coincident vertices. The merge joins
    * vertices whose every attribute agrees within 1e-4 (the tolerance mergeVertices uses),
    * reindexes, smooths the normals, then keeps only positions and normals -- an outline hull
    * reads no UVs.
    */
  def smoothedGeometry(geometry: BufferGeometry[?, ?]): BufferGeometry[?, ?] =
    val cloned = geometry.clone()
    val source = if cloned.getIndex() != null then cloned.toNonIndexed() else cloned
    val names  = ThreeInterop.attributeNames(source)
    val rows   = names.map(name => (name, ThreeInterop.attributeDoubles(source, name))).toMap
    val sizes  = names.map(name => (name, ThreeInterop.attributeItemSize(source, name).toInt)).toMap
    val count  = ThreeInterop.positionCount(source).toInt

    def keyOf(vertex: Int): String =
      val parts = names.flatMap { name =>
        val size = sizes(name)
        val row  = rows(name)
        (0 until size).map(offset =>
          math.round(row(vertex * size + offset) / VertexTolerance).toLong
        )
      }
      parts.mkString(",")

    val merged = names.map(name => (name, mutable.ArrayBuilder.make[Double])).toMap
    val remap  = new Array[Int](count)
    val seen   = mutable.HashMap.empty[String, Int]
    (0 until count).foreach { vertex =>
      val key = keyOf(vertex)
      seen.get(key) match
        case Some(existing) => remap(vertex) = existing
        case None =>
          names.foreach { name =>
            val size = sizes(name)
            val row  = rows(name)
            val out  = merged(name)
            (0 until size).foreach(offset => out += row(vertex * size + offset))
          }
          seen.update(key, seen.size)
          remap(vertex) = seen.size - 1
    }

    /* The merged attributes are written back onto the clone rather than a fresh
     * BufferGeometry: upstream's `mergeVertices` returns the geometry it was given,
     * so a smoothed box stays a BoxGeometry with its material groups intact. */
    names.foreach { name =>
      ThreeInterop.setFloatAttribute(cloned, name, merged(name).result(), sizes(name))
    }
    val triangles = count / 3
    val indices   = mutable.ArrayBuilder.make[Double]
    (0 until triangles).foreach { triangle =>
      (0 until 3).foreach(corner => indices += remap(triangle * 3 + corner).toDouble)
    }
    ThreeInterop.setIndexAttribute(cloned, indices.result())
    cloned.computeVertexNormals()
    names.filterNot(name => name == "position" || name == "normal").foreach(cloned.deleteAttribute(_))
    cloned
  end smoothedGeometry

  final case class OutlineOptions(
    thickness: Double = 0.0038,
    color: Int = Pal.ink,
    opacity: Double = 1.0
  )

  /** Attach an ink shell to `mesh`. Thickness is in normalised device units (~0.004 reads as a
    * 2px line at 1080p).
    */
  def hullOutline(
    mesh: Mesh[?, ?, ?],
    opts: OutlineOptions = OutlineOptions()
  ): Option[Mesh[?, ?, ?]] =
    val instanced = ThreeInterop.asInstancedMesh(mesh)
    val material = ThreeInterop.shaderMaterialRaw(
      vertexShader = if instanced.nonEmpty then instancedHullVertexGlsl else hullVertexGlsl,
      fragmentShader = hullFragmentGlsl,
      uniforms = List(
        "uThickness"  -> ShaderUniformValue.Scalar(opts.thickness),
        "uColor"      -> ShaderUniformValue.ThreeColor(opts.color),
        "uOpacity"    -> ShaderUniformValue.Scalar(opts.opacity),
        "uResolution" -> ShaderUniformValue.Vector(resolution.clone())
      ),
      side = 1.0,
      transparent = opts.opacity < 1.0,
      depthWrite = true,
      fog = false
    )
    materials += material

    val geometry = smoothedGeometry(mesh.geometry)
    val shell: Mesh[?, ?, ?] = instanced match
      case Some(source) =>
        val outlined = new InstancedMesh(geometry, material, source.count)
        outlined.instanceMatrix = source.instanceMatrix
        outlined.count = source.count
        outlined
      case None =>
        new Mesh(geometry, material)
    shell.castShadow = false
    shell.receiveShadow = false
    shell.renderOrder = mesh.renderOrder - 1.0
    shell.frustumCulled = mesh.frustumCulled
    ThreeInterop.addChild(mesh, shell)
    Some(shell)
  end hullOutline

  /** Outline every mesh in a subtree whose name is not marked `noOutline`. */
  def hullOutlineTree(root: Object3D[?], opts: OutlineOptions = OutlineOptions()): root.type =
    val targets = mutable.ListBuffer.empty[Mesh[?, ?, ?]]
    root.traverse((node: Object3D[Object3DEventMap]) =>
      if ThreeInterop.isMesh(node) &&
        !ThreeInterop.hasUserDataFlag(node, "noOutline") &&
        !ThreeInterop.hasUserDataFlag(node, "isOutline")
      then ThreeInterop.asMesh(node).foreach(targets += _)
    )
    targets.foreach { target =>
      hullOutline(target, opts).foreach(shell => ThreeInterop.setUserDataFlag(shell, "isOutline"))
    }
    root

  /** Move a light so its direction stays fixed relative to the local surface (from upstream
    * `src/main.js`): lighting stays pinned to the player's surface frame rather than world space.
    */
  def seatLight(
    light: DirectionalLight,
    local: (Double, Double, Double),
    basis: PlanetBasis,
    origin: Vector3
  ): Unit =
    val offset = new Vector3(0.0, 0.0, 0.0)
      .addScaledVector(ThreeInterop.asVector3Like(basis.east), local._1)
      .addScaledVector(ThreeInterop.asVector3Like(basis.up), local._2)
      .addScaledVector(ThreeInterop.asVector3Like(basis.north), local._3)
    light.target.position.copy(ThreeInterop.asVector3Like(origin))
    light.position.copy(ThreeInterop.asVector3Like(origin)).add(ThreeInterop.asVector3Like(offset))
end Outline
