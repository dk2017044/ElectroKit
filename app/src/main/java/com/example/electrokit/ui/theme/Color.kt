package com.example.electrokit.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Colors: Default Electric Blue & Variants
val PrimaryBlue = Color(0xFF2563EB)
val PrimaryBlueDark = Color(0xFF1E40AF)
val PrimaryBlueLight = Color(0xFF60A5FA)

// Accent Colors Palette Options
val AccentBlue = Color(0xFF2563EB)
val AccentGreen = Color(0xFF10B981)
val AccentPurple = Color(0xFF8B5CF6)
val AccentOrange = Color(0xFFF59E0B)
val AccentRed = Color(0xFFEF4444)

// Accent Colors: Cyan
val AccentCyan = Color(0xFF00E5FF)
val AccentCyanLight = Color(0xFFE0F7FA)

// Surface & Background - Light Mode
val BackgroundLight = Color(0xFFF0F4F8)
val SurfaceLight = Color(0xFFFFFFFF)
val CardBorderLight = Color(0xFFE5E7EB)
val TextPrimaryLight = Color(0xFF1F2937)
val TextSecondaryLight = Color(0xFF4B5563)

// Surface & Background - Dark Mode
val BackgroundDark = Color(0xFF0B0F19)
val SurfaceDark = Color(0xFF111827)
val CardBorderDark = Color(0xFF1F2937)
val TextPrimaryDark = Color(0xFFF9FAFB)
val TextSecondaryDark = Color(0xFF9CA3AF)

fun getAccentColorByName(name: String): Color {
    return when (name.lowercase()) {
        "green" -> AccentGreen
        "purple" -> AccentPurple
        "orange" -> AccentOrange
        "red" -> AccentRed
        else -> AccentBlue
    }
}
