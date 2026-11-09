package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.JetBrainsMono

@Composable
fun CoinDeciderSection(
  subTab: Int,
  isFlipping: Boolean,
  lastCoinResult: Boolean?,
  headsCount: Int,
  tailsCount: Int,
  deciderQuestion: String,
  deciderAnswer: String?,
  isDeciding: Boolean,
  customOptions: List<String>,
  onSubTabChange: (Int) -> Unit,
  onFlipCoin: () -> Unit,
  onResetCoinStats: () -> Unit,
  onQuestionChange: (String) -> Unit,
  onAddOption: (String) -> Unit,
  onRemoveOption: (Int) -> Unit,
  onAskDecider: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxWidth()) {
    // Mode Switcher (Coin vs Decider)
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
      SegmentedButton(
        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
        onClick = { onSubTabChange(0) },
        selected = subTab == 0,
        label = { Text("🪙 Coin Flip") }
      )
      SegmentedButton(
        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
        onClick = { onSubTabChange(1) },
        selected = subTab == 1,
        label = { Text("🔮 Oracle / Yes-No") }
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    if (subTab == 0) {
      CoinFlipView(
        isFlipping = isFlipping,
        lastResult = lastCoinResult,
        headsCount = headsCount,
        tailsCount = tailsCount,
        onFlip = onFlipCoin,
        onReset = onResetCoinStats
      )
    } else {
      DeciderView(
        question = deciderQuestion,
        answer = deciderAnswer,
        isDeciding = isDeciding,
        customOptions = customOptions,
        onQuestionChange = onQuestionChange,
        onAddOption = onAddOption,
        onRemoveOption = onRemoveOption,
        onAsk = onAskDecider
      )
    }
  }
}

@Composable
fun CoinFlipView(
  isFlipping: Boolean,
  lastResult: Boolean?,
  headsCount: Int,
  tailsCount: Int,
  onFlip: () -> Unit,
  onReset: () -> Unit
) {
  val rotationY = remember { Animatable(0f) }

  LaunchedEffect(isFlipping) {
    if (isFlipping) {
      rotationY.animateTo(
        targetValue = rotationY.value + 1440f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
      )
    }
  }

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // 3D Coin presentation
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("coin_card"),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Coin Circle
        Box(
          modifier = Modifier
            .size(160.dp)
            .graphicsLayer {
              this.rotationY = rotationY.value
              cameraDistance = 12f * density
            }
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                colors = if (lastResult == false) listOf(Color(0xFFE2E8F0), Color(0xFF64748B))
                else listOf(Color(0xFFFDE047), Color(0xFFB45309))
              )
            )
            .border(
              width = 4.dp,
              brush = Brush.linearGradient(
                listOf(Color.White.copy(alpha = 0.8f), Color.Transparent, Color.Black.copy(alpha = 0.4f))
              ),
              shape = CircleShape
            ),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = if (lastResult == false) "TAILS" else "HEADS",
              style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp
              ),
              color = if (lastResult == false) Color(0xFF0F172A) else Color(0xFF451A03),
              modifier = Modifier.testTag("coin_outcome_text")
            )
            Text(
              text = if (lastResult == false) "🪙 2" else "👑 1",
              fontSize = 20.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Stats Row
        val totalFlips = headsCount + tailsCount
        val headsPct = if (totalFlips > 0) (headsCount * 100 / totalFlips) else 50
        val tailsPct = if (totalFlips > 0) (100 - headsPct) else 50

        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("HEADS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
              Text(
                "$headsCount ($headsPct%)",
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = JetBrainsMono, fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("TAILS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
              Text(
                "$tailsCount ($tailsPct%)",
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = JetBrainsMono, fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.secondary
              )
            }
            IconButton(onClick = onReset, modifier = Modifier.size(32.dp)) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Reset stats",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Flip Button
    Button(
      onClick = onFlip,
      enabled = !isFlipping,
      modifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .testTag("flip_coin_button"),
      shape = RoundedCornerShape(16.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.tertiary,
        contentColor = MaterialTheme.colorScheme.onTertiary
      ),
      elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = if (isFlipping) "FLIPPING COIN..." else "FLIP COIN",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DeciderView(
  question: String,
  answer: String?,
  isDeciding: Boolean,
  customOptions: List<String>,
  onQuestionChange: (String) -> Unit,
  onAddOption: (String) -> Unit,
  onRemoveOption: (Int) -> Unit,
  onAsk: () -> Unit
) {
  var newOptionText by remember { mutableStateOf("") }

  Column(modifier = Modifier.fillMaxWidth()) {
    // Oracle Reveal Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("oracle_card"),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), MaterialTheme.colorScheme.surface)
            )
          )
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "🔮 THE ORACLE SAYS",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(14.dp))

        AnimatedContent(
          targetState = if (isDeciding) "Consulting fate..." else (answer ?: "Ask a question below!"),
          transitionSpec = { fadeIn().togetherWith(fadeOut()) },
          label = "DeciderAnimation"
        ) { text ->
          Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier
              .padding(vertical = 12.dp)
              .testTag("oracle_answer_text")
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Optional Question Input
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
          value = question,
          onValueChange = onQuestionChange,
          label = { Text("What are you deciding? (Optional)") },
          placeholder = { Text("e.g., Should I order pizza tonight?") },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("decider_question_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "CUSTOM CHOICES (Leave empty for Yes/No Oracle)",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Options list
        customOptions.forEachIndexed { index, option ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "• $option",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = { onRemoveOption(index) }, modifier = Modifier.size(28.dp)) {
              Icon(imageVector = Icons.Default.Close, contentDescription = "Remove option", modifier = Modifier.size(16.dp))
            }
          }
        }

        // Add Option Row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = newOptionText,
            onValueChange = { newOptionText = it },
            placeholder = { Text("Add custom choice...") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
          )
          Spacer(modifier = Modifier.width(8.dp))
          IconButton(
            onClick = {
              if (newOptionText.isNotBlank()) {
                onAddOption(newOptionText)
                newOptionText = ""
              }
            },
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primaryContainer)
          ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add choice", tint = MaterialTheme.colorScheme.onPrimaryContainer)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Ask Button
    Button(
      onClick = onAsk,
      enabled = !isDeciding,
      modifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .testTag("ask_decider_button"),
      shape = RoundedCornerShape(16.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
      )
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = if (isDeciding) "DECIDING..." else "DECIDE FOR ME",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}
