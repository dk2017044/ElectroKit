package com.example.electrokit.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * A standardized OutlinedTextField color scheme used across all calculator screens.
 * Ensures input text, label, cursor and border are always visible with accent highlights.
 */
@Composable
fun electroKitTextFieldColors(
    accentColor: Color = MaterialTheme.colorScheme.primary
): TextFieldColors = OutlinedTextFieldDefaults.colors(
    // ── Container ──────────────────────────────────────────────────────────────
    focusedContainerColor   = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
    disabledContainerColor  = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
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
    unfocusedLabelColor = accentColor.copy(alpha = 0.75f),
    disabledLabelColor  = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
    errorLabelColor     = MaterialTheme.colorScheme.error,

    // ── Placeholder ────────────────────────────────────────────────────────────
    focusedPlaceholderColor   = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
    disabledPlaceholderColor  = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),

    // ── Border (Glassmorphic Accent Border Highlight) ───────────────────────────
    focusedBorderColor   = accentColor,
    unfocusedBorderColor = accentColor.copy(alpha = 0.28f),
    disabledBorderColor  = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
    errorBorderColor     = MaterialTheme.colorScheme.error,

    // ── Leading / Trailing icons ───────────────────────────────────────────────
    focusedLeadingIconColor   = accentColor,
    unfocusedLeadingIconColor = accentColor.copy(alpha = 0.75f),
    focusedTrailingIconColor  = accentColor,
    unfocusedTrailingIconColor = accentColor.copy(alpha = 0.75f),
)
