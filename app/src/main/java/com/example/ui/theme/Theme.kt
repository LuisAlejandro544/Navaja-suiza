package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = AmberPrimary,
    onPrimary = Slate950,
    primaryContainer = AmberDark,
    onPrimaryContainer = AmberLight,
    secondary = CyanAccent,
    onSecondary = Slate950,
    secondaryContainer = Slate700,
    onSecondaryContainer = Slate50,
    tertiary = TealAccent,
    background = Slate950,
    onBackground = Slate50,
    surface = Slate900,
    onSurface = Slate50,
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate200,
    surfaceContainer = Slate900,
    surfaceContainerHigh = Slate800,
    surfaceContainerHighest = Slate700,
    error = CrimsonError,
    onError = PureWhite
  )

private val LightColorScheme =
  lightColorScheme(
    primary = AmberPrimary,
    onPrimary = PureWhite,
    primaryContainer = AmberLight,
    onPrimaryContainer = Slate900,
    secondary = CyanAccent,
    onSecondary = PureWhite,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = Slate900,
    tertiary = TealAccent,
    background = CoolWhite,
    onBackground = LightOnSurface,
    surface = PureWhite,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Slate700,
    surfaceContainer = LightSurface,
    surfaceContainerHigh = CoolWhite,
    surfaceContainerHighest = LightSurfaceVariant,
    error = CrimsonError,
    onError = PureWhite
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
