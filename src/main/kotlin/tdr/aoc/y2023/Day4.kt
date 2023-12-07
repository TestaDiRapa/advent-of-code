package tdr.aoc.y2023

import tdr.aoc.utils.getInput
import tdr.aoc.utils.toListOfInt
import kotlin.math.pow

data class ScratchCard(
    val id: Int,
    val winningNumbers: List<Int>,
    val numbers: List<Int>
) {

    val points = numbers.toSet().intersect(winningNumbers.toSet()).let {
        if(it.isEmpty()) 0
        else (2.0.pow(it.size - 1)).toInt()
    }

    val matches = numbers.toSet().intersect(winningNumbers.toSet()).size

}

fun List<String>.toCards() = map {
    val (rawId, allNumbers) = it.split(":\\s+".toRegex())
    val (_, id) = rawId.split("\\s+".toRegex())
    val (rawWinning, rawYours) = allNumbers.split("\\s+\\|\\s+".toRegex())
    val winning = rawWinning.toListOfInt("\\s+".toRegex())
    val yours = rawYours.toListOfInt("\\s+".toRegex())
    ScratchCard(id.toInt(), winning, yours)
}

suspend fun main() {
    val input = getInput("2023", "4")
    val cards = input.split("\n").toCards()

    // Part 1
    val totalPoints = cards.sumOf { it.points }
    println("The total value of the cards is: $totalPoints")

    // Part 2
    val scratchCards = Array(cards.size) { 1 }
    cards.forEach { card ->
        val copies = scratchCards[card.id - 1]
        (1 .. card.matches).forEach {
            val nextCardIndex = it + card.id - 1
            if(nextCardIndex < scratchCards.size) {
                scratchCards[nextCardIndex] += copies
            }
        }
    }
    println("The total number of scratchcards is: ${scratchCards.sum()}")
}