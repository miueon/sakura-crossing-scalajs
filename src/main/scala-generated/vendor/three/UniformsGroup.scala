// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "UniformsGroup")
class UniformsGroup[TEventMap <: UniformsGroupEventMap]() extends EventDispatcher[TEventMap]:
  var id: Double = js.native
  def isUniformsGroup: true = js.native
  var uniforms: js.Array[Uniform[js.Any] | js.Array[Uniform[js.Any]]] = js.native
  var usage: 35040 | 35041 | 35042 | 35044 | 35045 | 35046 | 35048 | 35049 | 35050 = js.native
  @JSName("add")
  protected def add$raw(uniform: Uniform[js.Any] | js.Array[Uniform[js.Any]]): UniformsGroup[TEventMap] = js.native
  override def clone(): UniformsGroup[UniformsGroupEventMap] = js.native
  @JSName("copy")
  protected def copy$raw(source: UniformsGroup[UniformsGroupEventMap]): UniformsGroup[TEventMap] = js.native
  def dispose(): Unit = js.native
  @JSName("remove")
  protected def remove$raw(uniform: Uniform[js.Any] | js.Array[Uniform[js.Any]]): UniformsGroup[TEventMap] = js.native
  @JSName("setName")
  protected def setName$raw(name: String): UniformsGroup[TEventMap] = js.native
  @JSName("setUsage")
  protected def setUsage$raw(value: 35040 | 35041 | 35042 | 35044 | 35045 | 35046 | 35048 | 35049 | 35050): UniformsGroup[TEventMap] = js.native

object UniformsGroup:
  extension [TEventMap <: UniformsGroupEventMap, Self <: UniformsGroup[TEventMap]](self: Self)
    inline def add(uniform: Uniform[js.Any] | js.Array[Uniform[js.Any]]): Self = self.add$raw(uniform).asInstanceOf[Self]
    inline def remove(uniform: Uniform[js.Any] | js.Array[Uniform[js.Any]]): Self = self.remove$raw(uniform).asInstanceOf[Self]
    inline def setName(name: String): Self = self.setName$raw(name).asInstanceOf[Self]
    inline def setUsage(value: 35040 | 35041 | 35042 | 35044 | 35045 | 35046 | 35048 | 35049 | 35050): Self = self.setUsage$raw(value).asInstanceOf[Self]
    inline def copy(source: UniformsGroup[UniformsGroupEventMap]): Self = self.copy$raw(source).asInstanceOf[Self]
