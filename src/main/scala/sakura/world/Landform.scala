package sakura.world

import sakura.ThreeInterop
import vendor.three.*

import scala.collection.mutable
import scala.scalajs.js

/** The drainage channel excavation, ported 1:1 from upstream `src/world/landform.js`.
  *
  * The channel goes *into* the ground rather than onto it: the faces inside the
  * footprint are removed from the terrain grid and the planet sphere, leaving a
  * clean hole whose edges stay at natural grade, and the channel's own concrete
  * seals it. The channel runs between the two hill ranges rather than circling
  * the planet; `hills.js` reads the bounds from here so its corridor matches.
  */
object Landform:

  final case class Canal(z: Double, half: Double, depth: Double)

  val canal: Canal = Canal(z = -24.0, half = 2.5, depth = 1.75)

  val CanalX0: Double = -98.0
  val CanalX1: Double = 106.0

  val FootprintHalf: Double = 3.4

  def inTrench(x: Double, z: Double): Boolean =
    x > CanalX0 && x < CanalX1 && math.abs(z - canal.z) < FootprintHalf

  /** Drop every triangle with a vertex inside the excavation; "any vertex" rather
    * than the centroid guarantees no surviving triangle reaches into the
    * footprint. Works on indexed and non-indexed geometry alike; returns the
    * number of triangles dropped.
    */
  def cutTrench(geometry: BufferGeometry[?, ?]): Int =
    val position = ThreeInterop.positionAttribute(geometry)
    val index    = geometry.getIndex()

    def inside(vertex: Int): Boolean =
      inTrench(position.getX(vertex.toDouble), position.getZ(vertex.toDouble))

    if index != null then
      val source = ThreeInterop.readAttributeDoubles(index, index.count.toInt)
      val keep   = mutable.ArrayBuilder.make[Double]
      var t      = 0
      var kept   = 0
      while t < source.length do
        if !(inside(source(t).toInt) || inside(source(t + 1).toInt) || inside(source(t + 2).toInt)) then
          keep += source(t)
          keep += source(t + 1)
          keep += source(t + 2)
          kept += 3
        t += 3
      geometry.setIndex(js.Array(keep.result()*))
      source.length / 3 - kept / 3
    else
      val names   = ThreeInterop.attributeNames(geometry)
      val rows    = names.map(name => (name, ThreeInterop.attributeDoubles(geometry, name))).toMap
      val sizes   = names.map(name => (name, ThreeInterop.attributeItemSize(geometry, name).toInt)).toMap
      val out     = names.map(name => (name, mutable.ArrayBuilder.make[Double])).toMap
      var dropped = 0
      var t       = 0
      val count   = position.count.toInt
      while t < count do
        if inside(t) || inside(t + 1) || inside(t + 2) then dropped += 1
        else
          names.foreach { name =>
            val size = sizes(name)
            val row  = rows(name)
            (0 until 3).foreach { corner =>
              (0 until size).foreach(k => out(name) += row((t + corner) * size + k))
            }
          }
        t += 3
      names.foreach { name =>
        ThreeInterop.setFloatAttribute(geometry, name, out(name).result(), sizes(name))
      }
      dropped
    end if
  end cutTrench
end Landform
