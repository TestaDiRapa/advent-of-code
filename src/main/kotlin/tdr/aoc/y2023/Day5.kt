package tdr.aoc.y2023

import tdr.aoc.utils.Range
import tdr.aoc.utils.RangeSet
import tdr.aoc.utils.getInput
import tdr.aoc.utils.toListOfLong
import java.lang.Exception
import kotlin.system.measureTimeMillis

//fun getSeeds(input: String): List<Long> {
//    val (_, rawSeeds) = input.split(":\\s+".toRegex())
//    return rawSeeds.toListOfLong("\\s+".toRegex())
//}
//
//fun getSeedsAsRange(input: String): Sequence<Long> {
//    val ranges = getSeeds(input).chunked(2)
//    return sequence {
//        ranges.forEach { r ->
//            val (start, range) = r
//            yieldAll(start until (start + range))
//        }
//    }
//}
//
//fun List<String>.toMappingFlow(): Pair<List<String>, MappingFlow> = fold(Pair(emptyList<String>(), emptyMap<String, Mapper>())) { acc, it ->
//        val (flow, mappings) = acc
//        val lines = it.split("\n")
//        val flowStep = "([a-z\\-]+) map:".toRegex().find(lines.first())?.groupValues?.get(1)
//            ?: throw Exception("Cannot find flow name in ${lines.first()}")
//        val mapper = lines.drop(1).map {
//            val (dest, src, step) = it.toListOfLong("\\s+".toRegex())
//            Mapping(src, dest, step)
//        }.let { Mapper(it) }
//        Pair(
//            flow + flowStep,
//            mappings + (flowStep to mapper)
//        )
//    }.let {
//        Pair(it.first, MappingFlow(it.second))
//    }
//
//data class Mapping(val source: Long, val destination: Long, val range: Long) {
//    fun map(input: Long) =
//        if(input in (source until (source + range)))
//            destination + (input - source)
//        else null
//}
//
//data class Mapper(
//    val mappings: List<Mapping>
//) {
//
//    fun map(input: Long) = mappings.firstNotNullOfOrNull {
//        it.map(input)
//    } ?: input
//
//}
//
//data class MappingFlow(
//    val maps: Map<String, Mapper>
//) {
//
//    tailrec fun mapEndToEnd(input: Long, flow: List<String>): Long {
//        if(flow.isEmpty()) return input
//        val mapper = maps.getValue(flow.first())
//        return mapEndToEnd(mapper.map(input), flow.drop(1))
//    }
//
//}

data class RangeMap(
    val wholeRange: RangeSet = RangeSet(emptyList()),
    val rangeMap: Map<Range, Range> = emptyMap()
) {

    fun map(input: RangeSet): RangeSet {
        val matchingRange = rangeMap.entries.flatMap { (src, dst) ->
            val offset = dst.start - src.start
            input.ranges.mapNotNull {
                val intersection = src intersect it
                if(!intersection.isEmpty) {
                    Range(
                        intersection.start + offset,
                        intersection.end + offset
                    )
                } else null
            }
        }.fold(RangeSet(emptyList())) { acc, it ->
            acc union it
        }
        val fullRange = wholeRange.complement().ranges.flatMap { complementRange ->
            input.ranges.mapNotNull {
                val intersection = complementRange intersect it
                if(!intersection.isEmpty) intersection else null
            }
        }.fold(matchingRange) { acc, it ->
            acc union it
        }
        return fullRange
    }

}

data class MappingFlow(
    val maps: Map<String, RangeMap>
) {

    tailrec fun mapEndToEnd(input: RangeSet, flow: List<String>): RangeSet {
        if(flow.isEmpty()) return input
        val mapper = maps.getValue(flow.first())
        return mapEndToEnd(mapper.map(input), flow.drop(1))
    }

}

fun getSeeds(input: String): List<RangeSet> {
    val (_, rawSeeds) = input.split(":\\s+".toRegex())
    return rawSeeds.toListOfLong("\\s+".toRegex()).map {
        RangeSet(listOf(Range(it, it)))
    }
}

fun getSeedsAsPairs(input: String): List<RangeSet> {
    val (_, rawSeeds) = input.split(":\\s+".toRegex())
    return rawSeeds.toListOfLong("\\s+".toRegex()).chunked(2).map {
        RangeSet(listOf(Range(it.first(), it.first() + it.last() - 1)))
    }
}

fun List<String>.toMappingFlow(): Pair<List<String>, MappingFlow> = fold(Pair(emptyList<String>(), emptyMap<String, RangeMap>())) { acc, it ->
        val (flow, mappings) = acc
        val lines = it.split("\n")
        val flowStep = "([a-z\\-]+) map:".toRegex().find(lines.first())?.groupValues?.get(1)
            ?: throw Exception("Cannot find flow name in ${lines.first()}")
        val mapper = lines.drop(1).fold(RangeMap()) { map, line ->
            val (dest, src, step) = line.toListOfLong("\\s+".toRegex())
            RangeMap(
                map.wholeRange union Range(src, src + step - 1),
                map.rangeMap + (Range(src, src + step - 1) to Range(dest, dest + step - 1))
            )
        }
        Pair(
            flow + flowStep,
            mappings + (flowStep to mapper)
        )
    }.let {
        Pair(it.first, MappingFlow(it.second))
    }

suspend fun main() {
    val input = getInput("2023", "5")
    val blocks = input.split("\n\n")
    val seeds = getSeeds(blocks.first())
    val (flow, mappingFlow) = blocks.drop(1).toMappingFlow()

    val totalTime = measureTimeMillis {
        // Part 1
        val minimalPlace = seeds.minOf {
            mappingFlow.mapEndToEnd(it, flow).min()
        }
        println("The minimum of the location ids is: $minimalPlace")

        // Part 2
        val minimalPlaceAsRange = getSeedsAsPairs(blocks.first()).minOf {
            mappingFlow.mapEndToEnd(it, flow).min()
        }
        println("The minimum of the location ids reading the seeds as ranges is: $minimalPlaceAsRange")
    }
    println("The total execution time is: $totalTime ms")

}