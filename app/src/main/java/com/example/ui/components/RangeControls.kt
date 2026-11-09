package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuickPreset
import com.example.data.model.SortOrder
import com.example.ui.theme.JetBrainsMono

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RangeControls(
  minText: String,
  maxText: String,
  countText: String,
  uniqueNumbers: Boolean,
  sortOrder: SortOrder,
  isDecimalMode: Boolean,
  decimalPlaces: Int,
  presets: List<QuickPreset>,
  errorMessage: String?,
  isGenerating: Boolean,
  onMinChange: (String) -> Unit,
  onMaxChange: (String) -> Unit,
  onCountChange: (String) -> Unit,
  onAdjustMin: (Long) -> Unit,
  onAdjustMax: (Long) -> Unit,
  onSwapRange: () -> Unit,
  onUniqueToggle: (Boolean) -> Unit,
  onSortChange: (SortOrder) -> Unit,
  onDecimalToggle: (Boolean) -> Unit,
  onDecimalPlacesChange: (Int) -> Unit,
  onPresetSelect: (QuickPreset) -> Unit,
  onGenerate: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxWidth()) {
    // Quick Presets Row
    Text(
      text = "QUICK RANGES",
      style = MaterialTheme.typography.labelSmall,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
      modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
    )

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      presets.forEach { preset ->
        val isSelected = minText == preset.min.toString() &&
            maxText == preset.max.toString() &&
            countText == preset.count.toString()
        FilterChip(
          selected = isSelected,
          onClick = { onPresetSelect(preset) },
          label = {
            Text(
              text = preset.label,
              style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = JetBrainsMono
              )
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
          ),
          modifier = Modifier.testTag("preset_${preset.label}")
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Range Inputs Card (Min - Max)
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
      ),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "RANGE LIMITS",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )

          IconButton(
            onClick = onSwapRange,
            modifier = Modifier.size(36.dp),
            enabled = !isGenerating
          ) {
            Icon(
              imageVector = Icons.Default.SwapHoriz,
              contentDescription = "Swap Min and Max",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Min Input
          Column(modifier = Modifier.weight(1f)) {
            OutlinedTextField(
              value = minText,
              onValueChange = onMinChange,
              label = { Text("Min") },
              singleLine = true,
              keyboardOptions = KeyboardOptions(
                keyboardType = if (isDecimalMode) KeyboardType.Decimal else KeyboardType.Number
              ),
              textStyle = MaterialTheme.typography.titleMedium.copy(
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.Bold
              ),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("min_input"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
              )
            )

            if (!isDecimalMode) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                StepperTextButton("-10") { onAdjustMin(-10) }
                StepperTextButton("-1") { onAdjustMin(-1) }
                StepperTextButton("+1") { onAdjustMin(1) }
                StepperTextButton("+10") { onAdjustMin(10) }
              }
            }
          }

          // Max Input
          Column(modifier = Modifier.weight(1f)) {
            OutlinedTextField(
              value = maxText,
              onValueChange = onMaxChange,
              label = { Text("Max") },
              singleLine = true,
              keyboardOptions = KeyboardOptions(
                keyboardType = if (isDecimalMode) KeyboardType.Decimal else KeyboardType.Number
              ),
              textStyle = MaterialTheme.typography.titleMedium.copy(
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.Bold
              ),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("max_input"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
              )
            )

            if (!isDecimalMode) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                StepperTextButton("-10") { onAdjustMax(-10) }
                StepperTextButton("-1") { onAdjustMax(-1) }
                StepperTextButton("+1") { onAdjustMax(1) }
                StepperTextButton("+10") { onAdjustMax(10) }
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Quantity & Rules Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
      ),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        // Count selector
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Quantity to Generate",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "Number of random results per draw",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          OutlinedTextField(
            value = countText,
            onValueChange = onCountChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = MaterialTheme.typography.titleMedium.copy(
              fontFamily = JetBrainsMono,
              fontWeight = FontWeight.Bold
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .width(72.dp)
              .testTag("count_input")
          )
        }

        // Quick count chips
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("1", "2", "5", "10", "20").forEach { quickCount ->
            FilterChip(
              selected = countText == quickCount,
              onClick = { onCountChange(quickCount) },
              label = { Text(quickCount, fontFamily = JetBrainsMono) },
              modifier = Modifier.weight(1f)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Unique toggle (only for count > 1 and integer mode)
        val countInt = countText.toIntOrNull() ?: 1
        if (countInt > 1 && !isDecimalMode) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Unique numbers only",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = "No duplicate numbers in the generated list",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Switch(
              checked = uniqueNumbers,
              onCheckedChange = onUniqueToggle,
              modifier = Modifier.testTag("unique_switch")
            )
          }
          Spacer(modifier = Modifier.height(12.dp))
        }

        // Sorting Options (if count > 1)
        if (countInt > 1) {
          Text(
            text = "Sort Results",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 6.dp)
          )
          SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SortOrder.entries.forEachIndexed { index, order ->
              SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = SortOrder.entries.size),
                onClick = { onSortChange(order) },
                selected = sortOrder == order,
                label = {
                  Text(
                    text = when (order) {
                      SortOrder.NONE -> "Random"
                      SortOrder.ASC -> "Low → High"
                      SortOrder.DESC -> "High → Low"
                    },
                    fontSize = 11.sp
                  )
                }
              )
            }
          }
          Spacer(modifier = Modifier.height(12.dp))
        }

        // Decimal Mode Toggle
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Decimal numbers mode",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Medium
            )
            Text(
              text = "Generate floating point random values",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          Switch(
            checked = isDecimalMode,
            onCheckedChange = onDecimalToggle,
            modifier = Modifier.testTag("decimal_switch")
          )
        }

        if (isDecimalMode) {
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Precision:",
              style = MaterialTheme.typography.bodySmall
            )
            (1..4).forEach { places ->
              FilterChip(
                selected = decimalPlaces == places,
                onClick = { onDecimalPlacesChange(places) },
                label = { Text("${places} dec") }
              )
            }
          }
        }
      }
    }

    // Error message display
    AnimatedVisibility(visible = errorMessage != null) {
      if (errorMessage != null) {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.errorContainer
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.ErrorOutline,
              contentDescription = "Error",
              tint = MaterialTheme.colorScheme.error,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = errorMessage,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onErrorContainer
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Primary GENERATE Action Button
    Button(
      onClick = onGenerate,
      enabled = !isGenerating,
      modifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .testTag("generate_button"),
      shape = RoundedCornerShape(16.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
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
          text = if (isGenerating) "GENERATING..." else "GENERATE RANDOM NUMBER",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )
      }
    }
  }
}

@Composable
private fun StepperTextButton(text: String, onClick: () -> Unit) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = MaterialTheme.colorScheme.surfaceVariant,
    onClick = onClick,
    modifier = Modifier.padding(1.dp)
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMono),
      color = MaterialTheme.colorScheme.primary,
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
    )
  }
}
