// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "ConeGeometry")
class ConeGeometry(radius: js.UndefOr[Double] = js.undefined, height: js.UndefOr[Double] = js.undefined, radialSegments: js.UndefOr[Double] = js.undefined, heightSegments: js.UndefOr[Double] = js.undefined, openEnded: js.UndefOr[Boolean] = js.undefined, thetaStart: js.UndefOr[Double] = js.undefined, thetaLength: js.UndefOr[Double] = js.undefined) extends CylinderGeometry(js.native: js.UndefOr[Double], js.native: js.UndefOr[Double], js.native: js.UndefOr[Double], js.native: js.UndefOr[Double], js.native: js.UndefOr[Double], js.native: js.UndefOr[Boolean], js.native: js.UndefOr[Double], js.native: js.UndefOr[Double]):
  override def parameters: CylinderGeometryParameters = js.native
  override def `type`: String = js.native

@js.native
@JSImport("three", "ConeGeometry")
object ConeGeometry extends js.Object:
  def fromJSON(data: js.Any): ConeGeometry = js.native
