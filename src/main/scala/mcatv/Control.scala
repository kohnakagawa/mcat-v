package mcatv

import chisel3._
import chisel3.util.ListLookup

object Control {

  import Instructions._
  import ALUOPs._
  import BCUOPs._
  import IMMTypes._
  import AccessTypes._

  val Y = true.B
  val N = false.B

  val default = List(N, ALU_XXX_OP, N, IMM_X, N, WORD_ACCESS, N, N, N, N, N, DISABLE)
  // wr_en, alu_op, imm_en, imm_type, wm_en, access_type, from_mem, ld_sext, wr_pc4, pc_from_alu, pc_src1, bcu_op
  val map = Array(
    ADD -> List(Y, ADD_OP, N, IMM_X, N, WORD_ACCESS, N, N, N, N, N, DISABLE),
    SUB -> List(Y, SUB_OP, N, IMM_X, N, WORD_ACCESS, N, N, N, N, N, DISABLE),
    SLL -> List(Y, SLL_OP, N, IMM_X, N, WORD_ACCESS, N, N, N, N, N, DISABLE),
    SLT -> List(Y, SLT_OP, N, IMM_X, N, WORD_ACCESS, N, N, N, N, N, DISABLE),
    SLTU -> List(Y, SLTU_OP, N, IMM_X, N, WORD_ACCESS, N, N, N, N, N, DISABLE),
    XOR -> List(Y, XOR_OP, N, IMM_X, N, WORD_ACCESS, N, N, N, N, N, DISABLE),
    SRL -> List(Y, SRL_OP, N, IMM_X, N, WORD_ACCESS, N, N, N, N, N, DISABLE),
    SRA -> List(Y, SRA_OP, N, IMM_X, N, WORD_ACCESS, N, N, N, N, N, DISABLE),
    OR  -> List(Y, OR_OP, N, IMM_X, N, WORD_ACCESS, N, N, N, N, N, DISABLE),
    AND -> List(Y, AND_OP, N, IMM_X, N, WORD_ACCESS, N, N, N, N, N, DISABLE),
    ADDI -> List(Y, ADD_OP, Y, IMM_I, N, WORD_ACCESS, N, N, N, N, N, DISABLE),
    SLTI -> List(Y, SLT_OP, Y, IMM_I, N, WORD_ACCESS, N, N, N, N, N, DISABLE),
    SLTIU -> List(Y, SLTU_OP, Y, IMM_I, N, WORD_ACCESS, N, N, N, N, N, DISABLE),
    XORI -> List(Y, XOR_OP, Y, IMM_I, N, WORD_ACCESS, N, N, N, N, N, DISABLE),
    ORI -> List(Y, OR_OP, Y, IMM_I, N, WORD_ACCESS, N, N, N, N, N, DISABLE),
    ANDI -> List(Y, AND_OP, Y, IMM_I, N, WORD_ACCESS, N, N, N, N, N, DISABLE),
    SB -> List(N, ADD_OP, Y, IMM_S, Y, BYTE_ACCESS, N, N, N, N, N, DISABLE),
    SH -> List(N, ADD_OP, Y, IMM_S, Y, HALF_ACCESS, N, N, N, N, N, DISABLE),
    SW -> List(N, ADD_OP, Y, IMM_S, Y, WORD_ACCESS, N, N, N, N, N, DISABLE),
    LB -> List(Y, ADD_OP, Y, IMM_I, N, BYTE_ACCESS, Y, Y, N, N, N, DISABLE),
    LH -> List(Y, ADD_OP, Y, IMM_I, N, HALF_ACCESS, Y, Y, N, N, N, DISABLE),
    LW -> List(Y, ADD_OP, Y, IMM_I, N, WORD_ACCESS, Y, Y, N, N, N, DISABLE),
    LBU -> List(Y, ADD_OP, Y, IMM_I, N, BYTE_ACCESS, Y, N, N, N, N, DISABLE),
    LHU -> List(Y, ADD_OP, Y, IMM_I, N, HALF_ACCESS, Y, N, N, N, N, DISABLE),
    JAL -> List(Y, ADD_OP, Y, IMM_J, N, WORD_ACCESS, N, N, Y, Y, Y, DISABLE),
    JALR -> List(Y, ADD_OP, Y, IMM_I, N, WORD_ACCESS, N, N, Y, Y, N, DISABLE),
    LUI -> List(Y, COPY_OP, Y, IMM_U, N, WORD_ACCESS, N, N, N, N, N, DISABLE),
    AUIPC -> List(Y, ADD_OP, Y, IMM_U, N, WORD_ACCESS, N, N, N, N, Y, DISABLE),
    BEQ -> List(N, ADD_OP, Y, IMM_B, N, WORD_ACCESS, N, N, N, N, Y, EQ_OP),
    BNE -> List(N, ADD_OP, Y, IMM_B, N, WORD_ACCESS, N, N, N, N, Y, NE_OP),
    BLT -> List(N, ADD_OP, Y, IMM_B, N, WORD_ACCESS, N, N, N, N, Y, LT_OP),
    BGE -> List(N, ADD_OP, Y, IMM_B, N, WORD_ACCESS, N, N, N, N, Y, GE_OP),
    BLTU -> List(N, ADD_OP, Y, IMM_B, N, WORD_ACCESS, N, N, N, N, Y, LTU_OP),
    BGEU -> List(N, ADD_OP, Y, IMM_B, N, WORD_ACCESS, N, N, N, N, Y, GEU_OP)
  )
  // wr_en, alu_op, imm_en, imm_type, wm_en, access_type, from_mem, ld_sext, wr_pc4, pc_from_alu, pc_src1, bcu_op
}

class ControlIO(implicit p: Parameters) extends CoreBundle()(p) {
  val inst = Input(UInt(xlen.W))

  val wren = Output(Bool())
  val aluop = Output(UInt(4.W))
  val immen = Output(Bool())
  val immtype = Output(UInt(4.W))
  val wmen = Output(Bool())
  val acctype = Output(UInt(2.W))
  val frommem = Output(Bool())
  val ldsext = Output(Bool())
  val wrpcPlus4 = Output(Bool())
  val pcFromAlu = Output(Bool())
  val pcSrc1 = Output(Bool())
  val bcuop = Output(UInt(4.W))
}

class Control(implicit val p: Parameters) extends Module with CoreParams {
  val io = IO(new ControlIO())

  val sigs = ListLookup(io.inst, Control.default, Control.map)

  io.wren := sigs(0)
  io.aluop := sigs(1)
  io.immen := sigs(2)
  io.immtype := sigs(3)
  io.wmen := sigs(4)
  io.acctype := sigs(5)
  io.frommem := sigs(6)
  io.ldsext := sigs(7)
  io.wrpcPlus4 := sigs(8)
  io.pcFromAlu := sigs(9)
  io.pcSrc1 := sigs(10)
  io.bcuop := sigs(11)
}
