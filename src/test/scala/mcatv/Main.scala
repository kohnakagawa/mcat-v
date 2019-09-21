package mcatv

import chisel3._

// from sbt shell test:runMain mcatv.McatvRepl
object McatvRepl extends App {
  implicit val p = (new McatvConfig).toInstance
  iotesters.Driver.executeFirrtlRepl(args, () => new Top)
}
