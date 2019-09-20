package mcatv

import chisel3._
import chisel3.util._

object Utils {
  def sextTo(x: UInt, n: Int): UInt = {
    require(x.getWidth <= n)
    if (x.getWidth == n) x
    else Cat(Fill(n - x.getWidth, x(x.getWidth-1)), x)
  }

  def padTo(x: UInt, n: Int): UInt = {
    require(x.getWidth <= n)
    if (x.getWidth == n) x
    else Cat(Fill(n - x.getWidth, 0.U), x)
  }
}
