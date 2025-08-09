package com.travelassistant.travella

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.travelassistant.travella.navigation.AppNavigation
import com.travelassistant.travella.ui.theme.TravellaTheme
import com.travelassistant.travella.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Compute shared text OUTSIDE compose (no remember needed)
        val sharedText: String? =
            if (intent?.action == Intent.ACTION_SEND && intent?.type == "text/plain") {
                intent?.getStringExtra(Intent.EXTRA_TEXT)
            } else null

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()

            TravellaTheme(settingsViewModel = settingsViewModel) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    // pass shared text into nav
                    AppNavigation(
                        navController = navController,
                        sharedEmailText = sharedText
                    )
                }
            }
        }
    }

    // Optional: allow receiving a new share while app is open
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}
