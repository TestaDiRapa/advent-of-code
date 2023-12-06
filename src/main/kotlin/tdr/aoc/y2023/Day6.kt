package tdr.aoc.y2023

import tdr.aoc.utils.asListWithHeader
import tdr.aoc.utils.getInput
import tdr.aoc.utils.prod
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis

fun String.toTimeAndDistancePairs() = split("\n").let {
    val times = it.first().asListWithHeader<Int>().second
    val distances = it.last().asListWithHeader<Int>().second
    times.zip(distances)
}

fun xRange(time: Double, distance: Double): IntRange {
    val delta = (time*time) - 4 * (distance + 1)
    val x1 =  (sqrt(delta) - time) / -2
    val x2 =  (- sqrt(delta) - time) / -2
    val start = max(0.0, min(x1, x2))
    val end = max(x1, x2)
    return ceil(start).toInt() .. floor(end).toInt()
}


suspend fun main() {
    val input = getInput("2023", "6")

    val time = measureTimeMillis {
        // Part 1
        val timeDistanceCombinations = input.toTimeAndDistancePairs().map {
            val range = xRange(it.first.toDouble(), it.second.toDouble())
            range.last - range.first + 1
        }.prod()
        println("The possible race-winning combinations are: $timeDistanceCombinations")

        // Part 2
        val (firstLine, secondLine) = input.split("\n")
        val time = firstLine.split(":\\s+".toRegex()).last().replace("\\s+".toRegex(), "").toDouble()
        val distance = secondLine.split(":\\s+".toRegex()).last().replace("\\s+".toRegex(), "").toDouble()
        val longRaceRange = xRange(time, distance)
        val longRaceCombinations = longRaceRange.last - longRaceRange.first + 1
        println("The possible race-winning combinations of the long race are: $longRaceCombinations")
    }
    println("The total computation time is $time")

}