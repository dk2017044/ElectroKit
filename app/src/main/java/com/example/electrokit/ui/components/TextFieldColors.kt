package com.example.electrokit.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * A standardized OutlinedTextField color scheme used across all calculator screens.
 * Ensures input text, label, cursor and border are always visible in both
 * light and dark theme modes.
 */
@Composable
fun electroKitTextFieldColors(
    accentColor: Color = Color(0xFF2563EB)
): TextFieldColors = OutlinedTextFieldDefaults.colors(
    // ── Container ──────────────────────────────────────────────────────────────
    focusedContainerColor   = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    disabledContainerColor  = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
    errorContainerColor     = MaterialTheme.colorScheme.errorContainer,

    // ── Text (what the user types) ─────────────────────────────────────────────
    focusedTextColor   = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    disabledTextColor  = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
    errorTextColor     = MaterialTheme.colorScheme.onSurface,

    // ── Cursor ─────────────────────────────────────────────────────────────────
    cursorColor      = accentColor,
    errorCursorColor = MaterialTheme.colorScheme.error,

    // ── Label ──────────────────────────────────────────────────────────────────
    focusedLabelColor   = accentColor,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
    disabledLabelColor  = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
    errorLabelColor     = MaterialTheme.colorScheme.error,

    // ── Placeholder ────────────────────────────────────────────────────────────
    focusedPlaceholderColor   = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
    disabledPlaceholderColor  = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),

    // ── Border ─────────────────────────────────────────────────────────────────
    focusedBorderColor   = accentColor,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
    disabledBorderColor  = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
    errorBorderColor     = MaterialTheme.colorScheme.error,

    // ── Leading / Trailing icons ───────────────────────────────────────────────
    focusedLeadingIconColor   = accentColor,
    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
    focusedTrailingIconColor  = accentColor,
    unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
)
