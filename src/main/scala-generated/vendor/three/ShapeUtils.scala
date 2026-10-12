// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "ShapeUtils")
class ShapeUtils() extends js.Object

@js.native
@JSImport("three", "ShapeUtils")
object ShapeUtils extends js.Object:
  def area(contour: facadecontract.std.JSReadonlyArray[Vector2Like]): Double = js.native
  def isClockWise(pts: facadecontract.std.JSReadonlyArray[Vector2Like]): Boolean = js.native
  def triangulateShape(contour: js.Array[Vector2Like], holes: js.Array[js.Array[Vector2Like]]): js.Array[js.Array[Double]] = js.native
