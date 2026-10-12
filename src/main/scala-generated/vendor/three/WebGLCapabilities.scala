// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "WebGLCapabilities")
class WebGLCapabilities(gl: js.Object, extensions: js.Any, parameters: WebGLCapabilitiesParameters) extends js.Object:
  def isWebGL2: Boolean = js.native
  var logarithmicDepthBuffer: Boolean = js.native
  var maxAttributes: Double = js.native
  var maxCubemapSize: Double = js.native
  var maxFragmentUniforms: Double = js.native
  var maxSamples: Double = js.native
  var maxTextures: Double = js.native
  var maxTextureSize: Double = js.native
  var maxVaryings: Double = js.native
  var maxVertexTextures: Double = js.native
  var maxVertexUniforms: Double = js.native
  var precision: String = js.native
  var reversedDepthBuffer: Boolean = js.native
  var samples: Double = js.native
  def getMaxAnisotropy(): Double = js.native
  def getMaxPrecision(precision: String): String = js.native
  def textureFormatReadable(textureFormat: 1021 | 1022 | 1023 | 1026 | 1027 | 1028 | 1029 | 1030 | 1031 | 1032 | 1033): Boolean = js.native
  def textureTypeReadable(textureType: 1009 | 1010 | 1011 | 1012 | 1013 | 1014 | 1015 | 1016 | 1017 | 1018 | 1020 | 35899 | 35902): Boolean = js.native
