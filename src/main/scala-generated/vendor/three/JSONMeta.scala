// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait JSONMeta extends js.Object:
  var animations: js.Dictionary[AnimationClipJSON] = js.native
  var geometries: js.Dictionary[BufferGeometryJSON] = js.native
  var images: js.Dictionary[SourceJSON] = js.native
  var materials: js.Dictionary[MaterialJSON] = js.native
  var nodes: js.Dictionary[js.Any] = js.native
  var shapes: js.Dictionary[ShapeJSON] = js.native
  var skeletons: js.Dictionary[SkeletonJSON] = js.native
  var textures: js.Dictionary[TextureJSON] = js.native

object JSONMeta:
  def apply(
    geometries: js.Dictionary[BufferGeometryJSON],
    materials: js.Dictionary[MaterialJSON],
    textures: js.Dictionary[TextureJSON],
    images: js.Dictionary[SourceJSON],
    shapes: js.Dictionary[ShapeJSON],
    skeletons: js.Dictionary[SkeletonJSON],
    animations: js.Dictionary[AnimationClipJSON],
    nodes: js.Dictionary[js.Any]
  ): JSONMeta =
    js.Dynamic.literal(
      "geometries" -> geometries.asInstanceOf[js.Any],
      "materials" -> materials.asInstanceOf[js.Any],
      "textures" -> textures.asInstanceOf[js.Any],
      "images" -> images.asInstanceOf[js.Any],
      "shapes" -> shapes.asInstanceOf[js.Any],
      "skeletons" -> skeletons.asInstanceOf[js.Any],
      "animations" -> animations.asInstanceOf[js.Any],
      "nodes" -> nodes.asInstanceOf[js.Any]
    ).asInstanceOf[JSONMeta]
