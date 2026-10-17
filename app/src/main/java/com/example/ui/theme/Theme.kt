package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CoupleGuardColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.White,
    primaryContainer = NavyCardElevated,
    onPrimaryContainer = CyanGlow,
    secondary = CyanAccent,
    onSecondary = NavyDarkBackground,
    secondaryContainer = CyanSubtle,
    onSecondaryContainer = CyanGlow,
    tertiary = SafetyGreen,
    onTertiary = NavyDarkBackground,
    error = EmergencyRed,
    onError = Color.White,
    errorContainer = EmergencyRedSubtle,
    onErrorContainer = EmergencyRed,
    background = NavyDarkBackground,
    onBackground = TextPrimary,
    surface = NavyDarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = NavyDarkCard,
    onSurfaceVariant = TextSecondary,
    outline = NavyCardBorder,
    outlineVariant = DividerColor
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // CoupleGuard is committed to the sleek dark navy security aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = CoupleGuardColorScheme,
        typography = Typography,
        content = content
    )
}
