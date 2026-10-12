// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "PropertyBinding")
class PropertyBinding(rootNode_1: Object3D[Object3DEventMap] | Skeleton, path_1: String, parsedPath_1: js.UndefOr[js.Object | Null] = js.undefined) extends js.Object:
  def node: js.Object | Null = js.native
  def parsedPath: js.Object = js.native
  def path: String = js.native
  def rootNode: Object3D[Object3DEventMap] | Skeleton = js.native
  def bind(): Unit = js.native
  def unbind(): Unit = js.native

@js.native
@JSImport("three", "PropertyBinding")
object PropertyBinding extends js.Object:
  def Composite: js.Any = js.native
  def create(root: js.Object, path: String, parsedPath: js.UndefOr[js.Object | Null] = js.undefined): Composite | PropertyBinding = js.native
  def findNode(root: js.Object, nodeName: String | Double): js.Object | Null = js.native
  def parseTrackName(trackName: String): ParseTrackNameResults = js.native
  def sanitizeNodeName(name: String): String = js.native
