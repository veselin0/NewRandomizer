package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = IndigoPrimaryDark,
  onPrimary = IndigoOnPrimaryDark,
  primaryContainer = IndigoContainerDark,
  onPrimaryContainer = IndigoOnContainerDark,
  secondary = CyanSecondaryDark,
  onSecondary = CyanOnSecondaryDark,
  secondaryContainer = CyanContainerDark,
  onSecondaryContainer = CyanOnContainerDark,
  tertiary = AmberTertiaryDark,
  onTertiary = AmberOnTertiaryDark,
  tertiaryContainer = AmberContainerDark,
  onTertiaryContainer = AmberOnContainerDark,
  background = BackgroundDark,
  onBackground = Color(0xFFF1F5F9),
  surface = SurfaceDark,
  onSurface = Color(0xFFF8FAFC),
  surfaceVariant = SurfaceVariantDark,
  onSurfaceVariant = Color(0xFF94A3B8),
  outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
  primary = IndigoPrimaryLight,
  onPrimary = IndigoOnPrimaryLight,
  primaryContainer = IndigoContainerLight,
  onPrimaryContainer = IndigoOnContainerLight,
  secondary = CyanSecondaryLight,
  onSecondary = CyanOnSecondaryLight,
  secondaryContainer = CyanContainerLight,
  onSecondaryContainer = CyanOnContainerLight,
  tertiary = AmberTertiaryLight,
  onTertiary = AmberOnTertiaryLight,
  tertiaryContainer = AmberContainerLight,
  onTertiaryContainer = AmberOnContainerLight,
  background = BackgroundLight,
  onBackground = Color(0xFF0F172A),
  surface = SurfaceLight,
  onSurface = Color(0xFF0F172A),
  surfaceVariant = SurfaceVariantLight,
  onSurfaceVariant = Color(0xFF64748B),
  outline = OutlineLight
)

@Composable
fun RandomizerTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Default to our vibrant custom palette
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
    content = content
  )
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  RandomizerTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
