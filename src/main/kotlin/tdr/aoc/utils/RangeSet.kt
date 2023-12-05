package tdr.aoc.utils

import kotlin.math.max
import kotlin.math.min

data class Range(val start: Long, val end: Long) {
    val isEmpty = start > end

    infix fun intersect(other: Range): Range =
        if(isEmpty || other.isEmpty || other.end < start || end < other.start) Range(0, -1)
        else Range(
            max(start, other.start),
            min(end, other.end)
        )
}

class RangeSet(
    ranges: List<Range>
) {

    val ranges = ranges.sortedBy { it.start }

    fun isEmpty() = ranges.isEmpty() || ranges.all { it.isEmpty }

    infix fun intersect(other: Range): RangeSet =
        if(isEmpty() || other.isEmpty || other.end < ranges.first().start || ranges.last().end < other.start) RangeSet(emptyList())
        else ranges.filter {
                (other.start <= it.start && other.end >= it.start) ||
                (other.start <= it.end && other.end >= it.end) ||
                (other.start >= it.start && other.end <= it.end)
            }.map {
                Range(
                    max(other.start, it.start),
                    min(other.end, it.end)
                )
            }.let {
                RangeSet(it)
            }

    infix fun union(other: Range): RangeSet =
        when {
            isEmpty() && other.isEmpty -> RangeSet(emptyList())
            isEmpty() -> RangeSet(listOf(other))
            other.isEmpty -> this
            other.end < ranges.first().start -> RangeSet(listOf(other) + ranges)
            ranges.last().end < other.start -> RangeSet(ranges + other)
            else -> {
                val (adjacent, nonAdjacent) = ranges.partition {
                    (other.start <= it.start && other.end >= it.start) ||
                    (other.start <= it.end && other.end >= it.end) ||
                    (other.start >= it.start && other.end <= it.end)
                }
                if (adjacent.isNotEmpty()) {
                    val merged = Range(
                        adjacent.minOf { it.start },
                        adjacent.maxOf { it.end }
                    )
                    RangeSet(nonAdjacent + merged)
                } else {
                    RangeSet(nonAdjacent + other)
                }
            }
        }

    fun complement(): RangeSet =
        if(isEmpty()) this
        else (0 until (ranges.size - 1)).mapNotNull { idx ->
            Range(ranges[idx].end + 1, ranges[idx+1].start - 1).takeIf { !it.isEmpty }
        }.let { RangeSet(it + Range(Long.MIN_VALUE, ranges.first().start - 1) + Range(ranges.last().end + 1, Long.MAX_VALUE)) }

    fun min() = ranges.first().start

}