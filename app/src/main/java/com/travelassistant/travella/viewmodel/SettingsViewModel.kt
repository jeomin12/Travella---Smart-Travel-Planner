package com.travelassistant.travella.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("travella_settings", Context.MODE_PRIVATE)

    // Dark Mode
    private val _isDarkMode = MutableStateFlow(
        sharedPreferences.getBoolean("dark_mode", false)
    )
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    // Language
    private val _selectedLanguage = MutableStateFlow(
        sharedPreferences.getString("language", "English") ?: "English"
    )
    val selectedLanguage: StateFlow<String> = _selectedLanguage

    // Currency
    private val _selectedCurrency = MutableStateFlow(
        sharedPreferences.getString("currency", "USD") ?: "USD"
    )
    val selectedCurrency: StateFlow<String> = _selectedCurrency

    // Notifications
    private val _notificationsEnabled = MutableStateFlow(
        sharedPreferences.getBoolean("notifications_enabled", true)
    )
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled

    // Location Services
    private val _locationEnabled = MutableStateFlow(
        sharedPreferences.getBoolean("location_enabled", true)
    )
    val locationEnabled: StateFlow<Boolean> = _locationEnabled

    // Auto-sync
    private val _autoSyncEnabled = MutableStateFlow(
        sharedPreferences.getBoolean("auto_sync", true)
    )
    val autoSyncEnabled: StateFlow<Boolean> = _autoSyncEnabled

    // Biometric Authentication
    private val _biometricEnabled = MutableStateFlow(
        sharedPreferences.getBoolean("biometric_auth", false)
    )
    val biometricEnabled: StateFlow<Boolean> = _biometricEnabled

    // Data Management
    private val _offlineModeEnabled = MutableStateFlow(
        sharedPreferences.getBoolean("offline_mode", false)
    )
    val offlineModeEnabled: StateFlow<Boolean> = _offlineModeEnabled

    // Available options
    val availableLanguages = listOf(
        "English", "Spanish", "French", "German", "Italian",
        "Portuguese", "Chinese", "Japanese", "Korean", "Hindi"
    )

    val availableCurrencies = listOf(
        "USD", "EUR", "GBP", "JPY", "AUD", "CAD", "CHF",
        "CNY", "INR", "KRW", "SGD", "HKD", "BRL", "MXN"
    )

    // App info
    fun getAppVersion(): String = "1.0.0"
    fun getBuildNumber(): String = "1"

    // Settings update functions
    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        sharedPreferences.edit().putBoolean("dark_mode", enabled).apply()
    }

    fun updateLanguage(language: String) {
        _selectedLanguage.value = language
        sharedPreferences.edit().putString("language", language).apply()
    }

    fun updateCurrency(currency: String) {
        _selectedCurrency.value = currency
        sharedPreferences.edit().putString("currency", currency).apply()
    }

    fun toggleNotifications(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        sharedPreferences.edit().putBoolean("notifications_enabled", enabled).apply()
    }

    fun toggleLocation(enabled: Boolean) {
        _locationEnabled.value = enabled
        sharedPreferences.edit().putBoolean("location_enabled", enabled).apply()
    }

    fun toggleAutoSync(enabled: Boolean) {
        _autoSyncEnabled.value = enabled
        sharedPreferences.edit().putBoolean("auto_sync", enabled).apply()
    }

    fun toggleBiometric(enabled: Boolean) {
        _biometricEnabled.value = enabled
        sharedPreferences.edit().putBoolean("biometric_auth", enabled).apply()
    }

    fun toggleOfflineMode(enabled: Boolean) {
        _offlineModeEnabled.value = enabled
        sharedPreferences.edit().putBoolean("offline_mode", enabled).apply()
    }

    // Data management
    fun clearCache() {
        // Implementation for clearing cache
    }

    fun exportData() {
        // Implementation for data export
    }

    fun importData() {
        // Implementation for data import
    }

    fun resetSettings() {
        sharedPreferences.edit().clear().apply()
        _isDarkMode.value = false
        _selectedLanguage.value = "English"
        _selectedCurrency.value = "USD"
        _notificationsEnabled.value = true
        _locationEnabled.value = true
        _autoSyncEnabled.value = true
        _biometricEnabled.value = false
        _offlineModeEnabled.value = false
    }
}