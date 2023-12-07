package tdr.aoc.y2023

import tdr.aoc.utils.getInput

// region withoutJoker
enum class Card(val seed: String, val value: Int) {
    CA("A", 12),
    CK("K", 11),
    CQ("Q", 10),
    CJ("J", 9),
    CT("T", 8),
    C9("9", 7),
    C8("8", 6),
    C7("7", 5),
    C6("6", 4),
    C5("5", 3),
    C4("4", 2),
    C3("3", 1),
    C2("2", 0);

    companion object {
        fun fromSeed(seed: String) = Card.entries.first {
            it.seed == seed.uppercase()
        }
    }
}

enum class HandType(val value: Int, val isType: (List<Card>) -> Boolean) {
    FIVE_OF_KIND(6, { cards ->
        require(cards.size == 5)
        cards.groupBy { it.value }.size == 1
    }),
    FOUR_OF_KIND(5, { cards ->
        require(cards.size == 5)
        cards.groupBy { it.value }.let {
            it.size == 2 && it.values.any { c -> c.size == 4 }
        }
    }),
    FULL_HOUSE(4, { cards ->
        require(cards.size == 5)
        cards.groupBy { it.value }.let {
            it.size == 2 && it.values.any { c -> c.size == 3 } && it.values.any { c -> c.size == 2 }
        }
    }),
    THREE_OF_KIND(3, { cards ->
        require(cards.size == 5)
        cards.groupBy { it.value }.let {
            it.size == 3 && it.values.any { c -> c.size == 3 } && it.values.any { c -> c.size == 1 }
        }
    }),
    TWO_PAIR(2, { cards ->
        require(cards.size == 5)
        cards.groupBy { it.value }.let {
            it.size == 3 && it.values.count { c -> c.size == 2 } == 2
        }
    }),
    ONE_PAIR(1, { cards ->
        require(cards.size == 5)
        cards.groupBy { it.value }.size == 4
    }),
    HIGH_CARD(0, { cards ->
        require(cards.size == 5)
        cards.groupBy { it.value }.size == 5
    });

    companion object {
        fun classifyCards(cards: List<Card>) = HandType.entries.first { it.isType(cards) }
    }
}

tailrec fun compareCards(cards1: List<Card>, cards2: List<Card>): Int {
    require(cards1.size == cards2.size)
    return when {
        cards1.isEmpty() -> 0
        cards1.first() != cards2.first() -> cards1.first().value.compareTo(cards2.first().value)
        else -> compareCards(cards1.drop(1), cards2.drop(1))
    }
}

data class Hand(
    val cards: List<Card>,
    val bid: Int
) : Comparable<Hand> {
    val type = HandType.classifyCards(cards)

    override fun compareTo(other: Hand): Int = when {
        type != other.type -> type.value.compareTo(other.type.value)
        else -> compareCards(cards, other.cards)
    }

}

fun String.toListOfHands() = split("\n").map {
    val (rawCards, rawBid) = it.split("\\s+".toRegex())
    val cards = rawCards.map { c -> Card.fromSeed(c.toString()) }
    Hand(cards, rawBid.toInt())
}

// endregion

// region withJoker

enum class CardWithJoker(val seed: String, val value: Int) {
    CA("A", 12),
    CK("K", 11),
    CQ("Q", 10),
    CT("T", 9),
    C9("9", 8),
    C8("8", 7),
    C7("7", 6),
    C6("6", 5),
    C5("5", 4),
    C4("4", 3),
    C3("3", 2),
    C2("2", 1),
    CJ("J", 0);

    companion object {
        fun fromSeed(seed: String) = CardWithJoker.entries.first {
            it.seed == seed.uppercase()
        }
    }
}

fun List<CardWithJoker>.haveJoker() = any { it == CardWithJoker.CJ }

enum class HandTypeWithJoker(val value: Int, val isType: (List<CardWithJoker>) -> Boolean) {
    FIVE_OF_KIND(6, { cards ->
        require(cards.size == 5)
        val joker = cards.haveJoker()
        cards.groupBy { it }.let {
            it.size == 1 || (it.size == 2 && joker)
        }
    }),
    FOUR_OF_KIND(5, { cards ->
        require(cards.size == 5)
        val joker = cards.haveJoker()
        cards.groupBy { it }.let {
            (it.size == 2 && it.values.any { c -> c.size == 4 }) ||
            (joker && it.size == 3 && (
                it.filterKeys { k -> k != CardWithJoker.CJ }.values.maxOf { v -> v.size } + it.getValue(CardWithJoker.CJ).size == 4
            ))
        }
    }),
    FULL_HOUSE(4, { cards ->
        require(cards.size == 5)
        val joker = cards.haveJoker()
        cards.groupBy { it }.let {
            (!joker && it.size == 2 && it.values.any { c -> c.size == 3 } && it.values.any { c -> c.size == 2 }) ||
            (joker && it.size == 3 && it.getValue(CardWithJoker.CJ).size == 1 && it.values.any { c -> c.size == 2 })
        }
    }),
    THREE_OF_KIND(3, { cards ->
        require(cards.size == 5)
        val joker = cards.haveJoker()
        cards.groupBy { it }.let {
            (!joker && it.size == 3 && it.values.any { c -> c.size == 3 } && it.values.any { c -> c.size == 1 }) ||
            (joker && it.size == 4 &&
                (it.filterKeys { k -> k != CardWithJoker.CJ }.values.maxOf { v -> v.size } + it.getValue(CardWithJoker.CJ).size) == 3
            )
        }
    }),
    TWO_PAIR(2, { cards ->
        require(cards.size == 5)
        val joker = cards.haveJoker()
        cards.groupBy { it }.let {
            (!joker && it.size == 3 && it.values.count { c -> c.size == 2 } == 2)
        }
    }),
    ONE_PAIR(1, { cards ->
        require(cards.size == 5)
        val joker = cards.haveJoker()
        cards.groupBy { it }.let {
            (!joker && it.size == 4) || (it.size == 5 && joker)
        }
    }),
    HIGH_CARD(0, { cards ->
        require(cards.size == 5)
        cards.groupBy { it.value }.size == 5 && !cards.haveJoker()
    });

    companion object {
        fun classifyCards(cards: List<CardWithJoker>): HandTypeWithJoker {
            return HandTypeWithJoker.entries.first {
                it.isType(cards)
            }
        }
    }
}

tailrec fun compareCardsWithJoker(cards1: List<CardWithJoker>, cards2: List<CardWithJoker>): Int {
    require(cards1.size == cards2.size)
    return when {
        cards1.isEmpty() -> 0
        cards1.first() != cards2.first() -> cards1.first().value.compareTo(cards2.first().value)
        else -> compareCardsWithJoker(cards1.drop(1), cards2.drop(1))
    }
}

data class HandWithJoker(
    val cards: List<CardWithJoker>,
    val bid: Int
) : Comparable<HandWithJoker> {
    val type = HandTypeWithJoker.classifyCards(cards)

    override fun compareTo(other: HandWithJoker): Int = when {
        type != other.type -> type.value.compareTo(other.type.value)
        else -> compareCardsWithJoker(cards, other.cards)
    }

}

fun String.toListOfHandsWithJoker() = split("\n").map {
    val (rawCards, rawBid) = it.split("\\s+".toRegex())
    val cards = rawCards.map { c -> CardWithJoker.fromSeed(c.toString()) }
    HandWithJoker(cards, rawBid.toInt())
}

// endregion

suspend fun main() {
    val input = getInput("2023", "7")

    // Part 1
    val totalWinnings = input.toListOfHands().sorted().mapIndexed { idx, hand ->
        hand.bid * (idx + 1)
    }.sum()
    println("The total winnings are: $totalWinnings")

    // Part 2
    val totalWinningsWithJoker = input.toListOfHandsWithJoker().sorted().mapIndexed { idx, hand ->
        hand.bid * (idx + 1)
    }.sum()
    println("The total winnings including the Joker rule are: $totalWinningsWithJoker")

}