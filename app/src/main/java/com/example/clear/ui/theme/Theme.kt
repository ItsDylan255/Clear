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
    primaryContainer = Color(0xFFDCE8F4),
    onPrimaryContainer = Color(0xFF1B3550),
    secondary = Color(0xFF5E7388),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE4EAF0),
    onSecondaryContainer = LightTextDark,
    tertiary = LightWarning,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextDark,
    surface = LightCard,
    onSurface = LightTextDark,
    surfaceVariant = LightSurfaceSoft,
    onSurfaceVariant = LightTextSecondary,
    outline = Color(0xFF8793A0),
    outlineVariant = Color(0xFFDDE3E9),
    error = LightError,
    onError = Color.White,
    surfaceContainerLowest = LightCard,
    surfaceContainerLow = Color(0xFFF9FAFC),
    surfaceContainer = LightCard,
    surfaceContainerHigh = Color(0xFFF1F4F7),
    surfaceContainerHighest = Color(0xFFEAEEF2)
)

private val DarkScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color(0xFF10202F),
    primaryContainer = Color(0xFF2A425C),
    onPrimaryContainer = Color(0xFFD6E6F7),
    secondary = Color(0xFF9DB0C3),
    onSecondary = Color(0xFF14202B),
    secondaryContainer = DarkSurfaceSoft,
    onSecondaryContainer = DarkTextDark,
    tertiary = DarkWarning,
    onTertiary = Color(0xFF2B1B17),
    background = DarkBackground,
    onBackground = DarkTextDark,
    surface = DarkCard,
    onSurface = DarkTextDark,
    surfaceVariant = DarkSurfaceSoft,
    onSurfaceVariant = DarkTextSecondary,
    outline = Color(0xFF6E7B88),
    outlineVariant = Color(0xFF2D3844),
    error = DarkError,
    onError = Color(0xFF2B1B17),
    surfaceContainerLowest = Color(0xFF0E1216),
    surfaceContainerLow = Color(0xFF171D23),
    surfaceContainer = DarkCard,
    surfaceContainerHigh = Color(0xFF222A32),
    surfaceContainerHighest = Color(0xFF2A343E)
)

// Weiche, grosse Rundungen. Buttons (large) sind bei 56 dp Hoehe voll gerundet.
val ClearShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

// Einheitliche Abstaende
object ClearSpacing {
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
}

// Ruhige Schrift: etwas mehr Zeilenabstand, weniger Fettdruck
val ClearTypography = Typography(
    headlineMedium = TextStyle(fontSize = 28.sp, lineHeight = 36.sp, fontWeight = FontWeight.Medium),
    headlineSmall = TextStyle(fontSize = 24.sp, lineHeight = 32.sp, fontWeight = FontWeight.Medium),
    titleLarge = TextStyle(fontSize = 22.sp, lineHeight = 30.sp, fontWeight = FontWeight.Medium),
    titleMedium = TextStyle(fontSize = 17.sp, lineHeight = 24.sp, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 25.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
    labelLarge = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium),
    labelMedium = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium)
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
