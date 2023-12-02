package tdr.aoc.y2023

import tdr.aoc.utils.getInput
import tdr.aoc.utils.prod

enum class Colors { RED, GREEN, BLUE }
data class Sample(val qty: Int, val color: Colors)
data class Extraction(val samples: List<Sample>) {

    fun isPlausible(truth: Map<Colors, Int>) =
        samples.groupBy { it.color }.mapValues { (_, v) ->
            v.sumOf { it.qty }
        }.entries.all { (k, v) ->
            truth[k]?.let {
                v <= it
            } ?: false
        }
}

data class Game(val extractions: List<Extraction>) {

    fun getPower() = Colors.entries.map { c ->
            extractions.maxOf { e ->
                e.samples.filter { it.color == c }.maxOfOrNull { it.qty } ?: 0
            }
        }.prod()

}

fun String.toMapOfGames() = split("\n").associate { line ->
    val (id, sets) = line.split(": ", limit = 2)
    id.split(" ", limit = 2).last().toInt() to sets.split("; ").map { e ->
        e.split(", ").map {
            val (num, color) = it.split(" ")
            Sample(num.toInt(), Colors.valueOf(color.uppercase()))
        }.let {
            Extraction(it)
        }
    }.let { Game(it) }
}

suspend fun main() {
    val input = getInput("2023", "2").toMapOfGames()

    // Part 1
    val truth = mapOf(Colors.RED to 12, Colors.GREEN to 13, Colors.BLUE to 14)
    val sumOfPossibleGames = input.entries.filter { (_, game) ->
        game.extractions.all { it.isPlausible(truth) }
    }.sumOf { it.key }
    println("The sum of all possible games is: $sumOfPossibleGames")

    // Part 2
    val sumOfPowers = input.values.sumOf { it.getPower() }
    println("The sum of the powers of the games is: $sumOfPowers")
}