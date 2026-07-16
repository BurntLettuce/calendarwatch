package com.ledger.calendarwatch.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Typography

/**
 * To use the real Cinzel / Cinzel Decorative fonts,
 * drop the .ttf files into res/font/ and swap
 * FontFamily.Serif below for FontFamily(Font(R.font.cinzel)), etc.
 */
private val AppFont = FontFamily.Serif

private val AppColorScheme = Colors(
    primary = CardColors.Gold,
    primaryVariant = CardColors.Purple,
    secondary = CardColors.Purple,
    secondaryVariant = CardColors.Gold,
    background = CardColors.Background,
    surface = CardColors.Surface,
    error = CardColors.RedBright,
    onPrimary = CardColors.Background,
    onSecondary = CardColors.TextPrimary,
    onBackground = CardColors.TextPrimary,
    onSurface = CardColors.TextPrimary,
    onError = CardColors.TextPrimary
)

private val AppTypography = Typography(
    title1 = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Bold, fontSize = 24.sp),
    title2 = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Bold, fontSize = 20.sp),
    title3 = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    body1 = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Normal, fontSize = 15.sp),
    body2 = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Normal, fontSize = 13.sp),
    caption1 = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Normal, fontSize = 12.sp),
    caption2 = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Normal, fontSize = 11.sp)
)

/** Applies the colors and serif to everything inside [content]. */
@Composable
fun CardTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = AppColorScheme,
        typography = AppTypography,
        content = content
    )
}
