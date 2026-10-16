package com.example.clear.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LightScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = Color.White,
    primaryContainer = LightSurfaceSoft,
    onPrimaryContainer = LightTextDark,
    secondary = LightPrimary,
    onSecondary = Color.White,
    secondaryContainer = LightSurfaceSoft,
    onSecondaryContainer = LightTextDark,
    tertiary = LightWarning,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextDark,
    surface = LightCard,
    onSurface = LightTextDark,
    surfaceVariant = Color(0xFFEDECE6),
    onSurfaceVariant = LightTextSecondary,
    outline = Color(0xFF8E8D88),
    outlineVariant = Color(0xFFDAD9D3),
    error = LightError,
    onError = Color.White,
    // Alle Flaechen neutral, damit nirgends ein Farbstich der Standardfarben durchkommt
    surfaceContainerLowest = LightCard,
    surfaceContainerLow = Color(0xFFFAF9F5),
    surfaceContainer = LightCard,
    surfaceContainerHigh = LightCard,
    surfaceContainerHighest = Color(0xFFEFEEE8)
)

private val DarkScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color(0xFF2B2B2D),
    primaryContainer = DarkSurfaceSoft,
    onPrimaryContainer = DarkTextDark,
    secondary = DarkPrimary,
    onSecondary = Color(0xFF2B2B2D),
    secondaryContainer = DarkSurfaceSoft,
    onSecondaryContainer = DarkTextDark,
    tertiary = DarkWarning,
    onTertiary = Color(0xFF2B2B2D),
    background = DarkBackground,
    onBackground = DarkTextDark,
    surface = DarkCard,
    onSurface = DarkTextDark,
    surfaceVariant = Color(0xFF4A4A4D),
    onSurfaceVariant = DarkTextSecondary,
    outline = Color(0xFF8A8A8E),
    outlineVariant = Color(0xFF55555A),
    error = DarkError,
    onError = Color(0xFF2B2B2D),
    surfaceContainerLowest = Color(0xFF242426),
    surfaceContainerLow = Color(0xFF2F2F31),
    surfaceContainer = DarkCard,
    surfaceContainerHigh = Color(0xFF424245),
    surfaceContainerHighest = Color(0xFF4A4A4D)
)

// Einheitliche Eckenradien
val ClearShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp)
)

// Einheitliche Abstaende
object ClearSpacing {
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
}

val ClearTypography = Typography(
    headlineMedium = TextStyle(fontSize = 28.sp, lineHeight = 36.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp)
)

@Composable
fun ClearTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        shapes = ClearShapes,
        typography = ClearTypography,
        content = content
    )
}
