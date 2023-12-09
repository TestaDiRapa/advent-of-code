package tdr.aoc.y2023

import tdr.aoc.utils.getInput
import tdr.aoc.utils.toListOf

tailrec fun predictNewValue(input: List<Long>, output: Long): Long =
    if(input.all { it == 0L }) output
    else {
        val derivative = input.zipWithNext().map { (a, b) ->
            b - a
        }
        predictNewValue(derivative, output + derivative.last())
    }

suspend fun main() {
    val input = getInput("2023", "9").split("\n").map {
        it.toListOf<Long>("\\s+".toRegex())
    }

    // Part 1
    val sumOfPredictions = input.sumOf {
        predictNewValue(it, it.last())
    }
    println("The sum of the predicted values is: $sumOfPredictions")

    // Part 2
    val sumOfOldPredictions = input.sumOf {
        predictNewValue(it.reversed(), it.first())
    }
    println("The sum of the predicted old values is: $sumOfOldPredictions")
}