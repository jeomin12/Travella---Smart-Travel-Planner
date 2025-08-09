package com.travelassistant.travella.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.travelassistant.travella.viewmodel.SettingsViewModel

@Composable
fun TravellaTheme(
    settingsViewModel: SettingsViewModel? = null,
    content: @Composable () -> Unit
) {
    val systemInDarkTheme = isSystemInDarkTheme()

    // Get dark mode preference from ViewModel if available
    val darkModeEnabled = settingsViewModel?.let { viewModel ->
        val isDarkMode by viewModel.isDarkMode.collectAsState()
        isDarkMode
    } ?: systemInDarkTheme

    val colorScheme = if (darkModeEnabled) {
        TravellaDarkColors
    } else {
        TravellaLightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TravellaTypography,
        content = content
    )
}