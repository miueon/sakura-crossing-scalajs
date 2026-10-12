// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait ColorSpaceDefinition extends js.Object:
  var fromXYZ: Matrix3 = js.native
  var luminanceCoefficients: js.Tuple3[Double, Double, Double] = js.native
  var outputColorSpaceConfig: js.UndefOr[ColorSpaceDefinitionOutputColorSpaceConfig] = js.native
  var primaries: js.Tuple6[Double, Double, Double, Double, Double, Double] = js.native
  var toXYZ: Matrix3 = js.native
  var transfer: "linear" | "srgb" = js.native
  var whitePoint: js.Tuple2[Double, Double] = js.native
  var workingColorSpaceConfig: js.UndefOr[ColorSpaceDefinitionWorkingColorSpaceConfig] = js.native

object ColorSpaceDefinition:
  def apply(
    primaries: js.Tuple6[Double, Double, Double, Double, Double, Double],
    whitePoint: js.Tuple2[Double, Double],
    transfer: "linear" | "srgb",
    toXYZ: Matrix3,
    fromXYZ: Matrix3,
    luminanceCoefficients: js.Tuple3[Double, Double, Double],
    workingColorSpaceConfig: js.UndefOr[ColorSpaceDefinitionWorkingColorSpaceConfig] = js.undefined,
    outputColorSpaceConfig: js.UndefOr[ColorSpaceDefinitionOutputColorSpaceConfig] = js.undefined
  ): ColorSpaceDefinition =
    val _res = js.Dynamic.literal(
      "primaries" -> primaries.asInstanceOf[js.Any],
      "whitePoint" -> whitePoint.asInstanceOf[js.Any],
      "transfer" -> transfer.asInstanceOf[js.Any],
      "toXYZ" -> toXYZ.asInstanceOf[js.Any],
      "fromXYZ" -> fromXYZ.asInstanceOf[js.Any],
      "luminanceCoefficients" -> luminanceCoefficients.asInstanceOf[js.Any]
    )
    workingColorSpaceConfig.foreach(v => _res.updateDynamic("workingColorSpaceConfig")(v.asInstanceOf[js.Any]))
    outputColorSpaceConfig.foreach(v => _res.updateDynamic("outputColorSpaceConfig")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[ColorSpaceDefinition]
