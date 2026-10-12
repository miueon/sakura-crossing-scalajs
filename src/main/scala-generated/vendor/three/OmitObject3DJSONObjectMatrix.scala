// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait OmitObject3DJSONObjectMatrix extends js.Object:
  var animations: js.UndefOr[js.Array[String]] = js.native
  var castShadow: js.UndefOr[Boolean] = js.native
  var children: js.UndefOr[js.Array[String]] = js.native
  var frustumCulled: js.UndefOr[Boolean] = js.native
  var layers: Double = js.native
  var material: js.UndefOr[String | js.Array[String]] = js.native
  var matrixAutoUpdate: js.UndefOr[Boolean] = js.native
  var name: js.UndefOr[String] = js.native
  var pivot: js.UndefOr[js.Tuple3[Double, Double, Double]] = js.native
  var receiveShadow: js.UndefOr[Boolean] = js.native
  var renderOrder: js.UndefOr[Double] = js.native
  var static: js.UndefOr[Boolean] = js.native
  var `type`: String = js.native
  var up: js.Tuple3[Double, Double, Double] = js.native
  var userData: js.UndefOr[js.Dictionary[js.Any]] = js.native
  var uuid: String = js.native
  var visible: js.UndefOr[Boolean] = js.native

object OmitObject3DJSONObjectMatrix:
  def apply(
    `type`: String,
    matrixAutoUpdate: js.UndefOr[Boolean] = js.undefined,
    uuid: String,
    name: js.UndefOr[String] = js.undefined,
    castShadow: js.UndefOr[Boolean] = js.undefined,
    receiveShadow: js.UndefOr[Boolean] = js.undefined,
    visible: js.UndefOr[Boolean] = js.undefined,
    frustumCulled: js.UndefOr[Boolean] = js.undefined,
    renderOrder: js.UndefOr[Double] = js.undefined,
    static: js.UndefOr[Boolean] = js.undefined,
    userData: js.UndefOr[js.Dictionary[js.Any]] = js.undefined,
    layers: Double,
    up: js.Tuple3[Double, Double, Double],
    pivot: js.UndefOr[js.Tuple3[Double, Double, Double]] = js.undefined,
    material: js.UndefOr[String | js.Array[String]] = js.undefined,
    children: js.UndefOr[js.Array[String]] = js.undefined,
    animations: js.UndefOr[js.Array[String]] = js.undefined
  ): OmitObject3DJSONObjectMatrix =
    val _res = js.Dynamic.literal("type" -> `type`.asInstanceOf[js.Any])
    matrixAutoUpdate.foreach(v => _res.updateDynamic("matrixAutoUpdate")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("uuid")(uuid.asInstanceOf[js.Any])
    name.foreach(v => _res.updateDynamic("name")(v.asInstanceOf[js.Any]))
    castShadow.foreach(v => _res.updateDynamic("castShadow")(v.asInstanceOf[js.Any]))
    receiveShadow.foreach(v => _res.updateDynamic("receiveShadow")(v.asInstanceOf[js.Any]))
    visible.foreach(v => _res.updateDynamic("visible")(v.asInstanceOf[js.Any]))
    frustumCulled.foreach(v => _res.updateDynamic("frustumCulled")(v.asInstanceOf[js.Any]))
    renderOrder.foreach(v => _res.updateDynamic("renderOrder")(v.asInstanceOf[js.Any]))
    static.foreach(v => _res.updateDynamic("static")(v.asInstanceOf[js.Any]))
    userData.foreach(v => _res.updateDynamic("userData")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("layers")(layers.asInstanceOf[js.Any])
    _res.updateDynamic("up")(up.asInstanceOf[js.Any])
    pivot.foreach(v => _res.updateDynamic("pivot")(v.asInstanceOf[js.Any]))
    material.foreach(v => _res.updateDynamic("material")(v.asInstanceOf[js.Any]))
    children.foreach(v => _res.updateDynamic("children")(v.asInstanceOf[js.Any]))
    animations.foreach(v => _res.updateDynamic("animations")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[OmitObject3DJSONObjectMatrix]
