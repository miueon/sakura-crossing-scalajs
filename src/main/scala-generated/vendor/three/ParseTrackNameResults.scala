// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait ParseTrackNameResults extends js.Object:
  var nodeName: String = js.native
  var objectIndex: String = js.native
  var objectName: String = js.native
  var propertyIndex: String = js.native
  var propertyName: String = js.native

object ParseTrackNameResults:
  def apply(
    nodeName: String,
    objectName: String,
    objectIndex: String,
    propertyName: String,
    propertyIndex: String
  ): ParseTrackNameResults =
    js.Dynamic.literal(
      "nodeName" -> nodeName.asInstanceOf[js.Any],
      "objectName" -> objectName.asInstanceOf[js.Any],
      "objectIndex" -> objectIndex.asInstanceOf[js.Any],
      "propertyName" -> propertyName.asInstanceOf[js.Any],
      "propertyIndex" -> propertyIndex.asInstanceOf[js.Any]
    ).asInstanceOf[ParseTrackNameResults]
