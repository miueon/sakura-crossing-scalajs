// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

// #WARN: Disambiguated erasure-colliding overload 'toArray' with @targetName("toArray_1") and @JSName("toArray")
@js.native
@JSImport("three", "Color")
class Color(color: js.UndefOr[String | Double | Color] = js.undefined) extends js.Object:
  def this(r: Double, g: Double, b: Double) = this(js.native: js.UndefOr[String | Double | Color])
  var b: Double = js.native
  var g: Double = js.native
  def isColor: true = js.native
  var r: Double = js.native
  @JSName("add")
  protected def add$raw(color: Color): Color = js.native
  @JSName("addColors")
  protected def addColors$raw(color1: Color, color2: Color): Color = js.native
  @JSName("addScalar")
  protected def addScalar$raw(s: Double): Color = js.native
  @JSName("applyMatrix3")
  protected def applyMatrix3$raw(m: Matrix3): Color = js.native
  @JSName("clone")
  protected def clone$raw(): Color = js.native
  @JSName("convertLinearToSRGB")
  protected def convertLinearToSRGB$raw(): Color = js.native
  @JSName("convertSRGBToLinear")
  protected def convertSRGBToLinear$raw(): Color = js.native
  @JSName("copy")
  protected def copy$raw(color: Color): Color = js.native
  @JSName("copyLinearToSRGB")
  protected def copyLinearToSRGB$raw(color: Color): Color = js.native
  @JSName("copySRGBToLinear")
  protected def copySRGBToLinear$raw(color: Color): Color = js.native
  // #WARN: Color.equals collides with the pinned platform member 'java.lang.Object#equals' only at the Scala source name; the JS wire key 'equals' is preserved with @JSName and the member is emitted under a derived collision-free name (the inherited member stays callable under Scala semantics)
  @JSName("equals")
  def equals_1(color: Color): Boolean = js.native
  @JSName("fromArray")
  protected def fromArray$raw(array: facadecontract.std.JSArrayLike[Double] | js.Array[Double], offset: js.UndefOr[Double] = js.undefined): Color = js.native
  @JSName("fromBufferAttribute")
  protected def fromBufferAttribute$raw(attribute: BufferAttribute[BufferAttributeEventMap] | InterleavedBufferAttribute, index: Double): Color = js.native
  def getHex(colorSpace: js.UndefOr[String] = js.undefined): Double = js.native
  def getHexString(colorSpace: js.UndefOr[String] = js.undefined): String = js.native
  def getHSL(target: HSL, colorSpace: js.UndefOr[String] = js.undefined): HSL = js.native
  def getRGB(target: RGB, colorSpace: js.UndefOr[String] = js.undefined): RGB = js.native
  def getStyle(colorSpace: js.UndefOr[String] = js.undefined): String = js.native
  @JSName(js.Symbol.iterator)
  def jsIterator(): js.Object = js.native
  @JSName("lerp")
  protected def lerp$raw(color: Color, alpha: Double): Color = js.native
  @JSName("lerpColors")
  protected def lerpColors$raw(color1: Color, color2: Color, alpha: Double): Color = js.native
  @JSName("lerpHSL")
  protected def lerpHSL$raw(color: Color, alpha: Double): Color = js.native
  @JSName("multiply")
  protected def multiply$raw(color: Color): Color = js.native
  @JSName("multiplyScalar")
  protected def multiplyScalar$raw(s: Double): Color = js.native
  @JSName("offsetHSL")
  protected def offsetHSL$raw(h: Double, s: Double, l: Double): Color = js.native
  @JSName("set")
  protected def set$raw(color: String | Double | Color): Color = js.native
  @JSName("set")
  protected def set$raw(r: Double, g: Double, b: Double): Color = js.native
  @JSName("setColorName")
  protected def setColorName$raw(style: String, colorSpace: js.UndefOr[String] = js.undefined): Color = js.native
  @JSName("setFromVector3")
  protected def setFromVector3$raw(vector: Vector3): Color = js.native
  @JSName("setHex")
  protected def setHex$raw(hex: Double, colorSpace: js.UndefOr[String] = js.undefined): Color = js.native
  @JSName("setHSL")
  protected def setHSL$raw(h: Double, s: Double, l: Double, colorSpace: js.UndefOr[String] = js.undefined): Color = js.native
  @JSName("setRGB")
  protected def setRGB$raw(r: Double, g: Double, b: Double, colorSpace: js.UndefOr[String] = js.undefined): Color = js.native
  @JSName("setScalar")
  protected def setScalar$raw(scalar: Double): Color = js.native
  @JSName("setStyle")
  protected def setStyle$raw(style: String, colorSpace: js.UndefOr[String] = js.undefined): Color = js.native
  @JSName("sub")
  protected def sub$raw(color: Color): Color = js.native
  def toArray(array: js.UndefOr[js.Array[Double]] = js.undefined, offset: js.UndefOr[Double] = js.undefined): js.Array[Double] = js.native
  // #WARN: Disambiguated erasure-colliding overload 'toArray' with @targetName("toArray_1") and @JSName("toArray")
  @scala.annotation.targetName("toArray_1")
  @JSName("toArray")
  def toArray(xyz: facadecontract.std.JSArrayLike[Double], offset: js.UndefOr[Double]): facadecontract.std.JSArrayLike[Double] = js.native
  def toJSON(): Double = js.native


object Color:
  extension [Self <: Color](self: Self)
    inline def set(color: String | Double | Color): Self = self.set$raw(color).asInstanceOf[Self]
    inline def set(r: Double, g: Double, b: Double): Self = self.set$raw(r, g, b).asInstanceOf[Self]
    inline def setFromVector3(vector: Vector3): Self = self.setFromVector3$raw(vector).asInstanceOf[Self]
    inline def setScalar(scalar: Double): Self = self.setScalar$raw(scalar).asInstanceOf[Self]
    inline def setHex(hex: Double, colorSpace: js.UndefOr[String] = js.undefined): Self = self.setHex$raw(hex, colorSpace).asInstanceOf[Self]
    inline def setRGB(r: Double, g: Double, b: Double, colorSpace: js.UndefOr[String] = js.undefined): Self = self.setRGB$raw(r, g, b, colorSpace).asInstanceOf[Self]
    inline def setHSL(h: Double, s: Double, l: Double, colorSpace: js.UndefOr[String] = js.undefined): Self = self.setHSL$raw(h, s, l, colorSpace).asInstanceOf[Self]
    inline def setStyle(style: String, colorSpace: js.UndefOr[String] = js.undefined): Self = self.setStyle$raw(style, colorSpace).asInstanceOf[Self]
    inline def setColorName(style: String, colorSpace: js.UndefOr[String] = js.undefined): Self = self.setColorName$raw(style, colorSpace).asInstanceOf[Self]
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]
    inline def copy(color: Color): Self = self.copy$raw(color).asInstanceOf[Self]
    inline def copySRGBToLinear(color: Color): Self = self.copySRGBToLinear$raw(color).asInstanceOf[Self]
    inline def copyLinearToSRGB(color: Color): Self = self.copyLinearToSRGB$raw(color).asInstanceOf[Self]
    inline def convertSRGBToLinear(): Self = self.convertSRGBToLinear$raw().asInstanceOf[Self]
    inline def convertLinearToSRGB(): Self = self.convertLinearToSRGB$raw().asInstanceOf[Self]
    inline def offsetHSL(h: Double, s: Double, l: Double): Self = self.offsetHSL$raw(h, s, l).asInstanceOf[Self]
    inline def add(color: Color): Self = self.add$raw(color).asInstanceOf[Self]
    inline def addColors(color1: Color, color2: Color): Self = self.addColors$raw(color1, color2).asInstanceOf[Self]
    inline def addScalar(s: Double): Self = self.addScalar$raw(s).asInstanceOf[Self]
    inline def applyMatrix3(m: Matrix3): Self = self.applyMatrix3$raw(m).asInstanceOf[Self]
    inline def sub(color: Color): Self = self.sub$raw(color).asInstanceOf[Self]
    inline def multiply(color: Color): Self = self.multiply$raw(color).asInstanceOf[Self]
    inline def multiplyScalar(s: Double): Self = self.multiplyScalar$raw(s).asInstanceOf[Self]
    inline def lerp(color: Color, alpha: Double): Self = self.lerp$raw(color, alpha).asInstanceOf[Self]
    inline def lerpColors(color1: Color, color2: Color, alpha: Double): Self = self.lerpColors$raw(color1, color2, alpha).asInstanceOf[Self]
    inline def lerpHSL(color: Color, alpha: Double): Self = self.lerpHSL$raw(color, alpha).asInstanceOf[Self]
    inline def fromArray(array: facadecontract.std.JSArrayLike[Double] | js.Array[Double], offset: js.UndefOr[Double] = js.undefined): Self = self.fromArray$raw(array, offset).asInstanceOf[Self]
    inline def fromBufferAttribute(attribute: BufferAttribute[BufferAttributeEventMap] | InterleavedBufferAttribute, index: Double): Self = self.fromBufferAttribute$raw(attribute, index).asInstanceOf[Self]

  // #WARN: static 'NAMES' is declared read-write upstream; the member-level native publication cannot express a JS setter, so its write direction is withheld
  @js.native
  @JSImport("three", "Color.NAMES")
  def NAMES: ColorCompanionNAMES = js.native
