fun Int.digits(): List<Int> = toString().toList().map { "$it".toInt() }

fun lcm(x: Long, y: Long, z: Long): Long = lcm(lcm(x, y), z)

fun lcm(x: Long, y: Long): Long = (x * y) / gcd(x, y)

fun gcd(x: Long, y: Long): Long = if (y != 0L) gcd(y, x % y) else x