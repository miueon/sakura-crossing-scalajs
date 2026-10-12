// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait UVGenerator extends js.Object:
  def generateSideWallUV(geometry: ExtrudeGeometry, vertices: js.Array[Double], indexA: Double, indexB: Double, indexC: Double, indexD: Double): js.Array[Vector2] = js.native
  def generateTopUV(geometry: ExtrudeGeometry, vertices: js.Array[Double], indexA: Double, indexB: Double, indexC: Double): js.Array[Vector2] = js.native

object UVGenerator:
  def of(
    generateSideWallUV: js.Function6[ExtrudeGeometry, js.Array[Double], Double, Double, Double, Double, js.Array[Vector2]],
    generateTopUV: js.Function5[ExtrudeGeometry, js.Array[Double], Double, Double, Double, js.Array[Vector2]]
  ): UVGenerator =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "generateSideWallUV" -> generateSideWallUV.asInstanceOf[js.Any],
      "generateTopUV" -> generateTopUV.asInstanceOf[js.Any]
    ).asInstanceOf[UVGenerator]
