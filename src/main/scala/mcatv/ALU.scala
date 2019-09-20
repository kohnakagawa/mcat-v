package mcatv

import chisel3._
import chisel3.util._

object ALUOPs {
  val ADD_OP = 0.U(4.W)
  val SUB_OP = 1.U(4.W)
  val AND_OP = 2.U(4.W)
  val OR_OP  = 3.U(4.W)
  val XOR_OP = 4.U(4.W)
  val SLL_OP = 5.U(4.W)
  val SRL_OP = 6.U(4.W)
  val SRA_OP = 7.U(4.W)
  val SLT_OP = 8.U(4.W)
  val SLTU_OP = 9.U(4.W)
  val COPY_OP = 10.U(4.W)

  val ALU_XXX_OP = 15.U(4.W)
}

class ALUIO(implicit p: Parameters) extends CoreBundle()(p) {
  val src1 = Input(UInt(xlen.W))
  val src2 = Input(UInt(xlen.W))
  val op = Input(UInt(4.W))

  val dst = Output(UInt(xlen.W))
}

class ALU(implicit val p: Parameters) extends Module with CoreParams {
  import ALUOPs._

  val io = IO(new ALUIO)

  val shamt = io.src2(4, 0).asUInt

  io.dst := 0.U
  switch (io.op) {
    is (ADD_OP) { io.dst := io.src1 + io.src2 }
    is (SUB_OP) { io.dst := io.src1 - io.src2 }
    is (AND_OP) { io.dst := io.src1 & io.src2 }
    is (OR_OP)  { io.dst := io.src1 | io.src2 }
    is (XOR_OP) { io.dst := io.src1 ^ io.src2 }
    is (SLL_OP) { io.dst := io.src1 << shamt }
    is (SRL_OP) { io.dst := io.src1 >> shamt }
    is (SRA_OP) { io.dst := (io.src1.asSInt >> shamt).asUInt }
    is (SLT_OP) { io.dst := (io.src1.asSInt < io.src2.asSInt).asUInt }
    is (SLTU_OP) { io.dst := (io.src1 < io.src2).asUInt }
    is (COPY_OP) { io.dst := io.src2 }
  }
}
