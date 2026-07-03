package com.enriquebecerra.snaketracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = TerrariumGreenBright,
    onPrimary = Color(0xFF0A2110),
    primaryContainer = TerrariumMoss,
    onPrimaryContainer = Color(0xFFD3EBD8),
    secondary = TerrariumGreenMuted,
    onSecondary = Color(0xFF12190F),
    tertiary = Brown80,
    onTertiary = Color(0xFF2E1F1B),
    background = TerrariumBackground,
    onBackground = TerrariumOnSurface,
    surface = TerrariumSurface,
    onSurface = TerrariumOnSurface,
    surfaceVariant = TerrariumSurfaceVariant,
    onSurfaceVariant = TerrariumOnSurfaceMuted,
    outline = TerrariumOutline,
    error = TerrariumError
)

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    secondary = GreenGrey40,
    tertiary = Brown40
)

@Composable
fun SnakeTrackerTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
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
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
