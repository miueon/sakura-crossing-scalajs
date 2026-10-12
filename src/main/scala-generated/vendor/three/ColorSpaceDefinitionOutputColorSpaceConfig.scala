// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait ColorSpaceDefinitionOutputColorSpaceConfig extends js.Object:
  var drawingBufferColorSpace: String = js.native
  var toneMappingNode: js.UndefOr["extended" | "standard"] = js.native

object ColorSpaceDefinitionOutputColorSpaceConfig:
  def apply(
    drawingBufferColorSpace: String,
    toneMappingNode: js.UndefOr["extended" | "standard"] = js.undefined
  ): ColorSpaceDefinitionOutputColorSpaceConfig =
    val _res = js.Dynamic.literal("drawingBufferColorSpace" -> drawingBufferColorSpace.asInstanceOf[js.Any])
    toneMappingNode.foreach(v => _res.updateDynamic("toneMappingNode")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[ColorSpaceDefinitionOutputColorSpaceConfig]
