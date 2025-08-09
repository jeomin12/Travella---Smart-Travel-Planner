package com.travelassistant.travella.viewmodel


import android.app.Application
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsViewModelTest {
    private val app: Application = ApplicationProvider.getApplicationContext()
    private val vm = SettingsViewModel(app)

    @Test
    fun `toggle dark mode updates state`() = runTest {
        vm.toggleDarkMode(true)
        assertTrue(vm.isDarkMode.value)
        vm.toggleDarkMode(false)
        assertFalse(vm.isDarkMode.value)
    }
}
