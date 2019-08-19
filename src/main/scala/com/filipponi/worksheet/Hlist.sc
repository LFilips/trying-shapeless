

import shapeless._
import labelled._
import shapeless.Generic.Aux
import shapeless.syntax.SingletonOps
import syntax.singleton._


/**
  * The hlist is able to represent an heterogeneous list (that's why the name HList).
  */

"hello" :: 13L :: true :: HNil
//res0: String :: Long :: Boolean :: shapeless.HNil = hello :: 13 :: true :: HNil


('a ->> true) :: ('b ->> 1) :: ('s ->> "string") :: HNil
//Boolean with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[String("a")],Boolean]
// :: Int with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[String("b")],Int]
// :: String with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[String("s")],String]
// :: shapeless.HNil = true :: 1 :: string :: HNil

//this representation already make me think of a case class
case class Test(a: Boolean, b: Int, s: String)


//converting a type to an hlist

case class IceCream(name: String, numCherries: Int, inCone: Boolean)

val iceCreamGen = Generic[IceCream]
val iceCream = IceCream("vaniglia", 1, false)

//this create an Hlist with the singleton types
val repr = iceCreamGen.to(iceCream)
repr.head
val iceCream2 = iceCreamGen.from(repr)

//it is not limited to case class

val tupleGen = Generic[(String, Int, Boolean)]
tupleGen.to(("Hello", 123, true))
// res4: tupleGen.Repr = Hello :: 123 :: true :: HNil
tupleGen.from("Hello" :: 123 :: true :: HNil)
// res5: (String, Int, Boolean) = (Hello,123,true)


//scala tuple can accept up to 22 tuples
case class BigData(
                    a:Int,b:Int,c:Int,d:Int,e:Int,f:Int,g:Int,h:Int,i:Int,j:Int,
                    k:Int,l:Int,m:Int,n:Int,o:Int,p:Int,q:Int,r:Int,s:Int,t:Int,
                    u:Int,v:Int)
Generic[BigData].from(Generic[BigData].to(BigData(
  1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22)))
// res6: BigData = BigData
(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22)



case class Red()
case class Amber()
case class Green()
type Light = Red :+: Amber :+: Green :+: CNil

/** `H :+: T` can either be `H` or `T`.
  * With Inl is T with Inr is H
  */
val red: Light = Inl(Red())
// red: Light = Inl(Red())
val green: Light = Inr(Inr(Inl(Green())))
// green: Light = Inr(Inr(Inl(Green())))




import shapeless.Generic
sealed trait Shape
final case class Rectangle(width: Double, height: Double) extends Shape
final case class Circle(radius: Double) extends Shape
val gen = Generic[Shape]
//gen: shapeless.Generic[Shape]{type Repr = Rectangle :+: Circle :+: shapeless.CNil} = anon$macro$1$1@4d95c771
gen.to(Rectangle(3.0, 4.0))
