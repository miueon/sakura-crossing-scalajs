package facadecontract.std

import scala.language.implicitConversions
import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
trait JSArrayLike[+A] extends js.Object:
  def length: Int = js.native
  @JSBracketAccess
  def apply(index: Int): A = js.native

object JSArrayLike:
  inline def from[A](arr: js.Array[A]): JSArrayLike[A] = arr.asInstanceOf[JSArrayLike[A]]

  extension [A](arr: js.Array[A])
    inline def asArrayLike: JSArrayLike[A] = arr.asInstanceOf[JSArrayLike[A]]

  given [A]: Conversion[js.Array[A], JSArrayLike[A]] with
    def apply(arr: js.Array[A]): JSArrayLike[A] = arr.asInstanceOf[JSArrayLike[A]]

@js.native
trait JSIterableIterator[+A] extends js.Iterator[A] with js.Iterable[A]:
  @JSName(js.Symbol.iterator)
  override def jsIterator(): JSIterableIterator[A] = js.native

object JSIterableIterator:
  inline def from[A](iter: js.Iterator[A] & js.Iterable[A]): JSIterableIterator[A] =
    iter.asInstanceOf[JSIterableIterator[A]]

  extension [A](iter: js.Iterator[A] & js.Iterable[A])
    inline def asIterableIterator: JSIterableIterator[A] =
      iter.asInstanceOf[JSIterableIterator[A]]

@js.native
trait JSReadonlyArray[+A] extends JSArrayLike[A] with js.Iterable[A]:
  def concat[B >: A](items: JSReadonlyArray[B]*): JSReadonlyArray[B] = js.native
  def slice(start: Int = js.native, end: Int = js.native): JSReadonlyArray[A] = js.native
  def indexOf[B >: A](searchElement: B, fromIndex: Int = js.native): Int = js.native
  def lastIndexOf[B >: A](searchElement: B, fromIndex: Int = js.native): Int = js.native
  def includes[B >: A](searchElement: B, fromIndex: Int = js.native): Boolean = js.native
  def join(separator: String = js.native): String = js.native

object JSReadonlyArray:
  inline def from[A](arr: js.Array[A]): JSReadonlyArray[A] = arr.asInstanceOf[JSReadonlyArray[A]]

  extension [A](arr: js.Array[A])
    inline def asReadonly: JSReadonlyArray[A] = arr.asInstanceOf[JSReadonlyArray[A]]

  given [A]: Conversion[js.Array[A], JSReadonlyArray[A]] with
    def apply(arr: js.Array[A]): JSReadonlyArray[A] = arr.asInstanceOf[JSReadonlyArray[A]]

opaque type JSUnknown = js.Any

object JSUnknown:
  inline def from(raw: js.Any): JSUnknown = raw

  extension (u: JSUnknown)
    inline def raw: js.Any = u
    inline def narrowAs[T](predicate: js.Any => Boolean): Option[T] =
      if predicate(u) then Some(u.asInstanceOf[T]) else None
    inline def unsafeAs[T]: T = u.asInstanceOf[T]
