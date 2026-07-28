package com.example.electrokit.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

val AppTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    )
)

@Composable
fun ElectroKitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accentColor: Color = PrimaryBlue,
    content: @Composable () -> Unit
) {
    val darkColorScheme = darkColorScheme(
        primary = accentColor,
        onPrimary = SurfaceDark,
        primaryContainer = accentColor.copy(alpha = 0.25f),
        secondary = AccentCyan,
        onSecondary = SurfaceDark,
        background = BackgroundDark,
        surface = SurfaceDark,
        onBackground = TextPrimaryDark,
        onSurface = TextPrimaryDark,
        outline = CardBorderDark
    )

    val lightColorScheme = lightColorScheme(
        primary = accentColor,
        onPrimary = SurfaceLight,
        primaryContainer = accentColor.copy(alpha = 0.15f),
        secondary = AccentCyan,
        onSecondary = SurfaceLight,
        background = BackgroundLight,
        surface = SurfaceLight,
        onBackground = TextPrimaryLight,
        onSurface = TextPrimaryLight,
        outline = CardBorderLight
    )

    val colorScheme = if (darkTheme) darkColorScheme else lightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
