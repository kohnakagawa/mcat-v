package mcatv

import chisel3._
import chisel3.util._

object AccessTypes {
  val BYTE_ACCESS = 0.U(2.W)
  val HALF_ACCESS = 1.U(2.W)
  val WORD_ACCESS = 2.U(2.W)
  val DWORD_ACCESS = 3.U(2.W)
}

// NOTE: single cycle CPU の場合には、1命令でメモリへの読み書きが同時に発生するケースが存在しないので、addrなどはread/writeの二つを兼ねる
class DCacheIO(implicit p: Parameters) extends CoreBundle()(p) {
  val addr = Input(UInt(xlen.W))
  val wrdata = Input(UInt(xlen.W))
  val acctype = Input(UInt(2.W))
  val wen = Input(Bool())
  val ldsext = Input(Bool())

  val rddata = Output(UInt(xlen.W))
}

// NOTE: non-align の load/store に対して例外をだす様に変更する必要あり
class DCache(implicit val p: Parameters) extends Module with CoreParams {
  val io = IO(new DCacheIO())

  val cache = Mem(p(CACHE_SIZE), UInt(xlen.W))

  val index  = (io.addr >> 2.U).asUInt
  val offset = ((io.addr & 3.U) * 8.U).asUInt()(5, 0)

  import AccessTypes._

  val signBitsIdx = MuxLookup(io.acctype, 31.U,
    Array(
      BYTE_ACCESS -> 7.U,
      HALF_ACCESS -> 15.U,
      WORD_ACCESS -> 31.U
    )
  )
  val sextMask = MuxLookup(io.acctype, 0.U,
    Array(
      BYTE_ACCESS -> "hffffff00".U,
      HALF_ACCESS -> "hffff0000".U,
      WORD_ACCESS -> 0.U
    )
  )

  val rddata = (cache(index) >> offset).asUInt
  when (io.ldsext) {
    when (rddata(signBitsIdx)) {
      io.rddata := rddata | sextMask
    } .otherwise {
      io.rddata := rddata
    }
  } .otherwise {
    io.rddata := rddata
  }

  when (io.wen) {
    cache(index) := (io.wrdata.asUInt & (~sextMask).asUInt) << offset
  }
}

class ICacheIO(implicit p: Parameters) extends CoreBundle()(p) {
  val addr = Input(UInt(xlen.W))
  val rddata = Output(UInt(xlen.W))
}

class ICache(implicit val p: Parameters) extends Module with CoreParams {
  val io = IO(new ICacheIO())

  val cache = Mem(p(CACHE_SIZE), UInt(xlen.W))
  val index = (io.addr >> 2.U).asUInt
  io.rddata := cache(index)
}

