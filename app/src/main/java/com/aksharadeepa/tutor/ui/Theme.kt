package com.aksharadeepa.tutor.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Dark Blue + White Palette ─────────────────────────────────
val DarkBlue        = Color(0xFF0A1628)   // deepest navy — main background
val MidBlue         = Color(0xFF1A2E4A)   // card background
val AccentBlue      = Color(0xFF1565C0)   // primary action blue
val BrightBlue      = Color(0xFF2196F3)   // highlights, progress bars
val LightBlue       = Color(0xFFBBDEFB)   // subtle tint, badges
val White           = Color(0xFFFFFFFF)   // primary text
val OffWhite        = Color(0xFFE3F2FD)   // secondary text / subtitles
val GreyText        = Color(0xFF90A4AE)   // hint / disabled text
val SuccessGreen    = Color(0xFF4CAF50)   // correct answers
val WarnRed         = Color(0xFFEF5350)   // wrong answers / revision badge
val GoldYellow      = Color(0xFFFFC107)   // streak / highlight accents

private val DarkBlueScheme = darkColorScheme(
    primary             = AccentBlue,
    onPrimary           = White,
    primaryContainer    = MidBlue,
    onPrimaryContainer  = OffWhite,
    secondary           = BrightBlue,
    onSecondary         = White,
    error               = WarnRed,
    onError             = White,
    background          = DarkBlue,
    onBackground        = White,
    surface             = MidBlue,
    onSurface           = White,
    surfaceVariant      = Color(0xFF1E3A5F),
    outline             = Color(0xFF2A4A7F),
)

@Composable
fun AksharaDeepTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkBlueScheme,
        content = content
    )
}
