package tdr.aoc.utils

fun Iterable<Int>.prod() = fold(1) { acc, it -> acc * it }