package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = KeralaGreen,
    onPrimary = Color.White,
    primaryContainer = KeralaGreenLight,
    onPrimaryContainer = KeralaGreenDark,
    secondary = KasavuGold,
    onSecondary = Color(0xFF241A00),
    secondaryContainer = KasavuGoldLight,
    onSecondaryContainer = Color(0xFF3B2A00),
    tertiary = KasavuGoldDark,
    onTertiary = Color.White,
    tertiaryContainer = KasavuGoldContainer,
    onTertiaryContainer = Color(0xFF241A00),
    background = KeralaIvoryBg,
    onBackground = KeralaInk,
    surface = KeralaSurface,
    onSurface = KeralaInk,
    surfaceVariant = KeralaSurfaceVariant,
    onSurfaceVariant = KeralaMuted,
    outline = KeralaLine,
    outlineVariant = KeralaLineLight,
    error = KeralaRed,
    onError = Color.White,
    errorContainer = KeralaRedLight,
    onErrorContainer = KeralaRed
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4EDE9A),
    onPrimary = Color(0xFF003820),
    primaryContainer = KeralaGreenDark,
    onPrimaryContainer = Color(0xFF86FBB7),
    secondary = Color(0xFFE5B232),
    onSecondary = Color(0xFF3E2D00),
    secondaryContainer = Color(0xFF5A4300),
    onSecondaryContainer = KasavuGoldLight,
    tertiary = KasavuGold,
    background = Color(0xFF0F1A14),
    onBackground = Color(0xFFE1E8E2),
    surface = Color(0xFF16231C),
    onSurface = Color(0xFFE1E8E2),
    surfaceVariant = Color(0xFF1D2F25),
    onSurfaceVariant = Color(0xFFB5C4BB),
    outline = Color(0xFF334A3E),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep Kerala PSC theme consistent
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
