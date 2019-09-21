package mcatv

import util.Random.nextInt
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, PeekPokeTester}

class BCUUnitTest(c: BCU) extends PeekPokeTester(c) {
  import BCUOPs._

  val src1Vals = Seq.fill(20)(nextInt)
  val src2Vals = Seq.fill(20)(nextInt)
  val ops = Seq(
    EQ_OP -> ((x: Int, y: Int) => x == y),
    GE_OP -> ((x: Int, y: Int) => x >= y),
    GEU_OP -> ((x: Int, y: Int) => Integer.toUnsignedLong(x) >= Integer.toUnsignedLong(y)),
    LT_OP -> ((x: Int, y: Int) => x < y),
    LTU_OP -> ((x: Int, y: Int) => Integer.toUnsignedLong(x) < Integer.toUnsignedLong(y)),
    NE_OP -> ((x: Int, y: Int) => x != y)
  )

  for ((op, fn) <- ops) {
    for ((src1, src2) <- src1Vals zip src2Vals) {
      poke(c.io.src1, src1)
      poke(c.io.src2, src2)
      poke(c.io.op, op)
      step(1)
      expect(c.io.result, fn(src1, src2))
    }
  }
}

class BCUTester extends ChiselFlatSpec {
  implicit val p = (new McatvConfig).toInstance
  "Basic Branch Control Unit test" should "be passed" in {
    iotesters.Driver.execute(Array(), () => new BCU) {
      c => new BCUUnitTest(c)
    } should be (true)
  }
}

