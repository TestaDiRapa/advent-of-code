package tdr.aoc.utils

fun String.toListOfInt(separator: Regex) = split(separator).map { it.toInt() }

fun String.toListOfInt(separator: String) = toListOfInt(separator.toRegex())