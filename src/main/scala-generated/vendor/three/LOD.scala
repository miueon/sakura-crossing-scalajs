// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "LOD")
class LOD[TEventMap <: Object3DEventMap]() extends Object3D[TEventMap]:
  var autoUpdate: Boolean = js.native
  def isLOD: true = js.native
  def levels: js.Array[LODLevels] = js.native
  override def `type`: String = js.native
  @JSName("addLevel")
  protected def addLevel$raw(`object`: Object3D[Object3DEventMap], distance: js.UndefOr[Double] = js.undefined, hysteresis: js.UndefOr[Double] = js.undefined): LOD[TEventMap] = js.native
  def getCurrentLevel(): Double = js.native
  def getObjectForDistance(distance: Double): Object3D[Object3DEventMap] | Null = js.native
  def removeLevel(distance: Double): Boolean = js.native
  override def toJSON(meta: js.UndefOr[JSONMeta] = js.undefined): LODJSON = js.native
  def update(camera: Camera): Unit = js.native

object LOD:
  extension [TEventMap <: Object3DEventMap, Self <: LOD[TEventMap]](self: Self)
    inline def addLevel(`object`: Object3D[Object3DEventMap], distance: js.UndefOr[Double] = js.undefined, hysteresis: js.UndefOr[Double] = js.undefined): Self = self.addLevel$raw(`object`, distance, hysteresis).asInstanceOf[Self]
