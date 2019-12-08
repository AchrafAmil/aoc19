package days

import digits

private fun main() {
    val ints = INPUT
            .split(',')
            .map { it.toInt() }

    print(day5Part2(ints))
}

fun day5Part2(ints: List<Int>): List<Int> {
    return compute(ints, listOf(5))
}

fun day5Part1(ints: List<Int>): List<Int> {
    return compute(ints, listOf(1))
}

fun compute(ints: List<Int>, args: List<Int>): List<Int> {
    val memory = ints.toMutableList()
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

private const val INPUT =
        "3,225,1,225,6,6,1100,1,238,225,104,0,1002,92,42,224,1001,224,-3444,224,4,224,102,8,223,223,101,4,224,224,1,224,223,223,1102,24,81,225,1101,89,36,224,101,-125,224,224,4,224,102,8,223,223,101,5,224,224,1,224,223,223,2,118,191,224,101,-880,224,224,4,224,1002,223,8,223,1001,224,7,224,1,224,223,223,1102,68,94,225,1101,85,91,225,1102,91,82,225,1102,85,77,224,101,-6545,224,224,4,224,1002,223,8,223,101,7,224,224,1,223,224,223,1101,84,20,225,102,41,36,224,101,-3321,224,224,4,224,1002,223,8,223,101,7,224,224,1,223,224,223,1,188,88,224,101,-183,224,224,4,224,1002,223,8,223,1001,224,7,224,1,224,223,223,1001,84,43,224,1001,224,-137,224,4,224,102,8,223,223,101,4,224,224,1,224,223,223,1102,71,92,225,1101,44,50,225,1102,29,47,225,101,7,195,224,101,-36,224,224,4,224,102,8,223,223,101,6,224,224,1,223,224,223,4,223,99,0,0,0,677,0,0,0,0,0,0,0,0,0,0,0,1105,0,99999,1105,227,247,1105,1,99999,1005,227,99999,1005,0,256,1105,1,99999,1106,227,99999,1106,0,265,1105,1,99999,1006,0,99999,1006,227,274,1105,1,99999,1105,1,280,1105,1,99999,1,225,225,225,1101,294,0,0,105,1,0,1105,1,99999,1106,0,300,1105,1,99999,1,225,225,225,1101,314,0,0,106,0,0,1105,1,99999,107,677,677,224,1002,223,2,223,1006,224,329,1001,223,1,223,1108,226,677,224,102,2,223,223,1006,224,344,101,1,223,223,1107,226,226,224,1002,223,2,223,1006,224,359,101,1,223,223,8,677,226,224,1002,223,2,223,1006,224,374,1001,223,1,223,1107,677,226,224,102,2,223,223,1005,224,389,1001,223,1,223,1008,677,677,224,1002,223,2,223,1006,224,404,1001,223,1,223,108,677,677,224,102,2,223,223,1005,224,419,1001,223,1,223,1107,226,677,224,102,2,223,223,1006,224,434,101,1,223,223,1008,226,226,224,1002,223,2,223,1006,224,449,1001,223,1,223,107,226,226,224,102,2,223,223,1006,224,464,1001,223,1,223,1007,677,226,224,1002,223,2,223,1006,224,479,1001,223,1,223,1108,226,226,224,102,2,223,223,1006,224,494,1001,223,1,223,8,226,226,224,1002,223,2,223,1005,224,509,1001,223,1,223,7,226,677,224,102,2,223,223,1005,224,524,101,1,223,223,1008,677,226,224,102,2,223,223,1005,224,539,101,1,223,223,107,226,677,224,1002,223,2,223,1006,224,554,1001,223,1,223,1108,677,226,224,102,2,223,223,1005,224,569,101,1,223,223,108,226,226,224,1002,223,2,223,1005,224,584,1001,223,1,223,7,677,226,224,1002,223,2,223,1005,224,599,1001,223,1,223,108,226,677,224,1002,223,2,223,1006,224,614,101,1,223,223,1007,677,677,224,1002,223,2,223,1006,224,629,101,1,223,223,7,677,677,224,102,2,223,223,1005,224,644,101,1,223,223,1007,226,226,224,1002,223,2,223,1006,224,659,1001,223,1,223,8,226,677,224,102,2,223,223,1005,224,674,1001,223,1,223,4,223,99,226"
