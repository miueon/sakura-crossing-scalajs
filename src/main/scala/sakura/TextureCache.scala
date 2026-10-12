package sakura

import scala.collection.mutable

/** Lifecycle-scoped cache for synthesized GPU textures.
  *
  * Keying by name means a surface asked for twice is drawn once: every climax of the crossing
  * prototype shares one canvas texture per surface type. The cache owns the textures it built, so
  * the scene releases them through [[disposeAll]] on unmount instead of tracking each one.
  */
final class TextureCache(synthesize: String => MaterialTexture):
  private val textures = mutable.LinkedHashMap.empty[String, MaterialTexture]

  def get(key: String): MaterialTexture = textures.getOrElseUpdate(key, synthesize(key))

  def disposeAll(): Unit =
    textures.valuesIterator.foreach(_.dispose())
    textures.clear()
end TextureCache
