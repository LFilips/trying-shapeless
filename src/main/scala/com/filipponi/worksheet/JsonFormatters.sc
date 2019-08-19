
import shapeless._
import labelled._
import shapeless.syntax.SingletonOps
import spray.json.{JsObject, JsValue}
import syntax.singleton._

//Traits for defining a generic JsonFormat
trait JsonFormat[T] {
  def read(js: JsValue) : T
  def write(n: T) : JsObject
}

// extra syntax for spray.json
object JsonFormat {
  def apply[T](implicit f: Lazy[JsonFormat[T]]): JsonFormat[T] = f.value

  implicit class RichJsValue(val j: JsValue) extends AnyVal {
    def :+(kv: (String, JsValue)): JsValue = JsObject(j.asJsObject.fields + kv)
  }
}

/**
  * The type class for the HNil is simple, will just write the emptyJson, but i'm not sure about the read?
  */
implicit object HnilFormat extends JsonFormat[HNil] {
  override def read(js: JsValue) = HNil

  override def write(n: HNil) = JsObject() //writes an empty JsObject
}

/**
  * This is the type class for the hlist.
  * It basically uses the fact that is a list and we will need:
  * A witness for the value, the witness allow to pass from the type level to the value level.
  *
  * a formatter for the head and a formatter for the tail
  *
  */
implicit def hlistFormat[Key <: Symbol,Value,Remaining <: HList]
(implicit key:Witness.Aux[Key], //something is just Witness[T] but doens't really compile
 jfh: JsonFormat[Value],
 jft: JsonFormat[Remaining])= new JsonFormat[HList] {
  override def read(js: JsValue) = ???

  override def write(hlist: FieldType[Key,Value] :: Remaining) = {
   val elementName: String =  key.value.name
    val elementValue: JsValue = jfh.write(hlist.head)
    jft.write(hlist.tail).asJsObject :+ (elementName -> elementValue)
  }
}

implicit def doubleToInt(d: Double) = d.toInt
