package mcatv

case object XLEN extends Field[Int]
case object CACHE_SIZE extends Field[Int]

class McatvConfig extends Config((site, here, up) => {
  case XLEN => 32
  case CACHE_SIZE => 1024
})
