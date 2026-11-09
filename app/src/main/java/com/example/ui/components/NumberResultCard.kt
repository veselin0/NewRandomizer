package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberTertiaryDark
import com.example.ui.theme.CyanSecondaryDark
import com.example.ui.theme.JetBrainsMono

@OptIn(ExperimentalAnimationApi::class, ExperimentalLayoutApi::class)
@Composable
fun NumberResultCard(
  numbers: List<Long>,
  decimals: List<Double>,
  isGenerating: Boolean,
  rollingText: String,
  onReroll: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isDecimal = decimals.isNotEmpty()
  val resultStrings = if (isDecimal) decimals.map { it.toString() } else numbers.map { it.toString() }

  // Scale bounce animation when rolling finishes
  val scaleAnim = remember { Animatable(1f) }
  LaunchedEffect(numbers, decimals, isGenerating) {
    if (!isGenerating && resultStrings.isNotEmpty()) {
      scaleAnim.animateTo(
        targetValue = 1.08f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
      )
      scaleAnim.animateTo(1f)
    }
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("result_card"),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.verticalGradient(
            colors = listOf(
              MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
              MaterialTheme.colorScheme.surface
            )
          )
        )
        .padding(20.dp)
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top badge & action buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
          ) {
            Text(
              text = if (isGenerating) "RANDOMIZING..." else if (resultStrings.size > 1) "${resultStrings.size} NUMBERS" else "RESULT",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            // Copy button
            FilledTonalIconButton(
              onClick = {
                val copyText = resultStrings.joinToString(", ")
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Random Result", copyText))
                Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
              },
              modifier = Modifier
                .size(36.dp)
                .testTag("copy_button"),
              colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
              )
            ) {
              Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy result",
                modifier = Modifier.size(18.dp)
              )
            }

            // Share button
            FilledTonalIconButton(
              onClick = {
                val sendIntent = Intent().apply {
                  action = Intent.ACTION_SEND
                  putExtra(Intent.EXTRA_TEXT, "Random Number: ${resultStrings.joinToString(", ")}")
                  type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Share Random Number"))
              },
              modifier = Modifier
                .size(36.dp)
                .testTag("share_button"),
              colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
              )
            ) {
              Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share result",
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large Single Result or Multi-Result Flow
        if (resultStrings.size <= 1) {
          Box(
            modifier = Modifier
              .scale(scaleAnim.value)
              .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
          ) {
            AnimatedContent(
              targetState = if (isGenerating) rollingText else (resultStrings.firstOrNull() ?: "-"),
              transitionSpec = {
                (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
              },
              label = "SingleResultAnimation"
            ) { displayedNumber ->
              Text(
                text = displayedNumber,
                style = MaterialTheme.typography.displayLarge.copy(
                  fontFamily = JetBrainsMono,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = if (displayedNumber.length > 7) 36.sp else if (displayedNumber.length > 5) 44.sp else 58.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("primary_number_display")
              )
            }
          }
        } else {
          // Multi-Number Results Grid
          FlowRow(
            modifier = Modifier
              .fillMaxWidth()
              .scale(scaleAnim.value)
              .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            resultStrings.forEachIndexed { index, value ->
              MultiNumberChip(
                index = index + 1,
                value = value,
                isLowest = !isDecimal && numbers.minOrNull() == numbers.getOrNull(index),
                isHighest = !isDecimal && numbers.maxOrNull() == numbers.getOrNull(index)
              )
            }
          }

          // Summary Stats Bar
          if (!isDecimal && numbers.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            MultiNumberStatsRow(numbers = numbers)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Reroll Tap Target
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = !isGenerating) { onReroll() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Reroll",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Tap to reroll",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }
    }
  }
}

@Composable
fun MultiNumberChip(
  index: Int,
  value: String,
  isLowest: Boolean,
  isHighest: Boolean,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.padding(horizontal = 4.dp),
    shape = RoundedCornerShape(14.dp),
    color = when {
      isHighest -> MaterialTheme.colorScheme.primaryContainer
      isLowest -> MaterialTheme.colorScheme.secondaryContainer
      else -> MaterialTheme.colorScheme.surfaceVariant
    },
    border = if (isHighest || isLowest) {
      androidx.compose.foundation.BorderStroke(
        width = 1.dp,
        color = if (isHighest) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
      )
    } else null
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(18.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "$index",
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
          color = MaterialTheme.colorScheme.onSurface
        )
      }
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium.copy(
          fontFamily = JetBrainsMono,
          fontWeight = FontWeight.Bold
        ),
        color = when {
          isHighest -> MaterialTheme.colorScheme.onPrimaryContainer
          isLowest -> MaterialTheme.colorScheme.onSecondaryContainer
          else -> MaterialTheme.colorScheme.onSurfaceVariant
        }
      )
    }
  }
}

@Composable
fun MultiNumberStatsRow(numbers: List<Long>, modifier: Modifier = Modifier) {
  val sum = numbers.sum()
  val avg = String.format("%.1f", numbers.average())
  val min = numbers.minOrNull() ?: 0
  val max = numbers.maxOrNull() ?: 0

  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceAround
    ) {
      StatItem(label = "Sum", value = sum.toString())
      StatItem(label = "Avg", value = avg)
      StatItem(label = "Min", value = min.toString())
      StatItem(label = "Max", value = max.toString())
    }
  }
}

@Composable
private fun StatItem(label: String, value: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
    )
    Text(
      text = value,
      style = MaterialTheme.typography.labelMedium.copy(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Bold
      ),
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}
