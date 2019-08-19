package com.filipponi

import shapeless.{:+:, ::, CNil, Coproduct, Generic, HList, HNil, Inl, Inr, Lazy}

/**
  * This is the typical type class definition
  */
trait CsvEncoder[A] {
  def encode(value: A): List[String]
}

object CsvEncoder {

  // "Summoner" method
  def apply[A](implicit enc: CsvEncoder[A]): CsvEncoder[A] =
    enc

  // "Constructor" method
  def instance[A](func: A => List[String]): CsvEncoder[A] =
    new CsvEncoder[A] {
      def encode(value: A): List[String] =
        func(value)
    }

  def writeCsv[A](values: List[A])(implicit enc: CsvEncoder[A]): String =
    values.map(value => enc.encode(value).mkString(",")).mkString("\n")

  implicit val stringEncoder: CsvEncoder[String] =
    instance(str => List(str))
  implicit val intEncoder: CsvEncoder[Int] =
    instance(num => List(num.toString))
  implicit val doubleEncoder: CsvEncoder[Double] =
    instance(num => List(num.toString))
  implicit val booleanEncoder: CsvEncoder[Boolean] =
    instance(bool => List(if(bool) "yes" else "no"))

  /**
    * This piece is the first implicit that will be resolved in case we search for a type that is not strictly defined
    * (for example type like String and Double have their specific representation and the implicit resolution will use
    * that one). Whenever an implicit of type A is needed, the compiler will search for that in the scope. What this
    * generic implicit is saying in the method signature is that any class A can be encoded as soon we do have a Generic[A]
    * available in the scope (that is there thanks to shapeless) and an Encoder for R. But what is R? R is the generic
    * representation of the type A, that is in the form of a HList and CList for product and coproduct. Supping that
    * the type that i'm searching for is a case class Person(name:String,age:Int). When the compiler will look for an
    * implicit for this class will find the genericEncoder to be suitable and then look for the Generic[A] { type Repr = R }.
    * The type Repr will be HList String :: Int :: HNil, and then will look for a CsvEncoder[String::Int::HNil]. At this
    * point the encoder: def hlistEncoder[H, T <: HList]
    * will be suitable and will recursively search every type of the HList. But there is need of the base type otherwise
    * it will fail.
    */
  implicit def genericEncoder[A, R](
                                     implicit
                                     gen: Generic[A] { type Repr = R }, //can be rewritten as Generic.Aux[A, R]
                                     enc: CsvEncoder[R]
                                   ): CsvEncoder[A] =
    instance(a => enc.encode(gen.to(a)))

  implicit def pairEncoder[A, B]
  (implicit aEnc: CsvEncoder[A], bEnc: CsvEncoder[B]): CsvEncoder[(A, B)] =
    instance{value: (A, B) => aEnc.encode(value._1) ::: bEnc.encode(value._2)}

  /**
    * Hnil encoder.
    */
  implicit val hnilEncoder: CsvEncoder[HNil] =
    instance(hnil => Nil)

  implicit def hlistEncoder[H, T <: HList](implicit hEncoder: CsvEncoder[H], tEncoder: CsvEncoder[T]) : CsvEncoder[H::T] =
    instance {
      case h :: t =>
        hEncoder.encode(h) ++ tEncoder.encode(t)
    }

  implicit val cnilEncoder: CsvEncoder[CNil] =
    instance(cnil => throw new Exception("Inconceivable!"))

  implicit def coproductEncoder[H, T <: Coproduct](
  implicit
  hEncoder: Lazy[CsvEncoder[H]],
  tEncoder: CsvEncoder[T]
  ): CsvEncoder[H :+: T] =
    instance {
      case Inl(h) => hEncoder.value.encode(h)
      case Inr(t) => tEncoder.encode(t)
    }


}
