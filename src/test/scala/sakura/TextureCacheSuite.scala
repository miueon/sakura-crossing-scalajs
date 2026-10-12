package sakura

import vendor.three.Texture



/** Laws of the texture cache: one synthesis per key, and a release that empties the cache, so a
  * rebuilt scene re-synthesizes its surfaces instead of handing back a disposed texture.
  */
class TextureCacheSuite extends munit.FunSuite:

  private def countingCache(onBuild: () => Unit): TextureCache =
    new TextureCache(_ =>
      onBuild()
      new Texture()
    )

  test("a texture is synthesized once per key") {
    var built  = 0
    val cache  = countingCache(() => built += 1)
    val first  = cache.get("warning-stripe")
    val second = cache.get("warning-stripe")
    assert(first eq second, "the same key handed back a second texture")
    assertEquals(built, 1)
    assert(cache.get("crossbuck-sign") ne first, "a different key shares another surface's texture")
    assertEquals(built, 2)
  }

  test("disposeAll empties the cache so the next request synthesizes again") {
    var built = 0
    val cache = countingCache(() => built += 1)
    cache.get("asphalt-road")
    cache.disposeAll()
    cache.get("asphalt-road")
    assertEquals(built, 2, "the cache handed back a texture it had already released")
  }
end TextureCacheSuite
