// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait Intersection[TIntersected <: Object3D[?]] extends js.Object:
  var barycoord: js.UndefOr[Vector3 | Null] = js.native
  var batchId: js.UndefOr[Double] = js.native
  var distance: Double = js.native
  var distanceToRay: js.UndefOr[Double] = js.native
  var face: js.UndefOr[Face | Null] = js.native
  var faceIndex: js.UndefOr[Double | Null] = js.native
  var index: js.UndefOr[Double] = js.native
  var instanceId: js.UndefOr[Double] = js.native
  var normal: js.UndefOr[Vector3] = js.native
  var `object`: TIntersected = js.native
  var point: Vector3 = js.native
  var pointOnLine: js.UndefOr[Vector3] = js.native
  var uv: js.UndefOr[Vector2] = js.native
  var uv1: js.UndefOr[Vector2] = js.native

object Intersection:
  def apply[TIntersected <: Object3D[?]](
    distance: Double,
    distanceToRay: Option[js.UndefOr[Double]] = None,
    point: Vector3,
    index: Option[js.UndefOr[Double]] = None,
    face: Option[js.UndefOr[Face | Null]] = None,
    faceIndex: Option[js.UndefOr[Double | Null]] = None,
    barycoord: js.UndefOr[Vector3 | Null] = js.undefined,
    `object`: TIntersected,
    uv: Option[js.UndefOr[Vector2]] = None,
    uv1: Option[js.UndefOr[Vector2]] = None,
    normal: js.UndefOr[Vector3] = js.undefined,
    instanceId: Option[js.UndefOr[Double]] = None,
    pointOnLine: js.UndefOr[Vector3] = js.undefined,
    batchId: js.UndefOr[Double] = js.undefined
  ): Intersection[TIntersected] =
    val _res = js.Dynamic.literal("distance" -> distance.asInstanceOf[js.Any])
    distanceToRay.foreach(v => _res.updateDynamic("distanceToRay")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("point")(point.asInstanceOf[js.Any])
    index.foreach(v => _res.updateDynamic("index")(v.asInstanceOf[js.Any]))
    face.foreach(v => _res.updateDynamic("face")(v.asInstanceOf[js.Any]))
    faceIndex.foreach(v => _res.updateDynamic("faceIndex")(v.asInstanceOf[js.Any]))
    barycoord.foreach(v => _res.updateDynamic("barycoord")(v.asInstanceOf[js.Any]))
    _res.updateDynamic("object")(`object`.asInstanceOf[js.Any])
    uv.foreach(v => _res.updateDynamic("uv")(v.asInstanceOf[js.Any]))
    uv1.foreach(v => _res.updateDynamic("uv1")(v.asInstanceOf[js.Any]))
    normal.foreach(v => _res.updateDynamic("normal")(v.asInstanceOf[js.Any]))
    instanceId.foreach(v => _res.updateDynamic("instanceId")(v.asInstanceOf[js.Any]))
    pointOnLine.foreach(v => _res.updateDynamic("pointOnLine")(v.asInstanceOf[js.Any]))
    batchId.foreach(v => _res.updateDynamic("batchId")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[Intersection[TIntersected]]
