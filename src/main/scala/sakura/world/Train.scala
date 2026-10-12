package sakura.world

import sakura.ThreeInterop
import sakura.core.{Pal, Textures, Toon, Util}
import vendor.three.*

import scala.collection.mutable

/** A three-car suburban EMU, ported 1:1 from upstream `src/world/train.js`.
  *
  * Cream body, blue waist stripe, dark strip windows. Interiors are painted
  * rather than modelled -- flat silhouette bands and a soft highlight sit
  * directly on the glass, which is how a background artist would draw a train
  * going past.
  */
object Train:

  private val CarLength    = 19.4
  private val CarWidth     = 2.86
  private val Pitch        = 20.1
  private val Floor        = 1.06
  private val Top          = 3.74
  private val Roof         = 3.96
  private val ContactWireY = 4.88

  // Window bays for one car side: (centreX, width)
  private val Doors = Vector(-7.0, -2.4, 2.4, 7.0)
  private val DoorW = 1.32
  private val Bays  = Vector((-8.5, 1.7), (-4.7, 3.2), (0.0, 3.4), (4.7, 3.2), (8.5, 1.7))

  private val materials = mutable.HashMap.empty[String, Material[?]]

  private def initMaterials(): Unit =
    if materials.contains("body") then ()
    else
      materials("body") = Toon.cel(Toon.CelOptions(color = Pal.trainBody, bands = 3, tint = 0x6f6796))
      materials("bodyShade") = Toon.cel(Toon.CelOptions(color = Pal.trainBodyShade, bands = 3, tint = 0x6f6796))
      materials("stripe") = Toon.cel(Toon.CelOptions(color = Pal.trainStripe, bands = 3, tint = 0x4a4a92))
      materials("stripe2") = Toon.cel(Toon.CelOptions(color = Pal.trainStripe2, bands = 3, tint = 0x3f5a8a))
      materials("roof") = Toon.cel(Toon.CelOptions(color = Pal.trainRoof, bands = 3, tint = 0x60597f))
      materials("skirt") = Toon.cel(Toon.CelOptions(color = Pal.trainSkirt, bands = 3, tint = 0x5b5480))
      materials("window") = Toon.flat(Toon.FlatOptions(color = Pal.trainWindow))
      materials("windowLit") = Toon.flat(Toon.FlatOptions(color = Pal.trainWindowLit))
      materials("door") = Toon.cel(Toon.CelOptions(color = Pal.trainDoor, bands = 3, tint = 0x6f6796))
      materials("dark") = Toon.cel(Toon.CelOptions(color = Pal.black, bands = 2, tint = 0x4b4560))
      materials("metal") = Toon.cel(Toon.CelOptions(color = Pal.metalDark, bands = 3, tint = 0x5c5680))
      materials("wheel") = Toon.cel(Toon.CelOptions(color = 0x4a4552, bands = 2, tint = 0x4b4560))
      materials("headlight") = Toon.flat(Toon.FlatOptions(color = 0xfff6da))
      materials("tail") = Toon.flat(Toon.FlatOptions(color = 0xff5a4a))

  /** Glass sits *outside* the window frame, not level with it -- otherwise the
    * frame box wins the depth test and the side of the train reads blank.
    */
  private def addGlass(group: Group[Object3DEventMap], cx: Double, w: Double, sz: Double, rng: Util.RngKit): Unit =
    val y0    = 2.16
    val y1    = 3.16
    val zz    = sz * (CarWidth / 2.0 + 0.032)
    val glass = new Mesh(new PlaneGeometry(w, y1 - y0), materials("window"))
    glass.position.set(cx, (y0 + y1) / 2.0, zz)
    glass.rotation.y = if sz > 0 then 0.0 else math.Pi
    ThreeInterop.setUserDataFlag(glass, "noOutline")
    ThreeInterop.addChild(group, glass)

    /* What you see through a train window, with nobody on the train: the lit
     * ceiling strip, the grab rail, and the dark band of seat backs.  Three
     * horizontal bands, no figures.  `rng` is still drawn from so the seeded
     * sequence -- and every other random decision on the train -- is unchanged. */
    rng.next()
    val height = y1 - y0
    def band(yFrac: Double, hh: Double, color: Int, off: Double): Mesh[?, ?, ?] =
      val b = new Mesh(new PlaneGeometry(w - 0.06, hh), Toon.flat(Toon.FlatOptions(color = color)))
      b.position.set(cx, y0 + height * yFrac, zz + sz * off)
      b.rotation.y = if sz > 0 then 0.0 else math.Pi
      ThreeInterop.setUserDataFlag(b, "noOutline")
      ThreeInterop.addChild(group, b)
      b
    band(0.14, height * 0.28, 0x515a72, 0.018) // seat backs
    band(0.30, 0.035, 0x9fa8bb, 0.021)         // the top edge of the seat run
    band(0.72, 0.045, 0xb9c0cc, 0.021)         // grab rail
    band(0.93, 0.07, 0xf0ead8, 0.018)          // ceiling light strip

    // one flat diagonal highlight, the animator's shorthand for glass
    val hi = new Mesh(
      new PlaneGeometry(w * 0.2, (y1 - y0) * 0.95),
      Toon.flat(Toon.FlatOptions(color = 0xdfeaf6, transparent = true, opacity = 0.13,
        depthWrite = Some(false)))
    )
    hi.position.set(cx - w * 0.2, (y0 + y1) / 2.0, zz + sz * 0.012)
    hi.rotation.set(0.0, if sz > 0 then 0.0 else math.Pi, 0.24)
    ThreeInterop.setUserDataFlag(hi, "noOutline")
    ThreeInterop.addChild(group, hi)
    ()
  end addGlass

  private def buildCar(
    cab: Boolean,
    tail: Boolean,
    rng: Util.RngKit
  ): (Group[Object3DEventMap], Vector[Group[Object3DEventMap]]) =
    val car        = new Group()
    val bodyHeight = Top - Floor
    val parts      = mutable.HashMap.empty[String, mutable.ArrayBuffer[Util.BakePart]]
    List("body", "stripe", "roof", "skirt", "door", "dark", "metal").foreach(key =>
      parts(key) = mutable.ArrayBuffer.empty[Util.BakePart]
    )

    parts("body") += Util.BakePart(new BoxGeometry(CarLength, bodyHeight, CarWidth),
      Some(Util.trs(0.0, (Floor + Top) / 2.0, 0.0)))

    // roof: slightly inset, flatter tone
    parts("roof") += Util.BakePart(new BoxGeometry(CarLength - 0.1, Roof - Top, CarWidth - 0.24),
      Some(Util.trs(0.0, (Top + Roof) / 2.0, 0.0)))
    Vector(-1.0, 1.0).foreach { s =>
      parts("roof") += Util.BakePart(new BoxGeometry(CarLength - 0.05, 0.07, 0.1),
        Some(Util.trs(0.0, Top + 0.02, s * (CarWidth / 2.0 - 0.06))))
    }

    // waist stripe, wrapped round the whole body
    val stripeY = 1.92
    val stripeH = 0.34
    parts("stripe") += Util.BakePart(new BoxGeometry(CarLength + 0.02, stripeH, CarWidth + 0.03),
      Some(Util.trs(0.0, stripeY, 0.0)))
    parts("stripe") += Util.BakePart(new BoxGeometry(CarLength + 0.02, 0.07, CarWidth + 0.04),
      Some(Util.trs(0.0, stripeY - stripeH / 2.0 - 0.055, 0.0)))

    // underframe + skirt
    parts("skirt") += Util.BakePart(new BoxGeometry(CarLength - 0.3, 0.5, CarWidth - 0.34),
      Some(Util.trs(0.0, Floor - 0.25, 0.0)))
    parts("skirt") += Util.BakePart(new BoxGeometry(CarLength - 1.6, 0.28, CarWidth - 0.8),
      Some(Util.trs(0.0, Floor - 0.52, 0.0)))

    // doors and glass
    Vector(1.0, -1.0).foreach { sz =>
      Doors.foreach { dx =>
        parts("door") += Util.BakePart(new BoxGeometry(DoorW, Top - Floor - 0.12, 0.05),
          Some(Util.trs(dx, (Floor + Top) / 2.0 - 0.02, sz * (CarWidth / 2.0 + 0.012))))
        parts("dark") += Util.BakePart(new BoxGeometry(0.05, Top - Floor - 0.12, 0.06),
          Some(Util.trs(dx, (Floor + Top) / 2.0 - 0.02, sz * (CarWidth / 2.0 + 0.02))))
        addGlass(car, dx, 0.94, sz, rng)
      }
      Bays.foreach { case (bx, bw) =>
        // frame first, sitting flush with the body; glass goes on top of it
        parts("metal") += Util.BakePart(new BoxGeometry(bw + 0.12, 1.14, 0.035),
          Some(Util.trs(bx, 2.66, sz * (CarWidth / 2.0 + 0.008))))
        addGlass(car, bx, bw, sz, rng)
      }
    }

    // bogies
    Vector(-6.3, 6.3).foreach { bx =>
      parts("metal") += Util.BakePart(new BoxGeometry(2.9, 0.42, CarWidth - 0.9),
        Some(Util.trs(bx, 0.78, 0.0)))
      parts("dark") += Util.BakePart(new BoxGeometry(3.3, 0.2, 0.28),
        Some(Util.trs(bx, 0.62, 0.0)))
    }

    // roof equipment
    Vector(-5.6, -1.4, 3.2, 7.4).foreach { rx =>
      parts("roof") += Util.BakePart(new BoxGeometry(2.1, 0.3, 1.5),
        Some(Util.trs(rx, Roof + 0.13, if rx % 2 == 0 then 0.18 else -0.18)))
    }
    Vector(-8.2, 0.6, 8.6).foreach { rx =>
      parts("metal") += Util.BakePart(new BoxGeometry(0.7, 0.16, 0.7),
        Some(Util.trs(rx, Roof + 0.07, -0.7)))
    }

    /* ------------------------------- cab end ------------------------------- */
    if cab || tail then
      val s  = if cab then 1.0 else -1.0
      val fx = s * (CarLength / 2.0)
      // black mask around the windscreen
      parts("dark") += Util.BakePart(new BoxGeometry(0.1, 1.34, CarWidth - 0.22),
        Some(Util.trs(fx + s * 0.03, 2.86, 0.0)))
      // windscreen: two panes with a centre pillar
      Vector(-0.72, 0.72).foreach { wz =>
        val pane = new Mesh(new PlaneGeometry(1.16, 1.02), materials("window"))
        pane.position.set(fx + s * 0.085, 2.88, wz)
        pane.rotation.y = if s > 0 then math.Pi / 2.0 else -math.Pi / 2.0
        ThreeInterop.setUserDataFlag(pane, "noOutline")
        ThreeInterop.addChild(car, pane)
        val hi = new Mesh(
          new PlaneGeometry(0.3, 0.98),
          Toon.flat(Toon.FlatOptions(color = 0xe4eef8, transparent = true, opacity = 0.16,
            depthWrite = Some(false)))
        )
        hi.position.set(fx + s * 0.095, 2.88, wz - 0.26)
        hi.rotation.set(0.0, if s > 0 then math.Pi / 2.0 else -math.Pi / 2.0, 0.26)
        ThreeInterop.setUserDataFlag(hi, "noOutline")
        ThreeInterop.addChild(car, hi)
      }
      parts("body") += Util.BakePart(new BoxGeometry(0.12, 1.4, 0.16),
        Some(Util.trs(fx + s * 0.05, 2.86, 0.0)))

      // destination board
      val dest = new Mesh(
        new PlaneGeometry(1.5, 0.38),
        Toon.flat(Toon.FlatOptions(color = 0xffffff, map = Some(Textures.trainDest()), cache = false))
      )
      dest.position.set(fx + s * 0.09, 3.52, 0.0)
      dest.rotation.y = if s > 0 then math.Pi / 2.0 else -math.Pi / 2.0
      ThreeInterop.setUserDataFlag(dest, "noOutline")
      ThreeInterop.addChild(car, dest)
      parts("dark") += Util.BakePart(new BoxGeometry(0.08, 0.5, 1.66),
        Some(Util.trs(fx + s * 0.04, 3.52, 0.0)))

      // head / tail lights
      Vector(-1.06, 1.06).foreach { lz =>
        parts("dark") += Util.BakePart(new BoxGeometry(0.14, 0.42, 0.5),
          Some(Util.trs(fx + s * 0.05, 1.55, lz)))
        val lamp = new Mesh(new PlaneGeometry(0.34, 0.16),
          if cab then materials("headlight") else materials("tail"))
        lamp.position.set(fx + s * 0.13, 1.63, lz)
        lamp.rotation.y = if s > 0 then math.Pi / 2.0 else -math.Pi / 2.0
        ThreeInterop.setUserDataFlag(lamp, "noOutline")
        ThreeInterop.addChild(car, lamp)
        val lamp2 = new Mesh(new PlaneGeometry(0.34, 0.14),
          if cab then materials("tail") else materials("headlight"))
        lamp2.position.set(fx + s * 0.13, 1.44, lz)
        lamp2.rotation.y = if s > 0 then math.Pi / 2.0 else -math.Pi / 2.0
        ThreeInterop.setUserDataFlag(lamp2, "noOutline")
        ThreeInterop.addChild(car, lamp2)
      }

      // front skirt + coupler
      parts("skirt") += Util.BakePart(new BoxGeometry(0.34, 0.78, CarWidth - 0.5),
        Some(Util.trs(fx + s * 0.1, 0.82, 0.0)))
      parts("dark") += Util.BakePart(new BoxGeometry(0.5, 0.22, 0.34),
        Some(Util.trs(fx + s * 0.25, 0.62, 0.0)))

      // car number plate
      val plateNumber = new Mesh(
        new PlaneGeometry(0.8, 0.24),
        Toon.flat(Toon.FlatOptions(color = 0xffffff, map = Some(Textures.trainNumber()), cache = false))
      )
      plateNumber.position.set(fx - s * 1.4, 1.42, CarWidth / 2.0 + 0.02)
      ThreeInterop.setUserDataFlag(plateNumber, "noOutline")
      ThreeInterop.addChild(car, plateNumber)
    end if

    /* ----------------------------- merged meshes ----------------------------- */
    val materialFor = Map("body" -> "body", "stripe" -> "stripe", "roof" -> "roof",
      "skirt" -> "skirt", "door" -> "door", "dark" -> "dark", "metal" -> "metal")
    materialFor.foreach { (key, materialKey) =>
      val list = parts(key)
      if list.nonEmpty then
        val mesh = new Mesh(Util.bake(list.toList), materials(materialKey))
        mesh.castShadow = true
        mesh.receiveShadow = true
        ThreeInterop.addChild(car, mesh)
        if key == "body" || key == "roof" || key == "skirt" then
          sakura.core.Outline.hullOutline(mesh, sakura.core.Outline.OutlineOptions(thickness = 0.0034))
    }

    /* -------------------------------- wheels --------------------------------
     * The hub is `planetRigid` so the bake re-seats it and leaves the rig; the
     * axle inside is what spins. */
    val wheels        = mutable.ArrayBuffer.empty[Group[Object3DEventMap]]
    val wheelGeometry = new CylinderGeometry(0.43, 0.43, 0.14, 12.0)
    wheelGeometry.rotateX(math.Pi / 2.0)
    Vector(-6.3, 6.3).foreach { bx =>
      Vector(-1.05, 1.05).foreach { wx =>
        Vector(-0.72, 0.72).foreach { wz =>
          val hub = new Group()
          hub.position.set(bx + wx, Railway.RailTop + 0.43, wz)
          ThreeInterop.setUserDataFlag(hub, "planetRigid")
          val axle = new Group()
          ThreeInterop.addChild(hub, axle)
          val wheel = new Mesh(wheelGeometry, materials("wheel"))
          wheel.castShadow = true
          ThreeInterop.addChild(axle, wheel)
          wheels += axle
          ThreeInterop.addChild(car, hub)
        }
      }
    }

    /* ------------------------------ pantograph ------------------------------ */
    if cab || tail then
      val pantograph = new Group()
      pantograph.position.set(if cab then -4.0 else 4.0, Roof + 0.05, 0.0)
      pantograph.scale.y = (ContactWireY - pantograph.position.y) / 1.64
      ThreeInterop.addChild(pantograph, Util.box(1.5, 0.08, 1.5, materials("metal"), 0.0, 0.04, 0.0))
      Vector(-1.0, 1.0).foreach { s =>
        val lower = Util.box(0.06, 0.9, 0.06, materials("metal"), s * 0.35, 0.5, 0.0)
        lower.rotation.z = s * 0.55
        ThreeInterop.addChild(pantograph, lower)
        val upper = Util.box(0.05, 0.78, 0.05, materials("metal"), s * 0.0575, 1.247, 0.0)
        upper.rotation.z = s * 0.148
        ThreeInterop.addChild(pantograph, upper)
      }
      ThreeInterop.addChild(pantograph, Util.box(0.1, 0.06, 1.3, materials("dark"), 0.0, 1.6, 0.0))
      ThreeInterop.addChild(pantograph, Util.box(0.24, 0.05, 1.34, materials("metal"), 0.0, 1.64, 0.0))
      pantograph.traverse((node: Object3D[Object3DEventMap]) =>
        if ThreeInterop.isMesh(node) then node.castShadow = true
      )
      ThreeInterop.addChild(car, pantograph)

    (car, wheels.toVector)
  end buildCar

  final class TrainApi(
    val group: Group[Object3DEventMap],
    val cars: Vector[Group[Object3DEventMap]],
    val wheels: Vector[Group[Object3DEventMap]],
    val length: Double,
    val speed: Double
  ):
    var dir: Double = 1.0
    var x: Double   = 0.0
    /** normalised progress, used by the petal wind */
    var gust: Double = 0.0

    /** distance along the track from the crossing, signed, shortest way round */
    def offset: Double = Planet.wrapDelta(x, 0.0)

    private val toCenter   = new Matrix4().makeTranslation(Planet.Center.x, Planet.Center.y, Planet.Center.z)
    private val fromCenter = new Matrix4().makeTranslation(-Planet.Center.x, -Planet.Center.y, -Planet.Center.z)

    /** Called once after the world is projected onto the planet. */
    def planetize(): Unit =
      group.visible = true
      group.matrixAutoUpdate = false
      update(0.0)

    def update(dt: Double): Unit =
      x = Planet.wrapX(x + dir * speed * dt)

      // spin about the planet axis: T(C) · Rz(-x/R) · T(-C)
      group.matrix
        .makeRotationZ(-x / Planet.R)
        .premultiply(toCenter)
        .multiply(fromCenter)
      group.matrixWorldNeedsUpdate = true

      val spin = (speed * dt) / 0.43
      wheels.foreach(wheel => wheel.rotation.z -= spin * dir)

      // the wind that shoves the petals about as it goes through
      val near = math.max(0.0, 1.0 - math.abs(offset) / 46.0)
      gust = math.max(gust * math.exp(-dt * 1.4), near * near)
  end TrainApi

  def buildTrain(ctx: WorldCtx): TrainApi =
    initMaterials()
    val rng   = Util.rngKit(5150)
    val group = new Group()
    group.name = "train"
    group.visible = false
    ctx.add(group)

    val allWheels = mutable.ArrayBuffer.empty[Group[Object3DEventMap]]
    val cars      = mutable.ArrayBuffer.empty[Group[Object3DEventMap]]
    (0 until 3).foreach { i =>
      val (car, carWheels) = buildCar(cab = i == 0, tail = i == 2, rng)
      car.position.x = (i - 1) * Pitch
      ThreeInterop.addChild(group, car)
      cars += car
      allWheels ++= carWheels
    }

    TrainApi(group = group, cars = cars.toVector, wheels = allWheels.toVector,
      length = Pitch * 3, speed = 23.5)
  end buildTrain
end Train
