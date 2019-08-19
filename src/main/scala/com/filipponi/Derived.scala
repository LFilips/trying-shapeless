package com.filipponi

import shapeless.Lazy
import spray.json.{JsObject, JsValue, JsonFormat}

object Derived {

  import JsonFormat._
  import shapeless._
  import labelled._
  import spray.json.{JsObject, JsValue}


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
    * a formatter for the head and a formatter for the tail
    *
    */
  implicit def hlistFormat[Key <: Symbol,Value,Remaining <: HList]
  (implicit key:Witness.Aux[Key], //something is just Witness[T] but doens't really compile
   jfh: JsonFormat[Value],
   jft: JsonFormat[Remaining]): JsonFormat[FieldType[Key, Value] :: Remaining]= new JsonFormat[FieldType[Key, Value] :: Remaining] {
    override def read(json: JsValue): FieldType[Key, Value] :: Remaining = {
      /**
        * here i'm tring to read a json value into a hlist.
        * My json is something like
        * example {{{
        * {
        *   "name":"Luca",
        *   "surname" : "Filipponi"
        *   }
        * }}}
        * So i parse the json into a json object, then i get the field that represented by the name of the key
        * using the jsonFormatter for the head
        *
        */
      val fields = json.asJsObject.fields
      val head = jfh.read(fields(key.value.name))
      val tail = jft.read(json)
      field[Key](head) :: tail
    }

    override def write(hlist: FieldType[Key,Value] :: Remaining) = {
      jft.write(hlist.tail).asJsObject :+
        (key.value.name -> jfh.write(hlist.head))
    }
  }


}
// extra syntax for spray.json
object JsonFormat {
  def apply[T](implicit f: Lazy[JsonFormat[T]]): JsonFormat[T] = f.value

  implicit class RichJsValue(val j: JsValue) extends AnyVal {
    def :+(kv: (String, JsValue)): JsValue = JsObject(j.asJsObject.fields + kv)
  }
}
