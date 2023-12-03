package tdr.aoc.y2023

import tdr.aoc.utils.Matrix
import tdr.aoc.utils.getInput

data class ProbablePart(
    val number: Int,
    val rowIdx: Int,
    val start: Int,
    val end: Int
) {

    companion object {
        fun new(rawNumber: String, row: Int, start: Int, end: Int) =
            ProbablePart(rawNumber.toInt(), row, start, end)
    }

}

data class Gear(val x: Int, val y: Int) {

    fun isAdjacent(part: ProbablePart) =
        (x in (part.rowIdx -1) .. (part.rowIdx + 1)) && (y in (part.start-1) .. (part.end + 1))

}

fun Matrix.findProbablePartsInSchematic(): List<ProbablePart> {
    var rowIdx = 0
    val probableParts = mutableListOf<ProbablePart>()
    val matchRegex = "[^0-9]*([0-9]+)[^0-9]*".toRegex()
    while(rowIdx < matrix.size) {
        val row = matrix[rowIdx]
        matchRegex.findAll(String(row.toCharArray())).mapNotNull { match ->
            match.groups[1]?.let {
                ProbablePart.new(
                    it.value,
                    rowIdx,
                    it.range.first,
                    it.range.last
                )
            }
        }.forEach {
            probableParts.add(it)
        }
        rowIdx++
    }
    return probableParts
}

fun Matrix.findGears(): List<Gear> {
    var rowIdx = 0
    val gears = mutableListOf<Gear>()
    val matchRegex = ".?(\\*).?".toRegex()
    while(rowIdx < matrix.size) {
        val row = matrix[rowIdx]
        matchRegex.findAll(String(row.toCharArray())).mapNotNull { match ->
            match.groups[1]?.let {
                Gear(rowIdx, it.range.first)
            }
        }.forEach {
            gears.add(it)
        }
        rowIdx++
    }
    return gears
}

suspend fun main() {
    val input = getInput("2023", "3").split("\n")
    val engine = Matrix.fromInput(input)

    // Part 1
    val probableParts = engine.findProbablePartsInSchematic()
    val sumOfParts = probableParts.filter {
        val subMatrix = engine.subMatrix(it.rowIdx-1, it.rowIdx+1, it.start-1, it.end+1)
        subMatrix.toString().replace("\n", "").contains("[^0-9.]".toRegex())
    }.sumOf { it.number }
    println("The sum of the engine parts is $sumOfParts")

    // Part 2
    val gears = engine.findGears()
    val sumOfGearRatios = gears.map { g ->
        probableParts.filter { g.isAdjacent(it) }
    }.filter {
        it.size == 2
    }.sumOf { it.first().number * it.last().number }
    println("The sum of all the gear ratios is $sumOfGearRatios")
}