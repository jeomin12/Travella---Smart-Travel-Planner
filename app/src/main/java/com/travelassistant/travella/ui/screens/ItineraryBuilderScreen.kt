package com.travelassistant.travella.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.travelassistant.travella.viewmodel.TripDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryBuilderScreen(
    navController: NavHostController,
    tripViewModel: TripDashboardViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Itinerary Builder") })
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            Text("Upload booking confirmations (PDF/Email parsing not implemented)", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Manual Input & Drag-and-Drop Day Planning Interface Coming Soon")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("dashboard") }) {
                Text("Back to Dashboard")
            }
        }
    }
}