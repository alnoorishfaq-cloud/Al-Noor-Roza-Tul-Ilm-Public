package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = SchoolGreenPrimaryDark,
    onPrimary = Color(0xFF00382B),
    primaryContainer = SchoolGreenContainerDark,
    onPrimaryContainer = Color(0xFFA1F2D6),
    secondary = Color(0xFF80D5E7),
    onSecondary = Color(0xFF00363F),
    secondaryContainer = Color(0xFF004E5B),
    onSecondaryContainer = Color(0xFFBCEBF5),
    tertiary = Color(0xFFB4C5FF),
    background = SchoolBackgroundDark,
    surface = SchoolSurfaceDark,
    surfaceVariant = SchoolSurfaceVariantDark,
    onBackground = Color(0xFFE2ECEA),
    onSurface = Color(0xFFE2ECEA),
    outline = SchoolOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = SchoolGreenPrimary,
    onPrimary = SchoolGreenOnPrimary,
    primaryContainer = SchoolGreenContainer,
    onPrimaryContainer = SchoolGreenOnContainer,
    secondary = SchoolSecondary,
    onSecondary = Color.White,
    secondaryContainer = SchoolSecondaryContainer,
    onSecondaryContainer = SchoolOnSecondaryContainer,
    tertiary = SchoolBlue,
    background = SchoolBackgroundLight,
    surface = SchoolSurfaceLight,
    surfaceVariant = SchoolSurfaceVariantLight,
    onBackground = Color(0xFF102520),
    onSurface = Color(0xFF102520),
    outline = SchoolOutlineLight
)

@Composable
fun AlNoorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.surface.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
