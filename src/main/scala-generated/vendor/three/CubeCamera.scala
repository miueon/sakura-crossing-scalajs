// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "CubeCamera")
class CubeCamera(near: Double, far: Double, renderTarget_1: CubeRenderTarget | WebGLCubeRenderTarget) extends Object3D[Object3DEventMap]:
  var activeMipmapLevel: Double = js.native
  var coordinateSystem: 2000 | 2001 | Null = js.native
  var renderTarget: WebGLCubeRenderTarget = js.native
  def update(renderer: CubeCameraRenderer, scene: Object3D[Object3DEventMap]): Unit = js.native
  def updateCoordinateSystem(): Unit = js.native
