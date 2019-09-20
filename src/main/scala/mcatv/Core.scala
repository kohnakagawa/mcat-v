package mcatv

import chisel3.Bundle

trait CoreParams {
  implicit val p: Parameters
  val xlen = p(XLEN)
}

abstract class CoreBundle(implicit val p: Parameters) extends Bundle with CoreParams

class Core {

}
