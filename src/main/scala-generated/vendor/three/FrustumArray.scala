// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "FrustumArray")
class FrustumArray() extends js.Object:
  var coordinateSystem: 2000 | 2001 = js.native
  override def clone(): FrustumArray = js.native
  def containsPoint(point: Vector3): Boolean = js.native
  @JSName("copy")
  protected def copy$raw(source: FrustumArray): FrustumArray = js.native
  def intersectsBox(box: Box3): Boolean = js.native
  def intersectsObject(`object`: Object3D[Object3DEventMap]): Boolean = js.native
  def intersectsSphere(sphere: Sphere): Boolean = js.native
  def intersectsSprite(sprite: Sprite[Object3DEventMap]): Boolean = js.native
  def setFromArrayCamera(cameraArray: ArrayCamera): FrustumArray = js.native

object FrustumArray:
  extension [Self <: FrustumArray](self: Self)
    inline def copy(source: FrustumArray): Self = self.copy$raw(source).asInstanceOf[Self]
