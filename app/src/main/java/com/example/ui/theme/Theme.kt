package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SophisticatedColorScheme = darkColorScheme(
    primary = TealAccent,
    onPrimary = Color(0xFF020617),             // Slate-950 text on primary teal
    primaryContainer = TealAccentGlow,
    onPrimaryContainer = TealAccent,
    secondary = TealAccentMuted,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1E293B),    // Slate-800
    onSecondaryContainer = Color(0xFFF1F5F9),  // Slate-100
    background = SophisticatedBg,
    onBackground = TextPrimary,
    surface = SophisticatedSurface,
    onSurface = TextPrimary,
    surfaceVariant = SophisticatedSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = SophisticatedSurfaceVariant,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRedContainer,
    onErrorContainer = ErrorRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme for Sophisticated Dark design
    dynamicColor: Boolean = false, // Disable dynamic colors to preserve exact brand colors
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = SophisticatedColorScheme,
        typography = Typography,
        content = content
    )
}
