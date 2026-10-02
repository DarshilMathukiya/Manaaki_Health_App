package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val DarkColorScheme = darkColorScheme(
    primary = DarkGeoPrimary,
    onPrimary = DarkGeoOnPrimary,
    primaryContainer = DarkGeoSageContainer,
    onPrimaryContainer = DarkGeoSageOnContainer,
    secondary = DarkGeoPrimary,
    onSecondary = DarkGeoOnPrimary,
    secondaryContainer = DarkGeoSageContainer,
    onSecondaryContainer = DarkGeoSageOnContainer,
    tertiary = DarkHealthWarningAmber,
    onTertiary = DarkGeoOnPrimary,
    tertiaryContainer = DarkHealthWarningAmberBg,
    onTertiaryContainer = DarkHealthWarningAmber,
    background = DarkGeoBackground,
    onBackground = DarkGeoTextPrimary,
    surface = DarkGeoSurface,
    onSurface = DarkGeoTextPrimary,
    surfaceVariant = DarkGeoSageContainer,
    onSurfaceVariant = DarkGeoTextSecondary,
    outline = DarkGeoBorder
)

private val LightColorScheme = lightColorScheme(
    primary = LightGeoPrimary,
    onPrimary = LightGeoOnPrimary,
    primaryContainer = LightGeoSageContainer,
    onPrimaryContainer = LightGeoSageOnContainer,
    secondary = LightGeoPrimary,
    onSecondary = LightGeoOnPrimary,
    secondaryContainer = LightGeoSageContainer,
    onSecondaryContainer = LightGeoSageOnContainer,
    tertiary = LightHealthWarningAmber,
    onTertiary = LightGeoOnPrimary,
    tertiaryContainer = LightHealthWarningAmberBg,
    onTertiaryContainer = LightHealthWarningAmber,
    background = LightGeoBackground,
    onBackground = LightGeoTextPrimary,
    surface = LightGeoSurface,
    onSurface = LightGeoTextPrimary,
    surfaceVariant = LightGeoSageContainer,
    onSurfaceVariant = LightGeoTextSecondary,
    outline = LightGeoBorder
)

@Composable
fun ManaakiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val palette = if (darkTheme) DarkManaakiPalette else LightManaakiPalette
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalManaakiColors provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

