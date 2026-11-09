package com.example.data.model

import java.util.UUID

enum class GenerationType(val label: String) {
  NUMBERS("Numbers"),
  DICE("Dice"),
  COIN("Coin"),
  DECIDER("Decider"),
  LIST("List")
}

enum class SortOrder(val label: String) {
  NONE("Unsorted"),
  ASC("Ascending (Low-High)"),
  DESC("Descending (High-Low)")
}

enum class DiceType(val maxFace: Int, val label: String) {
  D4(4, "d4"),
  D6(6, "d6"),
  D8(8, "d8"),
  D10(10, "d10"),
  D12(12, "d12"),
  D20(20, "d20"),
  D100(100, "d100")
}

data class DiceRollResult(
  val diceType: DiceType,
  val value: Int
)

data class HistoryItem(
  val id: String = UUID.randomUUID().toString(),
  val timestamp: Long = System.currentTimeMillis(),
  val type: GenerationType,
  val title: String,
  val details: String,
  val resultsString: String,
  val isStarred: Boolean = false
)

data class QuickPreset(
  val label: String,
  val min: Long,
  val max: Long,
  val count: Int = 1,
  val unique: Boolean = true
)
