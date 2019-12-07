package days

private fun main() {
    val result = day4part2(128392, 643281)

    print(result.size)
}

fun day4part2(start: Int, end: Int): List<Int> {
    return (start..end).toList().filter {
        val digits = it.toString().toList().map { it.toInt() }
        val hasGroupOfExactlyTwo = it.toString().toList().groupingBy { it }.eachCount().any { it.value == 2 }
        digits.sorted() == digits && hasGroupOfExactlyTwo
    }
}

fun day4part1(start: Int, end: Int): List<Int> {
    return (start..end).toList().filter {
        val digits = it.toString().toList().map { it.toInt() }
        digits.sorted() == digits && digits.toSet().size < digits.size
    }
}
