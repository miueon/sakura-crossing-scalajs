package sakura.foliage

import sakura.ThreeInterop
import vendor.three.*

/** A planned instance transform on the flat plane: translation, an axis-angle rotation and
  * scale. Pure data, so grove and fence layouts are testable without three.js; the instanced
  * materials bend the rendered vertices onto the planet at draw time. The rotation is stored as
  * axis-angle because that is the form the vendor facade can write into a Quaternion.
  */
final case class PlannedTransform(
  position: (Double, Double, Double),
  axis: (Double, Double, Double),
  angle: Double,
  scale: (Double, Double, Double)
)

/** What one instanced builder hands to the scene: roots to mount and the GPU resources the host
  * must dispose on unmount. Batches combine with `+` so the composition site mounts a whole
  * foliage pass without per-builder bookkeeping. Canvas textures stay outside: the host's
  * TextureCache owns every synthesized surface along one disposal path.
  */
final case class SceneBatch(
  roots: List[Object3D[?]],
  geometries: List[BufferGeometry[?, ?]],
  materials: List[Material[?]]
):

  def +(other: SceneBatch): SceneBatch =
    SceneBatch(
      roots = roots ++ other.roots,
      geometries = geometries ++ other.geometries,
      materials = materials ++ other.materials
    )

object SceneBatch:

  val empty: SceneBatch = SceneBatch(Nil, Nil, Nil)

  /** Allocates and fills an InstancedMesh from planned transforms; build-time only. */
  def instancedMesh(
    geometry: BufferGeometry[?, ?],
    material: Material[?],
    transforms: Seq[PlannedTransform]
  ): InstancedMesh[?, ?, ?] =
    val mesh     = new InstancedMesh(geometry, material, transforms.size.toDouble)
    val position = new Vector3()
    val axis     = new Vector3()
    val rotation = new Quaternion()
    val scale    = new Vector3()
    val matrix   = new Matrix4()
    transforms.zipWithIndex.foreach { case (transform, index) =>
      val (px, py, pz) = transform.position
      val (ax, ay, az) = transform.axis
      val (sx, sy, sz) = transform.scale
      position.set(px, py, pz)
      axis.set(ax, ay, az)
      rotation.setFromAxisAngle(ThreeInterop.asVector3Like(axis), transform.angle)
      scale.set(sx, sy, sz)
      matrix.compose(position, rotation, scale)
      mesh.setMatrixAt(index, matrix)
    }
    ThreeInterop.markForUpload(mesh.instanceMatrix)
    mesh
  end instancedMesh
end SceneBatch
