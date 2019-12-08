package opcode

import digits

class RealtimeOpcodeComputer(software: List<Long>) {
    private val memory = software.toMutableList()

    var name: String? = null

    var inputStream = mutableListOf<Long>()
    var outputStream = mutableListOf<Long>()

    fun start() {
        var index = 0
        loop@ while (index < memory.size) {
            val instructionDigits = memory[index].toInt().digits()

            val opcode = instructionDigits.takeLast(2).joinToString("").toInt()
            val paramsModes = instructionDigits.dropLast(2).reversed()
            when (opcode) {
                1, 2, 7, 8 -> {
                    val params = listOf(memory[index + 1], memory[index + 2], memory[index + 3])
                    memory.applyInstruction(opcode, paramsModes, params)
                    index += 4
                }
                3 -> {
                    if (inputStream.isEmpty()) println("$name waiting for input")
                    while (inputStream.isEmpty()) {
                        // wait for input
                        Thread.sleep(10)
                    }
                    synchronized(inputStream) {
                        println("$name reading from stream = $inputStream")
                        memory[memory[index + 1].toInt()] = inputStream.removeAt(0)
                    }
                    index += 2
                }
                4 -> {
                    val paramMode = paramsModes.getOrElse(0) { 0 }
                    synchronized(outputStream) {
                        val value = memory.accessParam(paramMode, index + 1)
                        println("$name writing $value to stream = $outputStream")
                        outputStream.add(value)
                    }
                    index += 2
                }
                5 -> {
                    index = memory.jumpIfTrue(index, paramsModes)
                }
                6 -> {
                    index = memory.jumpIfFalse(index, paramsModes)
                }
                99 -> {
                    println("$name about to halt. Bye.")
                    return
                }
                else -> throw IllegalStateException()
            }
        }
    }

    private fun MutableList<Long>.jumpIfTrue(
        index: Int,
        paramsModes: List<Int>
    ): Int = jumpIf(index, paramsModes, isJumpIfFalse = false)

    private fun MutableList<Long>.jumpIfFalse(
        index: Int,
        paramsModes: List<Int>
    ): Int = jumpIf(index, paramsModes, isJumpIfFalse = true)

    private fun MutableList<Long>.jumpIf(
        index: Int,
        paramsModes: List<Int>,
        isJumpIfFalse: Boolean
    ): Int {
        val firstParamMode = paramsModes.getOrElse(0) { 0 }
        val condition = accessParam(firstParamMode, index + 1).toInt()
        return if ((condition != 0) xor isJumpIfFalse) {
            val secondParamMode = paramsModes.getOrElse(1) { 0 }
            accessParam(secondParamMode, index + 2).toInt()
        } else {
            index + 3
        }
    }

    private fun MutableList<Long>.accessParam(
        mode: Int,
        index: Int
    ): Long = when (mode) {
        0 -> this[this[index].toInt()]
        1 -> this[index]
        else -> throw IllegalArgumentException("parameter mode $mode not recognized")
    }

    private fun MutableList<Long>.applyInstruction(opcode: Int, paramsModes: List<Int>, params: List<Long>) {
        val first = if (paramsModes.getOrNull(0) == 1) params[0] else this[params[0].toInt()]
        val second = if (paramsModes.getOrNull(1) == 1) params[1] else this[params[1].toInt()]
        val result = when (opcode) {
            1 -> first + second
            2 -> first * second
            7 -> if (first < second) 1L else 0L
            8 -> if (first == second) 1L else 0L
            else -> throw  IllegalArgumentException()
        }
        this[params[2].toInt()] = result
    }
}
