package mcatv

import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, PeekPokeTester}

class GPRUnitTest(c: GPR) extends PeekPokeTester(c) {
  poke(c.io.wraddr, 1)
  poke(c.io.wrdata, 45)
  poke(c.io.wen, true)
  step(1)

  poke(c.io.wraddr, 2)
  poke(c.io.wrdata, 56)
  poke(c.io.wen, true)
  step(1)

  poke(c.io.rdaddr1, 1)
  poke(c.io.rdaddr2, 2)
  poke(c.io.wen, false)
  expect(c.io.rddata1, 45)
  expect(c.io.rddata2, 56)
  step(1)

  expect(c.io.rddata1, 45)
  expect(c.io.rddata2, 56)
}

class GPRTester extends ChiselFlatSpec {
  implicit val p = (new McatvConfig).toInstance
  "Basic GPR test" should "be passed" in {
    iotesters.Driver.execute(Array(), () => new GPR) {
      c => new GPRUnitTest(c)
    } should be (true)
  }
}
