// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Camera")
class Camera() extends Object3D[Object3DEventMap]:
  var coordinateSystem: 2000 | 2001 = js.native
  def isCamera: Boolean = js.native
  var matrixWorldInverse: Matrix4 = js.native
  var projectionMatrix: Matrix4 = js.native
  var projectionMatrixInverse: Matrix4 = js.native
  def reversedDepth: Boolean = js.native
  var viewport: js.UndefOr[Vector4] = js.native
  @JSName("clone")
  protected override def clone$raw(recursive: js.UndefOr[Boolean] = js.undefined): Camera = js.native
  @JSName("copy")
  protected override def copy$raw(`object`: Object3D[Object3DEventMap], recursive: js.UndefOr[Boolean] = js.undefined): Camera = js.native

object Camera:
  extension [Self <: Camera](self: Self)
    inline def clone(): Self = self.clone$raw(js.undefined).asInstanceOf[Self]
    inline def copy(source: Camera, recursive: js.UndefOr[Boolean] = js.undefined): Self = self.copy$raw(source, recursive).asInstanceOf[Self]
