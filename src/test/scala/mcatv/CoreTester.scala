package mcatv

import chisel3._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, PeekPokeTester}

class CoreTest(c: DataPath)(implicit p: Parameters) extends PeekPokeTester(c) {
  val xlen = p(XLEN)

  poke(c.gpr.regs(10), 12)
  poke(c.gpr.regs(11), 13)
  poke(c.gpr.regs(12), 14)

  poke(c.io.instIO.addr, 0)
  poke(c.io.instIO.rddata, 0x00c58533)
  poke(c.io.dataIO.addr, 0)
  poke(c.io.dataIO.rddata, 0)
  poke(c.io.dataIO.wen, true)
  poke(c.io.dataIO.wrdata, 0)

  step(1)

  poke(c.gpr.regs(10.U), 12)

  peek(c.gpr.regs(10.U))
}

class CoreTester extends ChiselFlatSpec {
  implicit val p = (new McatvConfig).toInstance
  "Core test" should "be passed" in {
    iotesters.Driver.execute(Array(), () => new DataPath) {
      c => new CoreTest(c)
    } should be (true)
  }
}


