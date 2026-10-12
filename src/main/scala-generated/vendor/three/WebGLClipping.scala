// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "WebGLClipping")
class WebGLClipping(properties: WebGLProperties) extends js.Object:
  var numIntersection: Double = js.native
  var numPlanes: Double = js.native
  var uniform: WebGLClippingUniform = js.native
  def beginShadows(): Unit = js.native
  def endShadows(): Unit = js.native
  def init(planes: js.Array[js.Any], enableLocalClipping: Boolean): Boolean = js.native
  def setGlobalState(planes: js.Array[Plane], camera: Camera): Unit = js.native
  def setState(material: Material[MaterialEventMap], camera: Camera, useCache: Boolean): Unit = js.native
