// app/src/main/java/com/travelassistant/travella/navigation/AppNavigation.kt
package com.travelassistant.travella.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.travelassistant.travella.ui.components.BottomNavScreen
import com.travelassistant.travella.ui.screens.*
import com.travelassistant.travella.viewmodel.SettingsViewModel
import com.travelassistant.travella.viewmodel.TripDashboardViewModel
import com.travelassistant.travella.viewmodel.UserViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    sharedEmailText: String?
) {
    // shared across the app
    val settingsViewModel: SettingsViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController = navController)
        }

        composable("login") {
            val userVm: UserViewModel = viewModel()
            LoginScreen(navController = navController, userViewModel = userVm)
        }

        composable("signup") {
            val userVm: UserViewModel = viewModel()
            SignupScreen(navController = navController, userViewModel = userVm)
        }

        // Root: bottom nav scaffold
        composable("home") {
            val tripVm: TripDashboardViewModel = viewModel()
            BottomNavScreen(
                navController = navController,
                settingsViewModel = settingsViewModel,
                tripViewModel = tripVm
            )
        }

        // Itinerary builder (kept as-is)
        composable(
            route = "itinerary/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.IntType })
        ) {
            val tripVm: TripDashboardViewModel = viewModel()
            ItineraryBuilderScreen(navController = navController, tripViewModel = tripVm)
        }

        // Trip detail
        composable(
            route = "tripDetail/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId") ?: 0
            val tripVm: TripDashboardViewModel = viewModel()
            TripDetailScreen(
                tripId = tripId,
                navController = navController,
                viewModel = tripVm
            )
        }

        composable("collaborators") {
            TripCollaboratorsScreen(navController = navController)
        }

        composable("help") {
            HelpScreen(navController = navController)
        }

        composable("offline") {
            OfflineAccessScreen(navController = navController)
        }

        composable("profile") {
            ProfileSetupScreen(navController = navController)
        }

        // Import from Email
        composable("importEmail") {
            val tripVm: TripDashboardViewModel = viewModel()
            ImportFromEmailScreen(
                navController = navController,
                prefilled = sharedEmailText ?: "",
                tripViewModel = tripVm
            )
        }
    }
}
