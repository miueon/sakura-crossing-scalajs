// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait InfoMemory extends js.Object:
  var attributes: Double = js.native
  var attributesSize: Double = js.native
  var geometries: Double = js.native
  var indexAttributes: Double = js.native
  var indexAttributesSize: Double = js.native
  var indirectStorageAttributes: Double = js.native
  var indirectStorageAttributesSize: Double = js.native
  var programs: Double = js.native
  var programsSize: Double = js.native
  var readbackBuffers: Double = js.native
  var readbackBuffersSize: Double = js.native
  var renderTargets: Double = js.native
  var storageAttributes: Double = js.native
  var storageAttributesSize: Double = js.native
  var textures: Double = js.native
  var texturesSize: Double = js.native
  var total: Double = js.native
  var uniformBuffers: Double = js.native
  var uniformBuffersSize: Double = js.native

object InfoMemory:
  def apply(
    attributes: Double,
    attributesSize: Double,
    geometries: Double,
    indexAttributes: Double,
    indexAttributesSize: Double,
    indirectStorageAttributes: Double,
    indirectStorageAttributesSize: Double,
    programs: Double,
    programsSize: Double,
    readbackBuffers: Double,
    readbackBuffersSize: Double,
    renderTargets: Double,
    storageAttributes: Double,
    storageAttributesSize: Double,
    textures: Double,
    texturesSize: Double,
    uniformBuffers: Double,
    uniformBuffersSize: Double,
    total: Double
  ): InfoMemory =
    js.Dynamic.literal(
      "attributes" -> attributes.asInstanceOf[js.Any],
      "attributesSize" -> attributesSize.asInstanceOf[js.Any],
      "geometries" -> geometries.asInstanceOf[js.Any],
      "indexAttributes" -> indexAttributes.asInstanceOf[js.Any],
      "indexAttributesSize" -> indexAttributesSize.asInstanceOf[js.Any],
      "indirectStorageAttributes" -> indirectStorageAttributes.asInstanceOf[js.Any],
      "indirectStorageAttributesSize" -> indirectStorageAttributesSize.asInstanceOf[js.Any],
      "programs" -> programs.asInstanceOf[js.Any],
      "programsSize" -> programsSize.asInstanceOf[js.Any],
      "readbackBuffers" -> readbackBuffers.asInstanceOf[js.Any],
      "readbackBuffersSize" -> readbackBuffersSize.asInstanceOf[js.Any],
      "renderTargets" -> renderTargets.asInstanceOf[js.Any],
      "storageAttributes" -> storageAttributes.asInstanceOf[js.Any],
      "storageAttributesSize" -> storageAttributesSize.asInstanceOf[js.Any],
      "textures" -> textures.asInstanceOf[js.Any],
      "texturesSize" -> texturesSize.asInstanceOf[js.Any],
      "uniformBuffers" -> uniformBuffers.asInstanceOf[js.Any],
      "uniformBuffersSize" -> uniformBuffersSize.asInstanceOf[js.Any],
      "total" -> total.asInstanceOf[js.Any]
    ).asInstanceOf[InfoMemory]
