package sakura.world

import scala.math

import sakura.Palette
import sakura.PlanetCurvature
import sakura.ToonMaterial
import vendor.three.*

object CatenaryStructure:
  val ContactHeight: Double   = 5.0
  val MessengerHeight: Double = 5.95
  val MastHeight: Double      = 6.6
  val MaxWireSegment: Double  = 5.0

  private val MastSpan       = 50.25
  private val MastSideOffset = 2.4
  private val CrossarmHalf   = 3.1

  def build(
    curvature: PlanetCurvature,
    groundAt: (Double, Double) => Double,
    offsetX: Double
  ): SceneAssembly =
    val steelBuilder = new GeometryBuilder()
    val mastCount    = math.ceil(RailwayTrack.Length / MastSpan).toInt
    val mastSpacing  = RailwayTrack.Length / mastCount

    (0 until mastCount).foreach { index =>
      val x    = offsetX - RailwayTrack.Length / 2.0 + (index + 0.5) * mastSpacing
      val base = groundAt(x, 0.0)
      List(-MastSideOffset, MastSideOffset).foreach { z =>
        val foot = groundAt(x, z)
        steelBuilder.addBeam(x, foot, z, x, base + MastHeight, z, 0.14)
      }
      steelBuilder.addBeam(
        x,
        base + MastHeight - 0.25,
        -CrossarmHalf,
        x,
        base + MastHeight - 0.25,
        CrossarmHalf,
        0.14
      )
      steelBuilder.addBeam(x, base + 0.3, -MastSideOffset, x, base + 5.8, MastSideOffset, 0.09)
      steelBuilder.addBeam(x, base + 0.3, MastSideOffset, x, base + 5.8, -MastSideOffset, 0.09)
      steelBuilder.addBeam(x, base + 5.8, 0.0, x, base + MessengerHeight, 0.0, 0.06)
    }

    val wireSegments = math.ceil(RailwayTrack.Length / MaxWireSegment).toInt
    val wireStep     = RailwayTrack.Length / wireSegments
    (0 until wireSegments).foreach { index =>
      val x0 = offsetX - RailwayTrack.Length / 2.0 + index * wireStep
      val x1 = x0 + wireStep
      val y0 = groundAt(x0, 0.0)
      val y1 = groundAt(x1, 0.0)
      steelBuilder.addBeam(x0, y0 + ContactHeight, 0.0, x1, y1 + ContactHeight, 0.0, 0.035)
      steelBuilder.addBeam(x0, y0 + MessengerHeight, 0.0, x1, y1 + MessengerHeight, 0.0, 0.035)
      steelBuilder.addBeam(x0, y0 + ContactHeight, 0.0, x0, y0 + MessengerHeight, 0.0, 0.025)
    }

    val steelGeometry = steelBuilder.result()
    val steelMaterial = ToonMaterial.celMaterial(Palette.railSteel, curvature)
    val group         = new Group()
    group.add(new Mesh(steelGeometry, steelMaterial))

    SceneAssembly(group, List(steelGeometry), List(steelMaterial))
end CatenaryStructure
