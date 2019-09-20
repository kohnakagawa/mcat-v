package mcatv

import chisel3._

class TopIO(implicit p: Parameters) extends CoreBundle()(p) {}

class Top(implicit val p: Parameters) extends Module with CoreParams {
  val io = IO(new TopIO)

  val dpath = Module(new DataPath)
  val icache = Module(new ICache)
  val dcache = Module(new DCache())

  dpath.io.instIO <> icache.io
  dpath.io.dataIO <> dcache.io
}

