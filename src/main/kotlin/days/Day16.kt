package days

import digits

private fun main() {
    val ints = INPUT
        .map { "$it".toInt() }

    println(day16Part1(ints))
    println(day16Part2(ints))
}

fun day16Part2(inputs: List<Int>): String {
    val offset = inputs.take(7).joinToString("").toInt()
    val afterOffset = List(10_000) { inputs }.flatten().let { it.subList(offset, it.size) }

    val signal = afterOffset.toMutableList()

    repeat(100) {
        var sum = 0
        for (index in (signal.size - 1) downTo 0) {
            sum += signal[index]
            signal[index] = sum.digits().last()
        }
    }

    return signal.take(8).joinToString("")
}

fun day16Part1(inputs: List<Int>): String {
    return applyFft(inputs, 100).take(8).joinToString("")
}

fun applyFft(inputs: List<Int>, times: Int = 1): List<Int> {
    var signal = inputs

    repeat(times) {
        signal = signal.mapIndexed { inputsIndex, _ ->
            val pattern = PATTERN.repeated(inputsIndex + 1)

            val newDigit = signal
                .mapIndexed { digitIndex, digit ->
                    digit * pattern[(digitIndex + 1) % pattern.size]
                }
                .sum()
                .digits()
                .last()

            newDigit
        }
    }

    return signal
}

private fun <E> List<E>.repeated(times: Int): List<E> = this.flatMap { element -> List(times) { element } }

private val PATTERN = listOf(0, 1, 0, -1)
private const val INPUT =
    "59750530221324194853012320069589312027523989854830232144164799228029162830477472078089790749906142587998642764059439173975199276254972017316624772614925079238407309384923979338502430726930592959991878698412537971672558832588540600963437409230550897544434635267172603132396722812334366528344715912756154006039512272491073906389218927420387151599044435060075148142946789007756800733869891008058075303490106699737554949348715600795187032293436328810969288892220127730287766004467730818489269295982526297430971411865028098708555709525646237713045259603175397623654950719275982134690893685598734136409536436003548128411943963263336042840301380655801969822"