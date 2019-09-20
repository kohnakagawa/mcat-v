package mcatv

import chisel3._
import chisel3.util._

object BCUOPs {
  val EQ_OP = 0.U(4.W)
  val GE_OP = 1.U(4.W)
  val GEU_OP = 2.U(4.W)
  val LT_OP = 3.U(4.W)
  val LTU_OP = 4.U(4.W)
  val NE_OP = 5.U(4.W)

  val DISABLE = 15.U(4.W)
}

class BCUIO(implicit p: Parameters) extends CoreBundle()(p) {
  val src1 = Input(UInt(xlen.W))
  val src2 = Input(UInt(xlen.W))
  val op = Input(UInt(4.W))

  val result = Output(Bool())
}

class BCU(implicit val p: Parameters) extends Module with CoreParams {
  import BCUOPs._
  val io = IO(new BCUIO)

  io.result := false.B
  switch (io.op) {
    is (EQ_OP) { io.result := io.src1 === io.src2 }
    is (GE_OP) { io.result := io.src1.asSInt >= io.src2.asSInt }
    is (GEU_OP) { io.result := io.src1 >= io.src2 }
    is (LT_OP) { io.result := io.src1.asSInt < io.src2.asSInt}
    is (LTU_OP) { io.result := io.src1 < io.src2}
    is (NE_OP) { io.result := io.src1 =/= io.src2 }
  }
}
