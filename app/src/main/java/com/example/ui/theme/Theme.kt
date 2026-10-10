package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GoldAccentLight,
    onPrimary = Color(0xFF382300),
    primaryContainer = BerryPrimary,
    onPrimaryContainer = Color(0xFFFFD7F2),
    secondary = SlateSecondaryLight,
    onSecondary = Color(0xFF0F3240),
    tertiary = GoldAccent,
    background = AtelierMidnightDark,
    surface = AtelierSurfaceDark,
    surfaceVariant = AtelierSurfaceVariantDark,
    onBackground = Color(0xFFECE5ED),
    onSurface = Color(0xFFECE5ED)
)

private val LightColorScheme = lightColorScheme(
    primary = BerryPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD7F2),
    onPrimaryContainer = BerryPrimaryDark,
    secondary = SlateSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCFE5F1),
    onSecondaryContainer = SlateSecondaryDark,
    tertiary = GoldAccent,
    onTertiary = Color(0xFF332000),
    tertiaryContainer = Color(0xFFFFE8B8),
    onTertiaryContainer = GoldAccentDark,
    background = SandBackgroundLight,
    surface = SandSurfaceLight,
    surfaceVariant = SandSurfaceVariant,
    onBackground = Color(0xFF201B22),
    onSurface = Color(0xFF201B22),
    outline = Color(0xFF83737F),
    outlineVariant = Color(0xFFD4C8D2)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our bespoke atelier palette for consistent branding
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
