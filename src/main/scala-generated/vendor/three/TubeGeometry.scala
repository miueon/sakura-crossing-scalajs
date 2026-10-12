// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

// #WARN: heritage application of 'BufferGeometry' widens the argument of binder 'Attributes' to the substituted bound (argument-conformance-unproven); a checker-proven structural relation materialized as synthetic heritage is the lane that keeps such an argument exact
@js.native
@JSImport("three", "TubeGeometry")
class TubeGeometry(path: js.UndefOr[Curve[Vector3]] = js.undefined, tubularSegments: js.UndefOr[Double] = js.undefined, radius: js.UndefOr[Double] = js.undefined, radialSegments: js.UndefOr[Double] = js.undefined, closed: js.UndefOr[Boolean] = js.undefined) extends BufferGeometry[NormalOrGLBufferAttributes, BufferGeometryEventMap]:
  var binormals: js.Array[Vector3] = js.native
  var normals: js.Array[Vector3] = js.native
  def parameters: TubeGeometryParameters = js.native
  var tangents: js.Array[Vector3] = js.native
  override def `type`: String = js.native

@js.native
@JSImport("three", "TubeGeometry")
object TubeGeometry extends js.Object:
  def fromJSON(data: js.Any): TubeGeometry = js.native
