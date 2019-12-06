package days

private fun main() {
    val ints = INPUT
        .split(',')
        .map { it.toInt() }

    print(day2Part2(ints))
}

fun compute(ints: String): String = compute(ints.split(',').map { it.toInt() }).joinToString(",")

fun day2Part1(ints: List<Int>): Int {
    return compute(ints).first()
}

fun day2Part2(ints: List<Int>): Pair<Int, Int> {
    for (noun in 0..99) {
        for (verb in 0..99) {
            val attempt = ints.mapIndexed { index, i ->
                when (index) {
                    1 -> noun
                    2 -> verb
                    else -> i
                }
            }
            if (compute(attempt).first() == 19690720) return Pair(noun, verb)
        }
    }
    throw IllegalStateException()
}

private fun compute(ints: List<Int>): List<Int> {
    var memory = ints
    loop@ for (i in memory.indices step 4) {
        if (i + 4 >= memory.size) break
        val instruct = memory.subList(i, i + 4)
        when (instruct[0]) {
            1, 2 -> memory = memory.applyInstruct(instruct)
            99 -> break@loop
            else -> throw IllegalStateException()
        }
    }
    return memory
}

private fun List<Int>.applyInstruct(instruct: List<Int>): List<Int> {
    val first = this[instruct[1]]
    val second = this[instruct[2]]
    val result = if (instruct[0] == 1) first + second else first * second
    return this.mapIndexed { index, i ->
        if (index == instruct[3]) result else i
    }
}


private const val INPUT =
    "1,12,2,3,1,1,2,3,1,3,4,3,1,5,0,3,2,1,13,19,1,9,19,23,2,13,23,27,2,27,13,31,2,31,10,35,1,6,35,39,1,5,39,43,1,10,43,47,1,5,47,51,1,13,51,55,2,55,9,59,1,6,59,63,1,13,63,67,1,6,67,71,1,71,10,75,2,13,75,79,1,5,79,83,2,83,6,87,1,6,87,91,1,91,13,95,1,95,13,99,2,99,13,103,1,103,5,107,2,107,10,111,1,5,111,115,1,2,115,119,1,119,6,0,99,2,0,14,0"