// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Source")
class Source[TData](data_1: TData) extends js.Object:
  var data: TData = js.native
  var dataReady: Boolean = js.native
  def id: Double = js.native
  def isSource: true = js.native
  def needsUpdate_=(value: Boolean): Unit = js.native
  var uuid: String = js.native
  var version: Double = js.native
  def getSize(target: Vector3): Vector3 = js.native
  def toJSON(meta: js.UndefOr[String | js.Any] = js.undefined): SourceJSON = js.native
