// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait ExtrudeGeometryOptions extends js.Object:
  var bevelEnabled: js.UndefOr[Boolean] = js.native
  var bevelOffset: js.UndefOr[Double] = js.native
  var bevelSegments: js.UndefOr[Double] = js.native
  var bevelSize: js.UndefOr[Double] = js.native
  var bevelThickness: js.UndefOr[Double] = js.native
  var curveSegments: js.UndefOr[Double] = js.native
  var depth: js.UndefOr[Double] = js.native
  var extrudePath: js.UndefOr[Curve[Vector3]] = js.native
  var steps: js.UndefOr[Double] = js.native
  var UVGenerator: js.UndefOr[UVGenerator] = js.native

object ExtrudeGeometryOptions:
  def apply(
    curveSegments: Option[js.UndefOr[Double]] = None,
    steps: Option[js.UndefOr[Double]] = None,
    depth: Option[js.UndefOr[Double]] = None,
    bevelEnabled: Option[js.UndefOr[Boolean]] = None,
    bevelThickness: Option[js.UndefOr[Double]] = None,
    bevelSize: Option[js.UndefOr[Double]] = None,
    bevelOffset: Option[js.UndefOr[Double]] = None,
    bevelSegments: Option[js.UndefOr[Double]] = None,
    extrudePath: Option[js.UndefOr[Curve[Vector3]]] = None,
    UVGenerator: Option[js.UndefOr[UVGenerator]] = None
  ): ExtrudeGeometryOptions =
    val _res = js.Dynamic.literal()
    curveSegments.foreach(v => _res.updateDynamic("curveSegments")(v.asInstanceOf[js.Any]))
    steps.foreach(v => _res.updateDynamic("steps")(v.asInstanceOf[js.Any]))
    depth.foreach(v => _res.updateDynamic("depth")(v.asInstanceOf[js.Any]))
    bevelEnabled.foreach(v => _res.updateDynamic("bevelEnabled")(v.asInstanceOf[js.Any]))
    bevelThickness.foreach(v => _res.updateDynamic("bevelThickness")(v.asInstanceOf[js.Any]))
    bevelSize.foreach(v => _res.updateDynamic("bevelSize")(v.asInstanceOf[js.Any]))
    bevelOffset.foreach(v => _res.updateDynamic("bevelOffset")(v.asInstanceOf[js.Any]))
    bevelSegments.foreach(v => _res.updateDynamic("bevelSegments")(v.asInstanceOf[js.Any]))
    extrudePath.foreach(v => _res.updateDynamic("extrudePath")(v.asInstanceOf[js.Any]))
    UVGenerator.foreach(v => _res.updateDynamic("UVGenerator")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[ExtrudeGeometryOptions]
