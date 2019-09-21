package mcatv

import util.Random.nextInt
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, PeekPokeTester}

class ALUUnitTest(c: ALU) extends PeekPokeTester(c) {
  import ALUOPs._

  val src1Vals = Seq.fill(20)(nextInt)
  val src2Vals = Seq.fill(20)(nextInt)
  val ops = Seq(
    ADD_OP -> ((x: Long, y: Long) => x + y),
    SUB_OP -> ((x: Long, y: Long) => x - y),
    AND_OP -> ((x: Long, y: Long) => x & y),
    OR_OP -> ((x: Long, y: Long) => x | y),
    XOR_OP -> ((x: Long, y: Long) => x ^ y),
    SLL_OP -> ((x: Long, y: Long) => x << (y & 0x1fL)),
    SRL_OP -> ((x: Long, y: Long) => Integer.toUnsignedLong(x) >> Integer.toUnsignedLong(y & 0x1fL)),
    SRA_OP -> ((x: Long, y: Long) => x >> (y & 0x1fL)),
    SLT_OP -> ((x: Long, y: Long) => if (x < y) 1L else 0L),
    SLTU_OP -> ((x: Long, y: Long) => if (Integer.toUnsignedLong(x) < Integer.toUnsignedLong(y)) 1L else 0L),
    COPY_OP -> ((x: Long, y: Long) => y)
  )

  for ((op, fn) <- ops) {
    for ((src1, src2) <- src1Vals zip src2Vals) {
      poke(c.io.src1, src1)
      poke(c.io.src2, src2)
      poke(c.io.op, op)
      step(1)
      expect(c.io.dst, Integer.toUnsignedLong(fn(src1, src2)))
    }
  }
}

class ALUTester extends ChiselFlatSpec {
  implicit val p = (new McatvConfig).toInstance
  "Basic Arithmetic Logic Unit test" should "be passed" in {
    iotesters.Driver.execute(Array(), () => new ALU) {
      c => new ALUUnitTest(c)
    } should be (true)
  }
}
