// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

// #WARN: Disambiguated erasure-colliding overload 'toArray' with @targetName("toArray_1") and @JSName("toArray")
// #WARN: Disambiguated erasure-colliding overload 'toArray' with @targetName("toArray_2") and @JSName("toArray")
@js.native
@JSImport("three", "Vector3")
class Vector3(x_1: js.UndefOr[Double] = js.undefined, y_1: js.UndefOr[Double] = js.undefined, z_1: js.UndefOr[Double] = js.undefined) extends js.Object:
  def isVector3: true = js.native
  var x: Double = js.native
  var y: Double = js.native
  var z: Double = js.native
  @JSName("add")
  protected def add$raw(v: Vector3Like): Vector3 = js.native
  @JSName("addScalar")
  protected def addScalar$raw(s: Double): Vector3 = js.native
  @JSName("addScaledVector")
  protected def addScaledVector$raw(v: Vector3Like, s: Double): Vector3 = js.native
  @JSName("addVectors")
  protected def addVectors$raw(a: Vector3Like, b: Vector3Like): Vector3 = js.native
  def angleTo(v: Vector3): Double = js.native
  @JSName("applyAxisAngle")
  protected def applyAxisAngle$raw(axis: Vector3Like, angle: Double): Vector3 = js.native
  @JSName("applyEuler")
  protected def applyEuler$raw(euler: Euler): Vector3 = js.native
  @JSName("applyMatrix3")
  protected def applyMatrix3$raw(m: Matrix3): Vector3 = js.native
  @JSName("applyMatrix4")
  protected def applyMatrix4$raw(m: Matrix4): Vector3 = js.native
  @JSName("applyNormalMatrix")
  protected def applyNormalMatrix$raw(m: Matrix3): Vector3 = js.native
  @JSName("applyQuaternion")
  protected def applyQuaternion$raw(q: QuaternionLike): Vector3 = js.native
  @JSName("ceil")
  protected def ceil$raw(): Vector3 = js.native
  @JSName("clamp")
  protected def clamp$raw(min: Vector3Like, max: Vector3Like): Vector3 = js.native
  @JSName("clampLength")
  protected def clampLength$raw(min: Double, max: Double): Vector3 = js.native
  @JSName("clampScalar")
  protected def clampScalar$raw(min: Double, max: Double): Vector3 = js.native
  @JSName("clone")
  protected def clone$raw(): Vector3 = js.native
  @JSName("copy")
  protected def copy$raw(v: Vector3Like): Vector3 = js.native
  @JSName("cross")
  protected def cross$raw(v: Vector3Like): Vector3 = js.native
  @JSName("crossVectors")
  protected def crossVectors$raw(a: Vector3Like, b: Vector3Like): Vector3 = js.native
  def distanceTo(v: Vector3Like): Double = js.native
  def distanceToSquared(v: Vector3Like): Double = js.native
  @JSName("divide")
  protected def divide$raw(v: Vector3Like): Vector3 = js.native
  @JSName("divideScalar")
  protected def divideScalar$raw(s: Double): Vector3 = js.native
  def dot(v: Vector3Like): Double = js.native
  // #WARN: Vector3.equals collides with the pinned platform member 'java.lang.Object#equals' only at the Scala source name; the JS wire key 'equals' is preserved with @JSName and the member is emitted under a derived collision-free name (the inherited member stays callable under Scala semantics)
  @JSName("equals")
  def equals_1(v: Vector3Like): Boolean = js.native
  @JSName("floor")
  protected def floor$raw(): Vector3 = js.native
  @JSName("fromArray")
  protected def fromArray$raw(array: facadecontract.std.JSArrayLike[Double] | js.Array[Double], offset: js.UndefOr[Double] = js.undefined): Vector3 = js.native
  @JSName("fromBufferAttribute")
  protected def fromBufferAttribute$raw(attribute: BufferAttribute[BufferAttributeEventMap] | InterleavedBufferAttribute, index: Double): Vector3 = js.native
  def getComponent(index: Double): Double = js.native
  @JSName(js.Symbol.iterator)
  def jsIterator(): js.Iterator[Double] = js.native
  def length(): Double = js.native
  def lengthSq(): Double = js.native
  @JSName("lerp")
  protected def lerp$raw(v: Vector3Like, alpha: Double): Vector3 = js.native
  @JSName("lerpVectors")
  protected def lerpVectors$raw(v1: Vector3Like, v2: Vector3Like, alpha: Double): Vector3 = js.native
  def manhattanDistanceTo(v: Vector3Like): Double = js.native
  def manhattanLength(): Double = js.native
  @JSName("max")
  protected def max$raw(v: Vector3Like): Vector3 = js.native
  @JSName("min")
  protected def min$raw(v: Vector3Like): Vector3 = js.native
  @JSName("multiply")
  protected def multiply$raw(v: Vector3Like): Vector3 = js.native
  @JSName("multiplyScalar")
  protected def multiplyScalar$raw(s: Double): Vector3 = js.native
  @JSName("multiplyVectors")
  protected def multiplyVectors$raw(a: Vector3Like, b: Vector3Like): Vector3 = js.native
  @JSName("negate")
  protected def negate$raw(): Vector3 = js.native
  @JSName("normalize")
  protected def normalize$raw(): Vector3 = js.native
  @JSName("project")
  protected def project$raw(camera: Camera): Vector3 = js.native
  @JSName("projectOnPlane")
  protected def projectOnPlane$raw(planeNormal: Vector3): Vector3 = js.native
  @JSName("projectOnVector")
  protected def projectOnVector$raw(v: Vector3): Vector3 = js.native
  @JSName("random")
  protected def random$raw(): Vector3 = js.native
  @JSName("randomDirection")
  protected def randomDirection$raw(): Vector3 = js.native
  @JSName("reflect")
  protected def reflect$raw(normal: Vector3Like): Vector3 = js.native
  @JSName("round")
  protected def round$raw(): Vector3 = js.native
  @JSName("roundToZero")
  protected def roundToZero$raw(): Vector3 = js.native
  @JSName("set")
  protected def set$raw(x: Double, y: Double, z: js.UndefOr[Double] = js.undefined): Vector3 = js.native
  @JSName("setComponent")
  protected def setComponent$raw(index: Double, value: Double): Vector3 = js.native
  @JSName("setFromColor")
  protected def setFromColor$raw(color: RGB): Vector3 = js.native
  @JSName("setFromCylindrical")
  protected def setFromCylindrical$raw(s: Cylindrical): Vector3 = js.native
  @JSName("setFromCylindricalCoords")
  protected def setFromCylindricalCoords$raw(radius: Double, theta: Double, y: Double): Vector3 = js.native
  @JSName("setFromEuler")
  protected def setFromEuler$raw(e: Euler): Vector3 = js.native
  @JSName("setFromMatrix3Column")
  protected def setFromMatrix3Column$raw(matrix: Matrix3, index: Double): Vector3 = js.native
  @JSName("setFromMatrixColumn")
  protected def setFromMatrixColumn$raw(matrix: Matrix4, index: Double): Vector3 = js.native
  @JSName("setFromMatrixPosition")
  protected def setFromMatrixPosition$raw(m: Matrix4): Vector3 = js.native
  @JSName("setFromMatrixScale")
  protected def setFromMatrixScale$raw(m: Matrix4): Vector3 = js.native
  @JSName("setFromSpherical")
  protected def setFromSpherical$raw(s: Spherical): Vector3 = js.native
  @JSName("setFromSphericalCoords")
  protected def setFromSphericalCoords$raw(r: Double, phi: Double, theta: Double): Vector3 = js.native
  @JSName("setLength")
  protected def setLength$raw(l: Double): Vector3 = js.native
  @JSName("setScalar")
  protected def setScalar$raw(scalar: Double): Vector3 = js.native
  @JSName("setX")
  protected def setX$raw(x: Double): Vector3 = js.native
  @JSName("setY")
  protected def setY$raw(y: Double): Vector3 = js.native
  @JSName("setZ")
  protected def setZ$raw(z: Double): Vector3 = js.native
  @JSName("sub")
  protected def sub$raw(v: Vector3Like): Vector3 = js.native
  @JSName("subScalar")
  protected def subScalar$raw(s: Double): Vector3 = js.native
  @JSName("subVectors")
  protected def subVectors$raw(a: Vector3Like, b: Vector3Like): Vector3 = js.native
  def toArray(array: js.UndefOr[js.Array[Double]] = js.undefined, offset: js.UndefOr[Double] = js.undefined): js.Array[Double] = js.native
  // #WARN: Disambiguated erasure-colliding overload 'toArray' with @targetName("toArray_1") and @JSName("toArray")
  @scala.annotation.targetName("toArray_1")
  @JSName("toArray")
  def toArray(array: js.UndefOr[js.Tuple3[Double, Double, Double]], offset: js.UndefOr[0]): js.Tuple3[Double, Double, Double] = js.native
  // #WARN: Disambiguated erasure-colliding overload 'toArray' with @targetName("toArray_2") and @JSName("toArray")
  @scala.annotation.targetName("toArray_2")
  @JSName("toArray")
  def toArray(array: facadecontract.std.JSArrayLike[Double], offset: js.UndefOr[Double]): facadecontract.std.JSArrayLike[Double] = js.native
  @JSName("transformDirection")
  protected def transformDirection$raw(m: Matrix4): Vector3 = js.native
  @JSName("unproject")
  protected def unproject$raw(camera: Camera): Vector3 = js.native

object Vector3:
  extension [Self <: Vector3](self: Self)
    inline def set(x: Double, y: Double, z: js.UndefOr[Double] = js.undefined): Self = self.set$raw(x, y, z).asInstanceOf[Self]
    inline def setScalar(scalar: Double): Self = self.setScalar$raw(scalar).asInstanceOf[Self]
    inline def setX(x: Double): Self = self.setX$raw(x).asInstanceOf[Self]
    inline def setY(y: Double): Self = self.setY$raw(y).asInstanceOf[Self]
    inline def setZ(z: Double): Self = self.setZ$raw(z).asInstanceOf[Self]
    inline def setComponent(index: Double, value: Double): Self = self.setComponent$raw(index, value).asInstanceOf[Self]
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]
    inline def copy(v: Vector3Like): Self = self.copy$raw(v).asInstanceOf[Self]
    inline def add(v: Vector3Like): Self = self.add$raw(v).asInstanceOf[Self]
    inline def addScalar(s: Double): Self = self.addScalar$raw(s).asInstanceOf[Self]
    inline def addVectors(a: Vector3Like, b: Vector3Like): Self = self.addVectors$raw(a, b).asInstanceOf[Self]
    inline def addScaledVector(v: Vector3Like, s: Double): Self = self.addScaledVector$raw(v, s).asInstanceOf[Self]
    inline def sub(v: Vector3Like): Self = self.sub$raw(v).asInstanceOf[Self]
    inline def subScalar(s: Double): Self = self.subScalar$raw(s).asInstanceOf[Self]
    inline def subVectors(a: Vector3Like, b: Vector3Like): Self = self.subVectors$raw(a, b).asInstanceOf[Self]
    inline def multiply(v: Vector3Like): Self = self.multiply$raw(v).asInstanceOf[Self]
    inline def multiplyScalar(s: Double): Self = self.multiplyScalar$raw(s).asInstanceOf[Self]
    inline def multiplyVectors(a: Vector3Like, b: Vector3Like): Self = self.multiplyVectors$raw(a, b).asInstanceOf[Self]
    inline def applyEuler(euler: Euler): Self = self.applyEuler$raw(euler).asInstanceOf[Self]
    inline def applyAxisAngle(axis: Vector3Like, angle: Double): Self = self.applyAxisAngle$raw(axis, angle).asInstanceOf[Self]
    inline def applyMatrix3(m: Matrix3): Self = self.applyMatrix3$raw(m).asInstanceOf[Self]
    inline def applyNormalMatrix(m: Matrix3): Self = self.applyNormalMatrix$raw(m).asInstanceOf[Self]
    inline def applyMatrix4(m: Matrix4): Self = self.applyMatrix4$raw(m).asInstanceOf[Self]
    inline def applyQuaternion(q: QuaternionLike): Self = self.applyQuaternion$raw(q).asInstanceOf[Self]
    inline def project(camera: Camera): Self = self.project$raw(camera).asInstanceOf[Self]
    inline def unproject(camera: Camera): Self = self.unproject$raw(camera).asInstanceOf[Self]
    inline def transformDirection(m: Matrix4): Self = self.transformDirection$raw(m).asInstanceOf[Self]
    inline def divide(v: Vector3Like): Self = self.divide$raw(v).asInstanceOf[Self]
    inline def divideScalar(s: Double): Self = self.divideScalar$raw(s).asInstanceOf[Self]
    inline def min(v: Vector3Like): Self = self.min$raw(v).asInstanceOf[Self]
    inline def max(v: Vector3Like): Self = self.max$raw(v).asInstanceOf[Self]
    inline def clamp(min: Vector3Like, max: Vector3Like): Self = self.clamp$raw(min, max).asInstanceOf[Self]
    inline def clampScalar(min: Double, max: Double): Self = self.clampScalar$raw(min, max).asInstanceOf[Self]
    inline def clampLength(min: Double, max: Double): Self = self.clampLength$raw(min, max).asInstanceOf[Self]
    inline def floor(): Self = self.floor$raw().asInstanceOf[Self]
    inline def ceil(): Self = self.ceil$raw().asInstanceOf[Self]
    inline def round(): Self = self.round$raw().asInstanceOf[Self]
    inline def roundToZero(): Self = self.roundToZero$raw().asInstanceOf[Self]
    inline def negate(): Self = self.negate$raw().asInstanceOf[Self]
    inline def normalize(): Self = self.normalize$raw().asInstanceOf[Self]
    inline def setLength(l: Double): Self = self.setLength$raw(l).asInstanceOf[Self]
    inline def lerp(v: Vector3Like, alpha: Double): Self = self.lerp$raw(v, alpha).asInstanceOf[Self]
    inline def lerpVectors(v1: Vector3Like, v2: Vector3Like, alpha: Double): Self = self.lerpVectors$raw(v1, v2, alpha).asInstanceOf[Self]
    inline def cross(v: Vector3Like): Self = self.cross$raw(v).asInstanceOf[Self]
    inline def crossVectors(a: Vector3Like, b: Vector3Like): Self = self.crossVectors$raw(a, b).asInstanceOf[Self]
    inline def projectOnVector(v: Vector3): Self = self.projectOnVector$raw(v).asInstanceOf[Self]
    inline def projectOnPlane(planeNormal: Vector3): Self = self.projectOnPlane$raw(planeNormal).asInstanceOf[Self]
    inline def reflect(normal: Vector3Like): Self = self.reflect$raw(normal).asInstanceOf[Self]
    inline def setFromSpherical(s: Spherical): Self = self.setFromSpherical$raw(s).asInstanceOf[Self]
    inline def setFromSphericalCoords(r: Double, phi: Double, theta: Double): Self = self.setFromSphericalCoords$raw(r, phi, theta).asInstanceOf[Self]
    inline def setFromCylindrical(s: Cylindrical): Self = self.setFromCylindrical$raw(s).asInstanceOf[Self]
    inline def setFromCylindricalCoords(radius: Double, theta: Double, y: Double): Self = self.setFromCylindricalCoords$raw(radius, theta, y).asInstanceOf[Self]
    inline def setFromMatrixPosition(m: Matrix4): Self = self.setFromMatrixPosition$raw(m).asInstanceOf[Self]
    inline def setFromMatrixScale(m: Matrix4): Self = self.setFromMatrixScale$raw(m).asInstanceOf[Self]
    inline def setFromMatrixColumn(matrix: Matrix4, index: Double): Self = self.setFromMatrixColumn$raw(matrix, index).asInstanceOf[Self]
    inline def setFromMatrix3Column(matrix: Matrix3, index: Double): Self = self.setFromMatrix3Column$raw(matrix, index).asInstanceOf[Self]
    inline def setFromEuler(e: Euler): Self = self.setFromEuler$raw(e).asInstanceOf[Self]
    inline def setFromColor(color: RGB): Self = self.setFromColor$raw(color).asInstanceOf[Self]
    inline def fromArray(array: facadecontract.std.JSArrayLike[Double] | js.Array[Double], offset: js.UndefOr[Double] = js.undefined): Self = self.fromArray$raw(array, offset).asInstanceOf[Self]
    inline def fromBufferAttribute(attribute: BufferAttribute[BufferAttributeEventMap] | InterleavedBufferAttribute, index: Double): Self = self.fromBufferAttribute$raw(attribute, index).asInstanceOf[Self]
    inline def random(): Self = self.random$raw().asInstanceOf[Self]
    inline def randomDirection(): Self = self.randomDirection$raw().asInstanceOf[Self]
