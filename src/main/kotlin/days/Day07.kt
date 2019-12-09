package days

import opcode.AmplifiersLoop
import opcode.RealtimeOpcodeComputer
import kotlin.math.max

private fun main() {
    val ints = INPUT
        .split(',')
        .map { it.toLong() }

    print(day7Part2(ints))
}

fun day7Part2(controllerSoftware: List<Long>): Long? {
    var maxSignal: Long? = null
    for (a in 5..9) {
        for (b in (5..9).minus(a)) {
            for (c in (5..9).minus(listOf(a, b))) {
                for (d in (5..9).minus(listOf(a, b, c))) {
                    for (e in (5..9).minus(listOf(a, b, c, d))) {
                        val amplifiersLoop =
                            AmplifiersLoop(controllerSoftware, listOf(a, b, c, d, e))
                        val signal = amplifiersLoop.run().first()
                        maxSignal = maxSignal?.let { max(it, signal) } ?: signal
                    }
                }
            }
        }
    }
    return maxSignal
}

fun day7Part1(controllerSoftware: List<Int>): Int? {
    return allPossiblePhaseSettings(controllerSoftware)
        .maxBy { it.value }
        ?.value
}

fun allPossiblePhaseSettings(controllerSoftware: List<Int>): MutableMap<List<Int>, Int> {
    val allPhaseSettings = mutableMapOf<List<Int>, Int>()
    for (a in 0..4) {
        for (b in (0..4).minus(a)) {
            for (c in (0..4).minus(listOf(a, b))) {
                for (d in (0..4).minus(listOf(a, b, c))) {
                    for (e in (0..4).minus(listOf(a, b, c, d))) {
                        val phaseSettings = listOf(a, b, c, d, e)
                        val output = fiveAmplifiers(controllerSoftware, phaseSettings)
                        allPhaseSettings[phaseSettings] = output
                    }
                }
            }
        }
    }
    return allPhaseSettings
}

fun fiveAmplifiers(controllerSoftware: List<Int>, phaseSettings: List<Int>): Int {
    var previousAmplifierSignalOutput = 0L
    for (i in 0..4) {
        val realtimeOpcodeComputer = RealtimeOpcodeComputer(controllerSoftware.map { it.toLong() })
        realtimeOpcodeComputer.inputStream = mutableListOf(phaseSettings[i].toLong(), previousAmplifierSignalOutput)
        realtimeOpcodeComputer.start()
        previousAmplifierSignalOutput = realtimeOpcodeComputer.outputStream.first()
    }
    return previousAmplifierSignalOutput.toInt()
}

private const val INPUT =
    "3,8,1001,8,10,8,105,1,0,0,21,34,55,68,85,106,187,268,349,430,99999,3,9,1001,9,5,9,1002,9,5,9,4,9,99,3,9,1002,9,2,9,1001,9,2,9,1002,9,5,9,1001,9,2,9,4,9,99,3,9,101,3,9,9,102,3,9,9,4,9,99,3,9,1002,9,5,9,101,3,9,9,102,5,9,9,4,9,99,3,9,1002,9,4,9,1001,9,2,9,102,3,9,9,101,3,9,9,4,9,99,3,9,1001,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,1001,9,1,9,4,9,3,9,1001,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,102,2,9,9,4,9,99,3,9,1002,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,101,1,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,1001,9,1,9,4,9,3,9,101,1,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,101,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,1001,9,1,9,4,9,99,3,9,1001,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,101,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,1002,9,2,9,4,9,99,3,9,102,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,101,1,9,9,4,9,3,9,102,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,101,1,9,9,4,9,3,9,1002,9,2,9,4,9,99,3,9,102,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,102,2,9,9,4,9,3,9,101,1,9,9,4,9,3,9,102,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,101,2,9,9,4,9,3,9,101,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,102,2,9,9,4,9,99"
