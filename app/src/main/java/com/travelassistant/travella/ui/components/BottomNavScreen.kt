package com.travelassistant.travella.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.travelassistant.travella.ui.screens.*
import com.travelassistant.travella.viewmodel.ExpenseViewModel // Import ExpenseViewModel
import com.travelassistant.travella.viewmodel.ReminderViewModel
import com.travelassistant.travella.viewmodel.SettingsViewModel
import com.travelassistant.travella.viewmodel.TripDashboardViewModel

data class TabItem(val title: String, val route: String, val icon: ImageVector)

@Composable
fun BottomNavScreen(
    navController: NavHostController,              // app-level navController (for pushing detail/import)
    settingsViewModel: SettingsViewModel,
    tripViewModel: TripDashboardViewModel
) {
    val tabs = listOf(
        TabItem("Home", "dashboard", Icons.Default.Home),
        TabItem("Map", "map", Icons.Default.Map),
        TabItem("Expenses", "expenses", Icons.Default.AttachMoney),
        TabItem("Reminders", "reminders", Icons.Default.Notifications),
        TabItem("Settings", "settings", Icons.Default.Settings)
    )

    val bottomNavController = rememberNavController()
    val reminderViewModel: ReminderViewModel = viewModel()
    val expenseViewModel: ExpenseViewModel = viewModel() // Instantiate ExpenseViewModel here

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                tabs.forEach { tab ->
                    val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            bottomNavController.navigate(tab.route) {
                                popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "dashboard",
            modifier = Modifier.padding(padding)
        ) {
            composable("dashboard") {
                // IMPORTANT: use app-level navController so Home can push to detail/import
                TripDashboardScreen(navController = navController, viewModel = tripViewModel)
            }
            composable("map") { MapScreen(bottomNavController) }
            composable("expenses") { SimpleExpenseTrackerScreen(bottomNavController, expenseViewModel) } // Pass expenseViewModel
            composable("reminders") { ReminderScreen(reminderViewModel) }
            composable("settings") { SettingsScreen(bottomNavController, settingsViewModel) }
        }
    }
}
