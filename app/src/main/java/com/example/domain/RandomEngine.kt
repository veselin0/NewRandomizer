package com.example.domain

import com.example.data.model.DiceRollResult
import com.example.data.model.DiceType
import com.example.data.model.SortOrder
import java.security.SecureRandom
import java.util.Random
import kotlin.math.pow
import kotlin.math.round

class RandomEngine(private val random: Random = SecureRandom()) {

  /**
   * Generates [count] random integers in range [[min], [max]].
   * If [unique] is true, numbers will not repeat.
   */
  fun generateNumbers(
    min: Long,
    max: Long,
    count: Int,
    unique: Boolean = false,
    sortOrder: SortOrder = SortOrder.NONE
  ): List<Long> {
    require(min <= max) { "Minimum value ($min) cannot be greater than maximum value ($max)." }
    require(count > 0) { "Count must be at least 1." }

    val rangeSize = max - min + 1
    if (unique) {
      require(count <= rangeSize) {
        "Cannot generate $count unique numbers in a range of size $rangeSize ($min to $max)."
      }
    }

    val result = if (unique) {
      // If the range is small enough, we can generate a pool and sample or shuffle
      if (rangeSize <= 10_000) {
        val pool = (min..max).toMutableList()
        pool.shuffle(random)
        pool.take(count)
      } else {
        // For very large ranges, use a HashSet to collect unique picks
        val set = mutableSetOf<Long>()
        while (set.size < count) {
          val next = nextLongInRange(min, max)
          set.add(next)
        }
        set.toList()
      }
    } else {
      List(count) { nextLongInRange(min, max) }
    }

    return when (sortOrder) {
      SortOrder.NONE -> result
      SortOrder.ASC -> result.sorted()
      SortOrder.DESC -> result.sortedDescending()
    }
  }

  /**
   * Generates [count] random decimal numbers in range [[min], [max]] with specified decimal places.
   */
  fun generateDecimals(
    min: Double,
    max: Double,
    count: Int,
    decimalPlaces: Int = 2,
    sortOrder: SortOrder = SortOrder.NONE
  ): List<Double> {
    require(min <= max) { "Min ($min) cannot be greater than Max ($max)." }
    require(count > 0) { "Count must be at least 1." }

    val factor = 10.0.pow(decimalPlaces)
    val result = List(count) {
      val raw = min + (max - min) * random.nextDouble()
      round(raw * factor) / factor
    }

    return when (sortOrder) {
      SortOrder.NONE -> result
      SortOrder.ASC -> result.sorted()
      SortOrder.DESC -> result.sortedDescending()
    }
  }

  /**
   * Rolls a set of polyhedral dice.
   */
  fun rollDice(dice: List<DiceType>): List<DiceRollResult> {
    return dice.map { type ->
      val value = random.nextInt(type.maxFace) + 1
      DiceRollResult(diceType = type, value = value)
    }
  }

  /**
   * Flips a coin. Returns true for Heads, false for Tails.
   */
  fun flipCoin(): Boolean {
    return random.nextBoolean()
  }

  /**
   * Picks a decision. If custom options are provided, picks from them.
   * Otherwise returns a classic 8-ball / Oracle answer.
   */
  fun pickDecision(customOptions: List<String> = emptyList()): String {
    val nonBlank = customOptions.filter { it.isNotBlank() }
    if (nonBlank.isNotEmpty()) {
      return nonBlank[random.nextInt(nonBlank.size)]
    }

    val defaultAnswers = listOf(
      "Yes, definitely!",
      "It is certain.",
      "Most likely.",
      "Outlook good.",
      "Signs point to yes.",
      "Ask again later.",
      "Better not tell you now.",
      "Cannot predict now.",
      "Don't count on it.",
      "My reply is no.",
      "Outlook not so good.",
      "Very doubtful."
    )
    return defaultAnswers[random.nextInt(defaultAnswers.size)]
  }

  /**
   * Picks random items from a custom list.
   */
  fun pickFromList(items: List<String>, count: Int = 1, unique: Boolean = true): List<String> {
    val cleaned = items.filter { it.isNotBlank() }
    if (cleaned.isEmpty()) return emptyList()

    return if (unique) {
      val sampleSize = count.coerceAtMost(cleaned.size)
      cleaned.shuffled(random).take(sampleSize)
    } else {
      List(count) { cleaned[random.nextInt(cleaned.size)] }
    }
  }

  fun shuffleList(items: List<String>): List<String> {
    return items.filter { it.isNotBlank() }.shuffled(random)
  }

  private fun nextLongInRange(min: Long, max: Long): Long {
    val bound = max - min + 1
    if (bound <= 0) {
      // Overflow handling for huge 64-bit bounds
      var r = random.nextLong()
      if (r < 0) r = -r
      return min + (r % (max - min))
    }
    if (bound <= Int.MAX_VALUE) {
      return min + random.nextInt(bound.toInt())
    }
    var bits: Long
    var valLong: Long
    do {
      bits = (random.nextLong() ushr 1)
      valLong = bits % bound
    } while (bits - valLong + (bound - 1) < 0L)
    return min + valLong
  }
}
