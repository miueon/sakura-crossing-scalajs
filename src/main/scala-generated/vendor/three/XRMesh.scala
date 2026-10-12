// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRMesh extends js.Object:
  var indices: js.typedarray.Uint32Array = js.native
  var lastChangedTime: Double = js.native
  var meshSpace: XRSpace = js.native
  var semanticLabel: js.UndefOr[String] = js.native
  var vertices: js.typedarray.Float32Array = js.native

object XRMesh:
  def apply(
    meshSpace: XRSpace,
    vertices: js.typedarray.Float32Array,
    indices: js.typedarray.Uint32Array,
    lastChangedTime: Double,
    semanticLabel: js.UndefOr[String] = js.undefined
  ): XRMesh =
    val _res = js.Dynamic.literal(
      "meshSpace" -> meshSpace.asInstanceOf[js.Any],
      "vertices" -> vertices.asInstanceOf[js.Any],
      "indices" -> indices.asInstanceOf[js.Any],
      "lastChangedTime" -> lastChangedTime.asInstanceOf[js.Any]
    )
    semanticLabel.foreach(v => _res.updateDynamic("semanticLabel")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[XRMesh]
