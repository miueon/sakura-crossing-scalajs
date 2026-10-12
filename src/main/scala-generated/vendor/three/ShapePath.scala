// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "ShapePath")
class ShapePath() extends js.Object:
  var color: Color = js.native
  var currentPath: Path | Null = js.native
  var subPaths: js.Array[Path] = js.native
  var `type`: String = js.native
  var userData: js.Dictionary[js.Any] = js.native
  @JSName("bezierCurveTo")
  protected def bezierCurveTo$raw(aCP1x: Double, aCP1y: Double, aCP2x: Double, aCP2y: Double, aX: Double, aY: Double): ShapePath = js.native
  @JSName("lineTo")
  protected def lineTo$raw(x: Double, y: Double): ShapePath = js.native
  @JSName("moveTo")
  protected def moveTo$raw(x: Double, y: Double): ShapePath = js.native
  @JSName("quadraticCurveTo")
  protected def quadraticCurveTo$raw(aCPx: Double, aCPy: Double, aX: Double, aY: Double): ShapePath = js.native
  @JSName("splineThru")
  protected def splineThru$raw(pts: js.Array[Vector2]): ShapePath = js.native
  def toShapes(): js.Array[Shape] = js.native

object ShapePath:
  extension [Self <: ShapePath](self: Self)
    inline def moveTo(x: Double, y: Double): Self = self.moveTo$raw(x, y).asInstanceOf[Self]
    inline def lineTo(x: Double, y: Double): Self = self.lineTo$raw(x, y).asInstanceOf[Self]
    inline def quadraticCurveTo(aCPx: Double, aCPy: Double, aX: Double, aY: Double): Self = self.quadraticCurveTo$raw(aCPx, aCPy, aX, aY).asInstanceOf[Self]
    inline def bezierCurveTo(aCP1x: Double, aCP1y: Double, aCP2x: Double, aCP2y: Double, aX: Double, aY: Double): Self = self.bezierCurveTo$raw(aCP1x, aCP1y, aCP2x, aCP2y, aX, aY).asInstanceOf[Self]
    inline def splineThru(pts: js.Array[Vector2]): Self = self.splineThru$raw(pts).asInstanceOf[Self]
