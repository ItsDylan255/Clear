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
    background = LightBackground,
    onBackground = LightTextDark,
    surface = LightCard,
    onSurface = LightTextDark,
    onSurfaceVariant = LightTextSecondary,
    secondaryContainer = LightSurfaceSoft,
    onSecondaryContainer = LightTextDark,
    tertiary = LightWarning,
    error = LightError,
    surfaceContainer = LightCard,
    surfaceContainerHigh = LightCard
)

private val DarkScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color(0xFF1B241E),
    background = DarkBackground,
    onBackground = DarkTextDark,
    surface = DarkCard,
    onSurface = DarkTextDark,
    onSurfaceVariant = DarkTextSecondary,
    secondaryContainer = DarkSurfaceSoft,
    onSecondaryContainer = DarkTextDark,
    tertiary = DarkWarning,
    error = DarkError,
    outline = Color(0xFF6E7A70),
    outlineVariant = Color(0xFF3F4A41),
    surfaceVariant = Color(0xFF3A433C),
    surfaceContainer = DarkCard,
    surfaceContainerHigh = Color(0xFF323A34)
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
