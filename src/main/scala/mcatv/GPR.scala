package mcatv

import chisel3._

class GPRIO(implicit p: Parameters) extends CoreBundle()(p) {
  val wen = Input(Bool())

  val rdaddr1 = Input(UInt(5.W))
  val rdaddr2 = Input(UInt(5.W))
  val wraddr = Input(UInt(5.W))

  val rddata1 = Output(UInt(xlen.W))
  val rddata2 = Output(UInt(xlen.W))
  val wrdata = Input(UInt(xlen.W))
}

class GPR(implicit val p: Parameters) extends Module with CoreParams {
  val io = IO(new GPRIO())

  val regs = Mem(32, UInt(xlen.W))

  io.rddata1 := Mux(io.rdaddr1 =/= 0.U, regs(io.rdaddr1), 0.U)
  io.rddata2 := Mux(io.rdaddr2 =/= 0.U, regs(io.rdaddr2), 0.U)

  when (io.wen && (io.wraddr =/= 0.U)) {
    regs(io.wraddr) := io.wrdata
  }
}
