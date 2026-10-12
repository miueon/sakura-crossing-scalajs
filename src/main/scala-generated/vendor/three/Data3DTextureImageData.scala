// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait Data3DTextureImageData extends js.Object:
  var data: js.typedarray.Float32Array | js.typedarray.Float64Array | js.typedarray.Int16Array | js.typedarray.Int32Array | js.typedarray.Int8Array | js.typedarray.Uint16Array | js.typedarray.Uint32Array | js.typedarray.Uint8Array | js.typedarray.Uint8ClampedArray | Null = js.native
  var depth: Double = js.native
  var height: Double = js.native
  var width: Double = js.native

object Data3DTextureImageData:
  def apply(
    data: js.typedarray.Float32Array | js.typedarray.Float64Array | js.typedarray.Int16Array | js.typedarray.Int32Array | js.typedarray.Int8Array | js.typedarray.Uint16Array | js.typedarray.Uint32Array | js.typedarray.Uint8Array | js.typedarray.Uint8ClampedArray | Null,
    width: Double,
    height: Double,
    depth: Double
  ): Data3DTextureImageData =
    js.Dynamic.literal(
      "data" -> data.asInstanceOf[js.Any],
      "width" -> width.asInstanceOf[js.Any],
      "height" -> height.asInstanceOf[js.Any],
      "depth" -> depth.asInstanceOf[js.Any]
    ).asInstanceOf[Data3DTextureImageData]
