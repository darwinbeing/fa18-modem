package modem

import chisel3._
import chisel3.util._
import dsptools.numbers.Real
//import freechips.rocketchip.diplomacy.LazyModule
//import freechips.rocketchip.subsystem.BaseSubsystem

// Written by Kunmo Kim : kunmok@berkeley.edu
// Description: This module calculates branch metric for every n-bit reception
class BranchMetric_backup[T <: Data, U <: Data: Real](params: CodingParams[T, U]) extends Module {
  require(params.m >= 1)
  require(params.k >= 1)
  require(params.n >= 2)

  val io = IO(new Bundle {
    val in        = Input(Vec(params.n, SInt(2.W)))
    val out       = Output(Vec(params.nStates, Vec(params.numInputs, Vec(params.n, UInt(params.n.W)))))
    val out_dec   = Output(Vec(params.nStates, Vec(params.numInputs, UInt((log2Ceil(params.n)+1).W))))
  })

  val trellisObj  = new Trellis[T, U](params)

  for (currentStates <- 0 until params.nStates) {
    for (currentInputs <- 0 until params.numInputs) {
      for (r <- 0 until params.n) {
        //        io.out(currentStates)(currentInputs)(r) := io.in(r) ^ trellisObj.output_table(currentStates)(currentInputs)(r).U  // this is obsolete
        if(params.softDecision == false) {        // hard decision
          when(io.in(r) === 0.S) {
            io.out(currentStates)(currentInputs)(r) := 0.U
          }.otherwise {
            io.out(currentStates)(currentInputs)(r) := Mux(io.in(r) === (2 * trellisObj.output_table(currentStates)(currentInputs)(r) - 1).S, 0.U, (1).U)
          }
        }else{                                    // soft decision
          //          io.out(currentStates)(currentInputs)(r) := -1*trellisObj.output_table(currentStates)(currentInputs)(r)*io.in(r)
        }
      }
      io.out_dec(currentStates)(currentInputs)  := io.out(currentStates)(currentInputs).reduce(_ + _)
    }
  }
}
