package sakura

import vendor.three.*

import scala.math
import scala.scalajs.js

/** Three's material map field uses the default image and event-map parameters. */
type MaterialTexture = Texture[js.Any, TextureEventMap]

private[sakura] enum ShaderUniformValue:
  case Scalar(value: Double)
  case Vector(value: Vector2)
  case ColorHex(value: Int)
  case ThreeColor(value: Int)
  case TextureRef(value: Texture[?, ?])

private[sakura] object ThreeInterop:
  type TextureRef         = Texture[?, ?]
  type MaterialMapRef     = MaterialTexture
  type CanvasTextureRef   = CanvasTexture[?]
  type BufferGeometryRef  = BufferGeometry[?, ?]
  type BufferAttributeRef = BufferAttribute[?]
  type MaterialRef        = Material[?]
  type MeshRef            = Mesh[?, ?, Object3DEventMap]
  type InstancedMeshRef   = InstancedMesh[?, ?, ?]
  type Object3DRef        = Object3D[?]
  type SceneObjectRef     = Object3D[Object3DEventMap]
  type GroupRef           = Group[Object3DEventMap]
  type SceneRef           = Scene[Object3DEventMap]

  extension (texture: TextureRef)
    def asMaterialMap: MaterialMapRef = texture.asInstanceOf[MaterialMapRef]

  extension (parent: SceneObjectRef)
    def attach(child: Object3DRef): Unit = parent.add(child.asInstanceOf[SceneObjectRef])

  extension (attribute: BufferAttributeRef)
    def markNeedsUpdate(): Unit = attribute.version += 1.0

  /** Three's `add` signature uses the default event map, while scene children may carry subclass
    * maps; event-map arguments erase at runtime and do not change scene-graph behavior.
    */
  def addChild(parent: Object3D[?], child: Object3D[?]): Unit =
    parent.asInstanceOf[Object3D[Object3DEventMap]]
      .add(child.asInstanceOf[Object3D[Object3DEventMap]])

  def setScreenRenderTarget(renderer: WebGLRenderer): Unit = renderer.setRenderTarget(null)

  /** CanvasTexture is a runtime Texture; its image parameter is erased and only narrower in Scala. */
  def asMaterialTexture(texture: Texture[?, ?]): MaterialTexture =
    texture.asInstanceOf[MaterialTexture]

  def asVector3Like(vector: Vector3): Vector3Like = vector.asInstanceOf[Vector3Like]

  /** `Quaternion.copy` and `multiply` are declared over the structural `QuaternionLike`, which
    * `Quaternion` does not extend: the generated facade keeps the two apart, so composing two
    * real quaternions has to cross the boundary here rather than at every call site.
    */
  def asQuaternionLike(quaternion: Quaternion): QuaternionLike = quaternion.asInstanceOf[QuaternionLike]

  def shaderMaterial(
    vertexShader: String,
    fragmentShader: String,
    uniforms: List[(String, ShaderUniformValue)],
    side: Double = 0.0,
    transparent: Boolean = false,
    opacity: Double = 1.0,
    depthTest: Boolean = false,
    depthWrite: Boolean = false
  ): ShaderMaterial =
    val uniformMap = js.Dynamic.literal()
    uniforms.foreach { case (name, value) => uniformMap.updateDynamic(name)(uniformEntry(value)) }
    val parameters = js.Dynamic.literal(
      vertexShader = vertexShader,
      fragmentShader = fragmentShader,
      uniforms = uniformMap,
      side = side,
      transparent = transparent,
      opacity = opacity,
      depthTest = depthTest,
      depthWrite = depthWrite,
      toneMapped = false
    )
    new ShaderMaterial(parameters.asInstanceOf[ShaderMaterialParameters])
  end shaderMaterial

  def setShaderUniform(material: ShaderMaterial, name: String, value: ShaderUniformValue): Unit =
    material.asInstanceOf[js.Dynamic]
      .selectDynamic("uniforms")
      .selectDynamic(name)
      .updateDynamic("value")(uniformValue(value))

  def meshes(root: Object3D[?]): List[Mesh[?, ?, ?]] =
    val pending = scala.collection.mutable.ArrayBuffer[Object3D[?]](root)
    val found   = List.newBuilder[Mesh[?, ?, ?]]
    while pending.nonEmpty do
      val node     = pending.remove(pending.length - 1)
      val children = node.children
      (0 until children.length).foreach(index => pending += children(index))
      val isMesh = node.asInstanceOf[js.Dynamic].selectDynamic("isMesh")
      if !js.isUndefined(isMesh) && isMesh.asInstanceOf[Boolean] then
        found += node.asInstanceOf[Mesh[?, ?, ?]]
    found.result()

  def asInstancedMesh(mesh: Mesh[?, ?, ?]): Option[InstancedMesh[?, ?, ?]] =
    asInstancedObject(mesh.asInstanceOf[Object3D[?]])

  def asInstancedObject(node: Object3D[?]): Option[InstancedMesh[?, ?, ?]] =
    val isInstanced = node.asInstanceOf[js.Dynamic].selectDynamic("isInstancedMesh")
    if !js.isUndefined(isInstanced) && isInstanced.asInstanceOf[Boolean] then
      Some(node.asInstanceOf[InstancedMesh[?, ?, ?]])
    else None

  def asMesh(node: Object3D[?]): Option[Mesh[?, ?, ?]] =
    if isMesh(node) then Some(node.asInstanceOf[Mesh[?, ?, ?]]) else None

  private type PreciseMesh =
    Mesh[BufferGeometry[?, ?], Material[?] | js.Array[Material[MaterialEventMap]], Object3DEventMap]

  def meshGeometry(mesh: Mesh[?, ?, ?]): BufferGeometry[?, ?] =
    mesh.asInstanceOf[PreciseMesh].geometry

  def setMeshGeometry(mesh: Mesh[?, ?, ?], geometry: BufferGeometry[?, ?]): Unit =
    mesh.asInstanceOf[PreciseMesh].geometry = geometry

  def positionAttribute(geometry: BufferGeometry[?, ?]): BufferAttribute[BufferAttributeEventMap] =
    geometry.getAttribute("position").asInstanceOf[BufferAttribute[BufferAttributeEventMap]]

  def hasShaderMaterial(mesh: Mesh[?, ?, ?]): Boolean =
    val material = mesh.asInstanceOf[js.Dynamic].selectDynamic("material")
    val flag     = material.selectDynamic("isShaderMaterial")
    !js.isUndefined(flag) && flag.asInstanceOf[Boolean]

  def hasUserDataFlag(node: Object3D[?], name: String): Boolean =
    val value = node.userData.asInstanceOf[js.Dynamic].selectDynamic(name)
    !js.isUndefined(value) && value.asInstanceOf[Boolean]

  def setUserDataFlag(node: Object3D[?], name: String): Unit =
    node.userData.asInstanceOf[js.Dynamic].updateDynamic(name)(true)

  /** Reads a numeric `userData` entry a builder published (jump heights, fence tops). */
  def userDataNumber(node: Object3D[?], name: String): Double =
    val value = node.userData.asInstanceOf[js.Dynamic].selectDynamic(name)
    if js.isUndefined(value) then
      throw new IllegalStateException(s"sakura-crossing: no numeric userData '$name' on this node")
    value.asInstanceOf[Double]

  /** Reads an `Object3D` a builder published for animation (the cat's head, tail and body). */
  def userDataObject(node: Object3D[?], name: String): Option[Object3D[?]] =
    val value = node.userData.asInstanceOf[js.Dynamic].selectDynamic(name)
    Option.when(!js.isUndefined(value) && value != null)(value.asInstanceOf[Object3D[Object3DEventMap]])

  /** Three's setter-only `needsUpdate` is absent from the generated facade; incrementing the
    * public version counter triggers the same renderer upload path.
    */
  def markForUpload(texture: Texture[?, ?]): Unit = texture.version += 1.0

  def markForUpload(attribute: BufferAttribute[?]): Unit = attribute.version += 1.0

  def markForUpload(attribute: InstancedBufferAttribute): Unit = attribute.version += 1.0

  /** A scoped handle over the shader object three hands to `onBeforeCompile`; keeps the JS object
    * behind this interop boundary so the toon patch stays pure string surgery.
    */
  final class ShaderCompileScope private[ThreeInterop] (private val shader: js.Dynamic):
    def setUniformColor(name: String, color: Color): Unit =
      val uniforms = shader.selectDynamic("uniforms").asInstanceOf[js.Dictionary[js.Any]]
      uniforms(name) = js.Dynamic.literal(value = color)

    def replaceFragmentInclude(includeName: String, replacement: String): Unit =
      val include     = s"#include <$includeName>"
      val source      = shader.fragmentShader.toString
      val occurrences = source.split(include, -1).length - 1
      if occurrences != 1 then
        throw new IllegalStateException(
          s"sakura-crossing: expected exactly one $include in the fragment shader, found $occurrences"
        )
      shader.updateDynamic("fragmentShader")(source.replace(include, replacement))
  end ShaderCompileScope

  final case class PatchedShader(uniformNames: List[String], fragment: String)

  /** Three declares `onBeforeCompile` and `customProgramCacheKey` as methods, so assigning the
    * upstream's per-material closures has to go through the dynamic boundary.
    */
  def setOnBeforeCompile(material: Material[?], patch: ShaderCompileScope => Unit): Unit =
    material
      .asInstanceOf[js.Dynamic]
      .updateDynamic("onBeforeCompile")((shader: js.Dynamic) => patch(new ShaderCompileScope(shader)): js.Any)

  def runOnBeforeCompile(material: Material[?], fragmentShader: String): PatchedShader =
    val uniforms  = js.Dynamic.literal()
    val shader    = js.Dynamic.literal(uniforms = uniforms, fragmentShader = fragmentShader)
    val onCompile = material.asInstanceOf[js.Dynamic].selectDynamic("onBeforeCompile")
    if js.isUndefined(onCompile) then
      throw new IllegalStateException("sakura-crossing: material has no onBeforeCompile patch")
    onCompile.asInstanceOf[js.Function1[js.Dynamic, Unit]](shader)
    val names = uniforms.asInstanceOf[js.Dictionary[js.Any]].keys.toList
    PatchedShader(names, shader.fragmentShader.toString)

  def setCustomProgramCacheKey(material: Material[?], key: () => String): Unit =
    material
      .asInstanceOf[js.Dynamic]
      .updateDynamic("customProgramCacheKey")((() => key()): js.Function0[String])

  def customProgramCacheKey(material: Material[?]): String =
    material
      .asInstanceOf[js.Dynamic]
      .selectDynamic("customProgramCacheKey")
      .asInstanceOf[js.Function0[String]]()

  private def userDataOf(node: Object3D[?] | Material[?]): js.Dynamic =
    node match
      case material: Material[?] => material.userData.asInstanceOf[js.Dynamic]
      case other                 => other.asInstanceOf[js.Dynamic].selectDynamic("userData")

  def setUserDataColorHolder(node: Object3D[?] | Material[?], key: String, hex: Int): Unit =
    userDataOf(node).updateDynamic(key)(js.Dynamic.literal(value = new Color(hex)))

  def userDataColorHex(node: Object3D[?] | Material[?], key: String): Double =
    userDataOf(node)
      .selectDynamic(key)
      .selectDynamic("value")
      .asInstanceOf[Color]
      .getHex()

  def isMeshToonMaterial(mesh: Mesh[?, ?, ?]): Boolean =
    val flag = mesh.asInstanceOf[js.Dynamic].selectDynamic("material").selectDynamic("isMeshToonMaterial")
    !js.isUndefined(flag) && flag.asInstanceOf[Boolean]

  def gradientMapWidth(material: MeshToonMaterial): Double =
    val map = material.gradientMap
    if map == null then -1.0
    else map.asInstanceOf[js.Dynamic].selectDynamic("image").selectDynamic("width").asInstanceOf[Double]

  def matrixElements(matrix: Matrix4): Array[Double] =
    val elements = matrix.elements.asInstanceOf[js.Array[Double]]
    Array.tabulate(16)(elements(_))

  def positionCount(geometry: BufferGeometry[?, ?]): Double =
    geometry.getAttribute("position").asInstanceOf[BufferAttribute[BufferAttributeEventMap]].count

  def geometryPosition(geometry: BufferGeometry[?, ?], index: Int): (Double, Double, Double) =
    val position = geometry.getAttribute("position").asInstanceOf[BufferAttribute[BufferAttributeEventMap]]
    (position.getX(index.toDouble), position.getY(index.toDouble), position.getZ(index.toDouble))

  final case class RadiusRange(min: Double, max: Double)

  def geometryRadii(
    geometry: BufferGeometry[?, ?],
    centerX: Double,
    centerY: Double,
    centerZ: Double
  ): RadiusRange =
    val position = geometry.getAttribute("position").asInstanceOf[BufferAttribute[BufferAttributeEventMap]]
    var min      = Double.PositiveInfinity
    var max      = Double.NegativeInfinity
    (0 until position.count.toInt).foreach { index =>
      val dx     = position.getX(index.toDouble) - centerX
      val dy     = position.getY(index.toDouble) - centerY
      val dz     = position.getZ(index.toDouble) - centerZ
      val radius = math.sqrt(dx * dx + dy * dy + dz * dz)
      min = math.min(min, radius)
      max = math.max(max, radius)
    }
    RadiusRange(min, max)

  def groups(geometry: BufferGeometry[?, ?]): Vector[(Double, Double, Option[Double])] =
    geometry.groups.toVector.map(group =>
      (group.start, group.count, group.materialIndex.toOption.map(_.toDouble))
    )

  def hasAttribute(geometry: BufferGeometry[?, ?], name: String): Boolean =
    !js.isUndefined(geometry.asInstanceOf[js.Dynamic].selectDynamic("attributes").selectDynamic(name))

  def attributeNames(geometry: BufferGeometry[?, ?]): List[String] =
    js.Object
      .keys(geometry.asInstanceOf[js.Dynamic].selectDynamic("attributes").asInstanceOf[js.Object])
      .toList

  def attributeItemSize(geometry: BufferGeometry[?, ?], name: String): Double =
    geometry
      .asInstanceOf[js.Dynamic]
      .selectDynamic("attributes")
      .selectDynamic(name)
      .selectDynamic("itemSize")
      .asInstanceOf[Double]

  def attributeDoubles(geometry: BufferGeometry[?, ?], name: String): Array[Double] =
    val attribute =
      geometry.asInstanceOf[js.Dynamic].selectDynamic("attributes").selectDynamic(name)
    val array = attribute.selectDynamic("array")
    val count = attribute.selectDynamic("count").asInstanceOf[Double].toInt
    val size  = attribute.selectDynamic("itemSize").asInstanceOf[Double].toInt
    val typed = array.asInstanceOf[js.Array[Double]]
    Array.tabulate(count * size)(typed(_))

  def setFloatAttribute(
    geometry: BufferGeometry[?, ?],
    name: String,
    values: Array[Double],
    itemSize: Int
  ): Unit =
    val array = new js.typedarray.Float32Array(values.length)
    values.zipWithIndex.foreach { case (value, index) => array(index) = value.toFloat }
    geometry.setAttribute(name, new BufferAttribute(array, itemSize.toDouble))

  def setIndexAttribute(geometry: BufferGeometry[?, ?], values: Array[Double]): Unit =
    geometry.setIndex(js.Array(values*))

  def writeAttributeDoubles(attribute: BufferAttribute[?], values: Array[Double], offset: Int): Unit =
    val array = attribute.array.asInstanceOf[js.Array[Double]]
    values.zipWithIndex.foreach { case (value, index) => array(offset + index) = value }

  def readAttributeDoubles(attribute: BufferAttribute[?], count: Int): Array[Double] =
    val array = attribute.array.asInstanceOf[js.Array[Double]]
    Array.tabulate(count)(array(_))

  def materialArray(materials: Material[?]*): js.Array[Material[MaterialEventMap]] =
    js.Array(materials.map(_.asInstanceOf[Material[MaterialEventMap]])*)

  def children(node: Object3D[?]): Vector[Object3D[?]] =
    node.children.toVector

  def firstChild(node: Object3D[?]): Option[Object3D[?]] =
    if node.children.length == 0 then None else Some(node.children(0))

  def isMesh(node: Object3D[?]): Boolean =
    val flag = node.asInstanceOf[js.Dynamic].selectDynamic("isMesh")
    !js.isUndefined(flag) && flag.asInstanceOf[Boolean]

  def isLine(node: Object3D[?]): Boolean =
    val flag = node.asInstanceOf[js.Dynamic].selectDynamic("isLine")
    !js.isUndefined(flag) && flag.asInstanceOf[Boolean]

  def transparentMaterial(node: Object3D[?]): Boolean =
    val material = node.asInstanceOf[js.Dynamic].selectDynamic("material")
    val materialIsArray =
      js.Dynamic.global.Array.isArray(material).asInstanceOf[Boolean]
    if materialIsArray then false
    else
      val transparent = material.asInstanceOf[js.Dynamic].selectDynamic("transparent")
      !js.isUndefined(transparent) && transparent.asInstanceOf[Boolean]

  def asShaderMaterial(mesh: Mesh[?, ?, ?]): ShaderMaterial =
    mesh.material.asInstanceOf[ShaderMaterial]

  def uniformScalar(material: ShaderMaterial, name: String): Double =
    material.uniforms(name).value.asInstanceOf[Double]

  def uniformColorHex(material: ShaderMaterial, name: String): Double =
    material.uniforms(name).value.asInstanceOf[Color].getHex()

  def uniformVector2(material: ShaderMaterial, name: String): (Double, Double) =
    val vector = material.uniforms(name).value.asInstanceOf[Vector2]
    (vector.x, vector.y)

  def updateVector2Uniform(material: ShaderMaterial, name: String, x: Double, y: Double): Unit =
    material
      .uniforms(name)
      .value
      .asInstanceOf[Vector2]
      .set(x, y)

  def dataTextureBytes(texture: DataTexture): Array[Int] =
    val data = texture.image.data
    if data == null then Array.empty
    else
      val bytes = data.asInstanceOf[js.Array[Double]]
      Array.tabulate(bytes.length)(index => bytes(index).toInt)

  def domeRadius(mesh: Mesh[?, ?, ?]): Double =
    mesh.geometry
      .asInstanceOf[js.Dynamic]
      .selectDynamic("parameters")
      .selectDynamic("radius")
      .asInstanceOf[Double]

  def meshMaterialOpacity(node: Object3D[?]): Double =
    node.asInstanceOf[js.Dynamic].selectDynamic("material").selectDynamic("opacity").asInstanceOf[Double]

  def meshMapWrapS(node: Object3D[?]): Double =
    node
      .asInstanceOf[js.Dynamic]
      .selectDynamic("material")
      .selectDynamic("map")
      .selectDynamic("wrapS")
      .asInstanceOf[Double]

  def matrixFromAttribute(matrix: Matrix4, attribute: BufferAttribute[?], offset: Int): Unit =
    val array = attribute.array.asInstanceOf[js.Array[Double]]
    matrix.fromArray(js.Array((0 until 16).map(index => array(offset + index))*))

  def matrixToAttribute(matrix: Matrix4, attribute: BufferAttribute[?], offset: Int): Unit =
    val elements = matrixElements(matrix)
    writeAttributeDoubles(attribute, elements, offset)

  final case class InstanceTransform(position: (Double, Double, Double), scale: (Double, Double, Double))

  def instanceTransform(instanced: InstancedMesh[?, ?, ?], index: Int): InstanceTransform =
    val matrix = new Matrix4()
    matrixFromAttribute(matrix, instanced.instanceMatrix, index * 16)
    val position   = new Vector3()
    val quaternion = new Quaternion()
    val scale      = new Vector3()
    matrix.decompose(position, quaternion, scale)
    InstanceTransform((position.x, position.y, position.z), (scale.x, scale.y, scale.z))

  def shaderMaterialRaw(
    vertexShader: String,
    fragmentShader: String,
    uniforms: List[(String, ShaderUniformValue)],
    side: Double,
    transparent: Boolean,
    depthWrite: Boolean,
    fog: Boolean
  ): ShaderMaterial =
    val uniformMap = js.Dynamic.literal()
    uniforms.foreach { case (name, value) => uniformMap.updateDynamic(name)(uniformEntry(value)) }
    val parameters = js.Dynamic.literal(
      vertexShader = vertexShader,
      fragmentShader = fragmentShader,
      uniforms = uniformMap,
      side = side,
      transparent = transparent,
      depthWrite = depthWrite,
      fog = fog
    )
    new ShaderMaterial(parameters.asInstanceOf[ShaderMaterialParameters])
  end shaderMaterialRaw

  def uniformEntry(value: ShaderUniformValue): js.Any =
    js.Dynamic.literal(value = uniformValue(value))

  private def uniformValue(value: ShaderUniformValue): js.Any = value match
    case ShaderUniformValue.Scalar(scalar) => scalar.asInstanceOf[js.Any]
    case ShaderUniformValue.Vector(vector) => vector.asInstanceOf[js.Any]
    case ShaderUniformValue.ColorHex(hex) =>
      val (red, green, blue) = Palette.rgb(hex)
      new Vector3(red, green, blue).asInstanceOf[js.Any]
    case ShaderUniformValue.ThreeColor(hex)     => new Color(hex).asInstanceOf[js.Any]
    case ShaderUniformValue.TextureRef(texture) => texture.asInstanceOf[js.Any]
end ThreeInterop
