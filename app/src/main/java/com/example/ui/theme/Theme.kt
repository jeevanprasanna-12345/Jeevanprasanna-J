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

private val DarkColorScheme =
  darkColorScheme(
    primary = BlushPink,
    secondary = SageLight,
    tertiary = AccentHoney,
    background = CharcoalBark,
    surface = DarkRosewood,
    onPrimary = CharcoalBark,
    onSecondary = DarkRosewood,
    onBackground = WarmLinen,
    onSurface = WarmLinen
  )

private val LightColorScheme =
  lightColorScheme(
    primary = RosewoodDeep,
    secondary = SoftSage,
    tertiary = AccentHoney,
    background = WarmLinen,
    surface = Color(0xFFFFFDFB),
    onPrimary = WarmLinen,
    onSecondary = WarmLinen,
    onBackground = CharcoalBark,
    onSurface = CharcoalBark
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic color by default to preserve the customized floral design
  dynamicColor: Boolean = false,
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
