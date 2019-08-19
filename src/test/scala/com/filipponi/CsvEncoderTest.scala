package com.filipponi

import org.scalatest.{FlatSpec, Matchers}
import shapeless._

/**
  * Trying to test if the implicit resolution is working
  */
class CsvEncoderTest extends FlatSpec with Matchers {


  "CsvEncoder object" should "provide type class for HList" in {

    import com.filipponi.CsvEncoder._

    val test = Test("test",b = true,1)

    //creating the hlist from the case class using GenericRepr
    val testHlist = Generic[Test].to(test)

    /** now i need a specific instance for the hlist String :: boolean :: Int :: HNil
      * the search for the implicit creates it?
      * the summoning is possible because i have a hlist implicit and then i have an implicit for all the other types
      * that i need. Probably the compiler go looking for a Hlist implicit and founds the one that is defined:
      * hlistEncoder[H, T <: HList]
      * It is tricky because all this happen at compile time, but is the same as using generics. The generic definition
      * of my hlist will be expanded to be the one that i really need!
      *
      */

    writeCsv(List(test)) should be ("test,yes,1")

  }

  "CsvEncoder object" should "provide type class for CList" in {


    import com.filipponi.CsvEncoder._

    //there is something wrong in the way that i summon the implicit

    val shapes: List[Shape] =
      List(
        Rectangle(1, 2),
        Circle(3),
        Rectangle(4, 5),
        Circle(6)
      )

    writeCsv(shapes)should be ("1.0,2.0\n3.0\n4.0,5.0\n6.0")

  }

}

sealed trait Shape
final case class Rectangle(width: Double, height: Double) extends Shape
final case class Circle(radius: Double) extends Shape

case class Test(a: String, b: Boolean, i: Int)