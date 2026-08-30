package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SinaRed,
    onPrimary = Color.White,
    primaryContainer = SinaRedDark,
    onPrimaryContainer = Color.White,
    secondary = SinaGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = Color(0xFFFEF3C7),
    tertiary = SinaCyan,
    onTertiary = Color.Black,
    background = SinaDark,
    onBackground = SinaTextPrimaryDark,
    surface = SinaDarkCard,
    onSurface = SinaTextPrimaryDark,
    surfaceVariant = SinaDarkSurface,
    onSurfaceVariant = SinaTextSecondaryDark,
    outline = SinaBorderDark,
    error = Color(0xFFF87171),
    onError = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = SinaRed,
    onPrimary = Color.White,
    primaryContainer = SinaRedLight,
    onPrimaryContainer = SinaRedDark,
    secondary = SinaGold,
    onSecondary = Color.White,
    secondaryContainer = SinaGoldLight,
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = SinaNavy,
    onTertiary = Color.White,
    background = SinaLightBackground,
    onBackground = SinaTextPrimaryLight,
    surface = SinaLightSurface,
    onSurface = SinaTextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = SinaTextSecondaryLight,
    outline = SinaBorderLight,
    error = Color(0xFFDC2626),
    onError = Color.White
)

@Composable
fun SinaKalaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
