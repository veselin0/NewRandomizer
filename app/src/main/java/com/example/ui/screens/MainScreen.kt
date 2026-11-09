package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CoinDeciderSection
import com.example.ui.components.DiceSection
import com.example.ui.components.HistoryBottomSheet
import com.example.ui.components.ListPickerSection
import com.example.ui.components.NumberResultCard
import com.example.ui.components.RangeControls
import com.example.ui.theme.JetBrainsMono
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.RandomizerUiState
import com.example.ui.viewmodel.RandomizerViewModel
import com.example.util.HapticsHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
  viewModel: RandomizerViewModel,
  modifier: Modifier = Modifier
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val context = LocalContext.current
  val haptics = remember { HapticsHelper(context) }
  var showAboutDialog by remember { mutableStateOf(false) }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Casino,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Randomizer",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold
            )
          }
        },
        actions = {
          // Haptics toggle
          IconButton(
            onClick = {
              viewModel.toggleHaptics()
              if (!state.hapticsEnabled) {
                haptics.tick()
              }
            },
            modifier = Modifier.testTag("haptics_toggle_button")
          ) {
            Icon(
              imageVector = if (state.hapticsEnabled) Icons.Default.Vibration else Icons.Outlined.Vibration,
              contentDescription = if (state.hapticsEnabled) "Disable Haptics" else "Enable Haptics",
              tint = if (state.hapticsEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          // History button with count badge
          IconButton(
            onClick = { viewModel.toggleHistorySheet(true) },
            modifier = Modifier.testTag("history_button")
          ) {
            if (state.history.isNotEmpty()) {
              BadgedBox(
                badge = {
                  Badge {
                    Text(
                      text = if (state.history.size > 99) "99+" else state.history.size.toString(),
                      fontFamily = JetBrainsMono
                    )
                  }
                }
              ) {
                Icon(
                  imageVector = Icons.Default.History,
                  contentDescription = "View History",
                  tint = MaterialTheme.colorScheme.onSurface
                )
              }
            } else {
              Icon(
                imageVector = Icons.Default.History,
                contentDescription = "View History",
                tint = MaterialTheme.colorScheme.onSurface
              )
            }
          }

          // About Dialog button
          IconButton(
            onClick = { showAboutDialog = true },
            modifier = Modifier.testTag("about_button")
          ) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = "About App",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    bottomBar = {
      NavigationBar(
        modifier = Modifier.testTag("bottom_nav_bar")
      ) {
        NavigationBarItem(
          selected = state.currentTab == AppTab.NUMBERS,
          onClick = { viewModel.setTab(AppTab.NUMBERS) },
          icon = { Icon(Icons.Default.Pin, contentDescription = "Numbers") },
          label = { Text("Numbers") },
          modifier = Modifier.testTag("tab_numbers")
        )
        NavigationBarItem(
          selected = state.currentTab == AppTab.DICE,
          onClick = { viewModel.setTab(AppTab.DICE) },
          icon = { Icon(Icons.Default.Casino, contentDescription = "Dice") },
          label = { Text("Dice") },
          modifier = Modifier.testTag("tab_dice")
        )
        NavigationBarItem(
          selected = state.currentTab == AppTab.COIN,
          onClick = { viewModel.setTab(AppTab.COIN) },
          icon = { Icon(Icons.Default.MonetizationOn, contentDescription = "Coin") },
          label = { Text("Coin / Decider") },
          modifier = Modifier.testTag("tab_coin")
        )
        NavigationBarItem(
          selected = state.currentTab == AppTab.LIST,
          onClick = { viewModel.setTab(AppTab.LIST) },
          icon = { Icon(Icons.Default.FormatListNumbered, contentDescription = "List Picker") },
          label = { Text("List Picker") },
          modifier = Modifier.testTag("tab_list")
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentAlignment = Alignment.TopCenter
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .widthIn(max = 600.dp)
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 20.dp, vertical = 12.dp)
      ) {
        when (state.currentTab) {
          AppTab.NUMBERS -> {
            NumberResultCard(
              numbers = state.generatedNumbers,
              decimals = state.generatedDecimals,
              isGenerating = state.isGenerating,
              rollingText = state.rollingPreviewText,
              onReroll = {
                if (state.hapticsEnabled) haptics.rollBurst()
                viewModel.generateNumbers(
                  onTick = { if (state.hapticsEnabled) haptics.tick() },
                  onComplete = { if (state.hapticsEnabled) haptics.success() }
                )
              }
            )

            Spacer(modifier = Modifier.height(16.dp))

            RangeControls(
              minText = state.minText,
              maxText = state.maxText,
              countText = state.countText,
              uniqueNumbers = state.uniqueNumbers,
              sortOrder = state.sortOrder,
              isDecimalMode = state.isDecimalMode,
              decimalPlaces = state.decimalPlaces,
              presets = state.presets,
              errorMessage = state.numberErrorMessage,
              isGenerating = state.isGenerating,
              onMinChange = { viewModel.updateMinText(it) },
              onMaxChange = { viewModel.updateMaxText(it) },
              onCountChange = { viewModel.updateCountText(it) },
              onAdjustMin = { delta ->
                viewModel.adjustMin(delta)
                if (state.hapticsEnabled) haptics.tick()
              },
              onAdjustMax = { delta ->
                viewModel.adjustMax(delta)
                if (state.hapticsEnabled) haptics.tick()
              },
              onSwapRange = {
                val tempMin = state.minText
                viewModel.updateMinText(state.maxText)
                viewModel.updateMaxText(tempMin)
                if (state.hapticsEnabled) haptics.tick()
              },
              onUniqueToggle = { viewModel.toggleUniqueNumbers(it) },
              onSortChange = { viewModel.setSortOrder(it) },
              onDecimalToggle = { viewModel.toggleDecimalMode(it) },
              onDecimalPlacesChange = { viewModel.setDecimalPlaces(it) },
              onPresetSelect = { preset ->
                if (state.hapticsEnabled) haptics.tick()
                viewModel.applyPreset(preset)
              },
              onGenerate = {
                if (state.hapticsEnabled) haptics.rollBurst()
                viewModel.generateNumbers(
                  onTick = { if (state.hapticsEnabled) haptics.tick() },
                  onComplete = { if (state.hapticsEnabled) haptics.success() }
                )
              }
            )
          }

          AppTab.DICE -> {
            DiceSection(
              selectedDice = state.selectedDice,
              diceRolls = state.diceRolls,
              isRolling = state.isRollingDice,
              onAddDice = { type ->
                if (state.hapticsEnabled) haptics.tick()
                viewModel.addDice(type)
              },
              onRemoveDiceAt = { index ->
                if (state.hapticsEnabled) haptics.tick()
                viewModel.removeDiceAt(index)
              },
              onClearDice = {
                if (state.hapticsEnabled) haptics.tick()
                viewModel.clearDice()
              },
              onSetPreset = { count, type ->
                if (state.hapticsEnabled) haptics.rollBurst()
                viewModel.setStandardDice(count, type)
              },
              onRoll = {
                if (state.hapticsEnabled) haptics.rollBurst()
                viewModel.rollDice(
                  onTick = { if (state.hapticsEnabled) haptics.tick() },
                  onComplete = { if (state.hapticsEnabled) haptics.success() }
                )
              }
            )
          }

          AppTab.COIN -> {
            CoinDeciderSection(
              subTab = state.coinSubTab,
              isFlipping = state.isCoinFlipping,
              lastCoinResult = state.lastCoinResult,
              headsCount = state.headsCount,
              tailsCount = state.tailsCount,
              deciderQuestion = state.deciderQuestion,
              deciderAnswer = state.deciderAnswer,
              isDeciding = state.isDeciding,
              customOptions = state.customDeciderOptions,
              onSubTabChange = { viewModel.setCoinSubTab(it) },
              onFlipCoin = {
                if (state.hapticsEnabled) haptics.rollBurst()
                viewModel.flipCoin(
                  onTick = { if (state.hapticsEnabled) haptics.tick() },
                  onComplete = { if (state.hapticsEnabled) haptics.success() }
                )
              },
              onResetCoinStats = {
                if (state.hapticsEnabled) haptics.tick()
                viewModel.resetCoinStats()
              },
              onQuestionChange = { viewModel.updateDeciderQuestion(it) },
              onAddOption = { viewModel.addCustomDeciderOption(it) },
              onRemoveOption = { viewModel.removeCustomDeciderOption(it) },
              onAskDecider = {
                if (state.hapticsEnabled) haptics.rollBurst()
                viewModel.askDecider(
                  onTick = { if (state.hapticsEnabled) haptics.tick() },
                  onComplete = { if (state.hapticsEnabled) haptics.success() }
                )
              }
            )
          }

          AppTab.LIST -> {
            ListPickerSection(
              items = state.listItems,
              pickCount = state.listPickCount,
              pickedItems = state.pickedItems,
              isPicking = state.isPickingList,
              newItemInput = state.newItemInput,
              onNewItemChange = { viewModel.updateNewItemInput(it) },
              onAddItem = {
                if (state.hapticsEnabled) haptics.tick()
                viewModel.addListItem()
              },
              onRemoveItem = { item ->
                if (state.hapticsEnabled) haptics.tick()
                viewModel.removeListItem(item)
              },
              onClearItems = {
                if (state.hapticsEnabled) haptics.tick()
                viewModel.clearListItems()
              },
              onPickCountChange = { viewModel.setListPickCount(it) },
              onPick = {
                if (state.hapticsEnabled) haptics.rollBurst()
                viewModel.pickFromList(
                  onTick = { if (state.hapticsEnabled) haptics.tick() },
                  onComplete = { if (state.hapticsEnabled) haptics.success() }
                )
              },
              onShuffle = {
                if (state.hapticsEnabled) haptics.rollBurst()
                viewModel.shuffleList(
                  onTick = { if (state.hapticsEnabled) haptics.tick() },
                  onComplete = { if (state.hapticsEnabled) haptics.success() }
                )
              }
            )
          }
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }

  // History Bottom Sheet
  if (state.showHistorySheet) {
    HistoryBottomSheet(
      history = state.history,
      onDismiss = { viewModel.toggleHistorySheet(false) },
      onToggleStar = { viewModel.toggleStarredHistory(it) },
      onClearHistory = { viewModel.clearHistory() }
    )
  }

  // About Dialog
  if (showAboutDialog) {
    AlertDialog(
      onDismissRequest = { showAboutDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Casino, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          Spacer(modifier = Modifier.width(8.dp))
          Text("About Randomizer")
        }
      },
      text = {
        Column {
          Text(
            text = "Randomizer is a cryptographic-grade random number generator designed for precision, games, raffles, and everyday choices.",
            style = MaterialTheme.typography.bodyMedium
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "• Set custom Min and Max ranges (integers or decimals)\n" +
                "• Draw multiple unique numbers at once\n" +
                "• Roll polyhedral dice (d4 to d100)\n" +
                "• 3D Coin Flipper & Yes/No Oracle\n" +
                "• List & name raffle winner picker\n" +
                "• Complete history log with star favorites",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      },
      confirmButton = {
        TextButton(onClick = { showAboutDialog = false }) {
          Text("Got it")
        }
      }
    )
  }
}
