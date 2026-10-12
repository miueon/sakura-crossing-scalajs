// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait ColorManagement extends js.Object:
  var enabled: Boolean = js.native
  var spaces: js.Dictionary[ColorSpaceDefinition] = js.native
  var workingColorSpace: String = js.native
  def colorSpaceToWorking(color: Color, sourceColorSpace: String): Color = js.native
  def convert(color: Color, sourceColorSpace: String, targetColorSpace: String): Color = js.native
  def define(colorSpaces: js.Dictionary[ColorSpaceDefinition]): Unit = js.native
  def fromWorkingColorSpace(color: Color, targetColorSpace: String): Color = js.native
  def getLuminanceCoefficients(target: Vector3, colorSpace: js.UndefOr[String] = js.undefined): Vector3 = js.native
  def getPrimaries(colorSpace: String): js.Tuple6[Double, Double, Double, Double, Double, Double] = js.native
  def getTransfer(colorSpace: String): "linear" | "srgb" = js.native
  def toWorkingColorSpace(color: Color, sourceColorSpace: String): Color = js.native
  def workingToColorSpace(color: Color, targetColorSpace: String): Color = js.native

object ColorManagement:
  def of(
    colorSpaceToWorking: js.Function2[Color, String, Color],
    convert: js.Function3[Color, String, String, Color],
    define: js.Function1[js.Dictionary[ColorSpaceDefinition], Unit],
    enabled: Boolean,
    fromWorkingColorSpace: js.Function2[Color, String, Color],
    getLuminanceCoefficients: js.Function2[Vector3, js.UndefOr[String], Vector3],
    getPrimaries: js.Function1[String, js.Tuple6[Double, Double, Double, Double, Double, Double]],
    getTransfer: js.Function1[String, "linear" | "srgb"],
    spaces: js.Dictionary[ColorSpaceDefinition],
    toWorkingColorSpace: js.Function2[Color, String, Color],
    workingColorSpace: String,
    workingToColorSpace: js.Function2[Color, String, Color]
  ): ColorManagement =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "colorSpaceToWorking" -> colorSpaceToWorking.asInstanceOf[js.Any],
      "convert" -> convert.asInstanceOf[js.Any],
      "define" -> define.asInstanceOf[js.Any],
      "enabled" -> enabled.asInstanceOf[js.Any],
      "fromWorkingColorSpace" -> fromWorkingColorSpace.asInstanceOf[js.Any],
      "getLuminanceCoefficients" -> getLuminanceCoefficients.asInstanceOf[js.Any],
      "getPrimaries" -> getPrimaries.asInstanceOf[js.Any],
      "getTransfer" -> getTransfer.asInstanceOf[js.Any],
      "spaces" -> spaces.asInstanceOf[js.Any],
      "toWorkingColorSpace" -> toWorkingColorSpace.asInstanceOf[js.Any],
      "workingColorSpace" -> workingColorSpace.asInstanceOf[js.Any],
      "workingToColorSpace" -> workingToColorSpace.asInstanceOf[js.Any]
    ).asInstanceOf[ColorManagement]
