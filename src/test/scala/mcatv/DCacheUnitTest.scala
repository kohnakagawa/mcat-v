package mcatv

import chisel3._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, PeekPokeTester}

class DCacheUnitTest(c: DCache) extends PeekPokeTester(c) {

  import AccessTypes._

  poke(c.io.addr, 2)
  poke(c.io.wrdata, "hf".U(32.W))
  poke(c.io.acctype, BYTE_ACCESS)
  poke(c.io.wen, 1)
  poke(c.io.ldsext, 0)

  step(1)

  poke(c.io.addr, 2)
  poke(c.io.wrdata, 0)
  poke(c.io.acctype, BYTE_ACCESS)
  poke(c.io.wen, 0)
  poke(c.io.ldsext, 1)

  expect(c.io.rddata, "hf".U(32.W))
}

class DCacheTester extends ChiselFlatSpec {
  implicit val p = (new McatvConfig).toInstance
  "Basic GPR test" should "be passed" in {
    iotesters.Driver.execute(Array(), () => new DCache) {
      c => new DCacheUnitTest(c)
    } should be (true)
  }
}
