package tdr.aoc.y2023

import tdr.aoc.utils.getInput

val numToDigit = mapOf("one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5, "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9)

val numberRegex = ("^(" + ('0' .. '9').joinToString("|") { "($it)" } + "|" + numToDigit.keys.joinToString("|") { "($it)" } + ")").toRegex()

fun retrieveNumberFromLine(line: String) =
    (line.first { it in ('0'..'9') }.digitToInt() * 10) + line.last { it in '0'..'9' }.digitToInt()

tailrec fun parseLineExtractingNumbers(line: String, numbers: List<Int> = emptyList()): List<Int> {
    if(line.isBlank()) return numbers
    val match = numberRegex.find(line)?.value
    return when {
        match == null -> parseLineExtractingNumbers(line.substring(1), numbers)
        match.length == 1 -> parseLineExtractingNumbers(line.substring(1), numbers + match.toInt())
        else -> parseLineExtractingNumbers(line.substring(1), numbers + numToDigit.getValue(match))
    }
}

suspend fun main() {
    val sumOfCalibrationValues = getInput("2023", "1").split("\n").sumOf {
        retrieveNumberFromLine(it)
    }
    println("The sum of the calibration values is: $sumOfCalibrationValues")

    val sumOfIncludingWords = getInput("2023", "1").split("\n").sumOf {
        val parsedLine = parseLineExtractingNumbers(it)
        (parsedLine.first() * 10) + parsedLine.last()
    }
    println("The sum of the calibration values including text numbers is: $sumOfIncludingWords")

}