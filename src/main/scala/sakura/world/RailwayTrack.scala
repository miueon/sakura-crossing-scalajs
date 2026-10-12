package sakura.world

import scala.math

import sakura.Palette
import sakura.PlanetCurvature
import sakura.ToonMaterial
import vendor.three.*

object RailwayTrack:
  val Gauge: Double              = 1.067
  val Length: Double             = 1005.0
  val MaxSegmentLength: Double   = 5.0
  val SleeperSpacing: Double    = 0.60
  val SleeperLength: Double     = 2.10
  val SleeperWidth: Double      = 0.20
  val SleeperHeight: Double     = 0.14
  val BallastWidth: Double      = 3.40
  val BallastDepth: Double      = 0.25
  val RailWidth: Double         = 0.07
  val RailHeight: Double        = 0.15
  val RailHeadTop: Double       = 0.285
  val RoadCrossingWidth: Double = 6.3
  val RoadCrossingHeight: Double = 0.275

  private val RailCenterHeight = RailHeadTop - RailHeight / 2.0
  private val SleeperCenterY   = SleeperHeight / 2.0
  private val BallastCenterY   = -BallastDepth / 2.0 + 0.005 / 2.0
  private val RailOffsets      = List(-Gauge / 2.0, Gauge / 2.0)

  def build(
    curvature: PlanetCurvature,
    groundAt: (Double, Double) => Double,
    offsetX: Double
  ): SceneAssembly =
    val ballastBuilder = new GeometryBuilder()
    val sleeperBuilder = new GeometryBuilder()
    val railBuilder    = new GeometryBuilder()
    val plateBuilder   = new GeometryBuilder()

    val segmentCount  = math.ceil(Length / MaxSegmentLength).toInt
    val segmentLength = Length / segmentCount
    (0 until segmentCount).foreach { index =>
      val x = offsetX - Length / 2.0 + (index + 0.5) * segmentLength
      ballastBuilder.addBox(x, groundAt(x, 0.0) + BallastCenterY, 0.0, segmentLength, BallastDepth, BallastWidth)
      RailOffsets.foreach { railZ =>
        railBuilder.addBox(
          x,
          groundAt(x, railZ) + RailCenterHeight,
          railZ,
          segmentLength,
          RailHeight,
          RailWidth
        )
      }
    }

    val sleeperCount = math.round(Length / SleeperSpacing).toInt
    (0 until sleeperCount).foreach { index =>
      val x = offsetX - Length / 2.0 + (index + 0.5) * SleeperSpacing
      sleeperBuilder.addBox(
        x,
        groundAt(x, 0.0) + SleeperCenterY,
        0.0,
        SleeperWidth,
        SleeperHeight,
        SleeperLength
      )
    }

    val stoneSpacing = 0.8
    val stoneRows    = math.ceil(Length / stoneSpacing).toInt
    (0 until stoneRows).foreach { row =>
      val x = offsetX - Length / 2.0 + (row + 0.5) * Length / stoneRows
      List(-1.48, -1.22, 1.22, 1.48).zipWithIndex.foreach { case (baseZ, column) =>
        val seed   = row * 4 + column
        val jitter = sample(seed)
        val z      = baseZ + (sample(seed + 1) - 0.5) * 0.12
        val width  = 0.16 + jitter * 0.12
        val height = 0.05 + sample(seed + 2) * 0.06
        val depth  = 0.15 + sample(seed + 3) * 0.11
        ballastBuilder.addTetrahedron(
          x + (sample(seed + 4) - 0.5) * 0.18,
          groundAt(x, z) + 0.006,
          z,
          width,
          height,
          depth,
          sample(seed + 5) - 0.5,
          sample(seed + 6) - 0.5
        )
      }
    }

    val plateSegments = math.ceil(RoadCrossingWidth / MaxSegmentLength).toInt
    val plateLength   = RoadCrossingWidth / plateSegments
    (0 until plateSegments).foreach { segment =>
      val x = offsetX - RoadCrossingWidth / 2.0 + (segment + 0.5) * plateLength
      RailOffsets.foreach { railZ =>
        List(-1.0, 1.0).foreach { side =>
          val plateZ = railZ + side * (RailWidth / 2.0 + 0.07)
          plateBuilder.addBox(
            x,
            groundAt(x, plateZ) + RailHeadTop - 0.01,
            plateZ,
            plateLength,
            0.02,
            0.14
          )
        }
      }
    }

    val ballastGeometry = ballastBuilder.result()
    val sleeperGeometry = sleeperBuilder.result()
    val railGeometry    = railBuilder.result()
    val plateGeometry   = plateBuilder.result()

    val ballastMaterial = ToonMaterial.celMaterial(Palette.concrete, curvature)
    val sleeperMaterial = ToonMaterial.celMaterial(Palette.timber, curvature)
    val railMaterial    = ToonMaterial.celMaterial(Palette.railSteel, curvature)
    val plateMaterial   = ToonMaterial.celMaterial(Palette.concrete, curvature)

    val group = new Group()
    group.add(new Mesh(ballastGeometry, ballastMaterial))
    group.add(new Mesh(sleeperGeometry, sleeperMaterial))
    group.add(new Mesh(railGeometry, railMaterial))
    group.add(new Mesh(plateGeometry, plateMaterial))

    SceneAssembly(
      group,
      List(ballastGeometry, sleeperGeometry, railGeometry, plateGeometry),
      List(ballastMaterial, sleeperMaterial, railMaterial, plateMaterial)
    )

  private def sample(seed: Int): Double =
    val raw = math.sin(seed * 12.9898 + 78.233) * 43758.5453
    raw - math.floor(raw)
end RailwayTrack
