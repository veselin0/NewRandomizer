package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.DiceRollResult
import com.example.data.model.DiceType
import com.example.data.model.GenerationType
import com.example.data.model.HistoryItem
import com.example.data.model.QuickPreset
import com.example.data.model.SortOrder
import com.example.domain.RandomEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppTab(val label: String) {
  NUMBERS("Numbers"),
  DICE("Dice"),
  COIN("Coin & Decider"),
  LIST("List Picker")
}

data class RandomizerUiState(
  val currentTab: AppTab = AppTab.NUMBERS,

  // Numbers State
  val minText: String = "1",
  val maxText: String = "100",
  val countText: String = "1",
  val uniqueNumbers: Boolean = true,
  val sortOrder: SortOrder = SortOrder.NONE,
  val isDecimalMode: Boolean = false,
  val decimalPlaces: Int = 2,
  val isGenerating: Boolean = false,
  val rollingPreviewText: String = "",
  val generatedNumbers: List<Long> = listOf(42L),
  val generatedDecimals: List<Double> = emptyList(),
  val numberErrorMessage: String? = null,

  // Presets
  val presets: List<QuickPreset> = listOf(
    QuickPreset("1 - 10", 1, 10, 1, false),
    QuickPreset("1 - 50", 1, 50, 1, false),
    QuickPreset("1 - 100", 1, 100, 1, false),
    QuickPreset("1 - 1,000", 1, 1000, 1, false),
    QuickPreset("Dice (1-6)", 1, 6, 1, false),
    QuickPreset("Binary (0-1)", 0, 1, 1, false),
    QuickPreset("Lotto 6/49", 1, 49, 6, true),
    QuickPreset("Lucky 7", 1, 777, 1, false)
  ),

  // Dice State
  val selectedDice: List<DiceType> = listOf(DiceType.D6, DiceType.D6),
  val diceRolls: List<DiceRollResult> = listOf(
    DiceRollResult(DiceType.D6, 3),
    DiceRollResult(DiceType.D6, 5)
  ),
  val isRollingDice: Boolean = false,

  // Coin & Decider State
  val coinSubTab: Int = 0, // 0 = Coin, 1 = Oracle/Decider
  val isCoinFlipping: Boolean = false,
  val lastCoinResult: Boolean? = true, // true = Heads, false = Tails
  val headsCount: Int = 1,
  val tailsCount: Int = 0,
  val deciderQuestion: String = "",
  val deciderAnswer: String? = "Outlook good.",
  val isDeciding: Boolean = false,
  val customDeciderOptions: List<String> = emptyList(),

  // List Picker State
  val listItems: List<String> = listOf("Alice", "Bob", "Charlie", "Diana", "Ethan"),
  val listPickCount: Int = 1,
  val pickedItems: List<String> = emptyList(),
  val isPickingList: Boolean = false,
  val newItemInput: String = "",

  // History & Settings
  val history: List<HistoryItem> = emptyList(),
  val showHistorySheet: Boolean = false,
  val hapticsEnabled: Boolean = true
)

class RandomizerViewModel(
  private val engine: RandomEngine = RandomEngine()
) : ViewModel() {

  private val _uiState = MutableStateFlow(RandomizerUiState())
  val uiState: StateFlow<RandomizerUiState> = _uiState.asStateFlow()

  fun setTab(tab: AppTab) {
    _uiState.update { it.copy(currentTab = tab) }
  }

  // --- NUMBERS GENERATOR ---
  fun updateMinText(value: String) {
    _uiState.update { it.copy(minText = value, numberErrorMessage = null) }
  }

  fun updateMaxText(value: String) {
    _uiState.update { it.copy(maxText = value, numberErrorMessage = null) }
  }

  fun updateCountText(value: String) {
    _uiState.update { it.copy(countText = value, numberErrorMessage = null) }
  }

  fun toggleUniqueNumbers(unique: Boolean) {
    _uiState.update { it.copy(uniqueNumbers = unique, numberErrorMessage = null) }
  }

  fun setSortOrder(order: SortOrder) {
    _uiState.update { it.copy(sortOrder = order) }
  }

  fun toggleDecimalMode(enabled: Boolean) {
    _uiState.update { it.copy(isDecimalMode = enabled, numberErrorMessage = null) }
  }

  fun setDecimalPlaces(places: Int) {
    _uiState.update { it.copy(decimalPlaces = places.coerceIn(1, 4)) }
  }

  fun applyPreset(preset: QuickPreset) {
    _uiState.update {
      it.copy(
        minText = preset.min.toString(),
        maxText = preset.max.toString(),
        countText = preset.count.toString(),
        uniqueNumbers = preset.unique,
        isDecimalMode = false,
        numberErrorMessage = null
      )
    }
    generateNumbers()
  }

  fun adjustMin(delta: Long) {
    val current = _uiState.value.minText.toLongOrNull() ?: 1L
    updateMinText((current + delta).toString())
  }

  fun adjustMax(delta: Long) {
    val current = _uiState.value.maxText.toLongOrNull() ?: 100L
    updateMaxText((current + delta).toString())
  }

  fun generateNumbers(onTick: (() -> Unit)? = null, onComplete: (() -> Unit)? = null) {
    val state = _uiState.value
    val count = state.countText.toIntOrNull() ?: 1

    if (count <= 0) {
      _uiState.update { it.copy(numberErrorMessage = "Count must be at least 1.") }
      return
    }

    if (count > 200) {
      _uiState.update { it.copy(numberErrorMessage = "Maximum count is 200.") }
      return
    }

    if (state.isDecimalMode) {
      val minD = state.minText.toDoubleOrNull()
      val maxD = state.maxText.toDoubleOrNull()
      if (minD == null || maxD == null) {
        _uiState.update { it.copy(numberErrorMessage = "Please enter valid decimal numbers for Min and Max.") }
        return
      }
      if (minD > maxD) {
        _uiState.update { it.copy(numberErrorMessage = "Min ($minD) cannot be greater than Max ($maxD).") }
        return
      }

      viewModelScope.launch {
        _uiState.update { it.copy(isGenerating = true, numberErrorMessage = null) }
        // Fast rolling preview animation
        repeat(7) {
          onTick?.invoke()
          val temp = engine.generateDecimals(minD, maxD, 1, state.decimalPlaces, SortOrder.NONE)
          _uiState.update { it.copy(rollingPreviewText = temp.first().toString()) }
          delay(40)
        }

        val result = engine.generateDecimals(minD, maxD, count, state.decimalPlaces, state.sortOrder)
        val historyEntry = HistoryItem(
          type = GenerationType.NUMBERS,
          title = "Range: [$minD .. $maxD] (${state.decimalPlaces} dec)",
          details = "Generated $count decimal number(s)",
          resultsString = result.joinToString(", ")
        )

        _uiState.update {
          it.copy(
            isGenerating = false,
            generatedDecimals = result,
            generatedNumbers = emptyList(),
            history = listOf(historyEntry) + it.history
          )
        }
        onComplete?.invoke()
      }
    } else {
      val minL = state.minText.toLongOrNull()
      val maxL = state.maxText.toLongOrNull()
      if (minL == null || maxL == null) {
        _uiState.update { it.copy(numberErrorMessage = "Please enter valid integers for Min and Max.") }
        return
      }
      if (minL > maxL) {
        _uiState.update { it.copy(numberErrorMessage = "Min ($minL) cannot be greater than Max ($maxL).") }
        return
      }

      val rangeSize = maxL - minL + 1
      if (state.uniqueNumbers && count > rangeSize) {
        _uiState.update {
          it.copy(numberErrorMessage = "Cannot pick $count unique numbers from range size of $rangeSize.")
        }
        return
      }

      viewModelScope.launch {
        _uiState.update { it.copy(isGenerating = true, numberErrorMessage = null) }
        // Rolling number animation
        repeat(8) {
          onTick?.invoke()
          val temp = engine.generateNumbers(minL, maxL, 1, false, SortOrder.NONE)
          _uiState.update { it.copy(rollingPreviewText = temp.first().toString()) }
          delay(35)
        }

        val result = engine.generateNumbers(minL, maxL, count, state.uniqueNumbers, state.sortOrder)
        val historyEntry = HistoryItem(
          type = GenerationType.NUMBERS,
          title = "Range: [$minL .. $maxL]",
          details = "$count ${if (state.uniqueNumbers) "unique " else ""}number(s) • ${state.sortOrder.label}",
          resultsString = result.joinToString(", ")
        )

        _uiState.update {
          it.copy(
            isGenerating = false,
            generatedNumbers = result,
            generatedDecimals = emptyList(),
            history = listOf(historyEntry) + it.history
          )
        }
        onComplete?.invoke()
      }
    }
  }

  // --- DICE ROLLER ---
  fun addDice(type: DiceType) {
    if (_uiState.value.selectedDice.size < 12) {
      _uiState.update { it.copy(selectedDice = it.selectedDice + type) }
    }
  }

  fun removeDiceAt(index: Int) {
    _uiState.update {
      val list = it.selectedDice.toMutableList()
      if (index in list.indices) {
        list.removeAt(index)
      }
      it.copy(selectedDice = list)
    }
  }

  fun clearDice() {
    _uiState.update { it.copy(selectedDice = emptyList()) }
  }

  fun setStandardDice(count: Int, type: DiceType) {
    _uiState.update { it.copy(selectedDice = List(count) { type }) }
    rollDice()
  }

  fun rollDice(onTick: (() -> Unit)? = null, onComplete: (() -> Unit)? = null) {
    val dice = _uiState.value.selectedDice
    if (dice.isEmpty()) return

    viewModelScope.launch {
      _uiState.update { it.copy(isRollingDice = true) }
      repeat(6) {
        onTick?.invoke()
        val tempRolls = engine.rollDice(dice)
        _uiState.update { it.copy(diceRolls = tempRolls) }
        delay(40)
      }
      val finalRolls = engine.rollDice(dice)
      val total = finalRolls.sumOf { it.value }
      val summary = finalRolls.joinToString(", ") { "${it.diceType.label}:${it.value}" }
      val historyEntry = HistoryItem(
        type = GenerationType.DICE,
        title = "Dice Roll: Total $total",
        details = "${dice.size} dice (${dice.groupBy { it.label }.map { "${it.value.size}${it.key}" }.joinToString("+")})",
        resultsString = summary
      )

      _uiState.update {
        it.copy(
          isRollingDice = false,
          diceRolls = finalRolls,
          history = listOf(historyEntry) + it.history
        )
      }
      onComplete?.invoke()
    }
  }

  // --- COIN & DECIDER ---
  fun setCoinSubTab(index: Int) {
    _uiState.update { it.copy(coinSubTab = index) }
  }

  fun flipCoin(onTick: (() -> Unit)? = null, onComplete: (() -> Unit)? = null) {
    viewModelScope.launch {
      _uiState.update { it.copy(isCoinFlipping = true) }
      repeat(7) {
        onTick?.invoke()
        _uiState.update { it.copy(lastCoinResult = !it.lastCoinResult!!) }
        delay(50)
      }
      val result = engine.flipCoin()
      val historyEntry = HistoryItem(
        type = GenerationType.COIN,
        title = if (result) "Coin: Heads" else "Coin: Tails",
        details = "Flip #${_uiState.value.headsCount + _uiState.value.tailsCount + 1}",
        resultsString = if (result) "HEADS" else "TAILS"
      )
      _uiState.update {
        it.copy(
          isCoinFlipping = false,
          lastCoinResult = result,
          headsCount = if (result) it.headsCount + 1 else it.headsCount,
          tailsCount = if (!result) it.tailsCount + 1 else it.tailsCount,
          history = listOf(historyEntry) + it.history
        )
      }
      onComplete?.invoke()
    }
  }

  fun resetCoinStats() {
    _uiState.update { it.copy(headsCount = 0, tailsCount = 0, lastCoinResult = null) }
  }

  fun updateDeciderQuestion(q: String) {
    _uiState.update { it.copy(deciderQuestion = q) }
  }

  fun addCustomDeciderOption(option: String) {
    if (option.isNotBlank()) {
      _uiState.update { it.copy(customDeciderOptions = it.customDeciderOptions + option.trim()) }
    }
  }

  fun removeCustomDeciderOption(index: Int) {
    _uiState.update {
      val list = it.customDeciderOptions.toMutableList()
      if (index in list.indices) list.removeAt(index)
      it.copy(customDeciderOptions = list)
    }
  }

  fun askDecider(onTick: (() -> Unit)? = null, onComplete: (() -> Unit)? = null) {
    viewModelScope.launch {
      _uiState.update { it.copy(isDeciding = true) }
      repeat(6) {
        onTick?.invoke()
        val temp = engine.pickDecision(_uiState.value.customDeciderOptions)
        _uiState.update { it.copy(deciderAnswer = temp) }
        delay(60)
      }
      val answer = engine.pickDecision(_uiState.value.customDeciderOptions)
      val question = _uiState.value.deciderQuestion.ifBlank { "Decision" }
      val historyEntry = HistoryItem(
        type = GenerationType.DECIDER,
        title = "Oracle: $answer",
        details = question,
        resultsString = answer
      )
      _uiState.update {
        it.copy(
          isDeciding = false,
          deciderAnswer = answer,
          history = listOf(historyEntry) + it.history
        )
      }
      onComplete?.invoke()
    }
  }

  // --- LIST PICKER ---
  fun updateNewItemInput(text: String) {
    _uiState.update { it.copy(newItemInput = text) }
  }

  fun addListItem() {
    val item = _uiState.value.newItemInput.trim()
    if (item.isNotEmpty() && !_uiState.value.listItems.contains(item)) {
      _uiState.update {
        it.copy(
          listItems = it.listItems + item,
          newItemInput = ""
        )
      }
    }
  }

  fun removeListItem(item: String) {
    _uiState.update {
      it.copy(
        listItems = it.listItems - item,
        pickedItems = it.pickedItems - item
      )
    }
  }

  fun clearListItems() {
    _uiState.update { it.copy(listItems = emptyList(), pickedItems = emptyList()) }
  }

  fun setListPickCount(count: Int) {
    _uiState.update { it.copy(listPickCount = count.coerceIn(1, 20)) }
  }

  fun pickFromList(onTick: (() -> Unit)? = null, onComplete: (() -> Unit)? = null) {
    val items = _uiState.value.listItems
    if (items.isEmpty()) return

    viewModelScope.launch {
      _uiState.update { it.copy(isPickingList = true) }
      repeat(6) {
        onTick?.invoke()
        val temp = engine.pickFromList(items, _uiState.value.listPickCount, true)
        _uiState.update { it.copy(pickedItems = temp) }
        delay(60)
      }
      val finalPick = engine.pickFromList(items, _uiState.value.listPickCount, true)
      val historyEntry = HistoryItem(
        type = GenerationType.LIST,
        title = "Winner: ${finalPick.joinToString(", ")}",
        details = "From pool of ${items.size} item(s)",
        resultsString = finalPick.joinToString(", ")
      )
      _uiState.update {
        it.copy(
          isPickingList = false,
          pickedItems = finalPick,
          history = listOf(historyEntry) + it.history
        )
      }
      onComplete?.invoke()
    }
  }

  fun shuffleList(onTick: (() -> Unit)? = null, onComplete: (() -> Unit)? = null) {
    val items = _uiState.value.listItems
    if (items.isEmpty()) return

    viewModelScope.launch {
      _uiState.update { it.copy(isPickingList = true) }
      repeat(4) {
        onTick?.invoke()
        _uiState.update { it.copy(listItems = engine.shuffleList(it.listItems)) }
        delay(50)
      }
      val shuffled = engine.shuffleList(items)
      val historyEntry = HistoryItem(
        type = GenerationType.LIST,
        title = "Shuffled ${shuffled.size} items",
        details = "Order randomized",
        resultsString = shuffled.joinToString(" -> ")
      )
      _uiState.update {
        it.copy(
          isPickingList = false,
          listItems = shuffled,
          history = listOf(historyEntry) + it.history
        )
      }
      onComplete?.invoke()
    }
  }

  // --- HISTORY & SETTINGS ---
  fun toggleHistorySheet(show: Boolean) {
    _uiState.update { it.copy(showHistorySheet = show) }
  }

  fun toggleStarredHistory(id: String) {
    _uiState.update {
      val updated = it.history.map { item ->
        if (item.id == id) item.copy(isStarred = !item.isStarred) else item
      }
      it.copy(history = updated)
    }
  }

  fun clearHistory() {
    _uiState.update { it.copy(history = emptyList()) }
  }

  fun toggleHaptics() {
    _uiState.update { it.copy(hapticsEnabled = !it.hapticsEnabled) }
  }
}
