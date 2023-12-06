package tdr.aoc.utils

fun String.toListOfInt(separator: Regex) = split(separator).map { it.toInt() }

fun String.toListOfLong(separator: Regex) = split(separator).map { it.toLong() }

inline fun <reified T> String.toListOf(separator: Regex): List<T> = split(separator).map {
    when(T::class) {
        Int::class -> it.toInt()
        Long::class -> it.toLong()
        else -> throw IllegalArgumentException("No type conversion available for type ${T::class.simpleName}")
    } as T
}

inline fun <reified T> String.asListWithHeader(): Pair<String, List<T>> {
    val (header, values) = split(":\\s+".toRegex())
    return header to values.toListOf("\\s+".toRegex())
}