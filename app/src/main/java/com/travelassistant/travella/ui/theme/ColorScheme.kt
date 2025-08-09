package com.travelassistant.travella.ui.theme


import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

val TravellaLightColors: ColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    background = LightBackground,
    surface = LightSurface,
    onSurface = LightTextPrimary
)

val TravellaDarkColors: ColorScheme = darkColorScheme(
    primary = BlueLight,
    onPrimary = Color.Black,
    background = DarkBackground,
    surface = DarkSurface,
    onSurface = DarkTextPrimary
)
