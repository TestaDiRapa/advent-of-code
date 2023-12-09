package tdr.aoc.utils

import java.util.SortedMap
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.sqrt

fun Iterable<Int>.prod() = fold(1) { acc, it -> acc * it }

fun Iterable<Double>.prod() = fold(1.0) { acc, it -> acc * it }

data class PrimeFactors(
    val factors: SortedMap<Double, Int> = sortedMapOf()
) {

    fun addFactor(f: Double): PrimeFactors {
        if(factors.containsKey(f)) {
            factors[f] = factors.getValue(f) + 1
        } else {
            factors[f] = 1
        }
        return this
    }

    fun lcm(other: PrimeFactors): PrimeFactors {
        val commonFactors = sortedMapOf<Double, Int>()
        val common = factors.keys.intersect(other.factors.keys).onEach {
            commonFactors[it] = max(factors.getValue(it), other.factors.getValue(it))
        }
        (factors.keys - common).forEach {
            commonFactors[it] = factors[it]
        }
        (other.factors.keys - common).forEach {
            commonFactors[it] = other.factors[it]
        }
        return PrimeFactors(commonFactors)
    }

}

object PrimeFactorizer {

    private val primeNumbers: MutableList<Int> = mutableListOf()

    private fun Int.isPrime() = (2 .. ceil(sqrt(toDouble())).toInt()).none { this % it == 0 }

    fun primes(limit: Int) = sequence {
        yieldAll(primeNumbers.takeWhile { it <= limit })
        val end = primeNumbers.lastOrNull() ?: 2
        (end .. limit).forEach {
            if(it.isPrime()) {
                primeNumbers.add(it)
                yield(it)
            }
        }

    }

    tailrec fun factorize(number: Int, factors: PrimeFactors = PrimeFactors()): PrimeFactors {
        if(number == 1) return factors
        val factor = primes(number/2).firstOrNull { number % it == 0 } ?: number
        return factorize(number/factor, factors.addFactor(factor.toDouble()))
    }

}