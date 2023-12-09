package tdr.aoc.y2023

import tdr.aoc.utils.Directions
import tdr.aoc.utils.Graph
import tdr.aoc.utils.PrimeFactorizer
import tdr.aoc.utils.PrimeFactors
import tdr.aoc.utils.getInput
import tdr.aoc.utils.prod
import java.lang.Exception
import kotlin.math.pow

fun String.toListOfDirections() = map {
    Directions.valueOf(it.toString())
}

fun List<String>.toGraph(): Graph<String, Directions> {
    val graph = Graph<String, Directions>()
    return fold(graph) { acc, it ->
        val (node, _) = it.split("\\s+=\\s+".toRegex())
        val links = "\\(([A-Z]+),\\s+([A-Z]+)\\)".toRegex().find(it) ?: throw Exception("Match failed")
        acc.addNode(node, mapOf(
            Directions.L to links.groupValues[1],
            Directions.R to links.groupValues[2]
        ))
        acc
    }
}

fun findPathAToZ(graph: Graph<String, Directions>, directions: List<Directions>): Int {
    var count = 0
    var node = "AAA"
    do {
        val d = directions[count % directions.size]
        count++
        node = graph.next(node, d)
    } while (node != "ZZZ" || (count % directions.size) != 0)
    return count
}

fun findCycles(graph: Graph<String, Directions>, directions: List<Directions>, startNode: String): List<Int> {
    var count = 0
    var node = startNode
    do {
        val d = directions[count % directions.size]
        count++
        node = graph.next(node, d)
    } while (!node.endsWith("Z"))
    val cycleNode = node
    val cycles = mutableListOf(count)
    do {
        val d = directions[count % directions.size]
        count++
        node = graph.next(node, d)
        if(node.endsWith("Z")) {
            val last = cycles.last()
            cycles.add(count - last)
        }
    } while (node != cycleNode || (count % directions.size) != 0)
    return cycles
}

suspend fun main() {
    val input = getInput("2023", "8").split("\n")
    val directions = input.first().toListOfDirections()
    val graph = input.drop(1).toGraph()

    // Part 1
    val steps = findPathAToZ(graph, directions)
    println("The total steps to go from AAA to ZZZ in a whole set of instruction is: $steps")

    // Part 2
    val aNodes = graph.nodes.keys.filter { it.endsWith("A") }
    val cycles = aNodes.map { findCycles(graph, directions, it) }
    // From this point going on, it works only because the input is weird
    val factorizer = PrimeFactorizer
    val primeSteps = cycles.map {
        factorizer.factorize(it.first())
    }.fold(PrimeFactors()) { acc, it ->
        acc.lcm(it)
    }.factors.entries.map { (k, v) ->
        k.toDouble().pow(v)
    }.prod().toLong()
    println("The total steps to go from multiple As to multiple Zs is $primeSteps")
}