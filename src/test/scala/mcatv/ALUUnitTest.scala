package mcatv

import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, PeekPokeTester}

class ALUUnitTest(c: ALU) extends PeekPokeTester(c) {
  import ALUOPs._

  poke(c.io.src1, 1)
  poke(c.io.src2, 1 << 10)
  poke(c.io.op, SLL_OP)

  expect(c.io.dst, 1)
}

class ALUTester extends ChiselFlatSpec {
  implicit val p = (new McatvConfig).toInstance
  "Basic GPR test" should "be passed" in {
    iotesters.Driver.execute(Array(), () => new ALU) {
      c => new ALUUnitTest(c)
    } should be (true)
  }
}
