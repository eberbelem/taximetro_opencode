package com.taximetro.ui.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = TaximetroColors.GreenDigital,
    secondary = TaximetroColors.AmberFlag2,
    background = TaximetroColors.DarkBackground,
    surface = TaximetroColors.DarkSurface,
    onPrimary = TaximetroColors.TextPrimary,
    onSecondary = TaximetroColors.TextPrimary,
    onBackground = TaximetroColors.TextPrimary,
    onSurface = TaximetroColors.TextPrimary,
    error = TaximetroColors.RedStop
)

private val LightColorScheme = lightColorScheme(
    primary = TaximetroColors.GreenDim,
    secondary = TaximetroColors.AmberFlag2,
    background = TaximetroColors.LightBackground,
    surface = TaximetroColors.LightSurface,
    onPrimary = TaximetroColors.TextPrimary,
    onSecondary = TaximetroColors.TextPrimary,
    onBackground = TaximetroColors.LightTextPrimary,
    onSurface = TaximetroColors.LightTextPrimary,
    error = TaximetroColors.RedStop
)

@Composable
fun TaximetroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TaximetroTypography,
        content = content
    )
}
