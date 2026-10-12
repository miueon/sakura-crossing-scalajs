// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait SceneJSONObject extends Object3DJSONObject:
  var backgroundBlurriness: js.UndefOr[Double] = js.native
  var backgroundIntensity: js.UndefOr[Double] = js.native
  var backgroundRotation: js.Array[Double | js.UndefOr["XYZ" | "XZY" | "YXZ" | "YZX" | "ZXY" | "ZYX"]] = js.native
  var environmentIntensity: js.UndefOr[Double] = js.native
  var environmentRotation: js.Array[Double | js.UndefOr["XYZ" | "XZY" | "YXZ" | "YZX" | "ZXY" | "ZYX"]] = js.native
  var fog: js.UndefOr[FogExp2JSON | FogJSON] = js.native

object SceneJSONObject:
  def apply(
    fog: js.UndefOr[FogExp2JSON | FogJSON] = js.undefined,
    backgroundBlurriness: js.UndefOr[Double] = js.undefined,
    backgroundIntensity: js.UndefOr[Double] = js.undefined,
    backgroundRotation: js.Array[Double | js.UndefOr["XYZ" | "XZY" | "YXZ" | "YZX" | "ZXY" | "ZYX"]],
    environmentIntensity: js.UndefOr[Double] = js.undefined,
    environmentRotation: js.Array[Double | js.UndefOr["XYZ" | "XZY" | "YXZ" | "YZX" | "ZXY" | "ZYX"]],
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
  ): SceneJSONObject =
    val _res = js.Dynamic.literal()
    fog.foreach(v => _res.updateDynamic("fog")(v.asInstanceOf[js.Any]))
    backgroundBlurriness.foreach(v => _res.updateDynamic("backgroundBlurriness")(v.asInstanceOf[js.Any]))
    backgroundIntensity.foreach(v => _res.updateDynamic("backgroundIntensity")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("backgroundRotation")(backgroundRotation.asInstanceOf[js.Any])
    environmentIntensity.foreach(v => _res.updateDynamic("environmentIntensity")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("environmentRotation")(environmentRotation.asInstanceOf[js.Any])
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
    _res.asInstanceOf[SceneJSONObject]
