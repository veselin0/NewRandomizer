package com.example

import com.example.data.model.DiceType
import com.example.data.model.SortOrder
import com.example.domain.RandomEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExampleUnitTest {

  private lateinit var engine: RandomEngine

  @Before
  fun setUp() {
    engine = RandomEngine()
  }

  @Test
  fun testGenerateNumbers_withinRange() {
    val min = 10L
    val max = 50L
    val count = 20
    val results = engine.generateNumbers(min = min, max = max, count = count, unique = false)

    assertEquals(count, results.size)
    results.forEach { num ->
      assertTrue("Number $num is out of range [$min, $max]", num in min..max)
    }
  }

  @Test
  fun testGenerateNumbers_unique() {
    val min = 1L
    val max = 15L
    val count = 15
    val results = engine.generateNumbers(min = min, max = max, count = count, unique = true)

    assertEquals(count, results.size)
    val distinctCount = results.toSet().size
    assertEquals(count, distinctCount)
  }

  @Test
  fun testGenerateNumbers_sortingAscending() {
    val results = engine.generateNumbers(1, 100, 10, unique = false, sortOrder = SortOrder.ASC)
    for (i in 0 until results.size - 1) {
      assertTrue(results[i] <= results[i + 1])
    }
  }

  @Test
  fun testGenerateNumbers_sortingDescending() {
    val results = engine.generateNumbers(1, 100, 10, unique = false, sortOrder = SortOrder.DESC)
    for (i in 0 until results.size - 1) {
      assertTrue(results[i] >= results[i + 1])
    }
  }

  @Test
  fun testGenerateDecimals_withinRange() {
    val min = 0.5
    val max = 2.5
    val results = engine.generateDecimals(min, max, 10, decimalPlaces = 2)
    results.forEach { dec ->
      assertTrue(dec in min..max)
    }
  }

  @Test
  fun testRollDice_valuesWithinFace() {
    val dice = listOf(DiceType.D4, DiceType.D6, DiceType.D20, DiceType.D100)
    val rolls = engine.rollDice(dice)

    assertEquals(4, rolls.size)
    rolls.forEach { roll ->
      assertTrue(roll.value in 1..roll.diceType.maxFace)
    }
  }

  @Test
  fun testPickDecision_fromCustomOptions() {
    val options = listOf("OptionA", "OptionB", "OptionC")
    val answer = engine.pickDecision(options)
    assertTrue(answer in options)
  }

  @Test
  fun testPickFromList_unique() {
    val pool = listOf("Red", "Green", "Blue", "Yellow")
    val picked = engine.pickFromList(pool, count = 3, unique = true)
    assertEquals(3, picked.size)
    assertEquals(3, picked.toSet().size)
    picked.forEach { assertTrue(it in pool) }
  }
}
