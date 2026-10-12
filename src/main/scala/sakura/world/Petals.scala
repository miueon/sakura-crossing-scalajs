package sakura.world

import sakura.ThreeInterop
import sakura.core.{Pal, Textures, Toon, Util}
import vendor.three.*

import scala.collection.mutable

/** Falling blossom, ported 1:1 from upstream `src/world/petals.js`.
  *
  * Petals drift down in slow overlapping waves, tumbling on their own axis.
  * Density is deliberately restrained -- enough to catch the light across the
  * road and the tracks, never enough to obscure the frame. The whole field
  * takes a sideways shove when the train goes past.
  */
object Petals:

  private val Count = 980
  private val Top   = 6.8
  private val Z0    = -30.0
  private val Z1    = 34.0
  private val Half  = 9.5

  final private case class Petal(
    mesh: InstancedMesh[?, ?, ?],
    index: Int,
    var x: Double,
    var y: Double,
    var z: Double,
    fall: Double,
    swayAmp: Double,
    swayFreq: Double,
    var phase: Double,
    spin: Vector3,
    spinRate: Double,
    var angle: Double,
    scale: Double,
    drift: Double
  )

  final case class PetalsApi(update: (Double, Double, Double) => Unit, meshes: Vector[InstancedMesh[?, ?, ?]])

  def buildPetals(ctx: WorldCtx): PetalsApi =
    val rng = Util.rngKit(8123)
    val tex = Textures.petalTex()

    val geometry = new PlaneGeometry(0.185, 0.135)
    val groups = Vector(
      (Pal.petal, math.round(Count * 0.55).toDouble), (Pal.blossomLight, math.round(Count * 0.28).toDouble),
      (Pal.petalDeep, (Count - math.round(Count * 0.55) - math.round(Count * 0.28)).toDouble)
    )

    val meshes = mutable.ArrayBuffer.empty[InstancedMesh[?, ?, ?]]
    val petals = mutable.ArrayBuffer.empty[Petal]
    groups.foreach { case (color, n) =>
      val material = Toon.flat(Toon.FlatOptions(color = color, map = Some(tex),
        transparent = true, opacity = 0.95, depthWrite = Some(false), side = 2, alphaTest = 0.32, cache = false))
      val inst = new InstancedMesh(geometry, material, n)
      inst.instanceMatrix.setUsage(35048)
      inst.frustumCulled = false
      inst.renderOrder = 4.0
      ThreeInterop.setUserDataFlag(inst, "noOutline")
      ctx.add(inst)
      meshes += inst
      (0 until n.toInt).foreach { i =>
        petals += Petal(
          mesh = inst,
          index = i,
          x = rng.range(-Half, Half),
          y = rng.range(0.2, Top),
          z = rng.range(Z0, Z1),
          fall = rng.range(0.42, 0.86),
          swayAmp = rng.range(0.25, 0.75),
          swayFreq = rng.range(0.5, 1.35),
          phase = rng.range(0.0, 10.0),
          spin = new Vector3(rng.range(-1.0, 1.0), rng.range(-1.0, 1.0), rng.range(-1.0, 1.0)).normalize(),
          spinRate = rng.range(0.5, 2.4),
          angle = rng.range(0.0, 6.28),
          scale = rng.range(0.78, 1.25),
          drift = rng.range(-0.16, 0.16)
        )
      }
    }

    val dummy  = new Object3D()
    val q      = new Quaternion()
    val scaleV = new Vector3()
    var t      = 0.0

    def respawn(petal: Petal): Unit =
      petal.x = rng.range(-Half, Half)
      petal.z = rng.range(Z0, Z1)
      petal.y = Top + rng.range(0.0, 1.4)
      petal.phase = rng.range(0.0, 10.0)

    def update(dt: Double, gust: Double, gustDir: Double): Unit =
      t += dt
      val wind = gust * 5.4 * gustDir
      val lift = gust * 1.5
      petals.foreach { petal =>
        // large slow wave + small fast flutter: reads as air, not noise
        val sway  = math.sin(t * petal.swayFreq + petal.phase)
        val sway2 = math.sin(t * petal.swayFreq * 2.7 + petal.phase * 1.7)
        petal.y -= (petal.fall + gust * 0.4) * dt
        petal.x += (petal.swayAmp * sway * 0.55 + petal.drift + wind * 0.24) * dt
        petal.z += (petal.swayAmp * sway2 * 0.32 + wind * 0.05) * dt
        petal.y += lift * math.max(0.0, 1.0 - math.abs(petal.z) / 8.0) * dt
        petal.angle += petal.spinRate * dt * (1.0 + gust)

        val cx = Street.centerX(petal.z)
        if petal.x < cx - Half then petal.x = cx + Half
        if petal.x > cx + Half then petal.x = cx - Half
        if petal.z < Z0 then petal.z = Z1
        if petal.z > Z1 then petal.z = Z0
        if petal.y < Street.groundY(petal.z) + 0.04 then respawn(petal)

        q.setFromAxisAngle(ThreeInterop.asVector3Like(petal.spin), petal.angle)
        dummy.position.set(petal.x, petal.y, petal.z)
        dummy.quaternion.set(q.x, q.y, q.z, q.w)
        scaleV.setScalar(petal.scale)
        dummy.scale.copy(ThreeInterop.asVector3Like(scaleV))
        dummy.updateMatrix()
        petal.mesh.setMatrixAt(petal.index.toDouble, dummy.matrix)
      }
      meshes.foreach(mesh => ThreeInterop.markForUpload(mesh.instanceMatrix))
      ()
    end update

    // settle the field so the very first frame already has petals mid-air
    (0 until 40).foreach(_ => update(0.1, 0.0, 1.0))

    buildFallenPetals(ctx, tex)
    PetalsApi(update, meshes.toVector)
  end buildPetals

  /** Fallen blossom over arbitrary patches, for the districts away from the
    * street: each tone is one instanced mesh, so a dozen scattered drifts still
    * cost three draw calls.
    */
  def buildFallenPatches(ctx: WorldCtx, patches: Vector[PetalPatch]): Boolean =
    if patches.isEmpty then false
    else
      val rng      = Util.rngKit(6619)
      val tex      = Textures.petalTex()
      val geometry = new PlaneGeometry(0.17, 0.125)
      geometry.rotateX(-math.Pi / 2.0)
      val tones = Vector(Pal.petal, Pal.blossomLight, Pal.petalDeep)
      val lists = Vector.fill(3)(mutable.ArrayBuffer.empty[Matrix4])
      val dummy = new Object3D()

      patches.foreach { patch =>
        (0 until patch.n).foreach { _ =>
          // biased to the edges: wind pushes petals against kerbs and walls
          val u      = rng.next()
          val edge   = if u < 0.45 then rng.sign * rng.range(0.34, 0.5) else rng.range(-0.34, 0.34)
          val other  = rng.range(-0.48, 0.48)
          val alongX = rng.chance(0.5)
          dummy.position.set(
            patch.x + (if alongX then other else edge) * patch.w,
            patch.y + 0.021,
            patch.z + (if alongX then edge else other) * patch.d
          )
          dummy.rotation.set(0.0, rng.range(0.0, 6.28), 0.0)
          val s = rng.range(0.8, 1.3)
          dummy.scale.set(s, 1.0, s)
          dummy.updateMatrix()
          lists(rng.int(0, 2)) += dummy.matrix.clone()
        }
      }

      lists.zipWithIndex.foreach { case (list, i) =>
        if list.nonEmpty then
          val inst = new InstancedMesh(geometry,
            Toon.flat(Toon.FlatOptions(color = tones(i), map = Some(tex), transparent = true,
              opacity = 0.9, depthWrite = Some(false), alphaTest = 0.32, cache = false)),
            list.length.toDouble)
          list.zipWithIndex.foreach { case (m, k) => inst.setMatrixAt(k.toDouble, m) }
          inst.renderOrder = 2.0
          ThreeInterop.setUserDataFlag(inst, "noOutline")
          ctx.add(inst)
      }
      true

  /** Petals that have already landed: they drift into the gutters, along the
    * kerb line and across the crossing deck.
    */
  private def buildFallenPetals(ctx: WorldCtx, tex: Texture[?, ?]): Unit =
    val rng      = Util.rngKit(4471)
    val geometry = new PlaneGeometry(0.17, 0.125)
    geometry.rotateX(-math.Pi / 2.0)

    val tones = Vector(Pal.petal, Pal.blossomLight, Pal.petalDeep)
    val lists = Vector.fill(3)(mutable.ArrayBuffer.empty[Matrix4])
    val dummy = new Object3D()

    def place(x: Double, z: Double, y: Double): Unit =
      dummy.position.set(x, y + 0.019, z)
      dummy.rotation.set(0.0, rng.range(0.0, 6.28), 0.0)
      val s = rng.range(0.8, 1.25)
      dummy.scale.set(s, 1.0, s)
      dummy.updateMatrix()
      lists(rng.int(0, 2)) += dummy.matrix.clone()

    (0 until 620).foreach { _ =>
      val z  = rng.range(-26.0, 32.0)
      val cx = Street.centerX(z)
      val y  = Street.groundY(z)
      val r  = rng.next()
      if r < 0.42 then
        // hugging the gutters, where wind and rain push them
        val s = rng.sign
        place(cx + s * rng.range(2.35, 3.12), z, y)
      else if r < 0.62 then
        // on the pavement, against the tactile line
        val s = rng.sign
        place(cx + s * rng.range(3.2, 4.6), z, y + 0.135)
      else if r < 0.78 then
        // caught along the crossing deck
        val dz = rng.range(-2.4, 2.4)
        place(cx + rng.range(-3.1, 3.1), dz, 0.32)
      else
        // a thin scatter over the open road
        place(cx + rng.range(-3.0, 3.0), z, y)
    }

    lists.zipWithIndex.foreach { case (list, i) =>
      if list.nonEmpty then
        val inst = new InstancedMesh(geometry,
          Toon.flat(Toon.FlatOptions(color = tones(i), map = Some(tex), transparent = true,
            opacity = 0.9, depthWrite = Some(false), alphaTest = 0.32, cache = false)),
          list.length.toDouble)
        list.zipWithIndex.foreach { case (m, k) => inst.setMatrixAt(k.toDouble, m) }
        inst.renderOrder = 2.0
        ThreeInterop.setUserDataFlag(inst, "noOutline")
        ctx.add(inst)
    }
  end buildFallenPetals
end Petals
