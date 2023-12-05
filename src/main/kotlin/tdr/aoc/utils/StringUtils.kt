package tdr.aoc.utils

fun String.toListOfInt(separator: Regex) = split(separator).map { it.toInt() }

fun String.toListOfLong(separator: Regex) = split(separator).map { it.toLong() }