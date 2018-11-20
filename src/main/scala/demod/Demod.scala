package modem

import chisel3._
import chisel3.util._
import chisel3.experimental.FixedPoint
import dsptools.numbers._

trait DemodulationParams[T<: Data, U<: Data] extends PacketBundleParams[T] with BitsBundleParams[U] {
  val tdummy: DspComplex[T]
  val udummy: U
}

case class HardDemodParams(
  val width: Int,
  val bitsWidth: Int
) extends DemodulationParams[FixedPoint, UInt]{
  val protoIQ = DspComplex(FixedPoint(width.W, (width-3).BP)).cloneType
  val protoBits = UInt(1.W)
  val tdummy = DspComplex(FixedPoint(width.W, (width-3).BP)).cloneType
  val udummy = UInt(1.W)
}


class Demodulator[T <: Data:Real:BinaryRepresentation, U <: Data](params: DemodulationParams[T, U]) extends Module {
  val io = IO(new Bundle {
    val in = Flipped(Decoupled(PacketBundle(params)))
    val out = Decoupled(PacketBundle(params))
  })
}