package com.pocketnas.pro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2E5CFF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE3FF),
    onPrimaryContainer = Color(0xFF00144B),
    secondary = Color(0xFF00696E),
    background = Color(0xFFF8F9FC),
    surface = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9DB8FF),
    onPrimary = Color(0xFF002878),
    primaryContainer = Color(0xFF0040A8),
    onPrimaryContainer = Color(0xFFDCE3FF),
    secondary = Color(0xFF4CDBE2),
    background = Color(0xFF101318),
    surface = Color(0xFF171A20),
)

@Composable
fun PocketNasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
