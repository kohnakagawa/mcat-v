package mcatv

import chisel3._
import chisel3.util._

object IMMTypes {
  val IMM_U = 0.U(4.W)
  val IMM_J = 1.U(4.W)
  val IMM_I = 2.U(4.W)
  val IMM_B = 3.U(4.W)
  val IMM_S = 4.U(4.W)
  val IMM_X = 5.U(4.W)
}

class ImmgenIO(implicit p: Parameters) extends CoreBundle()(p) {
  val inst = Input(UInt(xlen.W))
  val immtype = Input(UInt(4.W)) // NOTE: ビット幅もしかしたら取りすぎかも

  val imm = Output(UInt(xlen.W))
}

class Immgen(implicit val p: Parameters) extends Module with CoreParams {
  import IMMTypes._

  val io = IO(new ImmgenIO())

  val inst = io.inst

  val immU = Cat(inst(31, 12), 0.U(12.W)).asSInt
  val immJ = Cat(inst(31), inst(19, 12), inst(20), inst(30, 21), 0.U(1.W)).asSInt
  val immI = inst(31, 20).asSInt
  val immB = Cat(inst(31), inst(7), inst(30, 25), inst(11, 8), 0.U(1.W)).asSInt
  val immS = Cat(inst(31, 25), inst(11, 7)).asSInt

  io.imm := MuxLookup(io.immtype, 0.S, Seq(
    IMM_U -> immU,
    IMM_J -> immJ,
    IMM_I -> immI,
    IMM_B -> immB,
    IMM_S -> immS
  )).asUInt

  // instruction の形式ごとにimmとしてどこを選択するかが変化する
}
