// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Shape")
class Shape(points: js.UndefOr[js.Array[Vector2]] = js.undefined) extends Path(js.native: js.UndefOr[js.Array[Vector2]]):
  var holes: js.Array[Path] = js.native
  override def `type`: String = js.native
  var uuid: String = js.native
  def extractPoints(divisions: Double): ShapeExtractPointsReturn = js.native
  @JSName("fromJSON")
  protected override def fromJSON$raw(json: CurveJSON): Shape = js.native
  def getPointsHoles(divisions: Double): js.Array[js.Array[Vector2]] = js.native

object Shape:
  extension [Self <: Shape](self: Self)
    inline def fromJSON(json: ShapeJSON): Self = self.fromJSON$raw(json).asInstanceOf[Self]
