
/**
  * The repl is perfect for working with shapeless because i can see the evaluation of
  * type that will happen
  */

/**
  * I need all these imports all the time.
  */

import shapeless._
import labelled._
import shapeless.syntax.SingletonOps
import syntax.singleton._

/**
  * Shapeless has some concepts that need to be understood before starting playing with it.
  * One of that the singleton type.
  * The singleton type is obtained using the .narrow from the singletonOps, that enriches
  * the any type.
  * The narrow operation can narrow your expression to a singleton type
  * the weird thing is that if the type is added automatically with IJ
  * i got something like SingletonOps#T which i don't really understand.
  * but if i run the repl i got:
  * String("test")
  * AType.type (the same as the case object type)
  */


"test".narrow // String("test) <: String
//res0: String("test") = test

1.narrow // Int(1) <: Int
//res1: Int(1) = 1

//Doing the same with case object just returns their type!

case object AType
AType.narrow // Atype.type
//res2: AType.type = AType

/**
  * After the singleton that are the labels, that can be attached to a singleton type
  */

'a ->> "bar"
//String with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[String("a")],String]


/**
  * In the workshop there are two more examples but i can't make it compile!
  * ?
  */

//field[Symbol('a)](bar)implicitly[Witness[String("foo")]].value

'a