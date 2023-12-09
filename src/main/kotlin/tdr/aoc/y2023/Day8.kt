package tdr.aoc.y2023

import tdr.aoc.utils.Directions
import tdr.aoc.utils.Graph
import tdr.aoc.utils.Tree
import tdr.aoc.utils.getInput
import java.lang.Exception

fun String.toListOfDirections() = map {
    Directions.valueOf(it.toString())
}

fun List<String>.toGraph(): Graph<String, Directions> {
    val graph = Graph<String, Directions>()
    return fold(graph) { acc, it ->
        val (node, rawLinks) = it.split("\\s+=\\s+".toRegex())
        val links = "\\(([A-Z]+),\\s+([A-Z]+)\\)".toRegex().find(it) ?: throw Exception("Match failed")
        acc.addNode(node, mapOf(
            Directions.L to links.groupValues[1],
            Directions.R to links.groupValues[2]
        ))
        acc
    }
}

fun zToZTree(graph: Graph<String, Directions>, next: String, visited: List<String> = emptyList()): Tree<String> {
    val right = graph.next(next, Directions.R)
    val left = graph.next(next, Directions.L)
    val leftNode =
        if(left == "ZZZ") Tree(left, null, null)
        else zToZTree(graph, left)
    val rightNode =
        if(right == "ZZZ") Tree(right, null, null)
        else zToZTree(graph, right)
    return Tree(next, leftNode, rightNode)
}

fun zToZDistance(
tree: Tree<String>?): Tree<Int> {
    if(tree == null) return Tree(0, null, null)
    val lDistance = zToZDistance(tree.left)
    val rDistance = zToZDistance(tree.right)
    return if(lDistance.value == rDistance.value) Tree(lDistance.value + 1, null, null)
    else Tree(0, lDistance, rDistance)
}
fun findPathAToZ(graph: Graph<String, Directions>, directions: List<Directions>): Int {
    var idx = 0
    var count = 0
    var node = "AAA"
    do {
        count++
        idx = (idx + 1) % directions.size
        val d = directions[idx]
        node = graph.next(node, d)
    } while (node != "ZZZ" || idx != (directions.size -1))
    return count
}

suspend fun main() {
    val input = getInput("2023", "8").split("\n")
    val directions = input.first().toListOfDirections()
    val graph = input.drop(1).toGraph()

    // Part 1
    val lTree = zToZTree(graph, graph.next("ZZZ", Directions.L))
    val rTree = zToZTree(graph, graph.next("ZZZ", Directions.R))
    val tree = Tree("ZZZ", lTree, rTree)
    println(tree)
    // val steps = findPathAToZ(graph, directions)
    // println("The number of steps needed to reach the destination is $steps")
}