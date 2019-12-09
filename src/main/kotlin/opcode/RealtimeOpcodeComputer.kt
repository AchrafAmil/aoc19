package opcode

import digits

class RealtimeOpcodeComputer(software: List<Long>) {
    private val memory = software.toMutableList()
    private var relativeBase = 0

    var name: String? = null

    var inputStream = mutableListOf<Long>()
    var outputStream = mutableListOf<Long>()

    fun start() {
        var index = 0
        loop@ while (index < memory.size) {
            val instructionDigits = readMemory(index, MODE_IMMEDIATE).toInt().digits()

            val opcode = instructionDigits.takeLast(2).joinToString("").toInt()
            val paramsModes = instructionDigits.dropLast(2).reversed()
            when (opcode) {
                1, 2, 7, 8 -> {
                    applyInstruction(opcode, paramsModes, listOf(index + 1, index + 2, index + 3))
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
                        writeMemory(index + 1, inputStream.removeAt(0), MODE_POSITION)
                    }
                    index += 2
                }
                4 -> {
                    val paramMode = paramsModes.getOrElse(0) { 0 }
                    synchronized(outputStream) {
                        val value = readMemory(index + 1, paramMode)
                        println("$name writing $value to stream = $outputStream")
                        outputStream.add(value)
                    }
                    index += 2
                }
                5 -> {
                    index = jumpIfTrue(index, paramsModes)
                }
                6 -> {
                    index = jumpIfFalse(index, paramsModes)
                }
                9 -> {
                    relativeBase += paramsModes.getOrElse(0) { 0 }
                }
                99 -> {
                    println("$name about to halt. Bye.")
                    return
                }
                else -> throw IllegalStateException("unknown opcode instruction: $opcode")
            }
        }
    }

    private fun jumpIfTrue(
        index: Int,
        paramsModes: List<Int>
    ): Int = jumpIf(index, paramsModes, isJumpIfFalse = false)

    private fun jumpIfFalse(
        index: Int,
        paramsModes: List<Int>
    ): Int = jumpIf(index, paramsModes, isJumpIfFalse = true)

    private fun jumpIf(
        index: Int,
        paramsModes: List<Int>,
        isJumpIfFalse: Boolean
    ): Int {
        val firstParamMode = paramsModes.getOrElse(0) { 0 }
        val condition = readMemory(index + 1, firstParamMode).toInt()
        return if ((condition != 0) xor isJumpIfFalse) {
            val secondParamMode = paramsModes.getOrElse(1) { 0 }
            readMemory(index + 2, secondParamMode).toInt()
        } else {
            index + 3
        }
    }

    private fun applyInstruction(opcode: Int, paramsModes: List<Int>, params: List<Int>) {
        val first = readMemory(params[0], paramsModes.getOrElse(0) { MODE_POSITION })
        val second = readMemory(params[1], paramsModes.getOrElse(1) { MODE_POSITION })
        val result = when (opcode) {
            1 -> first + second
            2 -> first * second
            7 -> if (first < second) 1L else 0L
            8 -> if (first == second) 1L else 0L
            else -> throw  IllegalArgumentException()
        }
        writeMemory(params[2], result, paramsModes.getOrElse(2) { MODE_POSITION })
        println("instruction with opcode $opcode writing $result as result of $first & $second")
    }

    private fun readMemory(index: Int, accessMode: Int): Long {
        return try {
            when (accessMode) {
                MODE_POSITION -> memory[memory[index].toInt()]
                MODE_IMMEDIATE -> memory[index]
                MODE_RELATIVE -> memory[memory[index].toInt() + relativeBase]
                else -> throw IllegalArgumentException("parameter mode $accessMode not recognized")
            }
        } catch (_: IndexOutOfBoundsException) {
            DEFAULT_MEMORY_VALUE
        }
    }

    private fun writeMemory(index: Int, value: Long, accessMode: Int = 0) {
        when (accessMode) {
            MODE_POSITION -> memory[memory[index].toInt()] = value
            MODE_RELATIVE -> memory[memory[index].toInt() + relativeBase] = value
            MODE_IMMEDIATE -> throw IllegalArgumentException("tried to write in immediate mode")
            else -> throw IllegalArgumentException("parameter mode $accessMode not recognized")
        }
    }

    companion object {
        private const val DEFAULT_MEMORY_VALUE = 0L
        private const val MODE_POSITION = 0
        private const val MODE_IMMEDIATE = 1
        private const val MODE_RELATIVE = 2
    }
}
