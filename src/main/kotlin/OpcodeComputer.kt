class OpcodeComputer(software: List<Int>) {
    private val memory = software.toMutableList()

    fun compute(args: List<Int>): List<Int> {
        val input = args.toMutableList()
        val output = mutableListOf<Int>()
        var index = 0

        loop@ while (index < memory.size) {
            val instructionDigits = memory[index].digits()

            val opcode = instructionDigits.takeLast(2).joinToString("").toInt()
            val paramsModes = instructionDigits.dropLast(2).reversed()
            when (opcode) {
                1, 2, 7, 8 -> {
                    val params = listOf(memory[index + 1], memory[index + 2], memory[index + 3])
                    memory.applyInstruction(opcode, paramsModes, params)
                    index += 4
                }
                3 -> {
                    memory[memory[index + 1]] = input.removeAt(0)
                    index += 2
                }
                4 -> {
                    val paramMode = paramsModes.getOrElse(0) { 0 }
                    output.add(memory.accessParam(paramMode, index + 1))
                    index += 2
                }
                5 -> {
                    index = memory.jumpIfTrue(index, paramsModes)
                }
                6 -> {
                    index = memory.jumpIfFalse(index, paramsModes)
                }
                99 -> break@loop
                else -> throw IllegalStateException()
            }
        }

        return output
    }

    private fun MutableList<Int>.jumpIfTrue(
        index: Int,
        paramsModes: List<Int>
    ): Int = jumpIf(index, paramsModes, isJumpIfFalse = false)

    private fun MutableList<Int>.jumpIfFalse(
        index: Int,
        paramsModes: List<Int>
    ): Int = jumpIf(index, paramsModes, isJumpIfFalse = true)

    private fun MutableList<Int>.jumpIf(
        index: Int,
        paramsModes: List<Int>,
        isJumpIfFalse: Boolean
    ): Int {
        val firstParamMode = paramsModes.getOrElse(0) { 0 }
        val condition = accessParam(firstParamMode, index + 1)
        if ((condition != 0) xor isJumpIfFalse) {
            val secondParamMode = paramsModes.getOrElse(1) { 0 }
            return accessParam(secondParamMode, index + 2)
        } else {
            return index + 3
        }
    }

    private fun MutableList<Int>.accessParam(
        mode: Int,
        index: Int
    ) = when (mode) {
        0 -> this[this[index]]
        1 -> this[index]
        else -> throw IllegalArgumentException("parameter mode $mode not recognized")
    }

    private fun MutableList<Int>.applyInstruction(opcode: Int, paramsModes: List<Int>, params: List<Int>) {
        val first = if (paramsModes.getOrNull(0) == 1) params[0] else this[params[0]]
        val second = if (paramsModes.getOrNull(1) == 1) params[1] else this[params[1]]
        val result = when (opcode) {
            1 -> first + second
            2 -> first * second
            7 -> if (first < second) 1 else 0
            8 -> if (first == second) 1 else 0
            else -> throw  IllegalArgumentException()
        }
        this[params[2]] = result
    }
}