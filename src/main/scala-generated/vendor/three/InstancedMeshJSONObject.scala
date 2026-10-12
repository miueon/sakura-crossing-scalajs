// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait InstancedMeshJSONObject extends MeshJSONObject:
  var count: Double = js.native
  var instanceColor: js.UndefOr[BufferAttributeJSON] = js.native
  var instanceMatrix: BufferAttributeJSON = js.native

object InstancedMeshJSONObject:
  def apply(
    count: Double,
    instanceMatrix: BufferAttributeJSON,
    instanceColor: js.UndefOr[BufferAttributeJSON] = js.undefined,
    geometry: String,
    uuid: String,
    `type`: String,
    name: js.UndefOr[String] = js.undefined,
    castShadow: js.UndefOr[Boolean] = js.undefined,
    receiveShadow: js.UndefOr[Boolean] = js.undefined,
    visible: js.UndefOr[Boolean] = js.undefined,
    frustumCulled: js.UndefOr[Boolean] = js.undefined,
    renderOrder: js.UndefOr[Double] = js.undefined,
    static: js.UndefOr[Boolean] = js.undefined,
    userData: js.UndefOr[js.Dictionary[js.Any]] = js.undefined,
    layers: Double,
    matrix: js.Tuple16[Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double],
    up: js.Tuple3[Double, Double, Double],
    pivot: js.UndefOr[js.Tuple3[Double, Double, Double]] = js.undefined,
    matrixAutoUpdate: js.UndefOr[Boolean] = js.undefined,
    material: js.UndefOr[String | js.Array[String]] = js.undefined,
    children: js.UndefOr[js.Array[String]] = js.undefined,
    animations: js.UndefOr[js.Array[String]] = js.undefined
  ): InstancedMeshJSONObject =
    val _res = js.Dynamic.literal(
      "count" -> count.asInstanceOf[js.Any],
      "instanceMatrix" -> instanceMatrix.asInstanceOf[js.Any]
    )
    instanceColor.foreach(v => _res.updateDynamic("instanceColor")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("geometry")(geometry.asInstanceOf[js.Any])
    _res.updateDynamic("uuid")(uuid.asInstanceOf[js.Any])
    _res.updateDynamic("type")(`type`.asInstanceOf[js.Any])
    name.foreach(v => _res.updateDynamic("name")(v.asInstanceOf[js.Any]))
    castShadow.foreach(v => _res.updateDynamic("castShadow")(v.asInstanceOf[js.Any]))
    receiveShadow.foreach(v => _res.updateDynamic("receiveShadow")(v.asInstanceOf[js.Any]))
    visible.foreach(v => _res.updateDynamic("visible")(v.asInstanceOf[js.Any]))
    frustumCulled.foreach(v => _res.updateDynamic("frustumCulled")(v.asInstanceOf[js.Any]))
    renderOrder.foreach(v => _res.updateDynamic("renderOrder")(v.asInstanceOf[js.Any]))
    static.foreach(v => _res.updateDynamic("static")(v.asInstanceOf[js.Any]))
    userData.foreach(v => _res.updateDynamic("userData")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("layers")(layers.asInstanceOf[js.Any])
    _res.updateDynamic("matrix")(matrix.asInstanceOf[js.Any])
    _res.updateDynamic("up")(up.asInstanceOf[js.Any])
    pivot.foreach(v => _res.updateDynamic("pivot")(v.asInstanceOf[js.Any]))
    matrixAutoUpdate.foreach(v => _res.updateDynamic("matrixAutoUpdate")(v.asInstanceOf[js.Any]))
    material.foreach(v => _res.updateDynamic("material")(v.asInstanceOf[js.Any]))
    children.foreach(v => _res.updateDynamic("children")(v.asInstanceOf[js.Any]))
    animations.foreach(v => _res.updateDynamic("animations")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[InstancedMeshJSONObject]
