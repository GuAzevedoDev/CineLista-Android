package com.gustavo.cinelista.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CineColors = lightColorScheme(
    primary = Color(0xFF8F3F23), onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDBCA), onPrimaryContainer = Color(0xFF351004),
    secondary = Color(0xFF52634F), onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD5E8CF), onSecondaryContainer = Color(0xFF10200F),
    background = Color(0xFFFFF8F2), onBackground = Color(0xFF251A16),
    surface = Color(0xFFFFF8F2), onSurface = Color(0xFF251A16),
    surfaceVariant = Color(0xFFF2DFD5), onSurfaceVariant = Color(0xFF594239),
    error = Color(0xFFBA1A1A), onError = Color(0xFFFFFFFF)
)

@Composable
fun CineListaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = CineColors, content = content)
}
