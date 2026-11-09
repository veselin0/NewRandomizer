package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DiceRollResult
import com.example.data.model.DiceType
import com.example.ui.theme.AmberTertiaryDark
import com.example.ui.theme.CyanSecondaryDark
import com.example.ui.theme.JetBrainsMono

@OptIn(ExperimentalAnimationApi::class, ExperimentalLayoutApi::class)
@Composable
fun DiceSection(
  selectedDice: List<DiceType>,
  diceRolls: List<DiceRollResult>,
  isRolling: Boolean,
  onAddDice: (DiceType) -> Unit,
  onRemoveDiceAt: (Int) -> Unit,
  onClearDice: () -> Unit,
  onSetPreset: (Int, DiceType) -> Unit,
  onRoll: () -> Unit,
  modifier: Modifier = Modifier
) {
  val total = diceRolls.sumOf { it.value }

  // Rotation animation for rolling effect
  val rotationAnim = remember { Animatable(0f) }
  LaunchedEffect(isRolling) {
    if (isRolling) {
      rotationAnim.animateTo(
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
          animation = tween(durationMillis = 300, easing = LinearEasing),
          repeatMode = RepeatMode.Restart
        )
      )
    } else {
      rotationAnim.snapTo(0f)
    }
  }

  Column(modifier = modifier.fillMaxWidth()) {
    // Quick Presets
    Text(
      text = "DICE PRESETS",
      style = MaterialTheme.typography.labelSmall,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
      modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    )

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      FilterChip(
        selected = selectedDice.size == 1 && selectedDice.firstOrNull() == DiceType.D6,
        onClick = { onSetPreset(1, DiceType.D6) },
        label = { Text("1d6") }
      )
      FilterChip(
        selected = selectedDice.size == 2 && selectedDice.all { it == DiceType.D6 },
        onClick = { onSetPreset(2, DiceType.D6) },
        label = { Text("2d6") }
      )
      FilterChip(
        selected = selectedDice.size == 3 && selectedDice.all { it == DiceType.D6 },
        onClick = { onSetPreset(3, DiceType.D6) },
        label = { Text("3d6") }
      )
      FilterChip(
        selected = selectedDice.size == 1 && selectedDice.firstOrNull() == DiceType.D20,
        onClick = { onSetPreset(1, DiceType.D20) },
        label = { Text("1d20 (D&D)") }
      )
      FilterChip(
        selected = selectedDice.size == 2 && selectedDice.all { it == DiceType.D20 },
        onClick = { onSetPreset(2, DiceType.D20) },
        label = { Text("2d20 Advantage") }
      )
      FilterChip(
        selected = selectedDice.size == 1 && selectedDice.firstOrNull() == DiceType.D100,
        onClick = { onSetPreset(1, DiceType.D100) },
        label = { Text("1d100 (Percent)") }
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Dice Tray Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("dice_tray_card"),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              listOf(
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
                MaterialTheme.colorScheme.surface
              )
            )
          )
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top Bar: Total count & Clear
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
          ) {
            Text(
              text = if (isRolling) "ROLLING DICE..." else "TOTAL SUM",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSecondaryContainer,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }

          if (selectedDice.isNotEmpty()) {
            FilledTonalButton(
              onClick = onClearDice,
              contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
              Text("Clear all", style = MaterialTheme.typography.labelSmall)
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Total display
        Text(
          text = if (isRolling) "..." else total.toString(),
          style = MaterialTheme.typography.displayLarge.copy(
            fontFamily = JetBrainsMono,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 52.sp
          ),
          color = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.testTag("dice_total_text")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Visual Dice in Tray
        if (selectedDice.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(120.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Tap the dice buttons below to add dice to your tray!",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(16.dp)
            )
          }
        } else {
          FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            diceRolls.forEachIndexed { index, roll ->
              DiceBadge(
                roll = roll,
                isRolling = isRolling,
                rotationDegrees = if (isRolling) rotationAnim.value else 0f,
                onRemove = { onRemoveDiceAt(index) }
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Add Dice Selector Palette
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "ADD DICE TO TRAY (${selectedDice.size}/12)",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          DiceType.entries.forEach { type ->
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable(enabled = selectedDice.size < 12) { onAddDice(type) }
                .testTag("add_dice_${type.label}")
            ) {
              Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = type.label.uppercase(),
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold
                  ),
                  color = MaterialTheme.colorScheme.primary
                )
                Text(
                  text = "+1",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Roll Button
    Button(
      onClick = onRoll,
      enabled = selectedDice.isNotEmpty() && !isRolling,
      modifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .testTag("roll_dice_button"),
      shape = RoundedCornerShape(16.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary
      ),
      elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = Icons.Default.Casino,
          contentDescription = null,
          modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = if (isRolling) "ROLLING..." else "ROLL ${selectedDice.size} DICE",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )
      }
    }
  }
}

@Composable
fun DiceBadge(
  roll: DiceRollResult,
  isRolling: Boolean,
  rotationDegrees: Float,
  onRemove: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier.padding(4.dp)
  ) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surfaceVariant,
      border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)),
      modifier = Modifier
        .size(68.dp)
        .rotate(rotationDegrees)
    ) {
      Column(
        modifier = Modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = roll.diceType.label,
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = if (isRolling) "?" else roll.value.toString(),
          style = MaterialTheme.typography.titleLarge.copy(
            fontFamily = JetBrainsMono,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp
          ),
          color = MaterialTheme.colorScheme.secondary
        )
      }
    }

    // Small close icon on corner
    Box(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .size(20.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surface)
        .clickable { onRemove() }
        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.Close,
        contentDescription = "Remove dice",
        modifier = Modifier.size(12.dp),
        tint = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
