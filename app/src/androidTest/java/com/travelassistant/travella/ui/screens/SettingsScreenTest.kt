package com.travelassistant.travella.ui.screens


import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.travelassistant.travella.ui.theme.TravellaTheme
import com.travelassistant.travella.viewmodel.SettingsViewModel
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun exportData_dialog_opens_and_toggles() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        val nav = TestNavHostController(context)
        val vm = SettingsViewModel(context)

        composeRule.setContent {
            TravellaTheme {
                SettingsScreen(navController = nav, viewModel = vm)
            }
        }

        // Open the dialog
        composeRule.onNodeWithText("Export Data").assertIsDisplayed().performClick()
        composeRule.onNodeWithText("Export to PDF").assertIsDisplayed()
        composeRule.onNodeWithText("Trips").assertIsDisplayed()
        composeRule.onNodeWithText("Expenses").assertIsDisplayed()
        // Close
        composeRule.onNodeWithText("Cancel").performClick()
        // Dialog should be gone
        composeRule.onNodeWithText("Export to PDF").assertDoesNotExist()
    }
}
