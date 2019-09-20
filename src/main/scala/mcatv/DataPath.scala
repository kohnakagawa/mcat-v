package mcatv

import chisel3._

class DataPathIO(implicit p: Parameters) extends CoreBundle()(p) {
  val dataIO = Flipped(new DCacheIO)
  val instIO = Flipped(new ICacheIO)
}

class DataPath(implicit val p: Parameters) extends Module with CoreParams {
  val io = IO(new DataPathIO())

  val pc = RegInit(0.U(xlen.W))
  val gpr = Module(new GPR)
  val alu = Module(new ALU)
  val immgen = Module(new Immgen)
  val ctrl = Module(new Control)
  val bcu = Module(new BCU)

  io.instIO.addr := pc
  val inst = io.instIO.rddata

  val pcPlus4 = pc + 4.U

  ctrl.io.inst := inst
  immgen.io.inst := inst
  immgen.io.immtype := ctrl.io.immtype

  val rdaddr = inst(11, 7)
  val rs1addr = inst(19, 15)
  val rs2addr = inst(24, 20)

  gpr.io.rdaddr1 := rs1addr
  gpr.io.rdaddr2 := rs2addr
  gpr.io.wraddr := rdaddr
  gpr.io.wen := ctrl.io.wren
  gpr.io.wrdata := Mux(ctrl.io.wrpcPlus4, pcPlus4,
    Mux(ctrl.io.frommem, io.dataIO.rddata, alu.io.dst))

  alu.io.src1 := Mux(ctrl.io.pcSrc1, pc, gpr.io.rddata1)
  alu.io.src2 := Mux(ctrl.io.immen, immgen.io.imm, gpr.io.rddata2)
  alu.io.op := ctrl.io.aluop

  bcu.io.src1 := gpr.io.rddata1
  bcu.io.src2 := gpr.io.rddata2
  bcu.io.op := ctrl.io.bcuop

  io.dataIO.addr := alu.io.dst
  io.dataIO.acctype := ctrl.io.acctype
  io.dataIO.wen := ctrl.io.wmen
  io.dataIO.wrdata := gpr.io.rddata2
  io.dataIO.ldsext := ctrl.io.ldsext

  pc := Mux(ctrl.io.pcFromAlu | bcu.io.result, alu.io.dst & (~1.U).asUInt, pcPlus4)
}
